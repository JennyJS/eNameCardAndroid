package com.scu.jenny.enamecard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scu.jenny.enamecard.storage.DrawableManager;
import com.scu.jenny.enamecard.storage.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by jenny on 2/21/16.
 */
public class NameViewAdapter extends ArrayAdapter {
    final private List<User> contactsList;

    public NameViewAdapter(Context context, int resource, List<User> contacts){
        super(context, resource, contacts);
        this.contactsList = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final User user = contactsList.get(position);
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.customized_name_card_row, null);
        ImageView profileIV = (ImageView) row.findViewById(R.id.ncProfileIV);
        if (user.imageURL != null) {
            DrawableManager.getInstance().fetchDrawableOnThread(user.imageURL, profileIV);
        }


        TextView textView = (TextView) row.findViewById(R.id.contactName);
        textView.setText(user.firstName + " " + user.lastName);

        TextView phoneNumberView = (TextView) row.findViewById(R.id.phoneNumber);
        phoneNumberView.setText(user.phoneNumber);

        ImageView[] imageViews = new ImageView[] {
                (ImageView) row.findViewById(R.id.fbIV),
                (ImageView) row.findViewById(R.id.twIV)
        };

        for (int i = 0; i < user.socialMedias.size(); i++) {
            final User.SocialMedia socialMedia = user.socialMedias.get(i);
            DrawableManager.getInstance().fetchDrawableOnThread(socialMedia.imageURL, imageViews[i]);
            break;
        }

        return row;
    }
}
