package com.taixin.android.onvif.logic;

import java.util.ArrayList;

import com.taixin.android.onvif.sdk.obj.Device;

/**
 * onivf功能对外抽象接口
 */
public interface IOnvifManager {
	/*发现设备*/
	public ArrayList<Device> discoverDevices();
}
