package com.taixin.android.onvif.app.data;

public class LocalSetting {
	/*自动播放*/
	private boolean autoPlay;
	/*是否实时模式（高清）,否代表流畅模式（一般）*/
	private boolean isActual;
	/*是否是主码流，否代表子码流*/
	private boolean isMainStream;
	public boolean isAutoPlay() {
		return autoPlay;
	}
	public void setAutoPlay(boolean autoPlay) {
		this.autoPlay = autoPlay;
	}
	public boolean isActual() {
		return isActual;
	}
	public void setActual(boolean isActual) {
		this.isActual = isActual;
	}
	public boolean isMainStream() {
		return isMainStream;
	}
	public void setMainStream(boolean isMainStream) {
		this.isMainStream = isMainStream;
	}
	
}
