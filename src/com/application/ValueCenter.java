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
    Map<String, Object> uuid_deferredResult = new ConcurrentHashMap<String, Object>();

    //用户信息  已处理
    Map<String, UserBean> uuid_userinfo = new ConcurrentHashMap<>();

    //查询时间  已处理
    Map<String,SelectInfoBean> session_selecttime = new ConcurrentHashMap<>();

    //websocket choseDevice  已处理
    Map<WebSocketSession, String> websocket_map = new ConcurrentHashMap<>();

    // token_map
    Map<String, Boolean> token_map = new ConcurrentHashMap<>();

    // 定时清理
    Map<String,Long>  time_map  = new ConcurrentHashMap<>();

    // config list
    Map<String, List<ValueInfoBean>> uuid_config = new ConcurrentHashMap<>();

    // 保存的  通过用户读取的设备
    Map<String,List<Integer>> uuid_user_device_chose = new ConcurrentHashMap<>();

    // 保存的  通过设备关联用户
    Map<String,List<Integer>> uuid_device_imei = new ConcurrentHashMap<>();

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
        //uuid
        Iterator<Map.Entry<String, Long>> entries03 = time_map.entrySet().iterator();
        long curtime = System.currentTimeMillis();
        while (entries03.hasNext()) {
            Map.Entry<String, Long> entry = entries03.next();
            String key = entry.getKey();
            long value = entry.getValue();
            if ((curtime-value)>=1000*60*30){
                uuid_userinfo.remove(key);
                session_selecttime.remove(key);
                uuid_deferredResult.remove(key);
                uuid_config.remove(key);
                uuid_device_imei.remove(key);
                uuid_user_device_chose.remove(key);
                time_map.remove(key);
            }
        }
    }




    /****************************** get set 方法 ************************************/


    public Map<WebSocketSession, String> getWebsocket_map() {
        return websocket_map;
    }

    public void setWebsocket_map(Map<WebSocketSession, String> websocket_map) {
        this.websocket_map = websocket_map;
    }

    public Map<String, List<Integer>> getUuid_user_device_chose() {
        return uuid_user_device_chose;
    }

    public void setUuid_user_device_chose(Map<String, List<Integer>> uuid_user_device_chose) {
        this.uuid_user_device_chose = uuid_user_device_chose;
    }

    public Map<String, List<Integer>> getUuid_device_imei() {
        return uuid_device_imei;
    }

    public void setUuid_device_imei(Map<String, List<Integer>> uuid_device_imei) {
        this.uuid_device_imei = uuid_device_imei;
    }

    public Map<String, SelectInfoBean> getUuid_selecttime() {
        return session_selecttime;
    }

    public void setSession_selecttime(Map<String, SelectInfoBean> session_selecttime) {
        this.session_selecttime = session_selecttime;
    }

    public Map<String, List<ValueInfoBean>> getUuid_config() {
        return uuid_config;
    }

    public void setUuid_config(Map<String, List<ValueInfoBean>> uuid_config) {
        this.uuid_config = uuid_config;
    }

    public Map<String, Boolean> getToken_map() {     return token_map;    }

    public void setToken_map(Map<String, Boolean> token_map) {
        this.token_map = token_map;
    }

    public Map<String, CurData> getAll_ExData() {
        return all_ExData;
    }

    public void setAll_ExData(Map<String, CurData> all_ExData) {
        this.all_ExData = all_ExData;
    }


    public int getRegLastestData() {
        return RegLastestData;
    }

    public void setRegLastestData(int regLastestData) {
        RegLastestData = regLastestData;
    }

//    public Map<String, Map<String, DeviceBean>> getSession_device() {
//        return session_device;
//    }
//
//    public void setSession_device(Map<String, Map<String, DeviceBean>> session_device) {
//        this.session_device = session_device;
//    }


    public Map<String, UserBean> getUuid_userinfo() {
        return uuid_userinfo;
    }

    public void setUuid_userinfo(Map<String, UserBean> uuid_userinfo) {
        this.uuid_userinfo = uuid_userinfo;
    }

    public Map<String, Object> getUuid_deferredResult() {
        return uuid_deferredResult;
    }

    public void setUuid_deferredResult(Map<String, Object> uuid_deferredResult) {
        this.uuid_deferredResult = uuid_deferredResult;
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

    public Map<String, Long> getTime_map() {
        return time_map;
    }

    public void setTime_map(Map<String, Long> time_map) {
        this.time_map = time_map;
    }
}
