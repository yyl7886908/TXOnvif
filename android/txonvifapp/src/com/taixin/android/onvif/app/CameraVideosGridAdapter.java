package com.taixin.android.onvif.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.taixin.android.onvif.app.CameraVideoListAdapter.CameraViewHolder;
import com.taixin.android.onvif.app.util.FileSizeUtil;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CameraVideosGridAdapter extends BaseAdapter {
	private List<File> list;
	private Context ctx;  
	private LayoutInflater inflater = null;
	private List<Map<String, Object>> allValues = new ArrayList<Map<String, Object>>();

	public CameraVideosGridAdapter(Context ctx, List<Map<String, Object>> allValues){
		this.ctx = ctx;  
        this.allValues = allValues;  
        inflater = LayoutInflater.from(ctx);
	}
	@Override
	public int getCount() {
		return allValues.size();  
	}

	@Override
	public Object getItem(int position) {
		return allValues.get(position);  
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
			holder.item_select_ok=(ImageView) convertView.findViewById(R.id.file_select_ok);
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
        ImageView item_select_ok;
        //CheckBox cb;
    }

}
