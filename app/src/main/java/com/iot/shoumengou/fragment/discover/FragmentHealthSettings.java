package com.iot.shoumengou.fragment.discover;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityMain;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentHealthSettings extends Fragment implements View.OnClickListener {
    EditText editHighHeartRate, editLowHeartRate, editHighPressure1, editHighPressure2, editLowPressure1, editLowPressure2, editHighTemp, editLowTemp;
    TextView tvCancel, tvConfirm, tvSetDefault;
    TextView tvMeasure_period;
    ImageView ivBack;

    String[] strarray;

    public FragmentHealthSettings() {
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
        ViewGroup layout = (ViewGroup)inflater.inflate(R.layout.fragment_health_settings, container, false);

        initControl(layout);
        setEventListener();

        strarray = getResources().getStringArray(R.array.health_period_array);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDefault();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
       switch (v.getId()) {
           case R.id.ID_IMG_BACK:
            case R.id.ID_TXTVIEW_CANCEL:
                FragmentParentDiscover parentFrag = ((FragmentParentDiscover) FragmentHealthSettings.this.getParentFragment());
                Objects.requireNonNull(parentFrag).popChildFragment(false);
                break;
            case R.id.ID_TXTVIEW_CONFIRM:
                onConfirm();
                break;
            case R.id.ID_TEXT_SET_DEFAULT:
                setDefault();
                break;
           case R.id.ID_EDIT_HEALTH_PERIOD:
               onPeriod();
               break;
        }
    }

    private void onPeriod() {
        ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
        if (monitoringWatchInfo == null)
            return;

        int selected = 0;
        String period = tvMeasure_period.getText().toString();
        if (period.equals(strarray[1])) {
            selected = 1;
        }
        if (period.equals(strarray[2])) {
            selected = 2;
        }
        if (period.equals(strarray[3])) {
            selected = 3;
        }

        new AlertDialog.Builder(getActivity())
                .setSingleChoiceItems(R.array.health_period_array, selected, (dialogInterface, which) -> {
                    switch (which) {
                        case 0:
                            monitoringWatchInfo.measure_period = 2;
                            tvMeasure_period.setText(strarray[0]);
                            break;
                        case 1:
                            monitoringWatchInfo.measure_period = 10;
                            tvMeasure_period.setText(strarray[1]);
                            break;
                        case 2:
                            monitoringWatchInfo.measure_period = 30;
                            tvMeasure_period.setText(strarray[2]);
                            break;
                        case 3:
                            monitoringWatchInfo.measure_period = 60;
                            tvMeasure_period.setText(strarray[3]);
                            break;
                    }

                    dialogInterface.dismiss();
                })
                .show();

    }

   @SuppressLint("SetTextI18n")
   private void onConfirm() {
       ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
       if (monitoringWatchInfo == null)
           return;

       if(editHighHeartRate.getText().toString().isEmpty()) {
           editHighHeartRate.setText("40");
       }
       else if (!editHighHeartRate.getText().toString().isEmpty() && Integer.parseInt(editHighHeartRate.getText().toString()) < 40) {
           editHighHeartRate.setText("40");
       }
       else if (!editHighHeartRate.getText().toString().isEmpty() && Integer.parseInt(editHighHeartRate.getText().toString()) > 150) {
           editHighHeartRate.setText("150");
       }

       if(editLowHeartRate.getText().toString().isEmpty()) {
           editLowHeartRate.setText("40");
       }
       else if (!editLowHeartRate.getText().toString().isEmpty() && Integer.parseInt(editLowHeartRate.getText().toString()) < 40) {
           editLowHeartRate.setText("40");
       }
       else if (!editLowHeartRate.getText().toString().isEmpty() && Integer.parseInt(editLowHeartRate.getText().toString()) > 150) {
           editLowHeartRate.setText("150");
       }

       if(editHighPressure1.getText().toString().isEmpty()) {
           editHighPressure1.setText("80");
       }
       else if (!editHighPressure1.getText().toString().isEmpty() && Integer.parseInt(editHighPressure1.getText().toString()) < 80) {
           editHighPressure1.setText("80");
       }
       else if (!editHighPressure1.getText().toString().isEmpty() && Integer.parseInt(editHighPressure1.getText().toString()) > 200) {
           editHighPressure1.setText("200");
       }

       if(editHighPressure2.getText().toString().isEmpty()) {
           editHighPressure2.setText("80");
       }
       else if (!editHighPressure2.getText().toString().isEmpty() && Integer.parseInt(editHighPressure2.getText().toString()) < 80) {
           editHighPressure2.setText("80");
       }
       else if (!editHighPressure2.getText().toString().isEmpty() && Integer.parseInt(editHighPressure2.getText().toString()) > 200) {
           editHighPressure2.setText("200");
       }

       if(editLowPressure1.getText().toString().isEmpty()) {
           editLowPressure1.setText("50");
       }
       else if (!editLowPressure1.getText().toString().isEmpty() && Integer.parseInt(editLowPressure1.getText().toString()) < 50) {
           editLowPressure1.setText("50");
       }
       else if (!editLowPressure1.getText().toString().isEmpty() && Integer.parseInt(editLowPressure1.getText().toString()) > 120) {
           editLowPressure1.setText("120");
       }

       if(editLowPressure2.getText().toString().isEmpty()) {
           editLowPressure2.setText("50");
       }
       else if (!editLowPressure2.getText().toString().isEmpty() && Integer.parseInt(editLowPressure2.getText().toString()) < 50) {
           editLowPressure2.setText("50");
       }
       else if (!editLowPressure2.getText().toString().isEmpty() && Integer.parseInt(editLowPressure2.getText().toString()) > 120) {
           editLowPressure2.setText("120");
       }

       if(editHighTemp.getText().toString().isEmpty()) {
           editHighTemp.setText("33.0");
       }
       else if (!editHighTemp.getText().toString().isEmpty() && Float.parseFloat(editHighTemp.getText().toString()) > 42.0) {
           editHighTemp.setText("42.0");
       }
       else if (!editHighTemp.getText().toString().isEmpty() && Float.parseFloat(editHighTemp.getText().toString()) < 33.0) {
           editHighTemp.setText("33.0");
       }

       if(editLowTemp.getText().toString().isEmpty()) {
           editLowTemp.setText("33.0");
       }
       else if (!editLowTemp.getText().toString().isEmpty() && Float.parseFloat(editLowTemp.getText().toString()) > 42.0) {
           editLowTemp.setText("42.0");
       }
       else if (!editLowTemp.getText().toString().isEmpty() && Float.parseFloat(editLowTemp.getText().toString()) < 33.0) {
           editLowTemp.setText("33.0");
       }

       if (Float.parseFloat(editHighHeartRate.getText().toString()) <= Float.parseFloat(editLowHeartRate.getText().toString())) {
            editLowHeartRate.setText("40");
        }
        else if (Float.parseFloat(editHighPressure2.getText().toString()) <= Float.parseFloat(editHighPressure1.getText().toString()) ||
                Float.parseFloat(editLowPressure2.getText().toString()) <= Float.parseFloat(editLowPressure1.getText().toString())) {
           editHighPressure1.setText("80");
           editLowPressure1.setText("50");
        }
        else if (Float.parseFloat(editLowTemp.getText().toString()) >= Float.parseFloat(editHighTemp.getText().toString())) {
           editLowTemp.setText("33.0");
        }

       monitoringWatchInfo.heart_rate_high_limit = Integer.parseInt(editHighHeartRate.getText().toString());
       monitoringWatchInfo.heart_rate_low_limit = Integer.parseInt(editLowHeartRate.getText().toString());
       monitoringWatchInfo.blood_pressure_high_left_limit = Integer.parseInt(editHighPressure1.getText().toString());
       monitoringWatchInfo.blood_pressure_high_right_limit = Integer.parseInt(editHighPressure2.getText().toString());
       monitoringWatchInfo.blood_pressure_low_left_limit = Integer.parseInt(editLowPressure1.getText().toString());
       monitoringWatchInfo.blood_pressure_low_right_limit = Integer.parseInt(editLowPressure2.getText().toString());
       monitoringWatchInfo.temperature_low_limit = Float.parseFloat(editLowTemp.getText().toString());
       monitoringWatchInfo.temperature_high_limit = Float.parseFloat(editHighTemp.getText().toString());
       String period = tvMeasure_period.getText().toString();
       if (period.equals(strarray[0])) {
           monitoringWatchInfo.measure_period = 2;
       }
       if (period.equals(strarray[1])) {
           monitoringWatchInfo.measure_period = 10;
       }
       if (period.equals(strarray[2])) {
           monitoringWatchInfo.measure_period = 30;
       }
       if (period.equals(strarray[3])) {
           monitoringWatchInfo.measure_period = 60;
       }

       ((ActivityMain) Objects.requireNonNull(getActivity())).showProgress();
       HttpAPI.setHealthPeriod(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(),
               monitoringWatchInfo.serial,
               monitoringWatchInfo.heart_rate_high_limit,
               monitoringWatchInfo.heart_rate_low_limit,
               monitoringWatchInfo.blood_pressure_high_left_limit,
               monitoringWatchInfo.blood_pressure_high_right_limit,
               monitoringWatchInfo.blood_pressure_low_left_limit,
               monitoringWatchInfo.blood_pressure_low_right_limit,
               monitoringWatchInfo.temperature_high_limit,
               monitoringWatchInfo.temperature_low_limit,
               monitoringWatchInfo.measure_period,
               new VolleyCallback() {
           @RequiresApi(api = Build.VERSION_CODES.N)
           @Override
           public void onSuccess(String response) {
               try {
                   JSONObject jsonObject = new JSONObject(response);
                   int iRetCode = jsonObject.getInt("retcode");
                   if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                       ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                       String sMsg = jsonObject.getString("msg");
                       Util.ShowDialogError(sMsg);
                       return;
                   }

                   Util.updateWatchEntry(monitoringWatchInfo, monitoringWatchInfo);
                   Util.setMoniteringWatchInfo(monitoringWatchInfo);

                   ((ActivityMain) Objects.requireNonNull(getActivity())).dismissProgress();
                   FragmentParentDiscover parentFrag = ((FragmentParentDiscover) FragmentHealthSettings.this.getParentFragment());
                   Objects.requireNonNull(parentFrag).popChildFragment(false);
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
       }, "FragmentHealthSettings");
    }

    private void loadDefault() {
        ItemWatchInfo monitoringWatchInfo = Util.monitoringWatch;
        if (monitoringWatchInfo == null)
            return;

        editHighHeartRate.setText(String.valueOf(monitoringWatchInfo.heart_rate_high_limit));
        editLowHeartRate.setText(String.valueOf(monitoringWatchInfo.heart_rate_low_limit));
        editHighPressure1.setText(String.valueOf(monitoringWatchInfo.blood_pressure_high_left_limit));
        editHighPressure2.setText(String.valueOf(monitoringWatchInfo.blood_pressure_high_right_limit));
        editLowPressure1.setText(String.valueOf(monitoringWatchInfo.blood_pressure_low_left_limit));
        editLowPressure2.setText(String.valueOf(monitoringWatchInfo.blood_pressure_low_right_limit));
        editLowTemp.setText(String.valueOf(monitoringWatchInfo.temperature_low_limit));
        editHighTemp.setText(String.valueOf(monitoringWatchInfo.temperature_high_limit));

        if (monitoringWatchInfo.measure_period == 2) {
            tvMeasure_period.setText(strarray[0]);
        }
        if (monitoringWatchInfo.measure_period == 10) {
            tvMeasure_period.setText(strarray[1]);
        }
        if (monitoringWatchInfo.measure_period == 30) {
            tvMeasure_period.setText(strarray[2]);
        }
        if (monitoringWatchInfo.measure_period == 60) {
            tvMeasure_period.setText(strarray[3]);
        }

        if (!monitoringWatchInfo.isManager) {
            tvMeasure_period.setEnabled(false);
            editHighHeartRate.setEnabled(false);
            editLowHeartRate.setEnabled(false);
            editHighPressure1.setEnabled(false);
            editHighPressure2.setEnabled(false);
            editLowPressure1.setEnabled(false);
            editLowPressure2.setEnabled(false);
            editHighTemp.setEnabled(false);
            editLowTemp.setEnabled(false);
            tvCancel.setVisibility(View.GONE);
            tvConfirm.setVisibility(View.GONE);
            tvSetDefault.setVisibility(View.GONE);
        }
    }

    private void setDefault() {
        editHighHeartRate.setText(String.valueOf(Prefs.DEFAULT_MAX_RATE));
        editLowHeartRate.setText(String.valueOf(Prefs.DEFAULT_MIN_RATE));
        editHighPressure1.setText(String.valueOf(Prefs.DEFAULT_HIGH_PRESSURE_MIN));
        editHighPressure2.setText(String.valueOf(Prefs.DEFAULT_HIGH_PRESSURE_MAX));
        editLowPressure1.setText(String.valueOf(Prefs.DEFAULT_LOW_PRESSURE_MIX));
        editLowPressure2.setText(String.valueOf(Prefs.DEFAULT_LOW_PRESSURE_MAX));
        editLowTemp.setText(String.valueOf(Prefs.DEFAULT_LOW_TEMP));
        editHighTemp.setText(String.valueOf(Prefs.DEFAULT_HIGH_TEMP));
        tvMeasure_period.setText(strarray[1]);
    }

    private void initControl (ViewGroup layout) {
        editHighHeartRate = layout.findViewById(R.id.ID_EDIT_MAX_HEART_RATE);
        editLowHeartRate = layout.findViewById(R.id.ID_EDIT_MIN_HEART_RATE);
        editHighPressure1 = layout.findViewById(R.id.ID_EDIT_HIGH_PRESSURE_1);
        editHighPressure2 = layout.findViewById(R.id.ID_EDIT_HIGH_PRESSURE_2);
        editLowPressure1 = layout.findViewById(R.id.ID_EDIT_LOW_PRESSURE_1);
        editLowPressure2 = layout.findViewById(R.id.ID_EDIT_LOW_PRESSURE_2);
        editHighTemp = layout.findViewById(R.id.ID_EDIT_MAX_TEMP);
        editLowTemp = layout.findViewById(R.id.ID_EDIT_MIN_TEMP);

        editHighHeartRate.setInputType(InputType.TYPE_CLASS_NUMBER);
        editLowHeartRate.setInputType(InputType.TYPE_CLASS_NUMBER);
        editHighPressure1.setInputType(InputType.TYPE_CLASS_NUMBER);
        editHighPressure2.setInputType(InputType.TYPE_CLASS_NUMBER);
        editLowPressure1.setInputType(InputType.TYPE_CLASS_NUMBER);
        editLowPressure2.setInputType(InputType.TYPE_CLASS_NUMBER);

        ivBack = layout.findViewById(R.id.ID_IMG_BACK);

        tvCancel = layout.findViewById(R.id.ID_TXTVIEW_CANCEL);
        tvConfirm = layout.findViewById(R.id.ID_TXTVIEW_CONFIRM);
        tvSetDefault = layout.findViewById(R.id.ID_TEXT_SET_DEFAULT);

        tvMeasure_period = layout.findViewById(R.id.ID_EDIT_HEALTH_PERIOD);
    }

    @SuppressLint("SetTextI18n")
    private void setEventListener() {
        ivBack.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvSetDefault.setOnClickListener(this);
        tvMeasure_period.setOnClickListener(this);

        editHighHeartRate.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if(editHighHeartRate.getText().toString().isEmpty()) {
                    editHighHeartRate.setText("40");
                }
                else if (!editHighHeartRate.getText().toString().isEmpty() && Integer.parseInt(editHighHeartRate.getText().toString()) < 40) {
                    editHighHeartRate.setText("40");
                }
                else if (!editHighHeartRate.getText().toString().isEmpty() && Integer.parseInt(editHighHeartRate.getText().toString()) > 150) {
                    editHighHeartRate.setText("150");
                }
            }
        });

        editLowHeartRate.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if(editLowHeartRate.getText().toString().isEmpty()) {
                    editLowHeartRate.setText("40");
                }
                else if (!editLowHeartRate.getText().toString().isEmpty() && Integer.parseInt(editLowHeartRate.getText().toString()) < 40) {
                    editLowHeartRate.setText("40");
                }
                else if (!editLowHeartRate.getText().toString().isEmpty() && Integer.parseInt(editLowHeartRate.getText().toString()) > 150) {
                    editLowHeartRate.setText("150");
                }
            }
        });

        editHighPressure1.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if(editHighPressure1.getText().toString().isEmpty()) {
                    editHighPressure1.setText("80");
                }
                else if (!editHighPressure1.getText().toString().isEmpty() && Integer.parseInt(editHighPressure1.getText().toString()) < 80) {
                    editHighPressure1.setText("80");
                }
                else if (!editHighPressure1.getText().toString().isEmpty() && Integer.parseInt(editHighPressure1.getText().toString()) > 200) {
                    editHighPressure1.setText("200");
                }
            }
        });

        editHighPressure2.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if(editHighPressure2.getText().toString().isEmpty()) {
                    editHighPressure2.setText("80");
                }
                else if (!editHighPressure2.getText().toString().isEmpty() && Integer.parseInt(editHighPressure2.getText().toString()) < 80) {
                    editHighPressure2.setText("80");
                }
                else if (!editHighPressure2.getText().toString().isEmpty() && Integer.parseInt(editHighPressure2.getText().toString()) > 200) {
                    editHighPressure2.setText("200");
                }
            }
        });

        editLowPressure1.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if(editLowPressure1.getText().toString().isEmpty()) {
                    editLowPressure1.setText("50");
                }
                else if (!editLowPressure1.getText().toString().isEmpty() && Integer.parseInt(editLowPressure1.getText().toString()) < 50) {
                    editLowPressure1.setText("50");
                }
                else if (!editLowPressure1.getText().toString().isEmpty() && Integer.parseInt(editLowPressure1.getText().toString()) > 120) {
                    editLowPressure1.setText("120");
                }
            }
        });

        editLowPressure2.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if(editLowPressure2.getText().toString().isEmpty()) {
                    editLowPressure2.setText("50");
                }
                else if (!editLowPressure2.getText().toString().isEmpty() && Integer.parseInt(editLowPressure2.getText().toString()) < 50) {
                    editLowPressure2.setText("50");
                }
                else if (!editLowPressure2.getText().toString().isEmpty() && Integer.parseInt(editLowPressure2.getText().toString()) > 120) {
                    editLowPressure2.setText("120");
                }
            }
        });

        editHighTemp.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if(editHighTemp.getText().toString().isEmpty()) {
                    editHighTemp.setText("33.0");
                }
                else if (!editHighTemp.getText().toString().isEmpty() && Float.parseFloat(editHighTemp.getText().toString()) > 42.0) {
                    editHighTemp.setText("42.0");
                }
                else if (!editHighTemp.getText().toString().isEmpty() && Float.parseFloat(editHighTemp.getText().toString()) < 33.0) {
                    editHighTemp.setText("33.0");
                }
            }
        });

        editLowTemp.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if(editLowTemp.getText().toString().isEmpty()) {
                    editLowTemp.setText("33.0");
                }
                else if (!editLowTemp.getText().toString().isEmpty() && Float.parseFloat(editLowTemp.getText().toString()) > 42.0) {
                    editLowTemp.setText("42.0");
                }
                else if (!editLowTemp.getText().toString().isEmpty() && Float.parseFloat(editLowTemp.getText().toString()) < 33.0) {
                    editLowTemp.setText("33.0");
                }
            }
        });
    }
}
