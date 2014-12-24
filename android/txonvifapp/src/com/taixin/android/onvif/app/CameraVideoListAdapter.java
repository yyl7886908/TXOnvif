package com.taixin.android.onvif.app;

import java.io.File;
import java.util.List;

import com.taixin.android.onvif.app.R;
import com.taixin.android.onvif.app.util.FileSizeUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CameraVideoListAdapter extends BaseAdapter {

	private List<File> list;
	private Context context;
	private LayoutInflater inflater = null;
	public CameraVideoListAdapter(List<File> list, Context context){
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
		CameraViewHolder holder = null;
		if(convertView == null){
			holder = new CameraViewHolder();
			convertView = inflater.inflate(R.layout.camera_videos_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.camera_video_title);
			holder.videSize = (TextView) convertView.findViewById(R.id.camera_video_size);
			holder.img = (ImageView) convertView.findViewById(R.id.camera_video_img);
			convertView.setTag(holder);
		}else{
			holder = (CameraViewHolder) convertView.getTag();
		}
		File file = list.get(position);
		holder.title.setText(file.getName());
		holder.videSize.setText("文件大小 : "+FileSizeUtil.getAutoFileOrFilesSize(file.getAbsolutePath().toString()));
		return convertView;
	}
	
	final class CameraViewHolder{
        ImageView img;
        TextView title;
        TextView videSize;
    }

}
