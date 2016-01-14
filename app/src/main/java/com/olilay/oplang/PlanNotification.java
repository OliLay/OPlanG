package com.olilay.oplang;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oliver Layer on 13.11.2015.
 */
public class PlanNotification
{
    private Notificator notificator;

    private Date date;
    private String[] lines;


    public PlanNotification(List<Change> changes, Date date, boolean isTeacher, Plan plan, Context context)
    {
        notificator = new Notificator(plan.isToday(), context);
        this.date = date;
        this.lines = new String[50];

        if(isTeacher)
        {
            lines[0] = changes.size() + " Vertretungen für " + Settings.getUsername(context);
        }
        else
        {
            lines[0] = changes.size() + " Vertretungen für Klasse " + Settings.getUserClass(context);
        }

        for(int i = 0; i < changes.size(); i++)
        {
            lines[i + 1] = changes.get(i).toString(isTeacher);
        }
    }


    public void push()
    {
        notificator.Notify("OPlanG: " + new SimpleDateFormat("EEEE", Locale.GERMAN).format(date), lines);
    }

}
