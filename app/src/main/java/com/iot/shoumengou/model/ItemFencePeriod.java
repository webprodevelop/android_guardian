//@formatter:off
package com.iot.shoumengou.model;

import android.content.Context;

import com.iot.shoumengou.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemFencePeriod implements Serializable {
	public int			startTime;
	public int			endTime;

	public ItemFencePeriod() {
		this.startTime = -1;
		this.endTime = -1;
	}

	public ItemFencePeriod(String strStartTime, String strEndTime) {
		if (strStartTime == null || strEndTime == null) {
			return;
		}

		String[] startTimes = strStartTime.split(":");
		this.startTime = Integer.parseInt(startTimes[0]) * 60 + Integer.parseInt(startTimes[1]);
		String[] endTimes = strEndTime.split(":");
		this.endTime = Integer.parseInt(endTimes[0]) * 60 + Integer.parseInt(endTimes[1]);
	}

	public String getStartTimeText(Context context) {
		if (startTime == -1) {
			return "<u>" + context.getResources().getString(R.string.str_select_start_time) + "</u>";
		}
		String strStartTime = String.format("%02d:%02d", startTime / 60, startTime % 60);

		return "<u>" + strStartTime + "</u>";
	}

	public String getEndTimeText(Context context) {
		if (endTime == -1) {
			return "<u>" +context.getResources().getString(R.string.str_select_end_time) + "</u>";
		}
		String strEndTime = String.format("%02d:%02d", endTime / 60, endTime % 60);

		return "<u>" +strEndTime + "</u>";
	}

	public String getPeriodString() {
		if (startTime == -1 || endTime == -1) {
			return null;
		}
		String strStartTime = String.format("%02d:%02d", startTime / 60, startTime % 60);
		String strEndTime = String.format("%02d:%02d", endTime / 60, endTime % 60);

		return strStartTime + "-" + strEndTime;
	}
}
