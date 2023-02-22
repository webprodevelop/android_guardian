//@formatter:off
package com.iot.shoumengou.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.coolerfall.widget.lunar.LunarView;
import com.coolerfall.widget.lunar.MonthDay;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iot.shoumengou.App;
import com.iot.shoumengou.R;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityWatchInfo extends ActivityBase implements OnClickListener {
    private final String TAG = "ActivityWatchInfo";

    private final int REQUEST_SELECT_ADDRESS = 0;
    public final static int REQUEST_ADD_WATCH_INFO = 1;

    private ImageView ivBack;
    private TextView tvInputDesc;
    private EditText edtName;
    private EditText edtPhone;
    private TextView tvBirthday;
    private LinearLayout llGregorian, llLunar;
    private TextView tvContent;
    private TextView tvResidence;
    private EditText editAddress;
    private LinearLayout llMan;
    private LinearLayout llWoman;
    private TextView tvContinue;

    private Spinner spinnerRelation;

    private ItemWatchInfo itemWatchInfo = null;
    private ArrayList<String> illList = new ArrayList<>();
    private String birthDesc = "";

    private MonthDay mMonthDay = new MonthDay(Calendar.getInstance());

    private String[] relations;
    // require mark
    private TextView tvRequireName;
    private TextView tvRequirePhone;
    private TextView tvRequireSex;
    private TextView tvRequireBirthDay;
    private TextView tvRequireRelation;
    private TextView tvRequireResidence;
    private TextView tvRequireAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
        }

        itemWatchInfo = (ItemWatchInfo) getIntent().getSerializableExtra("device_data");

        if (itemWatchInfo == null) {
            finish();
        }

        initControls();
        setEventListener();

