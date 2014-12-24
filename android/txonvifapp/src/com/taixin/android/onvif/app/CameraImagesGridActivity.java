package com.taixin.android.onvif.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.taixin.android.onvif.app.util.Usb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class CameraImagesGridActivity extends Activity {

	//private final static String imagePath = "/storage/external_storage/sda1/CameraRecordImages";
	private CameraImagesAdapter mAdapter;
	private GridView imagesGrid;
	private List<File> imageList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_images_gird);
		initData();
	}
	
	public void initData(){
		imagesGrid = (GridView) findViewById(R.id.camera_images_grid);
		imageList = this.getCameraImagesFile();
		if(imageList.size() <= 0 || imageList == null){
			Toast.makeText(this, "没有发现图片文件", Toast.LENGTH_LONG).show();
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
		if(files == null || files.length<=0)
			Toast.makeText(this, "file is null", Toast.LENGTH_LONG).show();
		if(files!=null){
			for(File f : files){
				fileList.add(f);
				System.out.println("each file name = "+f.getName());
				System.out.println("each file path = "+f.getPath());
			}
		}
		System.out.println("file list size = "+fileList.size());
		return fileList;
	}
}
