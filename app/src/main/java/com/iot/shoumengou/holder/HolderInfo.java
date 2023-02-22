//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderInfo {
	public ImageView	ivInfoImage;
	public TextView		tvInfoTitle;

	public HolderInfo(View layout) {
		ivInfoImage = layout.findViewById(R.id.ID_IMG_INFO);
		tvInfoTitle = layout.findViewById(R.id.ID_TEXT_INFO);
	}
}
