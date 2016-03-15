package com.scu.jenny.enamecard;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.scu.jenny.enamecard.Profile.MyProfileActivity;

public class MainPageActivity extends TabActivity  {
    public static final String userInfoKey = "USER_INFO";
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        setTabs();
//        JSONObject userObj = null;
//        try {
//            userObj = new JSONObject(getIntent().getExtras().getString(userInfoKey));
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Error getting user info", Toast.LENGTH_SHORT).show();
//            return;
//        }
    }



//        // create the TabHost that will contain the Tabs
//        final TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
//
//
//        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
////        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
//        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third Tab");
//        TabHost.TabSpec tab4 = tabHost.newTabSpec("Fourth Tab");
//
//
//        // Set the Tab name and Activity
//        // that will be opened when particular Tab will be selected
////        tab2.setIndicator("Cards");
////        tab2.setContent(new Intent(this, NameCardsActivity.class));
//
//
//        tab1.setIndicator("Profile");
//        tab1.setContent(new Intent(this, MyProfileActivity.class));
//
//
//
//        tab3.setIndicator("Scan");
//        tab3.setContent(new Intent(this, ScanActivity.class));
//
//        tab4.setIndicator("QR Code");
//        tab4.setContent(new Intent(this, QRActivity.class));
//
//
//
//        /** Add the tabs  to the TabHost to display. */
//        tabHost.addTab(tab1);
////        tabHost.addTab(tab2);
//        tabHost.addTab(tabHost.newTabSpec("Second Tab").setIndicator("Cards", getResources().getDrawable(R.drawable.button_cards)).setContent(new Intent(this, NameCardsActivity.class)));
//        tabHost.addTab(tab3);
//        tabHost.addTab(tab4);
//
//        this.tabHost = tabHost;
//
//        updateTabHost();
//
//        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
//            @Override
//            public void onTabChanged(String tabId) {
//                updateTabHost();
//            }
//        });
//    }
//
//    private void updateTabHost() {
//        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
//            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.brightText);
//        }
//        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.color.brightBG);
//    }

    private void setTabs() {
        addTab("Profile", R.drawable.profile, MyProfileActivity.class);
        addTab("Cards", R.drawable.cards, Contacts.class);

        addTab("Scan", R.drawable.scan, ScanActivity.class);
        addTab("QR Code", R.drawable.qr, QRActivity.class);
    }

    private void addTab(String labelId, int drawableId, Class<?> c) {
        TabHost tabHost = getTabHost();
        Intent intent = new Intent(this, c);
        TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(labelId);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawableId);

        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
    }

}
