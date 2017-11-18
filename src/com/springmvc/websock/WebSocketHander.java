package com.springmvc.websock;


import com.application.ValueCenter;
import com.bean.CurData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.web.socket.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.*;


public class WebSocketHander implements WebSocketHandler {
    private Logger log = Logger.getLogger(getClass());
    private Thread updataUserData = null;
    private ValueCenter valueCenter = ValueCenter.getInstance();

    //初次链接成功执行
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String device_id = (String) webSocketSession.getAttributes().get("uuid");
        valueCenter.getWebsocket_map().put(webSocketSession, device_id);

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

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        valueCenter.getWebsocket_map().remove(webSocketSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        valueCenter.getWebsocket_map().remove(webSocketSession);
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
        Iterator<Map.Entry<WebSocketSession, String>> it = valueCenter.getWebsocket_map().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<WebSocketSession, String> entry = it.next();
            WebSocketSession session = entry.getKey();
            if (session.isOpen()) {
                String uid = entry.getValue();
                CurData data = curData.get(uid);
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    StringWriter strwriter = new StringWriter();
                    mapper.writeValue(strwriter,data);
                    String json_str = strwriter.toString();

                    if (!json_str.equals("null"))
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
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



