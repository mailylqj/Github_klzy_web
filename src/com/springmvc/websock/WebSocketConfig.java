package com.springmvc.websock;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket//开启websocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHander02(), "/ws/socket02").addInterceptors((new HandshakeInterceptor())).setAllowedOrigins("*");
        registry.addHandler(new WebSocketHander01(), "/ws/socket01").addInterceptors(new HandshakeInterceptor()).setAllowedOrigins("*");

    }
}
