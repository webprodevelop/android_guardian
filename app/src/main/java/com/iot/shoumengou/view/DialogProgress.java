//@formatter:off
package com.iot.shoumengou.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.iot.shoumengou.R;

public class DialogProgress extends Dialog {

	public DialogProgress(Context a_context) {
		super(a_context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_progress);
		getWindow().setBackgroundDrawable(
			new ColorDrawable(android.graphics.Color.TRANSPARENT)
		);
	}
}
