package com.mina;


import com.ideal.logic.logic;
import com.ideal.logic.logic_imp;
import com.pack.RPCPackage;
import com.pack.ReceiveCmd;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class RealHandler extends IoHandlerAdapter {
	private ReceiveCmd receive =ReceiveCmd.getInstance();

	@Override
	public synchronized void messageReceived(IoSession iosession, Object message) throws Exception {
		IoBuffer buffer = (IoBuffer) message;
		//System.out.println("real——客户端收到消息 "+buffer.limit()+"  " + Thread.currentThread().getId() );
		logic_imp imp = new logic_imp();
		com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
		RPCPackage.RPCProtoData proto_data = new RPCPackage.RPCProtoData();
		boolean ret = RPCPackage.UnPackageFrontProtoData(proto_data, buffer, rpc_service);
		if (ret) {
			final com.google.protobuf.Descriptors.MethodDescriptor method = rpc_service.getDescriptorForType().getMethods().get(proto_data.method_index);
			receive.receiveMsg(method, proto_data.message,iosession);
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		//System.out.println("real——客户端异常  "+cause);
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageSent(IoSession iosession, Object obj) throws Exception {
		//System.out.println("real——客户端消息发送");
		super.messageSent(iosession, obj);
	}

	@Override
	public void sessionClosed(IoSession iosession) throws Exception {
		//System.out.println("real——客户端会话关闭");
		super.sessionClosed(iosession);
	}

	@Override
	public void sessionCreated(IoSession iosession) throws Exception {
		//System.out.println("real——客户端会话创建");
		super.sessionCreated(iosession);
	}

	@Override
	public void sessionIdle(IoSession iosession, IdleStatus idlestatus) throws Exception {
		//System.out.println("real——客户端会话休眠");
		super.sessionIdle(iosession, idlestatus);
	}

	@Override
	public void sessionOpened(IoSession iosession) throws Exception {
		//System.out.println("real——客户端会话打开");
		super.sessionOpened(iosession);
	}

}
