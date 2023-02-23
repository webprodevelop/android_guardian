//@formatter:off
package com.iot.shoumengou.model;

import android.content.Intent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyServiceInfo {
	public int amount;
	public int financialId;
	public String createTime;
	public String serviceEnd;
	public String userName;
	public int deviceType;
	public String deviceSerial;
	public String freeServiceStart;
	public String freeServiceEnd;
	public int serviceYears;
	public int payType;
	public int id;
	public int customerId;
	public int orderId;
	public String serviceStart;
	public boolean status;

	public MyServiceInfo() {

	}

	public MyServiceInfo(int amount,
						 int financialId,
						 String createTime,
						 String serviceEnd,
						 String userName,
						 int deviceType,
						 String deviceSerial,
						 String freeServiceStart,
						 String freeServiceEnd,
						 int serviceYears,
						 int payType,
						 int id,
						 int customerId,
						 int orderId,
						 String serviceStart,
						 boolean status) {
		this.amount = amount;
		this.financialId = financialId;
		this.createTime = createTime;
		this.serviceEnd = serviceEnd;
		this.userName = userName;
		this.deviceType = deviceType;
		this.deviceSerial = deviceSerial;
		this.freeServiceStart = freeServiceStart;
		this.serviceYears = serviceYears;
		this.payType = payType;
		this.id = id;
		this.customerId = customerId;
		this.orderId = orderId;
		this.serviceStart = serviceStart;
		this.status = status;
	}

	public void toIntent(Intent intent) {

	}

	public void fromIntent(Intent intent) {

	}

	public void serialize(DataOutputStream dos) throws IOException {

	}

	public void deserialize(DataInputStream dis) throws IOException {

	}
}
