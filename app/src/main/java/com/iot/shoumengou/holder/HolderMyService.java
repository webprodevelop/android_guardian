//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderMyService {
	public TextView		tvServiceName, tvServiceTerm, tvServiceDate;

	public HolderMyService(View layout) {
		tvServiceName = layout.findViewById(R.id.ID_TEXT_SERVICE_NAME);
		tvServiceTerm = layout.findViewById(R.id.ID_TEXT_SERVICE_TERM);
		tvServiceDate = layout.findViewById(R.id.ID_TEXT_DATE);
	}
}
