package com.iot.shoumengou.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityScan;
import com.iot.shoumengou.adapter.AdapterFollowService;
import com.iot.shoumengou.adapter.AdapterMyService;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.MyServiceInfo;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentServiceCenter extends Fragment {
    private final int REQUEST_ADD_NEW_SERVICE = 100;
    private ListView lvMyService, lvFollowService;
    private LinearLayout llNoContent;
    private TextView tvRegister;
    private AdapterMyService adapterMyService;
    private final ArrayList<MyServiceInfo> infoList = new ArrayList<>();
    private AdapterFollowService adapterFollowService;

    public FragmentServiceCenter() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup layout =  (ViewGroup)inflater.inflate(R.layout.fragment_service_center, container, false);

        initControl(layout);
        setEventListener();

        adapterFollowService.notifyDataSetChanged();

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        getServiceInfo();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_NEW_SERVICE && resultCode == RESULT_OK) {
            getServiceInfo();
        }
    }

    private void initControl(ViewGroup layout) {
        llNoContent = layout.findViewById(R.id.ID_LL_NO_CONTENT);
        tvRegister = layout.findViewById(R.id.ID_TEXT_CONFIRM);
        lvMyService = layout.findViewById(R.id.ID_LSTVIEW_MY_SERVICE);
        adapterMyService = new AdapterMyService(getActivity(), infoList);
        lvMyService.setAdapter(adapterMyService);

        lvFollowService = layout.findViewById(R.id.ID_LSTVIEW_FOLLOW_UP_SERVICE);
        adapterFollowService = new AdapterFollowService(getActivity(), Util.allPriceList);
        lvFollowService.setAdapter(adapterFollowService);
    }

    private void setEventListener() {
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivityScan.class);
            intent.putExtra("device_type", "");
            startActivityForResult(intent, REQUEST_ADD_NEW_SERVICE);
        });
    }

    public void getServiceInfo () {
        HttpAPI.inquirePaidServiceBunch(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                infoList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                        String sMsg = jsonObject.getString("msg");
                        Util.ShowDialogError(sMsg);
                        if (infoList.size() == 0) {
                            llNoContent.setVisibility(View.VISIBLE);
                        }
                        else {
                            llNoContent.setVisibility(View.GONE);
                        }
                        return;
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i ++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        int amount = object.optInt("amount");
                        int financialId = object.optInt("financial_id");
                        String createdTime = object.optString("create_time");
                        String serviceEnd = object.optString("service_end");
                        String userName = object.optString("user_name");
                        int deviceType = object.optInt("device_type");
                        String deviceSerial = object.optString("device_serial");
                        String freeServiceStart = object.optString("free_service_start");
                        String freeServiceEnd = object.optString("free_service_end");
                        int serviceYears = object.optInt("service_years");
                        int payType = object.optInt("pay_type");
                        int id = object.optInt("id");
                        int customerId = object.optInt("customer_id");
                        int orderId = object.getInt("order_id");
                        String serviceStart = object.optString("service_start");
                        boolean status = object.optBoolean("status");

                        MyServiceInfo myServiceInfo = new MyServiceInfo(amount,
                                financialId,
                                createdTime,
                                serviceEnd,
                                userName,
                                deviceType,
                                deviceSerial,
                                freeServiceStart,
                                freeServiceEnd,
                                serviceYears,
                                payType,
                                id,
                                customerId,
                                orderId,
                                serviceStart,
                                status);

                        infoList.add(myServiceInfo);
                    }

                    if (infoList.size() == 0) {
                        llNoContent.setVisibility(View.VISIBLE);
                    }
                    else {
                        llNoContent.setVisibility(View.GONE);
                    }
                    adapterMyService.notifyDataSetChanged();
                }
                catch (JSONException e) {
                    Util.ShowDialogError(R.string.str_api_failed);
                    if (infoList.size() == 0) {
                        llNoContent.setVisibility(View.VISIBLE);
                    }
                    else {
                        llNoContent.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onError(Object error) {
                Util.ShowDialogError(R.string.str_api_failed);
                if (infoList.size() == 0) {
                    llNoContent.setVisibility(View.VISIBLE);
                }
                else {
                    llNoContent.setVisibility(View.GONE);
                }
            }
        }, FragmentServiceCenter.class.getSimpleName());
    }
}
