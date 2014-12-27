package com.taixin.android.onvif.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.app.AlertDialog.Builder;  
import android.content.DialogInterface;  
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
	private List<File> fileList;
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
				// 弹出确认框  
                Builder builder = new Builder(CameraVideosListActivity.this);  
                builder.setTitle("提示");  
                builder.setMessage("确定要删除所有文件吗?");  
          
                builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						for(File file : fileList){
							file.delete();
						}
						fileList.clear();
						mAdapter.notifyDataSetChanged();
					}

                });
                builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                    	
                    }  
                });  

                builder.create().show();  
				
			}
		}
	}

	private void initListView(){
		fileList = this.getCameraVideosFile();
		if(fileList.size() <= 0 || fileList == null){
			Toast toast = Toast.makeText(CameraVideosListActivity.this, "没有发现视频文件", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}else{
			mAdapter = new CameraVideoListAdapter(fileList, this);
			listView.setAdapter(mAdapter);
			listView.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view,final int position, long id) {
					final File file = fileList.get(position);
					System.out.println("file name ==="+file.getPath());
					 // 弹出确认框  
                    Builder builder = new Builder(CameraVideosListActivity.this);  
                    builder.setTitle("提示");  
                    builder.setMessage("确定要删除该文件(" + file.getName() + ")吗?");  
                    builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                            // 将SD卡中的文件删除  
                        	if (file.exists()) {  
                                file.delete();  
                            }  
                        	fileList.remove(position);
        					mAdapter.notifyDataSetChanged();  
                        }  
                    });  
                    builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                        }  
                    });  
  
                    builder.create().show();  
					
					
					return false;
				}
			});

			listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					File file = fileList.get(position);
					openFile(file);
				}
			});
		}
	}


}
