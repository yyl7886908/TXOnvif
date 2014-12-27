package com.taixin.android.onvif.app;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.taixin.android.onvif.app.DeviceListAdapter.DeviceViewHolder;
import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.GridsItemStatus;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
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
			if(list.size()>1){
				Toast toast = Toast.makeText(getApplicationContext(), "只能选择一个设备", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return false;
			}
			onvifMgr.getOnvifData().getCurrentCameras().clear();
			ArrayList<GridsItemStatus> gridsItem= onvifMgr.getOnvifData().getGridsItemList();
			for(GridsItemStatus item : gridsItem){
				item.setStatus(-1);
			}
//			if(list.size()<=0){
//				onvifMgr.getOnvifData().getCurrentCameras().clear();
//				ArrayList<GridsItemStatus> gridsItem= onvifMgr.getOnvifData().getGridsItemList();
//				for(GridsItemStatus item : gridsItem){
//					item.setStatus(-1);
//				}
//				return true;
//			}

			for(int i =0;i<list.size();i++){
				HashMap<String, String> temp = list.get(i);
				if(temp.get("flag").equals("true")){
					CameraData device =onvifMgr.getOnvifData().getCameras().get(i);
					if(!onvifMgr.checkDeviceIsInCurrent(device.getDevice().getUuid())){
						onvifMgr.getOnvifData().getCurrentCameras().add(device);
						onvifMgr.getOnvifData().getGridsItemList().get(i).setStatus(1);
					}else{
						onvifMgr.getOnvifData().getGridsItemList().get(i).setStatus(1);
						System.out.println("set status 1 ======="+i);
					}	
				}
			}
			System.out.println("current cameras size = "+onvifMgr.getOnvifData().getCurrentCameras().size());
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
				DeviceViewHolder holder = (DeviceViewHolder) view.getTag();
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
			if(onvifMgr.checkDeviceIsInCurrent(deviceList.get(i).getDevice().getUuid()))
				map.put("flag", "true");
			else
				map.put("flag", "false");
			list.add(map);
		}
	}
}
