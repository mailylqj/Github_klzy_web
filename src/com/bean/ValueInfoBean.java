package com.bean;

/**
 * Created by yyPan on 2017/6/1.
 */

public class ValueInfoBean {

    // 类型 0 开关量 1 数值量 2 报警 3 进度
    private int type;
    // 变量名
    private String name;
    // 值
    private float value;
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
    private String showType;
    //  max
    private int max;
    //  min
    private int min;
    //  排序
    private int index;

    //************** write
    private int writeadd;


    //……………………………………………………


    public ValueInfoBean(int type, String name, float value, String unit, int dataType, int decimals, int rwType, int permission, String showType, int max, int min, int index, int writeadd) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.dataType = dataType;
        this.decimals = decimals;
        this.rwType = rwType;
        this.permission = permission;
        this.showType = showType;
        this.max = max;
        this.min = min;
        this.index = index;
        this.writeadd = writeadd;
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
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

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getWriteadd() {
        return writeadd;
    }

    public void setWriteadd(int writeadd) {
        this.writeadd = writeadd;
    }
}
