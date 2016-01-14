package com.olilay.oplang;

import android.content.Context;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Oliver Layer on 13.01.2016.
 */
public class TeacherPlanData extends PlanData
{

    public TeacherPlanData(Plan plan, boolean showOnlyTomorrow, Context context)
    {
        super(plan, showOnlyTomorrow, context);
    }


    @Override
    protected List<Change> getMyChanges()
    {
        return getChangesOfMyself(Settings.getUsername(context), changes);
    }

    @Override
    protected void createParser(String content)
    {
        parser = new TeacherParser(content);
    }

    @Override
    protected URL getDataURL() throws MalformedURLException
    {
        if(plan.isToday())
        {
            return new URL("https://secure.gymnasium-pullach.de/layer/Lehrer%20Heute.pdf");
        }
        else
        {
            return new URL("https://secure.gymnasium-pullach.de/layer/Lehrer%20Morgen.pdf");
        }
    }

    @Override
    protected void onRefreshed(List<Change> changes, Date date)
    {
        plan.onPlanDataRefreshed(new PlanNotification(changes, date, true, plan, context), plan);
    }


    private List<Change> getChangesOfMyself(String name, List<Change> changes)
    {
        List<Change> myChanges = new ArrayList<Change>();

        for(int i = 0; i < changes.size(); i++)
        {
            if(changes.get(i).getNewTeacher().contains("(" + name + ")"))
            {
                myChanges.add(changes.get(i));
            }
        }

        Log.v("TeacherPlanData.java", myChanges.size() + " changes for me out of " + changes.size() + " changes overall.");

        Collections.sort(myChanges);

        return myChanges;
    }

}
