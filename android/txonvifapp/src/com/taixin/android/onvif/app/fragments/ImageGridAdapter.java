package com.taixin.android.onvif.app.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.taixin.android.onvif.app.R;

public class ImageGridAdapter extends BaseAdapter {
	private Context context; 
	private LayoutInflater inflater = null;
	private int count;
	public ImageGridAdapter(Context context, int count){ 
		inflater = LayoutInflater.from(context);
		this.context = context; 
		this.count = count;
	} 
	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageGridViewHolder holder = null;
		if(convertView == null){
			holder = new ImageGridViewHolder();
			convertView = inflater.inflate(R.layout.local_image_grid_item, null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.local_image_view);
			//holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);  
			//holder.imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			holder.imageView.setPadding(2,2,2,2);
			convertView.setTag(holder);

		}else{
			holder = (ImageGridViewHolder) convertView.getTag();
		}
		holder.imageView.setImageResource(R.drawable.ic_launcher);
		return convertView;
	}

	class ImageGridViewHolder{
		ImageView imageView;
	}
}
