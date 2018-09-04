package com.example.yggdrasil.realdiabeticapp;

import android.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.nfc.FormatException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.yggdrasil.realdiabeticapp.BroadcastManager.AlarmManagerBroadcastReceiver;
import com.example.yggdrasil.realdiabeticapp.BroadcastManager.NotifyBroadcastReceiver;
import com.example.yggdrasil.realdiabeticapp.Models.CaretakerProfile;
import com.example.yggdrasil.realdiabeticapp.Models.GlucoseValue;
import com.example.yggdrasil.realdiabeticapp.Models.NotifyModel;
import com.example.yggdrasil.realdiabeticapp.Models.UserProfile;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmInsert;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmSearch;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;

public class test_AddBGActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Boolean isConnect;
    private String email;
    private BroadcastReceiver broadcastReceiver;
    private TextInputLayout dateL, timeL, gluL, measuredL;
    private TextInputEditText dateI, timeI, gluI, measuredI;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private DatePickerDialog.OnDateSetListener datePickerDialog;
    private TimePickerDialog.OnTimeSetListener timePickerDialog;
    private String TAG;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbloodglucose);
        isConnect = new InternetConnection().internetConnectionAvailable(200);
        TAG = getClass().getSimpleName();
        if(isConnect){
            mAuth = FirebaseAuth.getInstance();
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(mAuth == null)
                        startActivity( new Intent( test_AddBGActivity.this, NewLoginActivity.class));
                    //finish();
                }
            };
            email = mAuth.getCurrentUser().getEmail();
        }else{
            email = new SimplePreference(test_AddBGActivity.this).getEmailFromPref();
        }
        broadcastReceiver = new NotifyBroadcastReceiver();
        if (ContextCompat.checkSelfPermission( test_AddBGActivity.this,android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("permission", "permission denied to SEND_SMS - requesting it");

            ActivityCompat.requestPermissions( (Activity)test_AddBGActivity.this,
                    new String[]{android.Manifest.permission.SEND_SMS}, 123);
        }
        dateL = findViewById(R.id.dateInput);
        timeL = findViewById(R.id.timeInput);
        gluL = findViewById(R.id.gluInput);
        measuredL = findViewById(R.id.measuredInput);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        dateI = findViewById(R.id.dateEditText);
        timeI = findViewById(R.id.timeEditText);
        gluI = findViewById(R.id.gluEditText);
        measuredI = findViewById(R.id.measuredEditText);

        Button reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateI.setText(null);
                timeI.setText(null);
                gluI.setText(null);
                measuredI.setText(null);
            }
        });

        Button add = findViewById(R.id.add_value);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RealmInsert insert = new RealmInsert();
                String[] arr_date = dateI.getText().toString().split("/");
                String real_date = arr_date[2] + "-" + arr_date[1] + "-" + arr_date[0];

                insert.insertGlucose(email,
                        gluI.getText().toString(),
                        real_date,
                        timeI.getText().toString(),
                        measuredI.getText().toString(),
                        interpret( gluI.getText().toString(), measuredI.getText().toString()),
                        Boolean.FALSE);
                RealmUpdate update = new RealmUpdate();
                update.upsertStatus(email,
                        Boolean.FALSE, Boolean.FALSE,
                        Boolean.FALSE, Boolean.FALSE,
                        Boolean.FALSE, Boolean.TRUE);
                Intent intent = new Intent(test_AddBGActivity.this, NotifyBroadcastReceiver.class);
                intent.setAction("android.action.notify");
                intent.putExtra("date", dateI.getText().toString());
                intent.putExtra("time", timeI.getText().toString());
                intent.putExtra("glu", gluI.getText().toString());
                intent.putExtra("mea", measuredI.getText().toString());
                intent.putExtra("status", interpret( gluI.getText().toString(), measuredI.getText().toString()));
                sendBroadcast(intent);

                startActivity( new Intent( test_AddBGActivity.this, MainActivity.class));
                finish();

                send();
                dateI.setText(null);
                timeI.setText(null);
                gluI.setText(null);
                measuredI.setText(null);

            }
        });

        gluI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(test_AddBGActivity.this);
                final EditText editText = new EditText(test_AddBGActivity.this);
                editText.setMaxLines(1);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setPadding(15, 0, 15, 0);
                dialog.setTitle("Blood Glucose");
                dialog.setMessage("Enter Blood Glucose Value");
                dialog.setView(editText);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gluI.setText(editText.getText().toString());
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.create().show();
                dialog.setCancelable(true);
            }
        });

        measuredI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] mea_list = new String[]{
                        "Before Meal", "After Meal",
                        "Exercise", "After Using Medicine"
                };
                final AlertDialog.Builder builder = new AlertDialog.Builder(test_AddBGActivity.this);
                final AtomicReference<String> mea = new AtomicReference<>();
                builder.setTitle("Select your Measured Event: ");
                builder.setSingleChoiceItems(new String[]{
                        "Before Meal", "After Meal",
                        "Exercise", "After Using Medicine"
                }, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        mea.set(mea_list[which]);
                        measuredI.setText(mea_list[which]);
                        builder.setCancelable(true);
                        builder.create().dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*Log.d(TAG, mea.get());
                        mea.get();
                        measuredI.setText(mea.get());
                        builder.setCancelable(true);*/
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setCancelable(true);
                    }
                });
                builder.create().show();
                builder.setCancelable(true);
            }
        });
        dateI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                //Toast.makeText(getApplicationContext(), year, Toast.LENGTH_LONG).show();
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(test_AddBGActivity.this,
                        datePickerDialog,
                        year, month, day);
                dialog.getWindow();
                dialog.show();
            }
        });

        datePickerDialog = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;

                String date = null;
                if (month < 10) {
                    if (day < 10) {
                        date = "0" + day + "/" + "0" + month + "/" + year;
                    } else if (day >= 10) {
                        date = day + "/" + "0" + month + "/" + year;
                    }
                } else if (month >= 10) {
                    if (day < 10) {
                        date = "0" + day + "/" + month + "/" + year;
                    } else if (day >= 10) {
                        date = day + "/" + month + "/" + year;
                    }
                }
                dateI.setText(date);
            }
        };

        timeI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                //Toast.makeText(getApplicationContext(), year, Toast.LENGTH_LONG).show();
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(test_AddBGActivity.this,
                        timePickerDialog,
                        hour, minute, true);
                dialog.getWindow();
                dialog.show();
            }
        });

        timePickerDialog = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                String time = null;
                if (hour < 10) {
                    if (minute < 10) {
                        time = "0" + hour + ":" + "0" + minute;
                    } else if (minute >= 10) {
                        time = "0" + hour + ":" + minute;
                    }
                } else if (hour >= 10) {
                    if (minute < 10) {
                        time = hour + ":" + "0" + minute;
                    } else if (minute >= 10) {
                        time = hour + ":" + minute;
                    }
                }
                timeI.setText(time);
            }
        };

    }


    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void send(){
        RealmSearch realmSearch = new RealmSearch();
        NotifyModel notifyModel = realmSearch.searchNotify( email);

        if( notifyModel.getEveryTime() && notifyModel.getViaEmail())
            sendEmail();
        else if( notifyModel.getHi())
            sendEmail();
        else if( notifyModel.getLo())
            sendEmail();
        else if( notifyModel.getHyper())
            sendEmail();
        else if( notifyModel.getHypo())
            sendEmail();

        if( notifyModel.getEveryTime() && notifyModel.getViaSMS())
            sendSMS();
        else if( notifyModel.getHi())
            sendSMS();
        else if( notifyModel.getLo())
            sendSMS();
        else if( notifyModel.getHyper())
            sendSMS();
        else if( notifyModel.getHypo())
            sendSMS();


    }

    public void sendSMS(){
        Realm realm = Realm.getDefaultInstance();
        CaretakerProfile profile = realm.where(CaretakerProfile.class)
                .equalTo("Email", email)
                .findFirst();
        if( profile != null)
            Log.d(TAG, String.valueOf(profile));

        /*if (ContextCompat.checkSelfPermission( test_AddBGActivity.this,android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("permission", "permission denied to SEND_SMS - requesting it");

            ActivityCompat.requestPermissions( (Activity)test_AddBGActivity.this,
                    new String[]{android.Manifest.permission.SEND_SMS}, 123);
        }*/


        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage( "+66954402268", "095440268",
                    "Patient lastest blood glucose measured at " + dateI.getText().toString() +
                            ", " + timeI.getText().toString() + ", Blood Glucose level is " + gluI.getText().toString()
                    , null, null);
            Toast.makeText(test_AddBGActivity.this, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(test_AddBGActivity.this, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

        /*SMSManager smsManager = new SMSManager(test_AddBGActivity.this);
        smsManager.sentMail( profile.getcTel(),
                "Patient lastest blood glucose measured at " + dateI.getText().toString() +
                        ", " + timeI.getText().toString() + ", Blood Glucose level is " + gluI.getText().toString()
        );*/
    }

    public void sendEmail(){
        LongOperation l = new LongOperation("friend4diabetic@gmail.com", "167761511",
                dateI.getText().toString(), timeI.getText().toString(),
                gluI.getText().toString(), "friend4diabetic@gmail.com");
        l.execute();
    }

    private String interpret(String value, String Measured){
        if ( Measured.equals("Before Meal") | Measured.equals("Exercise") )
        {
            if (Integer.valueOf(value) > 200)
                return "Hyperglycemia";
            else if (Integer.valueOf(value) > 130)
                return "High Blood Glucose";
            else if (Integer.valueOf(value) > 70)
                return "Normal Blood Glucose";
            else if (Integer.valueOf(value) > 50)
                return "Low Blood Glucose";
            else
                return "Hypoglycemia";
        }
        else if (Measured.equals("After Meal") | Measured.equals("After Using Medicine") )
        {
            if (Integer.valueOf(value) > 270)
                return "Hyperglycemia";
            else if (Integer.valueOf(value) > 180)
                return "High Blood Glucose";
            else if (Integer.valueOf(value) > 100)
                return "Normal Blood Glucose";
            else if (Integer.valueOf(value) > 50)
                return "Low Blood Glucose";
            else
                return "Hypoglycemia";
        }
        else
            return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.action.notify");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity( new Intent( test_AddBGActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
