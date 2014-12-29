package com.taixin.android.onvif.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.logic.searchDevicesListener;
import com.taixin.android.onvif.app.util.FileUtil;
import com.taixin.android.onvif.app.util.Usb;
import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.ImagingSetting;
import com.taixin.ffmpeg.player.ITxRecorder;
import com.taixin.ffmpeg.player.TXFFMpegRecorder;
import com.taixin.ffmpeg.widget.VideoView;

public class CameraHomeActivity extends Activity implements searchDevicesListener{

	private String TAG = "CamerasGridActivity";
	// 定义一个变量，来标识是否退出
	private static boolean isExit = false;
	private String photoFolder = "/CameraRecordImages/";
	private String videoFolder = "/CameraRecordVideos/";
	private int videoViewWidth;
	private int videoViewHeight;
	private Handler handler;
	private ImageButton addDeviceButton;
	private ImageButton homeButton;
	private ProgressBar progressBar;
	private VideoView vv;
	private IOnvifManager onvifMgr;
	private ITxRecorder recorder;
	/*0代表刚进入第一次search 1代表点击button*/
	private int searchDeviceFlag = 0;
	/*0 代表this，1代表DeviceListActivity, 2代表LoginActivity, 3代表浏览FileActivity*/
	private int onResumeFlag;
	private int itemPosition;
	/*代表默认的固定位置0*/
	private int position = 0;
	/*-1 代表默认图片 ； 0代表搜到设备，没有播放； 1代表正在播放*/
	private int cameraStatus = -1;

	private boolean vvIsFullScreen;
	/*摄像头控制相关*/
	/*控制菜单, 频道切换菜单*/
	private CameraData camera;
	private String username;
	private String password;
	private String deviceService;
	private String imageService;
	private String ptzService;
	private String mediaService;
	private String profileToken;
	private PopupWindow menu, channelMenu, imageMenu, fileMenu, recordMenu,photoingMenu;
	private LayoutInflater inflater;	
	private View layout;
	private ImageButton channelSwitchBtn, imageSetBtn, photoBtn, recordingBtn, fileBtn, recordFlagBtn;
	private ImageButton highBtn, middleBtn, lowBtn;
	private ImageButton picBtn, videoBtn;
	private ImageView recordFlagView;
	private SeekBar chromBar, brightBar, constrastbar;
	//private Button imageSaveBtn;
	/*第几路视频*/
	private int channelFlag = 0;
	private boolean isCruising = false;
	private boolean cruise = true;
	private String AUTHUri;/*录制的时候使用*/
	private boolean recordingFlag = false;

	/**
	 * 屏幕录制状态提示
	 * 
	 */
	private View view;// 透明窗体
	private long startTime;
	private boolean viewAdded = false;// 透明窗体是否已经显示
	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;
	/** 录制闪烁动画 */
	private Animation animation;
	private ImageView iconImage;
	private TextView tvNotice;
	private TextView tvNoticeTime;
	private Chronometer chronometer;  /*计时器*/ 
	private boolean is_pvr_pause=false;
	private String photoingImagePath;
	private ImageView floatImage;
	public Timer mTimer ;// 定时器
	private int timeCtn;
	private int timeCountFlag =1;

