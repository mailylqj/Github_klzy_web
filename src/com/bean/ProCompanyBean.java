package com.bean;

import java.util.List;

public class ProCompanyBean {
    private int id;
    private String pro_company ;

    public ProCompanyBean(int id, String pro_company) {
        this.id = id;
        this.pro_company = pro_company;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPro_company() {
        return pro_company;
    }

    public void setPro_company(String pro_company) {
        this.pro_company = pro_company;
    }
}
