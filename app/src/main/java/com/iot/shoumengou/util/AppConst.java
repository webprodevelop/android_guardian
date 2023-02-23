//@formatter:off
package com.iot.shoumengou.util;

public class AppConst {
	// Activity Request Code
	public static final int		REQUEST_PERMISSION_CAMERA	= 100;
	public static final int		REQUEST_PERMISSION_LOCATION	= 101;
	public static final int		REQUEST_PERMISSION_STORAGE	= 102;

	// Activity Extra Param
	public static final String	EXTRA_APPKEY		= "appkey";
	public static final String	EXTRA_ACCESSTOKEN	= "accesstoken";
	public static final String	EXTRA_SN			= "sn";

	public static final String	ACTION_PUSH_RECEIVED		= "com.iot.shoumengou.receiver.Push";
	public static final String	ACTION_ALARM				= "com.iot.shoumengou.receiver.Alarm";
}
