package com.taixin.android.onvif.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.data.OrderRecordModel;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.service.OnvifService;
import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;
import com.taixin.ffmpeg.player.ITxRecorder;
import com.taixin.ffmpeg.player.TXFFMpegRecorder;

public class OrderRecordActivity extends Activity {

	private EditText etStartTime; 
	private EditText etEndTime; 
	private Button saveButton;
	private CheckBox orderRecordByDayCb;
	private IOnvifManager onvifMgr;
	private ITxRecorder recorder;
	@Override
	protected void onCreate(Bundle bundle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(bundle);
		setContentView(R.layout.order_record);
		onvifMgr = OnvifManager.getInstance();
		recorder = new TXFFMpegRecorder();
		displayView();
	}

	private void displayView(){
		etStartTime = (EditText) this.findViewById(R.id.order_record_day_start_time); 
		etEndTime = (EditText) this.findViewById(R.id.order_record_day_stop_time); 
		orderRecordByDayCb = (CheckBox) this.findViewById(R.id.order_record_day_mode_cb); 
		saveButton = (Button) this.findViewById(R.id.order_record_mode_save_button);
		EtOnClickLisener lisener = new EtOnClickLisener();
		etStartTime.setOnClickListener(lisener);
		etEndTime.setOnClickListener(lisener);
		BtnOnClickLisener btnListener = new BtnOnClickLisener();
		saveButton.setOnClickListener(btnListener);
		OrderRecordModel model = onvifMgr.getOrderedRecordModel();
		if(model != null){
			orderRecordByDayCb.setChecked(true);
			etStartTime.setText(model.getStartTime());
			etEndTime.setText(model.getEndTime());
		}
	}

	@SuppressLint("NewApi")
	class EtOnClickLisener implements OnClickListener{

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(OrderRecordActivity.this); 
			View view = View.inflate(OrderRecordActivity.this, R.layout.day_time_dialog, null); 
			final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker); 
			builder.setView(view); 

			Calendar cal = Calendar.getInstance(); 
			cal.setTimeInMillis(System.currentTimeMillis()); 
			timePicker.setIs24HourView(true); 
			timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY)); 
			timePicker.setCurrentMinute(Calendar.MINUTE); 
			if(v == etStartTime){
				final int inType = etStartTime.getInputType(); 
				etStartTime.setInputType(InputType.TYPE_NULL); 
				etStartTime.setInputType(inType); 
				etStartTime.setSelection(etStartTime.getText().length()); 
				builder.setTitle("选取起始时间"); 
				builder.setOnDismissListener(new OnDismissListener(){
					@Override
					public void onDismiss(DialogInterface dialog) {
						System.out.println("==========onDismiss");
						StringBuffer sb = new StringBuffer(); 
						sb.append(timePicker.getCurrentHour()) 
						.append(":").append(timePicker.getCurrentMinute()); 
						if(orderRecordByDayCb.isChecked())
							etStartTime.setText(sb);
					}
				});

			}else if(v == etEndTime){
				int inType = etEndTime.getInputType(); 
				etEndTime.setInputType(InputType.TYPE_NULL); 
				etEndTime.setInputType(inType); 
				etEndTime.setSelection(etEndTime.getText().length()); 
				builder.setTitle("选取结束时间"); 
				builder.setOnDismissListener(new OnDismissListener(){
					@Override
					public void onDismiss(DialogInterface dialog) {
						System.out.println("==========onDismiss");
						StringBuffer sb = new StringBuffer(); 
						sb.append(timePicker.getCurrentHour()) 
						.append(":").append(timePicker.getCurrentMinute()); 
						if(orderRecordByDayCb.isChecked())
							etEndTime.setText(sb);
					}
				});
			}
			Dialog dialog = builder.create(); 
			dialog.show(); 
		}
	}

	class BtnOnClickLisener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == saveButton){
				if(orderRecordByDayCb.isChecked()){
					if(etStartTime.getText().toString().equals("") || etStartTime.getText().toString().length()<=0){
						Toast toast = Toast.makeText(getApplicationContext(), "请先设置开始时间！", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
					if(etEndTime.getText().equals("") || etEndTime.getText().length()<=0){
						Toast toast = Toast.makeText(getApplicationContext(), "请先设置开始时间！", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
					if(!compareTime(etStartTime.getText().toString(), etEndTime.getText().toString())){
						Toast toast = Toast.makeText(getApplicationContext(), "开始时间大于结束时间，请重新设置！", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						return;
					}
					/*获取上一次连接的摄像头数据，存储开始时间和结束时间*/
					LocalCamera preCamera = onvifMgr.getPreviousCameraInfo();
					OrderRecordModel rMode = new OrderRecordModel();
					if(preCamera == null){
						Toast toast = Toast.makeText(getApplicationContext(), "获取上一次连接设备信息失败，请先连接摄像头", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}else{
						rMode.setUuid(preCamera.getUuid());
						rMode.setStartTime(etStartTime.getText().toString());
						rMode.setEndTime(etEndTime.getText().toString());
						int duration = getTimeDiff(etStartTime.getText().toString(), etEndTime.getText().toString());
						rMode.setDuration(duration);
						if(onvifMgr.saveOrderRecordModel(rMode)){
							Toast toast = Toast.makeText(getApplicationContext(), "预约成功！", Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							//startRecordService();
							//startRecording();
						}else{
							Toast toast = Toast.makeText(getApplicationContext(), "预约失败！", Toast.LENGTH_LONG);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
				}
			}
		}

	}

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

	/*此处启动service*/
	public void startRecordService(){
		Intent intent = new Intent(this, OnvifService.class);
		startService(intent);
	}
}