	@Override
	protected void onCreate(Bundle bundle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(bundle);
		setContentView(R.layout.camera_home);
		onvifMgr = OnvifManager.getInstance();
		onvifMgr.setContext(getApplicationContext());
		onvifMgr.setListener(this);
		recorder = new TXFFMpegRecorder();
		displayView();
		deviceSearch();
		initMenu();
		CreatNoticeView();
		animation = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.pvr_notice);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == timeCountFlag){
					long nowTime = System.currentTimeMillis();
					SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
					Date date = new Date(nowTime - startTime);
					System.out.println(formatter.format(date));
					String str=formatter.format(date);
					tvNoticeTime.setText(str);
				}else{
					isExit = false;
				}
			}
		};   
	}
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("onResume ======= onResumeFlag = "+onResumeFlag);
		if(onResumeFlag == 1){
			System.out.println("onvifMgr size ===="+onvifMgr.getOnvifData().getCurrentCameras().size());
			if(onvifMgr.getOnvifData().getCurrentCameras().size() <= 0){
				System.out.println("================");
				vv.stopPlayback();
				return;
			}else{
				vv.requestFocus();
				cameraStatus = 0;
				playVV();
			}
		}else if(onResumeFlag == 2){
			if(onvifMgr.getOnvifData().getCurrentCameras().get(position).isAuth()){
				/*认证，获取视频流OK*/
				onvifMgr.play(position, vv);
			}else{	
				Toast.makeText(this, "请先登陆", Toast.LENGTH_SHORT).show();
			}
		}else if(onResumeFlag ==3){
			fileMenu.dismiss();
		}
		//super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println(" onKeyDown========="+KeyEvent.KEYCODE_DPAD_DOWN);
		if(vvIsFullScreen){
			if(!menu.isShowing()){
				switch(keyCode){
				case KeyEvent.KEYCODE_DPAD_LEFT:
					onvifMgr.ptzLeft(username, password, ptzService, profileToken);
					return true;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					onvifMgr.ptzRight(username, password, ptzService, profileToken);
					return true;
				case KeyEvent.KEYCODE_DPAD_UP:
					onvifMgr.ptzUp(username, password, ptzService, profileToken);
					return true;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					onvifMgr.ptzDown(username, password, ptzService, profileToken);
					return true;
				case KeyEvent.KEYCODE_MENU:	
					showCtrlMenu();
					return true;
				case KeyEvent.KEYCODE_DPAD_CENTER:
					showCtrlMenu();
					return true;
				case KeyEvent.KEYCODE_BACK:
					this.setVVDefault();
					return true;
				}
			}
		}else{
			if(keyCode == KeyEvent.KEYCODE_BACK){
				this.exit();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void displayView(){
		progressBar = (ProgressBar) findViewById(R.id.loading);
		//progressBar.setVisibility(View.GONE);
		addDeviceButton = (ImageButton) findViewById(R.id.home_add_device_button); 
		addDeviceButton.requestFocus();
		homeButton = (ImageButton) findViewById(R.id.home_home_button);
		vv = (VideoView) findViewById(R.id.homeVideoView);
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
				System.out.println("home button is clicked!");
				Intent intent = new Intent(CameraHomeActivity.this, HomeSettingActivity.class);
				CameraHomeActivity.this.startActivity(intent);
			}
		});
		VVOnClickListener listener = new VVOnClickListener();
		vv.setOnClickListener(listener);
		//
	}
	/*Video View的点击事件*/
	class VVOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(!vvIsFullScreen){
				playVV();
			}else{
				showCtrlMenu();
			}
		}

	};

	/*video view全屏其他控件隐藏*/
	private void setVVFullScreen(){
		addDeviceButton.setVisibility(View.GONE);
		homeButton.setVisibility(View.GONE);
		FrameLayout.LayoutParams layoutParams=  
				new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT); 
		vv.setLayoutParams(layoutParams);
		vvIsFullScreen = true;
		if(camera == null){
			this.getDeviceInfo();
		}
	}
	/*video view 全屏状态返回*/
	private void setVVDefault(){
		addDeviceButton.setVisibility(View.VISIBLE);
		homeButton.setVisibility(View.VISIBLE);
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		FrameLayout.LayoutParams layoutParams = null;
		if(srceenW > 1800 && screenH > 1000){
			layoutParams= new FrameLayout.LayoutParams(videoViewWidth, videoViewHeight); 
		}else{
			layoutParams= new FrameLayout.LayoutParams(900, 500); 
		}
		vv.setLayoutParams(layoutParams);
		System.out.println("layoutParams width = "+layoutParams.width);
		System.out.println("layoutParams height = "+layoutParams.height);
		vvIsFullScreen = false;

	}
	/*获取设备的信息*/
	public void getDeviceInfo(){
		camera = onvifMgr.getOnvifData().getCurrentCameras().get(position);
		password = camera.getPassword();
		username = camera.getUsername();
		deviceService = camera.getDevice().getDeviceService();
		if(camera.getCapability() == null || camera.getCapability().equals(null)){
			System.out.println("username password deviceservice-->"+username+password+deviceService);
			boolean getCapa = onvifMgr.getDeviceCapabilities(username, password, deviceService);
			if(!getCapa){
				Toast toast = Toast.makeText(this, "鉴权失败！！！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
		}
		mediaService = camera.getCapability().getMediaService();
		ptzService = camera.getCapability().getPtzService();
		imageService = camera.getCapability().getImagingService();
		boolean getProf = onvifMgr.getMediaProfiles(username, password, mediaService);
		if(getProf){
			//Toast.makeText(this, "获取profiles成功", Toast.LENGTH_SHORT).show();;
		}
		profileToken = camera.getProfiles().get(channelFlag).getToken();
	}

	private void playVV(){
		if(cameraStatus == -1){
			/*无设备*/
			Toast toast = Toast.makeText(getApplicationContext(), "请先添加设备", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}else if(cameraStatus == 0){
			/*没有认证的话先认证*/
			checkAndSetCameraAuth(position);
		}else if(cameraStatus == 1){
			if(!vvIsFullScreen){
				setVVFullScreen();	
			}
			else{
				if(!menu.isShowing()){

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
	/*搜索设备完毕的回调,优先连接上一次连接的摄像头，没有的话手动选择摄像头*/
	@Override
	public void searchEnd() {
		System.out.println("camera home activity search end call back");
		ArrayList<Device> deviceList= onvifMgr.getOnvifData().getDeviceList();
		handler.post(new Runnable(){
			@Override
			public void run() {
				progressBar.setVisibility(View.INVISIBLE);
			}
		});
		if(deviceList.size()<=0){
			handler.post(new Runnable(){
				@Override
				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(), "没有搜索到设备，请确认摄像头在局域网内", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			});
			return;
		}

		if(deviceList.size()>0){
			if(searchDeviceFlag == 0){
				/*优先连接上一次连接的摄像头*/
				LocalCamera lCamera = onvifMgr.getPreviousCameraInfo();
				if(lCamera != null){
					CameraData camera = onvifMgr.checkPrevCameraIsSearched(lCamera);
					if(camera != null){
						/*认证播放*/
						String username = lCamera.getUsername();
						String password = lCamera.getPassword();
						String deviceService = camera.getDevice().getDeviceService();
						onvifMgr.getOnvifData().getCurrentCameras().clear();
						onvifMgr.getOnvifData().getCurrentCameras().add(camera);
						boolean isGetCapa = onvifMgr.getDeviceCapabilities(username, password, deviceService);
						boolean auth = onvifMgr.getMediaStreamUri(username, password, deviceService);
						if(isGetCapa && auth){
							handler.post(new Runnable(){
								@Override
								public void run() {
									onvifMgr.play(position, vv);
									videoViewWidth = vv.getWidth();
									videoViewHeight = vv.getHeight();
									System.out.println("===================w = "+videoViewWidth+"=======h = "+videoViewHeight);
								}
							});
							cameraStatus = 1;
						}
						return;
					}
				}
			}
			onResumeFlag = 1;
			Intent intent = new Intent();
			intent.setClass(CameraHomeActivity.this, DeviceListActivity.class);
			CameraHomeActivity.this.startActivity(intent);
		}else{
			Log.e("camera home activity ", "did not find any cameras!!!");
			Toast toast = Toast.makeText(this, "没有搜索到摄像头设备", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	/*显示的摄像头状态，没有认证的话认证 认证的话开始播放*/
	private void checkAndSetCameraAuth(int position){
		CameraData camera = onvifMgr.getOnvifData().getCurrentCameras().get(position);
		LocalCamera lCamera = onvifMgr.getLocalCameraByUUid(camera);
		if(lCamera == null){
			onResumeFlag = 2;
			Intent intent = new Intent();
			intent.putExtra("grid_item_position", position);
			intent.setClass(CameraHomeActivity.this, LoginActivity.class);
			CameraHomeActivity.this.startActivity(intent);
		}else{
			String username = camera.getUsername();
			String password = camera.getPassword();
			String deviceService = onvifMgr.getOnvifData().getCurrentCameras().get(position).getDevice().getDeviceService();
			boolean isGetCapa = onvifMgr.getDeviceCapabilities(username, password, deviceService);
			//boolean auth = onvifMgr.getMediaStreamUri(username, password, deviceService);
			if(isGetCapa){
				if(onvifMgr.getMediaStreamUri(username, password, deviceService)){
					onvifMgr.play(position, vv);
					cameraStatus = 1;
				}else{
					Toast toast = Toast.makeText(getApplicationContext(), "获取视频流地址失败	", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}else {
				Toast toast = Toast.makeText(getApplicationContext(), "鉴权失败", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}
	}
	/*摄像头控制相关*/
	/*初始化控制菜单*/
	public void initMenu(){
		initCtrlMenu();
		initFileMenu();
		initChannelMenu();
		initImageMenu();
		initRecordMenu();
		initPhotoingMenu();
	}

	public void initCtrlMenu(){
		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.camera_control_menu, null,false);
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			menu = new PopupWindow(layout, srceenW -200, 150, true);
		}else{
			menu = new PopupWindow(layout, srceenW -200, 100, true);
		}
		menu.setFocusable(true);
		menu.setBackgroundDrawable(new BitmapDrawable());
		menu.setAnimationStyle(android.R.style.Animation_Dialog);  

		ctrlMenuOnClickListener listener = new ctrlMenuOnClickListener();
		channelSwitchBtn = (ImageButton) layout.findViewById(R.id.channel_switch_btn);
		channelSwitchBtn.setOnClickListener(listener);
		imageSetBtn = (ImageButton) layout.findViewById(R.id.image_setting_btn);
		imageSetBtn.setOnClickListener(listener);
		//	ptzBtn = (ImageButton) layout.findViewById(R.id.ptz_button);
		//ptzBtn.setOnClickListener(listener);
		photoBtn = (ImageButton) layout.findViewById(R.id.photo_button);
		photoBtn.setOnClickListener(listener);
		recordingBtn = (ImageButton) layout.findViewById(R.id.recording_button);
		recordingBtn.setOnClickListener(listener);
		fileBtn = (ImageButton) layout.findViewById(R.id.file_button);
		fileBtn.setOnClickListener(listener);
	}
	/*控制菜单按钮的点击事件*/
	class ctrlMenuOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v==fileBtn){
				if(!fileMenu.isShowing())
					showFileMenu();
				else
					fileMenu.dismiss();
			}else if(v == channelSwitchBtn){
				if(!channelMenu.isShowing())
					showChannelMenu();
				else
					channelMenu.dismiss();
			}else if(v == imageSetBtn){
				if(!imageMenu.isShowing()){
					CameraData camera = onvifMgr.getOnvifData().getCurrentCameras().get(position);
					String videoSourceToken = camera.getProfiles().get(channelFlag).getVideoSourceToken();
					ImagingSetting setting = onvifMgr.getImagingSetting(username, password, imageService, videoSourceToken);
					System.out.println("亮度= "+setting.getBrightness()+setting.getColorSaturation()+setting.getContrast());
					System.out.println("色= "+setting.getBrightness());
					System.out.println("对比度= "+setting.getContrast());
					brightBar.setProgress((int)setting.getBrightness()/10);
					chromBar.setProgress((int)setting.getColorSaturation()/10);
					constrastbar.setProgress((int)setting.getContrast()/10);
					showImageMenu();
				}
				else 
					imageMenu.dismiss();
			}
			//			else if(v == ptzBtn){
			//				if(cruise){
			//					//onvifMgr.ptzCruise(username, password, ptzService, profileToken, cruise);
			//					cruise = false;
			//				}else{
			//					//onvifMgr.ptzCruise(username, password, ptzService, profileToken,cruise);
			//					cruise = true;
			//				}
			//			}
			else if(v == photoBtn){
				List<String> list = Usb.getUsbDirList();
				if(list.size()<=0){
					Toast toast = Toast.makeText(CameraHomeActivity.this, "请先插入U盘", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				String folder = list.get(0)+photoFolder;
				FileUtil.createDir(folder);
				String filename = list.get(0)+"/CameraRecordImages/"+getCurrentTime()+".jpg";
				FileUtil.createFile(filename);
				vv.photoImage(filename);
				photoingImagePath = filename;
				new Handler().postDelayed(new Runnable(){  
					public void run() {  
						showPhotoingMenu();
					}  
				}, 500); 
			}else if(v == recordingBtn){
				if(!recordingFlag){
					List<String> list = Usb.getUsbDirList();
					if(list.size()<=0){
						Toast toast = Toast.makeText(CameraHomeActivity.this, "请先插入U盘", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
					String folder = list.get(0)+videoFolder;
					FileUtil.createDir(folder);
					String filename = list.get(0)+videoFolder+getCurrentTime()+".ts";
					FileUtil.createFile(filename);
					System.out.println("java recording file name = "+filename);
					AUTHUri=onvifMgr.getAuthUriByPosition(position);
					//vv.startRecordingRtspStream(AUTHUri, filename, 0);
					recorder.startRecoderRTSPStream(AUTHUri, filename, -1);
					recordingFlag = true;
					animation = AnimationUtils.loadAnimation(getApplicationContext(),
							R.anim.pvr_notice);
					refreshorShowView();
				}else{
					recorder.stopRecordRTSPStream();
					recordingFlag = false;
					recordMenu.dismiss();
					removeView();
				}

			}
		}
	}

	public void initRecordMenu(){
		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.record_menu, null,false);
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			recordMenu = new PopupWindow(layout, 100, 50, true);
		}else{
			recordMenu = new PopupWindow(layout, 100, 100, true);
		}
		recordMenu.setBackgroundDrawable(new BitmapDrawable());
		recordMenu.setAnimationStyle(android.R.style.Animation_Dialog);
		recordFlagBtn = (ImageButton) layout.findViewById(R.id.record_flag_btn);
		recordFlagBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				recorder.stopRecordRTSPStream();
				recordingFlag = false;
				recordMenu.dismiss();
			}

		});
	}
	//显示录制标志菜单
	private void showRecordMenu(){
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			recordMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 500, -screenH +300);  
		}else{
			recordMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 500, -screenH + 300);  
		}
		recordMenu.setFocusable(true);
		recordMenu.update();  
	}
	/*初始化file弹出框*/
	public void initFileMenu(){
		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.file_ctrl_menu, null,false);
		LinearLayout layMenu = (LinearLayout) layout.findViewById(R.id.fileCtrlMenuLay);
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			fileMenu = new PopupWindow(layout, srceenW -600, 130, true);
		}else{
			fileMenu = new PopupWindow(layout, srceenW -600, 80, true);
		}

		fileMenu.setFocusable(true);
		fileMenu.setBackgroundDrawable(new BitmapDrawable());
		fileMenu.setAnimationStyle(android.R.style.Animation_Dialog); 
		fileMenu.setOutsideTouchable(true);
		picBtn = (ImageButton) layout.findViewById(R.id.pic_button);
		videoBtn = (ImageButton) layout.findViewById(R.id.video_button);
		fileMenuOnClickListener listener = new fileMenuOnClickListener();
		picBtn.setOnClickListener(listener);
		videoBtn.setOnClickListener(listener);
		picBtn.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					fileMenu.dismiss();
					return true;
				}
				return false;
			}
		});
		videoBtn.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					fileMenu.dismiss();
					return true;
				}
				return false;
			}
		});
	}

	/*初始化channel弹出框*/
	public void initChannelMenu(){
		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.channel_switch_menu, null,false);
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			channelMenu = new PopupWindow(layout, srceenW -800, 130, true);
		}else{
			channelMenu = new PopupWindow(layout, srceenW -700, 80, true);
		}
		channelMenu.setFocusable(true);
		channelMenu.setBackgroundDrawable(new BitmapDrawable());
		channelMenu.setAnimationStyle(android.R.style.Animation_Dialog); 
		highBtn = (ImageButton) layout.findViewById(R.id.high_button);
		middleBtn = (ImageButton) layout.findViewById(R.id.middle_button);
		lowBtn = (ImageButton) layout.findViewById(R.id.low_button);
		channelMenuOnClickListener listener = new channelMenuOnClickListener();
		highBtn.setOnClickListener(listener);
		middleBtn.setOnClickListener(listener);
		lowBtn.setOnClickListener(listener);
		highBtn.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					channelMenu.dismiss();
					return true;
				}
				return false;
			}
		});
		middleBtn.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					channelMenu.dismiss();
					return true;
				}
				return false;
			}
		});
		lowBtn.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					channelMenu.dismiss();
					return true;
				}
				return false;
			}
		});
	}
	//显示控制菜单
	private void showCtrlMenu(){
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			menu.showAsDropDown(this.findViewById(R.id.homeVideoView), 100, -screenH/4);  
		}else{
			menu.showAsDropDown(this.findViewById(R.id.homeVideoView), 100, -screenH/4);  
		}
		menu.update();  
	}
	/*控制菜单按钮的点击事件*/
	class fileMenuOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == picBtn){
				List<String> list = Usb.getUsbDirList();
				if(list.size()<=0){
					Toast toast = Toast.makeText(CameraHomeActivity.this, "请先插入U盘", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}else{
					onResumeFlag = 3;
					Intent intent = new Intent(CameraHomeActivity.this, CameraImagesGridActivity.class);
					CameraHomeActivity.this.startActivity(intent);
				}	
			}else if(v == videoBtn){
				List<String> list = Usb.getUsbDirList();
				if(list.size()<=0){
					Toast toast = Toast.makeText(CameraHomeActivity.this, "请先插入U盘", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}else{
					onResumeFlag = 3;
					Intent intent = new Intent(CameraHomeActivity.this, CameraVideosListActivity.class);
					CameraHomeActivity.this.startActivity(intent);
				}	
			}
		}
	}
	/*控制菜单按钮的点击事件*/
	class channelMenuOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == highBtn){
				Toast.makeText(CameraHomeActivity.this, "高清", Toast.LENGTH_SHORT).show();
				switchChannelById(0);
				channelSwitchBtn.setImageDrawable(getResources().getDrawable(R.drawable.high_stream_selector));
			}else if(v == middleBtn){
				Toast.makeText(CameraHomeActivity.this, "均衡", Toast.LENGTH_SHORT).show();
				switchChannelById(1);
				channelSwitchBtn.setImageDrawable(getResources().getDrawable(R.drawable.middle_stream_selector));
			}else if(v ==lowBtn){
				Toast.makeText(CameraHomeActivity.this, "流畅", Toast.LENGTH_SHORT).show();
				switchChannelById(2);
				channelSwitchBtn.setImageDrawable(getResources().getDrawable(R.drawable.low_stream_selector));
			}
		}
	}

	/*根据id切换视频流*/
	public void switchChannelById(int id){
		String uri = null;
		int size = camera.getStreamUri().size();
		if(id<=size-1)
			uri =camera.getStreamUri().get(id).getStreamURI();
		else
			uri = camera.getStreamUri().get(size-1).getStreamURI();
		String authUri = getAuthUri(username, password,uri);
		System.out.println("authuri = "+authUri);
		vv.setVideoPath(authUri);
		vv.requestFocus();
		vv.start();	
		if(id<=size-1)
			channelFlag = id;
		else 
			channelFlag = size-1;
	}
	/*username uri password 拼接,得到可以认证的RTSP视频流地址*/
	public String getAuthUri(String username, String password, String uri){
		//String authUri = "";
		String uris[] = uri.split("//");
		String authUri = uris[0]+"//"+username+":"+password+"@"+uris[1];
		this.AUTHUri = authUri;
		return authUri;
	}
	private void showChannelMenu(){
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			channelMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 300, -screenH/2);    
		}else{
			channelMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 300, -screenH/2);   
		}

		channelMenu.update(); 
	}

	private void showFileMenu(){
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			fileMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 300, -screenH/2);    
		}else{
			fileMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 300, -screenH/2);   
		}

		fileMenu.update(); 
	}
	/**
	 * 图像处理接口
	 */
	/*初始化image弹出框*/
	public void initImageMenu(){
		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.image_setting_menu, null,false);
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			imageMenu = new PopupWindow(layout, srceenW -600, 300, true);;    
		}else{
			imageMenu = new PopupWindow(layout, srceenW -600, 250, true);
		}

		imageMenu.setFocusable(true);
		imageMenu.setBackgroundDrawable(new BitmapDrawable());
		imageMenu.setAnimationStyle(android.R.style.Animation_Dialog); 
		brightBar = (SeekBar) layout.findViewById(R.id.bright_seekbar);
		chromBar = (SeekBar) layout.findViewById(R.id.chrom_seekbar);
		constrastbar = (SeekBar) layout.findViewById(R.id.contrast_seekbar);
		seekBarListener barListener = new seekBarListener();
		brightBar.setOnSeekBarChangeListener(barListener);
		chromBar.setOnSeekBarChangeListener(barListener);
		constrastbar.setOnSeekBarChangeListener(barListener);
		constrastbar.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					imageMenu.dismiss();
					return true;
				}
				return false;
			}
			
		});
		//		imageSaveBtn= (Button) layout.findViewById(R.id.imgae_save_btn);
		//		ImageMenuOnClickListener listener = new ImageMenuOnClickListener();
		//		imageSaveBtn.setOnClickListener(listener);
	}
	//显示控制菜单
	private void showImageMenu(){
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			imageMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 300, -screenH+200);  
		}else{
			imageMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 300, -screenH/3*2);  
		}
		imageMenu.update();  
	}

	class  seekBarListener implements OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			float bright = brightBar.getProgress()*10;
			float chrom = chromBar.getProgress()*10;
			float constrast = constrastbar.getProgress()*10;
			String videoSourceToken = camera.getProfiles().get(channelFlag).getVideoSourceToken();
			boolean ret = onvifMgr.setImagingSetting(username, password, imageService, videoSourceToken, bright, chrom, constrast);
			if(ret)
				System.out.println("=================image setting  sucess！！！");
		}
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	}

	//	class ImageMenuOnClickListener implements OnClickListener{
	//		@Override
	//		public void onClick(View v) {
	//			if(v == imageSaveBtn){
	//				float bright = brightBar.getProgress()*10;
	//				float chrom = chromBar.getProgress()*10;
	//				float constrast = constrastbar.getProgress()*10;
	//				String videoSourceToken = camera.getProfiles().get(channelFlag).getVideoSourceToken();
	//				boolean ret = onvifMgr.setImagingSetting(username, password, imageService, videoSourceToken, bright, chrom, constrast);
	//				if(ret)
	//					Toast.makeText(CameraHomeActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
	//					imageMenu.dismiss();
	//			}
	//		}
	//	}

	/*检测U盘安装情况*/
	public void checkUDisk(){
		Toast toast;
		String status=Environment.getExternalStorageState();
		if(status.equals(Environment.MEDIA_MOUNTED)){
			System.out.println("check U Disk media mounted");
		}else{
			System.out.println("check u Disk no media mounted");
		}
	}

	/*获取当前时间*/
	public String getCurrentTime(){
		SimpleDateFormat   formatter   =   new   SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");     
		Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间     
		String   str   =   formatter.format(curDate);
		return str;
	}

	/*悬浮框*/
	/**
	 * show notice
	 * 
	 */
	private void CreatNoticeView() {
		view = LayoutInflater.from(this).inflate(R.layout.noticedialog, null);
		iconImage = (ImageView) view.findViewById(R.id.notice_iv);
		tvNotice = (TextView) view.findViewById(R.id.tv_notice);
		tvNotice.setText("正在录制 ");
		tvNotice.setBackgroundColor(R.color.black);
		tvNoticeTime = (TextView) view.findViewById(R.id.tv_notice_time);
		tvNoticeTime.setText("21:00");
		tvNoticeTime.setBackgroundColor(R.color.black);
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		/*
		 * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
		 * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
		 * PixelFormat.TRANSPARENT：悬浮窗透明
		 */
		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
		Display mDisplay=windowManager.getDefaultDisplay();

		layoutParams.x = (int) (mDisplay.getWidth()*0.4);

		layoutParams.y = (int) (-mDisplay.getHeight()*0.6);
	}

	public void timerTask() {
		//创建定时线程执行更新任务
		tvNoticeTime.setText("00:00");
		mTimer = new Timer();
		timeCtn = 0;
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage( timeCountFlag);// 向Handler发送消息
				timeCtn++;
			}
		}, 1000, 1000);// 定时任务
	}
	/**
	 * 刷新或显示悬浮窗
	 */
	private void refreshorShowView() {
		iconImage.startAnimation(animation);
		if (viewAdded) {
			windowManager.updateViewLayout(view, layoutParams);
		} else {
			windowManager.addView(view, layoutParams);
			viewAdded = true;
		}
		startTime = System.currentTimeMillis();
		timerTask();
	}

	/**
	 * 关闭悬浮窗
	 */
	public void removeView() {
		if (viewAdded) {
			windowManager.removeView(view);
			viewAdded = false;
			animation.cancel();
			tvNotice.setText(" 正在录制 ");
			mTimer.cancel();
		}
	}

	/*初始化channel弹出框*/
	public void initPhotoingMenu(){
		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.float_photoing_menu, null,false);
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			photoingMenu = new PopupWindow(layout, srceenW -1200, 130, true);
		}else{
			photoingMenu = new PopupWindow(layout, srceenW -900, 150, true);
		}
		photoingMenu.setFocusable(true);
		photoingMenu.setBackgroundDrawable(new BitmapDrawable());
		photoingMenu.setAnimationStyle(android.R.style.Animation_Dialog); 
		floatImage= (ImageView) layout.findViewById(R.id.floatPhotoView);

		//		photoImageButton.setOnClickListener(new OnClickListener(){
		//			@Override
		//			public void onClick(View v) {
		//				System.out.println("photoImageButton onClick=======");
		//			}
		//		});
	}
	//显示照相悬浮矿
	private void showPhotoingMenu(){
		Bitmap bitmap = getLoacalBitmap(photoingImagePath); //从本地取图片
		floatImage.setImageBitmap(bitmap);
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			photoingMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 900, -screenH);  
		}else{
			photoingMenu.showAsDropDown(this.findViewById(R.id.homeVideoView), 900, -screenH );  
		}
		photoingMenu.setFocusable(true);
		photoingMenu.update();  
		new Handler().postDelayed(new Runnable(){  
			public void run() {  
				photoingMenu.dismiss();
			}  
		}, 1*1000); 
	}

	/**
	 * 加载本地图片
	 * http://bbs.3gstdy.com
	 * @param url
	 * @return
	 */
	public  Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
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

	//		/*menu on key down listener */
	//		class menuOnKeyDownListener implements OnKeyDownListener{
	//			
	//		}
}
