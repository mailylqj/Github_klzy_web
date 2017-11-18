package com.bean;

public class Token {

    String token;
    String uuid;

    public Token(String token, String uuid) {
        this.token = token;
        this.uuid = uuid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
