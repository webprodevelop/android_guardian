//@formatter:off
package com.iot.shoumengou.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterRescue;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemRescue;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityRescueDetail extends ActivityBase implements OnClickListener {
	private final static String	TAG = "ActivityRescueQuery";

	private ImageView ivBack;
	ListView lvDetails;
	AdapterRescue adapterRescue;
	private final ArrayList<ItemRescue> rescueList = new ArrayList<>();

	private int rescueId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_rescue_detail);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		rescueId = getIntent().getIntExtra("rescue_id", -1);

		if (rescueId == -1) {
			finish();
			return;
		}

		initControls();
		setEventListener();

		getRescueDetail();
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMG_BACK);

		lvDetails = findViewById(R.id.ID_LSTVIEW);
		adapterRescue = new AdapterRescue(this, rescueList);
		lvDetails.setAdapter(adapterRescue);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);
	}

	private void getRescueDetail() {
		rescueList.clear();
		m_dlgProgress.show();
		HttpAPI.getRescueDetail(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), rescueId, new VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				m_dlgProgress.dismiss();
				try {
					JSONObject jsonObject = new JSONObject(result);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					JSONObject dataObject = jsonObject.optJSONObject("data");
					if (dataObject != null) {
						ItemRescue itemRescue = new ItemRescue();
						itemRescue.label = getString(R.string.str_alert_time);
						itemRescue.rescueTime = dataObject.optString("rescue_create_time");
						rescueList.add(itemRescue);

						JSONArray volunteers = dataObject.optJSONArray("volunteer_activity");
						if (volunteers != null) {
							for (int i = 0; i < volunteers.length(); i++) {
								JSONObject object = volunteers.getJSONObject(i);
								ItemRescue itemRescue1 = new ItemRescue();
								itemRescue1.label = getString(R.string.str_short_receive_time) + "(" + object.optString("phone") + ")";
								itemRescue1.rescueTime = object.optString("start_time");
								rescueList.add(itemRescue1);

								ItemRescue itemRescue2 = new ItemRescue();
								itemRescue2.label = getString(R.string.str_short_end_time) + "(" + object.optString("phone") + ")";
								itemRescue2.rescueTime = object.optString("end_time");
								rescueList.add(itemRescue2);
							}
						}

						ItemRescue itemRescue1 = new ItemRescue();
						itemRescue1.label = getString(R.string.str_agency_time);
						itemRescue1.rescueTime = dataObject.optString("rescue_center_finish_time");
						rescueList.add(itemRescue1);

						ItemRescue itemRescue2 = new ItemRescue();
						itemRescue2.label = getString(R.string.str_end_time);
						itemRescue2.rescueTime = dataObject.optString("rescue_finish_time");
						rescueList.add(itemRescue2);

						adapterRescue.notifyDataSetChanged();
					}
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_login_failed);
				}
			}

			@Override
			public void onError(Object error) {
				m_dlgProgress.dismiss();
				Util.ShowDialogError(R.string.str_login_failed);
			}
		}, TAG);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ID_IMG_BACK) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		App.Instance().cancelPendingRequests(TAG);
	}
}
