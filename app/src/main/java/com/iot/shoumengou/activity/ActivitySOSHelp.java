//@formatter:off
package com.iot.shoumengou.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.iot.shoumengou.App;
import com.iot.shoumengou.Global;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterVolunteer;
import com.iot.shoumengou.helper.RoomActivity;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivitySOSHelp extends ActivityBase implements OnClickListener {
	private ImageView ivBack;
	private MapView mvMap;

	private TextView tvAlarmTitle, tvPolice, tvAbnormal, tvLocation, tvTime, tvInfo;
	private final ArrayList<String> volunteerList = new ArrayList<>();
	private final AdapterVolunteer itemsAdapter =
			new AdapterVolunteer(this, volunteerList);
	private LinearLayout llRescueRoute, llContactPlatform, llAbnormal;
	private TextView tvUser;
	private TextView tvSOSSecure;
	private TextView tvSOSAddress;

	private BaiduMap baiduMap;
	private MapStatus.Builder mapStatusBuilder;
	private GeoCoder geoCoder;

	private String alarmData;

	private ItemNotification itemNotification = null;

	List<LatLng> hLocations = new ArrayList<>();
	List<Overlay> hOverlays = new ArrayList<>();

	private LatLng sosLocation = null;
	private LatLng myLocation = null;
	private boolean isFirstLocation = true;

	Handler handler = new Handler();

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			Global.gLocInterval = 10000;
			App.Instance().cancelPendingRequests(getTag());

			HttpAPI.getAlarmDetail(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.alarmId, new VolleyCallback() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						int iRetCode = jsonObject.getInt("retcode");
						if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
							if(iRetCode == HttpAPIConst.RespCode.PROCESSED_ALARM){
								onBackPressed();
                                return;
							}
						}else {
                            alarmData = jsonObject.getString("data");
                            setSosData();
                        }
					} catch (JSONException ignored) {
					}

					handler.postDelayed(runnable, 10000); // 20211014 (scott) update from 60s to 10s during SOS
				}
				@Override
				public void onError(Object error) {
					handler.postDelayed(runnable, 10000); // 20211014 (scott) update from 60s to 10s during SOS
				}
			}, getTag());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sos_help);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_red));
		}

		itemNotification = (ItemNotification) getIntent().getSerializableExtra("notification_data");
		alarmData = getIntent().getStringExtra("alarm_data");
		if (itemNotification == null || alarmData.isEmpty()) {
			finish();
		}

		initControls();
		setEventListener();

		initLocation();
		setSosData();

		handler.postDelayed(runnable, 10000); // 20211014 (scott) update from 60s to 10s during SOS
	}

	@Override
	protected void onPause() {
		super.onPause();

		App.Instance().cancelPendingRequests(getTag());
		handler.removeCallbacks(runnable);

		Global.gLocInterval = 600000;
	}



	@Override
	protected void onResume() {
		super.onResume();

		App.Instance().cancelPendingRequests(getTag());
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, 10000); // 20211014 (scott) update from 60s to 10s during SOS
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
		}
	}

	@Override
	protected void initControls() {
		super.initControls();

		tvAlarmTitle = findViewById(R.id.ID_TXTVIEW_TITLE);
		tvPolice = findViewById(R.id.ID_TXTVIEW_SOS_POLICY);
		tvAbnormal = findViewById(R.id.ID_TXTVIEW_SOS_ABNORMAL);
		tvLocation = findViewById(R.id.ID_TXTVIEW_SOS_PLACE);
		tvTime = findViewById(R.id.ID_TXTVIEW_SOS_TIME);

		ListView lvVolunteers = findViewById(R.id.ID_LIST_VOLUNTEER);
		lvVolunteers.setAdapter(itemsAdapter);
		tvInfo = findViewById(R.id.ID_TXTVIEW_SOS_INFO);

		llRescueRoute = findViewById(R.id.ID_LL_RESCURE_ROUTE);
		llContactPlatform = findViewById(R.id.ID_LL_CONTACT_PLATFORM);
		llAbnormal = findViewById(R.id.ID_LL_SOS_ABNORMAL);

		tvUser = findViewById(R.id.ID_TXTVIEW_SOS_USER);
		tvSOSSecure = findViewById(R.id.ID_TXTVIEW_SOS_SOURCE);
		tvSOSAddress = findViewById(R.id.ID_TXTVIEW_SOS_ADDRESS);

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		mvMap = findViewById(R.id.ID_VIEW_MAP);

		mvMap.showZoomControls(false);
		mvMap.showScaleControl(false);
		View child = mvMap.getChildAt(1);
		if ((child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}

		baiduMap = mvMap.getMap();
//		baiduMap.setMyLocationEnabled(true);

		if (itemNotification != null) {
			tvTime.setText(String.format(getResources().getString(R.string.str_sos_time), Util.getDateTimeFormatString(new Date(itemNotification.time))));
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(ActivitySOSHelp.this, ActivityNotification.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	@Override
	protected String getTag() {
		return "ActivitySOSHelp";
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);

		mapStatusBuilder = new MapStatus.Builder();

		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);

		llRescueRoute.setOnClickListener(this);
		llContactPlatform.setOnClickListener(this);
	}

	@Override
	public void showNotificationPopup(ItemNotification itemNotification) {
		super.showNotificationPopup(itemNotification);

		if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_NOTICE)
				&& itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_UPDATE_ALARM_INFO)
				&& this.itemNotification.alarmId == itemNotification.alarmId) {
			HttpAPI.getAlarmDetail(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.alarmId, new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						int iRetCode = jsonObject.getInt("retcode");
                        if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                            if(iRetCode == HttpAPIConst.RespCode.PROCESSED_ALARM){
                                onBackPressed();
                                return;
                            }
                        }else {
                            alarmData = jsonObject.getString("data");
                            setSosData();
                        }
					} catch (JSONException ignored) {
					}
				}
				@Override
                public void onError(Object error) {

                }
            }, getTag());
		}
	}

	private void initLocation() {
	}

	@SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
	private void setSosData() {
		tvUser.setText("");
		volunteerList.clear();

		try {
			JSONObject jsonObject = new JSONObject(alarmData);
			JSONArray contactObject = jsonObject.optJSONArray("contact");
			JSONArray volunteers = jsonObject.optJSONArray("volunteerList");
			JSONObject infoObject = jsonObject.optJSONObject("info");
			JSONObject statusObject = jsonObject.optJSONObject("status");

			if (contactObject != null && contactObject.length() > 0) {
				tvUser.setText(String.format(getString(R.string.str_volunteer), contactObject.getJSONObject(0).optString("name")));

				tvSOSSecure.setText(String.format(getString(R.string.str_distance_rescue), "100"));
				tvSOSAddress.setText(String.format(getString(R.string.str_contact), contactObject.getJSONObject(0).optString("phone")));
			}

			baiduMap.clear();
			hLocations.clear();
			hOverlays.clear();
			OverlayOptions overlayOptions;
			if (infoObject != null) {
				tvAlarmTitle.setText(infoObject.optString("alarmTitle"));
				tvPolice.setText(infoObject.optString("title"));
				if (infoObject.optString("content").isEmpty()){
					llAbnormal.setVisibility(View.GONE);
				}else {
					tvAbnormal.setText(infoObject.optString("content"));
				}
				tvLocation.setText(infoObject.optString("place"));
				tvTime.setText(infoObject.optString("create_time"));

				if (!infoObject.optString("lat").isEmpty() &&
						!infoObject.optString("lon").isEmpty()) {
					try {
						sosLocation = new LatLng(Double.parseDouble(infoObject.optString("lat")), Double.parseDouble(infoObject.optString("lon")));
						overlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.rescue_marker)).position(sosLocation).zIndex(2);
						baiduMap.addOverlay(overlayOptions);
					}catch(Exception ignored) {

					}
				}
			}

			if (sosLocation != null) {
				double distance = statusObject.optDouble("distance") * 1000;
				if (distance > 1000) {
					distance /= 1000;
					tvInfo.setText(String.format(getString(R.string.str_distance_you_kilo), String.format("%.1f", distance)));
				}
				else {
					tvInfo.setText(String.format(getString(R.string.str_distance_you), String.format("%.0f", distance)));
				}
			}

			myLocation = new LatLng(Double.parseDouble(statusObject.optString("lat")), Double.parseDouble(statusObject.optString("lon")));
			overlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.self_marker)).position(myLocation).zIndex(2);
			baiduMap.addOverlay(overlayOptions);

			if (volunteers != null && volunteers.length() > 0) {
				String name = volunteers.getJSONObject(0).optString("volunteer_no");
				tvUser.setText(String.format(getString(R.string.str_volunteer), name));

				for (int i = 0; i < volunteers.length(); i++) {
					try {
						double distance = Double.parseDouble(volunteers.getJSONObject(i).optString("distance"));
						if (distance >= 1) {
							volunteerList.add(String.format(getString(R.string.str_distance_rescue_label_kilo), i+1, String.format("%.1f", distance)));
						}
						else {
							distance *= 1000;
							volunteerList.add(String.format(getString(R.string.str_distance_rescue_label), i+1, String.format("%.0f", distance)));
						}
					}
					catch (Exception ignored) {

					}
				}

				for (int i = 0; i < volunteers.length(); i++) {
					JSONObject item = volunteers.getJSONObject(i);
					item.optString("lat");
					if (!item.optString("lat").isEmpty() && !item.optString("lon").isEmpty()) {
						try {
							LatLng hLocation = new LatLng(Double.parseDouble(item.optString("lat")), Double.parseDouble(item.optString("lon")));
							overlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.person_marker)).position(hLocation).zIndex(2);
							Overlay hOverlay = baiduMap.addOverlay(overlayOptions);
							hLocations.add(hLocation);
							hOverlays.add(hOverlay);
						}catch(Exception ignored) {

						}
					}
				}
			}
			if (sosLocation != null) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				builder.include(sosLocation);
				MapStatusUpdate msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
				baiduMap.animateMapStatus(msUpdate);
				if (isFirstLocation) { // 20211014 (scott) set zoom 18 at first time.
					isFirstLocation = false;
					baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatusBuilder.zoom(18).build()));
				}
