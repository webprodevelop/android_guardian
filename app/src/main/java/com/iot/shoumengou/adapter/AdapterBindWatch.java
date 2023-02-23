//@formatter:off
package com.iot.shoumengou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderBindWatch;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import java.util.ArrayList;

public class AdapterBindWatch extends ArrayAdapter<ItemWatchInfo> {
    ArrayList<ItemWatchInfo>     bindWatchList;
    public AdapterBindWatch(Context context, ArrayList<ItemWatchInfo> bindWatches) {
        super(context, 0, bindWatches);

        bindWatchList = bindWatches;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemWatchInfo item = getItem(position);

        HolderBindWatch viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_bind_watch, parent, false);
            viewHolder = new HolderBindWatch(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderBindWatch) convertView.getTag();
        }

        viewHolder.tvUser.setText(item.name);
        if (item.serial.equals(Prefs.Instance().getMoniteringWatchSerial())) {
            viewHolder.tvCotrol.setBackgroundResource(R.drawable.selector_small_green_button_fill);
        } else {
            viewHolder.tvCotrol.setBackgroundResource(R.drawable.selector_small_button_fill);
        }

        viewHolder.tvCotrol.setOnClickListener(view -> {
            if (!item.serial.equals(Prefs.Instance().getMoniteringWatchSerial())) {
                Util.setMoniteringWatchInfo(item);

                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
