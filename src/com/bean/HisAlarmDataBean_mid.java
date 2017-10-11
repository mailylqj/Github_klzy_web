package com.bean;

public class HisAlarmDataBean_mid {
	long stime;
	long   etime;
	int    code;
	String other;

	public HisAlarmDataBean_mid(long stime, long etime, int code, String other) {
		this.stime = stime;
		this.etime = etime;
		this.code = code;
		this.other = other;
	}

	public long getStime() {
		return stime;
	}

	public void setStime(long stime) {
		this.stime = stime;
	}

	public long getEtime() {
		return etime;
	}

	public void setEtime(long etime) {
		this.etime = etime;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
}
