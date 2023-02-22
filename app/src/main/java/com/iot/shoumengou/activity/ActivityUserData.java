//@formatter:off
package com.iot.shoumengou.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iot.shoumengou.App;
import com.iot.shoumengou.BuildConfig;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.iot.shoumengou.util.Util.resizeBitmap;

public class ActivityUserData extends ActivityBase implements View.OnClickListener {
	private final String TAG = "ActivityUserData";

	private final static int            CAMERA_REQUEST_CODE     = 0;
	private final static int            GALLERY_REQUEST_CODE    = 1;

	private ImageView				ivBack;
	private RelativeLayout			rlImportPhoto;
	private EditText				edtName;
	private LinearLayout			llWoman;
	private LinearLayout			llMan;
	private TextView				tvBirthday;
	private TextView				tvContent;
	private TextView				tvPhone;
	private EditText				edtWeixinId;
	private EditText				edtQQNumber;
	private EditText				edtMail;
	private TextView				tvConfirm;
	private TextView				tvUploadPhoto;
	private ImageView				ivPhoto;

	private RelativeLayout			rlCard;
	private TextView				tvCardInfo;
	private ImageView				ivCard;

	private Bitmap					photoBitmap = null, cardBitmap = null;

	private Button				m_btnAcquire;
	private CountDownTimer		m_timer;
	private int					m_iCount;
	AlertDialog confirmDlg;

