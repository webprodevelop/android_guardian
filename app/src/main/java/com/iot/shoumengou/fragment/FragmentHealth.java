//@formatter:off
package com.iot.shoumengou.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityBindWatch;
import com.iot.shoumengou.activity.ActivityHealthDisplay;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.activity.ActivityNotification;
import com.iot.shoumengou.activity.ActivityScan;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemDeviceInfo;
import com.iot.shoumengou.model.ItemHeartRate;
import com.iot.shoumengou.model.ItemSensorInfo;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;
import com.iot.shoumengou.view.GraphView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

public class FragmentHealth extends Fragment implements View.OnClickListener {
	private final static String		TAG = "FragmentHealth";

	ImageView			ivSettings;
	ImageView			ivNotification;
	TextView			tvNotificationCount;
	ImageView			ivNextMonth;
	ImageView			ivPrevMonth;
	TextView			tvWatchUser;
	TextView			tvWeather;
	TextView			tvHeartRate;
	TextView			tvHighRate;
	TextView			tvLowRate;
	TextView			tvUpdateTime;
	TextView			tvDate;
	TextView			tvDisplayHealth;
	TextView			tvNewCount;
	TextView			tvNormalRateArea;
	GraphView			graphView;

	private Date						mDate = null;
	private ItemWatchInfo				moniteringWatchInfo = null;
	private ArrayList<ItemHeartRate>	heartRateList = new ArrayList<>();
	private Handler						handler = new Handler();

	static FragmentHealth fragment = null;

	private IOTDBHelper iotdbHelper;

	public static FragmentHealth getInstance() {
		if (fragment == null) {
			fragment = new FragmentHealth();
		}
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		iotdbHelper = new IOTDBHelper(context);
		super.onAttach(context);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		fragment = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_health, container, false);

		initControls(rootView);
		setEventListener();

		handler.post(getHeartRateHistoryRunnable);

