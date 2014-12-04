package com.taixin.android.onvif.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

public class CameraImageShowActivity extends Activity {

	private ImageView img;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_image_show);
		img = (ImageView) findViewById(R.id.camera_show_image);
		Bundle b=getIntent().getExtras();
		String filePath=b.getString("filepath");
		System.out.println("file path = "+filePath);
		Bitmap bitmap = getLoacalBitmap(filePath); //从本地取图片
		img .setImageBitmap(bitmap);
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
	
}
