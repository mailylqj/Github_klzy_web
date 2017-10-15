package com.application;

import com.bean.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ValueCenter {

    private static ValueCenter instance = new ValueCenter();

    public static ValueCenter getInstance() {
        return instance;
    }

    public ValueCenter(){
        StartTimer();
    }

    //在线列表   自动处理
    List<String> all_OnlineDevice = new ArrayList<String>();

    //实时数据列表  需通过设备列表刷新来删除相关数据
    Map<String, ModbusRecordData> all_LastestData = new ConcurrentHashMap<String, ModbusRecordData>();

    //实时数据解析列表  （同上）
    Map<String, CurData> all_ExData = new ConcurrentHashMap<>();

    //异步返回     （通过session 来删除）
    Map<String, Object> session_deferredResult_map = new ConcurrentHashMap<String, Object>();

    //用户信息  已处理
    Map<String, UserBean> session_userinfo = new ConcurrentHashMap<>();

    //设备列表  已处理
    Map<String, Map<String, DeviceBean>> session_device = new ConcurrentHashMap<>();

    //查询时间  已处理
    Map<String, Map<String, List<Long>>> session_selecttime = new ConcurrentHashMap<>();

    //websocket session   已处理
    Map<String, WebSocketSession> ss_map = new ConcurrentHashMap<>();

    //websocket choseDevice  已处理
    Map<WebSocketSession, List<String>> websoket_map = new ConcurrentHashMap<>();

    //session 超时
    Map<String, Long> session_timeout = new ConcurrentHashMap<>();

    // config list
    Map<String, List<ValueInfoBean>> config_map = new ConcurrentHashMap<>();


    private int RegOnlien = 0;
    private int RegLastestData = 0;

    /******************************** 清理相关数据 **************************************/
    public void StartTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                DataClear();
            }
        }, 100, 30 * 60 * 1000);
    }

    // 冗余数据清理
    private void DataClear() {
        //
        Iterator<Map.Entry<String, ModbusRecordData>> entries01 = all_LastestData.entrySet().iterator();
        while (entries01.hasNext()) {
            Map.Entry<String, ModbusRecordData> entry = entries01.next();
            String key = entry.getKey();
            if (!all_OnlineDevice.contains(key))
                all_LastestData.remove(key);
        }
        //
        Iterator<Map.Entry<String, CurData>> entries02 = all_ExData.entrySet().iterator();
        while (entries02.hasNext()) {
            Map.Entry<String, CurData> entry = entries02.next();
            String key = entry.getKey();
            if (!all_OnlineDevice.contains(key))
                all_LastestData.remove(key);
        }
        //session
        Iterator<Map.Entry<String, Long>> entries03 = session_timeout.entrySet().iterator();
        long curtime = System.currentTimeMillis();
        while (entries03.hasNext()) {
            Map.Entry<String, Long> entry = entries03.next();
            String key = entry.getKey();
            long value = entry.getValue();
            if ((curtime-value)>=1000*60*30){
                session_userinfo.remove(key);
                session_selecttime.remove("mdata" + "#" +key);

                //websocket 处理
                WebSocketSession  webSocketSession =  ss_map.get(key);
                websoket_map.remove(webSocketSession);
                ss_map.remove(key);
                session_timeout.remove(key);

                //异步
                session_deferredResult_map.remove( "login" + "#" +key);
                session_deferredResult_map.remove( "dlist" + "#" +key);
                session_deferredResult_map.remove( "config" + "#" +key);
                session_deferredResult_map.remove( "control" + "#" +key);
                session_deferredResult_map.remove( "mdata" + "#" +key);
                session_deferredResult_map.remove( "cdata" + "#" +key);
                session_deferredResult_map.remove( "adata" + "#" +key);
            }
        }
    }


    /****************************** get set 方法 ************************************/

    public Map<String, List<ValueInfoBean>> getConfig_map() {
        return config_map;
    }

    public void setConfig_map(Map<String, List<ValueInfoBean>> config_map) {
        this.config_map = config_map;
    }

    public Map<String, WebSocketSession> getSs_map() {
        return ss_map;
    }

    public void setSs_map(Map<String, WebSocketSession> ss_map) {
        this.ss_map = ss_map;
    }

    public Map<WebSocketSession, List<String>> getWebsoket_map() {
        return websoket_map;
    }

    public void setWebsoket_map(Map<WebSocketSession, List<String>> websoket_map) {
        this.websoket_map = websoket_map;
    }


    public Map<String, Map<String, List<Long>>> getSession_selecttime() {
        return session_selecttime;
    }

    public void setSession_selecttime(Map<String, Map<String, List<Long>>> session_selecttime) {
        this.session_selecttime = session_selecttime;
    }

    public Map<String, Long> getSession_timeout() {
        return session_timeout;
    }

    public void setSession_timeout(Map<String, Long> session_timeout) {
        this.session_timeout = session_timeout;
    }

    public Map<String, CurData> getAll_ExData() {
        return all_ExData;
    }

    public void setAll_ExData(Map<String, CurData> all_ExData) {
        this.all_ExData = all_ExData;
    }

    public int getRegOnlien() {
        return RegOnlien;
    }

    public void setRegOnlien(int regOnlien) {
        RegOnlien = regOnlien;
    }

    public int getRegLastestData() {
        return RegLastestData;
    }

    public void setRegLastestData(int regLastestData) {
        RegLastestData = regLastestData;
    }

    public Map<String, Map<String, DeviceBean>> getSession_device() {
        return session_device;
    }

    public void setSession_device(Map<String, Map<String, DeviceBean>> session_device) {
        this.session_device = session_device;
    }

    public Map<String, UserBean> getSession_userinfo() {
        return session_userinfo;
    }

    public void setSession_userinfo(Map<String, UserBean> session_userinfo) {
        this.session_userinfo = session_userinfo;
    }

    public Map<String, Object> getSession_deferredResult_map() {
        return session_deferredResult_map;
    }

    public void setSession_deferredResult_map(Map<String, Object> session_deferredResult_map) {
        this.session_deferredResult_map = session_deferredResult_map;
    }

    public List<String> getAll_OnlineDevice() {
        return all_OnlineDevice;
    }

    public void setAll_OnlineDevice(List<String> all_OnlineDevice) {
        synchronized (this.all_OnlineDevice) {
            this.all_OnlineDevice = all_OnlineDevice;
        }
    }

    public Map<String, ModbusRecordData> getAll_LastestData() {
        return all_LastestData;
    }

    public void setAll_LastestData(Map<String, ModbusRecordData> all_LastestData) {
        this.all_LastestData.clear();
        this.all_LastestData.putAll(all_LastestData);
    }
}