		return rootView;
	}

	Runnable getHeartRateHistoryRunnable = new Runnable() {
		@Override
		public void run() {
			getHeartRateHistory();
			handler.postDelayed(getHeartRateHistoryRunnable, 2 * 60 * 60 * 1000);
		}
	};

	private void initControls(View layout) {
		ivSettings = layout.findViewById(R.id.ID_IMGVIEW_SETTING);
		ivNotification = layout.findViewById(R.id.ID_IMGVIEW_NOTIFICATION);
		tvNotificationCount = layout.findViewById(R.id.ID_TEXTVIEW_COUNT);
		ivNextMonth = layout.findViewById(R.id.ID_IMGVIEW_NEXT_MONTH);
		ivPrevMonth = layout.findViewById(R.id.ID_IMGVIEW_PREV_MONTH);
		tvWatchUser = layout.findViewById(R.id.ID_TXTVIEW_WATCH_USER);
		tvWeather = layout.findViewById(R.id.ID_TXTVIEW_WEATHER);
		tvHeartRate = layout.findViewById(R.id.ID_TXTVIEW_HEART_RATE);
		tvHighRate = layout.findViewById(R.id.ID_TXTVIEW_HEART_HIGH_RATE);
		tvLowRate = layout.findViewById(R.id.ID_TXTVIEW_HEART_LOW_RATE);
		tvUpdateTime = layout.findViewById(R.id.ID_TXTVIEW_UPDATE_TIME);
		tvDate = layout.findViewById(R.id.ID_TXTVIEW_DATE);
		tvDisplayHealth = layout.findViewById(R.id.ID_TXTVIEW_HEALTH_DISPLAY);
		tvNewCount = layout.findViewById(R.id.ID_TXTVIEW_NEW_COUNT);
		tvNormalRateArea = layout.findViewById(R.id.ID_TXTVIEW_NORMAL_RATE_AREA);
		graphView = layout.findViewById(R.id.ID_GRAPH_VIEW);

		Calendar calendar = Calendar.getInstance();
		mDate = calendar.getTime();
		setDate(true);

		setWatchUser(true);
		setHeartRateArea();

		checkWatchExpired();
		checkSensorExpired();
	}

	private void checkWatchExpired() {
		ArrayList<ItemWatchInfo> watchInfoArrayList = Util.getAllWatchEntries();
		for (ItemWatchInfo itemWatchInfo : watchInfoArrayList) {
			if (itemWatchInfo.isExpiredService() && itemWatchInfo.serial.equals(Prefs.Instance().getMoniteringWatchSerial())) {
				showExpiredWatch(itemWatchInfo.name);

				handler.postDelayed(() -> {
					Prefs.Instance().setMoniteringWatchSerial("");
					Prefs.Instance().commit();

					setWatchUser(false);
					setHeartRateArea();
					getHeartRateHistory();
					setHeartRateCount();
				}, 2000);
			}
		}
	}

	private void checkSensorExpired() {
		Iterator sensorIterator = Util.sensorMap.keySet().iterator();
		while(sensorIterator.hasNext()) {
			String type = (String)sensorIterator.next();
			for (ItemSensorInfo itemSensorInfo : Util.sensorMap.get(type)) {
				if (itemSensorInfo.isExpiredService()) {
					showExpiredSensor(itemSensorInfo.locationLabel, Util.sensorTypeMap.get(itemSensorInfo.type).name);
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		checkNotiAlarm();
	}

	private void setWatchUser(boolean whenOpen) {
		moniteringWatchInfo = Util.monitoringWatch;
		if (moniteringWatchInfo == null) {
			if (whenOpen) {
				showNoWatchAlert();
			}
			tvWatchUser.setText(R.string.str_bind_watch);
		} else {
			tvWatchUser.setText(moniteringWatchInfo.name);
		}
	}

	private void checkNotiAlarm() {
		tvNotificationCount.setText(String.valueOf(iotdbHelper.notificationAlarmCounts()));
	}

	private void showNoWatchAlert() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View confirmView = layoutInflater.inflate(R.layout.alert_no_watch, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

		btnConfirm.setOnClickListener(v -> {
			confirmDlg.dismiss();

			Intent intent = new Intent(getActivity(), ActivityScan.class);
			intent.putExtra("device_type", "");
			Objects.requireNonNull(getActivity()).startActivityForResult(intent, ActivityMain.REQUEST_SCAN_SMART_WATCH_FROM_HEALTH);
		});

		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
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

	private void showExpiredSensor(String groupName, String sensorType) {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View confirmView = layoutInflater.inflate(R.layout.alert_expire_sensor_service, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
		TextView tvMsg = confirmView.findViewById(R.id.ID_TXTVIEW_MESSAGE);
		if (groupName != null) {
			tvMsg.setText(String.format(getString(R.string.str_expire_sensor_service_alert), groupName, sensorType));
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityMain.REQUEST_SCAN_SMART_WATCH_FROM_HEALTH) {
			if (resultCode == Activity.RESULT_OK) {
				setWatchUser(false);
				setHeartRateArea();
				getHeartRateHistory();
				setHeartRateCount();
			}
		} else if (requestCode == ActivityMain.REQUEST_BIND_WATCH_LIST || requestCode == ActivityMain.REQUEST_NOTIFICATION_LIST || requestCode == ActivityMain.REQUEST_HEALTH_DISPLAY) {
			setWatchUser(false);
			setHeartRateArea();
			getHeartRateHistory();
			setHeartRateCount();
		}
	}

	private void setDate(boolean first) {
		if (!first) {
			Calendar calendar = Calendar.getInstance();
			long curTime = calendar.getTimeInMillis();
			if (curTime <= mDate.getTime()) {
				ivNextMonth.setOnClickListener(null);
				ivNextMonth.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.color_tab_normal), PorterDuff.Mode.SRC_IN);

				mDate.setTime(curTime);
			} else {
				ivNextMonth.setOnClickListener(this);
				ivNextMonth.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.color_text_blue), PorterDuff.Mode.SRC_IN);
			}
		} else {
			ivNextMonth.setOnClickListener(null);
			ivNextMonth.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.color_tab_normal), PorterDuff.Mode.SRC_IN);
		}

		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SpannableString content = new SpannableString(sdf.format(mDate));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		tvDate.setText(content);

		setHeartRate(tvDate.getText().toString());
	}

	private void setEventListener() {
		ivSettings.setOnClickListener(this);
		ivNotification.setOnClickListener(this);
		ivNextMonth.setOnClickListener(this);
		ivPrevMonth.setOnClickListener(this);
		tvWatchUser.setOnClickListener(this);
		tvWeather.setOnClickListener(this);
		tvDate.setOnClickListener(this);
		tvDisplayHealth.setOnClickListener(this);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_SETTING:
				onSettings();
				break;
			case R.id.ID_IMGVIEW_NOTIFICATION:
				onNotification();
				break;
			case R.id.ID_IMGVIEW_NEXT_MONTH:
				onNextMonth();
				break;
			case R.id.ID_IMGVIEW_PREV_MONTH:
				onPrevMonth();
				break;
			case R.id.ID_TXTVIEW_WATCH_USER:
				onWatchUser();
				break;
			case R.id.ID_TXTVIEW_WEATHER:
				onWeather();
				break;
			case R.id.ID_TXTVIEW_DATE:
				onDate();
				break;
			case R.id.ID_TXTVIEW_HEALTH_DISPLAY:
				onDisplayHealth();
				break;
		}
	}

	private void onSettings() {
		if (moniteringWatchInfo == null) {
			return;
		}

		if (!moniteringWatchInfo.isManager) {
			Util.showToastMessage(getContext(), R.string.str_no_permission);
			return;
		}

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View settingsView = layoutInflater.inflate(R.layout.alert_settings, null);

		final AlertDialog settingsDlg = new AlertDialog.Builder(getActivity()).create();

		final EditText edtMaxValue = settingsView.findViewById(R.id.ID_EDTTEXT_MAX_VALUE);
		final EditText edtMinValue = settingsView.findViewById(R.id.ID_EDTTEXT_MIN_VALUE);

		int lowRate = Prefs.DEFAULT_MIN_RATE;
		int highRate = Prefs.DEFAULT_MAX_RATE;
		if (moniteringWatchInfo != null) {
			lowRate = moniteringWatchInfo.heart_rate_low_limit;
			highRate = moniteringWatchInfo.heart_rate_high_limit;
		}

		edtMaxValue.setText(String.valueOf(highRate));
		edtMinValue.setText(String.valueOf(lowRate));

		TextView btnCancel = settingsView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = settingsView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(v -> settingsDlg.dismiss());

		btnConfirm.setOnClickListener(v -> {
			String strMaxValue = edtMaxValue.getText().toString();
			String strMinValue = edtMinValue.getText().toString();
			int maxValue = Prefs.DEFAULT_MAX_RATE;
			int minValue = Prefs.DEFAULT_MIN_RATE;
			try {
				if (!strMaxValue.isEmpty()) {
					maxValue = Integer.parseInt(strMaxValue);
				}
				if (!strMinValue.isEmpty()) {
					minValue = Integer.parseInt(strMinValue);
				}
			} catch (NumberFormatException ignored) {

			}

			if (maxValue < minValue) {
				Toast.makeText(getContext(), R.string.str_set_rate_error, Toast.LENGTH_LONG).show();
				return;
			}

			if (moniteringWatchInfo != null) {
				moniteringWatchInfo.heart_rate_high_limit = maxValue;
				moniteringWatchInfo.heart_rate_low_limit = minValue;

				Util.updateWatchEntry(moniteringWatchInfo, moniteringWatchInfo);
			}

			setHeartRateArea();
			setHeartRatePeriod(minValue, maxValue);

			settingsDlg.dismiss();
		});

		settingsDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		settingsDlg.setView(settingsView);
		settingsDlg.show();
	}

	private void setHeartRateArea() {
		int lowRate = Prefs.DEFAULT_MIN_RATE;
		int highRate = Prefs.DEFAULT_MAX_RATE;
		if (moniteringWatchInfo != null) {
			lowRate = moniteringWatchInfo.heart_rate_low_limit;
			highRate = moniteringWatchInfo.heart_rate_high_limit;

			graphView.setHeartRateRange(
					moniteringWatchInfo.heart_rate_low_limit,
					moniteringWatchInfo.heart_rate_high_limit,
					moniteringWatchInfo.blood_pressure_low_left_limit,
					moniteringWatchInfo.blood_pressure_low_right_limit);

		}
		else {
			graphView.setHeartRateRange(
					Prefs.DEFAULT_MIN_RATE,
					Prefs.DEFAULT_MIN_RATE,
					Prefs.DEFAULT_MIN_RATE,
					Prefs.DEFAULT_MIN_RATE);
		}

		tvNormalRateArea.setText(String.format(getString(R.string.str_normal_rate_area_value), lowRate, highRate));
	}

	private void setHeartRatePeriod(final int lowRate, final int highRate) {
		ItemWatchInfo itemWatchInfo = Util.findWatchEntry(Prefs.Instance().getMoniteringWatchSerial());
		if (itemWatchInfo == null) {
			return;
		}
		HttpAPI.setHeartRatePeriod(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemWatchInfo.id, lowRate, highRate, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					if (moniteringWatchInfo != null) {
						moniteringWatchInfo.heart_rate_low_limit = lowRate;
						moniteringWatchInfo.heart_rate_high_limit = highRate;

						Util.updateWatchEntry(moniteringWatchInfo, moniteringWatchInfo);
					}
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_update_failed);
				}
			}

			@Override
			public void onError(Object error) {
				Util.ShowDialogError(R.string.str_update_failed);
			}
		}, TAG);
	}

	private void onNotification() {
		Intent intent = new Intent(getActivity(), ActivityNotification.class);
		Objects.requireNonNull(getActivity()).startActivityForResult(intent, ActivityMain.REQUEST_NOTIFICATION_LIST);
	}

	private void onNextMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		mDate = calendar.getTime();
		setDate(false);
	}

	private void onPrevMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		mDate = calendar.getTime();
		setDate(false);
	}

	private void onWatchUser() {
		if (moniteringWatchInfo == null) {
			Intent intent = new Intent(getActivity(), ActivityScan.class);
			intent.putExtra("device_type", "");
			Objects.requireNonNull(getActivity()).startActivityForResult(intent, ActivityMain.REQUEST_SCAN_SMART_WATCH_FROM_HEALTH);
		} else {
			Intent intent = new Intent(getActivity(), ActivityBindWatch.class);
			Objects.requireNonNull(getActivity()).startActivityForResult(intent, ActivityMain.REQUEST_BIND_WATCH_LIST);
		}
	}

	private void onWeather() {
		((ActivityMain) Objects.requireNonNull(getActivity())).onWeather();
	}

	private void onDate() {
		final DatePickerDialog dlgDatePicker;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);

		dlgDatePicker = new DatePickerDialog(
				getActivity(),
				(view, year, month, dayOfMonth) -> {
					Calendar calendar1 = Calendar.getInstance();
					calendar1.set(Calendar.YEAR, year);
					calendar1.set(Calendar.MONTH, month);
					calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					mDate = calendar1.getTime();
					setDate(false);
				},
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)
		);
		dlgDatePicker.show();
	}

	public void onDisplayHealth() {
		Prefs.Instance().setNewHeartRateCount(0);
		Prefs.Instance().commit();

		Intent intent = new Intent(getActivity(), ActivityHealthDisplay.class);
		Objects.requireNonNull(getActivity()).startActivityForResult(intent, ActivityMain.REQUEST_HEALTH_DISPLAY);
	}

	public void getHeartRateHistory() {
		if (Prefs.Instance().getMoniteringWatchSerial().isEmpty()) {
			return;
		}

		ItemWatchInfo itemWatchInfo = Util.findWatchEntry(Prefs.Instance().getMoniteringWatchSerial());

		//m_dlgProgress.show();
		heartRateList.clear();

		HttpAPI.getHeartRateHistory(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemWatchInfo.id, new VolleyCallback() {
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

					JSONArray dataArray = jsonObject.getJSONArray("data");
					int newHeartRateCount = Prefs.Instance().getNewHeartRateCount();
					/*
					for (int i = 0; i < dataArray.length(); i++) {
						JSONObject dataObject = dataArray.getJSONObject(i);

						String checkTime = dataObject.getString("check_time");
						int heartRate = dataObject.getInt("heart_rate");

						ItemHeartRate newHeartRate = new ItemHeartRate(itemWatchInfo.id, Util.parseDateTimeSecFormatString(checkTime), heartRate, 0.0, 0, 0, "");

						ItemHeartRate itemHeartRate = iotdbHelper.findHeartRateEntry(newHeartRate.checkTime);
						if (itemHeartRate == null) {
							iotdbHelper.addHeartRateEntry(newHeartRate);
							newHeartRateCount++;
						}
					}
					 */

					Prefs.Instance().setNewHeartRateCount(newHeartRateCount);
					Prefs.Instance().commit();

					setHeartRateCount();
					setHeartRate(tvDate.getText().toString());
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_api_failed);
				}
			}

			@Override
			public void onError(Object error) {
				//m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_api_failed);
			}
		}, TAG);
	}

	private void setHeartRate(String strDate) {
		if (Prefs.Instance().getMoniteringWatchSerial().isEmpty()) {
			tvUpdateTime.setText(String.format(getString(R.string.str_update_time), ""));
			tvHeartRate.setText("-");
			tvLowRate.setText(String.format(getString(R.string.str_low_rate), "-"));
			tvHighRate.setText(String.format(getString(R.string.str_high_rate), "-"));

			return;
		}

		ItemWatchInfo itemWatchInfo = Util.findWatchEntry(Prefs.Instance().getMoniteringWatchSerial());
		//heartRateList = iotdbHelper.getDayHeartRateEntries(itemWatchInfo.id, strDate);

		graphView.setRateData(heartRateList);

		//tvHeartRate.setText(String.valueOf(iotdbHelper.getRecentHeartRate(itemWatchInfo.id)));
		if (heartRateList.size() > 0) {
			ItemHeartRate latestHeartRate = heartRateList.get(heartRateList.size() - 1);
			tvUpdateTime.setText(String.format(getString(R.string.str_update_time), latestHeartRate.getDateTimeSecString()));
			//tvLowRate.setText(String.format(getString(R.string.str_low_rate), String.valueOf(iotdbHelper.getMinHeartRate(itemWatchInfo.id, strDate))));
			//tvHighRate.setText(String.format(getString(R.string.str_high_rate), String.valueOf(iotdbHelper.getMaxHeartRate(itemWatchInfo.id, strDate))));
		} else {
			tvUpdateTime.setText(String.format(getString(R.string.str_update_time), ""));
			tvLowRate.setText(String.format(getString(R.string.str_low_rate), "-"));
			tvHighRate.setText(String.format(getString(R.string.str_high_rate), "-"));
		}
	}

	private void setHeartRateCount() {
		int newHeartRateCount = Prefs.Instance().getNewHeartRateCount();
		if (newHeartRateCount > 0) {
			if (newHeartRateCount > 99) {
				newHeartRateCount = 99;
			}
			tvNewCount.setVisibility(View.VISIBLE);
			tvNewCount.setText(String.valueOf(newHeartRateCount));
		} else {
			tvNewCount.setVisibility(View.INVISIBLE);
			tvNewCount.setText("");
		}
	}

	public void getWeatherInfo(String city, final String district) {
		//m_dlgProgress.show();

		HttpAPI.getWeatherInfo(city, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				//m_dlgProgress.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("code");
					if (iRetCode != 10000) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					JSONObject resultObject = jsonObject.getJSONObject("result");
					JSONArray weatherArray = resultObject.getJSONArray("HeWeather5");
					if (weatherArray.length() > 0) {
						JSONObject weatherObj = weatherArray.getJSONObject(0);
						JSONObject aqiObj = weatherObj.getJSONObject("aqi");
						JSONObject cityObj = aqiObj.getJSONObject("city");
						int	aqiValue = cityObj.getInt("aqi");
						String	qltyValue = cityObj.getString("qlty");
						JSONObject basicObj = weatherObj.getJSONObject("basic");
						JSONObject updateObj = basicObj.getJSONObject("update");
						String	locTimeValue = updateObj.getString("loc");

						int minTempValue = 0;
						int maxTempValue = 0;
						JSONArray dailyForecastArray = weatherObj.getJSONArray("daily_forecast");
						if (dailyForecastArray.length() > 0) {
							JSONObject todayObj = dailyForecastArray.getJSONObject(0);
							JSONObject tmpObj = todayObj.getJSONObject("tmp");
							minTempValue = tmpObj.getInt("min");
							maxTempValue = tmpObj.getInt("max");
						}

						JSONObject nowObj = weatherObj.getJSONObject("now");
						JSONObject condObj = nowObj.getJSONObject("cond");
						int codeValue = condObj.getInt("code");
						String weatherValue = condObj.getString("txt");
						int tmpValue = nowObj.getInt("tmp");

						((ActivityMain) Objects.requireNonNull(getActivity())).showWeatherPopup(codeValue, weatherValue, tmpValue, maxTempValue, minTempValue, district, locTimeValue, qltyValue, aqiValue);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            App.Instance().cancelPendingRequests(TAG);
        }
    }
}
