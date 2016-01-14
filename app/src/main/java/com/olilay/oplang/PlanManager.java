package com.olilay.oplang;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oliver Layer on 10.11.2015.
 */
public class PlanManager
{
    private boolean isTeacher;
    private String cookie;

    private Plan[] plans;
    private PlanFragment[] planFragments;

    private boolean executedRelogin;

    private Activity activity;
    private Context context;


    public PlanManager(String cookie, boolean isTeacher, PlanFragment[] planFragments, Activity activity)
    {
        this.cookie = cookie;
        this.isTeacher = isTeacher;
        this.activity = activity;
        this.planFragments = planFragments;

        this.context = activity;
    }

    public PlanManager(boolean isTeacher, Context context) //Service version: for push notif, only supports PlanData
    {
        this.isTeacher = isTeacher;
        this.context = context;
    }


    private void initPlans(boolean isTeacher)
    {
        plans = new Plan[2];

        if(planFragments == null)
        {
            planFragments = new PlanFragment[2];
        }

        plans[0] = new Plan(cookie, true, isTeacher, planFragments[0], this);
        plans[1] = new Plan(cookie, false, isTeacher, planFragments[1], this);
    }


    public void initPlans()
    {
        initPlans(isTeacher);
    }

    public void refreshPlanImages()
    {
        plans[0].refreshPlanImage();
        plans[1].refreshPlanImage();
    }

    public void refreshPlanData(boolean showOnlyTomorrow)
    {
        if(Settings.getDailyPush(context) || Settings.getSpecialPush(context))
        {
            plans[0].refreshPlanData(showOnlyTomorrow);
            plans[1].refreshPlanData(showOnlyTomorrow);
        }
    }

    public void changePlans()
    {
        isTeacher = !isTeacher; //invert boolean: teachers will toggle to pupils, and back if they want to
        initPlans(isTeacher);
        refreshPlanImages();
    }


    public void onPlanImageRefreshed(Bitmap bitmap, Plan plan)
    {

    }

    public void onPlanDataRefreshed(PlanNotification planNotification, Plan plan)
    {
        planNotification.push();
    }

    public void relogin()
    {
        if(!executedRelogin) //prevent double re-login (cause both planImages will call this)
        {
            Log.v("OPlanG", "Executing relogin!");

            executedRelogin = true;

            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent); //stats the LoginActivity again, because the user needs a new session
        }

    }


    public Activity getActivity()
    {
        return activity;
    }

    public Context getContext()
    {
        return context;
    }
}
