//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.database.IOTDBHelper;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemSensorInfo;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivitySensorInfo extends ActivityBase implements OnClickListener {
    private final String TAG = "ActivitySensorInfo";

    private final int REQUEST_SELECT_ADDRESS = 0;
    private final int REQUEST_BIND_COMPLETE = 1;

    private ImageView ivBack;
    private Spinner spResidence;
    private ArrayAdapter residenceAdapter;
    private Spinner spBelongs;
    private ArrayAdapter belongAdapter;
    private TextView tvInputDesc;
    private TextView tvResidence;
    private EditText editAddress;
    private EditText editLabel;
    private TextView tvConfirm;

    private ItemSensorInfo itemSensorInfo;
    private boolean isRegister;

    String[] residencies, addresses;

    // require mark
    private TextView tvRequireResidence;
    private TextView tvRequireAddress;
    private TextView tvRequireLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
        }

        itemSensorInfo = (ItemSensorInfo) getIntent().getSerializableExtra("device_data");
        isRegister = (boolean) getIntent().getSerializableExtra("isRegister");

        if (itemSensorInfo == null) {
            finish();
        }

        initControls();
        setEventListener();
    }

    @Override
    protected void initControls() {
        super.initControls();

        ivBack = findViewById(R.id.ID_IMGVIEW_BACK);

        spResidence = findViewById(R.id.ID_SPINNER_RESIDENCE);
        residencies = getResources().getStringArray(R.array.location_tab_array);
        residenceAdapter = new ArrayAdapter(this, R.layout.item_spinner, residencies);
        residenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spResidence.setAdapter(residenceAdapter);
        spResidence.setSelection(0);

        spBelongs = findViewById(R.id.ID_SPINNER_BELONGS_TO);
        addresses = getResources().getStringArray(R.array.belong_tab_array);
        belongAdapter = new ArrayAdapter(this, R.layout.item_spinner, addresses);
        belongAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spBelongs.setAdapter(belongAdapter);
        spBelongs.setSelection(0);

        tvInputDesc = findViewById(R.id.ID_TXTVIEW_INPUT_DESC);
        tvResidence = findViewById(R.id.ID_TXTVIEW_RESIDENCE);
        editAddress = findViewById(R.id.ID_EDIT_ADDRESS);
        editLabel = findViewById(R.id.ID_EDIT_LABEL);
        tvConfirm = findViewById(R.id.ID_BTN_CONFIRM);

        tvRequireResidence = findViewById(R.id.ID_TXTVIEW_REQUIRE_RESIDENCE);
        tvRequireAddress = findViewById(R.id.ID_TXTVIEW_REQUIRE_ADDRESS);
        tvRequireLabel = findViewById(R.id.ID_TXTVIEW_REQUIRE_LABEL);

        tvRequireResidence.setVisibility(View.GONE);
        tvRequireAddress.setVisibility(View.GONE);
        tvRequireLabel.setVisibility(View.GONE);

        if (isRegister){
            tvInputDesc.setVisibility(View.VISIBLE);
        }else{
            tvInputDesc.setVisibility(View.GONE);
        }

        setAddressData();
    }

    private void setAddressData() {
        for (int i = 0; i < residencies.length; i++) {
            if (residencies[i].equals(itemSensorInfo.locationLabel)) {
                spResidence.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < addresses.length; i++) {
            if (addresses[i].equals(itemSensorInfo.labelRelative)) {
                spBelongs.setSelection(i);
                break;
            }
        }

        editAddress.setText(itemSensorInfo.address);
        editLabel.setText(itemSensorInfo.contactName);
        tvResidence.setText(itemSensorInfo.residence);
    }

    @Override
    protected void setEventListener() {
        ivBack.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvResidence.setOnClickListener(this);
        editAddress.setOnClickListener(this);
        editLabel.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ID_IMGVIEW_BACK:
                finish();
                break;
            case R.id.ID_BTN_CONFIRM:
                onConfirm();
                break;
            case R.id.ID_TXTVIEW_RESIDENCE:
                tvRequireResidence.setVisibility(View.GONE);
                onLocation();
                break;
            case R.id.ID_EDIT_ADDRESS:
                tvRequireAddress.setVisibility(View.GONE);
                break;
            case R.id.ID_EDIT_LABEL:
                tvRequireLabel.setVisibility(View.GONE);
                break;
        }
    }

    private void onConfirm() {
        itemSensorInfo.locationLabel = spResidence.getSelectedItem().toString();
        itemSensorInfo.labelRelative = spBelongs.getSelectedItem().toString();
        itemSensorInfo.residence = tvResidence.getText().toString();
        itemSensorInfo.address = editAddress.getText().toString();
        itemSensorInfo.contactName = editLabel.getText().toString();

        if (itemSensorInfo.residence.isEmpty()) {
            tvRequireResidence.setVisibility(View.VISIBLE);
            return;
        }

        if (itemSensorInfo.address.isEmpty()) {
            tvRequireAddress.setVisibility(View.VISIBLE);
            editAddress.requestFocus();
            return;
        }

        if (itemSensorInfo.contactName.isEmpty()) {
            tvRequireLabel.setVisibility(View.VISIBLE);
            editLabel.requestFocus();
            return;
        }

        updateSensorInfo();
    }

    private void updateSensorInfo() {
        m_dlgProgress.show();

        HttpAPI.updateSensorInfo(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), itemSensorInfo, new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                m_dlgProgress.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                        String sMsg = jsonObject.getString("msg");
                        Util.ShowDialogError(sMsg);
                        return;
                    }

                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    itemSensorInfo.batteryStatus = dataObject.getBoolean("battery_status");
                    itemSensorInfo.alarmStatus = dataObject.getBoolean("alarm_status");
                    itemSensorInfo.serviceStartDate = dataObject.getString("service_start");
                    itemSensorInfo.serviceEndDate = dataObject.getString("service_end");

                    IOTDBHelper iotdbHelper = new IOTDBHelper(ActivitySensorInfo.this);
                    ItemSensorInfo deviceInfo = Util.findSensorEntry(itemSensorInfo.type, itemSensorInfo.serial);
                    if (deviceInfo == null) {
                        Util.addSensorEntry(itemSensorInfo);
                    } else {
                        Util.updateSensorEntry(deviceInfo, itemSensorInfo);
                    }

                    startBindCompleteActivity();
                } catch (JSONException e) {
                    Util.ShowDialogError(R.string.str_page_loading_failed);
                }
            }

            @Override
            public void onError(Object error) {
                m_dlgProgress.dismiss();
                Util.ShowDialogError(R.string.str_page_loading_failed);
            }
        }, TAG);
    }

    private void startBindCompleteActivity() {
        Intent intent = new Intent(this, ActivityBindComplete.class);
        intent.putExtra("device_data", itemSensorInfo);
        startActivityForResult(intent, REQUEST_BIND_COMPLETE);
    }

    private void onLocation() {
        Intent intent = new Intent(this, ActivitySelectAddress.class);
        startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_ADDRESS) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    itemSensorInfo.province = data.getStringExtra("province_data");
                    itemSensorInfo.city = data.getStringExtra("city_data");
                    itemSensorInfo.district = data.getStringExtra("district_data");
                    itemSensorInfo.residence = data.getStringExtra("address_data");
                    itemSensorInfo.lat = data.getStringExtra("lat_data");
                    itemSensorInfo.lon = data.getStringExtra("lon_data");

                    tvResidence.setText((itemSensorInfo.residence));
                }
            }
        } else if (requestCode == REQUEST_BIND_COMPLETE) {
            setResult(resultCode);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        App.Instance().cancelPendingRequests(TAG);
    }
}
