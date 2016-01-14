package com.olilay.oplang;

import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Oliver Layer on 22.10.2015.
 */
public class ExceptionSender extends AsyncTask<Exception, Void, Void>
{

    public ExceptionSender(Exception e)
    {
        e.printStackTrace(); //for debug
        execute(e);
    }


    @Override
    public Void doInBackground(Exception... e)
    {
        sendException(e[0]);

        return null;
    }


    private String getExceptionString(Exception e)
    {
        String s = "Version: " + BuildConfig.VERSION_NAME + "; " + e.getMessage() + "; ";

        for(int i = 0; i < e.getStackTrace().length; i++)
        {
            s += e.getStackTrace()[i].toString() + "; ";
        }

        return s;
    }

    private void sendException(Exception e)
    {
        try
        {
            URL url = new URL("http://file.layer.site/oplang/error.php?ex=" + getExceptionString(e));

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();
            con.getInputStream();
        }
        catch(Exception ex)
        {
            Log.v("OPlanG: ExceptionSender", "Sending exception to php file failed!" + ex.toString());
        }
    }

}
