package com.bean;

import com.google.protobuf.ByteString;

/**
 * Created by yyPan on 2017/6/9.
 */

public class DeviceBean {
    private String deviceNO;
    private String deviceName;
    private String deviceAdd;  //通过标点符号来区分层级
    private int deviceType;
    private boolean isOnLine;
    private boolean isSave;
    private boolean isReg;
    private double longitude;
    private double latitude;
    private String fileName;

    public DeviceBean(){}

    public DeviceBean(String deviceNO, String deviceName, String deviceAdd, int deviceType, boolean isOnLine, boolean isSave, double longitude, double latitude, int alarmValue,  String fileName, ByteString alarm_array) {
        this.deviceNO = deviceNO;
        this.deviceName = deviceName;
        this.deviceAdd = deviceAdd;
        this.deviceType = deviceType;
        this.isOnLine = isOnLine;
        this.isSave = isSave;
        this.longitude = longitude;
        this.latitude = latitude;
        this.fileName = fileName;

    }

    public String getDeviceNO() {
        return deviceNO;
    }

    public void setDeviceNO(String deviceNO) {
        this.deviceNO = deviceNO;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAdd() {
        return deviceAdd;
    }

    public void setDeviceAdd(String deviceAdd) {
        this.deviceAdd = deviceAdd;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public boolean isReg() {
        return isReg;
    }

    public void setReg(boolean reg) {
        isReg = reg;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
