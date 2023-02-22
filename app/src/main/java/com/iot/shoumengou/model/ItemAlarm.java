//@formatter:off
package com.iot.shoumengou.model;

import android.content.Intent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ItemAlarm implements Comparable<ItemAlarm> {
	public long			id;
	public String		title;
	public long			date;
	public boolean		enabled;
	public int			days;
	public long			nextOccurence;
	public String		content;

	public ItemAlarm() {
		id = 0;
		title = "";
		date = System.currentTimeMillis();
		enabled = true;
		days = 0;
		content = "";

		update();
	}

	public void setDays(int days) {
		this.days = days;
		update();
	}

	public boolean getOutdated() {
		return nextOccurence < System.currentTimeMillis();
	}

	public void update() {
		Calendar now = Calendar.getInstance();

		Calendar alarm = Calendar.getInstance();

		alarm.setTimeInMillis(date);
		alarm.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

		if (days != 0) {
			while (true)
			{
				int day = (alarm.get(Calendar.DAY_OF_WEEK) + 5) % 7;

				if (alarm.getTimeInMillis() > now.getTimeInMillis() && (days & (1 << day)) > 0)
					break;

				alarm.add(Calendar.DAY_OF_MONTH, 1);
			}
		}

		nextOccurence = alarm.getTimeInMillis();

		date = nextOccurence;
	}

	public void toIntent(Intent intent) {
		intent.putExtra("alarm_id", id);
		intent.putExtra("alarm_title", title);
		intent.putExtra("alarm_date", date);
		intent.putExtra("alarm_alarm", enabled);
		intent.putExtra("alarm_days", days);
		intent.putExtra("alarm_content", content);
	}

	public void fromIntent(Intent intent) {
		id = intent.getLongExtra("alarm_id", 0);
		title = intent.getStringExtra("alarm_title");
		date = intent.getLongExtra("alarm_date", 0);
		enabled = intent.getBooleanExtra("alarm_alarm", true);
		days = intent.getIntExtra("alarm_days", 0);
		content = intent.getStringExtra("alarm_content");
		update();
	}

	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeLong(id);
		dos.writeUTF(title);
		dos.writeLong(date);
		dos.writeBoolean(enabled);
		dos.writeInt(days);
		dos.writeUTF(content);
	}

	public void deserialize(DataInputStream dis) throws IOException {
		id = dis.readLong();
		title = dis.readUTF();
		date = dis.readLong();
		enabled = dis.readBoolean();
		days = dis.readInt();
		content = dis.readUTF();
		update();
	}

	@Override
	public int compareTo(ItemAlarm aThat) {
		final long thisNext = nextOccurence;
		final long thatNext = aThat.nextOccurence;
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;

		if (this == aThat)
			return EQUAL;

		if (thisNext > thatNext)
			return AFTER;
		else if (thisNext < thatNext)
			return BEFORE;
		else
			return EQUAL;
	}
}
