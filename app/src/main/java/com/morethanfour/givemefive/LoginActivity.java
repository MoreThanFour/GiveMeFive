package com.morethanfour.givemefive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;

public class LoginActivity extends Activity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set activity's view
        setContentView(R.layout.activity_login);

        // Setup login button
        loginButton = (Button) findViewById(R.id.loginButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onClickButtonLogin(View v) {
        Collection<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("user_friends");
        permissions.add("email");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
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
                    Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
                    startActivity(intent);
                }
            }
        });
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
}
