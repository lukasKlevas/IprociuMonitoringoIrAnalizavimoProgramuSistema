package com.habit.tracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationBroadCast extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.e("ReminderBroadcast","RUN");
        final String message=intent.getExtras().getString(MyNotificationManager.MESSAGE);
        final int request_code=intent.getExtras().getInt(MyNotificationManager.REQUEST_CODE);
        final long timeInterval=intent.getExtras().getLong(MyNotificationManager.TIME_INTERVAL);
        MyNotificationManager myNotificationManager= new MyNotificationManager(context);
        NotificationCompat.Builder _builder = myNotificationManager.setNotification(context.getString(R.string.app_name), message);
        myNotificationManager.getAlarmManager().notify(101, _builder.build());
        myNotificationManager.setReminder(timeInterval,message,request_code);
    }
}