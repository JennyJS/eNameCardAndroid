package com.scu.jenny.enamecard.storage;

import android.provider.BaseColumns;

/**
 * Created by jenny on 3/9/16.
 */
public class DB {
    public DB(){}
    public static abstract class DBEntry implements BaseColumns{
        public static String TABLE_NAME = "entry";

    }
}
