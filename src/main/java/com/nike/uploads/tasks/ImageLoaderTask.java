package com.nike.uploads.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.view.View;

import com.nike.uploads.model.DiskLruImageCache;
import com.nike.uploads.model.ImageWithPb;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Fez on 7/31/2014.
 */
public class ImageLoaderTask extends AsyncTask<ImageWithPb, Void, ImageWithPb> {

    protected ImageWithPb doInBackground(ImageWithPb... images_with_pb) {
        ImageWithPb container = images_with_pb[0];
        Bitmap bitmap = getBitmapDownloaded(container.getImageUpload().getMediaUrl());

        if(bitmap != null) {
            container.setBitmap(bitmap);
            return container;
        }

        return null;
    }

    protected void onPostExecute(ImageWithPb result) {
        if(result == null)
            return;

        result.getImage().setImageBitmap(result.getBitmap()); //set the bitmap to the imageview.
        result.getPb().setVisibility(View.GONE);  // hide the progressbar after downloading the image.
        result.getImage().setVisibility(View.VISIBLE); // display the imageview now with the image

        // add bitmap to disk cache for future use
        int length = result.getImageUpload().getMediaUrl().length();
        DiskLruImageCache mDiskLruCache = DiskLruImageCache.getInstance(DiskLruImageCache.getContext());
        mDiskLruCache.addBitmapToCache(result.getImageUpload().getMediaUrl().substring(length-16).replace(".","")
                ,result.getBitmap());
    }

    /** This function downloads the image and returns the Bitmap **/
    private Bitmap getBitmapDownloaded(String url) {
        Bitmap bitmap = null;
        try {
            InputStream in = (InputStream) new URL(url).getContent();
            bitmap = BitmapFactory.decodeStream(in);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, 350, 350);
            in.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
