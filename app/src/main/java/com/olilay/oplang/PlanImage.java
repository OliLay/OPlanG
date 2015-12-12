package com.olilay.oplang;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oliver Layer on 10.11.2015.
 */
public class PlanImage extends AsyncTask<String, Void, Bitmap>
{
    private Plan plan;
    private boolean internetConnectionFail;


    public PlanImage(String cookie, Plan plan)
    {
        this.plan = plan;

        execute(cookie);
    }


    protected Bitmap doInBackground(String... cookie)
    {
        return getImage(cookie[0]);
    }

    protected void onPostExecute(Bitmap bitmap)
    {
        if(internetConnectionFail || bitmap != null)
        {
            plan.onPlanImageRefreshed(bitmap);
        }
        else //session not valid
        {
            plan.getPlanManager().relogin();
        }
    }


    private Bitmap getImage(String cookie)
    {
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();

        for(int i = 1; i < 10; i++)
        {
            Bitmap page = getPage(i, cookie);

            if(page == null) //there are no more pages
            {
                break;
            }
            else
            {
                bitmaps.add(page);
            }
        }

        return mergePages(bitmaps);
    }

    private Bitmap getPage(int number, String cookie)
    {
        try
        {
            URL url = new URL(getURL(plan.isToday(), plan.isTeacher()) + number);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.addRequestProperty("Cookie", cookie);
            con.setConnectTimeout(2000);
            con.setReadTimeout(2000);
            con.setUseCaches(false);
            con.setDoOutput(true);

            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());

            return BitmapFactory.decodeStream(bis);
        }
        catch(UnknownHostException ue)
        {
            internetConnectionFail = true;
            Log.v("OPlanG", "No internet connection to get image!");

            return null;
        }
        catch(Exception e)
        {
            new ExceptionSender(e);

            return null;
        }
    }

    private Bitmap mergePages(List<Bitmap> bitmaps)
    {
        if(bitmaps.size() == 0)
        {
            return null; //there are no bitmaps, so we don't want to merge any
        }
        else if(bitmaps.size() == 1)
        {
            return bitmaps.get(0); //we can't merge only one bitmap, so we return it
        }
        else
        {
            for(int i = 1; i < bitmaps.size(); i++) //we merge every bitmap with the first one
            {
                Bitmap mergedBitmap = mergeBitmap(bitmaps.get(0), bitmaps.get(i));
                bitmaps.set(0, mergedBitmap);
            }

            return bitmaps.get(0);
        }
    }

    private Bitmap mergeBitmap(Bitmap b1, Bitmap b2)
    {
        Bitmap bothBitmaps = Bitmap.createBitmap(b1.getWidth(), b1.getHeight() + b2.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(bothBitmaps);
        comboImage.drawBitmap(b1, 0f, 0f, null);
        comboImage.drawBitmap(b2, 0f, b1.getHeight() , null);

        return bothBitmaps;
    }


    public static String getURL(boolean isToday, boolean isTeacher) throws MalformedURLException
    {
        if(isToday && isTeacher)
        {
            return "https://secure.gymnasium-pullach.de/download.php?id=Lehrer%20Heute_";
        }
        else if(!isToday && isTeacher)
        {
            return "https://secure.gymnasium-pullach.de/download.php?id=Lehrer%20Morgen_";
        }
        else if(isToday && !isTeacher)
        {
            return "https://secure.gymnasium-pullach.de/download.php?id=Schueler%20Heute_";
        }
        else
        {
            return "https://secure.gymnasium-pullach.de/download.php?id=Schueler%20Morgen_";
        }
    }

}
