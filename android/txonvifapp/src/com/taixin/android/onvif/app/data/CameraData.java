package com.taixin.android.onvif.app.data;

import java.util.ArrayList;

import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.DeviceCapability;
import com.taixin.android.onvif.sdk.obj.DeviceInfo;
import com.taixin.android.onvif.sdk.obj.MediaProfilesInfo;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;

public class CameraData {
	
	private Device device;
	private DeviceInfo deviceInfo;
	private DeviceCapability capability;
	private ArrayList<MediaProfilesInfo> profiles;
	private ArrayList<MediaStreamUri> streamUri;
	
	public ArrayList<MediaStreamUri> getStreamUri() {
		return streamUri;
	}
	public void setStreamUri(ArrayList<MediaStreamUri> streamUri) {
		this.streamUri = streamUri;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public DeviceCapability getCapability() {
		return capability;
	}
	public void setCapability(DeviceCapability capability) {
		this.capability = capability;
	}
	public ArrayList<MediaProfilesInfo> getProfiles() {
		return profiles;
	}
	public void setProfiles(ArrayList<MediaProfilesInfo> profiles) {
		this.profiles = profiles;
	}
}
