package com.iot.shoumengou.fragment.discover;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityAccountManage;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.adapter.AdapterHeartRate;
import com.iot.shoumengou.helper.RoomActivity;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemHeartRate;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.AppConst;
import com.iot.shoumengou.util.Util;
import com.iot.shoumengou.view.GraphView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentReport extends Fragment implements View.OnClickListener {
    ImageView ivBack, ivSettings;
//    ImageView ivRefresh;
    TextView tvHeartRate, tvBloodPressure, tvTemperature, tvRateArea, tvAreaValue, tvAverage;
    GraphView graphView;
    TextView tvHeaderValue;
    ListView lvHeartRate;
    AdapterHeartRate adapterHeartRate;
    private final ArrayList<ItemHeartRate> heartRateList = new ArrayList<>();
    Spinner spinnerDate;

    LinearLayout llNotification;
    ImageView ivClose;
    TextView tvNotificationContent;
    TextView tvSupport, tvCallPhone;

    private int activeTabIndex = 0;
    private boolean isLoading = true;

    public FragmentReport() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
//        handler.removeCallbacks(dismissRefreshRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActivityMain) Objects.requireNonNull(getActivity())).resumeHealthProgress();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isLoading = false;

        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_report, container, false);

        initControl(rootView);
        setEventListener();

        //getHealthDataList();

        return rootView;
    }

    public void setActiveTabIndex(int index) {
        this.activeTabIndex = index;
    }

    private void initControl(View layout) {
        ivBack = layout.findViewById(R.id.ID_IMG_BACK);
        tvHeartRate = layout.findViewById(R.id.ID_TXTVIEW_HEART);
        tvBloodPressure = layout.findViewById(R.id.ID_TEXTVIEW_BLOOD);
        tvTemperature = layout.findViewById(R.id.ID_TXTVIEW_BODY_TEMPERARTURE);
        tvRateArea = layout.findViewById(R.id.ID_TXTVIEW_NORMAL_RATE_AREA);
        tvAreaValue = layout.findViewById(R.id.ID_TXTVIEW_NORMAL_RATE_AREA_VALUE);
        tvAverage = layout.findViewById(R.id.ID_TXT_AVERAGE);
        graphView = layout.findViewById(R.id.ID_GRAPH_VIEW);

        tvHeaderValue = layout.findViewById(R.id.ID_TEXT_HEADER_VALUE);

        lvHeartRate = layout.findViewById(R.id.ID_LSTVIEW_HEART_RATE);
        adapterHeartRate = new AdapterHeartRate(getActivity(), heartRateList);
        lvHeartRate.setAdapter(adapterHeartRate);

        spinnerDate = layout.findViewById(R.id.spinner);
        //Creating the ArrayAdapter instance having the country list
        String[] dateArray = getResources().getStringArray(R.array.date_array);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.item_spinner, dateArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(arrayAdapter);
        spinnerDate.setSelection(0);

        ivSettings = layout.findViewById(R.id.ID_IMG_SETTING);

        llNotification = layout.findViewById(R.id.ID_LL_NOTIFICATION);
        ivClose = layout.findViewById(R.id.ID_IMG_CLOSE);
        tvNotificationContent = layout.findViewById(R.id.ID_TXTVIEW_ADVICE_CONTENT);
        tvSupport = layout.findViewById(R.id.ID_TXTVIEW_SUPPORT);
        tvCallPhone = layout.findViewById(R.id.ID_TXTVIEW_CALL_PHONE);

        SpannableString content = new SpannableString(getString(R.string.str_heart_rate));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvHeartRate.setText(content);
        content = new SpannableString(getString(R.string.str_blood_pressure));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvBloodPressure.setText(content);
        content = new SpannableString(getString(R.string.str_body_temperature));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvTemperature.setText(content);
    }

    private void setEventListener() {
        ivBack.setOnClickListener(this);
        tvHeartRate.setOnClickListener(this);
        tvBloodPressure.setOnClickListener(this);
        tvTemperature.setOnClickListener(this);

        lvHeartRate.setOnItemClickListener((parent, view, position, id) -> {

        });

        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                getHealthDataList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ivSettings.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        tvSupport.setOnClickListener(this);
        tvCallPhone.setOnClickListener(this);

        updateStatus();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        FragmentParentDiscover parentFrag = ((FragmentParentDiscover) FragmentReport.this.getParentFragment());
        switch (v.getId()) {
            case R.id.ID_IMG_BACK:
                Objects.requireNonNull(parentFrag).popChildFragment(false);
                FragmentNewDiscover fragmentNewDiscover = (FragmentNewDiscover)((FragmentParentDiscover)(FragmentReport.this.getParentFragment())).getChildFragment(FragmentNewDiscover.class.getSimpleName());

                if ( fragmentNewDiscover != null && Util.monitoringWatch != null) {
                    fragmentNewDiscover.loadLastHealthData();
                }
                break;
            case R.id.ID_TXTVIEW_HEART:
                activeTabIndex = 0;
                break;
            case R.id.ID_TEXTVIEW_BLOOD:
                activeTabIndex = 1;
                break;
            case R.id.ID_TXTVIEW_BODY_TEMPERARTURE:
                activeTabIndex = 2;
                break;
            case R.id.ID_IMG_CLOSE:
                llNotification.setVisibility(View.GONE);
                break;
            case R.id.ID_IMG_SETTING:
                ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
                if (monitoringWatchInfo == null)
                    return;
                Objects.requireNonNull(parentFrag).pushChildFragment(new FragmentHealthSettings(), FragmentHealthSettings.class.getSimpleName());
                break;
            case R.id.ID_TXTVIEW_SUPPORT:
                onChartRequest();
                break;
            case R.id.ID_TXTVIEW_CALL_PHONE:
                onPhoneRequest();
                break;
        }

        updateStatus();
    }

    private void onPhoneRequest() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    AppConst.REQUEST_PERMISSION_STORAGE
            );
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:400-0909-119"));
            Objects.requireNonNull(getActivity()).startActivity(intent);
        }
    }

    private void onChartRequest() {
        ((ActivityMain) Objects.requireNonNull(getActivity())).showProgress();
        HttpAPI.requestChat(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), 0, new VolleyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                        ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();

                        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                        View confirmView = layoutInflater.inflate(R.layout.alert_chart, null);

                        final AlertDialog confirmDlg = new AlertDialog.Builder(getContext()).create();

                        TextView btnTitle = confirmView.findViewById(R.id.ID_TXTVIEW_TITLE);
                        btnTitle.setText(jsonObject.getString("msg"));

                        TextView btnCancel = confirmView.findViewById(R.id.ID_TXTVIEW_CONFIRM);

                        btnCancel.setOnClickListener(v -> confirmDlg.dismiss());

                        confirmDlg.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        confirmDlg.setView(confirmView);
                        confirmDlg.show();

                        return;
                    }

                    JSONObject dataObject = jsonObject.getJSONObject("data");

                    RoomActivity.setChatId(dataObject.getString("roomId"));
                    RoomActivity.setChatPass(dataObject.getString("password"));

                    Intent intent = new Intent(getActivity(), RoomActivity.class);
                    Objects.requireNonNull(getActivity()).startActivity(intent);
                    ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                } catch (JSONException e) {
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

    private double getAverageValue(boolean isHigh) {
        double result = 0;
        for (int i = 0; i < heartRateList.size(); i++) {
            ItemHeartRate itemHeartRate = heartRateList.get(i);
            if (activeTabIndex == 0) {
                result += itemHeartRate.heartRate;
            } else if (activeTabIndex == 1) {
                if (isHigh) {
                    result += itemHeartRate.highBloodPressure;
                } else {
                    result += itemHeartRate.lowBloodPressure;
                }
            } else {
                result += itemHeartRate.temperature;
            }
        }

        return result / heartRateList.size();
    }

    public void updateStatus() {
        switch (activeTabIndex) {
            case 0:
                tvRateArea.setText(R.string.str_normal_rate_area);
                tvAreaValue.setText(R.string.str_normal_rate_area_value);
                tvHeaderValue.setText(R.string.str_heart_rate);
                tvNotificationContent.setText(getString(R.string.str_heart_rate) + getString(R.string.str_health_suggest_text));
                tvAverage.setText(String.format(getString(R.string.str_average_heart_rate), (int) getAverageValue(false)));
                break;
            case 1:
                tvRateArea.setText(R.string.str_normal_pressure_area);
                tvAreaValue.setText(R.string.str_normal_pressure_value);
                tvHeaderValue.setText(R.string.str_blood_pressure_header);
                tvNotificationContent.setText(getString(R.string.str_blood_pressure) + getString(R.string.str_health_suggest_text));
                tvAverage.setText(String.format(getString(R.string.str_average_pressure), (int) getAverageValue(true), (int) getAverageValue(false)));
                break;
            case 2:
                tvRateArea.setText(R.string.str_normal_temp_area);
                tvAreaValue.setText(R.string.str_normal_temp_value);
                tvHeaderValue.setText(R.string.str_body_temperature);
                tvNotificationContent.setText(getString(R.string.str_body_temperature) + getString(R.string.str_health_suggest_text));
                tvAverage.setText(String.format(getString(R.string.str_average_temp), getAverageValue(false)));
                break;
        }
        graphView.setDrawMask(activeTabIndex);
        tvHeartRate.setTextColor(Objects.requireNonNull(getActivity()).getColor(activeTabIndex == 0 ? android.R.color.holo_blue_dark : android.R.color.black));
        tvBloodPressure.setTextColor(getActivity().getColor(activeTabIndex == 1 ? android.R.color.holo_blue_dark : android.R.color.black));
        tvTemperature.setTextColor(getActivity().getColor(activeTabIndex == 2 ? android.R.color.holo_blue_dark : android.R.color.black));

        setHeartRateArea();

        adapterHeartRate.setValueMask(activeTabIndex);
    }

    @SuppressLint("DefaultLocale")
    private void setHeartRateArea() {
        ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
        if (monitoringWatchInfo == null) {
            return;
        }

        float lowRate = monitoringWatchInfo.heart_rate_low_limit;
        float highRate = monitoringWatchInfo.heart_rate_high_limit;

        if (activeTabIndex == 1) {
            highRate = monitoringWatchInfo.blood_pressure_high_right_limit;
            lowRate = monitoringWatchInfo.blood_pressure_high_left_limit;
        } else if (activeTabIndex == 2) {
            lowRate = monitoringWatchInfo.temperature_low_limit;
            highRate = monitoringWatchInfo.temperature_high_limit;
        }

        graphView.setHeartRateRange(
                lowRate,
                highRate,
                monitoringWatchInfo.blood_pressure_low_left_limit,
                monitoringWatchInfo.blood_pressure_low_right_limit);
        graphView.setRateData(heartRateList);
        if (activeTabIndex == 0)
            tvAreaValue.setText(String.format(getString(R.string.str_normal_rate_area_value), (int) lowRate, (int) highRate));
        else if (activeTabIndex == 1) {
            tvAreaValue.setText(String.format(getString(R.string.str_normal_pressure_value),
                    String.format("%d", monitoringWatchInfo.blood_pressure_high_left_limit),
                    String.format("%d", monitoringWatchInfo.blood_pressure_high_right_limit),
                    String.format("%d", monitoringWatchInfo.blood_pressure_low_left_limit),
                    String.format("%d", monitoringWatchInfo.blood_pressure_low_right_limit)));
        } else {
            tvAreaValue.setText(String.format(getString(R.string.str_normal_temp_value),
                    String.format("%.1f", lowRate), String.format("%.1f", highRate)));
        }
    }

//    public void alertTakeOff() {
////        ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
//        ivRefresh.setEnabled(true);
//        takeOnStatus = false;
////        handler.removeCallbacks(dismissRefreshRunnable);
//    }

    public void getHealthDataList() {
        ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
        if (monitoringWatchInfo == null) {
            return;
        }

        int days = 7;
        switch (spinnerDate.getSelectedItemPosition()) {
            case 1:
                days = 20;
                break;
            case 2:
                days = 30;
                break;
            case 3:
                days = 90;
                break;
        }

        graphView.setDaysInRange(days);

        if (isLoading)
            return;

        ((ActivityMain) Objects.requireNonNull(getActivity())).showProgress();
        isLoading = true;

        heartRateList.clear();
        Util.clearHeartRateEntry();
        HttpAPI.getHealthDataList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), monitoringWatchInfo.serial, days, new VolleyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int iRetCode = jsonObject.getInt("retcode");
                    if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                        ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
//                        String sMsg = jsonObject.getString("msg");
//                        Util.ShowDialogError(sMsg);
                        isLoading = false;
                        return;
                    }

                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObject = dataArray.getJSONObject(i);
                        ItemHeartRate newHeartRate = new ItemHeartRate(dataObject);

                        if (heartRateList.size() == 0 || newHeartRate.checkTime - heartRateList.get(heartRateList.size() - 1).checkTime > 10000) {
                            heartRateList.add(newHeartRate);
                            Util.addHeartRateEntry(newHeartRate);
                        }
                    }

                    heartRateList.sort((o1, o2) -> (int) (o2.checkTime - o1.checkTime));

                    adapterHeartRate.notifyDataSetChanged();
                    updateStatus();
                    isLoading = false;
                    ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                } catch (JSONException e) {
                    isLoading = false;
                    ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                    Util.ShowDialogError(R.string.str_api_failed);
                }
            }

            @Override
            public void onError(Object error) {
                ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                Util.ShowDialogError(R.string.str_api_failed);
                isLoading = false;
            }
        }, "FragmentReport");
    }
}
