package com.scu.jenny.enamecard.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jenny on 2/28/16.
 */
public class KVStore {
    private final SharedPreferences sharedPrefs;

    public static final String USER_PRIMARY_KEY = "userPK";

    private KVStore(Context context) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static KVStore singleton;

    public static void init(Context context) {
        singleton = new KVStore(context);
    }

    public static KVStore getInstance(){return singleton;}

    public void set(String key, String value){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void set(String key, long value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public String get(String key, String dft){
        return sharedPrefs.getString(key, dft);
    }

    public Long get(String key, long dft) { return sharedPrefs.getLong(key, dft);}

    /********* Helper **********/
    public static long getCurrentUserPK() {
        return singleton.get(USER_PRIMARY_KEY, 0);
    }

    public static void setCurrentUserPK(long userPK) {
        singleton.set(USER_PRIMARY_KEY, userPK);
    }
}
