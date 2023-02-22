package com.iot.shoumengou.util;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

public class CustomLinearLayoutManager extends LinearLayoutManager {
	public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
	}

	@Override
	protected int getExtraLayoutSpace(RecyclerView.State state) {
//		return super.getExtraLayoutSpace(state);
		return 1000;
	}

	@Override
	public View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
		// Need to be called in order to layout new row/column
		View nextFocus = super.onFocusSearchFailed(focused, focusDirection, recycler, state);

		if (nextFocus == null) {
			return null;
		}

		int nextPos = getPosition(focused);

		return findViewByPosition(nextPos);
	}
}


