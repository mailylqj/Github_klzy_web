package com.bean;

import com.google.protobuf.ByteString;

import java.util.List;
import java.util.Map;

public class ModbusData {

    long time;
    List<ShowMValueInfoBean> value_list;

    /*************************************************/
    public ModbusData(long time, List<ShowMValueInfoBean> value_list) {

        this.time = time;
        this.value_list = value_list;
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