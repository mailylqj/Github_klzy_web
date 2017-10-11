package com.bean;

public class HisContorlDataBean {
	// 时间
	long date;
	// 变量名
	String name;
	// IMEI号
	String imei;
	// 值
	float  content;

	public HisContorlDataBean(Long date, String name, String imei, float content) {
		this.date = date;
		this.name = name;
		this.imei = imei;
		this.content = content;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public float getContent() {
		return content;
	}

	public void setContent(float content) {
		this.content = content;
	}
}
