package com.taixin.android.onvif.sdk;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.DeviceCapability;
import com.taixin.android.onvif.sdk.obj.DeviceInfo;
import com.taixin.android.onvif.sdk.obj.ImagingSetting;
import com.taixin.android.onvif.sdk.obj.MediaProfilesInfo;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;
import com.taixin.android.onvif.sdk.obj.PTZType;


public class TXOnvif implements SimpleOnvif{
		
	public String deviceService;
	public String mediaService;
	private String TAG = "TXOnvif";
	@Override
	public ArrayList<Device> discoverDevices() {
			ArrayList<Device> deviceList = _discoverDevices();
			Log.i(TAG, "deviceList size = "+deviceList.size());
			for(Device device : deviceList){
				String Address = device.getAddress();
				String XAddr = device.getXAddrs();
				String a[] = XAddr.split(" ");
				device.setDeviceService(a[0]);
				String uuids[] = Address.split(":");
				if(uuids.length == 1){
					device.setUuid(uuids[0]);
				}else{
					device.setUuid(uuids[2]);
				}
			}
			return deviceList;
	}
	
	@Override
	public DeviceCapability getDeviceCapabilities(String username, String password, String deviceService) {
		DeviceCapability capa = _getDeviceCapabilities(username, password, deviceService);
		return capa;
	}
	
	@Override
	public DeviceInfo getDeviceInfo(String username, String password, String deviceService) {
		DeviceInfo info = _getDeviceInfomation(username, password, deviceService);
		System.out.println("info.manufacturer = "+info.getManufacturer());
		return info;
	}
	
	@Override
	public ArrayList<MediaProfilesInfo> getMediaProfiles(String username, String password, String mediaService) {
		ArrayList<MediaProfilesInfo> profiles= _getMediaProfiles(username, password, mediaService);
		return profiles;
	}
	
	@Override
	public ArrayList<MediaStreamUri> getMediaStreamUri(String username, String password, String deviceService) {
		ArrayList<MediaStreamUri> streamUri= _getMediaStreamUri(username, password, deviceService);
		return streamUri;
	}
	

	@Override
	public ImagingSetting getImagingSetting(String username, String password,String imagingService, String videoSourceToken) {
		ImagingSetting imgSetting = _getImagingSetting(username, password, imagingService, videoSourceToken);
		return imgSetting;
	}
	
	@Override
	public int setImagingSetting(String username, String password,String imagingService, String videoSourceToken,ImagingSetting imgSetting) {
		return _setImagingSetting(username, password, imagingService, videoSourceToken, imgSetting);
	}
	
	@Override
	public int ptzContinuousMove(String username, String password,
			String ptzService, String profileToken, PTZType type, float x,
			float y, float z) {
		int ptzType = 0;
		switch(type){
		case PTZ_MOVE:
			ptzType = 0;
			break;
		case PTZ_ZOOM:
			ptzType = 1;
			break;
		}
		return _ptzContinuousMove(username, password, ptzService, profileToken, ptzType, x, y, z);
	}
	
	@Override
	public int ptzRelativeMove(String username, String password,
			String ptzService, String profileToken, PTZType type, float x,
			float y, float z) {
		int ptzType = 0;
		switch(type){
		case PTZ_MOVE:
			ptzType = 0;
			break;
		case PTZ_ZOOM:
			ptzType = 1;
			break;
		}
		return _ptzRelativeMove(username, password, ptzService, profileToken, ptzType, x, y, z);
	}

	
	@Override
	public int ptzStop(String username, String password, String ptzService, String profileToken, PTZType type) {
		int ptzType = 0;
		switch(type){
		case PTZ_MOVE:
			ptzType = 0;
			break;
		case PTZ_ZOOM:
			ptzType = 1;
			break;
		}
		return _ptzStop(username, password, ptzService, profileToken, ptzType);
	}
	
	public native ArrayList<Device>								_discoverDevices();
	public native DeviceCapability 								_getDeviceCapabilities(String username, String password, String deviceService);
	public native ArrayList<MediaProfilesInfo>	 	_getMediaProfiles(String username, String password, String mediaService);
	public native ArrayList<MediaStreamUri>				_getMediaStreamUri(String username, String password, String deviceService);
	public native DeviceInfo											_getDeviceInfomation(String username, String password, String deviceService);
	public native ImagingSetting									_getImagingSetting(String username, String password, String imagingService, String videoSourceToken);
	public native int 														_setImagingSetting(String username, String password,String imagingService, String videoSourceToken,ImagingSetting imgSetting);
	public native int 														_ptzStop(String username, String password, String ptzService, String profileToken, int ptzType);
	public native int 														_ptzContinuousMove(String username, String password,String ptzService, String profileToken, int type, float x, float y, float z);
	public native int 														_ptzRelativeMove(String username, String password,String ptzService, String profileToken, int type, float x, float y, float z);
	
	static{
		try{  	 
			System.loadLibrary("txonvif");
			System.loadLibrary("txonvif-jni"); 

		} catch (UnsatisfiedLinkError ule) {  
			System.err.println("WARNING: Could not load library!");  
        }  
	}

}
