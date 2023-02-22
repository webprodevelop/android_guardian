//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.helper.AliPayResult;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemDeviceInfo;
import com.iot.shoumengou.model.ItemPaidService;
import com.iot.shoumengou.model.ItemPrice;
import com.iot.shoumengou.model.ItemSensorInfo;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class ActivityServiceTerm extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityServiceTerm";

	private ImageView		ivBack;
	private TextView		tvYear1;
	private TextView		tvYear2;
	private TextView		tvYear3;
	private TextView		tvYear4;
	private TextView		tvYear5;
	private TextView		tvServiceTerm;
	private TextView		tvAmount;
	private TextView		tvPay;
	private LinearLayout	llWeixin;
	private LinearLayout	llZhifubao;
	private LinearLayout	llSocial;
	private LinearLayout	llOthers;

	private ItemDeviceInfo	itemDeviceInfo;
	int serviceYears = 0;
	int payType =  -1;
	ArrayList<ItemPrice> payAmounts;
	String payAmount;
	String serviceRealStart = "";
	String serviceStart = "";
	String serviceEnd = "";

	public static final String WX_APP_ID = "wx7a58ef5cb07ff9f8";
	private IWXAPI wxapi;
	public static boolean finishedWXPay;
	public static int WXPayResult;
	public static String WXPayResultStr;

	private void regToWx() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		wxapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);

		// 将应用的appId注册到微信
		wxapi.registerApp(WX_APP_ID);

		//建议动态监听微信启动广播进行注册到微信
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// 将该app注册到微信
				wxapi.registerApp(WX_APP_ID);
			}
		}, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_term);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		itemDeviceInfo = (ItemDeviceInfo)getIntent().getSerializableExtra("device_data");
		if (itemDeviceInfo == null) {
			finish();
		}

		finishedWXPay = false;
		regToWx();
		initControls();
		setEventListener();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (finishedWXPay) {
			finishedWXPay = false;
			if (WXPayResult == 0) {
				startPayComplete();
			} else {
				m_dlgProgress.dismiss();
				if (WXPayResultStr != null && !WXPayResultStr.isEmpty())
					Util.ShowDialogError(WXPayResultStr);
				else {
//					String error = getString(R.string.pay_result_callback_msg, String.valueOf(WXPayResult));
					String error = getString(R.string.str_payment_cancel);
					Util.ShowDialogError(error);
				}
			}
		}
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		tvYear1 = findViewById(R.id.ID_TXTVIEW_YEAR1);
		tvYear2 = findViewById(R.id.ID_TXTVIEW_YEAR2);
		tvYear3 = findViewById(R.id.ID_TXTVIEW_YEAR3);
		tvYear4 = findViewById(R.id.ID_TXTVIEW_YEAR4);
		tvYear5 = findViewById(R.id.ID_TXTVIEW_YEAR5);
		tvServiceTerm = findViewById(R.id.ID_TXTVIEW_SERVICE_TERM);
		tvAmount = findViewById(R.id.ID_TXTVIEW_AMOUNT);
		llWeixin = findViewById(R.id.ID_LYT_WEIXIN);
		llZhifubao = findViewById(R.id.ID_LYT_ZHIFUBAO);
		llSocial = findViewById(R.id.ID_LYT_SOCIAL);
		llOthers = findViewById(R.id.ID_LYT_OTHERS);
		tvPay = findViewById(R.id.ID_BTN_PAY);

		if (!itemDeviceInfo.isExpiredService()) {
			serviceStart = itemDeviceInfo.serviceStartDate;
			serviceEnd = itemDeviceInfo.serviceEndDate;
			serviceRealStart = itemDeviceInfo.serviceEndDate;
		} else {
			Calendar calendar = Calendar.getInstance();
			serviceStart = Util.getDateFormatStringIgnoreLocale(calendar.getTime());
			serviceEnd = Util.getDateFormatStringIgnoreLocale(calendar.getTime());
			serviceRealStart = Util.getDateFormatStringIgnoreLocale(calendar.getTime());
		}
		tvServiceTerm.setText(String.format(getString(R.string.str_term), serviceStart, serviceEnd));
		tvAmount.setText("");

		getPayAmounts();
	}

	private void getPayAmounts() {
		if (itemDeviceInfo.type.isEmpty()) {
				payAmounts = Util.allPriceList.get(0).priceList;
		} else {
			for (int i=1; i<Util.allPriceList.size(); i++) {
				if (Util.allPriceList.get(i).type.equals(itemDeviceInfo.type)) {
					payAmounts = Util.allPriceList.get(i).priceList;
					break;
				}
			}
		}
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		tvYear1.setOnClickListener(this);
		tvYear2.setOnClickListener(this);
		tvYear3.setOnClickListener(this);
		tvYear4.setOnClickListener(this);
		tvYear5.setOnClickListener(this);
		llWeixin.setOnClickListener(this);
		llZhifubao.setOnClickListener(this);
		llSocial.setOnClickListener(this);
		llOthers.setOnClickListener(this);
		tvPay.setOnClickListener(this);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_TXTVIEW_YEAR1:
			case R.id.ID_TXTVIEW_YEAR2:
			case R.id.ID_TXTVIEW_YEAR3:
			case R.id.ID_TXTVIEW_YEAR4:
			case R.id.ID_TXTVIEW_YEAR5:
				onSelectYear(view);
				break;
			case R.id.ID_LYT_WEIXIN:
			case R.id.ID_LYT_ZHIFUBAO:
			case R.id.ID_LYT_SOCIAL:
			case R.id.ID_LYT_OTHERS:
				onSelectPaymentMode(view);
				break;
			case R.id.ID_BTN_PAY:
				onPay();
				break;
		}
	}

	@SuppressLint({"NonConstantResourceId", "SetTextI18n"})
	private void onSelectYear(View selectView) {
		tvYear1.setSelected(false);
		tvYear2.setSelected(false);
		tvYear3.setSelected(false);
		tvYear4.setSelected(false);
		tvYear5.setSelected(false);

		selectView.setSelected(true);

		int preServiceYears = serviceYears;
		switch (selectView.getId()) {
			case R.id.ID_TXTVIEW_YEAR1:
				serviceYears = 1;
				payAmount = payAmounts.get(0).value;
				break;
			case R.id.ID_TXTVIEW_YEAR2:
				serviceYears = 2;
				payAmount = payAmounts.get(1).value;
				break;
			case R.id.ID_TXTVIEW_YEAR3:
				serviceYears = 3;
				payAmount = payAmounts.get(2).value;
				break;
			case R.id.ID_TXTVIEW_YEAR4:
				serviceYears = 4;
				payAmount = payAmounts.get(3).value;
				break;
			case R.id.ID_TXTVIEW_YEAR5:
				serviceYears = 5;
				payAmount = payAmounts.get(4).value;
				break;
		}
		tvAmount.setText(payAmount + getString(R.string.str_rmb));
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Util.parseDateFormatString(serviceEnd));
		calendar.add(Calendar.YEAR, -preServiceYears);
		calendar.add(Calendar.YEAR, serviceYears);
		serviceEnd = Util.getDateFormatStringIgnoreLocale(calendar.getTime());
		tvServiceTerm.setText(String.format(getString(R.string.str_term), serviceStart, serviceEnd));
	}

	@SuppressLint("NonConstantResourceId")
	private void onSelectPaymentMode(View selectView) {
		llWeixin.setSelected(false);
		llZhifubao.setSelected(false);
		llSocial.setSelected(false);
		llOthers.setSelected(false);

		selectView.setSelected(true);

		switch (selectView.getId()) {
			case R.id.ID_LYT_WEIXIN:
				payType = 2;
				break;
			case R.id.ID_LYT_ZHIFUBAO:
				payType = 1;
				break;
			case R.id.ID_LYT_SOCIAL:
				payType = 0;
				break;
			case R.id.ID_LYT_OTHERS:
				payType = 3;
				break;
		}
	}

	private void onPay() {
		if (serviceYears == 0) {
			return;
		}

		if (payType == -1) {
			return;
		}

		String deviceType;
		if (!itemDeviceInfo.type.isEmpty()) {
			deviceType = itemDeviceInfo.type;
		} else {
			deviceType = "5G";
		}

		m_dlgProgress.show();
		HttpAPI.requestPay(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemDeviceInfo.serial, deviceType, serviceYears, serviceRealStart, serviceEnd, payAmount, payType, new VolleyCallback() {
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
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					orderInfo = dataObject.getString("orderInfo");
					orderId = dataObject.getInt("order_id");
					if (payType == 1)
						startZhifubaoPay ();
					else
						startWXPay(new JSONObject(orderInfo));
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

	private static final int SDK_PAY_FLAG = 1;
	private int orderId;
	private String orderInfo;

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == SDK_PAY_FLAG) {
				m_dlgProgress.dismiss();
				AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
				/*
				 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					startPayComplete();
				} else if (TextUtils.equals(resultStatus, "6001")) {
					// cancel payment
					Util.ShowDialogError(getString(R.string.str_payment_cancel));
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					Util.ShowDialogError(getString(R.string.pay_failed) + resultInfo);
				}
			}
		};
	};

	private void startZhifubaoPay () {
		m_dlgProgress.show();
		final Runnable payRunnable = () -> {
			PayTask alipay = new PayTask(ActivityServiceTerm.this);
			Map<String, String> result = alipay.payV2(orderInfo, true);
			Log.i("msp", result.toString());

			Message msg = new Message();
			msg.what = SDK_PAY_FLAG;
			msg.obj = result;
			mHandler.sendMessage(msg);
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	private void startWXPay(JSONObject object) {
		m_dlgProgress.show();
		PayReq req = new PayReq();
		req.appId			= object.optString("appid");
		req.partnerId		= object.optString("partnerid");
		req.prepayId		= object.optString("prepayid");
		req.nonceStr		= object.optString("noncestr");
		req.timeStamp		= object.optString("timestamp");
		req.packageValue	= object.optString("package");
		req.sign			= object.optString("sign");
		req.extData			= "app data"; // optional
		if (!wxapi.sendReq(req)) {
			m_dlgProgress.dismiss();
			Util.ShowDialogError(R.string.str_page_loading_failed);
		}
		else {
			m_dlgProgress.dismiss();
		}
	}

	private void startPayComplete() {
		HttpAPI.inquirePay(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemDeviceInfo.serial, orderId, new VolleyCallback() {
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
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					int orderId = dataObject.getInt("order_id");
					int amount = dataObject.getInt("amount");
					int payType = dataObject.getInt("pay_type");
					int serviceYears = dataObject.getInt("service_years");
					String serviceStart = dataObject.getString("service_start");
					String serviceEnd = dataObject.getString("service_end");
					String payTime = dataObject.getString("pay_time");

					ItemPaidService itemPaidService = new ItemPaidService();
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

					ItemDeviceInfo deviceInfo = Util.findWatchEntry(itemDeviceInfo.serial);
					if (deviceInfo == null) {
						deviceInfo = Util.findSensorEntry(itemDeviceInfo.type, itemDeviceInfo.serial);
					}
					if (deviceInfo != null) {
						deviceInfo.serviceStartDate = serviceStart;
						deviceInfo.serviceEndDate = serviceEnd;

						if (deviceInfo.type.isEmpty()) {
							Util.updateWatchEntry((ItemWatchInfo)deviceInfo, (ItemWatchInfo)deviceInfo);
						} else {
							Util.updateSensorEntry((ItemSensorInfo)deviceInfo, (ItemSensorInfo)deviceInfo);
						}
					}
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_page_loading_failed);
					return;
				}

				Intent intent = new Intent(ActivityServiceTerm.this, ActivityPayComplete.class);
				startActivityForResult(intent, 1);
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}
}
