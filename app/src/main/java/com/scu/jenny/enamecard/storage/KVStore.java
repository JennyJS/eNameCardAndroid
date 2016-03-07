package com.scu.jenny.enamecard.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jenny on 2/28/16.
 */
public class KVStore {
    private final Context context;
    private final SharedPreferences sharedPrefs;

    private KVStore(Context context) {
        this.context = context;
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

    public String get(String key, String dft){
        return sharedPrefs.getString(key, dft);
    }
}
