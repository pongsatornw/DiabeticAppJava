package com.example.yggdrasil.realdiabeticapp.RealmManagement;

import android.content.Context;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.yggdrasil.realdiabeticapp.Models.AlarmModel;
import com.example.yggdrasil.realdiabeticapp.Models.CaretakerProfile;
import com.example.yggdrasil.realdiabeticapp.Models.NotifyModel;
import com.example.yggdrasil.realdiabeticapp.Models.UpdateStatus;
import com.example.yggdrasil.realdiabeticapp.Models.UserProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

public class RealmUpdate {

    private Realm realm;
    private String TAG = "RealmUpdate";
    /**
     *
     * @param email Email of User that use in this application.
     * @param fname First Name of User
     * @param lname Last Name of User
     * @param gender Gender of User
     * @param birthdate Birth Date of User
     * @param tel Telephone Number of User
     */

    public void upsertUserProfile( final String email, final String fname, final String lname,
                                   final String gender, final String birthdate, final String tel){
        Log.d(getClass().getSimpleName(), email + "01");

        realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    Log.d(getClass().getSimpleName(), email + "02");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Log.d(TAG, birthdate);
                    //String[] arr_date = birthdate.split("/");
                    //Log.d(getClass().getSimpleName(), birthdate);
                    //Log.d(getClass().getSimpleName(), arr_date[0]);
                    //Log.d(getClass().getSimpleName(), arr_date[1]);
                    //Log.d(getClass().getSimpleName(), arr_date[2]);
                    //String real_date = arr_date[2] + "-" + arr_date[1] + "-" + arr_date[0];
                    Date date = format.parse(birthdate);
                    Log.d(getClass().getSimpleName(), email + "03");
                    UserProfile data = realm.where(UserProfile.class)
                            .equalTo("Email", email)
                            .findFirst();
                    Log.d(getClass().getSimpleName(), email + "04");
                    //realm.close();

                    realm = Realm.getDefaultInstance();
                    if (data == null) {
                        UserProfile userProfile = realm.createObject(UserProfile.class, email);
                        userProfile.setFName(fname);
                        userProfile.setLName(lname);
                        userProfile.setGender(gender);
                        userProfile.setBirthdate(date);
                        userProfile.setTel(tel);
                        Log.d(getClass().getSimpleName(), email + "05");
                        Log.d(getClass().getSimpleName(), "Create Account Finishhh!!");
                        return;
                    }
                    data.setFName(fname);
                    data.setLName(lname);
                    data.setGender(gender);
                    data.setBirthdate(date);
                    data.setTel(tel);
                    Log.d(getClass().getSimpleName(), String.valueOf(data));
                } catch (ParseException exception) {
                    exception.printStackTrace();
                }
            }
        }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    realm.close();
                    Log.d(TAG, "Add Profile Success ");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    realm.close();
                    Log.e(TAG, "insert Profile Error: onError: ", error);
                }

            });
        if (!realm.isClosed())
            realm.close();
    }

    public void upsertCaregiverProfile( final String email, final String cfname, final String clname
                , final String cemail, final String ctel){
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                CaretakerProfile data = realm.where(CaretakerProfile.class)
                        .equalTo("Email", email)
                        .findFirst();
                Log.d(getClass().getSimpleName(), email);

                realm = Realm.getDefaultInstance();
                if ( data == null ){
                    CaretakerProfile cprofile = realm.createObject( CaretakerProfile.class, email);
                    cprofile.setcFName(cfname);
                    Log.d(getClass().getSimpleName(), cfname);
                    cprofile.setcLName(clname);
                    Log.d(getClass().getSimpleName(), clname);
                    cprofile.setcTel(ctel);
                    Log.d(getClass().getSimpleName(), ctel);
                    cprofile.setcEmail(cemail);
                    Log.d(getClass().getSimpleName(), cemail);
                    Log.d(RealmUpdate.class.getSimpleName(), "Create Caretaker Profile Object");
                    realm.close();
                    return;
                }
                data.setcFName(cfname);
                Log.d(getClass().getSimpleName(), cfname);
                data.setcLName(clname);
                Log.d(getClass().getSimpleName(), clname);
                data.setcEmail(cemail);
                Log.d(getClass().getSimpleName(), cemail);
                data.setcTel(ctel);
                Log.d(getClass().getSimpleName(), ctel);
                Log.d(getClass().getSimpleName(), "Add/change caregiver profile");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if(!realm.isClosed())
                  realm.close();
                Log.d(RealmUpdate.class.getSimpleName(), "Caretaker Profile add/change complete!!!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                realm.close();
            }
        });
        if ( !realm.isClosed())
            realm.close();
    }

    public void upsertStatus( final String email
        , final Boolean profile, final Boolean allow_profile
        , final Boolean cprofile, final Boolean allow_cprofile
        , final Boolean value, final Boolean allow_value){

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                UpdateStatus data = realm.where(UpdateStatus.class)
                        .equalTo("Email", email)
                        .findFirst();
                Log.d(getClass().getSimpleName(), email);

                realm = Realm.getDefaultInstance();
                if ( data == null ){
                    UpdateStatus status = realm.createObject( UpdateStatus.class, email);
                    status.setProfile(profile);
                    Log.d(getClass().getSimpleName(), String.valueOf(profile) );
                    status.setCaregiver_profile(cprofile);
                    Log.d(getClass().getSimpleName(), String.valueOf(cprofile) );
                    status.setGlucose_value(value);
                    Log.d(getClass().getSimpleName(), String.valueOf(value) );
                    Log.d(RealmUpdate.class.getSimpleName(), "Create Update Status Object");
                    realm.close();
                    return;
                }
                if( allow_profile) {
                    data.setProfile(profile);
                    Log.d(getClass().getSimpleName(), String.valueOf(profile));
                }
                if( allow_cprofile){
                    data.setCaregiver_profile(cprofile);
                    Log.d(getClass().getSimpleName(), String.valueOf(cprofile));
                }
                if( allow_value) {
                    data.setGlucose_value(value);
                    Log.d(getClass().getSimpleName(), String.valueOf(value));
                }
                Log.d(getClass().getSimpleName(), "Change status Table");
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if(!realm.isClosed())
                    realm.close();
                Log.d(RealmUpdate.class.getSimpleName(), "Status Table add/update complete!!!");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull Throwable error) {
                if(!realm.isClosed())
                    realm.close();
            }
        });
        if ( !realm.isClosed())
            realm.close();
    }


    public void upsertAlarm( final String email, final String id, final String mode
            , final String dayofweek, final String hourofday, final String minuteofhour){
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AlarmModel data = realm.where(AlarmModel.class)
                        .equalTo("Email", email)
                        .equalTo( "ID", id)
                        .findFirstAsync();
                if (data == null){
                    AlarmModel alarmModel = realm.createObject(AlarmModel.class);

                    alarmModel.setEmail(email);
                    alarmModel.setID(id);
                    alarmModel.setMode(mode);
                    alarmModel.setHourOfDay(hourofday);
                    alarmModel.setMinuteOfHour(minuteofhour);
                    if ( mode.equals("exact")){
                        alarmModel.setDatyOfWeek(dayofweek);
                    }   else {
                        alarmModel.setDatyOfWeek(null);
                    }
                    realm.close();
                }
                else {
                    data.setEmail(email);
                    data.setID(id);
                    data.setMode(mode);
                    data.setHourOfDay(hourofday);
                    data.setMinuteOfHour(minuteofhour);
                    if ( mode.equals("exact")){
                        data.setDatyOfWeek(dayofweek);
                    }   else {
                        data.setDatyOfWeek(null);
                    }
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(@NonNull  Throwable error) {
                realm.close();
                error.printStackTrace();
            }
        });
        if (!realm.isClosed())
            realm.close();
    }

    public void upsertNotifySetting( final String Email, final Boolean hyper, final Boolean hi,
                                    final Boolean low, final Boolean hypo, final Boolean sms,
                                     final Boolean email, final Boolean every){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                NotifyModel data = realm.where(NotifyModel.class)
                        .equalTo("Email", Email)
                        .findFirst();
                if (data == null){
                    Log.d(TAG, "upsertNotify null data");
                    NotifyModel notifyModel = realm.createObject(NotifyModel.class, Email);
                    notifyModel.setViaSMS(Boolean.TRUE);
                    notifyModel.setViaEmail(Boolean.TRUE);
                    notifyModel.setEveryTime(Boolean.TRUE);
                    notifyModel.setHyper(Boolean.TRUE);
                    notifyModel.setHypo(Boolean.TRUE);
                    notifyModel.setHi(Boolean.TRUE);
                    notifyModel.setLo(Boolean.TRUE);

                    Log.d( getClass().getSimpleName(), "Create Notify Setting");
                }
                else {
                    Log.d(TAG, "upsertNotify data not null");
                    data.setViaSMS(sms);
                    data.setViaEmail(email);
                    data.setLo(low);
                    data.setHyper(hyper);
                    data.setHypo(hypo);
                    data.setHi(hi);
                    data.setEveryTime(every);

                }
            }
        });
        if (!realm.isClosed())
            realm.close();
    }

}
