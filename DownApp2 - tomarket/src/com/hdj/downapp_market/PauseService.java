package com.hdj.downapp_market;

import java.util.List;

import com.hdj.downapp.util.GlobalConstants;
import com.mz.utils.EasyOperateClickUtil;
import com.mz.utils.ActivityUtils;
import com.mz.utils.SprefUtil;
import com.umeng.analytics.c;

import u.aly.co;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;

public class PauseService extends Service {
	int count = 0;
	String top = "", last = "";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("-------onStartCommand---" + count);
		checkTopActivity();
		showNotif();
		return START_STICKY;
	}
	private static final int NOTIFY_FAKEPLAYER_ID=1339;  
	void showNotif() {
		
		Intent i = new Intent(this,MainActivity.class);
        //注意Intent的flag设置：FLAG_ACTIVITY_CLEAR_TOP: 如果activity已在当前任务中运行，在它前端的activity都会被关闭，它就成了最前端的activity。FLAG_ACTIVITY_SINGLE_TOP: 如果activity已经在最前端运行，则不需要再加载。设置这两个flag，就是让一个且唯一的一个activity（服务界面）运行在最前端。
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);           
        Notification myNotify = new Notification.Builder(this) 
                                .setSmallIcon(R.drawable.ic_launcher) 
                                .setTicker(getString(R.string.background_service)) 
                                .setContentTitle(getString(R.string.background_service)) 
                                .setContentText(getString(R.string.background_service_content))
                                .setContentIntent(pi) 
                                .build();             
        //设置notification的flag，表明在点击通知后，通知并不会消失，也在最右图上仍在通知栏显示图标。这是确保在activity中退出后，状态栏仍有图标可提下拉、点击，再次进入activity。
        myNotify.flags |= Notification.FLAG_NO_CLEAR;
        
       // 步骤 2：startForeground( int, Notification)将服务设置为foreground状态，使系统知道该服务是用户关注，低内存情况下不会killed，并提供通知向用户表明处于foreground状态。
        startForeground(NOTIFY_FAKEPLAYER_ID,myNotify);
	}

	private void checkTopActivity() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					String top = ActivityUtils
							.getLauncherTopApp(getApplicationContext());
					System.out.println("-----==========" + top + "==" + count+"   "+EasyOperateClickUtil
							.getEasyTag(getApplicationContext()));
					if (!SprefUtil.getCountBool(getApplicationContext(),
							SprefUtil.C_AUTO_HOOK, true)) {
						try {
							Thread.sleep(5 * 1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						count = 0;
						continue;
					}
					top = ActivityUtils
							.getLauncherTopApp(getApplicationContext());
					if (!top.equals(last)) {
						last = top;
						count = 0;
					} else {
						last = top;
						count++;
						if (count >= 30) {
							while ((EasyOperateClickUtil
									.getEasyTag(getApplicationContext())) != EasyOperateClickUtil.UNCLICK) {
							boolean bool=	EasyOperateClickUtil.setEasyTag(
										getApplicationContext(),
										EasyOperateClickUtil.UNCLICK);
							if(bool!=true) continue;
							System.out.println(bool+"====="+EasyOperateClickUtil.getEasyTag(getApplicationContext()));
							}
							count = 0;
							ActivityUtils.openAPK(PauseService.this,
									GlobalConstants.HDJ_GIT_PN);

						}
						try {
							Thread.sleep(5 * 1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
			}
		}).start();

	}
	@Override
	public void onDestroy() {
		Intent localIntent = new Intent();
		localIntent.setClass(this, PauseService.class); //销毁时重新启动Service
		this.startService(localIntent);
		super.onDestroy();
	}

}
