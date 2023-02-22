//@formatter:off
package com.iot.shoumengou.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityPushManage extends ActivityBase implements OnClickListener, CompoundButton.OnCheckedChangeListener {
	private final String TAG = "ActivityPushManage";

	private ImageView		mBackImg;
	private TextView		tvGetPermission;
	private LinearLayout	llFowlowSystem;
	private Switch			swSOSEmergency;
	private Switch			swFireAlarm;
	private Switch			swDisconnectWatch;
	private Switch			swDisconnectFireSensor;
	private Switch			swAbnormalHeartRate;
	private Switch			swWatchLowBattery;
	private Switch			swFireSensorLowBattery;
	private Switch			swElectronicFence;
	private Switch			swMorningReminder;
	private Switch			swEveningReminder;

	private boolean			enableNotification = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_manage);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();

		enableNotification =  NotificationManagerCompat.from(this).areNotificationsEnabled();
		boolean bFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		getAlarmSetInfo();
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		tvGetPermission = findViewById(R.id.ID_BTN_GET_PERMISSION);
		llFowlowSystem = findViewById(R.id.ID_LYT_FOLLOW_SYSTEM);
		swSOSEmergency = findViewById(R.id.ID_SWITCH_SOS_EMERGENCY);
		swFireAlarm = findViewById(R.id.ID_SWITCH_FIRE_ALARM);
		swDisconnectWatch = findViewById(R.id.ID_SWITCH_DISCONNECT_WATCH);
		swDisconnectFireSensor = findViewById(R.id.ID_SWITCH_DISCONNECT_FIRESENSOR);
		swAbnormalHeartRate = findViewById(R.id.ID_SWITCH_ABNORMAL_HEART_RATE);
		swWatchLowBattery = findViewById(R.id.ID_SWITCH_WATCH_LOW_BATTERY);
		swFireSensorLowBattery = findViewById(R.id.ID_SWITCH_FIRESENSOR_LOW_BATTERY);
		swElectronicFence = findViewById(R.id.ID_SWITCH_ELECTRONIC_FENCE);
		swMorningReminder = findViewById(R.id.ID_SWITCH_MORNING_REMINDER);
		swEveningReminder = findViewById(R.id.ID_SWITCH_EVENING_REMINDER);

		swSOSEmergency.setChecked(Prefs.Instance().getSosStatus());
		swFireAlarm.setChecked(Prefs.Instance().getFireStatus());
		swDisconnectWatch.setChecked(Prefs.Instance().getWatchNetStatus());
		swDisconnectFireSensor.setChecked(Prefs.Instance().getFireNetStatus());
		swAbnormalHeartRate.setChecked(Prefs.Instance().getHeartRateStatus());
		swWatchLowBattery.setChecked(Prefs.Instance().getWatchBatteryStatus());
		swFireSensorLowBattery.setChecked(Prefs.Instance().getFireBatteryStatus());
		swElectronicFence.setChecked(Prefs.Instance().getElecFenceStatus());
		swMorningReminder.setChecked(Prefs.Instance().getMorningStatus());
		swEveningReminder.setChecked(Prefs.Instance().getEveningStatus());
	}

	@Override
	protected void setEventListener() {
		mBackImg.setOnClickListener(this);
		tvGetPermission.setOnClickListener(this);
		llFowlowSystem.setOnClickListener(this);

		swSOSEmergency.setOnCheckedChangeListener(this);
		swFireAlarm.setOnCheckedChangeListener(this);
		swDisconnectWatch.setOnCheckedChangeListener(this);
		swDisconnectFireSensor.setOnCheckedChangeListener(this);
		swAbnormalHeartRate.setOnCheckedChangeListener(this);
		swWatchLowBattery.setOnCheckedChangeListener(this);
		swFireSensorLowBattery.setOnCheckedChangeListener(this);
		swElectronicFence.setOnCheckedChangeListener(this);
		swMorningReminder.setOnCheckedChangeListener(this);
		swEveningReminder.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				setAlarmSetInfo();
				finish();
				break;
			case R.id.ID_BTN_GET_PERMISSION:
				onGetPermission();
				break;
			case R.id.ID_LYT_FOLLOW_SYSTEM:
				onNotificationSettings();
				break;
		}
	}

	private void onNotificationSettings() {
		Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
				//.putExtra(Settings.EXTRA_CHANNEL_ID, MY_CHANNEL_ID);
		startActivity(settingsIntent);
	}

	private void onGetPermission() {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", getPackageName(), null);
		intent.setData(uri);
		startActivity(intent);
	}

	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
		if (!checked) {
			showConfirmChecked(compoundButton);
		}
	}

	private void showConfirmChecked(final CompoundButton compoundButton) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View confirmView = layoutInflater.inflate(R.layout.alert_confirm_off_notification, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				confirmDlg.dismiss();

				compoundButton.setChecked(true);
			}
		});

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				confirmDlg.dismiss();

				compoundButton.setChecked(false);
			}
		});

		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.setCancelable(false);
		confirmDlg.show();
	}

	private void setAlarmSetInfo() {
		m_dlgProgress.show();

		HttpAPI.setAlarmSetInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), swSOSEmergency.isChecked(), swFireAlarm.isChecked(), swDisconnectWatch.isChecked(),
				swDisconnectFireSensor.isChecked(), swAbnormalHeartRate.isChecked(), swWatchLowBattery.isChecked(), swFireSensorLowBattery.isChecked(),
				swElectronicFence.isChecked(), swMorningReminder.isChecked(), swEveningReminder.isChecked(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

                    String strData = jsonObject.getString("data");
                    JSONObject dataObject = new JSONObject(strData);
					boolean sosStatus = dataObject.getBoolean("sos_status");
					boolean fireStatus = dataObject.getBoolean("fire_status");
					boolean watchNetStatus = dataObject.getBoolean("watch_net_status");
					boolean fireNetStatus = dataObject.getBoolean("fire_net_status");
					boolean heartRateStatus = dataObject.getBoolean("heart_rate_status");
					boolean watchBatteryStatus = dataObject.getBoolean("watch_battery_status");
					boolean fireBatteryStatus = dataObject.getBoolean("fire_battery_status");
					boolean electronFenceStatus = dataObject.getBoolean("electron_fence_status");
					boolean morningStatus = dataObject.getBoolean("morning_status");
					boolean eveningStatus = dataObject.getBoolean("evening_status");

					Prefs.Instance().setSosStatus(sosStatus);
					Prefs.Instance().setFireStatus(fireStatus);
					Prefs.Instance().setWatchNetStatus(watchNetStatus);
					Prefs.Instance().setFireNetstatus(fireNetStatus);
					Prefs.Instance().setHeartrateStatus(heartRateStatus);
					Prefs.Instance().setWatchbatteryStatus(watchBatteryStatus);
					Prefs.Instance().setFirebatteryStatus(fireBatteryStatus);
					Prefs.Instance().setElecfenceSattus(electronFenceStatus);
					Prefs.Instance().setMorningStatus(morningStatus);
					Prefs.Instance().setEveningStatus(eveningStatus);
					Prefs.Instance().commit();
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
					return;
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}

	private void getAlarmSetInfo() {
		m_dlgProgress.show();

		HttpAPI.getAlarmSetInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					String strData = jsonObject.getString("data");
					JSONObject dataObject = new JSONObject(strData);
					boolean sosStatus = dataObject.getBoolean("sos_status");
					boolean fireStatus = dataObject.getBoolean("fire_status");
					boolean watchNetStatus = dataObject.getBoolean("watch_net_status");
					boolean fireNetStatus = dataObject.getBoolean("fire_net_status");
					boolean heartRateStatus = dataObject.getBoolean("heart_rate_status");
					boolean watchBatteryStatus = dataObject.getBoolean("watch_battery_status");
					boolean fireBatteryStatus = dataObject.getBoolean("fire_battery_status");
					boolean electronFenceStatus = dataObject.getBoolean("electron_fence_status");
					boolean morningStatus = dataObject.getBoolean("morning_status");
					boolean eveningStatus = dataObject.getBoolean("evening_status");

					Prefs.Instance().setSosStatus(sosStatus);
					Prefs.Instance().setFireStatus(fireStatus);
					Prefs.Instance().setWatchNetStatus(watchNetStatus);
					Prefs.Instance().setFireNetstatus(fireNetStatus);
					Prefs.Instance().setHeartrateStatus(heartRateStatus);
					Prefs.Instance().setWatchbatteryStatus(watchBatteryStatus);
					Prefs.Instance().setFirebatteryStatus(fireBatteryStatus);
					Prefs.Instance().setElecfenceSattus(electronFenceStatus);
					Prefs.Instance().setMorningStatus(morningStatus);
					Prefs.Instance().setEveningStatus(eveningStatus);
					Prefs.Instance().commit();

					swSOSEmergency.setChecked(sosStatus);
					swFireAlarm.setChecked(fireStatus);
					swDisconnectWatch.setChecked(watchNetStatus);
					swDisconnectFireSensor.setChecked(fireNetStatus);
					swAbnormalHeartRate.setChecked(heartRateStatus);
					swWatchLowBattery.setChecked(watchBatteryStatus);
					swFireSensorLowBattery.setChecked(fireBatteryStatus);
					swElectronicFence.setChecked(electronFenceStatus);
					swMorningReminder.setChecked(morningStatus);
					swEveningReminder.setChecked(eveningStatus);
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
					return;
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
	protected void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}
}
