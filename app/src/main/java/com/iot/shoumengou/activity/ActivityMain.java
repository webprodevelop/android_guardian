//@formatter:off
package com.iot.shoumengou.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.search.core.SearchResult;
//import com.baidu.mapapi.search.geocode.GeoCodeResult;
//import com.baidu.mapapi.search.geocode.GeoCoder;
//import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
//import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.iot.shoumengou.App;
import com.iot.shoumengou.Global;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterPager;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.fragment.FragmentDevice;
import com.iot.shoumengou.fragment.FragmentHealth;
import com.iot.shoumengou.fragment.FragmentLocation;
import com.iot.shoumengou.fragment.FragmentServiceCenter;
import com.iot.shoumengou.fragment.FragmentUser;
import com.iot.shoumengou.fragment.discover.FragmentNewDiscover;
import com.iot.shoumengou.fragment.discover.FragmentParentDiscover;
import com.iot.shoumengou.fragment.discover.FragmentReport;
import com.iot.shoumengou.helper.JCRoomEvent;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;
import com.iot.shoumengou.view.DialogProgress;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ActivityMain extends FragmentActivity {
	public static final int				REQUEST_FENCE_ADDEDIT 					= 0;
	public static final int				REQUEST_DISCOVER_DETAIL 				= 1;
	public final static int				REQUEST_SCAN_SMART_WATCH				= 2;
	public final static int				REQUEST_SCAN_FIRE_SENSOR				= 3;
	public final static int				REQUEST_SCAN_SMOKE_SENSOR				= 4;
	public final static int				REQUEST_USER_DATA						= 5;
	public final static int				REQUEST_SOS_CONTACT						= 6;
	public final static int				REQUEST_SCAN_SMART_WATCH_FROM_HEALTH	= 7;
	public final static int				REQUEST_BIND_WATCH_LIST					= 8;
	public final static int				REQUEST_NOTIFICATION_LIST				= 9;
	public final static int				REQUEST_HEALTH_DISPLAY					= 10;
	public final static int				REQUEST_REFRESH_WATCH					= 11;

	private PopupWindow popupWindow = null;

	// Receiver for JPush
	public class ReceiverJPushMessage extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (!intent.getAction().equals(AppConst.ACTION_PUSH_RECEIVED))
					return;

				ItemNotification itemNotification = (ItemNotification)intent.getSerializableExtra("notification_data");
				if (itemNotification != null) {
					showNotificationPopup(itemNotification);
				}
			}
			catch (Exception ignored) {
			}
		}

	}
	/*
	// Receiver for WakeUp
	private final BroadcastReceiver m_receiverService = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			WakeLocker.Acquire(getApplicationContext());
			WakeLocker.Release();
		}
	};
	*/
	// Static
	public static boolean IS_IN_STACK = false;
	public static boolean IS_FOREGROUND = false;

	public static boolean IS_ABNORMAL_SET = false;
	public static int 		ABNORMAL_TYPE = 0;

	// View
	private ViewPager		m_pager;
	ArrayList<Fragment> 	fragmentArrayList;
	private RelativeLayout	m_tabDiscover;
	private LinearLayout	m_tabHealth;
	private LinearLayout	m_tabLocation;
	private LinearLayout	m_tabDevice;
	private RelativeLayout	m_tabUser;
	private ImageView		m_discoverNotification;
	private ImageView		m_infoNotification;
	private LinearLayout	m_rootView;

	private DialogProgress		m_dlgProgress;

	private ProgressDialog m_healthProgressDlg;

	// For JPush
	private ReceiverJPushMessage m_receiverJPushMsg;

//	private GeoCoder geoCoder;
	private BaiduLocationListener baiduLocationListener;
	private LocationClient locationClient;

	private String curProvince, curCity, curDistrict;

	private Long m_up_loc_time = null;

	boolean doubleBackToExitPressedOnce = false;