//				loadWatchPosition(sosLocation);
//				animateMapStatus(range);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		itemsAdapter.notifyDataSetChanged();
	}

	private final OnGetGeoCoderResultListener getGeoCoderResultListener = new OnGetGeoCoderResultListener() {
		@Override
		public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
			if (reverseGeoCodeResult == null
					|| reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			ReverseGeoCodeResult.AddressComponent address = reverseGeoCodeResult.getAddressDetail();
			// Set Address to m_txtAutoAddress
			String sAddress = address.province;
			if (!address.city.isEmpty())			sAddress += " " + address.city;
			if (!address.district.isEmpty())		sAddress += " " + address.district;
			if (!address.street.isEmpty())			sAddress += " " + address.street;
			if (!address.streetNumber.isEmpty())	sAddress += " " + address.streetNumber;
			if (!sAddress.isEmpty()) {
				tvSOSAddress.setText(sAddress);
			}
		}
	};

	private void startNavigationApp(Context context) {
		try {
			if(isAvilible(context,"com.baidu.BaiduMap")){//传入指定应用包名

					Intent intent = Intent.getIntent("intent://map/direction?destination=latlng:"+sosLocation.latitude+","+sosLocation.longitude+"|name:报警位置" +
							"&origin=" + "我的位置" + "&mode=walking#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent); //启动调用
			}else{//未安装
				//market为路径，id为包名
				//显示手机上所有的market商店
				Toast.makeText(context, "您尚未安装百度地图", Toast.LENGTH_LONG).show();
				Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		} catch (Exception e) {
		Log.e("intent", e.getMessage());
	}
	}

	/* 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName：应用包名
     * @return
			 */
	private boolean isAvilible(Context context, String packageName){
		//获取packagemanager
		final PackageManager packageManager = context.getPackageManager();
		//获取所有已安装程序的包信息
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		//用于存储所有已安装程序的包名
		List<String> packageNames = new ArrayList<String>();
		//从pinfo中将包名字逐一取出，压入pName list中
		if(packageInfos != null){
			for(int i = 0; i < packageInfos.size(); i++){
				String packName = packageInfos.get(i).packageName;
				packageNames.add(packName);
			}
		}
		//判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
		return packageNames.contains(packageName);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Global.gLocInterval = 600000;

//		baiduMap.setMyLocationEnabled(false);
		mvMap.onDestroy();
		mvMap = null;

		App.Instance().cancelPendingRequests(getTag());
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				onBackPressed();
				break;
			case R.id.ID_IMGVIEW_NAVIGATION:
//				onNavigation();
				break;
			case R.id.ID_LL_RESCURE_ROUTE:
//				drawTrack();
				onNavigation();
				break;
			case R.id.ID_LL_CONTACT_PLATFORM:
				onChartRequest();
				break;
		}
	}

	private void onNavigation() {
		startNavigationApp(getApplicationContext());
	}

	private void onChartRequest() {
		m_dlgProgress.show();
		HttpAPI.requestChat(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.alarmId, new VolleyCallback() {
			@RequiresApi(api = Build.VERSION_CODES.N)
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						m_dlgProgress.dismiss();

						LayoutInflater layoutInflater = LayoutInflater.from(ActivitySOSHelp.this);
						View confirmView = layoutInflater.inflate(R.layout.alert_chart, null);

						final AlertDialog confirmDlg = new AlertDialog.Builder(ActivitySOSHelp.this).create();

						TextView btnTitle = confirmView.findViewById(R.id.ID_TXTVIEW_TITLE);
						btnTitle.setText(jsonObject.getString("msg"));

						TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

						btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

						confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						confirmDlg.setView(confirmView);
						confirmDlg.show();

						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");

					RoomActivity.setChatId(dataObject.getString("roomId"));
					RoomActivity.setChatPass(dataObject.getString("password"));

					Intent intent = new Intent(ActivitySOSHelp.this, RoomActivity.class);
					startActivity(intent);
					m_dlgProgress.dismiss();
				}
				catch (JSONException e) {
					m_dlgProgress.dismiss();
					Util.ShowDialogError(R.string.str_api_failed);
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_api_failed);
			}
		}, "ActivitySOSHelp");
	}
}
