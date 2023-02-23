//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ZoomControls;

import androidx.appcompat.app.AlertDialog;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterPeriod;
import com.iot.shoumengou.model.ItemFence;
import com.iot.shoumengou.model.ItemFencePeriod;
import com.iot.shoumengou.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ActivityFenceSet extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityFenceSet";

	private ImageView								ivBack;
	private AutoCompleteTextView					actSearch;
	private ImageView								ivSearch;
	private MapView									mvMap;
	private TextView								tvRadius;
	private TextView								tvConfirm;
	private TextView								tvFenceName;
	private TextView								tvAddress;
	private ImageView								ivAdd;
	private AdapterPeriod							periodAdapter;

	private BaiduMap								baiduMap;
	private SuggestionSearch						suggestionSearch;
	private List<SuggestionResult.SuggestionInfo>	suggestionInfoList;
	private ArrayAdapter<String>					adapterAddress;
	private MapStatus.Builder						mapStatusBuilder;
	private GeoCoder								geoCoder;
	private BaiduLocationListener					baiduLocationListener;
	private LocationClient							locationClient;

	private boolean									isFirstLocation = true;
	private ItemFence								curFence = null;
	private ArrayList<ItemFencePeriod>				itemFencePeriodArrayList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fence_set);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		curFence = (ItemFence) getIntent().getSerializableExtra("fence_data");
		if (curFence == null) {
			finish();
		}

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		actSearch = findViewById(R.id.ID_TXTVIEW_AUTO_ADDRESS);
		ivSearch = findViewById(R.id.ID_IMGVIEW_SEARCH);
		mvMap = findViewById(R.id.ID_VIEW_MAP);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);
		tvAddress = findViewById(R.id.ID_TXTVIEW_ADDRESS);
		tvRadius = findViewById(R.id.ID_BTN_RADIUS);
		tvFenceName = findViewById(R.id.ID_TXTVIEW_FENCE_NAME);
		ivAdd = findViewById(R.id.ID_IMGVIEW_ADD);
		ListView periodListView = findViewById(R.id.ID_LSTVIEW_PERIOD);
		itemFencePeriodArrayList = curFence.getFencePeriodList();
		periodAdapter = new AdapterPeriod(this, itemFencePeriodArrayList);
		periodListView.setAdapter(periodAdapter);

		if (curFence.name != null && !curFence.name.isEmpty()) {
			tvFenceName.setText(curFence.name);
		} else {
			tvFenceName.setText(R.string.str_electronic_fence_name);
		}
		tvRadius.setText(curFence.getRadius(this));

		adapterAddress = new ArrayAdapter<>(this, R.layout.listitem_address);
		actSearch.setAdapter(adapterAddress);

		mvMap.showZoomControls(false);
		mvMap.showScaleControl(false);
		View child = mvMap.getChildAt(1);
		if ((child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.INVISIBLE);
		}

		baiduMap = mvMap.getMap();
		baiduMap.setMyLocationEnabled(true);

		suggestionSearch = SuggestionSearch.newInstance();
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		ivSearch.setOnClickListener(this);
		tvFenceName.setOnClickListener(this);
		tvRadius.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
		ivAdd.setOnClickListener(this);

		suggestionSearch.setOnGetSuggestionResultListener(suggestionResultListener);
		actSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				suggestionSearch.requestSuggestion(
						new SuggestionSearchOption().keyword(editable.toString()).city("丹东")
				);
			}
		});

		actSearch.setOnItemClickListener((parent, view, position, id) -> {
			SuggestionResult.SuggestionInfo info;
			if (suggestionInfoList == null)
				return;
			if (suggestionInfoList.size() == 0)
				return;
			if (position >= suggestionInfoList.size())
				position = suggestionInfoList.size() - 1;
			info = suggestionInfoList.get(position);
			curFence.lat = String.valueOf(info.pt.latitude);
			curFence.lon = String.valueOf(info.pt.longitude);
			// Point Position
			setPointMap(curFence.lat, curFence.lon, curFence.radius);

			actSearch.dismissDropDown();
		});

		mapStatusBuilder = new MapStatus.Builder();

		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(getGeoCoderResultListener);

		LocationClientOption locationClientOption = new LocationClientOption();
		locationClientOption.setScanSpan(5000);
		locationClientOption.setCoorType("BD09LL");		// gcj02
		locationClientOption.setIsNeedAddress(true);
		locationClientOption.setOpenGps(true);
		locationClient = new LocationClient(this);
		locationClient.setLocOption(locationClientOption);
		baiduLocationListener = new BaiduLocationListener();
		locationClient.registerLocationListener(baiduLocationListener);

		if (curFence.lat != null && curFence.lon != null) {
			setPointMap(curFence.lat, curFence.lon, curFence.radius);
		} else {
			locationClient.start();
		}

		baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				curFence.lat = String.valueOf(latLng.latitude);
				curFence.lon = String.valueOf(latLng.longitude);

				setPointMap(curFence.lat, curFence.lon, curFence.radius);

				// Dismiss DropDown
				actSearch.dismissDropDown();
			}
			@Override public void onMapPoiClick(MapPoi mapPoi) {
				LatLng latLng = mapPoi.getPosition();
				onMapClick(latLng);
			}
		});
	}

	private void setPointMap(String lat, String lng, int radius) {
		if (lat == null || lat.isEmpty() || lng == null || lng.isEmpty()) {
			return;
		}
		MyLocationData locData = new MyLocationData.Builder().accuracy(radius)
				.direction(-1)
				.latitude(Double.parseDouble(lat))
				.longitude(Double.parseDouble(lng))
				.build();

		baiduMap.setMyLocationData(locData);

		LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

		if (isFirstLocation) {
			isFirstLocation = false;
			mapStatusBuilder.target(latLng).zoom(18.0f);
			baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatusBuilder.build()));
		}
		OverlayOptions overlayOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_blue)).position(latLng);
		baiduMap.clear();
		baiduMap.addOverlay(overlayOptions);

		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
	}

	private final OnGetGeoCoderResultListener	getGeoCoderResultListener = new OnGetGeoCoderResultListener() {
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
				tvAddress.setText(sAddress);
				curFence.address = sAddress;

				// When called from m_clientLoc, Once getting address is finished, stop Client
				if (locationClient != null) {
					locationClient.stop();
					locationClient = null;
				}
			}
		}
	};

	private void showSearchDropDown() {
		adapterAddress.getFilter().filter("");
		actSearch.showDropDown();
	}

	/*private Runnable runnableDropDown = new Runnable() {
		@Override
		public void run() {
			showSearchDropDown();
		}
	};*/

	OnGetSuggestionResultListener suggestionResultListener = new OnGetSuggestionResultListener() {
		@Override
		public void onGetSuggestionResult(SuggestionResult result) {
			if (result == null)
				return;
			suggestionInfoList = result.getAllSuggestions();
			if (suggestionInfoList == null)
				return;
			adapterAddress.clear();
			for (SuggestionResult.SuggestionInfo info : suggestionInfoList) {
				String sAddress = "";
				if (info.district != null && info.district.compareTo("null") != 0)
					sAddress += info.district + " ";
				sAddress += info.key;
				adapterAddress.add(sAddress);
			}
			adapterAddress.notifyDataSetChanged();
			/*handlerDropDown.removeCallbacks(runnableDropDown);
			handlerDropDown.postDelayed(runnableDropDown, 1000);*/
			showSearchDropDown();
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		baiduMap.setMyLocationEnabled(false);
		mvMap.onDestroy();
		mvMap = null;

		if (locationClient != null) {
			locationClient.stop();
			locationClient.unRegisterLocationListener(baiduLocationListener);
		}
		if (suggestionSearch != null) {
			suggestionSearch.destroy();
		}
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_IMGVIEW_SEARCH:
				onSearch();
				break;
			case R.id.ID_BTN_CONFIRM:
				onConfirm();
				break;
			case R.id.ID_BTN_RADIUS:
				onRadius();
				break;
			case R.id.ID_TXTVIEW_FENCE_NAME:
				onFenceName();
				break;
			case R.id.ID_IMGVIEW_ADD:
				onAdd();
				break;
		}
	}

	private void onSearch() {

	}

	private void onConfirm() {
		if (curFence.name == null || curFence.name.isEmpty()) {
			Util.ShowDialogError(R.string.str_input_fence_name);
			return;
		}
		if (curFence.radius == 0) {
			Util.ShowDialogError(R.string.str_input_fence_radius);
			return;
		}
		if (curFence.lat == null || curFence.lon == null) {
			Util.ShowDialogError(R.string.str_input_fence_point);
			return;
		}
		Intent intent = new Intent();
		curFence.setFencePeriodList(itemFencePeriodArrayList);
		intent.putExtra("fence_data", curFence);
		intent.putExtra("add_fence", getIntent().getBooleanExtra("add_fence", true));
		setResult(RESULT_OK, intent);
		finish();
	}

	private void onRadius() {
		new AlertDialog.Builder(this)
				.setSingleChoiceItems(R.array.fence_radius_array, 0, (dialogInterface, which) -> {
					switch (which) {
						case 0:
							curFence.radius = 300;
							break;
						case 1:
							curFence.radius = 500;
							break;
						case 2:
							curFence.radius = 800;
							break;
						case 3:
							curFence.radius = 1000;
							break;
						case 4:
							curFence.radius = 2000;
							break;
						case 5:
							curFence.radius = 3000;
							break;
						case 6:
							curFence.radius = 4000;
							break;
						case 7:
							curFence.radius = 5000;
							break;
					}

					dialogInterface.dismiss();
					tvRadius.setText(curFence.getRadius(ActivityFenceSet.this));

					setPointMap(curFence.lat, curFence.lon, curFence.radius);
				})
				.show();
	}

	private void onFenceName() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View fenceNameView = layoutInflater.inflate(R.layout.alert_fence_name, null);

		final android.app.AlertDialog fenceNameDlg = new android.app.AlertDialog.Builder(this).create();

		final EditText edtFenceName = fenceNameView.findViewById(R.id.ID_EDTTEXT_FENCE_NAME);
		if (curFence.name != null) {
			edtFenceName.setText(curFence.name);
		}

		TextView btnCancel = fenceNameView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = fenceNameView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(v -> fenceNameDlg.dismiss());

		btnConfirm.setOnClickListener(v -> {
			curFence.name = edtFenceName.getText().toString();
			tvFenceName.setText(curFence.name);

			fenceNameDlg.dismiss();
		});

		fenceNameDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		fenceNameDlg.setView(fenceNameView);
		fenceNameDlg.show();
	}

	private void onAdd() {
		ItemFencePeriod lastFencePeriod = null;
		if (itemFencePeriodArrayList.size() > 0) {
			lastFencePeriod = itemFencePeriodArrayList.get(itemFencePeriodArrayList.size() - 1);
		}

		if (lastFencePeriod != null && (lastFencePeriod.startTime == -1 || lastFencePeriod.endTime == -1)) {
			return;
		}

		itemFencePeriodArrayList.add(new ItemFencePeriod());
		periodAdapter.notifyDataSetChanged();
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

			setPointMap(curFence.lat, curFence.lon, curFence.radius);
		}
	}
}
