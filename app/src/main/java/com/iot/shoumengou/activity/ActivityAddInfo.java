//@formatter:off
package com.iot.shoumengou.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterArea;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemArea;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;
import com.iot.shoumengou.view.MultiSelectionSpinner;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.iot.shoumengou.util.Util.resizeBitmap;

public class ActivityAddInfo extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityAddInfo";

	private final int		REQUEST_BIND_COMPLETE	= 1;
	private final static int            CAMERA_REQUEST_CODE     = 0;
	private final static int            GALLERY_REQUEST_CODE    = 2;

	private ImageView		ivBack;
	private RelativeLayout	rlCardFront, rlCardBack;
	private ImageView		ivCardFront, ivCardBack;
	private TextView		tvCardFront, tvCardBack;
	private LinearLayout	llCardInfo;
	private TextView		tvCardNum;
	private TextView 		tvCardAddr;
	private Spinner			spProvince, spCity, spDistrict;
	private final ArrayList<ItemArea> areaArrayList = new ArrayList<>();
	private final ArrayList<ItemArea> cityList = new ArrayList<>();
	private final ArrayList<ItemArea> districtList = new ArrayList<>();
	private AdapterArea		provinceAdapter, cityAdapter, districtAdapter;
	private EditText		tvCC;
	private MultiSelectionSpinner 			spCurrentHistory;
	private EditText		editOtherHistory;
	private MultiSelectionSpinner			spFamilyHistory;
	private EditText		editOtherFamilyHistory;

	private TextView		tvComplete;

	private ItemWatchInfo		itemWatchInfo;

	String [] illHistory;

	private boolean			isCardImport = true;
	private Bitmap			cardFrontBitmap, cardBackBitmap;
	private final Handler handler = new Handler();

	private final Runnable runnable = this::setEventListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_info);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		itemWatchInfo = (ItemWatchInfo) getIntent().getSerializableExtra("device_data");
//		ArrayList<String> illList = getIntent().getStringArrayListExtra("ill_list");

		if (itemWatchInfo == null) {
			finish();
		}

		initControls();
		loadAreaData();
		loadDefaultData();
		handler.postDelayed(runnable, 0);
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);

		rlCardFront = findViewById(R.id.ID_RL_CARD_FRONT);
		ivCardFront = findViewById(R.id.ID_IMG_CARD_FRONT);
		tvCardFront = findViewById(R.id.ID_TEXT_CARD_FRONT);
		rlCardBack = findViewById(R.id.ID_RL_CARD_BACK);
		ivCardBack = findViewById(R.id.ID_IMG_CARD_BACK);
		tvCardBack = findViewById(R.id.ID_TEXT_CARD_BACK);

		llCardInfo = findViewById(R.id.ID_LL_CARD_INFO);
		tvCardNum = findViewById(R.id.ID_CARD_NUMBER);
		tvCardAddr = findViewById(R.id.ID_CARD_ADDR);

//		llCardInfo.setVisibility(View.GONE);

		spProvince = findViewById(R.id.ID_SPINNER_PROVINCE);
		provinceAdapter = new AdapterArea(ActivityAddInfo.this, areaArrayList);
		spProvince.setAdapter(provinceAdapter);
		spCity = findViewById(R.id.ID_SPINNER_CITY);
		cityAdapter = new AdapterArea(ActivityAddInfo.this, cityList);
		spCity.setAdapter(cityAdapter);
		spDistrict = findViewById(R.id.ID_SPINNER_DISTRICT);
		districtAdapter = new AdapterArea(ActivityAddInfo.this, districtList);
		spDistrict.setAdapter(districtAdapter);

		tvCC = findViewById(R.id.ID_EDIT_CC);

		spCurrentHistory = findViewById(R.id.ID_SPINNER_CURRENT_HISTORY);
		illHistory = getResources().getStringArray(R.array.user_ill_array);
		List<String> illHistoryList = Arrays.asList(illHistory);
		spCurrentHistory.setItems(illHistoryList);
//		ArrayAdapter<String> currentAdapter = new ArrayAdapter(this, R.layout.item_spinner, illHistory);
//		currentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Setting the ArrayAdapter data on the Spinner
//		spCurrentHistory.setAdapter(currentAdapter);
//		spCurrentHistory.setSelection(0);
		editOtherHistory = findViewById(R.id.ID_EDIT_OTHER_HISTORY);

		spFamilyHistory = findViewById(R.id.ID_SPINNER_FAMILY_HISTORY);
//		List<String> illHistoryList = Arrays.asList(illHistory);
		spFamilyHistory.setItems(illHistoryList);
