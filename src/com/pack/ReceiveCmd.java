package com.pack;


import com.application.ValueCenter;
import com.bean.*;
import com.bean.RetModbusData;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Message;
import com.ideal.logic.alarm_data.alarm_data_info;
import com.ideal.logic.alarm_data.read_alarm_data_response;
import com.ideal.logic.control_data.control_data_info;
import com.ideal.logic.control_data.control_response;
import com.ideal.logic.control_data.read_control_data_response;
import com.ideal.logic.modbus_data.data_info;
import com.ideal.logic.modbus_data.read_data_response;
import com.ideal.logic.server_lastestdata.lastestdata_info;
import com.ideal.logic.server_lastestdata.server_lastestdata_response;
import com.ideal.logic.server_lastestdata.unreg_lastestdata_response;
import com.ideal.logic.server_online.online_info;
import com.ideal.logic.server_online.server_online_response;
import com.ideal.logic.server_online.unreg_online_response;
import com.mina.RealMinaClient;
import com.mysql.SqlCmd;
import com.util.ErrorCode;
import com.util.Helper;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;

public class ReceiveCmd {
    private ValueCenter valueCenter = ValueCenter.getInstance();
    private static ReceiveCmd instance = null;
    private Logger log = Logger.getLogger(getClass());

    public static ReceiveCmd getInstance() {
        if (instance == null) {
            instance = new ReceiveCmd();
        }
        return instance;
    }

    public void receiveMsg(MethodDescriptor method, Message message, IoSession session) {
        if (method.getName().equalsIgnoreCase("command_control")) {
            control_response data_rep = (control_response) message;
            command_Control(data_rep);
        } else if (method.getName().equalsIgnoreCase("modbus_read")) {
            read_data_response data_rep = (read_data_response) message;
            readModData(data_rep);
        } else if (method.getName().equalsIgnoreCase("controlDataRead")) {
            read_control_data_response data_rep = (read_control_data_response) message;
            readControlData(data_rep);
        } else if (method.getName().equalsIgnoreCase("alarmDataRead")) {
            read_alarm_data_response data_rep = (read_alarm_data_response) message;
            readAlarmData(data_rep);
        } else if (method.getName().equalsIgnoreCase("regServerLastestdata")) {
            server_lastestdata_response data_rep = (server_lastestdata_response) message;
            alllastestdata(data_rep);
            return;
        } else if (method.getName().equalsIgnoreCase("regServerOnline")) {
            server_online_response data_rep = (server_online_response) message;
            allonlinedevice(data_rep);
            return;
        } else if (method.getName().equalsIgnoreCase("unregServerLastestdata")) {
            unreg_lastestdata_response data_rep = (unreg_lastestdata_response) message;
            unreg_alllast(data_rep);
        } else if (method.getName().equalsIgnoreCase("unregServerOnline")) {
            unreg_online_response data_rep = (unreg_online_response) message;
            unreg_allonline(data_rep);
        }

        RealMinaClient.getInstance().getSessions_deque().offerLast(session);
    }

    private void alllastestdata(server_lastestdata_response data_rep) {
        Map<String, ModbusRecordData> modbus_map = valueCenter.getAll_LastestData();
        if (data_rep.getResult() == 0) {
            int count = data_rep.getInfoCount();
            for (int i = 0; i < count; i++) {
                lastestdata_info info = data_rep.getInfo(i);
                String uid = info.getInfo().toStringUtf8();
                long time = info.getTime();
                ByteString msg = info.getModbusMsg();
                ByteString alarmcode = info.getAlarmcode();
                ModbusRecordData cmp_data = modbus_map.get(uid);

                if (cmp_data == null) {
                    ModbusRecordData data = new ModbusRecordData();
                    data.setRecord_time(time);
                    data.setModbus_msg(msg);
                    data.setAlarm_code(alarmcode);
                    modbus_map.put(uid, data);
                    Helper.getInstance().ExCurData(uid, data);
                } else {
                    if (cmp_data.getModbus_msg().equals(msg) && cmp_data.equals(alarmcode)) {
                        cmp_data.setRecord_time(time);
                    } else {
                        ModbusRecordData data = new ModbusRecordData();
                        data.setRecord_time(time);
                        data.setModbus_msg(msg);
                        data.setAlarm_code(alarmcode);
                        modbus_map.put(uid, data);
                        Helper.getInstance().ExCurData(uid, data);
                    }
                }
            }
            valueCenter.setRegLastestData(10);
            //log.info("实时数据---------------"+count+"---"+System.currentTimeMillis());
        }
    }

