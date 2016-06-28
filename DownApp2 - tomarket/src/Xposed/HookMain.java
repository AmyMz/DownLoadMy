package Xposed;

import com.hdj.downapp.util.GlobalConstants;
import com.hdj.downapp_market.SetClickActivity;
import com.mz.utils.EasyOperateClickUtil;

import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookMain implements IXposedHookLoadPackage {
	public static final int OPERA_TIME_1S = 1000;
	private boolean vpnDisconnected_click = false, showVpnConnClick = false,
			isShowVPN = false, vpnConnect = false, isBaiduDownc = false,
			isBaiduDown = false, ishookclick = false, is360Downc = false,
			is360jumpc = false, is360liuliangc = false, baiduProgress = false,
			_360Progress = false, isYybDown = false, yybProgress = false,
			yybGo = false, yybLiuliang = false;;
	Handler handler;

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		// TODO Auto-generated method stub
		String packageName = lpparam.packageName;
		ClassLoader classLoader = lpparam.classLoader;
		ApplicationInfo appInfo = lpparam.appInfo;
		if (appInfo == null || TextUtils.isEmpty(packageName))
			return;
		if (!(packageName.equals(GlobalConstants.BAIDU_PN)
				|| (packageName.equals(GlobalConstants._360_PN))
				|| (packageName.equals(GlobalConstants.YYB_PN))
		/*
		 * ||packageName .equals (GlobalConstants.VPN_DIALOG)||
		 * packageName.equals(GlobalConstants.SETTINGS)
		 */
		|| packageName.equals(GlobalConstants.HDJ_GIT_PN))) {
			return;
		}

		System.out.println("packageName======****" + packageName);
		// 点击断开连接
		if (packageName.equals(GlobalConstants.VPN_DIALOG)) {
			HookMethod(View.class, "refreshDrawableState", packageName,
					MethodInt.VPN_DISCONNECT);
		}
		if (packageName.equals(GlobalConstants.SETTINGS)) {
			HookMethod(AbsListView.class, "isTextFilterEnabled", packageName,
					MethodInt.AbsListView_isTextFilterEnabled);// 点击listview、
			HookMethod(View.class, "refreshDrawableState", packageName,
					MethodInt.VIEW_TOSTRING_VPN_SETTING_ISSHOW_VPN);
			HookMethod(View.class, "refreshDrawableState", packageName,
					MethodInt.VIEW_TOSTRING_VPN_SETTING_CONNECT);

		}
		if (packageName.equals(GlobalConstants.BAIDU_PN)) {
			isBaiduDown = false;
			isBaiduDownc = false;// 3G情况下继续下载用的
			HookMethod(View.class, "refreshDrawableState", packageName,
					MethodInt.VIEW_TOSTRING_BAIDU_DOWN);
			HookMethod(TextView.class, "isSuggestionsEnabled", packageName,
					MethodInt.VIEW_TOSTRING_BAIDU_TV_PROGRESS);
		}
		if (packageName.equals(GlobalConstants._360_PN)) {
			is360jumpc = false;
			is360Downc = false;
			is360liuliangc = false;
			HookMethod(View.class, "refreshDrawableState", packageName,
					MethodInt.VIEW_TOSTRING_360_DOWN);
			HookMethod(TextView.class, "isSuggestionsEnabled", packageName,
					MethodInt.VIEW_TOSTRING_360_TV_PROGRESS);
		}
		if (packageName.equals(GlobalConstants.YYB_PN)) {
			isYybDown = false;
			yybGo = false;
			HookMethod(View.class, "refreshDrawableState", packageName,
					MethodInt.VIEW_TOSTRING_YYB_DOWN);
			// HookMethod(View.class, "getAnimation", packageName,
			// MethodInt.VIEW_TOSTRING_YYB_TV_LOAD);
			// HookMethod(View.class, "getResources", packageName,
			// MethodInt.VIEW_TOSTRING_YYB_TV_LOAD);
			// HookMethod(View.class, "getVisibility", packageName,
			// MethodInt.VIEW_TOSTRING_YYB_TV_LOAD);
			HookMethod(View.class, "getMeasuredWidth", packageName,
					MethodInt.VIEW_TOSTRING_YYB_TV_LOAD);
			HookMethod(TextView.class, "isSuggestionsEnabled", packageName,
					MethodInt.VIEW_TOSTRING_YYB_TV_PROGRESS);
			// HookMethod(View.class, "dispatchTouchEvent",
			// packageName,MethodInt.VIEW_TOSTRING_YYB_DOWN_DIS,
			// MotionEvent.class);
			// 后面参数是方法的参数类型
		}

		if (packageName.equals(GlobalConstants.hook)) {
			ishookclick = false;
			HookMethod(View.class, "refreshDrawableState", packageName,
					MethodInt.VIEW_TOSTRING_HOOK);
		}
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MethodInt.VPN_DISCONNECT:
					vpnDisconnected_click = false;
					break;
				case MethodInt.SHOW_VPN_CONNECT_OPERA:
					if (showVpnConnClick || !isShowVPN)
						return;
					showVpnConnClick = true;
					ListView lv_a = (ListView) msg.obj;
					int count = msg.arg1;
					int pos = 0;
					if (count > 2) {
						pos = 2;
					}
					lv_a.performItemClick(lv_a.getChildAt(pos), pos,
							lv_a.getItemIdAtPosition(pos));
					handler.sendEmptyMessageDelayed(MethodInt.SHOW_VPN_CONNECT,
							OPERA_TIME_1S);
					break;
				case MethodInt.SHOW_VPN_CONNECT:
					showVpnConnClick = false;
					break;
				case MethodInt.IS_SHOW_VPN:
					// isShowVPN=false;
					break;
				case MethodInt.VPN_CONNECT:
					vpnConnect = false;
					break;
				case MethodInt.BAIDU_DOWN:
					if (msg.obj != null) {
						final View v = (View) msg.obj;
						boolean re = SetClickActivity.setClick(v);
						if (!re) {
							Message msg2 = handler.obtainMessage();
							msg2.what = MethodInt.BAIDU_DOWN;
							msg2.obj = msg.obj;
							handler.sendMessageDelayed(msg2, 1000);
						}
					}
					break;
				case MethodInt.YYB_DOWN:
					if (msg.obj != null) {
						final View v = (View) msg.obj;
						boolean re = SetClickActivity.setClick(v);
						System.out.println("cilickR=========" + re);// 等待可以下载
						// System.out.println("***********"+yybGo);
						// if (!yybGo) {
						// Message msg2 = handler.obtainMessage();
						// msg2.what = MethodInt.YYB_DOWN;
						// msg2.obj = msg.obj;
						// handler.sendMessageDelayed(msg2, 1000);
						// }else {
						// boolean re=MainActivity.setSimulateClick(v, 0,0);
						// System.out.println("cilickR=========" + re);// 等待可以下载
						// }
					}
					break;
				case MethodInt._360_DOWN:
					if (msg.obj != null) {
						View v = (View) msg.obj;
						// boolean re=MainActivity.setSimulateClick(v,0,0);
						boolean re = SetClickActivity.setClick(v);
						System.out.println("click " + v.getId() + "------"
								+ v.getWidth() + ":" + v.getHeight() + re);
					}
					break;

				case MethodInt.HOOK:
					if (msg.obj != null) {
						Button btn = (Button) msg.obj;
						if (EasyOperateClickUtil.getEasyTag(btn.getContext()) == EasyOperateClickUtil.CILCKED)
							return;
						boolean res = SetClickActivity.setClick(btn);
						EasyOperateClickUtil.setEasyTag(btn.getContext(),
								EasyOperateClickUtil.CILCKED);
						System.out.println("HOOK==========="
								+ btn.getText()
								+ "  "
								+ res
								+ "==========="
								+ EasyOperateClickUtil.getEasyTag(btn
										.getContext()));
					}
					ishookclick = false;
					break;
				case MethodInt.BAIDU_PAUSE:
					baiduProgress = false;
					break;
				case MethodInt._360_PAUSE:
					_360Progress = false;
					break;
				case MethodInt.YYB_PAUSE:
					yybProgress = false;
					break;
				default:
					break;
				}
			};
		};

	}

	private void HookMethod(Class<?> clazz, String methodName,
			String packageName, final int type) {
		XposedHelpers.findAndHookMethod(clazz, methodName,
				new Object[] { new AppInfos_XC_MethodHook(type, packageName) });

	}

	private void HookMethod(final Class<?> class1, String methodName,
			final String packageName, final int type, Object... objects) {
		Object[] new_objects = new Object[objects.length + 1];
		for (int i = 0; i < objects.length; i++) {
			new_objects[i] = objects[i];
		}
		new_objects[objects.length] = new AppInfos_XC_MethodHook(type,
				packageName);
		XposedHelpers.findAndHookMethod(class1, methodName, new_objects);
	}

	class AppInfos_XC_MethodHook extends XC_MethodHook {
		int type;
		String packageName;

		public AppInfos_XC_MethodHook(int type, String packageName) {
			this.packageName = packageName;
			this.type = type;
		}

		@Override
		protected void afterHookedMethod(MethodHookParam param)
				throws Throwable {
			Log.i("TAG", "packagenaem----" + packageName + "----" + type);
			Object obj;
			switch (type) {
			case MethodInt.VPN_DISCONNECT:
				obj = param.thisObject;
				if (obj instanceof Button) {
					Button btn = (Button) obj;
					if (btn.getText().equals("Disconnect")
							|| btn.getText().equals("断开连接")) {
						if (vpnDisconnected_click)
							return;
						vpnDisconnected_click = true;
						SetClickActivity.setClick(btn);
						handler.sendEmptyMessageDelayed(
								MethodInt.VPN_DISCONNECT, OPERA_TIME_1S);
					}
				}
				break;
			case MethodInt.AbsListView_isTextFilterEnabled:
				obj = param.thisObject;
				if (obj instanceof ListView) {
					ListView lv_a = (ListView) obj;
					int id = lv_a.getId();
					int count = lv_a.getCount();
					if (count > 0) {
						Message msg = Message.obtain();
						msg.obj = lv_a;
						msg.arg1 = count;
						msg.what = MethodInt.SHOW_VPN_CONNECT_OPERA;
						handler.sendMessageDelayed(msg, 200);
					}
					return;
				}
				break;
			case MethodInt.VIEW_TOSTRING_VPN_SETTING_CONNECT:
				obj = param.thisObject;
				if (obj instanceof Button) {
					Button btn = (Button) obj;
					if (btn.getText().equals("Connect")
							|| btn.getText().equals("连接")) {
						if (vpnConnect)
							return;
						vpnConnect = true;
						SetClickActivity.setClick(btn);
						handler.sendEmptyMessageDelayed(MethodInt.VPN_CONNECT,
								OPERA_TIME_1S);
					}
				}
				break;
			case MethodInt.VIEW_TOSTRING_VPN_SETTING_ISSHOW_VPN:
				obj = param.thisObject;
				if (obj instanceof TextView) {
					TextView tv = (TextView) obj;
					String t = tv.getText().toString();
					if (t.equals("PPTP VPN") || t.equals("Connected")
							|| "已连接".equals(t) || "连接已断开".equals(t)) {
						isShowVPN = true;
						handler.sendEmptyMessageDelayed(MethodInt.IS_SHOW_VPN,
								OPERA_TIME_1S);
					}
				}
				break;
			case MethodInt.VIEW_TOSTRING_BAIDU_DOWN:
				obj = param.thisObject;
				if (obj instanceof TextView) {
					TextView btn = (TextView) obj;
					String s = btn.getText().toString();
					if ("继续下载".equals(s)) {
						System.out.println(s + "==text==" + btn.getId());
						if (isBaiduDownc)
							return;
						isBaiduDownc = true;
						Message msg = handler.obtainMessage();
						msg.what = MethodInt.BAIDU_DOWN;
						msg.obj = btn;
						handler.sendMessageDelayed(msg, 2000);// 这里多等2s才能正常下载
					}
				}
				if (obj instanceof View) {
					View v = (View) obj;
					int id = v.getId();
					if (id == 2131558608 && (v.getWidth() > 0)) {
						if (isBaiduDown)
							return;
						isBaiduDown = true;
						Message msg = handler.obtainMessage();
						msg.what = MethodInt.BAIDU_DOWN;
						msg.obj = v;
						handler.sendMessageDelayed(msg, 2000);// 这里多等2s才能正常下载
					}
				}

				break;
			case MethodInt.VIEW_TOSTRING_HOOK:
				obj = param.thisObject;
				if (obj instanceof Button) {
					Button btn = (Button) obj;
					String s = btn.getText().toString();
					if ("一键操作".equals(s)) {
						System.out
								.println("HOOK====="
										+ btn.getText()
										+ "===easy===="
										+ (EasyOperateClickUtil.getEasyTag(btn
												.getContext()) == EasyOperateClickUtil.CILCKED));
						if (EasyOperateClickUtil.getEasyTag(btn.getContext()) == EasyOperateClickUtil.CILCKED)
							return;

						// boolean res = SetClickActivity.setClick(btn);

						Message msg = handler.obtainMessage();
						msg.what = MethodInt.HOOK;
						msg.obj = btn;
						handler.sendMessageDelayed(msg, 1000);
					}
				}
				break;
			/*
			 * case MethodInt.VIEW_TOSTRING_360_JUMP: obj=param.thisObject;
			 * if(obj instanceof TextView){//2131493934 TextView
			 * t=(TextView)obj; //
			 * System.out.println("-------"+t.getId()+"----"+t.getText());
			 * if("跳过".equals(t.getText().toString().trim())){
			 * System.out.println("----tiaog---"+t.getId()+"----"+t.getText());
			 * System.out.println(MainActivity.setClick(t)); is360Downc=false; }
			 * } break;
			 */
			case MethodInt.VIEW_TOSTRING_360_DOWN:
				obj = param.thisObject;
				if (obj instanceof TextView) {// 2131493934 点击跳过
					TextView t = (TextView) obj;

					if ("跳过".equals(t.getText().toString().trim())) {
						System.out.println("============="
								+ t.getText().toString() + "===" + is360jumpc);
						if (is360jumpc)
							return;
						is360jumpc = true;
						is360Downc = false;
						SetClickActivity.setClick(t);
					}
				}
				if (obj instanceof TextView) {// 2131493934
					TextView t = (TextView) obj;
					if ("使用流量下载".equals(t.getText().toString().trim())) {
						System.out.println("----流量-tttt--" + t.getId() + "----"
								+ t.getText());
						if (is360liuliangc)
							return;
						is360liuliangc = true;
						SetClickActivity.setClick(t);
						// is360Downc=false;
					}
				}
				if (obj instanceof Button) {// 2131493934//点击使用流量下载
					Button t = (Button) obj;
					if ("使用流量下载".equals(t.getText().toString().trim())) {
						System.out.println("----流量--bbbbbb-" + t.getId()
								+ "----" + t.getText());
						if (is360liuliangc)
							return;
						is360liuliangc = true;
						SetClickActivity.setClick(t);
						// is360Downc=false;
					}
				}
				if (obj instanceof View) {// 点击下载按钮
					View v = (View) obj;
					if ((v.getId() == 2131493240) && (v.getWidth() > 0)) {
						System.out
								.println("-------" + v.getId() + "----" + "=="
										+ is360Downc + "==="
										+ v.getVisibility());
						// if(v.getVisibility()!=0){
						if (is360Downc || !is360jumpc)
							return;
						is360Downc = true;
						Message msg = handler.obtainMessage();
						msg.what = MethodInt._360_DOWN;
						msg.obj = v;
						handler.sendMessageDelayed(msg, 200);// 这里多等2s才能正常下载
						// }
					}
				}
				break;
			case MethodInt.VIEW_TOSTRING_360_TV_PROGRESS:
				obj = param.thisObject;
				if (obj instanceof TextView) {// 2131493243 下载TextView
					TextView t = (TextView) obj;
					if (t.getId() == 2131493243 && t.getWidth() > 0) {

						int halfFlag = EasyOperateClickUtil.getBaiduHalfFlag(t
								.getContext());
						if (halfFlag == EasyOperateClickUtil.NO_HALF)
							return;
						int halfValue = EasyOperateClickUtil
								.getBaiduHalfDownloadValue(t.getContext());
						System.out.println("****************" + t.getText()
								+ "********" + t.getId() + "****" + halfFlag
								+ "---" + halfValue);
						if (halfValue == 100)
							return;
						String text = t.getText().toString().trim();
						int index = text.indexOf("%");
						if (index > 0) {
							float f = Float
									.parseFloat(text.substring(0, index));
							if (f > halfValue && !_360Progress) {
								final TextView tv = t;
								_360Progress = true;
								SetClickActivity.openHook(tv.getContext());
								handler.sendEmptyMessageDelayed(
										MethodInt._360_PAUSE, 8000);
							}
						}
					}
				}
				break;
			case MethodInt.VIEW_TOSTRING_BAIDU_TV_PROGRESS:// 百度监控下载进度
				obj = param.thisObject;
				if (obj instanceof TextView) {// 2131559283
					TextView t = (TextView) obj;
					if (t.getId() == 2131559283 && t.getWidth() > 0) {
						int halfFlag = EasyOperateClickUtil.getBaiduHalfFlag(t
								.getContext());
						if (halfFlag == EasyOperateClickUtil.NO_HALF) {
							return;
						}
						int halfValue = EasyOperateClickUtil
								.getBaiduHalfDownloadValue(t.getContext());
						if (halfValue == 100)
							return;
						String text = t.getText().toString().trim();
						int index = text.indexOf("%");
						if (index > 0) {
							float f = Float
									.parseFloat(text.substring(0, index));
							System.out.println("________" + f + "----"
									+ baiduProgress + "=====" + halfValue);
							if (f > halfValue && !baiduProgress) {
								final TextView tv = t;
								baiduProgress = true;
								SetClickActivity.openHook(tv.getContext());
								// View v = (View) tv.getParent().getParent();
								// boolean res = MainActivity.setClick(v);
								// System.out.println("###" + v.getId()+
								// "---===================" + res);
								handler.sendEmptyMessageDelayed(
										MethodInt.BAIDU_PAUSE, 8000);

							}
						}
					}
				}
				break;
			case MethodInt.VIEW_TOSTRING_YYB_TV_LOAD:
				obj = param.thisObject;
				if (obj instanceof TextView) {//
					TextView t = (TextView) obj;
					if (t.getId() == 2131231373 && t.getWidth() > 0 && !yybGo) {
						System.out.println(t.getText().toString()
								+ "------tvload-----------");
						yybGo = true;
					}
				}// 没有break
			case MethodInt.VIEW_TOSTRING_YYB_DOWN:
				obj = param.thisObject;

				if (obj instanceof View) {
					View view = (View) obj;
					if (view.getId() == 2131231366 && view.getWidth() > 0) {
						if (isYybDown || !yybGo)
							return;
						isYybDown = true;
						Message msg = handler.obtainMessage();
						msg.what = MethodInt.YYB_DOWN;
						msg.obj = view;
						handler.sendMessageDelayed(msg, 2000);// 这里多等2s才能正常下载

					}
					// 快速安装
					if (view.getId() == 2131231818 && view.getWidth() > 0) {
						System.out.println("--------------vvvv-------------"
								+ view.getId() + "--------------");
						if (yybLiuliang)
							return;
						yybLiuliang = false;
						Message msg = handler.obtainMessage();
						msg.what = MethodInt.YYB_DOWN;
						msg.obj = view;
						handler.sendMessageDelayed(msg, 200);// 这里多等2s才能正常下载
					}

				}
				break;
			case MethodInt.VIEW_TOSTRING_YYB_DOWN_DIS:
				obj = param.thisObject;
				// if (obj instanceof TextView ) {
				// TextView view=(TextView)obj;
				// }
				// if (obj instanceof Button ) {
				// Button view=(Button)obj;
				// }
				// if (obj instanceof View ) {
				// View view=(View)obj;
				// }
				break;
			// 2131231373 下载 (11.4MB)***2131231370
			case MethodInt.VIEW_TOSTRING_YYB_TV_PROGRESS:
				obj = param.thisObject;
				if (obj instanceof TextView) {//
					TextView t = (TextView) obj;
					if (t.getId() == 2131231373 && t.getWidth() > 0) {
						int halfFlag = EasyOperateClickUtil.getBaiduHalfFlag(t
								.getContext());
						if (halfFlag == EasyOperateClickUtil.NO_HALF) {
							return;
						}
						int halfValue = EasyOperateClickUtil
								.getBaiduHalfDownloadValue(t.getContext());
						if (halfValue == 100)
							return;
						String text = t.getText().toString().trim();
						int index = text.indexOf("%");
						int start = text.indexOf(" ");
						System.out.println(text + "-----------------" + index
								+ "==" + start + "====" + halfValue
								+ text.substring(start + 1, index));
						if (index > 0) {
							float f = Float.parseFloat(text.substring(
									start + 1, index));
							if (f > halfValue && !yybProgress) {
								final TextView tv = t;
								yybProgress = true;
								SetClickActivity.openHook(tv.getContext());
								handler.sendEmptyMessageDelayed(
										MethodInt.YYB_PAUSE, 8000);

							}
						}
					}

				}
				break;
			default:
				break;

			}
		}
	}

}
