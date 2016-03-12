package com.scu.jenny.enamecard.Profile;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scu.jenny.enamecard.AdapterConnector;
import com.scu.jenny.enamecard.R;
import com.scu.jenny.enamecard.storage.DrawableManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by manhong on 3/12/16.
 */
public class GridViewAdapter extends ArrayAdapter {
    private final ArrayList<AdapterConnector> data;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<AdapterConnector> connectors) {
        super(context, layoutResourceId, connectors);
        this.data = connectors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView != null) {
            row = convertView;
        } else {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.grid_item_layout, null);
        }
        final AdapterConnector connection = data.get(position);
        ImageView socialMediaIV = (ImageView) row.findViewById(R.id.socialMediaIV);
        ImageView linkedIV = (ImageView) row.findViewById(R.id.linkedIV);

        try {
            InputStream inputStream = getContext().getAssets().open(connection.getIconName());
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            socialMediaIV.setImageDrawable(drawable);

            if (connection.getUrl() == null) {
                inputStream = getContext().getAssets().open("icon_add.png");
                drawable = Drawable.createFromStream(inputStream, null);
                linkedIV.setImageDrawable(drawable);
            } else {
                DrawableManager.getInstance().fetchDrawableOnThread(connection.getUrl(), linkedIV);
            }

            linkedIV.setOnClickListener(connection.getListener());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return row;
    }
}
