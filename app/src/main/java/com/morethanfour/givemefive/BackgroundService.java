package com.morethanfour.givemefive;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends Service
{
    // ----- CONSTANTS -----

    private static final String TAG = "BACKGROUND_SERVICE";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int TIME_OF_SLEEP = 1000 * 10;

    // ----- VARIABLES -----
    private ArrayList<String> friendsId;
    private ArrayList<Boolean> friendsIsNear;

    private Boolean isRunning;
    private Location currentLocation;

    // ----- CONSTRUCTORS -----

    public BackgroundService()
    {
        super();
        Log.i(TAG, "constructor");
    }

    // ----- OVERRIDE METHODS -----
    // Service methods

    @Override
    public void onCreate()
    {
        Log.i(TAG, "onCreate");

        super.onCreate();

        isRunning = true;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.i(TAG, "onLocationChanged");
                Log.i(TAG, "New location: 'lat' = " + location.getLatitude() + ", 'long' = " + location.getLongitude());

                if (isBetterLocation(location, currentLocation))
                {
                    currentLocation = location;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {
                Log.i(TAG, "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider)
            {
                Log.i(TAG, "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider)
            {
                Log.i(TAG, "onProviderDisabled");
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 10, locationListener);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand");

        // Initialize friends
        initFriends(intent);

        //Creating new thread for background service
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (isRunning)
                {
                    try
                    {
                        Thread.sleep(TIME_OF_SLEEP);
                        Log.d(TAG, "Time to restart");
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    ParseUser currentUser = ParseUser.getCurrentUser();

                    if (currentUser != null)
                    {
                        // Update current user location into Parse Database.
                        ParseGeoPoint currentParseLocation = new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                        currentUser.put("location", currentParseLocation);
                        currentUser.saveEventually();

                        //
                        List<ParseQuery<ParseUser>> friendsQuery = new ArrayList<ParseQuery<ParseUser>>();

                        for (int i = 0; i < friendsId.size(); i++)
                        {
                            ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
                            friendQuery.whereEqualTo("facebookId", friendsId.get(i));

                            friendsQuery.add(friendQuery);
                        }

                        ParseQuery.or(friendsQuery).findInBackground(new FindCallback<ParseUser>()
                        {
                            public void done(List<ParseUser> objects, ParseException e)
                            {
                                if (e == null)
                                {
                                    // The query was successful.

                                    for (int i = 0; i < objects.size(); i++)
                                    {
                                        Log.d(TAG, objects.get(i).get("location").toString());
                                    }

                                    // TODO: Look for each user if anyone is into the perimeter. For each one, call the Cloud Code to notify myself
                                }
                                else
                                {
                                    // Something went wrong.
                                    Log.e(TAG, "ERROR " + e.getCode() + ": " + e.getLocalizedMessage());
                                }
                            }
                        });
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.i(TAG, "onBind");

        return null;
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "onDestroy");

        isRunning = false;
    }


    // ----- INSTANCE METHODS -----

    private void initFriends(Intent intent)
    {
        Log.d(TAG, "Getting friend list");

        friendsId = new ArrayList<String>();
        friendsIsNear = new ArrayList<Boolean>();

        try
        {
            JSONArray friendList = new JSONArray(intent.getStringExtra("friendList"));

            for (int i = 0; i < friendList.length(); i++)
            {
                Log.d(TAG, "'facebookId' = " + friendList.getJSONObject(i).toString());

                friendsId.add((String) friendList.getJSONObject(i).get("id"));
                friendsIsNear.add(false);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }
    /**
     * Determines whether one Location reading is better than the current Location fix
     * @param location The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     * @return
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation)
    {
        if (currentBestLocation == null)
        {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer)
        {
            return true;

        }
        // If the new location is more than two minutes older, it must be worse
        else if (isSignificantlyOlder)
        {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate)
        {
            return true;
        }
        else if (isNewer && !isLessAccurate)
        {
            return true;
        }
        else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
        {
            return true;
        }

        return false;
    }

    /**
     * Checks whether two providers are the same
     * @param provider1
     * @param provider2
     * @return
     */
    private boolean isSameProvider(String provider1, String provider2)
    {
        if (provider1 == null)
        {
            return provider2 == null;
        }

        return provider1.equals(provider2);
    }
}
