package com.scu.jenny.enamecard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.scu.jenny.enamecard.storage.DrawableManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by jenny on 2/21/16.
 */
public class CustomAdapter extends ArrayAdapter{
    final private List<Connections> connectionsList;


    public CustomAdapter(Context context, int resource, List<Connections> connectionsList){
        super(context, resource, connectionsList);
        this.connectionsList = connectionsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final Connections connection = connectionsList.get(position);
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.customized_row, null);

        ImageView imageView1 = (ImageView) row.findViewById(R.id.social_media_view);
        ImageView imageView2 = (ImageView) row.findViewById(R.id.imageView2);

        try {
            InputStream inputStream = getContext().getAssets().open(connection.getIconName());
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView1.setImageDrawable(drawable);

            if (connection.getUrl() == null) {
                inputStream = getContext().getAssets().open("icon_add.png");
                drawable = Drawable.createFromStream(inputStream, null);
                imageView2.setImageDrawable(drawable);
            } else {
                DrawableManager.getInstance().fetchDrawableOnThread(connection.getUrl(), imageView2);
            }

            imageView2.setOnClickListener(connection.getListener());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return row;
    }
}
