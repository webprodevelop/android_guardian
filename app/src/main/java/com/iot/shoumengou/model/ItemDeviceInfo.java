//@formatter:off
package com.iot.shoumengou.model;

import com.iot.shoumengou.util.Util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class ItemDeviceInfo implements Serializable {
	//public final static int		TYPE_SMART_WATCH =	0;
	//public final static int		TYPE_SMOKE_SENSOR =	1;
	//public final static int		TYPE_FIRE_SENSOR =	2;

	public String		type;
	public int  		id;
	public String		serial = "";
	public boolean		isManager;
	public String		serviceStartDate = "";
	public String		serviceEndDate = "";
	public boolean		netStatus;
	public boolean		isDetail = false;

	public boolean isExpireDateMonthBefore() {
		if (serviceEndDate == null || serviceEndDate.isEmpty()) {
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		long curTime = calendar.getTimeInMillis();
		long serviceEndTime = Util.parseDateFormatString(serviceEndDate);
		calendar.setTimeInMillis(serviceEndTime);
		calendar.add(Calendar.MONTH, -1);
		long expiredTime = calendar.getTimeInMillis();

		return (expiredTime < curTime);
	}

	public boolean isExpiredService() {
		if (serviceEndDate == null || serviceEndDate.isEmpty()) {
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		long curTime = calendar.getTimeInMillis();
		long serviceEndTime = Util.parseDateFormatString(serviceEndDate);

		return (curTime > serviceEndTime);
	}
}
