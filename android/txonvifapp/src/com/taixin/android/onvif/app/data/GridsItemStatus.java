package com.taixin.android.onvif.app.data;

/*保存四宫格每一格的状态标志*/
public class GridsItemStatus {
	/*0 代表默认图片 ； 1代表搜到设备，没有播放； 2代表正在播放*/
	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
