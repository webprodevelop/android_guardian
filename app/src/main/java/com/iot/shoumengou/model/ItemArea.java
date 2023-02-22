//@formatter:off
package com.iot.shoumengou.model;

import java.util.ArrayList;

public class ItemArea{
	public String areaCode;
	public int areaLevel;
	public String areaName;
	public String cityCenter;
	public String cityCode;
	public ArrayList<ItemArea> list;

	public ItemArea() {
		areaCode = "";
		areaLevel = 0;
		areaName = "";
		cityCenter = "";
		cityCode = "";
		list = new ArrayList<>();
	}
}
