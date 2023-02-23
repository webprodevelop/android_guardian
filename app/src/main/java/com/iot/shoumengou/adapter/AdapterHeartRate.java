//@formatter:off
package com.iot.shoumengou.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderHeartRate;
import com.iot.shoumengou.model.ItemHeartRate;
import com.iot.shoumengou.util.Util;

import java.util.ArrayList;
import java.util.Date;

public class AdapterHeartRate extends ArrayAdapter<ItemHeartRate> {
    ArrayList<ItemHeartRate>     heartRateList;
    public static final int MAX_PRESSURE = 280;
    public static final int MAX_HEART_RATE = 220;
    public static final int MAX_TEMPERATURE = 43;

    private int valueMask = 0;

    public AdapterHeartRate(Context context, ArrayList<ItemHeartRate> heartRates) {
        super(context, 0, heartRates);

        heartRateList = heartRates;
    }

    public void setValueMask(int valueMask) {
        this.valueMask = valueMask;
        notifyDataSetChanged();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHeartRate item = getItem(position);

        HolderHeartRate viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_heart_rate, parent, false);
            viewHolder = new HolderHeartRate(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderHeartRate) convertView.getTag();
        }

        viewHolder.tvSurveyDate.setText(item.checkDate);
        viewHolder.tvSurveyTime.setText(Util.getTimeFormatStringIgnoreLocale(new Date(item.checkTime)));

        double realValue = item.heartRate;
        float low = 0;
        float high = 0;
        if (Util.monitoringWatch != null) {
            low = Util.monitoringWatch.heart_rate_low_limit;
            high = Util.monitoringWatch.heart_rate_high_limit;

            if (valueMask == 1) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    realValue = item.highBloodPressure;
                    low = Util.monitoringWatch.blood_pressure_high_left_limit;
                    high = Util.monitoringWatch.blood_pressure_high_right_limit;
                    SpannableString spannableString = new SpannableString(realValue <= 0 ? getContext().getString(R.string.str_minus_value) : String.format("%d", (int)Math.round(realValue)));
                    if (realValue > MAX_PRESSURE) {
                        spannableString = new SpannableString("--");
                        spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                                0,
                                spannableString.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else {
                        if (realValue >= low && realValue <= high) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                spannableString.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.color_text_list_item)),
                                        0,
                                        spannableString.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        } else {
                            spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                                    0,
                                    spannableString.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    spannableStringBuilder.append(spannableString);

                    realValue = item.lowBloodPressure;
                    low = Util.monitoringWatch.blood_pressure_low_left_limit;
                    high = Util.monitoringWatch.blood_pressure_low_right_limit;
                    spannableString = new SpannableString(" / " + (realValue <= 0 ? getContext().getString(R.string.str_minus_value) : String.format("%d", (int)Math.round(realValue))));
                    if (realValue > MAX_PRESSURE) {
                        spannableString = new SpannableString("--");
                        spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                                0,
                                spannableString.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else {
                        if (realValue >= low && realValue <= high) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                spannableString.setSpan(new ForegroundColorSpan(getContext().getColor(R.color.color_text_list_item)),
                                        0,
                                        spannableString.length(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        } else {
                            spannableString.setSpan(new ForegroundColorSpan(Color.RED),
                                    0,
                                    spannableString.length(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                    spannableStringBuilder.append(spannableString);

                    viewHolder.tvHeartRate.setText(spannableStringBuilder);
            } else if (valueMask == 2) {
                realValue = Double.parseDouble(String.format("%.1f", item.temperature));
                low = Util.monitoringWatch.temperature_low_limit;
                high = Util.monitoringWatch.temperature_high_limit;
            }
        }

        if (valueMask != 1) {
            boolean isOver = false;
            if (valueMask == 2) {
                if (realValue > MAX_TEMPERATURE) {
                    isOver = true;
                    viewHolder.tvHeartRate.setText("--");
                }
                else {
                    viewHolder.tvHeartRate.setText(realValue <= 0 ? getContext().getString(R.string.str_minus_value) :  String.valueOf(realValue));
                }
            } else {
                if (realValue > MAX_HEART_RATE) {
                    isOver = true;
                    viewHolder.tvHeartRate.setText("--");
                }
                else {
                    viewHolder.tvHeartRate.setText(realValue <= 0 ? getContext().getString(R.string.str_minus_value) : String.format("%d", (int) Math.round(realValue)));
                }
            }

            if (realValue >= low && realValue <= high && !isOver) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    viewHolder.tvHeartRate.setTextColor(getContext().getColor(R.color.color_text_list_item));
                }
            } else {
                viewHolder.tvHeartRate.setTextColor(Color.RED);
            }
        }
        if (position % 2 == 0) {
            viewHolder.llItemRoot.setBackgroundColor(Color.WHITE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                viewHolder.llItemRoot.setBackgroundColor(getContext().getColor(R.color.color_list_item));
            }
        }

        return convertView;
    }
}