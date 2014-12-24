package com.taixin.android.onvif.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.player.TXPlayer;
import com.taixin.android.onvif.app.util.Usb;
import com.taixin.ffmpeg.widget.MediaController;
import com.taixin.ffmpeg.widget.VideoView;

/*显示录制视频的列表*/
public class CameraVideosListActivity extends Activity {
	/*视频文件存储路径*/
	//private final String videoPath = "/storage/external_storage/sda1/CameraRecordVideos";
	private IOnvifManager onvifMgr;
	//private VideoView vv;
	private ListView listView;
	private TextView videoTitle;
	private TextView videSize;
	private CameraVideoListAdapter mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		//setTheme(R.style.Home_Setting_Style);
		setContentView(R.layout.camera_videos);
		onvifMgr = OnvifManager.getInstance();
		initData();
	}

	public void initData(){
		//vv = (VideoView) findViewById(R.id.tx_playback_video_view);
		listView = (ListView) findViewById(R.id.camera_videos_list);
		listView.setFocusable(true);
		onvifMgr = OnvifManager.getInstance();
		final List<File> list = this.getCameraVideosFile();
		if(list.size() <= 0 || list == null){
			Toast.makeText(this, "没有发现视频文件", Toast.LENGTH_LONG).show();
		}else{
			mAdapter = new CameraVideoListAdapter(list, this);
			listView.setAdapter(mAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					File file = list.get(position);
					openFile(file);
					//playVideoView(file);
				}
				
			});
		}
	}

	public void openFile(File f){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("videopath", f.getPath());
		intent.setClass(this, TXPlayer.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
	}
	/*获取当前的录制视频文件信息*/
	public List<File> getCameraVideosFile(){
		List<String> list = Usb.getUsbDirList();
		if(list.size()<=0){
			Toast toast = Toast.makeText(CameraVideosListActivity.this, "请先插入U盘", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		String videoPath = list.get(0)+"/CameraRecordVideos/";
		File file = new File(videoPath);
		ArrayList<File> fileList  = new ArrayList<File>();
		File files[] = file.listFiles();
		if(files == null || files.length<=0)
			Toast.makeText(this, "file is null", Toast.LENGTH_LONG).show();
		if(files!=null){
			for(File f : files){
				fileList.add(f);
			}
		}
		System.out.println("file list size = "+fileList.size());
		return fileList;
	}

//	private void playVideoView(File file){
//		//mMediaController = new MediaController(this);
//		//videoView.setMediaController(mMediaController);
//		vv.setVideoURI(Uri.parse(file.getPath())); 
//		vv.start(); 
//	}
}
