package com.bean;

public class ConfigBean {
    private int id;
    private String pro_company;
    private String use_company;
    private String filename;
    // 类型 0 开关量 1 数值量 2 报警 3 进度
    private int type;
    // 变量名
    private String name;
    // 单位
    private String unit;
    // 数据类型
    private int dataType;
    // 小数位数
    private int decimals;
    // 读写类型
    private int rwType;
    //  权限
    private int permission;

    //************** new Add
    //  显示类型  // 1  bit  2 int  3 progress 4 arcprogress 5 temp 6 rota 7 curve
    private String showName;
    //  max
    private int max;
    //  min
    private int min;

    public ConfigBean(int id, String pro_company, String use_company, String filename, int type, String name,
                      String unit, int dataType, int decimals, int rwType, int permission, String showname, int max, int min) {
        this.id = id;
        this.pro_company = pro_company;
        this.use_company = use_company;
        this.filename = filename;
        this.type = type;
        this.name = name;
        this.unit = unit;
        this.dataType = dataType;
        this.decimals = decimals;
        this.rwType = rwType;
        this.permission = permission;
        this.showName = showname;
        this.max = max;
        this.min = min;
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

    public String getUse_company() {
        return use_company;
    }

    public void setUse_company(String use_company) {
        this.use_company = use_company;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public int getRwType() {
        return rwType;
    }

    public void setRwType(int rwType) {
        this.rwType = rwType;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
