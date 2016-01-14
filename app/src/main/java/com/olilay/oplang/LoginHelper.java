package com.olilay.oplang;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oliver Layer on 20.08.2015.
 */
public class LoginHelper
{

    public static void saveLogin(LoginActivity la, String loginName, String loginPassword)
    {
        SharedPreferences p = la.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = p.edit();

        editor.putString("loginName", loginName);
        editor.putString("loginPassword", loginPassword);
        editor.apply();
    }

    public static List<String> getLogin(LoginActivity la)
    {
        SharedPreferences p = la.getPreferences(Context.MODE_PRIVATE);

        if(p.contains("loginName") && p.contains("loginPassword"))
        {
            List<String> r = new ArrayList<String>();
            r.add(p.getString("loginName", ""));
            r.add(p.getString("loginPassword", ""));

            return r;
        }
        else
        {
            return null; //No login has been saved
        }
    }

    public static void deleteLogin(LoginActivity la)
    {
        SharedPreferences p = la.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = p.edit();

        if(p.contains("loginName") && p.contains("loginPassword"))
        {
            editor.remove("loginName");
            editor.remove("loginPassword");
            editor.apply();
        }
    }
}