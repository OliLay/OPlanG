package com.olilay.oplang;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Oliver Layer on 23.09.2015.
 */
public class CustomViewPager extends ViewPager
{
    private PlanActivity planActivity;


    public CustomViewPager(Context context)
    {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return isAllowedToSwipe() && super.onInterceptTouchEvent(event); //this prevents the user from swapping tabs when a TouchImageView is zoomed
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return isAllowedToSwipe() && super.onTouchEvent(event); //this prevents the user from swapping tabs when a TouchImageView is zoomed
    }

    public void setPlanActivity(PlanActivity planActivity)
    {
        this.planActivity = planActivity;
    }

    public boolean isAllowedToSwipe()
    {
        PlanFragment planFragmentToday = (PlanFragment)planActivity.getFragments()[0];
        PlanFragment planFragmentTomorrow = (PlanFragment)planActivity.getFragments()[1];

        return planFragmentToday.isNotZoomed() && planFragmentTomorrow.isNotZoomed();
    }
}
