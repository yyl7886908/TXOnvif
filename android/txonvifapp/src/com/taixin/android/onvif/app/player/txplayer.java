package com.taixin.android.onvif.app.player;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.taixin.android.onvif.app.R;

public class txplayer extends Activity {
	private String tag = "TXPlayer play recorder";
	private VideoView videoView;
	private String videoPath;
	private MediaController mMediaController;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式 
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏 
		super.onCreate(savedInstanceState);
		setTheme(R.style.Home_Setting_Style);
		setContentView(R.layout.sysplayer); 
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
	
}
