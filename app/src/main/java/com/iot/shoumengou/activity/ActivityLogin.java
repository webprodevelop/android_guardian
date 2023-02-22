//@formatter:off
package com.iot.shoumengou.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.App;
import com.iot.shoumengou.BuildConfig;
import com.iot.shoumengou.Global;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemPrice;
import com.iot.shoumengou.model.ItemPriceList;
import com.iot.shoumengou.model.ItemType;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

public class ActivityLogin extends ActivityBase {
	private final String TAG = "ActivityLogin";

	private ImageView	m_imgMark;
	private EditText	m_edtPhone;
	private TextView	m_txtRequirePhone;
	private EditText	m_edtPswd;
	private TextView	m_txtRequirePswd;
	private Button		m_btnLogin;
	private Button		m_btnRegister;
	private TextView	m_txtForgot;

	private String		m_sToken;
	private String		m_sUser;
	private String		m_sPhone;
	private String		m_sPswd;

	private CheckBox 	cbName, cbPassword;

	private boolean		m_bIsClickedLogin;
	private boolean		m_bLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//getWindow().setStatusBarColor(Color.WHITE);
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();

		getAppInfo();
	}

	@Override
	protected void initControls() {
		super.initControls();

		m_imgMark		= findViewById(R.id.ID_IMGVIEW_MARK);
		m_edtPhone		= findViewById(R.id.ID_EDTTEXT_PHONE);
		m_edtPswd		= findViewById(R.id.ID_EDTTEXT_PSWD);
		m_txtRequirePhone	= findViewById(R.id.ID_TXTVIEW_REQUIRE_PHONE);
		m_txtRequirePswd	= findViewById(R.id.ID_TXTVIEW_REQUIRE_PSWD);
		m_btnLogin		= findViewById(R.id.ID_BUTTON_LOGIN);
		m_btnRegister	= findViewById(R.id.ID_BUTTON_REGISTER);
		m_txtForgot		= findViewById(R.id.ID_TXTVIEW_FORGOT);

		cbName = findViewById(R.id.ID_CHECKBOX_NAME);
		cbName.setChecked(Prefs.Instance().isSavedPhone());
		cbPassword = findViewById(R.id.ID_CHECKBOX_PASSWORD);
		cbPassword.setChecked(Prefs.Instance().isSavedPWSD());

		// Hide m_txtRequirePhone, m_txtRequirePswd
		m_txtRequirePhone.setVisibility(View.INVISIBLE);
		m_txtRequirePswd.setVisibility(View.INVISIBLE);
	}

	private final TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void afterTextChanged(Editable editable) {
			m_btnLogin.setEnabled(!(m_edtPhone.getText().toString().isEmpty() && m_edtPswd.getText().toString().isEmpty()));
		}
	};

	@Override
	protected void setEventListener() {
		m_edtPhone.addTextChangedListener(textWatcher);
		m_edtPswd.addTextChangedListener(textWatcher);

		// m_btnLogin
		m_btnLogin.setOnClickListener(v -> {
			m_txtRequirePhone.setVisibility(View.INVISIBLE);
			m_txtRequirePswd.setVisibility(View.INVISIBLE);

			// Check Value
			m_sPhone = m_edtPhone.getText().toString();
			if (m_sPhone.isEmpty()) {
				m_txtRequirePhone.setVisibility(View.VISIBLE);
				m_edtPhone.requestFocus();
				return;
			}
			if (m_sPhone.length() != 11) {
				Util.ShowDialogError(R.string.str_phone_number_incorrect, new Util.ResultProcess() {
					@Override
					public void process() {
						m_edtPhone.requestFocus();
					}
				});
				return;
			}

			m_sPswd = m_edtPswd.getText().toString();
			if (m_sPswd.isEmpty()) {
				m_txtRequirePswd.setVisibility(View.VISIBLE);
				m_edtPswd.requestFocus();
				return;
			}
			if (m_sPswd.length() < 6 || m_sPswd.length() > 20) {
				Util.ShowDialogError(R.string.str_password_invalid, new Util.ResultProcess() {
					@Override
					public void process() {
						m_edtPswd.requestFocus();
					}
				});
				return;
			}

			if (m_bIsClickedLogin)
				return;
			m_bIsClickedLogin = true;
			TryLogin();
		});

		// m_txtRegister
		m_btnRegister.setOnClickListener(v -> StartRegister());

		// m_txtForgot
		m_txtForgot.setOnClickListener(v -> StartForgot());

		// Set same Width and Height of the m_imgMark
		m_imgMark.getViewTreeObserver().addOnGlobalLayoutListener(
				() -> {
					ViewGroup.LayoutParams	lytparams = m_imgMark.getLayoutParams();
					lytparams.height = m_imgMark.getWidth();
					m_imgMark.setLayoutParams(lytparams);
				}
		);

		cbName.setOnCheckedChangeListener((buttonView, isChecked) -> {
			Prefs.Instance().setSavedPhone(isChecked);
			Prefs.Instance().commit();
		});
		cbPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
			Prefs.Instance().setSavedPWSD(isChecked);
			Prefs.Instance().commit();
		});

		m_sPhone	= Prefs.Instance().getUserPhone();
		m_sPswd		= Prefs.Instance().getUserPswd();

		m_edtPhone.setText(m_sPhone);
		m_edtPswd.setText(m_sPswd);
	}

	private void CheckLogin() {
		m_sPhone	= m_edtPhone.getText().toString();
		m_sPswd		= m_edtPswd.getText().toString();

		if (!m_sPhone.isEmpty() && !m_sPswd.isEmpty()) {
			TryLogin();
		}
	}

	private void TryLogin() {
		Global.JPUSH_ID = JPushInterface.getRegistrationID(getApplicationContext());
		m_dlgProgress.show();

		HttpAPI.login(m_sPhone, m_sPswd, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_bIsClickedLogin = false;
				m_dlgProgress.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								switch (iRetCode) {
									case HttpAPIConst.RespCode.PHONE_BLANK:
									case HttpAPIConst.RespCode.PHONE_INVAILD:
										m_edtPhone.requestFocus();
										break;
									case HttpAPIConst.RespCode.ACCOUNT_NOT_EXIST:
										m_edtPhone.setText("");
										m_edtPhone.requestFocus();
										break;
									case HttpAPIConst.RespCode.PASSWORD_INVAILD:
									case HttpAPIConst.RespCode.PASSWORD_FAIL:
										m_edtPswd.requestFocus();
										break;
									default:
								}
							}
						});
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					m_sToken = dataObject.optString("token");
					m_sUser = dataObject.optString("name");
					m_sPhone = dataObject.optString("mobile");
					boolean bSex = dataObject.optInt("sex") == 1;
					String strBirthday = dataObject.optString("birthday");
					String strWeixinId = dataObject.optString("weixin_id");
					String strQQId = dataObject.optString("qq_id");
					String strEmail = dataObject.optString("email");
					String strProvince = dataObject.optString("province");
					String strCity = dataObject.optString("city");
					String strDistrict = dataObject.optString("district");
					String strPhoto = dataObject.optString("picture");
					String card = dataObject.optString("id_card_front");
					Util.monitoringWatchId = dataObject.optInt("monitor_id");

					Prefs.Instance().setUserToken(m_sToken);
					Prefs.Instance().setUserName(m_sUser);
					Prefs.Instance().setUserPhone(m_sPhone);
					Prefs.Instance().setUserPswd(m_sPswd);
					Prefs.Instance().setSex(bSex);
					Prefs.Instance().setBirthday(strBirthday);
					Prefs.Instance().setWeixinId(strWeixinId);
					Prefs.Instance().setQQId(strQQId);
					Prefs.Instance().setEmail(strEmail);
					Prefs.Instance().setProvince(strProvince);
					Prefs.Instance().setCity(strCity);
					Prefs.Instance().setDistrict(strDistrict);
					Prefs.Instance().setUserPhoto(strPhoto);
					Prefs.Instance().setUserCard(card);

					Prefs.Instance().commit();

					Util.showToastMessage(ActivityLogin.this, R.string.str_login_success);

					StartMain();
				}
				catch (JSONException e) {
					m_dlgProgress.dismiss();
					Util.ShowDialogError(R.string.str_login_failed);
				}
			}

			@Override
			public void onError(Object error) {
				m_bIsClickedLogin = false;
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_login_failed);
			}
		}, TAG);
	}

	private void StartMain() {
		Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
		// When Intent has JPush Notification Data
		if (getIntent() != null && getIntent().getExtras() != null)
			intent.putExtras(getIntent().getExtras());
		startActivity(intent);

		finish();
	}

	private void StartRegister() {
		Intent intent = new Intent(ActivityLogin.this, ActivityRegister.class);
		startActivity(intent);
	}

	private void StartForgot() {
		Intent intent = new Intent(ActivityLogin.this, ActivityForgot.class);
		startActivity(intent);
	}

	private void checkPolicyAndAgreement() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View confirmView = layoutInflater.inflate(R.layout.alert_delete_alarm, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
		TextView txtAgree	= confirmView.findViewById(R.id.ID_TXT_HEADER);

		String strAgreeText = getResources().getString(R.string.str_agree_policy_text);
		String strAgreeUserAgreement = getResources().getString(R.string.str_agree_user_agreement);
		String strAgreePrivacyPolicy = getResources().getString(R.string.str_agree_privacy_policy);
		int indexAgreeUserAgreement = strAgreeText.indexOf(strAgreeUserAgreement);
		int indexAgreePrivacyPolicy = strAgreeText.indexOf(strAgreePrivacyPolicy);
		SpannableString agreeText = new SpannableString(strAgreeText);
		ClickableSpan clickableUserAgreement = new ClickableSpan() {
			@Override
			public void onClick(View textView) {
				Intent intent = new Intent(ActivityLogin.this, ActivityAgree.class);
				intent.putExtra("agreement_policy", true);
				startActivity(intent);
			}
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
			}
		};
		ClickableSpan clickablePrivacyPolicy = new ClickableSpan() {
			@Override
			public void onClick(View textView) {
				Intent intent = new Intent(ActivityLogin.this, ActivityAgree.class);
				intent.putExtra("agreement_policy", false);
				startActivity(intent);
			}
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
			}
		};
		agreeText.setSpan(clickableUserAgreement, indexAgreeUserAgreement, indexAgreeUserAgreement + strAgreeUserAgreement.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		agreeText.setSpan(clickablePrivacyPolicy, indexAgreePrivacyPolicy, indexAgreePrivacyPolicy + strAgreePrivacyPolicy.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		txtAgree.setText(agreeText);
		txtAgree.setMovementMethod(LinkMovementMethod.getInstance());
		txtAgree.setHighlightColor(Color.TRANSPARENT);

		btnCancel.setOnClickListener(v -> {
			confirmDlg.dismiss();

			finish();
		});

		btnConfirm.setOnClickListener(v -> {
			confirmDlg.dismiss();

			Prefs.Instance().setAgreed();
			Prefs.Instance().commit();
		});

		btnCancel.setText(R.string.str_nonagree);
		btnConfirm.setText(R.string.str_agree);

		confirmDlg.setCancelable(false);
		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
	}

	private void getAppInfo() {
		m_dlgProgress.show();

		HttpAPI.getAppInfo(new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						m_dlgProgress.dismiss();
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					//BuildConfig.VERSION_NAME
					JSONObject dataObject = jsonObject.getJSONObject("data");
					String strAgreement = dataObject.optString("agreement");
					String strPolicy = dataObject.optString("policy");
					JSONArray freePeriodList = dataObject.getJSONArray("freePeriodList");
					Util.freePriceList.clear();
					for (int i = 0; i < freePeriodList.length(); i++) {
						JSONObject freeItem = freePeriodList.getJSONObject(i);
						ItemPrice itemPrice = new ItemPrice();
						itemPrice.label = freeItem.optString("label");
						itemPrice.value = freeItem.optString("value");
						itemPrice.key = freeItem.optString("key");
						Util.freePriceList.add(itemPrice);
					}
					JSONArray allPriceList = dataObject.getJSONArray("allPriceList");
					Util.allPriceList.clear();
					for (int i = 0; i < allPriceList.length(); i++) {
						JSONObject freeItem = allPriceList.getJSONObject(i);
						ItemPriceList itemPriceList = new ItemPriceList();
						JSONArray priceList = freeItem.getJSONArray("pricelist");
						ArrayList<ItemPrice> itemPriceArrayList = new ArrayList<>();
						for (int j = 0; j < priceList.length(); j++) {
							JSONObject priceItem = priceList.getJSONObject(j);
							ItemPrice itemPrice = new ItemPrice();
							itemPrice.label = priceItem.optString("label");
							itemPrice.value = priceItem.optString("value");
							itemPrice.key = priceItem.optString("key");
							itemPriceArrayList.add(itemPrice);
						}
						itemPriceList.priceList = itemPriceArrayList;
						itemPriceList.label = freeItem.optString("label");
						itemPriceList.type = freeItem.optString("type");
						Util.addPriceList(itemPriceList);
					}
					JSONArray sensorTypeList = dataObject.getJSONArray("sensorTypeList");
					Util.sensorTypeList.clear();
					Util.sensorTypeMap.clear();
					Util.sensorMap.clear();
					for (int i = 0; i < sensorTypeList.length(); i++) {
						JSONObject sensorType = sensorTypeList.getJSONObject(i);
						ItemType itemType = new ItemType();
						itemType.createTimeStr = sensorType.optString("createTimeStr");
						itemType.pictureUrl = sensorType.optString("pictureUrl");
						itemType.createTime = sensorType.optString("createTime");
						itemType.shortName = sensorType.optString("shortName");
						itemType.createDateStr = sensorType.optString("createDateStr");
						itemType.updatedTimeStr = sensorType.optString("updatedTimeStr");
						itemType.updatedTime = sensorType.optString("updatedTime");
						itemType.id = sensorType.optInt("id");
						itemType.name = sensorType.optString("name");
						itemType.status = sensorType.optBoolean("status");
						itemType.type = sensorType.optString("type");
						itemType.updateDateStr = sensorType.optString("updateDateStr");
						Util.addSensorType(itemType);
					}
//					JSONArray alarmTypeList = dataObject.getJSONArray("alarmTypeList");
//					Util.alarmTypeList.clear();
//					for (int i = 0; i < alarmTypeList.length(); i++) {
//						JSONObject sensorType = sensorTypeList.getJSONObject(i);
//						ItemType itemType = new ItemType();
//						itemType.createTimeStr = sensorType.optString("createTimeStr");
//						itemType.pictureUrl = sensorType.optString("pictureUrl");
//						itemType.createTime = sensorType.optString("createTime");
//						itemType.shortName = sensorType.optString("shortName");
//						itemType.createDateStr = sensorType.optString("createDateStr");
//						itemType.updatedTimeStr = sensorType.optString("updatedTimeStr");
//						itemType.updatedTime = sensorType.optString("updatedTime");
//						itemType.id = sensorType.optInt("id");
//						itemType.name = sensorType.optString("name");
//						itemType.status = sensorType.optBoolean("status");
//						itemType.type = sensorType.optString("type");
//						itemType.updateDateStr = sensorType.optString("updateDateStr");
//						Util.alarmTypeList.add(itemType);
//					}
					String strUserBirthdayDesc = dataObject.optString("user_birth_desc");

					Prefs.Instance().setAgreement(strAgreement);
					Prefs.Instance().setPolicy(strPolicy);
					Prefs.Instance().setUserBirthDesc(strUserBirthdayDesc);
					Prefs.Instance().commit();

					JSONObject verObject = dataObject.getJSONObject("appVersion");
					Util.storeAppVersion = verObject.optString("app_ver_android");
					Util.storeAppURL = verObject.optString("store_url_android");

					m_dlgProgress.dismiss();
					if (BuildConfig.VERSION_NAME.compareTo(Util.storeAppVersion) < 0) {
						LayoutInflater layoutInflater = LayoutInflater.from(ActivityLogin.this);
						View confirmView = layoutInflater.inflate(R.layout.alert_new_version, null);

						final AlertDialog confirmDlg = new AlertDialog.Builder(ActivityLogin.this).create();

						TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
						TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

						btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

						btnConfirm.setOnClickListener(v -> {
							confirmDlg.dismiss();

							try {
								Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Util.storeAppURL));
								startActivity(i);
							}
							catch (Exception ignore) {

							}
						});

						confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						confirmDlg.setView(confirmView);
						confirmDlg.show();
					} else {
						if (Prefs.Instance().getAgreed()) {
							// Init Value
							m_bIsClickedLogin = false;
							m_bLogout = getIntent().getBooleanExtra("log_out", false);
							if (!m_bLogout) {
								CheckLogin();
							}
						} else {
							checkPolicyAndAgreement();
						}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}
}
