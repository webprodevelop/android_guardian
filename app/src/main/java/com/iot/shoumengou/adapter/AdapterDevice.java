//@formatter:off
package com.iot.shoumengou.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.activity.ActivityOrderDetail;
import com.iot.shoumengou.activity.ActivitySensorInfo;
import com.iot.shoumengou.activity.ActivityServiceTerm;
import com.iot.shoumengou.activity.ActivityWatchInfo;
import com.iot.shoumengou.fragment.FragmentDevice;
import com.iot.shoumengou.holder.HolderDevice;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemDeviceInfo;
import com.iot.shoumengou.model.ItemSensorInfo;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;
import com.iot.shoumengou.view.DialogProgress;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterDevice extends ArrayAdapter<ItemDeviceInfo> {
    private final String TAG = "FragmentDevice";
    protected DialogProgress m_dlgProgress;
    public FragmentDevice mDevice;

    ArrayList<ItemDeviceInfo>     deviceList;

    public AdapterDevice(Context context, ArrayList<ItemDeviceInfo> devices) {
        super(context, 0, devices);

        m_dlgProgress = new DialogProgress(context);
        m_dlgProgress.setCancelable(false);

        deviceList = devices;
    }

    @Nullable
    @Override
    public ItemDeviceInfo getItem(int position) {
        return deviceList.get(position);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ItemDeviceInfo itemDeviceInfo = getItem(position);


        HolderDevice viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_device, parent, false);
            viewHolder = new HolderDevice(convertView);
            convertView.setTag(viewHolder); // view lookup cache stored in tag
        } else {
            viewHolder = (HolderDevice) convertView.getTag();
        }

        viewHolder.llNetState.setVisibility(View.GONE);
        viewHolder.llCharging.setVisibility(View.GONE);
