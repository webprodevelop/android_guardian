//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderPeriod {

	public TextView		tvStartTime;
	public TextView		tvEndTime;

	public HolderPeriod(View layout) {
		tvStartTime = layout.findViewById(R.id.ID_TXTVIEW_START_TIME);
		tvEndTime = layout.findViewById(R.id.ID_TXTVIEW_END_TIME);
	}
}
