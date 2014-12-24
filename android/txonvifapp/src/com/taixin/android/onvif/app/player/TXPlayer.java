package com.taixin.android.onvif.app.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
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
//		 layoutParams.horizontalMargin=80;//
        params.x = (int) (mDisplay.getWidth()*0.8);
		
        params.y = (int) (-mDisplay.getHeight()*3);
        // 设置window type
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    
        params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        
        // 设置悬浮窗的长得宽
        params.width = 200;
        params.height = 100;
        
        wm.addView(playBackFlag, params);
        isAdded = true;
	}
	
}
