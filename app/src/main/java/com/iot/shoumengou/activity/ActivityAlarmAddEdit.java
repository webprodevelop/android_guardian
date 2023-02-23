//@formatter:off
package com.iot.shoumengou.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.iot.shoumengou.R;
import com.iot.shoumengou.model.ItemAlarm;
import com.iot.shoumengou.util.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ActivityAlarmAddEdit extends ActivityBase implements OnClickListener, CompoundButton.OnCheckedChangeListener {
	private final String TAG = "ActivityAlarmAddEdit";

	private ImageView				ivBack;
	private EditText				edtAlarmTitle;
	private TextView				tvAlarmTime;
	private EditText				edtAlarmContent;
	private TextView				tvRepeat;
	private TextView				tvConfirm;
	private Switch[]				weekDays = new Switch[7];

	private ItemAlarm				curAlarm;
	private DateTime				dateTime;
	private GregorianCalendar		calendar;
	private int						year;
	private int 					month;
	private int 					day;
	private int 					hour;
	private int						minute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_add_edit);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		curAlarm = new ItemAlarm();
		curAlarm.fromIntent(getIntent());

		dateTime = new DateTime(this);

		calendar = new GregorianCalendar();
		calendar.setTimeInMillis(curAlarm.date);

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);

		initControls();
		setEventListener();
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
		edtAlarmTitle = findViewById(R.id.ID_EDTTEXT_ALARM_TITLE);
		tvAlarmTime = findViewById(R.id.ID_TXTVIEW_ALARM_TIME);
		edtAlarmContent = findViewById(R.id.ID_EDTTEXT_ALARM_CONTENT);
		tvRepeat = findViewById(R.id.ID_TXTVIEW_REPEAT);
		weekDays[0] = findViewById(R.id.ID_SWITCH_MONDAY);
		weekDays[1] = findViewById(R.id.ID_SWITCH_TUESDAY);
		weekDays[2] = findViewById(R.id.ID_SWITCH_WEDNESDAY);
		weekDays[3] = findViewById(R.id.ID_SWITCH_THURSDAY);
		weekDays[4] = findViewById(R.id.ID_SWITCH_FRIDAY);
		weekDays[5] = findViewById(R.id.ID_SWITCH_SATURDAY);
		weekDays[6] = findViewById(R.id.ID_SWITCH_SUNDAY);
		tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);

		edtAlarmTitle.setText(curAlarm.title);
		tvAlarmTime.setText(dateTime.formatTime(curAlarm));
		edtAlarmContent.setText(curAlarm.content);
		tvRepeat.setText(dateTime.formatDays(this, curAlarm));

		updateDays();
	}

	private void updateDays() {
		final boolean[] days = dateTime.getDays(curAlarm);
		for (int i = 0; i < 7; i++) {
			weekDays[i].setChecked(days[i]);
		}
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
		tvAlarmTime.setOnClickListener(this);
		tvConfirm.setOnClickListener(this);
		for (Switch weekday : weekDays) {
			weekday.setOnCheckedChangeListener(this);
		}

		edtAlarmTitle.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				curAlarm.title = edtAlarmTitle.getText().toString();
			}
		});

		edtAlarmContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				curAlarm.content = edtAlarmContent.getText().toString();
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ID_IMGVIEW_BACK:
				finish();
				break;
			case R.id.ID_TXTVIEW_ALARM_TIME:
				onSelectTime();
				break;
			case R.id.ID_BTN_CONFIRM:
				onConfirm();
				break;
		}
	}

	private void onSelectTime() {
		TimePickerDialog mTimePicker;
		mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
				hour = selectedHour;
				minute = selectedMinute;

				calendar = new GregorianCalendar(year, month, day, hour, minute);
				curAlarm.date = calendar.getTimeInMillis();

				tvAlarmTime.setText(dateTime.formatTime(curAlarm));
			}
		}, hour, minute, true);//Yes 24 hour time
		mTimePicker.show();
	}

	private void onConfirm() {
		Intent intent = new Intent();

		curAlarm.toIntent(intent);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
		final boolean[] days = dateTime.getDays(curAlarm);
		switch (compoundButton.getId()) {
			case R.id.ID_SWITCH_MONDAY:
				days[0] = checked;
				break;
			case R.id.ID_SWITCH_TUESDAY:
				days[1] = checked;
				break;
			case R.id.ID_SWITCH_WEDNESDAY:
				days[2] = checked;
				break;
			case R.id.ID_SWITCH_THURSDAY:
				days[3] = checked;
				break;
			case R.id.ID_SWITCH_FRIDAY:
				days[4] = checked;
				break;
			case R.id.ID_SWITCH_SATURDAY:
				days[5] = checked;
				break;
			case R.id.ID_SWITCH_SUNDAY:
				days[6] = checked;
				break;
		}
		dateTime.setDays(curAlarm, days);
		tvRepeat.setText(dateTime.formatDays(this, curAlarm));
	}
}
