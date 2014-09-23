package com.taixin.android.onvif.demo;

import android.app.Activity;
import android.os.Bundle;

import com.taixin.android.onvif.sdk.TXOnvif;
import com.taixin.android.onvif.sdk.hello;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("before new");
		TXOnvif onvif = new TXOnvif();
		onvif.getDeviceCapabilities("admin", "12345", "http://192.168.1.102/onvif/device_service");
		
	}
}