//	private Handler locationHandler = new Handler();

	@SuppressLint("StaticFieldLeak")
	public static Context mainContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainContext = this;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		m_dlgProgress = new DialogProgress(this);
		m_dlgProgress.setCancelable(false);

		checkWatchEntries();
		getAllNotifications();
		//EventBus.getDefault().register(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void showProgress() {
		m_dlgProgress.show();
	}

	public void dismissProgress() {
		m_dlgProgress.dismiss();
	}

	public void initHealthProgress(){

	}

	public void startHealthProgress(Runnable refreshHealthData){
		Util.refreshHealthData = refreshHealthData;
		Util.monitoringWatch.takeOnStatus = true;
		Util.monitoringWatch.netStatus = true;
		Util.isUpdatedHealthData = false;
		Util.healthDataStart = new Date().getTime();
		Util.healthCheckStart = 0;
		m_healthProgressDlg = new ProgressDialog(this);
		m_healthProgressDlg.setMax(180); // Progress Dialog Max Value
		m_healthProgressDlg.setMessage(""); // Setting Message
		m_healthProgressDlg.setTitle("健康体征远程检测中······"); // Setting Title
		m_healthProgressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
		m_healthProgressDlg.setCancelable(false);
		m_healthProgressDlg.setProgress(0);
		m_healthProgressDlg.show();
		healthDataHandler.postDelayed(runnableHealthData, 1000);
		FragmentNewDiscover fragmentNewDiscover = (FragmentNewDiscover)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentNewDiscover.class.getSimpleName());
		if (fragmentNewDiscover != null) {
			fragmentNewDiscover.updateMonitoringWatchStatus();
		}
	}

	public void resumeHealthProgress(){
		if (null != m_healthProgressDlg && 0 != Util.healthDataStart && null != Util.refreshHealthData){
			healthDataHandler.postDelayed(runnableHealthData, 1000);
		}else{
			closeHealthProcess();
		}
		FragmentNewDiscover fragmentNewDiscover = (FragmentNewDiscover)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentNewDiscover.class.getSimpleName());
		if (fragmentNewDiscover != null) {
			fragmentNewDiscover.updateMonitoringWatchStatus();
		}
	}

	public void closeHealthProcess(){
		Util.isUpdatedHealthData = true;
		Util.healthDataStart = 0;
		Util.healthCheckStart = 0;
		if (null != m_healthProgressDlg) {
			m_healthProgressDlg.dismiss();
		}
		m_healthProgressDlg = null;
		Util.refreshHealthData = null;
		FragmentNewDiscover fragmentNewDiscover = (FragmentNewDiscover)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentNewDiscover.class.getSimpleName());
		if (fragmentNewDiscover != null) {
			fragmentNewDiscover.updateMonitoringWatchStatus();
		}
	}

	private final Handler healthDataHandler = new Handler();

	private final Runnable failHealthData = new Runnable() {
		@Override
		public void run() {
			if (Util.monitoringWatch != null) {
				if (!Util.monitoringWatch.takeOnStatus){
					Util.ShowDialogError(Util.monitoringWatch.name + "的手表佩戴异常了，无法获取健康数据");
				}else if(!Util.monitoringWatch.netStatus){
					Util.ShowDialogError(Util.monitoringWatch.name + "的手表佩戴异常了，无法获取健康数据");
				}else{
//					Util.ShowDialogError(Util.monitoringWatch.name + "的手表离线未能获取数据");
				}

				// scott-- set watch take on status as off
				HttpAPI.receiveHealthData(Prefs.Instance().getUserToken(),
						Prefs.Instance().getUserPhone(),
						"5G", Prefs.Instance().getMoniteringWatchSerial(), false,
						new VolleyCallback() {
							@Override
							public void onSuccess(String result) {
								//success
							}

							@Override
							public void onError(Object error) {
								//fail
							}
						}, ActivityMain.class.getSimpleName());

			}
		}
	};

	private final Runnable runnableHealthData = new Runnable() {
		@Override
		public void run() {
			if (null == m_healthProgressDlg) {
				return;
			}
			if (Util.healthDataStart == 0){
				return;
			}

			long now = new Date().getTime();
			long timeDiff = now - Util.healthDataStart;

			if ( timeDiff > 180000){
				Util.monitoringWatch.netStatus = false;
				(ActivityMain.this).runOnUiThread(failHealthData);
				closeHealthProcess();
				return;
			}else {
				m_healthProgressDlg.setProgress((int) (timeDiff / 1000));
				if (!m_healthProgressDlg.isShowing()){
					m_healthProgressDlg.show();
				}
			}

			if (timeDiff > 10000) {
				if (!Util.monitoringWatch.takeOnStatus || !Util.monitoringWatch.netStatus) {
					(ActivityMain.this).runOnUiThread(failHealthData);
					closeHealthProcess();
					return;
				}
			}

			if (timeDiff > 30000) {
				if (Util.isUpdatedHealthData) {
					if (null != Util.refreshHealthData) {
						(ActivityMain.this).runOnUiThread(Util.refreshHealthData);
					}
					closeHealthProcess();
					return;
				}

				if (timeDiff > Util.healthCheckStart) {
					Util.healthCheckStart = timeDiff + 5000;
					tryGetLastHealthDataWithInterval((int) (timeDiff - 30000));
				}
			}

			healthDataHandler.postDelayed(runnableHealthData, 1000);
		}
	};

	public void tryGetLastHealthDataWithInterval(int interval) {
		ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
		if (monitoringWatchInfo == null) {
			return;
		}
		if (!Util.monitoringWatch.netStatus || !Util.monitoringWatch.takeOnStatus || Util.isUpdatedHealthData){
			return;
		}

		HttpAPI.getLastHealthDataWithTimeInterval(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, interval, new VolleyCallback() {
			@SuppressLint("SetTextI18n")
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					int iRetCode = jsonObject.getInt("retcode");

					if (iRetCode == HttpAPIConst.RespCode.DEVICE_STATUS_OFFLINE) {
						Util.monitoringWatch.netStatus = false;
					}else if(iRetCode == HttpAPIConst.RespCode.WATCH_STATUS_TAKEOFF) {
						Util.monitoringWatch.takeOnStatus = false;
					}else if(iRetCode == HttpAPIConst.RespCode.DB_NO_FIND) {

					}else if(iRetCode == HttpAPIConst.RESP_CODE_SUCCESS){
						Util.isUpdatedHealthData = true;
					}
				}
				catch (JSONException e) {

				}
			}

			@SuppressLint("SetTextI18n")
			@Override
			public void onError(Object error) {
			}
		}, ActivityMain.class.getSimpleName());
	}

	private void initControls() {
		m_pager = findViewById(R.id.ID_PAGER_CONTENT);
		
		m_pager.setOffscreenPageLimit(3);

		m_tabHealth = findViewById(R.id.ID_LYT_TAB_HEALTH);
		m_tabLocation = findViewById(R.id.ID_LYT_TAB_LOCATION);
		m_tabDiscover = findViewById(R.id.ID_LYT_TAB_DISCOVER);
		m_tabDevice = findViewById(R.id.ID_LYT_TAB_DEVICE);
		m_tabUser = findViewById(R.id.ID_LYT_TAB_INFO);
		m_discoverNotification = findViewById(R.id.ID_IMGVIEW_DISCOVER_NOTIFICATION);
		m_infoNotification = findViewById(R.id.ID_IMGVIEW_INFO_NOTIFICATION);
		m_rootView = findViewById(R.id.ID_LYT_ROOT);

		fragmentArrayList = new ArrayList<>();
		fragmentArrayList.add(new FragmentParentDiscover());
		fragmentArrayList.add(new FragmentServiceCenter());
		fragmentArrayList.add(new FragmentUser());
		AdapterPager m_adapter = new AdapterPager(getSupportFragmentManager(), fragmentArrayList);
		m_pager.setAdapter(m_adapter);

		m_tabDiscover.setSelected(true);
		m_tabHealth.setSelected(false);
		m_tabUser.setSelected(false);
		m_tabLocation.setSelected(false);
		m_tabDevice.setSelected(false);

		initLocation();
	}

	private void initLocation() {
//		geoCoder = GeoCoder.newInstance();
//		geoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);

		LocationClientOption option = new LocationClientOption();

		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
		);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
		int span=1000;
		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要

		locationClient = new LocationClient(this);
		locationClient.setLocOption(option);
		baiduLocationListener = new BaiduLocationListener();
		locationClient.registerLocationListener(baiduLocationListener);
		locationClient.start();
	}

	public void onWeather() {
		if (curCity == null || curCity.isEmpty()) {
			return;
		}
		if (curDistrict == null) {
			curDistrict = "";
		}

		FragmentHealth.getInstance().getWeatherInfo(curCity, curDistrict);
	}

	public void showDiscoverNotification(boolean show) {
//		if (show) {
//			m_discoverNotification.setVisibility(View.VISIBLE);
//		} else {
//			m_discoverNotification.setVisibility(View.GONE);
//		}
	}

	public void showInfoNotification() {
//		if (Prefs.Instance().getInfoNotification()) {
//			m_infoNotification.setVisibility(View.VISIBLE);
//		} else {
//			m_infoNotification.setVisibility(View.GONE);
//		}
	}

	public void goDevicePage() {
		if (Util.monitoringWatch != null) {
			Intent intent = new Intent(ActivityMain.this, ActivityServiceTerm.class);
			intent.putExtra("device_data", Util.monitoringWatch);
			startActivityForResult(intent, ActivityMain.REQUEST_REFRESH_WATCH);
		}
	}

	private void setEventListener() {
		m_tabDiscover.setOnClickListener(v -> {
			while(((FragmentParentDiscover)(fragmentArrayList.get(0))).getBackStackSize() > 1) {
				((FragmentParentDiscover)(fragmentArrayList.get(0))).popChildFragment(false);
			}
			FragmentNewDiscover fragmentNewDiscover = (FragmentNewDiscover)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentNewDiscover.class.getSimpleName());
			if (fragmentNewDiscover != null) {
				fragmentNewDiscover.updateNotificationState();
				if (Util.monitoringWatch != null) {
					fragmentNewDiscover.loadLastHealthData();
				}
			}
			m_pager.setCurrentItem(0);
		});
		m_tabHealth.setOnClickListener(v -> m_pager.setCurrentItem(1));
		m_tabUser.setOnClickListener(v -> m_pager.setCurrentItem(2));

		m_tabLocation.setOnClickListener(v -> m_pager.setCurrentItem(3));
		m_tabDevice.setOnClickListener(v -> m_pager.setCurrentItem(4));

		// Page Listener
		m_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {
			}

			@Override
			public void onPageSelected(int i) {
				m_tabHealth.setSelected(false);
				m_tabLocation.setSelected(false);
				m_tabDiscover.setSelected(false);
				m_tabDevice.setSelected(false);
				m_tabUser.setSelected(false);

				switch (i) {
					case 0:
						m_tabDiscover.setSelected(true);
						break;
					case 1:
						m_tabHealth.setSelected(true);
						break;
					case 2:
						m_tabUser.setSelected(true);
						break;
					case 3:
						m_tabLocation.setSelected(true);
						break;
					case 4:
						m_tabDevice.setSelected(true);
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (m_tabDiscover.isSelected() && ((FragmentParentDiscover)(fragmentArrayList.get(0))).getBackStackSize() > 1) {
			((FragmentParentDiscover)(fragmentArrayList.get(0))).popChildFragment(false);
		}
		else {
			if (doubleBackToExitPressedOnce) {
				moveTaskToBack(true);
				return;
			}

			this.doubleBackToExitPressedOnce = true;
			Toast.makeText(this, R.string.str_again_exit, Toast.LENGTH_SHORT).show();

			new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 3000);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_FENCE_ADDEDIT) {
			FragmentLocation fragmentLocation = (FragmentLocation)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentLocation.class.getSimpleName());
			if (fragmentLocation != null) {
				fragmentLocation.onActivityResult(requestCode, resultCode, data);
			}
		} else if (requestCode == REQUEST_DISCOVER_DETAIL) {
		} else if (requestCode == REQUEST_SCAN_SMART_WATCH || requestCode == REQUEST_SCAN_FIRE_SENSOR || requestCode == REQUEST_SCAN_SMOKE_SENSOR || requestCode == REQUEST_REFRESH_WATCH) {
			FragmentDevice fragmentDevice = (FragmentDevice)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentDevice.class.getSimpleName());
			if (fragmentDevice != null) {
				fragmentDevice.onActivityResult(requestCode, resultCode, data);
			}
		} else if (requestCode == REQUEST_USER_DATA || requestCode == REQUEST_SOS_CONTACT) {
//		} else if (requestCode == REQUEST_NOTIFICATION_LIST) {

		} else if (requestCode == REQUEST_HEALTH_DISPLAY) {
			FragmentHealth.getInstance().onActivityResult(requestCode, resultCode, data);
		} else if (requestCode == REQUEST_SCAN_SMART_WATCH_FROM_HEALTH || requestCode == REQUEST_BIND_WATCH_LIST) {
			FragmentHealth.getInstance().onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		IS_FOREGROUND = false;

//		locationHandler.removeCallbacks(locationRunnable);
//		JPushInterface.onPause(getBaseContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
		IS_IN_STACK = true;
		IS_FOREGROUND = true;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
						AppConst.REQUEST_PERMISSION_LOCATION
				);
			}
			if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
						AppConst.REQUEST_PERMISSION_STORAGE
				);
			}
			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.CAMERA },
						AppConst.REQUEST_PERMISSION_STORAGE
				);
			}
			if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.RECORD_AUDIO },
						AppConst.REQUEST_PERMISSION_STORAGE
				);
			}
			if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.CALL_PHONE },
						AppConst.REQUEST_PERMISSION_STORAGE
				);
			}
			if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.READ_PHONE_STATE },
						AppConst.REQUEST_PERMISSION_STORAGE
				);
			}
			//Util.ignoreBatteryOptimization(this);
		}

		ItemNotification itemNotification = (ItemNotification)getIntent().getSerializableExtra("notification_data");
		if (itemNotification != null) {
			showNotificationPopup(itemNotification);
			getIntent().removeExtra("notification_data");
		}

		if (IS_ABNORMAL_SET) {
			showAbnormalReport();
			IS_ABNORMAL_SET = false;
		}

		if (fragmentArrayList != null && fragmentArrayList.get(0).isAdded()) {
			FragmentNewDiscover fragmentNewDiscover = (FragmentNewDiscover) ((FragmentParentDiscover) (fragmentArrayList.get(0))).getChildFragment(FragmentNewDiscover.class.getSimpleName());
			if (fragmentNewDiscover != null ) {
				fragmentNewDiscover.updateNotificationState();
				if (Util.monitoringWatch != null) {
					fragmentNewDiscover.loadLastHealthData();
				}
			}
		}

