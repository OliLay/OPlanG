package com.olilay.oplang;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Oliver Layer on 03.11.2015.
 */
public class AccessChecker extends AsyncTask<Void, Void, Boolean>
{
    private String cookie;

    private LoginActivity loginActivity;

    public AccessChecker(String cookie, LoginActivity loginActivity)
    {
        this.cookie = cookie;
        this.loginActivity = loginActivity;

        execute();
    }

    @Override
    public Boolean doInBackground(Void... voids)
    {
        try
        {
            URL url = new URL(PlanImage.getURL(true, true) + "1");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("Cookie", cookie);
            con.setDoOutput(true);

            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());

            if(BitmapFactory.decodeStream(bis) == null)
            {
                System.out.println("User is NOT a teacher!");
                return false;
            }
            else
            {
                System.out.println("User is a teacher!");
                return true;
            }
        }
        catch(Exception e)
        {
            new ExceptionSender(e);

            return false;
        }
    }

    @Override
    public void onPostExecute(Boolean result)
    {
        Settings.setIsTeacher(result, loginActivity);
        loginActivity.onLoginCompleted();
    }
}
