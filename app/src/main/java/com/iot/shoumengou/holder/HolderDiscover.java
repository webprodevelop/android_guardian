//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderDiscover {

	public TextView		tvTitle;
	public TextView		tvContent;
	public TextView		tvTime;
	public TextView		tvKind;
	public ImageView	ivPic;

	public HolderDiscover(View root) {
		tvTitle = root.findViewById(R.id.ID_TXTVIEW_TITLE);
		tvContent = root.findViewById(R.id.ID_TXTVIEW_CONTENT);
		tvTime = root.findViewById(R.id.ID_TXTVIEW_TIME);
		tvKind = root.findViewById(R.id.ID_TXTVIEW_KIND);
		ivPic = root.findViewById(R.id.ID_IMGVIEW_PIC);
	}
}
