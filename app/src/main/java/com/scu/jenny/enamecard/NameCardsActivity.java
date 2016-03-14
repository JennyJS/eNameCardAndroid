package com.scu.jenny.enamecard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class NameCardsActivity extends AppCompatActivity {
    ListView contactsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_cards);

//        myListView = (ListView)findViewById(R.id.list_view);
//        final List<AdapterConnector> connectionList = new ArrayList<>();
//        connectionList.add(new AdapterConnector("icon_qora.png", "icon_add.png"));
//        connectionList.add(new AdapterConnector("icon_twitter.png", "icon_add.png"));
//        connectionList.add(new AdapterConnector("icon_facebook.png", "icon_add.png"));
//        connectionList.add(new AdapterConnector("icon_linkedin.png", "icon_add.png"));
//
//        myListView.setAdapter(new CustomAdapter(this, R.layout.customized_row, connectionList));
        contactsListView = (ListView) findViewById(R.id.myCardsListView);
        List<Contacts> contactsList = new ArrayList<>();
        contactsList.add(new Contacts("Manhong Ren", "icon_twitter.png"));
        contactsList.add(new Contacts("Zhouying Ren", "icon_linkedin.png"));
        contactsList.add(new Contacts("Archer Li", "icon_facebook.png"));
        contactsListView.setAdapter(new CustomNameCardAdapter(this, R.layout.customized_name_card_row, contactsList));
    }
}
