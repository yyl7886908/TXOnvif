package com.taixin.android.onvif.logic;

import java.util.ArrayList;

import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.OnvifData;
import com.taixin.android.onvif.sdk.SimpleOnvif;
import com.taixin.android.onvif.sdk.TXOnvif;
import com.taixin.android.onvif.sdk.obj.Device;

/**
 * onivf功能对外抽象接口实现
 */
public class OnvifManager implements IOnvifManager {
	
	private static OnvifManager instance;
	public static OnvifManager getInstance(){
		if(instance == null){
			instance = new OnvifManager();
		}
		return instance;
	}
	
	private SimpleOnvif onvifSdk= null;
	private OnvifData onvfData = null;
	public OnvifManager(){
		onvifSdk = new TXOnvif();
		onvfData = new OnvifData();
	}
	
	public OnvifData getOnvfData() {
		return onvfData;
	}


	public void setOnvfData(OnvifData onvfData) {
		this.onvfData = onvfData;
	}


	@Override
	public ArrayList<Device> discoverDevices() {
		ArrayList<Device> deviceList = onvifSdk.discoverDevices();
		ArrayList<CameraData> cameras = new ArrayList<CameraData>();
		for(int i = 0; i < deviceList.size(); i++){
			CameraData camera = new CameraData();
			camera.setDevice(deviceList.get(i));
			cameras.add(camera);
		}
		onvfData.setCameras(cameras);
		
		return deviceList;
	}

}
