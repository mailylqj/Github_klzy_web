package com.util;

import com.application.ValueCenter;
import com.bean.*;
import com.google.protobuf.ByteString;
import com.ideal.logic.control_data;
import com.mysql.SqlCmd;
import com.pack.CRCUtil;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;

public class Helper {

    private static Helper instance = new Helper();
    private ByteServer byteServer = ByteServer.getInstance();
    private ValueCenter valueCenter = ValueCenter.getInstance();
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Helper getInstance() {
        return instance;
    }


    /**
     * 实时数据解析
     */
    public void ExCurData(String uid, ModbusRecordData data) {
        ByteString msg = data.getModbus_msg();
        ByteString alarm = data.getAlarm_code();
        long time = data.getRecord_time();
        List<ValueInfoBean> list = SqlCmd.getInstance().getconfigfile(uid, 1);
        List<String> alarm_list = ExCurAlarmData(uid, list, 9, alarm.toByteArray());
        CurData curData = ExCValueData(uid, list, 9, msg.toByteArray(), time, alarm_list);
        valueCenter.getAll_ExData().put(uid, curData);
    }

    /**
     * @param uid
     * @param userlevel
     * @param alarm
     * @return
     */
    public List<String> ExCurAlarmData(String uid, List<ValueInfoBean> res_list, int userlevel, byte[] alarm) {
        List<ValueInfoBean> config_list = res_list;
        List<ValueInfoBean> alarm_config_list = new ArrayList<>();
        for (ValueInfoBean bean : config_list) {
            if (bean.getType() == 2) {
                alarm_config_list.add(bean);
            }
        }

        List<String> list = new ArrayList<>();
        byte b[] = new byte[5];

        for (int i = 0; i < alarm.length / 2; i++) {
            b[0] = alarm[i * 2];
            b[1] = alarm[i * 2 + 1];
            String hex = Integer.toBinaryString((int) byteServer.byteArrayToValue(b, 0, 0, 0));
            hex = "0000000000000000" + hex;
            int strlen = hex.length();
            for (int m = 0; m < 16; m++) {
                if (Integer.parseInt(hex.substring(strlen - 1 - m, strlen - m)) == 1) {
                    if (alarm_config_list.size() > (i * 6 + m)) {
                        ValueInfoBean alarm_bean = alarm_config_list.get(i * 16 + m);
                        if (userlevel >= alarm_bean.getPermission())
                            list.add(alarm_bean.getName());
                    }
                }
            }
        }
        return list;
    }


