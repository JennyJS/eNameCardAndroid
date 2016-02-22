package com.scu.jenny.enamecard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MyProfile extends AppCompatActivity {
    private ListView myListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        myListView = (ListView)findViewById(R.id.list_view);
        final List<Connections> connectionList = new ArrayList<>();
        connectionList.add(new Connections("icon_phone_call.jpg", "icon_add.jpg"));
        connectionList.add(new Connections("icon_facebook.jpg", "icon_add.jpg"));

        myListView.setAdapter(new CustomAdapter(this, R.layout.customized_row, connectionList));

    }

}
