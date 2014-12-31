package com.taixin.android.onvif.app.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.data.LocalSetting;
import com.taixin.android.onvif.app.data.OnvifData;
import com.taixin.android.onvif.app.data.OrderRecordData;
import com.taixin.android.onvif.app.data.OrderRecordModel;
import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.ImagingSetting;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;
import com.taixin.ffmpeg.widget.VideoView;

/**
 * onivf功能对外抽象接口
 */
public interface IOnvifManager {
	
	public OnvifData getOnvifData();
	/*设置context*/
	public void setContext(Context context);
	/*获取应用使用的次数*/
	public int getAppUsingCount();
	/*对应用的使用次数加1*/
	public boolean addAppUsingCount();
	/*检查uuid是否保存在本地，是的话给相关设备的username password 赋值*/
	public LocalCamera getLocalCameraByUUid(CameraData camera);
	/*保存用户名和密码*/
	public boolean saveNewCameraToLocal(LocalCamera camera);
	/*获取本地保存的LocalSetting*/
	public LocalSetting getLocalSetting();
	/*保存上一次连接设备的信息*/
	public boolean savePreviousCameraInfo(LocalCamera camera);
	/*获取上一次连接设备的信息*/
	public LocalCamera getPreviousCameraInfo();
	/*检测上一次连接的设备是否在本次搜索的设备列表中,如果在的话返回这个摄像头对象*/
	public CameraData checkPrevCameraIsSearched(LocalCamera camera);
	/*本地设置保存新的LocalSetting*/
	public boolean setLocalSetting(LocalSetting localSet);
	/*搜索设备监听器*/
	public void setListener(searchDevicesListener listener) ;
	/*发现设备*/
	public ArrayList<Device> discoverDevices();
	/*获取能力*/
	public boolean getDeviceCapabilities(String username, String password, String deviceService);
	/*获取配置文件*/
	public boolean getMediaProfiles(String username, String password, String mediaService);
	/*获取视频流*/
	public boolean getMediaStreamUri(String username, String password, String deviceService);
	/*云台 上下左右*/
	public void ptzLeft(String username, String password, String ptzService, String profileToken);
	public void ptzRight(String username, String password, String ptzService, String profileToken);
	public void ptzUp(String username, String password, String ptzService, String profileToken);
	public void ptzDown(String username, String password, String ptzService, String profileToken);
	public void ptzCruiseStart(String username, String password, String ptzService, String profileToken);
	public void ptzCruiseStop(String username, String password, String ptzService,String profileToken);
	public void ptzCruise(String username, String password, String ptzService,String profileToken, boolean cruise);
	/*图像设置*/
	public boolean setImagingSetting(String username, String password, String imageService, String videoSourceToken, float bright, float chrom, float constrast);
	/*获取图像设置的参数*/
	public ImagingSetting getImagingSetting(String username, String password,String imagingService, String videoSourceToken);
	/*抽象出来的播放动作, position代表是第position位置的摄像头对象*/
	public void play(int position, VideoView vv);
	/*获取认证后的uri*/
	public String getAuthUriByPosition(int position);
	/*获取U盘里面的所有图片*/
	public List<HashMap<String, String>> getImages();
	/**
	 * 下面预约和定时函数录制相关
	 */
	/*预约录制保存对象 淘汰*/
	//public boolean saveOrderRecordModel(OrderRecordModel oRecord);
	/*预约录制保存新对象*/
	public boolean saveOrderRecordData(OrderRecordData data);
	/*读取所有预约录制的对象 淘汰*/
	//public OrderRecordModel getOrderedRecordModel();
	/*读取所有预约录制的数据*/
	public OrderRecordData getOrderedRecordData();
	
	/*此处为录制单独开启一部分获取信息接口*/
	/*检查uuid是否保存在本地，是的话给相关设备的username password 赋值*/
	public LocalCamera getLocalCameraByUUidForRecord(String uuid);
	
	public ArrayList<MediaStreamUri> getMediaStreamUriForRecord(String username, String password, String deviceService);
	
	/**
	 * 以下接口和DeviceListActivity有关
	 */
	public boolean checkDeviceIsInCurrent(String uuid);
	/*退出释放所有的资源*/
	public void resourceTerm();
	
	/*四格视频的相关接口*/
	public boolean saveGridItemCameraToLocal(int itemIndex, String uuid);/*保存每个gird item 对应的摄像头uuid*/
	/*根据item index 获取摄像头列表里面对应的摄像头*/
	public CameraData getCameraDataByIndex(int itemIndex);
}
