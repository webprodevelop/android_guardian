//@formatter:off
package com.iot.shoumengou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderInfo;
import com.iot.shoumengou.model.ItemDiscover;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterInfo extends ArrayAdapter<ItemDiscover> {
    private final String TAG = "AdapterInfo";

    ArrayList<ItemDiscover> infoList;
    public AdapterInfo(Context context, ArrayList<ItemDiscover> infos) {
        super(context, 0, infos);

        infoList = infos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemDiscover itemInfo = getItem(position);

        HolderInfo viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_info, parent, false);
            viewHolder = new HolderInfo(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderInfo) convertView.getTag();
        }

        if (itemInfo.picture != null && !itemInfo.picture.isEmpty()) {
            Picasso.get().load(itemInfo.picture).placeholder(R.drawable.ic_image_not_supported_24).into(viewHolder.ivInfoImage);
        } else {
            viewHolder.ivInfoImage.setImageResource(R.drawable.ic_image_not_supported_24);
        }
        viewHolder.tvInfoTitle.setText(itemInfo.title);

        return convertView;
    }
}