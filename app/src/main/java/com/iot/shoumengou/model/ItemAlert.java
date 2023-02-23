//@formatter:off
package com.iot.shoumengou.model;

import android.content.Context;

import com.iot.shoumengou.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemAlert {
	private Context		m_context;
	private String		m_sDate;
	private String		m_sGroup;
	private String		m_sDeviceSn;
	private String		m_sDeviceType;
	private String		m_sAlert;

	public ItemAlert(Context a_context) {
		m_context	= a_context;
		m_sDate		= "";
		m_sGroup	= "";
		m_sDeviceSn	= "";
		m_sAlert	= "";
	}

	public void SetDate			(String a_sDate)		{	m_sDate		= a_sDate;		}
	public void SetGroup		(String a_sGroup)		{	m_sGroup	= a_sGroup;		}
	public void SetDeviceSn		(String a_sDeviceSn)	{	m_sDeviceSn	= a_sDeviceSn;	}
	public void SetDeviceType	(String a_sDeviceType)	{	m_sDeviceType	= a_sDeviceType;}
	public void SetAlert		(String a_sAlert)		{	m_sAlert	= a_sAlert;		}

	public String GetDate		() {	return m_sDate;		}
	public String GetGroup		() {	return m_sGroup;	}
	public String GetDeviceSn	() 	{	return m_sDeviceSn;	}
	public String GetDeviceType	() {	return m_sDeviceType;}
	public String GetAlert		() {	return m_sAlert;	}

	public void SetFromJson(JSONObject a_jsonAlert) {
		long lDate	= 0;
		JSONObject		jsonObject;
		Date			date;
		SimpleDateFormat	sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {	m_sAlert		= a_jsonAlert.getString		("alarmStr");			}	catch (JSONException e) {}
		try {	m_sGroup		= a_jsonAlert.getString		("groupName");		}	catch (JSONException e) {}
		try {	m_sDeviceSn		= a_jsonAlert.getString		("sn");				}	catch (JSONException e) {}
		try {	m_sDeviceType	= a_jsonAlert.getString		("type");				}	catch (JSONException e) {}
		try {
			jsonObject	= a_jsonAlert.getJSONObject("eventDate");
			lDate		= jsonObject.getLong("time");
			date		= new Date(lDate);
			m_sDate = sdf.format(date);
		} catch (JSONException e) {}
		if (m_sGroup.isEmpty())
			m_sGroup = m_context.getResources().getString(R.string.str_my_device);
	}

}