    /**
     * 实时数据解析
     *
     * @param uid
     * @param list
     * @param userlevel
     * @param modbus
     * @param date
     * @param alarm_list
     * @return
     */
    public CurData ExCValueData(String uid, List<ValueInfoBean> list, int userlevel, byte[] modbus, long date, List<String> alarm_list) {
        List<ShowCValueInfoBean> data_list = new ArrayList<>();

        int runtime = 0;
        int DataLen = modbus[2] & 0xFF;
        float value = 0;
        byte b[] = new byte[10];

        for (int i = 0; i < DataLen / 2; i++) {
            if (i < 10) {
                b[0] = modbus[i * 2 + 3];
                b[1] = modbus[i * 2 + 4];
                String hex = Integer.toBinaryString((int) byteServer.byteArrayToValue(b, 0, 0, 0));
                hex = "0000000000000000" + hex;
                int strlen = hex.length();
                for (int m = 0; m < 16; m++) {
                    value = Integer.parseInt(hex.substring(strlen - 1 - m, strlen - m));
                    ValueInfoBean bean = list.get(i * 16 + m);
                    if (bean.getType() == 0) {
                        if (userlevel >= bean.getPermission()) {
                            ShowCValueInfoBean data = new ShowCValueInfoBean(bean.getName(), value, bean.getUnit(), bean.getRwType(), bean.getPermission(), bean.getShowType(), bean.getMax(), bean.getMin(), bean.getWriteadd());
                            data_list.add(data);
                        }
                        runtime = i * 16 + m;
                    } else {
                        i = 9;
                        break;
                    }
                }
            } else {
                runtime++;
                if (list.size() > runtime) {
                    ValueInfoBean bean = list.get(runtime);
                    if (bean.getType() != 1)
                        break;

                    int dataType = Integer.valueOf(bean.getDataType());
                    int formatLen = Integer.valueOf(bean.getDecimals());

                    if (dataType == 1 || dataType == 2) {
                        b[0] = modbus[i * 2 + 3];
                        b[1] = modbus[i * 2 + 4];
                        b[3] = 0;
                        b[4] = 0;
                        value = byteServer.byteArrayToValue(b, dataType, 0, formatLen);
                    } else if (dataType == 3 || dataType == 4 || dataType == 5) {
                        b[0] = modbus[i * 2 + 3];
                        b[1] = modbus[i * 2 + 4];
                        b[2] = modbus[i * 2 + 5];
                        b[3] = modbus[i * 2 + 6];
                        i++;
                        value = byteServer.byteArrayToValue(b, dataType, 0, formatLen);
                    } else if (dataType == 6) {
                        b[0] = modbus[i * 2 + 3];
                        b[1] = modbus[i * 2 + 4];
                        b[2] = modbus[i * 2 + 5];
                        b[3] = modbus[i * 2 + 6];
                        b[0] = modbus[i * 2 + 7];
                        b[1] = modbus[i * 2 + 8];
                        b[2] = modbus[i * 2 + 9];
                        b[3] = modbus[i * 2 + 10];
                        i = i + 2;
                        value = byteServer.byteArrayToValue(b, dataType, 0, formatLen);
                    }
                    if (userlevel >= bean.getPermission()) {
                        ShowCValueInfoBean data = new ShowCValueInfoBean(bean.getName(), value, bean.getUnit(), bean.getRwType(), bean.getPermission(), bean.getShowType(), bean.getMax(), bean.getMin(), bean.getWriteadd());
                        data_list.add(data);
                    }
                }
            }
        }
        return new CurData(uid, date, data_list, alarm_list);
    }

    /**
     * 历史数据解析
     */
    public ModbusData ExMValueData(List<ValueInfoBean> list, int userlevel, byte[] modbus, long date) {
        List<ShowMValueInfoBean> data_list = new ArrayList<>();

        int runtime = 0;
        int DataLen = modbus[2] & 0xFF;
        float value = 0;
        byte b[] = new byte[10];

        for (int i = 0; i < DataLen / 2; i++) {
            if (i < 10) {
                b[0] = modbus[i * 2 + 3];
                b[1] = modbus[i * 2 + 4];
                String hex = Integer.toBinaryString((int) byteServer.byteArrayToValue(b, 0, 0, 0));
                hex = "0000000000000000" + hex;
                int strlen = hex.length();
                for (int m = 0; m < 16; m++) {
                    value = Integer.parseInt(hex.substring(strlen - 1 - m, strlen - m));
                    ValueInfoBean bean = list.get(i * 16 + m);
                    if (bean.getType() == 0) {
                        if (userlevel >= bean.getPermission()) {
                            ShowMValueInfoBean data = new ShowMValueInfoBean(bean.getName(), value, bean.getUnit());
                            data_list.add(data);
                        }
                        runtime = i * 16 + m;
                    } else {
                        i = 9;
                        break;
                    }
                }
            } else {
                runtime++;
                if (list.size() > runtime) {
                    ValueInfoBean bean = list.get(runtime);
                    if (bean.getType() != 1)
                        break;

                    int dataType = Integer.valueOf(bean.getDataType());
                    int formatLen = Integer.valueOf(bean.getDecimals());

                    if (dataType == 1 || dataType == 2) {
                        b[0] = modbus[i * 2 + 3];
                        b[1] = modbus[i * 2 + 4];
                        b[3] = 0;
                        b[4] = 0;
                        value = byteServer.byteArrayToValue(b, dataType, 0, formatLen);
                    } else if (dataType == 3 || dataType == 4 || dataType == 5) {
                        b[0] = modbus[i * 2 + 3];
                        b[1] = modbus[i * 2 + 4];
                        b[2] = modbus[i * 2 + 5];
                        b[3] = modbus[i * 2 + 6];
                        i++;
                        value = byteServer.byteArrayToValue(b, dataType, 0, formatLen);
                    } else if (dataType == 6) {
                        b[0] = modbus[i * 2 + 3];
                        b[1] = modbus[i * 2 + 4];
                        b[2] = modbus[i * 2 + 5];
                        b[3] = modbus[i * 2 + 6];
                        b[0] = modbus[i * 2 + 7];
                        b[1] = modbus[i * 2 + 8];
                        b[2] = modbus[i * 2 + 9];
                        b[3] = modbus[i * 2 + 10];
                        i = i + 2;
                        value = byteServer.byteArrayToValue(b, dataType, 0, formatLen);
                    }
                    if (userlevel >= bean.getPermission()) {
                        ShowMValueInfoBean data = new ShowMValueInfoBean(bean.getName(), value, bean.getUnit());
                        data_list.add(data);
                    }
                }
            }
        }
        return new ModbusData(date, data_list);
    }

