package com.olilay.oplang;

import android.app.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Date;
import java.util.List;

/**
 * Created by Oliver Layer on 14.09.2015.
 */
public class PlanFragment extends Fragment
{
    private boolean today;

    private PlanManager planManager;
    private ProgressBar progressBar;
    private TouchImageView imageView;
    private SwipeRefreshLayout view;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = (SwipeRefreshLayout)inflater.inflate(layout, container, false);

        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                planManager.refreshPlanImages();
                view.setRefreshing(false);
            }
        });

        view.setColorSchemeResources(R.color.orange);

        return view;
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


    public void beginLoading(Activity activity, PlanManager planManager)
    {
        this.planManager = planManager;

        findControls(activity);
        progressBar.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(null);
        imageView.setOnPositionChangedListener(new OnPositionChangedListener() {
            @Override
            public void onYPositionChanged(float y) {
                if(y == 0.0 && imageView.isNotZoomed())
                {
                    view.setEnabled(true);
                }
                else
                {
                    view.setEnabled(false);
                }
            }
        });
    }

    public void finishLoading(Activity activity, Bitmap bitmap)
    {
        try
        {
            findControls(activity);
            progressBar.setVisibility(View.INVISIBLE);

            if(bitmap == null) //couldn't get the PlanImage
            {
                imageView.setZoomable(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_signal_cellular_connected_no_internet_0_bar));
            }
            else
            {
                imageView.setZoomable(true);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                imageView.setImageBitmap(bitmap);
            }
        }
        catch(NullPointerException e)
        {
            //Happens when user switches back to login while loading plans
        }
        catch(Exception e)
        {
            new ExceptionSender(e);
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


