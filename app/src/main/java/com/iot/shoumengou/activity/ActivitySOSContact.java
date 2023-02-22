//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivitySOSContact extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivitySOSContact";

	private ImageView				ivBack;
	private TextView				tvInputDesc;
	private EditText				edtSecondContact;
	private EditText				edtThirdContact;
	private EditText				edtSecondPhoneNumber;
	private EditText				edtThirdPhoneNumber;
	private TextView				tvConfirm2;
	private TextView				tvConfirm3;

	private String					contactName1 = "";
	private String					contactPhone1 = "";
	private String					contactName2 = "";
	private String					contactPhone2 = "";
	private String					contactName3 = "";
	private String					contactPhone3 = "";

	private ItemWatchInfo			monitoringWatchInfo = null;

	private Button 					btnAcquire1, btnAcquire2;
	private EditText				edtCode1, edtCode2;

	private CountDownTimer		m_timer1, m_timer2;
	private int					m_iCount1, m_iCount2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sos_contact);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();
//		setEnableControls2(false);
//		setEnableControls3(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (m_timer1 != null) {
			m_timer1.cancel();
		}
		if (m_timer2 != null) {
			m_timer2.cancel();
		}
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		tvInputDesc = findViewById(R.id.ID_TXTVIEW_INPUT_DESC);
		TextView tvFirstContact = findViewById(R.id.ID_TEXT_FIRST_CONTACT);
		TextView tvFirstPhoneNumber = findViewById(R.id.ID_TEXT_FIRST_PHONENUMBER);
		edtSecondContact = findViewById(R.id.ID_EDTTEXT_SECOND_CONTACT);
		edtThirdContact = findViewById(R.id.ID_EDTTEXT_THIRD_CONTACT);
		edtSecondPhoneNumber = findViewById(R.id.ID_EDTTEXT_SECOND_PHONENUMBER);
		edtThirdPhoneNumber = findViewById(R.id.ID_EDTTEXT_THIRD_PHONENUMBER);

		btnAcquire1 = findViewById(R.id.ID_BUTTON_ACQUIRE_1);
		edtCode1 = findViewById(R.id.ID_EDTTEXT_ACQUIRE1);
		btnAcquire2 = findViewById(R.id.ID_BUTTON_ACQUIRE_2);
		edtCode2 = findViewById(R.id.ID_EDTTEXT_ACQUIRE2);
		tvConfirm2 = findViewById(R.id.ID_BTN_CONFIRM2);
		tvConfirm3 = findViewById(R.id.ID_BTN_CONFIRM3);

		contactName1 = Prefs.Instance().getUserName();
		contactPhone1 = Prefs.Instance().getUserPhone();

		monitoringWatchInfo = Util.monitoringWatch;
		if (monitoringWatchInfo != null) {
			contactName2 = monitoringWatchInfo.sosContactName2;
			contactPhone2 = monitoringWatchInfo.sosContactPhone2;
			contactName3 = monitoringWatchInfo.sosContactName3;
			contactPhone3 = monitoringWatchInfo.sosContactPhone3;
		}

		if (contactName1 != null && !contactName1.isEmpty()) {
			tvFirstContact.setText(contactName1);
		}
		if (contactName2 != null && !contactName2.isEmpty()) {
			edtSecondContact.setText(contactName2);
		}
		if (contactName3 != null && !contactName3.isEmpty()) {
			edtThirdContact.setText(contactName3);
		}
		if (contactPhone1 != null && !contactPhone1.isEmpty()) {
			tvFirstPhoneNumber.setText(contactPhone1);
		}
		if (contactPhone2 != null && !contactPhone2.isEmpty()) {
			edtSecondPhoneNumber.setText(contactPhone2);
		}
		if (contactPhone3 != null && !contactPhone3.isEmpty()) {
			edtThirdPhoneNumber.setText(contactPhone3);
		}
	}

