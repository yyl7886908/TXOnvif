package com.taixin.android.onvif.app.player;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;

import com.taixin.android.onvif.app.R;
import com.taixin.android.onvif.app.view.VideoView;

public class TXPlayer extends Activity {

	private VideoView videoView;
	private String videoPath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式 
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏 
		super.onCreate(savedInstanceState);
		setTheme(R.style.Home_Setting_Style);
		setContentView(R.layout.txplayer); 
		videoView = (VideoView) findViewById(R.id.tx_video_view);
		if(getIntent().getExtras().getString("videopath")!=null){
			videoPath=getIntent().getExtras().getString("videopath");
		}else{
			Toast.makeText(this, "文件格式不支持！", Toast.LENGTH_LONG).show();
		}
		videoView.setVideoURI(Uri.parse(videoPath)); 
		MediaController mediaController = new MediaController(this); 
		videoView.setMediaController(mediaController); 
		videoView.start(); 
		videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Toast toast = Toast.makeText(TXPlayer.this, "视频已经停止", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
