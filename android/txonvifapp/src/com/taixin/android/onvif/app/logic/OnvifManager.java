package com.taixin.android.onvif.app.logic;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.taixin.android.onvif.app.data.CameraData;
import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.data.LocalSetting;
import com.taixin.android.onvif.app.data.OnvifData;
import com.taixin.android.onvif.app.data.OrderRecordModel;
import com.taixin.android.onvif.app.data.VideoInfo;
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
	private String localCameraFileName = "local_camera";/*本地存储摄像头信息列表的文件名*/
	private String localSettingFileName = "local_setting";/*本地存储设置信息的文件名*/
	private String previousCameraFileName = "local_previous_camera";/*本地存储上一次连接的摄像头信息文件名*/
	private String orderRecordModeFileName = "local_order_record_model";/*本地预约录制的对象保存文件*/
	private static OnvifManager instance;
	public static OnvifManager getInstance(){
		if(instance == null){
			instance = new OnvifManager();
		}
		return instance;
	}
	/*搜索设备监听器*/
	private searchDevicesListener listener;

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

	public searchDevicesListener getListener() {
		return listener;
	}

	public void setListener(searchDevicesListener listener) {
		this.listener = listener;
	}
	@Override
	public ArrayList<Device> discoverDevices() {
		ArrayList<Device> deviceList = onvifSdk.discoverDevices();
		onvfData.setDeviceList(deviceList);
		Log.i(TAG, "deviceList size = "+deviceList.size());
		ArrayList<CameraData> cameras = new ArrayList<CameraData>();
		for(int i = 0; i < deviceList.size(); i++){
			CameraData camera = new CameraData();
			camera.setDevice(deviceList.get(i));
			cameras.add(camera);
		}
		onvfData.setCameras(cameras);
		if(listener != null){  
			listener.searchEnd();
		}  
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
				/*鉴权成功，保存此次连接的摄像头信息*/
				LocalCamera lCamera = new LocalCamera();
				lCamera.setUsername(username);
				lCamera.setPassword(password);
				lCamera.setUuid(camera.getDevice().getUuid());
				savePreviousCameraInfo(lCamera);
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
		System.out.println("before get media stream uri = "+username+password+deviceService);
		ArrayList<MediaStreamUri> uris = onvifSdk.getMediaStreamUri(username, password, deviceService);
		System.out.println("after get media stream uri ");
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
	@Override
	public boolean savePreviousCameraInfo(LocalCamera camera) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(previousCameraFileName, Activity.MODE_PRIVATE); 
		String strtmp;
		try {
			strtmp = SerializableUtil.obj2Str(camera);
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			editor.putString("local_previous_camera",strtmp);
			editor.commit();
			System.out.println("save previous camera Info success!!!!");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public LocalCamera getPreviousCameraInfo() {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(previousCameraFileName, 
				Activity.MODE_PRIVATE); 
		String str = mySharedPreferences.getString("local_previous_camera", "default");
		if(!str.equals("default")){
			try {
				LocalCamera camera = (LocalCamera) SerializableUtil.str2Obj(str);
				System.out.println("getPreviousCameraInfo camera uuid username password = "+camera.getUuid()+camera.getUsername()+camera.getPassword());
				return camera;
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	@Override
	public CameraData checkPrevCameraIsSearched(LocalCamera camera){
		for(CameraData ca : onvfData.getCameras()){
			if(ca.getDevice().getUuid().equals(camera.getUuid())){
				ca.setUsername(camera.getUsername());
				ca.setPassword(camera.getPassword());
				return ca;
			}
		}
		return null;
	}
	/*获取本地存储的摄像头列表数据*/
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
		System.out.println("saveNewCameraToLocal!!!!!");
		SharedPreferences mySharedPreferences = context.getSharedPreferences(localCameraFileName, Activity.MODE_PRIVATE); 
		String str = mySharedPreferences.getString("local_camera", "default");
		if(str.equals("default")){
			/*没有相关的文件*/
			ArrayList<LocalCamera> lCameras = onvfData.getLocalCameras();
			/*新用户保存数据*/
			lCameras.add(camera);
			onvfData.setLocalCameras(lCameras);
			try {
				String strtmp = SerializableUtil.list2String(lCameras);
				SharedPreferences.Editor editor = mySharedPreferences.edit();
				editor.putString("local_camera",strtmp);
				editor.commit();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		vv.setVideoPath(autuUri);
		vv.requestFocus();
		vv.start();
		onvfData.getGridsItemList().get(position).setStatus(1);
	}
	@Override
	public String getAuthUriByPosition(int position) {
		LocalSetting localSet = this.getLocalSetting();
		String username = onvfData.getCurrentCameras().get(position).getUsername();
		String password = onvfData.getCurrentCameras().get(position).getPassword();
		String uri = "";
		if(localSet.isMainStream())
			uri = onvfData.getCurrentCameras().get(position).getStreamUri().get(0).getStreamURI();
		else
			uri = onvfData.getCurrentCameras().get(position).getStreamUri().get(1).getStreamURI();
		String authUri = getAuthUri(username, password, uri);
		return authUri;
	}
	
	@Override
	public List<HashMap<String, String>> getImages() {
		// 指定要查询的uri资源
		String path = "/storage/external_storage/sda1/";
		Uri uri = Uri.parse(path);
		// 获取ContentResolver
		ContentResolver contentResolver = context.getContentResolver();
		// 查询的字段
		String[] projection = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
		// 条件
		String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
		// 条件值(這裡的参数不是图片的格式，而是标准，所有不要改动)
		String[] selectionArgs = { "image/jpeg" };
		// 排序
		String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
		// 查询sd卡上的图片
		Cursor cursor = contentResolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		List<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
		if (cursor != null) {
			HashMap<String, String> imageMap = null;
			cursor.moveToFirst();
			while (cursor.moveToNext()) {
				imageMap = new HashMap<String, String>();
				// 获得图片的id
				imageMap.put("imageID", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media._ID)));
				// 获得图片显示的名称
				System.out.println("image name = "+cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
				imageMap.put("imageName", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
				// 获得图片的信息
				imageMap.put(
						"imageInfo",
						""
								+ cursor.getLong(cursor
										.getColumnIndex(MediaStore.Images.Media.SIZE) / 1024)
										+ "kb");
				// 获得图片所在的路径(可以使用路径构建URI)
				imageMap.put("data", cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA)));
				imageList.add(imageMap);
			}
			// 关闭cursor
			cursor.close();
		}
		System.out.println("imageList size = "+imageList.size());
		return imageList;
	}

	@Override
	public boolean saveOrderRecordModel(OrderRecordModel oRecord) {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(orderRecordModeFileName, Activity.MODE_PRIVATE); 
		String strtmp;
		try {
			strtmp = SerializableUtil.obj2Str(oRecord);
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			editor.putString("local_order_record_model",strtmp);
			editor.commit();
			System.out.println("save order_record_mode Info success!!!!");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public OrderRecordModel getOrderedRecordModel() {
		SharedPreferences mySharedPreferences = context.getSharedPreferences(orderRecordModeFileName, 
				Activity.MODE_PRIVATE); 
		String str = mySharedPreferences.getString("local_order_record_model", "default");
		if(!str.equals("default")){
			try {
				OrderRecordModel rMode = (OrderRecordModel) SerializableUtil.str2Obj(str);
				System.out.println("get local_order_record_model = "+rMode.getUuid());
				return rMode;
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public LocalCamera getLocalCameraByUUidForRecord(String uuid) {
		getLocalCameraData();
		for(LocalCamera lCamera : onvfData.getLocalCameras()){
			if(uuid.equals(lCamera.getUuid())){
				return lCamera;
			}
		}
		return null;
	}
	
	@Override
	public ArrayList<MediaStreamUri> getMediaStreamUriForRecord(String username, String password,
			String deviceService) {
		ArrayList<MediaStreamUri> uris = onvifSdk.getMediaStreamUri(username, password, deviceService);
		return uris;
	}

	

}