//	public void setEnableControls2(boolean flag) {
//		edtSecondContact.setEnabled(flag);
//		edtSecondPhoneNumber.setEnabled(flag);
//
//		btnAcquire1.setEnabled(flag);
//		edtCode1.setEnabled(flag);
//
//		if(flag) {
//			tvConfirm2.setText(R.string.str_confirm);
//			tvConfirm2.setBackgroundResource(R.drawable.selector_small_button_fill);
//		}
//		else {
//			tvConfirm2.setText(R.string.str_edit);
//			tvConfirm2.setBackgroundResource(R.drawable.selector_small_green_button_fill);
//		}
//
//		ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
//		tvConfirm2.setEnabled(monitoringWatchInfo != null && monitoringWatchInfo.isManager);
//	}

//	public void setEnableControls3(boolean flag) {
//		edtThirdContact.setEnabled(flag);
//		edtThirdPhoneNumber.setEnabled(flag);
//
//		btnAcquire2.setEnabled(flag);
//		edtCode2.setEnabled(flag);
//		if(flag) {
//			tvConfirm3.setText(R.string.str_confirm);
//			tvConfirm3.setBackgroundResource(R.drawable.selector_small_button_fill);
//		}
//		else {
//			tvConfirm3.setText(R.string.str_edit);
//			tvConfirm3.setBackgroundResource(R.drawable.selector_small_green_button_fill);
//		}
//
//		ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
//		tvConfirm3.setEnabled(monitoringWatchInfo != null && monitoringWatchInfo.isManager);
//	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		tvConfirm2.setOnClickListener(this);
		tvConfirm3.setOnClickListener(this);

		edtSecondContact.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				contactName2 = editable.toString();
				checkValid();
			}
		});
		edtSecondPhoneNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				contactPhone2 = editable.toString();
				checkValid();
			}
		});

		edtThirdContact.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				contactName3 = editable.toString();
				checkValid();
			}
		});
		edtThirdPhoneNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				contactPhone3 = editable.toString();
				checkValid();
			}
		});

		btnAcquire1.setOnClickListener(v -> {
			if (contactPhone2 == null || contactPhone2.isEmpty()) {
				Util.ShowDialogError(R.string.str_sos_contact_phonenumber_desc);
				edtSecondPhoneNumber.requestFocus();
				return;
			}
			if (contactPhone2 != null && contactPhone2.length() != 11) {
				Util.ShowDialogError(R.string.str_phone_number_incorrect);
				edtSecondPhoneNumber.requestFocus();
				return;
			}
			TryGetCode(edtSecondPhoneNumber.getText().toString(), 1);
		});
		btnAcquire2.setOnClickListener(v -> {
			if (contactPhone3 == null || contactPhone3.isEmpty()) {
				Util.ShowDialogError(R.string.str_sos_contact_phonenumber_desc);
				edtThirdPhoneNumber.requestFocus();
				return;
			}
			if (contactPhone3 != null && contactPhone3.length() != 11) {
				Util.ShowDialogError(R.string.str_phone_number_incorrect);
				edtThirdPhoneNumber.requestFocus();
				return;
			}
			TryGetCode(edtThirdPhoneNumber.getText().toString(), 2);
		});
	}

	private void checkValid() {
		boolean bValid = false;
		if (!contactName1.isEmpty() || !contactPhone1.isEmpty() || !contactName2.isEmpty() || !contactPhone2.isEmpty() || !contactName3.isEmpty() || !contactPhone3.isEmpty()) {
			bValid = true;
		}

		if (bValid) {
			tvInputDesc.setVisibility(View.INVISIBLE);
		} else {
			tvInputDesc.setVisibility(View.VISIBLE);
		}
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_BTN_CONFIRM2:
				onConfirm2();
				break;
			case R.id.ID_BTN_CONFIRM3:
				onConfirm3();
				break;
		}
	}

	private boolean checkConfirm2(){
		boolean check = true;
		if (contactName2 == null || contactName2.isEmpty()) {
			Util.ShowDialogError(R.string.str_sos_contact_name_desc);
			edtSecondContact.requestFocus();
			check = false;
		}else if (contactPhone2 == null || contactPhone2.isEmpty()) {
			Util.ShowDialogError(R.string.str_sos_contact_phonenumber_desc);
			edtSecondPhoneNumber.requestFocus();
			check = false;
		}else if (contactPhone2 != null && contactPhone2.length() != 11) {
			Util.ShowDialogError(R.string.str_phone_number_incorrect);
			edtSecondPhoneNumber.requestFocus();
			check = false;
		}else if (edtCode1.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_sos_contact_code_desc);
			edtCode1.requestFocus();
			check = false;
		}
