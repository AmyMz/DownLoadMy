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
        //ע��Intent��flag���ã�FLAG_ACTIVITY_CLEAR_TOP: ���activity���ڵ�ǰ���������У�����ǰ�˵�activity���ᱻ�رգ����ͳ�����ǰ�˵�activity��FLAG_ACTIVITY_SINGLE_TOP: ���activity�Ѿ�����ǰ�����У�����Ҫ�ټ��ء�����������flag��������һ����Ψһ��һ��activity��������棩��������ǰ�ˡ�
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);           
        Notification myNotify = new Notification.Builder(this) 
                                .setSmallIcon(R.drawable.ic_launcher) 
                                .setTicker(getString(R.string.background_service)) 
                                .setContentTitle(getString(R.string.background_service)) 
                                .setContentText(getString(R.string.background_service_content))
                                .setContentIntent(pi) 
                                .build();             
        //����notification��flag�������ڵ��֪ͨ��֪ͨ��������ʧ��Ҳ������ͼ������֪ͨ����ʾͼ�ꡣ����ȷ����activity���˳���״̬������ͼ�����������������ٴν���activity��
        myNotify.flags |= Notification.FLAG_NO_CLEAR;
        
       // ���� 2��startForeground( int, Notification)����������Ϊforeground״̬��ʹϵͳ֪���÷������û���ע�����ڴ�����²���killed�����ṩ֪ͨ���û���������foreground״̬��
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
		localIntent.setClass(this, PauseService.class); //����ʱ��������Service
		this.startService(localIntent);
		super.onDestroy();
	}

}
