package com.morethanfour.givemefive;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView ListViewFriend;
    private ArrayList<String> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listItems = new ArrayList<String>();

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
