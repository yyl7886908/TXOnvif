package com.taixin.android.onvif.app;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.taixin.android.onvif.app.util.FileSizeUtil;
import com.taixin.android.onvif.app.util.FileUtil;

public class CameraVideoListAdapter extends BaseAdapter {

	private List<File> list;
	private Context context;
	private LayoutInflater inflater = null;
	private boolean isSelectedAll;
	private boolean isNeedDelete;
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
			holder.item_select_ok=(ImageView) convertView.findViewById(R.id.file_select_ok);
			convertView.setTag(holder);
		}else{
			holder = (CameraViewHolder) convertView.getTag();
//			if(isNeedDelete){
//				System.out.println("get view  isNeedDelete");
//				if(holder.item_select_ok.VISIBLE == View.VISIBLE){
//					File file = list.get(position);
//					//context.getContentResolver().delete(Uri.parse(file.getPath()), null, null);
//					file.delete();
//					list.remove(position);
//				}
//			}
		}
		if(isSelectedAll){
			holder.item_select_ok.setVisibility(View.VISIBLE);
		}
		else {
			holder.item_select_ok.setVisibility(View.GONE);
		}
		File file = list.get(position);
		holder.title.setText(file.getName());
		holder.videSize.setText("文件大小 : "+FileSizeUtil.getAutoFileOrFilesSize(file.getAbsolutePath().toString()));
		return convertView;
	}
	
	/*删除选中的文件*/
	public void deleteSelectedFile(String filePath){
		FileUtil.deleteFile(filePath);
	}
	
	
	final class CameraViewHolder{
        ImageView img;
        TextView title;
        TextView videSize;
        ImageView item_select_ok;
        //CheckBox cb;
    }

	public boolean isSelectedAll() {
		return isSelectedAll;
	}
	public void setSelectedAll(boolean isSelectedAll) {
		this.isSelectedAll = isSelectedAll;
	}
	
	public boolean isNeedDelete() {
		return isNeedDelete;
	}
	public void setNeedDelete(boolean isNeedDelete) {
		this.isNeedDelete = isNeedDelete;
	}
	
	
}
