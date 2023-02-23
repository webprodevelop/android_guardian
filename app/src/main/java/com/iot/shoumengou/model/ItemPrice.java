//@formatter:off
package com.iot.shoumengou.model;

public class ItemPrice {
	public String		label;
	public String 		value;
	public String 		key;

	public ItemPrice() {

	}

	public ItemPrice(String label, String value, String key) {
		this.label = label;
		this.value = value;
		this.key = key;
	}
}
