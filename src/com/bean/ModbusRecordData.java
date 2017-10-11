package com.bean;

import com.google.protobuf.ByteString;

public class ModbusRecordData
{
    long record_time = 0;
    ByteString modbus_msg = null;
    ByteString alarm_code;



    public ByteString getAlarm_code() {
        return alarm_code;
    }

    public void setAlarm_code(ByteString alarm_code) {
        this.alarm_code = alarm_code;
    }

    public long getRecord_time() {
        return record_time;
    }

    public void setRecord_time(long record_time) {
        this.record_time = record_time;
    }

    public ByteString getModbus_msg() {
        return modbus_msg;
    }

    public void setModbus_msg(ByteString modbus_msg) {
        this.modbus_msg = modbus_msg;
    }
}
