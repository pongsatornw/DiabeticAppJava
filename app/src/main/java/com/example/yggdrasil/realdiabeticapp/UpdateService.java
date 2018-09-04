package com.example.yggdrasil.realdiabeticapp;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Process;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.yggdrasil.realdiabeticapp.Models.CaretakerProfile;
import com.example.yggdrasil.realdiabeticapp.Models.GlucoseValue;
import com.example.yggdrasil.realdiabeticapp.Models.UpdateStatus;
import com.example.yggdrasil.realdiabeticapp.Models.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Yggdrasil on 16/4/2561.
 */

public class UpdateService extends Service {

    private String TAG;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        isRunning = true;
        TAG = "UpdateService: ";
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Run thread!!!");
                Log.i(TAG, intent.getStringExtra("email"));
                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                Realm realm = Realm.getDefaultInstance();
                UpdateStatus updateStatus = realm.where(UpdateStatus.class)
                        .equalTo("Email", intent.getStringExtra("email"))
                        .findFirst();
                if(updateStatus != null) {
                    Log.i( TAG, updateStatus.getEmail());
                    update(updateStatus, intent.getStringExtra("email"));
                }
                else {
                    Log.i( TAG, "Null Object");
                    realm.close();

                }

                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        stopSelf();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void update(UpdateStatus updateStatus, String email){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Realm realm ;
        if( !updateStatus.isProfile() ){        // Do if isUpdate == false
            Log.d(TAG, "In update method.");
            realm = Realm.getDefaultInstance();
            UserProfile userProfile = realm.where(UserProfile.class)
                    .equalTo("Email", email)
                    .findFirst();
            if( userProfile != null) {
                Log.d(TAG, String.valueOf(userProfile));
                Map<String, Object> data = new HashMap<>();
                data.put(Constants.Firestore.firstname, userProfile.getFName());
                data.put(Constants.Firestore.lastname, userProfile.getLName());
                //data.put(Constants.Firestore.birthdate, userProfile.getBirthdate());
                data.put(Constants.Firestore.birthdate, new SimpleDateFormat(
                        "yyyy-MM-dd").format(userProfile.getBirthdate()));

                data.put(Constants.Firestore.gender, userProfile.getGender());
                data.put(Constants.Firestore.tel, userProfile.getTel());

                db.collection(Constants.Collect_Profile)
                        .document(Constants.Document_User)
                        .collection(email.substring(0, 1) + "_Start")
                        .document(email)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Log.d(TAG, "DocumentSnapshot added: ");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document: ", e);
                            }
                        });
            }
        }
        if( !updateStatus.isCaregiver_profile() ){
            realm = Realm.getDefaultInstance();
            CaretakerProfile caretakerProfile = realm.where(CaretakerProfile.class)
                    .equalTo("Email", email)
                    .findFirst();
            if( caretakerProfile != null) {
                Map<String, Object> data = new HashMap<>();
                data.put(Constants.Firestore.firstname, caretakerProfile.getcFName());
                data.put(Constants.Firestore.lastname, caretakerProfile.getcLName());
                data.put(Constants.Firestore.email, caretakerProfile.getcEmail());
                data.put(Constants.Firestore.tel, caretakerProfile.getcTel());

                db.collection(Constants.Collect_Profile)
                        .document(Constants.Document_Caregiver)
                        .collection(email.substring(0, 1) + "_Start")
                        .document(email)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Log.d(TAG, "DocumentSnapshot added: ");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document: ", e);
                            }
                        });
            }
        }
        if( !updateStatus.isGlucose_value() ) {
            realm = Realm.getDefaultInstance();
            final FirebaseFirestore db_temp = db;
            final String mail = email;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<GlucoseValue> glucoseValue = realm.where(GlucoseValue.class)
                            .equalTo("Email", mail)
                            .equalTo("Update", Boolean.FALSE)
                            .findAll();

                    ////
                    if (glucoseValue.size() > 0) {
                        for (GlucoseValue value : glucoseValue) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Map<String, Object> data = new HashMap<>();
                            data.put(Constants.Firestore.date, value.getDate());
                            data.put(Constants.Firestore.time, value.getTime());
                            data.put(Constants.Firestore.value, value.getValue());
                            data.put(Constants.Firestore.measured, value.getMeasured());
                            data.put(Constants.Firestore.status, value.getStatus());
                            Log.d("Update", String.valueOf(value.getUpdate()) );
                            value.setUpdate(Boolean.TRUE);
                            Log.d("Update", String.valueOf(value.getUpdate()) );
                            db_temp.collection(Constants.Collect_GValue)
                                    .document(mail.substring(0, 1) + "_Start")
                                    .collection(mail)
                                    .document(df.format(value.getDate()) + ", "+ String.valueOf(value.getTime()))
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void avoid) {
                                            Log.d(TAG, "DocumentSnapshot added: ");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document: ", e);
                                        }
                                    });
                        }
                    }
                    ////
                    }
            });


        }
    }
}
