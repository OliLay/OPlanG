package com.olilay.oplang;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Oliver Layer on 11.10.2015.
 */
public class BootReceiver extends BroadcastReceiver {

    //The boot receiver will be called when the android device starts. (called in manifest)

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(Settings.getSpecialPush(context))
        {
            PlanService.setAlarmManager(Settings.getInterval(context), context);
        }

        if(Settings.getDailyPush(context))
        {
            PlanService.setAlarmManager(context);
        }
    }
}