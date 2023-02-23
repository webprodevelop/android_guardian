//@formatter:off
package com.iot.shoumengou.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ActivityForgot extends ActivityBase {
    private final String TAG = "ActivityForgot";

    private EditText m_edtAcquire;
    private EditText m_edtPhone;
    private EditText m_edtPswd;
    private TextView m_txtRequireAuthCode;
    private TextView m_txtRequirePhone;
    private TextView m_txtRequirePswd;
    private Button m_btnConfirm;
    private Button m_btnBack;
    private Button m_btnAcquire;

    private String m_sAuthCode;
    private String m_sPhone;
    private String m_sPswd;

    private CountDownTimer m_timer;
    private int m_iCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        initControls();
        setEventListener();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (m_timer != null) {
            m_timer.cancel();
        }
    }

    @Override
    protected void initControls() {
        super.initControls();

        m_edtAcquire = findViewById(R.id.ID_EDTTEXT_ACQUIRE);
        m_edtPhone = findViewById(R.id.ID_EDTTEXT_PHONE);
        m_edtPswd = findViewById(R.id.ID_EDTTEXT_PSWD);
        m_txtRequireAuthCode = findViewById(R.id.ID_TXTVIEW_REQUIRE_AUTH_CODE);
        m_txtRequirePhone = findViewById(R.id.ID_TXTVIEW_REQUIRE_PHONE);
        m_txtRequirePswd = findViewById(R.id.ID_TXTVIEW_REQUIRE_PSWD);
        m_btnAcquire = findViewById(R.id.ID_BUTTON_ACQUIRE);
        m_btnConfirm = findViewById(R.id.ID_BUTTON_CONFIRM);
        m_btnBack = findViewById(R.id.ID_BUTTON_BACK);

        m_txtRequireAuthCode.setVisibility(View.INVISIBLE);
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
            m_btnConfirm.setEnabled(!(m_edtPhone.getText().toString().isEmpty() &&
                    m_edtPswd.getText().toString().isEmpty() &&
                    m_edtAcquire.getText().toString().isEmpty()));
        }
    };

    @Override
    protected void setEventListener() {
        m_edtPhone.addTextChangedListener(textWatcher);
        m_edtAcquire.addTextChangedListener(textWatcher);
        m_edtPswd.addTextChangedListener(textWatcher);

        m_btnAcquire.setOnClickListener(v -> {
            m_txtRequirePhone.setVisibility(View.INVISIBLE);

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

            TryGetCode();
        });

        m_btnConfirm.setOnClickListener(v -> {
            m_txtRequireAuthCode.setVisibility(View.INVISIBLE);
            m_txtRequirePhone.setVisibility(View.INVISIBLE);
            m_txtRequirePswd.setVisibility(View.INVISIBLE);

            // Check Value
            m_sPhone = m_edtPhone.getText().toString();
            if (m_sPhone.isEmpty()) {
                m_txtRequirePhone.setVisibility(View.VISIBLE);
                m_edtPhone.requestFocus();
                return;
            }
            m_sAuthCode = m_edtAcquire.getText().toString();
            if (m_sAuthCode.isEmpty()) {
                m_txtRequireAuthCode.setVisibility(View.VISIBLE);
                m_edtAcquire.requestFocus();
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

			/*if (m_sAuthCode.compareTo(Global.CODE) != 0) {
				m_txtRequireAuthCode.setVisibility(View.VISIBLE);
				Util.ShowDialogError(R.string.str_auth_code_failed);
				return;
			}*/

            TryReset();
        });

        m_btnBack.setOnClickListener(v -> finish());
    }

    void startAcquireCountDown() {
        m_btnAcquire.setEnabled(false);

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
                m_btnAcquire.setText(sMsg);
            }

            @Override
            public void onFinish() {
                setAcpuireBtn();
//				Util.ShowDialogError(R.string.str_auth_code_not_available);
            }
        };
        m_timer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m_timer != null) {
            m_timer.cancel();
        }

        App.Instance().cancelPendingRequests(TAG);
    }

    private void TryGetCode() {
        startAcquireCountDown();

        HttpAPI.getUpdatePasswordVerifyCode(Prefs.Instance().getUserToken(), m_sPhone, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                        String sMsg = jsonObject.optString("msg");
                        Util.ShowDialogError(ActivityForgot.this, sMsg, new Util.ResultProcess() {
                            @Override
                            public void process() {
                                setAcpuireBtn();
                            }
                        });
                    }
                    //Global.CODE = jsonObject.optString("verifyCode");
                } catch (JSONException e) {
                    setAcpuireBtn();
                    Util.ShowDialogError(R.string.str_reset_failed);
                }
            }

            @Override
            public void onError(Object error) {
                setAcpuireBtn();
                Util.ShowDialogError(R.string.str_reset_failed);
            }
        }, TAG);
    }

    private void setAcpuireBtn() {
        m_btnAcquire.setText(R.string.str_acquire_code);
        m_btnAcquire.setEnabled(true);
        if (m_timer != null) {
            m_timer.cancel();
        }
    }

    private void TryReset() {
        m_dlgProgress.show();
        if (m_timer != null) {
            m_timer.cancel();
        }

        HttpAPI.forgotPassword(m_sPhone, m_sAuthCode, m_sPswd, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                m_dlgProgress.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                        String sMsg = jsonObject.optString("msg");
                        setAcpuireBtn();
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
                                        m_edtAcquire.setText("");
                                        m_edtPhone.requestFocus();
                                        break;
                                    case HttpAPIConst.RespCode.PASSWORD_INVAILD:
                                        m_edtPswd.requestFocus();
                                        break;
                                    case HttpAPIConst.RespCode.VALIDATE_CODE_BLANK:
                                    case HttpAPIConst.RespCode.VALIDATE_CODE_FAIL:
                                    case HttpAPIConst.RespCode.VALIDATE_CODE_EXPIRED:
                                        m_edtAcquire.setText("");
                                        m_edtAcquire.requestFocus();
                                        break;
                                    default:
                                }
                            }
                        });
                        return;
                    }

                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    String strToken = dataObject.optString("token");