//		getWatchSetInfo();
    }

    @Override
    protected void initControls() {
        super.initControls();

        ivBack = findViewById(R.id.ID_IMGVIEW_BACK);
        tvInputDesc = findViewById(R.id.ID_TXTVIEW_INPUT_DESC);
        edtName = findViewById(R.id.ID_EDTTEXT_NAME);
        edtPhone = findViewById(R.id.ID_EDTTEXT_PHONE);
        llMan = findViewById(R.id.ID_LYT_MAN);
        llWoman = findViewById(R.id.ID_LYT_WOMEN);
        llGregorian = findViewById(R.id.ID_LL_GREGORIAN);
        llLunar = findViewById(R.id.ID_LL_LUNAR);
        tvBirthday = findViewById(R.id.ID_TXTVIEW_BIRTHDAY);
        tvContent = findViewById(R.id.ID_TXTVIEW_CONTENT);
        tvResidence = findViewById(R.id.ID_TXTVIEW_RESIDENCE);
        editAddress = findViewById(R.id.ID_EDIT_ADDRESS);
        tvContinue = findViewById(R.id.ID_BTN_CONTINUE);


        tvRequireName = findViewById(R.id.ID_TXTVIEW_REQUIRE_NAME);
        tvRequirePhone = findViewById(R.id.ID_TXTVIEW_REQUIRE_PHONE);
        tvRequireSex = findViewById(R.id.ID_TXTVIEW_REQUIRE_SEX);
        tvRequireBirthDay = findViewById(R.id.ID_TXTVIEW_REQUIRE_BIRTHDAY);
        tvRequireRelation = findViewById(R.id.ID_TXTVIEW_REQUIRE_RELATION);
        tvRequireResidence = findViewById(R.id.ID_TXTVIEW_REQUIRE_RESIDENCE);
        tvRequireAddress = findViewById(R.id.ID_TXTVIEW_REQUIRE_ADDRESS);

        tvRequireName.setVisibility(View.GONE);
        tvRequirePhone.setVisibility(View.GONE);
        tvRequireSex.setVisibility(View.GONE);
        tvRequireBirthDay.setVisibility(View.GONE);
        tvRequireRelation.setVisibility(View.GONE);
        tvRequireResidence.setVisibility(View.GONE);
        tvRequireAddress.setVisibility(View.GONE);

        spinnerRelation = findViewById(R.id.ID_SPINNER_RELATION);
        //Creating the ArrayAdapter instance having the country list
        relations = getResources().getStringArray(R.array.user_relation_array);
        ArrayAdapter arrayAdapter = new ArrayAdapter(ActivityWatchInfo.this, R.layout.item_spinner, relations);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerRelation.setAdapter(arrayAdapter);
        spinnerRelation.setSelection(0);

        if (itemWatchInfo.userRelation != null && !itemWatchInfo.userRelation.isEmpty()) {
            for (int i = 0; i < relations.length; i++) {
                if (relations[i].equals(itemWatchInfo.userRelation)) {
                    spinnerRelation.setSelection(i);
                    break;
                }
            }
        }

        if (itemWatchInfo.name != null && !itemWatchInfo.name.isEmpty()) {
            edtName.setText(itemWatchInfo.name);
        }

        if (itemWatchInfo.phone != null && !itemWatchInfo.phone.isEmpty()) {
            edtPhone.setText(itemWatchInfo.phone);
        }

        if (itemWatchInfo.sex == 1) {
            llMan.setSelected(true);
        } else if (itemWatchInfo.sex == 0) {
            llWoman.setSelected(true);
        }

        llGregorian.setSelected(itemWatchInfo.userBirthdaySolar == 1);
        llLunar.setSelected(itemWatchInfo.userBirthdaySolar != 1);

        if (itemWatchInfo.birthday != null && !itemWatchInfo.birthday.isEmpty()) {
            String strBirthday = Util.convertDateString(itemWatchInfo.birthday, false);
            tvBirthday.setText(strBirthday);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Util.getDateFromFormatString(strBirthday));
            mMonthDay = new MonthDay(calendar);
            if (llLunar.isSelected()) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, mMonthDay.getLunar().getLunarYearNum());
                calendar1.set(Calendar.MONTH, mMonthDay.getLunar().getLunarMonthNum() - 1);
                calendar1.set(Calendar.DAY_OF_MONTH, mMonthDay.getLunar().getLunarDayNum());
                Date date = calendar1.getTime();

                strBirthday = Util.getDateFormatString(date);
                tvBirthday.setText(strBirthday);
            }
            tvBirthday.setTextColor(Color.BLACK);
        }

        if (!itemWatchInfo.residence.isEmpty()) {
            tvResidence.setText(itemWatchInfo.residence);
            tvResidence.setTextColor(Color.BLACK);
        }

        if (!itemWatchInfo.address.isEmpty()) {
            editAddress.setText(itemWatchInfo.address);
            editAddress.setTextColor(Color.BLACK);
        }
    }

    @Override
    protected void setEventListener() {

        ivBack.setOnClickListener(this);
        tvContinue.setOnClickListener(this);
        edtName.setOnClickListener(this);
        edtPhone.setOnClickListener(this);
        tvBirthday.setOnClickListener(this);
        llMan.setOnClickListener(this);
        llWoman.setOnClickListener(this);
        llGregorian.setOnClickListener(this);
        llLunar.setOnClickListener(this);
        tvResidence.setOnClickListener(this);
        editAddress.setOnClickListener(this);

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemWatchInfo.name = editable.toString();
                checkValid();
            }
        });
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemWatchInfo.phone = editable.toString();
                checkValid();
            }
        });
        editAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemWatchInfo.address = editable.toString();
                checkValid();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_ADDRESS) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    itemWatchInfo.province = data.getStringExtra("province_data");
                    itemWatchInfo.city = data.getStringExtra("city_data");
                    itemWatchInfo.district = data.getStringExtra("district_data");
                    itemWatchInfo.residence = data.getStringExtra("address_data");
                    itemWatchInfo.lat = data.getStringExtra("lat_data");
                    itemWatchInfo.lon = data.getStringExtra("lon_data");
                    tvResidence.setText(itemWatchInfo.residence);
                }
            }
        } else if (requestCode == REQUEST_ADD_WATCH_INFO) {
            if (resultCode == RESULT_OK) {
                setResult(resultCode);
                finish();
            }
        } else {
            setResult(resultCode);
            finish();
        }
    }

    private void checkValid() {
        boolean bValid = false;
        if (!itemWatchInfo.name.isEmpty() || !itemWatchInfo.phone.isEmpty() || !itemWatchInfo.birthday.isEmpty()) {
            bValid = true;
        }

        if (bValid) {
            tvInputDesc.setVisibility(View.INVISIBLE);
        } else {
            tvInputDesc.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ID_IMGVIEW_BACK:
                finish();
                break;
            case R.id.ID_BTN_CONTINUE:
                onContinue();
                break;
            case R.id.ID_EDTTEXT_NAME:
                tvRequireName.setVisibility(View.GONE);
                break;
            case R.id.ID_EDTTEXT_PHONE:
                tvRequirePhone.setVisibility(View.GONE);
                break;
            case R.id.ID_TXTVIEW_BIRTHDAY:
                tvRequireBirthDay.setVisibility(View.GONE);
                onBirthday();
                break;
            case R.id.ID_LYT_MAN:
            case R.id.ID_LYT_WOMEN:
                tvRequireSex.setVisibility(View.GONE);
                onSelectSex(view);
                break;
            case R.id.ID_LL_GREGORIAN:
            case R.id.ID_LL_LUNAR:
                tvRequireBirthDay.setVisibility(View.GONE);
                onSelectCalendar(view);
                break;
            case R.id.ID_TXTVIEW_RESIDENCE:
                tvRequireResidence.setVisibility(View.GONE);
                onAddress();
                break;
            case R.id.ID_EDIT_ADDRESS:
                tvRequireAddress.setVisibility(View.GONE);
                break;
        }
    }

    private void onSelectCalendar(View view) {
        llGregorian.setSelected(false);
        llLunar.setSelected(false);

        view.setSelected(true);

        Date selectedDate = mMonthDay.getCalendar().getTime();
        String strBirthday = Util.getDateFormatString(selectedDate);
        itemWatchInfo.birthday = Util.convertDateString(strBirthday, true);
        itemWatchInfo.userBirthdaySolar = llGregorian.isSelected() ? 1 : 0;
        if (!llGregorian.isSelected()) {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.YEAR, mMonthDay.getLunar().getLunarYearNum());
            calendar1.set(Calendar.MONTH, mMonthDay.getLunar().getLunarMonthNum() - 1);
            calendar1.set(Calendar.DAY_OF_MONTH, mMonthDay.getLunar().getLunarDayNum());
            Date date = calendar1.getTime();

            strBirthday = Util.getDateFormatString(date);
            tvBirthday.setText(strBirthday);
            tvBirthday.setTextColor(Color.BLACK);
        }
        tvBirthday.setText(strBirthday);
        tvBirthday.setTextColor(Color.BLACK);
    }

    private void onAddress() {
        Intent intent = new Intent(this, ActivitySelectAddress.class);
        intent.putExtra("lat_data", itemWatchInfo.lat);
        intent.putExtra("lon_data", itemWatchInfo.lon);
        startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
    }

    private void onBirthday() {
//		final DatePickerDialog dlgDatePicker;
//		Calendar calendar = Calendar.getInstance();
//
//		dlgDatePicker = new DatePickerDialog(
//				this,
//				(view, year, month, dayOfMonth) -> {
//					Calendar calendar1 = Calendar.getInstance();
//					calendar1.set(Calendar.YEAR, year);
//					calendar1.set(Calendar.MONTH, month);
//					calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//					Date date = calendar1.getTime();
//
//					String strBirthday = Util.getDateFormatString(date);
//					itemWatchInfo.birthday = Util.convertDateString(strBirthday, true);
//					tvBirthday.setText(strBirthday);
//					tvBirthday.setTextColor(Color.BLACK);
//
//					checkValid();
//				},
//				calendar.get(Calendar.YEAR),
//				calendar.get(Calendar.MONTH),
//				calendar.get(Calendar.DAY_OF_MONTH)
//		);
//		dlgDatePicker.show();

        final BottomSheetDialog dialog = new BottomSheetDialog(ActivityWatchInfo.this);
        @SuppressLint("InflateParams") View parentView = getLayoutInflater().inflate(R.layout.dialog_date_picker, null);
        dialog.setContentView(parentView);
        ((View) parentView.getParent()).setBackgroundColor(Color.TRANSPARENT);

        Spinner yearSpinner = parentView.findViewById(R.id.ID_SPINNER_YEAR);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] yearList = new String[150];
        int index = 0, currentSelectedIndex = 0;
        for (int i = currentYear - 149; i <= currentYear; i++) {
            yearList[index] = String.valueOf(i);
            if (i == mMonthDay.getCalendar().get(Calendar.YEAR)) {
                currentSelectedIndex = index;
            }
            index++;
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter(this, R.layout.item_date_spinner, yearList);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(currentSelectedIndex);

        Spinner monthSpinner = parentView.findViewById(R.id.ID_SPINNER_MONTH);
        String[] monthList = getResources().getStringArray(R.array.month_array);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter(this, R.layout.item_date_spinner, monthList);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setSelection(mMonthDay.getCalendar().get(Calendar.MONTH));

        LunarView datePicker = parentView.findViewById(R.id.ID_LUNAR_VIEW);
        datePicker.setOnDatePickListener((view, monthDay) -> mMonthDay = monthDay);
        datePicker.goToMonthDay(mMonthDay.getCalendar().get(Calendar.YEAR),
                mMonthDay.getCalendar().get(Calendar.MONTH),
                mMonthDay.getCalendar().get(Calendar.DAY_OF_MONTH));

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                datePicker.goToMonthDay(Integer.parseInt(yearList[position]), monthSpinner.getSelectedItemPosition(), mMonthDay.getCalendar().get(Calendar.DAY_OF_MONTH));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                datePicker.goToMonthDay(Integer.parseInt(yearList[yearSpinner.getSelectedItemPosition()]), position, mMonthDay.getCalendar().get(Calendar.DAY_OF_MONTH));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView tvCancel = parentView.findViewById(R.id.ID_TXTVIEW_CANCEL);
        tvCancel.setOnClickListener(v -> dialog.dismiss());
        TextView tvConfirm = parentView.findViewById(R.id.ID_TXTVIEW_CONFIRM);
        tvConfirm.setOnClickListener(v -> {
            Date selectedDate = mMonthDay.getCalendar().getTime();
            String strBirthday = Util.getDateFormatString(selectedDate);
            itemWatchInfo.birthday = Util.convertDateString(strBirthday, true);
            if (!llGregorian.isSelected()) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, mMonthDay.getLunar().getLunarYearNum());
                calendar1.set(Calendar.MONTH, mMonthDay.getLunar().getLunarMonthNum() - 1);
                calendar1.set(Calendar.DAY_OF_MONTH, mMonthDay.getLunar().getLunarDayNum());
                Date date = calendar1.getTime();

                strBirthday = Util.getDateFormatString(date);
            }
            tvBirthday.setText(strBirthday);
            tvBirthday.setTextColor(Color.BLACK);

            dialog.dismiss();
        });

        dialog.show();
    }

    private void onSelectSex(View view) {
        llMan.setSelected(false);
        llWoman.setSelected(false);

        view.setSelected(true);

        itemWatchInfo.sex = llMan.equals(view) ? 1 : 0;
    }

    private void onContinue() {
        if (itemWatchInfo.name.isEmpty()) {
            tvRequireName.setVisibility(View.VISIBLE);
            edtName.requestFocus();
            return;
        }

        if (itemWatchInfo.phone.isEmpty()) {
            tvRequirePhone.setVisibility(View.VISIBLE);
            edtPhone.requestFocus();
            return;
        }
        if (itemWatchInfo.phone.length() != 11) {
            Util.ShowDialogError(R.string.str_phone_number_incorrect, new Util.ResultProcess() {
                @Override
                public void process() {
                    edtPhone.requestFocus();
                }
            });
            return;
        }

        if (itemWatchInfo.sex == -1) {
            tvRequireSex.setVisibility(View.VISIBLE);
            return;
        }

        if (itemWatchInfo.birthday.isEmpty()) {
            tvRequireBirthDay.setVisibility(View.VISIBLE);
            return;
        }

        if (itemWatchInfo.residence.isEmpty()) {
            tvRequireResidence.setVisibility(View.VISIBLE);
            return;
        }

        if (itemWatchInfo.address.isEmpty()) {
            tvRequireAddress.setVisibility(View.VISIBLE);
            editAddress.requestFocus();
            return;
        }

        itemWatchInfo.userRelation = relations[spinnerRelation.getSelectedItemPosition()];

        Intent intent = new Intent(this, ActivityAddInfo.class);
        intent.putExtra("device_data", itemWatchInfo);
        intent.putExtra("ill_list", illList);

        startActivityForResult(intent, REQUEST_ADD_WATCH_INFO);
    }

    private void getWatchSetInfo() {
        HttpAPI.getWatchSetInfo(new VolleyCallback() {
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

                    illList.clear();
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    JSONArray illArrayObj = dataObject.getJSONArray("ill_list");
                    for (int i = 0; i < illArrayObj.length(); i++) {
                        illList.add((String) illArrayObj.get(i));
                    }
                    birthDesc = dataObject.getString("watch_birth_desc");
                    tvContent.setText(birthDesc);
                } catch (JSONException e) {
                    Util.ShowDialogError(R.string.str_page_loading_failed);
                    return;
                }
            }

            @Override
            public void onError(Object error) {
                m_dlgProgress.dismiss();
                Util.ShowDialogError(R.string.str_page_loading_failed);
            }
        }, TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        App.Instance().cancelPendingRequests(TAG);
    }
}
