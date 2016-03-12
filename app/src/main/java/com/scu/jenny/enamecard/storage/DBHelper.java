package com.scu.jenny.enamecard.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
    public static final String PRIMARY_ID = "id";

    //Basic Table-column names
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";

    public static final String[] USER_COLUMS = {
            PHONE_NUMBER, FIRST_NAME, LAST_NAME
    };

    //FB Table-column names
    public static final String FB_ID = "fbId";
    public static final String PHOTO_PATH = "photoPath";
    public static final String USER_PK_ID = "basicTableId";

    public static final String[] FB_COLUMS = {
            USER_PK_ID, FB_ID, PHOTO_PATH
    };

    //Create tables
    private static final String CREATE_TABLE_BASIC = "CREATE TABLE IF NOT EXISTS " + TABLE_BASIC
            + "(" + PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PHONE_NUMBER + " TEXT,"
            + FIRST_NAME + " TEXT," + LAST_NAME + ")";

    private static final String CREATE_TABLE_FB = "CREATE TABLE IF NOT EXISTS " + TABLE_FB + "(" +
            PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FB_ID + " TEXT," +
            PHOTO_PATH + " TEXT," +
            USER_PK_ID + " INTEGER," + " FOREIGN KEY ("+ USER_PK_ID +") REFERENCES "+TABLE_BASIC+"("+ PRIMARY_ID +"));";

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
        if (dbHelper == null) {
            throw new IllegalStateException("DB hasn't been init");
        }
        return dbHelper;
    }

    /****************** Util ****************/
//
//    public User findUserByPhoneNumber() {
//
//    }

    public User getUserByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BASIC, // a. table
                USER_COLUMS, // b. column names
                " " + PHONE_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(phoneNumber)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        User user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2));
        db.close();
        return user;
    }

    public Facebook getFBByUserID(long userPK) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FB,
                FB_COLUMS,
                " " + USER_PK_ID + "= ?",
                new String[]{String.valueOf(userPK)},
                null,
                null,
                null,
                null
                );
        if (cursor.getCount() == 0) {
            System.out.println("Not getting FB " + userPK);
            return null;
        }
        System.out.println("Getting FB");
        cursor.moveToFirst();
        return new Facebook(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
    }

    public long updateOrCreateUserRecord(User user) {
        // Find if user record exists first
        User old = getUserByPhoneNumber(user.phoneNumber);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PHONE_NUMBER, user.phoneNumber);
        values.put(FIRST_NAME, user.firstName );
        values.put(LAST_NAME, user.lastName);
        long row_id;

        if (old == null) {
            row_id = db.insert(TABLE_BASIC, null, values);
        } else {
            row_id = db.update(TABLE_BASIC, values, null, null);
        }

        KVStore.setCurrentUserPK(row_id);
        db.close();
        return row_id;
    }

    public void createFBRecord(Facebook fb) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_PK_ID, fb.userID);
        values.put(FB_ID, fb.fbId);
        values.put(PHOTO_PATH, fb.imageURL);
        db.insert(TABLE_FB, null, values);
        return;
    }

    public void deleteFBRecordByUserID(long userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FB, USER_PK_ID + "= ?", new String[]{String.valueOf(userID)});
    }
}
