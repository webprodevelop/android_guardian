//@formatter:off
package com.iot.shoumengou.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.activity.ActivityScan;
import com.iot.shoumengou.adapter.AdapterDevice;
import com.iot.shoumengou.fragment.discover.FragmentParentDiscover;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemDeviceInfo;
import com.iot.shoumengou.model.ItemSensorInfo;
import com.iot.shoumengou.model.ItemType;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;
import com.iot.shoumengou.view.DialogProgress;
import com.iot.shoumengou.view.ExpandableHeightListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentDevice extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
	private final String TAG = "FragmentDevice";

	private class SensorViewData {
		private View						view;
		private ImageView					Add;
		private TextView					noTxt;
		private TextView					Txt;
		private AdapterDevice					Adapter;
		private final ArrayList<ItemDeviceInfo>			Sensors = new ArrayList<>();

		public void initSensorControls(ViewGroup layout, int viewId, int AddId, int txtId, int noTxtId, int ListId) {
			view = layout.findViewById(viewId);
			Add = layout.findViewById(AddId);
			Txt = layout.findViewById(txtId);
			noTxt = layout.findViewById(noTxtId);
			ExpandableHeightListView list = layout.findViewById(ListId);
			list.setExpanded(true);
			Sensors.clear();
			Adapter = new AdapterDevice(getActivity(), Sensors);
			Adapter.mDevice = FragmentDevice.this;
			list.setAdapter(Adapter);
			Add.setOnClickListener(FragmentDevice.this);
			noTxt.setOnClickListener(FragmentDevice.this);
		}

		public void filteredAdd(ArrayList<ItemSensorInfo> sensors) {
			Sensors.clear();
			if (sensors != null) {
				for (ItemSensorInfo itemSensorInfo : sensors) {
					if (!itemSensorInfo.labelRelative.equals(spinnerPosition.getSelectedItem().toString())) {
						continue;
					}

					if ((spinnerWorkingStatus.getSelectedItemPosition() == 0 && !itemSensorInfo.netStatus) ||
							(spinnerWorkingStatus.getSelectedItemPosition() == 1 && itemSensorInfo.netStatus)) {
						continue;
					}

					Sensors.add(itemSensorInfo);
				}
			}
			if (Sensors.size() == 0) {
				noTxt.setVisibility(View.VISIBLE);
			} else {
				noTxt.setVisibility(View.GONE);
			}
			Adapter.notifyDataSetChanged();
		}

		public void addSensors(ArrayList<ItemSensorInfo> sensors) {
			Sensors.clear();
			if (sensors != null) {
				Sensors.addAll(sensors);
			}
			if (Sensors.size() == 0) {
				noTxt.setVisibility(View.VISIBLE);
			} else {
				noTxt.setVisibility(View.GONE);
			}
			Adapter.notifyDataSetChanged();
		}
	}

	private ImageView					ivBack;
	private TextView					tvFilter;
	private RelativeLayout				rlWatch;
	private ImageView					mAddWatch;
	private ExpandableHeightListView	mWatchList;
	private TextView					mNoWatch;
	private TextView					tvRelation;
	private AdapterDevice				mWatchAdapter;
	private final ArrayList<ItemDeviceInfo>		mWatchArray = new ArrayList<>();

	private final ArrayList<SensorViewData>	mTypeList = new ArrayList<>();

	private LinearLayout	llFilter;
	private Spinner spinnerPosition, spinnerDeviceType, spinnerWorkingStatus;
	private String [] belongsDataArray, statsDataArray;
	private ArrayAdapter beLongsArrayAdapter;
	private DialogProgress m_dlgProgress;

	public static boolean mRefresh = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_device, container, false);

		initControls(rootView);
		setEventListener();

		initTypeSensors();
		getWatchList(true);
		getSensorList();

		return rootView;
	}

	private void initSensorControls(ViewGroup layout) {
		mTypeList.clear();
		SensorViewData	typeData;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE01, R.id.ID_IMGVIEW_TYPE01_ADD, R.id.ID_TXTVIEW_TYPE01, R.id.ID_TXTVIEW_NO_TYPE01_SENSOR, R.id.ID_LSTVIEW_TYPE01_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE02, R.id.ID_IMGVIEW_TYPE02_ADD, R.id.ID_TXTVIEW_TYPE02, R.id.ID_TXTVIEW_NO_TYPE02_SENSOR, R.id.ID_LSTVIEW_TYPE02_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE03, R.id.ID_IMGVIEW_TYPE03_ADD, R.id.ID_TXTVIEW_TYPE03, R.id.ID_TXTVIEW_NO_TYPE03_SENSOR, R.id.ID_LSTVIEW_TYPE03_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE04, R.id.ID_IMGVIEW_TYPE04_ADD, R.id.ID_TXTVIEW_TYPE04, R.id.ID_TXTVIEW_NO_TYPE04_SENSOR, R.id.ID_LSTVIEW_TYPE04_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE05, R.id.ID_IMGVIEW_TYPE05_ADD, R.id.ID_TXTVIEW_TYPE05, R.id.ID_TXTVIEW_NO_TYPE05_SENSOR, R.id.ID_LSTVIEW_TYPE05_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE06, R.id.ID_IMGVIEW_TYPE06_ADD, R.id.ID_TXTVIEW_TYPE06, R.id.ID_TXTVIEW_NO_TYPE06_SENSOR, R.id.ID_LSTVIEW_TYPE06_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE07, R.id.ID_IMGVIEW_TYPE07_ADD, R.id.ID_TXTVIEW_TYPE07, R.id.ID_TXTVIEW_NO_TYPE07_SENSOR, R.id.ID_LSTVIEW_TYPE07_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE08, R.id.ID_IMGVIEW_TYPE08_ADD, R.id.ID_TXTVIEW_TYPE08, R.id.ID_TXTVIEW_NO_TYPE08_SENSOR, R.id.ID_LSTVIEW_TYPE08_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE09, R.id.ID_IMGVIEW_TYPE09_ADD, R.id.ID_TXTVIEW_TYPE09, R.id.ID_TXTVIEW_NO_TYPE09_SENSOR, R.id.ID_LSTVIEW_TYPE09_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE10, R.id.ID_IMGVIEW_TYPE10_ADD, R.id.ID_TXTVIEW_TYPE10, R.id.ID_TXTVIEW_NO_TYPE10_SENSOR, R.id.ID_LSTVIEW_TYPE10_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE11, R.id.ID_IMGVIEW_TYPE11_ADD, R.id.ID_TXTVIEW_TYPE11, R.id.ID_TXTVIEW_NO_TYPE11_SENSOR, R.id.ID_LSTVIEW_TYPE11_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE12, R.id.ID_IMGVIEW_TYPE12_ADD, R.id.ID_TXTVIEW_TYPE12, R.id.ID_TXTVIEW_NO_TYPE12_SENSOR, R.id.ID_LSTVIEW_TYPE12_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE13, R.id.ID_IMGVIEW_TYPE13_ADD, R.id.ID_TXTVIEW_TYPE13, R.id.ID_TXTVIEW_NO_TYPE13_SENSOR, R.id.ID_LSTVIEW_TYPE13_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE14, R.id.ID_IMGVIEW_TYPE14_ADD, R.id.ID_TXTVIEW_TYPE14, R.id.ID_TXTVIEW_NO_TYPE14_SENSOR, R.id.ID_LSTVIEW_TYPE14_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE15, R.id.ID_IMGVIEW_TYPE15_ADD, R.id.ID_TXTVIEW_TYPE15, R.id.ID_TXTVIEW_NO_TYPE15_SENSOR, R.id.ID_LSTVIEW_TYPE15_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE16, R.id.ID_IMGVIEW_TYPE16_ADD, R.id.ID_TXTVIEW_TYPE16, R.id.ID_TXTVIEW_NO_TYPE16_SENSOR, R.id.ID_LSTVIEW_TYPE16_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE17, R.id.ID_IMGVIEW_TYPE17_ADD, R.id.ID_TXTVIEW_TYPE17, R.id.ID_TXTVIEW_NO_TYPE17_SENSOR, R.id.ID_LSTVIEW_TYPE17_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE18, R.id.ID_IMGVIEW_TYPE18_ADD, R.id.ID_TXTVIEW_TYPE18, R.id.ID_TXTVIEW_NO_TYPE18_SENSOR, R.id.ID_LSTVIEW_TYPE18_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE19, R.id.ID_IMGVIEW_TYPE19_ADD, R.id.ID_TXTVIEW_TYPE19, R.id.ID_TXTVIEW_NO_TYPE19_SENSOR, R.id.ID_LSTVIEW_TYPE19_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE20, R.id.ID_IMGVIEW_TYPE20_ADD, R.id.ID_TXTVIEW_TYPE20, R.id.ID_TXTVIEW_NO_TYPE20_SENSOR, R.id.ID_LSTVIEW_TYPE20_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE21, R.id.ID_IMGVIEW_TYPE21_ADD, R.id.ID_TXTVIEW_TYPE21, R.id.ID_TXTVIEW_NO_TYPE21_SENSOR, R.id.ID_LSTVIEW_TYPE21_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE22, R.id.ID_IMGVIEW_TYPE22_ADD, R.id.ID_TXTVIEW_TYPE22, R.id.ID_TXTVIEW_NO_TYPE22_SENSOR, R.id.ID_LSTVIEW_TYPE22_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE23, R.id.ID_IMGVIEW_TYPE23_ADD, R.id.ID_TXTVIEW_TYPE23, R.id.ID_TXTVIEW_NO_TYPE23_SENSOR, R.id.ID_LSTVIEW_TYPE23_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE24, R.id.ID_IMGVIEW_TYPE24_ADD, R.id.ID_TXTVIEW_TYPE24, R.id.ID_TXTVIEW_NO_TYPE24_SENSOR, R.id.ID_LSTVIEW_TYPE24_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE25, R.id.ID_IMGVIEW_TYPE25_ADD, R.id.ID_TXTVIEW_TYPE25, R.id.ID_TXTVIEW_NO_TYPE25_SENSOR, R.id.ID_LSTVIEW_TYPE25_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE26, R.id.ID_IMGVIEW_TYPE26_ADD, R.id.ID_TXTVIEW_TYPE26, R.id.ID_TXTVIEW_NO_TYPE26_SENSOR, R.id.ID_LSTVIEW_TYPE26_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE27, R.id.ID_IMGVIEW_TYPE27_ADD, R.id.ID_TXTVIEW_TYPE27, R.id.ID_TXTVIEW_NO_TYPE27_SENSOR, R.id.ID_LSTVIEW_TYPE27_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE28, R.id.ID_IMGVIEW_TYPE28_ADD, R.id.ID_TXTVIEW_TYPE28, R.id.ID_TXTVIEW_NO_TYPE28_SENSOR, R.id.ID_LSTVIEW_TYPE28_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE29, R.id.ID_IMGVIEW_TYPE29_ADD, R.id.ID_TXTVIEW_TYPE29, R.id.ID_TXTVIEW_NO_TYPE29_SENSOR, R.id.ID_LSTVIEW_TYPE29_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE30, R.id.ID_IMGVIEW_TYPE30_ADD, R.id.ID_TXTVIEW_TYPE30, R.id.ID_TXTVIEW_NO_TYPE30_SENSOR, R.id.ID_LSTVIEW_TYPE30_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE31, R.id.ID_IMGVIEW_TYPE31_ADD, R.id.ID_TXTVIEW_TYPE31, R.id.ID_TXTVIEW_NO_TYPE31_SENSOR, R.id.ID_LSTVIEW_TYPE31_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE32, R.id.ID_IMGVIEW_TYPE32_ADD, R.id.ID_TXTVIEW_TYPE32, R.id.ID_TXTVIEW_NO_TYPE32_SENSOR, R.id.ID_LSTVIEW_TYPE32_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE33, R.id.ID_IMGVIEW_TYPE33_ADD, R.id.ID_TXTVIEW_TYPE33, R.id.ID_TXTVIEW_NO_TYPE33_SENSOR, R.id.ID_LSTVIEW_TYPE33_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE34, R.id.ID_IMGVIEW_TYPE34_ADD, R.id.ID_TXTVIEW_TYPE34, R.id.ID_TXTVIEW_NO_TYPE34_SENSOR, R.id.ID_LSTVIEW_TYPE34_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE35, R.id.ID_IMGVIEW_TYPE35_ADD, R.id.ID_TXTVIEW_TYPE35, R.id.ID_TXTVIEW_NO_TYPE35_SENSOR, R.id.ID_LSTVIEW_TYPE35_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE36, R.id.ID_IMGVIEW_TYPE36_ADD, R.id.ID_TXTVIEW_TYPE36, R.id.ID_TXTVIEW_NO_TYPE36_SENSOR, R.id.ID_LSTVIEW_TYPE36_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE37, R.id.ID_IMGVIEW_TYPE37_ADD, R.id.ID_TXTVIEW_TYPE37, R.id.ID_TXTVIEW_NO_TYPE37_SENSOR, R.id.ID_LSTVIEW_TYPE37_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE38, R.id.ID_IMGVIEW_TYPE38_ADD, R.id.ID_TXTVIEW_TYPE38, R.id.ID_TXTVIEW_NO_TYPE38_SENSOR, R.id.ID_LSTVIEW_TYPE38_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE39, R.id.ID_IMGVIEW_TYPE39_ADD, R.id.ID_TXTVIEW_TYPE39, R.id.ID_TXTVIEW_NO_TYPE39_SENSOR, R.id.ID_LSTVIEW_TYPE39_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE40, R.id.ID_IMGVIEW_TYPE40_ADD, R.id.ID_TXTVIEW_TYPE40, R.id.ID_TXTVIEW_NO_TYPE40_SENSOR, R.id.ID_LSTVIEW_TYPE40_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE41, R.id.ID_IMGVIEW_TYPE41_ADD, R.id.ID_TXTVIEW_TYPE41, R.id.ID_TXTVIEW_NO_TYPE41_SENSOR, R.id.ID_LSTVIEW_TYPE41_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE42, R.id.ID_IMGVIEW_TYPE42_ADD, R.id.ID_TXTVIEW_TYPE42, R.id.ID_TXTVIEW_NO_TYPE42_SENSOR, R.id.ID_LSTVIEW_TYPE42_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE43, R.id.ID_IMGVIEW_TYPE43_ADD, R.id.ID_TXTVIEW_TYPE43, R.id.ID_TXTVIEW_NO_TYPE43_SENSOR, R.id.ID_LSTVIEW_TYPE43_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE44, R.id.ID_IMGVIEW_TYPE44_ADD, R.id.ID_TXTVIEW_TYPE44, R.id.ID_TXTVIEW_NO_TYPE44_SENSOR, R.id.ID_LSTVIEW_TYPE44_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE45, R.id.ID_IMGVIEW_TYPE45_ADD, R.id.ID_TXTVIEW_TYPE45, R.id.ID_TXTVIEW_NO_TYPE45_SENSOR, R.id.ID_LSTVIEW_TYPE45_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE46, R.id.ID_IMGVIEW_TYPE46_ADD, R.id.ID_TXTVIEW_TYPE46, R.id.ID_TXTVIEW_NO_TYPE46_SENSOR, R.id.ID_LSTVIEW_TYPE46_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE47, R.id.ID_IMGVIEW_TYPE47_ADD, R.id.ID_TXTVIEW_TYPE47, R.id.ID_TXTVIEW_NO_TYPE47_SENSOR, R.id.ID_LSTVIEW_TYPE47_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE48, R.id.ID_IMGVIEW_TYPE48_ADD, R.id.ID_TXTVIEW_TYPE48, R.id.ID_TXTVIEW_NO_TYPE48_SENSOR, R.id.ID_LSTVIEW_TYPE48_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE49, R.id.ID_IMGVIEW_TYPE49_ADD, R.id.ID_TXTVIEW_TYPE49, R.id.ID_TXTVIEW_NO_TYPE49_SENSOR, R.id.ID_LSTVIEW_TYPE49_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE50, R.id.ID_IMGVIEW_TYPE50_ADD, R.id.ID_TXTVIEW_TYPE50, R.id.ID_TXTVIEW_NO_TYPE50_SENSOR, R.id.ID_LSTVIEW_TYPE50_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE51, R.id.ID_IMGVIEW_TYPE51_ADD, R.id.ID_TXTVIEW_TYPE51, R.id.ID_TXTVIEW_NO_TYPE51_SENSOR, R.id.ID_LSTVIEW_TYPE51_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE52, R.id.ID_IMGVIEW_TYPE52_ADD, R.id.ID_TXTVIEW_TYPE52, R.id.ID_TXTVIEW_NO_TYPE52_SENSOR, R.id.ID_LSTVIEW_TYPE52_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE53, R.id.ID_IMGVIEW_TYPE53_ADD, R.id.ID_TXTVIEW_TYPE53, R.id.ID_TXTVIEW_NO_TYPE53_SENSOR, R.id.ID_LSTVIEW_TYPE53_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE54, R.id.ID_IMGVIEW_TYPE54_ADD, R.id.ID_TXTVIEW_TYPE54, R.id.ID_TXTVIEW_NO_TYPE54_SENSOR, R.id.ID_LSTVIEW_TYPE54_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE55, R.id.ID_IMGVIEW_TYPE55_ADD, R.id.ID_TXTVIEW_TYPE55, R.id.ID_TXTVIEW_NO_TYPE55_SENSOR, R.id.ID_LSTVIEW_TYPE55_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE56, R.id.ID_IMGVIEW_TYPE56_ADD, R.id.ID_TXTVIEW_TYPE56, R.id.ID_TXTVIEW_NO_TYPE56_SENSOR, R.id.ID_LSTVIEW_TYPE56_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE57, R.id.ID_IMGVIEW_TYPE57_ADD, R.id.ID_TXTVIEW_TYPE57, R.id.ID_TXTVIEW_NO_TYPE57_SENSOR, R.id.ID_LSTVIEW_TYPE57_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE58, R.id.ID_IMGVIEW_TYPE58_ADD, R.id.ID_TXTVIEW_TYPE58, R.id.ID_TXTVIEW_NO_TYPE58_SENSOR, R.id.ID_LSTVIEW_TYPE58_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE59, R.id.ID_IMGVIEW_TYPE59_ADD, R.id.ID_TXTVIEW_TYPE59, R.id.ID_TXTVIEW_NO_TYPE59_SENSOR, R.id.ID_LSTVIEW_TYPE59_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE60, R.id.ID_IMGVIEW_TYPE60_ADD, R.id.ID_TXTVIEW_TYPE60, R.id.ID_TXTVIEW_NO_TYPE60_SENSOR, R.id.ID_LSTVIEW_TYPE60_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE61, R.id.ID_IMGVIEW_TYPE61_ADD, R.id.ID_TXTVIEW_TYPE61, R.id.ID_TXTVIEW_NO_TYPE61_SENSOR, R.id.ID_LSTVIEW_TYPE61_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE62, R.id.ID_IMGVIEW_TYPE62_ADD, R.id.ID_TXTVIEW_TYPE62, R.id.ID_TXTVIEW_NO_TYPE62_SENSOR, R.id.ID_LSTVIEW_TYPE62_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE63, R.id.ID_IMGVIEW_TYPE63_ADD, R.id.ID_TXTVIEW_TYPE63, R.id.ID_TXTVIEW_NO_TYPE63_SENSOR, R.id.ID_LSTVIEW_TYPE63_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE64, R.id.ID_IMGVIEW_TYPE64_ADD, R.id.ID_TXTVIEW_TYPE64, R.id.ID_TXTVIEW_NO_TYPE64_SENSOR, R.id.ID_LSTVIEW_TYPE64_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE65, R.id.ID_IMGVIEW_TYPE65_ADD, R.id.ID_TXTVIEW_TYPE65, R.id.ID_TXTVIEW_NO_TYPE65_SENSOR, R.id.ID_LSTVIEW_TYPE65_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE66, R.id.ID_IMGVIEW_TYPE66_ADD, R.id.ID_TXTVIEW_TYPE66, R.id.ID_TXTVIEW_NO_TYPE66_SENSOR, R.id.ID_LSTVIEW_TYPE66_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE67, R.id.ID_IMGVIEW_TYPE67_ADD, R.id.ID_TXTVIEW_TYPE67, R.id.ID_TXTVIEW_NO_TYPE67_SENSOR, R.id.ID_LSTVIEW_TYPE67_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE68, R.id.ID_IMGVIEW_TYPE68_ADD, R.id.ID_TXTVIEW_TYPE68, R.id.ID_TXTVIEW_NO_TYPE68_SENSOR, R.id.ID_LSTVIEW_TYPE68_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE69, R.id.ID_IMGVIEW_TYPE69_ADD, R.id.ID_TXTVIEW_TYPE69, R.id.ID_TXTVIEW_NO_TYPE69_SENSOR, R.id.ID_LSTVIEW_TYPE69_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE70, R.id.ID_IMGVIEW_TYPE70_ADD, R.id.ID_TXTVIEW_TYPE70, R.id.ID_TXTVIEW_NO_TYPE70_SENSOR, R.id.ID_LSTVIEW_TYPE70_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE71, R.id.ID_IMGVIEW_TYPE71_ADD, R.id.ID_TXTVIEW_TYPE71, R.id.ID_TXTVIEW_NO_TYPE71_SENSOR, R.id.ID_LSTVIEW_TYPE71_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE72, R.id.ID_IMGVIEW_TYPE72_ADD, R.id.ID_TXTVIEW_TYPE72, R.id.ID_TXTVIEW_NO_TYPE72_SENSOR, R.id.ID_LSTVIEW_TYPE72_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE73, R.id.ID_IMGVIEW_TYPE73_ADD, R.id.ID_TXTVIEW_TYPE73, R.id.ID_TXTVIEW_NO_TYPE73_SENSOR, R.id.ID_LSTVIEW_TYPE73_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE74, R.id.ID_IMGVIEW_TYPE74_ADD, R.id.ID_TXTVIEW_TYPE74, R.id.ID_TXTVIEW_NO_TYPE74_SENSOR, R.id.ID_LSTVIEW_TYPE74_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE75, R.id.ID_IMGVIEW_TYPE75_ADD, R.id.ID_TXTVIEW_TYPE75, R.id.ID_TXTVIEW_NO_TYPE75_SENSOR, R.id.ID_LSTVIEW_TYPE75_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE76, R.id.ID_IMGVIEW_TYPE76_ADD, R.id.ID_TXTVIEW_TYPE76, R.id.ID_TXTVIEW_NO_TYPE76_SENSOR, R.id.ID_LSTVIEW_TYPE76_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE77, R.id.ID_IMGVIEW_TYPE77_ADD, R.id.ID_TXTVIEW_TYPE77, R.id.ID_TXTVIEW_NO_TYPE77_SENSOR, R.id.ID_LSTVIEW_TYPE77_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE78, R.id.ID_IMGVIEW_TYPE78_ADD, R.id.ID_TXTVIEW_TYPE78, R.id.ID_TXTVIEW_NO_TYPE78_SENSOR, R.id.ID_LSTVIEW_TYPE78_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE79, R.id.ID_IMGVIEW_TYPE79_ADD, R.id.ID_TXTVIEW_TYPE79, R.id.ID_TXTVIEW_NO_TYPE79_SENSOR, R.id.ID_LSTVIEW_TYPE79_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE80, R.id.ID_IMGVIEW_TYPE80_ADD, R.id.ID_TXTVIEW_TYPE80, R.id.ID_TXTVIEW_NO_TYPE80_SENSOR, R.id.ID_LSTVIEW_TYPE80_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE81, R.id.ID_IMGVIEW_TYPE81_ADD, R.id.ID_TXTVIEW_TYPE81, R.id.ID_TXTVIEW_NO_TYPE81_SENSOR, R.id.ID_LSTVIEW_TYPE81_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE82, R.id.ID_IMGVIEW_TYPE82_ADD, R.id.ID_TXTVIEW_TYPE82, R.id.ID_TXTVIEW_NO_TYPE82_SENSOR, R.id.ID_LSTVIEW_TYPE82_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE83, R.id.ID_IMGVIEW_TYPE83_ADD, R.id.ID_TXTVIEW_TYPE83, R.id.ID_TXTVIEW_NO_TYPE83_SENSOR, R.id.ID_LSTVIEW_TYPE83_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE84, R.id.ID_IMGVIEW_TYPE84_ADD, R.id.ID_TXTVIEW_TYPE84, R.id.ID_TXTVIEW_NO_TYPE84_SENSOR, R.id.ID_LSTVIEW_TYPE84_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE85, R.id.ID_IMGVIEW_TYPE85_ADD, R.id.ID_TXTVIEW_TYPE85, R.id.ID_TXTVIEW_NO_TYPE85_SENSOR, R.id.ID_LSTVIEW_TYPE85_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE86, R.id.ID_IMGVIEW_TYPE86_ADD, R.id.ID_TXTVIEW_TYPE86, R.id.ID_TXTVIEW_NO_TYPE86_SENSOR, R.id.ID_LSTVIEW_TYPE86_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE87, R.id.ID_IMGVIEW_TYPE87_ADD, R.id.ID_TXTVIEW_TYPE87, R.id.ID_TXTVIEW_NO_TYPE87_SENSOR, R.id.ID_LSTVIEW_TYPE87_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE88, R.id.ID_IMGVIEW_TYPE88_ADD, R.id.ID_TXTVIEW_TYPE88, R.id.ID_TXTVIEW_NO_TYPE88_SENSOR, R.id.ID_LSTVIEW_TYPE88_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE89, R.id.ID_IMGVIEW_TYPE89_ADD, R.id.ID_TXTVIEW_TYPE89, R.id.ID_TXTVIEW_NO_TYPE89_SENSOR, R.id.ID_LSTVIEW_TYPE89_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE90, R.id.ID_IMGVIEW_TYPE90_ADD, R.id.ID_TXTVIEW_TYPE90, R.id.ID_TXTVIEW_NO_TYPE90_SENSOR, R.id.ID_LSTVIEW_TYPE90_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE91, R.id.ID_IMGVIEW_TYPE91_ADD, R.id.ID_TXTVIEW_TYPE91, R.id.ID_TXTVIEW_NO_TYPE91_SENSOR, R.id.ID_LSTVIEW_TYPE91_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE92, R.id.ID_IMGVIEW_TYPE92_ADD, R.id.ID_TXTVIEW_TYPE92, R.id.ID_TXTVIEW_NO_TYPE92_SENSOR, R.id.ID_LSTVIEW_TYPE92_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE93, R.id.ID_IMGVIEW_TYPE93_ADD, R.id.ID_TXTVIEW_TYPE93, R.id.ID_TXTVIEW_NO_TYPE93_SENSOR, R.id.ID_LSTVIEW_TYPE93_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE94, R.id.ID_IMGVIEW_TYPE94_ADD, R.id.ID_TXTVIEW_TYPE94, R.id.ID_TXTVIEW_NO_TYPE94_SENSOR, R.id.ID_LSTVIEW_TYPE94_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE95, R.id.ID_IMGVIEW_TYPE95_ADD, R.id.ID_TXTVIEW_TYPE95, R.id.ID_TXTVIEW_NO_TYPE95_SENSOR, R.id.ID_LSTVIEW_TYPE95_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE96, R.id.ID_IMGVIEW_TYPE96_ADD, R.id.ID_TXTVIEW_TYPE96, R.id.ID_TXTVIEW_NO_TYPE96_SENSOR, R.id.ID_LSTVIEW_TYPE96_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE97, R.id.ID_IMGVIEW_TYPE97_ADD, R.id.ID_TXTVIEW_TYPE97, R.id.ID_TXTVIEW_NO_TYPE97_SENSOR, R.id.ID_LSTVIEW_TYPE97_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE98, R.id.ID_IMGVIEW_TYPE98_ADD, R.id.ID_TXTVIEW_TYPE98, R.id.ID_TXTVIEW_NO_TYPE98_SENSOR, R.id.ID_LSTVIEW_TYPE98_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
		typeData = new SensorViewData();
		typeData.initSensorControls (layout, R.id.ID_TYPE99, R.id.ID_IMGVIEW_TYPE99_ADD, R.id.ID_TXTVIEW_TYPE99, R.id.ID_TXTVIEW_NO_TYPE99_SENSOR, R.id.ID_LSTVIEW_TYPE99_SENSOR);
		mTypeList.add(typeData);
		if (Util.sensorTypeList.size() == mTypeList.size()) return;
	}

	private void initTypeSensors () {
		for (int i=0; i<Util.sensorTypeList.size(); i++) {
			ItemType itemType = Util.sensorTypeMap.get(Util.sensorTypeList.get(i));
			SensorViewData vData = mTypeList.get(i);
			vData.Txt.setText(Objects.requireNonNull(itemType).name);
			vData.noTxt.setTag(itemType.type);
			vData.Add.setTag(itemType.type);
			vData.view.setVisibility(View.VISIBLE);
		}
	}

	private void setTypeSensor(int index, int pos){
		ItemType itemType = Util.sensorTypeMap.get(Util.sensorTypeList.get(index));
		SensorViewData vData = mTypeList.get(pos);
		vData.Txt.setText(Objects.requireNonNull(itemType).name);
		vData.noTxt.setTag(itemType.type);
		vData.Add.setTag(itemType.type);
		vData.view.setVisibility(View.VISIBLE);
	}

	private void initControls(ViewGroup layout) {
		initSensorControls (layout);

		ivBack = layout.findViewById(R.id.ID_IMG_BACK);
		tvFilter = layout.findViewById(R.id.ID_TEXT_FILTER);
		rlWatch = layout.findViewById(R.id.ID_RL_WATCH);
		tvRelation = layout.findViewById(R.id.ID_TEXT_RELATION);
		mAddWatch = layout.findViewById(R.id.ID_IMGVIEW_WATCH_ADD);
		mWatchList = layout.findViewById(R.id.ID_LSTVIEW_WATCH);
		mNoWatch = layout.findViewById(R.id.ID_TXTVIEW_NO_WATCH);

		mWatchList.setExpanded(true);
		mWatchArray.clear();
		mWatchAdapter = new AdapterDevice(getActivity(), mWatchArray);
		mWatchAdapter.mDevice = this;
		mWatchList.setAdapter(mWatchAdapter);

		llFilter = layout.findViewById(R.id.ID_LL_FILTER);
		llFilter.setVisibility(View.GONE);

		spinnerDeviceType = layout.findViewById(R.id.spinner_device_type);
		//Creating the ArrayAdapter instance having the country list
		String [] temp = getResources().getStringArray(R.array.device_type_array);
		String [] dataArray = new String[Util.sensorTypeList.size() + 1];
		dataArray[0] = temp[0];
		for(int i = 0; i < Util.sensorTypeList.size(); i++) {
			dataArray[i+1] = Objects.requireNonNull(Util.sensorTypeMap.get(Util.sensorTypeList.get(i))).shortName;
		}
		ArrayAdapter arrayAdapterDeviceType = new ArrayAdapter(getActivity(), R.layout.item_spinner, dataArray);
		arrayAdapterDeviceType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Setting the ArrayAdapter data on the Spinner
		spinnerDeviceType.setAdapter(arrayAdapterDeviceType);
		spinnerDeviceType.setSelection(0);

		spinnerPosition = layout.findViewById(R.id.spinner_position);
		//Creating the ArrayAdapter instance having the country list
		belongsDataArray = getResources().getStringArray(R.array.user_relation_array);
		beLongsArrayAdapter = new ArrayAdapter(getActivity(), R.layout.item_spinner, belongsDataArray);
		beLongsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Setting the ArrayAdapter data on the Spinner
		spinnerPosition.setAdapter(beLongsArrayAdapter);
		spinnerPosition.setSelection(0);

		spinnerWorkingStatus = layout.findViewById(R.id.spinner_working_status);
		//Creating the ArrayAdapter instance having the country list
		statsDataArray = getResources().getStringArray(R.array.working_type_array);
		ArrayAdapter arrayAdapterWorkingStatus = new ArrayAdapter(getActivity(), R.layout.item_spinner, statsDataArray);
		arrayAdapterWorkingStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Setting the ArrayAdapter data on the Spinner
		spinnerWorkingStatus.setAdapter(arrayAdapterWorkingStatus);
		spinnerWorkingStatus.setSelection(0);

		m_dlgProgress = new DialogProgress(getContext());
		m_dlgProgress.setCancelable(false);
	}

	private void setEventListener() {
		ivBack.setOnClickListener(this);
		tvFilter.setOnClickListener(this);
		mAddWatch.setOnClickListener(this);
		mNoWatch.setOnClickListener(this);

		spinnerDeviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					belongsDataArray = getResources().getStringArray(R.array.user_relation_array);
					tvRelation.setText(R.string.str_filter_relation);
				}
				else {
					belongsDataArray = getResources().getStringArray(R.array.belong_tab_array);
					tvRelation.setText(R.string.str_attribute);
				}
				beLongsArrayAdapter = new ArrayAdapter(getActivity(), R.layout.item_spinner, belongsDataArray);
				beLongsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				//Setting the ArrayAdapter data on the Spinner
				spinnerPosition.setAdapter(beLongsArrayAdapter);
				spinnerPosition.setSelection(0);

				statsDataArray = getResources().getStringArray(R.array.working_type_array);
				if (position == 0) {
					statsDataArray = getResources().getStringArray(R.array.working_type_array);
				}
				else {
					statsDataArray = getResources().getStringArray(R.array.sensor_working_type_array);
				}
				ArrayAdapter arrayAdapterWorkingStatus = new ArrayAdapter(getActivity(), R.layout.item_spinner, statsDataArray);
				arrayAdapterWorkingStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				//Setting the ArrayAdapter data on the Spinner
				spinnerWorkingStatus.setAdapter(arrayAdapterWorkingStatus);
				spinnerWorkingStatus.setSelection(0);

				onFilter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		spinnerPosition.setOnItemSelectedListener(this);
		spinnerWorkingStatus.setOnItemSelectedListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		FragmentParentDiscover parentFrag = ((FragmentParentDiscover) FragmentDevice.this.getParentFragment());
		switch (view.getId()) {
			case R.id.ID_IMG_BACK:
				if (llFilter.getVisibility() == View.GONE) {
					Objects.requireNonNull(parentFrag).popChildFragment(mRefresh);
					mRefresh = false;
				}
				else {
					llFilter.setVisibility(View.GONE);
					tvFilter.setVisibility(View.VISIBLE);
					rlWatch.setVisibility(View.VISIBLE);
					mWatchArray.clear();
					mWatchArray.addAll(Util.getAllWatchEntries());
					mWatchAdapter.notifyDataSetChanged();
					refreshSensors();
				}
				break;
			case R.id.ID_TEXT_FILTER:
				llFilter.setVisibility(View.VISIBLE);
				tvFilter.setVisibility(View.GONE);
				onFilter();
				break;
			case R.id.ID_IMGVIEW_WATCH_ADD:
			case R.id.ID_TXTVIEW_NO_WATCH:
				onAddWatch();
				break;
			default:
				String type = (String) view.getTag();
				if (type != null && !type.isEmpty())
					onAddSensor(type);
				break;
		}
	}

