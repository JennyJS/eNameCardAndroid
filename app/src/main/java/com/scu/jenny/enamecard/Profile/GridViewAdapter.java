package com.scu.jenny.enamecard.Profile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

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
    private static final int DISPLAYED = 1;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<AdapterConnector> connectors) {
        super(context, layoutResourceId, connectors);
        this.data = connectors;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AdapterConnector connection = data.get(position);
//        System.out.println(data.size());
        View row = null;
        if (convertView != null) {
            row = convertView;
        } else {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(position == data.size() -1){

                row = inflater.inflate(R.layout.last_grid_view, null);
                GradientDrawable bgShape = (GradientDrawable)row.getBackground();
                bgShape.setColor(adjustAlpha(Color.parseColor(connection.mediaType.color), 0.7f));
                return row;
            }
            row = inflater.inflate(R.layout.grid_item_layout, null);
        }

        GradientDrawable bgShape = (GradientDrawable)row.getBackground();
        bgShape.setColor(adjustAlpha(Color.parseColor(connection.mediaType.color), 0.7f));

        try {
            ImageView socialMediaIV = (ImageView) row.findViewById(R.id.socialMediaIV);

            ViewFlipper viewFlipper = (ViewFlipper) row.findViewById(R.id.grid_view_flipper);

            ImageView unlinkedIV = (ImageView) row.findViewById(R.id.unlinkedIV);
            ImageView linkedIV = (ImageView) row.findViewById(R.id.linkedIV);

            InputStream inputStream = getContext().getAssets().open(connection.mediaType.iconName);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            socialMediaIV.setImageDrawable(drawable);


            if (connection.url == null) {
                // Show unlinked view
                inputStream = getContext().getAssets().open("link_icon_word.png");
                drawable = Drawable.createFromStream(inputStream, null);
                unlinkedIV.setImageDrawable(drawable);
                unlinkedIV.setOnClickListener(connection.listener);

                if (viewFlipper.getDisplayedChild() == 1) {
                    viewFlipper.setInAnimation(MyProfileActivity.context, R.anim.in_from_right);
                    viewFlipper.setOutAnimation(MyProfileActivity.context, R.anim.out_to_left);
                    viewFlipper.showPrevious();
                } else {
                    viewFlipper.setDisplayedChild(0);
                }
            } else {
                // Show linked view
                DrawableManager.getInstance().fetchDrawableOnThread(connection.url, linkedIV);
                linkedIV.setOnClickListener(connection.listener);

                if (viewFlipper.getDisplayedChild() == 0) {
                    viewFlipper.setInAnimation(MyProfileActivity.context, R.anim.in_from_left);
                    viewFlipper.setOutAnimation(MyProfileActivity.context, R.anim.out_to_right);
                    viewFlipper.showNext();
                } else {
                    viewFlipper.setDisplayedChild(1);
                }


            }

            System.out.println("after changing... position:" + position + " fliperViewIndex:" + viewFlipper.getDisplayedChild());
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
