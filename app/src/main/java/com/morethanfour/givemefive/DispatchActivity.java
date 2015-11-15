package com.morethanfour.givemefive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class DispatchActivity extends Activity {

    public DispatchActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start an intent for the login activity if the user is not logged in
        // or the main activity if the user il already logged
        if (AccessToken.getCurrentAccessToken() != null) {
            requestUserFriends(AccessToken.getCurrentAccessToken());
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void requestUserFriends(AccessToken currentAccessToken) {
        new GraphRequest(
                currentAccessToken,
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() == null){
                            startMainActivity(response.getJSONObject());
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't access user's friend list from FB.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        ).executeAsync();
    }

    private void startMainActivity(JSONObject data) {
        try {
            Intent toBackgroundService = new Intent(this, BackgroundService.class);
            toBackgroundService.putExtra("FriendList", data.getJSONArray("data").toString());
            startService(toBackgroundService);

            Intent toMainActivity = new Intent(DispatchActivity.this, MainActivity.class);
            toMainActivity.putExtra("friendList", data.getJSONArray("data").toString());
            startActivity(toMainActivity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
