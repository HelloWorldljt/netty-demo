package asyn;

import BIO.TimeServerHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author lijiangtao
 * @description 伪异步IO 时间服务端
 * @date 2020/9/22 0022
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("server is start in port:"+8080);
        TimeServerHandlerExecutePool timeServerHandlerExecutePool = new TimeServerHandlerExecutePool(50, 1000);
        Socket accept = null;
        while (true){
            accept = serverSocket.accept();
            timeServerHandlerExecutePool.execute(new TimeServerHandler(accept));
        }
    }

}
