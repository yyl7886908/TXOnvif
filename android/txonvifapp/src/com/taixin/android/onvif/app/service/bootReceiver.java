package com.taixin.android.onvif.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class bootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("TXOnvif app", "收到bootCompleted广播");
		Toast.makeText(context, "tx onvif 收到启动广播", Toast.LENGTH_LONG).show();
		/*启动wifi服务端*/
		Intent remoteService  = new Intent(context, OnvifService.class);
		context.startService(remoteService);
	}

}
