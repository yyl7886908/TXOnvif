package com.taixin.android.onvif.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.taixin.android.onvif.app.R;
import com.taixin.android.onvif.app.R.id;
import com.taixin.android.onvif.app.R.layout;
import com.taixin.ffmpeg.widget.VideoView;


public class GridViewAdapter extends BaseAdapter{ 
	private Context context; 
	private LayoutInflater inflater = null;
	public GridViewAdapter(Context context){ 
		inflater = LayoutInflater.from(context);
		this.context = context; 
	} 
	public int getCount() { 
		return 4; 
	} 

	public Object getItem(int item) { 
		return item; 
	} 

	public long getItemId(int id) { 
		return id; 
	} 
	public VideoView getVV(int position){
		GridViewHolder holder = new GridViewHolder();
		View view = inflater.inflate(R.layout.grid_item, null);
		holder.vv = (VideoView) view.findViewById(R.id.grid_video_view);
		return holder.vv;
	}

	//创建View方法 
	public View getView(int position, View convertView, ViewGroup parent) { 
		GridViewHolder holder = null;
		if(convertView == null){
			holder = new GridViewHolder();
			convertView = inflater.inflate(R.layout.grid_item, null);
			//holder.defaltImage = (ImageView) convertView.findViewById(R.id.ItemImage);
			holder.vv = (VideoView) convertView.findViewById(R.id.grid_video_view);
			convertView.setTag(holder);
		}else{
			holder = (GridViewHolder) convertView.getTag();
		}
		//holder.defaltImage.setImageResource(R.drawable.camera_default);
		return convertView;
	} 


	public final class GridViewHolder{
		//ImageView defaltImage;
		public VideoView vv;
	}
}