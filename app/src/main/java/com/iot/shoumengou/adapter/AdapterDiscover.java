//@formatter:off
package com.iot.shoumengou.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.R;
import com.iot.shoumengou.holder.HolderDiscover;
import com.iot.shoumengou.model.ItemDiscover;
import com.iot.shoumengou.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterDiscover extends ArrayAdapter<ItemDiscover> {
    public AdapterDiscover(Context context, ArrayList<ItemDiscover> discovers) {
        super(context, 0, discovers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemDiscover item = getItem(position);

        HolderDiscover viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_discover, parent, false);
            viewHolder = new HolderDiscover(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderDiscover) convertView.getTag();
        }

        viewHolder.tvKind.setText(item.newsType);
        viewHolder.tvTitle.setText(item.title);
        viewHolder.tvTime.setText(item.updatedDateStr);
        viewHolder.tvContent.setText(Html.fromHtml(Util.extractTextFromHtmlContent(item.content)));

        if (item.picture != null && !item.picture.isEmpty())
            Picasso.get().load(item.picture).into(viewHolder.ivPic);

        return convertView;
    }
}