package com.springmvc.websock;

import org.apache.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class HandshakeInterceptor implements org.springframework.web.socket.server.HandshakeInterceptor {

    //初次握手访问前
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            //ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            //HttpSession   session  = servletRequest.getServletRequest().getSession(false);
            String url  = request.getURI().getPath();
            String uuid  = url.substring(11,url.length());
            //if (session != null)
            map.put("uuid",uuid);
            return true;
        }
        return  true;
    }

    //初次握手访问后
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
         //System.err.println("有人访问了：" + serverHttpRequest.getRemoteAddress());
    }
}