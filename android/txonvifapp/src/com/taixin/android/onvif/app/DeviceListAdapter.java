package com.taixin.android.onvif.app;

import java.util.ArrayList;
import java.util.HashMap;

import com.taixin.android.onvif.app.R;
import com.taixin.android.onvif.app.R.drawable;
import com.taixin.android.onvif.app.R.id;
import com.taixin.android.onvif.app.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceListAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> list;
	private Context context;
	private LayoutInflater inflater = null;
	public DeviceListAdapter(ArrayList<HashMap<String, String>> list, Context context){
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
		// TODO 自动生成的方法存根
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DeviceViewHolder holder = null;
		if(convertView == null){
			holder = new DeviceViewHolder();
			convertView = inflater.inflate(R.layout.device_list_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.device_title);
			holder.uuid = (TextView) convertView.findViewById(R.id.device_uuid);
			holder.deviceService = (TextView) convertView.findViewById(R.id.device_service);
			holder.cb = (CheckBox) convertView.findViewById(R.id.device_checkbox);
			holder.img = (ImageView) convertView.findViewById(R.id.device_img);
			
			convertView.setTag(holder);
		}else{
			holder = (DeviceViewHolder) convertView.getTag();
		}
		holder.uuid.setText(list.get(position).get("uuid").toString());
		holder.deviceService.setText(list.get(position).get("deviceService").toString());
		//if(onvifMgr.get)
		holder.cb.setChecked(list.get(position).get("flag").equals("true"));
		holder.img.setImageResource(R.drawable.camera);
		String str[] = list.get(position).get("deviceService").toString().split("/");
		holder.title.setText(str[2]);
		return convertView;
	}

	public final class DeviceViewHolder{
		public  CheckBox cb;
		public   ImageView img;
		public    TextView title;
		public    TextView uuid;
		public    TextView deviceService;
    }
}