//		tvConfirm2.setEnabled(check);
		return check;
	}

	private void onConfirm2() {
		if (!checkConfirm2()){
			return;
		}

		if (monitoringWatchInfo != null) {
			m_dlgProgress.show();

			ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
			if (monitoringWatchInfo == null) {
				return;
			}

			HttpAPI.setSosContacts(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, contactName2, contactPhone2, edtCode1.getText().toString(), "", "", edtCode2.getText().toString(), new VolleyCallback() {
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
									switch (iRetCode) {
										case HttpAPIConst.RespCode.PHONE_BLANK:
										case HttpAPIConst.RespCode.PHONE_INVAILD:
											edtSecondPhoneNumber.requestFocus();
											break;
										case HttpAPIConst.RespCode.VALIDATE_CODE_BLANK:
										case HttpAPIConst.RespCode.VALIDATE_CODE_FAIL:
										case HttpAPIConst.RespCode.VALIDATE_CODE_EXPIRED:
											edtCode1.setText("");
											edtCode1.requestFocus();
											break;
										default:
									}
								}
							});

							return;
						}

						monitoringWatchInfo.sosContactName1 = contactName1;
						monitoringWatchInfo.sosContactPhone1 = contactPhone1;
						monitoringWatchInfo.sosContactName2 = contactName2;
						monitoringWatchInfo.sosContactPhone2 = contactPhone2;
						monitoringWatchInfo.sosContactName3 = contactName3;
						monitoringWatchInfo.sosContactPhone3 = contactPhone3;

						Util.updateWatchEntry(monitoringWatchInfo, monitoringWatchInfo);

						setResult(RESULT_OK);
						finish();

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
	}

	private boolean checkConfirm3(){
		boolean check = true;
		if (contactName3 == null || contactName3.isEmpty()) {
			Util.ShowDialogError(R.string.str_sos_contact_name_desc);
			edtThirdContact.requestFocus();
			check = false;
		}else if (contactPhone3 == null || contactPhone3.isEmpty()) {
			Util.ShowDialogError(R.string.str_sos_contact_phonenumber_desc);
			edtThirdPhoneNumber.requestFocus();
			check = false;
		}else if (contactPhone3 != null && contactPhone3.length() != 11) {
			Util.ShowDialogError(R.string.str_phone_number_incorrect);
			edtThirdPhoneNumber.requestFocus();
			check = false;
		}else if (edtCode2.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_sos_contact_code_desc);
			edtCode2.requestFocus();
			check = false;
		}
