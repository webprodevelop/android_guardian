//@formatter:off
package com.iot.shoumengou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
	private static final String PREFS = "manage_sensor";

	private static final String PREF_USER_TOKEN			= "user_token";
	private static final String PREF_USER_NAME			= "user_name";
	private static final String PREF_USER_PHONE			= "user_phone";
	private static final String PREF_USER_PSWD			= "user_pswd";
	private static final String PREF_SAVE_PHONE			= "save_phone";
	private static final String PREF_SAVE_PSWD			= "save_pswd";
	private static final String PREF_MONITERING_WATCH	= "monitering_watch_id";
	private static final String PREF_SEX				= "user_sex";
	private static final String PREF_BIRTHDAY			= "user_birthday";
	private static final String PREF_WEIXIN_ID			= "user_weixin_id";
	private static final String PREF_QQ_ID				= "user_qq_id";
	private static final String PREF_EMAIL				= "user_email";
	private static final String PREF_PROVINCE			= "user_province";
	private static final String PREF_CITY				= "user_city";
	private static final String PREF_DISTRICT			= "user_district";
	private static final String PREF_USER_PHOTO			= "user_photo";
	private static final String PREF_CARD_PHOTO			= "card_photo";
	private static final String PREF_CONTENT			= "user_content";
	private static final String PREF_POLICY				= "user_policy";
	private static final String PREF_AGREEMENT			= "user_agreement";
	private static final String PREF_WATCH_FREE_DAYS	= "watch_free_days";
	private static final String PREF_YANGAN_FREE_DAYS	= "yangan_free_days";
	private static final String PREF_RANQI_FREE_DAYS	= "ranqi_free_days";
	private static final String PREF_WATCH_PAY			= "watch_pay";
	private static final String PREF_YANGAN_PAY			= "yangan_pay";
	private static final String PREF_RANQI_PAY			= "ranpi_pay";
	private static final String PREF_USER_BIRTH_DESC	= "user_birth_desc";
	private static final String PREF_SOS_STATUS			= "sos_status";
	private static final String PREF_FIRE_STATUS		= "fire_status";
	private static final String PREF_WATCH_NETSTATUS	= "watch_netstatus";
	private static final String PREF_FIRE_NETSTATUS		= "fire_netstatus";
	private static final String PREF_HEARTRATE_STATUS	= "heartrate_status";
	private static final String PREF_WATCHBATTERY_STATUS= "watchbattery_status";
	private static final String PREF_FIREBATTERY_STATUS	= "firebattery_status";
	private static final String PREF_ELECFENCE_SATTUS	= "elecfence_status";
	private static final String PREF_MORNING_STATUS		= "morning_status";
	private static final String PREF_EVENING_STATUS		= "evening_status";
	private static final String PREF_NEW_HEART_RATES	= "new_heart_rate_count";
	private static final String PREF_INFO_NOTIFICATION	= "info_notification";

	private static final String PREF_AGREED			= "agreement";

	public static final int	DEFAULT_MAX_RATE		= 100;
	public static final int	DEFAULT_MIN_RATE		= 60;
	public static final int DEFAULT_HIGH_PRESSURE_MAX = 140;
	public static final int DEFAULT_HIGH_PRESSURE_MIN = 90;
	public static final int DEFAULT_LOW_PRESSURE_MAX = 90;
	public static final int DEFAULT_LOW_PRESSURE_MIX = 60;
	public static final float DEFAULT_HIGH_TEMP = 37.2f;
	public static final float DEFAULT_LOW_TEMP = 36.0f;

	private static Prefs		m_instance;
	private final SharedPreferences			m_prefsRead;
	private final SharedPreferences.Editor	m_prefsWrite;

	public static Prefs Instance() {
		return m_instance;
	}

	@SuppressLint("CommitPrefEdits")
	public Prefs(Context a_context) {
		m_instance = this;

		m_prefsRead = a_context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		m_prefsWrite = m_prefsRead.edit();
	}

	public void commit() {
		m_prefsWrite.commit();
	}

	public String getUserName() {
		return m_prefsRead.getString(PREF_USER_NAME, "");
	}

	public String getUserPhone() {
		return m_prefsRead.getString(PREF_USER_PHONE, "");
	}

	public String getUserPswd() {
		return m_prefsRead.getString(PREF_USER_PSWD, "");
	}

	public boolean isSavedPhone() {
		return m_prefsRead.getBoolean(PREF_SAVE_PHONE, false);
	}

	public boolean isSavedPWSD() {
		return m_prefsRead.getBoolean(PREF_SAVE_PSWD, false);
	}

	public String getUserToken() {
		return m_prefsRead.getString(PREF_USER_TOKEN, "");
	}

	public String getMoniteringWatchSerial() {
		return m_prefsRead.getString(PREF_MONITERING_WATCH, "");
	}

	public boolean getSex() {
		return m_prefsRead.getBoolean(PREF_SEX, true);
	}

	public String getBirthday() {
		return m_prefsRead.getString(PREF_BIRTHDAY, "");
	}

	public String getWeixinId() {
		return m_prefsRead.getString(PREF_WEIXIN_ID, "");
	}

	public String getQQId() {
		return m_prefsRead.getString(PREF_QQ_ID, "");
	}

	public String getEmail() {
		return m_prefsRead.getString(PREF_EMAIL, "");
	}

	public String getProvince() {
		return m_prefsRead.getString(PREF_PROVINCE, "");
	}

	public String getCity() {
		return m_prefsRead.getString(PREF_CITY, "");
	}

	public String getDistrict() {
		return m_prefsRead.getString(PREF_DISTRICT, "");
	}

	public String getContent() {
		return m_prefsRead.getString(PREF_CONTENT, "");
	}

	public String getPolicy() {
		return m_prefsRead.getString(PREF_POLICY, "");
	}

	public String getAgreement() {
		return m_prefsRead.getString(PREF_AGREEMENT, "");
	}

	public boolean getAgreed() {
		return m_prefsRead.getBoolean(PREF_AGREED, false);
	}

