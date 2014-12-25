package com.taixin.android.onvif.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taixin.android.onvif.app.CameraVideoListAdapter.CameraViewHolder;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.player.TXPlayer;
import com.taixin.android.onvif.app.util.Usb;

/*显示录制视频的列表*/
public class CameraVideosListActivity extends Activity {

	private String tag = "CameraVideosListActivity";
	/*视频文件存储路径*/
	private String videoFolder = "/CameraRecordVideos/";
	private IOnvifManager onvifMgr;
	private List<String> items = null;//存放名称    
    private List<String> paths = null;//存放路径    
	private ListView listView;
	private TextView videoTitle;
	private TextView videSize;
	private CameraVideoListAdapter mAdapter;
	private Button selectButton;
	private Button deleteButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_videos);
		onvifMgr = OnvifManager.getInstance();
		initData();
	}

	public void initData(){
		selectButton =  (Button) findViewById(R.id.video_select_button);
		deleteButton =  (Button) findViewById(R.id.video_delete_button);
		MyButtonOnClickListener listener = new MyButtonOnClickListener();
		selectButton.setOnClickListener(listener);
		deleteButton.setOnClickListener(listener);
		listView = (ListView) findViewById(R.id.camera_videos_list);
		listView.setFocusable(true);
		onvifMgr = OnvifManager.getInstance();
		initListView();
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
		//String videoPath = list.get(0)+videoFolder;
		String videoPath = list.get(0)+"/CameraRecordVideos/";
		String imagePath = list.get(0)+"/CameraRecordImages/";
		Log.i(tag, "video path = "+videoPath);
		File file = new File(videoPath);
		ArrayList<File> fileList  = new ArrayList<File>();
		File files[] = file.listFiles();
		if(files == null || files.length<=0)
			Log.i(tag, "files ======== null");
		if(files!=null){
			//			for(File f : files){
			//				fileList.add(f);
			//			}
			for(int i = files.length-1; i>=0;i--){
				File f = files[i];
				fileList.add(f);
			}
		}
		System.out.println("file list size = "+fileList.size());
		return fileList;
	}

	class MyButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Button button = (Button)v;
			if(button == selectButton){
				System.out.println("select button===");
				if(mAdapter.isSelectedAll()){
					mAdapter.setSelectedAll(false);
					mAdapter.notifyDataSetChanged();
				}else {
					mAdapter.setSelectedAll(true);
					mAdapter.notifyDataSetChanged();
				}
			}else if(button == deleteButton){
				System.out.println("删除  button  已经全选");
				mAdapter.setNeedDelete(true);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private void initListView(){
		final List<File> list = this.getCameraVideosFile();
		if(list.size() <= 0 || list == null){
			Toast toast = Toast.makeText(CameraVideosListActivity.this, "没有发现视频文件", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}else{
			mAdapter = new CameraVideoListAdapter(list, this);
			listView.setAdapter(mAdapter);
			listView.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					return false;
				}
			});

			listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					File file = list.get(position);
					openFile(file);
				}
			});
		}
	}


}
