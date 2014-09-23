package com.taixin.android.onvif.sdk;

public interface SimpleOnvif {

	public void discoverDevices();
	
	public void getDeviceCapabilities(String username, String password, String deviceService);
}
