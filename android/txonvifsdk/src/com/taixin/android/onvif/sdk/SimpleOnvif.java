package com.taixin.android.onvif.sdk;

import java.util.ArrayList;

import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.DeviceCapability;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;

public interface SimpleOnvif {

	public ArrayList<Device> discoverDevices();
	
	public DeviceCapability getDeviceCapabilities(String username, String password, String deviceService);
	
	public void getDeviceInfo();
	
	public void getMediaProfiles();
	
	public ArrayList<MediaStreamUri> getMediaStreamUri(String username, String password, String deviceService);
}
