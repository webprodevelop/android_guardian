/**************************************************************************
 *
 * Copyright (C) 2012-2015 Alex Taradov <alex@taradov.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *************************************************************************/

package com.iot.shoumengou.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iot.shoumengou.R;
import com.iot.shoumengou.model.ItemAlarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTime
{
  private Context mContext;
  private String[] mFullDayNames;
  private String[] mShortDayNames;
  private SimpleDateFormat mTimeFormat;
  private SimpleDateFormat mDateFormat;

  public DateTime(Context context)
  {
    mContext = context;
    update();
  }

  public void update()
  {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

    mDateFormat = new SimpleDateFormat("E MMM d, yyyy");

    mTimeFormat = new SimpleDateFormat("HH:mm");

    mFullDayNames = new String[7];
    mShortDayNames = new String[7];

    SimpleDateFormat fullFormat = new SimpleDateFormat("EEEE");
    SimpleDateFormat shortFormat = new SimpleDateFormat("E");
    Calendar calendar;

    calendar = new GregorianCalendar(2012, Calendar.AUGUST, 6);

    for (int i = 0; i < 7; i++)
    {
      mFullDayNames[i] = fullFormat.format(calendar.getTime());
      mShortDayNames[i] = shortFormat.format(calendar.getTime());
      calendar.add(Calendar.DAY_OF_WEEK, 1);
    }
    // only support chinese;
    mShortDayNames = new String[] {"周一","周二","周三","周四","周五","周六","周日"};
  }

  public String formatTime(ItemAlarm alarm)
  {
    return mTimeFormat.format(new Date(alarm.date));
  }

  public String formatDate(ItemAlarm alarm)
  {
    return mDateFormat.format(new Date(alarm.date));
  }

  public String formatDays(Context context, ItemAlarm alarm)
  {
    boolean[] days = getDays(alarm);
    String res = "";

    if (alarm.days == 0) {
      res = context.getResources().getString(R.string.str_no_repeat);
      return res;
    } else {
      if (days[0] && days[1] && days[2] && days[3] && days[4] && !days[5] && !days[6]) {
        res = context.getResources().getString(R.string.str_work_day);
        return res;
      }
      if (!days[0] && !days[1] && !days[2] && !days[3] && !days[4] && days[5] && days[6]) {
        res = context.getResources().getString(R.string.str_week_end);
        return res;
      }
      if (days[0] && days[1] && days[2] && days[3] && days[4] && days[5] && days[6]) {
        res = context.getResources().getString(R.string.str_week_daily);
        return res;
      }

      for (int i = 0; i < 7; i++)
        if (days[i])
          res += ("" == res) ? mShortDayNames[i] : ", " + mShortDayNames[i];
    }

    return res;
  }

  public boolean[] getDays(ItemAlarm alarm)
  {
    int offs = 0;
    boolean[] rDays = new boolean[7];
    int aDays = alarm.days;

    for (int i = 0; i < 7; i++)
      rDays[(i+offs) % 7] = (aDays & (1 << i)) > 0;

    return rDays;
  }

  public void setDays(ItemAlarm alarm, boolean[] days)
  {
    int offs = 0;
    int sDays = 0;

    for (int i = 0; i < 7; i++)
      sDays |= days[(i+offs) % 7] ? (1 << i) : (0 << i);

    alarm.setDays(sDays);
  }

  public String[] getFullDayNames()
  {
    return mFullDayNames;
  }

  public String[] getShortDayNames()
  {
    return mShortDayNames;
  }
}

