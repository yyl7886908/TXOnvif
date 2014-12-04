package com.taixin.android.onvif.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CameraImagesAdapter extends BaseAdapter {

	private List<File> list;
	private Context context;
	private LayoutInflater inflater = null;
	public CameraImagesAdapter(List<File> list, Context context){
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.list = list;
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CameraImagesViewHolder holder = null;
		if(convertView == null){
			holder = new CameraImagesViewHolder();
			convertView = inflater.inflate(R.layout.camera_images_grid_item, null);
			holder.img = (ImageButton) convertView.findViewById(R.id.camera_image_button);
			holder.title = (TextView) convertView.findViewById(R.id.camera_image_title);
			convertView.setTag(holder);
		}else{
			holder = (CameraImagesViewHolder) convertView.getTag();
		}
		final File file = list.get(position);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bm = BitmapFactory.decodeFile(file.getPath(), options);
		holder.img.setImageBitmap(bm);
		holder.title.setText(file.getName());
		final int p = position;
		holder.img.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				System.out.println("button clicked ======= position = "+p);
				Intent intent = new Intent(context, CameraImageShowActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("filepath",file.getPath());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
			
		});
		return convertView;
	}
	/**
	* 加载本地图片
	* http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getLoacalBitmap(String url) {
	     try {
	          FileInputStream fis = new FileInputStream(url);
	          return BitmapFactory.decodeStream(fis);
	     } catch (FileNotFoundException e) {
	          e.printStackTrace();
	          return null;
	     }
	}

	final class CameraImagesViewHolder{
		ImageButton img;
		TextView title;
	}
}
