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


public class WebSocketHander01 implements WebSocketHandler {
    private Logger log = Logger.getLogger(getClass());
    private Thread updataUserData = null;
    private ValueCenter valueCenter = ValueCenter.getInstance();

    //初次链接成功执行
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        log.info("链接成功......");
        String sessionId = (String) webSocketSession.getAttributes().get("SESSION_ID");
        valueCenter.getSs_map().put(sessionId, webSocketSession);

        valueCenter.getWebsoket_map().put(webSocketSession, new ArrayList<String>());
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
            updataUserData.start();
        }
    }

    //接受消息处理消息
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

        sendMessageToUser(webSocketSession, new TextMessage(webSocketMessage.getPayload() + "返回！01"));
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        valueCenter.getWebsoket_map().remove(webSocketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        log.info("链接关闭......" + closeStatus.toString());
        valueCenter.getWebsoket_map().remove(webSocketSession);
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
        Iterator<Map.Entry<WebSocketSession, List<String>>> it = valueCenter.getWebsoket_map().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<WebSocketSession, List<String>> entry = it.next();
            WebSocketSession session = entry.getKey();
            List<CurData> return_list = new ArrayList<>();
            if (session.isOpen()) {
                List<String> list = entry.getValue();
                for (String uid : list) {
                    CurData data = curData.get(uid);
                    if (data != null) {
                        return_list.add(data);
                    }
                }
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String json_str = mapper.writeValueAsString(return_list);
                    if (!json_str.equals("[]"))
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
        List<String> list = valueCenter.getWebsoket_map().get(session);
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



