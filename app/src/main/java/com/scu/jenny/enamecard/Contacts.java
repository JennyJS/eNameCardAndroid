package com.scu.jenny.enamecard;

import java.util.List;

/**
 * Created by jenny on 2/21/16.
 */
public class Contacts {
    private String personName;
    private String iconName;
    public Contacts(String personName, String iconName){
        this.personName = personName;
        this.iconName = iconName;
    }
    public String getPersonName() {
        return personName;
    }

    public String getIconName(){
        return iconName;
    }
}
