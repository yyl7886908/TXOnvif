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
	public void discoverDevices() {
		ArrayList<Device> deviceList = _discoverDevices();
		System.out.println("deviceList size = "+deviceList.size());
		System.out.println("deviceService = "+deviceList.get(0).getXAddrs());
		String a[] = deviceList.get(0).getXAddrs().split(" "); 
		System.out.println("a 0 = "+a[0]);
		Toast.makeText(context, "找到"+deviceList.size()+"个设备!!", Toast.LENGTH_LONG).show();
		deviceService = a[0];
		getDeviceCapabilities();
	}
	
	@Override
	public void getDeviceCapabilities() {
		DeviceCapability capa = _getDeviceCapabilities("admin", "12345", deviceService);
		System.out.println("deviceService ="+capa.getDeviceService());
		System.out.println("mediaService ="+capa.getMediaService());
		mediaService = capa.getMediaService();
		System.out.println("ptzService ="+capa.getPtzService());
		System.out.println("imageService ="+capa.getImagingService());
		getDeviceInfo();
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
		getMediaStreamUri();
	}
	
	@Override
	public void getMediaStreamUri() {
		ArrayList<MediaStreamUri> streamUri= _getMediaStreamUri("admin", "12345", deviceService);
		System.out.println("streamUri size = "+streamUri.size());
		for(MediaStreamUri stream : streamUri){
			System.out.println("stream name = "+stream.getName());
			System.out.println("stream uri = "+stream.getStreamURI());
		}
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
