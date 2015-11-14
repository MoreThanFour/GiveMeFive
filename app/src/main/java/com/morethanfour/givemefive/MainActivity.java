package com.morethanfour.givemefive;

<<<<<<< HEAD
=======
import android.app.AlertDialog;
import android.content.DialogInterface;
>>>>>>> origin/master
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
<<<<<<< HEAD
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
=======
>>>>>>> origin/master

public class MainActivity extends AppCompatActivity {

    private ListView ListViewFriend;
    private ArrayList<String> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        listItems = new ArrayList<String>();
=======
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        startService(new Intent(this, LocationService.class));
    }
>>>>>>> origin/master

        Intent intent = getIntent();
        try {
            JSONArray FriendList = new JSONArray(intent.getStringExtra("FriendList"));
            for (int i = 0; i < FriendList.length(); i++) {
                listItems.add((String) FriendList.getJSONObject(i).get("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListViewFriend = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        ListViewFriend.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, listItems));

    }

}
