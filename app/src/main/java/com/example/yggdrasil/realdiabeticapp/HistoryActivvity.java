package com.example.yggdrasil.realdiabeticapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.yggdrasil.realdiabeticapp.Models.GlucoseValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Yggdrasil on 18/4/2561.
 */

public class HistoryActivvity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private boolean isConnected;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        if(isConnected = new InternetConnection().internetConnectionAvailable(500)){
            mAuth = FirebaseAuth.getInstance();
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(mAuth == null) {
                        startActivity(new Intent(HistoryActivvity.this, NewLoginActivity.class));
                        //finish();
                    }
                }
            };
            email = mAuth.getCurrentUser().getEmail();
            Log.d("Online email: ", email);
        }else{
            email = new SimplePreference(HistoryActivvity.this).getEmailFromPref();
            Log.d("Offline email: ", email);
        }

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        List<GlucoseValue> glucoseValues = new ArrayList<>();
        Realm realm;
        realm = Realm.getDefaultInstance();
        RealmResults<GlucoseValue> values = realm.where(GlucoseValue.class)
                .equalTo("Email", email)
                .findAll();

        if( values.size() > 0) {
            glucoseValues.addAll(values);
        }
        mAdapter = new RecyclerAdapter(glucoseValues);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( isConnected = new InternetConnection().internetConnectionAvailable(500) ) {
            mAuth.addAuthStateListener(mAuthStateListener);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity( new Intent( this, MainActivity.class));
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

}
