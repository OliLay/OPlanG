package com.olilay.oplang;

/**
 * Created by Oliver Layer on 20.08.2015.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter
{
    private int numberOfTabs;

    private Fragment[] fragments = new Fragment[10];

    public PagerAdapter(FragmentManager fm, int numberOfTabs, Fragment... fragments)
    {
        super(fm);

        this.numberOfTabs = numberOfTabs;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
