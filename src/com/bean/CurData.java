package com.bean;

import java.util.List;

public class CurData {
    String uid;
    long time;
    List<ShowCValueInfoBean> value_list;
    List<String> alarm_list;

    /*************************************************/
    public CurData(String uid, long time, List<ShowCValueInfoBean> value_list, List<String> alarm_list) {
        this.uid = uid;
        this.time = time;
        this.value_list = value_list;
        this.alarm_list = alarm_list;
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

    public List<ShowCValueInfoBean> getValue_list() {
        return value_list;
    }

    public void setValue_list(List<ShowCValueInfoBean> value_list) {
        this.value_list = value_list;
    }

    public List<String> getAlarm_list() {
        return alarm_list;
    }

    public void setAlarm_list(List<String> alarm_list) {
        this.alarm_list = alarm_list;
    }
}