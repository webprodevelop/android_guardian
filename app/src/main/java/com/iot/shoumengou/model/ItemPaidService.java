//@formatter:off
package com.iot.shoumengou.model;

import java.io.Serializable;

public class ItemPaidService implements Serializable {
	public int			orderId;
	public String		type;
	public int  		deviceId;
	public String  		userPhone;
	public double		amount;
	public int			payType;
	public int			serviceYears;
	public String		serviceStartDate = "";
	public String		serviceEndDate = "";
	public String		payTime;
}
