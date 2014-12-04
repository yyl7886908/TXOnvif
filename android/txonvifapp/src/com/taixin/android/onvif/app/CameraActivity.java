package com.taixin.android.onvif.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.util.Usb;
import com.taixin.android.onvif.sdk.obj.ImagingSetting;
import com.taixin.ffmpeg.widget.VideoView;

public class CameraActivity extends Activity{
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
	private PopupWindow menu, channelMenu, imageMenu, fileMenu;
	private LayoutInflater inflater;	
	private View layout;
	private ImageButton channelSwitchBtn, imageSetBtn, ptzBtn, photoBtn, recordingBtn, fileBtn;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		//setTheme(R.style.Camera_Show_Style);
		setContentView(R.layout.camera);
		initData();
		initMenu();
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
				Toast.makeText(this, "获取能力成功", Toast.LENGTH_SHORT).show();;
			}
		}
		mediaService = camera.getCapability().getMediaService();
		ptzService = camera.getCapability().getPtzService();
		imageService = camera.getCapability().getImagingService();
		boolean getProf = onvifMgr.getMediaProfiles(username, password, mediaService);
		if(getProf){
			Toast.makeText(this, "获取profiles成功", Toast.LENGTH_SHORT).show();;
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
					AUTHUri=onvifMgr.getAuthUriByPosition(position);
					mVideoView.startRecordingRtspStream(AUTHUri, filename, 0);
					recordingFlag = true;
				}else{
					mVideoView.stopRecordingRtspStream();
					recordingFlag = false;
				}
				
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
}
