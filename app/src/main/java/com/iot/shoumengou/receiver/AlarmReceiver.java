/**************************************************************************
 *
 * Copyright (C) 2012-2015 Alex Taradov <alex@taradov.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *************************************************************************/

package com.iot.shoumengou.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.activity.ActivityNotification;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.model.ItemAlarm;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.util.AppConst;

public class AlarmReceiver extends BroadcastReceiver
{
  private final String TAG = "AlarmReceiver";

  @Override
  public void onReceive(Context context, Intent intent)
  {
    if (intent.getAction().equals(AppConst.ACTION_ALARM)) {
      ItemAlarm alarm = new ItemAlarm();
      alarm.fromIntent(intent);

      ItemNotification itemNotification = new ItemNotification(
              alarm.id,
              ItemNotification.PUSH_TYPE_CLOCK,
              "",
              "",
              alarm.content,
              -1,
              alarm.date,
              alarm.title,
              alarm.content,
              0);
      IOTDBHelper iotdbHelper = new IOTDBHelper(context);
      iotdbHelper.addNotificationEntry(itemNotification);

      if (ActivityMain.IS_FOREGROUND || ActivityNotification.IS_FOREGROUND) {
        notifyAlarm(context, alarm);

        new Handler().postDelayed(() -> {
          Intent iPush = new Intent(AppConst.ACTION_PUSH_RECEIVED);
          iPush.putExtra("notification_data", itemNotification);
          LocalBroadcastManager.getInstance(context).sendBroadcast(iPush);
        }, 1000);
      } else {
        notifyAlarm(context, alarm);
      }
    }
  }

  private void notifyAlarm(Context context, ItemAlarm itemAlarm) {
    final String NOTIFICATION_CHANNEL_ID = "alarm_notification";

    NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

    Intent notificationIntent = new Intent(context, ActivityNotification.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(itemAlarm.title)
            .setContentText(itemAlarm.content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      builder.setSmallIcon(R.drawable.logo);
      CharSequence channelName  = "Alarm Nofification";
      int importance = NotificationManager.IMPORTANCE_DEFAULT;

      NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);

      assert notificationManager != null;
      notificationManager.createNotificationChannel(channel);

    } else builder.setSmallIcon(R.drawable.logo);

    assert notificationManager != null;
    notificationManager.notify(1234, builder.build());
  }
}

