package com.taixin.android.onvif.app;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.taixin.android.onvif.app.data.LocalCamera;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.util.SerializableUtil;

public class LoginActivity extends Activity {

	private EditText userName, password;  
	private CheckBox rem_pw, auto_login;  
	private Button btn_login;    
	private String userNameValue,passwordValue;  
	private String defaultUser = "admin";
	private String defaultPass = "12345";
	private int position;
	private IOnvifManager onvifMgr;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.camera_login);
		onvifMgr = OnvifManager.getInstance();
		Bundle extras = getIntent().getExtras(); 
		position = extras.getInt("grid_item_position");
		displayView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void displayView(){
		userName = (EditText) findViewById(R.id.et_zh);  
		password = (EditText) findViewById(R.id.et_mima);  
		rem_pw = (CheckBox) findViewById(R.id.cb_mima);  
		auto_login = (CheckBox) findViewById(R.id.cb_auto);  
		btn_login = (Button) findViewById(R.id.btn_login); 
		btn_login.setFocusable(true);
		// 登录监听事件  现在默认为用户名为：admin 密码：12345  
		btn_login.setOnClickListener(new OnClickListener() {  
			public void onClick(View v) {  
				userNameValue = userName.getText().toString();  
				if(userNameValue == null || userNameValue.length()<=0)
					userNameValue = defaultUser;
				passwordValue = password.getText().toString();  
				if(passwordValue == null || passwordValue.length() <= 0)
					passwordValue = defaultPass;
				//登录成功和记住密码框为选中状态才保存用户信息  
				String deviceService = onvifMgr.getOnvifData().getCurrentCameras().get(position).getDevice().getDeviceService();
				boolean isGetCapa = onvifMgr.getDeviceCapabilities(userNameValue, passwordValue, deviceService);
				boolean auth = onvifMgr.getMediaStreamUri(userNameValue, passwordValue, deviceService);
				Toast toast;
				if(isGetCapa && auth){
					//onvifMgr.getOnvifData().getGridsItemList().get(position).setAuth(true);
					onvifMgr.getOnvifData().getCurrentCameras().get(position).setAuth(true);
					onvifMgr.getOnvifData().getCurrentCameras().get(position).setUsername(userNameValue);
					onvifMgr.getOnvifData().getCurrentCameras().get(position).setPassword(passwordValue);
					Toast.makeText(LoginActivity.this,"登录成功", Toast.LENGTH_LONG).show(); 
					//storageLocalData();
					String uuid = onvifMgr.getOnvifData().getCurrentCameras().get(position).getDevice().getUuid();
					LocalCamera lCamera = new LocalCamera();
					lCamera.setUuid(uuid);
					lCamera.setPassword(passwordValue);
					lCamera.setUsername(userNameValue);
					if(onvifMgr.saveNewCameraToLocal(lCamera)){
						toast = Toast.makeText(LoginActivity.this,"保存成功", Toast.LENGTH_LONG); 
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					else{
						toast = Toast.makeText(LoginActivity.this,"保存失败", Toast.LENGTH_LONG); 
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					finish();
				}else{
					toast = Toast.makeText(LoginActivity.this,"用户名或密码错误，请重新登录", Toast.LENGTH_LONG); 
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}  
		});  

		//监听记住密码多选框按钮事件  
		rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
				if (rem_pw.isChecked()) {  
					System.out.println("记住密码已选中");   
				}else {  
					System.out.println("记住密码没有选中");  
				}  

			}  
		});  

		//监听自动登录多选框事件  
		auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {  
				if (auto_login.isChecked()) {  
					System.out.println("自动登录已选中");  
				} else {  
					System.out.println("自动登录没有选中"); 
				}  
			}  
		});  
	}

	/*将用户名密码存储本地*/
	public void storageLocalData(){
		String uuid = onvifMgr.getOnvifData().getCurrentCameras().get(position).getDevice().getUuid();
		LocalCamera lCamera = new LocalCamera();
		lCamera.setUuid(uuid);
		lCamera.setPassword(passwordValue);
		lCamera.setUsername(userNameValue);
		ArrayList<LocalCamera> list = new ArrayList<LocalCamera>();
		list.add(lCamera);
		try {
			String str = SerializableUtil.list2String(list);
			SharedPreferences settings = getSharedPreferences("local_camera", 0);
			//2、让setting处于编辑状态
			SharedPreferences.Editor editor = settings.edit();
			//3、存放数据
			editor.putString("local_camera",str);
			//4、完成提交
			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
