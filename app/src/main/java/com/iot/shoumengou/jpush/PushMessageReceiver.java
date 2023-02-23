package com.iot.shoumengou.jpush;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.activity.ActivityLogin;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.helper.RoomActivity;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class PushMessageReceiver extends JPushMessageReceiver {
    private static final String TAG = "PushMessageReceiver";

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        Log.e(TAG, "[onMessage] " + customMessage);
        Intent intent = new Intent("com.jiguang.demo.message");
        intent.putExtra("msg", customMessage.message);
        context.sendBroadcast(intent);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Log.e(TAG, "[onNotifyMessageOpened] " + message);

        Bundle bundle = new Bundle();
        bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle);
        bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent);
        bundle.putString(JPushInterface.EXTRA_EXTRA, message.notificationExtras);
        processMessage(context, bundle, true);
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            Log.d(TAG, "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
            return;
        }
        if (nActionExtra.equals("my_extra1")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一");
        } else if (nActionExtra.equals("my_extra2")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二");
        } else if (nActionExtra.equals("my_extra3")) {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三");
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义");
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Log.e(TAG, "[onNotifyMessageArrived] " + message);

        Bundle bundle = new Bundle();
        bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle);
        bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent);
        bundle.putString(JPushInterface.EXTRA_EXTRA, message.notificationExtras);
        processMessage(context, bundle, false);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        Log.e(TAG, "[onNotifyMessageDismiss] " + message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        Log.e(TAG, "[onRegister] " + registrationId);
        Intent intent = new Intent("com.jiguang.demo.message");
        intent.putExtra("rid", registrationId);
        context.sendBroadcast(intent);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        Log.e(TAG, "[onConnected] " + isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        Log.e(TAG, "[onCommandResult] " + cmdMessage);
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
        super.onNotificationSettingsCheck(context, isOn, source);
        Log.e(TAG, "[onNotificationSettingsCheck] isOn:" + isOn + ",source:" + source);
    }

    private void processMessage(Context context, Bundle bundle, boolean isOpened) {
        try {
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

            JSONObject extrasJson = new JSONObject(extras);
            String jPushData = extrasJson.optString("iot_data");

            processNotification(context, jPushData, title, alert, isOpened);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void processNotification(Context context, String jPushData, String title, String alert, boolean isOpened) {
        JSONObject noticeObject = null;
        try {
            noticeObject = new JSONObject(jPushData);


            boolean isNew = true;

            String receiver = noticeObject.optString("receiver_phone", "");
            if (!receiver.isEmpty() && !receiver.equals(Prefs.Instance().getUserPhone())){
                return;
            }

            int notificationId = noticeObject.optInt("db_id");
            ItemNotification itemNotification;
            IOTDBHelper iotdbHelper = new IOTDBHelper(context);
            if (noticeObject.optString("type").equals(ItemNotification.PUSH_TYPE_CLOCK)) {
                itemNotification = iotdbHelper.findNotificationEntry(notificationId);
            }
            else {
                itemNotification = Util.findNotificationEntry(notificationId);
            }

            if (itemNotification != null) {
                isNew = false;
            } else {
                itemNotification = new ItemNotification(
                        notificationId,
                        noticeObject.optString("type"),
                        noticeObject.optString("notice_type"),
                        noticeObject.optString("device_type"),
                        noticeObject.optString("device_serial"),
                        noticeObject.optInt("alarm_id"),
                        noticeObject.getLong("time"),
                        title,
                        alert,
                        noticeObject.optInt("alarmStatus"));
                if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_CHART)) {
                    itemNotification.isRead = 1;
                }

                if (itemNotification.id > 0 && !itemNotification.type.equals(ItemNotification.PUSH_TYPE_CLOCK)) {
                    Util.addNotification(itemNotification);
                }
                else if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_CLOCK)) {
                    iotdbHelper.addNotificationEntry(itemNotification);
                }
            }

            if (isNew && itemNotification.type.equals(ItemNotification.PUSH_TYPE_CHART)) {
                RoomActivity.setChatId(noticeObject.getString("roomId"));
                RoomActivity.setChatPass(noticeObject.getString("password"));
                Intent intent = new Intent(context, RoomActivity.class);
    //            intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }

            if (isOpened) {
                if (isNew) {
                    if (App.Instance().getCurrentActivity() == null) {
                        Intent intentLogin = new Intent(context, ActivityLogin.class);
                        //                intentLogin.putExtras(bundle);
                        intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intentLogin);
                    } else {
                        Intent intentNew = new Intent(context, App.Instance().getCurrentActivity().getClass());
                        intentNew.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        App.Instance().getCurrentActivity().startActivity(intentNew);
                    }
                }
            }
            else {
                if (isNew) {
                    Intent intent = new Intent(AppConst.ACTION_PUSH_RECEIVED);
                    intent.putExtra("notification_data", itemNotification);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
