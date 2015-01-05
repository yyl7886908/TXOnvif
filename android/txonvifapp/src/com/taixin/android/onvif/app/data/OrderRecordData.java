package com.taixin.android.onvif.app.data;

import java.io.Serializable;

public class OrderRecordData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String uuid;
	private int itemIndex;
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	private String startTime;/*开始时间*/
	private int duration;/*时长*/
	private String endTime;/*结束时间*/
	private boolean isZhouyiCheck;
	private boolean isZhouerCheck;
	private boolean isZhousanCheck;
	private boolean isZhousiCheck;
	private boolean isZhouwuCheck;
	private boolean isZhouliuCheck;
	private boolean isZhouqiCheck;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public boolean isZhouyiCheck() {
		return isZhouyiCheck;
	}
	public void setZhouyiCheck(boolean isZhouyiCheck) {
		this.isZhouyiCheck = isZhouyiCheck;
	}
	public boolean isZhouerCheck() {
		return isZhouerCheck;
	}
	public void setZhouerCheck(boolean isZhouerCheck) {
		this.isZhouerCheck = isZhouerCheck;
	}
	public boolean isZhousanCheck() {
		return isZhousanCheck;
	}
	public void setZhousanCheck(boolean isZhousanCheck) {
		this.isZhousanCheck = isZhousanCheck;
	}
	public boolean isZhousiCheck() {
		return isZhousiCheck;
	}
	public void setZhousiCheck(boolean isZhousiCheck) {
		this.isZhousiCheck = isZhousiCheck;
	}
	public boolean isZhouwuCheck() {
		return isZhouwuCheck;
	}
	public void setZhouwuCheck(boolean isZhouwuCheck) {
		this.isZhouwuCheck = isZhouwuCheck;
	}
	public boolean isZhouliuCheck() {
		return isZhouliuCheck;
	}
	public void setZhouliuCheck(boolean isZhouliuCheck) {
		this.isZhouliuCheck = isZhouliuCheck;
	}
	public boolean isZhouqiCheck() {
		return isZhouqiCheck;
	}
	public void setZhouqiCheck(boolean isZhouqiCheck) {
		this.isZhouqiCheck = isZhouqiCheck;
	}
	
	
}
