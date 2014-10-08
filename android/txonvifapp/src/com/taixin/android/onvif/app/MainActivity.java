package com.taixin.android.onvif.app;

import android.app.Activity;
import android.os.Bundle;

import com.taixin.android.onvif.widget.test;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		test t = new test();
		t.syso();
	}
}
