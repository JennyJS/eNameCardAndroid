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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by jenny on 2/21/16.
 */
public class CustomNameCardAdapter extends ArrayAdapter {
    final private List<Contacts> contactsList;

    public CustomNameCardAdapter(Context context, int resource, List<Contacts> contacts){
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

        // using bitmap to solve out of memory issue, however, leaving fileNotFound to fix
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap myBitmap = BitmapFactory.decodeFile(contacts.getIconName(),options);
        Drawable d = new BitmapDrawable(Resources.getSystem(),myBitmap);
        imageView.setImageDrawable(d);
        textView.setText(contacts.getPersonName());

//        try {
//            InputStream inputStream = getContext().getAssets().open(contacts.getIconName());
//            Drawable drawable = Drawable.createFromStream(inputStream, null);
//            imageView.setImageDrawable(drawable);
//
//            inputStream = getContext().getAssets().open(contacts.getIconName());
//            drawable = Drawable.createFromStream(inputStream, null);
//            imageView2.setImageDrawable(drawable);
//
//            textView.setText(contacts.getPersonName());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return row;
    }
}
