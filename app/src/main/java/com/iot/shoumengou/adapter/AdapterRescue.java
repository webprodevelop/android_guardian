//@formatter:off
package com.iot.shoumengou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderRescue;
import com.iot.shoumengou.model.ItemRescue;

import java.util.ArrayList;

public class AdapterRescue extends ArrayAdapter<ItemRescue> {
    ArrayList<ItemRescue>     rescueList;
    public AdapterRescue(Context context, ArrayList<ItemRescue> heartRates) {
        super(context, 0, heartRates);

        rescueList = heartRates;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemRescue item = getItem(position);

        HolderRescue viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_rescue, parent, false);
            viewHolder = new HolderRescue(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderRescue) convertView.getTag();
        }

        viewHolder.tvSurveyName.setText(item.label);
        viewHolder.tvSurveyTime.setText(item.rescueTime);
        return convertView;
    }
}