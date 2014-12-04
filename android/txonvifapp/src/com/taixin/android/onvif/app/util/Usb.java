package com.taixin.android.onvif.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class Usb {
	private static String TAG = " Usb info :";
	private static List<Integer> usbList = new ArrayList<Integer>();
	private static ArrayList<String> paths = null;
	private static List<String> mDirList;
	
	public static List<String> getmDirList() {
		return mDirList;
	}

	private static boolean checkUsbexist(int number) {
		for (int j = 0; j < usbList.size(); j++) {
			if (number == usbList.get(j)) {
				return true;
			}

		}
		return false;
	}

	public static List<String> getUsbDirList() {
		mDirList = new ArrayList<String>();
		usbList.clear();
		
		try {
			FileInputStream fis = new FileInputStream("/proc/mounts");
			byte[] buf = new byte[256];
			String mountString = null;
			int len = 0;
			
			try {
				while ((len = fis.read(buf, 0, 256)) > 0) {
					System.out.println("len: " + len);
					mountString += new String(buf, 0, len);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			String[] strbuf = mountString.split("\n");
			int mountMessageLen = strbuf.length;

			for (int i = 0; i < mountMessageLen; i++) {
				// 碰到一个u盘为/mnt/sda4为/dev/block/vold/8:4
				if (strbuf[i].startsWith("/dev/block/vold/8:")) {
					int no = Integer.parseInt(strbuf[i].split(" ")[0]
							.split(":")[1]);
					if (!checkUsbexist(no)) {
						mDirList.add(strbuf[i].split(" ")[1]); // usb设备路径
						usbList.add(no);
					}
				} else if (strbuf[i].startsWith("/dev/block/vold/253:")) {
					int no = Integer.parseInt(strbuf[i].split(" ")[0]
							.split(":")[1]);
					if (!checkUsbexist(no)) {
						mDirList.add(strbuf[i].split(" ")[1]); // usb设备路径
						usbList.add(no);
					}
				}
			}
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		return mDirList;
	}

	public static String getFilePath(List<String> m_basePath) {

		paths = new ArrayList<String>();
		
		if (m_basePath == null || m_basePath.size() < 1
				|| m_basePath.get(0) == null) {
			Log.i(TAG, "------ m_basePath == null || m_basePath.size() < 1 -----");
			return "";
		}
		File file = new File(m_basePath.get(0));
		Log.i(TAG, "------ file = "+file+", m_basePath.get(0) = "+m_basePath.get(0));
			
		StringBuffer sb = new StringBuffer();
		sb.append("{\"check_free_space\":");
		if (!file.exists()) {
			return "";
		}
		StatFs allstatFs = new StatFs(file.getParent());
		Log.i(TAG,"parent path:" + file.getParent());
		
		long[] avalilablesize = new long[m_basePath.size()];
		long[] allsize = new long[m_basePath.size()];
		for (int i = 0; i < m_basePath.size(); i++) {
			File f = new File(m_basePath.get(i));
			if (f.isDirectory()) {
				paths.add(f.getPath());
				StatFs statFs = new StatFs(f.getPath());
				long blockSize = statFs.getBlockSize();
				long blocks = statFs.getAvailableBlocks();
				long allblocks = statFs.getBlockCount();
				long availableSpare = (blocks * blockSize);
				avalilablesize[i] = availableSpare;
				allsize[i] = allblocks * blockSize;
				
			}
		}
		long allAvaiSum = 0;
		long allsum = 0;
		for (int i = 0; i < avalilablesize.length; i++) {
			allAvaiSum += avalilablesize[i];
			allsum += allsize[i];
		}
		sb.append("{\"path\":\"" + file.getParent() + "\",\"left_space\":"
				+ allAvaiSum + ",\"all_space\":" + allsum + "}");
		sb.append("}");
		Log.i(TAG, "---path string ="+ sb.toString());
		return sb.toString();
	}

	public static boolean hasUsb(List<String> Path) {
		getFilePath(Path);
		if (paths.size() < 1) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean checkUsbMemory(long needSpace) {		
		StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
		long blockSize = statFs.getBlockSize();
		long blocks = statFs.getAvailableBlocks();
		long availableSpare = (blocks * blockSize)/1024;
		long Space = needSpace * 1024;
		Log.i("", "The freespace is:" + availableSpare + "KB");
		if (Space > availableSpare) {			
			return false;
		} else {			 
			return true;
		}
	}

}
