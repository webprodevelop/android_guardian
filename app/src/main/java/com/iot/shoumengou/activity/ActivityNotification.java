//@formatter:off
package com.iot.shoumengou.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterNotification;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityNotification extends ActivityBase {
	private ImageView						mBackImg;
	private ImageView						mReadAll;
	private ImageView						mDeleteAll;
	private ListView						mNotiList;
	private ArrayList<ItemNotification>		mNotiArray = new ArrayList<>();

	private ReceiverJPushMessage			m_receiverJPushMsg;

	public static boolean IS_FOREGROUND = false;

	boolean doubleBackToExitPressedOnce = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();

		registerReceiverJPushMsg();
	}

	private void loadNotificationList() {
		mNotiArray = Util.getAllNotifications(ActivityNotification.this);

		AdapterNotification mNotiAdapter = new AdapterNotification(this, mNotiArray);
		mNotiList.setAdapter(mNotiAdapter);

		mBackImg.setVisibility(View.VISIBLE);
		mReadAll.setVisibility(View.VISIBLE);
		mDeleteAll.setVisibility(View.VISIBLE);

		for(int i = 0; i < mNotiArray.size(); i++) {
			if (mNotiArray.get(i).type.equals(ItemNotification.PUSH_TYPE_ALARM) &&
					(mNotiArray.get(i).alarmStatus == 0 || mNotiArray.get(i).alarmStatus == 2)) {
				mBackImg.setVisibility(View.GONE);
				mReadAll.setVisibility(View.GONE);
				mDeleteAll.setVisibility(View.GONE);
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (mBackImg.getVisibility() == View.VISIBLE) {
			super.onBackPressed();
		}
		else {
			if (doubleBackToExitPressedOnce) {
				moveTaskToBack(true);
				return;
			}

			this.doubleBackToExitPressedOnce = true;
			Toast.makeText(this, R.string.str_again_exit, Toast.LENGTH_SHORT).show();

			new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 3000);
		}
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		mReadAll = findViewById(R.id.ID_IMGVIEW_READALL);
		mDeleteAll = findViewById(R.id.ID_IMGVIEW_DELETE);
		mNotiList = findViewById(R.id.ID_LSTVIEW_NOTIFICATION);
	}

	@Override
	protected void setEventListener() {
		mBackImg.setOnClickListener(view -> onBackPressed());
		mReadAll.setOnClickListener(view -> onReadAll());
		mDeleteAll.setOnClickListener(view -> onDeleteAll());
		mNotiList.setOnItemClickListener((adapterView, view, position, id) -> checkIsRead(mNotiArray.get(position)));
	}

	@Override
	protected void onPause() {
		super.onPause();
		IS_FOREGROUND = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadNotificationList();
		IS_FOREGROUND = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		IS_FOREGROUND = false;

		if (m_receiverJPushMsg != null) {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(m_receiverJPushMsg);
		}
	}

	private void onReadAll() {
		if (this.mNotiArray.size() > 0) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View confirmView = layoutInflater.inflate(R.layout.alert_logout, null);

			final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();

			TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
			TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
			TextView tvConfirm = confirmView.findViewById(R.id.ID_TEXT_CONFIRM);
			tvConfirm.setText(R.string.str_read_all_confirm);

			btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

			btnConfirm.setOnClickListener(v -> {
				confirmDlg.dismiss();
				readAllNotification();
			});

			confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			confirmDlg.setView(confirmView);
			confirmDlg.show();
		}
	}

	private void readAllNotification() {
		HttpAPI.readAllNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String result) {

			}

			@Override
			public void onError(Object error) {

			}
		}, ActivityNotification.class.getSimpleName());

		for (ItemNotification itemNotification : mNotiArray) {
			if (itemNotification.isRead == 0) {
				if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_CLOCK)) {
					itemNotification.isRead = 1;
					IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
					iotdbHelper.updateNotificationEntry(itemNotification, itemNotification);
				} else {
					itemNotification.isRead = 1;
					Util.updateNotificationEntry(itemNotification, itemNotification);
				}
			}
		}

		loadNotificationList();
	}

	private void onDeleteAll() {
		if (this.mNotiArray.size() > 0) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View confirmView = layoutInflater.inflate(R.layout.alert_logout, null);

			final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();

			TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
			TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
			TextView tvConfirm = confirmView.findViewById(R.id.ID_TEXT_CONFIRM);
			tvConfirm.setText(R.string.str_delete_all_confirm);

			btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

			btnConfirm.setOnClickListener(v -> {
				confirmDlg.dismiss();
				deleteAllNotification();
			});

			confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			confirmDlg.setView(confirmView);
			confirmDlg.show();
		}
	}

	private void deleteAllNotification() {
		HttpAPI.removeAllNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String result) {

			}

			@Override
			public void onError(Object error) {

			}
		}, ActivityNotification.class.getSimpleName());

		for (ItemNotification itemNotification : mNotiArray) {
			if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_CLOCK)) {
				IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
				iotdbHelper.deleteNotification(itemNotification.id);
			} else {
				// Need to implement by new API
				Util.deleteNotificationEntry(itemNotification.id);
			}
		}

		loadNotificationList();
	}

	private void registerReceiverJPushMsg() {
		m_receiverJPushMsg = new ReceiverJPushMessage();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(AppConst.ACTION_PUSH_RECEIVED);
		LocalBroadcastManager.getInstance(this).registerReceiver(m_receiverJPushMsg, filter);
	}

	public void deleteNotification(ItemNotification itemNotification) {
		m_dlgProgress.show();
		if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_CLOCK)) {
			m_dlgProgress.dismiss();
			IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
			iotdbHelper.deleteNotification(itemNotification.id);
			loadNotificationList();
		}
		else {
			HttpAPI.removeNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
				@Override
				public void onSuccess(String result) {
					m_dlgProgress.dismiss();
					Util.deleteNotificationEntry(itemNotification.id);
					loadNotificationList();
				}

				@Override
				public void onError(Object error) {
					m_dlgProgress.dismiss();
				}
			}, ActivityNotification.class.getSimpleName());
		}
	}

	private void checkIsRead(ItemNotification itemNotification) {
		if ((itemNotification.type.equals(ItemNotification.PUSH_TYPE_ALARM) && (itemNotification.alarmStatus == 0 || itemNotification.alarmStatus == 2)) ||
			Util.getAlarmCount() == 0) {
			if (itemNotification.isRead > 0) {
				showNotification(itemNotification);
			}
			else {
				if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_CLOCK)) {
					itemNotification.isRead = 1;
					IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityNotification.this);
					iotdbHelper.updateNotificationEntry(itemNotification, itemNotification);
				}
				else {
					HttpAPI.readNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
						@Override
						public void onSuccess(String result) {
							itemNotification.isRead = 1;
							Util.updateNotificationEntry(itemNotification, itemNotification);
							showNotification(itemNotification);
						}

						@Override
						public void onError(Object error) {
							Util.ShowDialogError(R.string.str_api_failed);
						}
					}, ActivityNotification.class.getSimpleName());
				}
			}
		}
	}

	public void showNotification(ItemNotification itemNotification) {
		if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_NOTICE)) {
			if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_HEALTH_ABNORMAL)) {
				Intent intent = new Intent(ActivityNotification.this, ActivityMain.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				ActivityMain.IS_ABNORMAL_SET = true;
				ActivityMain.ABNORMAL_TYPE = 0;
				if (itemNotification.alert.contains(getString(R.string.str_blood_pressure))) {
					ActivityMain.ABNORMAL_TYPE = 1;
				}
				else if (itemNotification.alert.contains(getString(R.string.str_body_temperature))) {
					ActivityMain.ABNORMAL_TYPE = 2;
				}
				startActivity(intent);
			}
		}
		else if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_ALARM) &&
				(itemNotification.alarmStatus == 0 || itemNotification.alarmStatus == 2)){
			int alarmId = itemNotification.alarmId;

			m_dlgProgress.show();
			HttpAPI.getAlarmDetail(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), alarmId, new VolleyCallback() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						int iRetCode = jsonObject.getInt("retcode");
						if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
							m_dlgProgress.dismiss();
							return;
						}

						String strData = jsonObject.getString("data");
						Intent intent = new Intent(ActivityNotification.this, ActivitySOSHelp.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						intent.putExtra("notification_data", itemNotification);
						intent.putExtra("alarm_data", strData);
						m_dlgProgress.dismiss();
						startActivity(intent);
					}
					catch (JSONException ignored) {
						m_dlgProgress.dismiss();
					}
				}

				@Override
				public void onError(Object error) {
					m_dlgProgress.dismiss();
				}
			}, getTag());
		}
		else if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_CLOCK)) {
			Intent intent = new Intent(ActivityNotification.this, ActivityAlarmSet.class);
			startActivity(intent);
		}

		loadNotificationList();
	}

	public class ReceiverJPushMessage extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (!intent.getAction().equals(AppConst.ACTION_PUSH_RECEIVED) || !IS_FOREGROUND)
					return;

				ItemNotification itemNotification = (ItemNotification)intent.getSerializableExtra("notification_data");
				if (itemNotification != null) {
					loadNotificationList();
				}
			}
			catch (Exception ignored) {
			}
		}

	}
}
