//@formatter:off
package com.iot.shoumengou.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderFollowService;
import com.iot.shoumengou.model.ItemPriceList;

import java.util.ArrayList;

public class AdapterFollowService extends ArrayAdapter<ItemPriceList> {
    private final String TAG = "AdapterFollowService";
    private Activity context;

    ArrayList<ItemPriceList> infoList;
    public AdapterFollowService(Activity context, ArrayList<ItemPriceList> infos) {
        super(context, 0, infos);
        this.context = context;
        infoList = infos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemPriceList itemInfo = getItem(position);

        HolderFollowService viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_follow_service, parent, false);
            viewHolder = new HolderFollowService(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderFollowService) convertView.getTag();
        }

        viewHolder.tvServiceName.setText(itemInfo.label);
        viewHolder.tvServiceTerm1.setText(itemInfo.priceList.get(0).label);
        viewHolder.tvServicePrice1.setText(itemInfo.priceList.get(0).value + context.getString(R.string.str_rmb));
        viewHolder.tvServiceTerm2.setText(itemInfo.priceList.get(1).label);
        viewHolder.tvServicePrice2.setText(itemInfo.priceList.get(1).value + context.getString(R.string.str_rmb));
        viewHolder.tvServiceTerm3.setText(itemInfo.priceList.get(2).label);
        viewHolder.tvServicePrice3.setText(itemInfo.priceList.get(2).value + context.getString(R.string.str_rmb));
        viewHolder.tvServiceTerm4.setText(itemInfo.priceList.get(3).label);
        viewHolder.tvServicePrice4.setText(itemInfo.priceList.get(3).value + context.getString(R.string.str_rmb));
        viewHolder.tvServiceTerm5.setText(itemInfo.priceList.get(4).label);
        viewHolder.tvServicePrice5.setText(itemInfo.priceList.get(4).value + context.getString(R.string.str_rmb));

        return convertView;
    }
}