package com.hdj.downapp_market;


import com.hdj.downapp.util.GlobalConstants;
import com.mz.utils.EasyOperateClickUtil;
import com.mz.utils.ActivityUtils;
import com.mz.utils.SprefUtil;
import com.mz.utils.VpnUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HookBroadcast extends BroadcastReceiver {
	boolean market = true;
	@Override
	public void onReceive(final Context context, Intent intent) {
		boolean autoMarket=SprefUtil.getCountBool(context,SprefUtil.C_AUTO_MARKET, true);
		
		if (intent.getAction().equals(GlobalConstants.HDJ_GIT_BROADCAST)) {
			System.out.println("TIME!" + System.currentTimeMillis()+autoMarket+"  "+isNetWorkConneted(context));
			if(!autoMarket) return;
			int count = SprefUtil.getCountInt(context, SprefUtil.C_COUNT, 0);
//			if (!isNetWorkConneted(context)) {
//				EasyOperateClickUtil.setEasyTag(context,EasyOperateClickUtil.UNCLICK);
//				ActivityUtils.openAPK(context, GlobalConstants.HDJ_GIT_PN);
//			} else {
				SprefUtil.putCountInt(context, SprefUtil.C_COUNT, count + 1);
				MainActivity.handleDownload();
				/*
				 * if(VpnUtil.isVpnConnect()){
				 * preferences.edit().putInt("count", count+1).commit();
				 * MainActivity.handleDownload(); } else {
				 * OpenOtherAPKUtil.openAPK2(context, "com.hdjad.github"); }
				 */
//			}
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(market){
						String top=ActivityUtils.getLauncherTopApp(context);//getTopActivity(context);
//						System.out.println("----------"+top);
						if(top.equals(GlobalConstants.INSTALL_ACTIVITY)/*||top.equals(GlobalConstants.INSTALL_MIUI)*/){
							EasyOperateClickUtil.setEasyTag(context,EasyOperateClickUtil.UNCLICK);
							int flag=-1;
							while((flag=EasyOperateClickUtil.getEasyTag(context))!=EasyOperateClickUtil.UNCLICK){
								EasyOperateClickUtil.setEasyTag(context,EasyOperateClickUtil.UNCLICK);
							}
							int waitTime=SprefUtil.getCountInt(context, SprefUtil.C_INSTALL_TIME, 0);
							System.out.println("ActivityUtils========1111========="+market+"===="+waitTime);
							try{
								Thread.sleep(waitTime*1000);
							}catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(SprefUtil.getCountBool(context, SprefUtil.C_AUTO_HOOK, true))
							{
								ActivityUtils.openAPK(context, GlobalConstants.HDJ_GIT_PN);
								market = false;
							}
							
						}try{
							Thread.sleep(2000);
						}catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();
			
		}

	}

	

	private boolean isNetWorkConneted(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null)
			return false;
		NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
		if (networkInfos == null || networkInfos.length <= 0)
			return false;
		for (int i = 0; i < networkInfos.length; i++) {
			if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

}
