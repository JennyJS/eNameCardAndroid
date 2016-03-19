package com.scu.jenny.enamecard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;
import com.scu.jenny.enamecard.storage.DBHelper;
import com.scu.jenny.enamecard.storage.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NameCardsActivity extends AppCompatActivity {
    ListView contactsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_cards);
        contactsListView = (ListView) findViewById(R.id.myCardsListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }


    private void updateListView() {
        contactsListView.invalidateViews();
        final List<User> contactsList = new ArrayList<>();
        contactsListView.setAdapter(new CustomNameCardAdapter(this, R.layout.customized_name_card_row, contactsList));

        new NetworkAsyncTask(NameCardsActivity.this, "Fetching name cards", new ProcessResponse() {
            @Override
            public void process(String jsonRespose) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonRespose);
                    JSONArray nameCards = jsonObject.getJSONArray("nameCards");
                    List<User> contacts = new ArrayList<>();
                    for (int i = 0; i < nameCards.length(); i++) {
                        User user = User.getUserFromJsonObj(nameCards.getJSONObject(i));
                        DBHelper.getInstance().updateOrCreateUserRecord(user);
                        contacts.add(user);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contactsListView.setAdapter(new CustomNameCardAdapter(NameCardsActivity.this, R.layout.customized_name_card_row, contactsList));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute("GET", "/name-cards");
    }
}
