package com.iot.shoumengou.http;

import com.iot.shoumengou.Global;
import com.iot.shoumengou.model.ItemSensorInfo;
import com.iot.shoumengou.model.ItemWatchInfo;

import java.util.HashMap;
import java.util.Map;

public class HttpAPI {
    public static void login(String phoneNumber, String password, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "login");
        params.put("mobile", phoneNumber);
        params.put("password", password);
        params.put("registration_id", Global.JPUSH_ID);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getVerifyCode(String phoneNumber, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getVerifyCode");
        params.put("mobile", phoneNumber);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getRegisterVerifyCode(String phoneNumber, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getRegisterVerifyCode");
        params.put("mobile", phoneNumber);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getUpdateUserInfoVerifyCode(String token, String phoneNumber, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getUpdateUserInfoVerifyCode");
        params.put("token", token);
        params.put("mobile", phoneNumber);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getUpdatePasswordVerifyCode(String token, String phoneNumber, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getUpdatePasswordVerifyCode");
        params.put("token", token);
        params.put("mobile", phoneNumber);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void register(String phoneNumber, String code, String password, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "register");
        params.put("mobile", phoneNumber);
        params.put("verify_code", code);
        params.put("password", password);
        params.put("registration_id", Global.JPUSH_ID);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void forgotPassword(String phoneNumber, String code, String password, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "forgotPassword");
        params.put("mobile", phoneNumber);
        params.put("verify_code", code);
        params.put("password", password);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getAppInfo(final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getAppInfo");

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void updateUserInfo(String token, String mobile, String name, boolean sex, String birthday, String photo, String strCard, String phone, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "updateUserInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("name", name);
        params.put("sex", sex ? "1" : "0");
        params.put("birthday", birthday);
        params.put("picture_src", photo);
        params.put("phone", phone);
        params.put("id_card_front_src", strCard);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void resetPassword(String token, String mobile, String oldPassword, String newPassword, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "resetPassword");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("old_password", oldPassword);
        params.put("new_password", newPassword);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getLastHealthData(String token, String mobile, String deviceSerial, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getLastHealthData");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", deviceSerial);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getLastHealthDataWithTimeInterval(String token, String mobile, String deviceSerial, Integer timeInterval, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getLastHealthData");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", deviceSerial);
        params.put("time_interval", String.valueOf(timeInterval));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getNewsList(String token, String mobile, String lastTime, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getNewsList");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("last_release_time", lastTime);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getNewsInfo(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getNewsInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getWatchSetInfo(final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getWatchSetInfo");

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void registerWatch(String token, String mobile, String serial, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "registerWatch");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void updateWatchInfo(String token,
                                       String mobile,
                                       ItemWatchInfo itemWatchInfo,
                                       final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "updateWatchInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(itemWatchInfo.id));
        params.put("device_serial", itemWatchInfo.serial);
        params.put("user_name", itemWatchInfo.name);
        params.put("user_phone", itemWatchInfo.phone);
        params.put("user_sex", String.valueOf(itemWatchInfo.sex));
        params.put("user_birthday", itemWatchInfo.birthday);
        params.put("user_birthday_solar", String.valueOf(itemWatchInfo.userBirthdaySolar));
        params.put("user_relative", itemWatchInfo.userRelation);
        params.put("residence", itemWatchInfo.residence);
        params.put("address", itemWatchInfo.address);
        params.put("id_card_front", itemWatchInfo.idCardFront);
        params.put("id_card_back", itemWatchInfo.idCardBack);
        params.put("id_card_num", itemWatchInfo.idCardNum);
        params.put("id_card_address", itemWatchInfo.idCardAddr);
        params.put("social_province", itemWatchInfo.socialProvince);
        params.put("social_city", itemWatchInfo.socialCity);
        params.put("social_district", itemWatchInfo.socialDisctrict);
        params.put("social_num", itemWatchInfo.socialNum);
        params.put("ill_history", itemWatchInfo.ill_history);
        params.put("ill_history_other", itemWatchInfo.ill_history_other);
        params.put("family_ill_history", itemWatchInfo.family_ill_history);
        params.put("family_ill_history_other", itemWatchInfo.family_ill_history_other);
        params.put("province", itemWatchInfo.province == null ? "" : itemWatchInfo.province);
        params.put("city", itemWatchInfo.city == null ? "" : itemWatchInfo.city);
        params.put("district", itemWatchInfo.district == null ? "" : itemWatchInfo.district);
        params.put("lon", itemWatchInfo.lon);
        params.put("lat", itemWatchInfo.lat);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void registerSensor(String token, String mobile, String serial, String type, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "registerSensor");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);
        params.put("type", type);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void updateSensorInfo(String token, String mobile, ItemSensorInfo itemSensorInfo, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "updateSensorInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(itemSensorInfo.id));
        params.put("type", itemSensorInfo.type);
        params.put("device_serial", itemSensorInfo.serial);
        params.put("contact_name", itemSensorInfo.contactName);
        params.put("contact_phone", itemSensorInfo.contactPhone);
        params.put("province", itemSensorInfo.province == null ? "" : itemSensorInfo.province);
        params.put("city", itemSensorInfo.city == null ? "" : itemSensorInfo.city);
        params.put("district", itemSensorInfo.district == null ? "" : itemSensorInfo.district);
        params.put("lon", itemSensorInfo.lon);
        params.put("lat", itemSensorInfo.lat);
        params.put("label", itemSensorInfo.locationLabel);
        params.put("label_relative", itemSensorInfo.labelRelative);
        params.put("address", itemSensorInfo.address);
        params.put("residence", itemSensorInfo.residence);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getSensorList(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getSensorList");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getWatchList(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getWatchList");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void setSosContacts(String token,
                                      String mobile,
                                      String sosContact2Name,
                                      String sosContact2Phone,
                                      String sosContact2Code,
                                      String sosContact3Name,
                                      String sosContact3Phone,
                                      String sosContact3Code,
                                      final VolleyCallback resultCallback,
                                      String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "setSosContacts");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("sos_contact2_name", sosContact2Name);
        params.put("sos_contact2_phone", sosContact2Phone);
        params.put("sos_contact2_verify_code", sosContact2Code);
        params.put("sos_contact3_name", sosContact3Name);
        params.put("sos_contact3_phone", sosContact3Phone);
        params.put("sos_contact3_verify_code", sosContact3Code);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void setHeartRatePeriod(String token, String mobile, int id, int lowRate, int highRate, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "setHeartRatePeriod");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(id));
        params.put("heart_rate_high", String.valueOf(highRate));
        params.put("heart_rate_low", String.valueOf(lowRate));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void setHealthPeriod(String token, String mobile, String serial,
                                       int heart_rate_high_limit,
                                       int heart_rate_low_limit,
                                       int blood_pressure_high_left_limit,
                                       int blood_pressure_high_right_limit,
                                       int blood_pressure_low_left_limit,
                                       int blood_pressure_low_right_limit,
                                       float temperature_high_limit,
                                       float temperature_low_limit,
                                       int measure_period,
                                       final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "setHealthPeriod");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);

        params.put("heart_rate_high_limit", String.valueOf(heart_rate_high_limit));
        params.put("heart_rate_low_limit", String.valueOf(heart_rate_low_limit));
        params.put("blood_pressure_high_left_limit", String.valueOf(blood_pressure_high_left_limit));
        params.put("blood_pressure_high_right_limit", String.valueOf(blood_pressure_high_right_limit));
        params.put("blood_pressure_low_left_limit", String.valueOf(blood_pressure_low_left_limit));
        params.put("blood_pressure_low_right_limit", String.valueOf(blood_pressure_low_right_limit));
        params.put("temperature_high_limit", String.valueOf(temperature_high_limit));
        params.put("temperature_low_limit", String.valueOf(temperature_low_limit));
        params.put("measure_period", String.valueOf(measure_period));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void deleteWatch(String token, String mobile, String serial, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "deleteWatch");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void deleteSensor(String token, String mobile, String serial, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "deleteSensor");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getAlarmSetInfo(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getAlarmSetInfo");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void setAlarmSetInfo(String token, String mobile, boolean sosStatus, boolean fireStatus, boolean watchNetStatus, boolean fireNetStatus, boolean heartRateStatus,
                                       boolean watchBatteryStatus, boolean fireBatteryStatus, boolean elecFenceStatus, boolean morningStatus, boolean eveningStatus, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "setAlarmSetInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("sos_status", String.valueOf(sosStatus));
        params.put("fire_status", String.valueOf(fireStatus));
        params.put("watch_net_status", String.valueOf(watchNetStatus));
        params.put("fire_net_status", String.valueOf(fireNetStatus));
        params.put("heart_rate_status", String.valueOf(heartRateStatus));
        params.put("watch_battery_status", String.valueOf(watchBatteryStatus));
        params.put("fire_battery_status", String.valueOf(fireBatteryStatus));
        params.put("electron_fence_status", String.valueOf(elecFenceStatus));
        params.put("morning_status", String.valueOf(morningStatus));
        params.put("evening_status", String.valueOf(eveningStatus));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getAlarmDetail(String token, String mobile, int alarmId, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getAlarmDetail");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("alarm_id", String.valueOf(alarmId));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void updateMobile(String token, String mobile, String newMobile, String verifyCode, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "updateMobile");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("new_mobile", newMobile);
        params.put("verify_code", verifyCode);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getHealthDataList(String token, String mobile, String deviceSerial, int days, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getHealthDataList");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", deviceSerial);
        params.put("days", String.valueOf(days));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getHeartRateHistory(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getHeartRateHistory");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

//    public static void getRecentHeartRateInfo(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
//        String url = HttpAPIConst.URL_API;
//
//        Map<String, String> params = new HashMap<>();
//        params.put("pAct", "getRecentHeartRateInfo");
//        params.put("token", token);
//        params.put("mobile", mobile);
//        params.put("id", String.valueOf(id));
//
//        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
//    }
//
//    public static void requestPaidService(String token, String mobile, String serial, String type, int serviceYear, String serviceStart, String serviceEnd, int amount, int payType, final VolleyCallback resultCallback, String tag) {
//        String url = HttpAPIConst.URL_API;
//
//        Map<String, String> params = new HashMap<>();
//        params.put("pAct", "requestPaidService");
//        params.put("token", token);
//        params.put("mobile", mobile);
//        params.put("device_serial", serial);
//        params.put("device_type", type);
//        params.put("service_years", String.valueOf(serviceYear));
//        params.put("service_start", serviceStart);
//        params.put("service_end", serviceEnd);
//        params.put("amount", String.valueOf(amount));
//        params.put("pay_type", String.valueOf(payType));
//
//        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
//    }

    public static void requestPay(String token, String mobile, String serial, String type, int serviceYear, String serviceStart, String serviceEnd, String amount, int payType, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "requestPay");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);
        params.put("device_type", type);
        params.put("service_years", String.valueOf(serviceYear));
        params.put("service_start", serviceStart);
        params.put("service_end", serviceEnd);
        params.put("amount", amount);
        params.put("pay_type", String.valueOf(payType));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void inquirePay(String token, String mobile, String serial, int orderId, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "inquirePay");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);
        params.put("order_id", Integer.toString(orderId));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void inquirePaidService(String token, String mobile, String device_serial, String type, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "inquirePaidService");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", device_serial);
        params.put("device_type", type);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void cancelPaidService(String token, String mobile, String device_serial, String type, int orderId, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "cancelPaidService");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", device_serial);
        params.put("device_type", type);
        params.put("order_id", String.valueOf(orderId));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getWatchPos(String token, String mobile, String serial, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getWatchPos");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getWatchPosList(String token, String mobile, String serial, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getWatchPosList");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void setPosUpdateMode(String token, String mobile, String serial, int mode, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "setPosUpdateMode");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);
        params.put("mode", String.valueOf(mode));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void setElecFenceInfo(String token, String mobile, String serial, String fenceList, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "setElecFenceInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);
        params.put("fence_list", fenceList);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getElecFenceInfo(String token, String mobile, String serial, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getElecFenceInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_serial", serial);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getWeatherInfo(String city, final VolleyCallback resultCallback, String tag) {
        String url = "https://way.jd.com/he/freeweather?city=" + city + "&appkey=3dc194ba40b2ded0a8286bb42030a231";
        //"http://api.map.baidu.com/telematics/v3/weather?location=dandong&output=json&ak=3p49MVra6urFRGOT9s8UBWr2"

        VolleyRequest.getStringResponse(url, resultCallback, tag);
    }

    public static void inquirePaidServiceBunch(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "inquirePaidServiceBunch");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getRescueList(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getRescueList");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getRescueDetail(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getRescueDetail");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("rescue_id", String.valueOf(id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getIdCardFrontInfo(String token, String mobile, String id_card_front_src, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getIdCardFrontInfo");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id_card_front_src", id_card_front_src);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void requestChat(String token, String mobile, int alarmid, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "requestChat");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("alarm_id", Integer.toString(alarmid));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void updateLocation(String token, String mobile, String lat, String lon, String province, String city, String disctrict, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "updateLocation");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("lat", lat);
        params.put("lon", lon);
        params.put("province", province);
        params.put("city", city);
        params.put("district", disctrict);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getAllNotificationList(String token, String mobile, int last_id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getAllNotificationList");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("last_id", String.valueOf(last_id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void getAllNotificationList(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "getAllNotificationList");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void removeNotification(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "removeNotification");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void removeAccount(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "removeAccount");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void readNotification(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "readNotification");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void requestHealthData(String token, String mobile, String deviceType, String deviceSerial, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "requestHealthData");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_type", deviceType);
        params.put("device_serial", deviceSerial);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void receiveHealthData(String token, String mobile, String deviceType, String deviceSerial, boolean isSuccess, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "receiveHealthData");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_type", deviceType);
        params.put("device_serial", deviceSerial);
        if (isSuccess){
            params.put("resp_code", String.valueOf(HttpAPIConst.RespCode.SUCCESS));
        }else{
            params.put("resp_code", String.valueOf(HttpAPIConst.RespCode.LOGIC_ERROR));
        }
        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void requestWatchPos(String token, String mobile, String deviceType, String deviceSerial, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "requestWatchPos");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("device_type", deviceType);
        params.put("device_serial", deviceSerial);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void readAllNotification(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "readAllNotification");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void removeAllNotification(String token, String mobile, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "removeAllNotification");
        params.put("token", token);
        params.put("mobile", mobile);

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }

    public static void updateMonitorId(String token, String mobile, int id, final VolleyCallback resultCallback, String tag) {
        String url = HttpAPIConst.URL_API;

        Map<String, String> params = new HashMap<>();
        params.put("pAct", "updateMonitorId");
        params.put("token", token);
        params.put("mobile", mobile);
        params.put("id", String.valueOf(id));

        VolleyRequest.getStringResponsePost(url, params, resultCallback, tag);
    }
}