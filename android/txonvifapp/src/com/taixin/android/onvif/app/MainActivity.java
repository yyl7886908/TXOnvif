package com.taixin.android.onvif.app;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.taixin.android.onvif.sdk.SimpleOnvif;
import com.taixin.android.onvif.sdk.TXOnvif;
import com.taixin.android.onvif.sdk.obj.Device;
import com.taixin.android.onvif.sdk.obj.DeviceCapability;
import com.taixin.android.onvif.sdk.obj.MediaStreamUri;

public class MainActivity extends Activity {

	
	private Button searchBTN;
	private Button capaBTN;
	private Button streamUriBTN;
	private Button playBTN;
	private Button leftBTN;
	private Button rightBTN;
	private Button upBTN;
	private Button downBTN;
	
	private SimpleOnvif onvif;
	private Context context;
	private ArrayList<Device> deviceList;
	private String deviceService;
	private String mediaService;
	private String[] rtspURI;
	private VideoView vv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this.getApplicationContext();
		onvif = new TXOnvif(context);
		initUI();
	}
	
	private void initUI(){
		vv = (VideoView)findViewById(R.id.video1);
		searchBTN = (Button)findViewById(R.id.button1);		
		capaBTN = (Button)findViewById(R.id.button2);
		streamUriBTN = (Button)findViewById(R.id.button3);
		playBTN = (Button)findViewById(R.id.button4);
		upBTN = (Button)findViewById(R.id.buttonup);
		downBTN = (Button)findViewById(R.id.buttondown);
		leftBTN = (Button)findViewById(R.id.buttonleft);
		rightBTN = (Button)findViewById(R.id.buttonright);
		
		MyButtonListener listener = new MyButtonListener();
		searchBTN.setOnClickListener(listener);
		capaBTN.setOnClickListener(listener);
		streamUriBTN.setOnClickListener(listener);
		playBTN.setOnClickListener(listener);
		upBTN.setOnClickListener(listener);
		downBTN.setOnClickListener(listener);
		leftBTN.setOnClickListener(listener);
		rightBTN.setOnClickListener(listener);
		
		searchBTN.requestFocus();
	}
	
	private String compoundURI(String tmpuri){
		String uri = "";
		String uris[] = tmpuri.split("//");
		System.out.println("uri0 = "+ uris[0]);
		System.out.println("uri1 = "+ uris[1]);
		uri = uris[0]+"//admin:12345@"+uris[1];
		System.out.println("uri = " + uri);
		return uri;
	}
	
	class MyButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == searchBTN){
				System.out.println("search button clicked");
				deviceList = onvif.discoverDevices();
				Toast toast = Toast.makeText(context, "find  "+deviceList.size()+"  devices!!", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}else if(v == capaBTN){
				System.out.println("capabilities button is clicked");
				if(deviceList.size() != 0){
					String a[] = deviceList.get(0).getXAddrs().split(" ");
					deviceService = a[0];
					System.out.println("deviceService = "+deviceService);
					DeviceCapability capa = onvif.getDeviceCapabilities("admin", "12345", deviceService);
					System.out.println("deviceService ="+capa.getDeviceService());
					System.out.println("mediaService ="+capa.getMediaService());
					mediaService = capa.getMediaService();
					System.out.println("ptzService ="+capa.getPtzService());
					System.out.println("imageService ="+capa.getImagingService());
					}
				}else if(v == streamUriBTN){
					System.out.println("stream uri button is clicked");
					ArrayList<MediaStreamUri> streamUri= onvif.getMediaStreamUri("admin", "12345", deviceService);
					System.out.println("streamUri size = "+streamUri.size());
					rtspURI = new String[streamUri.size()];
					int i = 0;
					for(MediaStreamUri stream : streamUri){
						System.out.println("stream name = "+stream.getName());
						System.out.println("stream uri = "+stream.getStreamURI());
						rtspURI[i] = stream.getStreamURI();
						i++;
					}
				}else if(v == playBTN){
					if(rtspURI[0] != null){
						System.out.println("play button is clicked");
						System.out.println("rtspURI[0] = "+rtspURI[0]);
						String uri = compoundURI(rtspURI[0]);
						vv.setVideoURI(Uri.parse(uri)); 
						vv.requestFocus();  
					  vv.start();
					}
					
				}
			}
		}
}
