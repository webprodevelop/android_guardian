//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
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
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityChangePassword extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityChangePassword";

	private ImageView		mBackImg;
	private EditText		edtOriginPassword;
	private EditText		edtNewPassword;
	private EditText		edtConfirmPassword;
	private TextView		tvConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		edtOriginPassword = findViewById(R.id.ID_EDTTEXT_ORIGN_PASSWORD);
		edtNewPassword = findViewById(R.id.ID_EDTTEXT_NEW_PASSWORD);
		edtConfirmPassword = findViewById(R.id.ID_EDTTEXT_CONFIRM_PASSWORD);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);
	}

	@Override
	protected  void setEventListener() {
		mBackImg.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_BTN_CONFIRM:
				onConfirm();
				break;
		}
	}

	private void onConfirm() {
		if (edtOriginPassword.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_orgin_password);
			edtOriginPassword.requestFocus();
			return;
		} else if (edtNewPassword.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_new_password);
			edtNewPassword.requestFocus();
			return;
		} else if (edtNewPassword.length() < 6 || edtNewPassword.length() > 20) {
			Util.ShowDialogError(R.string.str_password_invalid);
			edtNewPassword.requestFocus();
			return;
		} else if (edtConfirmPassword.getText().toString().isEmpty()) {
			Util.ShowDialogError(R.string.str_input_confirm_password);
			edtConfirmPassword.requestFocus();
			return;
		} else if (!edtNewPassword.getText().toString().equals(edtConfirmPassword.getText().toString())) {
			Util.ShowDialogError(R.string.str_incorrect_confirm_password);
			edtConfirmPassword.requestFocus();
			return;
		}

		resetPassword();
	}

	private void resetPassword() {
		m_dlgProgress.show();

		final String strNewPassword = edtNewPassword.getText().toString();
		String strOldPassword = edtOriginPassword.getText().toString();

		HttpAPI.resetPassword(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strOldPassword, strNewPassword, new VolleyCallback() {
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
									case HttpAPIConst.RespCode.PASSWORD_BLANK:
									case HttpAPIConst.RespCode.PASSWORD_INVAILD:
										edtNewPassword.requestFocus();
										break;
									case HttpAPIConst.RespCode.PASSWORD_OLD_FAIL:
										edtOriginPassword.setText("");
										edtOriginPassword.requestFocus();
										break;
									default:
								}
							}
						});
						return;
					}

					Prefs.Instance().setUserPswd(strNewPassword);
					Prefs.Instance().commit();

					Util.showToastMessage(ActivityChangePassword.this, R.string.str_change_password_success);

					finish();
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_change_password_failed);
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_change_password_failed);
			}
		}, TAG);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}
}
