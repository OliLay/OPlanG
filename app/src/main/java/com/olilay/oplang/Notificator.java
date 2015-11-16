package com.olilay.oplang;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Oliver Layer on 12.10.2015.
 */
public class Notificator
{
    private boolean today;
    private Context context;

    public Notificator(boolean today, Context context)
    {
        this.today = today;
        this.context = context;
    }


    public void Notify(String heading, String[] lines)
    {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(heading);

        for (int i = 0; i < lines.length; i++)
        {
            if(lines[i] != null)
            {
                if(i < 6)
                {
                    inboxStyle.addLine(lines[i]); //we add a line for each change
                }
                else if(i >= 6) //android notifications support up to 6 lines, so we have to stop here
                {
                    inboxStyle.addLine("klicken, um weitere anzuzeigen ...");
                    break;
                }
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notificon)
                .setContentTitle(heading)
                .setContentText(lines[0])
                .setStyle(inboxStyle)
                .setGroup("group_key_plan");

        Intent resultIntent = new Intent(context, LoginActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent); //we create a pending intent that will be called when the user clicks on the notification

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(getNotifID(today), builder.build());
    }

    public void Notify(String heading, String line)
    {
        this.Notify(heading, new String[] {line});
    }

    private int getNotifID(boolean today)
    {
        if(today) { return 1; }
            else return 2;
    }
}
