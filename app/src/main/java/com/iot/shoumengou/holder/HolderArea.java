//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderArea {

	public TextView		tvName;

	public HolderArea(View root) {
		tvName = root.findViewById(R.id.ID_TXTVIEW_NAME);
	}
}
