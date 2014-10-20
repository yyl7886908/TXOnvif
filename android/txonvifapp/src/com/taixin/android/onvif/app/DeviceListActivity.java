package com.taixin.android.onvif.app;

import java.util.ArrayList;
import java.util.HashMap;

import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.logic.IOnvifManager;
import com.taixin.android.onvif.logic.OnvifManager;
import com.taixin.android.onvif.sdk.obj.Device;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
public class DeviceListActivity extends Activity {

	private IOnvifManager onvifMgr;
	private ListView listView;
	String titleData[] = new String[]{
			"海康", "汉邦"
	};
	String uuidData[] = null;
	String serviceData[] = null;
	@Override
	protected void onCreate(Bundle bundle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(bundle);
		setContentView(R.layout.device_list);
		onvifMgr = OnvifManager.getInstance();
		displayDeviceList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void displayDeviceList(){
		listView = (ListView) findViewById(R.id.device_list);
		
		ArrayList<CameraData> list = OnvifManager.getInstance().getOnvfData().getCameras();
		uuidData = new String[list.size()+1];
		serviceData=new String[list.size()+1];
		for(int i =0;i<list.size()+1; i++){
			uuidData[i] = list.get(0).getDevice().getUuid();
			serviceData[i] = list.get(0).getDevice().getDeviceService();
		}
		SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.device_list_item, 
				new String[]{"title","img"}, new int[]{R.id.device_title,R.id.img});

		listView.setAdapter(adapter);
	}

	private ArrayList<HashMap<String, Object>> getData() {    	
		ArrayList<HashMap<String, Object>> dlist = new ArrayList<HashMap<String,Object>>();

		for(int i =0;i<titleData.length;i++){
			HashMap<String, Object>map = new HashMap<String, Object>();        	
			map.put("title", titleData[i]);
			map.put("uuid", uuidData[i]);
			map.put("service_adderss", serviceData[i]);
			map.put("img", R.drawable.ic_launcher);
			dlist.add(map);	
		}
		return dlist;
	}
}