    private void unreg_alllast(unreg_lastestdata_response data_rep) {
        if (data_rep.getResult() == 0) {

        } else {

        }
    }

    private void allonlinedevice(server_online_response data_rep) {
        if (data_rep.getResult() == 0) {
            List<String> list = new ArrayList<String>();
            int count = data_rep.getInfoCount();
            for (int i = 0; i < count; i++) {
                online_info info = data_rep.getInfo(i);
                list.add(info.getUid());
            }
            List<String> src_list = new ArrayList<>();
            src_list = valueCenter.getAll_OnlineDevice();
            valueCenter.setAll_OnlineDevice(list);
            log.info(Thread.currentThread().getId() + " ------当前在线设备数:" + count);

            Map<String, CurData> all_ExData = valueCenter.getAll_ExData();
            int size = src_list.size();
            for (int j = 0; j < size; j++) {
                String uid = src_list.get(j);
                if (!list.contains(uid)) {
                    all_ExData.remove(uid);
                }
            }

        }
    }

    private void unreg_allonline(unreg_online_response data_rep) {
        if (data_rep.getResult() == 0) {

        } else {

        }
    }

    public void command_Control(control_response data_rep) {
        String token_info = data_rep.getSessionid();
        int pos = token_info.indexOf("#");
        String uuid = token_info.substring(0, pos);
        String token_str = token_info.substring(pos + 1, token_info.length());
        Token token = new Token(token_str, uuid);
        String key = "control" + "#" + uuid;
        DeferredResult<Object> result = (DeferredResult<Object>) valueCenter.getUuid_deferredResult().get(key);

        if (data_rep.getResult() == 0) {
            Helper.getInstance().DefReturn(result, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, null);
        } else {
            Helper.getInstance().DefReturn(result, ErrorCode.FAILURE, ErrorCode.FAILURE_STR, token, null);
        }
        valueCenter.getUuid_deferredResult().remove(key);
    }

    public void readModData(read_data_response data_rep) {
        String token_info = data_rep.getSessionid();
        int pos = token_info.indexOf("#");
        String uuid = token_info.substring(0, pos);
        String token_str = token_info.substring(pos + 1, token_info.length());
        Token token = new Token(token_str, uuid);
        String key = "mdata" + "#" + uuid;
        long date = 0;
        String uid = data_rep.getUid().toStringUtf8();

        int userlevel = Helper.getInstance().getUserLevel(uuid, key);
        if (userlevel >= 0) {
            List<ValueInfoBean> list = SqlCmd.getInstance().getconfigfile(uid, 1);
            List<ModbusData> data_list = new ArrayList<>();
            int num = data_rep.getInfoCount();
            for (int i = 0; i < num; ++i) {
                data_info info = data_rep.getInfo(i);
                byte[] modbus = info.getData().toByteArray();
                date = info.getTime();
                ModbusData modData = Helper.getInstance().ExMValueData(list, userlevel, modbus, date);
                data_list.add(modData);
            }
            DeferredResult<Object> result = (DeferredResult<Object>) valueCenter.getUuid_deferredResult().get(key);

            if (data_list.size() > 0) {
                RetModbusData retmodbusdata = new RetModbusData(uid, 0, 0, data_list);
                Helper.getInstance().DefReturn(result, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, retmodbusdata);

                SelectInfoBean bean = valueCenter.getUuid_selecttime().get(uuid);
                List<Long> timelist = bean.getTime_list();
                int size  = timelist.size();
                if (timelist.get(size-1) < date){
                    timelist.add(date);
                }else{
                    timelist.remove(size-1);
                }
            } else {
                Helper.getInstance().DefReturn(result, ErrorCode.NO_DATA, ErrorCode.NO_DATA_STR, token, null);
            }
            valueCenter.getUuid_deferredResult().remove(key);
        }
    }


