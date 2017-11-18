package com.bean;

import com.bean.ModbusData;

import java.util.List;

public class RetModbusData {
    String uid;
    int countpage;
    int curpage;
    List<ModbusData> data;

    public RetModbusData(String uid, int countpage, int curpage, List<ModbusData> data) {

        this.uid = uid;
        this.countpage = countpage;
        this.curpage = curpage;
        this.data = data;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCountpage() {
        return countpage;
    }

    public void setCountpage(int countpage) {
        this.countpage = countpage;
    }

    public int getCurpage() {
        return curpage;
    }

    public void setCurpage(int curpage) {
        this.curpage = curpage;
    }

    public List<ModbusData> getData() {
        return data;
    }

    public void setData(List<ModbusData> data) {
        this.data = data;
    }
}

