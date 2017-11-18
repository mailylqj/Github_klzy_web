package com.bean;

public class Level {

    private  int id;
    private String levelname;

    public Level(int id, String levelname) {
        this.id = id;
        this.levelname = levelname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLevelname() {
        return levelname;
    }

    public void setLevelname(String levelname) {
        this.levelname = levelname;
    }
}
