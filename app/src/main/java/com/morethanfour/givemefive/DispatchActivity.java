package com.morethanfour.givemefive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class DispatchActivity extends Activity {

    public DispatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start an intent for the login activity if the user is not logged in
        // or the main activity if the user il already logged

        if (ParseUser.getCurrentUser() != null){
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            //ParseUser.logOut();
                            JSONObject data = response.getJSONObject();

                            try {
                                Intent toMainActivity = new Intent(DispatchActivity.this, MainActivity.class);
                                toMainActivity.putExtra("FriendList", data.getJSONArray("data").toString());
                                startActivity(toMainActivity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        }
        else{
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
