package com.bean;

public class ShowUserBean {
    int id;
    String username;
    String password;
    String pro_company;
    String use_company;
    String level_name;
    int level;

    public ShowUserBean(int id, String username, String password, String pro_company, String use_company, String level_name,  int level) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.pro_company = pro_company;
        this.use_company = use_company;
        this.level_name = level_name;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getLevel_name() {
        return level_name;
    }

    public void setLevel_name(String level_name) {
        this.level_name = level_name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
