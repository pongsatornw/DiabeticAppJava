package com.example.yggdrasil.realdiabeticapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Yggdrasil on 11/2/2561.
 */
// Use to send Email and SMS
public class Intepret implements Runnable{

    private Context context;
    private String value;
    private String code;
    public Intepret(Activity activity, String value, String code) {
        this.context = activity;
        this.value = value;
        this.code = code;
    }

    @Override
    public void run() {
        switch (code){
            case "0" :
                break;
            case "1" :
                break;
            case "2" :
                break;
        }


    }

}
