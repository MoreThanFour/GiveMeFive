package com.morethanfour.givemefive;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;

public class Application extends android.app.Application {

    // Debugging tag for the application
    public static final String APPTAG = "GiveMeFive";

    // Used to pass location from MainActivity to PostActivity
    public static final String INTENT_EXTRA_LOCATION = "location";

    // Key for saving the search distance preference
    private static final String KEY_SEARCH_DISTANCE = "searchDistance";

    private static final float DEFAULT_SEARCH_DISTANCE = 250.0f;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Parse App
        Parse.initialize(this, "YaCzPCUVo20yXIbc4ZeKCh6leX2g7nuDRZyW32VC", "pFUZc2aWVUMvkjfnVJM1lH9bkPLLXQdgCuUfPPyE");

        // Sync Facebook with Parse
        ParseFacebookUtils.initialize(getApplicationContext());

        // Initialize Facebook App
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Save installation into Parse Database for Push Notification.
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
