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
    private static final int SERVER_PORT = 89; // 服务端端口,根据需要改写，注意安全组要开放端口
    private final static Logger logger = Logger.getLogger(ServerSocket.class.getName());

    private Server() throws Exception {
        super(SERVER_PORT);
    }

    /**
     * 使用线程处理每个客户端传输的文件
     */
    private void load() throws Exception {
        int i = 0;
        while (true) {
            Socket socket = this.accept();
            System.out.println("connect success");
            /**
             * 服务端处理客户端的连接请求是同步进行的，每次接收到来自客户端的连接请求后，
             * 都要先跟当前的客户端通信完之后才能再处理下一个连接请求。 这在并发比较多的情况下会严重影响程序的性能
             * 为此可以把它改为如下这种异步处理与客户端通信的方式
             */
            // 每接收到一个Socket就建立一个新的线程来处理它
            Thread thread = new Thread(new Task(socket), "client：" + i++);
            thread.start();
            logger.info(Thread.currentThread().getName() + "connect success！");
        }
    }

    /**
     * 处理客户端传输过来的文件线程类
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
                //TOD 过期就断开连接
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
            Server server = new Server(); // 启动服务端
            System.out.println("Waiting for connection with client...");
            server.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
