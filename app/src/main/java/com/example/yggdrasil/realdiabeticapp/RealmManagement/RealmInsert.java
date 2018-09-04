package com.example.yggdrasil.realdiabeticapp.RealmManagement;

import android.app.Activity;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.*;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.yggdrasil.realdiabeticapp.Models.CaretakerProfile;
import com.example.yggdrasil.realdiabeticapp.Models.GlucoseValue;
import com.example.yggdrasil.realdiabeticapp.Models.UserAccount;
import com.example.yggdrasil.realdiabeticapp.Models.UserProfile;

import java.text.ParseException;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmAsyncTask;

/**
 * No @ViewById
 */

public class RealmInsert extends Activity {

    private String TAG = "RealmInsert";
    private Realm realm;

    public void insertAccount(final String email, final String password) {
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                // Add a person
                realm = Realm.getDefaultInstance();
                UserAccount userAccount = realm.createObject(UserAccount.class, email);
                userAccount.setPassword(password);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
                Log.e(TAG, "Add new account success! ");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                realm.close();
                Log.e(TAG, "insertUsernameError: onError: ", error);
            }

        });
        realm.close();
    }


    /** use updateUserprofile method in RealmUpdate class instead
     *
     * @param email email
     * @param fName first name
     * @param lName last name
     * @param birthDate birthdate
     * @param gender gender
     */
    @Deprecated
    public void insertProfile( final String email, final String fName, final String lName
        , final String birthDate, final String gender){
        /*realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a person
                UserProfile userProfile = realm.createObject(UserProfile.class, email);
                userProfile.setFName(fName);
                userProfile.setLName(lName);
                userProfile.setBirthdate(birthDate);
                userProfile.setGender(gender);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "Add new profile success! ");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                realm.close();
                Log.e(TAG, "insert Profile Error: onError: ", error);
            }

        });
        realm.close();*/
    }

    public void insertCaretakerProfile( final String email, final String cfName, final String clName,
                                        final String cTel, final String cEmail) {
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a person
                CaretakerProfile caretakerProfile = realm.createObject(CaretakerProfile.class, email);
                caretakerProfile.setcFName(cfName);
                caretakerProfile.setcLName(clName);
                caretakerProfile.setcEmail(cEmail);
                caretakerProfile.setcTel(cTel);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "Add new account success! ");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                realm.close();
                Log.e(TAG, "insertUsernameError: onError: ", error);
            }

        });
        realm.close();
    }

    public void insertGlucose( final String email, final String Value, final String date,
                               final String Time, final String Measured, final String status,
                               final Boolean update){
        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add a person
                try {
                    GlucoseValue glucoseValue = realm.createObject(GlucoseValue.class);
                    glucoseValue.setDate((new SimpleDateFormat("yyyy-MM-dd"))
                            .parse(date));
                    glucoseValue.setEmail(email);
                    glucoseValue.setValue(Value);
                    glucoseValue.setTime(Time);
                    glucoseValue.setMeasured(Measured);
                    glucoseValue.setStatus(status);
                    glucoseValue.setUpdate(update);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Add new account success! ");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                realm.close();
                Log.e(TAG, "insertUsernameError: onError: ", error);
            }
        });
        if(realm.isClosed())
            realm.close();
    }

    @Override
    public void onStart(){
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop(){
        super.onStop();
        if ( !realm.isClosed())
            realm.close();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( !realm.isClosed())
            realm.close();
    }
}
