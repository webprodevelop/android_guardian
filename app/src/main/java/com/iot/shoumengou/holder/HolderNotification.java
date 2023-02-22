//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderNotification {
	public TextView		tvTitle;
	public TextView		tvStatus;
	public TextView		tvContent;
	public TextView		tvTime;
	public TextView		tvView;
	public ImageView	ivIcon;

	public HolderNotification(View root) {
		tvTitle = root.findViewById(R.id.ID_TXTVIEW_TITLE);
		tvStatus = root.findViewById(R.id.ID_TXTVIEW_STATS);
		tvContent = root.findViewById(R.id.ID_TXTVIEW_CONTENT);
		tvTime = root.findViewById(R.id.ID_TXTVIEW_TIME);
		tvView = root.findViewById(R.id.ID_BTN_VIEW);
		ivIcon = root.findViewById(R.id.ID_IMGVIEW_ICON);
	}
}
