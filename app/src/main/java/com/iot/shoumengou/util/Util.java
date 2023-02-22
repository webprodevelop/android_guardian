//@formatter:off
package com.iot.shoumengou.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemDiscover;
import com.iot.shoumengou.model.ItemHeartRate;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.model.ItemPrice;
import com.iot.shoumengou.model.ItemPriceList;
import com.iot.shoumengou.model.ItemSensorInfo;
import com.iot.shoumengou.model.ItemType;
import com.iot.shoumengou.model.ItemWatchInfo;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Util {
	public static ArrayList<ItemPrice> freePriceList = new ArrayList<>();
//	public static ArrayList<ItemType> alarmTypeList = new ArrayList<>();
	public static ArrayList<ItemDiscover> recommendedList = new ArrayList<>();
	public static ArrayList<ItemDiscover> discoverList = new ArrayList<>();
	public static ArrayList<ItemNotification> notificationList = new ArrayList<>();
	public static ArrayList<ItemWatchInfo> watchList = new ArrayList<>();
	public static ArrayList<ItemHeartRate> measureList = new ArrayList<>();
	public static int monitoringWatchId = 0;
	public static ItemWatchInfo monitoringWatch;
	public static ArrayList<String> sensorTypeList = new ArrayList<>();
	public static Map<String, ItemType> sensorTypeMap = new HashMap<>();
	public static ArrayList<ItemPriceList> allPriceList = new ArrayList<>();
	public static Map<String, ArrayList<ItemSensorInfo>> sensorMap = new HashMap<>();
	public static String storeAppVersion;
	public static String storeAppURL;
	public static boolean isUpdatedHealthData = true;
	public static long healthDataStart = 0;
	public static long healthCheckStart = 0;
	public static Runnable refreshHealthData = null;

//	public static void ShowDialogSuccess(int message) {
//		Context context = App.Instance().getCurrentActivity();
//		if (context == null) {
//			context = ActivityMain.mainContext;
//		}
//		String str_message = context.getResources().getString(message);
//		AlertDialog.Builder		builder = new AlertDialog.Builder(context);
//		builder.setMessage(str_message);
//		builder.setPositiveButton(context.getResources().getString(R.string.str_ok), (dialog, which) -> dialog.dismiss());
//		AlertDialog dialog = builder.create();
//		dialog.show();
//	}

	public static void ShowDialogError(int message) {
		Context context = App.Instance().getCurrentActivity();
		if (context == null) {
			context = ActivityMain.mainContext;
		}
		String str_message = context.getResources().getString(message);
		ShowDialogError(str_message);
	}

	public static void ShowDialogError(int message, final ResultProcess process) {
		Context context = App.Instance().getCurrentActivity();
		if (context == null) {
			context = ActivityMain.mainContext;
		}
		String str_message = context.getResources().getString(message);
		AlertDialog.Builder		builder = new AlertDialog.Builder(context);
		builder.setMessage(str_message);
		builder.setPositiveButton(context.getResources().getString(R.string.str_ok), (dialog, which) -> dialog.dismiss()
		);
		AlertDialog dialog = builder.create();
		dialog.setOnDismissListener(dialog1 -> process.process());
		dialog.show();
	}

	public static void ShowDialogError(String str_message) {
		Context context = App.Instance().getCurrentActivity();
		if (context == null) {
			context = ActivityMain.mainContext;
		}
		AlertDialog.Builder		builder = new AlertDialog.Builder(context);
		builder.setMessage(str_message);
		builder.setPositiveButton(context.getResources().getString(R.string.str_ok), (dialog, which) -> dialog.dismiss());
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public static void ShowDialogError(String a_sError, final ResultProcess process) {
		Context context = App.Instance().getCurrentActivity();
		if (context == null) {
			context = ActivityMain.mainContext;
		}
		AlertDialog.Builder		builder = new AlertDialog.Builder(context);
		builder.setMessage(a_sError);
		builder.setPositiveButton(context.getResources().getString(R.string.str_ok), (dialog, which) -> dialog.dismiss()
		);
		AlertDialog dialog = builder.create();
		dialog.setOnDismissListener(dialog1 -> process.process());
		dialog.show();
	}

	public static void ShowDialogError(final Context a_context, String a_sError, final ResultProcess process) {
		AlertDialog.Builder		builder = new AlertDialog.Builder(a_context);
		builder.setMessage(a_sError);
		builder.setPositiveButton(a_context.getResources().getString(R.string.str_ok), (dialog, which) -> dialog.dismiss()
		);
		AlertDialog dialog = builder.create();
		dialog.setOnDismissListener(dialog1 -> process.process());
		dialog.show();
	}

	public static abstract class ResultProcess {
		abstract public void process();
	}

	public static void showToastMessage(Context context, int resId) {
		LayoutInflater inflater = LayoutInflater.from(context);
		@SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_toast, null);

		TextView text = layout.findViewById(R.id.text);
		text.setText(resId);

		Toast toast = Toast.makeText(context,resId, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.show();
	}

	public static void showToastMessage(Context context, String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		@SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.custom_toast, null);

		TextView text = layout.findViewById(R.id.text);
		text.setText(msg);

		Toast toast = Toast.makeText(context,msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(layout);
		toast.show();
	}

	public static long parseDateTimeSecFormatString(String strDate) {
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			Date date = sdf.parse(strDate);
			return Objects.requireNonNull(date).getTime();
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return 0;
	}

	public static long parseDateFormatString(String strDate) {
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date date = sdf.parse(strDate);
			return Objects.requireNonNull(date).getTime();
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return 0;
	}

	public static String getDateTimeSecFormatString(Date date) {
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(date);
	}

	public static String getDateTimeFormatString(Date date) {
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		return sdf.format(date);
	}

	public static String getTimeFormatStringIgnoreLocale(Date date) {
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

		return sdf.format(date);
	}

	public static String getDateFormatStringIgnoreLocale(Date date) {
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return sdf.format(date);
	}

	public static String getDateFormatString(Date date) {
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(App.getAppContext().getString(R.string.str_date_format));

		return sdf.format(date);
	}

	public static Date getDateFromFormatString(String date) {
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(App.getAppContext().getString(R.string.str_date_format));

		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static String getDayString(Date date) {
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");

		return sdf.format(date);
	}

	public static String convertDateString(String strDate, boolean formatted) {
		String strConvert = "";
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat(App.getAppContext().getString(R.string.str_date_format));
		@SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (formatted) {
				Date date = sdf1.parse(strDate);
				strConvert = sdf2.format(Objects.requireNonNull(date));
			} else {
				Date date = sdf2.parse(strDate);
				strConvert = sdf1.format(Objects.requireNonNull(date));
			}
		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return strConvert;
	}

	public static String extractTextFromHtmlContent(String strContent) {
		int sIndex = strContent.indexOf("<img");
		while (sIndex != -1) {
			int eIndex = strContent.indexOf("/>", sIndex);
			strContent = strContent.replace(strContent.substring(sIndex, eIndex + 2), "");
			sIndex = strContent.indexOf("<img");
		}

		return strContent;
	}

	public static void showBottomNumberPicker(Activity activity, int minValue, int maxValue, int value, NumberPicker.OnValueChangeListener valueChangeListener) {
		final BottomSheetDialog dialog = new BottomSheetDialog(activity);
		@SuppressLint("InflateParams") View parentView = activity.getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
		dialog.setContentView(parentView);
		((View)parentView.getParent()).setBackgroundColor(Color.TRANSPARENT);

		NumberPicker numberPicker = parentView.findViewById(R.id.ID_NUMPICK);
		numberPicker.setMaxValue(maxValue);
		numberPicker.setMinValue(minValue);
		numberPicker.setValue(value);
		numberPicker.setWrapSelectorWheel(false);
		numberPicker.setOnValueChangedListener(valueChangeListener);

		dialog.show();
	}

	public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		byte[] byteFormat = stream.toByteArray();
		// get the base 64 string

		return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
	}

	public static void setMoniteringWatchInfo(ItemWatchInfo watchInfo) {
		if (watchInfo != null && watchInfo.id != Util.monitoringWatchId) {
			HttpAPI.updateMonitorId(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), watchInfo.id,
					new VolleyCallback() {
						@Override
						public void onSuccess(String result) {
						}

						@Override
						public void onError(Object error) {
						}
					}, "setMoniteringWatchInfo");
		}
		monitoringWatch = watchInfo;
		if (watchInfo != null) {
			Util.monitoringWatchId = watchInfo.id;
		}
	}

	public static int getResId(String resName, Class<?> c) {
		try {
			Field idField = c.getDeclaredField(resName);
			return idField.getInt(idField);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static boolean isDateMonthBefore(String date) {
		Calendar calendar = Calendar.getInstance();
		long curTime = calendar.getTimeInMillis();
		long serviceEndTime = Util.parseDateFormatString(date);
		calendar.setTimeInMillis(serviceEndTime);
		calendar.add(Calendar.MONTH, -1);
		long expiredTime = calendar.getTimeInMillis();

		return (expiredTime < curTime);
	}

	public static boolean isDateWeekBefore(String date) {
		Calendar calendar = Calendar.getInstance();
		long curTime = calendar.getTimeInMillis();
		long serviceEndTime = Util.parseDateFormatString(date);
		calendar.setTimeInMillis(serviceEndTime);
		calendar.add(Calendar.DATE, -7);
		long expiredTime = calendar.getTimeInMillis();

		return (expiredTime < curTime);
	}

	public static void addNotification(ItemNotification entry) {
		if (entry.noticeType.equals(ItemNotification.NOTICE_TYPE_ALARM_OFF)) {
			for (int i = 0; i < notificationList.size(); i++) {
				ItemNotification itemNotification = notificationList.get(i);
				if (itemNotification.alarmId == entry.alarmId) {
					itemNotification.alarmStatus = 1;
					break;
				}
			}
		}
		notificationList.add(entry);
	}

	public static boolean hasAlarmWaiting(){
		for (int i = 0; i < notificationList.size(); i++) {
			ItemNotification itemNotification = notificationList.get(i);
			if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_ALARM) && itemNotification.alarmStatus != 1  && itemNotification.alarmStatus != 3) {
				return true;
			}
		}
		return false;
	}

	public static ItemNotification findNotificationEntry(int id) {
		for (int i = 0; i < notificationList.size(); i++) {
			ItemNotification entry = notificationList.get(i);
			if (entry.id == id)
				return entry;
		}
		return null;
	}

	public static void updateNotificationEntry(ItemNotification oldEntry, ItemNotification entry)  {
		for (int i = 0; i < notificationList.size(); i++) {
			ItemNotification itemNotification = notificationList.get(i);
			if (itemNotification.id == oldEntry.id) {
				notificationList.remove(i);
				notificationList.add(i, entry);
				return;
			}
		}
	}

	public static ArrayList<ItemNotification> getAllNotifications(Context context) {
		ArrayList<ItemNotification> tempResult = new ArrayList<>(notificationList);

		IOTDBHelper iotdbHelper = new IOTDBHelper(context);
		tempResult.addAll(iotdbHelper.getAllNotificationEntries());

		ArrayList<ItemNotification> alarmList = new ArrayList<>();
		ArrayList<ItemNotification> alarmOffList = new ArrayList<>();
		ArrayList<ItemNotification> noticeList = new ArrayList<>();
		for (int i = 0; i < tempResult.size(); i++) {
			if (tempResult.get(i).type.equals(ItemNotification.PUSH_TYPE_ALARM)) {
				if (tempResult.get(i).alarmStatus == 0 || tempResult.get(i).alarmStatus == 2)
					alarmList.add(tempResult.get(i));
				else {
					alarmOffList.add(tempResult.get(i));
				}
			}
			else {
				noticeList.add((tempResult.get(i)));
			}
		}

		Collections.sort(alarmList, (o1, o2) -> Long.compare(o2.time, o1.time));
		Collections.sort(alarmOffList, (o1, o2) -> Long.compare(o2.time, o1.time));
		Collections.sort(noticeList, (o1, o2) -> Long.compare(o2.time, o1.time));

		alarmList.addAll(alarmOffList);
		alarmList.addAll(noticeList);

		return alarmList;
	}

	public static int getAlarmCount() {
		ArrayList<ItemNotification> tempResult = new ArrayList<>(notificationList);
		int result = 0;
		for (int i = 0; i < tempResult.size(); i++) {
			if (tempResult.get(i).type.equals(ItemNotification.PUSH_TYPE_ALARM) &&
					(tempResult.get(i).alarmStatus == 0 || tempResult.get(i).alarmStatus == 2)) {
				result++;
			}
		}

		return result;
	}

	public static void deleteNotificationEntry(int id) {
		for (int i = 0; i < notificationList.size(); i++) {
			ItemNotification entry = notificationList.get(i);
			if (entry.id == id) {
				notificationList.remove(entry);
				return;
			}
		}
	}

	public static void clearNotificationTable() {
		notificationList.clear();
	}

	public static int getNotificationCounts(Context context) {
		ArrayList<ItemNotification> resultList = getAllNotifications(context);
		int result = 0;
		for (int i = 0; i < resultList.size(); i++) {
			if (resultList.get(i).isRead < 1) {
				result += 1;
			}
		}

		return result;
	}

	public static void addWatchEntry(ItemWatchInfo watchEntry) {
		watchList.add(watchEntry);
	}

	public static ItemWatchInfo findWatchEntry(String serial) {
		for (int i = 0; i < watchList.size(); i++) {
			ItemWatchInfo watchInfo = watchList.get(i);
			if (watchInfo.serial.equals(serial))
				return watchInfo;
		}
		return null;
	}

	public static void updateWatchEntry(ItemWatchInfo oldWatchEntry, ItemWatchInfo watchEntry)  {
		for (int i = 0; i < watchList.size(); i++) {
			ItemWatchInfo watchInfo = watchList.get(i);
			if (watchInfo.serial.equals(oldWatchEntry.serial)) {
				watchList.remove(i);
				watchList.add(i, watchEntry);
				if (monitoringWatch != null && monitoringWatch.id == watchEntry.id) {
					setMoniteringWatchInfo(watchEntry);
				}
				return;
			}
		}
	}

	public static ArrayList<ItemWatchInfo> getAllWatchEntries() {
		return watchList;
	}

	public static void deleteWatchEntry(String serial) {
		for (int i = 0; i < watchList.size(); i++) {
			ItemWatchInfo watchInfo = watchList.get(i);
			if (watchInfo.serial.equals(serial)) {
				watchList.remove(watchInfo);
				return;
			}
		}
	}

	public static void clearWatchTable() {
		watchList = new ArrayList<>();
	}

	public static int parseInt(JSONObject object, String key, int defaultKey) {
		int iValue = defaultKey;

		try {
			String value = object.getString(key);
			if (!value.isEmpty()) {
				iValue = Integer.parseInt(value);
			}
		} catch (Exception ignored) {
		}

		return iValue;
	}
	public static float parseFloat(JSONObject object, String key, float defaultKey) {
		float iValue = defaultKey;

		try {
			String value = object.getString(key);
			if (!value.isEmpty()) {
				iValue = Float.parseFloat(value);
			}
		} catch (Exception ignored) {
		}

		return iValue;
	}

	public static void addHeartRateEntry(ItemHeartRate heartRateEntry) {
		measureList.add(heartRateEntry);
	}

	public static void clearHeartRateEntry() {
		measureList.clear();
	}

	public static Bitmap resizeBitmap(Bitmap bitmap,int newWidth,int newHeight) {
		Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

		float ratioX = newWidth / (float) bitmap.getWidth();
		float ratioY = newHeight / (float) bitmap.getHeight();
		float middleX = newWidth / 2.0f;
		float middleY = newHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2.0f, middleY - bitmap.getHeight() / 2.0f, new Paint(Paint.FILTER_BITMAP_FLAG));

		return scaledBitmap;
	}

	public static void addSensorType(ItemType sensorType) {
		sensorTypeList.add(sensorType.type);
		sensorTypeMap.put(sensorType.type, sensorType);
		sensorMap.put(sensorType.type, new ArrayList<>());
	}

	public static void addPriceList(ItemPriceList priceList) {
		allPriceList.add(priceList);
	}

	public static void addSensorEntry(ItemSensorInfo sensorEntry) {
		if (sensorTypeMap.get(sensorEntry.type) != null) {
			Objects.requireNonNull(sensorMap.get(sensorEntry.type)).add(sensorEntry);
		}
	}

	public static void deleteSensorEntry(String type, String serial) {
		for (int i = 0; i < Objects.requireNonNull(sensorMap.get(type)).size(); i++) {
			ItemSensorInfo sensorEntry = Objects.requireNonNull(sensorMap.get(type)).get(i);
			if (sensorEntry.serial.equals(serial)) {
				Objects.requireNonNull(sensorMap.get(type)).remove(sensorEntry);
				return;
			}
		}
	}

	public static void updateSensorEntry(ItemSensorInfo oldSensorEntry, ItemSensorInfo newSensorEntry)  {
		for (int i = 0; i < Objects.requireNonNull(sensorMap.get(oldSensorEntry.type)).size(); i++) {
			ItemSensorInfo sensorEntry = Objects.requireNonNull(sensorMap.get(oldSensorEntry.type)).get(i);
			if (sensorEntry.serial.equals(oldSensorEntry.serial)) {
				Objects.requireNonNull(sensorMap.get(oldSensorEntry.type)).remove(sensorEntry);
				Objects.requireNonNull(sensorMap.get(oldSensorEntry.type)).add(newSensorEntry);
				return;
			}
		}
	}

	public static ItemSensorInfo findSensorEntry(String type, String serial) {
		for (int i = 0; i < Objects.requireNonNull(sensorMap.get(type)).size(); i++) {
			ItemSensorInfo sensorEntry = Objects.requireNonNull(sensorMap.get(type)).get(i);
			if (sensorEntry.serial.equals(serial)) {
				return sensorEntry;
			}
		}
		return null;
	}

	public static ArrayList<ItemSensorInfo> getAllSensorEntries(String type) {
		return new ArrayList<>(Objects.requireNonNull(sensorMap.get(type)));
	}

	public static void ignoreBatteryOptimization(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);

			boolean hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
			if (!hasIgnored) {
				Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setData(Uri.parse("package:" + activity.getPackageName()));
				if (intent.resolveActivity(activity.getPackageManager()) != null) {
					activity.startActivity(intent);
				}
			} else {
				Log.d("ignoreBattery", "hasIgnored");
			}
		}
	}
}
