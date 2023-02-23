package com.iot.shoumengou.view;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.View;

import java.util.Objects;

public class CustomRecyclerView extends RecyclerView {

	Context context;

	public CustomRecyclerView(Context context) {
		super(context);
		this.context = context;
	}

	public CustomRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean fling(int velocityX, int velocityY) {

		velocityY *= 0.5;
		velocityX *= 0.5;
		// velocityX *= 0.7; for Horizontal recycler view. comment velocityY line not require for Horizontal Mode.

		return super.fling(velocityX, velocityY);
	}

	@Override
	public View focusSearch(View focused, int direction) {

		// only perform position checking for horizental linearlayout, you must implement for other cases.
		if (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
			LayoutManager layoutManager = getLayoutManager();
			if (layoutManager instanceof LinearLayoutManager && ((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL) {
				int nextPos = getNextPos(focused, direction);
				if (nextPos == -1) {
					int next_focus_id;
					if(direction == View.FOCUS_LEFT) {
						next_focus_id = getNextFocusLeftId();
					}
					else {
						next_focus_id = getNextFocusRightId();
					}
					if(next_focus_id != NO_ID) {
						return ((Activity)getContext()).findViewById(next_focus_id);
					}
					return null;
				}
			}
		}

		return super.focusSearch(focused, direction);
	}

	public int getNextPos(View focused, int direction) {

		int cur_pos = getChildLayoutPosition(focused), nextPos = -1, dx = 0;
		int child_count = Objects.requireNonNull(getAdapter()).getItemCount(), left_end_pos = 0, right_end_pos = 0;

		boolean isReverseLayout, isLayoutRTL;

		LayoutManager layoutManager = getLayoutManager();
		if (layoutManager instanceof LinearLayoutManager) {
			LinearLayoutManager linearlayoutManager = (LinearLayoutManager) layoutManager;

			isReverseLayout = linearlayoutManager.getReverseLayout();
			isLayoutRTL = (linearlayoutManager.getLayoutDirection() == LAYOUT_DIRECTION_RTL);

			if (!isLayoutRTL && !isReverseLayout || isLayoutRTL && isReverseLayout) {
				dx = 1;
				left_end_pos = 0;
				right_end_pos = child_count - 1;
			} else {
				dx = -1;
				left_end_pos = child_count - 1;
				right_end_pos = 0;
			}

			if (direction == View.FOCUS_LEFT) {
				nextPos = cur_pos - dx;
			} else if (direction == View.FOCUS_RIGHT) {
				nextPos = cur_pos + dx;
			}
		}

		if (dx > 0) {
			if (nextPos < left_end_pos || right_end_pos < nextPos) {
				nextPos = -1;
			}
		} else {
			if (nextPos > left_end_pos || right_end_pos > nextPos) {
				nextPos = -1;
			}
		}

		return nextPos;
	}
}