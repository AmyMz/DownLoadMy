package com.hdj.downapp_market;

import com.hdj.downapp.util.GlobalConstants;
import com.mz.utils.ActivityUtils;
import com.mz.utils.EasyOperateClickUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class SetClickActivity extends Activity {
	Button connect_vpn;
	public static SetClickActivity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context=this;
		
	}
	
	boolean jump=false;
	
	public static boolean setClick(View btn) {
		if(EasyOperateClickUtil.getAutoClickFlag(btn.getContext())==EasyOperateClickUtil.AUTOCLICK){
			return btn.performClick();
		}else {
			return false;
		}
		
	}
	public static boolean setSimulateClick(View view, float x, float y) {
		System.out.println("vvvvclickmain  "+view.getId()+"------"+view.getWidth()+":"+view.getHeight());
		long downTime = SystemClock.uptimeMillis();
		final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
		downTime += 200;
		final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
		
		boolean onTouchEvent = view.onTouchEvent(downEvent);
		boolean onTouchEvent2 = view.onTouchEvent(upEvent);
		System.out.println(onTouchEvent+"----"+onTouchEvent2);
		downEvent.recycle();
		upEvent.recycle();
		System.out.println(onTouchEvent+":"+onTouchEvent2);
		return onTouchEvent && onTouchEvent2;
	}
	public static void openHook(Context context){
		try{
			EasyOperateClickUtil.setEasyTag(context, EasyOperateClickUtil.UNCLICK);
			ActivityUtils.openAPK(context, GlobalConstants.HDJ_GIT_PN/*"com.mz.webdownload"*/);
			System.exit(0);
		}catch(Exception e){
			System.out.println("Exception====="+e);
		}
		
	}
}
