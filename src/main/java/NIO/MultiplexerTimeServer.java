package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author lijiangtao
 * @description 多路复用类
 * @date 2020/9/23
 */
public class MultiplexerTimeServer implements Runnable{

	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private volatile boolean stop;

	public MultiplexerTimeServer(int port){
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
			serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
			System.out.println("time server is start in port:"+port);
		}catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		while (!stop){
			try {
				selector.select(1000);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				SelectionKey key = null;
				while (iterator.hasNext()){
					key = iterator.next();
					iterator.remove();
					handleInput(key);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (selector != null){
			try {
				//多路复用器selector 关闭后，上面注册的channel 和 pipe 等资源会自动关闭，释放资源
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void stop(){
		this.stop = true;
	}

	private void handleInput(SelectionKey key) throws IOException {
		if(key.isValid()){
			if (key.isAcceptable()) {
				//处理新连接  相当于完成tcp三次握手
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);
				//创建一个新的链接到selector
				sc.register(selector,SelectionKey.OP_READ);
			}
			if (key.isReadable()) {
				SocketChannel socketChannel = (SocketChannel) key.channel();
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int read = socketChannel.read(readBuffer);
				if (read > 0){
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body =new String(bytes, "UTF-8");
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?new Date().toString():"BAD ORDER";

				}else if(read < 0){
					key.cancel();
					socketChannel.close();
				}else {
					;//读取到 0 字节
				}
			}
		}
	}

	private void doWrite(SocketChannel channel,String response) throws IOException {
		if(response != null && response.length() > 0){
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);
			writeBuffer.flip();
			channel.write(writeBuffer);
		}
	}

}