package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lijiangtao
 * @description time 客户端
 * @date 2020/9/27
 */
public class TimeClientHandle implements Runnable{
	private String host;
	public Integer port;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean stop;

	public TimeClientHandle(String host,int port){
		this.host = host;
		this.port = port;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		try {
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (!stop){
			try {
				selector.select(1000);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				SelectionKey key = null;
				while (iterator.hasNext()) {
					key = iterator.next();
					iterator.remove();
					try {
						handleInput(key);
					} catch (IOException e) {
						e.printStackTrace();
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if(key.isValid()){
			SocketChannel socketChannel = (SocketChannel) key.channel();
			if(key.isConnectable()){
				if (socketChannel.finishConnect()) {
					socketChannel.register(selector,SelectionKey.OP_READ);
					doWrite(socketChannel);
				}else {
					System.exit(1);
				}
			}

			if (key.isReadable()) {
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int read = socketChannel.read(readBuffer);
				if(read > 0){
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("now is :"+body);
				}else if(read < 0) {
					//对端链路关闭
					key.cancel();
					socketChannel.close();
				}else {
					;//读到0字节，忽略
				}
			}
		}
	}

	private void doConnect() throws IOException {
		if(socketChannel.connect(new InetSocketAddress(host,port))){
			socketChannel.register(selector,SelectionKey.OP_READ);
			doWrite(socketChannel);
		}else {
			socketChannel.register(selector,SelectionKey.OP_CONNECT);
		}

	}

	private void doWrite(SocketChannel socketChannel) throws IOException {
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		socketChannel.write(writeBuffer);
		if(!writeBuffer.hasRemaining()){
			System.out.println("send order to server success");
		}
	}

}