package com.nike.uploads.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nike.uploads.R;
import com.nike.uploads.db.ImageDBOpenHelper;
import com.nike.uploads.model.DiskLruImageCache;
import com.nike.uploads.model.ImageUpload;
import com.nike.uploads.model.ImageWithPb;
import com.nike.uploads.tasks.ImageLoaderTask;

/**
 * Created by Fez on 8/1/2014.
 */
public class ImageCursorAdapter extends CursorAdapter {
    private Context context;
    LayoutInflater inflater;
    private final Cursor imageCursor;

    private DiskLruImageCache mDiskLruCache;

    private int COLUMN_ID_INDEX = 0;
    private int COLUMN_TITLE_INDEX = 0;
    public static int COLUMN_LINK_INDEX = 0;
    public int COLUMN_MEDIA_INDEX = 0;
    public int COLUMN_MEDIA_NAME_INDEX = 0;
    public int COLUMN_DATE_TAKEN_INDEX = 0;
    public int COLUMN_DESC_INDEX = 0;
    public int COLUMN_PUBLISHED_INDEX = 0;
    public int COLUMN_AUTHOR_INDEX = 0;
    public int COLUMN_AUTHOR_ID_INDEX = 0;
    public int COLUMN_TAGS_INDEX = 0;

    public ImageCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
        this.imageCursor = c;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        COLUMN_ID_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_ID);
        COLUMN_TITLE_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_TITLE);
        COLUMN_LINK_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_LINK);
        COLUMN_MEDIA_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_MEDIA);
        COLUMN_MEDIA_NAME_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_M_NAME);
        COLUMN_DATE_TAKEN_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_DATE_TAKEN);
        COLUMN_DESC_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_DESC);
        COLUMN_PUBLISHED_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_PUBLISHED);
        COLUMN_AUTHOR_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_AUTHOR);
        COLUMN_AUTHOR_ID_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_AUTHOR_ID);
        COLUMN_TAGS_INDEX = c.getColumnIndex(ImageDBOpenHelper.COLUMN_TAGS);

/*        final int maxMemory = (int) Runtime.getRuntime().maxMemory() / 1024;
    final int cacheSize = maxMemory / 8;*/
        mDiskLruCache = DiskLruImageCache.getInstance(DiskLruImageCache.getContext());
    }

    static class ViewHolder {
        protected TextView text;
        protected ImageView image;
        protected ProgressBar pb;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if(view.getTag() == null) {
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view
                    .findViewById(R.id.grid_item_label);

            viewHolder.image = (ImageView) view
                    .findViewById(R.id.grid_item_image);

            viewHolder.pb = (ProgressBar) view
                    .findViewById(R.id.progressBar);

            view.setTag(viewHolder);
        }

        ViewHolder newHolder = (ViewHolder) view.getTag();

        newHolder.text.setText(cursor.getString(COLUMN_TITLE_INDEX));
        Bitmap alreadyBitmap = mDiskLruCache.getBitmap(String.valueOf(cursor.getLong(COLUMN_ID_INDEX)));
        if(alreadyBitmap != null) {
            newHolder.image.setImageBitmap(alreadyBitmap);
            newHolder.pb.setVisibility(View.GONE);
        }
        else {
            newHolder.pb.setVisibility(View.VISIBLE);
            newHolder.image.setVisibility(View.GONE);
            ImageWithPb image_with_pb = new ImageWithPb();
            image_with_pb.setImage(newHolder.image);
            image_with_pb.setPb(newHolder.pb);
            ImageUpload imageUpload = getImageFromCursor(cursor);
            image_with_pb.setImageUpload(imageUpload);
            // Launch a task to download the image from the JSON returned media URL
            new ImageLoaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image_with_pb);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.adapter_image, parent, false);
    }

    public ImageUpload getImageFromCursor(Cursor cursor){
        ImageUpload image = new ImageUpload();
        image.setId(cursor.getLong(COLUMN_ID_INDEX));
        image.setTitle(cursor.getString(COLUMN_TITLE_INDEX));
        image.setLink(cursor.getString(COLUMN_LINK_INDEX));
        image.setMediaUrl(cursor.getString(COLUMN_MEDIA_INDEX));
        image.setMediaName(cursor.getString(COLUMN_MEDIA_NAME_INDEX));
        image.setDateTaken(cursor.getString(COLUMN_DATE_TAKEN_INDEX));
        image.setDescription(cursor.getString(COLUMN_DESC_INDEX));
        image.setPublished(cursor.getString(COLUMN_PUBLISHED_INDEX));
        image.setAuthor(cursor.getString(COLUMN_AUTHOR_INDEX));
        image.setAuthorId(cursor.getString(COLUMN_AUTHOR_ID_INDEX));
        image.setTags(cursor.getString(COLUMN_TAGS_INDEX));
        return image;
    }
}
