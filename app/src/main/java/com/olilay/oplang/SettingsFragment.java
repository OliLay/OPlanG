package com.olilay.oplang;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Oliver Layer on 24.10.2015.
 */
public class SettingsFragment extends Fragment
{
    private String givenName;
    private String lastName;
    private String wholeName;
    private PlanActivity planActivity;

    public static SettingsFragment newInstance(String wholeName, String givenName, String lastName, boolean isTeacher)
    {
        SettingsFragment fragment = new SettingsFragment();

        final Bundle args = new Bundle(3);

        if(isTeacher)
        {
            args.putInt("layout", R.layout.fragment_settings_teacher); //we use a different layout for teachers
        }
        else
        {
            args.putInt("layout", R.layout.fragment_settings_pupil);
        }

        args.putString("wholeName", wholeName);
        args.putString("givenName", givenName);
        args.putString("lastName", lastName);
        args.putBoolean("isTeacher", isTeacher);

        fragment.setArguments(args);

        return fragment;
    }

    public void init(PlanActivity activity)
    {
        this.planActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        givenName = getArguments().getString("givenName");
        lastName = getArguments().getString("lastName");
        wholeName = getArguments().getString("wholeName");
    }

    @Override
    public void Refresh(Activity activity, String... args)
    {
        TextView txt_Name = (TextView)activity.findViewById(R.id.txt_Name);

        if(givenName != null && lastName != null)
        {
            txt_Name.setText(givenName + " " + lastName);
        }
        else
        {
            txt_Name.setText(wholeName);
        }
    }
}
