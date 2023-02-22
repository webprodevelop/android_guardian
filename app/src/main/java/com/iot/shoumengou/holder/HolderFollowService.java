//@formatter:off
package com.iot.shoumengou.holder;

import android.view.View;
import android.widget.TextView;

import com.iot.shoumengou.R;

public class HolderFollowService {
	public TextView		tvServiceName,
			tvServiceTerm1, tvServicePrice1,
			tvServiceTerm2, tvServicePrice2,
			tvServiceTerm3, tvServicePrice3,
			tvServiceTerm4, tvServicePrice4,
			tvServiceTerm5, tvServicePrice5;

	public HolderFollowService(View layout) {
		tvServiceName = layout.findViewById(R.id.ID_TEXT_SERVICE_NAME);
		tvServiceTerm1 = layout.findViewById(R.id.ID_TEXT_TERM_1);
		tvServicePrice1 = layout.findViewById(R.id.ID_TEXT_PRICE_1);
		tvServiceTerm2 = layout.findViewById(R.id.ID_TEXT_TERM_2);
		tvServicePrice2 = layout.findViewById(R.id.ID_TEXT_PRICE_2);
		tvServiceTerm3 = layout.findViewById(R.id.ID_TEXT_TERM_3);
		tvServicePrice3 = layout.findViewById(R.id.ID_TEXT_PRICE_3);
		tvServiceTerm4 = layout.findViewById(R.id.ID_TEXT_TERM_4);
		tvServicePrice4 = layout.findViewById(R.id.ID_TEXT_PRICE_4);
		tvServiceTerm5 = layout.findViewById(R.id.ID_TEXT_TERM_5);
		tvServicePrice5 = layout.findViewById(R.id.ID_TEXT_PRICE_5);
	}
}
