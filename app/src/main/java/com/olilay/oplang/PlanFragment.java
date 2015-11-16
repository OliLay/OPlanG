package com.olilay.oplang;

import android.app.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.Touch;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Date;
import java.util.List;

/**
 * Created by Oliver Layer on 14.09.2015.
 */
public class PlanFragment extends Fragment
{
    private boolean today;

    private ProgressBar progressBar;
    private TouchImageView imageView;

    public static PlanFragment newInstance(boolean today)
    {
        PlanFragment fragment = new PlanFragment();

        final Bundle args = new Bundle(3);

        if(today)
        {
            args.putInt("layout", R.layout.fragment_plan_today);
        }
        else
        {
            args.putInt("layout", R.layout.fragment_plan_tomorrow);
        }

        args.putBoolean("isToday", today);
        args.putBoolean("isPlan", true);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        today = getArguments().getBoolean("isToday");
    }


    protected void findControls(Activity activity)
    {
        if(today)
        {
            imageView = (TouchImageView)activity.findViewById(R.id.iv_plan_today);
            progressBar = (ProgressBar) activity.findViewById(R.id.pg_PlanToday);
        }
        else
        {
            imageView =(TouchImageView)activity.findViewById(R.id.iv_plan_tomorrow);
            progressBar = (ProgressBar) activity.findViewById(R.id.pg_PlanTomorrow);
        }
    }


    public void beginLoading(Activity activity)
    {
        findControls(activity);
        progressBar.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(null);
    }

    public void finishLoading(Activity activity, Bitmap bitmap)
    {
        try
        {
            findControls(activity);
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(bitmap);
        }
        catch(NullPointerException e)
        {
            //Happens when user switches back to login while loading plans.
        }
    }


    public boolean isNotZoomed()
    {
        if(imageView != null)
        {
            return imageView.isNotZoomed(); //the imageView is created delayed, so we have to check for a NullRef Exception
        }
        else
        {
            return true;
        }
    }

}
