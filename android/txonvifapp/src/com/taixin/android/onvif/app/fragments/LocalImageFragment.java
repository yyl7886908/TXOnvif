package com.taixin.android.onvif.app.fragments;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.taixin.android.onvif.app.R;
import com.taixin.android.onvif.app.logic.IOnvifManager;
import com.taixin.android.onvif.app.logic.OnvifManager;
import com.taixin.android.onvif.app.util.Usb;

public class LocalImageFragment extends Fragment {
	private IOnvifManager onvifMgr;
	private GridView imageGrid;
	private ImageGridAdapter imageGridAdapter;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		System.out.println("=======local image fragment on onActivityCreated!!!!!!");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("=======local image fragment on onCreateView!!!!!!");
		View v = inflater.inflate(R.layout.local_image_fragment, container, false);  
		//initData(v);
		return v;
	}
	
	@Override
	public void onResume() {
		System.out.println("===========local image fragment set onResume");
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		System.out.println("===========local image fragment onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		System.out.println("===========local image fragment set user visible hint");
		super.setUserVisibleHint(isVisibleToUser);
	}

	public void initData(View v){
		onvifMgr = OnvifManager.getInstance();
		List<String> usbList = Usb.getUsbDirList();
		System.out.println("usb list size = "+usbList.size());
		for(String dir:usbList){
			System.out.println("dir ===================="+dir);
		}
		imageGrid = (GridView) v.findViewById(R.id.local_images_grid);
		imageGridAdapter = new ImageGridAdapter(this.getActivity().getApplicationContext(), 5);
		imageGrid.setAdapter(imageGridAdapter);
		
	}
	
	
}
