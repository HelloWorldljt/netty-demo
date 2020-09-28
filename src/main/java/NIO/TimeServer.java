package NIO;

/**
 * @author lijiangtao
 * @description nio 时间服务
 * @date 2020/9/22 0022
 */
public class TimeServer {

    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimeServer multiplexerTimeServer = new MultiplexerTimeServer(port);
        new Thread(multiplexerTimeServer,"nio-multiplexerTimeServer-001").start();
    }

}
