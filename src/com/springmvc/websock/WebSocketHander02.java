package com.springmvc.websock;


import com.application.ValueCenter;
import com.bean.CurData;
import com.bean.ModbusData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;


public class WebSocketHander02 implements WebSocketHandler {
    private Logger log = Logger.getLogger(getClass());
    private Map<WebSocketSession, List<String>> websoket_map = new HashMap<>();
    private Thread updataUserData = null;


    //初次链接成功执行
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        log.info("链接成功02......");
        websoket_map.put(webSocketSession, new ArrayList<String>());

        if (updataUserData == null) {
            updataUserData = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            sendMessageToAllUsers();
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
           // updataUserData.start();
        }
    }

    //接受消息处理消息
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        sendMessageToUser(webSocketSession, new TextMessage(webSocketMessage.getPayload() + "返回！02"));
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        websoket_map.remove(webSocketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        log.info("链接关闭......" + closeStatus.toString());
        websoket_map.remove(webSocketSession);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    /**
     * 给所有在线用户发送消息
     */
    public void sendMessageToAllUsers() {
        Map<String, CurData> curData = ValueCenter.getInstance().getAll_ExData();
        Iterator<Map.Entry<WebSocketSession, List<String>>> it = websoket_map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<WebSocketSession, List<String>> entry = it.next();
            WebSocketSession session = entry.getKey();
            Map<String, CurData> return_map = new HashMap<>();
            if (session.isOpen()) {
                List<String> list = entry.getValue();
                for (String uid : list) {
                    CurData data = curData.get(uid);
                    if (data != null) {
                        return_map.put(uid, data);
                    }
                }
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String json_str = mapper.writeValueAsString(return_map);
                    session.sendMessage(new TextMessage(json_str));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param
     * @param message
     */
    public void sendMessageToUser(WebSocketSession session, TextMessage message) {
        List<String> list = websoket_map.get(session);
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



