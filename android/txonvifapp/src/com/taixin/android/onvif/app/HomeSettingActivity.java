package com.taixin.android.onvif.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.taixin.android.onvif.app.listener.ButtonOnFocusListener;

public class HomeSettingActivity extends FragmentActivity {
	private Fragment[] mFragments;  
	private FragmentManager fragmentManager;  
	private FragmentTransaction fragmentTransaction;  
	private ImageButton localSettingBtn, localPhotoBtn, localVideoBtn;  

	@Override
	protected void onCreate(Bundle bundle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(bundle);
		setTheme(R.style.Home_Setting_Style);  
		setContentView(R.layout.home_setting);
		intFragments();
	}

	private void intFragments(){
		mFragments = new Fragment[3];  
		fragmentManager = getSupportFragmentManager();  
		mFragments[0] = fragmentManager.findFragmentById(R.id.fragement_videos);  
		mFragments[1] = fragmentManager.findFragmentById(R.id.fragement_photos);  
		mFragments[2] = fragmentManager.findFragmentById(R.id.fragement_setting);  
		fragmentTransaction = fragmentManager.beginTransaction()  
				.hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]);  
		fragmentTransaction.show(mFragments[0]).commit();  
		setFragmentIndicator();  
	}

	private void setFragmentIndicator() {  
		localSettingBtn= (ImageButton) findViewById(R.id.local_setting); 
		localPhotoBtn= (ImageButton) findViewById(R.id.local_photos);
		localVideoBtn= (ImageButton) findViewById(R.id.local_videos);
		myOnClickListener listener = new myOnClickListener();
		localSettingBtn.setOnClickListener(listener);
		localPhotoBtn.setOnClickListener(listener);
		localVideoBtn.setOnClickListener(listener);
	}  
	/*ImageButton点击事件*/
	class myOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			fragmentTransaction = fragmentManager.beginTransaction()  
					.hide(mFragments[0]).hide(mFragments[1])  
					.hide(mFragments[2]);
			if(v == localVideoBtn)
				fragmentTransaction.show(mFragments[0]).commit();
			else if(v == localPhotoBtn)
				fragmentTransaction.show(mFragments[1]).commit();
			else if(v == localSettingBtn)
				fragmentTransaction.show(mFragments[2]).commit();
		}
	}
}