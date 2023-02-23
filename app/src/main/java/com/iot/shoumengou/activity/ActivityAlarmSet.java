//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterAlarm;
import com.iot.shoumengou.model.ItemAlarm;

public class ActivityAlarmSet extends ActivityBase implements OnClickListener {
	private final String TAG = "ActivityAlarmSet";

	public final static int NEW_ALARM_ACTIVITY		= 0;
	public final static int EDIT_ALARM_ACTIVITY		= 1;

	private ImageView				ivBack;
	private ImageView				ivAdd;
	private TextView				tvNoAlarm;
	private ListView				alarmListView;
	private AdapterAlarm			alarmAdapter;

	private ItemAlarm				curAlarm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_set);

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
		ivAdd = findViewById(R.id.ID_IMGVIEW_ADD_ALARM);
		tvNoAlarm = findViewById(R.id.ID_TXTVIEW_NO_ALARMS);
		alarmListView = findViewById(R.id.ID_LSTVIEW_ALARM);

		alarmAdapter = new AdapterAlarm(this);
		alarmListView.setAdapter(alarmAdapter);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		ivAdd.setOnClickListener(this);
	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_IMGVIEW_ADD_ALARM:
				onAddAlarm();
				break;
		}
	}

	private void onAddAlarm() {
		Intent intent = new Intent(this, ActivityAlarmAddEdit.class);

		curAlarm = new ItemAlarm();
		curAlarm.toIntent(intent);

		startActivityForResult(intent, NEW_ALARM_ACTIVITY);
	}

	public void onEditAlarm(final ItemAlarm alarm) {
		Intent intent = new Intent(this, ActivityAlarmAddEdit.class);

		curAlarm = alarm;
		curAlarm.toIntent(intent);
		startActivityForResult(intent, ActivityAlarmSet.EDIT_ALARM_ACTIVITY);
	}

	public void onDeleteAlarm(final int index) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View confirmView = layoutInflater.inflate(R.layout.alert_delete_alarm, null);

		final AlertDialog confirmDlg = new AlertDialog.Builder(this).create();

		TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
		TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				confirmDlg.dismiss();
			}
		});

		btnConfirm.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				alarmAdapter.delete(index);

				confirmDlg.dismiss();
			}
		});

		confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		confirmDlg.setView(confirmView);
		confirmDlg.show();
	}

	public void updateNoAlarms(boolean show) {
		if (show) {
			tvNoAlarm.setVisibility(View.VISIBLE);
		} else {
			tvNoAlarm.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NEW_ALARM_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				curAlarm.fromIntent(data);
				alarmAdapter.add(curAlarm);
			}
			curAlarm = null;
		}
		else if (requestCode == EDIT_ALARM_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				curAlarm.fromIntent(data);
				alarmAdapter.update(curAlarm);
			}
			curAlarm = null;
		}
	}
}
