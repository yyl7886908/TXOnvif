package com.taixin.android.onvif.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.util.FileCreater;
import com.taixin.android.onvif.app.util.Usb;
import com.taixin.android.onvif.sdk.obj.ImagingSetting;
import com.taixin.ffmpeg.player.ITxRecorder;
import com.taixin.ffmpeg.player.TXFFMpegRecorder;
import com.taixin.ffmpeg.widget.VideoView;

public class CameraActivity extends Activity{
	private String tag = "CameraActivity";
	/*当前设备在current设备中的位置*/
	private int position;
	/*当前设备的信息*/
	private CameraData camera;
	private VideoView mVideoView;
	private String mVideoPath;
	private IOnvifManager onvifMgr;
	private String username;
	private String password;
	private String deviceService;
	private String imageService;
	private String ptzService;
	private String mediaService;
	private String profileToken;
	/*控制菜单, 频道切换菜单*/
	private PopupWindow menu, channelMenu, imageMenu, fileMenu, recordMenu, photoingMenu;
	private LayoutInflater inflater;	
	private View layout;
	private ImageButton channelSwitchBtn, imageSetBtn, ptzBtn, photoBtn, recordingBtn, fileBtn, recordFlagBtn;
	private ImageButton highBtn, middleBtn, lowBtn;
	private ImageButton picBtn, videoBtn;
	private SeekBar chromBar, brightBar, constrastbar;
	private Button imageSaveBtn;
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
	private ITxRecorder recorder;
	private View view;// 透明窗体
	private long startTime;
	private boolean viewAdded = false;// 透明窗体是否已经显示
	private WindowManager windowManager;
	private WindowManager.LayoutParams layoutParams;
	/** 录制闪烁动画 */
	private Animation animation;
	private ImageView iconImage;
	private TextView tvNotice;
	private boolean is_pvr_pause=false;
	
