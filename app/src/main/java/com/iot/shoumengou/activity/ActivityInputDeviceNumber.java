//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemDeviceInfo;
import com.iot.shoumengou.model.ItemSensorInfo;
import com.iot.shoumengou.model.ItemType;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityInputDeviceNumber extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityInputDeviceNumber";

	public final static int				REQUEST_DEVICE_INFO		= 0;
	public final static int				REQUEST_BIND_COMPLETE	= 1;

	private ImageView	ivBack;
	private TextView	tvTitle;
	private TextView	tvDeviceType;
	private EditText	edtNumber;
	private TextView	tvInputDesc;
	private TextView	tvContinue;

	private String		deviceType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_device);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		deviceType = getIntent().getStringExtra("device_type");

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		tvTitle = findViewById(R.id.ID_TXTVIEW_TITLE);
		tvDeviceType = findViewById(R.id.ID_TXTVIEW_DEVICE_TYPE);
		edtNumber = findViewById(R.id.ID_EDTTEXT_NUMBER);
		tvInputDesc = findViewById(R.id.ID_TXTVIEW_INPUT_DESC);
		tvContinue = findViewById(R.id.ID_BTN_CONTINUE);

		tvContinue.setEnabled(false);

		if (deviceType.isEmpty()) {
			tvDeviceType.setVisibility(View.GONE);
			tvTitle.setText(R.string.str_bind_watch);
		} else {
			ItemType itemType = Util.sensorTypeMap.get(deviceType);
			if (itemType != null){
				String title = getString(R.string.str_bind) + itemType.name;
				tvTitle.setText(title);
			}else{
				tvTitle.setText(R.string.str_bind_device);
			}
			tvDeviceType.setText(deviceType);
		}
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		tvContinue.setOnClickListener(this);

		edtNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (edtNumber.getText().length() > 0) {
					tvContinue.setEnabled(true);
					tvInputDesc.setVisibility(View.INVISIBLE);
				} else {
					tvContinue.setEnabled(false);
					tvInputDesc.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_BTN_CONTINUE:
				onContinue();
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setResult(resultCode);
		finish();
	}

	private void onContinue() {
		String strDeviceSerial = edtNumber.getText().toString();
		if (!deviceType.isEmpty()) {
			if (!strDeviceSerial.startsWith(deviceType)) {
				strDeviceSerial = deviceType + strDeviceSerial;
			}
		}

		if (deviceType.isEmpty()) {
			registerWatch(strDeviceSerial);
		} else {
			registerSensor(strDeviceSerial, deviceType);
		}
	}

	private void registerWatch(final String serial) {
		m_dlgProgress.show();

		HttpAPI.registerWatch(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), serial, new VolleyCallback() {
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

					JSONObject dataObject = jsonObject.getJSONObject("data");
					ItemWatchInfo itemWatchInfo = new ItemWatchInfo(dataObject);
					itemWatchInfo.type = "";

					ItemWatchInfo itemWatch = Util.findWatchEntry(itemWatchInfo.serial);
					if (itemWatch == null) {
						Util.addWatchEntry(itemWatchInfo);
					} else {
						Util.updateWatchEntry(itemWatch, itemWatchInfo);
					}

					Util.setMoniteringWatchInfo(itemWatchInfo);
					Prefs.Instance().setMoniteringWatchSerial(itemWatchInfo.serial);
					Prefs.Instance().commit();

					if (itemWatchInfo.isManager && itemWatchInfo.phone.isEmpty()) {
						startDeviceInfoActivity(itemWatchInfo);
					} else {
						startBindCompleteActivity(itemWatchInfo);
					}
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

	private void registerSensor(final String serial, String type) {
		m_dlgProgress.show();

		HttpAPI.registerSensor(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), serial, type, new VolleyCallback() {
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

					JSONObject dataObject = jsonObject.getJSONObject("data");
					ItemSensorInfo itemSensorInfo = new ItemSensorInfo(dataObject);
					ItemSensorInfo itemSensor = Util.findSensorEntry(itemSensorInfo.type, itemSensorInfo.serial);
					if (itemSensor == null) {
						Util.addSensorEntry(itemSensorInfo);
					} else {
						Util.updateSensorEntry(itemSensor, itemSensorInfo);
					}

					if (itemSensorInfo.isManager && itemSensorInfo.contactPhone.isEmpty()) {
						startDeviceInfoActivity(itemSensorInfo);
					} else {
						startBindCompleteActivity(itemSensorInfo);
					}
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

	private void startDeviceInfoActivity(ItemDeviceInfo itemDeviceInfo) {
		Intent intent;
		if (deviceType.isEmpty()) {
			intent = new Intent(this, ActivityWatchInfo.class);
			intent.putExtra("device_data", (ItemWatchInfo)itemDeviceInfo);
		} else {
			intent = new Intent(this, ActivitySensorInfo.class);
			intent.putExtra("device_data", (ItemSensorInfo)itemDeviceInfo);
		}

		startActivityForResult(intent, REQUEST_DEVICE_INFO);
	}

	private void startBindCompleteActivity(ItemDeviceInfo itemDeviceInfo) {
		Intent intent = new Intent(this, ActivityBindComplete.class);
		if (deviceType.isEmpty()) {
			intent.putExtra("device_data", (ItemWatchInfo)itemDeviceInfo);
		} else {
			intent.putExtra("device_data", (ItemSensorInfo)itemDeviceInfo);
		}
		startActivityForResult(intent, REQUEST_BIND_COMPLETE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}
}
