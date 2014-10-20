package com.taixin.android.onvif.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.color;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.taixin.android.onvif.logic.IOnvifManager;
import com.taixin.android.onvif.logic.OnvifManager;
import com.taixin.android.onvif.sdk.obj.Device;

public class CamerasGridActivity extends Activity {
	private GridView cameraGrid;
	private ImageButton addDeviceButton;
	private ImageButton homeButton;
	private IOnvifManager onvifMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameras_grid);
		onvifMgr = OnvifManager.getInstance();
		displayView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}



	private void displayView(){
		addDeviceButton = (ImageButton) findViewById(R.id.add_device_button); 
		homeButton = (ImageButton) findViewById(R.id.home_button);
		cameraGrid = (GridView) findViewById(R.id.GridView1); 
		cameraGrid.setAdapter(new GridViewAdapter(this)); 
		cameraGrid.setOnItemClickListener(new OnItemClickListener() { 
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) { 
				Toast.makeText(CamerasGridActivity.this, "pic" + position, Toast.LENGTH_SHORT).show(); 
			} 
		}); 
		addDeviceButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ArrayList<Device> deviceList= onvifMgr.discoverDevices();
				if(deviceList.size()>0){
					Intent intent = new Intent();
					intent.setClass(CamerasGridActivity.this, DeviceListActivity.class);
					CamerasGridActivity.this.startActivity(intent);
				}
				
			}
		});
	}
}