package com.taixin.android.onvif.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.taixin.android.onvif.app.R;
import com.taixin.android.onvif.app.data.LocalSetting;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;

public class LocalSettingFragment extends Fragment {
	private IOnvifManager onvifMgr;
	private ImageButton saveBtn;
	//private Button saveBtn;
	private RadioGroup modeGroup;
	private RadioGroup streamGroup;
	private CheckBox autoPlayBox;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.local_setting_fragment, container, false);  
		onvifMgr = OnvifManager.getInstance();
		saveBtn = (ImageButton) v.findViewById(R.id.local_setting_save_button);
		modeGroup = (RadioGroup) v.findViewById(R.id.local_setting_mode_group);
		streamGroup = (RadioGroup) v.findViewById(R.id.local_setting_streams_group);
		autoPlayBox = (CheckBox) v.findViewById(R.id.local_setting_auto_play_cb);
		ButtonOnClickListener listener = new ButtonOnClickListener();
		saveBtn.setOnClickListener(listener);
		return v;
	}

	class ButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == saveBtn){
				LocalSetting localSet = new LocalSetting();
				if(modeGroup.getCheckedRadioButtonId() == R.id.local_setting_actual_mode)
					localSet.setActual(true);
				else
					localSet.setActual(false);
				if(autoPlayBox.isChecked())
					localSet.setAutoPlay(true);
				else
					localSet.setAutoPlay(true);
				if(streamGroup.getCheckedRadioButtonId() == R.id.local_setting_main_stream)
					localSet.setMainStream(true);
				else
					localSet.setMainStream(false);
				if(onvifMgr.setLocalSetting(localSet)){
					System.out.println("保存成功");
					Toast.makeText(getActivity(),"保存成功", Toast.LENGTH_SHORT).show();
				}else{
					System.out.println("保存失败");
				}
			}
		}

	}

}
