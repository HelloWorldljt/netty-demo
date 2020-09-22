package BIO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author lijiangtao
 * @description BIO 时间服务端
 * @date 2020/9/22 0022
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("time server is statrt in port:"+port);
        Socket accept = null;
        while (true){
            accept = serverSocket.accept();
            new Thread(new TimeServerHandler(accept)).start();
        }
    }

}
