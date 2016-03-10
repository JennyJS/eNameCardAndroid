package com.scu.jenny.enamecard.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jenny on 3/8/16.
 */
public class DBHelper extends SQLiteOpenHelper{

    private static DBHelper dbHelper;

    // Logcat tag
    private static final String LOG = DBHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "userInfo";

    //Table Names
    private static final String TABLE_BASIC = "basicInfo";
    private static final String TABLE_FB = "fbInfo";

    //Common colum names
    public static final String KEY_USER_ID = "id";

    //Basic Table-column names
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";

    //FB Table-column names
    public static final String FB_ID = "fbId";
    public static final String PHOTO_PATH = "photoPath";
    public static final String USER_ID = "basicTableId";

    //Create tables
    private static final String CREATE_TABLE_BASIC = "CREATE TABLE " + TABLE_BASIC
            + "(" + KEY_USER_ID + " INTEGER PRIMARY KEY," + PHONE_NUMBER + " TEXT,"
            + FIRST_NAME + " TEXT," + LAST_NAME + ")";

    private static final String CREATE_TABLE_FB = "CREATE TABLE " + TABLE_BASIC
            + "(" + KEY_USER_ID + " INTEGER PRIMARY KEY," + FB_ID + " TEXT,"
            + PHOTO_PATH + USER_ID + " INTEGER" + " FOREIGN KEY ("+USER_ID+") REFERENCES "+TABLE_BASIC+"("+KEY_USER_ID+"));";



    public DBHelper(Context context) {
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

    public static void init(Context context) {
        dbHelper = new DBHelper(context);
    }

    public static DBHelper getInstance(){
        return dbHelper;
    }

    /****************** Util ****************/
//
//    public User findUserByPhoneNumber() {
//
//    }

    public long createUserRecord(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PHONE_NUMBER, user.phoneNumber);
        values.put(FIRST_NAME, user.firstName );
        values.put(LAST_NAME, user.lastName);

        // insert row
        long row_id = db.insert(TABLE_BASIC, null, values);

        return row_id;
    }

    public void createFBRecord(Facebook fb) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(FB_ID, fb.fbId);
        values.put(PHOTO_PATH, fb.imageURL);
        db.insert(TABLE_FB, null, values);
        return;
    }
}
