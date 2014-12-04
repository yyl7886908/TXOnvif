package com.taixin.android.onvif.app.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taixin.android.onvif.app.R;
import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.data.OrderRecordModel;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.util.Usb;
import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;
import com.taixin.ffmpeg.player.ITxRecorder;
import com.taixin.ffmpeg.player.TXFFMpegRecorder;

public class OnvifService extends Service {
	/**
	 * 流程
	 * 1、读取配置文件里面的预约录制的数据（）
	 * 2、数据过时清理数据，数据没过时开始计算时间
	 * 3、时间到了开始对摄像头发起请求，开始录制
	 */
	private IOnvifManager onvifMgr;
	private TimerTask task;
	private Handler handler;
	private final Timer timer = new Timer(); 
	private final static int Time_Is_Reaching = 1;/*录制的时间到了*/
	private final static int Time_Now = 2; /*当前时间*/
	private ITxRecorder recorder;
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
	private boolean is_pvr_pause=false;
 
	@Override
	public void onCreate() {
		super.onCreate();
		onvifMgr = OnvifManager.getInstance();
		recorder = new TXFFMpegRecorder();
		startRecording();
		startTask();
		timer.schedule(task, 0, 60*1000);   
		handler = new Handler(){
			@Override  
			public void handleMessage(Message msg) {  
				switch(msg.what)
				{
				case Time_Is_Reaching:
					/*时间到，开始获取摄像头的信息开始录制*/
					Toast toast2 = Toast.makeText(getApplicationContext(), "时间到!!!开始录制", Toast.LENGTH_LONG);
					toast2.setGravity(Gravity.CENTER, 0, 0);
					toast2.show();
					break;
				}
				super.handleMessage(msg);  
			}   
		};
		
		animation = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.pvr_notice);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}

	/*每一分钟检测一次，如果开始时间与现在时间相等，则开始录制*/
	public void startTask(){
		task = new TimerTask() {  
			@Override  
			public void run() {  
				getOrderTime();
			}  
		};  
	}

	public boolean getOrderTime(){
		OrderRecordModel model = onvifMgr.getOrderedRecordModel();
		if(model == null)
			return false;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");       
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间  
			final String curTime = formatter.format(curDate);
			handler.post(new Runnable(){
				@Override
				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(), "当前时间是："+curTime, Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			});
			if(curTime.equals(model.getStartTime())){
				handler.sendEmptyMessage(Time_Is_Reaching);	
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*获取要定时录制的摄像头信息, 开始录制*/
	public boolean startRecording(){
		OrderRecordModel model = onvifMgr.getOrderedRecordModel();
		System.out.println("startRecording model =="+model.getUuid());
		LocalCamera lCamera = onvifMgr.getLocalCameraByUUidForRecord(model.getUuid());
		String username = "";
		String password = "";
		String deviceService = "";
		String mediaService = "" ;
		if(lCamera != null){
			username = lCamera.getUsername();
			password = lCamera.getPassword();
		}else{
			System.out.println("没有找到用户名和密码");
			return false;
		}
		System.out.println("startRecording username =="+username);
		System.out.println("startRecording password =="+password);
		if(lCamera == null)
			return false;
		System.out.println("before discover=====");
		ArrayList<Device> devices = onvifMgr.discoverDevices();
		System.out.println("after discover=====");
		if(devices.size()<=0 || devices == null){
			System.out.println("没有搜到一个设备！");
			Toast toast = Toast.makeText(getApplicationContext(), "没有搜到要任何一个设备", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}

		for(Device d : devices){
			if(d.getUuid().equals(model.getUuid())){
				deviceService = d.getDeviceService();
			}
		}
		if(deviceService == "" || deviceService.length() <=0){
			System.out.println("没有搜到要录制的设备！");
			Toast toast = Toast.makeText(getApplicationContext(), "没有搜到要录制的设备", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		ArrayList<MediaStreamUri> uris = onvifMgr.getMediaStreamUriForRecord(username, password, deviceService);
		for(MediaStreamUri uri : uris){
			System.out.println("=====stream uri = "+uri.getStreamURI());
		}
		String authUri = this.getAuthUri(username, password, uris.get(0).getStreamURI());
		this.startLocalRecording(authUri, model.getDuration());
		return true;
	}

	/*username uri password 拼接,得到可以认证的RTSP视频流地址*/
	public String getAuthUri(String username, String password, String uri){
		//String authUri = "";
		String uris[] = uri.split("//");
		String authUri = uris[0]+"//"+username+":"+password+"@"+uris[1];
		return authUri;
	}

	/*录制*/
	public boolean startLocalRecording(String authuri, int time){
		List<String> list = Usb.getUsbDirList();
		if(list.size()<=0){
			Toast toast = Toast.makeText(this, "请先插入U盘", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		String filename = list.get(0)+"/CameraRecordVideos/"+getCurrentTime()+".avi";
		recorder.startRecoderRTSPStream(authuri, filename, time);
		return true;
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
		
		layoutParams.y = (int) (-mDisplay.getHeight()*0.5);
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
			tvNotice.setText(" 正在录制 00:00:00");
		}
	}
}
