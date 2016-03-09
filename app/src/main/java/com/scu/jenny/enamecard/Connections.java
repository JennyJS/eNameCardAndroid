package com.scu.jenny.enamecard;

import android.view.View;

/**
 * Created by jenny on 2/21/16.
 */
public class Connections {
    private String iconName;
    private String addIcon;
    private View.OnClickListener listener;
    public Connections(String iconName, String addIcon, View.OnClickListener listener){
        this.iconName = iconName;
        this.addIcon = addIcon;
        this.listener = listener;
    }
    public String getIconName() {
        return iconName;
    }

    public String getAddIcon(){
        return addIcon;
    }

    public View.OnClickListener getListener(){
        return listener;
    }

}
