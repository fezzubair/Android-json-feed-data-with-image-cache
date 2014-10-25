package com.nike.uploads.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.disklrucache.DiskLruCache;
import com.nike.uploads.R;
import com.nike.uploads.model.DiskLruImageCache;
import com.nike.uploads.model.ImageUpload;
import com.nike.uploads.model.ImageWithPb;
import com.nike.uploads.tasks.ImageLoaderTask;

import java.util.List;

/**
 * Created by Fez on 7/30/2014.
 */
public class ImageAdapter extends BaseAdapter{
    private Context context;
    private final List<ImageUpload> imageValues;

    private DiskLruImageCache mDiskLruCache;

    public ImageAdapter(Context c, List<ImageUpload> values){
        this.context = c;
        this.imageValues = values;
        this.mDiskLruCache = DiskLruImageCache.getInstance(DiskLruImageCache.getContext());
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView = null;

        if (convertView == null) {

            gridView = new View(context);

            gridView = inflater.inflate(R.layout.adapter_image, null);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) gridView
                    .findViewById(R.id.grid_item_label);

            viewHolder.image = (ImageView) gridView
                    .findViewById(R.id.grid_item_image);

            viewHolder.pb = (ProgressBar) gridView
                    .findViewById(R.id.progressBar);

            gridView.setTag(viewHolder);

        } else {
            gridView = convertView;
        }

        ViewHolder newHolder = (ViewHolder) gridView.getTag();
        newHolder.text.setText(imageValues.get(position).getTitle());

        int length = imageValues.get(position).getMediaUrl().length();
        Bitmap alreadyBitmap = mDiskLruCache.getBitmap(imageValues.get(position)
                .getMediaUrl().substring(length-16).replace(".",""));
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
            image_with_pb.setImageUpload(imageValues.get(position));
            // Launch a task to download the image from the JSON returned media URL
            new ImageLoaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image_with_pb);
        }

        return gridView;
    }


    @Override
    public int getCount() {
        return imageValues.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        protected TextView text;
        protected ImageView image;
        protected ProgressBar pb;
    }
}
