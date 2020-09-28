package NIO;

/**
 * @author lijiangtao
 * @description 时间服务客户端
 * @date 2020/9/28
 */
public class TimeClient {

	public static void main(String[] args) {
		int port = 8080;
		new Thread(new TimeClientHandle("127.0.0.1",port),"timeclient-001").start();
	}

}