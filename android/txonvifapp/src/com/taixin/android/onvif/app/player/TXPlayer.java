package com.taixin.android.onvif.app.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.taixin.android.onvif.app.R;
import com.taixin.ffmpeg.widget.MediaController;
import com.taixin.ffmpeg.widget.VideoView;

public class TXPlayer extends Activity {
	private String tag = "TXPlayer play recorder";
	private VideoView videoView;
	private String videoPath;
	private MediaController mMediaController;

	/*悬浮狂*/
	private boolean isAdded = false; // 是否已增加悬浮窗
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	private Button btn_floatView;
	private TextView playBackFlag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式 
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏 
		super.onCreate(savedInstanceState);
		setTheme(R.style.Home_Setting_Style);
		setContentView(R.layout.txplayer); 
		createFloatView();
		videoView = (VideoView) findViewById(R.id.tx_video_view);
		if(getIntent().getExtras().getString("videopath")!=null){
			videoPath=getIntent().getExtras().getString("videopath");
		}else{
			Toast.makeText(this, "文件格式不支持！", Toast.LENGTH_LONG).show();
		}
		mMediaController = new MediaController(this);
		videoView.setMediaController(mMediaController);
		videoView.setVideoURI(Uri.parse(videoPath)); 
		videoView.start(); 
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if( isAdded){
				wm.removeView(playBackFlag);
				isAdded = false;
			}
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
			Log.i(tag, "left button is clicked===");
			mMediaController.fastBack();
			return false;  
		}  else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
			Log.i(tag, "right button is clicked===");
			mMediaController.fastForwad();
			return false;  
		}  else if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
			Log.i(tag, "center button is clicked===");
			mMediaController.doPauseResume();
			return false;  
		}  
		return super.onKeyDown(keyCode, event);
	}
	


	/**
	 * 创建悬浮窗
	 */
	private void createFloatView() {
		btn_floatView = new Button(getApplicationContext());
		btn_floatView.setText("悬浮窗");
		playBackFlag = new TextView(getApplicationContext());
		playBackFlag.setText("录像");
		playBackFlag.setTextColor(R.color.red);
		playBackFlag.setTextSize(40);
		wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, LayoutParams.TYPE_SYSTEM_ERROR,
				LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
		Display mDisplay=wm.getDefaultDisplay();
		params.x = (int) (mDisplay.getWidth()*0.8);
		params.y = (int) (-mDisplay.getHeight()*3);
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		params.width = 200;
		params.height = 100;
		wm.addView(playBackFlag, params);
		isAdded = true;
	}

}
