package com.iot.shoumengou.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iot.shoumengou.model.ItemDiscover;
import com.iot.shoumengou.model.ItemFence;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.model.ItemPaidService;

import java.util.ArrayList;

public class IOTDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "manage_iot.db";
    private static final String TABLE_DISCOVER = "discover_table";
    private static final String TABLE_SENSOR = "sensor_table";
    private static final String TABLE_WATCH = "watch_table";
    private static final String TABLE_NOTIFICATION = "notification_table";
    private static final String TABLE_HEART_RATE = "heart_rate_table";
    private static final String TABLE_PAID_SERVICE = "paid_service_table";
    private static final String TABLE_FENCE = "fence_table";
    private static final int DATABASE_VERSION = 6;

    private static final String COLUMN_DISCOVER_ID = "_id";
    private static final String COLUMN_DISCOVER_KIND = "kind";
    private static final String COLUMN_DISCOVER_TITLE = "title";
    private static final String COLUMN_DISCOVER_DATE = "date";
    private static final String COLUMN_DISCOVER_CONTENT = "content";
    private static final String COLUMN_DISCOVER_IMAGE = "image";
    private static final String COLUMN_DISCOVER_READ = "read";

    private static final String COLUMN_SENSOR_ID = "_id";
    private static final String COLUMN_SENSOR_SERIAL = "serial";
    private static final String COLUMN_SENSOR_IS_MANAGER = "is_manager";
    private static final String COLUMN_SENSOR_SERVICE_START = "service_start";
    private static final String COLUMN_SENSOR_SERVICE_END = "service_end";
    private static final String COLUMN_SENSOR_NET_STATUS = "net_status";
    private static final String COLUMN_SENSOR_TYPE = "sensor_type";
    private static final String COLUMN_SENSOR_CONTACT_NAME = "contact_name";
    private static final String COLUMN_SENSOR_CONTACT_PHONE = "contact_phone";
    private static final String COLUMN_SENSOR_LABEL = "location_label";
    private static final String COLUMN_SENSOR_LAT = "lat";
    private static final String COLUMN_SENSOR_LON = "lon";
    private static final String COLUMN_SENSOR_PROVINCE = "province";
    private static final String COLUMN_SENSOR_CITY = "city";
    private static final String COLUMN_SENSOR_DISTRICT = "district";
    private static final String COLUMN_SENSOR_ADDRESS = "address";
    private static final String COLUMN_SENSOR_BATTERY_STATUS = "battery_status";
    private static final String COLUMN_SENSOR_ALARM_STATUS = "alarm_status";
    private static final String COLUMN_SENSOR_LABEL_RELATIVE = "label_relative";
    private static final String COLUMN_SENSOR_RESIDENCE = "residence";

    private static final String COLUMN_WATCH_ID = "_id";
    private static final String COLUMN_WATCH_SERIAL = "serial";
    private static final String COLUMN_WATCH_IS_MANAGER = "is_manager";
    private static final String COLUMN_WATCH_SERVICE_START = "service_start";
    private static final String COLUMN_WATCH_SERVICE_END = "service_end";
    private static final String COLUMN_WATCH_NET_STATUS = "net_status";
    private static final String COLUMN_WATCH_NAME = "name";
    private static final String COLUMN_WATCH_PHONE = "phone";
    private static final String COLUMN_WATCH_SEX = "sex";
    private static final String COLUMN_WATCH_BIRTHDAY = "birthday";
    private static final String COLUMN_WATCH_ILL_HISTORY = "ill_history";
    private static final String COLUMN_WATCH_ILL_HISTORY_OTHER = "ill_history_other";
    private static final String COLUMN_WATCH_FAMILY_ILL_HISTORY = "family_ill_history";
    private static final String COLUMN_WATCH_ILL_FAMILY_HISTORY_OTHER = "family_ill_history_other";
    private static final String COLUMN_WATCH_LAT = "lat";
    private static final String COLUMN_WATCH_LON = "lon";
    private static final String COLUMN_WATCH_PROVINCE = "province";
    private static final String COLUMN_WATCH_CITY = "city";
    private static final String COLUMN_WATCH_DISTRICT = "district";
    private static final String COLUMN_WATCH_RESIDENCE = "residence";
    private static final String COLUMN_WATCH_ADDRESS = "address";
    private static final String COLUMN_WATCH_CHARGE_STATUS = "charge_status";
    private static final String COLUMN_WATCH_CONTACT1_NAME = "sos_contact1_name";
    private static final String COLUMN_WATCH_CONTACT1_PHONE = "sos_contact1_phone";
    private static final String COLUMN_WATCH_CONTACT2_NAME = "sos_contact2_name";
    private static final String COLUMN_WATCH_CONTACT2_PHONE = "sos_contact2_phone";
    private static final String COLUMN_WATCH_CONTACT3_NAME = "sos_contact3_name";
    private static final String COLUMN_WATCH_CONTACT3_PHONE = "sos_contact3_phone";
    private static final String COLUMN_WATCH_PRESSURE_LOW_LEFT_LIMIT = "pressure_low_left_limit";
    private static final String COLUMN_WATCH_PRESSURE_HIGH_LEFT_LIMIT = "pressure_high_left_limit";
    private static final String COLUMN_WATCH_PRESSURE_LOW_RIGHT_LIMIT = "pressure_low_right_limit";
    private static final String COLUMN_WATCH_PRESSURE_HIGH_RIGHT_LIMIT = "pressure_high_right_limit";
    private static final String COLUMN_WATCH_TEMP_LOW_LIMIT = "temp_low_limit";
    private static final String COLUMN_WATCH_TEMP_HIGH_LIMIT = "temp_high_limit";
    private static final String COLUMN_WATCH_HEART_RATE_HIGH = "heart_rate_high";
    private static final String COLUMN_WATCH_HEART_RATE_LOW = "heart_rate_low";
    private static final String COLUMN_WATCH_POS_UPDATE_MODE = "pos_update_mode";
    private static final String COLUMN_WATCH_SOCIAL_PROVINCE = "social_province";
    private static final String COLUMN_WATCH_SOCIAL_CITY = "social_city";
    private static final String COLUMN_WATCH_SOCIAL_DISTRICT = "social_district";
    private static final String COLUMN_WATCH_SOCIAL_NUM = "social_num";
    private static final String COLUMN_WATCH_CARD_FRONT = "id_card_front";
    private static final String COLUMN_WATCH_CARD_BACK = "id_card_back";
    private static final String COLUMN_WATCH_CARD_NUM = "id_card_num";
    private static final String COLUMN_WATCH_RELATION = "user_relation";

    private static final String COLUMN_NOTIFICATION_ID = "id";
    private static final String COLUMN_NOTIFICATION_TYPE = "type";
    private static final String COLUMN_NOTIFICATION_NOTIFY_TYPE = "notice_type";
    private static final String COLUMN_NOTIFICATION_DEVICE_TYPE = "device_type";
    private static final String COLUMN_NOTIFICATION_DEVICE_SERIAL = "device_serial";
    private static final String COLUMN_NOTIFICATION_TIME = "time";
    private static final String COLUMN_NOTIFICATION_ALARM_ID = "alarm_id";
    private static final String COLUMN_NOTIFICATION_IS_READ = "is_read";
    private static final String COLUMN_NOTIFICATION_TITLE = "title";
    private static final String COLUMN_NOTIFICATION_ALERT = "alert";

    private static final String COLUMN_HEART_RATE_WATCHID = "_id";
    private static final String COLUMN_HEART_RATE_DATE = "check_date";
    private static final String COLUMN_HEART_RATE_TIME = "check_time";
    private static final String COLUMN_HEART_RATE = "heart_rate";

    private static final String COLUMN_PAID_SERVICE_ORDER_ID = "_id";
    private static final String COLUMN_PAID_SERVICE_DEVICE_ID = "item_id";
    private static final String COLUMN_PAID_SERVICE_DEVICE_TYPE = "item_type";
    private static final String COLUMN_PAID_SERVICE_USER = "mobile";
    private static final String COLUMN_PAID_SERVICE_AMOUNT = "amount";
    private static final String COLUMN_PAID_SERVICE_PAY_TYPE = "pay_type";
    private static final String COLUMN_PAID_SERVICE_YEARS = "service_years";
    private static final String COLUMN_PAID_SERVICE_START = "service_start";
    private static final String COLUMN_PAID_SERVICE_END = "service_end";
    private static final String COLUMN_PAID_SERVICE_PAY_TIME = "pay_time";

    private static final String COLUMN_FENCE_ID = "_id";
    private static final String COLUMN_FENCE_NAME = "name";
    private static final String COLUMN_FENCE_ADDRESS = "address";
    private static final String COLUMN_FENCE_LAT = "lat";
    private static final String COLUMN_FENCE_LON = "lot";
    private static final String COLUMN_FENCE_RADIUS = "radius";
    private static final String COLUMN_FENCE_TERMS = "terms";

    public IOTDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_DISCOVER = "CREATE TABLE " + TABLE_DISCOVER + "("
                + COLUMN_DISCOVER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_DISCOVER_KIND + " TEXT,"
                + COLUMN_DISCOVER_TITLE + " TEXT,"
                + COLUMN_DISCOVER_DATE + " TEXT,"
                + COLUMN_DISCOVER_CONTENT + " TEXT,"
                + COLUMN_DISCOVER_IMAGE + " TEXT,"
                + COLUMN_DISCOVER_READ + " INTEGER DEFAULT 0" + ")";

        db.execSQL(CREATE_TABLE_DISCOVER);

        String CREATE_TABLE_SENSOR = "CREATE TABLE " + TABLE_SENSOR + "("
                + COLUMN_SENSOR_ID + " INTEGER,"
                + COLUMN_SENSOR_SERIAL + " TEXT PRIMARY KEY,"
                + COLUMN_SENSOR_IS_MANAGER + " INTEGER DEFAULT 0,"
                + COLUMN_SENSOR_SERVICE_START + " TEXT,"
                + COLUMN_SENSOR_SERVICE_END + " TEXT,"
                + COLUMN_SENSOR_NET_STATUS + " INTEGER DEFAULT 0,"
                + COLUMN_SENSOR_TYPE + " TEXT,"
                + COLUMN_SENSOR_CONTACT_NAME + " TEXT,"
                + COLUMN_SENSOR_CONTACT_PHONE + " TEXT,"
                + COLUMN_SENSOR_LABEL + " TEXT,"
                + COLUMN_SENSOR_LAT + " TEXT,"
                + COLUMN_SENSOR_LON + " TEXT,"
                + COLUMN_SENSOR_PROVINCE + " TEXT,"
                + COLUMN_SENSOR_CITY + " TEXT,"
                + COLUMN_SENSOR_DISTRICT + " TEXT,"
                + COLUMN_SENSOR_ADDRESS + " TEXT,"
                + COLUMN_SENSOR_BATTERY_STATUS + " INTEGER DEFAULT 0,"
                + COLUMN_SENSOR_ALARM_STATUS + " INTEGER DEFAULT 0,"
                + COLUMN_SENSOR_LABEL_RELATIVE + " TEXT,"
                + COLUMN_SENSOR_RESIDENCE + " TEXT"+ ")";

        db.execSQL(CREATE_TABLE_SENSOR);

        String CREATE_TABLE_WATCH = "CREATE TABLE " + TABLE_WATCH + "("
                + COLUMN_WATCH_ID + " INTEGER,"
                + COLUMN_WATCH_SERIAL + " TEXT PRIMARY KEY,"
                + COLUMN_WATCH_IS_MANAGER + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_SERVICE_START + " TEXT,"
                + COLUMN_WATCH_SERVICE_END + " TEXT,"
                + COLUMN_WATCH_NET_STATUS + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_NAME + " TEXT,"
                + COLUMN_WATCH_PHONE + " TEXT,"
                + COLUMN_WATCH_SEX + " INTEGER DEFAULT -1,"
                + COLUMN_WATCH_BIRTHDAY + " TEXT,"
                + COLUMN_WATCH_ILL_HISTORY + " TEXT,"
                + COLUMN_WATCH_ILL_HISTORY_OTHER + " TEXT,"
                + COLUMN_WATCH_FAMILY_ILL_HISTORY + " TEXT,"
                + COLUMN_WATCH_ILL_FAMILY_HISTORY_OTHER + " TEXT,"
                + COLUMN_WATCH_LAT + " TEXT,"
                + COLUMN_WATCH_LON + " TEXT,"
                + COLUMN_WATCH_PROVINCE + " TEXT,"
                + COLUMN_WATCH_CITY + " TEXT,"
                + COLUMN_WATCH_DISTRICT + " TEXT,"
                + COLUMN_WATCH_RESIDENCE + " TEXT,"
                + COLUMN_WATCH_ADDRESS + " TEXT,"
                + COLUMN_WATCH_CHARGE_STATUS + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_CONTACT1_NAME + " TEXT,"
                + COLUMN_WATCH_CONTACT1_PHONE + " TEXT,"
                + COLUMN_WATCH_CONTACT2_NAME + " TEXT,"
                + COLUMN_WATCH_CONTACT2_PHONE + " TEXT,"
                + COLUMN_WATCH_CONTACT3_NAME + " TEXT,"
                + COLUMN_WATCH_CONTACT3_PHONE + " TEXT,"
                + COLUMN_WATCH_PRESSURE_LOW_LEFT_LIMIT + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_PRESSURE_HIGH_LEFT_LIMIT + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_PRESSURE_LOW_RIGHT_LIMIT + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_PRESSURE_HIGH_RIGHT_LIMIT + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_TEMP_LOW_LIMIT + " NUMBER DEFAULT 0,"
                + COLUMN_WATCH_TEMP_HIGH_LIMIT + " NUMBER DEFAULT 0,"
                + COLUMN_WATCH_HEART_RATE_HIGH + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_HEART_RATE_LOW + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_POS_UPDATE_MODE + " INTEGER DEFAULT 0,"
                + COLUMN_WATCH_SOCIAL_PROVINCE + " TEXT,"
                + COLUMN_WATCH_SOCIAL_CITY + " TEXT,"
                + COLUMN_WATCH_SOCIAL_DISTRICT + " TEXT,"
                + COLUMN_WATCH_SOCIAL_NUM + " TEXT,"
                + COLUMN_WATCH_CARD_FRONT + " TEXT,"
                + COLUMN_WATCH_CARD_BACK + " TEXT,"
                + COLUMN_WATCH_CARD_NUM + " TEXT,"
                + COLUMN_WATCH_RELATION + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_WATCH);

        String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION + "("
                + COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTIFICATION_TYPE + " TEXT,"
                + COLUMN_NOTIFICATION_NOTIFY_TYPE + " TEXT,"
                + COLUMN_NOTIFICATION_DEVICE_TYPE + " TEXT,"
                + COLUMN_NOTIFICATION_DEVICE_SERIAL + " TEXT,"
                + COLUMN_NOTIFICATION_TIME + " LONG DEFAULT 0,"
                + COLUMN_NOTIFICATION_ALARM_ID + " INTEGER DEFAULT -1,"
                + COLUMN_NOTIFICATION_IS_READ + " INTEGER DEFAULT 0,"
                + COLUMN_NOTIFICATION_TITLE + " TEXT,"
                + COLUMN_NOTIFICATION_ALERT + " TEXT" + ")";

        db.execSQL(CREATE_TABLE_NOTIFICATION);

        String CREATE_TABLE_HEART_RATE = "CREATE TABLE " + TABLE_HEART_RATE + "("
                + COLUMN_HEART_RATE_WATCHID + " INTEGER,"
                + COLUMN_HEART_RATE_DATE + " TEXT,"
                + COLUMN_HEART_RATE_TIME + " LONG PRIMARY KEY,"
                + COLUMN_HEART_RATE + " INTEGER DEFAULT 0" + ")";

        db.execSQL(CREATE_TABLE_HEART_RATE);

        String CREATE_TABLE_PAID_SERVICE = "CREATE TABLE " + TABLE_PAID_SERVICE + "("
                + COLUMN_PAID_SERVICE_ORDER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_PAID_SERVICE_DEVICE_ID + " INTEGER,"
                + COLUMN_PAID_SERVICE_DEVICE_TYPE + " INTEGER,"
                + COLUMN_PAID_SERVICE_USER + " TEXT,"
                + COLUMN_PAID_SERVICE_AMOUNT + " INTEGER,"
                + COLUMN_PAID_SERVICE_PAY_TYPE + " INTEGER,"
                + COLUMN_PAID_SERVICE_YEARS + " INTEGER,"
                + COLUMN_PAID_SERVICE_START + " TEXT,"
                + COLUMN_PAID_SERVICE_END + " TEXT)";

        db.execSQL(CREATE_TABLE_PAID_SERVICE);

        String CREATE_TABLE_FENCE = "CREATE TABLE " + TABLE_FENCE + "("
                + COLUMN_FENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FENCE_NAME + " TEXT,"
                + COLUMN_FENCE_ADDRESS + " TEXT,"
                + COLUMN_FENCE_LAT + " TEXT,"
                + COLUMN_FENCE_LON + " TEXT,"
                + COLUMN_FENCE_RADIUS + " INTEGER,"
                + COLUMN_FENCE_TERMS + " TEXT)";

        db.execSQL(CREATE_TABLE_FENCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DISCOVER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATCH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEART_RATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAID_SERVICE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FENCE);

        onCreate(db);
    }

    public void addDiscoverEntry(ItemDiscover discoverEntry) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DISCOVER_ID, discoverEntry.id);
        contentValues.put(COLUMN_DISCOVER_KIND, discoverEntry.newsType);
        contentValues.put(COLUMN_DISCOVER_TITLE, discoverEntry.title);
        contentValues.put(COLUMN_DISCOVER_DATE, discoverEntry.updatedDateStr);
        contentValues.put(COLUMN_DISCOVER_CONTENT, discoverEntry.content);
        contentValues.put(COLUMN_DISCOVER_IMAGE, discoverEntry.picture);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_DISCOVER, null, contentValues);
    }

    public void addNotificationEntry(ItemNotification notificationEntry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTIFICATION_ID, notificationEntry.id);
        contentValues.put(COLUMN_NOTIFICATION_TYPE, notificationEntry.type);
        contentValues.put(COLUMN_NOTIFICATION_NOTIFY_TYPE, notificationEntry.noticeType);
        contentValues.put(COLUMN_NOTIFICATION_DEVICE_TYPE, notificationEntry.deviceType);
        contentValues.put(COLUMN_NOTIFICATION_DEVICE_SERIAL, notificationEntry.deviceSerial);
        contentValues.put(COLUMN_NOTIFICATION_TIME, notificationEntry.time);
        contentValues.put(COLUMN_NOTIFICATION_ALARM_ID, notificationEntry.alarmId);
        contentValues.put(COLUMN_NOTIFICATION_IS_READ, notificationEntry.isRead);
        contentValues.put(COLUMN_NOTIFICATION_TITLE, notificationEntry.title);
        contentValues.put(COLUMN_NOTIFICATION_ALERT, notificationEntry.alert);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_NOTIFICATION, null, contentValues);
    }

    public void addPaidServiceEntry(ItemPaidService paidServiceEntry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PAID_SERVICE_ORDER_ID, paidServiceEntry.orderId);
        contentValues.put(COLUMN_PAID_SERVICE_DEVICE_ID, paidServiceEntry.deviceId);
        contentValues.put(COLUMN_PAID_SERVICE_DEVICE_TYPE, paidServiceEntry.type);
        contentValues.put(COLUMN_PAID_SERVICE_USER, paidServiceEntry.userPhone);
        contentValues.put(COLUMN_PAID_SERVICE_AMOUNT, paidServiceEntry.amount);
        contentValues.put(COLUMN_PAID_SERVICE_PAY_TYPE, paidServiceEntry.payType);
        contentValues.put(COLUMN_PAID_SERVICE_YEARS, paidServiceEntry.serviceYears);
        contentValues.put(COLUMN_PAID_SERVICE_START, paidServiceEntry.serviceStartDate);
        contentValues.put(COLUMN_PAID_SERVICE_END, paidServiceEntry.serviceEndDate);
        contentValues.put(COLUMN_PAID_SERVICE_PAY_TIME, paidServiceEntry.payTime);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.insert(TABLE_PAID_SERVICE, null, contentValues);
    }

    public long addFenceEntry(ItemFence fenceEntry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FENCE_NAME, fenceEntry.name);
        contentValues.put(COLUMN_FENCE_ADDRESS, fenceEntry.address);
        contentValues.put(COLUMN_FENCE_LAT, fenceEntry.lat);
        contentValues.put(COLUMN_FENCE_LON, fenceEntry.lon);
        contentValues.put(COLUMN_FENCE_RADIUS, fenceEntry.radius);
        contentValues.put(COLUMN_FENCE_TERMS, fenceEntry.strGuardTimeList);

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        long id = sqLiteDatabase.insert(TABLE_FENCE, null, contentValues);

        return (int)id;
    }

    public void deleteNotification(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_NOTIFICATION, COLUMN_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteDiscoverEntry(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_DISCOVER, COLUMN_DISCOVER_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    public void clearAll() {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_DISCOVER);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_SENSOR);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_WATCH);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NOTIFICATION);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_HEART_RATE);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_PAID_SERVICE);
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_FENCE);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteNotificationEntry(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_NOTIFICATION, COLUMN_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deletePaidServiceEntry(int orderId) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_PAID_SERVICE, COLUMN_PAID_SERVICE_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void deleteFenceEntry(int id) {
        try {
            SQLiteDatabase sqLiteDatabase = getWritableDatabase();
            sqLiteDatabase.delete(TABLE_FENCE, COLUMN_FENCE_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void updateDiscoverEntry(ItemDiscover oldDiscoverEntry, ItemDiscover newDiscoverEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DISCOVER_ID, newDiscoverEntry.id);
        contentValues.put(COLUMN_DISCOVER_KIND, newDiscoverEntry.newsType);
        contentValues.put(COLUMN_DISCOVER_TITLE, newDiscoverEntry.title);
        contentValues.put(COLUMN_DISCOVER_DATE, newDiscoverEntry.updatedDateStr);
        contentValues.put(COLUMN_DISCOVER_CONTENT, newDiscoverEntry.content);
        contentValues.put(COLUMN_DISCOVER_IMAGE, newDiscoverEntry.picture);
        contentValues.put(COLUMN_DISCOVER_READ, newDiscoverEntry.readCnt);

        sqLiteDatabase.update(TABLE_DISCOVER, contentValues, COLUMN_DISCOVER_ID + " = ?",
                new String[]{oldDiscoverEntry.id + ""});
    }


    public void updateNotificationEntry(ItemNotification oldNotificationEntry, ItemNotification newNotificationEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NOTIFICATION_ID, newNotificationEntry.id);
        contentValues.put(COLUMN_NOTIFICATION_TYPE, newNotificationEntry.type);
        contentValues.put(COLUMN_NOTIFICATION_NOTIFY_TYPE, newNotificationEntry.noticeType);
        contentValues.put(COLUMN_NOTIFICATION_DEVICE_TYPE, newNotificationEntry.deviceType);
        contentValues.put(COLUMN_NOTIFICATION_DEVICE_SERIAL, newNotificationEntry.deviceSerial);
        contentValues.put(COLUMN_NOTIFICATION_TIME, newNotificationEntry.time);
        contentValues.put(COLUMN_NOTIFICATION_ALARM_ID, newNotificationEntry.alarmId);
        contentValues.put(COLUMN_NOTIFICATION_IS_READ, newNotificationEntry.isRead);
        contentValues.put(COLUMN_NOTIFICATION_TITLE, newNotificationEntry.title);
        contentValues.put(COLUMN_NOTIFICATION_ALERT, newNotificationEntry.alert);

        sqLiteDatabase.update(TABLE_NOTIFICATION, contentValues, COLUMN_NOTIFICATION_ID + " = ?",
                new String[]{oldNotificationEntry.id + ""});
    }

    public void updatePaidServiceEntry(ItemPaidService oldPaidServiceEntry, ItemPaidService newPaidServiceEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PAID_SERVICE_ORDER_ID, newPaidServiceEntry.orderId);
        contentValues.put(COLUMN_PAID_SERVICE_DEVICE_ID, newPaidServiceEntry.deviceId);
        contentValues.put(COLUMN_PAID_SERVICE_DEVICE_TYPE, newPaidServiceEntry.type);
        contentValues.put(COLUMN_PAID_SERVICE_USER, newPaidServiceEntry.userPhone);
        contentValues.put(COLUMN_PAID_SERVICE_AMOUNT, newPaidServiceEntry.amount);
        contentValues.put(COLUMN_PAID_SERVICE_PAY_TYPE, newPaidServiceEntry.payType);
        contentValues.put(COLUMN_PAID_SERVICE_YEARS, newPaidServiceEntry.serviceYears);
        contentValues.put(COLUMN_PAID_SERVICE_START, newPaidServiceEntry.serviceStartDate);
        contentValues.put(COLUMN_PAID_SERVICE_END, newPaidServiceEntry.serviceEndDate);
        contentValues.put(COLUMN_PAID_SERVICE_PAY_TIME, newPaidServiceEntry.payTime);

        sqLiteDatabase.update(TABLE_PAID_SERVICE, contentValues, COLUMN_PAID_SERVICE_ORDER_ID + " = ?",
                new String[]{oldPaidServiceEntry.orderId + ""});
    }

    public void updateFenceEntry(ItemFence oldFenceEntry, ItemFence newFenceEntry)  {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FENCE_NAME, newFenceEntry.name);
        contentValues.put(COLUMN_FENCE_ADDRESS, newFenceEntry.address);
        contentValues.put(COLUMN_FENCE_LAT, newFenceEntry.lat);
        contentValues.put(COLUMN_FENCE_LON, newFenceEntry.lon);
        contentValues.put(COLUMN_FENCE_RADIUS, newFenceEntry.radius);
        contentValues.put(COLUMN_FENCE_TERMS, newFenceEntry.strGuardTimeList);

        sqLiteDatabase.update(TABLE_PAID_SERVICE, contentValues, COLUMN_FENCE_ID + " = ?",
                new String[]{oldFenceEntry.id + ""});
    }

    public ItemNotification findNotificationEntry(long time) {
        String query = "Select * FROM " + TABLE_NOTIFICATION + " WHERE " + COLUMN_NOTIFICATION_TIME + "=" + time;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemNotification notificationEntry = new ItemNotification();
        if (cursor.moveToFirst()) {
            notificationEntry.id = cursor.getInt(0);
            notificationEntry.type = cursor.getString(1);
            notificationEntry.noticeType = cursor.getString(2);
            notificationEntry.deviceType = cursor.getString(3);
            notificationEntry.deviceSerial = cursor.getString(4);
            notificationEntry.time = cursor.getLong(5);
            notificationEntry.alarmId = cursor.getInt(6);
            notificationEntry.isRead = cursor.getInt(7);
            notificationEntry.title = cursor.getString(8);
            notificationEntry.alert = cursor.getString(9);

            cursor.close();
        } else {
            notificationEntry = null;
        }
        return notificationEntry;
    }

    public ItemFence findFenceEntry(int id) {
        String query = "Select * FROM " + TABLE_FENCE + " WHERE " + COLUMN_FENCE_ID + "=" + id;
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        ItemFence itemFence = new ItemFence();
        if (cursor.moveToFirst()) {
            itemFence.id = cursor.getInt(0);
            itemFence.name = cursor.getString(1);
            itemFence.address = cursor.getString(2);
            itemFence.lat = cursor.getString(3);
            itemFence.lon = cursor.getString(4);
            itemFence.radius = cursor.getInt(5);
            itemFence.strGuardTimeList = cursor.getString(6);

            cursor.close();
        } else {
            itemFence = null;
        }
        return itemFence;
    }

    public ArrayList<ItemNotification> getAllNotificationEntries() {
        ArrayList<ItemNotification> entryList = new ArrayList<>();
        // Select all query
        //String query = "Select * FROM " + TABLE_NOTIFICATION  + " ORDER BY " + COLUMN_NOTIFICATION_IS_READ + " DESC, " + COLUMN_NOTIFICATION_TIME + " DESC";
        String query = "Select * FROM " + TABLE_NOTIFICATION  + " ORDER BY " + COLUMN_NOTIFICATION_TIME + " DESC";

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.rawQuery(query, null)) {
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while (hasNext) {
                ItemNotification itemNotification = new ItemNotification();
                itemNotification.id = cursor.getInt(0);
                itemNotification.type = cursor.getString(1);
                itemNotification.noticeType = cursor.getString(2);
                itemNotification.deviceType = cursor.getString(3);
                itemNotification.deviceSerial = cursor.getString(4);
                itemNotification.time = cursor.getLong(5);
                itemNotification.alarmId = cursor.getInt(6);
                itemNotification.isRead = cursor.getInt(7);
                itemNotification.title = cursor.getString(8);
                itemNotification.alert = cursor.getString(9);

                entryList.add(itemNotification);

                hasNext = cursor.moveToNext();
            }
        }

        return entryList;
    }

    public ArrayList<ItemFence> getFenceEntries() {
        ArrayList<ItemFence> entryList = new ArrayList<>();
        // Select all query
        String query = "Select * FROM " + TABLE_FENCE;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.rawQuery(query, null)) {
            // Looping through all rows and adding them to list
            boolean hasNext = cursor.moveToFirst();
            while (hasNext) {
                ItemFence itemFence = new ItemFence();
                itemFence.id = cursor.getInt(0);
                itemFence.name = cursor.getString(1);
                itemFence.address = cursor.getString(2);
                itemFence.lat = cursor.getString(3);
                itemFence.lon = cursor.getString(4);
                itemFence.radius = cursor.getInt(5);
                itemFence.strGuardTimeList = cursor.getString(6);

                entryList.add(itemFence);

                hasNext = cursor.moveToNext();
            }
        }

        return entryList;
    }

    public int notificationAlarmCounts() {
        int result = 0;
        String query = "Select * FROM " + TABLE_NOTIFICATION;

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        try (Cursor cursor = sqLiteDatabase.rawQuery(query, null)) {
            boolean hasNext = cursor.moveToFirst();
            while (hasNext) {
                boolean isRead = cursor.getInt(7) == 1;

                if (!isRead) {
                    result++;
                }

                hasNext = cursor.moveToNext();
            }
        }

        return result;
    }
}