//                    String strMobile = dataObject.optString("mobile");
//                    String strPassword = m_sPswd;
//                    String strUser = dataObject.optString("name");
//                    boolean bSex = dataObject.getInt("sex") == 1;
//                    String strBirthday = dataObject.optString("birthday");
//                    String strWeixinId = dataObject.optString("weixin_id");
//                    String strQQId = dataObject.optString("qq_id");
//                    String strEmail = dataObject.optString("email");
//                    String strProvince = dataObject.optString("province");
//                    String strCity = dataObject.optString("city");
//                    String strDistrict = dataObject.optString("district");
//                    String strPhoto = dataObject.optString("picture");
//                    String card = dataObject.optString("id_card_front");
//
//                    Prefs.Instance().setUserToken(strToken);
//                    Prefs.Instance().setUserPhone(strMobile);
//                    Prefs.Instance().setUserPswd(strPassword);
//                    Prefs.Instance().setUserName(strUser);
//                    Prefs.Instance().setSex(bSex);
//                    Prefs.Instance().setBirthday(strBirthday);
//                    Prefs.Instance().setWeixinId(strWeixinId);
//                    Prefs.Instance().setQQId(strQQId);
//                    Prefs.Instance().setEmail(strEmail);
//                    Prefs.Instance().setProvince(strProvince);
//                    Prefs.Instance().setCity(strCity);
//                    Prefs.Instance().setDistrict(strDistrict);
//                    Prefs.Instance().setUserPhoto(strPhoto);
//                    Prefs.Instance().setUserCard(card);
//                    Prefs.Instance().commit();
                    Prefs.Instance().setUserToken(strToken);
                    Prefs.Instance().setUserPhone(m_sPhone);
                    Prefs.Instance().setUserPswd(m_sPswd);
                    Prefs.Instance().commit();
                } catch (JSONException e) {
                    setAcpuireBtn();
                    Util.ShowDialogError(R.string.str_reset_failed);
                    return;
                }
//                Prefs.Instance().setUserPswd(m_sPswd);
//                Prefs.Instance().commit();

				/*Util.ShowDialogSuccess(
						ActivityForgot.this,
						getResources().optString(R.string.str_reset_success)
				);*/
                Util.showToastMessage(ActivityForgot.this, R.string.str_reset_success);
//                StartMain();
                StartLogin();
            }

            @Override
            public void onError(Object error) {
                m_dlgProgress.dismiss();
                setAcpuireBtn();
                Util.ShowDialogError(R.string.str_reset_failed);
            }
        }, TAG);
    }

//    private void StartMain() {
//        Intent intent = new Intent(ActivityForgot.this, ActivityMain.class);
//        startActivity(intent);
//        finish();
//    }
    private void StartLogin() {
        Intent intent = new Intent(ActivityForgot.this, ActivityLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
