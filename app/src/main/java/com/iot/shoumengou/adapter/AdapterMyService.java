//@formatter:off
package com.iot.shoumengou.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderMyService;
import com.iot.shoumengou.model.MyServiceInfo;

import java.util.ArrayList;

public class AdapterMyService extends ArrayAdapter<MyServiceInfo> {
    private final String TAG = "AdapterMyService";
    private Activity context;

    ArrayList<MyServiceInfo> infoList;
    public AdapterMyService(Activity context, ArrayList<MyServiceInfo> infos) {
        super(context, 0, infos);
        this.context = context;
        infoList = infos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyServiceInfo itemInfo = getItem(position);

        HolderMyService viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_my_service, parent, false);
            viewHolder = new HolderMyService(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderMyService) convertView.getTag();
        }

        viewHolder.tvServiceName.setText(itemInfo.userName);
        viewHolder.tvServiceTerm.setText(itemInfo.serviceYears + context.getString(R.string.str_year));
        viewHolder.tvServiceDate.setText(String.format(context.getString(R.string.str_term), itemInfo.serviceStart, itemInfo.serviceEnd));

        return convertView;
    }
}