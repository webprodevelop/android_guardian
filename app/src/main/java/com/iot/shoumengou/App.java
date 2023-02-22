//@formatter:off
package com.iot.shoumengou;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.activity.ActivityNotification;
import com.iot.shoumengou.fragment.discover.FragmentNewDiscover;
import com.iot.shoumengou.fragment.discover.FragmentParentDiscover;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.jpush.PushMessageReceiver;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class App extends Application {
	public static final String TAG = App.class.getSimpleName();
	private RequestQueue mRequestQueue;

	private static App		m_instance;

	private Prefs		m_prefs;

	private static Context context;

	private static Activity mActivity = null;

	int MAX_SERIAL_THREAD_POOL_SIZE = 1;
	final int MAX_CACHE_SIZE = 2 * 1024 * 1024; //2 MB

	public static boolean PUSH_INIT_FLAG = false;
	public static int 	PUSH_LAST_ID = 0;

	private final Handler notificationHandler = new Handler();

	private final Runnable runnableNotification = new Runnable() {
		@Override
		public void run() {
			try {
				getAllNotifications();
			}catch(Exception ex){

			}
			notificationHandler.postDelayed(runnableNotification, 10000);
		}
	};

	public static App Instance() {
		return m_instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		m_instance = this;
		App.context = getApplicationContext();
		Init();
		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				mActivity = activity;
			}

			@Override
			public void onActivityDestroyed(Activity activity) {
//				mActivity = null;
			}

			/** Unused implementation **/
			@Override
			public void onActivityStarted(Activity activity) {}

			@Override
			public void onActivityResumed(Activity activity) {
				mActivity = activity;
			}
			@Override
			public void onActivityPaused(Activity activity) {}

			@Override
			public void onActivityStopped(Activity activity) {}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
		});

		PUSH_INIT_FLAG = false;
		PUSH_LAST_ID = 0;

		if (android.os.Build.DEVICE.contains("HW")) {
			notificationHandler.postDelayed(runnableNotification, 10000);
		}
	}

	private boolean isMyServiceRunning(Class serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

    public static Context getAppContext() {
        return App.context;
    }

	public Activity getCurrentActivity() {
		return mActivity;
	}

	private void Init() {
		m_prefs = new Prefs(this);
		// Baidu
		SDKInitializer.initialize(this);
		SDKInitializer.setCoordType(CoordType.BD09LL);
		// JPush
		JPushInterface.setDebugMode(true);
		JPushInterface.init(getBaseContext());

//		if(!isMyServiceRunning(ServiceJPush.class)) {
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//				context.startForegroundService(new Intent(context, ServiceJPush.class));
//			} else {
//				context.startService(new Intent(context, ServiceJPush.class));
//			}
//		}
	}

	private static Network getNetwork() {
		BaseHttpStack stack = new HurlStack();
		return new BasicNetwork(stack);
	}

	public Prefs GetPrefs() {
		return m_prefs;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			Cache cache = new DiskBasedCache(context.getCacheDir(), MAX_CACHE_SIZE);
			Network network = getNetwork();
			mRequestQueue = new RequestQueue(cache, network, MAX_SERIAL_THREAD_POOL_SIZE);
//			mRequestQueue = Volley.newRequestQueue(getApplicationContext());

			mRequestQueue.start();
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	private void getAllNotifications() {
		if (Prefs.Instance().getUserToken().isEmpty() || (App.PUSH_INIT_FLAG == false)) {
			return;
		}
		HttpAPI.getAllNotificationList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), PUSH_LAST_ID, new VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						return;
					}

					JSONArray jsonArray = jsonObject.getJSONArray("data");

					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						String dataString = object.getString("dataJson");
						JSONObject dataObject = new JSONObject(dataString);

						if (PUSH_INIT_FLAG) {
							int noti_id = object.getInt("id");
							if (noti_id > PUSH_LAST_ID){
								PUSH_LAST_ID = noti_id;
							}
							PushMessageReceiver.processNotification(getAppContext(), dataString, object.optString("title"),
									object.optString("msg"), false);
						}
//						JSONObject dataObject = new JSONObject(dataString);

//						ItemNotification itemNotification = new ItemNotification(
//								object.getInt("id"),
//								dataObject.optString("type"),
//								dataObject.optString("notice_type"),
//								dataObject.optString("device_type"),
//								dataObject.optString("device_serial"),
//								dataObject.optInt("alarm_id"),
//								dataObject.optLong("time"),
//								object.optString("title"),
//								object.optString("msg"),
//								object.optInt("alarmStatus"));
//
//						itemNotification.isRead = object.optBoolean("readStatus") ? 1: 0;


					}
//					PUSH_INIT_FLAG = true;
				}
				catch (Exception ignore) {
				}
			}

			@Override
			public void onError(Object error) {
			}
		}, ActivityMain.class.getSimpleName());
	}
}
