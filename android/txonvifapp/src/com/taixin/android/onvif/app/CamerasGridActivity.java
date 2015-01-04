package com.taixin.android.onvif.app;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.taixin.android.onvif.app.util.timeUtil;
import com.taixin.android.onvif.sdk.obj.Device;

public class CamerasGridActivity extends Activity implements searchDevicesListener {

	private Handler handler;
	// 定义一个变量，来标识是否退出
	private static boolean isExit = false;
	private String TAG = "CamerasGridActivity";
	private String photoFolder = "/CameraRecordImages/";
	private String videoFolder = "/CameraRecordVideos/";
	private int videoViewWidth;
	private int videoViewHeight;
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
	private int gridItemNoDevice = 0;
	private int gridItemIsNotOnLine = 1;
	private int gridItemIsPlaying = 2;
	private int gridItemIsNotPlaying = 3;
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
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				isExit = false;
			}
		};  
		displayView();
		deviceSearch();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			this.exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
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
				itemPosition = position;
				String uuid = onvifMgr.getGirdItemCameraUUid(position);
				if(uuid != null){
					/*已经添加设备，开始搜索有没有上线*/
					CameraData camera = onvifMgr.checkIsOnLine(uuid);
					if(camera == null){
						Toast toast = Toast.makeText(getApplicationContext(), "设备没有上线", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}else{
						/*获取设备信息，开始播放*/
						GridViewHolder holder = (GridViewHolder) v.getTag();
						if(holder.vv.isPlaying()){
							/*全屏*/
							/*正在播放*/
							Intent intent = new Intent();
							intent.putExtra("grid_item_position", position);
							System.out.println("position = "+position);
							intent.setClass(CamerasGridActivity.this, CameraActivity.class);
							CamerasGridActivity.this.startActivity(intent);
							overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						}else{
							playByItemPosition(position);
						}
					}
				}else{
					/*无设备,先查看有无可用的设备*/
					if(onvifMgr.checkoutVisibleCamera()){
						onResumeFlag = onResumeFlagFromdevice;
						Intent intent = new Intent();
						intent.putExtra("grid_item_position", itemPosition);
						intent.setClass(CamerasGridActivity.this, DeviceListActivity.class);
						CamerasGridActivity.this.startActivity(intent); 
					}else{
						Toast toast = Toast.makeText(getApplicationContext(), "没有可用设备", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
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

	/*播放*/
	public void playByItemPosition(int position){
		for(int i = 0;i <4;i++){
			View view = cameraGrid.getChildAt(i);
			GridViewHolder holder = (GridViewHolder) view.getTag();
			if(i == position){
				holder.vv.setBackgroundColor(R.color.transparency);
				onvifMgr.play(position, holder.vv);
			}
			else{
				if(onvifMgr.getOnvifData().getGridsItemList().get(i).getStatus() == gridItemIsPlaying){
					holder.vv.stopPlayback();
					holder.vv.setBackgroundColor(R.color.black);
				}
			}
		}
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
				cameraGrid.requestFocus();
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
				if(camera != null){
					/*检查用户名密码，开始播放*/
					camera.setIndex(i);
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
				}else{
					/*设备没有在线*/
					onvifMgr.getOnvifData().getGridsItemList().get(i).setStatus(gridItemIsNotOnLine);
				}
			}
		}
	}


	/*按两次退出键退出*/
	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			handler.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}
}