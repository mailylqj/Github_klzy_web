package com.bean;

import java.util.List;

public class SelectInfoBean {
    String uid;
    long  starttime;
    long  endtime;
    long  curtime;
    List<Long> time_list;

    public SelectInfoBean(String uid, long starttime, long endtime, long curtime, List<Long> time_list) {
        this.uid = uid;
        this.starttime = starttime;
        this.endtime = endtime;
        this.curtime = curtime;
        this.time_list = time_list;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public long getCurtime() {
        return curtime;
    }

    public void setCurtime(long curtime) {
        this.curtime = curtime;
    }

    public List<Long> getTime_list() {
        return time_list;
    }

    public void setTime_list(List<Long> time_list) {
        this.time_list = time_list;
    }
}
