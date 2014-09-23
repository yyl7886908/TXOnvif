package com.taixin.android.onvif.sdk.obj;

public class DeviceInfo {
	
	private String XAddrs;
	private String Types;
	private String Address;
	private String item;
	private int 	 MetadataVersion;
	
	public DeviceInfo(){
		
	}
	
	public DeviceInfo(String XAddrs){
		this.XAddrs = XAddrs;
	}
	
	public DeviceInfo(String XAddrs, String Types, String Address, String item, int MetadataVersion)
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
