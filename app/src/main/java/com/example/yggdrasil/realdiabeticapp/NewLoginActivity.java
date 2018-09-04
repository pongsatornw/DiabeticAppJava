package com.example.yggdrasil.realdiabeticapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yggdrasil.realdiabeticapp.Models.UserAccount;
import com.example.yggdrasil.realdiabeticapp.RealmManagement.RealmSearch;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import io.realm.Realm;

/**
 * Created by Yggdrasil on 1/4/2561.
 */

public class NewLoginActivity extends AppCompatActivity implements View.OnClickListener{

    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private String TAG;
    private String idToken;
    private final Context mContext = this;

    private String name, email;
    private String photo;
    private Uri photoUri;
    private SignInButton mSignInButton;
    private boolean isNew, triggerProvide;
    private CallbackManager mCallbackManager;
    private EditText edt1, edt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        TAG = getClass().getSimpleName();
        triggerProvide = false;
        findViewById(R.id.login_btn).setOnClickListener(this);
        edt1 = findViewById(R.id.login_edt);
        edt2 = findViewById(R.id.login2_edt);
        findViewById(R.id.login_with_google).setOnClickListener(this);
        findViewById(R.id.activity_login_clickhere).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( getApplicationContext(), NewRegisterForEmali.class));
            }
        });
        configureSignIn();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "Google Auth, is new account? : " + String.valueOf(isNew));
                if ( mAuth.getCurrentUser() == null){}
                else if ( mAuth.getCurrentUser() != null) {  // Old User
                    triggerProvide = false;
                    //startActivity( new Intent(NewLoginActivity.this, MainActivity.class) );
                }
            }
        };

        // Initial Facebook Login
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook Login Cancel!");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Facebook login error Occur!");
                error.printStackTrace();
            }
        });

    }

    public void configureSignIn(){
        // Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder( getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e( TAG, "Connection Error!");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.login_with_google:
                signIn();
                break;
            case R.id.login_button:
                break;
            case R.id.login_btn:
                emailSignIn();
                break;
        }
    }

    private void emailSignIn(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if( isConnected){       // Online Mode
            mAuth.signInWithEmailAndPassword(edt1.getText().toString(), edt2.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                // New Activity
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(NewLoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                final AlertDialog.Builder builder = new AlertDialog.Builder(NewLoginActivity.this);
                                builder.setTitle("Error");
                                builder.setMessage("Do not find this account! Do you want to register?");
                                builder.setPositiveButton( "Yes", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        startActivity( new Intent( NewLoginActivity.this, NewRegisterForEmali.class));
                                    }
                                });
                                builder.setNegativeButton( "No", new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        builder.create().dismiss();
                                        // Dismiss dialog!
                                    }
                                });
                                builder.create().show();
                                builder.setCancelable(true);
                                builder.create().setCancelable(true);
                            }
                        }
                    });
        }
        else if(!isConnected){  // Offline Mode
            Realm realm = Realm.getDefaultInstance();
            UserAccount userAccount = realm.where(UserAccount.class)
                    .equalTo("Email", email)
                    .findFirst();
            if( userAccount != null) {
                email = userAccount.getEmail();
                SimplePreference simplePreference = new SimplePreference(NewLoginActivity.this);
                simplePreference.setEmailFromPref(email);
            }
            else{
                Toast.makeText( NewLoginActivity.this, "Account not found!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else{

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        triggerProvide = true;
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d(TAG, "signInWithCredential:success");
                            Log.d(TAG, "Google, is new account? : " + String.valueOf(isNew));
                            FirebaseUser user = mAuth.getCurrentUser();
                            if( isNew){
                                startActivity( new Intent(NewLoginActivity.this, NewRegisterForSocialActivity.class));
                            }
                            else if( !isNew) {
                                startActivity( new Intent(NewLoginActivity.this, MainActivity.class));
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText( NewLoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                });

    }

    private void handleFacebookAccessToken(AccessToken accessToken){
        Log.d(TAG, "handleFacebookToken: " + accessToken);

        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential( credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d(TAG, "Facebook, is new account? : " + String.valueOf(isNew));
                            Log.d(TAG, "Facebook Login Success!");
                            if ( isNew)
                                startActivity( new Intent( NewLoginActivity.this, NewRegisterForSocialActivity.class) );
                            else if ( !isNew)
                                startActivity( new Intent( NewLoginActivity.this, MainActivity.class) );
                        } else {
                            Toast.makeText(NewLoginActivity.this, "Facebook Login Failed!",
                                    Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });

    }

    private void signInWithFirebase(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Complete Anonymous link");
                        } else {
                            task.getException().printStackTrace();
                        }
                    }

                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
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

}
