package com.bean;

public class Imei {
    private int id;
    private String imei;
    private String pro_company;
    private String use_company;
    private String info;

    public Imei(int id, String imei, String pro_company, String use_company, String info) {

        this.id = id;
        this.imei = imei;
        this.pro_company = pro_company;
        this.use_company = use_company;
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
