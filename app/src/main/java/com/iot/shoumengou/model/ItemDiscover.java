//@formatter:off
//@formatter:off
package com.iot.shoumengou.model;

import org.json.JSONObject;

import java.io.Serializable;

public class ItemDiscover implements Serializable {
	public String	updatedTime;
	public String	releaseTime;
	public String	statusStr;
	public String	createDateStr;
	public String	updatedTimeStr;
	public String	title;
	public String	releaseTimeStr;
	public String	content;
	public String	picture;
	public String	newsType;
	public String	publishTo;
	public String	newsBranch;
	public String	updatedDateStr;
	public String	createdTime;
	public String	readCnt;
	public int		id;
	public String	createdTimeStr;
	public boolean	status;

	public ItemDiscover(JSONObject dataObject) {
		updatedTime = dataObject.optString("updatedTime");
		releaseTime = dataObject.optString("releaseTime");
		statusStr = dataObject.optString("statusStr");
		createDateStr = dataObject.optString("createDateStr");
		updatedTimeStr = dataObject.optString("updatedTimeStr");
		title = dataObject.optString("title");
		releaseTimeStr = dataObject.optString("releaseTimeStr");
		content = dataObject.optString("content");
		picture = dataObject.optString("picture");
		newsType = dataObject.optString("newsType");
		publishTo = dataObject.optString("publishTo");
		newsBranch = dataObject.optString("newsBranch");
		updatedDateStr = dataObject.optString("updatedDateStr");
		createdTime = dataObject.optString("createdTime");
		readCnt = dataObject.optString("readCnt");
		createdTimeStr = dataObject.optString("createdTimeStr");
		id = dataObject.optInt("id");
		status = dataObject.optBoolean("status");
	}
}

