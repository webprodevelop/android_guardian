//@formatter:off
package com.iot.shoumengou.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderArea;
import com.iot.shoumengou.model.ItemArea;

import java.util.ArrayList;

public class AdapterArea extends ArrayAdapter<ItemArea> {
    ArrayList<ItemArea>     areaList;
    public AdapterArea(Context context, ArrayList<ItemArea> areaList) {
        super(context, 0, areaList);

        this.areaList = areaList;
    }

    @Override
    public int getCount() {
        return areaList.size();
    }

    @Nullable
    @Override
    public ItemArea getItem(int position) {
        return areaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemArea item = getItem(position);

        HolderArea viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_area, parent, false);
            viewHolder = new HolderArea(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderArea) convertView.getTag();
        }

        if (item.areaName != null) {
            viewHolder.tvName.setText(item.areaName);
        }
        else {
            viewHolder.tvName.setText("");
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ItemArea item = getItem(position);

        HolderArea viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_area, parent, false);
            viewHolder = new HolderArea(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderArea) convertView.getTag();
        }

        if (item.areaName != null) {
            viewHolder.tvName.setText(item.areaName);
        }
        else {
            viewHolder.tvName.setText("");
        }
        return convertView;
    }
}