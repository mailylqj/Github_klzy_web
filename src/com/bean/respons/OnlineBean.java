package com.bean.respons;

import com.google.protobuf.ByteString;

public class OnlineBean {

	int imagesId;
	String name;
	String contents;
	int code;

	ByteString uidString;
	ByteString modAdd;
	ByteString dataAdd;
	String fileName;
	float latitude;// 纬度
	float longitude; // 经度

	public int getImagesId() {
		return imagesId;
	}

	public String getContents() {
		return contents;
	}

	public int getCode() {
		return code;
	}

	public ByteString getUidString() {
		return uidString;
	}

	public ByteString getModAdd() {
		return modAdd;
	}

	public ByteString getDataAdd() {
		return dataAdd;
	}

	public String getFileName() {
		return fileName;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public String getName() {
		return name;
	}

	public OnlineBean(int imagesId, String name, String contents, int code, ByteString uidString, ByteString modAdd, ByteString dataAdd, String fileName, float latitude, float longitude) {
		this.imagesId = imagesId;
		this.name = name;
		this.contents = contents;
		this.code = code;
		this.uidString = uidString;
		this.modAdd = modAdd;
		this.dataAdd = dataAdd;
		this.fileName = fileName;
		this.latitude = latitude;
		this.longitude = longitude;
	}

}
