package com.taixin.android.onvif.app.util;

public class timeUtil {
	public static String formatTime(int time) {
		int hour = time / 3600;
		int minute = time % (60 * 60) / 60;
		int second = time % 60;

		return String.format("%02d:%02d:%02d", hour, minute, second);
		}
}
