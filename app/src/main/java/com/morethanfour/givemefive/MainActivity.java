package com.morethanfour.givemefive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;


public class MainActivity extends Activity implements OnMapReadyCallback {

    private ListView ListViewFriend;
    DrawerLayout drawer;
    private ArrayList<String> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, LocationService.class));

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ListViewFriend = (ListView) findViewById(R.id.left_drawer);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        listItems = new ArrayList<String>();

        displayFriendList(getIntent());
    }

    @Override
    public void onMapReady(GoogleMap map) {


    }

    private void displayFriendList(Intent intent) {
        try {
            JSONArray FriendList = new JSONArray(intent.getStringExtra("FriendList"));
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

    public void onClickFloatingButtonOpenDrawer(View v){
        drawer.openDrawer(Gravity.RIGHT);
    }

}
