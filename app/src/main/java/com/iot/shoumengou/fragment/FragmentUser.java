//@formatter:off
package com.iot.shoumengou.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.iot.shoumengou.BuildConfig;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityAccountManage;
import com.iot.shoumengou.activity.ActivityAgree;
import com.iot.shoumengou.activity.ActivityAlarmSet;
import com.iot.shoumengou.activity.ActivityChangePassword;
import com.iot.shoumengou.activity.ActivityLogin;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.activity.ActivityPushManage;
import com.iot.shoumengou.activity.ActivityRescueQuery;
import com.iot.shoumengou.activity.ActivitySOSContact;
import com.iot.shoumengou.activity.ActivityUserData;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.helper.RoomActivity;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class FragmentUser extends Fragment implements View.OnClickListener {

	private ImageView		mPhotoView;
	private TextView		mNameView;
	private TextView		mPhoneNumberView;
	private RelativeLayout	mUserDataView;
	private RelativeLayout	mRescueQuery;
	private RelativeLayout	mSOSContactSetView;
	private ImageView		mSOSContactNotiView;
	private RelativeLayout	mAlarmSetView;
	private RelativeLayout	mPushManageView;
	private ImageView		mPushManageNotiView;
	private RelativeLayout	mAccountManageView;
	private RelativeLayout	mChangePasswordView;
	private RelativeLayout	mLogoutView;
	private RelativeLayout	mRemoveAccountView;
	private RelativeLayout	mAgreement;
	private RelativeLayout	mPrivacy;
	private TextView		mVersion;

	private TextView		mUserPerfect;

	private IOTDBHelper iotdbHelper;

	private ItemWatchInfo	 monitoringWatchInfo = null;

	@Override
	public void onAttach(Context context) {
		iotdbHelper = new IOTDBHelper(context);
		super.onAttach(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_user, container, false);

		initControls(rootView);
		setEventListener();

		return rootView;
	}

	private void initControls(ViewGroup layout) {
		mPhotoView = layout.findViewById(R.id.ID_IMGVIEW_PHOTO);
		mNameView = layout.findViewById(R.id.ID_TXTVIEW_NAME);
		mPhoneNumberView = layout.findViewById(R.id.ID_TXTVIEW_PHONE_NUMBER);
		mUserDataView = layout.findViewById(R.id.ID_LYT_USER_DATA);
		mUserPerfect = layout.findViewById(R.id.ID_TEXT_PERFECT);
		mRescueQuery = layout.findViewById(R.id.ID_LYT_RESCUE_QUERY);
		mSOSContactSetView = layout.findViewById(R.id.ID_LYT_SOS_CONTACT_SET);
		mSOSContactNotiView = layout.findViewById(R.id.ID_IMGVIEW_SOS_CONTACT_NOTI);
		mAlarmSetView = layout.findViewById(R.id.ID_LYT_ALARM_SET);
		mPushManageView = layout.findViewById(R.id.ID_LYT_NOTIFICATION_MANAGE);
//		mPushManageNotiView = layout.findViewById(R.id.ID_IMGVIEW_NOTIFICATION_MANAGE_NOTI);
		mAccountManageView = layout.findViewById(R.id.ID_LYT_CASH_MAANGE);
		mChangePasswordView = layout.findViewById(R.id.ID_LYT_UPDATE_PASSWORD);
		mLogoutView = layout.findViewById(R.id.ID_LYT_LOGOUT);
		mRemoveAccountView = layout.findViewById(R.id.ID_LYT_RMACCOUNT);

		mAgreement = layout.findViewById(R.id.ID_LYT_AGREEMENT);
		mPrivacy = layout.findViewById(R.id.ID_LYT_POLICY);

		mVersion = layout.findViewById(R.id.ID_TXTVIEW_VERSION);
		mVersion.setText(BuildConfig.VERSION_NAME);
		layout.findViewById(R.id.ID_LYT_VERSION).setOnClickListener(this);
	}

	private void setEventListener() {
		mUserDataView.setOnClickListener(this);
		mRescueQuery.setOnClickListener(this);
		mSOSContactSetView.setOnClickListener(this);
		mAlarmSetView.setOnClickListener(this);
		mPushManageView.setOnClickListener(this);
		mAccountManageView.setOnClickListener(this);
		mChangePasswordView.setOnClickListener(this);
		mLogoutView.setOnClickListener(this);
		mRemoveAccountView.setOnClickListener(this);
		mAgreement.setOnClickListener(this);
		mPrivacy.setOnClickListener(this);
	}

	public void showPushManageNotification(boolean show) {
		if (show) {
			mPushManageNotiView.setVisibility(View.VISIBLE);
		} else {
			mPushManageNotiView.setVisibility(View.GONE);
		}

		showInfoNotification();
	}

	private void showInfoNotification() {
		//boolean showInfoNoti = (mSOSContactNotiView.getVisibility() == View.VISIBLE || mPushManageNotiView.getVisibility() == View.VISIBLE);
		boolean showInfoNoti = (mSOSContactNotiView.getVisibility() == View.VISIBLE);

		Prefs.Instance().setInfoNotification(showInfoNoti);
		Prefs.Instance().commit();
		((ActivityMain)getActivity()).showInfoNotification();
	}

	private void setUserInfo() {
		mNameView.setText(Prefs.Instance().getUserName());
		mPhoneNumberView.setText(Prefs.Instance().getUserPhone());
		if (!Prefs.Instance().getUserPhoto().isEmpty()) {
			Picasso.get().load(Prefs.Instance().getUserPhoto()).placeholder(R.drawable.img_contact).into(mPhotoView);
		} else {
			mPhotoView.setImageResource(R.drawable.img_contact);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ActivityMain.REQUEST_USER_DATA) {
			if (resultCode == Activity.RESULT_OK) {
				//setUserInfo();
			}
		} else {
			if (requestCode == ActivityMain.REQUEST_SOS_CONTACT) {
				if (resultCode == Activity.RESULT_OK) {
					//checkSOSContacts();
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!Prefs.Instance().getUserPhoto().isEmpty() && !Prefs.Instance().getCardPhoto().isEmpty()) {
			mUserPerfect.setVisibility(View.INVISIBLE);
		}
		setUserInfo();
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_LYT_USER_DATA:
				onUserData();
				break;
			case R.id.ID_LYT_SOS_CONTACT_SET:
				onSOSContactSet();
				break;
			case R.id.ID_LYT_RESCUE_QUERY:
				onRescueQuery();
				break;
			case R.id.ID_LYT_ALARM_SET:
				onAlarmSet();
				break;
			case R.id.ID_LYT_NOTIFICATION_MANAGE:
//				onChartRequest();
				onConnectService();
				break;
			case R.id.ID_LYT_CASH_MAANGE:
				onAccountManagement();
				break;
			case R.id.ID_LYT_UPDATE_PASSWORD:
				onChangePassword();
				break;
			case R.id.ID_LYT_LOGOUT:
				onLogout();
				break;
			case R.id.ID_LYT_RMACCOUNT:
				onRemoveAccount();
				break;
			case R.id.ID_LYT_AGREEMENT:
				Intent intent1 = new Intent(getActivity(), ActivityAgree.class);
				intent1.putExtra("agreement_policy", true);
				startActivity(intent1);
				break;
			case R.id.ID_LYT_POLICY:
				Intent intent2 = new Intent(getActivity(), ActivityAgree.class);
				intent2.putExtra("agreement_policy", false);
				startActivity(intent2);
				break;
			case R.id.ID_LYT_VERSION:
				if (BuildConfig.VERSION_NAME.compareTo(Util.storeAppVersion) < 0) {
					LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
					View confirmView = layoutInflater.inflate(R.layout.alert_new_version, null);

					final AlertDialog confirmDlg = new AlertDialog.Builder(getActivity()).create();

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
				}
				break;
		}
	}

	private void onUserData() {
		Intent intent = new Intent(getActivity(), ActivityUserData.class);
		startActivity(intent);
	}

	private void onSOSContactSet() {
		Intent intent = new Intent(getActivity(), ActivitySOSContact.class);
		startActivity(intent);
	}

	private void onRescueQuery() {
		Intent intent = new Intent(getActivity(), ActivityRescueQuery.class);
		startActivity(intent);
	}

	private void onAlarmSet() {
		Intent intent = new Intent(getActivity(), ActivityAlarmSet.class);
		startActivity(intent);
	}

	private void onPushManagement() {
		Intent intent = new Intent(getActivity(), ActivityPushManage.class);
		startActivity(intent);
	}

	private void onAccountManagement() {
		Intent intent = new Intent(getActivity(), ActivityAccountManage.class);
		startActivity(intent);
	}

	private void onChangePassword() {
		Intent intent = new Intent(getActivity(), ActivityChangePassword.class);
		startActivity(intent);
	}

	private void onLogout() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View confirmView = layoutInflater.inflate(R.layout.alert_logout, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

		btnConfirm.setOnClickListener(v -> {
			confirmDlg.dismiss();

			iotdbHelper.clearAll();

			if (!Prefs.Instance().isSavedPhone()) {
				Prefs.Instance().setUserPhone("");
				Prefs.Instance().commit();
			}
			if (!Prefs.Instance().isSavedPWSD()) {
				Prefs.Instance().setUserPswd("");
				Prefs.Instance().commit();
			}

			Intent intent = new Intent(getActivity(), ActivityLogin.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.putExtra("log_out", true);
			startActivity(intent);
		});

		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
	}

	private void onRemoveAccount() {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		View confirmView = layoutInflater.inflate(R.layout.alert_remove_account, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

		btnConfirm.setOnClickListener(v -> {
			confirmDlg.dismiss();

			((ActivityMain) Objects.requireNonNull(getActivity())).showProgress();
			HttpAPI.removeAccount(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
				@Override
				public void onSuccess(String response) {
					try {
						JSONObject jsonObject = new JSONObject(response);
						int iRetCode = jsonObject.getInt("retcode");
						if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
							((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
							return;
						}

						iotdbHelper.clearAll();

						if (!Prefs.Instance().isSavedPhone()) {
							Prefs.Instance().setUserPhone("");
							Prefs.Instance().commit();
						}
						if (!Prefs.Instance().isSavedPWSD()) {
							Prefs.Instance().setUserPswd("");
							Prefs.Instance().commit();
						}

						Intent intent = new Intent(getActivity(), ActivityLogin.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intent.putExtra("log_out", true);
						startActivity(intent);

						((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
					}
					catch (JSONException e) {
						((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
						Util.ShowDialogError(R.string.str_api_failed);
					}
				}

				@Override
				public void onError(Object error) {
					((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
					Util.ShowDialogError(R.string.str_api_failed);
				}
			}, "FragmentUser");
		});

		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
	}

	private void onConnectService() {
		if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
					getActivity(),
					new String[]{Manifest.permission.CALL_PHONE},
					AppConst.REQUEST_PERMISSION_STORAGE
			);
			return;
		}
		if (null != Util.servicePhone && !Util.servicePhone.isEmpty()){
			Intent intent = new Intent(Intent.ACTION_CALL);
//			intent.setData(Uri.parse("tel:400-0909-119"));
			intent.setData(Uri.parse("tel:" + Util.servicePhone));
			Objects.requireNonNull(getActivity()).startActivity(intent);
		}
	}

	private void onChartRequest() {
		((ActivityMain) Objects.requireNonNull(getActivity())).showProgress();
		HttpAPI.requestChat(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), 0, new VolleyCallback() {
			@RequiresApi(api = Build.VERSION_CODES.N)
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();

						LayoutInflater layoutInflater = LayoutInflater.from(getContext());
						View confirmView = layoutInflater.inflate(R.layout.alert_chart, null);

						final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

						TextView btnTitle = confirmView.findViewById(R.id.ID_TXTVIEW_TITLE);
						btnTitle.setText(jsonObject.getString("msg"));

						TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

						btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

						confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						confirmDlg.setView(confirmView);
						confirmDlg.show();

						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");

					RoomActivity.setChatId(dataObject.getString("roomId"));
					RoomActivity.setChatPass(dataObject.getString("password"));

					Intent intent = new Intent(getActivity(), RoomActivity.class);
					startActivity(intent);
					((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
				}
				catch (JSONException e) {
					((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
					Util.ShowDialogError(R.string.str_api_failed);
				}
			}

			@Override
			public void onError(Object error) {
				((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
				Util.ShowDialogError(R.string.str_api_failed);
			}
		}, "FragmentUser");
	}
}
