package com.mz.utils;

import java.util.ArrayList;
import java.util.List;

import com.hdj.downapp_market.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public class ActivityUtils {
	public static void openAPK(Context context, String appPackageName) {
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.setClassName("com.hdjad.github",
					"com.hdjad.github.activity.LoginActivity");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);

			// Intent
			// intent=context.getPackageManager().getLaunchIntentForPackage(appPackageName);
			// System.out.println("--------------intent"+intent);
			// context.startActivity(intent);

		} catch (Exception e) {
			System.out.println("===================" + e);
			e.printStackTrace();
		}
	}

	public static void openOther(Context context, String appPackageName) {
		try {
			// Intent intent = new Intent();
			// intent.setAction("android.intent.action.MAIN");
			// intent.setClassName("com.hdjad.github",
			// "com.hdjad.github.activity.LoginActivity");
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// context.startActivity(intent);

			Intent intent = context.getPackageManager()
					.getLaunchIntentForPackage(appPackageName);
			System.out.println("--------------intent" + intent);
			context.startActivity(intent);

		} catch (Exception e) {
			System.out.println("===================" + e);
			e.printStackTrace();
		}
	}
	public static void openYYB(Context context){
		Intent intent=new Intent();
		intent.setComponent(new ComponentName("com.tencent.android.qqdownloader", "com.tencent.midas.wx.APMidasWXPayActivity"));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	public static void openYYB2(Context context){
		
		Intent intent2=new Intent();
		intent2.setComponent(new ComponentName("com.tencent.android.qqdownloader", "com.connector.tencent.connector.ConnectionActivity"));
		intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent2);
		
	}

	public static void openAPK2(Context context, String packageName) {
		List<ResolveInfo> matches = findActivitiesForPackage(context,
				packageName);
		if ((matches != null) && matches.size() > 0) {
			ResolveInfo resolveInfo = matches.get(0);
			ActivityInfo activityInfo = resolveInfo.activityInfo;
			startApk(activityInfo.packageName, activityInfo.name);
		}
	}

	private static void startApk(String packageName, String name) {
		String cmd = "am start -n" + packageName + "/" + name + "\n";
		try {
			CmdUtil.run(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<ResolveInfo> findActivitiesForPackage(Context context,
			String packageName) {
		final PackageManager pm = context.getPackageManager();
		final Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setPackage(packageName);
		final List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
		return apps != null ? apps : new ArrayList<ResolveInfo>();
	}

	/**
	 * ªÒ»°’ª∂•activity
	 * 
	 * @param context
	 * @return
	 */
	/*public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

		if (runningTaskInfos != null)
			return (runningTaskInfos.get(0).topActivity).toString();
		else
			return null;
	}
*/
	public static String getPackageName(Context context, String path) {
		String packageName = null;
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			packageName = info.packageName;

		}
		return packageName;
	}

	public static boolean isNoSwitch(Context context) {
		long ts = System.currentTimeMillis();
		UsageStatsManager usageStatsManager = (UsageStatsManager) context
				.getSystemService("usagestats");
		List queryUsageStats = usageStatsManager.queryUsageStats(
				UsageStatsManager.INTERVAL_BEST, 0, ts);
		if (queryUsageStats == null || queryUsageStats.isEmpty()) {
			return false;
		}
		return true;
	}

	public static String getLauncherTopApp(Context context) {
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> appTasks = activityManager
					.getRunningTasks(1);
			if (null != appTasks && !appTasks.isEmpty()) {
				return appTasks.get(0).topActivity.toString();
			}
		} else {
			UsageStatsManager sUsageStatsManager = (UsageStatsManager) context
					.getSystemService("usagestats");
			long endTime = System.currentTimeMillis();
			long beginTime = endTime - 10000;
			if (sUsageStatsManager == null) {
				sUsageStatsManager = (UsageStatsManager) context
						.getSystemService(Context.USAGE_STATS_SERVICE);
			}
			String result = "";
			UsageEvents.Event event = new UsageEvents.Event();
			UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime,
					endTime);
			while (usageEvents.hasNextEvent()) {
				usageEvents.getNextEvent(event);
				if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
					ComponentName com=new ComponentName(event.getPackageName(), event.getClassName());
					result = com.toString();
				}
			}
			if (!android.text.TextUtils.isEmpty(result)) {
				return result;
			}
		}
		return "";
	}
	public static void showPermission(Context context){
		if(!(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)&&!ActivityUtils.isNoSwitch(context)){
			Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			Toast.makeText(context, context.getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
			}
	}

}
