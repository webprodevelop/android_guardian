//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderAlarm {

	public TextView		tvAlarmTime;
	public Switch		swOnOff;
	public TextView		tvAlarmName;
	public TextView		tvAlarmContent;
	public TextView		tvAlarmPeriod;
	public TextView		tvDelete;
	public TextView		tvUpdate;

	public HolderAlarm(View layout) {
		tvAlarmTime = layout.findViewById(R.id.ID_TXTVIEW_ALARM_TIME);
		swOnOff = layout.findViewById(R.id.ID_SWITCH_ONOFF);
		tvAlarmName = layout.findViewById(R.id.ID_TXTVIEW_ALARM_NAME);
		tvAlarmContent = layout.findViewById(R.id.ID_TXTVIEW_SUGGEST);
		tvAlarmPeriod = layout.findViewById(R.id.ID_TXTVIEW_ALARM_PERIOD);
		tvDelete = layout.findViewById(R.id.ID_BTN_DELETE);
		tvUpdate = layout.findViewById(R.id.ID_BTN_UPDATE);
	}
}
