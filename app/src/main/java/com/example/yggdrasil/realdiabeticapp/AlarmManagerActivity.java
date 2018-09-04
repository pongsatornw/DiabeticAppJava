package com.example.yggdrasil.realdiabeticapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.yggdrasil.realdiabeticapp.BroadcastManager.AlarmManagerBroadcastReceiver;

/**
 * Created by Yggdrasil on 11/2/2561.
 */

public class AlarmManagerActivity extends AppCompatActivity {

    private Button button;
    private BroadcastReceiver broadcastReceiver;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        broadcastReceiver = new AlarmManagerBroadcastReceiver();
        button = (Button) findViewById(R.id.button_test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBroadcastReceiver();
            }
        });
    }

    public void setBroadcastReceiver(){
        Intent intent = new Intent();
        //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction("a.b.c.d");
        intent.putExtra("repeat", 0);
        //intent.putExtra("Foo", "Bar");
        sendBroadcast(intent);
        alarmToBroadcast();
        //alarmInexactBroadcast();
        setAlarm();
        //cancelAlarm();

    }

    public void alarmToBroadcast(){
        Log.d(getClass().getSimpleName(), "Enter alarmTOBroadcast");
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent( AlarmManagerActivity.this, AlarmManagerBroadcastReceiver.class);
        intent.setAction("a.b.c.d");
        alarmIntent = PendingIntent.getBroadcast( getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if(alarmMgr != null)
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() +
                            15*1000, alarmIntent);
        else
            Log.d(getClass().getSimpleName(), "Null alarmManager");
    }

    public void alarmInexactBroadcast(){
        alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 41);
        calendar.set(Calendar.SECOND, 40);
        Intent intent = new Intent( AlarmManagerActivity.this, AlarmManagerBroadcastReceiver.class);
        intent.setAction("a.b.c.d");
        PendingIntent pi = PendingIntent.getBroadcast( getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if(alarmMgr != null)
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP
                    , calendar.getTimeInMillis()
                    ,AlarmManager.INTERVAL_DAY, pi);
        else
            Log.d(getClass().getSimpleName(), "Null alarmManager");
    }

    public void setAlarm() {
    //public void setAlarm(Context context, String ID, String Hour_Period, String Minute_Period) {
        alarmMgr =(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmManagerActivity.this, AlarmManagerBroadcastReceiver.class);
        intent.setAction("a.b.c.d");
        PendingIntent pi = PendingIntent.getBroadcast( getApplicationContext(), 0, intent, 0);
        //After after 5 seconds
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 40);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()
                //, (1000 * 60) * (60 * Integer.valueOf(Hour_Period)
                //         + Integer.valueOf(Minute_Period) )
                , (1000) * (60)
                , pi);
    }
    public void cancelAlarm(){
        Intent intent = new Intent(AlarmManagerActivity.this, AlarmManagerBroadcastReceiver.class);
        intent.setAction("a.b.c.d");
        PendingIntent pi = PendingIntent.getBroadcast( getApplicationContext(), 0, intent, 0);
        if (alarmMgr!= null) {
            alarmMgr.cancel(pi);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("a.b.c.d");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }



}
