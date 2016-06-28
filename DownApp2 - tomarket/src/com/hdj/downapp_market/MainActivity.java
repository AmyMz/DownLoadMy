package com.hdj.downapp_market;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Clob;
import java.util.List;

import com.hdj.downapp.util.GlobalConstants;
import com.hdj.downapp.util.IMEIUtil2;
import com.hdj.downapp.util.ListViewHelpUtil;
import com.hdj.downapp.util.StringUtil;
import com.mz.bean.AppInfo;
import com.mz.db.DBDao;
import com.mz.utils.ActivityUtils;
import com.mz.utils.EasyOperateClickUtil;
import com.mz.utils.SprefUtil;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	static MainActivity mainActivity;

	private TextView tv_new_imei;
	private TextView tv_old_imei;
	private TextView tv_imei_recognition;
	private TextView tv_percent_tips, tv_pn_tips,tv_install_tips;
	private ListView lv_appInfo;
	private static Button btn_down;
	private  Button btn_get_app_sd,btn_install_time_set;// broadcast
	private Button btn_percent_set, btn_addToDB;
	private EditText et_percent, et_add_pn, et_add_app_name,et_install_time;
	private ToggleButton tb_tomarket, tb_to_hdj_hook,tb_auto_click;
	private RadioGroup rgMarket;
	private RadioButton rbBaiDu,rb360,rbYyb;
	private boolean isIMEISame = true;
	private Toast toast = null;
	private Context context;
	private MyAdapter adapter;
	private DBDao dbDao;
	private int selection;
	private int installTime;
	private int market;
	private final static int MARKET_BAIDU=0;
	private final static int MARKET_360=1;
	private final static int MARKET_YYB=2;

	/**
	 * 供广播调用跳转到应用市场
	 */
	public static void handleDownload() {
		btn_down.performClick();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		mainActivity=this;
		dbDao = DBDao.getInstance(context);
		startService(new Intent(this, PauseService.class));
		setTitle(String.valueOf(SprefUtil.getCountInt(context,
				SprefUtil.C_COUNT, 0)));
		selection = SprefUtil.getCountInt(context, SprefUtil.C_SELECTION, 0);
		installTime = SprefUtil.getCountInt(context, SprefUtil.C_INSTALL_TIME, 0);
		market = SprefUtil.getCountInt(context, SprefUtil.C_MARKET, MARKET_BAIDU);
		findViewById();
		initIMEI();
		ListViewHelpUtil.setListViewHeightBasedOnChildren(lv_appInfo);
		ActivityUtils.showPermission(context);
	}

	List<AppInfo> list;

	private void findViewById() {
		lv_appInfo = (ListView) findViewById(R.id.lv_app_choose_list);
		list = dbDao.loadAll();
		if (list != null)
			adapter = new MyAdapter(context, list);
		adapter.setSelection(selection);
		lv_appInfo.setAdapter(adapter);
		tv_new_imei = (TextView) findViewById(R.id.tv_new_imei);
		tv_old_imei = (TextView) findViewById(R.id.tv_old_imei);
		tv_imei_recognition = (TextView) findViewById(R.id.tv_imei_recognition);
		tv_percent_tips = (TextView) findViewById(R.id.tv_percent_tips);
		tv_pn_tips = (TextView) findViewById(R.id.tv_package_name_tips);
		tv_install_tips = (TextView) findViewById(R.id.tv_install_tips);
		btn_percent_set = (Button) findViewById(R.id.btn_percent_set);
		btn_addToDB = (Button) findViewById(R.id.btn_package_name_set);
		btn_down = (Button) findViewById(R.id.btn_down);
		btn_get_app_sd = (Button) findViewById(R.id.btn_get_app_sd);
		btn_install_time_set = (Button) findViewById(R.id.btn_install_time_set);
		et_percent = (EditText) findViewById(R.id.et_percent);
		et_add_pn = (EditText) findViewById(R.id.et_package_name);
		et_add_app_name = (EditText) findViewById(R.id.et_app_name);
		et_install_time = (EditText) findViewById(R.id.et_install_time);
		tb_tomarket = (ToggleButton) findViewById(R.id.tb_to_market);
		tb_to_hdj_hook = (ToggleButton) findViewById(R.id.tb_to_hdj_hook);
		tb_auto_click = (ToggleButton) findViewById(R.id.tb_auto_click);
		rgMarket = (RadioGroup) findViewById(R.id.rg_market);
		rbBaiDu = (RadioButton) findViewById(R.id.rb_bd);
		rb360 = (RadioButton) findViewById(R.id.rb_360);
		rbYyb = (RadioButton) findViewById(R.id.rb_yyb);
		setEvent();
		getpackageName();
		tv_install_tips.setText(String.format(getString(R.string.set_install_tips), installTime));
		et_install_time.setText(installTime+"");
		tb_auto_click.setChecked((EasyOperateClickUtil.getAutoClickFlag(context)==EasyOperateClickUtil.AUTOCLICK)?true:false);
	}

	private void setEvent() {
		int flag = EasyOperateClickUtil.getBaiduHalfFlag(this);
		System.out.println("halfhalf------------"+flag);
		if (flag == EasyOperateClickUtil.HALF) {
			int percent = EasyOperateClickUtil.getBaiduHalfDownloadValue(this);
			tv_percent_tips.setText(String.format(getString(R.string.current_percent_tips), percent));
			et_percent.setText(percent + "");
		} else {
			tv_percent_tips.setText(getString(R.string.not_set_percent));
		}
		boolean autoMarket = SprefUtil.getCountBool(context,SprefUtil.C_AUTO_MARKET, true);
		tb_tomarket.setChecked(autoMarket);
		tb_tomarket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SprefUtil.putCountBool(context,
								SprefUtil.C_AUTO_MARKET, isChecked);
					}
				});
		boolean autoHook = SprefUtil.getCountBool(context,SprefUtil.C_AUTO_HOOK, true);
		tb_to_hdj_hook.setChecked(autoHook);
		tb_to_hdj_hook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SprefUtil.putCountBool(context, SprefUtil.C_AUTO_HOOK,
								isChecked);
					}
				});
		tb_auto_click.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				EasyOperateClickUtil.setAutoClickFlag(context, isChecked?EasyOperateClickUtil.AUTOCLICK:EasyOperateClickUtil.DO_NOT_AUTOCLICK);
			}
		});
		btn_down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				jumpMarket(context);
			}
		});

		btn_percent_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(et_percent.getText().toString().trim())
						|| et_percent.getText().toString().trim().equals(0)
						|| et_percent.getText().toString().trim().equals(00)) {
					showToast(getString(R.string.set_percent_err_tips));
				} else {
					int value = Integer.parseInt(et_percent.getText()
							.toString().trim());
					EasyOperateClickUtil.setBaiduHalfFlag(
							getApplicationContext(), EasyOperateClickUtil.HALF);
					EasyOperateClickUtil.setBaiDuHalfDownloadValue(
							getApplicationContext(), value);
					showToast(getString(R.string.set_percent_ok));
					int percent = EasyOperateClickUtil
							.getBaiduHalfDownloadValue(getApplicationContext());
					tv_percent_tips.setText(String.format(
							getString(R.string.current_percent_tips), percent));
				}
			}
		});
		btn_addToDB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!TextUtils.isEmpty(et_add_pn.getText().toString())
						&& et_add_pn.getText().toString().length() > 0) {
					AppInfo app = new AppInfo();
					app.setAppName(et_add_app_name.getText().toString());
					app.setPackageName(et_add_pn.getText().toString());
					dbDao.insertAPP(app);
					notifyList();
				}
			}
		});
		lv_appInfo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				adapter.setSelection(position);
				adapter.notifyDataSetChanged();
				selection = position;
				SprefUtil.putCountInt(context, SprefUtil.C_SELECTION, position);
				getpackageName();
			}
		});
		lv_appInfo.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				showChooseDialog(position);
