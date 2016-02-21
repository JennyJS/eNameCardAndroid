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
public class CustomAdapter extends ArrayAdapter{
    final private List<Connections> connectionsList;


    public CustomAdapter(Context context, int resource, List<Connections> connectionsList){
        super(context, resource, connectionsList);
        this.connectionsList = connectionsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final Connections connections = connectionsList.get(position);
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(R.layout.customized_row, null);
//
//        holder.textView = (TextView)row.findViewById(R.id.textView);
//        holder.textView.setText(animal.getAnimalName());
        ImageView imageView1 = (ImageView) row.findViewById(R.id.imageView);
        ImageView imageView2 = (ImageView) row.findViewById(R.id.imageView2);

        try {
            InputStream inputStream = getContext().getAssets().open(connections.getIconName());
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView1.setImageDrawable(drawable);

            inputStream = getContext().getAssets().open(connections.getAddIcon());
            drawable = Drawable.createFromStream(inputStream, null);
            imageView2.setImageDrawable(drawable);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return row;
    }
}
