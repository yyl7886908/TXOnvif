package com.taixin.android.onvif.app.data;

import java.util.ArrayList;
/**
 *摄像头列表的所有数据
 */
public class OnvifData {
	
	private ArrayList<CameraData> cameras;

	public ArrayList<CameraData> getCameras() {
		return cameras;
	}

	public void setCameras(ArrayList<CameraData> cameras) {
		this.cameras = cameras;
	}
}
