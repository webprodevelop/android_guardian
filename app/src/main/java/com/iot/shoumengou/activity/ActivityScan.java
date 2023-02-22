//@formatter:off
package com.iot.shoumengou.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
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
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ActivityScan extends ActivityBase implements OnClickListener, ZXingScannerView.ResultHandler {
	private final static String	TAG = "ActivityScan";

	public final static int				REQUEST_DEVICE_INFO		= 0;
	public final static int				REQUEST_BIND_COMPLETE	= 1;
	public final static int				REQUEST_INPUT_NUMBER	= 2;

	private TextView			tvTitle;
	private ZXingScannerView	scannerView;
	private TextView			tvManualInput;
	private ImageView			ivBack;
	private ImageView			ivGoDeviceInfo;

	private String				deviceType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();

		// Set Scanner;
		ArrayList<BarcodeFormat> arrFormat = new ArrayList<>();
		arrFormat.add(BarcodeFormat.QR_CODE);
		scannerView.setFormats(arrFormat);
		scannerView.setAutoFocus(true);
		scannerView.setLaserColor(R.color.colorAccent);
		scannerView.setMaskColor(R.color.colorAccent);
		if (Build.MANUFACTURER.toUpperCase().equals("HUAWEI"))
			scannerView.setAspectTolerance(0.5f);
	}

	@Override
	protected void initControls() {
		super.initControls();

		tvTitle = findViewById(R.id.ID_TXTVIEW_TITLE);
		scannerView = findViewById(R.id.ID_SCANNER);
		tvManualInput = findViewById(R.id.ID_TXTVIEW_MANUAL_INPUT);
		TextView tvScanDesc = findViewById(R.id.ID_TXTVIEW_SCAN_DESC);
		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		ivGoDeviceInfo = findViewById(R.id.ID_IMGVIEW_GO_DEVICE_INFO);

		deviceType = getIntent().getStringExtra("device_type");
		if (deviceType.isEmpty()) {
			tvTitle.setText(R.string.str_bind_watch);
			tvScanDesc.setText(R.string.str_smartwatch_qr_desc);
		} else {
			ItemType itemType = Util.sensorTypeMap.get(deviceType);
			if (itemType != null){
				String title = getString(R.string.str_bind) + itemType.name;
				tvTitle.setText(title);
			}else{
				tvTitle.setText(R.string.str_bind_device);
			}

			tvScanDesc.setText(R.string.str_sensor_qr_desc);
		}
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		ivGoDeviceInfo.setOnClickListener(this);
		tvManualInput.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		resetCamera();
	}

	public void resetCamera(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.CAMERA },
						AppConst.REQUEST_PERMISSION_CAMERA
				);
				return;
			}
		}
		scannerView.setResultHandler(this);
		scannerView.startCamera();
	}

	@Override
	public void onPause() {
		super.onPause();
		scannerView.stopCamera();
	}

	@Override
	public void handleResult(Result result) {
		if (deviceType.isEmpty()) {
			registerWatch(result.getText());
		} else {
			registerSensor(result.getText(), deviceType);
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
					int iRetCode = jsonObject.optInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.optString("msg");
						Util.ShowDialogError(sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								resetCamera();
							}
						});
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
					finish();
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
				finish();
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
						Util.ShowDialogError(sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								resetCamera();
							}
						});
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

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_IMGVIEW_GO_DEVICE_INFO:
				startDeviceInfoActivity(null);
				break;
			case R.id.ID_TXTVIEW_MANUAL_INPUT:
				onManualInput();
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setResult(resultCode);
		finish();
	}

	private void startDeviceInfoActivity(ItemDeviceInfo itemDeviceInfo) {
		Intent intent;
		if (deviceType.isEmpty()) {
			intent = new Intent(this, ActivityWatchInfo.class);
			intent.putExtra("device_data", itemDeviceInfo);
		} else {
			intent = new Intent(this, ActivitySensorInfo.class);
			intent.putExtra("device_data", itemDeviceInfo);
		}

        startActivityForResult(intent, REQUEST_DEVICE_INFO);
	}

	private void startBindCompleteActivity(ItemDeviceInfo itemDeviceInfo) {
		Intent intent = new Intent(this, ActivityBindComplete.class);
		if (deviceType.isEmpty()) {
			intent.putExtra("device_data", itemDeviceInfo);
		} else {
			intent.putExtra("device_data", itemDeviceInfo);
		}
		startActivityForResult(intent, REQUEST_BIND_COMPLETE);
	}

	private void onManualInput() {
		Intent intent = new Intent(this, ActivityInputDeviceNumber.class);
		intent.putExtra("device_type", deviceType);
		startActivityForResult(intent, REQUEST_INPUT_NUMBER);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}
}
