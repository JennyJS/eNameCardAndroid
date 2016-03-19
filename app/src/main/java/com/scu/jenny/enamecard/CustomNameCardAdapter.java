package com.scu.jenny.enamecard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
public class CustomNameCardAdapter extends ArrayAdapter {
    final private List<User> contactsList;

    public CustomNameCardAdapter(Context context, int resource, List<User> contacts){
        super(context, resource, contacts);
        this.contactsList = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final User contact = contactsList.get(position);
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.customized_name_card_row, null);


        TextView textView = (TextView) row.findViewById(R.id.contactName);
        ImageView imageView = (ImageView) row.findViewById(R.id.contactMethod);

        DrawableManager.getInstance().fetchDrawableOnThread(contact.imageURL, imageView);
        textView.setText(contact.firstName + " " + contact.lastName);
        return row;
    }
}
