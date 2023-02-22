//@formatter:off
package com.iot.shoumengou.model;

import android.content.Context;

import com.iot.shoumengou.BuildConfig;
import com.iot.shoumengou.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ItemFence implements Serializable {
	public int						id;
	public String					name;
	public String					address;
	public String					lat;
	public String					lon;
	public int						radius;
	public String					strGuardTimeList;

	private boolean useNewGuard = true;

	public ItemFence() {
		this.id = 0;
		this.name = null;
		this.address = null;
		this.lat = null;
		this.lon = null;
		this.radius = 300;
		this.strGuardTimeList = null;
	}

	public void  setFence(ItemFence itemFence) {
		this.id = itemFence.id;
		this.name = itemFence.name;
		this.address = itemFence.address;
		this.lat = itemFence.lat;
		this.lon = itemFence.lon;
		this.radius = itemFence.radius;
		this.strGuardTimeList = itemFence.strGuardTimeList;
	}

	public String getRadius(Context context) {
		return String.format(context.getResources().getString(R.string.str_radius_format), radius);
	}

	public ArrayList<ItemFencePeriod> getFencePeriodList() {
		ArrayList<ItemFencePeriod> itemFencePeriodArrayList = new ArrayList<>();
		if (strGuardTimeList != null) {
			String[] guardTimeArray = strGuardTimeList.split(",");
			for (String guardTimes : guardTimeArray) {
				String[] fencePeriod = guardTimes.split("-");
				if (fencePeriod.length > 1) {
					ItemFencePeriod itemFencePeriod = new ItemFencePeriod(fencePeriod[0], fencePeriod[1]);
					itemFencePeriodArrayList.add(itemFencePeriod);
				}
			}
		}

		return itemFencePeriodArrayList;
	}

	public void setFencePeriodList(ArrayList<ItemFencePeriod> itemFencePeriodArrayList) {
		if (itemFencePeriodArrayList == null || itemFencePeriodArrayList.size() == 0) {
			return;
		}

		strGuardTimeList = "";
		for (ItemFencePeriod itemFencePeriod : itemFencePeriodArrayList) {
			if (itemFencePeriod.getPeriodString() != null) {
				strGuardTimeList += itemFencePeriod.getPeriodString() + ",";
			}
		}
	}

	public JSONObject getJSONObject() {
		JSONObject fenceObj = new JSONObject();
		try {
			fenceObj.put("name", name);
			fenceObj.put("address", address);
			fenceObj.put("lat", lat);
			fenceObj.put("lon", lon);
			fenceObj.put("radius", radius);
			if (useNewGuard) {
				if (strGuardTimeList != null) {
					JSONArray jsonArray = new JSONArray();
					String[] guardTimeArray = strGuardTimeList.split(",");
					for (String guardTimes : guardTimeArray) {
						String[] fencePeriod = guardTimes.split("-");
						if (fencePeriod.length > 1) {
							JSONObject guardTimeObj = new JSONObject();
							guardTimeObj.put("start_time", fencePeriod[0]);
							guardTimeObj.put("end_time", fencePeriod[1]);
							jsonArray.put(guardTimeObj);
						}
					}
					fenceObj.put("guardtime_list", jsonArray);
				}
			} else {
				fenceObj.put("guardtime_list", strGuardTimeList);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return fenceObj;
	}

	public ItemFence(JSONObject fenceObj) {
		try {
			this.name = fenceObj.getString("name");
			this.address = fenceObj.getString("address");
			this.lat = fenceObj.getString("lat");
			this.lon = fenceObj.getString("lon");
			this.radius = fenceObj.getInt("radius");
			if (useNewGuard) {
				this.strGuardTimeList = "";
				JSONArray jsonArray = fenceObj.getJSONArray("guardtime_list");
				for (int i=0; i<jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					String startTime = obj.getString("start_time");
					String endTime = obj.getString("end_time");
					strGuardTimeList += startTime + "-" + endTime + ",";
				}
			} else {
				this.strGuardTimeList = fenceObj.getString("guardtime_list");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
