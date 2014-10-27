package com.taixin.android.onvif.logic;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.data.LocalSetting;
import com.taixin.android.onvif.app.data.OnvifData;
import com.taixin.android.onvif.app.util.SerializableUtil;
import com.taixin.android.onvif.sdk.SimpleOnvif;
import com.taixin.android.onvif.sdk.TXOnvif;
import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.DeviceCapability;
import com.taixin.android.onvif.sdk.obj.ImagingSetting;
import com.taixin.android.onvif.sdk.obj.MediaProfilesInfo;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;
import com.taixin.android.onvif.sdk.obj.PTZType;
import com.taixin.ffmpeg.widget.VideoView;

/**
 * onivf功能对外抽象接口实现
 */
public class OnvifManager implements IOnvifManager {

	private String TAG = "TXOnvif";
	private String localCameraFileName = "local_camera";
	private String localSettingFileName = "local_setting";
	private static OnvifManager instance;
	public static OnvifManager getInstance(){
		if(instance == null){
			instance = new OnvifManager();
		}
		return instance;
	}
	/*是否正在巡航的标志*/
	private boolean isCruising = false;
	private int flag = 0;/*0代表左巡航，1代表右巡航*/
	private Timer timer;
	private TimerTask task;
	private SimpleOnvif onvifSdk= null;
	private OnvifData onvfData = null;
	private Context context;
	public OnvifManager(){
		onvifSdk = new TXOnvif();
		onvfData = new OnvifData();
	}

