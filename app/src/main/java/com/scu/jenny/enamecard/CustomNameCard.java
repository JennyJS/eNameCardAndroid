package com.scu.jenny.enamecard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by jenny on 2/21/16.
 */
public class CustomNameCard extends ArrayAdapter {
    final private List<Contacts> contactsList;

    public CustomNameCard(Context context, int resource, List<Contacts> contacts){
        super(context, resource, contacts);
        this.contactsList = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final Contacts contacts = contactsList.get(position);
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.customized_name_card_row, null);


        TextView textView = (TextView) row.findViewById(R.id.contactName);
        ImageView imageView = (ImageView) row.findViewById(R.id.contactMethod);
        ImageView imageView2 = (ImageView) row.findViewById(R.id.contactMethod2);

        try {
            InputStream inputStream = getContext().getAssets().open(contacts.getIconName());
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);

            inputStream = getContext().getAssets().open(contacts.getIconName());
            drawable = Drawable.createFromStream(inputStream, null);
            imageView2.setImageDrawable(drawable);

            textView.setText(contacts.getPersonName());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return row;
    }
}
