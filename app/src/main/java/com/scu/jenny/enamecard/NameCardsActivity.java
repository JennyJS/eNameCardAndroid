package com.scu.jenny.enamecard;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.scu.jenny.enamecard.Profile.AdapterConnector;
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
    SwipeMenuListView contactsListView;
    final List<User> contacts = new ArrayList<>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_cards);
        contactsListView = (SwipeMenuListView) findViewById(R.id.myCardsListView);
        adapter= new NameViewAdapter(NameCardsActivity.this, R.layout.customized_name_card_row, contacts);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(70));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(70));
                // set a icon
                deleteItem.setIcon(R.drawable.tw__ic_dialog_close);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        contactsListView.setMenuCreator(creator);

        contactsListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 1:
                    {
                        User userToRemove = contacts.get(position);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("phoneNumber", userToRemove.phoneNumber);
                            new NetworkAsyncTask(NameCardsActivity.this, "Deleting nameCard...", new ProcessResponse() {
                                @Override
                                public void process(String jsonRespose) {
                                    try {
                                        JSONObject jsonRes = new JSONObject(jsonRespose);
                                        if (jsonRes.has("status") && jsonRes.getString("status").equals("success")) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    contacts.remove(position);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(NameCardsActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(NameCardsActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }).execute("DELETE", "/name-cards", jsonObject.toString());
                            break;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            }
        });

        contactsListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        contactsListView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
            }

            @Override
            public void onMenuClose(int position) {
            }
        });

        contactsListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListView();
    }


    private void updateListView() {
        new NetworkAsyncTask(NameCardsActivity.this, "Fetching name cards", new ProcessResponse() {
            @Override
            public void process(String jsonRespose) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonRespose);
                    JSONArray nameCards = jsonObject.getJSONArray("nameCards");
                    for (int i = 0; i < nameCards.length(); i++) {
                        User user = User.getUserFromJsonObj(nameCards.getJSONObject(i));
                        DBHelper.getInstance().updateOrCreateUserRecord(user);
                        contacts.add(user);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            contactsListView.setAdapter(adapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute("GET", "/name-cards");
    }
}
