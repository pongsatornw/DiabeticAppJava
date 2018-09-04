package com.example.yggdrasil.realdiabeticapp.BroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.yggdrasil.realdiabeticapp.NewLoginActivity;
import com.example.yggdrasil.realdiabeticapp.R;
import com.example.yggdrasil.realdiabeticapp.SMSManager;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by Yggdrasil on 14/2/2561.
 */

public class NotifyBroadcastReceiver extends BroadcastReceiver {

    private String date;
    private String time;
    private String glu;
    private String measured;
    @Override
    public void onReceive(Context context, Intent intent) {
        if ( intent.getAction().equals("android.action.notify")){
            Log.d( getClass().getSimpleName(), "enter notify broadcast");
            date = intent.getExtras().getString("date");
            Log.d( getClass().getSimpleName(), "date: " + date);
            time = intent.getExtras().getString("time");
            glu = intent.getExtras().getString("glu");
            measured = intent.getExtras().getString("mea");

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, "01")
                            .setSmallIcon(R.drawable.ic_menu_share)
                            .setContentTitle("Friend4Diabetic")
                            .setContentText("Lasted measured blood glucose at "
                            + date +" "+ time+" your BG is: " + glu);
            PendingIntent pi = PendingIntent.getActivity(context, 1,
                    new Intent(context, NewLoginActivity.class), 0);
            mBuilder.setContentIntent(pi);

            Notification notification = mBuilder.build();
            NotificationManagerCompat.from(context).notify(0, notification);
        }else if( intent.getAction().equals("android.action.notify_sound")){

        }
        else
            Log.d( getClass().getSimpleName(), "Not enter notify broadcast");
    }


}
