package com.nike.uploads.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Fez on 7/28/2014.
 */
public class ImageDBOpenHelper extends SQLiteOpenHelper {

    private static final String LOGTAG = "NIKE";

    private static final String DATABASE_NAME = "images.db";
    private static final int DATABASE_VERSION = 10;

    public static final String TABLE_IMAGES = "images";

//    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_MEDIA = "media";
    public static final String COLUMN_M_NAME = "m_name";
    public static final String COLUMN_DATE_TAKEN = "date_taken";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_PUBLISHED = "published";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_AUTHOR_ID = "author_id";
    public static final String COLUMN_TAGS = "tags";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_IMAGES + " (" +
//                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_LINK + " TEXT, " +
                    COLUMN_MEDIA + " TEXT, " +
                    COLUMN_M_NAME + " TEXT, " +
                    COLUMN_DATE_TAKEN + " TEXT, " +
                    COLUMN_DESC + " TEXT, " +
                    COLUMN_PUBLISHED + " TEXT, " +
                    COLUMN_AUTHOR + " TEXT, " +
                    COLUMN_AUTHOR_ID + " TEXT, " +
                    COLUMN_TAGS + " TEXT " +
                    ")";


    public ImageDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.i(LOGTAG, "Table has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
        Log.i(LOGTAG, "Table has been dropped");
    }

}
