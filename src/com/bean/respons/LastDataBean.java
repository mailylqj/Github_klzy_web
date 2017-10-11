package com.bean.respons;

public class LastDataBean {

	String uidsString;
	String modAdd;
	String dataAdd;
	String timeString;
	byte[] modbus;

	public String getModAdd() {
		return modAdd;
	}

	public void setModAdd(String modAdd) {
		this.modAdd = modAdd;
	}

	public String getDataAdd() {
		return dataAdd;
	}

	public void setDataAdd(String dataAdd) {
		this.dataAdd = dataAdd;
	}

	public String getUidsString() {
		return uidsString;
	}

	public void setUidsString(String uidsString) {
		this.uidsString = uidsString;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public byte[] getModbus() {
		return modbus;
	}

	public void setModbus(byte[] modbus) {
		this.modbus = modbus;
	}

}
