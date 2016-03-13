package com.scu.jenny.enamecard.Profile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
        final AdapterConnector connection = data.get(position);

        View row = null;
        if (convertView != null) {
            row = convertView;
        } else {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.grid_item_layout, null);
        }

        GradientDrawable bgShape = (GradientDrawable)row.getBackground();
        bgShape.setColor(adjustAlpha(Color.parseColor(connection.mediaType.color), 0.7f));

        ImageView socialMediaIV = (ImageView) row.findViewById(R.id.socialMediaIV);
        ImageView linkedIV = (ImageView) row.findViewById(R.id.linkedIV);

        try {
            InputStream inputStream = getContext().getAssets().open(connection.mediaType.iconName);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            socialMediaIV.setImageDrawable(drawable);

            if (connection.url == null) {
                inputStream = getContext().getAssets().open("link_icon_word.png");
                drawable = Drawable.createFromStream(inputStream, null);
                linkedIV.setImageDrawable(drawable);
            } else {
                DrawableManager.getInstance().fetchDrawableOnThread(connection.url, linkedIV);
            }

            linkedIV.setOnClickListener(connection.listener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return row;
    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
