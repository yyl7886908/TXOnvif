package com.taixin.android.onvif.sdk;

public interface SimpleOnvif {

	public void discoverDevices();
	
	public void getDeviceCapabilities();
	
	public void getDeviceInfo();
	
	public void getMediaProfiles();
	
	public void getMediaStreamUri();
}