//	public int getWatchFreeDays() {
//		return m_prefsRead.getInt(PREF_WATCH_FREE_DAYS, 0);
//	}

//	public int getYanganFreeDays() {
//		return m_prefsRead.getInt(PREF_YANGAN_FREE_DAYS, 0);
//	}

	public int getRanqiFreeDays() {
		return m_prefsRead.getInt(PREF_RANQI_FREE_DAYS, 0);
	}

	public String getWatchPay() {
		return m_prefsRead.getString(PREF_WATCH_PAY, "");
	}

	public String getYanganPay() {
		return m_prefsRead.getString(PREF_YANGAN_PAY, "");
	}

	public String getRanqiPay() {
		return m_prefsRead.getString(PREF_RANQI_PAY, "");
	}

//	public String getUserBirthDesc() {
//		return m_prefsRead.getString(PREF_USER_BIRTH_DESC, "");
//	}

	public String getUserPhoto() {
		return m_prefsRead.getString(PREF_USER_PHOTO, "");
	}

	public String getCardPhoto() {
		return m_prefsRead.getString(PREF_CARD_PHOTO, "");
	}

	public boolean getSosStatus() {
		return m_prefsRead.getBoolean(PREF_SOS_STATUS, false);
	}

	public boolean getFireStatus() {
		return m_prefsRead.getBoolean(PREF_FIRE_STATUS, false);
	}

	public boolean getWatchNetStatus() {
		return m_prefsRead.getBoolean(PREF_WATCH_NETSTATUS, false);
	}

	public boolean getFireNetStatus() {
		return m_prefsRead.getBoolean(PREF_FIRE_NETSTATUS, false);
	}

	public boolean getHeartRateStatus() {
		return m_prefsRead.getBoolean(PREF_HEARTRATE_STATUS, false);
	}

	public boolean getWatchBatteryStatus() {
		return m_prefsRead.getBoolean(PREF_WATCHBATTERY_STATUS, false);
	}

	public boolean getFireBatteryStatus() {
		return m_prefsRead.getBoolean(PREF_FIREBATTERY_STATUS, false);
	}

	public boolean getElecFenceStatus() {
		return m_prefsRead.getBoolean(PREF_ELECFENCE_SATTUS, false);
	}

	public boolean getInfoNotification() {
		return m_prefsRead.getBoolean(PREF_INFO_NOTIFICATION, true);
	}

	public boolean getMorningStatus() {
		return m_prefsRead.getBoolean(PREF_MORNING_STATUS, false);
	}

	public boolean getEveningStatus() {
		return m_prefsRead.getBoolean(PREF_EVENING_STATUS, false);
	}

	public int getNewHeartRateCount() {
		return m_prefsRead.getInt(PREF_NEW_HEART_RATES, 0);
	}

	public void setUserName(String a_sUserName) {
		m_prefsWrite.putString(PREF_USER_NAME, a_sUserName);
	}

	public void setUserPhone(String a_sUserPhone) {
		m_prefsWrite.putString(PREF_USER_PHONE, a_sUserPhone);
	}

	public void setUserPswd(String a_sUserPswd) {
		m_prefsWrite.putString(PREF_USER_PSWD, a_sUserPswd);
	}

	public void setSavedPhone(boolean flag) {
		m_prefsWrite.putBoolean(PREF_SAVE_PHONE, flag);
	}

	public void setSavedPWSD(boolean flag) {
		m_prefsWrite.putBoolean(PREF_SAVE_PSWD, flag);
	}

	public void setUserToken(String a_sUserToken) {
		m_prefsWrite.putString(PREF_USER_TOKEN, a_sUserToken);
	}

	public void setMoniteringWatchSerial(String serial) {
		m_prefsWrite.putString(PREF_MONITERING_WATCH, serial);
	}

	public void setSex(boolean a_bSex) {
		m_prefsWrite.putBoolean(PREF_SEX, a_bSex);
	}

	public void setBirthday(String a_sBirthday) {
		m_prefsWrite.putString(PREF_BIRTHDAY, a_sBirthday);
	}

	public void setWeixinId(String a_sWeixinId) {
		m_prefsWrite.putString(PREF_WEIXIN_ID, a_sWeixinId);
	}

	public void setQQId(String a_sQQId) {
		m_prefsWrite.putString(PREF_QQ_ID, a_sQQId);
	}

	public void setEmail(String a_sEmail) {
		m_prefsWrite.putString(PREF_EMAIL, a_sEmail);
	}

	public void setProvince(String a_sProvince) {
		m_prefsWrite.putString(PREF_PROVINCE, a_sProvince);
	}

	public void setCity(String a_sCity) {
		m_prefsWrite.putString(PREF_CITY, a_sCity);
	}

	public void setDistrict(String a_sDistrict) {
		m_prefsWrite.putString(PREF_DISTRICT, a_sDistrict);
	}

	public void setUserPhoto(String a_sUserPhoto) {
		m_prefsWrite.putString(PREF_USER_PHOTO, a_sUserPhoto);
	}

	public void setUserCard(String a_sUserCard) {
		m_prefsWrite.putString(PREF_CARD_PHOTO, a_sUserCard);
	}

	public void setContent(String a_sContent) {
		m_prefsWrite.putString(PREF_CONTENT, a_sContent);
	}

	public void setPolicy(String a_sPolicy) {
		m_prefsWrite.putString(PREF_POLICY, a_sPolicy);
	}

	public void setAgreement(String a_sAgreement) {
		m_prefsWrite.putString(PREF_AGREEMENT, a_sAgreement);
	}

	public void setAgreed() {
		m_prefsWrite.putBoolean(PREF_AGREED, true);
	}

	public void setWatchFreeDays(int a_iWatchFreeDays) {
		m_prefsWrite.putInt(PREF_WATCH_FREE_DAYS, a_iWatchFreeDays);
	}

	public void setYanganFreeDays(int a_iYanganFreeDays) {
		m_prefsWrite.putInt(PREF_YANGAN_FREE_DAYS, a_iYanganFreeDays);
	}

	public void setRanqiFreeDays(int a_iRanqiFreeDays) {
		m_prefsWrite.putInt(PREF_RANQI_FREE_DAYS, a_iRanqiFreeDays);
	}

	public void setWatchPay(String a_sWatchPay) {
		m_prefsWrite.putString(PREF_WATCH_PAY, a_sWatchPay);
	}

