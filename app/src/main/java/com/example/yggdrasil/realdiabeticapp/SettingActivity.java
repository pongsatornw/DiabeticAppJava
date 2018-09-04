package com.example.yggdrasil.realdiabeticapp;

/**
 * Created by Yggdrasil on 6/2/2561.
 */

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yggdrasil.realdiabeticapp.Models.CaretakerProfile;
import com.example.yggdrasil.realdiabeticapp.Models.NotifyModel;
import com.example.yggdrasil.realdiabeticapp.Models.UserProfile;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmSearch;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmUpdate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.SimpleFormatter;

import io.realm.RealmObject;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String email;
    private Context context;
    private android.support.v7.widget.Toolbar toolbar;
    private LinearLayout linear_fname, linear_lname, linear_birthdate, linear_gender, linear_tel,
            linear_carefname, linear_carelname, linear_careemail, linear_caretel;
    private TextView textView_fname, textView_lname, textView_birthdate, textView_gender, textView_tel,
            textView_carefname, textView_carelname, textView_careemail, textView_caretel;
    private Switch email_sw, sms_sw, sw1, sw2, sw3, sw4, sw5;
    private List<LinearLayout> list_linear;
    private List<TextView> list_textview;
    private AlertDialog.Builder alertdialog;
    DatePickerDialog.OnDateSetListener datePickerDialog;
    private String TAG;
    private Boolean isConnected;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        isConnected = new InternetConnection().internetConnectionAvailable(200);
        if( isConnected){
            mAuth = FirebaseAuth.getInstance();
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if ( mAuth == null) startActivity( new Intent(SettingActivity.this, NewLoginActivity.class));
                }
            };
            email = mAuth.getCurrentUser().getEmail();
        } else {
            SimplePreference preference = new SimplePreference(SettingActivity.this);
            email = preference.getEmailFromPref();
        }
        TAG = getClass().getSimpleName();
        this.context = getApplicationContext();
        list_linear = new ArrayList<>();
        list_textview = new ArrayList<>();
        toolbar = findViewById(R.id.settingtb);
        toolbar.setCollapsible(true);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity( new Intent( SettingActivity.this, MainActivity.class) );
                finish();
            }
        });

        initialVIew();
        initialValue();
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.settinglo_fname){
            myAlertDialog("First Name", "Enter your First Name: ", textView_fname);
        }
        else if(v.getId() == R.id.settinglo_lname){
            myAlertDialog("Last Name", "Enter your Last Name: ", textView_lname);
        }
        else if(v.getId() == R.id.settinglo_birthdate){
            setBirthDate();
        }
        else if(v.getId() == R.id.settinglo_gender){
            final String[] gender_list = new String[]{"Male", "Female", "Others", "Rather not say"};
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select your gender: ");
            builder.setSingleChoiceItems( new String[]{
                    "Male", "Female", "Others", "Rather not say"
            }, 0, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int which){
                    textView_gender.setText(gender_list[which]);
                    builder.setCancelable(true);
                }

            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    builder.setCancelable(true);
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });
            builder.create().show();
            builder.setCancelable(true);
        }
        else if(v.getId() == R.id.settinglo_tel){
            myAlertDialog("Telephone Number", "Enter your telephone number: ", textView_tel);
        }
        else if(v.getId() == R.id.settinglo_c_fname){
            myAlertDialog("Caretaker First Name", "Enter caretaker First Name: ", textView_carefname);
        }
        else if(v.getId() == R.id.settinglo_c_lname){
            myAlertDialog("Caretaker Last Name", "Enter caretaker Last Name: ", textView_carelname);
        }
        else if(v.getId() == R.id.settinglo_c_email){
            myAlertDialog("Caretaker Email", "Enter caretaker email: ", textView_careemail);
        }
        else if(v.getId() == R.id.settinglo_c_tel){
            myAlertDialog("Caretaker Telephone Number", "Enter caretaker telephone number: ", textView_caretel);
        }

    }

    public void initialVIew(){

        email_sw = findViewById(R.id.seekBar1);
        sms_sw = findViewById(R.id.seekBar2);
        sw1 = findViewById(R.id.seekBar3);
        sw2 = findViewById(R.id.seekBar4);
        sw3 = findViewById(R.id.seekBar5);
        sw4 = findViewById(R.id.seekBar6);
        sw5 = findViewById(R.id.seekBar7);

        /* layout initial & add to list */
        list_linear.add(linear_fname = findViewById(R.id.settinglo_fname));
        list_linear.add(linear_lname = findViewById(R.id.settinglo_lname));
        list_linear.add(linear_birthdate = findViewById(R.id.settinglo_birthdate));
        list_linear.add(linear_gender = findViewById(R.id.settinglo_gender));
        list_linear.add(linear_tel = findViewById(R.id.settinglo_tel));
        list_linear.add(linear_carefname = findViewById(R.id.settinglo_c_fname) );
        list_linear.add(linear_carelname = findViewById(R.id.settinglo_c_lname) );
        list_linear.add(linear_careemail = findViewById(R.id.settinglo_c_email) );
        list_linear.add(linear_caretel = findViewById(R.id.settinglo_c_tel) );
        /* textView initial & add to list */
        list_textview.add(textView_fname = findViewById(R.id.settingtv_fname));
        list_textview.add(textView_lname = findViewById(R.id.settingtv_lname));
        list_textview.add(textView_birthdate = findViewById(R.id.settingtv_birthdate));
        list_textview.add(textView_gender = findViewById(R.id.settingtv_gender));
        list_textview.add(textView_tel = findViewById(R.id.settingtv_tel));
        list_textview.add(textView_carefname = findViewById(R.id.settingtv_c_fname) );
        list_textview.add(textView_carelname = findViewById(R.id.settingtv_c_lname) );
        list_textview.add(textView_careemail = findViewById(R.id.settingtv_c_email) );
        list_textview.add(textView_caretel = findViewById(R.id.settingtv_c_tel) );

        // layout set event
        for(LinearLayout linearLayout : list_linear){
            linearLayout.setOnClickListener(this);
        }

        // textView set event
        for(TextView textView : list_textview){
            textView.setOnClickListener(this);
        }
    }

    public void initialValue(){
        RealmSearch search = new RealmSearch();
        UserProfile profile = search.searchUserProfile(email);
        Log.d(getClass().getSimpleName(), email);
        InternetConnection connection = new InternetConnection();
        Boolean conn_status = connection.internetConnectionAvailable(1000);
        if( profile != null) {
            Log.d("Tel: ", "profile not null");
            textView_fname.setText(profile.getFName());
            textView_lname.setText(profile.getLName());
            textView_birthdate.setText((new SimpleDateFormat("yyyy-MM-dd")).format(profile.getBirthdate()));
            textView_gender.setText(profile.getGender());
            textView_tel.setText(profile.getTel());
            Log.d("Tel: ", profile.getTel());
        }else  if( conn_status ){
            Log.d("Tel: ", "profile is null");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.Collect_Profile)
                    .document(Constants.Document_User)
                    .collection(mAuth.getCurrentUser().getEmail().substring(0, 1) + "_Start")
                    .document(mAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult() ;
                                textView_fname.setText(document.getString(Constants.Firestore.firstname) );
                                textView_lname.setText(document.getString(Constants.Firestore.lastname) );
                                textView_birthdate.setText(document.getString(Constants.Firestore.birthdate));
                                textView_gender.setText(document.getString(Constants.Firestore.gender));
                                textView_tel.setText(document.getString(Constants.Firestore.tel));

                            } else {
                                Log.w(TAG, "Error getting profile from firebase.", task.getException());
                            }
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error getting document: ", e);
                        }
                    });
        } else {
            // No Operation!
        }
        CaretakerProfile cprofile = search.searchCaretakerProfile( email);
        if(cprofile != null){
            textView_carefname.setText( cprofile.getcFName());
            textView_carelname.setText( cprofile.getcLName());
            textView_careemail.setText( cprofile.getcEmail());
            textView_caretel.setText( cprofile.getcTel());
        }else if( conn_status ){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.Collect_Profile)
                    .document(Constants.Document_Caregiver)
                    .collection(mAuth.getCurrentUser().getEmail().substring(0, 1) + "_Start")
                    .document(mAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                textView_carefname.setText(document.getString(Constants.Firestore.firstname));
                                textView_carelname.setText(document.getString(Constants.Firestore.lastname));
                                textView_careemail.setText(document.getString(Constants.Firestore.email));
                                textView_caretel.setText(document.getString(Constants.Firestore.tel));

                            } else {
                                Log.w(TAG, "Error getting Caregiver from firebase.", task.getException());
                            }
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error getting document: ", e);
                        }
                    });
        } else{
            // No operation
        }
        NotifyModel notifyModel = search.searchNotify(email);
        if (notifyModel != null){
            email_sw.setChecked(notifyModel.getViaEmail());
            sms_sw.setChecked(notifyModel.getViaSMS());
            sw1.setChecked(notifyModel.getEveryTime());
            sw2.setChecked(notifyModel.getHyper());
            sw3.setChecked(notifyModel.getHi());
            sw4.setChecked(notifyModel.getLo());
            sw5.setChecked(notifyModel.getHypo());
        }

    }

    public void myAlertDialog(String title, String message, final TextView textView){
        alertdialog = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setMaxLines(1);
        if(textView == textView_tel || textView == textView_caretel){
            edittext.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        if(textView == textView_careemail){
            edittext.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        }
        alertdialog.setTitle( title);
        alertdialog.setMessage( message);
        alertdialog.setView( edittext);
        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                String text = edittext.getText().toString();
                textView.setText( text);
            }
        });
        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        alertdialog.create().show();
        alertdialog.setCancelable(false);
        Toast.makeText(getApplicationContext(), textView.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    public void setBirthDate(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //final AtomicReference<String> outer_date = new AtomicReference<>();
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
                //outer_date.set(date);
                String[] arr_date = date.split("/");
                String real_date = arr_date[2] + "-" + arr_date[1] + "-" + arr_date[0];
                Toast.makeText(SettingActivity.this, real_date, Toast.LENGTH_SHORT).show();
                textView_birthdate.setText(date);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog( SettingActivity.this,
                datePickerDialog,
                year, month, day);
        dialog.getWindow();
        dialog.show();
    }

    @Override
    public void onBackPressed(){
        onPause();
    }

    @Override
    public void onPause(){
        super.onPause();
        RealmUpdate realmUpdate = new RealmUpdate();
        realmUpdate.upsertUserProfile( email,
                textView_fname.getText().toString(), textView_lname.getText().toString(),
                textView_gender.getText().toString(), textView_birthdate.getText().toString(),
                textView_tel.getText().toString());

        Log.d(getClass().getSimpleName(), "Prepare to caretaker Profile");
        realmUpdate.upsertCaregiverProfile( email,
                textView_carefname.getText().toString(), textView_carelname.getText().toString(),
                textView_careemail.getText().toString(), textView_caretel.getText().toString());
        Toast.makeText( SettingActivity.this, "Data Change Saved!", Toast.LENGTH_SHORT).show();
        realmUpdate.upsertNotifySetting(email,
                email_sw.isChecked(), sms_sw.isChecked(),
                sw1.isChecked(), sw2.isChecked(),
                sw3.isChecked(), sw4.isChecked(),
                sw5.isChecked());
        realmUpdate.upsertStatus( email,
                false, true,
                false, true,
                false, false);

        Toast.makeText(SettingActivity.this, "Prepare", Toast.LENGTH_SHORT).show();
        startActivity( new Intent( context, MainActivity.class) );
        finish();
    }

    @Override
    public void onResume(){
        super.onResume();
        if( isConnected)
            mAuth.addAuthStateListener(mAuthStateListener);
    }
}
