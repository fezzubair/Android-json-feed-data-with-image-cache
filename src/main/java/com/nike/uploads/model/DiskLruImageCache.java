package com.nike.uploads.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.nike.uploads.BuildConfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Fez on 7/31/2014.
 */
public class DiskLruImageCache {

    private static DiskLruImageCache myObj;

    private static Context context;
    private DiskLruCache mDiskCache;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.PNG;
    private int mCompressQuality = 100;

    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "nikefeedimages";
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final String LOGTAG = "NIKE";

    private DiskLruImageCache(Context c) {
        try {
            context = c;
            final File diskCacheDir = getDiskCacheDir(context, DISK_CACHE_SUBDIR);
            mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DiskLruImageCache getInstance(Context context){
        if(myObj == null){
            myObj = new DiskLruImageCache(context);
        }
        return myObj;
    }

    public static Context getContext(){
        return context;
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
            throws IOException, FileNotFoundException {
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), Utils.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
// Check if media is mounted or storage is built-in, if so, try and use external cache dir
// otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Utils.isExternalStorageRemovable() ?
                        Utils.getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public void put(String key, Bitmap data) {
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit(key);
            if (editor == null) {
                return;
            }

            if (writeBitmapToFile(data, editor)) {
                mDiskCache.flush();
                editor.commit();
                Log.i(LOGTAG, "image put on disk cache " + key);
            } else {
                editor.abort();
                Log.i(LOGTAG, "ERROR on: image put on disk cache " + key);
            }
        } catch (IOException e) {
                Log.i(LOGTAG, "ERROR on: image put on disk cache " + key);
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public Bitmap getBitmap(String key) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn =
                        new BufferedInputStream(in, Utils.IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        Log.i(LOGTAG, bitmap == null ? "" : "image read from disk " + key);

        return bitmap;
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        synchronized (mDiskCacheLock) {
            if (myObj != null && myObj.getBitmap(key) == null) {
                myObj.put(key, bitmap);
            }
        }
    }

    public void clearCache() {
        Log.i(LOGTAG, "disk cache CLEARED");
        try {
            mDiskCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
