package com.mina;



import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class BalanceHandler extends IoHandlerAdapter {

	@Override
	public synchronized  void  messageReceived(IoSession iosession, Object message) throws Exception {
		IoBuffer buffer = (IoBuffer) message;
		buffer.position(5);
		String ipString = String.valueOf(buffer.get() & 0xFF)+"."+String.valueOf(buffer.get() & 0xFF)+"."+String.valueOf(buffer.get() & 0xFF)+"."+String.valueOf(buffer.get() & 0xFF);
		int port  = (int)((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
		RealMinaClient.getInstance().set_ConnectionInfo(ipString, port);
		//System.out.println("balance------------------------客户端收到消息  ThreadID"+ipString+" "+port+" "+Thread.currentThread().getId());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		System.out.println("balance——客户端异常");
		super.exceptionCaught(session, cause);

	}

	@Override
	public void messageSent(IoSession iosession, Object obj) throws Exception {
		//System.out.println("balance——客户端消息发送  Thread"+ Thread.currentThread().getId());
		super.messageSent(iosession, obj);

	}

	@Override
	public void sessionClosed(IoSession iosession) throws Exception {
		System.out.println("balance——客户端会话关闭");
		super.sessionClosed(iosession);

	}

	@Override
	public void sessionCreated(IoSession iosession) throws Exception {
		//System.out.println("balance——客户端会话创建");
		super.sessionCreated(iosession);
	}

	@Override
	public void sessionIdle(IoSession iosession, IdleStatus idlestatus) throws Exception {
		System.out.println("balance——客户端会话休眠");
		super.sessionIdle(iosession, idlestatus);
	}

	@Override
	public void sessionOpened(IoSession iosession) throws Exception {
		//System.out.println("balance——客户端会话打开");
		super.sessionOpened(iosession);
	}

}
