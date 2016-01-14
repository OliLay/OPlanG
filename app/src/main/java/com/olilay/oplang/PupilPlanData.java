package com.olilay.oplang;

import android.content.Context;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Oliver Layer on 13.01.2016.
 */
public class PupilPlanData extends PlanData
{

    public PupilPlanData(Plan plan, boolean showOnlyTomorrow, Context context)
    {
        super(plan, showOnlyTomorrow, context);
    }


    @Override
    protected List<Change> getMyChanges()
    {
        return getChangesOfClass(Settings.getUserClass(context), changes);
    }

    @Override
    protected  void createParser(String content)
    {
        parser = new PupilParser(content);
    }

    @Override
    protected URL getDataURL() throws MalformedURLException
    {
        if(plan.isToday())
        {
            return new URL("https://secure.gymnasium-pullach.de/layer/Schueler%20Heute.pdf");
        }
        else
        {
            return new URL("https://secure.gymnasium-pullach.de/layer/Schueler%20Morgen.pdf");
        }
    }

    @Override
    protected void onRefreshed(List<Change> changes, Date date)
    {
        plan.onPlanDataRefreshed(new PlanNotification(changes, date, false, plan, context), plan);
    }


    private List<Change> getChangesOfClass(String _class, List<Change> changes)
    {
        List<Change> classChanges = new ArrayList<Change>();

        for(int i = 0; i < changes.size(); i++)
        {
            if(changes.get(i).getAffectedClasses().contains(_class))
            {
                classChanges.add(changes.get(i));
            }
        }

        Collections.sort(classChanges);

        return classChanges;
    }

}

