//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderWardInfo {
	public TextView		tvName;
	public TextView		tvStatus;

	public HolderWardInfo(View layout) {
		tvName = layout.findViewById(R.id.ID_TEXT_NAME);
		tvStatus = layout.findViewById(R.id.ID_TEXT_STATUS);
	}
}
