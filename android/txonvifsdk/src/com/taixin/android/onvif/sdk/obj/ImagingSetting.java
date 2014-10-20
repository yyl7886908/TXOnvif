package com.taixin.android.onvif.sdk.obj;

public class ImagingSetting {
	
	float brightness;             /* 亮度 */
	float colorSaturation;     	 /* 色度 */
	float contrast;               /* 对比度 */
	
	public ImagingSetting(){
		
	}
	
	public ImagingSetting(float brightness, float colorSaturation, float contrast){
		this.brightness = brightness;
		this.colorSaturation = colorSaturation;
		this.contrast = contrast;
	}
	public float getBrightness() {
		return brightness;
	}
	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}
	public float getColorSaturation() {
		return colorSaturation;
	}
	public void setColorSaturation(float colorSaturation) {
		this.colorSaturation = colorSaturation;
	}
	public float getContrast() {
		return contrast;
	}
	public void setContrast(float contrast) {
		this.contrast = contrast;
	} 
	 
	 
}
