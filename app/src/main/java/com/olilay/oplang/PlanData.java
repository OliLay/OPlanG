package com.olilay.oplang;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oliver Layer on 29.09.2015.
 */

public abstract class PlanData extends AsyncTask<Void, Void, Void>
{
    protected List<Change> changes;
    protected Parser parser;
    protected Plan plan;
    protected Context context;

    private Date date;
    private Date refreshDate;
    private boolean showOnlyTomorrow;


    public PlanData(Plan plan, boolean showOnlyTomorrow, Context context)
    {
        this.plan = plan;
        this.showOnlyTomorrow = showOnlyTomorrow;
        this.context = context;

        refresh();
    }


    protected Void doInBackground(Void... nothing)
    {
        try
        {
            File pdfFile = downloadFile();
            PDDocument document = PDDocument.load(pdfFile);
            PDFTextStripper s = new PDFTextStripper();

            String content = s.getText(document); //we get the text of the PDF
            document.close();
            pdfFile.delete(); //make sure we delete that temp file again

            createParser(content);

            refreshDate = parser.getRefreshDate();
            date = parser.getPlanDate();
            changes = parser.getChanges();

            if(showOnlyTomorrow) //this will be called when we only want to display the changes for tomorrow
            {
                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                cal1.setTime(new Date());
                cal1.add(Calendar.DATE, 1);
                cal2.setTime(date);

                if(cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
                {
                    List<Change> classChanges = getMyChanges();
                    onRefreshed(classChanges, date);
                }
            }
            else if(Settings.getSpecialPush(context) && isPlanNewer())
            {
                List<Change> newChanges = getMyChanges();

                if(newChanges.size() > 0 && areMyChangesRelevant(newChanges, Change.loadChangeList(getDate(), context)))
                {
                    onRefreshed(newChanges, date);

                    Change.saveChangeList(newChanges, getDate(), context);
                }
            }

            saveCurrentDate();
        }
        catch (UnknownHostException ue)
        {
            Log.v("OPlanG", "No internet connection to get PlanData!");
        }
        catch(Exception ex)
        {
            new ExceptionSender(ex);
        }

        return null;
    }


    private File downloadFile() throws IOException
    {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("layer", "*****".toCharArray());
            }
        });

        InputStream in = getDataURL().openStream();
        File tempFile = File.createTempFile("plan" + plan.isToday(), ".tmp");

        OutputStream out = new FileOutputStream(tempFile);
        byte[] buf = new byte[1024];
        int len;
        while((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();

        return tempFile;
    }

    private boolean areMyChangesRelevant(List<Change> newChanges, List<Change> oldChanges)
    {
        return !newChanges.equals(oldChanges);
    }


    private void saveCurrentDate()
    {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = p.edit();
        editor.putString("plan" + getIsTodayToString(plan.isToday()) + "refreshDate", Parser.DateFormat.format(refreshDate));
        editor.putString("plan" + getIsTodayToString(plan.isToday()) + "date", Parser.DateFormat.format(date));
        editor.apply();
    }

    private boolean isPlanNewer() throws ParseException {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String s = p.getString("plan" + getIsTodayToString(plan.isToday()) + "refreshDate", "01.01.1970 01:01");

        Date oldDate = Parser.DateFormat.parse(s);

        return refreshDate.after(oldDate);
    }


    public void refresh()
    {
        execute();
    }


    protected abstract List<Change> getMyChanges();

    protected abstract void createParser(String content);

    protected abstract URL getDataURL() throws MalformedURLException;

    protected abstract void onRefreshed(List<Change> changes, Date date);


    public Date getRefreshDate() { return refreshDate; }

    public Date getDate() { return date; }

    public static String getIsTodayToString(boolean isToday)
    {
        if(isToday)
        {
            return "Today";
        }
        else
        {
            return "NotToday";
        }
    }

}
