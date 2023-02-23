//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemDeviceInfo;
import com.iot.shoumengou.model.ItemPaidService;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityOrderDetail extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityOrderDetail";

	private ImageView		ivBack;
	private TextView		tvApplyRefund;
	private TextView		tvTermLength;
	private TextView		tvServiceTerm;
	private TextView		tvOrderNumber;
	private TextView		tvAmount;
	private TextView		tvConfirm;
	private TextView		tvPaymentMode;
	private TextView		tvPaymentTime;

	private ItemDeviceInfo	itemDeviceInfo;
	private ItemPaidService itemPaidService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		itemDeviceInfo = (ItemDeviceInfo)getIntent().getSerializableExtra("device_data");
		if (itemDeviceInfo == null) {
			finish();
		}

		initControls();
		setEventListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		inquirePaidService();
	}

	@Override
	protected void onStop() {
		super.onStop();

		App.Instance().cancelPendingRequests(TAG);
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		tvApplyRefund = findViewById(R.id.ID_TXTVIEW_APPLY_REFUND);
		tvTermLength = findViewById(R.id.ID_TXTVIEW_TERM_LENGTH);
		tvServiceTerm = findViewById(R.id.ID_TXTVIEW_SERVICE_TERM);
		tvOrderNumber = findViewById(R.id.ID_TXTVIEW_ORDER_NUMBER);
		tvAmount = findViewById(R.id.ID_TXTVIEW_AMOUNT);
		tvPaymentMode = findViewById(R.id.ID_TXTVIEW_PAYMENT_MODE);
		tvPaymentTime = findViewById(R.id.ID_TXTVIEW_DURATION);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);

		itemPaidService = null;
	}

	@SuppressLint("SetTextI18n")
	private void setPaidServiceInfo() {
		if (itemPaidService == null) {
			findViewById(R.id.ID_VIEW_TERM_LENGTH).setVisibility(View.GONE);
			findViewById(R.id.ID_VIEW_ORDER_NUMBER).setVisibility(View.GONE);
			findViewById(R.id.ID_VIEW_AMOUNT).setVisibility(View.GONE);
			findViewById(R.id.ID_VIEW_PAYMENT_MODE).setVisibility(View.GONE);
			findViewById(R.id.ID_VIEW_DURATION).setVisibility(View.GONE);
			tvTermLength.setText("");
			tvServiceTerm.setText("");
			tvAmount.setText("");
			tvPaymentMode.setText("");
			tvApplyRefund.setVisibility(View.GONE);
			tvServiceTerm.setText(String.format(getString(R.string.str_term), itemDeviceInfo.serviceStartDate, itemDeviceInfo.serviceEndDate));
		} else {
			findViewById(R.id.ID_VIEW_TERM_LENGTH).setVisibility(View.VISIBLE);
			findViewById(R.id.ID_VIEW_ORDER_NUMBER).setVisibility(View.VISIBLE);
			findViewById(R.id.ID_VIEW_AMOUNT).setVisibility(View.VISIBLE);
			findViewById(R.id.ID_VIEW_PAYMENT_MODE).setVisibility(View.VISIBLE);
			findViewById(R.id.ID_VIEW_DURATION).setVisibility(View.VISIBLE);

			if (Util.isDateWeekBefore(itemPaidService.serviceStartDate)) {
				//tvApplyRefund.setVisibility(View.VISIBLE);
				tvApplyRefund.setVisibility(View.GONE);
			} else {
				tvApplyRefund.setVisibility(View.GONE);
			}
			tvTermLength.setText(itemPaidService.serviceYears + getString(R.string.str_year));
			tvServiceTerm.setText(String.format(getString(R.string.str_term), itemPaidService.serviceStartDate, itemPaidService.serviceEndDate));
			tvAmount.setText(itemPaidService.amount + getString(R.string.str_rmb));
			switch (itemPaidService.payType) {
				case 1:
					tvPaymentMode.setText(R.string.str_zhifubao_payment);
					break;
				case 2:
					tvPaymentMode.setText(R.string.str_weixin_payment);
					break;
				case 3:
					tvPaymentMode.setText(R.string.str_social_payment);
					break;
				default:
					tvPaymentMode.setText(R.string.str_other);
			}
			tvOrderNumber.setText(String.valueOf(itemPaidService.orderId));
			tvPaymentTime.setText(itemPaidService.payTime);
		}
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		tvApplyRefund.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_TXTVIEW_APPLY_REFUND:
				onApplyRefund();
				break;
			case R.id.ID_BTN_CONFIRM:
				onConfirm();
				break;
		}
	}

	private void onApplyRefund() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View confirmView = layoutInflater.inflate(R.layout.alert_apply_refund, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

		btnConfirm.setOnClickListener(v -> {
			confirmDlg.dismiss();

			cancelPaidService();
		});

		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
	}

	private void onConfirm() {
         Intent intent = new Intent(this, ActivityServiceTerm.class);
         intent.putExtra("device_data", itemDeviceInfo);
         startActivity(intent);
	}

	private void inquirePaidService() {
		m_dlgProgress.show();

		String deviceType;
		if (!itemDeviceInfo.type.isEmpty()) {
			deviceType = itemDeviceInfo.type;
		} else {
			deviceType = "5G";
		}

		HttpAPI.inquirePaidService(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemDeviceInfo.serial, deviceType, new VolleyCallback() {
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

					if (!jsonObject.has("data")) {
						setPaidServiceInfo();
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					int orderId = dataObject.getInt("order_id");
					double amount = dataObject.getDouble("amount");
					int payType = dataObject.getInt("pay_type");
					int serviceYears = dataObject.getInt("service_years");
					String serviceStart = dataObject.getString("service_start");
					String serviceEnd = dataObject.getString("service_end");
					String payTime = dataObject.getString("pay_time");

					itemPaidService = new ItemPaidService();
					itemPaidService.orderId = orderId;
					itemPaidService.userPhone = Prefs.Instance().getUserPhone();
					itemPaidService.type = itemDeviceInfo.type;
					itemPaidService.deviceId = itemDeviceInfo.id;
					itemPaidService.amount = amount;
					itemPaidService.payType = payType;
					itemPaidService.serviceYears = serviceYears;
					itemPaidService.serviceStartDate = serviceStart;
					itemPaidService.serviceEndDate = serviceEnd;
					itemPaidService.payTime = payTime;

					setPaidServiceInfo();
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

	private void cancelPaidService() {
		m_dlgProgress.show();

		String deviceType;
		if (!itemDeviceInfo.type.isEmpty()) {
			deviceType = itemDeviceInfo.type;
		} else {
			deviceType = "5G";
		}

		HttpAPI.cancelPaidService(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemDeviceInfo.serial, deviceType, itemPaidService.orderId, new VolleyCallback() {
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

					startRefundActivity();
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

	private void startRefundActivity() {
		Intent intent = new Intent(ActivityOrderDetail.this, ActivityRefundComplete.class);
		startActivity(intent);
		finish();
	}
}