    /**
     * 控制数据解析
     */
    public HisContorlDataBean ExControlData(List<ValueInfoBean> config_list, int userlevel, control_data.control_data_info data_info) {

        long time = data_info.getTime();
        //String timeString = formatter.format(time);
        String imei = data_info.getImei();

        byte[] modbus_data = data_info.getModbusProc().toByteArray();
        byte[] b = new byte[8];
        b[0] = modbus_data[2];
        b[1] = modbus_data[3];
        int startadd = (int) (byteServer.byteArrayToValue(b, 1, 1, 0));
        b[0] = modbus_data[4];
        b[1] = modbus_data[5];
        int datalen = (int) byteServer.byteArrayToValue(b, 1, 1, 0);

        for (int i = 0; i < datalen * 2; i++) {
            b[i] = modbus_data[6 + i];
        }

        int index = 0;
        String data_name = null;
        float value = 0;

        for (ValueInfoBean bean : config_list) {
            if (bean.getRwType() > 0) {
                int data_type = bean.getDataType();
                if (data_type == 0 || data_type == 1 || data_type == 2)
                    index = index + 1;
                else if (data_type == 3 || data_type == 4 || data_type == 5)
                    index = index + 2;
                else if (data_type == 6)
                    index = index + 4;

                if (index == startadd) {
                    if (userlevel >= bean.getPermission()) {
                        data_name = bean.getName();
                        value = byteServer.byteArrayToValue(b, data_type, 1, bean.getDecimals());
                    }
                }
            }
        }
        return new HisContorlDataBean(time, data_name, imei, value);
    }

    /**
     * 报警数据解析
     */
    public Map<String, HisAlarmDataBean> ExAlarmData(Map<String, HisAlarmDataBean_mid> map, List<ValueInfoBean> config_list) {
        Map<String, HisAlarmDataBean> return_map = new TreeMap<>();
        int index = 0;
        List<String> config_alarm_info = new ArrayList<>();
        for (ValueInfoBean bean : config_list) {
            if (bean.getType() == 2) {
                config_alarm_info.add(bean.getName());
            }
        }

        int[] oldCode = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        long[] startTime = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        long[] eTime = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (HisAlarmDataBean_mid bean : map.values()) {
            long sTime = bean.getStime();
            long ElapseTime = bean.getEtime();
            String codeString = "0000000000000000" + Integer.toBinaryString(bean.getCode());
            int len = codeString.length();

            for (int m = 0; m < 16; m++) {
                int value = Integer.parseInt(codeString.substring(len - 1 - m, len - m));
                if (value == 1) {
                    // 新发生报警
                    if (oldCode[m] == 0) {
                        startTime[m] = sTime;
                        eTime[m] = eTime[m] + ElapseTime;
                        oldCode[m] = 1;
                    } else {// 报警持续
                        eTime[m] = eTime[m] + ElapseTime;
                        oldCode[m] = 1;
                    }
                } else {
                    // 报警结束
                    if (oldCode[m] == 1) {
                        List<String> contentList = new ArrayList<String>();
                        String info = config_alarm_info.get(m);
                        String endTime = Helper.getInstance().formatDuring(eTime[m]);
                        contentList.add(endTime);
                        contentList.add(info);
                        HisAlarmDataBean hisalarmBean = new HisAlarmDataBean(startTime[m], endTime, info, "");
                        return_map.put(hisalarmBean.getStime() + " " + index, hisalarmBean);
                        index++;
                        oldCode[m] = 0;
                        eTime[m] = 0;
                    }
                }
            }
        }
        // 最后还没结束的显示出来
        for (int n = 0; n < 16; n++) {
            if (oldCode[n] == 1) {
                List<String> contentList = new ArrayList<String>();
                if (config_alarm_info.size() > n) {
                    String info = config_alarm_info.get(n);
                    String endTime = Helper.getInstance().formatDuring(eTime[n]);
                    contentList.add(endTime);
                    contentList.add(info);
                    HisAlarmDataBean hisalarmBean = new HisAlarmDataBean(startTime[n], endTime, info, "");
                    return_map.put(hisalarmBean.getStime() + " " + index, hisalarmBean);
                    index++;
                }
            }
        }
        return return_map;
    }

