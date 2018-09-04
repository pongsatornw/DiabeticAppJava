package com.example.yggdrasil.realdiabeticapp;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by GsolC on 2/24/2017.
 */

public class LongOperation extends AsyncTask<Void, Void, String> {
    private String email;
    private String password;
    private String date;
    private String time;
    private String value;
    private String cemail;
    public LongOperation( String Email, String Password, String Date, String Time, String Value, String cEmail) {
        this.email =  Email;
        this.password = Password;
        this.date = Date;
        this.time = Time;
        this.value = Value;
        this.cemail = cEmail;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {

//            GMailSender sender = new GMailSender("sender.sendermail.com", "senders password");
//            sender.sendMail("subject",
//                    "body",
//                    "sender.sendermail.com",
//                    "reciepients.recepientmail.com");
//
            GMailSender sender = new GMailSender(email, password);
            sender.sendMail("Patient Bloodglucose Value on" + date + " " + time ,
                    "Parient Blood Glucose is: " + value + "\n",
                    email,
                    cemail);

        } catch (Exception e) {
            Log.e("error", e.getMessage(), e);
            return "Email Not Sent";
        }
        return "Email Sent";
    }

    @Override
    protected void onPostExecute(String result) {

        Log.e("LongOperation",result+"");
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
