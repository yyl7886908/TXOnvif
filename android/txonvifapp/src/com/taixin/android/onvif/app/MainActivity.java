//package com.taixin.android.onvif.app;
//
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.Toast;
//
//import com.taixin.android.onvif.app.data.CameraData;
//import com.taixin.android.onvif.app.data.DataManager;
//import com.taixin.android.onvif.app.data.OnvifData;
//import com.taixin.android.onvif.sdk.SimpleOnvif;
//import com.taixin.android.onvif.sdk.TXOnvif;
//import com.taixin.android.onvif.sdk.obj.Device;
//import com.taixin.android.onvif.sdk.obj.DeviceCapability;
//import com.taixin.android.onvif.sdk.obj.ImagingSetting;
//import com.taixin.android.onvif.sdk.obj.MediaProfilesInfo;
//import com.taixin.android.onvif.sdk.obj.MediaStreamUri;
//import com.taixin.android.onvif.sdk.obj.PTZType;
//import com.taixin.ffmpeg.widget.VideoView;
//
//public class MainActivity extends Activity {
//
//	private DataManager dataMgr = DataManager.getInstance();
//	ArrayList<CameraData> cameras = null;
//	OnvifData onvifData = null;
//	private Button searchBTN;
//	private Button capaBTN;
//	private Button proBTN;
//	private Button streamUriBTN;
//	private Button playBTN;
//	private Button changeBTN;
//	private Button leftBTN;
//	private Button rightBTN;
//	private Button upBTN;
//	private Button downBTN;
//	private Button leftCruiseBTN;
//	private Button rightCruiseBTN;
//	private Button stopBTN;
//	private Button zoomFarBTN;
//	private Button zoomNearBTN;
//	private Button image1BTN;
//	private Button image2BTN;
//	private Button image3BTN;
//	private Button image4BTN;
//	private Button image5BTN;
//	private SeekBar s1;
//	private SeekBar s2;
//	private SeekBar s3;
//
//
//	private SimpleOnvif onvif;
//	private Context context;
//	private ArrayList<Device> deviceList;
//	private String deviceService;
//	private String mediaService;
//	private String ptzService;
//	private String imagingService;
//	private String[] rtspURI;
//	private VideoView vv;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		onvif = new TXOnvif();
//		initUI();
//	}
//
//	private void initUI(){
//		vv = (VideoView)findViewById(R.id.video1);
//		searchBTN = (Button)findViewById(R.id.button1);		
//		capaBTN = (Button)findViewById(R.id.button2);
//		proBTN = (Button)findViewById(R.id.button_profile);
//		streamUriBTN = (Button)findViewById(R.id.button3);
//		playBTN = (Button)findViewById(R.id.button4);
//		changeBTN = (Button)findViewById(R.id.button5);
//		upBTN = (Button)findViewById(R.id.buttonup);
//		downBTN = (Button)findViewById(R.id.buttondown);
//		leftBTN = (Button)findViewById(R.id.buttonleft);
//		rightBTN = (Button)findViewById(R.id.buttonright);
//		leftCruiseBTN = (Button)findViewById(R.id.buttonleftcruise);
//		rightCruiseBTN = (Button)findViewById(R.id.buttonrightcruise);
//		stopBTN = (Button)findViewById(R.id.buttonstop);
//		zoomFarBTN = (Button)findViewById(R.id.buttonzoomfar);
//		zoomNearBTN = (Button)findViewById(R.id.buttonzoomnear);
//		image1BTN = (Button)findViewById(R.id.buttonimage1);
//		image2BTN = (Button)findViewById(R.id.buttonimage2);
//		image3BTN = (Button)findViewById(R.id.buttonimage3);
//		image4BTN = (Button)findViewById(R.id.buttonimage4);
//		image5BTN = (Button)findViewById(R.id.buttonimage5);
//		s1 = (SeekBar)findViewById(R.id.seekbar1);
//		s2 = (SeekBar)findViewById(R.id.seekbar2);
//		s3 = (SeekBar)findViewById(R.id.seekbar3);
//		MySeekBarChangeListerner seekBarLisener = new MySeekBarChangeListerner();
//		s1.setOnSeekBarChangeListener(seekBarLisener);
//		s2.setOnSeekBarChangeListener(seekBarLisener);
//		s3.setOnSeekBarChangeListener(seekBarLisener);
//
//
//		MyButtonListener listener = new MyButtonListener();
//		searchBTN.setOnClickListener(listener);
//		capaBTN.setOnClickListener(listener);
//		proBTN.setOnClickListener(listener);
//		streamUriBTN.setOnClickListener(listener);
//		playBTN.setOnClickListener(listener);
//		changeBTN.setOnClickListener(listener);
//		upBTN.setOnClickListener(listener);
//		downBTN.setOnClickListener(listener);
//		leftBTN.setOnClickListener(listener);
//		rightBTN.setOnClickListener(listener);
//		stopBTN.setOnClickListener(listener);
//		leftCruiseBTN.setOnClickListener(listener);
//		rightCruiseBTN.setOnClickListener(listener);
//		zoomFarBTN.setOnClickListener(listener);
//		zoomNearBTN.setOnClickListener(listener);
//		image1BTN.setOnClickListener(listener);
//		image2BTN.setOnClickListener(listener);
//		image3BTN.setOnClickListener(listener);
//		image4BTN.setOnClickListener(listener);
//		image5BTN.setOnClickListener(listener);
//
//		searchBTN.requestFocus();
//	}
//
//	private String compoundURI(String tmpuri){
//		String uri = "";
//		String uris[] = tmpuri.split("//");
//		System.out.println("uri0 = "+ uris[0]);
//		System.out.println("uri1 = "+ uris[1]);
//		uri = uris[0]+"//admin:12345@"+uris[1];
//		System.out.println("uri = " + uri);
//		return uri;
//	}
//
//	private void initData(ArrayList<Device> deviceList){
//		cameras = new ArrayList<CameraData>();
//		for(int i = 0; i < deviceList.size(); i++){
//			CameraData camera = new CameraData();
//			camera.setDevice(deviceList.get(i));
//			cameras.add(camera);
//			onvifData = new OnvifData();
//			onvifData.setCameras(cameras);
//			dataMgr.setOnvifData(onvifData);
//		}
//		System.out.println("data = " + dataMgr.getOnvifData().getCameras().get(0).getDevice().getXAddrs());
//	}
//
//	public void setTxt(ImagingSetting imgSetting){
//		s1.setProgress((int) imgSetting.getBrightness()/10);
//		s2.setProgress((int) imgSetting.getColorSaturation()/10);
//		s3.setProgress((int) imgSetting.getContrast()/10);
//	}
//	class MySeekBarChangeListerner implements OnSeekBarChangeListener{
//
//		@Override
//		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//			if(seekBar == s1){
//				System.out.println("s1 change progress = "+progress);
//			}else if(seekBar == s2){
//				System.out.println("s2 change progress = "+progress);
//			}else if(seekBar == s3){
//				System.out.println("s2 change progress = "+progress);
//			}
//		}
//
//		@Override
//		public void onStartTrackingTouch(SeekBar seekBar) {
//			// TODO 自动生成的方法存根
//
//		}
//
//		@Override
//		public void onStopTrackingTouch(SeekBar seekBar) {
//			// TODO 自动生成的方法存根
//
//		}
//
//	}
//	class MyButtonListener implements OnClickListener{
//		@Override
//		public void onClick(View v) {
//			if(v == searchBTN){
//				System.out.println("search button clicked");
//				deviceList = onvif.discoverDevices();
//				initData(deviceList);
//				Toast toast = Toast.makeText(context, "find  "+deviceList.size()+"  devices!!", Toast.LENGTH_LONG);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
//			}else if(v == capaBTN){
//				System.out.println("capabilities button is clicked");
//				if(deviceList.size() != 0){
//					String a[] = deviceList.get(0).getXAddrs().split(" ");
//					deviceService = a[0];
//					DeviceCapability capa = onvif.getDeviceCapabilities("admin", "12345", deviceService);
//					mediaService = capa.getMediaService();
//					dataMgr.getOnvifData().getCameras().get(0).setCapability(capa);
//				}
//			}else if(v == proBTN){
//				System.out.println("profiles button is clicked");
//				ArrayList<MediaProfilesInfo> profiles = onvif.getMediaProfiles("admin", "12345", mediaService);
//				dataMgr.getOnvifData().getCameras().get(0).setProfiles(profiles);
//				System.out.println("profile token = "+dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken());
//			}else if(v == streamUriBTN){
//				System.out.println("stream uri button is clicked");
//				ArrayList<MediaStreamUri> streamUri= onvif.getMediaStreamUri("admin", "12345", deviceService);
//				dataMgr.getOnvifData().getCameras().get(0).setStreamUri(streamUri);
//				System.out.println("streamUri size = "+streamUri.size());
//				rtspURI = new String[streamUri.size()];
//				int i = 0;
//				for(MediaStreamUri stream : streamUri){
//					System.out.println("stream name = "+stream.getName());
//					System.out.println("stream uri = "+stream.getStreamURI());
//					rtspURI[i] = stream.getStreamURI();
//					i++;
//				}
//			}else if(v == playBTN){
//				if(rtspURI[0] != null){
//					String uri = compoundURI(rtspURI[0]);
//					vv.setVideoURI(Uri.parse(uri));   
//					vv.start();
//				}				
//			}else if(v == changeBTN){
//				if(rtspURI[1] != null){
//					System.out.println("channel 2 = "+rtspURI[1]);
//					String uri = compoundURI(rtspURI[1]);
//					vv.setVideoURI(Uri.parse(uri)); 
//					vv.start();
//				}				
//			}
//			else if(v == stopBTN){
//				System.out.println("play button is clicked");
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				String profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				int ret = onvif.ptzStop("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE);
//				System.out.println("ptz stop ret = "+ret);
//			}else if(v == upBTN){
//				System.out.println("up button is clicked");
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				String profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				int ret = onvif.ptzContinuousMove("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE, 0,(float)  0.8, 0);
//				System.out.println("ret = "+ret);
//				try {
//					Thread.sleep(300);
//					onvif.ptzStop("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}else if(v == downBTN){
//				System.out.println("downBTN button is clicked");
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				String profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				int ret = onvif.ptzContinuousMove("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE, 0, (float) -0.8, 0);
//				System.out.println("ret = "+ret);
//				try {
//					Thread.sleep(300);
//					onvif.ptzStop("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}else if(v == leftBTN){
//				System.out.println("downBTN button is clicked");
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				String profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				int ret = onvif.ptzContinuousMove("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE, (float)-0.8, 0, 0);
//				System.out.println("ret = "+ret);
//				try {
//					Thread.sleep(500);
//					onvif.ptzStop("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}else if(v == rightBTN){
//				System.out.println("right button is clicked");
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				String profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				int ret = onvif.ptzContinuousMove("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE, (float)0.80, 0, 0);
//				System.out.println("ret = "+ret);
//				try {
//					Thread.sleep(500);
//					onvif.ptzStop("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}else if(v == leftCruiseBTN){
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				String profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				onvif.ptzStop("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE);
//
//				System.out.println("leftCuriseBTN button is clicked");
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				int ret = onvif.ptzContinuousMove("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE, (float)0.8, 0, 0);
//				System.out.println("ret = "+ret);
//			}else if(v == rightCruiseBTN){
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				String profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				onvif.ptzStop("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE);
//
//				System.out.println("rightCruiseBTN button is clicked");
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				int ret = onvif.ptzContinuousMove("admin", "12345", ptzService, profileToken, PTZType.PTZ_MOVE, (float)-0.8, 0, 0);
//				System.out.println("ret = "+ret);
//			}else if(v == zoomFarBTN){
//				System.out.println("zoomFarBTN button is clicked");
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				String profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				int ret = onvif.ptzContinuousMove("admin", "12345", ptzService, profileToken, PTZType.PTZ_ZOOM, 0, 0, (float)0.8);
//				System.out.println("ret = "+ret);
//			}else if(v == zoomNearBTN){
//				System.out.println("zoomNearBTN button is clicked");
//				ptzService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getPtzService();
//				String profileToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getToken();
//				int ret = onvif.ptzContinuousMove("admin", "12345", ptzService, profileToken, PTZType.PTZ_ZOOM, 0, 0,(float)-0.8);
//				System.out.println("ret = "+ret);
//			}else if(v == image1BTN){
//
//			}else if(v == image2BTN){
//
//			}else if(v == image3BTN){
//
//			}else if(v == image4BTN){
//				System.out.println("image4BTN button is clicked");
//				imagingService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getImagingService();
//				String videoSourceToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getVideoSourceToken();
//				ImagingSetting imgSetting = onvif.getImagingSetting("admin", "12345", imagingService, videoSourceToken);
//				System.out.println("brightness = "+imgSetting.getBrightness());
//				setTxt(imgSetting);
//			}else if(v == image5BTN){
//				System.out.println("image5BTN button is clicked");
//				imagingService = dataMgr.getOnvifData().getCameras().get(0).getCapability().getImagingService();
//				String videoSourceToken = dataMgr.getOnvifData().getCameras().get(0).getProfiles().get(0).getVideoSourceToken();
//				ImagingSetting imgSetting = new ImagingSetting((float)s1.getProgress()*10,(float)s2.getProgress()*10, (float)s3.getProgress()*10);
//				int ret = onvif.setImagingSetting("admin", "12345", imagingService, videoSourceToken, imgSetting);
//				if(ret == 0){
//					Toast toast = Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_LONG);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.show();
//				}	
//			}
//		}
//	}
//}
