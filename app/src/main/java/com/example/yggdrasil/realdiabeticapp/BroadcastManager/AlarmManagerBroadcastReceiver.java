package com.example.yggdrasil.realdiabeticapp.BroadcastManager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.yggdrasil.realdiabeticapp.NewLoginActivity;
import com.example.yggdrasil.realdiabeticapp.R;

/**
 * Created by Yggdrasil on 11/2/2561.
 */

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver{

    final public static String ONE_TIME = "onetime";
    PendingIntent pi;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), Intent.ACTION_BOOT_COMPLETED);
    if ( intent.getAction().equals("a.b.c.d")) {
            Log.d(getClass().getSimpleName(), "Custom Broadcast01");

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, "01")
                            .setSmallIcon(R.drawable.ic_menu_share)
                            .setContentTitle("Event tracker")
                            .setContentText("Events received");
            PendingIntent pi = PendingIntent.getActivity(context, 1,
                    new Intent(context, NewLoginActivity.class), 0);
            mBuilder.setContentIntent(pi);

            Notification notification = mBuilder.build();
            NotificationManagerCompat.from(context).notify(0, notification);
        Vibrator  vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);
        }
        else
            Log.d(getClass().getSimpleName(), "no this action for intent!");
    }

    // Repeat once a day.
    public void setBroadcastReceiver(Context context, Intent intent) {
        if ( intent.getAction().equals("com.example.yggdrasil.realdiabeticapp.customaction01"))
            Log.d(getClass().getSimpleName(), "asd");



    }

    // Repeat for some period.
    public void setAlarm(Context context, String ID, String Hour_Period, String Minute_Period) {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, Integer.valueOf(ID), intent, 0);
        //After after 5 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                , (1000 * 60) * (60 * Integer.valueOf(Hour_Period)
                        + Integer.valueOf(Minute_Period) )
                , pi);
    }

    public void cancelAlarm(Context context, String ID) {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, Integer.valueOf(ID), intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context, String ID){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, Integer.valueOf(ID), intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }

}