    public void readControlData(read_control_data_response data_rep) {
        String token_info = data_rep.getSessionid();
        int pos = token_info.indexOf("#");
        String uuid = token_info.substring(0, pos);
        String token_str = token_info.substring(pos + 1, token_info.length());
        Token token = new Token(token_str, uuid);

        String key = "cdata" + "#" + uuid;
        String uid = data_rep.getUid().toStringUtf8();
        int userlevel = Helper.getInstance().getUserLevel(uuid, key);

        if (userlevel >= 0) {
            List<ValueInfoBean> config_list = SqlCmd.getInstance().getconfigfile(uid, 1);
            Map<String, HisContorlDataBean> map = new TreeMap<>();

            int count = data_rep.getInfoCount();
            for (int i = 0; i < count; i++) {
                control_data_info data_info = data_rep.getInfo(i);
                HisContorlDataBean bean = Helper.getInstance().ExControlData(config_list, userlevel, data_info);
                if (bean != null)
                    map.put(bean.getDate() + " " + i, bean);
            }
            Collection<HisContorlDataBean> collection = map.values();
            List<HisContorlDataBean> valueList = new ArrayList<>(collection);
            DeferredResult<Object> result = (DeferredResult<Object>) valueCenter.getUuid_deferredResult().get(key);
            Helper.getInstance().DefReturn(result, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, valueList);
            valueCenter.getUuid_deferredResult().remove(key);
        }
    }


    public void readAlarmData(read_alarm_data_response data_rep) {
        String token_info = data_rep.getSessionid();
        int pos = token_info.indexOf("#");
        String uuid = token_info.substring(0, pos);
        String token_str = token_info.substring(pos + 1, token_info.length());
        Token token = new Token(token_str, uuid);

        String key = "adata" + "#" + uuid;
        String uid = data_rep.getUid().toStringUtf8();
        int userlevel = Helper.getInstance().getUserLevel(uuid, key);
        if (userlevel >= 0) {
            List<ValueInfoBean> config_list = SqlCmd.getInstance().getconfigfile(uid, 1);
            Map<String, HisAlarmDataBean_mid> map = new TreeMap<>();
            int count = data_rep.getInfoCount();
            for (int i = 0; i < count; i++) {
                alarm_data_info data_info = data_rep.getInfo(i);
                int code = data_info.getAlarmCode();
                ByteString codearray = data_info.getAlarmCodeArray();
                long stime = data_info.getStartTime();
                long etime = data_info.getElapseTime();

//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String startTimeString = formatter.format(stime);
                HisAlarmDataBean_mid bean = new HisAlarmDataBean_mid(stime, etime, code, "");
                map.put(bean.getStime() + " " + i, bean);
            }
            Map<String, HisAlarmDataBean> return_map = Helper.getInstance().ExAlarmData(map, config_list);
            Collection<HisAlarmDataBean> collection = return_map.values();
            List<HisAlarmDataBean> valueList = new ArrayList<>(collection);
            DeferredResult<Object> result = (DeferredResult<Object>) valueCenter.getUuid_deferredResult().get(key);
            Helper.getInstance().DefReturn(result, ErrorCode.SUCCESS, ErrorCode.SUCCESS_STR, token, valueList);
            valueCenter.getUuid_deferredResult().remove(key);
        }
    }
}
