package com.taixin.android.onvif.app;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

 
public class GridViewAdapter extends BaseAdapter{ 
	private Context context; 
	private int GridImgLayoutWidth = 475;
	private int GridImgLayoutHeight = 230;
	//图片数组 
	private Integer[] imgs = { 
			R.drawable.camera_default, R.drawable.camera_default,
			R.drawable.camera_default, R.drawable.camera_default
	}; 
	GridViewAdapter(Context context){ 
		this.context = context; 
	} 
	public int getCount() { 
		return imgs.length; 
	} 

	public Object getItem(int item) { 
		return item; 
	} 

	public long getItemId(int id) { 
		return id; 
	} 

	//创建View方法 
	public View getView(int position, View convertView, ViewGroup parent) { 
		ImageView imageView; 
		if (convertView == null) { 
			imageView = new ImageView(context); 
			imageView.setLayoutParams(new GridView.LayoutParams(GridImgLayoutWidth, GridImgLayoutHeight));//设置ImageView对象布局 
			imageView.setAdjustViewBounds(false);//设置边界对齐 
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置刻度的类型 
			imageView.setPadding(2, 2, 2, 2);//设置间距 
		}  
		else { 
			imageView = (ImageView) convertView; 
		} 
		imageView.setImageResource(imgs[position]);//为ImageView设置图片资源 
		return imageView; 
	} 
}