//		locationHandler.post(locationRunnable);
//		JPushInterface.onResume(getBaseContext());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		IS_IN_STACK = false;
		IS_FOREGROUND = false;

		if (m_receiverJPushMsg != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(m_receiverJPushMsg);
		}
//		JPushInterface.onPause(getBaseContext());

		if (locationClient != null) {
			locationClient.stop();
			locationClient.unRegisterLocationListener(baiduLocationListener);
		}
	}

	private void RegisterReceiverJPushMsg() {
		m_receiverJPushMsg = new ReceiverJPushMessage();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(AppConst.ACTION_PUSH_RECEIVED);
		LocalBroadcastManager.getInstance(this).registerReceiver(m_receiverJPushMsg, filter);
	}

	public void showAbnormalReport() {
		m_pager.setCurrentItem(0);
		((FragmentParentDiscover)(fragmentArrayList.get(0))).showReportFromPushNotification(ActivityMain.ABNORMAL_TYPE);
	}

	@SuppressLint("SetTextI18n")
	public void showNotificationPopup(final ItemNotification itemNotification) {
		FragmentNewDiscover fragmentNewDiscover = (FragmentNewDiscover)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentNewDiscover.class.getSimpleName());
		if (fragmentNewDiscover != null) fragmentNewDiscover.updateNotificationState();

		if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_NOTICE)) {
			if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_HEALTH_ABNORMAL) && Util.getAlarmCount() == 0) {
				if (popupWindow != null) {
					popupWindow.dismiss();
				}
				@SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_notification, null);
				LinearLayout llContainer = popupView.findViewById(R.id.ID_LL_CONTAINTER);
				TextView tvTitle = popupView.findViewById(R.id.ID_TXTVIEW_TITLE);
				TextView tvStatus = popupView.findViewById(R.id.ID_TXTVIEW_STATS);
				TextView tvTime = popupView.findViewById(R.id.ID_TXTVIEW_TIME);
				TextView tvContent = popupView.findViewById(R.id.ID_TXTVIEW_CONTENT);

				tvTitle.setText(R.string.str_notice_title);
				tvStatus.setText(R.string.str_unread_status);

				tvTime.setText(Util.getDateTimeFormatString(new Date(itemNotification.time)));
				tvContent.setText(itemNotification.alert);

				popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				popupWindow.setOnDismissListener(() -> popupWindow = null);

				llContainer.setOnClickListener(view -> {
					HttpAPI.readNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
						@Override
						public void onSuccess(String result) {
							itemNotification.isRead = 1;
							Util.updateNotificationEntry(itemNotification, itemNotification);
						}

						@Override
						public void onError(Object error) {

						}
					}, ActivityMain.class.getSimpleName());
					ActivityMain.ABNORMAL_TYPE = 0;
					if (itemNotification.alert.contains(getString(R.string.str_blood_pressure))) {
						ActivityMain.ABNORMAL_TYPE = 1;
					}
					else if (itemNotification.alert.contains(getString(R.string.str_body_temperature))) {
						ActivityMain.ABNORMAL_TYPE = 2;
					}
					showAbnormalReport();
					popupWindow.dismiss();
				});

				popupWindow.setAnimationStyle(R.style.popup_window_animation);
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				popupWindow.setFocusable(true);
				popupWindow.setOutsideTouchable(true);
				popupWindow.update();

				// Show popup window offset 1,1 to phoneBtton at the screen center.
				popupWindow.showAtLocation(m_rootView, Gravity.TOP, 0, 90);

				new Handler().postDelayed(popupWindow::dismiss, 5000);
			}
			else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_LOW_BATTERY) ||
					itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_FULL_BATTERY) ||
					itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_CONNECT) ||
					itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_DISCONNECT) ||
					itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_ON) ||
					itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_OFF) ||
					itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_BIRTHDAY) ||
					itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_HEALTH) ||
					itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_ALARM_OFF) ||
					itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_UPDATE_ALARM_INFO)) {
				if (Util.getAlarmCount() == 0) {
					if (popupWindow != null) {
						popupWindow.dismiss();
					}
					@SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_notification, null);
					LinearLayout llContainer = popupView.findViewById(R.id.ID_LL_CONTAINTER);
					TextView tvTitle = popupView.findViewById(R.id.ID_TXTVIEW_TITLE);
					TextView tvStatus = popupView.findViewById(R.id.ID_TXTVIEW_STATS);
					TextView tvTime = popupView.findViewById(R.id.ID_TXTVIEW_TIME);
					TextView tvContent = popupView.findViewById(R.id.ID_TXTVIEW_CONTENT);

					tvTitle.setText(R.string.str_notice_title);
					tvStatus.setText(R.string.str_unread_status);

					tvTime.setText(Util.getDateTimeFormatString(new Date(itemNotification.time)));
					tvContent.setText(itemNotification.alert);

					popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
					popupWindow.setOnDismissListener(() -> popupWindow = null);

					popupWindow.setAnimationStyle(R.style.popup_window_animation);
					popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					popupWindow.setFocusable(true);
					popupWindow.setOutsideTouchable(true);
					popupWindow.update();

					// Show popup window offset 1,1 to phoneBtton at the screen center.
					popupWindow.showAtLocation(m_rootView, Gravity.TOP, 0, 90);

					new Handler().postDelayed(popupWindow::dismiss, 5000);

					llContainer.setOnClickListener(view -> {
						HttpAPI.readNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
							@Override
							public void onSuccess(String result) {
								itemNotification.isRead = 1;
								Util.updateNotificationEntry(itemNotification, itemNotification);
							}

							@Override
							public void onError(Object error) {

							}
						}, this.getClass().getSimpleName());

						popupWindow.dismiss();
					});
				}

				ItemWatchInfo itemWatchInfo = Util.findWatchEntry(itemNotification.deviceSerial);
				if (itemWatchInfo != null) {
					if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_LOW_BATTERY))
					{
						itemWatchInfo.chargeStatus = 10;
					}
					else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_FULL_BATTERY)){
						itemWatchInfo.chargeStatus = 90;
					}
					else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_CONNECT) ||
							itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_DISCONNECT))
					{
						itemWatchInfo.netStatus = itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_CONNECT);
						FragmentReport fragmentReport = (FragmentReport)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentReport.class.getSimpleName());
