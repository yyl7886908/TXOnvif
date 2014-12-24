package com.taixin.android.onvif.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.taixin.android.onvif.app.util.Usb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraImageShowActivity extends Activity {

	private ImageView img;
	private String filePath;
	private List<File> fileList;
	private int fileIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_image_show);
		img = (ImageView) findViewById(R.id.camera_show_image);
		Bundle b=getIntent().getExtras();
		filePath=b.getString("filepath");
		System.out.println("file path = "+filePath);
		Bitmap bitmap = getLoacalBitmap(filePath); //从本地取图片
		img .setImageBitmap(bitmap);
		fileList = this.getCameraImagesFile();
		fileIndex = this.getImageShowingIndex(filePath);
	}
	/**
	* 加载本地图片
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public  Bitmap getLoacalBitmap(String url) {
	     try {
	          FileInputStream fis = new FileInputStream(url);
	          return BitmapFactory.decodeStream(fis);
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			showingLastImage();
			return false;
		}else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			showingNextImage();
			return false;
		}else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			showingNextImage();
			return false;
		}else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			showingLastImage();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/*获取当前的录制照片文件信息*/
	public List<File> getCameraImagesFile(){
		List<String> list = Usb.getUsbDirList();
		if(list.size()<=0){
			Toast toast = Toast.makeText(CameraImageShowActivity.this, "请先插入U盘", Toast.LENGTH_LONG);
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
	
	/*当前显示照片的index*/
	private int getImageShowingIndex(String filePath){
		for(int i = 0; i < fileList.size(); i++){
			File file = fileList.get(i);
			if(file.getPath().equals(filePath))
				return i;
		}
		return -1;
	}
	
	/*显示上一张图片*/
	private void showingLastImage(){
		if(fileIndex > 0){
			fileIndex = fileIndex -1;
			File file = fileList.get(fileIndex);
			Bitmap bitmap = getLoacalBitmap(file.getPath()); //从本地取图片
			img .setImageBitmap(bitmap);
		}else{
			fileIndex = fileList.size()-1;
			File file = fileList.get(fileIndex);
			Bitmap bitmap = getLoacalBitmap(file.getPath()); //从本地取图片
			img .setImageBitmap(bitmap);
		}
	}
	
	/*显示下一张图片*/
	private void showingNextImage(){
		if(fileIndex < fileList.size()-1){
			fileIndex = fileIndex + 1;
			File file = fileList.get(fileIndex);
			Bitmap bitmap = getLoacalBitmap(file.getPath()); //从本地取图片
			img .setImageBitmap(bitmap);
		}else{
			fileIndex = 0;
			File file = fileList.get(fileIndex);
			Bitmap bitmap = getLoacalBitmap(file.getPath()); //从本地取图片
			img .setImageBitmap(bitmap);
		}
	}
	
}
