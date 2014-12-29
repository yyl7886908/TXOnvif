package com.taixin.android.onvif.app.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taixin.android.onvif.app.R;
import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.data.OrderRecordData;
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
	private final static int Start_Time_Is_Reaching = 1;/*录制的时间到了*/
	private final static int Stop_Time_Is_Reaching = 2;/*录制的时间到了*/
	//private final static int Time_Now = 2; /*当前时间*/
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
	private boolean isRecording;
 
	@Override
	public void onCreate() {
		super.onCreate();
		onvifMgr = OnvifManager.getInstance();
		recorder = new TXFFMpegRecorder();
		
		startTask();
		timer.schedule(task, 0, 10*1000);   
		handler = new Handler(){
			@Override  
			public void handleMessage(Message msg) {  
				switch(msg.what)
				{
				case Start_Time_Is_Reaching:
					/*时间到，开始获取摄像头的信息开始录制*/
					Toast toast = Toast.makeText(getApplicationContext(), "开始定时录制", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					startRecording();
					break;
				case Stop_Time_Is_Reaching:
					/*时间到，开始获取摄像头的信息开始录制*/
					Toast toast1 = Toast.makeText(getApplicationContext(), "结束定时录制", Toast.LENGTH_LONG);
					toast1.setGravity(Gravity.CENTER, 0, 0);
					toast1.show();
					stopRecording();
					break;
				}
				
				super.handleMessage(msg);  
			}   
		};
		
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
				CheckOrderTime();
			}  
		};  
	}

	public boolean CheckOrderTime(){
		//OrderRecordModel model = onvifMgr.getOrderedRecordModel();
		OrderRecordData data = onvifMgr.getOrderedRecordData();
		if(data == null)
			return false;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");       
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间  
			final String curTime = formatter.format(curDate);
			System.out.println("current time =========="+curTime);
//			handler.post(new Runnable(){
//				@Override
//				public void run() {
////					Toast toast = Toast.makeText(getApplicationContext(), "当前时间是："+curTime, Toast.LENGTH_LONG);
////					toast.setGravity(Gravity.CENTER, 0, 0);
////					toast.show();
//				}
//			});
			
			
			if((curTime.equals(data.getStartTime()) || ( compareTime(data.getStartTime(), curTime) && compareTime(curTime, data.getEndTime()))) && checkCurrentTime(data)  && !isRecording){
				handler.sendEmptyMessage(Start_Time_Is_Reaching);	
				return true;
			}else if(isRecording && compareTime( data.getEndTime(),curTime)){
				handler.sendEmptyMessage(Stop_Time_Is_Reaching);	
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*查看当前时间周几是否需要录制*/
	private boolean checkCurrentTime(OrderRecordData data){
		int weekDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK);/*礼拜天是1，礼拜一是2...礼拜六是 7!*/
		if(weekDay == 2 && data.isZhouyiCheck())
			return true;
		else if(weekDay == 3 && data.isZhouerCheck())
			return true;
		else if(weekDay == 4 && data.isZhousanCheck())
			return true;
		else if(weekDay == 5 && data.isZhousiCheck())
			return true;
		else if(weekDay == 6 && data.isZhouwuCheck())
			return true;
		else if(weekDay == 7 && data.isZhouliuCheck())
			return true;
		else if(weekDay == 1 && data.isZhouqiCheck())
			return true;
		return false;
	}
	
	/*检查开始时间是否小于结束时间*/
	private boolean compareTime(String time1, String time2){
		java.util.Date nowdate=new java.util.Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
		try {
			Date d1  = sdf.parse(time1);
			Date d2 =sdf.parse(time2);
			boolean flag = d1.before(d2);
			if(flag)
				return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*获取要定时录制的摄像头信息, 开始录制*/
	public boolean startRecording(){
		OrderRecordData model = onvifMgr.getOrderedRecordData();
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
			Toast toast = Toast.makeText(getApplicationContext(), "定时录制没有找到用户名和密码", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
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
			Toast toast = Toast.makeText(getApplicationContext(), "定时录制没有搜到要任何一个设备", Toast.LENGTH_SHORT);
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
		String filename = list.get(0)+"/CameraRecordVideos/"+getCurrentTime()+".ts";
		recorder.startRecoderRTSPStream(authuri, filename, time);
		isRecording = true;
		return true;
	}

	/*获取当前时间*/
	public String getCurrentTime(){
		SimpleDateFormat   formatter   =   new   SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");     
		Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间     
		String   str   =   formatter.format(curDate);
		return str;
	}

	/*停止录制*/
	public void stopRecording(){
		recorder.stopRecordRTSPStream();
		isRecording = false;
	}
}
