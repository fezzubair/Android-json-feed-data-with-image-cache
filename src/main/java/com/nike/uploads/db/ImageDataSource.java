package com.nike.uploads.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nike.uploads.model.ImageUpload;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fez on 7/28/2014.
 */
public class ImageDataSource extends DataSource<List<ImageUpload>>{

    private static final String LOGTAG = "NIKE";

    SQLiteOpenHelper dbhelper;

    private static final String[] allColumns = {
//            ImageDBOpenHelper.COLUMN_ID,
            ImageDBOpenHelper.COLUMN_TITLE,
            ImageDBOpenHelper.COLUMN_LINK,
            ImageDBOpenHelper.COLUMN_MEDIA,
            ImageDBOpenHelper.COLUMN_DATE_TAKEN,
            ImageDBOpenHelper.COLUMN_DESC,
            ImageDBOpenHelper.COLUMN_PUBLISHED,
            ImageDBOpenHelper.COLUMN_AUTHOR,
            ImageDBOpenHelper.COLUMN_AUTHOR_ID,
            ImageDBOpenHelper.COLUMN_TAGS
    };

    public ImageDataSource(Context context) {
        dbhelper = new ImageDBOpenHelper(context);
    }

    public void open() {
        Log.i(LOGTAG, "Database opened");
        mDatabase = dbhelper.getWritableDatabase();
    }

    public void close() {
        Log.i(LOGTAG, "Database closed");
        dbhelper.close();
    }

    public ImageUpload create(ImageUpload image) {
        if(image == null)
                return null;
        ContentValues values = getContentValues(image);
        long insertid = mDatabase.insert(ImageDBOpenHelper.TABLE_IMAGES, null, values);
        image.setId(insertid);
        return image;
    }

    private ContentValues getContentValues(ImageUpload image) {
        ContentValues values = new ContentValues();
        values.put(ImageDBOpenHelper.COLUMN_TITLE, image.getTitle());
        values.put(ImageDBOpenHelper.COLUMN_LINK, image.getLink());
        values.put(ImageDBOpenHelper.COLUMN_MEDIA, image.getMediaUrl());
        values.put(ImageDBOpenHelper.COLUMN_M_NAME, image.getMediaName());
        values.put(ImageDBOpenHelper.COLUMN_DATE_TAKEN, image.getDateTaken());
        values.put(ImageDBOpenHelper.COLUMN_DESC, image.getDescription());
        values.put(ImageDBOpenHelper.COLUMN_PUBLISHED, image.getPublished());
        values.put(ImageDBOpenHelper.COLUMN_AUTHOR, image.getAuthor());
        values.put(ImageDBOpenHelper.COLUMN_AUTHOR_ID, image.getAuthorId());
        values.put(ImageDBOpenHelper.COLUMN_TAGS, image.getTags());
        return values;
    }

    public Cursor findAllWithCursor(){
        Cursor cursor = mDatabase.query(ImageDBOpenHelper.TABLE_IMAGES, allColumns, null, null, null, null, null);
    //    Cursor cursor = database.rawQuery( "select rowid _id,* from " + ImageDBOpenHelper.TABLE_IMAGES, null);
        if(cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public List<ImageUpload> findAll(){
        List<ImageUpload> images = null;

        Cursor cursor = mDatabase.query(ImageDBOpenHelper.TABLE_IMAGES, allColumns,
                null, null, null, null, null);
        //    Cursor cursor = database.rawQuery( "select rowid _id,* from " + ImageDBOpenHelper.TABLE_IMAGES, null);
        if(cursor != null && cursor.getCount() > 0){
            int COLUMN_TITLE_INDEX = cursor.getColumnIndex(ImageDBOpenHelper.COLUMN_TITLE);
            int COLUMN_LINK_INDEX = cursor.getColumnIndex(ImageDBOpenHelper.COLUMN_LINK);
            int COLUMN_MEDIA_INDEX = cursor.getColumnIndex(ImageDBOpenHelper.COLUMN_MEDIA);
            int COLUMN_DATE_TAKEN_INDEX = cursor.getColumnIndex(ImageDBOpenHelper.COLUMN_DATE_TAKEN);
            int COLUMN_DESC_INDEX = cursor.getColumnIndex(ImageDBOpenHelper.COLUMN_DESC);
            int COLUMN_PUBLISHED_INDEX = cursor.getColumnIndex(ImageDBOpenHelper.COLUMN_PUBLISHED);
            int COLUMN_AUTHOR_INDEX = cursor.getColumnIndex(ImageDBOpenHelper.COLUMN_AUTHOR);
            int COLUMN_AUTHOR_ID_INDEX = cursor.getColumnIndex(ImageDBOpenHelper.COLUMN_AUTHOR_ID);
            int COLUMN_TAGS_INDEX = cursor.getColumnIndex(ImageDBOpenHelper.COLUMN_TAGS);

            images = new ArrayList<ImageUpload>();

            while (cursor.moveToNext()){
                ImageUpload image = new ImageUpload();
                image.setTitle(cursor.getString(COLUMN_TITLE_INDEX));
                image.setLink(cursor.getString(COLUMN_LINK_INDEX));
                image.setMediaUrl(cursor.getString(COLUMN_MEDIA_INDEX));
                image.setDateTaken(cursor.getString(COLUMN_DATE_TAKEN_INDEX));
                image.setDescription(cursor.getString(COLUMN_DESC_INDEX));
                image.setPublished(cursor.getString(COLUMN_PUBLISHED_INDEX));
                image.setAuthor(cursor.getString(COLUMN_AUTHOR_INDEX));
                image.setAuthorId(cursor.getString(COLUMN_AUTHOR_ID_INDEX));
                image.setTags(cursor.getString(COLUMN_TAGS_INDEX));
                images.add(image);
            }
        }

        cursor.close();
        return images;
    }

    @Override
    public boolean insert(List<ImageUpload> entity) {
        return false;
    }

    @Override
    public boolean delete(List<ImageUpload> entity) {
        return false;
    }

    @Override
    public boolean update(List<ImageUpload> entity) {
        return false;
    }

    @Override
    public List read() {
        return null;
    }

    @Override
    public List read(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return null;
    }
}