	public void setOnvfData(OnvifData onvfData) {
		this.onvfData = onvfData;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public OnvifData getOnvifData() {
		// TODO 自动生成的方法存根
		return onvfData;
	}

	@Override
	public ArrayList<Device> discoverDevices() {
		ArrayList<Device> deviceList = onvifSdk.discoverDevices();
		Log.i(TAG, "deviceList size = "+deviceList.size());
		ArrayList<CameraData> cameras = new ArrayList<CameraData>();
		for(int i = 0; i < deviceList.size(); i++){
			CameraData camera = new CameraData();
			camera.setDevice(deviceList.get(i));
			cameras.add(camera);
		}
		onvfData.setCameras(cameras);

		return deviceList;
	}

	@Override
	public boolean getDeviceCapabilities(String username,
			String password, String deviceService) {
		DeviceCapability capa = onvifSdk.getDeviceCapabilities(username, password, deviceService);
		if(capa.equals(null)|| capa == null)
			return false;
		for(CameraData camera : onvfData.getCameras()){
			if(camera.getDevice().getDeviceService().equals(deviceService)){
				camera.setAuth(true);
				camera.setCapability(capa);
			}
		}
		for(CameraData camera : onvfData.getCurrentCameras()){
			if(camera.getDevice().getDeviceService().equals(deviceService)){
				camera.setAuth(true);
				camera.setCapability(capa);
			}
		}
		return true;
	}

	@Override
	public boolean getMediaProfiles(String username,
			String password, String mediaService) {
		ArrayList<MediaProfilesInfo> profiles = onvifSdk.getMediaProfiles(username, password, mediaService);
		if(profiles == null || profiles.size()<=0)
			return false;
		for(CameraData camera : onvfData.getCurrentCameras()){
			if(camera.isAuth() && camera.getCapability().getMediaService().equals(mediaService)){
				camera.setProfiles(profiles);
			}
		}
		for(CameraData camera : onvfData.getCameras()){
			if(camera.isAuth() && camera.getCapability().getMediaService().equals(mediaService)){
				camera.setProfiles(profiles);
			}
		}
		return true;
	}

	@Override
	public boolean getMediaStreamUri(String username,
			String password, String deviceService) {
		ArrayList<MediaStreamUri> uris = onvifSdk.getMediaStreamUri(username, password, deviceService);
		if(uris == null || uris.size() <= 0)
			return false;
		for(CameraData camera : onvfData.getCurrentCameras()){
			if(camera.isAuth() && camera.getCapability().getDeviceService().equals(deviceService)){
				camera.setStreamUri(uris);
			}
		}
		for(CameraData camera : onvfData.getCameras()){
			if(camera.isAuth() && camera.getCapability().getDeviceService().equals(deviceService)){
				camera.setStreamUri(uris);
			}
		}
		return true;
	}
	
	/*获取本地数据*/
	public void getLocalCameraData(){
		SharedPreferences mySharedPreferences = context.getSharedPreferences("local_camera", 
				Activity.MODE_PRIVATE); 
		String str = mySharedPreferences.getString("local_camera", "default");
		if(!str.equals("default")){
			try {
				List list = SerializableUtil.string2List(str);
				ArrayList<LocalCamera> lCameras = new ArrayList<LocalCamera>();
				if(list.size()>0){
					for(int i = 0; i<list.size(); i++){
						Object obj = list.get(i);
						LocalCamera camera = (LocalCamera)obj;
						lCameras.add(camera);
					}
				}
				onvfData.setLocalCameras(lCameras);
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public LocalCamera getLocalCameraByUUid(CameraData camera) {
		getLocalCameraData();
		String uuid = camera.getDevice().getUuid();
		for(LocalCamera lCamera : onvfData.getLocalCameras()){
			if(uuid.equals(lCamera.getUuid())){
				camera.setUsername(lCamera.getUsername());
				camera.setPassword(lCamera.getPassword());
				return lCamera;
			}
		}
		return null;
	}
	
	@Override
	public boolean saveNewCameraToLocal(LocalCamera camera) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(localCameraFileName, Activity.MODE_PRIVATE); 
		String str = mySharedPreferences.getString("local_camera", "default");
		if(!str.equals("default")){
			try {
				List list = SerializableUtil.string2List(str);
				ArrayList<LocalCamera> lCameras = onvfData.getLocalCameras();
				if(lCameras.size()>0){
					for(LocalCamera c : lCameras){
						if(c.getUuid().equals(camera.getUuid())){
							/*旧用户保存新密码*/
							c.setPassword(camera.getPassword());
							c.setUsername(camera.getUsername());
							String strtmp = SerializableUtil.list2String(lCameras);
							SharedPreferences.Editor editor = mySharedPreferences.edit();
							editor.putString("local_camera",strtmp);
							editor.commit();
							return true;
						}
					}
					/*新用户保存数据*/
					lCameras.add(camera);
					onvfData.setLocalCameras(lCameras);
					String strtmp = SerializableUtil.list2String(lCameras);
					SharedPreferences.Editor editor = mySharedPreferences.edit();
					editor.putString("local_camera",strtmp);
					editor.commit();
					return true;
				}
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public LocalSetting getLocalSetting() {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(localSettingFileName, Activity.MODE_PRIVATE); 
		boolean autoPlay = mySharedPreferences.getBoolean("auto_play", true);
		boolean isActual = mySharedPreferences.getBoolean("is_actual", true);
		boolean isMainStream = mySharedPreferences.getBoolean("is_main_stream", true);
		LocalSetting localSet = new LocalSetting();
		localSet.setAutoPlay(autoPlay);
		localSet.setActual(isActual);
		localSet.setMainStream(isMainStream);
		return localSet;
	}
	
	@Override
	public boolean setLocalSetting(LocalSetting localSet) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(localSettingFileName, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putBoolean("auto_play",localSet.isAutoPlay());
		editor.putBoolean("is_actual",localSet.isActual());
		editor.putBoolean("is_main_stream",localSet.isMainStream());
		editor.commit();
		return true;
	}
	
	@Override
	public void ptzLeft(String username, String password, String ptzService, String profileToken) {
		onvifSdk.ptzContinuousMove(username, password, ptzService, profileToken, PTZType.PTZ_MOVE, (float)-0.8, 0, 0);
		try {
			Thread.sleep(500);
			onvifSdk.ptzStop(username, password, ptzService, profileToken, PTZType.PTZ_MOVE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ptzRight(String username, String password, String ptzService, String profileToken) {
		onvifSdk.ptzContinuousMove(username, password, ptzService, profileToken, PTZType.PTZ_MOVE, (float)0.8, 0, 0);
		try {
			Thread.sleep(500);
			onvifSdk.ptzStop(username, password, ptzService, profileToken, PTZType.PTZ_MOVE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ptzUp(String username, String password, String ptzService, String profileToken) {
		onvifSdk.ptzContinuousMove(username, password, ptzService, profileToken, PTZType.PTZ_MOVE, 0, (float) 0.8, 0);		
		try {
			Thread.sleep(300);
			onvifSdk.ptzStop(username, password, ptzService, profileToken, PTZType.PTZ_MOVE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ptzDown(String username, String password, String ptzService, String profileToken) {
		onvifSdk.ptzContinuousMove(username, password, ptzService, profileToken, PTZType.PTZ_MOVE, 0, (float) -0.8, 0);		
		try {
			Thread.sleep(300);
			onvifSdk.ptzStop(username, password, ptzService, profileToken, PTZType.PTZ_MOVE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ptzCruiseStart(String username, String password, String ptzService,
			String profileToken) {
		isCruising = false;
		final String user = username;
		final String pass = password;
		final String service = ptzService;
		final String token = profileToken;
		new Thread(){
			public void run(){
				while(!isCruising){
					if(flag == 0){
						onvifSdk.ptzContinuousMove(user, pass, service, token, PTZType.PTZ_MOVE, (float)-0.8, 0, 0);
						flag = 1;
						try {
							sleep(40 * 1000);
							onvifSdk.ptzStop(user, pass, service, token, PTZType.PTZ_MOVE);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}else if(flag == 1){
						onvifSdk.ptzContinuousMove(user, pass, service, token, PTZType.PTZ_MOVE, (float)0.8, 0, 0);
						flag = 0;
						try {
							sleep(40 * 1000);
							onvifSdk.ptzStop(user, pass, service, token, PTZType.PTZ_MOVE);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};

		}.start();
	}

	@Override
	public void ptzCruiseStop(String username, String password,
			String ptzService, String profileToken) {
		isCruising = true;
		//onvifSdk.ptzStop(username, password, ptzService, password, PTZType.PTZ_MOVE);	
	}

	@Override
	public boolean setImagingSetting(String username, String password,
			String imageService, String videoSourceToken, float bright, float chrom, float constrast) {
		ImagingSetting imgSetting = new ImagingSetting(bright,chrom,constrast);
		int ret = onvifSdk.setImagingSetting(username, password, imageService, videoSourceToken, imgSetting);
		if(ret ==0)
			return true;
		return false;
	}

	@Override
	public ImagingSetting getImagingSetting(String username, String password,
			String imagingService, String videoSourceToken) {
		return onvifSdk.getImagingSetting(username, password, imagingService, videoSourceToken);
	}

	@Override
	public void ptzCruise(String username, String password, String ptzService,
			String profileToken, boolean cruise) {
		final String user = username;
		final String pass = password;
		final String service = ptzService;
		final String token = profileToken;
		if(timer == null)
			timer= new Timer(true);
		if(task == null){
			new TimerTask(){
				@Override
				public void run() {
					//每次需要执行的代码放到这里面。
					onvifSdk.ptzStop(user, pass, service, token, PTZType.PTZ_MOVE);
					/*巡航*/
					if(flag == 0){
						onvifSdk.ptzContinuousMove(user, pass, service, token, PTZType.PTZ_MOVE, (float)-0.8, 0, 0);
						flag = 1;
					}else if(flag == 1){
						onvifSdk.ptzContinuousMove(user, pass, service, token, PTZType.PTZ_MOVE, (float)0.8, 0, 0);
						flag = 1;
					}
				}
			};
			/*如果正在巡航，则停止巡航，如果没有巡航，则开始巡航*/
			if(cruise){
				timer.schedule(task, 40);
			}else{
				onvifSdk.ptzStop(user, pass, service, token, PTZType.PTZ_MOVE);
				timer.cancel();
			}
		}
	}

	/*username uri password 拼接,得到可以认证的RTSP视频流地址*/
	public String getAuthUri(String username, String password, String uri){
		String authUri = "";
		String uris[] = uri.split("//");
		authUri = uris[0]+"//"+username+":"+password+"@"+uris[1];
		return authUri;
	}
	
	@Override
	public void play(int position,VideoView vv) {
		LocalSetting localSet = this.getLocalSetting();
		String username = onvfData.getCurrentCameras().get(position).getUsername();
		String password = onvfData.getCurrentCameras().get(position).getPassword();
		String uri = "";
		if(localSet.isMainStream())
			uri = onvfData.getCurrentCameras().get(position).getStreamUri().get(0).getStreamURI();
		else
			uri = onvfData.getCurrentCameras().get(position).getStreamUri().get(1).getStreamURI();
		String autuUri = getAuthUri(username, password, uri);
		vv.setBackgroundColor(Color.TRANSPARENT);
		vv.setVideoPath(autuUri);
		vv.requestFocus();
		vv.start();
		onvfData.getGridsItemList().get(position).setStatus(1);
	}
}
