package com.olilay.oplang;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oliver Layer on 29.09.2015.
 */
public class Change implements Serializable, Comparable<Change>
{
    private List<String> AffectedClasses;
    private String Course;
    private String OldTeacher;
    private String NewTeacher;
    private String Subject;
    private String Room;
    private Integer Hour;
    private String Remark;


    public Change(List<String> affectedClasses, String course, int hour, String subject, String oldTeacher, String newTeacher, String room, String remark)
    {
        AffectedClasses = affectedClasses;
        Course = course;
        Subject = subject;
        OldTeacher = oldTeacher;
        NewTeacher = newTeacher;
        Room = room;
        Hour = hour;
        Remark = remark;

       // System.out.println(toStringDebug());
    }


    public List<String> getAffectedClasses()
    {
        return AffectedClasses;
    }

    public String getOldTeacher()
    {
        return OldTeacher;
    }

    public String getNewTeacher()
    {
        return NewTeacher;
    }

    public String getRoom()
    {
        return Room;
    }

    public Integer getHour()
    {
        return Hour;
    }

    public String getRemark()
    {
        return Remark;
    }

    public String getSubject() { return Subject; }

    public String getCourse() { return Course; }


    public static void saveChangeList(List<Change> changes, boolean today, Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = p.edit();

        Gson gson = new Gson();
        editor.putString("oldChanges" + PlanData.isTodayToString(today), gson.toJson(changes));
        editor.apply();
    }

    public static List<Change> loadChangeList(boolean today, Context context) throws JSONException
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = p.getString("oldChanges" + PlanData.isTodayToString(today), "");

        Type type = new TypeToken<List<Change>>(){}.getType();
        List<Change> changes = gson.fromJson(json, type);

        if(changes == null)
        {
            return new ArrayList<Change>();
        }
        else
        {
            return changes;
        }
    }


    private String toStringDebug()
    {
        return "Classes: " + getAffectedClasses().toString() + "; Course: " + getCourse() + "; OldTeacher: " + getOldTeacher() + "; NewTeacher: " + getNewTeacher();
    }

    public boolean equals(Change change)
    {
        return getCourse().equals(change.getCourse()) && getAffectedClasses().equals(change.getAffectedClasses()) && getHour().equals(change.getHour()) && getNewTeacher().equals(change.getNewTeacher()) && getOldTeacher().equals(change.getOldTeacher()) && getRemark().equals(change.getRemark()) && getRoom().equals(change.getRoom()) && getSubject().equals(change.getSubject());
    }

    @Override
    public String toString()
    {
        if(Remark.contains("entfällt") || Remark.contains("Eigenstudium") || (Room.isEmpty() && NewTeacher.isEmpty()))
        {
            return getHour() + ". Stunde: " + getSubject() + " bei " + getOldTeacher() + " entfällt";
        }
        else if(Remark.contains("Raumänderung"))
        {
            return getHour() + ". Stunde: " + getSubject() + " bei " + getOldTeacher() + ", Raumänderung (" + getRoom() + ")";
        }
        else
        {
            return getHour() + ". Stunde: " + getSubject() + " bei " + getOldTeacher() + ", " + getNewTeacher() + " vertritt";
        }
    }

    public int compareTo(Change change)
    {
        return getHour() - change.getHour(); //this is for sorting an ArrayList<Change> by comparing their hour
    }

}
