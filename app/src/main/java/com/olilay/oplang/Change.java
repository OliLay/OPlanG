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
import java.util.Arrays;
import java.util.Date;
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
    }

    public Change(String affectedClass, String course, int hour, String subject, String oldTeacher, String newTeacher, String room, String remark)
    {
        this(Arrays.asList(affectedClass), course, hour, subject, oldTeacher, newTeacher, room, remark);
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


    public static void saveChangeList(List<Change> changes, Date date, Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = p.edit();

        Gson gson = new Gson();
        editor.putString("oldChanges" + date.getDay() + date.getMonth() + date.getYear(), gson.toJson(changes));
        editor.apply();
    }

    public static List<Change> loadChangeList(Date date, Context context) throws JSONException
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String json = p.getString("oldChanges" + date.getDay() + date.getMonth() + date.getYear(), "");

        if(json.isEmpty())
        {
            return new ArrayList<Change>(); //we didn't save this date once
        }

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


    @Override
    public boolean equals(Object changeObject)
    {
        Change change = (Change)changeObject;

        return getCourse().equals(change.getCourse()) && getAffectedClasses().equals(change.getAffectedClasses()) && getHour().equals(change.getHour()) && getNewTeacher().equals(change.getNewTeacher()) && getOldTeacher().equals(change.getOldTeacher()) && getRemark().equals(change.getRemark()) && getRoom().equals(change.getRoom()) && getSubject().equals(change.getSubject());
    }

    @Override
    public int compareTo(Change change)
    {
        return getHour() - change.getHour(); //this is for sorting an ArrayList<Change> by comparing their hour
    }

    public String toString(boolean isTeacher)
    {
        if(isTeacher)
        {
            if(Remark.contains("Raumänderung"))
            {
                return Hour + ". Stunde: " + AffectedClasses.get(0) + ", Raumänderung";
            }
            else
            {
                return Hour + ". Stunde: " + AffectedClasses.get(0) + " statt " + OldTeacher + ", Vertretung";
            }
        }
        else
        {
            if(Remark.contains("entfällt") || Remark.contains("Eigenstudium") || (Room.isEmpty() && NewTeacher.isEmpty()))
            {
                return Hour + ". Stunde: " + Subject + " bei " + OldTeacher + " entfällt";
            }
            else if(Remark.contains("Raumänderung"))
            {
                if(Room.isEmpty())
                {
                    return Hour + ". Stunde: " + Subject + " bei " + OldTeacher + ", Raumänderung";
                }
                else
                {
                    return Hour + ". Stunde: " + Subject + " bei " + OldTeacher + ", Raumänderung (" + Room + ")";
                }
            }
            else if(getNewTeacher().isEmpty())
            {
                return Hour + ". Stunde: " + Subject + " bei " + OldTeacher + ", " + NewTeacher + " vertritt";
            }
            else
            {
                return Hour + ". Stunde: " + Subject + " bei " + OldTeacher + ", Vertretung";
            }
        }
    }

    public String toStringDebug()
    {
        return "Classes: " + getAffectedClasses().toString() + "; Course: " + getCourse() + "; OldTeacher: " + getOldTeacher() + "; NewTeacher: " + getNewTeacher();
    }

}
