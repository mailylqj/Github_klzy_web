package com.mina;

import com.application.ValueCenter;
import com.pack.RPCPackageDecoder;
import com.pack.RPCPackageEncoder;
import com.pack.SendCmd;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

public class RealMinaClient {

    private String host = null;
    private int port = 0;
    private IoConnector connector;
    private RealHandler clientHandler;
    private Deque<IoSession> sessions_deque = new LinkedList<>();
    private ValueCenter valueCenter  = ValueCenter.getInstance();
    private Thread regweb_thread  = null;
    private Logger log = Logger.getLogger(getClass());
    private static RealMinaClient instance = new RealMinaClient();

    public static RealMinaClient getInstance() {
        return instance;
    }

    public Deque<IoSession> getSessions_deque() {
        return sessions_deque;
    }

    public RealMinaClient() {
        init_IoConnector();
    }

    public void set_ConnectionInfo(String host, int port) {
        this.host = host;
        this.port = port;

        for (int i=0;i<5;i++){
            IoSession session = creat_Connection();
            if (session != null) {
                sessions_deque.offerLast(session);
                log.info("real---创建--");
            }
        }
    }

    /**
     *
     */
    public void regThreadStart(){
        if (regweb_thread ==  null){
            regweb_thread =  new regWeb_Thread();
            regweb_thread.start();
        }
    }

    /**
     * 初始化
     */
    private void init_IoConnector() {
        connector = new NioSocketConnector(10);
        clientHandler = new RealHandler();
        connector.setHandler(clientHandler);
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        chain.addLast("codec", new ProtocolCodecFilter(new RPCPackageEncoder(), new RPCPackageDecoder()));
    }

    /**
     * 创建
     *
     * @return
     */
    private IoSession creat_Connection() {
        try {
            if (host!= null && port != 0) {
                ConnectFuture connFuture = connector.connect(new InetSocketAddress(host, port));
                connFuture.awaitUninterruptibly();
                IoSession session = connFuture.getSession();
                SocketSessionConfig config = (SocketSessionConfig) connector.getSessionConfig();
                config.setReadBufferSize(100 * 1024);
                config.setIdleTime(IdleStatus.BOTH_IDLE, 300);
                //clientHandler.sessionOpened(session);
                return session;
            }else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送命令
     *
     * @param b
     * @return
     */
    public boolean send(ByteBuffer b) {
        IoBuffer buffer = IoBuffer.wrap(b);
        IoSession session = sessions_deque.pollFirst();

        if (session != null) {
            session.write(buffer);
            return true;
        }else{
            BalanceMinaClient.getInstance().start();
            try {
                Thread.sleep(500);
                session = sessions_deque.pollFirst();
                if (session != null) {
                    session.write(buffer);
                    return true;
                }else
                    return false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        return false;
    }

    public class regWeb_Thread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (valueCenter.getRegLastestData() == 0) {
                    SendCmd.getIntance().reg_server_lastestdata();
                    log.info(" ！------  实时数据连接！");
                }else{
                    valueCenter.setRegLastestData(valueCenter.getRegLastestData()-5);
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
