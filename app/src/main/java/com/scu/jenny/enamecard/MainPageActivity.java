package com.scu.jenny.enamecard;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends TabActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Third Tab");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("Fourth Tab");


        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("My Profile");
        tab1.setContent(new Intent(this,MyProfile.class));

        tab2.setIndicator("Cards");
        tab2.setContent(new Intent(this,MyProfile.class));

        tab3.setIndicator("Scan");
        tab3.setContent(new Intent(this,MyProfile.class));

        tab4.setIndicator("My QR code");
        tab4.setContent(new Intent(this,MyProfile.class));



        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);

    }
}
