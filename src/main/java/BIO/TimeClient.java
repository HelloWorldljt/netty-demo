package BIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author lijiangtao
 * @description 时间服务客户端
 * @date 2020/9/22 0022
 */
public class TimeClient {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        Socket socket = new Socket("127.0.0.1",port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("QUERY TIME ORDER");
        System.out.println("send order to server success");
        String res = in.readLine();
        System.out.println("now is:"+res);
    }

}
