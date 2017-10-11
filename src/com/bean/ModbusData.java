package com.bean;

import com.google.protobuf.ByteString;

import java.util.List;
import java.util.Map;

public class ModbusData {
    String uid;
    long time;
    List<ShowMValueInfoBean> value_list;

    /*************************************************/
    public ModbusData(String uid, long time, List<ShowMValueInfoBean> value_list) {
        this.uid = uid;
        this.time = time;
        this.value_list = value_list;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<ShowMValueInfoBean> getValue_list() {
        return value_list;
    }

    public void setValue_list(List<ShowMValueInfoBean> value_list) {
        this.value_list = value_list;
    }
}