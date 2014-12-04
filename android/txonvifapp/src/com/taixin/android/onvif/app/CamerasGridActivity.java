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
import android.util.Log;
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
import android.widget.Toast;

import com.taixin.android.onvif.app.GridViewAdapter.GridViewHolder;
import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.GridsItemStatus;
import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.util.SerializableUtil;
import com.taixin.android.onvif.sdk.obj.Device;


public class CamerasGridActivity extends Activity {

	private String TAG = "CamerasGridActivity";
	private GridView cameraGrid;
	private ImageButton addDeviceButton;
	private ImageButton homeButton;
	private IOnvifManager onvifMgr;
	private GridViewAdapter gAdapter;
	/*0 代表this，1代表DeviceListActivity, 2代表LoginActivity*/
	private int onResumeFlag;
	private int itemPosition;
	private String authUri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameras_grid);
		onvifMgr = OnvifManager.getInstance();
		onvifMgr.setContext(getApplicationContext());
		displayView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		if(onResumeFlag == 1){
			checkAfterOnResume();
		}else if(onResumeFlag == 2){
			if(onvifMgr.getOnvifData().getCurrentCameras().get(itemPosition).isAuth()){
				/*认证，获取视频流OK*/
				playByItemPosition(itemPosition);
			}else{	
				Toast.makeText(this, "请先登陆", Toast.LENGTH_SHORT).show();
			}
		}
		super.onResume();
	}

	private void displayView(){
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
				}
			} 
		}); 
		addDeviceButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				ArrayList<Device> deviceList= onvifMgr.discoverDevices();
				if(deviceList.size()>0){
					onResumeFlag = 1;
					Intent intent = new Intent();
					intent.setClass(CamerasGridActivity.this, DeviceListActivity.class);
					CamerasGridActivity.this.startActivity(intent);
				}
			}
		});
		homeButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CamerasGridActivity.this, HomeSettingActivity.class);
				CamerasGridActivity.this.startActivity(intent);
			}
		});
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
		onvifMgr.play(position, holder.vv);
	}
}