//						if (fragmentReport != null) fragmentReport.alertTakeOff();
					}
					else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_OFF) ||
							itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_ON)){
						itemWatchInfo.takeOnStatus = itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_ON);
						FragmentReport fragmentReport = (FragmentReport)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentReport.class.getSimpleName());
//						if (fragmentReport != null) fragmentReport.alertTakeOff();
					}
					Util.updateWatchEntry(itemWatchInfo, itemWatchInfo);
					if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_CONNECT) ||
							itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_DISCONNECT) ||
							itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_OFF) ||
							itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_ON)) {
						if (fragmentNewDiscover != null) fragmentNewDiscover.updateMonitoringWatchStatus();
					}
					((FragmentParentDiscover)(fragmentArrayList.get(0))).updateDeviceListIfNeeded();
				}
			}
			else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_UPDATE_HEALTH_DATA)) {
				Util.isUpdatedHealthData = true;
			}
			else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_UPDATE_WATCH_POS)) {
				FragmentLocation fragmentLocation = (FragmentLocation)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentLocation.class.getSimpleName());
				if (fragmentLocation != null) fragmentLocation.getWatchPos();
			}
		}
		else if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_ALARM) && Util.getAlarmCount() == 1){
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			int alarmId = itemNotification.alarmId;

			HttpAPI.getAlarmDetail(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), alarmId, new VolleyCallback() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						int iRetCode = jsonObject.getInt("retcode");
						if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
							return;
						}

						String strData = jsonObject.getString("data");
						JSONObject dataObject = new JSONObject(strData);
						JSONObject infoObject = dataObject.optJSONObject("info");
						if (infoObject != null) {
							@SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_notification, null);
							LinearLayout llContainer = popupView.findViewById(R.id.ID_LL_CONTAINTER);
							TextView tvTitle = popupView.findViewById(R.id.ID_TXTVIEW_TITLE);
							TextView tvStatus = popupView.findViewById(R.id.ID_TXTVIEW_STATS);
							TextView tvTime = popupView.findViewById(R.id.ID_TXTVIEW_TIME);
							TextView tvContent = popupView.findViewById(R.id.ID_TXTVIEW_CONTENT);

							tvTitle.setText(R.string.str_alarm_title);
							tvTitle.setTextColor(getColor(R.color.color_red));
							tvStatus.setText(R.string.str_progress_status);
							tvStatus.setTextColor(getColor(R.color.color_red));

							tvTitle.setText(getString(R.string.str_app_name));
							tvTime.setText(Util.getDateTimeFormatString(new Date(itemNotification.time)));
							tvContent.setText(infoObject.optString("title"));

							popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
							popupWindow.setOnDismissListener(() -> popupWindow = null);

							llContainer.setOnClickListener(view -> {
								HttpAPI.readNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
									@Override
									public void onSuccess(String result) {
										itemNotification.isRead = 1;
										Util.updateNotificationEntry(itemNotification, itemNotification);
									}

									@Override
									public void onError(Object error) {

									}
								}, ActivityMain.class.getSimpleName());

								Intent intent = new Intent(ActivityMain.this, ActivitySOSHelp.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
								intent.putExtra("notification_data", itemNotification);
								intent.putExtra("alarm_data", strData);
								startActivity(intent);

								if (popupWindow != null) {
									popupWindow.dismiss();
								}
							});

							popupWindow.setAnimationStyle(R.style.popup_window_animation);
							popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
							popupWindow.setFocusable(false);
							popupWindow.setOutsideTouchable(false);
							popupWindow.update();

							// Show popup window offset 1,1 to phoneBtton at the screen center.
							popupWindow.showAtLocation(m_rootView, Gravity.TOP, 0, 250);
						}
					}
					catch (JSONException ignored) {
					}
				}

				@Override
				public void onError(Object error) {

				}
			}, ActivityMain.class.getSimpleName());
		}
		else if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_CLOCK) && Util.getAlarmCount() == 0) {
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			@SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_notification, null);
			LinearLayout llContainer = popupView.findViewById(R.id.ID_LL_CONTAINTER);
			TextView tvTitle = popupView.findViewById(R.id.ID_TXTVIEW_TITLE);
			TextView tvStatus = popupView.findViewById(R.id.ID_TXTVIEW_STATS);
			TextView tvTime = popupView.findViewById(R.id.ID_TXTVIEW_TIME);
			TextView tvContent = popupView.findViewById(R.id.ID_TXTVIEW_CONTENT);

			tvTitle.setText(getString(R.string.str_notice_title));
			tvStatus.setText(getString(R.string.str_unread_status));
			tvTime.setText(Util.getDateTimeFormatString(new Date(itemNotification.time)));
			tvContent.setText(itemNotification.deviceSerial);

			popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			popupWindow.setOnDismissListener(() -> popupWindow = null);

			llContainer.setOnClickListener(view -> {
				IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityMain.this);
				itemNotification.isRead = 1;
				iotdbHelper.updateNotificationEntry(itemNotification, itemNotification);

				Intent intent = new Intent(ActivityMain.this, ActivityAlarmSet.class);
				startActivity(intent);

				popupWindow.dismiss();
			});

			popupWindow.setAnimationStyle(R.style.popup_window_animation);
			popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.update();

			// Show popup window offset 1,1 to phoneBtton at the screen center.
			popupWindow.showAtLocation(m_rootView, Gravity.TOP, 0, 90);

			new Handler().postDelayed(popupWindow::dismiss, 5000);
		}
	}

	@SuppressLint("SetTextI18n")
	public void showWeatherPopup(int codeValue, String weatherType, int temperature, int highTemp, int lowTemp, String district, String updateTime, String quality, int qualityValue) {
		@SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_weather, null);
		TextView tvTemperature = popupView.findViewById(R.id.ID_TXTVIEW_TEMPERATURE);
		TextView tvAddressAndTime = popupView.findViewById(R.id.ID_TXTVIEW_ADDRESS_TIME);
		ImageView ivWeatherType = popupView.findViewById(R.id.ID_IMGVIEW_WEATHER_TYPE);
		TextView tvTempRange = popupView.findViewById(R.id.ID_TXTVIEW_TEMPERATURE_RANGE);
		TextView tvWeatherQuality = popupView.findViewById(R.id.ID_TXTVIEW_QUALITY);
		TextView tvWeatherQualityValue = popupView.findViewById(R.id.ID_TXTVIEW_QUALITY_VALUE);
