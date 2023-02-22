//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderRescue {

	public TextView		tvSurveyName;
	public TextView		tvSurveyTime;

	public HolderRescue(View root) {
		tvSurveyName = root.findViewById(R.id.ID_TXTVIEW_SURVEY_NAME);
		tvSurveyTime = root.findViewById(R.id.ID_TXTVIEW_SURVEY_TIME);
	}
}
