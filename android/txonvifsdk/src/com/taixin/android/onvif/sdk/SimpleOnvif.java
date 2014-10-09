package com.taixin.android.onvif.sdk;

import java.util.ArrayList;

import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.DeviceCapability;
import com.taixin.android.onvif.sdk.obj.DeviceInfo;
import com.taixin.android.onvif.sdk.obj.MediaProfilesInfo;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;
import com.taixin.android.onvif.sdk.obj.PTZType;

public interface SimpleOnvif {

	public ArrayList<Device> discoverDevices();
	
	public DeviceCapability getDeviceCapabilities(String username, String password, String deviceService);
	
	public DeviceInfo getDeviceInfo(String username, String password, String deviceService);
	
	public ArrayList<MediaProfilesInfo> getMediaProfiles(String username, String password, String mediaService);
	
	public ArrayList<MediaStreamUri> getMediaStreamUri(String username, String password, String deviceService);

	public int ptzContinuousMove(String username, String password, String ptzService, String profileToken, PTZType type, float x, float y, float z);
	
	public int ptzRelativeMove(String username, String password, String ptzService, String profileToken, PTZType type, float x, float y, float z);
	
	public int ptzStop(String username, String password, String ptzService, String profileToken, PTZType type);

}
