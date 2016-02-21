package com.scu.jenny.enamecard;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.TabActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends TabActivity implements TabHost.TabContentFactory {
    List<Fragment> tabFragmentList = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("My profile").setIndicator("eBay").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("cards").setIndicator("Flickr").setContent(this));
  ;

//        setupUI();

    }


    @Override
    public View createTabContent(String tag) {
        //
        return null;
    }
}
