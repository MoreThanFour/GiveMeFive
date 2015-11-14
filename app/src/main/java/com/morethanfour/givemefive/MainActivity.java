package com.morethanfour.givemefive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private ListView ListViewFriend;
    private ArrayList<String> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, LocationService.class));

        listItems = new ArrayList<String>();

        Intent intent = getIntent();
        try {
            JSONArray FriendList = new JSONArray(intent.getStringExtra("FriendList"));
            for (int i = 0; i < FriendList.length(); i++) {
                Log.d("JSON", FriendList.getJSONObject(i).toString());
                listItems.add((String) FriendList.getJSONObject(i).get("name") + FriendList.getJSONObject(i).get("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListViewFriend = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        ListViewFriend.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_listview_item, listItems));
    }

}
