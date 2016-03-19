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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        setTabs();
    }

    private void setTabs() {
        addTab("Profile", R.drawable.profile, MyProfileActivity.class);
        addTab("Cards", R.drawable.cards, NameCardsActivity.class);

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
