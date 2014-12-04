package com.taixin.android.onvif.app.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.taixin.android.onvif.app.R;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;

public class LocalVideoFragment extends Fragment {
	/*视频文件存储路径*/
	private final String videoPath = "/storage/external_storage/sda1/DVBRecordFiles";
	private String dvrPath = "/DVBRecordFiles";	
	private ListView videoListView;
	private IOnvifManager onvifMgr;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		System.out.println("=======local video fragment onActivityCreated!!!!!!");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("=======local video fragment onCreateView!!!!!!");
		View v = inflater.inflate(R.layout.local_video_fragment, container, false);  
		initData();
		return v;
	}
	
	@Override
	public void onResume() {
		System.out.println("===========local video fragment set onResume");
		super.onResume();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		System.out.println("===========local video fragment set user visible hint");
		super.setUserVisibleHint(isVisibleToUser);
	}

	public void initData(){
		onvifMgr = OnvifManager.getInstance();
		//getCameraVideosFile();
	}

	
}
