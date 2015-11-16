package com.olilay.oplang;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Oliver Layer on 01.10.2015.
 */
public class PlanService extends Service
{
        public IBinder onBind(Intent intent)
        {
            return null;
        }

        @Override
        public void onCreate()
        {
            Log.v("OPlanG", System.currentTimeMillis() + ": OPlanG Service wurde erstellt.");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId)
        {
            Log.v("OPlanG", System.currentTimeMillis() + ": OPlanG Service wurde gestartet.");

            boolean specialPush = intent.getBooleanExtra("specialPush", true);
            boolean isTeacher = intent.getBooleanExtra("isTeacher", false);

            PlanManager planManager = new PlanManager(isTeacher, this);
            planManager.initPlans();
            planManager.refreshPlanData(!specialPush);

            stopSelf();

            return START_NOT_STICKY;
        }


        // Special Push service
        public static void setAlarmManager(int mins, Context context)
        {
            Intent serviceIntent = new Intent(context, PlanService.class);
            serviceIntent.putExtra("specialPush", true);
            serviceIntent.putExtra("isTeacher", Settings.getIsTeacher(context));
            PendingIntent servicePendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);

            long interval = DateUtils.MINUTE_IN_MILLIS * mins;
            long firstStart = System.currentTimeMillis() + interval;

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            am.cancel(servicePendingIntent);
            am.setInexactRepeating(AlarmManager.RTC, firstStart, interval, servicePendingIntent);

            Log.v("OPlanG", "Alarm gesetzt.");
        }

        // Daily Push service
        public static void setAlarmManager(Context context)
        {
            Intent serviceIntent = new Intent(context, PlanService.class);
            serviceIntent.putExtra("specialPush", false);
            serviceIntent.putExtra("isTeacher", Settings.getIsTeacher(context));
            PendingIntent servicePendingIntent = PendingIntent.getService(context, 1, serviceIntent, 0);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Settings.getDailyPushTime(context)[0]);
            calendar.set(Calendar.MINUTE, Settings.getDailyPushTime(context)[1]);

            am.cancel(servicePendingIntent);

            if(calendar.getTimeInMillis() < new Date().getTime()) //if the selected time took already place today
            {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, servicePendingIntent);

            Log.v("OPlanG", "Daily Alarm gesetzt.");
        }
}
