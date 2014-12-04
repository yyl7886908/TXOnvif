package com.taixin.android.onvif.app.data;

import java.util.ArrayList;

import com.taixin.android.onvif.sdk.obj.Device;

/**
 *摄像头列表的所有数据
 */
public class OnvifData {
	/*搜索到的所有的设备*/
	private ArrayList<Device> deviceList;
	/*所有的设备*/
	private ArrayList<CameraData> cameras;
	/*目前显示的设备*/
	private ArrayList<CameraData> currentCameras;
	/*四宫格数据*/
	private ArrayList<GridsItemStatus> gridsItems;
	/*本地存储的使用过的camera数据*/
	private ArrayList<LocalCamera> localCameras;
	/*本地存储的LocalSetting数据*/
	private LocalSetting localSet;

	private final static int GirdItemNum = 4;
	
	public OnvifData(){
		initData();
	}
	
	public ArrayList<Device> getDeviceList() {
		return deviceList;
	}

	public void setDeviceList(ArrayList<Device> deviceList) {
		this.deviceList = deviceList;
	}


	public ArrayList<GridsItemStatus> getGridsItemList() {
		return gridsItems;
	}

	public void setGridsItemList(ArrayList<GridsItemStatus> gridsItems) {
		this.gridsItems = gridsItems;
	}

	public ArrayList<LocalCamera> getLocalCameras() {
		return localCameras;
	}

	public void setLocalCameras(ArrayList<LocalCamera> localCameras) {
		this.localCameras = localCameras;
	}

	public ArrayList<CameraData> getCameras() {
		return cameras;
	}

	public void setCameras(ArrayList<CameraData> cameras) {
		this.cameras = cameras;
	}

	public ArrayList<CameraData> getCurrentCameras() {
		return currentCameras;
	}

	public void setCurrentCameras(ArrayList<CameraData> currentCameras) {
		this.currentCameras = currentCameras;
	}	

	public LocalSetting getLocalSet() {
		return localSet;
	}

	public void setLocalSet(LocalSetting localSet) {
		this.localSet = localSet;
	}
	
	/*初始化currentCameras 和gridsItems数据*/
	private void initData(){
		currentCameras = new ArrayList<CameraData>(GirdItemNum);
		
		gridsItems = new ArrayList<GridsItemStatus>(GirdItemNum);
		for(int i =0;i<GirdItemNum;i++){
			GridsItemStatus item = new GridsItemStatus();
			item.setStatus(-1);
			gridsItems.add(item);
		}
		localCameras = new ArrayList<LocalCamera>();
		localSet = new LocalSetting();
	}
}
