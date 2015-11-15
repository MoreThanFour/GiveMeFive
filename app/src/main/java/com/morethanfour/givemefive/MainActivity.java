package com.morethanfour.givemefive;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.facebook.AccessToken;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity implements OnMapReadyCallback {

    private ListView ListViewFriend;
    DrawerLayout drawer;
    private ArrayList<String> listItems;
    GoogleMap map;
    HashMap<String, MarkerOptions> friendsOnMap;
    boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        friendsOnMap = new HashMap<>();
        ListViewFriend = (ListView) findViewById(R.id.left_drawer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        listItems = new ArrayList<String>();

        JSONArray friendList = null;
        try {
            friendList = new JSONArray(getIntent().getStringExtra("friendList"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        displayFriendList(friendList);
        addFriendsToMap(friendList);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // Enabling MyLocation Layer of Google Map
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMyLocationChangeListener(myLocationChangeListener);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(new Criteria(), true);

        // Getting Current Location
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(myPosition).title("Start"));

            Log.d("MAPS", "INFOS" + myPosition.latitude + myPosition.longitude);
        }else {
            location = map.getMyLocation();
            if (location != null) {
                LatLng myLocation = new LatLng(location.getLatitude(),  location.getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));
            }
        }
    }

    private void makeUseOfNewLocation(Location location) {
        LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
        map.addMarker(new MarkerOptions()
                .position(mylocation)
                .title("C'est MOI :)"));

        //Center the MAP on me
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 13));
    }

    private void makeUseOfNewLocation(LatLng location) {
        map.addMarker(new MarkerOptions()
                .position(location)
                .title("MOI :)"));

        //Center the MAP on me
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
    }

    private void displayFriendList(JSONArray FriendList) {
        try {
            for (int i = 0; i < FriendList.length(); i++) {
                Log.d("JSON", FriendList.getJSONObject(i).toString());
                listItems.add((String) FriendList.getJSONObject(i).get("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Set the adapter for the list view
        ListViewFriend.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_listview_item, listItems));
    }

    private void addFriendsToMap(JSONArray friendList) {

        List<ParseQuery<ParseUser>> friendsQuery = new ArrayList<ParseQuery<ParseUser>>();

        for (int i = 0; i < friendList.length(); i++) {
            ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
            try {
                friendQuery.whereEqualTo("facebookId", (String) friendList.getJSONObject(i).get("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            friendsQuery.add(friendQuery);
        }

        if (!friendsQuery.isEmpty()) {
            ParseQuery.or(friendsQuery).findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> users, ParseException e) {
                    if (e == null) {
                        for (ParseUser user : users) {
                            ParseGeoPoint location = (ParseGeoPoint) user.get("location");
                            if (location != null)
                                addFriendToMap((String) user.get("facebookId"), ParseUser.getCurrentUser().getUsername(),new LatLng(location.getLatitude(), location.getLongitude()));
                        }
                    } else {
                        // Something went wrong.
                    }
                }
            });
        }
    }

    private void addFriendToMap(String id, String name, LatLng latLng) {
//        Bitmap profPict= null;
//        try {
//            URL image_value = new URL("https://graph.facebook.com/"+id+"/picture" );
//            profPict = BitmapFactory.decodeStream(image_value.openConnection().getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        MarkerOptions mo = new MarkerOptions()
                .position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user));


        friendsOnMap.put(id,mo);
        map.addMarker(mo);
    }

    public void onClickFloatingButtonOpenDrawer(View v) {
        drawer.openDrawer(Gravity.RIGHT);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            if(map != null && first){
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14.0f));
                first = false;
            }
        }
    };
}
