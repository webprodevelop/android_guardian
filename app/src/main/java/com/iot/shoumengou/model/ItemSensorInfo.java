//@formatter:off
package com.iot.shoumengou.model;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

public class ItemSensorInfo extends ItemDeviceInfo implements Serializable {

	public final static String	TYPE_STR_FIRE_SENSOR	= "YG";
	public final static String	TYPE_STR_SMOKE_SENSOR	= "QG";

	public String		sensorType = "";
	public String		contactName = "";
	public String		contactPhone = "";
	public String		locationLabel = "";
	public String 		labelRelative = "";
	public String		lat = "";
	public String		lon = "";
	public String		province = "";
	public String		city = "";
	public String		district = "";
	public String 		residence = "";
	public String		address = "";
	public boolean		batteryStatus;
	public boolean		alarmStatus;

	public ItemSensorInfo() {

	}

//	public ItemSensorInfo(String type, int id, String serial, boolean isManager, String serviceStartDate, String serviceEndDate, boolean netStatus, String sensorType, String contactName, String contactPhone,
//						  String locationLabel, String labelRelative, String lat, String lon, String province, String city, String district, String residence, String address, boolean batteryStatus, boolean alarmStatus) {
//		this.type = type;
//		this.id = id;
//		this.serial = serial;
//		this.isManager = isManager;
//		this.serviceStartDate = serviceStartDate;
//		this.serviceEndDate = serviceEndDate;
//		this.netStatus = netStatus;
//		this.sensorType = sensorType;
//		this.contactName = contactName;
//		this.contactPhone = contactPhone;
//		this.locationLabel = locationLabel;
//		this.labelRelative = labelRelative;
//		this.lat = lat;
//		this.lon = lon;
//		this.province = province;
//		this.city = city;
//		this.district = district;
//		this.residence = residence;
//		this.address = address;
//		this.batteryStatus = batteryStatus;
//		this.alarmStatus = alarmStatus;
//	}

	public ItemSensorInfo(JSONObject dataObject) {
		try {
		this.isManager = dataObject.optBoolean("is_manager");
		this.id = dataObject.optInt("id");
		this.type = dataObject.optString("type");
		this.sensorType = this.type;
		this.serial = dataObject.optString("device_serial");
		this.netStatus = dataObject.optBoolean("net_status");
		this.locationLabel = dataObject.optString("label");
		this.labelRelative = dataObject.optString("label_relative");
		this.contactName = dataObject.optString("contact_name");
		this.contactPhone = dataObject.optString("contact_phone");
		this.lat = dataObject.optString("lat");
		this.lon = dataObject.optString("lon");
		this.residence = dataObject.optString("residence");
		this.address = dataObject.optString("address");
		this.serviceStartDate = dataObject.optString("service_start");
		this.serviceEndDate = dataObject.optString("service_end");
		this.batteryStatus = dataObject.optBoolean("battery_status");
		this.alarmStatus = dataObject.optBoolean("alarm_status");
		this.province = dataObject.optString("province");
		this.city = dataObject.optString("city");
		this.district = dataObject.optString("district");
		} catch (Exception ex) {
			Log.d("scott-test-ItemSensorInfo", ex.getMessage());
		}
	}
}