//				showDeleteDialog(position);
				return true;
			}
		});
		btn_get_app_sd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
//				  
			}
		});
		btn_install_time_set.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(TextUtils.isEmpty(et_install_time.getText().toString())){
					et_install_time.setError(getString(R.string.set_install_error_tips));
					return;
				}try {
					installTime=Integer.parseInt(et_install_time.getText().toString());
				} catch (Exception e) {
					// TODO: handle exception
				}
				SprefUtil.putCountInt(context, SprefUtil.C_INSTALL_TIME, installTime);
				tv_install_tips.setText(String.format(getString(R.string.set_install_tips), installTime));
			}
		});
		switch (market) {
		case MARKET_BAIDU:
			rbBaiDu.setChecked(true);
			break;
		case MARKET_360:
			rb360.setChecked(true);
			break;
		case MARKET_YYB:
			rbYyb.setChecked(true);
			break;
		default:
			break;
		}
		rgMarket.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_bd:
					market=MARKET_BAIDU;
					SprefUtil.putCountInt(context, SprefUtil.C_MARKET, market);
					break;
				case R.id.rb_360:
					market=MARKET_360;
					SprefUtil.putCountInt(context, SprefUtil.C_MARKET, market);
					break;
				case R.id.rb_yyb:
					market=MARKET_YYB;
					SprefUtil.putCountInt(context, SprefUtil.C_MARKET, market);
					break;
				default:
					break;
				}
			}
		});
	}

	protected void showChooseDialog(final int position) {
		AlertDialog.Builder builder=new AlertDialog.Builder(context);
		String[] items={getString(R.string.change_item),getString(R.string.delete_item)};
		builder.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					showChangeDialog(position);
					break;
				case 1:
					showDeleteDialog(position);
					break;

				default:
					break;
				}
				
			}
		});
		builder.create().show();	
	}
	public void showChangeDialog(final int position){
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(getString(R.string.change_item_info));
		View view=LayoutInflater.from(context).inflate(R.layout.dialog_chang_item, null);
		builder.setView(view);
		final EditText et_appName=(EditText)view.findViewById(R.id.app_name);
		final EditText et_packageName=(EditText)view.findViewById(R.id.app_package_name);
		final String oldPN=list.get(position).getPackageName();
		et_appName.setText(list.get(position).getAppName());
		et_packageName.setText(list.get(position).getPackageName());
		builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				list.get(position).setAppName(et_appName.getText().toString());
				list.get(position).setAppName(et_packageName.getText().toString());
				AppInfo app=new AppInfo(et_packageName.getText().toString(),et_appName.getText().toString(), 0);
				System.out.println(app.toString());
				boolean result=dbDao.update(app, oldPN);
				if(!result) showToast(getString(R.string.change_qpp_error));
				notifyList();
			}
		});
		builder.setNegativeButton(getString(R.string.dialog_cancle), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				builder.create().dismiss();
			}
		});
		builder.create().show();
		
	}
	public void showDeleteDialog(final int position) {
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setPositiveButton(getString(R.string.dialog_ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dbDao.remove(list.get(position));
								if (selection == position) {
									selection = position - 1;
									SprefUtil.putCountInt(context,SprefUtil.C_SELECTION, selection);
								}
								notifyList();
							}
						})
				.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		dialog.setTitle(getString(R.string.dialog_tips));
		dialog.show();
	}
	protected void notifyList() {
		List<AppInfo> list2 = dbDao.loadAll();
		list.clear();
		list.addAll(list2);
		adapter.setList(list);
		adapter.setSelection(selection);
		getpackageName();
		adapter.notifyDataSetChanged();
		ListViewHelpUtil.setListViewHeightBasedOnChildren(lv_appInfo);
	}

	private void getpackageName() {
		if (list.size() > 0)
			tv_pn_tips.setText(String.format(getString(R.string.current_pn_tips), list.get(selection).getPackageName()));
	}
	

	/**
	 * 直接跳转不判断是否存在市场应用
	 * 
	 * @param context
	 * 
	 */
	public void jumpMarket(Context context) {

		selection = SprefUtil.getCountInt(context, SprefUtil.C_SELECTION, 0);
		if (list == null || list.size() <= 0 || list.size() < selection) {
			showToast(getString(R.string.not_set_package_yet));
			et_add_app_name.requestFocus();
			return;
		}
		String url = list.get(selection).getPackageName();
		System.out.println("url----" + url);
		if (TextUtils.isEmpty(url)) {
			showToast(getString(R.string.not_set_package_yet));
			et_add_app_name.requestFocus();
			return;
		}
		String packageName="";
		switch (market) {
		case MARKET_BAIDU:
			packageName=GlobalConstants.BAIDU_PN;
			break;
		case MARKET_360:
			packageName=GlobalConstants._360_PN;
			break;
		case MARKET_YYB:
			packageName=GlobalConstants.YYB_PN;
			if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP){
				ActivityUtils.openYYB(context);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
		System.out.println("--------------------------------------------------------------------------");
		Uri localUri = Uri.parse("market://details?id=" + url);
		Intent localIntent = new Intent("android.intent.action.VIEW", localUri);
		localIntent.setPackage(packageName);
		
		// localIntent.addCategory("android.intent.category.BROWSABLE");//可不用加
		// localIntent.addCategory("android.intent.category.DEFAULT");//可不用加
		localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(localIntent);
		System.out.println("--------------------------------------------------------------------------");

//		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
	        Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
	        try {
				File file=new File(new URI(uri.toString()));
				String path=file.getAbsolutePath();
				String appName=path.substring(path.lastIndexOf("/")+1, path.length());
					String packageName=ActivityUtils.getPackageName(context, path);
					System.out.println(appName+"======="+path+"========"+packageName);
					Toast.makeText(MainActivity.this, packageName, Toast.LENGTH_SHORT).show();
					if(!TextUtils.isEmpty(packageName)){
						et_add_app_name.setText(appName);
						et_add_pn.setText(packageName);
						btn_addToDB.performClick();
					}
//				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	    }
	}
	private void initIMEI() {
		IMEIUtil2 imeiUtil = new IMEIUtil2(MainActivity.this);
		imeiUtil.initIMEI();
		boolean isEqual = imeiUtil.isRecentlyIMEIEqual();
		if (isEqual) {
			CharSequence spanColor = StringUtil.SpanColor(getString(R.string.imei_same), Color.RED);
			tv_imei_recognition.setText(spanColor);
			isIMEISame = true;
		} else {
			CharSequence spanColor = StringUtil.SpanColor(getString(R.string.imei_difference), Color.BLUE);
			tv_imei_recognition.setText(spanColor);
			isIMEISame = false;
		}
		tv_old_imei.setText(getString(R.string.imei_old)+ imeiUtil.getOldIMEI());
		tv_new_imei.setText(getString(R.string.imei_new)+ imeiUtil.getNewIMEI());
	}
	

	private boolean isGo() {
		if (isIMEISame) {
			return true;
		}
		return false;
	}

	/**
	 * 友盟统计，计数
	 */
	private void showSplashOrToCount() {
		System.out.println(isGo());
		if (isGo())
			return;

		int count = SprefUtil.getCountInt(context, SprefUtil.C_COUNT, 0);
		SprefUtil.putCountInt(context, SprefUtil.C_COUNT, count + 1);
		System.out.println(isGo());
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.onResume(MainActivity.this);
	}

	@Override
	protected void onDestroy() {
		MobclickAgent.onKillProcess(MainActivity.this);
		super.onDestroy();
	};

	private void showToast(String tips) {
		toast = toast == null ? Toast.makeText(getApplicationContext(), "",
				Toast.LENGTH_SHORT) : toast;
		toast.setText(tips);
		toast.show();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		if(id==R.id.btn_clear_count){
			SprefUtil.putCountInt(context, SprefUtil.C_COUNT, 0);
			setTitle(SprefUtil.getCountInt(context, SprefUtil.C_COUNT, 0)+"");
		}
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
