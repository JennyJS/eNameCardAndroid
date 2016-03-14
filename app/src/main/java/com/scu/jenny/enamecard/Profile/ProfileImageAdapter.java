package com.scu.jenny.enamecard.Profile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scu.jenny.enamecard.Contacts;
import com.scu.jenny.enamecard.R;
import com.scu.jenny.enamecard.storage.DrawableManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by manhong on 3/13/16.
 */
public class ProfileImageAdapter extends ArrayAdapter {
    final private List<String> imageURLs;

    public ProfileImageAdapter(Context context, int resource, List<String> imageURLs){
        super(context, resource, imageURLs);
        this.imageURLs = imageURLs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.profile_image_layout, null);

        CircleImageView profileIV = (CircleImageView) row.findViewById(R.id.optionalProfileIV);

        DrawableManager.getInstance().fetchDrawableOnThread(imageURLs.get(position), profileIV);
        return row;
    }
}
