package com.taixin.android.onvif.app;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.taixin.android.onvif.app.GridViewAdapter.GridViewHolder;
import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.GridsItemStatus;
import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.logic.searchDevicesListener;
import com.taixin.android.onvif.app.util.SerializableUtil;
import com.taixin.android.onvif.sdk.obj.Device;

public class CamerasGridActivity extends Activity implements searchDevicesListener {

	private Handler handler;
	private String TAG = "CamerasGridActivity";
	private GridView cameraGrid;
	private ImageButton addDeviceButton;
	private ImageButton homeButton;
	private IOnvifManager onvifMgr;
	private GridViewAdapter gAdapter;
	/*0代表刚进入第一次search 1代表点击button*/
	private int searchDeviceFlag = 0;
	private ProgressBar progressBar;
	/*0 代表this，1代表DeviceListActivity, 2代表LoginActivity*/
	private int onResumeFlag = 0;
	private int onResumeFlagFromdevice = 1;
	private int onResumeFlagFromLogin = 2;
	private int itemPosition;
	private String authUri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameras_grid);
		onvifMgr = OnvifManager.getInstance();
		onvifMgr.setContext(getApplicationContext());
		onvifMgr.setListener(this);
		handler = new Handler();
		displayView();
		deviceSearch();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		System.out.println("on resume====="+onResumeFlag);
		if(onResumeFlag == onResumeFlagFromdevice){
			CameraData camera = onvifMgr.getOnvifData().getCurrentCameras().get(0);
			if(camera.isAuth()){
				/*直接播放*/
				Log.i(TAG, "onResumeFlagFromdevice ====camera is auth");
			}else{
				/*开始认证*/
				Log.i(TAG, "onResumeFlagFromdevice ====camera is not auth");
				LocalCamera lCamera = onvifMgr.getLocalCameraByUUid(camera);
				
				if(lCamera == null){
					/*本地没有保存，输入用户密码*/
					Log.i(TAG, "onResumeFlagFromdevice lCamera====null");
					onResumeFlag = onResumeFlagFromLogin;
					Intent intent = new Intent();
					intent.putExtra("grid_item_position", itemPosition);
					intent.setClass(CamerasGridActivity.this, LoginActivity.class);
					CamerasGridActivity.this.startActivity(intent);
				}else{
					/*认证播放*/
					
				}
			}
		}else if(onResumeFlag == onResumeFlagFromLogin){
			/*播放*/
			this.playByItemPosition(itemPosition);
		}
