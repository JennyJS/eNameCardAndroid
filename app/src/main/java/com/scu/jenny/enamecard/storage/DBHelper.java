package com.scu.jenny.enamecard.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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
    private static final String DATABASE_NAME = "eNameCard";

    //Table Names
    private static final String TABLE_USER = "user";
    private static final String TABLE_SOCIAL_MEDIA = "socialMedia";

    //Common colum names
    public static final String PRIMARY_ID = "id";

    //Basic Table-column names
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PROFILE_IMAGE_URL = "imageURL";

    public static final String[] USER_COLUMS = {
            PRIMARY_ID, PHONE_NUMBER, FIRST_NAME, LAST_NAME, PROFILE_IMAGE_URL
    };

    //Social Media Table-column names
    public static final String USER_PK_ID = "userId";
    public static final String MEDIA_TYPE = "mediaType";
    public static final String MEDIA_RECORD_ID = "mediaRecordId";
    public static final String IMAGE_URL = "imageURL";


    public static final String[] SOCIAL_MEDIA_COLUMS = {
            USER_PK_ID, MEDIA_TYPE, MEDIA_RECORD_ID, IMAGE_URL
    };

    //Create tables
    private static final String CREATE_TABLE_BASIC = "CREATE TABLE IF NOT EXISTS " + TABLE_USER
            + "(" + PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PHONE_NUMBER + " TEXT,"
            + FIRST_NAME + " TEXT," +
            LAST_NAME + " TEXT," +
            PROFILE_IMAGE_URL + " TEXT" +
            ")";

    private static final String CREATE_TABLE_SOCIAL_MEDIA = "CREATE TABLE IF NOT EXISTS " + TABLE_SOCIAL_MEDIA + "(" +
            PRIMARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MEDIA_TYPE + " TEXT," +
            MEDIA_RECORD_ID + " TEXT," +
            IMAGE_URL + " TEXT," +
            USER_PK_ID + " INTEGER," + " FOREIGN KEY ("+ USER_PK_ID +") REFERENCES "+ TABLE_USER +"("+ PRIMARY_ID +"));";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BASIC);
        db.execSQL(CREATE_TABLE_SOCIAL_MEDIA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOCIAL_MEDIA);
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

        Cursor cursor = db.query(TABLE_USER, // a. table
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

        final long id = cursor.getLong(0);
        final String firstName = cursor.getString(1);
        final String lastName = cursor.getString(2);
        phoneNumber = cursor.getColumnName(3);
        final String imageURL = cursor.getColumnName(4);

        List<User.SocialMedia> socialMedias = getSocialMediasByUserID(id);

        db.close();
        return new User(firstName, lastName, phoneNumber, imageURL, socialMedias);
    }

    public List<User.SocialMedia> getSocialMediasByUserID(long userPK) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SOCIAL_MEDIA,
                SOCIAL_MEDIA_COLUMS,
                " " + USER_PK_ID + "= ?",
                new String[]{String.valueOf(userPK)},
                null,
                null,
                null,
                null
                );
        if (cursor.getCount() == 0) {
            System.out.println("Not getting soical media" + userPK);
            return null;
        }
        System.out.println("Getting FB");
        cursor.moveToFirst();
        List<User.SocialMedia> socialMedias = new ArrayList<>();
        while(!cursor.isAfterLast()){
            Long userId = cursor.getLong(0);
            String mediaType = cursor.getString(1);
            String mediaRecordId = cursor.getString(2);
            String imageURL = cursor.getString(3);
            User.SocialMedia socialMedia = new User.SocialMedia(mediaType, mediaRecordId, imageURL);
            socialMedias.add(socialMedia);
            cursor.moveToNext();
        }
        return socialMedias;
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
            row_id = db.insert(TABLE_USER, null, values);
        } else {
            row_id = db.update(TABLE_USER, values, null, null);
        }

        // store social media
        for (User.SocialMedia socialMedia : user.socialMedias) {
            values = new ContentValues();
            values.put(USER_PK_ID, row_id);
            values.put(MEDIA_TYPE, socialMedia.mediaType);
            values.put(MEDIA_RECORD_ID, socialMedia.mediaRecordId);
            values.put(IMAGE_URL, socialMedia.imageURL);
            db.insert(TABLE_SOCIAL_MEDIA, null, values);
        }

        KVStore.setCurrentUserPK(row_id);
        CurrentUser.refreshFromDB(user);
        db.close();
        return row_id;
    }

    public void createFBRecord(User.SocialMedia socialMedia, long userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_PK_ID, userID);
        values.put(MEDIA_TYPE, socialMedia.mediaType);
        values.put(MEDIA_RECORD_ID, socialMedia.mediaRecordId);
        values.put(IMAGE_URL, socialMedia.imageURL);
        db.insert(TABLE_SOCIAL_MEDIA, null, values);
        return;
    }

    public void deleteFBRecordByUserID(long userID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SOCIAL_MEDIA, USER_PK_ID + "= ?", new String[]{String.valueOf(userID)});
    }
}
