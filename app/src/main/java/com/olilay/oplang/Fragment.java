package com.olilay.oplang;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Oliver Layer on 09.09.2015.
 */
public class Fragment extends android.support.v4.app.Fragment
{
    protected int layout;
    protected boolean isPlan;

    public static Fragment newInstance(int layout, boolean... plan)
    {
        Fragment fragment = new Fragment();

        final Bundle args = new Bundle(3);
        args.putInt("layout", layout);
        args.putBoolean("isPlan", plan[0]);

        if(plan[0])
        {
            args.putBoolean("isTodaysPlan", plan[1]);
        }

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        layout = getArguments().getInt("layout");
        isPlan = getArguments().getBoolean("isPlan");

        Refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(layout, container, false);
    }


    public void Refresh()
    {
        //to be overwritten...
    }

    public void Refresh(Activity activity, String... args)
    {
        //to be overwritten...
    }

    public void onRefreshed()
    {
        //to be overwritten...
    }

    protected void findControls(Activity activity)
    {
        //to be overwritten...
    }
}
