package com.bean;

/**
 * Created by yyPan on 2017/6/1.
 */

public class ShowMValueInfoBean {

    // 变量名
    private String name;
    // 值
    private float value;
    // 单位
    private String unit;



    public ShowMValueInfoBean(String name, float value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
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
}
