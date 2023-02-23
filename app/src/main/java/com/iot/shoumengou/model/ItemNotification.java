//@formatter:off
package com.iot.shoumengou.model;

import java.io.Serializable;

public class ItemNotification implements Serializable {
	public static String NOTICE_TYPE_LOW_BATTERY     = "low_battery";
	public static String NOTICE_TYPE_FULL_BATTERY    = "full_battery";
	public static String NOTICE_TYPE_DISCONNECT      = "disconnect";
	public static String NOTICE_TYPE_CONNECT         = "connect";
	public static String NOTICE_TYPE_HEALTH_ABNORMAL = "health_abnormal";
	public static String NOTICE_TYPE_UPDATE_HEALTH_DATA = "update_health_data";
	public static String NOTICE_TYPE_UPDATE_WATCH_POS = "update_watch_pos";
	public static String NOTICE_TYPE_TAKE_OFF = "take_off";
	public static String NOTICE_TYPE_TAKE_ON = "take_on";
	public static String NOTICE_TYPE_BIRTHDAY = "birthday_msg";
	public static String NOTICE_TYPE_HEALTH = "health_msg";
	public static String NOTICE_TYPE_ALARM_OFF = "alarm_off";
	public static String NOTICE_TYPE_UPDATE_ALARM_INFO = "update_alarm_info";

	public static String PUSH_TYPE_NOTICE = "notice";
	public static String PUSH_TYPE_ALARM = "alarm";
	public static String PUSH_TYPE_CLOCK = "clock";
	public static String PUSH_TYPE_CHART = "chat";

	public int 			id;
	public long			time;
	public String		type;
	public String 		noticeType;
	public String 		deviceType;
	public String		deviceSerial;
	public int 			alarmId;
	public int			isRead = 0;
	public String		title;
	public String 		alert;
	public int 			alarmStatus = 0;

	public ItemNotification() {

	}

	public ItemNotification(long id,
							String type,
							String noticeType,
							String deviceType,
							String deviceSerial,
							int alarmId,
							long time,
							String title,
							String alert,
							int alarmStatus) {
		this.id = (int)id;
		this.time = time;
		this.type = type;
		this.noticeType = noticeType;
		this.deviceType = deviceType;
		this.deviceSerial = deviceSerial;
		this.alarmId = alarmId;
		this.isRead = 0;
		this.title = title;
		this.alert = alert;
		this.alarmStatus = alarmStatus;
	}
}
