package sessionServer;

import com.ericsson.lte.session.consts.ParametersConstants;
import com.ericsson.lte.session.entity.RequestBean;
import com.ericsson.lte.session.entity.ResponseBean;
import com.ericsson.lte.session.utils.RedisUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Logger;

public class Server extends ServerSocket {
    private static final int SERVER_PORT = 89; // ����˶˿�,������Ҫ��д��ע�ⰲȫ��Ҫ���Ŷ˿�
    private final static Logger logger = Logger.getLogger(ServerSocket.class.getName());

    private Server() throws Exception {
        super(SERVER_PORT);
    }

    /**
     * ʹ���̴߳���ÿ���ͻ��˴�����ļ�
     */
    private void load() throws Exception {
        int i = 0;
        while (true) {
            Socket socket = this.accept();
            System.out.println("connect success");
            /**
             * ����˴���ͻ��˵�����������ͬ�����еģ�ÿ�ν��յ����Կͻ��˵����������
             * ��Ҫ�ȸ���ǰ�Ŀͻ���ͨ����֮������ٴ�����һ���������� ���ڲ����Ƚ϶������»�����Ӱ����������
             * Ϊ�˿��԰�����Ϊ���������첽������ͻ���ͨ�ŵķ�ʽ
             */
            // ÿ���յ�һ��Socket�ͽ���һ���µ��߳���������
            Thread thread = new Thread(new Task(socket), "client��" + i++);
            thread.start();
            logger.info(Thread.currentThread().getName() + "connect success��");
        }
    }

    /**
     * ����ͻ��˴���������ļ��߳���
     */
    class Task implements Runnable {
        private Socket socket;

        Task(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ObjectInputStream is = null;
            ObjectOutputStream os = null;
            try {
                is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                os = new ObjectOutputStream(socket.getOutputStream());
                Object obj = is.readObject();
                RequestBean req = (RequestBean) obj;
                if (req.getActionType().equalsIgnoreCase(ParametersConstants.STOP)){
                    os.writeObject(new ResponseBean(1, req.getStartTime(),req.getStopTime(), ParametersConstants.SESSION_ID_EXPIRE));
                    close(os, is, socket);
                }
                //TOD ���ھͶϿ�����
                try {
                    Thread.sleep(1000);
                    if (Objects.requireNonNull(RedisUtil.getJedis()).get(req.getDeliverySessionId()) == null){
                        os.writeObject(new ResponseBean(1, req.getStartTime(),req.getStopTime(), ParametersConstants.SESSION_ID_EXPIRE));
                        close(os, is, socket);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ResponseBean res = new ResponseBean(0, req.getStartTime(),req.getStopTime(), ParametersConstants.OK);
                os.writeObject(res);
                os.flush();
            } catch (IOException ex) {
                logger.warning("error reading data!" + ex);
            } catch (ClassNotFoundException ex) {
                logger.warning("Class Not Found " + ex);
            } finally {
                close(os, is, socket);
            }
        }

        private void close(ObjectOutputStream os, ObjectInputStream is, Socket socket) {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    logger.warning("close the os fail." + ex);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    logger.warning("close the is fail." + ex);
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.warning("close the socket fail." + ex);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(); // ���������
            System.out.println("Waiting for connection with client...");
            server.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
