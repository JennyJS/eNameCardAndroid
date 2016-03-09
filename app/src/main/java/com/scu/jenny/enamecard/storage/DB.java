package com.scu.jenny.enamecard.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by jenny on 3/8/16.
 */
public class DB extends SQLiteOpenHelper{
    // Logcat tag
    private static final String LOG = DB.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "userInfo";

    //Table Names
    private static final String TABLE_BASIC = "basicInfo";
    private static final String TABLE_FB = "fbInfo";

    //Common colum names
    private static final String KEY_USER_ID = "id";

    //Basic Table-column names
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    //FB Table-column names
    private static final String FB_ID = "fbId";
    private static final String PHOTO_PATH = "photoPath";

    //Create tables
    private static final String CREATE_TABLE_BASIC = "CREATE TABLE " + TABLE_BASIC
            + "(" + KEY_USER_ID + " INTEGER PRIMARY KEY," + PHONE_NUMBER + " TEXT,"
            + FIRST_NAME + " TEXT," + LAST_NAME + ")";

    private static final String CREATE_TABLE_FB = "CREATE TABLE " + TABLE_BASIC
            + "(" + KEY_USER_ID + " INTEGER PRIMARY KEY," + FB_ID + " TEXT,"
            + PHOTO_PATH + ")";



    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BASIC);
        db.execSQL(CREATE_TABLE_FB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BASIC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FB);

    }
}
