package com.example.yggdrasil.realdiabeticapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yggdrasil on 14/4/2561.
 */

public class NewRegisterForEmali extends AppCompatActivity implements View.OnClickListener {

    private String TAG;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText edt1, edt2, edt3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new);
        TAG = getClass().getSimpleName();
        findViewById(R.id.confirm_si01).setOnClickListener(this);
        findViewById(R.id.reset_si01).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if ( mAuth.getCurrentUser() == null){
                    Toast.makeText(NewRegisterForEmali.this,
                            "Plese check connection!",
                            Toast.LENGTH_SHORT ).show();
                    startActivity( new Intent(NewRegisterForEmali.this, NewLoginActivity.class) );
                }
            }
        };
        initialView();
    }

    private void initialView(){
        List<EditText> list = new ArrayList<>();
        list.add(edt1 = findViewById(R.id.email_si));
        list.add(edt2 = findViewById(R.id.pwd_si));
        list.add(edt3 = findViewById(R.id.cpwd_si));

        for( EditText editText : list){
            editText.setOnClickListener(this);
            editText.setMaxLines(1);
            editText.setFocusable(false);
        }
    }

    public void customDialog(String title, String message, final EditText editText){
        AlertDialog.Builder dialog = new AlertDialog.Builder(NewRegisterForEmali.this);
        final EditText edt = new EditText(NewRegisterForEmali.this);
        if( editText.getText().toString() != null) {
            edt.setText(editText.getText().toString());

            Log.d("Length: ", String.valueOf(editText.getText().length()));
            edt.post(new Runnable() {
                @Override
                public void run() {
                    edt.setSelection(edt.getText().length());
                }
            });
        }
        if( editText == edt1)
            edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        else{
            edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        edt.setMaxLines(1);
        edt.setPadding(60,0,0,20);
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

    private void signUp(){
        Log.d(TAG, "Sign Up");
        final String email = edt1.getText().toString();
        final String pwd = edt2.getText().toString();
        mAuth.createUserWithEmailAndPassword( email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity( new Intent( NewRegisterForEmali.this, NewLoginActivity.class));
                            finish();
                        }else {
                            // If sign in fails, display a message to the user.
                            // Link with credential
                            Toast.makeText(NewRegisterForEmali.this,
                                    "This email already sign in by others provider!!!",
                                    Toast.LENGTH_SHORT).show();
                            task.getException();
                            //linkAccount(email, pwd);


                        }

                        // ...
                    }
                });

    }

    @Override
    public void onClick(View v){
        switch( v.getId()){
            case R.id.reset_si01 :
                edt1.setText(null);
                edt2.setText(null);
                edt3.setText(null);
                break;
            case R.id.confirm_si01:
                signUp();
                break;
            case R.id.email_si:
                customDialog("Email", "Enter your email: ", edt1);
                break;
            case R.id.pwd_si:
                customDialog("Email", "Enter your password: ", edt2);
                break;
            case R.id.cpwd_si:
                customDialog("Email", "Enter password again: ", edt3);
                break;
        }
    }

    public void linkAccount(String email, String pwd){
        AuthCredential credential = EmailAuthProvider.getCredential(email, pwd);

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(NewRegisterForEmali.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}
