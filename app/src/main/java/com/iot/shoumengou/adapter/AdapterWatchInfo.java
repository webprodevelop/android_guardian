//@formatter:off
package com.iot.shoumengou.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderWardInfo;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import java.util.ArrayList;

public class AdapterWatchInfo extends ArrayAdapter<ItemWatchInfo> {
    private final String TAG = "FragmentDevice";
    Activity activity;

    ArrayList<ItemWatchInfo> infoList;
    public AdapterWatchInfo(Activity context, ArrayList<ItemWatchInfo> infos) {
        super(context, 0, infos);
        activity = context;
        infoList = infos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemWatchInfo itemInfo = getItem(position);

        HolderWardInfo viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_ward_info, parent, false);
            viewHolder = new HolderWardInfo(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderWardInfo) convertView.getTag();
        }

        viewHolder.tvName.setText(itemInfo.name);
        ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
        if (monitoringWatchInfo != null && monitoringWatchInfo.serial.equals(itemInfo.serial)) {
            viewHolder.tvStatus.setBackgroundResource(R.drawable.shape_green_border_selected);
        }
        else {
            viewHolder.tvStatus.setBackgroundResource(R.drawable.shape_small_button_fill_normal);
        }

        return convertView;
    }
}
