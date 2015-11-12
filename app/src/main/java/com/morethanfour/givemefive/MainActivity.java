package com.morethanfour.givemefive;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class MainActivity extends AppCompatActivity {

    TextView loginName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Parse App
        Parse.initialize(this, "YaCzPCUVo20yXIbc4ZeKCh6leX2g7nuDRZyW32VC", "pFUZc2aWVUMvkjfnVJM1lH9bkPLLXQdgCuUfPPyE");

        // Initialize Facebook App
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        // Sync Facebook with Parse
        ParseFacebookUtils.initialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);

        CallbackManager callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginName = (TextView) findViewById(R.id.textview_login);
                        loginName.setText(Profile.getCurrentProfile().getFirstName());
                        Log.d("LOGIN", "Succefully logged with token:" + loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("LOGIN ERROR", exception.toString());
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AccessToken.getCurrentAccessToken() == null){
            Log.d("LOGIN", "Not logged with Facebook");


        } else {

            LoginManager.getInstance().logOut();
            Log.d("LOGIN", "Disconnect");
        }

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
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
