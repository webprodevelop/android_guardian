package com.iot.shoumengou.fragment.discover;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityDiscoverDetail;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.activity.ActivityNotification;
import com.iot.shoumengou.activity.ActivityScan;
import com.iot.shoumengou.adapter.AdapterInfo;
import com.iot.shoumengou.adapter.MainAdapter;
import com.iot.shoumengou.fragment.FragmentDevice;
import com.iot.shoumengou.fragment.FragmentDiscover;
import com.iot.shoumengou.fragment.FragmentLocation;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemDiscover;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentNewDiscover extends Fragment implements View.OnClickListener  {
    private final String TAG = "FragmentNewDiscover";

    ImageView ivNotification;
    TextView tvNotificationCount;
    LinearLayout llName;
    TextView tvDate;
    RelativeLayout rlHeartRate, rlPressure, rlTemperature;
    TextView tvName, tvHeartRate, tvBloodPressure, tvTemperature, tvAdvice, tvSeeMoreAdvice, tvMore, tvSeeMoreInfo, tvStatus;
    TextView tvRelation;
    LinearLayout llReport, llHealth, llStatus, llLocation;

    private MainAdapter mainAdapter;

    private final Runnable refreshHealthData = new Runnable() {
        @Override
        public void run() {
            if (Util.monitoringWatch != null) {
                loadLastHealthData();
                Util.monitoringWatch.takeOnStatus = true;
                updateMonitoringWatchStatus();
            }
        }
    };

    ListView lvInfo;
    AdapterInfo adapterInfo;

    public FragmentNewDiscover() {
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
        ViewGroup rootView =  (ViewGroup)inflater.inflate(R.layout.fragment_new_discover, container, false);

        initControl(rootView);
        setEventListener();
        loadDiscoverList();
        updateNotificationState();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateMonitoringWatchStatus();
        ((ActivityMain) Objects.requireNonNull(getActivity())).resumeHealthProgress();
        if (Util.monitoringWatch != null) {
            loadLastHealthData();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        App.Instance().cancelPendingRequests(TAG);
    }

    @Override
    public void onAttach(Context context) {
	    super.onAttach(context);
    }

    public void updateMonitoringWatchStatus() {
        ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
        if (monitoringWatchInfo == null) {
            tvName.setText("");
            tvRelation.setText("");
            tvStatus.setText(R.string.str_no_guard_watch);

            tvHeartRate.setTextColor(Color.WHITE);
            tvHeartRate.setText("-");
            tvBloodPressure.setTextColor(Color.WHITE);
            tvBloodPressure.setText("-");
            tvTemperature.setTextColor(Color.WHITE);
            tvTemperature.setText("-");
            tvAdvice.setText(R.string.str_no_devices);
            return;
        }

        tvName.setText(monitoringWatchInfo.name);

        if (monitoringWatchInfo.netStatus) {
            if (monitoringWatchInfo.takeOnStatus) {
                tvStatus.setText(R.string.str_normal);
                tvStatus.setTextColor(Color.GREEN);
            } else {
                tvStatus.setText(R.string.str_no_take);
                tvStatus.setTextColor(Color.RED);
            }
        } else {
            tvStatus.setText(R.string.str_unconnect);
            tvStatus.setTextColor(Color.RED);
        }

        if (Util.isUpdatedHealthData == false){
            tvStatus.setText(R.string.str_check_health);
            tvStatus.setTextColor(Color.GREEN);
        }

        tvRelation.setText(monitoringWatchInfo.userRelation);
    }

    private void initControl(View layout) {
        ivNotification = layout.findViewById(R.id.ID_IMGVIEW_NOTIFICATION);
        tvNotificationCount = layout.findViewById(R.id.ID_TEXTVIEW_COUNT);

        tvDate = layout.findViewById(R.id.ID_TEXT_DATE);
        tvDate.setText(Util.getDateFormatStringIgnoreLocale(new Date()));

        rlHeartRate = layout.findViewById(R.id.ID_RL_HEART_RATE);
        rlPressure = layout.findViewById(R.id.ID_RL_PRESSURE);
        rlTemperature = layout.findViewById(R.id.ID_RL_TEMPERATURE);

        llName = layout.findViewById(R.id.ID_LL_NAME);
        tvName = layout.findViewById(R.id.ID_TEXT_NAME);
        tvStatus = layout.findViewById(R.id.ID_TEXT_STATUS);
        tvRelation = layout.findViewById(R.id.ID_TEXT_RELATION);
        tvHeartRate = layout.findViewById(R.id.ID_TEXT_HEAET_RATE);
        tvBloodPressure = layout.findViewById(R.id.ID_TEXT_BLOOD_PRESSURE);
        tvTemperature = layout.findViewById(R.id.ID_TEXT_TEMPERATURE);
        tvAdvice = layout.findViewById(R.id.ID_TEXT_ADVICE);
        tvSeeMoreAdvice = layout.findViewById(R.id.ID_TEXT_ADVICE_DETAILS);
        tvMore = layout.findViewById(R.id.ID_TEXT_SEE_MORE);
        tvSeeMoreInfo = layout.findViewById(R.id.ID_TEXT_INFO_DETAILS);

        lvInfo = layout.findViewById(R.id.ID_INFO_LIST);
        adapterInfo = new AdapterInfo(getActivity(), Util.recommendedList);
        lvInfo.setAdapter(adapterInfo);
        lvInfo.setDivider(null);

        com.iot.shoumengou.view.CustomRecyclerView mainView = layout.findViewById(R.id.ID_MAIN_VIEW);
        mainView.setLayoutManager(new com.iot.shoumengou.util.CustomLinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mainAdapter = new MainAdapter(FragmentNewDiscover.this, Util.discoverList, "");
        mainView.setAdapter(mainAdapter);

        llReport = layout.findViewById(R.id.ID_LL_REPORT);
        llStatus = layout.findViewById(R.id.ID_LL_STATUS);
        llHealth = layout.findViewById(R.id.ID_LL_HEALTH);
        llLocation = layout.findViewById(R.id.ID_LL_LOCATION);

        tvName.setText("");
        tvStatus.setText("");
        tvRelation.setText("");
    }

    public void updateNotificationState() {
        if(getActivity() != null) {
            if(Util.getNotificationCounts(getActivity()) > 0) {
                tvNotificationCount.setVisibility(View.VISIBLE);
            }
            else {
                tvNotificationCount.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void getHealthDataList() {
        ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
        if (monitoringWatchInfo == null) {
            return;
        }

        int days = 7;

        ((ActivityMain) Objects.requireNonNull(getActivity())).showProgress();

        HttpAPI.getHealthDataList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, days, new VolleyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
//                        String sMsg = jsonObject.getString("msg");
//                        Util.ShowDialogError(sMsg);
                        return;
                    }

                    float lowRate = monitoringWatchInfo.heart_rate_low_limit;
                    float highRate = monitoringWatchInfo.heart_rate_high_limit;

                    float highPressureMax = monitoringWatchInfo.blood_pressure_high_right_limit;
                    float highPressureMin = monitoringWatchInfo.blood_pressure_low_right_limit;
                    float lowPressureMax = monitoringWatchInfo.blood_pressure_high_left_limit;
                    float lowPressureMin = monitoringWatchInfo.blood_pressure_low_left_limit;

                    int abnormalRateCount = 0;
                    int abnormalPressureCount = 0;

                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);

                        int heartRate = Integer.parseInt(dataObject.optString("heart_rate"));

                        if (heartRate > highRate || heartRate < lowRate) {
                            abnormalRateCount += 1;
                        }

                        int highBloodPressure = Integer.parseInt(dataObject.optString("high_blood_pressure"));
                        int lowBloodPressure = Integer.parseInt(dataObject.optString("low_blood_pressure"));

                        if (highBloodPressure > highPressureMax || highBloodPressure < highPressureMin || lowBloodPressure > lowPressureMax || lowBloodPressure < lowPressureMin) {
                            abnormalPressureCount += 1;
                        }
                    }

                    tvAdvice.setText(String.format(getString(R.string.str_report_notification_info), abnormalRateCount, abnormalPressureCount));

                    ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                }
                catch (JSONException e) {
                    ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                    Util.ShowDialogError(R.string.str_api_failed);
                }
            }

            @Override
            public void onError(Object error) {
                ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                Util.ShowDialogError(R.string.str_api_failed);
            }
        }, "FragmentReport");
    }

    private void setEventListener() {
        ivNotification.setOnClickListener(this);
        llName.setOnClickListener(this);

        rlHeartRate.setOnClickListener(this);
        rlPressure.setOnClickListener(this);
        rlTemperature.setOnClickListener(this);

        tvSeeMoreAdvice.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        tvSeeMoreInfo.setOnClickListener(this);

        lvInfo.setOnItemClickListener((parent, view, position, id) -> {
            ItemDiscover itemDiscover = Util.recommendedList.get(position);

            Intent intent = new Intent(getContext(), ActivityDiscoverDetail.class);
            intent.putExtra("discover_data", itemDiscover);
            startActivityForResult(intent, ActivityMain.REQUEST_DISCOVER_DETAIL);
        });

        llReport.setOnClickListener(this);
        llHealth.setOnClickListener(this);
        llStatus.setOnClickListener(this);
        llLocation.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityMain.REQUEST_DISCOVER_DETAIL) {
            boolean bNotification = false;
            for (ItemDiscover itemDiscover : Util.discoverList) {
                if (itemDiscover.readCnt.equals("0")) {
                    bNotification = true;
                    break;
                }
            }

            ((ActivityMain) Objects.requireNonNull(getActivity())).showDiscoverNotification(bNotification);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        FragmentParentDiscover parentFrag = ((FragmentParentDiscover)FragmentNewDiscover.this.getParentFragment());
        switch (v.getId()) {
            case R.id.ID_IMGVIEW_NOTIFICATION:
                Intent intent = new Intent(getActivity(), ActivityNotification.class);
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
            case R.id.ID_LL_NAME:
                Objects.requireNonNull(parentFrag).pushChildFragment(new FragmentWardList(), FragmentWardList.class.getSimpleName());
                break;
            case R.id.ID_TEXT_ADVICE_DETAILS:
                if (Util.monitoringWatch == null) {
                    Util.ShowDialogError(Objects.requireNonNull(getContext()).getResources().getString(R.string.str_no_guard_watch));
                }else {
                    Objects.requireNonNull(parentFrag).pushChildFragment(new FragmentHealthSettings(), FragmentHealthSettings.class.getSimpleName());
                }
                break;
            case R.id.ID_RL_HEART_RATE:
            case R.id.ID_LL_REPORT:
                if (Util.monitoringWatch == null) {
                    Util.ShowDialogError(Objects.requireNonNull(getContext()).getResources().getString(R.string.str_no_guard_watch));
                }else {
                    FragmentReport instance = new FragmentReport();
                    instance.setActiveTabIndex(0);
                    Objects.requireNonNull(parentFrag).pushChildFragment(instance, FragmentReport.class.getSimpleName());
                }
                break;
            case R.id.ID_RL_PRESSURE:
                if (Util.monitoringWatch == null) {
                    Util.ShowDialogError(Objects.requireNonNull(getContext()).getResources().getString(R.string.str_no_guard_watch));
                }else {
                    FragmentReport instance1 = new FragmentReport();
                    instance1.setActiveTabIndex(1);
                    Objects.requireNonNull(parentFrag).pushChildFragment(instance1, FragmentReport.class.getSimpleName());
                }
                break;
            case R.id.ID_RL_TEMPERATURE:
                if (Util.monitoringWatch == null) {
                    Util.ShowDialogError(Objects.requireNonNull(getContext()).getResources().getString(R.string.str_no_guard_watch));
                }else {
                    FragmentReport instance2 = new FragmentReport();
                    instance2.setActiveTabIndex(2);
                    Objects.requireNonNull(parentFrag).pushChildFragment(instance2, FragmentReport.class.getSimpleName());
                }
                break;
            case R.id.ID_TEXT_SEE_MORE:
                FragmentDiscover fragmentDiscover = new FragmentDiscover();
                fragmentDiscover.setIsDiscover(true);
                Objects.requireNonNull(parentFrag).pushChildFragment(fragmentDiscover, FragmentDiscover.class.getSimpleName());
                break;
            case R.id.ID_TEXT_INFO_DETAILS:
                FragmentDiscover fragmentDiscover1 = new FragmentDiscover();
                fragmentDiscover1.setIsDiscover(false);
                Objects.requireNonNull(parentFrag).pushChildFragment(fragmentDiscover1, FragmentDiscover.class.getSimpleName());
                break;
            case R.id.ID_LL_STATUS:
                FragmentDevice fragmentDevice = new FragmentDevice();
                Objects.requireNonNull(parentFrag).pushChildFragment(fragmentDevice, FragmentDevice.class.getSimpleName());
                break;
            case R.id.ID_LL_HEALTH:
                ((ActivityMain) Objects.requireNonNull(getActivity())).showProgress();
                HttpAPI.requestHealthData(Prefs.Instance().getUserToken(),
                        Prefs.Instance().getUserPhone(),
                        "5G", Prefs.Instance().getMoniteringWatchSerial(),
                        new VolleyCallback() {
                            @Override
                            public void onSuccess(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    int iRetCode = jsonObject.getInt("retcode");

                                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                                        String sMsg = jsonObject.getString("msg");
                                        ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                                        Util.ShowDialogError(sMsg);
                                        if (iRetCode == HttpAPIConst.RespCode.DEVICE_STATUS_OFFLINE){
                                            Util.monitoringWatch.netStatus = false;
                                            updateMonitoringWatchStatus();
                                        }
                                        return;
                                    }

                                    // scott show process and wait to health data
                                    ((ActivityMain) Objects.requireNonNull(getActivity())).startHealthProgress(refreshHealthData);

                                } catch (JSONException e) {

                                }
                                ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();


                            }

                            @Override
                            public void onError(Object error) {
                                ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();

                            }
                        }, getActivity().getClass().getSimpleName());
                break;
            case R.id.ID_LL_LOCATION:
                Util.ShowDialogError(Objects.requireNonNull(getContext()).getResources().getString(R.string.str_location_error));
