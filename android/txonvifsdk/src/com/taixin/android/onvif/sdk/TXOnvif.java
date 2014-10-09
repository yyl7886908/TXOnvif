package com.taixin.android.onvif.sdk;

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.DeviceCapability;
import com.taixin.android.onvif.sdk.obj.DeviceInfo;
import com.taixin.android.onvif.sdk.obj.MediaProfilesInfo;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;


public class TXOnvif implements SimpleOnvif{
		
	public String deviceService;
	public String mediaService;
	public Context context;
	public TXOnvif(Context context){
		this.context = context;
	}
	@Override
	public ArrayList<Device> discoverDevices() {
			ArrayList<Device> deviceList = _discoverDevices();
			return deviceList;
	}
	
	@Override
	public DeviceCapability getDeviceCapabilities(String username, String password, String deviceService) {
		DeviceCapability capa = _getDeviceCapabilities(username, password, deviceService);
		return capa;
	}
	
	@Override
	public void getDeviceInfo() {
		DeviceInfo info = _getDeviceInfomation("admin", "12345", deviceService);
		System.out.println("info.manufacturer = "+info.getManufacturer());
		getMediaProfiles();
	}
	
	@Override
	public void getMediaProfiles() {
		ArrayList<MediaProfilesInfo> profiles= _getMediaProfiles("admin", "12345", mediaService);
		System.out.println("profiles size = "+profiles.size());
		//getMediaStreamUri();
	}
	
	@Override
	public ArrayList<MediaStreamUri> getMediaStreamUri(String username, String password, String deviceService) {
		ArrayList<MediaStreamUri> streamUri= _getMediaStreamUri(username, password, deviceService);
		return streamUri;
	}
	
	public native ArrayList<Device>								_discoverDevices();
	public native DeviceCapability 								_getDeviceCapabilities(String username, String password, String deviceService);
	public native ArrayList<MediaProfilesInfo>	 	_getMediaProfiles(String username, String password, String mediaService);
	public native ArrayList<MediaStreamUri>				_getMediaStreamUri(String username, String password, String deviceService);
	public native DeviceInfo											_getDeviceInfomation(String username, String password, String deviceService);
	
	static{
		try{  	 
			System.loadLibrary("txonvif");
			System.loadLibrary("txonvif-jni"); 

		} catch (UnsatisfiedLinkError ule) {  
			System.err.println("WARNING: Could not load library!");  
        }  
	}


}
