package com.scu.jenny.enamecard;

import android.view.View;

/**
 * Created by jenny on 2/21/16.
 */
public class Connections {
    private String iconName;
    private String url;
    private View.OnClickListener listener;
    public Connections(String iconName, String url, View.OnClickListener listener){
        this.iconName = iconName;
        this.url = url;
        this.listener = listener;
    }
    public String getIconName() {
        return iconName;
    }

    public String getUrl(){
        return url;
    }

    public View.OnClickListener getListener(){
        return listener;
    }

}