	private boolean				isCardImport = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_data);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();

		loadData();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(
						this,
						new String[] { Manifest.permission.CAMERA },
						AppConst.REQUEST_PERMISSION_CAMERA
				);
			}
		}
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

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		rlImportPhoto = findViewById(R.id.ID_LYT_IMPORT_PHOTO);
		edtName = findViewById(R.id.ID_EDTTEXT_NAME);
		llWoman = findViewById(R.id.ID_LYT_WOMEN);
		llMan = findViewById(R.id.ID_LYT_MAN);
		tvBirthday = findViewById(R.id.ID_TXTVIEW_BIRTHDAY);
		tvContent = findViewById(R.id.ID_TXTVIEW_CONTENT);
		tvPhone = findViewById(R.id.ID_TXTVIEW_PHONE);
		edtWeixinId = findViewById(R.id.ID_EDTTEXT_WEIXIN_ID);
		edtQQNumber = findViewById(R.id.ID_EDTTEXT_QQ_NUMBER);
		edtMail = findViewById(R.id.ID_EDTTEXT_MAIL);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);
		tvUploadPhoto = findViewById(R.id.ID_TXTVIEW_UPLOAD_PHOTO);
		ivPhoto = findViewById(R.id.ID_IMGVIEW_PHOTO);

		rlCard = findViewById(R.id.ID_RL_CARD);
		tvCardInfo = findViewById(R.id.ID_TEXT_CARD);
		ivCard = findViewById(R.id.ID_IMG_CARD);
	}

	private void checkConformBtn(){
		if (edtName.getText().toString().isEmpty()) {
			tvConfirm.setEnabled(false);
		}else {
			tvConfirm.setEnabled(true);
		}
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		rlImportPhoto.setOnClickListener(this);
		llWoman.setOnClickListener(this);
		llMan.setOnClickListener(this);
		tvBirthday.setOnClickListener(this);
		tvPhone.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
		rlCard.setOnClickListener(this);

//		tvConfirm.setText(R.string.str_edit);
		tvConfirm.setEnabled(false);

//		rlImportPhoto.setClickable(false);
		edtName.setEnabled(false);
		llMan.setClickable(false);
		llWoman.setClickable(false);
		tvBirthday.setClickable(false);
//		tvPhone.setClickable(false);
//		rlCard.setClickable(false);
	}

	private void loadData() {
		edtName.setText(Prefs.Instance().getUserName());
		if (Prefs.Instance().getSex()) {
			onSelectSex(llMan);
		} else {
			onSelectSex(llWoman);
		}
		if (!Prefs.Instance().getBirthday().isEmpty()) {
			tvBirthday.setText(Util.convertDateString(Prefs.Instance().getBirthday(), false));
			//tvBirthday.setTextColor(Color.BLACK);
		}
		tvContent.setText(Prefs.Instance().getContent());
		tvPhone.setText(Prefs.Instance().getUserPhone());
		edtWeixinId.setText(Prefs.Instance().getWeixinId());
		edtQQNumber.setText(Prefs.Instance().getQQId());
		edtMail.setText(Prefs.Instance().getEmail());

		String userPhoto = Prefs.Instance().getUserPhoto();
		if (!userPhoto.isEmpty()) {
			ivPhoto.setVisibility(View.VISIBLE);
			tvUploadPhoto.setVisibility(View.GONE);

			Picasso.get().load(userPhoto).placeholder(R.drawable.img_contact).into(ivPhoto);
		} else {
			ivPhoto.setVisibility(View.GONE);
			tvUploadPhoto.setVisibility(View.VISIBLE);
		}

		String userCard = Prefs.Instance().getCardPhoto();
		if (!userCard.isEmpty()) {
			tvCardInfo.setVisibility(View.GONE);

			Picasso.get().load(userCard).placeholder(R.drawable.img_contact).into(ivCard);
		} else {
			tvCardInfo.setVisibility(View.VISIBLE);
		}
		checkConformBtn();
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_LYT_IMPORT_PHOTO:
				isCardImport = false;
				onImportPhoto(false);
				break;
			case R.id.ID_LYT_MAN:
			case R.id.ID_LYT_WOMEN:
				onSelectSex(view);
				break;
			case R.id.ID_TXTVIEW_BIRTHDAY:
				onBirthday();
				break;
			case R.id.ID_TXTVIEW_PHONE:
//				onPhoneNUmber();
				break;
			case R.id.ID_BTN_CONFIRM:
				onConfirm();
				break;
			case R.id.ID_RL_CARD:
				isCardImport = true;
				onImportPhoto(true);
				break;
		}
	}

	private void onPhoneNUmber() {
		if (confirmDlg == null) {
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View parentView = layoutInflater.inflate(R.layout.dialog_confirm_phone_number, null);

			confirmDlg = new AlertDialog.Builder(this).create();

			final EditText editPhoneNumber = parentView.findViewById(R.id.ID_TXTVIEW_PHONE_NUMBER);
			m_btnAcquire = parentView.findViewById(R.id.ID_BUTTON_ACQUIRE);
			TextView tvConfirm = parentView.findViewById(R.id.ID_BTN_CONFIRM);
			ImageView ivClose = parentView.findViewById(R.id.ID_IMG_CLOSE);

			editPhoneNumber.setText(tvPhone.getText().toString());

			m_btnAcquire.setOnClickListener(v -> startAcquireCountDown());
			tvConfirm.setOnClickListener(v -> TryGetCode(editPhoneNumber.getText().toString()));
			ivClose.setOnClickListener(v -> confirmDlg.dismiss());

			confirmDlg.setView(parentView);
		}

		confirmDlg.show();
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

	private void setAcpuireBtn() {
		m_btnAcquire.setText(R.string.str_acquire_code);
		m_btnAcquire.setEnabled(true);
		if (m_timer != null) {
			m_timer.cancel();
		}
	}

	private void TryGetCode(String phoneNumber) {
		startAcquireCountDown();

		HttpAPI.getUpdateUserInfoVerifyCode(Prefs.Instance().getUserToken(), phoneNumber, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.optString("msg");
						Util.ShowDialogError(ActivityUserData.this, sMsg, new Util.ResultProcess() {
							@Override
							public void process() {
								setAcpuireBtn();
							}
						});

						return;
					}
					confirmDlg.dismiss();
				}
				catch (JSONException e) {
					setAcpuireBtn();
					Util.ShowDialogError(R.string.str_auth_code_not_available);
				}
			}

			@Override
			public void onError(Object error) {
				setAcpuireBtn();
				Util.ShowDialogError(R.string.str_auth_code_not_available);
			}
		}, TAG);
	}

	private void onBirthday() {
		final DatePickerDialog dlgDatePicker;
		Calendar calendar = Calendar.getInstance();

		dlgDatePicker = new DatePickerDialog(
				this,
				(view, year, month, dayOfMonth) -> {
					Calendar calendar1 = Calendar.getInstance();
					calendar1.set(Calendar.YEAR, year);
					calendar1.set(Calendar.MONTH, month);
					calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					Date date = calendar1.getTime();

					tvBirthday.setText(Util.getDateFormatString(date));
					tvBirthday.setTextColor(Color.BLACK);
				},
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)
		);
		dlgDatePicker.show();
	}

	private void onImportPhoto(boolean isCard) {
		final BottomSheetDialog dialog = new BottomSheetDialog(this);
		@SuppressLint("InflateParams") View parentView = getLayoutInflater().inflate(R.layout.dialog_select_picture_source, null);
		dialog.setContentView(parentView);
		((View)parentView.getParent()).setBackgroundColor(Color.TRANSPARENT);

		if (isCard) {
			TextView tvTitle = parentView.findViewById(R.id.ID_TXTVIEW_TITLE);
			tvTitle.setText(R.string.str_photo_import_title);
		}

		TextView tvPhoto = parentView.findViewById(R.id.ID_TXTVIEW_TAKE_PHOTO);
		TextView tvGallery = parentView.findViewById(R.id.ID_TXTVIEW_FROM_GALLERY);
		TextView tvCancel = parentView.findViewById(R.id.ID_TXTVIEW_CANCEL);

		tvPhoto.setOnClickListener(v -> {
			dialog.dismiss();

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
			Uri outputFileUri = getCaptureImageOutputUri();
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

			startActivityForResult(intent, CAMERA_REQUEST_CODE);
		});
		tvGallery.setOnClickListener(v -> {
			dialog.dismiss();

			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, ""), GALLERY_REQUEST_CODE);
		});
		tvCancel.setOnClickListener(v -> dialog.dismiss());

		dialog.show();
	}

	private Uri getCaptureImageOutputUri() {
		Uri outputFileUri = null;
		File getImage = getExternalCacheDir();
		if (getImage != null) {
			outputFileUri = FileProvider.getUriForFile(this, "com.iot.shoumengou.provider",
				new File(getImage.getPath(), "idCard.png"));
		}
		return outputFileUri;
	}

	private void onSelectSex(View view) {
		llMan.setSelected(false);
		llWoman.setSelected(false);

		view.setSelected(true);
	}

	private void onConfirm() {
//		if (!rlImportPhoto.isClickable()) {
//			tvConfirm.setText(R.string.str_confirm);
//			tvConfirm.setBackgroundResource(R.drawable.selector_small_button_fill);
//			rlImportPhoto.setClickable(true);
//			//edtName.setEnabled(true);
//			//llMan.setClickable(true);
//			//llWoman.setClickable(true);
//			//tvBirthday.setClickable(true);
//			tvPhone.setClickable(true);
//			rlCard.setClickable(true);
//
//			return;
//		}
		if (edtName.getText().toString().isEmpty()) {
			Util.showToastMessage(this, R.string.str_input_name);
			edtName.requestFocus();
			return;
		} else if (tvBirthday.getText().toString().equals(getString(R.string.str_select_required))) {
			Util.showToastMessage(this, R.string.str_select_birthday);
			return;
		}/* else  if (photoBitmap == null) {
			Util.showToastMessage(this, R.string.str_select_picture);
			return;
		}*/

		updateUserInfo();
	}

	private void updateUserInfo() {
		m_dlgProgress.show();

		String strName = edtName.getText().toString();
		boolean bSex = !llWoman.isSelected();
		String strBirthday = Util.convertDateString(tvBirthday.getText().toString(), true);
		String strMobile = tvPhone.getText().toString();
		String strPhoto = photoBitmap != null ? Util.getEncoded64ImageStringFromBitmap(photoBitmap) : "";
		String strCard = cardBitmap != null ? Util.getEncoded64ImageStringFromBitmap(cardBitmap) : "";

		HttpAPI.updateUserInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), strName, bSex, strBirthday, strPhoto, strCard, strMobile, new VolleyCallback() {
			@Override
			public void onSuccess(String response) {
				m_dlgProgress.dismiss();

				try {
					JSONObject jsonObject = new JSONObject(response);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.optString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					JSONObject dataObject = jsonObject.getJSONObject("data");
					String name = dataObject.optString("name");
					String mobile = dataObject.optString("mobile");
					boolean sex = dataObject.optInt("sex") == 1;
					String birthday = dataObject.optString("birthday");
					String weixinId = dataObject.optString("weixin_id");
					String qqId = dataObject.optString("qq_id");
					String email = dataObject.optString("email");
					String picture = dataObject.optString("picture");
					String card = dataObject.optString("id_card_front");

					Prefs.Instance().setUserName(name);
					Prefs.Instance().setUserPhone(mobile);
					Prefs.Instance().setSex(sex);
					Prefs.Instance().setBirthday(birthday);
					Prefs.Instance().setWeixinId(weixinId);
					Prefs.Instance().setQQId(qqId);
					Prefs.Instance().setEmail(email);
					Prefs.Instance().setUserPhoto(picture);
					Prefs.Instance().setUserCard(card);
					Prefs.Instance().commit();

					Util.showToastMessage(ActivityUserData.this, R.string.str_update_success);

					setResult(RESULT_OK);
					finish();
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_update_failed);
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_update_failed);
			}
		}, TAG);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == GALLERY_REQUEST_CODE || requestCode == CAMERA_REQUEST_CODE) {
				Uri pathUri = (requestCode == CAMERA_REQUEST_CODE) ? getCaptureImageOutputUri() : data.getData();
				Bitmap bitmap = null;
				if (pathUri == null) {
					bitmap = (Bitmap) data.getExtras().get("data");
				} else {
					try {
						bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pathUri);
						bitmap = getRotatedBitmap(bitmap, pathUri);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (bitmap != null) {
					final int REQ_SIZE = isCardImport ? 500 : 300;
					int rW, rH;
					if (bitmap.getWidth() < bitmap.getHeight()) {
						rW = REQ_SIZE;
						rH = (int) (REQ_SIZE * (bitmap.getHeight() / (float) bitmap.getWidth()));
					} else {
						rH = REQ_SIZE;
						rW = (int) (REQ_SIZE * (bitmap.getWidth() / (float) bitmap.getHeight()));
					}
					if (!isCardImport) {
						if (photoBitmap != null) {
							ivPhoto.setImageBitmap(null);
							photoBitmap.recycle();
							photoBitmap = null;
						}

						photoBitmap = resizeBitmap(bitmap, 300, 300);//Bitmap.createScaledBitmap(bitmap, rW, rH, false);//Utils.decodeSampledBitmapFromByteArray(byteArray, rW, rH);
						ivPhoto.setVisibility(View.VISIBLE);
						tvUploadPhoto.setVisibility(View.GONE);
						ivPhoto.setImageBitmap(photoBitmap);
					} else {
//						updateCardInfo(bitmap);
						updateCardInfo(resizeBitmap(bitmap, rW, rH));
//						updateCardInfo(resizeBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight()));
					}
					bitmap.recycle();
				}
			}
		}
	}

	private void updateCardInfo (Bitmap bitmap) {
		m_dlgProgress.show();
		HttpAPI.getIdCardFrontInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(),
				Util.getEncoded64ImageStringFromBitmap(bitmap), new VolleyCallback() {
					@Override
					public void onSuccess(String result) {
						m_dlgProgress.dismiss();
						try {
							JSONObject jsonObject = new JSONObject(result);
							int iRetCode = jsonObject.getInt("retcode");
							if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
								String sMsg = jsonObject.getString("msg");
								Util.ShowDialogError(sMsg);
								return;
							}

							JSONObject dataObject = jsonObject.getJSONObject("data");
							edtName.setText(dataObject.getString("name"));
							tvBirthday.setText(Util.convertDateString(dataObject.getString("birthday"), false));
							if (dataObject.getString("sex").equals("1")) {
								onSelectSex(llMan);
							} else {
								onSelectSex(llWoman);
							}

							if (cardBitmap != null) {
								ivCard.setImageBitmap(null);
								cardBitmap.recycle();
								cardBitmap = null;
							}

							cardBitmap = bitmap;
							ivCard.setImageBitmap(cardBitmap);
							tvCardInfo.setVisibility(View.GONE);
						}
						catch (Exception e) {
							Util.ShowDialogError(R.string.str_page_loading_failed);
						}
						checkConformBtn();
					}

					@Override
					public void onError(Object error) {
						m_dlgProgress.dismiss();
						Util.ShowDialogError(R.string.str_page_loading_failed);
					}
				}, TAG);
	}

	private Bitmap getRotatedBitmap(Bitmap bitmap, Uri photoPath) {
		ExifInterface ei;
		Bitmap rotatedBitmap = bitmap;
		try {
			InputStream input = getContentResolver().openInputStream(photoPath);
			if (Build.VERSION.SDK_INT > 23)
				ei = new ExifInterface(input);
			else {
				ei = new ExifInterface(photoPath.getPath());
			}
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED);

			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotatedBitmap = rotateImage(bitmap, 90);
					break;

				case ExifInterface.ORIENTATION_ROTATE_180:
					rotatedBitmap = rotateImage(bitmap, 180);
					break;

				case ExifInterface.ORIENTATION_ROTATE_270:
					rotatedBitmap = rotateImage(bitmap, 270);
					break;

				case ExifInterface.ORIENTATION_NORMAL:
				default:
					rotatedBitmap = bitmap;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rotatedBitmap;
	}

	public Bitmap rotateImage(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
				matrix, true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}
}
