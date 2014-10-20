package com.taixin.android.onvif.sdk.obj;

public class Device {
	
	private String XAddrs;
	private String Types;
	private String Address;
	private String item;
	private int 	 MetadataVersion;
	private String uuid;
	private String deviceService;
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getDeviceService() {
		return deviceService;
	}

	public void setDeviceService(String deviceService) {
		this.deviceService = deviceService;
	}

	public Device(){
		
	}
	
	public Device(String XAddrs){
		this.XAddrs = XAddrs;
	}
	
	public Device(String XAddrs, String Types, String Address, String item, int MetadataVersion)
	{
		this.Address = Address;
		this.XAddrs = XAddrs;
		this.Types = Types;
		this.item = item;
		this.MetadataVersion = MetadataVersion;
	}
	
	public String getXAddrs() {
		return XAddrs;
	}
	public void setXAddrs(String xAddrs) {
		XAddrs = xAddrs;
	}
	
	public String getTypes() {
		return Types;
	}

	public void setTypes(String types) {
		Types = types;
	}

	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public int getMetadataVersion() {
		return MetadataVersion;
	}
	public void setMetadataVersion(int metadataVersion) {
		MetadataVersion = metadataVersion;
	}
		
		
}
