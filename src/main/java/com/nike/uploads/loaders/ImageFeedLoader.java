package com.nike.uploads.loaders;

import android.content.Context;
import android.database.ContentObserver;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.nike.uploads.HttpManager;
import com.nike.uploads.db.ImageDataSource;
import com.nike.uploads.model.ImageUpload;
import com.nike.uploads.parsers.ImageJSONParser;

import java.util.List;

/**
 * Created by Fez on 8/7/2014.
 */
public class ImageFeedLoader extends AsyncTaskLoader<List<ImageUpload>> {

    private static final String LOGTAG = "NIKE";

    // We hold a reference to the Loader’s data here.
    private List<ImageUpload> mData;
    private ImageDataSource mDataSource;

    public ImageFeedLoader(Context context, ImageDataSource dataSource) {
        super(context);
        mDataSource = dataSource;
    }

    @Override
    public List<ImageUpload> loadInBackground() {
        Log.i(LOGTAG, "loadInBackground()");
        List<ImageUpload> data;
        String content = HttpManager.getDataUrlConnection(
                "http://api.flickr.com/services/feeds/photos_public.gne?tags=nike&format=json");
        if(content == null)
            return null;
        data = ImageJSONParser.parseFeed(content);
        return data;
    }

    @Override
    public void deliverResult(List<ImageUpload> data) {
        Log.i(LOGTAG, "deliverResult()");
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<ImageUpload> oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    /*********************************************************/
    /** (3) Implement the Loader’s state-dependent behavior **/
    /*********************************************************/

    @Override
    protected void onStartLoading() {
        Log.i(LOGTAG, "onStartLoading()");
        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

/*        // Begin monitoring the underlying data source.
        if (mObserver == null) {
            mObserver = new SampleObserver();
            // TODO: register the observer
        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }*/
    }

    @Override
    protected void onStopLoading() {
        Log.i(LOGTAG, "onStopLoading()");
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        Log.i(LOGTAG, "onReset()");
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
        if (mObserver != null) {
            // TODO: unregister the observer
            mObserver = null;
        }
    }

    @Override
    public void onCanceled(List<ImageUpload> data) {
        Log.i(LOGTAG, "onCanceled()");
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(List<ImageUpload> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    /*********************************************************************/
    /** (4) Observer which receives notifications when the data changes **/
    /*********************************************************************/

    // NOTE: Implementing an observer is outside the scope of this post (this example
    // uses a made-up "SampleObserver" to illustrate when/where the observer should
    // be initialized).

    // The observer could be anything so long as it is able to detect content changes
    // and report them to the loader with a call to onContentChanged(). For example,
    // if you were writing a Loader which loads a list of all installed applications
    // on the device, the observer could be a BroadcastReceiver that listens for the
    // ACTION_PACKAGE_ADDED intent, and calls onContentChanged() on the particular
    // Loader whenever the receiver detects that a new application has been installed.
    // Please don’t hesitate to leave a comment if you still find this confusing! :)
    private ContentObserver mObserver;

/*    public void insert(ImageUpload entity) {
        new InsertTask(this).execute(entity);
    }

    public void update(ImageUpload entity) {
        new UpdateTask(this).execute(entity);
    }

    public void delete(ImageUpload entity) {
        new DeleteTask(this).execute(entity);
    }

    private class InsertTask extends ContentChangingTask<ImageUpload, Void, Void> {
        InsertTask(ImageFeedLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(ImageUpload... params) {
            mDataSource.insert(params[0]);
            return (null);
        }
    }

    private class UpdateTask extends ContentChangingTask<ImageUpload, Void, Void> {
        UpdateTask(ImageFeedLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(ImageUpload... params) {
            mDataSource.update(params[0]);
            return (null);
        }
    }

    private class DeleteTask extends ContentChangingTask<ImageUpload, Void, Void> {
        DeleteTask(ImageFeedLoader loader) {
            super(loader);
        }

        @Override
        protected Void doInBackground(ImageUpload... params) {
            mDataSource.delete(params[0]);
            return (null);
        }
    }*/
}
