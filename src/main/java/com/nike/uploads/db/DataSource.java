package com.nike.uploads.db;

/**
 * Created by Fez on 8/9/2014.
 */
import java.util.List;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
public abstract class DataSource<T>{
    protected SQLiteDatabase mDatabase;
    public DataSource() {
    }
    public abstract boolean insert(T entity);
    public abstract boolean delete(T entity);
    public abstract boolean update(T entity);
    public abstract List read();
    public abstract List read(String selection, String[] selectionArgs,
                              String groupBy, String having, String orderBy);
}
