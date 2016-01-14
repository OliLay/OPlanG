package com.olilay.oplang;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TimePicker;

/**
 * Created by Oliver Layer on 19.09.2015.
 */
public class Settings
{
    private PlanActivity planActivity;
    private Spinner sp_Class;
    private NumberPicker np_Interval;
    private ArrayAdapter<CharSequence> adapter;
    private CheckBox cb_specialPush;
    private CheckBox cb_DailyPush;
    private TimePicker tp_DailyPush;
    private Button btn_Save;

    private boolean isTeacher;


    public Settings(PlanActivity planActivity, boolean isTeacher)
    {
        this.isTeacher = isTeacher;
        this.planActivity = planActivity;

        getControls();
        Load();
    }

    private void getControls()
    {
        cb_specialPush = (CheckBox)planActivity.findViewById(R.id.cb_SpecialPush);
        btn_Save = (Button)planActivity.findViewById(R.id.btn_Save);
        np_Interval = (NumberPicker)planActivity.findViewById(R.id.np_Interval);
        tp_DailyPush = (TimePicker)planActivity.findViewById(R.id.tp_DailyPush);
        cb_DailyPush = (CheckBox)planActivity.findViewById(R.id.cb_DailyPush);

        np_Interval.setMinValue(10);
        np_Interval.setMaxValue(180);
        np_Interval.setValue(getInterval(planActivity));
        np_Interval.setEnabled(false);

        tp_DailyPush.setIs24HourView(true);
        tp_DailyPush.setCurrentHour(18);
        tp_DailyPush.setCurrentMinute(0);
        tp_DailyPush.setEnabled(false);

        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });

        cb_specialPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                np_Interval.setEnabled(isChecked);
            }
        });

        cb_DailyPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tp_DailyPush.setEnabled(isChecked);
            }
        });

        adapter = ArrayAdapter.createFromResource(planActivity, R.array.classes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if(!isTeacher)
        {
            sp_Class = (Spinner) planActivity.findViewById(R.id.sp_Class);
            sp_Class.setAdapter(adapter);
        }
    }


    public void Load()
    {
        if(!isTeacher)
        {
            sp_Class.setSelection(adapter.getPosition(getUserClass(planActivity)));
        }

        cb_specialPush.setChecked(getSpecialPush(planActivity));
        cb_DailyPush.setChecked(getDailyPush(planActivity));
        tp_DailyPush.setCurrentHour(getDailyPushTime(planActivity)[0]);
        tp_DailyPush.setCurrentMinute(getDailyPushTime(planActivity)[1]);

        if(cb_specialPush.isChecked())
        {
            PlanService.setAlarmManager(getInterval(planActivity), planActivity);
        }

        if (cb_DailyPush.isChecked())
        {
            PlanService.setAlarmManager(planActivity);
        }
    }

    private void Save()
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(planActivity);
        SharedPreferences.Editor editor = p.edit();

        if(!isTeacher)
        {
            editor.putString("class", sp_Class.getSelectedItem().toString());
        }

        editor.putBoolean("getSpecialPush", cb_specialPush.isChecked());
        editor.putBoolean("getDailyPush", cb_DailyPush.isChecked());
        editor.putString("dailyPushTime", tp_DailyPush.getCurrentHour() + ":" + tp_DailyPush.getCurrentMinute());
        editor.putInt("interval", np_Interval.getValue());
        editor.apply();

        if(cb_specialPush.isChecked())
        {
            PlanService.setAlarmManager(getInterval(planActivity), planActivity);
        }

        if(cb_DailyPush.isChecked())
        {
            PlanService.setAlarmManager(planActivity);
        }
    }


    public static boolean getSpecialPush(Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        return p.getBoolean("getSpecialPush", false);
    }

    public static String getUserClass(Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        return p.getString("class", "5a");
    }

    public static int getInterval(Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        return p.getInt("interval", 60);
    }

    public static boolean getDailyPush(Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        return p.getBoolean("getDailyPush", false);
    }

    public static int[] getDailyPushTime(Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        String timeString = p.getString("dailyPushTime", "18:00");
        String[] timeStrings = timeString.split(":");

        int[] timeInts = new int[2];
        timeInts[0] = Integer.parseInt(timeStrings[0]);
        timeInts[1] = Integer.parseInt(timeStrings[1]);

        return timeInts;
    }

    public static void setIsTeacher(boolean isTeacher, Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = p.edit();

        editor.putBoolean("isTeacher", isTeacher);

        editor.apply();
    }

    public static boolean getIsTeacher(Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        return p.getBoolean("isTeacher", false);
    }

    public static void setUsername(String username, Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = p.edit();

        editor.putString("username", username);

        editor.apply();
    }

    public static String getUsername(Context context)
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);

        return p.getString("username", "");
    }
}
