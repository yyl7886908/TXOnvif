package com.taixin.android.onvif.app.listener;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageButton;

public class ButtonOnFocusListener implements OnFocusChangeListener {

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		ImageButton image = (ImageButton)v;
		if(hasFocus){
			image.getDrawable().setAlpha(255);//设置图片透明度0~255，0完全透明，255不透明
		}else{
			image.getDrawable().setAlpha(150);//还原图片
		}
	}

}
