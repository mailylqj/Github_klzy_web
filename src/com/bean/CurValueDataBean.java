package com.bean;

import java.util.List;

/**
 * Created by yyPan on 2017/6/21.
 */

public class CurValueDataBean {
    private  String  time;
    private  String  uidstr;
    private  List<Float> datalist;

    public CurValueDataBean(String time, String uidstr, List<Float> datalist) {
        this.time = time;
        this.uidstr = uidstr;
        this.datalist = datalist;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUidstr() {
        return uidstr;
    }

    public void setUidstr(String uidstr) {
        this.uidstr = uidstr;
    }

    public List<Float> getDatalist() {
        return datalist;
    }

    public void setDatalist(List<Float> datalist) {
        this.datalist = datalist;
    }
}
