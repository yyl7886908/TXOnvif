package com.taixin.android.onvif.sdk;

public class TXOnvif implements SimpleOnvif{
		
	@Override
	public void discoverDevices() {
		 _discoverDevices();
	}
	/**
	 * discover devices
	 */
	public native String _discoverDevices();
	
	static{
		System.loadLibrary("txonvif");
	}
}
