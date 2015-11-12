package com.morethanfour.givemefive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;

public class MainActivity extends Activity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("WIA", "ON CREATE");

        // Initialize Parse App
        Parse.initialize(this, "YaCzPCUVo20yXIbc4ZeKCh6leX2g7nuDRZyW32VC", "pFUZc2aWVUMvkjfnVJM1lH9bkPLLXQdgCuUfPPyE");

        // Initialize Facebook App
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        // Sync Facebook with Parse
        ParseFacebookUtils.initialize(this.getApplicationContext());

        if (AccessToken.getCurrentAccessToken() != null) {
            Log.d("TEST","TEST");
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        }
        // Set activity's view
        setContentView(R.layout.activity_main);

        // Setup login button
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collection<String> permissions = new ArrayList<String>();
                permissions.add("public_profile");
                permissions.add("user_friends");
                permissions.add("email");

                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("LOGIN", "Uh oh. The user cancelled the Facebook login.");
                        } else {
                            if (user.isNew()) {
                                Log.d("LOGIN", "User signed up and logged in through Facebook!");
                            } else {
                                Log.d("LOGIN", "User logged in through Facebook!");
                            }
                            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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
