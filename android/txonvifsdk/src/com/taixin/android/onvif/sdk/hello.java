package com.taixin.android.onvif.sdk;

public class hello {
	
	public String pHello(){
		System.out.println("before native sayHello");
		return sayhello();
	}
	
	public native String sayhello();

	static{
		try {  
            System.loadLibrary("hello");  
        } catch (UnsatisfiedLinkError ule) {  
            System.err.println("WARNING: Could not load library!");  
        }  
	}
}
