package com.scu.jenny.enamecard;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabWidget;

public class MainPageActivity extends TabActivity  {

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // create the TabHost that will contain the Tabs
        final TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third Tab");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("Fourth Tab");


        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab2.setIndicator("Cards");
        tab2.setContent(new Intent(this, NameCardsActivity.class));

        tab1.setIndicator("Profile");
        tab1.setContent(new Intent(this, MyProfileActivity.class));



        tab3.setIndicator("Scan");
        tab3.setContent(new Intent(this, ScanActivity.class));

        tab4.setIndicator("QR Code");
        tab4.setContent(new Intent(this, QRActivity.class));



        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);

        this.tabHost = tabHost;

        updateTabHost();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                updateTabHost();
            }
        });
    }

    private void updateTabHost() {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.brightText);
        }
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.color.brightBG);
    }
}
