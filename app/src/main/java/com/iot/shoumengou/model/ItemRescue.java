//@formatter:off
package com.iot.shoumengou.model;

public class ItemRescue {
	public int			rescueId;
	public String		label;
	public String 		rescueTime;

	public ItemRescue() {

	}

	public ItemRescue(int rescueId,
					  String label,
					  String rescueTime,
					  int status) {
		this.rescueId = rescueId;
		this.label = label;
		this.rescueTime = rescueTime;
	}
}
