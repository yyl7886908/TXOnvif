package com.taixin.android.onvif.demo;

import android.app.Activity;
import android.os.Bundle;

import com.taixin.android.onvif.sdk.TXOnvif;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("before new");
		TXOnvif onvif = new TXOnvif(this.getApplicationContext());
		onvif.discoverDevices();
	}
}
