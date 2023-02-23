package com.iot.shoumengou.fragment.discover;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.activity.ActivityScan;
import com.iot.shoumengou.adapter.AdapterWatchInfo;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemWatchInfo;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FragmentWardList extends Fragment implements View.OnClickListener  {
    ImageView ivBack;
    TextView tvConfirm, tvNoContent;
    ListView lvWardInfo;
    AdapterWatchInfo adapterWatchInfo;
    private final ArrayList<ItemWatchInfo> mWatchArray = new ArrayList<>();

    public FragmentWardList() {
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
        ViewGroup rootView =  (ViewGroup)inflater.inflate(R.layout.fragment_ward_list, container, false);

        initControl(rootView);
        setEventListener();

        checkWatchEntries();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
	    super.onAttach(context);
    }

    private void initControl(View layout) {
        ivBack = layout.findViewById(R.id.ID_IMG_BACK);
        tvConfirm = layout.findViewById(R.id.ID_TEXT_CONFIRM);
        tvNoContent = layout.findViewById(R.id.ID_TEXT_NO_CONTENT);
        tvNoContent.setVisibility(View.GONE);

        lvWardInfo = layout.findViewById(R.id.ID_LIST_VIEW_WARD);
        adapterWatchInfo = new AdapterWatchInfo(getActivity(), mWatchArray);
        lvWardInfo.setAdapter(adapterWatchInfo);
        lvWardInfo.setDivider(null);
    }

    private void setEventListener() {
        ivBack.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        lvWardInfo.setOnItemClickListener((parent, view, position, id) -> {
            ItemWatchInfo info = mWatchArray.get(position);
            Util.setMoniteringWatchInfo(info);

            adapterWatchInfo.notifyDataSetChanged();
        });
    }
    
    private void checkWatchEntries() {
        mWatchArray.clear();
        mWatchArray.addAll(Util.getAllWatchEntries());
        
        if (mWatchArray.size() > 0) {
            tvNoContent.setVisibility(View.GONE);
            adapterWatchInfo.notifyDataSetChanged();
        }
        else {
		    Util.clearWatchTable();
            HttpAPI.getWatchList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
                @Override
                public void onSuccess(String response) {
                    //m_dlgProgress.dismiss();
                    mWatchArray.clear();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int iRetCode = jsonObject.getInt("retcode");
                        if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
                            String sMsg = jsonObject.getString("msg");
                            Util.ShowDialogError(sMsg);
                            return;
                        }

                        JSONArray dataArrayObject = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArrayObject.length(); i++) {
                            JSONObject dataObject = dataArrayObject.getJSONObject(i);
			                ItemWatchInfo itemWatchInfo = new ItemWatchInfo(dataObject);
                            Util.addWatchEntry(itemWatchInfo);
                            mWatchArray.add(itemWatchInfo);
                            if (Util.monitoringWatchId == itemWatchInfo.id) {
                                Util.setMoniteringWatchInfo(itemWatchInfo);
                            }
                        }

                        if (mWatchArray.size() > 0) {
                            tvNoContent.setVisibility(View.GONE);
                        }
                        else {
                            tvNoContent.setVisibility(View.VISIBLE);
                        }
                        adapterWatchInfo.notifyDataSetChanged();
                    }
                    catch (JSONException e) {
                        Util.ShowDialogError(R.string.str_page_loading_failed);
                    }
                }

                @Override
                public void onError(Object error) {
                    //m_dlgProgress.dismiss();
                    Util.ShowDialogError(R.string.str_page_loading_failed);
                }
            }, FragmentWardList.class.getSimpleName());
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        FragmentParentDiscover parentFrag = ((FragmentParentDiscover) FragmentWardList.this.getParentFragment());
        switch (v.getId()) {
            case R.id.ID_IMG_BACK:
                Objects.requireNonNull(parentFrag).popChildFragment(true);
                break;
            case R.id.ID_TEXT_CONFIRM:
                Intent intent = new Intent(getActivity(), ActivityScan.class);
                intent.putExtra("device_type", "");
                Objects.requireNonNull(getActivity()).startActivity(intent);
                break;
        }
    }
}
