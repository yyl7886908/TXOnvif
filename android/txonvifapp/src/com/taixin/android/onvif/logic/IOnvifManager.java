package com.taixin.android.onvif.logic;

import java.util.ArrayList;

import android.content.Context;

import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.data.LocalSetting;
import com.taixin.android.onvif.app.data.OnvifData;
import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.ImagingSetting;
import com.taixin.ffmpeg.widget.VideoView;

/**
 * onivf功能对外抽象接口
 */
public interface IOnvifManager {
	
	public OnvifData getOnvifData();
	/*设置context*/
	public void setContext(Context context);
	/*检查uuid是否保存在本地，是的话给相关设备的username password 赋值*/
	public LocalCamera getLocalCameraByUUid(CameraData camera);
	/*保存用户名和密码*/
	public boolean saveNewCameraToLocal(LocalCamera camera);
	/*获取本地保存的LocalSetting*/
	public LocalSetting getLocalSetting();
	/*本地设置保存新的LocalSetting*/
	public boolean setLocalSetting(LocalSetting localSet);
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
	public ImagingSetting getImagingSetting(String username, String password,String imagingService, String videoSourceToken);

	/*抽象出来的播放动作, position代表是第position位置的摄像头对象*/
	public void play(int position, VideoView vv);
}
