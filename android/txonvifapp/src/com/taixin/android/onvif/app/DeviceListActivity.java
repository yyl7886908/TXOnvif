package com.taixin.android.onvif.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.taixin.android.onvif.app.DeviceListAdapter.ViewHolder;
import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.GridsItemStatus;
import com.taixin.android.onvif.logic.IOnvifManager;
import com.taixin.android.onvif.logic.OnvifManager;
public class DeviceListActivity extends Activity {

	private IOnvifManager onvifMgr;
	private ListView listView;
	private Button importBtn;
	private CheckBox cb;
	private DeviceListAdapter mAdapter;
	private ArrayList<HashMap<String, String>> list;
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/*先清空当前设备列表*/
			onvifMgr.getOnvifData().getCurrentCameras().clear();
			for(int i =0;i<list.size();i++){
				HashMap<String, String> temp = list.get(i);
				if(temp.get("flag").equals("true")){
					CameraData device =onvifMgr.getOnvifData().getCameras().get(i);
					onvifMgr.getOnvifData().getCurrentCameras().add(device);
				}
			}
			System.out.println("current cameras size = "+onvifMgr.getOnvifData().getCurrentCameras().size());
			for(GridsItemStatus item : onvifMgr.getOnvifData().getGridsItemList()){
				item.setStatus(-1);
			}
			for(int a = 0; a<onvifMgr.getOnvifData().getCurrentCameras().size(); a++){
				onvifMgr.getOnvifData().getGridsItemList().get(a).setStatus(0);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void displayDeviceList(){
		listView = (ListView) findViewById(R.id.device_list);
		list = new ArrayList<HashMap<String, String>>();
		// 为Adapter准备数据
		initDate();
		mAdapter = new DeviceListAdapter(list, this);
		// 绑定Adapter
		listView.setAdapter(mAdapter);
		listView.setFocusable(true);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ViewHolder holder = (ViewHolder) view.getTag();
				holder.cb.toggle();
				if(holder.cb.isChecked() == true){
					list.get(position).put("flag", "true");
				}else{
					list.get(position).put("flag", "false");
				}
			}
		});
	}

	// 初始化数据
	private void initDate() {
		ArrayList<CameraData> deviceList = OnvifManager.getInstance().getOnvifData().getCameras();
		for (int i = 0; i < deviceList.size(); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("title", "海康");
			map.put("uuid", deviceList.get(i).getDevice().getUuid());
			map.put("deviceService", deviceList.get(i).getDevice().getDeviceService());
			map.put("flag", "false");
			list.add(map);
		}
	}
}
