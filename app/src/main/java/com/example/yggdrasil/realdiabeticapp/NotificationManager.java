package com.example.yggdrasil.realdiabeticapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;

/**
 * Created by Yggdrasil on 11/2/2561.
 */

public class NotificationManager extends ContextWrapper{

    private MainApplication application;
    private NotificationManager notificationManager;
    public static final String Channel_ID = "";
    public static final String Channel_Name = "";
    private String Email;

    public NotificationManager(Context base) {
        super(base);

        this.Email = getSharedPreferences("EMAIL", MODE_PRIVATE).getString("email", null);
        createNotification();
    }

    public void createNotification() {

    }
}
