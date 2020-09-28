package AIO;

import java.io.IOException;

/**
 * @author lijiangtao
 * @description
 * @date 2020/9/27 0027
 */
public class TimeServer {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        AsyncTimeServerHandler timeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(timeServerHandler,"AIO-asyncTimeServerHandler-001").start();
    }

}
