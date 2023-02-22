//@formatter:off
package com.iot.shoumengou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterBindWatch;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.model.ItemDeviceInfo;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;
import com.iot.shoumengou.view.ExpandableHeightListView;

import java.util.ArrayList;

public class ActivityBindWatch extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityBindWatch";

	public final static int					REQUEST_SCAN_SMART_WATCH				= 2;

	private ImageView						mBackImg;
	private ExpandableHeightListView		mBindWatchList;
	private TextView						mBindWatch;
	private AdapterBindWatch				mBindWatchAdapter;
	private ArrayList<ItemWatchInfo>		mBindWatchArray = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_watch);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		loadBindWatchList();
		initControls();
		setEventListener();
	}

	private void loadBindWatchList() {
		mBindWatchArray = Util.getAllWatchEntries();
	}

	@Override
	protected void initControls() {
		super.initControls();

		mBackImg = findViewById(R.id.ID_IMGVIEW_BACK);
		mBindWatchList = findViewById(R.id.ID_LSTVIEW_WATCH);
		mBindWatch = findViewById(R.id.ID_BTN_BIND_DEVICE);

		mBindWatchList.setExpanded(true);
		mBindWatchAdapter = new AdapterBindWatch(this, mBindWatchArray);
		mBindWatchList.setAdapter(mBindWatchAdapter);
	}

	@Override
	protected void setEventListener() {
		mBackImg.setOnClickListener(this);
		mBindWatch.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_BTN_BIND_DEVICE:
				onBindWatch();
				break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_SCAN_SMART_WATCH) {
			if (resultCode == Activity.RESULT_OK) {
				loadBindWatchList();
				mBindWatchAdapter.notifyDataSetChanged();
			}
		}
	}

	private void onBindWatch() {
		Intent intent = new Intent(this, ActivityScan.class);
		intent.putExtra("device_type", "");
		startActivityForResult(intent, REQUEST_SCAN_SMART_WATCH);
	}
}
