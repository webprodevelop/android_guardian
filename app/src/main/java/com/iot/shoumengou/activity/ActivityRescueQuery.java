//@formatter:off
package com.iot.shoumengou.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.iot.shoumengou.App;
import com.iot.shoumengou.Prefs;
import com.iot.shoumengou.R;
import com.iot.shoumengou.adapter.AdapterRescue;
import com.iot.shoumengou.adapter.PaginationAdapter;
import com.iot.shoumengou.http.HttpAPI;
import com.iot.shoumengou.http.HttpAPIConst;
import com.iot.shoumengou.http.VolleyCallback;
import com.iot.shoumengou.model.ItemRescue;
import com.iot.shoumengou.util.CustomLinearLayoutManager;
import com.iot.shoumengou.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityRescueQuery extends ActivityBase implements OnClickListener {
	private final static String	TAG = "ActivityRescueQuery";

	ImageView ivBack;
	ListView lvHeartRate;
	AdapterRescue adapterRescue;
	private final ArrayList<ItemRescue> rescueList = new ArrayList<>();
	private final ArrayList<ItemRescue> filteredList = new ArrayList<>();

	private final ArrayList<String> paginationItems = new ArrayList<>();
	private PaginationAdapter paginationAdapter;

	private int pageSize = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_rescue_query);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			getWindow().setStatusBarColor(getColor(R.color.color_tab_selected));
		}

		int padding = getResources().getDimensionPixelSize(R.dimen.dim_gap_7);
		int itemHeight = getResources().getDimensionPixelSize(R.dimen.dim_gap_6);
		pageSize = (getResources().getDisplayMetrics().heightPixels - padding * 4) / itemHeight;

		initControls();
		setEventListener();

		getRescueList();
	}

	@Override
	protected void initControls() {
		super.initControls();

		ivBack = findViewById(R.id.ID_IMG_BACK);

		lvHeartRate = findViewById(R.id.ID_LSTVIEW);
		adapterRescue = new AdapterRescue(this, filteredList);
		lvHeartRate.setAdapter(adapterRescue);

		com.iot.shoumengou.view.CustomRecyclerView paginationView = findViewById(R.id.ID_PAGINATION_VIEW);
		paginationView.setLayoutManager(new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		paginationAdapter = new PaginationAdapter(this, paginationItems, "");
		paginationView.setAdapter(paginationAdapter);
	}

	@Override
	protected void setEventListener() {
		ivBack.setOnClickListener(this);

		lvHeartRate.setOnItemClickListener((parent, view, position, id) -> {
			Intent intent = new Intent(ActivityRescueQuery.this, ActivityRescueDetail.class);
			intent.putExtra("rescue_id", filteredList.get(position).rescueId);
			startActivity(intent);
		});
	}

	public void rebuildPagination(int currentPageNumber) {
		filteredList.clear();

		for (int i = 0; i < rescueList.size(); i++) {
			if (i >= ((currentPageNumber - 1) * pageSize) && i < currentPageNumber * pageSize) {
				filteredList.add(rescueList.get(i));
			}
		}

		adapterRescue.notifyDataSetChanged();

		getPagination(currentPageNumber);
	}

	private void getPagination(int currentPageNumber) {
		int eachSide = 1;
		int startPage;
		int pageCount = rescueList.size() / pageSize + (rescueList.size() % pageSize > 0 ? 1 : 0);
		int endPage;
		if (pageCount <= 2 * eachSide + 5) {
			startPage = 1;
			endPage = pageCount;
		}
		else if (currentPageNumber < eachSide * 3) {
			startPage = 1;
			endPage = 2 * eachSide + 3;
		}
		else if (currentPageNumber >= pageCount - (eachSide + 2)) {
			startPage = currentPageNumber - 2 * eachSide - 2;
			endPage = pageCount;
		}
		else {
			startPage = currentPageNumber - eachSide;
			endPage = currentPageNumber + eachSide;
		}

		paginationItems.clear();
		if (startPage > 1) {
			paginationItems.add("1");
		}
		if (startPage > 2) {
			paginationItems.add("...");
		}
		if (startPage <= endPage) {
			for (int i = startPage; i < endPage + 1; i++) {
				paginationItems.add(String.valueOf(i));
			}
		}
		if (endPage < pageCount - 1) {
			paginationItems.add("...");
		}
		if (endPage < pageCount) {
			paginationItems.add(String.valueOf(pageCount));
		}
		if (currentPageNumber > 1) {
			paginationItems.add(0, "<");
		}
		if (currentPageNumber < pageCount) {
			paginationItems.add(">");
		}

		paginationAdapter.notifyDataSetChanged();
	}

	private void getRescueList() {
		HttpAPI.getRescueList(Prefs.Instance().getUserToken(), Prefs.Instance().getUserPhone(), new VolleyCallback() {
			@Override
			public void onSuccess(String result) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					int iRetCode = jsonObject.getInt("retcode");
					if (iRetCode != HttpAPIConst.RESP_CODE_SUCCESS) {
						String sMsg = jsonObject.getString("msg");
						Util.ShowDialogError(sMsg);
						return;
					}

					JSONArray jsonArray = jsonObject.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject1 = jsonArray.getJSONObject(i);
						ItemRescue itemRescue = new ItemRescue();
						itemRescue.rescueId = jsonObject1.optInt("rescue_id");
						itemRescue.label = jsonObject1.optString("rescue_content");
						itemRescue.rescueTime = jsonObject1.optString("rescue_time");

						rescueList.add(itemRescue);
					}

					rebuildPagination(1);
				}
				catch (JSONException e) {
					Util.ShowDialogError(R.string.str_login_failed);
				}
			}

			@Override
			public void onError(Object error) {
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