//		if(onResumeFlag == 1){
//			checkAfterOnResume();
//		}else if(onResumeFlag == 2){
//			if(onvifMgr.getOnvifData().getCurrentCameras().get(itemPosition).isAuth()){
//				/*认证，获取视频流OK*/
//				playByItemPosition(itemPosition);
//			}else{	
//				Toast.makeText(this, "请先登陆", Toast.LENGTH_SHORT).show();
//			}
//		}
		super.onResume();
	}
	
	private void displayView(){
		progressBar = (ProgressBar) findViewById(R.id.device_search_loading);
		addDeviceButton = (ImageButton) findViewById(R.id.add_device_button); 
		
		homeButton = (ImageButton) findViewById(R.id.home_button);
		cameraGrid = (GridView) findViewById(R.id.GridView1);
		
		gAdapter = new GridViewAdapter(this);
		cameraGrid.setAdapter(gAdapter); 
		cameraGrid.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("item selected position = "+position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
		cameraGrid.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {

			}
		});
		cameraGrid.setOnItemClickListener(new OnItemClickListener() { 
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) { 
				int status = onvifMgr.getOnvifData().getGridsItemList().get(position).getStatus();
				itemPosition = position;
				if(status == -1){
					/*无设备*/
					Toast.makeText(getApplicationContext(), "请先添加设备", Toast.LENGTH_SHORT).show();
					onResumeFlag = onResumeFlagFromdevice;
					Intent intent = new Intent();
					intent.putExtra("grid_item_position", itemPosition);
					intent.setClass(CamerasGridActivity.this, DeviceListActivity.class);
					CamerasGridActivity.this.startActivity(intent); 
				}else if(status == 0){
					/*有设备，没有播放,获取设备能力和播放地址，先登陆*/
					/*查看本地有没有存取用户名和密码*/
					CameraData camera = onvifMgr.getOnvifData().getCurrentCameras().get(position);
					LocalCamera lCamera = onvifMgr.getLocalCameraByUUid(camera);
					if(lCamera == null){
						onResumeFlag = 2;
						Intent intent = new Intent();
						intent.putExtra("grid_item_position", position);
						intent.setClass(CamerasGridActivity.this, LoginActivity.class);
						CamerasGridActivity.this.startActivity(intent);
					}else{
						String username = camera.getUsername();
						String password = camera.getPassword();
						String deviceService = onvifMgr.getOnvifData().getCurrentCameras().get(position).getDevice().getDeviceService();
						boolean isGetCapa = onvifMgr.getDeviceCapabilities(username, password, deviceService);
						boolean auth = onvifMgr.getMediaStreamUri(username, password, deviceService);
						if(isGetCapa && auth){
							GridViewHolder holder = (GridViewHolder) v.getTag();
							/*vv背景设置为透明*/
							holder.vv.setBackgroundColor(R.color.transparency);
							onvifMgr.play(position, holder.vv);
						}
					}
				}else if(status == 1){
					/*正在播放*/
					Intent intent = new Intent();
					intent.putExtra("grid_item_position", position);
					System.out.println("position = "+position);
					intent.setClass(CamerasGridActivity.this, CameraActivity.class);
					CamerasGridActivity.this.startActivity(intent);
					//CamerasGridActivity.this.startActivityForResult(intent, 1);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				}
			} 
		}); 
		addDeviceButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				System.out.println("add button is clicked!");
				searchDeviceFlag = 1;
				deviceSearch();
			}
		});
		homeButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CamerasGridActivity.this, HomeSettingActivity.class);
				CamerasGridActivity.this.startActivity(intent);
			}
		});
		
		addDeviceButton.requestFocus();
	}

	/*onresume 后检查数据*/
	public void checkAfterOnResume(){
	
		ArrayList<GridsItemStatus> items = onvifMgr.getOnvifData().getGridsItemList();
		for(int i = 0;i<items.size();i++){
			View view = cameraGrid.getChildAt(i);
			GridViewHolder holder = (GridViewHolder) view.getTag();
			holder.vv.setBackgroundResource(Color.TRANSPARENT);
			if(items.get(i).getStatus() == 0){
				holder.vv.setBackgroundResource(R.drawable.camera_default);
			}
		}
	}

	/*播放*/
	public void playByItemPosition(int position){
		View view = cameraGrid.getChildAt(position);
		GridViewHolder holder = (GridViewHolder) view.getTag();
		holder.vv.setBackgroundColor(R.color.transparency);
		onvifMgr.play(position, holder.vv);
	}
	
	/*搜索设备，单独启线程执行*/
	public void deviceSearch(){
		progressBar.setVisibility(View.VISIBLE);
		Thread deviceSearchThread = new Thread(new Runnable(){
			@Override
			public void run() {
				onvifMgr.discoverDevices();				
			}
		});

		deviceSearchThread.start();	
	}

	@Override
	public void searchEnd() {
		System.out.println("camera home activity search end call back");
		ArrayList<Device> deviceList= onvifMgr.getOnvifData().getDeviceList();
		handler.post(new Runnable(){
			@Override
			public void run() {
				progressBar.setVisibility(View.INVISIBLE);
				autoPlayAfterSearch();
			}
		});
		
}
	/*搜索完毕自动播放*/
	public void autoPlayAfterSearch() {
		for(int i = 0; i<4;i++){
			String uuid = onvifMgr.getGirdItemCameraUUid(i);
			if(uuid != null){
				/*是否在线*/
				CameraData camera = onvifMgr.checkIsOnLine(uuid);
				camera.setIndex(i);
				if(camera != null){
					/*检查用户名密码，开始播放*/
					LocalCamera lCamera = onvifMgr.getLocalCameraByUUid(camera);
					if(lCamera != null){
						camera.setUsername(lCamera.getUsername());
						camera.setPassword(lCamera.getPassword());
						String deviceService = camera.getDevice().getDeviceService();
						boolean isGetCapa = onvifMgr.getDeviceCapabilities(lCamera.getUsername(), lCamera.getPassword(), deviceService);
						boolean auth = onvifMgr.getMediaStreamUri(lCamera.getUsername(), lCamera.getPassword(), deviceService);
						if(isGetCapa && auth){
							
							this.playByItemPosition(i);
						}
					}
				}
			}
		}
	}
	/*不是第一次使用的时候，搜索完毕自动匹配存储好的密码进行链接*/
	private void autoMatchAfterDiscoverEnd(){
		ArrayList<CameraData> cameraList = onvifMgr.getOnvifData().getCameras();//搜索到的所有设备
		System.out.println("camera List size ==="+cameraList.size());
		//先清空一下所有的List
		onvifMgr.getOnvifData().initData();
		for(CameraData camera : cameraList){
			LocalCamera lCamera = onvifMgr.getLocalCameraByUUid(camera);
			if(lCamera != null){
				camera.setUsername(lCamera.getUsername());
				camera.setPassword(lCamera.getPassword());
				
				onvifMgr.getOnvifData().getCurrentCameras().add(camera);
			}
		}
		System.out.println("current size====="+onvifMgr.getOnvifData().getCurrentCameras().size());
		/*本地没有保存用户名和密码*/
		if(onvifMgr.getOnvifData().getCurrentCameras().size()<=0){
			onResumeFlag = 1;
			Intent intent = new Intent();
			intent.setClass(CamerasGridActivity.this, DeviceListActivity.class);
			CamerasGridActivity.this.startActivity(intent);
		}else{
			/*本地保存用户名和密码，直接播放*/
			for(int i = 0; i<onvifMgr.getOnvifData().getCurrentCameras().size(); i++){
				onvifMgr.getOnvifData().getGridsItemList().get(i).setStatus(0);
				View view = cameraGrid.getChildAt(i);
				GridViewHolder holder = (GridViewHolder) view.getTag();
				holder.vv.setBackgroundResource(R.drawable.camera_default);
				
				CameraData camera = onvifMgr.getOnvifData().getCurrentCameras().get(i);
				String username = camera.getUsername();
				String password = camera.getPassword();
				String deviceService = onvifMgr.getOnvifData().getCurrentCameras().get(i).getDevice().getDeviceService();
				boolean isGetCapa = onvifMgr.getDeviceCapabilities(username, password, deviceService);
				boolean auth = onvifMgr.getMediaStreamUri(username, password, deviceService);
				if(isGetCapa && auth){
					/*vv背景设置为透明*/
					holder.vv.setBackgroundColor(R.color.transparency);
					onvifMgr.play(i, holder.vv);
					cameraGrid.requestFocus();
				}
			}
		}
	}
}