package com.bean;

public class NormalDeviceSelect {
    private int id;
    private String device_id;
    private int type;
    private int cmd_len;
    private int loop_time;
    private String pro_company;
    private String use_company;
    private double longitude;
    private double latitude;
    private String device_name;
    private String configfile_name;
    private int ischeck;
    private long creat_date;
    private long allow_date;

    public NormalDeviceSelect(int id,String device_id, int type, int cmd_len, int loop_time, String pro_company, String use_company, double longitude, double latitude, String device_name, String configfile_name, int ischeck, long creat_date, long allow_date) {
        this.id = id;
        this.device_id = device_id;
        this.type = type;
        this.cmd_len = cmd_len;
        this.loop_time = loop_time;
        this.pro_company = pro_company;
        this.use_company = use_company;
        this.longitude = longitude;
        this.latitude = latitude;
        this.device_name = device_name;
        this.configfile_name = configfile_name;
        this.ischeck = ischeck;
        this.creat_date = creat_date;
        this.allow_date = allow_date;
    }

    public NormalDeviceSelect(int id,String device_id, int type, int cmd_len, int loop_time,String pro_company, String use_company, double longitude, double latitude, String device_name, String configfile_name, long creat_date) {
        this.id = id;
        this.device_id = device_id;
        this.type = type;
        this.cmd_len = cmd_len;
        this.loop_time = loop_time;
        this.pro_company = pro_company;
        this.use_company = use_company;
        this.longitude = longitude;
        this.latitude = latitude;
        this.device_name = device_name;
        this.configfile_name = configfile_name;
        this.creat_date = creat_date;
    }

    public NormalDeviceSelect(int id,String device_id, int type, int cmd_len, int loop_time,  double longitude, double latitude, String device_name, String configfile_name, long allow_date) {
        this.id = id;
        this.device_id = device_id;
        this.type = type;
        this.cmd_len = cmd_len;
        this.loop_time = loop_time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.device_name = device_name;
        this.configfile_name = configfile_name;
        this.allow_date = allow_date;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCmd_len() {
        return cmd_len;
    }

    public void setCmd_len(int cmd_len) {
        this.cmd_len = cmd_len;
    }

    public int getLoop_time() {
        return loop_time;
    }

    public void setLoop_time(int loop_time) {
        this.loop_time = loop_time;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getConfigfile_name() {
        return configfile_name;
    }

    public void setConfigfile_name(String configfile_name) {
        this.configfile_name = configfile_name;
    }

    public int getIscheck() {
        return ischeck;
    }

    public void setIscheck(int ischeck) {
        this.ischeck = ischeck;
    }

    public long getCreat_date() {
        return creat_date;
    }

    public void setCreat_date(long creat_date) {
        this.creat_date = creat_date;
    }

    public long getAllow_date() {
        return allow_date;
    }

    public void setAllow_date(long allow_date) {
        this.allow_date = allow_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
