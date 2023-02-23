//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.model.ItemDeviceInfo;

public class ActivityBindComplete extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityBindComplete";

	private ImageView	ivBack;
	private TextView	tvTitle;
	private TextView	tvBindDesc;
	private TextView	tvConfirm;

	private ItemDeviceInfo itemDeviceInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_complete);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		itemDeviceInfo = (ItemDeviceInfo) getIntent().getSerializableExtra("device_data");

		if (itemDeviceInfo == null) {
			finish();
		}

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		tvTitle = findViewById(R.id.ID_TXTVIEW_TITLE);
		tvBindDesc = findViewById(R.id.ID_TXTVIEW_BIND_DESC);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);

		tvBindDesc.setText(getString(R.string.str_bind_service_term) + String.format(getString(R.string.str_term), itemDeviceInfo.serviceStartDate, itemDeviceInfo.serviceEndDate));
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
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
		setResult(RESULT_OK);
		finish();
	}
}