//		ImageView ivWeatherQuality = popupView.findViewById(R.id.ID_IMGVIEW_QUALITY);
		TextView tvWeatherType = popupView.findViewById(R.id.ID_TXTVIEW_TYPE);

		tvTemperature.setText(temperature + getString(R.string.str_degree));
		tvAddressAndTime.setText(String.format(getString(R.string.str_weather_address_time), district, updateTime));
		tvTempRange.setText(String.format(getString(R.string.str_weather_today_temperature), lowTemp, highTemp));
		tvWeatherQuality.setText(quality);
		tvWeatherQualityValue.setText(String.valueOf(qualityValue));
		//ivWeatherDegree.setImageResource();
		tvWeatherType.setText(weatherType);

		int weatherResId = Util.getResId("weather_" + codeValue, R.drawable.class);
		if (weatherResId != -1) {
			ivWeatherType.setImageResource(weatherResId);
		}

		final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


		popupWindow.setAnimationStyle(R.style.popup_window_animation);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();

		// Show popup window offset 1,1 to phoneButton at the screen center.
		popupWindow.showAtLocation(m_rootView, Gravity.TOP, 0, 170);
	}

//	private final OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {
//		@Override
//		public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//		}
//
//		@Override
//		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//			if (reverseGeoCodeResult == null
//					|| reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//				return;
//			}
//			ReverseGeoCodeResult.AddressComponent address = reverseGeoCodeResult.getAddressDetail();
//			curProvince = address.province;
//			String sAddress = address.province;
//			if (!address.city.isEmpty()) {
//				sAddress += " " + address.city;
//				curCity = address.city;
//			}
//			if (!address.district.isEmpty()) {
//				sAddress += " " + address.district;
//				curDistrict = address.district;
//			}
//			if (!address.street.isEmpty())			sAddress += " " + address.street;
//			if (!address.streetNumber.isEmpty())	sAddress += " " + address.streetNumber;
//			if (!sAddress.isEmpty()) {
//				Global.gAddress = sAddress;
//			}
//		}
//	};

	private void checkWatchEntries() {
		m_dlgProgress.show();
		HttpAPI.getWatchList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				boolean isRegistered = false;

				m_dlgProgress.dismiss();
				Util.clearWatchTable();
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					JSONArray dataArrayObject = jsonObject.getJSONArray("data");
					if (dataArrayObject.length() > 0) {
						for (int i = 0; i < dataArrayObject.length(); i++) {
							JSONObject dataObject = dataArrayObject.getJSONObject(i);

							ItemWatchInfo itemWatchInfo = new ItemWatchInfo(dataObject);
							Util.addWatchEntry(itemWatchInfo);

							if (Util.monitoringWatchId == itemWatchInfo.id) {
								isRegistered = true;
								Util.setMoniteringWatchInfo(itemWatchInfo);
								Prefs.Instance().setMoniteringWatchSerial(itemWatchInfo.serial);
							}

							if (i == dataArrayObject.length() - 1 && !isRegistered) {
								Util.setMoniteringWatchInfo(Util.getAllWatchEntries().get(0));
								Prefs.Instance().setMoniteringWatchSerial(Util.getAllWatchEntries().get(0).serial);
								Prefs.Instance().commit();
							}
						}
					}
					else {
						Util.setMoniteringWatchInfo(null);
						Prefs.Instance().setMoniteringWatchSerial("");
						Prefs.Instance().commit();
					}

					initControls();
					setEventListener();
					RegisterReceiverJPushMsg();
					showInfoNotification();
				}
				catch (JSONException e) {
					m_dlgProgress.dismiss();
					Util.ShowDialogError(R.string.str_page_loading_failed);
					initControls();
					setEventListener();
					RegisterReceiverJPushMsg();
					showInfoNotification();
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
				initControls();
				setEventListener();
				RegisterReceiverJPushMsg();
				showInfoNotification();
			}
		}, ActivityMain.class.getSimpleName());
	}

	private void getAllNotifications() {
		m_dlgProgress.show();
		HttpAPI.getAllNotificationList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				m_dlgProgress.dismiss();
				Util.clearNotificationTable();
				try {
					JSONObject jsonObject = new JSONObject(result);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
//						String sMsg = jsonObject.getString("msg");
//						Util.ShowDialogError(sMsg);
						return;
					}

					JSONArray jsonArray = jsonObject.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						String dataString = object.getString("dataJson");
						JSONObject dataObject = new JSONObject(dataString);
						if (App.PUSH_INIT_FLAG == false) {
							int noti_id = object.getInt("id");
							if (noti_id > App.PUSH_LAST_ID) {
								App.PUSH_LAST_ID = noti_id;
							}
						}

						ItemNotification itemNotification = new ItemNotification(
								object.getInt("id"),
								dataObject.optString("type"),
								dataObject.optString("notice_type"),
								dataObject.optString("device_type"),
								dataObject.optString("device_serial"),
								dataObject.optInt("alarm_id"),
								dataObject.optLong("time"),
								object.optString("title"),
								object.optString("msg"),
								object.optInt("alarmStatus"));

						itemNotification.isRead = object.optBoolean("readStatus") ? 1: 0;
						Util.addNotification(itemNotification);

						//if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_ALARM) &&
						//		(itemNotification.alarmStatus == 0 || itemNotification.alarmStatus == 2) &&
						//		popupWindow == null) {
						//	Intent intent = new Intent(ActivityMain.this, ActivityNotification.class);
						//	startActivity(intent);
						//}
					}

					App.PUSH_INIT_FLAG = true;
					if (Util.hasAlarmWaiting()){
						Intent intent = new Intent(ActivityMain.this, ActivityNotification.class);
						startActivity(intent);
					}

					FragmentNewDiscover fragmentNewDiscover = (FragmentNewDiscover)((FragmentParentDiscover)(fragmentArrayList.get(0))).getChildFragment(FragmentNewDiscover.class.getSimpleName());
					if (fragmentNewDiscover != null) fragmentNewDiscover.updateNotificationState();
				}
				catch (Exception ignore) {

				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.show();
			}
		}, ActivityMain.class.getSimpleName());
	}
	class BaiduLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			if (bdLocation == null) {
				return;
			}
			double dLat = bdLocation.getLatitude();
			double dLon = bdLocation.getLongitude();
			if (dLat == Double.MIN_VALUE && dLon == Double.MIN_VALUE) {
				return;
			}
			if (dLat == 0 || dLon == 0) {
				return;
			}

			Global.gLatValue = bdLocation.getLatitude();
			Global.gLonValue = bdLocation.getLongitude();
			Global.gAddress = bdLocation.getAddrStr();
			curProvince = bdLocation.getProvince();
			curCity = bdLocation.getCity();
			curDistrict = bdLocation.getDistrict();

			tryUploadLocation();
		}
	}

	private void tryUploadLocation() {
		long cur_time = System.currentTimeMillis();
		if (m_up_loc_time == null || Math.abs(cur_time - m_up_loc_time) > Global.gLocInterval) {
			m_up_loc_time = cur_time;
		} else {
			return;
		}
		HttpAPI.updateLocation(Prefs.Instance().getUserToken(),
				Prefs.Instance().getUserPhone(),
				String.valueOf(Global.gLatValue),
				String.valueOf(Global.gLonValue),
				curProvince,
				curCity,
				curDistrict,
				new VolleyCallback() {
					@Override
					public void onSuccess(String result) {
						//success
					}

					@Override
					public void onError(Object error) {
						//fail
					}
				},
				ActivityMain.class.getSimpleName());
	}

