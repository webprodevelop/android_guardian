package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;
import com.iot.shoumengou.view.DialogProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class ActivityBase extends AppCompatActivity {
    protected DialogProgress m_dlgProgress;

    protected View m_rootView;

    // Static
    private boolean IS_FOREGROUND = false;

    // For JPush
    private ReceiverJPushMessage m_receiverJPushMsg;

    private PopupWindow popupWindow = null;

    // Receiver for JPush
    public class ReceiverJPushMessage extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (!intent.getAction().equals(AppConst.ACTION_PUSH_RECEIVED))
                    return;

                ItemNotification itemNotification = (ItemNotification)intent.getSerializableExtra("notification_data");
                if (itemNotification != null) {
                    showNotificationPopup(itemNotification);
                }
            }
            catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IS_FOREGROUND = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        IS_FOREGROUND = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        App.Instance().cancelPendingRequests(getTag());
        if (m_receiverJPushMsg != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(m_receiverJPushMsg);
        }
    }

    private void RegisterReceiverJPushMsg() {
        m_receiverJPushMsg = new ReceiverJPushMessage();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(AppConst.ACTION_PUSH_RECEIVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(m_receiverJPushMsg, filter);
    }

    protected void initControls() {
        m_rootView = findViewById(R.id.ID_ROOT_VIEW);

        m_dlgProgress = new DialogProgress(this);
        m_dlgProgress.setCancelable(false);

        RegisterReceiverJPushMsg();
    }

    protected void setEventListener() {

    }

    protected String getTag() {
        return "ActivityBase";
    }

    @SuppressLint("SetTextI18n")
    public void showNotificationPopup(final ItemNotification itemNotification) {
        if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_NOTICE)) {
            if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_HEALTH_ABNORMAL) && Util.getAlarmCount() == 0) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_notification, null);
                LinearLayout llContainer = popupView.findViewById(R.id.ID_LL_CONTAINTER);
                TextView tvTitle = popupView.findViewById(R.id.ID_TXTVIEW_TITLE);
                TextView tvStatus = popupView.findViewById(R.id.ID_TXTVIEW_STATS);
                TextView tvTime = popupView.findViewById(R.id.ID_TXTVIEW_TIME);
                TextView tvContent = popupView.findViewById(R.id.ID_TXTVIEW_CONTENT);

                tvTitle.setText(getString(R.string.str_notice_title));
                tvStatus.setText(getString(R.string.str_unread_status));
                tvTime.setText(Util.getDateTimeFormatString(new Date(itemNotification.time)));
                tvContent.setText(itemNotification.alert);

                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setOnDismissListener(() -> popupWindow = null);

                llContainer.setOnClickListener(view -> {
                    HttpAPI.readNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            itemNotification.isRead = 1;
                            Util.updateNotificationEntry(itemNotification, itemNotification);
                        }

                        @Override
                        public void onError(Object error) {

                        }
                    }, this.getClass().getSimpleName());

                    Intent intent = new Intent(ActivityBase.this, ActivityMain.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    ActivityMain.IS_ABNORMAL_SET = true;
                    startActivity(intent);

                    popupWindow.dismiss();
                });

                popupWindow.setAnimationStyle(R.style.popup_window_animation);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.update();

                // Show popup window offset 1,1 to phoneBtton at the screen center.
                popupWindow.showAtLocation(m_rootView, Gravity.TOP, 0, 90);

                new Handler().postDelayed(popupWindow::dismiss, 5000);
            }
            else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_LOW_BATTERY) ||
                    itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_FULL_BATTERY) ||
                    itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_CONNECT) ||
                    itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_DISCONNECT) ||
                    itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_ON) ||
                    itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_OFF) ||
                    itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_BIRTHDAY) ||
                    itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_HEALTH)||
                    itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_ALARM_OFF) ||
                    itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_UPDATE_ALARM_INFO)) {
                if (Util.getAlarmCount() == 0) {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                    @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_notification, null);
                    LinearLayout llContainer = popupView.findViewById(R.id.ID_LL_CONTAINTER);
                    TextView tvTitle = popupView.findViewById(R.id.ID_TXTVIEW_TITLE);
                    TextView tvStatus = popupView.findViewById(R.id.ID_TXTVIEW_STATS);
                    TextView tvTime = popupView.findViewById(R.id.ID_TXTVIEW_TIME);
                    TextView tvContent = popupView.findViewById(R.id.ID_TXTVIEW_CONTENT);

                    tvTitle.setText(getString(R.string.str_notice_title));
                    tvStatus.setText(getString(R.string.str_unread_status));
                    tvTime.setText(Util.getDateTimeFormatString(new Date(itemNotification.time)));
                    tvContent.setText(itemNotification.alert);

                    popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setOnDismissListener(() -> popupWindow = null);

                    popupWindow.setAnimationStyle(R.style.popup_window_animation);
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popupWindow.setFocusable(true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.update();

                    // Show popup window offset 1,1 to phoneBtton at the screen center.
                    popupWindow.showAtLocation(m_rootView, Gravity.TOP, 0, 90);

                    new Handler().postDelayed(popupWindow::dismiss, 5000);
                    llContainer.setOnClickListener(view -> {
                        HttpAPI.readNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                itemNotification.isRead = 1;
                                Util.updateNotificationEntry(itemNotification, itemNotification);
                            }

                            @Override
                            public void onError(Object error) {

                            }
                        }, this.getClass().getSimpleName());

                        popupWindow.dismiss();
                    });
                }

                ItemWatchInfo itemWatchInfo = Util.findWatchEntry(itemNotification.deviceSerial);
                if (itemWatchInfo != null) {
                    if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_LOW_BATTERY))
                    {
                        itemWatchInfo.chargeStatus = 10;
                    }
                    else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_FULL_BATTERY)){
                        itemWatchInfo.chargeStatus = 90;
                    }
                    else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_CONNECT))
                    {
                        itemWatchInfo.netStatus = true;
                    }
                    else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_DISCONNECT)){
                        itemWatchInfo.netStatus = false;
                    }
                    else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_OFF)){
                        itemWatchInfo.takeOnStatus = false;
                    }
                    else if (itemNotification.noticeType.equals(ItemNotification.NOTICE_TYPE_TAKE_ON)) {
                        itemWatchInfo.takeOnStatus = true;
                    }
                    Util.updateWatchEntry(itemWatchInfo, itemWatchInfo);
                }
            }
        }
        else if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_ALARM) && Util.getAlarmCount() == 1){
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            int alarmId = itemNotification.alarmId;

            HttpAPI.getAlarmDetail(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), alarmId, new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int iRetCode = jsonObject.getInt("retcode");
                        if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                            return;
                        }

                        String strData = jsonObject.getString("data");
                        JSONObject dataObject = new JSONObject(strData);
                        JSONObject infoObject = dataObject.optJSONObject("info");
                        if (infoObject != null) {
                            @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_notification, null);
                            LinearLayout llContainer = popupView.findViewById(R.id.ID_LL_CONTAINTER);
                            TextView tvTitle = popupView.findViewById(R.id.ID_TXTVIEW_TITLE);
                            TextView tvStatus = popupView.findViewById(R.id.ID_TXTVIEW_STATS);
                            TextView tvTime = popupView.findViewById(R.id.ID_TXTVIEW_TIME);
                            TextView tvContent = popupView.findViewById(R.id.ID_TXTVIEW_CONTENT);

                            tvTitle.setText(R.string.str_alarm_title);
                            tvTitle.setTextColor(getColor(R.color.color_red));
                            tvStatus.setText(R.string.str_progress_status);
                            tvStatus.setTextColor(getColor(R.color.color_red));

                            tvTitle.setText(getString(R.string.str_app_name));
                            tvTime.setText(Util.getDateTimeFormatString(new Date(itemNotification.time)));
                            tvContent.setText(infoObject.optString("title"));

                            popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            popupWindow.setOnDismissListener(() -> popupWindow = null);

                            llContainer.setOnClickListener(view -> {
                                HttpAPI.readNotification(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemNotification.id, new VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        itemNotification.isRead = 1;
                                        Util.updateNotificationEntry(itemNotification, itemNotification);
                                    }

                                    @Override
                                    public void onError(Object error) {

                                    }
                                }, ActivityMain.class.getSimpleName());

                                Intent intent = new Intent(ActivityBase.this, ActivitySOSHelp.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra("notification_data", itemNotification);
                                intent.putExtra("alarm_data", strData);
                                startActivity(intent);

                                if (popupWindow != null) {
                                    popupWindow.dismiss();
                                }
                            });

                            popupWindow.setAnimationStyle(R.style.popup_window_animation);
                            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            popupWindow.setFocusable(false);
                            popupWindow.setOutsideTouchable(false);
                            popupWindow.update();

                            // Show popup window offset 1,1 to phoneBtton at the screen center.
                            popupWindow.showAtLocation(m_rootView, Gravity.TOP, 0, 250);
                        }
                    }
                    catch (JSONException ignored) {
                    }
                }

                @Override
                public void onError(Object error) {

                }
            }, getTag());
        }
        else if (itemNotification.type.equals(ItemNotification.PUSH_TYPE_CLOCK) && Util.getAlarmCount() == 0) {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            @SuppressLint("InflateParams") View popupView = getLayoutInflater().inflate(R.layout.popup_notification, null);
            LinearLayout llContainer = popupView.findViewById(R.id.ID_LL_CONTAINTER);
            TextView tvTitle = popupView.findViewById(R.id.ID_TXTVIEW_TITLE);
            TextView tvStatus = popupView.findViewById(R.id.ID_TXTVIEW_STATS);
            TextView tvTime = popupView.findViewById(R.id.ID_TXTVIEW_TIME);
            TextView tvContent = popupView.findViewById(R.id.ID_TXTVIEW_CONTENT);

            tvTitle.setText(getString(R.string.str_notice_title));
            tvStatus.setText(getString(R.string.str_unread_status));
            tvTime.setText(Util.getDateTimeFormatString(new Date(itemNotification.time)));
            tvContent.setText(itemNotification.deviceSerial);

            final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            llContainer.setOnClickListener(view -> {
                IOTDBHelper iotdbHelper = new IOTDBHelper(ActivityBase.this);
                itemNotification.isRead = 1;
                iotdbHelper.updateNotificationEntry(itemNotification, itemNotification);

                Intent intent = new Intent(ActivityBase.this, ActivityAlarmSet.class);
                startActivity(intent);

                popupWindow.dismiss();
            });

            popupWindow.setAnimationStyle(R.style.popup_window_animation);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.update();

            // Show popup window offset 1,1 to phoneBtton at the screen center.
            popupWindow.showAtLocation(m_rootView, Gravity.TOP, 0, 90);

            new Handler().postDelayed(popupWindow::dismiss, 5000);
        }
    }
}
