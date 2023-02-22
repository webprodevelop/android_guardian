package com.iot.shoumengou.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityRescueQuery;

import java.util.ArrayList;

public class PaginationAdapter extends RecyclerView.Adapter<com.iot.shoumengou.adapter.PaginationAdapter.ViewHolder> {
    private ArrayList<String> paginationList;
    private String tag;
    private Activity context;
    private int currentPageNumber = 1;
    private static final int eachSize = 1;

    public PaginationAdapter(Activity activity, ArrayList<String> paginationList, String tag) {
        super();
        context = activity;
        this.paginationList = paginationList;
        this.tag = tag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pagination, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.tvPageNumber.setText(paginationList.get(i));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final String pageInfo = paginationList.get(i);

        viewHolder.tvPageNumber.setText(String.valueOf(pageInfo));

        if(viewHolder.tvPageNumber.getText().toString().equals(String.valueOf(currentPageNumber))) {
            viewHolder.tvPageNumber.setBackgroundResource(R.drawable.shape_small_blue_button_fill_normal);
        }
        else {
            viewHolder.tvPageNumber.setBackgroundResource(R.drawable.shape_normal_oval);
        }

        viewHolder.itemView.setOnClickListener(v -> {
            String pageNumber = paginationList.get(i);
            if (pageNumber.equals("<")) {
                currentPageNumber -= 1;
            }
            else if (pageNumber.equals(">")) {
                currentPageNumber += 1;
            }
            else if (pageNumber.equals("...")) {
                if (currentPageNumber == paginationList.size() - (eachSize + 2)) {
                    currentPageNumber = paginationList.size() - (2 * eachSize) - 3;
                }
                else if (currentPageNumber < eachSize + 3) {
                    currentPageNumber = 2 * eachSize + 4;
                }
                else {
                    if (i > 2) {
                        currentPageNumber += eachSize + 1;
                    }
                    else {
                        currentPageNumber -= eachSize + 1;
                    }
                }
            }
            else {
                currentPageNumber = Integer.parseInt(pageNumber);
            }

            if (context instanceof ActivityRescueQuery) {
                ((ActivityRescueQuery) context).rebuildPagination(currentPageNumber);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(paginationList != null)
            return paginationList.size();
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvPageNumber;

        ViewHolder(View itemView) {
            super(itemView);
            tvPageNumber = itemView.findViewById(R.id.ID_TEXT_NUMBER);
        }
    }
}