//	Runnable locationRunnable = new Runnable() {
//		@Override
//		public void run() {
//			if (!Global.gLat.isEmpty() && !Global.gLon.isEmpty() && curProvince != null && !curProvince.isEmpty()) {
//
//			}
//
//			locationHandler.postDelayed(locationRunnable, 10 * 60 * 1000);
//		}
//	};

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(JCRoomEvent event) {
		if (Global.gLatValue == 0) {

		}

/*		switch (event.getEvent().) {
			is JCRoomEvent.Stop, is JCRoomEvent.Leave -> {
				finish()
			}
			is JCRoomEvent.ParticipantJoin, is JCRoomEvent.ParticipantLeft -> {
				layoutItemUI()
				refreshItemUI()
			}
			is JCRoomEvent.ParticipantUpdate -> {
				refreshItemUI()
				updateNetStatus(event.event.participant.netStatus)
			}

			is JCRoomEvent.Login -> {
				if (!event.event.result) {
					Toast.makeText(this, "client login failed, reason = " + event.event.reason, Toast.LENGTH_SHORT).show()
					return
				}
				joinToRoom("TEST", "TEST")
			}
			is JCRoomEvent.Join -> {
				if (event.event.result) {
					layoutItemUI()
					refreshItemUI()
				} else {
					when (event.event.reason) {
						JCMediaChannel.REASON_INVALID_PASSWORD -> Toast.makeText(this, getString(R.string.room_join_channel_failed_invalid_password), Toast.LENGTH_SHORT).show()
						JCMediaChannel.REASON_FULL -> Toast.makeText(this, getString(R.string.room_join_channel_failed_full), Toast.LENGTH_SHORT).show()
						JCMediaChannel.REASON_INTERNAL_ERROR -> Toast.makeText(this, getString(R.string.room_join_channel_failed_full), Toast.LENGTH_SHORT).show()
                        else -> Toast.makeText(this, getString(R.string.room_join_channel_failed), Toast.LENGTH_SHORT).show()
					}
				}
			}
			is JCRoomEvent.Logout -> {
			}
			is JCRoomEvent.NetChange -> {
			}
		}*/
	}
}