//                if (Util.monitoringWatch == null) {
//                    Context context = App.Instance().getCurrentActivity();
//                    if (context == null) {
//                        context = ActivityMain.mainContext;
//                    }
//                    String str_message = context.getResources().getString(R.string.str_no_device_msg);
//                    AlertDialog.Builder		builder = new AlertDialog.Builder(context);
//                    builder.setMessage(str_message);
//                    builder.setPositiveButton(context.getResources().getString(R.string.str_bind), (dialog, which) -> {
//                        dialog.dismiss();
//                        Intent intentScan = new Intent(getActivity(), ActivityScan.class);
//                        intentScan.putExtra("device_type", "");
//                        startActivity(intentScan);
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                }
//                else {
//                    FragmentLocation fragmentLocation = new FragmentLocation();
//                    Objects.requireNonNull(parentFrag).pushChildFragment(fragmentLocation, FragmentLocation.class.getSimpleName());
//                }
                break;
        }
    }

    public void loadLastHealthData() {
        ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
        if (monitoringWatchInfo == null) {
            return;
        }

        HttpAPI.getLastHealthData(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, new VolleyCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
//                        String sMsg = jsonObject.getString("msg");
//                        Util.ShowDialogError(sMsg);
                        return;
                    }
                    tvHeartRate.setText("");
                    tvBloodPressure.setText("");
                    tvTemperature.setText("");

                    JSONObject object = jsonObject.getJSONObject("data");

                    int iHeartRate = Integer.parseInt(object.optString("heart_rate"));
                    int low_heart_limit = Util.monitoringWatch.heart_rate_low_limit;
                    int high_hear_limit = Util.monitoringWatch.heart_rate_high_limit;

                    if(iHeartRate > high_hear_limit || iHeartRate < low_heart_limit) {
                        tvHeartRate.setTextColor(Color.RED);
                    }
                    else {
                        tvHeartRate.setTextColor(Color.WHITE);
                    }
                    if (object.optString("heart_rate").startsWith(("-"))) {
                        tvHeartRate.setText(R.string.str_minus_value);
                    }
                    else {
                        tvHeartRate.setText(object.optString("heart_rate"));
                    }

                    int iHighBloodTempRate = Integer.parseInt(object.optString("high_blood_pressure"));
                    int left_blood_high_limit = Util.monitoringWatch.blood_pressure_high_left_limit;
                    int right_blood_high_limit = Util.monitoringWatch.blood_pressure_high_right_limit;

                    int iLowBloodTempRate = Integer.parseInt(object.optString("low_blood_pressure"));
                    int left_blood_low_limit = Util.monitoringWatch.blood_pressure_low_left_limit;
                    int right_blood_low_limit = Util.monitoringWatch.blood_pressure_low_right_limit;

                    if(iHighBloodTempRate > right_blood_high_limit || iHighBloodTempRate < left_blood_high_limit) {
                        tvBloodPressure.setTextColor(Color.RED);
                    }
                    else if(iLowBloodTempRate > right_blood_low_limit || iLowBloodTempRate < left_blood_low_limit) {
                        tvBloodPressure.setTextColor(Color.RED);
                    }
                    else {
                        tvBloodPressure.setTextColor(Color.WHITE);
                    }
                    tvBloodPressure.setText(
                            (object.optString("high_blood_pressure").startsWith("-") ? getString(R.string.str_minus_value) : object.optString("high_blood_pressure")) +
                                    "/" +
                                    (object.optString("low_blood_pressure").startsWith("-") ? getString(R.string.str_minus_value) : object.optString("low_blood_pressure")));

                    float fTempRate = Float.parseFloat(object.optString("temperature"));
                    float low_temp_limit = Util.monitoringWatch.temperature_low_limit;
                    float high_temp_limit = Util.monitoringWatch.temperature_high_limit;

                    if(fTempRate > high_temp_limit || fTempRate < low_temp_limit) {
                        tvTemperature.setTextColor(Color.RED);
                    }
                    else {
                        tvTemperature.setTextColor(Color.WHITE);
                    }
                    tvTemperature.setText(object.optString("temperature").startsWith("-") ? getString(R.string.str_minus_value) : object.optString("temperature"));
                }
                catch (JSONException e) {
//                    tvHeartRate.setText("00");
//                    tvBloodPressure.setText("80~180");
//                    tvTemperature.setText("36.5");
//                    Util.ShowDialogError(R.string.str_api_failed);
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onError(Object error) {
//                tvHeartRate.setText("00");
//                tvBloodPressure.setText("80~180");
//                tvTemperature.setText("36.5");
//                Util.ShowDialogError(R.string.str_api_failed);
            }
        }, TAG);

        getHealthDataList();
    }

    public void loadDiscoverList() {
        Util.recommendedList.clear();
        Util.discoverList.clear();

        HttpAPI.getNewsList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), "", new VolleyCallback() {
            @Override
            public void onSuccess(String response) {
                boolean bNotification = false;

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                        String sMsg = jsonObject.getString("msg");
                        Util.ShowDialogError(sMsg);
                        return;
                    }

                    JSONObject object = jsonObject.getJSONObject("data");

                    JSONArray recArray = object.getJSONArray("hot_recommend");
                    for (int i = 0; i < recArray.length(); i++) {
                        JSONObject dataObject = recArray.getJSONObject(i);
                        ItemDiscover itemRecommended = new ItemDiscover(dataObject);
                        Util.recommendedList.add(itemRecommended);
                    }

                    mainAdapter.notifyDataSetChanged();

                    JSONArray dataArray = object.getJSONArray("common_sense");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);
                        ItemDiscover itemInfo = new ItemDiscover(dataObject);
                        if (itemInfo.readCnt.equals("0")) {
                            bNotification = true;
                        }
                        Util.discoverList.add(itemInfo);
                    }

                    adapterInfo.notifyDataSetChanged();
                    ((ActivityMain) Objects.requireNonNull(getActivity())).showDiscoverNotification(bNotification);
                }
                catch (JSONException e) {
                    Util.ShowDialogError(R.string.str_api_failed);
                }
            }

            @Override
            public void onError(Object error) {
                //m_dlgProgress.dismiss();
                Util.ShowDialogError(R.string.str_api_failed);
            }
        }, TAG);
    }
}
