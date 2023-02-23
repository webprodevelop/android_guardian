//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityAccountManage extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityAccountManage";

	private ImageView		mBackImg;
	private EditText		edtNewAccount;
	private EditText		edtVerifyCode;
	private Button			btnAcquire;
	private TextView		tvConfirm;

	private CountDownTimer		m_timer;
	private int					m_iCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_manage);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if(m_timer != null) {
			m_timer.cancel();
		}
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		TextView tvCurAccount = findViewById(R.id.ID_TXTVIEW_CUR_ACCOUNT);
		edtNewAccount = findViewById(R.id.ID_EDTTEXT_NEW_ACCOUNT);
		edtVerifyCode = findViewById(R.id.ID_EDTTEXT_VERIFY_CODE);
		btnAcquire = findViewById(R.id.ID_BUTTON_ACQUIRE);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);

		tvCurAccount.setText(Prefs.Instance().getUserPhone());
	}

	@Override
	protected void setEventListener() {
		mBackImg.setOnClickListener(this);
		btnAcquire.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
	}


	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_BUTTON_ACQUIRE:
				onVerify();
				break;
			case R.id.ID_BTN_CONFIRM:
				onConfirm();
				break;
		}
	}

	private void onVerify() {
		if (edtNewAccount.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_new_phonenumber);
			edtNewAccount.requestFocus();
			return;
		}
		if (edtNewAccount.getText().toString().length() != 11) {
			Util.ShowDialogError(R.string.str_phone_number_incorrect);
			edtNewAccount.requestFocus();
			return;
		}

		getVerifyCode();
	}

	private void onConfirm() {
		if (edtNewAccount.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_new_phonenumber);
			edtNewAccount.requestFocus();
			return;
		}
		if (edtNewAccount.getText().toString().length() != 11) {
			Util.ShowDialogError(R.string.str_phone_number_incorrect);
			edtNewAccount.requestFocus();
			return;
		}
		if (edtVerifyCode.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_verify_code);
			edtVerifyCode.requestFocus();
			return;
		}

		updateMobile();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (m_timer != null) {
			m_timer.cancel();
		}

		App.Instance().cancelPendingRequests(TAG);
	}

	void startAcquireCountDown() {
		btnAcquire.setEnabled(false);
		if (m_timer != null) {
			m_timer.cancel();
		}

		m_iCount = 61;
		m_timer = new CountDownTimer(60000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				m_iCount--;
//				int iMin = m_iCount / 60;
//				int iSec = m_iCount % 60;
//				String sMsg = "";
//				if (iMin != 0)
//					sMsg += iMin + getResources().getString(R.string.str_minute);
//				sMsg += iSec + getResources().getString(R.string.str_second);
				String sMsg = m_iCount + getResources().getString(R.string.str_second);
				btnAcquire.setText(sMsg);
			}
			@Override
			public void onFinish() {
				setAcpuireBtn();
//				String sMsg = getString(R.string.str_auth_code_not_available);
//				Util.ShowDialogError(sMsg);
			}
		};
		m_timer.start();
	}

	private void setAcpuireBtn() {
		btnAcquire.setText(R.string.str_acquire_code);
		btnAcquire.setEnabled(true);
		if (m_timer != null) {
			m_timer.cancel();
		}
	}

	private void getVerifyCode() {
		startAcquireCountDown();

		HttpAPI.getUpdateUserInfoVerifyCode(Prefs.Instance().getUserToken(), edtNewAccount.getText().toString(), new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(ActivityAccountManage.this, sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								setAcpuireBtn();
							}
						});
					}
					//Global.CODE = jsonObject.getString("verifyCode");
				}
				catch (JSONException e) {
					setAcpuireBtn();
					Util.ShowDialogError(getString(R.string.str_auth_code_not_available));
				}
			}

			@Override
			public void onError(Object error) {
				setAcpuireBtn();
				Util.ShowDialogError(R.string.str_auth_code_not_available);
			}
		}, TAG);
	}

	private void updateMobile() {
		m_dlgProgress.show();

		if (m_timer != null) {
			m_timer.cancel();
		}

		final String strNewMobile = edtNewAccount.getText().toString();
		String strVerifyCode = edtVerifyCode.getText().toString();

		HttpAPI.updateMobile(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strNewMobile, strVerifyCode, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						setAcpuireBtn();
						Util.ShowDialogError(sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								switch (iRetCode) {
									case HttpAPIConst.RespCode.PHONE_BLANK:
									case HttpAPIConst.RespCode.PHONE_INVAILD:
										edtNewAccount.requestFocus();
										break;
									case HttpAPIConst.RespCode.PHONE_REGISTERED:
										edtNewAccount.setText("");
										edtVerifyCode.setText("");
										edtNewAccount.requestFocus();
										break;
									case HttpAPIConst.RespCode.VALIDATE_CODE_BLANK:
									case HttpAPIConst.RespCode.VALIDATE_CODE_FAIL:
									case HttpAPIConst.RespCode.VALIDATE_CODE_EXPIRED:
										edtVerifyCode.setText("");
										edtVerifyCode.requestFocus();
										break;
									default:
								}
							}
						});
						return;
					}

					Prefs.Instance().setUserPhone(strNewMobile);
					Prefs.Instance().commit();
					Util.showToastMessage(ActivityAccountManage.this, R.string.str_change_mobile_success);
					finish();
				}
				catch (JSONException e) {
					setAcpuireBtn();
					Util.ShowDialogError(R.string.str_page_loading_failed);
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				setAcpuireBtn();
				Util.ShowDialogError(R.string.str_page_loading_failed);
			}
		}, TAG);
	}
}
