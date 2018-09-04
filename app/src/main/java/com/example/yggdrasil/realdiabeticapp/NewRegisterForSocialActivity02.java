package com.example.yggdrasil.realdiabeticapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yggdrasil.realdiabeticapp.Models.CaretakerProfile;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRegisterForSocialActivity02 extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore db;
    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText cfname, clname, cemail, ctel;
    private String TAG;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new03);

        initialView();
        TAG = getClass().getSimpleName();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if ( mAuth.getCurrentUser() == null){
                    Toast.makeText(NewRegisterForSocialActivity02.this,
                            "Plese check connection!",
                            Toast.LENGTH_SHORT ).show();
                    startActivity( new Intent(NewRegisterForSocialActivity02.this, NewLoginActivity.class) );
                }
            }
        };
    }

    public void initialView(){
        findViewById(R.id.reset_si03).setOnClickListener(this);
        findViewById(R.id.next_si03).setOnClickListener(this);
        List<EditText> list = new ArrayList<>();
        list.add( cfname = findViewById(R.id.cfname_si) );
        list.add( clname = findViewById(R.id.clname_si) );
        list.add( cemail = findViewById(R.id.cemail_si) );
        list.add( ctel = findViewById(R.id.ctel_si) );

        for( EditText editText : list){
            editText.setOnClickListener(this);
            editText.setMaxLines(1);
            editText.setFocusable(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.cfname_si:
                customDialog("Caregiver First Name", "Enter caregiver first name: "
                    , cfname);
                break;
            case R.id.clname_si:
                customDialog("Caregiver Last Name", "Enter caregiver last name: "
                    , clname);
                break;
            case R.id.cemail_si:
                customDialog("Caregiver Email", "Enter caregiver email: "
                    , cemail);
                break;
            case R.id.ctel_si:
                customDialog("Caregiver Telephone Number", "Enter caregiver te" +
                    "lephone Number: "
                    , ctel);
                break;
            case R.id.next_si03 :
                if (cfname.getText().toString().equals("")
                        || clname.getText().toString().equals("")
                        || cemail.getText().toString().equals("")
                        || ctel.getText().toString().equals("")){
                    // End if condition

                    Toast.makeText(NewRegisterForSocialActivity02.this,
                            "There are some empty field!",
                            Toast.LENGTH_SHORT).show() ;
                } else{
                    Toast.makeText(NewRegisterForSocialActivity02.this,
                            "Welcome to Application!",
                            Toast.LENGTH_SHORT).show() ;
                    next();
                    startActivity( new Intent( NewRegisterForSocialActivity02.this, MainActivity.class) );
                }

                break;
            case R.id.reset_si03 :
                cfname.setText(null);
                clname.setText(null);
                cemail.setText(null);
                ctel.setText(null);
                break;
        }
    }

    public void next(){

        RealmUpdate realmUpdate = new RealmUpdate();
        realmUpdate.upsertCaregiverProfile(
                mAuth.getCurrentUser().getEmail(),
                cfname.getText().toString(),
                clname.getText().toString(),
                cemail.getText().toString(),
                ctel.getText().toString());
        realmUpdate.upsertStatus(
                mAuth.getCurrentUser().getEmail(),
                false, false,
                true, true,
                false, false );


        Map<String, Object> data = new HashMap<>();
        data.put(Constants.Firestore.firstname, cfname.getText().toString());
        data.put(Constants.Firestore.lastname, clname.getText().toString());
        data.put(Constants.Firestore.email, cemail.getText().toString());
        data.put(Constants.Firestore.tel, ctel.getText().toString());
        db = FirebaseFirestore.getInstance();

        db.collection(Constants.Collect_Profile)
                .document(Constants.Document_Caregiver)
                    .collection(mAuth.getCurrentUser().getEmail().substring(0, 1) + "_Start")
                        .document(mAuth.getCurrentUser().getEmail())
                    .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " );
                        startActivity( new Intent( NewRegisterForSocialActivity02.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document: ", e);
                    }
                });
    }

    public void customDialog(String title, String message, final EditText editText){
        AlertDialog.Builder dialog = new AlertDialog.Builder(NewRegisterForSocialActivity02.this);
        final EditText edt = new EditText(NewRegisterForSocialActivity02.this);
        edt.setInputType(InputType.TYPE_CLASS_TEXT);
        edt.setMaxLines(1);
        edt.setFocusable(true);
        if( title.equals("Caregiver Email"))
            edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setView(edt);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                String fname = edt.getText().toString();
                editText.setText( fname);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        dialog.create().show();
    }

}
