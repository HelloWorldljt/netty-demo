package BIO;

import java.net.Socket;

/**
 * @author lijiangtao
 * @description timeserver 服务处理线程
 * @date 2020/9/22 0022
 */
public class TimeServerHandler implements  Runnable{

    private Socket socket;
    public TimeServerHandler(Socket socket){
        this.socket = socket;
    }


    public void run() {

    }
}
