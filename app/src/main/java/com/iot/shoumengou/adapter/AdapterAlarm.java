//@formatter:off
package com.iot.shoumengou.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;

import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityAlarmSet;
import com.iot.shoumengou.holder.HolderAlarm;
import com.iot.shoumengou.model.ItemAlarm;
import com.iot.shoumengou.receiver.AlarmReceiver;
import com.iot.shoumengou.util.AlarmDataSource;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.DateTime;

public class AdapterAlarm extends BaseAdapter {
    private final String TAG = "AdapterAlarm";

    ActivityAlarmSet activity;
    private AlarmDataSource mDataSource;
    private DateTime mDateTime;

    public AdapterAlarm(ActivityAlarmSet activity) {
        this.activity = activity;
        mDataSource = AlarmDataSource.getInstance(activity);
        mDateTime = new DateTime(activity);
        dataSetChanged();
    }

    private void dataSetChanged() {
        for (int i = 0; i < mDataSource.size(); i++)
            setAlarm(mDataSource.get(i));

        activity.updateNoAlarms(mDataSource.size() == 0);
        notifyDataSetChanged();
    }

    public void save() {
        mDataSource.save();
    }

    public void update(ItemAlarm alarm) {
        mDataSource.update(alarm);
        dataSetChanged();
    }

    public void updateAlarms() {
        for (int i = 0; i < mDataSource.size(); i++)
            mDataSource.update(mDataSource.get(i));
        dataSetChanged();
    }

    public void add(ItemAlarm alarm) {
        mDataSource.add(alarm);
        dataSetChanged();
    }

    public void delete(int index) {
        cancelAlarm(mDataSource.get(index));
        mDataSource.remove(index);
        dataSetChanged();
    }

    public int getCount() {
        return mDataSource.size();
    }

    public ItemAlarm getItem(int position) {
        return mDataSource.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemAlarm item = getItem(position);

        HolderAlarm viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            convertView = inflater.inflate(R.layout.item_alarm, parent, false);
            viewHolder = new HolderAlarm(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderAlarm) convertView.getTag();
        }

        viewHolder.swOnOff.setChecked(item.enabled);
        viewHolder.tvAlarmTime.setText(mDateTime.formatTime(item));
        viewHolder.tvAlarmName.setText(item.title);
        viewHolder.tvAlarmContent.setText(item.content);
        viewHolder.tvAlarmPeriod.setText(mDateTime.formatDays(activity, item));

        viewHolder.swOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                item.enabled = checked;
                if (checked) {
                    setAlarm(item);
                } else {
                    cancelAlarm(item);
                }
            }
        });

        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onDeleteAlarm(position);
            }
        });
        viewHolder.tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onEditAlarm(item);
            }
        });

        return convertView;
    }

    private void setAlarm(ItemAlarm alarm) {
        PendingIntent sender;
        Intent intent;

        if (alarm.enabled && !alarm.getOutdated()) {
            AlarmManager mAlarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);

            intent = new Intent(activity, AlarmReceiver.class);
            intent.setAction(AppConst.ACTION_ALARM);
            alarm.toIntent(intent);
            sender = PendingIntent.getBroadcast(activity.getApplicationContext(), (int)alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.date, sender);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.date, sender);
            }
        }
    }

    private void cancelAlarm(ItemAlarm alarm) {
        PendingIntent sender;
        Intent intent;

        AlarmManager mAlarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(activity, AlarmReceiver.class);
        intent.setAction(AppConst.ACTION_ALARM);
        alarm.toIntent(intent);
        sender = PendingIntent.getBroadcast(activity.getApplicationContext(), (int)alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(sender);
    }
}