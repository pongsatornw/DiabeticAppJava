package com.example.yggdrasil.realdiabeticapp.RealmManagement;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.yggdrasil.realdiabeticapp.Models.AlarmModel;
import com.example.yggdrasil.realdiabeticapp.Models.CaretakerProfile;
import com.example.yggdrasil.realdiabeticapp.Models.GlucoseValue;
import com.example.yggdrasil.realdiabeticapp.Models.NotifyModel;
import com.example.yggdrasil.realdiabeticapp.Models.UserAccount;
import com.example.yggdrasil.realdiabeticapp.Models.UserProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmSearch{

    private Realm realm;
    private String TAG = this.getClass().getSimpleName();
    public int mailDuplicate(String email){

        /*try{
            UserAccount userAccount = realm.where(UserAccount.class)
                    .equalTo("Email", email)
                    .findFirst();
            if (userAccount.getEmail().equals(email))
                return true;
            else
                return false;}
        catch (Exception e) {
            //Toast.makeText(get, "Some Error Occor", Toast.LENGTH_SHORT).show();
            Log.d("mailDuplicate Error! ", "Error");
            realm.close();
            return false;
        */
        realm = Realm.getDefaultInstance();
        try {
            RealmResults<UserAccount> userAccount = realm.where(UserAccount.class)
                    .equalTo("Email", email)
                    .findAll();
            Log.d("Size", String.valueOf(userAccount.size() ) );
            if(userAccount.size() >= 1)
                return 1;
            else
                return -1;
        }catch(Exception  error){
            error.printStackTrace();
        }
        return 0;
    }


    public int loginCheck(String email, String password){
        realm = Realm.getDefaultInstance();


        RealmResults<UserAccount> userAccount = realm.where(UserAccount.class)
                .equalTo("Email", email)
                .findAll();
        Log.d("Size", String.valueOf(userAccount.size() ) );
        if(userAccount.size() >= 1) {
            realm.close();
            return 1;
        }
        else {
            realm.close();
            return 0;
        }
    }


    public Float[] getPieResult( String email, String duration) throws ParseException{
        realm = Realm.getDefaultInstance();
        Float[] arr_count = { 0f, 0f, 0f, 0f, 0f};
        String current_date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        Log.d("Current_date", current_date);
        String[] curr_array = current_date.split("-");
        String due_date;

        if( duration.equals("Week")){
            if( Integer.valueOf(curr_array[2]) - 6 >= 1 && Integer.valueOf(curr_array[2]) - 6 <= 9 ){
                curr_array[2] = "0" + ( Integer.valueOf(curr_array[2]) - 6 );
            }
            else if( Integer.valueOf(curr_array[2]) - 6 >= 10 ){
                curr_array[2] = String.valueOf( Integer.valueOf(curr_array[2]) - 6 );
            }
            else { // ทดเดือน
                if (Integer.valueOf(curr_array[1]) - 1 >= 1 && Integer.valueOf(curr_array[1]) - 1 <= 9) {
                    curr_array[1] = "0" + (Integer.valueOf(curr_array[1]) - 1);
                    curr_array[2] = currDayInMonth(curr_array[2], curr_array[1], curr_array[0]);
                } else if (Integer.valueOf(curr_array[1]) - 1 >= 10) {
                    curr_array[1] = String.valueOf(Integer.valueOf(curr_array[1]) - 1);
                    curr_array[2] = currDayInMonth(curr_array[2], curr_array[1], curr_array[0]);
                } else {
                    curr_array[0] = String.valueOf( Integer.valueOf(curr_array[0]) - 1);
                    curr_array[1] = "12";
                    curr_array[2] = currDayInMonth(curr_array[2], curr_array[1], curr_array[0]);
                }
            }
        }
        else if( duration.equals("Month")){
            if( Integer.valueOf(curr_array[1]) - 1 >= 1 && Integer.valueOf(curr_array[1]) - 1 <= 9) {
                curr_array[1] = "0" + ( Integer.valueOf(curr_array[1]) - 1 );
            }
            else if( Integer.valueOf(curr_array[1]) - 1 >= 10){
                curr_array[1] = String.valueOf( Integer.valueOf(curr_array[1]) - 1 );
            }
            else{
                curr_array[1] = "12";
                curr_array[0] = String.valueOf( Integer.valueOf(curr_array[0]) - 1 );
            }
        }
        else if( duration.equals("3 months")){
            if( Integer.valueOf(curr_array[1]) - 3 >= 1 ) {
                curr_array[1] = "0" + ( Integer.valueOf(curr_array[1]) - 3 );
            } else{
                curr_array[1] = String.valueOf( 12 + Integer.valueOf(curr_array[1]) - 3);
                curr_array[0] = String.valueOf( Integer.valueOf(curr_array[0]) - 1 );
            }
        }
        due_date = curr_array[0] +"-" + curr_array[1] + "-" + curr_array[2];
        Log.d("due_date", due_date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        RealmResults<GlucoseValue> results = realm.where(GlucoseValue.class)
                .equalTo("Email", email)
                .between( "Date", format.parse(due_date),
                        format.parse(current_date))
                .findAll();
        Log.d( TAG, "size is " + String.valueOf(results.size() ) );
        if(results.size() >= 1) {
            for (GlucoseValue g : results) {
                Float value = Float.valueOf(g.getValue());
                if ( g.getStatus().equals("Hyperglycemia")) arr_count[0] += 1f;             // Hyper
                else if ( g.getStatus().equals("High Blood Glucose")) arr_count[1] += 1f;        // High
                else if ( g.getStatus().equals("Normal Blood Glucose")) arr_count[2] += 1f;         // Normal
                else if ( g.getStatus().equals("Low Blood Glucose")) arr_count[3] += 1f;         // Low
                else arr_count[4] += 1f;                         // Hypo
            }
        }
        realm.close();
        return arr_count;
    }

    public UserProfile searchUserProfile( final String email){
        realm = null;
        realm = Realm.getDefaultInstance();
        UserProfile userProfile = realm.where(UserProfile.class)
                .equalTo("Email", email)
                .findFirst();
        if(userProfile != null) {
            Log.d( getClass().getSimpleName(), userProfile.getEmail());
            return userProfile;
        }
        else {
            Log.d( getClass().getSimpleName(), "Null Object");
            realm.close();
            return null;

        }

    }

    public CaretakerProfile searchCaretakerProfile( final String email){

        realm = Realm.getDefaultInstance();
        CaretakerProfile caretakerProfile = realm.where(CaretakerProfile.class)
                .equalTo("Email", email)
                .findFirst();
        if(caretakerProfile != null) {
            Log.d( getClass().getSimpleName(), caretakerProfile.getEmail());
            return caretakerProfile;
        }
        else {
            Log.d( getClass().getSimpleName(), "Null Object");
            return null;
        }

    }

    public RealmResults<GlucoseValue> retChartValue( final String email){
        realm = Realm.getDefaultInstance();
        RealmResults<GlucoseValue> value = realm.where(GlucoseValue.class)
                .equalTo("Email", email)
                .sort("Date", Sort.ASCENDING)
                .findAll();
        Log.d(getClass().getSimpleName(), "GlucoseValue size:  " + value.size());
        realm.close();
        return value;
    }

    public int nextID( final String email){
        realm = Realm.getDefaultInstance();
        RealmResults<AlarmModel> value = realm.where(AlarmModel.class)
                .equalTo("Email", email)
                .findAll();
        return value.size();
    }

    private String currDayInMonth(String day, String month, String year){
        if( month.equals("01") || month.equals("03") || month.equals("05") || month.equals("07") ||
                month.equals("08") || month.equals("10") || month.equals("12") ){
            return String.valueOf( Integer.valueOf(day) +31 - 6);
        }
        else if ( month.equals("04") || month.equals("06") || month.equals("09") || month.equals("11")){
            return String.valueOf( Integer.valueOf(day) +30 -6);
        }

        else {
            if ( Integer.valueOf(year)%400 == 0 )
                return String.valueOf( Integer.valueOf(day) + 29 -6);           // leap year
            else if ( Integer.valueOf(year)%100 == 0)
                return String.valueOf( Integer.valueOf(day) + 28 -6);           // not leap year
            else if ( Integer.valueOf(year)%4 == 0)
                return String.valueOf( Integer.valueOf(day) + 29 -6);           // leap year
            else
                return String.valueOf( Integer.valueOf(day) + 28 -6);           // not leap year
        }
    }

    public GlucoseValue searchLastestGlucose( final String Email){
        realm = Realm.getDefaultInstance();
        Log.d(getClass().getSimpleName(), Email);
        RealmResults<GlucoseValue> value = realm.where(GlucoseValue.class)
                .equalTo("Email", Email)
                .sort("Date", Sort.ASCENDING)
                .findAll();
        Log.d("Value: ", String.valueOf(value) );
        if( value.size() >= 1)
            return value.last();
        else
            return null;
    }

    public NotifyModel searchNotify( final String Email){
        realm = Realm.getDefaultInstance();
        NotifyModel value = realm.where(NotifyModel.class)
                .equalTo("Email", Email)
                .findFirst();
        return value;
    }

}
