package com.example.yggdrasil.realdiabeticapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Yggdrasil on 16/4/2561.
 */

public class SimplePreference {

    private Context ccontext;

    protected SimplePreference(Context context){
        this.ccontext = context;
    }

    protected void setEmailFromPref(String email){
        SharedPreferences preferences = ccontext.getSharedPreferences("Email_Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.apply();
    }

    protected String getEmailFromPref(){
        SharedPreferences preferences = ccontext.getSharedPreferences("Email_Pref", Context.MODE_PRIVATE);
        return preferences.getString("email", null);
    }

}