//		ArrayAdapter<String> familyAdapter = new ArrayAdapter(this, R.layout.item_spinner, illHistory);
//		familyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//Setting the ArrayAdapter data on the Spinner
//		spFamilyHistory.setAdapter(familyAdapter);
//		spFamilyHistory.setSelection(0);

		editOtherFamilyHistory = findViewById(R.id.ID_EDIT_OTHER_FAMILY_HISTORY);

		tvComplete = findViewById(R.id.ID_BTN_COMPLETE);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		rlCardFront.setOnClickListener(this);
		rlCardBack.setOnClickListener(this);

		spProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				cityList.clear();

				if (!areaArrayList.get(position).list.isEmpty()) {
					cityList.addAll(areaArrayList.get(position).list);
				}
				cityAdapter = new AdapterArea(ActivityAddInfo.this, cityList);
				spCity.setAdapter(cityAdapter);
				if (!areaArrayList.get(position).list.isEmpty()) {
					districtList.clear();
					if (!cityList.get(0).list.isEmpty()) {
						districtList.addAll(cityList.get(0).list);
					}
					districtAdapter = new AdapterArea(ActivityAddInfo.this, districtList);
					spDistrict.setAdapter(districtAdapter);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				districtList.clear();
				if (!cityList.get(position).list.isEmpty()) {
					districtList.addAll(cityList.get(position).list);
				}
				districtAdapter = new AdapterArea(ActivityAddInfo.this, districtList);
				spDistrict.setAdapter(districtAdapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

//		spCurrentHistory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//
//			}
//		});

//		spFamilyHistory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//
//			}
//		});

		tvComplete.setOnClickListener(this);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_RL_CARD_FRONT:
				isCardImport = true;
				onImportPhoto();
				break;
			case R.id.ID_RL_CARD_BACK:
				isCardImport = false;
				onImportPhoto();
				break;
			case R.id.ID_BTN_COMPLETE:
				onComplete();
				break;
		}
	}

	private void loadDefaultData() {
		if (!itemWatchInfo.idCardFront.isEmpty()) {
			Picasso.get().load(itemWatchInfo.idCardFront).placeholder(R.drawable.img_contact).into(ivCardFront);
			tvCardFront.setVisibility(View.GONE);
		}
		if (!itemWatchInfo.idCardBack.isEmpty()) {
			Picasso.get().load(itemWatchInfo.idCardBack).placeholder(R.drawable.img_contact).into(ivCardBack);
			tvCardBack.setVisibility(View.GONE);
		}

		int provinceIndex = 0;
		for (int i = 0; i < areaArrayList.size(); i++) {
			if (areaArrayList.get(i).areaName.equals(itemWatchInfo.socialProvince)) {
				spProvince.setSelection(i);
				provinceIndex = i;
				break;
			}
		}

		cityList.clear();
		cityList.addAll(areaArrayList.get(provinceIndex).list);
		cityAdapter.notifyDataSetChanged();
		int cityIndex = 0;
		for (int i = 0; i < cityList.size(); i++) {
			if (cityList.get(i).areaName.equals(itemWatchInfo.socialCity)) {
				spCity.setSelection(i);
				cityIndex = i;
				break;
			}
		}

		districtList.clear();
		districtList.addAll(cityList.get(cityIndex).list);
		districtAdapter.notifyDataSetChanged();
		for (int i = 0; i < districtList.size(); i++) {
			if (districtList.get(i).areaName.equals(itemWatchInfo.socialDisctrict)) {
				spDistrict.setSelection(i);
				break;
			}
		}

		spCurrentHistory.setSelectionByString(itemWatchInfo.ill_history);
//		for (int i = 0; i < illHistory.length; i++) {
//			if (illHistory[i].equals(itemWatchInfo.ill_history)) {
//				spCurrentHistory.setSelection(i);
//				break;
//			}
//		}

		spFamilyHistory.setSelectionByString(itemWatchInfo.family_ill_history);
//		for (int i = 0; i < illHistory.length; i++) {
//			if (illHistory[i].equals(itemWatchInfo.family_ill_history)) {
//				spFamilyHistory.setSelection(i);
//				break;
//			}
//		}

		tvCC.setText(itemWatchInfo.socialNum);
		editOtherHistory.setText(itemWatchInfo.ill_history_other);
		editOtherFamilyHistory.setText(itemWatchInfo.family_ill_history_other);
		tvCardNum.setText(itemWatchInfo.idCardNum);
		tvCardAddr.setText(itemWatchInfo.idCardAddr);
	}

	private void loadAreaData() {
		m_dlgProgress.show();
		String json;
		try {
			InputStream is = getAssets().open("china_area.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, StandardCharsets.UTF_8);

			JSONArray provinceList = new JSONArray(json);
			for(int i = 0; i < provinceList.length(); i++) {
				JSONObject provinceObj = provinceList.getJSONObject(i);
				ItemArea province = new ItemArea();
				province.areaCode = provinceObj.optString("AreaCode");
				province.areaLevel = provinceObj.optInt("AreaLevel");
				province.areaName = provinceObj.optString("AreaName");
				province.cityCenter = provinceObj.optString("CityCenter");
				province.cityCode = provinceObj.optString("CityCode");

				JSONArray cityList = provinceObj.optJSONArray("list");
				if (cityList != null) {
					for (int j = 0; j < cityList.length(); j++) {
						JSONObject cityObj = cityList.getJSONObject(j);
						ItemArea city = new ItemArea();
						city.areaCode = cityObj.optString("AreaCode");
						city.areaLevel = cityObj.optInt("AreaLevel");
						city.areaName = cityObj.optString("AreaName");
						city.cityCenter = cityObj.optString("CityCenter");
						city.cityCode = cityObj.optString("CityCode");

						JSONArray districtList = cityObj.optJSONArray("list");
						if (districtList != null) {
							for (int k = 0; k < districtList.length(); k++) {
								JSONObject districtObj = districtList.getJSONObject(k);
								ItemArea district = new ItemArea();
								district.areaCode = districtObj.optString("AreaCode");
								district.areaLevel = districtObj.optInt("AreaLevel");
								district.areaName = districtObj.optString("AreaName");
								district.cityCenter = districtObj.optString("CityCenter");
								district.cityCode = districtObj.optString("CityCode");

								city.list.add(district);
							}
						}
						province.list.add(city);
					}
				}

				areaArrayList.add(province);
			}

			m_dlgProgress.dismiss();
			provinceAdapter.notifyDataSetChanged();
			spProvince.setSelection(0);
		}
		catch (Exception ex) {
			m_dlgProgress.dismiss();
			ex.printStackTrace();
		}
	}

	private void onImportPhoto() {
		final BottomSheetDialog dialog = new BottomSheetDialog(this);
		@SuppressLint("InflateParams") View parentView = getLayoutInflater().inflate(R.layout.dialog_select_picture_source, null);
		dialog.setContentView(parentView);
		((View)parentView.getParent()).setBackgroundColor(Color.TRANSPARENT);

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

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, getCaptureImageOutputUri());
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

	private void onComplete() {
		if (tvCC.getText().toString().isEmpty()) {
				//editOtherHistory.getText().toString().isEmpty() ||
				//editOtherFamilyHistory.getText().toString().isEmpty()) {
			Util.showToastMessage(this, R.string.str_fill_add_info);
			return;
		}

		updateWatchInfo();
	}

	private void updateWatchInfo() {
		m_dlgProgress.show();

		itemWatchInfo.idCardFront = cardFrontBitmap != null ? Util.getEncoded64ImageStringFromBitmap(cardFrontBitmap) : "";
		itemWatchInfo.idCardBack = cardBackBitmap != null ? Util.getEncoded64ImageStringFromBitmap(cardBackBitmap) : "";
		itemWatchInfo.idCardNum = tvCardNum.getText().toString();
		itemWatchInfo.idCardAddr = tvCardAddr.getText().toString();
		itemWatchInfo.socialNum = tvCC.getText().toString();

		itemWatchInfo.ill_history =  spCurrentHistory.getSelectedItemsAsString();
		itemWatchInfo.ill_history_other = editOtherHistory.getText().toString();
		itemWatchInfo.family_ill_history = spFamilyHistory.getSelectedItemsAsString();
		itemWatchInfo.family_ill_history_other = editOtherFamilyHistory.getText().toString();

		ArrayList<ItemArea> cities = areaArrayList.get(spProvince.getSelectedItemPosition()).list;
		itemWatchInfo.socialProvince = areaArrayList.get(spProvince.getSelectedItemPosition()).areaName;
		itemWatchInfo.socialCity = cities.get(spCity.getSelectedItemPosition()).areaName;
		itemWatchInfo.socialDisctrict = cities.get(spCity.getSelectedItemPosition()).list.get(spDistrict.getSelectedItemPosition()).areaName;

		HttpAPI.updateWatchInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemWatchInfo, new VolleyCallback() {
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

					JSONObject dataObject = jsonObject.getJSONObject("data");
					itemWatchInfo.chargeStatus = dataObject.getInt("charge_status");
					itemWatchInfo.serviceStartDate = dataObject.getString("service_start");
					itemWatchInfo.serviceEndDate = dataObject.getString("service_end");
					itemWatchInfo.idCardBack = dataObject.getString("user_id_card_back");
					itemWatchInfo.idCardFront = dataObject.getString("user_id_card_front");

					ItemWatchInfo itemWatch = Util.findWatchEntry(itemWatchInfo.serial);
					if (itemWatch == null) {
						Util.addWatchEntry(itemWatchInfo);
					} else {
						Util.updateWatchEntry(itemWatch, itemWatchInfo);
					}

					startBindCompleteActivity();
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

	private void startBindCompleteActivity() {
		Intent intent = new Intent(this, ActivityBindComplete.class);
		intent.putExtra("device_data", itemWatchInfo);

		startActivityForResult(intent, REQUEST_BIND_COMPLETE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		handler.removeCallbacks(runnable);
		App.Instance().cancelPendingRequests(TAG);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_BIND_COMPLETE) {
				setResult(resultCode);
				finish();
			} else if (requestCode == GALLERY_REQUEST_CODE || requestCode == CAMERA_REQUEST_CODE) {
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
						if (cardBackBitmap != null) {
							ivCardBack.setImageBitmap(null);
							cardBackBitmap.recycle();
							cardBackBitmap = null;
						}

						cardBackBitmap = resizeBitmap(bitmap, rW, rH);//Bitmap.createScaledBitmap(bitmap, rW, rH, false);//Utils.decodeSampledBitmapFromByteArray(byteArray, rW, rH);
//						cardBackBitmap = resizeBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
						ivCardBack.setImageBitmap(cardBackBitmap);
						tvCardBack.setVisibility(View.GONE);
					} else {
						if (cardFrontBitmap != null) {
							ivCardFront.setImageBitmap(null);
							cardFrontBitmap.recycle();
							cardFrontBitmap = null;
						}

						cardFrontBitmap = resizeBitmap(bitmap, rW, rH);//Bitmap.createScaledBitmap(bitmap, rW, rH, false);//Utils.decodeSampledBitmapFromByteArray(byteArray, rW, rH);
//						cardFrontBitmap = resizeBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
						ivCardFront.setImageBitmap(cardFrontBitmap);
						tvCardFront.setVisibility(View.GONE);
					}

					if (cardFrontBitmap != null) {
						updateCardInfo();
					}
					bitmap.recycle();
				}
			}
		}
	}

	private void initCardInfo() {
		cardFrontBitmap.recycle();
		cardFrontBitmap = null;
//		cardBackBitmap.recycle();
//		cardBackBitmap = null;

		if (!itemWatchInfo.idCardFront.isEmpty()) {
			Picasso.get().load(itemWatchInfo.idCardFront).placeholder(R.drawable.img_contact).into(ivCardFront);
			tvCardFront.setVisibility(View.GONE);
		}
		else {
			ivCardFront.setImageResource(R.drawable.img_contact);
			tvCardFront.setVisibility(View.VISIBLE);
		}
//		if (!itemWatchInfo.idCardBack.isEmpty()) {
//			Picasso.get().load(itemWatchInfo.idCardBack).placeholder(R.drawable.img_contact).into(ivCardBack);
//			tvCardBack.setVisibility(View.GONE);
//		}
//		else {
//			ivCardBack.setImageResource(R.drawable.img_contact);
//			tvCardBack.setVisibility(View.VISIBLE);
//		}
	}

	private void updateCardInfo () {
		m_dlgProgress.show();
		HttpAPI.getIdCardFrontInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(),
			Util.getEncoded64ImageStringFromBitmap(cardFrontBitmap), new VolleyCallback() {
				@Override
				public void onSuccess(String result) {
					m_dlgProgress.dismiss();
					try {
						JSONObject jsonObject = new JSONObject(result);
						int iRetCode = jsonObject.getInt("retcode");
						if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
							String sMsg = jsonObject.getString("msg");
							Util.ShowDialogError(sMsg);
							initCardInfo();
							return;
						}

						JSONObject dataObject = jsonObject.getJSONObject("data");

//						llCardInfo.setVisibility(View.VISIBLE);

						tvCardNum.setText(dataObject.optString("id_card_num"));
						tvCardAddr.setText(dataObject.optString("address"));
					}
					catch (Exception e) {
						m_dlgProgress.dismiss();
						initCardInfo();
						Util.ShowDialogError(R.string.str_page_loading_failed);
					}
				}

				@Override
				public void onError(Object error) {
					m_dlgProgress.dismiss();
					initCardInfo();
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
}
