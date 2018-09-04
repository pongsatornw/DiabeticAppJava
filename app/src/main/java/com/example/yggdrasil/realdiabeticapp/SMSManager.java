package com.example.yggdrasil.realdiabeticapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Yggdrasil on 8/2/2561.
 */

public class SMSManager{
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Context mContext;

    public SMSManager(Context context){
        this.mContext = context;
    }
    public void sentMail(String desTel, String body) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission( mContext,android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");

                ActivityCompat.requestPermissions( (Activity)mContext,
                        new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
            }
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage( "0954402268", "095440268", body, null, null);
            Toast.makeText(mContext, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(mContext, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


}
