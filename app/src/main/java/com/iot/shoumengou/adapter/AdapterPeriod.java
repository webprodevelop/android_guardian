//@formatter:off
package com.iot.shoumengou.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;

import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderPeriod;
import com.iot.shoumengou.model.ItemFencePeriod;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterPeriod extends ArrayAdapter<ItemFencePeriod> {
    ArrayList<ItemFencePeriod>     periodList;
    public AdapterPeriod(Context context, ArrayList<ItemFencePeriod> periods) {
        super(context, 0, periods);

        periodList = periods;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemFencePeriod item = getItem(position);

        final HolderPeriod viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_period, parent, false);
            viewHolder = new HolderPeriod(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderPeriod) convertView.getTag();
        }

        viewHolder.tvStartTime.setText(Html.fromHtml(item.getStartTimeText(getContext())));
        viewHolder.tvEndTime.setText(Html.fromHtml(item.getEndTimeText(getContext())));

        viewHolder.tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                if (item.startTime != 0) {
                    calendar.set(Calendar.HOUR_OF_DAY, item.startTime / 60);
                    calendar.set(Calendar.MINUTE, item.startTime / 60);
                }
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        ItemFencePeriod prevFencePeriod = null;
                        if (position > 0) {
                            prevFencePeriod = getItem(position - 1);
                        }

                        int startTime = selectedHour * 60 + selectedMinute;
                        if (prevFencePeriod != null && (prevFencePeriod.endTime >= startTime)) {
                            return;
                        }

                        item.startTime = startTime;

                        viewHolder.tvStartTime.setText(Html.fromHtml(item.getStartTimeText(getContext())));
                    }
                }, hour, minute, true);
                timePicker.show();
            }
        });

        viewHolder.tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                if (item.endTime != 0) {
                    calendar.set(Calendar.HOUR_OF_DAY, item.endTime / 60);
                    calendar.set(Calendar.MINUTE, item.endTime / 60);
                }
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        ItemFencePeriod prevFencePeriod = null;
                        if (position > 0) {
                            prevFencePeriod = getItem(position - 1);
                        }

                        int endTime = selectedHour * 60 + selectedMinute;
                        if (prevFencePeriod != null && (prevFencePeriod.endTime >= endTime)) {
                            return;
                        }

                        if (item.startTime >= endTime) {
                            return;
                        }

                        item.endTime = endTime;

                        viewHolder.tvEndTime.setText(Html.fromHtml(item.getEndTimeText(getContext())));
                    }
                }, hour, minute, true);//Yes 24 hour time
                timePicker.show();
            }
        });

        return convertView;
    }
}