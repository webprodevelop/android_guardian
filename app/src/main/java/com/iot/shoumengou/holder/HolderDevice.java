//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderDevice {

	public ImageView	ivDelete;
	public ImageView	ivSetting;
	public ImageView	ivType;
	public TextView		tvName;
	public TextView		tvSerial;
	public TextView		tvPhoneNumber;
	public LinearLayout	llNetState;
	public LinearLayout	llCharging;
//	public LinearLayout	llWorkState;
//	public LinearLayout	llAlarmState;
	public LinearLayout	llLabel;
	public LinearLayout	llLabelRelative;
	public LinearLayout	llPhoneNum;
	public TextView		tvNetState;
	public TextView		tvCharging;
//	public TextView		tvWorkState;
//	public TextView		tvAlarmState;
	public TextView		tvServiceTerm;
	public TextView		tvGoto;
	public TextView		tvLabel;
	public TextView		tvLabelRelative;

	public TextView		tvStatus;
	public TextView		tvRelativeLabel;
	public TextView		tvRelative;
	public ImageView	ivUp;
	public ImageView	ivDown;
	public LinearLayout	llShort;
	public LinearLayout	llDetail;

	public HolderDevice(View layout) {
		tvName = layout.findViewById(R.id.ID_TXTVIEW_NAME);
		ivDelete = layout.findViewById(R.id.ID_IMGVIEW_DELETE);
		ivSetting = layout.findViewById(R.id.ID_IMGVIEW_SETTING);
		ivType = layout.findViewById(R.id.ID_IMGVIEW_TYPE);
		tvSerial = layout.findViewById(R.id.ID_TXTVIEW_SERIAL);
		tvPhoneNumber = layout.findViewById(R.id.ID_TXTVIEW_PHONE_NUMBER);
		llNetState = layout.findViewById(R.id.ID_LYT_NETWORK_STATE);
		llCharging = layout.findViewById(R.id.ID_LYT_CHARGING);
//		llWorkState = layout.findViewById(R.id.ID_LYT_WORK_STATE);
//		llAlarmState = layout.findViewById(R.id.ID_LYT_ALARM_STATE);
		llLabel = layout.findViewById(R.id.ID_LYT_LABEL);
		llLabelRelative = layout.findViewById(R.id.ID_LYT_LABEL_RELATIVE);
		llPhoneNum = layout.findViewById(R.id.ID_LYT_PHONE_NUMBER);
		tvNetState = layout.findViewById(R.id.ID_TXTVIEW_NETWORK_STATE);
		tvCharging = layout.findViewById(R.id.ID_TXTVIEW_CHARGING);
//		tvWorkState = layout.findViewById(R.id.ID_TXTVIEW_WORK_STATE);
//		tvAlarmState = layout.findViewById(R.id.ID_TXTVIEW_ALARM_STATE);
		tvLabel = layout.findViewById(R.id.ID_TXTVIEW_LABEL);
		tvLabelRelative = layout.findViewById(R.id.ID_TXTVIEW_LABEL_RELATIVE);
		tvServiceTerm = layout.findViewById(R.id.ID_TXTVIEW_SERVICE_TERM);
		tvGoto = layout.findViewById(R.id.ID_TXTVIEW_GOTO);

		tvStatus = layout.findViewById(R.id.ID_TXTVIEW_STATUS);
		tvRelativeLabel = layout.findViewById(R.id.ID_TXTVIEW_RELATIVE_L);
		tvRelative = layout.findViewById(R.id.ID_TXTVIEW_RELATIVE);
		ivUp = layout.findViewById(R.id.ID_IMGVIEW_UP);
		ivDown = layout.findViewById(R.id.ID_IMGVIEW_DOWN);
		llShort = layout.findViewById(R.id.ID_CONTAINTER_SHORT);
		llDetail = layout.findViewById(R.id.ID_CONTAINTER_DETAIL);
	}
}
