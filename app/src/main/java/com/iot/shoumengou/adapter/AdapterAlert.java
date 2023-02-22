//@formatter:off
package com.iot.shoumengou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iot.shoumengou.R;
import com.iot.shoumengou.model.ItemAlert;

import java.util.ArrayList;

public class AdapterAlert extends BaseAdapter {

    private Context m_context;
    private ArrayList<ItemAlert> m_arrAlert;

    public AdapterAlert(Context a_context) {
        m_context = a_context;
    }

    public void SetAlertData(ArrayList<ItemAlert> a_arrAlert) {
        m_arrAlert = a_arrAlert;
    }

    @Override
    public int getCount() {
        if (m_arrAlert == null)
            return 0;
        return m_arrAlert.size();
    }

    @Override
    public Object getItem(int position) {
        if (m_arrAlert == null)
            return null;
        if (position >= m_arrAlert.size())
            return null;
        return m_arrAlert.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtDate;
        TextView txtGroup;
        TextView txtDevice;
        TextView txtAlert;
        ItemAlert itemAlert;

        if (position >= m_arrAlert.size())
            return null;

        if (convertView == null) {
            convertView = LayoutInflater.from(m_context).inflate(R.layout.item_alert, parent, false);
        }

        if (position % 2 == 0)
            convertView.setBackgroundColor(m_context.getResources().getColor(R.color.color_bg_odd));
        else
            convertView.setBackgroundColor(m_context.getResources().getColor(R.color.color_bg_even));

        txtDate = convertView.findViewById(R.id.ID_TXTVIEW_DATE);
        txtGroup = convertView.findViewById(R.id.ID_TXTVIEW_GROUP);
        txtDevice = convertView.findViewById(R.id.ID_TXTVIEW_DEVICE);
        txtAlert = convertView.findViewById(R.id.ID_TXTVIEW_ALERT);

        itemAlert = m_arrAlert.get(position);
        txtDate.setText(itemAlert.GetDate());
        txtGroup.setText(itemAlert.GetGroup());
        txtDevice.setText(itemAlert.GetDeviceType() + "-" + itemAlert.GetDeviceSn());
        txtAlert.setText(itemAlert.GetAlert());

        return convertView;
    }
}
