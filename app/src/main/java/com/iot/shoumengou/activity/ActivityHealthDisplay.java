//@formatter:off
package com.iot.shoumengou.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterHeartRate;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.model.ItemHeartRate;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import java.util.ArrayList;

public class ActivityHealthDisplay extends ActivityBase implements View.OnClickListener {
	private final String TAG = "ActivityHealthDisplay";

	private ImageView						mBackImg;
	private TextView						mAbnormalRecord;
	private TextView						mRestRecord;
	private TextView						mAdviceContent;
	private TextView						mCallPhone;
	private ListView						mHeartRateList;
	private AdapterHeartRate				mHeartRateAdapter;
	private ArrayList<ItemHeartRate>		mHeartRateArray = new ArrayList<>();
	private ArrayList<ItemHeartRate>		mHeartRateHistory = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_health_display);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		initControls();
		setEventListener();

		loadHeartRateHistory();
	}

	private void loadHeartRateHistory() {
		if (Prefs.Instance().getMoniteringWatchSerial().isEmpty()) {
			return;
		}
		IOTDBHelper iotdbHelper = new IOTDBHelper(this);
		ItemWatchInfo itemWatchInfo = Util.findWatchEntry(Prefs.Instance().getMoniteringWatchSerial());
		if (itemWatchInfo != null) {
			//mHeartRateHistory = iotdbHelper.getHeartRateEntries(itemWatchInfo.id);
		}

		onAbnormalRecord();
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		mAbnormalRecord = findViewById(R.id.ID_TXTVIEW_ABNORMAL_RECORD);
		mRestRecord = findViewById(R.id.ID_TXTVIEW_REST_RECORD);
		mAdviceContent = findViewById(R.id.ID_TXTVIEW_ADVICE_CONTENT);
		mCallPhone = findViewById(R.id.ID_TXTVIEW_CALL_PHONE);
		mHeartRateList = findViewById(R.id.ID_LSTVIEW_HEART_RATE);

		mHeartRateAdapter = new AdapterHeartRate(this, mHeartRateArray);
		mHeartRateList.setAdapter(mHeartRateAdapter);

		String strAdviceContent = getResources().getString(R.string.str_health_suggest_text);
		String strCallNumber = getResources().getString(R.string.str_call_number);
		int indexCallNumber = strAdviceContent.indexOf(strCallNumber);
		SpannableString adviceText = new SpannableString(strAdviceContent);
		ClickableSpan clickableCallNumber = new ClickableSpan() {
			@Override
			public void onClick(View textView) {
				Toast.makeText(ActivityHealthDisplay.this, "Click", Toast.LENGTH_LONG).show();
			}
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
			}
		};
		adviceText.setSpan(clickableCallNumber, indexCallNumber, indexCallNumber + strCallNumber.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		mAdviceContent.setText(adviceText);
		mAdviceContent.setMovementMethod(LinkMovementMethod.getInstance());
		mAdviceContent.setHighlightColor(Color.TRANSPARENT);
	}

	@Override
	protected void setEventListener() {
		mBackImg.setOnClickListener(this);
		mAbnormalRecord.setOnClickListener(this);
		mRestRecord.setOnClickListener(this);
		mCallPhone.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_TXTVIEW_ABNORMAL_RECORD:
				onAbnormalRecord();
				break;
			case R.id.ID_TXTVIEW_REST_RECORD:
				onRestRecord();
				break;
			case R.id.ID_TXTVIEW_CALL_PHONE:
				onCallPhone();
				break;
		}
	}

	private void onCallPhone() {

	}

	private void onAbnormalRecord() {
		loadAbnormalRecord();

		SpannableString content = new SpannableString(getString(R.string.str_abnormal_record));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		mAbnormalRecord.setText(content);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			mAbnormalRecord.setTextColor(getColor(android.R.color.holo_blue_dark));
		}

		mRestRecord.setText(R.string.str_rest_record);
		mRestRecord.setTextColor(Color.BLACK);
	}

	private void loadAbnormalRecord() {
		mHeartRateArray.clear();

		ItemWatchInfo itemWatchInfo = Util.monitoringWatch;
		if (itemWatchInfo == null) {
			return;
		}

		for (ItemHeartRate itemHeartRate : mHeartRateHistory) {
			if (itemHeartRate.heartRate < itemWatchInfo.heart_rate_low_limit || itemHeartRate.heartRate > itemWatchInfo.heart_rate_high_limit) {
				mHeartRateArray.add(itemHeartRate);
			}
		}

		mHeartRateAdapter.notifyDataSetChanged();
	}

	private void onRestRecord() {
		loadRestRecord();

		SpannableString content = new SpannableString(getString(R.string.str_rest_record));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		mRestRecord.setText(content);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			mRestRecord.setTextColor(getColor(android.R.color.holo_blue_dark));
		}

		mAbnormalRecord.setText(R.string.str_abnormal_record);
		mAbnormalRecord.setTextColor(Color.BLACK);
	}

	private void loadRestRecord() {
		mHeartRateArray.clear();

		for (ItemHeartRate itemHeartRate : mHeartRateHistory) {
			mHeartRateArray.add(itemHeartRate);
		}

		mHeartRateAdapter.notifyDataSetChanged();
	}
}
