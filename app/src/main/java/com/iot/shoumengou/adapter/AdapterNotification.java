//@formatter:off
package com.iot.shoumengou.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityNotification;
import com.iot.shoumengou.holder.HolderNotification;
import com.iot.shoumengou.model.ItemNotification;
import com.iot.shoumengou.util.Util;

import java.util.ArrayList;
import java.util.Date;

public class AdapterNotification extends ArrayAdapter<ItemNotification> {
    ArrayList<ItemNotification>     notificationList;
    public AdapterNotification(Context context, ArrayList<ItemNotification> notifications) {
        super(context, 0, notifications);

        notificationList = notifications;
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemNotification item = getItem(position);

        HolderNotification viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_notification, parent, false);
            viewHolder = new HolderNotification(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderNotification) convertView.getTag();
        }

        if (item.isRead == 0) {
            viewHolder.tvTitle.setTextColor(Color.BLACK);
            viewHolder.tvStatus.setTextColor(Color.BLACK);
            viewHolder.tvTime.setTextColor(Color.BLACK);
            viewHolder.tvContent.setTextColor(Color.BLACK);
        }
        else {
            viewHolder.tvTitle.setTextColor(Color.GRAY);
            viewHolder.tvStatus.setTextColor(Color.GRAY);
            viewHolder.tvTime.setTextColor(Color.GRAY);
            viewHolder.tvContent.setTextColor(Color.GRAY);
        }
        viewHolder.tvView.setBackgroundResource(R.drawable.selector_small_red_button_fill);
        viewHolder.tvView.setText(R.string.str_delete);

        if (item.type.equals(ItemNotification.PUSH_TYPE_ALARM)) {
            viewHolder.tvTitle.setText(R.string.str_alarm_title);
            if (item.alarmStatus == 0 || item.alarmStatus == 2) {
                viewHolder.ivIcon.setImageDrawable(viewHolder.ivIcon.getContext().getDrawable(R.drawable.sos_active));
                Animation myFadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.tween);
                viewHolder.ivIcon.startAnimation(myFadeInAnimation);
                viewHolder.tvTitle.setTextColor(viewHolder.ivIcon.getContext().getColor(R.color.color_red));
                viewHolder.tvStatus.setText(R.string.str_progress_status);
                viewHolder.tvStatus.setTextColor(viewHolder.ivIcon.getContext().getColor(R.color.color_red));
            } else {
                viewHolder.ivIcon.setImageDrawable(viewHolder.ivIcon.getContext().getDrawable(R.drawable.sos_inactive));
                viewHolder.tvStatus.setText(R.string.str_finish_status);
            }
        }
        else {
            if (item.noticeType.equals(ItemNotification.NOTICE_TYPE_ALARM_OFF)) {
                viewHolder.tvTitle.setText(R.string.str_alarm_title);
            }
            else {
                viewHolder.tvTitle.setText(R.string.str_notice_title);
            }
            if (item.isRead == 0) {
                viewHolder.ivIcon.setImageDrawable(viewHolder.ivIcon.getContext().getDrawable(R.drawable.notice_active));
                viewHolder.tvTitle.setTextColor(viewHolder.ivIcon.getContext().getColor(R.color.color_notification_active));
                viewHolder.tvStatus.setText(R.string.str_unread_status);
                viewHolder.tvStatus.setTextColor(viewHolder.ivIcon.getContext().getColor(R.color.color_notification_active));
            } else {
                viewHolder.ivIcon.setImageDrawable(viewHolder.ivIcon.getContext().getDrawable(R.drawable.notice_inactive));
                viewHolder.tvStatus.setText(R.string.str_read_status);
            }
        }

        viewHolder.tvTitle.setText(item.title);
        viewHolder.tvTime.setText(Util.getDateTimeFormatString(new Date(item.time)));
        viewHolder.tvContent.setText(item.alert);
        if (Util.getAlarmCount() > 0) {
            viewHolder.tvView.setEnabled(false);
        }
        else {
            viewHolder.tvView.setEnabled(true);
        }
        viewHolder.tvView.setOnClickListener(view -> {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View confirmView = layoutInflater.inflate(R.layout.alert_logout, null);

            final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

            TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
            TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
            TextView tvConfirm = confirmView.findViewById(R.id.ID_TEXT_CONFIRM);
            tvConfirm.setText(R.string.str_delete_confirm);

            btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

            btnConfirm.setOnClickListener(v -> {
                confirmDlg.dismiss();
                ((ActivityNotification) getContext()).deleteNotification(item);
            });

            confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDlg.setView(confirmView);
            confirmDlg.show();
        });

        return convertView;
    }
}