    // 时间计算
    public List<Long> calculatetime(List<Long> timelist, int type) {
        long StartTime = timelist.get(0);
        long EndTime = timelist.get(1);
        long MSTime = timelist.get(2);
        long METime = timelist.get(3);

        if (type == 1) {
            if (METime < EndTime) {
                if (MSTime == 0)
                    MSTime = StartTime;
                else
                    MSTime = METime;
                METime = MSTime + 2000 * 100;
                if (METime >= EndTime)
                    METime = EndTime;

                List<Long> list = new ArrayList<>();
                list.add(StartTime);
                list.add(EndTime);
                list.add(MSTime);
                list.add(METime);
                return list;
            } else
                return null;
        } else {
            if (MSTime != StartTime) {
                if (MSTime == 0) {
                    MSTime = StartTime;
                    METime = MSTime + 2000 * 100;
                    if (METime >= EndTime)
                        METime = EndTime;
                } else {
                    METime = MSTime;
                    MSTime = MSTime - 2000 * 100;
                    if (MSTime < StartTime)
                        MSTime = StartTime;
                }

                List<Long> list = new ArrayList<>();
                list.add(StartTime);
                list.add(EndTime);
                list.add(MSTime);
                list.add(METime);
                return list;
            } else
                return null;
        }
    }


    /**
     * 检查传入的开始时间和结束时间是否合法，若有空值或开始时间小于结束时间则
     * 取最近1小时的数据
     *
     * @param Stime
     * @param Etime
     * @return
     */
    public long[] checkTime(long Stime, long Etime) {
        long start_time = 0, end_time = 0;
        if (Etime == 0)
            end_time = System.currentTimeMillis();
        else
            end_time = Etime;

        if (Stime == 0)
            start_time = System.currentTimeMillis() - 1000 * 60 * 10;
        else
            start_time = Stime;

//        if (start_time == end_time) {
//            start_time = end_time - 1000 * 1;
//        }

        return new long[]{start_time, end_time};
    }

//    public long[] checkTime(long Stime, long Etime) {
//        long start_time = 0, end_time = 0;
//        try {
//            Calendar calendar = Calendar.getInstance();
//            TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");
//            calendar.setTimeZone(destTimeZone);
//            //
//            formatter.setTimeZone(destTimeZone);
//
//            if (Etime != null && !Etime.isEmpty()) {
//                calendar.setTime(formatter.parse(Etime));
//                end_time = calendar.getTimeInMillis();
//            }
//
//            if (Stime != null && !Stime.isEmpty()) {
//                calendar.setTime(formatter.parse(Stime));
//                start_time = calendar.getTimeInMillis();
//            } else {
//                long cur_time = calendar.getTime().getTime();
//                start_time = cur_time - 3600000;
//            }
//
//            if (end_time != 0 && end_time <= start_time) {
//                start_time = end_time - 3600000;
//            }
//            return new long[]{start_time, end_time};
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


    /**
     * 要转换的毫秒数
     *
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     */
    public String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;

