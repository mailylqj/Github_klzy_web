package com.mina;

import com.util.const_value;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class BalanceMinaClient {

	private String host = const_value.Host;
	private int port = const_value.PORT;
	private IoConnector connector;
	private BalanceHandler balanceHandler;


	private static BalanceMinaClient instance = null;

	public static BalanceMinaClient getInstance() {
		if (instance == null) {
			instance = new BalanceMinaClient();
		}
		return instance;
	}

	public BalanceMinaClient() {
		init_IoConnector();
	}

	/**
	 * 初始化
	 * 
	 */
	private void init_IoConnector() {
		connector = new NioSocketConnector();
		balanceHandler = new BalanceHandler();
		connector.setHandler(balanceHandler);
	}

	/**
	 * 创建
	 * @return
	 */
	private IoSession creat_Connection() {
		try {
			ConnectFuture connFuture = connector.connect(new InetSocketAddress(host, port));
			connFuture.awaitUninterruptibly();
			IoSession session = connFuture.getSession();
			//balanceHandler.sessionOpened(session);		
			//System.out.println("balance---创建--" );
			return session;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 发送命令
	 * @param
	 * @return
	 */
	public boolean send() {
		IoBuffer buffer = IoBuffer.wrap(new byte[] { 0x52, 0x49, 0x50, 0x30 });
		IoSession session = creat_Connection();
		if (session != null) {
			session.write(buffer);
			return true;
		}
		return  false;
	}

	/**
	 * 关闭
	 *
	 * @return
	 */
	public boolean closeConnection(IoSession session) {
		session.closeOnFlush();
		session.getCloseFuture().awaitUninterruptibly();
		//connector.dispose();
		return true;
	}

	public void start() {
		send();

//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		for (int i = 0; i < 10; i++) {
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					send();
//				}
//			}).start();
//		}
	}
}
