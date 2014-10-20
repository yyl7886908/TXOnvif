package com.taixin.android.onvif.app;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceListAdapter extends BaseAdapter {

	private Context context;
	
	public DeviceListAdapter(Context context){
		this.context = context;
	}
	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("mxy", "位置" + position);  

		View view = View.inflate(context, R.layout.device_list_item, null);    
		return view;  
	}

}
