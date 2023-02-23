//@formatter:off
package com.iot.shoumengou.model;

import com.iot.shoumengou.util.Util;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ItemHeartRate {
	public int			watchId;
	public String		checkDate;
	public long			checkTime;
	public int			heartRate;
	public double 		temperature;
	public int 			highBloodPressure;
	public int 			lowBloodPressure;
	public String		deviceSerial;

	public ItemHeartRate() {

	}

	public ItemHeartRate(JSONObject dataObject) {
		checkTime = Util.parseDateTimeSecFormatString(dataObject.optString("create_time"));
		checkDate = Util.getDateFormatStringIgnoreLocale(new Date(checkTime));
		heartRate = Integer.parseInt(dataObject.optString("heart_rate"));
		watchId = dataObject.optInt("id");
		highBloodPressure = Integer.parseInt(dataObject.optString("high_blood_pressure"));
		lowBloodPressure = Integer.parseInt(dataObject.optString("low_blood_pressure"));
		temperature = Float.parseFloat(dataObject.optString("temperature"));
		deviceSerial = dataObject.optString("device_serial");
	}

	public float getHourPercent() {
		float hourPercent = 0;

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(checkTime);
		hourPercent = calendar.get(Calendar.HOUR_OF_DAY) + (calendar.get(Calendar.MINUTE) / 60.f);

		return hourPercent;
	}

	public String getDateTimeSecString() {
		return Util.getDateTimeSecFormatString(new Date(checkTime));
	}

}
