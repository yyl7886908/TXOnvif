package com.taixin.android.onvif.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.data.OrderRecordData;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.service.OnvifService;
import com.taixin.ffmpeg.player.ITxRecorder;
import com.taixin.ffmpeg.player.TXFFMpegRecorder;

public class orderRecordActivity extends Activity{

	private String tag = "orderRecordActivity";
	private TimePicker startTimeP;
	private TimePicker endTimeP;
	private CheckBox cb1,cb2,cb3,cb4,cb5,cb6,cb7;
	private IOnvifManager onvifMgr;
	private ITxRecorder recorder;
	private Button OKButton;
	private int position;/*对应的摄像头的索引*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ordering_record);
		onvifMgr = OnvifManager.getInstance();
		recorder = new TXFFMpegRecorder();
		Bundle extras = getIntent().getExtras(); 
		position = extras.getInt("grid_item_position");
		displayView();
	}
	
	private void displayView(){
		startTimeP = (TimePicker) this.findViewById(R.id.startTimePicker);
		endTimeP = (TimePicker) this.findViewById(R.id.endTimePicker);
		startTimeP.setIs24HourView(true);
		endTimeP.setIs24HourView(true);
		cb1 = (CheckBox) this.findViewById(R.id.zhouyicb);
		cb2 = (CheckBox) this.findViewById(R.id.zhouercb);
		cb3 = (CheckBox) this.findViewById(R.id.zhousancb);
		cb4 = (CheckBox) this.findViewById(R.id.zhousicb);
		cb5 = (CheckBox) this.findViewById(R.id.zhouwucb);
		cb6 = (CheckBox) this.findViewById(R.id.zhouliucb);
		cb7 = (CheckBox) this.findViewById(R.id.zhouricb);
		OKButton = (Button) this.findViewById(R.id.order_record_mode_save_button);
		OKButton.setOnClickListener(new myOnClickListener());
		setRecordDataToUI();
	}
	/*获取本地存储的定时录制数据，显示到UI*/
	private void setRecordDataToUI(){
		OrderRecordData data = onvifMgr.getOrderRecordDataByIndex(position);
		if(data == null)
			return;
		Log.i(tag, "local record start time ===="+data.getStartTime());
		Log.i(tag, "local record end time ===="+data.getEndTime());
		Log.i(tag, "local record UUiD ===="+data.getUuid());
		String startT[] = data.getStartTime().split(":");
		String endT[] = data.getEndTime().split(":");
		startTimeP.setCurrentHour(Integer.parseInt(startT[0]));
		startTimeP.setCurrentMinute(Integer.parseInt(startT[1]));
		endTimeP.setCurrentHour(Integer.parseInt(endT[0]));
		endTimeP.setCurrentMinute(Integer.parseInt(endT[1]));
		if(data.isZhouyiCheck())
			cb1.setChecked(true);
		if(data.isZhouerCheck())
			cb2.setChecked(true);
		if(data.isZhousanCheck())
			cb3.setChecked(true);
		if(data.isZhousiCheck())
			cb4.setChecked(true);
		if(data.isZhouwuCheck())
			cb5.setChecked(true);
		if(data.isZhouliuCheck())
			cb6.setChecked(true);
		if(data.isZhouqiCheck())
			cb7.setChecked(true);
	}
	
	/*判断是否选择日期*/
	private boolean checkWeekDay(){
		if(cb1.isChecked() ||
				cb2.isChecked() ||
				cb3.isChecked() ||
				cb4.isChecked() ||
				cb5.isChecked() ||
				cb6.isChecked() ||
				cb7.isChecked()){
			return true;
		}
		return false;
	}
	/*获取timepicker时间转字符串00:00*/
	private String getTimerToString(TimePicker t){
		int h = t.getCurrentHour();
		int m = t.getCurrentMinute();
		String time = h+":"+m;
		return time;
	}
	
	/*检查开始时间是否小于结束时间*/
	private boolean compareTime(String startTime, String endTime){
		java.util.Date nowdate=new java.util.Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
		Date d1;
		try {
			d1 = sdf.parse(startTime);
			Date d2 = sdf.parse(endTime);
			boolean flag = d1.before(d2);
			if(flag)
				return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	/*获取时间差*/
	private int getTimeDiff(String startTime, String endTime){
		SimpleDateFormat dfs = new SimpleDateFormat("HH:mm");
		long between = 0;
		try {
			java.util.Date begin = dfs.parse(startTime);
			java.util.Date end = dfs.parse(endTime);
			between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
			System.out.println("两者相差  "+between+" 秒");
			int totalS = new Long(between / 1000).intValue();
			return totalS;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}
	
	/*开始时间，结束时间存储数据*/
	private boolean storagingRecordInfo(String startTime, String endTime){
		LocalCamera preCamera = onvifMgr.getPreviousCameraInfo();
		OrderRecordData rData = new OrderRecordData();
		rData.setItemIndex(position);
		if(preCamera == null){
			Toast toast = Toast.makeText(getApplicationContext(), "获取上一次连接设备信息失败，请先连接摄像头", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		rData.setUuid(preCamera.getUuid());
		rData.setStartTime(startTime);
		rData.setEndTime(endTime);
		int duration = getTimeDiff(startTime, endTime);
		rData.setDuration(duration);
		if(cb1.isChecked())
			rData.setZhouyiCheck(true);
		if(cb2.isChecked())
			rData.setZhouerCheck(true);
		if(cb3.isChecked())
			rData.setZhousanCheck(true);
		if(cb4.isChecked())
			rData.setZhousiCheck(true);
		if(cb5.isChecked())
			rData.setZhouwuCheck(true);
		if(cb6.isChecked())
			rData.setZhouliuCheck(true);
		if(cb7.isChecked())
			rData.setZhouqiCheck(true);
		if(!onvifMgr.saveOrderRecordDataByIndex(rData)){
			Toast toast = Toast.makeText(getApplicationContext(), "存储失败", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}
		return true;
	}
	class myOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Button b = (Button )v;
			if(b == OKButton){
				if(!checkWeekDay()){
					Toast toast = Toast.makeText(getApplicationContext(), "请选择日期", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				/*保存定时录制数据*/
				String startTime = getTimerToString(startTimeP);
				String endTime = getTimerToString(endTimeP);
				Log.i(tag, "start time = "+startTime);
				Log.i(tag, "end time = "+endTime);
				if(!compareTime(startTime, endTime)){
					Toast toast = Toast.makeText(getApplicationContext(), "开始时间大于结束时间，请重新设置", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				/*存储数据*/
				storagingRecordInfo(startTime, endTime);
				Toast toast = Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				startRecordService();
			}
		}
	}
	
	/*此处启动service*/
	public void startRecordService(){
		Intent intent = new Intent(this, OnvifService.class);
		startService(intent);
	}
}
