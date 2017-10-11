package com.bean;

public class HisAlarmDataBean {
	//报警开始时间
	long stime;
	//报警持续时间
	String etime;
	//报警信息
	String alarm_info;
	//其他 预留
	String other;

	public HisAlarmDataBean(long stime, String etime, String alarm_info, String other) {
		this.stime = stime;
		this.etime = etime;
		this.alarm_info = alarm_info;
		this.other = other;
	}

	public long getStime() {
		return stime;
	}

	public void setStime(long stime) {
		this.stime = stime;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}

	public String getAlarm_info() {
		return alarm_info;
	}

	public void setAlarm_info(String alarm_info) {
		this.alarm_info = alarm_info;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
}
