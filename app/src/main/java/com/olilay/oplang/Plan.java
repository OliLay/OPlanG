package com.olilay.oplang;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Oliver Layer on 22.08.2015.
 */
public class Plan
{
    private boolean today;
    private boolean isTeacher;
    private String cookie;

    private PlanData planData;
    private PlanImage planImage;
    private PlanManager planManager;

    private PlanFragment planFragment;


    public Plan(String cookie, boolean today, boolean isTeacher, PlanFragment planFragment, PlanManager planManager)
    {
        this.today = today;
        this.isTeacher = isTeacher;
        this.cookie = cookie;

        this.planFragment = planFragment;
        this.planManager = planManager;
    }


    public void refreshPlanImage()
    {
        planFragment.beginLoading(planManager.getActivity(), planManager);

        planImage = new PlanImage(cookie, this);
    }

    public void refreshPlanData(boolean showOnlyTomorrow)
    {
        if(Settings.getIsTeacher(planManager.getContext()))
        {
            planData = new TeacherPlanData(this, showOnlyTomorrow, planManager.getContext());
        }
        else
        {
            planData = new PupilPlanData(this, showOnlyTomorrow, planManager.getContext());
        }
    }


    public void onPlanImageRefreshed(Bitmap bitmap)
    {
        planFragment.finishLoading(planManager.getActivity(), bitmap); //this will set the new bitmap as image in the TouchImageView

        planManager.onPlanImageRefreshed(bitmap, this);
    }

    public void onPlanDataRefreshed(PlanNotification planNotification, Plan plan)
    {
        planManager.onPlanDataRefreshed(planNotification, plan);
    }


    public boolean isToday()
    {
        return today;
    }

    public boolean isTeacher()
    {
        return isTeacher;
    }

    public PlanManager getPlanManager() { return planManager; }

    public PlanData getPlanData() { return planData; }
}
