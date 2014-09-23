package com.taixin.android.onvif.sdk;

import com.taixin.android.onvif.sdk.obj.DeviceCapability;
import com.taixin.android.onvif.sdk.obj.DeviceInfo;


public class TXOnvif implements SimpleOnvif{
		
	
	@Override
	public void discoverDevices() {
		DeviceInfo info = _discoverDevices();
		System.out.println("info->xddrs = "+info.getXAddrs());
		System.out.println("info->xddrs = "+info.getItem());
		System.out.println("info->xddrs = "+info.getMetadataVersion());
		System.out.println("info->xddrs = "+info.getTypes());
		
	}
	
	@Override
	public void getDeviceCapabilities(String username, String password,
			String deviceService) {
		_getDeviceCapabilities("admin", "12345", "http://192.168.1.102/onvif/device_service");
	}
	
	public native DeviceInfo _discoverDevices();
	public native void _getDeviceCapabilities(String username, String password, String deviceService);
	
	static{
		try{  
			System.loadLibrary("txonvif-jni");  
			System.loadLibrary("txonvif");
		} catch (UnsatisfiedLinkError ule) {  
			System.err.println("WARNING: Could not load library!");  
        }  
	}

}
