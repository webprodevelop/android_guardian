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

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.iot.shoumengou.model.ItemAlarm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class AlarmDataSource
{
  private static final String TAG = "AlarmDataSource";

  @SuppressLint("StaticFieldLeak")
  private static final AlarmDataSource mDataSource = new AlarmDataSource();
  private static Context mContext = null;
  private static ArrayList<ItemAlarm> mList = null;

  private static final String DATA_FILE_NAME = "iot_alarm.txt";
  private static final long MAGIC_NUMBER = 0x54617261646f7641L;

  protected AlarmDataSource()
  {
  }

  public static synchronized AlarmDataSource getInstance(Context context)
  {
    if (mContext == null)
    {
      mContext = context.getApplicationContext();
      load();
    }
    return mDataSource;
  }

  private static void load()
  {
    Log.i(TAG, "DataSource.load()");

    mList = new ArrayList<ItemAlarm>();

    try
    {
      DataInputStream dis = new DataInputStream(mContext.openFileInput(DATA_FILE_NAME));
      long magic = dis.readLong();
      int size;

      if (MAGIC_NUMBER == magic)
      {
        size = dis.readInt();

        for (int i = 0; i < size; i++)
        {
          ItemAlarm alarm = new ItemAlarm();
          alarm.deserialize(dis);
          mList.add(alarm);
        }
      }

      dis.close();
    } catch (IOException ignored)
    {
    }
  }

  public static void save()
  {
    Log.i(TAG, "DataSource.save()");

    try
    {
      DataOutputStream dos = new DataOutputStream(mContext.openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE));

      dos.writeLong(MAGIC_NUMBER);
      dos.writeInt(mList.size());

      for (int i = 0; i < mList.size(); i++)
        mList.get(i).serialize(dos);

      dos.close();
    } catch (IOException ignored)
    {
    }
  }

  public static int size()
  {
    return mList.size();
  }

  public static ItemAlarm get(int position)
  {
    return mList.get(position);
  }

  public static void add(ItemAlarm alarm)
  {
    alarm.id = System.currentTimeMillis();
    mList.add(alarm);
    Collections.sort(mList);
    save();
  }

  public static void remove(int index)
  {
    mList.remove(index);
    save();
  }

  public static void update(ItemAlarm alarm)
  {
    alarm.update();
    Collections.sort(mList);
    save();
  }
}