//	private int filteredAddTypeSensors (int index, int startPos) {
//		int selectedType = spinnerDeviceType.getSelectedItemPosition();
//		String type = Util.sensorTypeList.get(index);
//		if (type == null || type.isEmpty() || index != selectedType - 1) {
//			mTypeList.get(index).view.setVisibility(View.GONE);
//		} else {
//			mTypeList.get(index).view.setVisibility(View.VISIBLE);
//			mTypeList.get(index).filteredAdd(Util.sensorMap.get(type));
//		}
//		return startPos;
//	}

	private int filteredAddTypeSensors (int index, int startPos) {
		String type = Util.sensorTypeList.get(index);
		if (type == null || type.isEmpty()) {
//			mTypeList.get(index).view.setVisibility(View.GONE);
		} else {
			ArrayList<ItemSensorInfo> sensors = filterSensers(Util.sensorMap.get(type));
			if (sensors != null && !sensors.isEmpty()) {
				mTypeList.get(startPos).view.setVisibility(View.VISIBLE);
				mTypeList.get(startPos).addSensors(sensors);
				setTypeSensor(index, startPos);
				startPos ++;
			}
		}
		return startPos;
	}

	private int filteredAddTypeSensorsEmpty (int index, int startPos) {
		String type = Util.sensorTypeList.get(index);
		if (type == null || type.isEmpty()) {
			mTypeList.get(startPos).view.setVisibility(View.GONE);
			setTypeSensor(index, startPos);
			startPos ++;
		} else {
			ArrayList<ItemSensorInfo> sensors = filterSensers(Util.sensorMap.get(type));
			if (sensors == null || sensors.isEmpty()) {
				mTypeList.get(startPos).view.setVisibility(View.VISIBLE);
				mTypeList.get(startPos).addSensors(sensors);
				setTypeSensor(index, startPos);
				startPos ++;
			}
		}
		return startPos;
	}

	private ArrayList<ItemSensorInfo> filterSensers (ArrayList<ItemSensorInfo> sensors){
		ArrayList<ItemSensorInfo> filtered = new ArrayList<>();
		if (sensors != null) {
			for (ItemSensorInfo itemSensorInfo : sensors) {
				if (!itemSensorInfo.labelRelative.equals(spinnerPosition.getSelectedItem().toString())) {
					continue;
				}

				if ((spinnerWorkingStatus.getSelectedItemPosition() == 0 && !itemSensorInfo.netStatus) ||
						(spinnerWorkingStatus.getSelectedItemPosition() == 1 && itemSensorInfo.netStatus)) {
					continue;
				}

				filtered.add(itemSensorInfo);
			}
		}
		return filtered;
	}



	private int addTypeSensors (int index, int startPos) {
		String type = Util.sensorTypeList.get(index);
		if (type == null || type.isEmpty()) {
//			mTypeList.get(index).view.setVisibility(View.GONE);
		} else {
			ArrayList<ItemSensorInfo> sensors = Util.sensorMap.get(type);
			if (sensors != null && !sensors.isEmpty()) {
				mTypeList.get(startPos).view.setVisibility(View.VISIBLE);
				mTypeList.get(startPos).addSensors(sensors);
				setTypeSensor(index, startPos);
				startPos ++;
			}
		}
		return startPos;
	}

	private int addTypeSensorsEmpty (int index, int startPos) {
		String type = Util.sensorTypeList.get(index);
		if (type == null || type.isEmpty()) {
			mTypeList.get(startPos).view.setVisibility(View.GONE);
			setTypeSensor(index, startPos);
			startPos ++;
		} else {
			ArrayList<ItemSensorInfo> sensors = Util.sensorMap.get(type);
			if (sensors == null || sensors.isEmpty()) {
				mTypeList.get(startPos).view.setVisibility(View.VISIBLE);
				mTypeList.get(startPos).addSensors(sensors);
				setTypeSensor(index, startPos);
				startPos ++;
			}
		}
		return startPos;
	}

	public void refreshSensors () {
		if (mWatchArray.isEmpty()) {
			mNoWatch.setVisibility(View.VISIBLE);
		}
		int startPos = 0;
		for (int i=0; i<Util.sensorTypeList.size(); i++) {
			startPos = addTypeSensors(i, startPos);
		}
		for (int i=0; i<Util.sensorTypeList.size(); i++) {
			startPos = addTypeSensorsEmpty(i, startPos);
		}
	}

	private void onFilter() {
		if (llFilter.getVisibility() == View.VISIBLE) {
			ArrayList<ItemWatchInfo> watchList =  Util.getAllWatchEntries();

			mWatchArray.clear();
			if (spinnerDeviceType.getSelectedItemPosition() == 0) {
				rlWatch.setVisibility(View.VISIBLE);
				for (int i = 0; i < watchList.size(); i++) {
					ItemWatchInfo itemWatchInfo = watchList.get(i);
					String relation = spinnerPosition.getSelectedItem().toString();
					if (!itemWatchInfo.userRelation.equals(relation)) {
						continue;
					}
					if ((spinnerWorkingStatus.getSelectedItemPosition() == 0 && (!itemWatchInfo.netStatus || !itemWatchInfo.takeOnStatus)) ||
							(spinnerWorkingStatus.getSelectedItemPosition() == 1 && (itemWatchInfo.netStatus && itemWatchInfo.takeOnStatus))) {
						continue;
					}
					mWatchArray.add(itemWatchInfo);
				}
			}
			else {
				rlWatch.setVisibility(View.GONE);
			}
			mWatchAdapter.notifyDataSetChanged();

			int startPos = 0;
			for (int i=0; i<Util.sensorTypeList.size(); i++) {
				startPos = filteredAddTypeSensors (i, startPos);
			}

			for (int i=0; i<Util.sensorTypeList.size(); i++) {
				startPos = filteredAddTypeSensorsEmpty (i, startPos);
			}
		}
	}

	private void onAddWatch() {
		Intent intent = new Intent(getActivity(), ActivityScan.class);
		intent.putExtra("device_type", "");
		Objects.requireNonNull(getActivity()).startActivityForResult(intent, ActivityMain.REQUEST_SCAN_SMART_WATCH);
	}

	private void onAddSensor(String type) {
		Intent intent = new Intent(getActivity(), ActivityScan.class);
		intent.putExtra("device_type", type);
		Objects.requireNonNull(getActivity()).startActivityForResult(intent, ActivityMain.REQUEST_SCAN_FIRE_SENSOR);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ActivityMain.REQUEST_SCAN_SMART_WATCH || requestCode == ActivityMain.REQUEST_SCAN_FIRE_SENSOR || requestCode == ActivityMain.REQUEST_SCAN_SMOKE_SENSOR) {
			//if (resultCode == Activity.RESULT_OK) {
			mRefresh = true;
				getWatchList(false);
				getSensorList();
			//}
		}
		if (requestCode == ActivityMain.REQUEST_REFRESH_WATCH) {
			mRefresh = true;
			getWatchList(true);
			getSensorList();
		}
	}

	public void getWatchList(boolean refresh) {
		m_dlgProgress.show();
		if (!refresh) {
			m_dlgProgress.dismiss();

			mWatchArray.clear();
			mWatchArray.addAll(Util.getAllWatchEntries());

			if (mWatchArray.size() > 0) {
				if (mWatchList != null) {
					mWatchAdapter.notifyDataSetChanged();
				}

				mNoWatch.setVisibility(View.GONE);
			}
			return;
		}

		Util.clearWatchTable();
		HttpAPI.getWatchList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();
				mWatchArray.clear();

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
						ItemWatchInfo itemWatchInfo = new ItemWatchInfo(dataObject);
						Util.addWatchEntry(itemWatchInfo);
						mWatchArray.add(itemWatchInfo);
						if (Util.monitoringWatchId == itemWatchInfo.id) {
							Util.setMoniteringWatchInfo(itemWatchInfo);
						}
					}

					if (mWatchArray.size() == 0) {
						mNoWatch.setVisibility(View.VISIBLE);
					} else {
						mNoWatch.setVisibility(View.GONE);
					}

					mWatchAdapter.notifyDataSetChanged();
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}

	public void getSensorList() {
		m_dlgProgress.show();

		HttpAPI.getSensorList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();
				for (int i=0; i<Util.sensorTypeList.size(); i++) {
					Objects.requireNonNull(Util.sensorMap.get(Util.sensorTypeList.get(i))).clear();
				}

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

						ItemSensorInfo itemSensorInfo = new ItemSensorInfo(dataObject);
						Util.addSensorEntry(itemSensorInfo);
					}

					refreshSensors();
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		onFilter();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
