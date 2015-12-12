package com.olilay.oplang;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Oliver Layer on 20.08.2015.
 */
public class PlanActivity extends AppCompatActivity {

    private Fragment[] fragments;
    private PlanManager planManager;
    private String cookie;
    private Settings settings;

    private boolean isTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        this.setTitle("OPlanG");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Heute"));
        tabLayout.addTab(tabLayout.newTab().setText("Morgen"));
        tabLayout.addTab(tabLayout.newTab().setText("Einstellungen"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Intent i = getIntent();
        cookie = i.getStringExtra("Cookie");
        isTeacher = i.getBooleanExtra("isTeacher", false);

        fragments = new Fragment[3];
        fragments[0] = PlanFragment.newInstance(true);
        fragments[1] = PlanFragment.newInstance(false);

        planManager = new PlanManager(cookie, isTeacher, new PlanFragment[] {(PlanFragment)fragments[0], (PlanFragment)fragments[1]}, this);

        fragments[2] = SettingsFragment.newInstance(i.getStringExtra("wholeName"), i.getStringExtra("givenName"), i.getStringExtra("lastName"), isTeacher);

        final CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setPlanActivity(this);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        settings = new Settings(this, isTeacher);
        fragments[2].Refresh(this);

        planManager.initPlans();
        planManager.refreshPlanImages();

        MenuInflater inflater = getMenuInflater();

        if(isTeacher) { inflater.inflate(R.menu.menu_plan_teacher, menu); }
        else { inflater.inflate(R.menu.menu_plan_pupil, menu); }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_refresh:
                planManager.refreshPlanImages();
                return true;
            case R.id.action_change: //this will we called when a teacher switches the plan
                planManager.changePlans();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public Fragment[] getFragments()
    {
        return fragments;
    }

}

