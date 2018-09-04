package com.example.yggdrasil.realdiabeticapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Yggdrasil on 8/4/2561.
 */

public class AddSelectActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Boolean isConnected;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg_method);

        isConnected = new InternetConnection().internetConnectionAvailable(200);
        if( isConnected) {
            mAuth = FirebaseAuth.getInstance();
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mAuth == null) {
                        startActivity(new Intent(AddSelectActivity.this, NewLoginActivity.class));
                        finish();
                    }
                }
            };
        }
        findViewById(R.id.manual).setOnClickListener(this);
        findViewById(R.id.mghbt1).setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.manual:
                Log.d("Manual", "manual");
                startActivity( new Intent( AddSelectActivity.this, test_AddBGActivity.class));
                finish();
                break;
            case R.id.mghbt1:
                startActivity( new Intent( AddSelectActivity.this, BluetoothActivity.class));
                finish();
                break;
        }
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isConnected)
            mAuth.addAuthStateListener(mAuthStateListener);
    }
}
