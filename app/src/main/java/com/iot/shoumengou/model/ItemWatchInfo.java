//@formatter:off
package com.iot.shoumengou.model;

import com.iot.shoumengou.util.Util;

import org.json.JSONObject;

import java.io.Serializable;

public class ItemWatchInfo extends ItemDeviceInfo implements Serializable {
	public String		name = "";
	public String		address = "";
	public String		ill_history = "";
	public String 		ill_history_other;
	public String		lon = "";
	public String 		family_ill_history;
	public String		family_ill_history_other;
	public int  		sex;
	public String 		residence = "";
	public int			chargeStatus;
	public String		phone = "";
	public String		birthday = "";
	public int 			userBirthdaySolar = 1;
	public String		idCardBack;
	public String		idCardFront;
	public String		idCardNum;
	public String		idCardAddr;
	public int			posUpdateMode;
	public String		userRelation;
	public String		lat = "";
	public String		province = "";
	public String		city = "";
	public String		district = "";
	public String		sosContactName1 = "";
	public String		sosContactPhone1 = "";
	public String		sosContactName2 = "";
	public String		sosContactPhone2 = "";
	public String		sosContactName3 = "";
	public String		sosContactPhone3 = "";
	public String 		socialProvince = "";
	public String 		socialCity = "";
	public String 		socialDisctrict = "";
	public String 		socialNum = "";
	public boolean		takeOnStatus = true;

	public int heart_rate_high_limit;
	public int heart_rate_low_limit;
	public int blood_pressure_high_left_limit;
	public int blood_pressure_high_right_limit;
	public int blood_pressure_low_left_limit;
	public int blood_pressure_low_right_limit;
	public float temperature_high_limit;
	public float temperature_low_limit;
	public int measure_period;

	public ItemWatchInfo() {

	}

	public ItemWatchInfo(JSONObject object) {
		netStatus = object.optBoolean("net_status");
		serviceEndDate = object.optString("service_end");
		name = object.optString("user_name");
		address = object.optString("address");
		ill_history_other = object.optString("user_ill_history_other");
		lon = object.optString("lon");
		family_ill_history = object.optString("user_family_ill_history");
		sex = object.optInt("user_sex");
		residence = object.optString("residence");
		chargeStatus = object.optInt("charge_status");
		phone = object.optString("user_phone");
		id = object.optInt("id");
		lat = object.optString("lat");
		serviceStartDate = object.optString("service_start");
		idCardBack = object.optString("user_id_card_back");
		family_ill_history_other = object.optString("user_family_ill_history_other");
		idCardNum = object.optString("user_id_card_num");
		idCardAddr = object.optString("user_id_card_address");
		socialProvince = object.optString("social_province");
		socialCity= object.optString("social_city");
		socialDisctrict = object.optString("social_district");
		socialNum = object.optString("social_num");
		isManager = object.optBoolean("is_manager");
		idCardFront = object.optString("user_id_card_front");
		posUpdateMode = object.optInt("pos_update_mode");
		birthday = object.optString("user_birthday");
		userBirthdaySolar = object.optInt("user_birthday_solar");
		ill_history = object.optString("user_ill_history");
		serial = object.optString("serial");
		userRelation = object.optString("user_relative");

		sosContactName1 = object.optString("sos_contact1_name");
		sosContactPhone1 = object.optString("sos_contact1_phone");
		sosContactName2 = object.optString("sos_contact2_name");
		sosContactPhone2 = object.optString("sos_contact2_phone");
		sosContactName3 = object.optString("sos_contact3_name");
		sosContactPhone3 = object.optString("sos_contact3_phone");
		province = object.optString("province");
		city = object.optString("city");
		district = object.optString("district");

		measure_period = Util.parseInt(object,"measure_period", 0);
		heart_rate_high_limit = Util.parseInt(object,"heart_rate_high_limit", 0);
		heart_rate_low_limit = Util.parseInt(object,"heart_rate_low_limit", 0);
		blood_pressure_high_left_limit = Util.parseInt(object,"blood_pressure_high_left_limit", 0);
		blood_pressure_high_right_limit = Util.parseInt(object,"blood_pressure_high_right_limit", 0);
		blood_pressure_low_left_limit = Util.parseInt(object,"blood_pressure_low_left_limit", 0);
		blood_pressure_low_right_limit = Util.parseInt(object,"blood_pressure_low_right_limit", 0);
		temperature_high_limit = Util.parseFloat(object, "temperature_high_limit", 0);
		temperature_low_limit = Util.parseFloat(object, "temperature_low_limit", 0);

		takeOnStatus = object.optBoolean("take_on_status");

		type = "";
	}
}
