package com.iot.shoumengou.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityDiscoverDetail;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.model.ItemDiscover;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class MainAdapter extends RecyclerView.Adapter<com.iot.shoumengou.adapter.MainAdapter.ViewHolder> {
    private ArrayList<ItemDiscover> mainInfoList;
    private String tag;
    private Fragment context;

    public MainAdapter(Fragment fragment, ArrayList<ItemDiscover> mainInfoList, String tag) {
        super();
        context = fragment;
        this.mainInfoList = mainInfoList;
        this.tag = tag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main, viewGroup, false);

        return new ViewHolder(v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final ItemDiscover itemRecommended = mainInfoList.get(i);

        viewHolder.ivVideo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (itemRecommended.picture != null && !itemRecommended.picture.isEmpty()) {
            Picasso.get().load(itemRecommended.picture).placeholder(R.drawable.ic_image_not_supported_24).into(viewHolder.ivVideo);
        }
        else {
//            viewHolder.ivVideo.setScaleType(ImageView.ScaleType.FIT_XY);
            viewHolder.ivVideo.setImageDrawable(viewHolder.ivVideo.getContext().getDrawable(R.drawable.ic_image_not_supported_24));
        }

        viewHolder.tvTitle.setText(itemRecommended.title);

        viewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context.getActivity(), ActivityDiscoverDetail.class);
            intent.putExtra("discover_data", itemRecommended);
            Objects.requireNonNull(context.getActivity()).startActivityForResult(intent, ActivityMain.REQUEST_DISCOVER_DETAIL);
        });
    }

    @Override
    public int getItemCount() {
        if(mainInfoList != null)
            return mainInfoList.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView ivVideo;
        TextView tvTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ivVideo = itemView.findViewById(R.id.ID_IMG_INFO);
            tvTitle = itemView.findViewById(R.id.ID_TEXT_INFO);
        }
    }
}
