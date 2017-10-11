package com.bean.respons;

public class VersionXmlBean {

	String version;
	String name;
	String url;
	public String getVersion() {
		return version;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	
	public VersionXmlBean(String version, String name, String url) {
		this.version = version;
		this.name = name;
		this.url = url;
	}
	
	
}
