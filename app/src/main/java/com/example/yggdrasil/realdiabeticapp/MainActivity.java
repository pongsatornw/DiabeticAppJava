package com.example.yggdrasil.realdiabeticapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yggdrasil.realdiabeticapp.Fragment.PagerAdapter;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import javax.annotation.Nonnull;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private TextView nav_username, nav_email;
    private View hView;
    private Toolbar toolbar;
    FragmentPagerAdapter adapterViewPager;
    private String email;
    private String provider;
    private FirebaseAuth mAuth;
    private FirebaseAuth offlineAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseFirestore db;
    boolean isConnected;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        boolean isConnected = netWorkStatus();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        hView =  navigationView.getHeaderView(0);
        nav_username = hView.findViewById(R.id.nav_username);
        nav_email = hView.findViewById(R.id.nav_email);
        imageView = hView.findViewById(R.id.imageView);

        // Network Connect
        if (isConnected){
            netWorkConnected();
        }
        // Network Not Connect
        else {
            netWorkNotConnect();
        }

        ViewPager vpPager = findViewById(R.id.viewpager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager());
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        TabLayout tabs = findViewById(R.id.tabs);
        vpPager.setAdapter(adapterViewPager);
        tabs.setupWithViewPager(vpPager);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@Nonnull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
        } else if (id == R.id.nav_bg) {
            Intent I = new Intent(MainActivity.this, AddSelectActivity.class);
            startActivity(I);
        } else if (id == R.id.nav_reminder) {
            /*Intent I = new Intent(getApplicationContext(), BluetoothActivity.class);
            startActivity(I);*/
        } else if (id == R.id.nav_sync) {

        } else if (id == R.id.nav_history) {
            startActivity( new Intent(MainActivity.this, HistoryActivvity.class) );
            finish();
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_setting) {
            startActivity( new Intent(MainActivity.this, SettingActivity.class) );
            finish();
        } else if (id == R.id.nav_signout) {
            signOut();
            startActivity(new Intent( MainActivity.this, NewLoginActivity.class) );
            finish();
         }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean netWorkStatus(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void netWorkConnected(){
        mAuth = FirebaseAuth.getInstance();
        if( mAuth.getCurrentUser() != null) {
            db = FirebaseFirestore.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mAuth.getCurrentUser() == null)
                        //new Intent(MainActivity.this, NewLoginActivity.class);
                        return;
                        //Toast.makeText(MainActivity.this, "Connection Lost", Toast.LENGTH_SHORT).show();
                }
            };

            email = mAuth.getCurrentUser().getEmail();
            nav_username.setText(mAuth.getCurrentUser().getDisplayName());
            nav_email.setText(email);
            if (mAuth.getCurrentUser().getPhotoUrl() != null) {
                Picasso.get()
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .into(imageView);
            }
        }
        else if (mAuth.getCurrentUser() == null){
            Toast.makeText(MainActivity.this, "Error, Account not found!", Toast.LENGTH_SHORT).show();
        }
    }

    private void netWorkNotConnect(){
        email = new SimplePreference(MainActivity.this).getEmailFromPref();
        nav_username.setText("- Use in offline mode"); // Fname, Lname
        nav_email.setText(email);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume(){
        super.onResume();
        isConnected = netWorkStatus();
        if(isConnected) {
            netWorkConnected();
            this.setTitle("Online");
            toolbar.setBackgroundResource(R.color.my_blue);
            Log.d("Online: ", " Network Online");
            updateInBackground();
        }

        else{
            netWorkNotConnect();
            this.setTitle("Offline");
            toolbar.setBackgroundResource(R.color.my_red);
            Log.d("Offline: ", " Network Offline");
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    protected void signOut(){
        Log.d("Provider ID", mAuth.getCurrentUser().getProviderId());
        for( UserInfo user : mAuth.getCurrentUser().getProviderData()){
            if (user.getProviderId().equals("facebook.com")) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                Log.d("Facebook SignOut", "Success!");
                return;
            }
        }
            Log.d("Normal SignOut", "Success!");
            mAuth.signOut();
    }

    private void updateInBackground(){
        Intent intent = new Intent(MainActivity.this, UpdateService.class);
        intent.putExtra("email", mAuth.getCurrentUser().getEmail());
        Log.d(getClass().getSimpleName(), "Prepare to call service");
        startService(intent);
    }

}


