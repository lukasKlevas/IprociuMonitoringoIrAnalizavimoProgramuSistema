package com.habit.tracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class MyNotificationManager extends ContextWrapper
{

    private static final String CHANNEL_ID="Goal";

    public static final String MESSAGE="Message";
    public static final String REQUEST_CODE="Request_Code";
    public static final String TIME_INTERVAL="Time_Interval";
    private static final CharSequence Channel_Name="Habit_Tracker";
    private NotificationManager notificationManager;
    private Context context;

    public MyNotificationManager(Context base)
    {
        super(base);
        context= base;
        createChannel();
    }

    public NotificationCompat.Builder setNotification(String title, String body)
    {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void createChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, Channel_Name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getAlarmManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getAlarmManager()
    {
        if(notificationManager == null)
        {
            notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
    public void setReminder(long timeInMillis,String message,int requestCode)
    {
        Intent intent= new Intent(context, NotificationBroadCast.class);
        intent.putExtra(MESSAGE,message);
        intent.putExtra(REQUEST_CODE,requestCode);
        intent.putExtra(TIME_INTERVAL,timeInMillis);
        PendingIntent pendingintent=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
        {
            pendingintent=PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingintent=PendingIntent.getBroadcast(context, requestCode, intent, 0);
        }
        AlarmManager alarmmanager= (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmmanager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingintent);
    }
}

