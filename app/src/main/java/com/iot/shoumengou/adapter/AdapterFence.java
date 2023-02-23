//@formatter:off
package com.iot.shoumengou.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.iot.shoumengou.R;
import com.iot.shoumengou.fragment.FragmentLocation;
import com.iot.shoumengou.holder.HolderFence;
import com.iot.shoumengou.model.ItemFence;

import java.util.ArrayList;

public class AdapterFence extends ArrayAdapter<ItemFence> {
    ArrayList<ItemFence>        fenceList;
    FragmentLocation            fragmentLocation;

    public AdapterFence(FragmentLocation fragmentLocation, ArrayList<ItemFence> fences) {
        super(fragmentLocation.getContext(), 0, fences);

        this.fragmentLocation = fragmentLocation;
        fenceList = fences;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemFence item = getItem(position);

        HolderFence viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_electronic_fence, parent, false);
            viewHolder = new HolderFence(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderFence) convertView.getTag();
        }

        if (item.name != null) {
            viewHolder.tvFenceName.setText(item.name);
        }
        if (item.address != null) {
            viewHolder.tvFenceAddress.setText(item.address);
        }

        viewHolder.tvUpdate.setOnClickListener(view -> fragmentLocation.startFenceAddEditActvity(item, false));

        viewHolder.tvDelete.setOnClickListener(view -> {
            fragmentLocation.deleteFenceItem(position);
        });

        return convertView;
    }
}