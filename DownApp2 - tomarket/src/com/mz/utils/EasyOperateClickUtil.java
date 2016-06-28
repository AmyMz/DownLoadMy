package com.mz.utils;

import android.content.Context;
import android.provider.Settings.System;

public class EasyOperateClickUtil {
	public static final int UNCLICK = 1;//一键操作按钮是否点击了
	public static final int CILCKED = 2;
	public static final String EASY_OPERATION_CLICK_DTATUS = "easy_operate_state_tag";
	public static final int HALF = 1; 
	public static final int NO_HALF = 2;
	public static final String HALF_DOWNLOAD_FLAG = "hanlf_download_flag";//是否要部分下载
	public static final int HALF_VALUE = 100; //默认部分下载值为100
	public static final String HALF_DOWNLOAD_VALUE = "hanlf_download_value";//部分下载的值
	
	public static final String IS_AUTOCLICK_FLAT ="market_auto_click_download_flag";
	public static final int  AUTOCLICK=1;
	public static final int  DO_NOT_AUTOCLICK=2;
	

	public static boolean  setEasyTag(Context context, int value) {
		return	System.putInt(context.getContentResolver(),EASY_OPERATION_CLICK_DTATUS, value);
	}
	public static int getEasyTag(Context context) {
		synchronized (EASY_OPERATION_CLICK_DTATUS) {
			return System.getInt(context.getContentResolver(),EASY_OPERATION_CLICK_DTATUS, UNCLICK);
		}
	}
	public static void setBaiduHalfFlag(Context context, int value) {
		synchronized (HALF_DOWNLOAD_FLAG) {
			System.putInt(context.getContentResolver(),HALF_DOWNLOAD_FLAG, value);
		}
	}
	public static int getBaiduHalfFlag(Context context) {
		synchronized (HALF_DOWNLOAD_FLAG) {
			return System.getInt(context.getContentResolver(),HALF_DOWNLOAD_FLAG, NO_HALF);
		}
	}
	public static void setBaiDuHalfDownloadValue(Context context, int value) {
		synchronized (HALF_DOWNLOAD_VALUE) {
			System.putInt(context.getContentResolver(),HALF_DOWNLOAD_VALUE, value);
		}
	}
	public static int getBaiduHalfDownloadValue(Context context) {
		synchronized (HALF_DOWNLOAD_VALUE) {
			return System.getInt(context.getContentResolver(),HALF_DOWNLOAD_VALUE, HALF_VALUE);
		}
	}
	public static int getAutoClickFlag(Context context) {
		return System.getInt(context.getContentResolver(),IS_AUTOCLICK_FLAT, DO_NOT_AUTOCLICK);
	}
	public static void setAutoClickFlag(Context context,int value) {
		 System.putInt(context.getContentResolver(), IS_AUTOCLICK_FLAT, value);
	}


}