        String str = "";
        if (days > 0)
            str = str + days + "d ";
        if (hours > 0)
            str = str + hours + "h ";
        if (minutes > 0)
            str = str + minutes + "min ";
        if (seconds > 0)
            str = str + seconds + "sec";
        return str;
    }


    /**
     * 控制命令组码
     *
     * @param type
     * @param decimal
     * @param add
     * @param value
     * @return
     */
    public byte[] controlCmd(int type, int decimal, int add, float value) {

        int valueCount = 0;
        byte cmd = 0;
        if (type == 0 || type == 1 || type == 2) {
            valueCount = 1;
            cmd = 0x06;
        } else if (type == 3 || type == 4 || type == 5) {
            valueCount = 2;
            cmd = 0x06;
        } else if (type == 6) {
            valueCount = 4;
            cmd = 0x06;
        }
        ByteBuffer cmdbyteBuffer = ByteBuffer.allocate(8 + 2 * valueCount);
        byte[] modbus_data = new byte[8 + 2 * valueCount];
        cmdbyteBuffer.put((byte) 0x01);
        cmdbyteBuffer.put(cmd);
        cmdbyteBuffer.putShort((short) (add));
        cmdbyteBuffer.putShort((short) valueCount);

        if (type == 0 || type == 1 || type == 2) {
            float f = value * (int) (Math.pow(10, decimal));
            short s = (short) f;
            cmdbyteBuffer.putShort(s);
        } else if (type == 3 || type == 4) {
            int i = (int) (value * (int) (Math.pow(10, decimal)));
            byte[] b = byteServer.int2byte(i);
            byte[] rb = new byte[4];
            rb[0] = b[1];
            rb[1] = b[0];
            rb[2] = b[3];
            rb[3] = b[2];
            cmdbyteBuffer.put(rb);
        } else if (type == 5) {
            BigDecimal bd = new BigDecimal(value);
            float f = bd.setScale(decimal, BigDecimal.ROUND_HALF_UP).floatValue();
            byte[] b = byteServer.float2byte(f);
            byte[] rb = new byte[4];
            rb[0] = b[1];
            rb[1] = b[0];
            rb[2] = b[3];
            rb[3] = b[2];
            cmdbyteBuffer.put(rb);
        } else if (type == 6) {
            BigDecimal bd = new BigDecimal(value);
            float f = bd.setScale(decimal, BigDecimal.ROUND_HALF_UP).floatValue();
            cmdbyteBuffer.putDouble(f);
        }

        Short crc = (short) CRCUtil.calcCrc16(cmdbyteBuffer.array(), 0, 6 + 2 * valueCount);
        cmdbyteBuffer.putShort(crc);
        modbus_data = cmdbyteBuffer.array();
        return modbus_data;
    }

    //
    public List<ValueInfoBean> getConfig() {

        return null;
    }

    /**
     * 获取当前用户等级
     *
     * @param session
     * @param key
     * @return
     */
    public int getUserLevel(String session, String key) {
        if (valueCenter.getUuid_userinfo().get(session) != null) {
            int userlevel = valueCenter.getUuid_userinfo().get(session).getLevel();
            return userlevel;
        } else {
            DeferredResult<Map<String, Integer>> result = (DeferredResult<Map<String, Integer>>) valueCenter.getUuid_deferredResult().get(key);
            Map<String, Integer> map = new HashMap<>();
            map.put("result", ErrorCode.UNLOGIN);
            result.setResult(map);
            return ErrorCode.UNLOGIN;
        }
    }

    public void DefReturn(DeferredResult<Object> deferredResult, int result, String message,Token token, Object data) {
        String token_str= null,uuid=null;
        if (token != null) {
            token_str = token.getToken();
            uuid = token.getUuid();
            if (token_str != null) {
                valueCenter.getToken_map().remove(token_str);
               //valueCenter.getToken_map().put(token_str,false);
            }
            if (uuid != null)
                token_str = JwtUtil.createJWT(uuid);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        map.put("message", message);
        map.put("token", token_str);
        map.put("data", data);

        if (deferredResult != null)
            deferredResult.setResult(map);
        if (token_str != null) {
            valueCenter.getToken_map().put(token_str, false);
        }
        valueCenter.getToken_map().put("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxM2UyYmQ1NWIxYTk0NjMzYWRjMjAzNDlkZGVjZWI5ZSIsImF1ZCI6IjE1MDk5MzU1Mzk3ODcifQ.MQbuzqiwZWkxJBA1U1NkdgpCLIWedjWgN5k5IlVxWew",false);
    }
}
