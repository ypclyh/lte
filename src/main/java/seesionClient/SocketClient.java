package seesionClient;
import com.ericsson.lte.session.utils.SessionEventPool;
import com.ericsson.lte.session.controller.SessionController;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class SocketClient extends Socket {
    private final static Logger logger = Logger.getLogger(SessionController.class.getName());
    private static final String SERVER_IP = "192.168.0.104"; // 服务器的公网IP地址
    private static final int SERVER_PORT = 89; // 服务端端口，要和之前服务器端监听的端口一致
    private Socket client;

    /**
     * 构造函数
     * 与服务器建立连接
     */
    public SocketClient() throws IOException {
        super(SERVER_IP, SERVER_PORT);
        this.client = this;
        logger.info("Cliect[port:" + client.getLocalPort() + "]  connected to the server successfully");
    }
    /**
     * 入口
     * @param args
     */
    public static void main(String[] args) {
        try {
            new SocketClient(); // 启动客户端连接
            SessionController sessionController = new SessionController();
            ExecutorService singleTonPool = SessionEventPool.getSingleTonPool();
            sessionController.handleRequestTask(singleTonPool);
            Thread.sleep(60000 * 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
