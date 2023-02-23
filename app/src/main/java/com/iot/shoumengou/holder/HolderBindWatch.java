//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderBindWatch {

	public TextView		tvUser;
	public TextView		tvCotrol;

	public HolderBindWatch(View root) {
		tvUser = root.findViewById(R.id.ID_TXTVIEW_USER);
		tvCotrol = root.findViewById(R.id.ID_BTN_CONTROL);
	}
}