	/*照片悬浮狂*/
	private boolean isAdded = false; // 是否已增加悬浮窗
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	private Button btn_floatView;
	private ImageButton photoImageButton;
	private String photoingImagePath;
	private ImageView floatImage;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		//setTheme(R.style.Camera_Show_Style);
		setContentView(R.layout.camera);
		initData();
		initMenu();
		CreatNoticeView();
		animation = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.pvr_notice);
		recorder = new TXFFMpegRecorder();
	}

	public void initData(){
		onvifMgr = OnvifManager.getInstance();
		mVideoView = (VideoView) findViewById(R.id.video_view);
		Bundle extras = getIntent().getExtras(); 
		position = extras.getInt("grid_item_position");
		onvifMgr.play(position, mVideoView);
		getDeviceInfo();
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
			if(getCapa){
				//Toast.makeText(this, "获取能力成功", Toast.LENGTH_SHORT).show();;
				Log.i(tag, "获取capability成功");
			}
		}
		mediaService = camera.getCapability().getMediaService();
		ptzService = camera.getCapability().getPtzService();
		imageService = camera.getCapability().getImagingService();
		boolean getProf = onvifMgr.getMediaProfiles(username, password, mediaService);
		if(getProf){
			Log.i(tag, "获取profiles成功");
		}
		profileToken = camera.getProfiles().get(channelFlag).getToken();
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(username == null || password == null || deviceService == null || ptzService == null || profileToken == null){
			initData();
		}
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
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public void initMenu(){
		initCtrlMenu();
		initFileMenu();
		initChannelMenu();
		initImageMenu();
		initRecordMenu();
		initPhotoingMenu();
	}

	/*初始化控制菜单*/
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
		ptzBtn = (ImageButton) layout.findViewById(R.id.ptz_button);
		ptzBtn.setOnClickListener(listener);
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
			}else if(v == ptzBtn){
				if(cruise){
					//onvifMgr.ptzCruise(username, password, ptzService, profileToken, cruise);
					cruise = false;
				}else{
					//onvifMgr.ptzCruise(username, password, ptzService, profileToken,cruise);
					cruise = true;
				}
			}else if(v == photoBtn){
				List<String> list = Usb.getUsbDirList();
				if(list.size()<=0){
					Toast toast = Toast.makeText(CameraActivity.this, "请先插入U盘", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				String filename = list.get(0)+"/CameraRecordImages/"+getCurrentTime()+".jpg";
				System.out.println("java photo file name = "+filename);
				mVideoView.photoImage(filename);
				photoingImagePath = filename;
				Toast toast = Toast.makeText(CameraActivity.this, "拍照成功", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				new Handler().postDelayed(new Runnable(){  
				     public void run() {  
				    	 showPhotoingMenu();
				     }  
				  }, 2*1000); 
				
			}else if(v == recordingBtn){
				if(!recordingFlag){
					List<String> list = Usb.getUsbDirList();
					if(list.size()<=0){
						Toast toast = Toast.makeText(CameraActivity.this, "请先插入U盘", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
					String filename = list.get(0)+"/CameraRecordVideos/"+getCurrentTime()+".avi";
					System.out.println("java recording file name = "+filename);
					FileCreater.createFile(filename);
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
				}else{
					mVideoView.stopRecordingRtspStream();
					recordingFlag = false;
				}
				
			}
		}
	/*初始化file弹出框*/
	public void initFileMenu(){
		inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.file_ctrl_menu, null,false);
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
		picBtn = (ImageButton) layout.findViewById(R.id.pic_button);
		videoBtn = (ImageButton) layout.findViewById(R.id.video_button);
		fileMenuOnClickListener listener = new fileMenuOnClickListener();
		picBtn.setOnClickListener(listener);
		videoBtn.setOnClickListener(listener);
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
	}
	//显示控制菜单
	private void showCtrlMenu(){
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			menu.showAsDropDown(this.findViewById(R.id.video_view), 100, -screenH/4);  
		}else{
			menu.showAsDropDown(this.findViewById(R.id.video_view), 100, -screenH/4);  
		}
		menu.update();  
	}
	/*控制菜单按钮的点击事件*/
	class fileMenuOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == picBtn){
				Intent intent = new Intent(CameraActivity.this, CameraImagesGridActivity.class);
				CameraActivity.this.startActivity(intent);
			}else if(v == videoBtn){
				Intent intent = new Intent(CameraActivity.this, CameraVideosListActivity.class);
				CameraActivity.this.startActivity(intent);
			}
		}
	}
	/*控制菜单按钮的点击事件*/
	class channelMenuOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == highBtn){
				Toast.makeText(CameraActivity.this, "高清", Toast.LENGTH_SHORT).show();
				switchChannelById(0);
				channelSwitchBtn.setImageDrawable(getResources().getDrawable(R.drawable.high_stream_selector));
			}else if(v == middleBtn){
				Toast.makeText(CameraActivity.this, "均衡", Toast.LENGTH_SHORT).show();
				switchChannelById(1);
				channelSwitchBtn.setImageDrawable(getResources().getDrawable(R.drawable.middle_stream_selector));
			}else if(v ==lowBtn){
				Toast.makeText(CameraActivity.this, "流畅", Toast.LENGTH_SHORT).show();
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
		mVideoView.setVideoPath(authUri);
		mVideoView.requestFocus();
		mVideoView.start();	
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
			channelMenu.showAsDropDown(this.findViewById(R.id.video_view), 300, -screenH/2);    
		}else{
			channelMenu.showAsDropDown(this.findViewById(R.id.video_view), 300, -screenH/2);   
		}
		
		channelMenu.update(); 
	}
	
	private void showFileMenu(){
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		if(srceenW > 1800 && screenH > 1000){
			fileMenu.showAsDropDown(this.findViewById(R.id.video_view), 300, -screenH/2);    
		}else{
			fileMenu.showAsDropDown(this.findViewById(R.id.video_view), 300, -screenH/2);   
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
		imageSaveBtn= (Button) layout.findViewById(R.id.imgae_save_btn);
		ImageMenuOnClickListener listener = new ImageMenuOnClickListener();
		imageSaveBtn.setOnClickListener(listener);
	}
	//显示控制菜单
	private void showImageMenu(){
		int srceenW =  this.getWindowManager().getDefaultDisplay().getWidth(); 
		int screenH = this.getWindowManager().getDefaultDisplay().getHeight();
		imageMenu.showAsDropDown(this.findViewById(R.id.video_view), 300, -screenH/3*2);  
		imageMenu.update();  
	}

	class ImageMenuOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == imageSaveBtn){
				float bright = brightBar.getProgress()*10;
				float chrom = chromBar.getProgress()*10;
				float constrast = constrastbar.getProgress()*10;
				String videoSourceToken = camera.getProfiles().get(channelFlag).getVideoSourceToken();
				boolean ret = onvifMgr.setImagingSetting(username, password, imageService, videoSourceToken, bright, chrom, constrast);
				if(ret)
					Toast.makeText(CameraActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

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
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		/*
		 * LayoutParams.TYPE_SYSTEM_ERROR：保证该悬浮窗所有View的最上层
		 * LayoutParams.FLAG_NOT_FOCUSABLE:该浮动窗不会获得焦点，但可以获得拖动
		 * PixelFormat.TRANSPARENT：悬浮窗透明
		 */
		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
//		 layoutParams.gravity = Gravity.RIGHT|Gravity.BOTTOM; //悬浮窗开始在右下角显示
//		 layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
//		 layoutParams.verticalMargin=80;
		Display mDisplay=windowManager.getDefaultDisplay();
//		 layoutParams.horizontalMargin=80;//
		layoutParams.x = (int) (mDisplay.getWidth()*0.4);
		
		layoutParams.y = (int) (-mDisplay.getHeight()*0.6);
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
		}
	}
	
	/*拍照特效*/
	/**
	 * 创建悬浮窗
	 */
	private void createFloatView(String filename) {
		btn_floatView = new Button(getApplicationContext());
		btn_floatView.setText("悬浮窗");
		photoImageButton = new ImageButton(getApplicationContext());
		ImageView iv = new ImageView(getApplicationContext());
		//photoImageButton.setBackgroundColor(R.color.blue);
		Bitmap bitmap = getLoacalBitmap("/storage/external_storage/sda/CameraRecordImages/2014_12_24_09_02_26.jpg"); //从本地取图片
		iv .setImageBitmap(bitmap);
		photoImageButton.setImageBitmap(bitmap);
		//photoImageButton.setImageURI(Uri.parse(filename));
		System.out.println("filename = "+filename);
        wm = (WindowManager) getApplicationContext()
        	.getSystemService(Context.WINDOW_SERVICE);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        Display mDisplay=wm.getDefaultDisplay();
        params.x = (int) (mDisplay.getWidth()*0.8);
        params.y = (int) (-mDisplay.getHeight()*3);
        // 设置window type
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置悬浮窗的长得宽
        params.width = 200;
        params.height = 100;
        wm.addView(iv, params);
        isAdded = true;
        photoImageButton.setFocusable(true);
        photoImageButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				System.out.println("photoImageButton  clicked===");
			}
        	
        });
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
				photoingMenu.showAsDropDown(this.findViewById(R.id.video_view), 900, -screenH);  
			}else{
				photoingMenu.showAsDropDown(this.findViewById(R.id.video_view), 900, -screenH );  
			}
			photoingMenu.setFocusable(true);
			photoingMenu.update();  
			 new Handler().postDelayed(new Runnable(){  
			     public void run() {  
			    	 photoingMenu.dismiss();
			     }  
			  }, 2*1000); 
		}
}
