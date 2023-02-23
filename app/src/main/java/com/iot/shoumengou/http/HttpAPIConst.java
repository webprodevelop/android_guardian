//@formatter:off
package com.iot.shoumengou.http;

public class HttpAPIConst {
//	public static final String	URL_API				= "https://apptest.shoumengou.com:4433/IOTCMS/guardian_api.do";
//	public static final String	URL_API				= "https://app.shoumengou.com/IOTCMS/guardian_api.do";
	public static final String	URL_API				= "http://192.168.10.104:8084/IOTCMS/guardian_api.do";
	public static final String	URL_CAMERA			= "https://open.ys7.com/api/lapp/live/address/limited";

	public static final String	CONTENT_TYPE		= "application/x-www-form-urlencoded;charset=UTF-8";
	public static final int		RESP_CODE_SUCCESS	= 200;
	public static class RespCode {
		public static final int SUCCESS = 200;
		public static final int OTHER = 300;
		public static final int TOKEN_BLANK = 301;
		public static final int API_NOT_EXIST = 302;
		public static final int API_PARAM_BLANK = 303;
		public static final int ACTION_BLANK = 304;
		public static final int DB_NO_FIND = 305;
		public static final int LOGIC_ERROR = 306;
		public static final int NO_PRIORITY = 307;
		public static final int NO_DEVICE = 308;
		public static final int NO_ALARM_TYPE = 309;
		public static final int NO_ALARM = 310;
		public static final int NO_ALARM_TASK = 311;
		public static final int NO_ALARM_TASK_SUB = 312;
		public static final int DUPLICATE_ALARM = 313;
		public static final int PROCESSED_ALARM = 314;

		public static final int PHONE_BLANK = 201;
		public static final int PHONE_INVAILD = 202;
		public static final int PHONE_FAIL = 203;
		//    public static final int PHONE_NOT_EXIST = 205;
		public static final int PHONE_REGISTERED = 206;
		public static final int PASSWORD_BLANK = 211;
		public static final int PASSWORD_INVAILD = 212;
		public static final int PASSWORD_FAIL = 213;
		public static final int PASSWORD_CONFORM_NOT_SAME = 215;
		public static final int PASSWORD_NEW_SAME = 216;
		public static final int PASSWORD_OLD_FAIL = 217;
		public static final int VALIDATE_CODE_BLANK = 221;
		public static final int VALIDATE_CODE_INVALID = 222;
		public static final int VALIDATE_CODE_FAIL = 223;
		public static final int VALIDATE_CODE_EXPIRED = 224;
		public static final int ITEM_ID_BLANK = 231;
		public static final int ITEM_ID_INVALID = 232;
		public static final int ITEM_ID_FAIL = 233;
		public static final int ITEM_TYPE_INVALID = 234;
		public static final int ITEM_OTHER_REGISTERED = 235;
		public static final int WATCH_ID_BLANK = 236;
		public static final int WATCH_ID_INVALID = 237;
		public static final int ROLE_NAME_EXIST = 241;
		public static final int ROLE_USED = 242;
		public static final int ACCOUNT_BLANK = 261;
		public static final int ACCOUNT_INVALID = 262;
		public static final int ACCOUNT_FAIL = 263;
		public static final int ACCOUNT_OTHER_LOGINED = 265;
		public static final int ACCOUNT_REGISTERED = 266;
		public static final int ACCOUNT_NOT_EXIST = 267;
		public static final int ACCOUNT_EXPIRED = 268;

		public static final int CHAT_ROOM_BUSY = 401;
		public static final int CHAT_KEFU_BUSY = 402;
		public static final int DEVICE_STATUS_OFFLINE = 411;
		public static final int DEVICE_STATUS_ALARM = 412;
		public static final int WATCH_STATUS_TAKEOFF = 416;

		public static final int PAY_FAILED = 500;
		public static final int PAY_PRE_FAILED = 501;
		public static final int PAY_NOT_EXIST = 502;
		public static final int PAY_IN_PROCESS = 503;

		public static final int WATCH_NOT_TAKE_ON = 601;
	}
}

