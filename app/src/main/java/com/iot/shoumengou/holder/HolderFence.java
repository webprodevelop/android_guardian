//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderFence {

	public TextView		tvFenceName;
	public TextView		tvFenceAddress;
	public TextView		tvDelete;
	public TextView		tvUpdate;

	public HolderFence(View layout) {
		tvFenceName = layout.findViewById(R.id.ID_TXTVIEW_FENCE_NAME);
		tvFenceAddress = layout.findViewById(R.id.ID_TXTVIEW_FENCE_ADDRESS);
		tvDelete = layout.findViewById(R.id.ID_BTN_DELETE);
		tvUpdate = layout.findViewById(R.id.ID_BTN_UPDATE);
	}
}
