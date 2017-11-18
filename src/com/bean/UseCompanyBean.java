package com.bean;

public class UseCompanyBean {
    private int id;
    private String use_company;
    private int pro_company_id;

    public UseCompanyBean(int id, String use_company, int pro_company_id) {
        this.id = id;
        this.use_company = use_company;
        this.pro_company_id = pro_company_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUse_company() {
        return use_company;
    }

    public void setUse_company(String use_company) {
        this.use_company = use_company;
    }

    public int getPro_company_id() {
        return pro_company_id;
    }

    public void setPro_company_id(int pro_company_id) {
        this.pro_company_id = pro_company_id;
    }
}
