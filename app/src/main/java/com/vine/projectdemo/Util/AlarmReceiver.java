package com.vine.projectdemo.Util;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bData = intent.getExtras();
        if(bData.get("title").equals("SchoolMap"))
        {
            Log.d("AlarmReceiver", "onReceive");

            Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+8:00"));
            cal.set(Calendar.SECOND, 1);
            SetAlarm(context, cal);
        }
    }

    public static void SetAlarm(Context context, Calendar cal) {
        Intent intent = new Intent(context, AlarmReceiver.class);

        intent.addCategory("ID." + String.valueOf(cal.get(Calendar.MONTH)) + "."
                + String.valueOf(cal.get(Calendar.DATE)) + "-"
                + String.valueOf((cal.get(Calendar.HOUR_OF_DAY) )) + "."
                + String.valueOf(cal.get(Calendar.MINUTE)) + "."
                + String.valueOf(cal.get(Calendar.SECOND)));
        String AlarmTimeTag = "AlarmTime " + String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":"
                + String.valueOf(cal.get(Calendar.MINUTE)) + ":"
                + String.valueOf(cal.get(Calendar.SECOND));
        intent.putExtra("title", "SchoolMap");
        intent.putExtra("time", AlarmTimeTag);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
    }

}