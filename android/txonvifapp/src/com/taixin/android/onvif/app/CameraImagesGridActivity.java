package com.taixin.android.onvif.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.taixin.android.onvif.app.util.Usb;

public class CameraImagesGridActivity extends Activity {

	//private final static String imagePath = "/storage/external_storage/sda1/CameraRecordImages";
	private CameraImagesAdapter mAdapter;
	private GridView imagesGrid;
	private List<File> imageList;
	private Button deleteAllButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_images_gird);
		initData();
	}
	
	public void initData(){
		deleteAllButton = (Button) this.findViewById(R.id.image_delete_button);
		deleteAllButton.setOnClickListener(new MyOnClickListener());
		imagesGrid = (GridView) findViewById(R.id.camera_images_grid);
		imageList = this.getCameraImagesFile();
		if(imageList.size() <= 0 || imageList == null){
			Toast toast = Toast.makeText(this, "没有照片文件", Toast.LENGTH_SHORT);
			toast.show();
		}else{
			mAdapter = new CameraImagesAdapter(imageList, this);
			imagesGrid.setAdapter(mAdapter);
			imagesGrid.setFocusable(true);
			imagesGrid.setVerticalScrollBarEnabled(true);
			imagesGrid.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					File file = imageList.get(position);
					System.out.println("==================file name = "+file.getName());
					Intent intent = new Intent(CameraImagesGridActivity.this, CameraImageShowActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("filepath",file.getPath());
					intent.putExtras(bundle);
					CameraImagesGridActivity.this.startActivity(intent);
				}
			});
		}
	}
	
	/*获取当前的录制照片文件信息*/
	public List<File> getCameraImagesFile(){
		List<String> list = Usb.getUsbDirList();
		if(list.size()<=0){
			Toast toast = Toast.makeText(CameraImagesGridActivity.this, "请先插入U盘", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		String imagePath = list.get(0)+"/CameraRecordImages/";
		File file = new File(imagePath);
		ArrayList<File> fileList  = new ArrayList<File>();
		File files[] = file.listFiles();
		if(files == null || files.length<=0){

		}
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
	
	class  MyOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			// 弹出确认框  
            Builder builder = new Builder(CameraImagesGridActivity.this);  
            builder.setTitle("提示");  
            builder.setMessage("确定要删除所有文件吗?");  
      
            builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for(File file : imageList){
						file.delete();
					}
					imageList.clear();
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
	};
}