//		tvConfirm3.setEnabled(check);
		return check;
	}
	private void onConfirm3() {
		if (!checkConfirm3()){
			return;
		}

		if (monitoringWatchInfo != null) {
			m_dlgProgress.show();

			ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
			if (monitoringWatchInfo == null) {
				return;
			}

			HttpAPI.setSosContacts(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, "", "", edtCode1.getText().toString(), contactName3, contactPhone3, edtCode2.getText().toString(), new VolleyCallback() {
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
									switch (iRetCode) {
										case HttpAPIConst.RespCode.PHONE_BLANK:
										case HttpAPIConst.RespCode.PHONE_INVAILD:
											edtThirdPhoneNumber.requestFocus();
											break;
										case HttpAPIConst.RespCode.VALIDATE_CODE_BLANK:
										case HttpAPIConst.RespCode.VALIDATE_CODE_FAIL:
										case HttpAPIConst.RespCode.VALIDATE_CODE_EXPIRED:
											edtCode2.setText("");
											edtCode2.requestFocus();
											break;
										default:
									}
								}
							});
							return;
						}

						monitoringWatchInfo.sosContactName1 = contactName1;
						monitoringWatchInfo.sosContactPhone1 = contactPhone1;
						monitoringWatchInfo.sosContactName2 = contactName2;
						monitoringWatchInfo.sosContactPhone2 = contactPhone2;
						monitoringWatchInfo.sosContactName3 = contactName3;
						monitoringWatchInfo.sosContactPhone3 = contactPhone3;

						Util.updateWatchEntry(monitoringWatchInfo, monitoringWatchInfo);

						setResult(RESULT_OK);
						finish();

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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}

	void startAcquireCountDown(final int index) {
		if (index == 1) {
			btnAcquire1.setEnabled(false);
			if (m_timer1 != null) {
				m_timer1.cancel();
			}

			m_iCount1 = 61;
			m_timer1 = new CountDownTimer(60000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {
					m_iCount1--;
//					int iMin = m_iCount1 / 60;
//					int iSec = m_iCount1 % 60;
//					String sMsg = "";
//					if (iMin != 0)
//						sMsg += iMin + getResources().getString(R.string.str_minute);
//					sMsg += iSec + getResources().getString(R.string.str_second);
					String sMsg = m_iCount1 + getResources().getString(R.string.str_second);
					btnAcquire1.setText(sMsg);
				}
				@Override
				public void onFinish() {
					setAcpuireBtn(index);
//					Util.ShowDialogError(R.string.str_auth_code_not_available);
				}
			};
			m_timer1.start();
		}
		else {
			btnAcquire2.setEnabled(false);
			if (m_timer2 != null) {
				m_timer2.cancel();
			}

			m_iCount2 = 61;
			m_timer2= new CountDownTimer(60000, 1000) {
				@Override
				public void onTick(long millisUntilFinished) {
					m_iCount2--;
//					int iMin = m_iCount2 / 60;
//					int iSec = m_iCount2 % 60;
//					String sMsg = "";
//					if (iMin != 0)
//						sMsg += iMin + getResources().getString(R.string.str_minute);
//					sMsg += iSec + getResources().getString(R.string.str_second);
					String sMsg = m_iCount2 + getResources().getString(R.string.str_second);
					btnAcquire2.setText(sMsg);
				}
				@Override
				public void onFinish() {
					setAcpuireBtn(index);
//					Util.ShowDialogError(R.string.str_auth_code_not_available);
				}
			};
			m_timer2.start();
		}
	}

	private void setAcpuireBtn(int index) {
		if (index == 1) {
			btnAcquire1.setText(R.string.str_acquire_code);
			btnAcquire1.setEnabled(true);
			if (m_timer1 != null) {
				m_timer1.cancel();
			}
		}
		else {
			btnAcquire2.setText(R.string.str_acquire_code);
			btnAcquire2.setEnabled(true);
			if (m_timer2 != null) {
				m_timer2.cancel();
			}
		}
	}

	private void TryGetCode(String phoneNumber, final int index) {
		startAcquireCountDown(index);

		HttpAPI.getUpdateUserInfoVerifyCode(Prefs.Instance().getUserToken(), phoneNumber, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.optString("msg");
						Util.ShowDialogError(ActivitySOSContact.this, sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								setAcpuireBtn(index);
							}
						});
					}
				}
				catch (JSONException e) {
					setAcpuireBtn(index);
					Util.ShowDialogError(R.string.str_auth_code_not_available);
				}
			}

			@Override
			public void onError(Object error) {
				setAcpuireBtn(index);
				Util.ShowDialogError(R.string.str_auth_code_not_available);
			}
		}, TAG);
	}
}