//        viewHolder.llWorkState.setVisibility(View.GONE);
//        viewHolder.llAlarmState.setVisibility(View.GONE);
        viewHolder.llLabel.setVisibility(View.GONE);
        viewHolder.llLabelRelative.setVisibility(View.GONE);
        viewHolder.llPhoneNum.setVisibility(View.GONE);

        if (itemDeviceInfo.isDetail){
            viewHolder.llShort.setVisibility(View.GONE);
            viewHolder.llDetail.setVisibility(View.VISIBLE);
        }else {
            viewHolder.llDetail.setVisibility(View.GONE);
            viewHolder.llShort.setVisibility(View.VISIBLE);
        }

        if (itemDeviceInfo.isExpireDateMonthBefore()) {
            viewHolder.tvGoto.setText(R.string.str_goto);
            viewHolder.tvGoto.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), ActivityServiceTerm.class);
                intent.putExtra("device_data", itemDeviceInfo);
                ((Activity)getContext()).startActivityForResult(intent, ActivityMain.REQUEST_REFRESH_WATCH);
            });
        } else {
            viewHolder.tvGoto.setText(R.string.str_detail);
            viewHolder.tvGoto.setOnClickListener(view -> {
                Intent intent = new Intent(getContext(), ActivityOrderDetail.class);
                intent.putExtra("device_data", itemDeviceInfo);
                ((Activity)getContext()).startActivityForResult(intent, ActivityMain.REQUEST_REFRESH_WATCH);
            });
        }

        if (itemDeviceInfo.type.isEmpty()) {
            ItemWatchInfo itemWatchInfo = (ItemWatchInfo) itemDeviceInfo;
            viewHolder.tvName.setText(itemWatchInfo.name);
            viewHolder.tvRelativeLabel.setText("关系:");
            viewHolder.tvRelative.setText(itemWatchInfo.userRelation);
            viewHolder.tvSerial.setText(itemWatchInfo.serial);
            viewHolder.tvPhoneNumber.setText(itemWatchInfo.phone);
            if (itemWatchInfo.netStatus) {
                if (itemWatchInfo.takeOnStatus) {
                    viewHolder.tvNetState.setText(R.string.str_normal);
                    viewHolder.tvNetState.setTextColor(Color.GREEN);
                    viewHolder.tvStatus.setText(R.string.str_normal);
                    viewHolder.tvStatus.setTextColor(Color.GREEN);
                }
                else {
                    viewHolder.tvNetState.setText(R.string.str_no_take);
                    viewHolder.tvNetState.setTextColor(Color.RED);
                    viewHolder.tvStatus.setText(R.string.str_no_take);
                    viewHolder.tvStatus.setTextColor(Color.RED);
                }

                viewHolder.tvCharging.setText(itemWatchInfo.chargeStatus + "%");
                if (itemWatchInfo.chargeStatus > 50) {
                    viewHolder.tvCharging.setTextColor(Color.GREEN);
                } else if (itemWatchInfo.chargeStatus > 30) {
                    viewHolder.tvCharging.setTextColor(getContext().getColor(android.R.color.holo_orange_dark));
                } else {
                    viewHolder.tvCharging.setTextColor(Color.RED);
                }
            } else {
                viewHolder.tvNetState.setText(R.string.str_unconnect);
                viewHolder.tvNetState.setTextColor(Color.RED);
                viewHolder.tvStatus.setText(R.string.str_unconnect);
                viewHolder.tvStatus.setTextColor(Color.RED);

                viewHolder.tvCharging.setText(R.string.str_unknown);
                viewHolder.tvCharging.setTextColor(Color.BLACK);
            }
            viewHolder.tvServiceTerm.setText(String.format(getContext().getString(R.string.str_term), itemWatchInfo.serviceStartDate, itemWatchInfo.serviceEndDate));
            viewHolder.ivType.setImageResource(R.drawable.img_watch);
            viewHolder.llNetState.setVisibility(View.VISIBLE);
            viewHolder.llCharging.setVisibility(View.VISIBLE);
            viewHolder.llPhoneNum.setVisibility(View.VISIBLE);
        } else {
            ItemSensorInfo itemSensorInfo = (ItemSensorInfo) itemDeviceInfo;
            viewHolder.tvName.setText(itemSensorInfo.contactName);
            viewHolder.tvSerial.setText(itemSensorInfo.serial);
//            viewHolder.tvPhoneNumber.setText(itemSensorInfo.contactPhone);
            viewHolder.tvLabel.setText(itemSensorInfo.locationLabel);
            viewHolder.tvLabelRelative.setText(itemSensorInfo.labelRelative);
            viewHolder.tvRelativeLabel.setText("归属:");
            viewHolder.tvRelative.setText(itemSensorInfo.labelRelative);
            if (itemSensorInfo.netStatus) {
                if (itemSensorInfo.batteryStatus) {
                    viewHolder.tvNetState.setText(R.string.str_normal);
                    viewHolder.tvNetState.setTextColor(Color.GREEN);
                    viewHolder.tvStatus.setText(R.string.str_normal);
                    viewHolder.tvStatus.setTextColor(Color.GREEN);
                } else {
                    viewHolder.tvNetState.setText(R.string.str_battery_low);
                    viewHolder.tvNetState.setTextColor(Color.RED);
                    viewHolder.tvStatus.setText(R.string.str_battery_low);
                    viewHolder.tvStatus.setTextColor(Color.RED);
                }
            } else {
                viewHolder.tvNetState.setText(R.string.str_unconnect);
                viewHolder.tvNetState.setTextColor(Color.RED);
                viewHolder.tvStatus.setText(R.string.str_unconnect);
                viewHolder.tvStatus.setTextColor(Color.RED);
            }

            if (!itemSensorInfo.alarmStatus) {
                viewHolder.tvNetState.setText(R.string.str_alarm);
                viewHolder.tvNetState.setTextColor(Color.RED);
                viewHolder.tvStatus.setText(R.string.str_alarm);
                viewHolder.tvStatus.setTextColor(Color.RED);
            }

            viewHolder.tvServiceTerm.setText(String.format(getContext().getString(R.string.str_term), itemSensorInfo.serviceStartDate, itemSensorInfo.serviceEndDate));
            if (Util.sensorTypeMap.get(itemDeviceInfo.type) != null) {
                if (Util.sensorTypeMap.get(itemDeviceInfo.type).pictureUrl != null &&
                        !Util.sensorTypeMap.get(itemDeviceInfo.type).pictureUrl.isEmpty()) {
                    Picasso.get().load(Util.sensorTypeMap.get(itemDeviceInfo.type).pictureUrl).placeholder(R.drawable.img_firesensor).into(viewHolder.ivType);
                }
            }
            viewHolder.llNetState.setVisibility(View.VISIBLE);
            viewHolder.llLabel.setVisibility(View.VISIBLE);
            viewHolder.llLabelRelative.setVisibility(View.VISIBLE);
        }

        viewHolder.ivSetting.setOnClickListener(view -> {
            if (itemDeviceInfo.isManager) {
                Intent intent;
                if (itemDeviceInfo.type.isEmpty()) {
                    intent = new Intent(getContext(), ActivityWatchInfo.class);
                    intent.putExtra("device_data", (ItemWatchInfo)itemDeviceInfo);
                    ((Activity)getContext()).startActivityForResult(intent, ActivityMain.REQUEST_SCAN_SMART_WATCH);
                } else {
                    intent = new Intent(getContext(), ActivitySensorInfo.class);
                    intent.putExtra("device_data", (ItemSensorInfo)itemDeviceInfo);
                    ((Activity)getContext()).startActivityForResult(intent, ActivityMain.REQUEST_SCAN_FIRE_SENSOR);
                }
            } else {
                Util.showToastMessage(getContext(), R.string.str_no_permission);
            }
        });

        viewHolder.ivDelete.setOnClickListener(view -> {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View confirmView = layoutInflater.inflate(R.layout.alert_release_device, null);

            final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

            TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CANCEL);
            TextView btnConfirm = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

            btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

            btnConfirm.setOnClickListener(v -> {
                confirmDlg.dismiss();

                String serial = itemDeviceInfo.serial;
                if (itemDeviceInfo.type.isEmpty()) {
                    deleteWatch(itemDeviceInfo, position);
                    Util.deleteWatchEntry(serial);
                } else {
                    deleteSensor(itemDeviceInfo, position);
                    Util.deleteSensorEntry(itemDeviceInfo.type, serial);
                }
            });

            confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDlg.setView(confirmView);
            confirmDlg.show();
        });

        viewHolder.ivUp.setOnClickListener(view -> {
            itemDeviceInfo.isDetail = false;
            notifyDataSetChanged();
        });

        viewHolder.ivDown.setOnClickListener(view -> {
            itemDeviceInfo.isDetail = true;
            notifyDataSetChanged();
        });

        return convertView;
    }

    private void deletedSuccess(ItemDeviceInfo itemDeviceInfo, int position) {
        deviceList.remove(position);
        if (itemDeviceInfo.type.isEmpty()) {
            if (itemDeviceInfo.serial.equals(Prefs.Instance().getMoniteringWatchSerial())) {
                Util.clearHeartRateEntry();
                FragmentDevice.mRefresh = true;
                if (!deviceList.isEmpty()) {
                    ItemWatchInfo ldeviceInfo = (ItemWatchInfo) deviceList.get(deviceList.size()-1);
                    Util.setMoniteringWatchInfo(ldeviceInfo);
                    Prefs.Instance().setMoniteringWatchSerial(ldeviceInfo.serial);
                    Prefs.Instance().commit();
                } else {
                    Util.setMoniteringWatchInfo(null);
                    Prefs.Instance().setMoniteringWatchSerial("");
                    Prefs.Instance().commit();
                }
            }
        }

        Util.showToastMessage(getContext(), R.string.str_release_device);
        notifyDataSetChanged();
        mDevice.refreshSensors();
    }

    private void deleteWatch(ItemDeviceInfo itemDeviceInfo, int position) {
        m_dlgProgress.show();
        HttpAPI.deleteWatch(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemDeviceInfo.serial, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                m_dlgProgress.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                        String sMsg = jsonObject.getString("msg");
                        Util.ShowDialogError(sMsg);
                    } else {
                        deletedSuccess(itemDeviceInfo, position);
                    }
                }
                catch (JSONException e) {
                    Util.ShowDialogError(R.string.str_update_failed);
                }
            }

            @Override
            public void onError(Object error) {
                m_dlgProgress.dismiss();
                Util.ShowDialogError(R.string.str_update_failed);
            }
        }, TAG);
    }

    private void deleteSensor(ItemDeviceInfo itemDeviceInfo, int position) {
        m_dlgProgress.show();
        HttpAPI.deleteSensor(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemDeviceInfo.serial, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                m_dlgProgress.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                        String sMsg = jsonObject.getString("msg");
                        Util.ShowDialogError(sMsg);
                    } else {
                        deletedSuccess(itemDeviceInfo, position);
                    }
                }
                catch (JSONException e) {
                    Util.ShowDialogError(R.string.str_update_failed);
                }
            }

            @Override
            public void onError(Object error) {
                m_dlgProgress.dismiss();
                Util.ShowDialogError(R.string.str_update_failed);
            }
        }, TAG);
    }
}
