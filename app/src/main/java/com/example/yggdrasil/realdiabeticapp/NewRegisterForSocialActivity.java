package com.example.yggdrasil.realdiabeticapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yggdrasil.realdiabeticapp.Models.UserProfile;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;

/**
 * Created by Yggdrasil on 3/4/2561.
 */

public class NewRegisterForSocialActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String Email;
    private String Password;
    private EditText si_birth, si_gender, si_fname, si_lname, si_tel;
    DatePickerDialog.OnDateSetListener datePickerDialog;
    FirebaseFirestore db;
    private String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new02);

        TAG = getClass().getSimpleName();
        mAuth = FirebaseAuth.getInstance();

        initialView();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if ( mAuth.getCurrentUser() == null){         // New User
                    startActivity( new Intent(NewRegisterForSocialActivity.this, NewLoginActivity.class) );
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void customDialog(String title, String message, final EditText editText){
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        if (title.equals("Telephone Number"))
            edittext.setInputType(InputType.TYPE_CLASS_PHONE);
        edittext.setMaxLines(1);
        alertdialog.setTitle( title);
        alertdialog.setMessage( message);
        alertdialog.setView( edittext);
        alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                String fname = edittext.getText().toString();
                editText.setText( fname);
            }
        });
        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        alertdialog.create().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertdialog.create().show();
        alertdialog.setCancelable(true);
    }

    @Override
    public void onClick(View v){
        switch ( v.getId()){
            case R.id.fname_si:
                customDialog( "First Name", "Enter your first name: ", (EditText) findViewById(R.id.fname_si));
                break;
            case R.id.lname_si:
                customDialog( "Last Name", "Enter your last name: ", (EditText) findViewById(R.id.lname_si));
                break;
            case R.id.birth_si:
                setBirthDate();
                break;
            case R.id.gender_si:
                final String[] gender_list = new String[]{"Male", "Female", "Others", "Rather not say"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final AtomicReference<String> gender = new AtomicReference<>();
                builder.setTitle("Select your gender: ");
                builder.setSingleChoiceItems( new String[]{
                        "Male", "Female", "Others", "Rather not say"
                }, 0, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which){
                        gender.set(gender_list[which]);
                        si_gender.setText(gender_list[which]);
                        builder.setCancelable(true);
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        si_gender.setText( gender.get());
                        builder.setCancelable(true);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setCancelable(true);
                    }
                });
                builder.create().show();

                builder.setCancelable(true);break;
            case R.id.tel_si:
                customDialog( "Telephone Number", "Enter your Telephone Number: ", (EditText) findViewById(R.id.tel_si));
                break;
            case R.id.reset_si02:
                Log.d(TAG, "Reset is clicked!");
                si_fname.setText(null);
                si_lname.setText(null);
                si_gender.setText(null);
                si_birth.setText(null);
                si_tel.setText(null);
                break;
            case R.id.next_si02:
                next();
                break;
        }
    }

    public void setBirthDate(){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(calendar.MONTH);
        int day = calendar.get(calendar.DAY_OF_MONTH);

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

                String[] arr_date = date.split("/");
                String real_date = arr_date[2] + "-" + arr_date[1] + "-" + arr_date[0];
                Toast.makeText(NewRegisterForSocialActivity.this, real_date, Toast.LENGTH_SHORT).show();
                si_birth.setText(date);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog( NewRegisterForSocialActivity.this,
                datePickerDialog,
                year, month, day);
        dialog.getWindow();
        dialog.show();
    }

    private void initialView(){

        findViewById(R.id.reset_si02).setOnClickListener(this);
        findViewById(R.id.next_si02).setOnClickListener(this);
        // List of EditText
        List<View> list_view = new ArrayList<>();
        list_view.add( si_birth = findViewById(R.id.birth_si) );
        list_view.add( si_gender = findViewById(R.id.gender_si) );
        list_view.add( si_fname = findViewById(R.id.fname_si) );
        list_view.add( si_lname = findViewById(R.id.lname_si) );
        list_view.add( si_tel = findViewById(R.id.tel_si) );

        for(View v : list_view){
            v.setOnClickListener(this);
        }
    }

    private void next(){
        // Store data in Realm
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long free_memory = statFs.getAvailableBytes();
        if( free_memory/(1024*1024) < 100) { //MB
            AlertDialog.Builder alert = new AlertDialog.Builder(NewRegisterForSocialActivity.this);
            alert.setTitle("Warning!");
            alert.setMessage(" Storage space is low!.\n Please free up some space. ");
            Log.d("Available Space[MB]: ", String.valueOf(free_memory/(1024*1024)));
            return;
        }

        /*Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction(){
            @Override
            public void execute(@NonNull Realm realm){
                UserProfile userProfile = realm.createObject(UserProfile.class, mAuth.getCurrentUser().getEmail());
                userProfile.setFName(si_fname.getText().toString());
                userProfile.setLName(si_lname.getText().toString());
                userProfile.setGender(si_gender.getText().toString());
                userProfile.setBirthdate(si_birth.getText().toString());
                userProfile.setTel(si_tel.getText().toString());
            }
        })*/
        RealmUpdate realmUpdate = new RealmUpdate();
        Log.d(TAG, String.valueOf(si_birth.getText().toString()));
        realmUpdate.upsertUserProfile(
                mAuth.getCurrentUser().getEmail(),
                si_fname.getText().toString(),
                si_lname.getText().toString(),
                si_gender.getText().toString(),
                si_birth.getText().toString(),
                si_tel.getText().toString());

        realmUpdate.upsertStatus(mAuth.getCurrentUser().getEmail(),
                Boolean.TRUE, Boolean.TRUE,
                Boolean.FALSE, Boolean.FALSE,
                Boolean.FALSE, Boolean.FALSE );

        // Store data in Cloud FireStore
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.Firestore.firstname, si_fname.getText().toString() );
        data.put(Constants.Firestore.lastname, si_lname.getText().toString() );
        data.put(Constants.Firestore.gender, si_gender.getText().toString() );
        data.put(Constants.Firestore.birthdate, si_birth.getText().toString());
        data.put(Constants.Firestore.tel, si_tel.getText().toString() );

        db = FirebaseFirestore.getInstance();
        db.collection(Constants.Collect_Profile)
                .document(Constants.Document_User)
                    .collection(mAuth.getCurrentUser().getEmail().substring(0, 1) + "_Start")
                        .document(mAuth.getCurrentUser().getEmail())
                    .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d(TAG, "DocumentSnapshot added: " );
                        startActivity( new Intent( NewRegisterForSocialActivity.this, NewRegisterForSocialActivity02.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document: ", e);
                    }
                });

        //startActivity( new Intent( NewRegisterForSocialActivity.this, NewRegisterforSocialActivity02.class));
    }

}
