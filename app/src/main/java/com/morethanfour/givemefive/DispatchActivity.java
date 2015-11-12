package com.morethanfour.givemefive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseUser;

public class DispatchActivity extends Activity {

    public DispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start an intent for the login activity if the user is not logged in
        // or the main activity if the user il already logged
        if (ParseUser.getCurrentUser() != null){
            ParseUser.logOut();
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}
