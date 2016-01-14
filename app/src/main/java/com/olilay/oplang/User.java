package com.olilay.oplang;

import android.content.Context;

/**
 * Created by Oliver Layer on 24.10.2015.
 */
public class User
{
    private String LastName;
    private String GivenName;
    private String WholeName;
    private String Class;
    private Session Session;
    private boolean IsTeacher;

    private LoginActivity loginActivity;

    public User(String wholeName, LoginActivity loginActivity)
    {
        this.WholeName = wholeName.substring(0, 1).toUpperCase() + wholeName.substring(1);
        this.loginActivity = loginActivity;

        String[] nameStrings = WholeName.split(" ");

        if(nameStrings.length >= 2)
        {
            LastName = nameStrings[0];
            GivenName = nameStrings[1];

            LastName = LastName.substring(0, 1).toUpperCase() + LastName.substring(1); //uppers the first letter of the name
            GivenName = GivenName.substring(0, 1).toUpperCase() + GivenName.substring(1);
        }

        Settings.setUsername(wholeName, loginActivity);
    }


    public void createSession(String password, boolean checkForTeacher)
    {
        Session = new Session(WholeName, password, checkForTeacher, loginActivity);
    }

    public void setClass(String _class)
    {
        Class = _class;
    }

    public void setIsTeacher(boolean isTeacher)
    {
        IsTeacher = isTeacher;
    }


    public boolean isTeacher()
    {
        return IsTeacher;
    }

    public String getUserClass()
    {
        return Class;
    }

    public String getLastName()
    {
        return LastName;
    }

    public String getGivenName()
    {
        return GivenName;
    }

    public String getWholeName() { return WholeName; }

    public Session getSession()
    {
        return Session;
    }
}
