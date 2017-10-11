package com.bean;

import com.sun.istack.internal.NotNull;

import java.util.List;

public class UserBean {
    @NotNull
    String username;
    @NotNull
    String password;

    int type;
    String pro_company;
    String use_company;
    int level;
    String imei;



    public UserBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserBean(String username, String password, int type, String pro_company, String use_company, int level, String imei) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.pro_company = pro_company;
        this.use_company = use_company;
        this.level = level;
        this.imei = imei;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", pro_company='" + pro_company + '\'' +
                ", use_company='" + use_company + '\'' +
                ", level=" + level +
                ", imei='" + imei + '\'' +
                '}';
    }

    public UserBean() {
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPro_company() {
        return pro_company;
    }

    public void setPro_company(String pro_company) {
        this.pro_company = pro_company;
    }

    public String getUse_company() {
        return use_company;
    }

    public void setUse_company(String use_company) {
        this.use_company = use_company;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