//	public void setYanganPay(String a_sYanganPay) {
//		m_prefsWrite.putString(PREF_YANGAN_PAY, a_sYanganPay);
//	}

	public void setRanqiPay(String a_sRanqiPay) {
		m_prefsWrite.putString(PREF_RANQI_PAY, a_sRanqiPay);
	}

	public void setUserBirthDesc(String a_sUserBirthDesc) {
		m_prefsWrite.putString(PREF_USER_BIRTH_DESC, a_sUserBirthDesc);
	}

	public void setSosStatus(boolean a_bSosStatus) {
		m_prefsWrite.putBoolean(PREF_SOS_STATUS, a_bSosStatus);
	}

	public void setFireStatus(boolean a_bFireStatus) {
		m_prefsWrite.putBoolean(PREF_FIRE_STATUS, a_bFireStatus);
	}

	public void setWatchNetStatus(boolean a_bWatchNetStatus) {
		m_prefsWrite.putBoolean(PREF_WATCH_NETSTATUS, a_bWatchNetStatus);
	}

	public void setFireNetstatus(boolean a_bFireNetstatus) {
		m_prefsWrite.putBoolean(PREF_FIRE_NETSTATUS, a_bFireNetstatus);
	}

	public void setHeartrateStatus(boolean a_bHeartrateStatus) {
		m_prefsWrite.putBoolean(PREF_HEARTRATE_STATUS, a_bHeartrateStatus);
	}

	public void setWatchbatteryStatus(boolean a_bWatchbatteryStatus) {
		m_prefsWrite.putBoolean(PREF_WATCHBATTERY_STATUS, a_bWatchbatteryStatus);
	}

	public void setFirebatteryStatus(boolean a_bFirebatteryStatus) {
		m_prefsWrite.putBoolean(PREF_FIREBATTERY_STATUS, a_bFirebatteryStatus);
	}

	public void setElecfenceSattus(boolean a_bElecfenceSattus) {
		m_prefsWrite.putBoolean(PREF_ELECFENCE_SATTUS, a_bElecfenceSattus);
	}

	public void setMorningStatus(boolean a_bMorningStatus) {
		m_prefsWrite.putBoolean(PREF_MORNING_STATUS, a_bMorningStatus);
	}

	public void setEveningStatus(boolean a_bEveningStatus) {
		m_prefsWrite.putBoolean(PREF_EVENING_STATUS, a_bEveningStatus);
	}

	public void setNewHeartRateCount(int a_iNewHeartRateCount) {
		m_prefsWrite.putInt(PREF_NEW_HEART_RATES, a_iNewHeartRateCount);
	}

	public void setInfoNotification(boolean a_bShowNoti) {
		m_prefsWrite.putBoolean(PREF_INFO_NOTIFICATION, a_bShowNoti);
	}
}
