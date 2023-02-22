//@formatter:off
package com.iot.shoumengou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iot.shoumengou.R;

import java.util.ArrayList;

public class AdapterVolunteer extends BaseAdapter {
    private final Context m_context;
    private final ArrayList<String> m_volunteerList;

    public AdapterVolunteer(Context context, ArrayList<String> list) {
        m_context = context;
        m_volunteerList = list;
    }

    @Override
    public int getCount() {
        if (m_volunteerList == null)
            return 0;
        return m_volunteerList.size();
    }

    @Override
    public Object getItem(int position) {
        if (m_volunteerList == null)
            return null;
        if (position >= m_volunteerList.size())
            return null;
        return m_volunteerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtTitle;

        if (position >= m_volunteerList.size())
            return null;

        if (convertView == null) {
            convertView = LayoutInflater.from(m_context).inflate(R.layout.item_volunteer, parent, false);
        }

        txtTitle = convertView.findViewById(R.id.ID_TEXT_TITLE);
        txtTitle.setText(m_volunteerList.get(position));

        return convertView;
    }
}
