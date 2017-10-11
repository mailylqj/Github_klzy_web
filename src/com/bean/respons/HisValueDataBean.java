package com.bean.respons;

import java.util.List;

public class HisValueDataBean {

	String time;
	List<Float> datalist;

	public HisValueDataBean(String time, List<Float> datalist) {
		this.time = time;
		this.datalist = datalist;
	}


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<Float> getDatalist() {
		return datalist;
	}

	public void setDatalist(List<Float> datalist) {
		this.datalist = datalist;
	}
}
