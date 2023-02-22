//@formatter:off
package com.iot.shoumengou.model;

import java.util.ArrayList;

public class ItemPriceList {
	public String		label;
	public ArrayList<ItemPrice> priceList;
	public String 		type;

	public ItemPriceList() {

	}

	public ItemPriceList(ArrayList<ItemPrice> list, String label, String type) {
		this.label = label;
		this.priceList = priceList;
		this.type = type;
	}
}
