//@formatter:off
package com.iot.shoumengou.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ZoomControls;

import androidx.fragment.app.Fragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
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
import com.iot.shoumengou.App;
import com.iot.shoumengou.Global;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityFenceSet;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.adapter.AdapterFence;
import com.iot.shoumengou.fragment.discover.FragmentParentDiscover;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemFence;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FragmentLocation extends Fragment implements View.OnClickListener {
	private final String TAG = "FragmentLocation";

	private LinearLayout							llTrack;
	private LinearLayout							llSettings;
	private MapView									mvMap;
	private TextView								tvSource;
	private TextView								tvAddress;
	private TextView								tvTrackSource;
	private TextView								tvTrackAddress;
	private TextView								tvTrackTime;
	private ImageView								ivNavigation;
	private ImageView								ivTrackPlay;
	private SeekBar									skTrack;
	private ImageView								ivBack;
	private LinearLayout							llMenu;
	private LinearLayout							llTrackFlow;
	private LinearLayout							llNavigation;
	private LinearLayout							llTrackControl;

	private LinearLayout							llCommonMode;
	private LinearLayout							llPowerSavingMode;
	private LinearLayout							llEmergencyMode;
	private TextView								tvNew;
	private AdapterFence							fenceAdapter;
	private final ArrayList<ItemFence>				fenceArray = new ArrayList<>();

	private AlertDialog								settingsDlg;

	private BaiduMap								baiduMap;
	private MapStatus.Builder						mapStatusBuilder;
	private GeoCoder								geoCoder;
	//private BaiduLocationListener					baiduLocationListener;
	//private LocationClient						locationClient;
	//private LocationClientOption					locationClientOption;

	private ItemFence								curFence = null;

	private ItemWatchInfo							monitoringWatchInfo = null;
	private final ArrayList<ItemPosInfo>			posInfoArrayList = new ArrayList<>();

	private boolean									isFirstLocation = true;
	private int										frequencyMode = 1;

	private final Handler							handler = new Handler();
	private boolean									isPlay = false;

	private final Handler dismissHandler = new Handler();

	private final Runnable dismissRefreshRunnable = new Runnable() {
		@Override
		public void run() {
			getWatchPos();
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		dismissHandler.removeCallbacks(dismissRefreshRunnable);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		baiduMap.setMyLocationEnabled(false);
		mvMap.onDestroy();
		mvMap = null;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_location, container, false);

		isFirstLocation = true;

		initControls(rootView);
		setEventListener();

		requestWatchPos();

		return rootView;
	}

	@SuppressLint({"SetTextI18n", "InflateParams"})
	private void initControls(View layout) {
		ivBack = layout.findViewById(R.id.ID_IMGVIEW_BACK);
//		TextView tvTitle = layout.findViewById(R.id.ID_TXTVIEW_TITLE);
		llTrack = layout.findViewById(R.id.ID_LYT_TRACK);
		llSettings = layout.findViewById(R.id.ID_LYT_SETTINGS);
		mvMap = layout.findViewById(R.id.ID_VIEW_MAP);
		tvSource = layout.findViewById(R.id.ID_TXTVIEW_SOURCE);
		tvAddress = layout.findViewById(R.id.ID_TXTVIEW_ADDRESS);
		ivNavigation = layout.findViewById(R.id.ID_IMGVIEW_NAVIGATION);
		ivTrackPlay= layout.findViewById(R.id.ID_IMGVIEW_TRACK_PLAY);
		skTrack = layout.findViewById(R.id.ID_SEEK_TRACK);
		llMenu = layout.findViewById(R.id.ID_LYT_MENU);
		llNavigation = layout.findViewById(R.id.ID_LYT_NAVIGATION);
		llTrackControl = layout.findViewById(R.id.ID_LYT_TRACK_CONTROL);
		llTrackFlow = (LinearLayout) getLayoutInflater().inflate(R.layout.item_track_flow, null);
		tvTrackSource = llTrackFlow.findViewById(R.id.ID_TXTVIEW_TRACK_SOURCE);
		tvTrackAddress = llTrackFlow.findViewById(R.id.ID_TXTVIEW_TRACK_ADDRESS);
		tvTrackTime = llTrackFlow.findViewById(R.id.ID_TXTVIEW_TRACK_TIME);

		monitoringWatchInfo = Util.monitoringWatch;

		initSettingsView();

		mvMap.showZoomControls(false);
		mvMap.showScaleControl(false);
		View child = mvMap.getChildAt(1);
		if ((child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}

		baiduMap = mvMap.getMap();
		baiduMap.setMyLocationEnabled(true);

		if (monitoringWatchInfo != null) {
			tvSource.setText(monitoringWatchInfo.name + getString(R.string.str_owner_watch));
			tvTrackSource.setText(monitoringWatchInfo.name + getString(R.string.str_owner_watch));

			frequencyMode = monitoringWatchInfo.posUpdateMode;
			switch (frequencyMode) {
				case 1:
					onPositionFrequencyMode(llCommonMode);
					break;
				case 2:
					onPositionFrequencyMode(llPowerSavingMode);
					break;
				case 3:
					onPositionFrequencyMode(llEmergencyMode);
					break;
			}
		}

		checkWatchExpired();
	}

	private void checkWatchExpired() {
		ArrayList<ItemWatchInfo> watchInfoArrayList = Util.getAllWatchEntries();
		for (ItemWatchInfo itemWatchInfo : watchInfoArrayList) {
			if (itemWatchInfo.isExpiredService() && itemWatchInfo.serial.equals(Prefs.Instance().getMoniteringWatchSerial())) {
				showExpiredWatch(itemWatchInfo.name != null && !itemWatchInfo.name.isEmpty() ? itemWatchInfo.name : itemWatchInfo.serial);
			}
		}
	}

	private void showExpiredWatch(String watchName) {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View confirmView = layoutInflater.inflate(R.layout.alert_expire_watch_service, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
		TextView tvMsg = confirmView.findViewById(R.id.ID_TXTVIEW_MESSAGE);
		if (watchName != null) {
			tvMsg.setText(String.format(getString(R.string.str_expire_watch_service_alert), watchName));
		}

		btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

		btnConfirm.setOnClickListener(v -> {
			confirmDlg.dismiss();

			((ActivityMain) Objects.requireNonNull(getActivity())).goDevicePage();
		});

		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
	}

	@SuppressLint("InflateParams")
	private void initSettingsView() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View settingsView = layoutInflater.inflate(R.layout.alert_location_settings, null);

		llCommonMode = settingsView.findViewById(R.id.ID_LYT_COMMON_MODE);
		llPowerSavingMode = settingsView.findViewById(R.id.ID_LYT_POWER_SAVING_MODE);
		llEmergencyMode = settingsView.findViewById(R.id.ID_LYT_EMERGENCY_MODE);
		tvNew = settingsView.findViewById(R.id.ID_BTN_NEW);
		ListView lvFenceListView = settingsView.findViewById(R.id.ID_LSTVIEW_ELECTRONIC_FENCE);

		fenceAdapter = new AdapterFence(this, fenceArray);
		lvFenceListView.setAdapter(fenceAdapter);

		TextView btnCancel = settingsView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = settingsView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(v -> settingsDlg.dismiss());

		btnConfirm.setOnClickListener(v -> {
			settingsDlg.dismiss();

			setPosUpdateMode();
		});

		settingsDlg = new AlertDialog.Builder(getContext()).create();

		settingsDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		settingsDlg.setView(settingsView);

//		getElecfenceInfo();
	}

	private void setEventListener() {
		ivBack.setOnClickListener(this);
		llTrack.setOnClickListener(this);
		llSettings.setOnClickListener(this);
		ivNavigation.setOnClickListener(this);
		ivTrackPlay.setOnClickListener(this);
		llCommonMode.setOnClickListener(this);
		llPowerSavingMode.setOnClickListener(this);
		llEmergencyMode.setOnClickListener(this);
		tvNew.setOnClickListener(this);
		skTrack.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				drawTrack(i);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		mapStatusBuilder = new MapStatus.Builder();

		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);

		/*locationClientOption = new LocationClientOption();
		locationClientOption.setScanSpan(5000);
		locationClientOption.setCoorType("BD09LL");
		locationClientOption.setIsNeedAddress(true);
		locationClientOption.setOpenGps(true);
		locationClient = new LocationClient(getContext());
		locationClient.setLocOption(locationClientOption);
		baiduLocationListener = new BaiduLocationListener();
		locationClient.registerLocationListener(baiduLocationListener);
		locationClient.start();*/
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
			if (!address.district.isEmpty())			sAddress += " " + address.district;
			if (!address.street.isEmpty())			sAddress += " " + address.street;
			if (!address.streetNumber.isEmpty())	sAddress += " " + address.streetNumber;
			if (!sAddress.isEmpty()) {
				if (isTrackScreen()) {
					tvTrackAddress.setText(sAddress);
					for (ItemPosInfo itemPosInfo : posInfoArrayList) {
						if (itemPosInfo.lat == reverseGeoCodeResult.getLocation().latitude && itemPosInfo.lon == reverseGeoCodeResult.getLocation().longitude) {
							tvTrackTime.setText(itemPosInfo.time);
							break;
						}
					}
					OverlayOptions overlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromView(llTrackFlow))
							.position(new LatLng(reverseGeoCodeResult.getLocation().latitude, reverseGeoCodeResult.getLocation().longitude))
							.zIndex(2);
					baiduMap.addOverlay(overlayOptions);

					//animateMapStatus(latlngs);
				} else {
					tvAddress.setText(sAddress);
				}
			}
		}
	};

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				onBack();
				break;
			case R.id.ID_LYT_TRACK:
				onTrack();
				break;
			case R.id.ID_LYT_SETTINGS:
				onSettings();
				break;
			case R.id.ID_IMGVIEW_NAVIGATION:
				onNavigation();
				break;
			case R.id.ID_IMGVIEW_TRACK_PLAY:
				onTrackPlay();
				break;
			case R.id.ID_LYT_COMMON_MODE:
			case R.id.ID_LYT_POWER_SAVING_MODE:
			case R.id.ID_LYT_EMERGENCY_MODE:
				onPositionFrequencyMode(view);
				break;
			case R.id.ID_BTN_NEW:
				onNewFence();
				break;
		}
	}

	public boolean isTrackScreen() {
		return llTrackControl.getVisibility() == View.VISIBLE;
	}

	public void onBack() {
		if (llTrackControl.getVisibility() == View.GONE) {
			FragmentParentDiscover parentFrag = ((FragmentParentDiscover) FragmentLocation.this.getParentFragment());
			Objects.requireNonNull(parentFrag).popChildFragment(false);
		}
		else {
//			ivBack.setVisibility(View.GONE);
			llMenu.setVisibility(View.VISIBLE);
			llNavigation.setVisibility(View.VISIBLE);
			llTrackControl.setVisibility(View.GONE);

			handler.removeCallbacks(trackPlayRunnable);
			isPlay = false;

			getWatchPos();
		}
	}

	private void onTrack() {
		ivBack.setVisibility(View.VISIBLE);
		llMenu.setVisibility(View.GONE);
		llNavigation.setVisibility(View.GONE);
		llTrackControl.setVisibility(View.VISIBLE);
		ivTrackPlay.setImageResource(R.drawable.img_play);
		isPlay = false;

		getWatchPosList();
	}

	private void drawTrack(int position) {
		if (posInfoArrayList.size() < 2) {
			return;
		}

		List<LatLng> latlngs = new ArrayList<>();
		for (int i = 0; i < posInfoArrayList.size(); i++) {
			ItemPosInfo itemPosInfo = posInfoArrayList.get(i);
			latlngs.add(new LatLng(itemPosInfo.lat, itemPosInfo.lon));
		}

		List<BitmapDescriptor> textureList = new ArrayList<>();
		textureList.add(BitmapDescriptorFactory.fromResource(R.drawable.track_dot));

		List<Integer> indexList = new ArrayList<>();
		indexList.add(0);

		baiduMap.clear();

		OverlayOptions ooPolyline = new PolylineOptions().width(20)
				.color(Objects.requireNonNull(getActivity()).getColor(R.color.color_tab_selected))
				.points(latlngs).dottedLine(true)
				.customTextureList(textureList)
				.textureIndex(indexList);

		//Draw a line layer on the map, mPolyline: line layer
		Polyline mPolyline = (Polyline) baiduMap.addOverlay(ooPolyline);
		mPolyline.setDottedLine(true);

		animateMapStatus(latlngs);

		PointF pos1 = new PointF();
		PointF pos2 = new PointF();
		if (position < latlngs.size() - 1) {
			pos1.x = (float) latlngs.get(position).longitude;
			pos1.y = (float) latlngs.get(position).latitude;
			pos2.x = (float) latlngs.get(position + 1).longitude;
			pos2.y = (float) latlngs.get(position + 1).latitude;
		} else {
			pos1.x = (float) latlngs.get(position - 1).longitude;
			pos1.y = (float) latlngs.get(position - 1).latitude;
			pos2.x = (float) latlngs.get(position).longitude;
			pos2.y = (float) latlngs.get(position).latitude;
		}

		int rotate = (int) (Math.toDegrees(Math.atan2(pos2.y - pos1.y, pos2.x - pos1.x))) - 90;

		LatLng lastLatLng = latlngs.get(position);
		loadTrackPosition(lastLatLng.latitude, lastLatLng.longitude, rotate);
	}

	public void animateMapStatus(List<LatLng> points) {
		if (null == points || points.isEmpty()) {
			return;
		}
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (LatLng point : points) {
			builder.include(point);
		}
		MapStatusUpdate msUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.build());
		baiduMap.animateMapStatus(msUpdate);
	}

	private void onSettings() {
		if (monitoringWatchInfo == null) {
			return;
		}

		if (!monitoringWatchInfo.isManager) {
			Util.showToastMessage(getContext(), R.string.str_no_permission);
			return;
		}

		settingsDlg.show();
	}

	private void onNavigation() {
		if (tvAddress.getText().toString().isEmpty()) {
			return;
		}
		startNavigationApp();
	}

	private void startNavigationApp() {
		String curAddress = Global.gAddress;
		String curLat = String.valueOf(Global.gLatValue);
		String curLon = String.valueOf(Global.gLonValue);
		String uri = "intent://map/direction?origin=latlng:" + curLat + "," + curLon + "|name:" + curAddress +
				"&destination=" + tvAddress.getText().toString() + "&mode=driving" + "&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
		try {
			Intent intent = Intent.getIntent(uri);
			startActivity(intent);
		} catch (URISyntaxException | ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void onTrackPlay() {
		if (isPlay) {
			ivTrackPlay.setImageResource(R.drawable.img_play);
			handler.removeCallbacks(trackPlayRunnable);
			isPlay = false;
		} else {
			ivTrackPlay.setImageResource(R.drawable.img_pause);
			handler.post(trackPlayRunnable);
			isPlay = true;
		}
	}

	Runnable trackPlayRunnable = new Runnable() {
		@Override
		public void run() {
			if (skTrack.getProgress() == skTrack.getMax()) {
				skTrack.setProgress(0);
				ivTrackPlay.setImageResource(R.drawable.img_play);
				isPlay = false;
			} else {
				skTrack.setProgress(skTrack.getProgress() + 1);
				handler.postDelayed(trackPlayRunnable, 1000);
			}
		}
	};

	@SuppressLint("NonConstantResourceId")
	private void onPositionFrequencyMode(View view) {
		llCommonMode.setSelected(false);
		llPowerSavingMode.setSelected(false);
		llEmergencyMode.setSelected(false);

		view.setSelected(true);

		switch (view.getId()) {
			case R.id.ID_LYT_COMMON_MODE:
				frequencyMode = 1;
				break;
			case R.id.ID_LYT_POWER_SAVING_MODE:
				frequencyMode = 2;
				break;
			case R.id.ID_LYT_EMERGENCY_MODE:
				frequencyMode = 3;
				break;
		}
	}

	private void onNewFence() {
		startFenceAddEditActvity(new ItemFence(), true);
	}

	public void startFenceAddEditActvity(ItemFence fence, boolean addOrEdit) {
		curFence = fence;
		Intent intent = new Intent(getContext(), ActivityFenceSet.class);
		intent.putExtra("fence_data", curFence);
		intent.putExtra("add_fence", addOrEdit);
		Objects.requireNonNull(getActivity()).startActivityForResult(intent, ActivityMain.REQUEST_FENCE_ADDEDIT);
	}

	public void deleteFenceItem(int index) {
		fenceArray.remove(index);
		fenceAdapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityMain.REQUEST_FENCE_ADDEDIT) {
			if (resultCode == RESULT_OK) {
				if (data != null){
					boolean addOrEdit = data.getBooleanExtra("add_fence", true);
					ItemFence itemFence = (ItemFence) data.getSerializableExtra("fence_data");
					curFence.setFence(itemFence);
					if (addOrEdit) {
						fenceArray.add(curFence);
					}

					fenceAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private void requestWatchPos() {
		monitoringWatchInfo = Util.monitoringWatch;

		if (monitoringWatchInfo == null) {
			return;
		}

		((ActivityMain) Objects.requireNonNull(getActivity())).showProgress();
		dismissHandler.postDelayed(dismissRefreshRunnable, 10000);
		HttpAPI.requestWatchPos(Prefs.Instance().getUserToken(),
				Prefs.Instance().getUserPhone(),
				"5G",
				monitoringWatchInfo.serial,
				new VolleyCallback() {
					@Override
					public void onSuccess(String response) {

					}

					@Override
					public void onError(Object error) {

					}
				}, TAG);
	}

	@SuppressLint("SetTextI18n")
	public void getWatchPos() {
		dismissHandler.removeCallbacks(dismissRefreshRunnable);
		monitoringWatchInfo = Util.monitoringWatch;

		if (monitoringWatchInfo == null) {
			return;
		}

		tvSource.setText(monitoringWatchInfo.name + getString(R.string.str_owner_watch));
		tvTrackSource.setText(monitoringWatchInfo.name + getString(R.string.str_owner_watch));

		HttpAPI.getWatchPos(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					if (!dataObject.has("lat") || !dataObject.has("lon") || !dataObject.has("time")) {
						Util.showToastMessage(getActivity(), R.string.str_no_connect_watch);
						return;
					}
					String lat = dataObject.getString("lat");
					String lon = dataObject.getString("lon");

					try {
						loadWatchPosition(Double.parseDouble(lat), Double.parseDouble(lon));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
				}
			}

			@Override
			public void onError(Object error) {
				((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}

	public void setPosUpdateMode() {
		HttpAPI.setPosUpdateMode(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, frequencyMode, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				//m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					monitoringWatchInfo.posUpdateMode = frequencyMode;
					Util.updateWatchEntry(monitoringWatchInfo, monitoringWatchInfo);

					setElecFenceInfo();
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
				}
			}

			@Override
			public void onError(Object error) {
				//m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}

	public void getElecfenceInfo() {
		if (monitoringWatchInfo == null) {
			return;
		}

		HttpAPI.getElecFenceInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				//m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					JSONObject dataObj = jsonObject.getJSONObject("data");
					if (dataObj.has("fence_list")) {
						fenceArray.clear();

						String strFenceList = dataObj.getString("fence_list");
						JSONArray fenceArrayObj = new JSONArray(strFenceList);
						for (int i = 0; i < fenceArrayObj.length(); i++) {
							JSONObject fenceObject = fenceArrayObj.getJSONObject(i);
							ItemFence itemFence = new ItemFence(fenceObject);
							fenceArray.add(itemFence);
						}
					}

				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
				}
			}

			@Override
			public void onError(Object error) {
				//m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}

	public void setElecFenceInfo() {
		JSONArray fenceList = new JSONArray();
		for (ItemFence itemFence : fenceArray) {
			fenceList.put(itemFence.getJSONObject());
		}

		HttpAPI.setElecFenceInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, fenceList.toString(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				//m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
					}
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
				}
			}

			@Override
			public void onError(Object error) {
				//m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}

	public void getWatchPosList() {
		posInfoArrayList.clear();

		HttpAPI.getWatchPosList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				//m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					JSONArray dataArrayObject = jsonObject.getJSONArray("data");
					for (int i = 0; i < dataArrayObject.length(); i++) {
						JSONObject dataObject = dataArrayObject.getJSONObject(i);
						String lat = dataObject.getString("lat");
						String lon = dataObject.getString("lon");
						String time = dataObject.getString("time");

						try {
							ItemPosInfo itemPosInfo = new ItemPosInfo();
							itemPosInfo.time = time;
							itemPosInfo.lat = Double.parseDouble(lat);
							itemPosInfo.lon = Double.parseDouble(lon);

							posInfoArrayList.add(itemPosInfo);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}

					drawTrack(0);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						skTrack.setMin(0);
						skTrack.setMax(posInfoArrayList.size() - 1);
						//skTrack.setProgress(0);
					}
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
				}
			}

			@Override
			public void onError(Object error) {
				//m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}

	private void loadTrackPosition(double dLat, double dLon, float rotation) {
		LatLng latLng = new LatLng(dLat, dLon);
		MarkerOptions markerOptions = new MarkerOptions()
				.position(latLng)
				.zIndex(1)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.img_arrow))
				.rotate(rotation)
				.draggable(false);
		baiduMap.addOverlay(markerOptions);

		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
	}

	private void loadWatchPosition(double dLat, double dLon) {
		LatLng latLng = new LatLng(dLat, dLon);

		baiduMap.clear();

		MyLocationData locData = new MyLocationData.Builder().accuracy(100)
				.direction(-1)
				.latitude(dLat)
				.longitude(dLon)
				.build();
		baiduMap.setMyLocationData(locData);

		if (isFirstLocation) {
			isFirstLocation = false;
			mapStatusBuilder.target(latLng).zoom(18.0f);
			baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatusBuilder.build()));
		}

		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
	}

	class BaiduLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			if (bdLocation == null || mvMap == null) {
				return;
			}
			double dLat = bdLocation.getLatitude();
			double dLon = bdLocation.getLongitude();
			if (dLat == Double.MIN_VALUE && dLon == Double.MIN_VALUE) {
				bdLocation.setLatitude(40.006629);
				bdLocation.setLongitude(124.360903);
			}

			loadWatchPosition(bdLocation.getLatitude(), bdLocation.getLatitude());
		}
	}

	public class ItemPosInfo {
		public String time;
		public double lat;
		public double lon;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser) {
			App.Instance().cancelPendingRequests(TAG);
		}
	}
}
