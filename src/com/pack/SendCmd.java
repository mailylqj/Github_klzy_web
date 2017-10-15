package com.pack;


import com.application.ValueCenter;
import com.bean.ValueInfoBean;
import com.google.protobuf.ByteString;
import com.ideal.logic.*;
import com.ideal.logic.control_data.control_req;
import com.ideal.logic.modbus_data.read_data_req;
import com.ideal.logic.server_lastestdata.reg_server_lastestdata;
import com.ideal.logic.server_lastestdata.unreg_server_lastestdata;
import com.ideal.logic.server_online.reg_server_online;
import com.ideal.logic.server_online.unreg_server_online;
import com.mina.RealMinaClient;
import com.mysql.SqlCmd;
import com.util.ErrorCode;
import com.util.Helper;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SendCmd {
    private ValueCenter valuecenter = ValueCenter.getInstance();
    private logic_req logic_client = logic_req.GetInstance();
    private RealMinaClient client = RealMinaClient.getInstance();
    private String uuid = UUID.randomUUID().toString().replace("-", "");
    private Logger log = Logger.getLogger(getClass());
    private static SendCmd instance = null;

    public static SendCmd getIntance() {
        if (instance == null) {
            instance = new SendCmd();
            return instance;
        } else
            return instance;
    }

    public SendCmd() {
        //log.info("----------------------uuid--" + uuid);
    }

    public void sendData(ByteBuffer byteBuffer, String key) {
        if ((!client.send(byteBuffer)) && key != null) {
            DeferredResult<Map<String, Object>> result = (DeferredResult<Map<String, Object>>) valuecenter.getSession_deferredResult_map().get(key);
            Map<String, Object> ret_map = new HashMap<>();
            ret_map.put("result", ErrorCode.SEND_ERROR);
            result.setResult(ret_map);
        }
    }

    public void reg_server_lastestdata() {
        reg_server_lastestdata req = logic_client.reg_server_lastestdata(uuid);
        String protobuf_method_name = "regServerLastestdata";
        logic_imp imp = new logic_imp();
        com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
        int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
        ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(req, (short) method_idx);
        sendData(byteBuffer, null);
    }

    public void unreg_server_lastestdata(IoSession session) {
        unreg_server_lastestdata req = logic_client.unreg_server_lastestdata(uuid);
        String protobuf_method_name = "unregServerLastestdata";
        logic_imp imp = new logic_imp();
        com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
        int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
        ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(req, (short) method_idx);
        IoBuffer buffer = IoBuffer.wrap(byteBuffer);
        session.write(buffer);
    }

    public void unreg_server_lastestdata() {
        unreg_server_lastestdata req = logic_client.unreg_server_lastestdata(uuid);
        String protobuf_method_name = "unregServerLastestdata";
        logic_imp imp = new logic_imp();
        com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
        int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
        ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(req, (short) method_idx);
        sendData(byteBuffer, null);
    }

    public void reg_server_online() {
        reg_server_online req = logic_client.reg_server_online(uuid);
        String protobuf_method_name = "regServerOnline";
        logic_imp imp = new logic_imp();
        com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
        int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
        ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(req, (short) method_idx);
        sendData(byteBuffer, null);
    }

    public void unreg_server_online() {
        unreg_server_online req = logic_client.unreg_server_online();
        String protobuf_method_name = "unregServerOnline";
        logic_imp imp = new logic_imp();
        com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
        int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
        ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(req, (short) method_idx);
        sendData(byteBuffer, null);
    }


    public void control(String uid, String imei, int datatype, int datadecimals, int writeadd, float value, String key) {

        byte[] modbus_data = Helper.getInstance().controlCmd(datatype, datadecimals, writeadd, value);

        ByteString uidByteString = ByteString.copyFrom(uid.getBytes());
        if (uidByteString != null && imei != null && modbus_data != null) {
            control_req wData_req = logic_client.commandControlClient(uidByteString, imei, modbus_data, key);
            String protobuf_method_name = "command_control";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, "control" + "#" + key);
        }
    }


    public void readModData(String uid, long Stime, long Etime, String key) {
        long[] time = Helper.getInstance().checkTime(Stime, Etime);
        List<Long> selecttime_list = new ArrayList<>();
        selecttime_list.add(time[0]);
        selecttime_list.add(time[1]);
        selecttime_list.add((long) 0);
        selecttime_list.add((long) 0);
        selecttime_list = Helper.getInstance().calculatetime(selecttime_list, 1);
        ModData(uid, selecttime_list, key);
    }

    public void readModDataPrev(String key) {
        Map<String, Map<String, List<Long>>> moddata_map = valuecenter.getSession_selecttime();
        Map<String, List<Long>> map = moddata_map.get(key);

        if (map != null) {
            String uid = "";
            List<Long> list = new ArrayList<>();
            for (Map.Entry<String, List<Long>> entry : map.entrySet()) {
                uid = entry.getKey();
                list = entry.getValue();
            }

            list = Helper.getInstance().calculatetime(list, 0);
            ModData(uid, list, key);
        } else {
            DeferredResult<Map<String, Object>> result = (DeferredResult<Map<String, Object>>) valuecenter.getSession_deferredResult_map().get(key);
            Map<String, Object> ret_map = new HashMap<>();
            ret_map.put("result", ErrorCode.REQUEST_ERROR);
            result.setResult(ret_map);
        }
    }

    public void readModDataNext(String key) {
        Map<String, Map<String, List<Long>>> moddata_map = valuecenter.getSession_selecttime();
        Map<String, List<Long>> map = moddata_map.get(key);

        if (map != null) {
            String uid = "";
            List<Long> list = new ArrayList<>();
            for (Map.Entry<String, List<Long>> entry : map.entrySet()) {
                uid = entry.getKey();
                list = entry.getValue();
            }

            list = Helper.getInstance().calculatetime(list, 1);
            ModData(uid, list, key);
        } else {
            DeferredResult<Map<String, Object>> result = (DeferredResult<Map<String, Object>>) valuecenter.getSession_deferredResult_map().get(key);
            Map<String, Object> ret_map = new HashMap<>();
            ret_map.put("result", ErrorCode.REQUEST_ERROR);
            result.setResult(ret_map);
        }
    }

    public void ModData(String uid, List<Long> list, String key) {
        if (list != null) {
            ByteString uidString = ByteString.copyFromUtf8(uid);
            ByteString modAdd = ByteString.copyFrom(new byte[]{0x01});
            ByteString startAdd = ByteString.copyFrom(new byte[]{0x00, 0x00});

            Map<String, List<Long>> stringListMap = new HashMap<>();
            stringListMap.put(uid, list);
            valuecenter.getSession_selecttime().put(key, stringListMap);

            read_data_req wData_req = logic_client.modbusReadClient(uidString, modAdd, startAdd, list.get(2), list.get(3), key);
            String protobuf_method_name = "modbus_read";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, key);
            //log.error("历史数据发送：   " +System.currentTimeMillis());
        } else {
            DeferredResult<Map<String, Object>> result = (DeferredResult<Map<String, Object>>) valuecenter.getSession_deferredResult_map().get(key);
            Map<String, Object> ret_map = new HashMap<>();
            ret_map.put("result", ErrorCode.SELECTEND);
            result.setResult(ret_map);
        }
    }

    public void readControlData(String uid, long Stime, long Etime, String key) {
        ByteString uidString = ByteString.copyFromUtf8(uid);
        long[] time = Helper.getInstance().checkTime(Stime, Etime);
        if (time != null) {
            control_data.read_control_req wData_req = logic_client.readControl(uidString, time[0], time[1], key);
            String protobuf_method_name = "controlDataRead";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, "cdata" + "#" +key);
        }
    }

    public void readAlarmData(String uid, long Stime, long Etime, String key) {
        ByteString uidString = ByteString.copyFromUtf8(uid);
        long[] time = Helper.getInstance().checkTime(Stime, Etime);
        if (time != null) {
            alarm_data.read_alarm_req wData_req = logic_client.readAlarm(uidString, time[0], time[1], key);
            String protobuf_method_name = "alarmDataRead";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, "adata" + "#" +key);
        }
    }












    /* *****************************************************************************************************过时
    public void readoffline(int index) {
        String username = valuecenter.getLogin_username();
        if (username != null && username.length() > 0) {
            update_offline_status_request req = logic_client.readoffline(username, index);
            String protobuf_method_name = "offlineRead";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(req, (short) method_idx);
            sendData(byteBuffer, 12);
        }
    }

    public void readonedeviceinfo(String uid) {
        String username = valuecenter.getLogin_username();
        device_info_request req = logic_client.read_deviceinfo(username, uid);
        String protobuf_method_name = "deviceRead";
        logic_imp imp = new logic_imp();
        com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
        int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
        ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(req, (short) method_idx);
        sendData(byteBuffer, 14);
    }



    public void login(String user_name, String password) {
        if (user_name != null && password != null) {
            login_req wData_req = logic_client.userLoginClient(user_name, password);
            String protobuf_method_name = "user_login";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, 2);
        }
    }

    public void getOnlin(String user_name) {
        if (user_name != null) {
            update_online_status_request wData_req = logic_client.updateOnlineStatusClient(user_name);
            String protobuf_method_name = "updateOnlineStatus";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, 3);

        }
    }

    public void regData(ByteString uidString, ByteString modAdd, ByteString startAdd) {

        if (uidString != null && modAdd != null && startAdd != null) {

            reg_lastest_data wData_req = logic_client.regRefreshLastestDataClient(uidString, modAdd, startAdd);
            String protobuf_method_name = "regRefreshLastestData";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, 4);
        }
    }

    public void unregData(ByteString uidString, ByteString modAdd, ByteString startAdd) {
        if (uidString != null && modAdd != null && startAdd != null) {

            unreg_lastest_data wData_req = logic_client.unregRefreshLastestDataClient(uidString, modAdd, startAdd);
            String protobuf_method_name = "unregRefreshLastestData";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, 5);
        }
    }

    public void getUpdateLastestDataBuffer(ByteString uidString, ByteString modAdd, ByteString startAdd) {
        if (uidString != null && modAdd != null && startAdd != null) {

            update_lastest_data_request wData_req = logic_client.updateLastestDataClient(uidString, modAdd, startAdd);
            String protobuf_method_name = "updateLastestData";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, 6);
        }
    }

    public void getControlBuffer(ByteString uidByteString, String imei, byte[] modbus_data) {
        if (uidByteString != null && imei != null && modbus_data != null) {

            control_req wData_req = logic_client.commandControlClient(uidByteString, imei, modbus_data);
            String protobuf_method_name = "command_control";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, 7);
        }
    }



    public void TimeSelect(boolean sortTime) {
        int his_loading = valuecenter.getHis_loading();
        if (his_loading > 0) {
            long cTime = EndTime - StartTime;
            long start_time = 0, end_time = 0;

            if (sortTime) {
                if (cTime > (60000 * 5)) {
                    end_time = EndTime;
                    start_time = end_time - (60000 * 5);
                    EndTime = start_time;
                    // 删不删原有显示
                    // valuecenter.setHis_loading(2);
                } else {
                    start_time = StartTime;
                    end_time = EndTime;
                    if (his_loading == 1)
                        valuecenter.setHis_loading(5); // 特定初次小于1分钟的
                    else
                        valuecenter.setHis_loading(3);
                }
            } else {
                if (cTime > (60000 * 5)) {
                    start_time = StartTime;
                    end_time = start_time + (60000 * 5);
                    StartTime = end_time;
                    // 删不删原有显示
                    // valuecenter.setHis_loading(2);
                } else {
                    start_time = StartTime;
                    end_time = EndTime;
                    if (his_loading == 1)
                        valuecenter.setHis_loading(5); // 特定初次小于1分钟的
                    else
                        valuecenter.setHis_loading(3);
                }
            }

            read_data_req wData_req = logic_client.modbusReadClient(L_uidString, L_modAdd, L_startAdd, start_time, end_time);
            String protobuf_method_name = "modbus_read";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, 8);
        }
    }

    public void getFile(String fileName) {
        update_file_request wData_req = logic_client.updateFile(fileName);
        String protobuf_method_name = "updateFile";
        logic_imp imp = new logic_imp();
        com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
        int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
        ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
        sendData(byteBuffer, 9);
    }

    public void changePwd(String username, String old_pwd, String new_pwd) {
        change_pwd_request wData_req = logic_client.changePwd(username, old_pwd, new_pwd);
        String protobuf_method_name = "changePassword";
        logic_imp imp = new logic_imp();
        com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
        int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
        ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
        sendData(byteBuffer, 10);
    }



    public void controlDataFD() {
        int control_loading = valuecenter.getControl_loading();
        if (control_loading > 0) {
            long cTime = C_EndTime - C_StartTime;
            long start_time = 0, end_time = 0;
            // 一天数据
            if (cTime > (60000 * 60 * 48)) {
                end_time = C_EndTime;
                start_time = end_time - (60000 * 60 * 48);
                C_EndTime = start_time;
                // 删不删原有显示（放接收端处理）
                // valuecenter.setAlarm_loading(2);
            } else {
                start_time = C_StartTime;
                end_time = C_EndTime;
                if (control_loading == 1)
                    valuecenter.setControl_loading(5); // 特定初次小于2天的
                else
                    valuecenter.setControl_loading(3);
            }
            read_control_req wData_req = logic_client.readControl(C_uidString, start_time, end_time);
            String protobuf_method_name = "controlDataRead";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, 11);
        }
    }

    public void readAlarm(ByteString uidByteString, String Stime, String Etime) {
        Stime = Stime + ":00";
        Etime = Etime + ":00";
        long start_time = 0, end_time = 0;
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");
            calendar.setTimeZone(destTimeZone);
            //
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(destTimeZone);

            if (Etime != null && !Etime.isEmpty()) {
                calendar.setTime(formatter.parse(Etime));
                end_time = calendar.getTimeInMillis();
            }

            if (Stime != null && !Stime.isEmpty()) {
                calendar.setTime(formatter.parse(Stime));
                start_time = calendar.getTimeInMillis();
            } else {
                long cur_time = calendar.getTime().getTime();
                start_time = cur_time - 3600000;
            }

            if (end_time != 0 && end_time <= start_time) {
                start_time = end_time - 3600000;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        valuecenter.setAlarm_loading(1);
        A_StartTime = start_time;
        A_EndTime = end_time;
        A_uidString = uidByteString;
        alarmDataFD();
    }

    /**
     * alarm_loading 1: 开始 ； 2：
     */
    /*
    public void alarmDataFD() {
        int alarm_loading = valuecenter.getAlarm_loading();
        if (alarm_loading > 0) {
            long cTime = A_EndTime - A_StartTime;
            long start_time = 0, end_time = 0;
            // 一天数据
            if (cTime > (60000 * 60 * 48)) {
                end_time = A_EndTime;
                start_time = end_time - (60000 * 60 * 48);
                A_EndTime = start_time;
                // 删不删原有显示（放接收端处理）
                // valuecenter.setAlarm_loading(2);
            } else {
                start_time = A_StartTime;
                end_time = A_EndTime;
                if (alarm_loading == 1)
                    valuecenter.setAlarm_loading(5); // 特定初次小于1天的
                else
                    valuecenter.setAlarm_loading(3);
            }

            read_alarm_req wData_req = logic_client.readAlarm(A_uidString, start_time, end_time);
            String protobuf_method_name = "alarmDataRead";
            logic_imp imp = new logic_imp();
            com.google.protobuf.Service rpc_service = logic.logic_server.newReflectiveService(imp);
            int method_idx = rpc_service.getDescriptorForType().findMethodByName(protobuf_method_name).getIndex();
            ByteBuffer byteBuffer = RPCPackage.PackageClientProtoData(wData_req, (short) method_idx);
            sendData(byteBuffer, 12);
        }
    }

    /*
     * @param value
     *
     * @param ctype 地址都是从1开始
     */
    /*
    public void forControlCmd(int valuetype, int XSD, float value, int add, int bit_id) {

        int valueCount = 0;
        byte cmd = 0;
        if (valuetype == 0 || valuetype == 1 || valuetype == 2) {
            valueCount = 1;
            cmd = 0x06;
        } else if (valuetype == 3 || valuetype == 4 || valuetype == 5) {
            valueCount = 2;
            cmd = 0x06;
        } else if (valuetype == 6) {
            valueCount = 4;
            cmd = 0x06;
        }
        ByteBuffer cmdbyteBuffer = ByteBuffer.allocate(8 + 2 * valueCount);
        byte[] modbus_data = new byte[8 + 2 * valueCount];
        cmdbyteBuffer.put((byte) 0x01);
        cmdbyteBuffer.put(cmd);
        cmdbyteBuffer.putShort((short) (add + bit_id));
        cmdbyteBuffer.putShort((short) valueCount);

        if (valuetype == 0 || valuetype == 1 || valuetype == 2) {
            float f = value * (int) (Math.pow(10, XSD));
            short s = (short) f;
            cmdbyteBuffer.putShort(s);
        } else if (valuetype == 3 || valuetype == 4) {
            int i = (int) (value * (int) (Math.pow(10, XSD)));
            byte[] b = intToBytes(i);
            byte[] rb = new byte[4];
            rb[0] = b[1];
            rb[1] = b[0];
            rb[2] = b[3];
            rb[3] = b[2];
            cmdbyteBuffer.put(rb);
        } else if (valuetype == 5) {
            BigDecimal bd = new BigDecimal(value);
            float f = bd.setScale(XSD, BigDecimal.ROUND_HALF_UP).floatValue();
            byte[] b = float2byte(f);
            byte[] rb = new byte[4];
            rb[0] = b[1];
            rb[1] = b[0];
            rb[2] = b[3];
            rb[3] = b[2];
            cmdbyteBuffer.put(rb);
        } else if (valuetype == 6) {
            BigDecimal bd = new BigDecimal(value);
            float f = bd.setScale(XSD, BigDecimal.ROUND_HALF_UP).floatValue();
            cmdbyteBuffer.putDouble(f);
        }

        Short crc = (short) CRCUtil.calcCrc16(cmdbyteBuffer.array(), 0, 6 + 2 * valueCount);
        cmdbyteBuffer.putShort(crc);
        modbus_data = cmdbyteBuffer.array();
        SendCmd.getIntance();
        getControlBuffer(valuecenter.getChose_uidString(), valuecenter.getImei(), modbus_data);
    }

    private byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (fbit >> (24 - i * 8));
        }
        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }

    private byte[] intToBytes(int value) {
        byte[] byte_src = new byte[4];
        byte_src[3] = (byte) ((value & 0xFF000000) >> 24);
        byte_src[2] = (byte) ((value & 0x00FF0000) >> 16);
        byte_src[1] = (byte) ((value & 0x0000FF00) >> 8);
        byte_src[0] = (byte) ((value & 0x000000FF));
        return byte_src;
    }
    */
}
