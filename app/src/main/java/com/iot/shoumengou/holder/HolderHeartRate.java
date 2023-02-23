//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderHeartRate {

	public TextView		tvSurveyDate;
	public TextView		tvSurveyTime;
	public TextView		tvHeartRate;
	public View			llItemRoot;

	public HolderHeartRate(View root) {
		llItemRoot = root.findViewById(R.id.ID_LYT_ROOT);
		tvSurveyDate = root.findViewById(R.id.ID_TXTVIEW_SURVEY_DATE);
		tvSurveyTime = root.findViewById(R.id.ID_TXTVIEW_SURVEY_TIME);
		tvHeartRate = root.findViewById(R.id.ID_TXTVIEW_HEART_RATE);
	}
}
