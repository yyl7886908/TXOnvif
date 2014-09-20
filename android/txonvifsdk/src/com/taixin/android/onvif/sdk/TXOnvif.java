package com.taixin.android.onvif.sdk;


public class TXOnvif implements SimpleOnvif{
		
	@Override
	public void discoverDevices() {
		 String var = _discoverDevices();
		 System.out.println("java back var = "+var);
	}
	/**
	 * discover devices
	 */
	public static native String _discoverDevices();
	
	static{
		try{  
			System.loadLibrary("txonvif-jni");  
			System.loadLibrary("txonvif");
		} catch (UnsatisfiedLinkError ule) {  
			System.err.println("WARNING: Could not load library!");  
        }  
	}
}
