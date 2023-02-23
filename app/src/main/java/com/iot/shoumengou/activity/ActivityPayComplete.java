//@formatter:off
package com.iot.shoumengou.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class ActivityPayComplete extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityPayComplete";

	private ImageView	ivBack;
	private TextView	tvTitle;
	private TextView	tvConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_complete);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		tvTitle = findViewById(R.id.ID_TXTVIEW_TITLE);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
	}

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
		finish();
	}
}
