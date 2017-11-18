package com.bean;

public class CurAlarmAdapterBean {
	String uidString;
	String limit;
	String alarmInfo;


	public String getUidString() {
		return uidString;
	}

	public String getAlarmInfo() {
		return alarmInfo;
	}

	public String getLimit() {
		return limit;
	}

	public CurAlarmAdapterBean(String uidString, String alarmInfo, String limit) {
		super();
		this.uidString = uidString;
		this.alarmInfo = alarmInfo;
		this.limit = limit;
	}

}
