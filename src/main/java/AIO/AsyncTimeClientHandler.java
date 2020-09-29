package AIO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @author lijiangtao
 * @description 时间服务客户端处理
 * @date 2020/9/29
 */
public class AsyncTimeClientHandler implements CompletionHandler<Void,AsyncTimeClientHandler>,Runnable {
	private AsynchronousSocketChannel client;
	private String host;
	private int port;
	private CountDownLatch countDownLatch;

	public AsyncTimeClientHandler(String host,int port) throws IOException {
		this.host = host;
		this.port = port;
		client = AsynchronousSocketChannel.open();
	}


	@Override
	public void run() {
		countDownLatch = new CountDownLatch(1);
		client.connect(new InetSocketAddress(host,port),this,this);
		try {
			countDownLatch.await();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void completed(Void result, AsyncTimeClientHandler attachment) {
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		client.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				if(attachment.hasRemaining()){
					client.write(attachment,attachment,this);
				}else {
					ByteBuffer readBuffer = ByteBuffer.allocate(1024);
					client.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
						@Override
						public void completed(Integer result, ByteBuffer attachment) {
							byte[] bytes = new byte[attachment.remaining()];
							attachment.get(bytes);
							String body;
							try {
								body = new String(bytes,"UTF-8");
								countDownLatch.countDown();
								System.out.println("now is :"+body);
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							exc.printStackTrace();
							try {
								client.close();
								countDownLatch.countDown();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});

				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				exc.printStackTrace();
				try {
					client.close();
					countDownLatch.countDown();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
		exc.printStackTrace();
		try {
			client.close();
			countDownLatch.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}