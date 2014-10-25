package com.nike.uploads;

import android.app.ActionBar;
import android.app.Dialog;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nike.uploads.adapter.ImageAdapter;
import com.nike.uploads.adapter.ImagePagerAdapter;
import com.nike.uploads.db.ImageDataSource;
import com.nike.uploads.dialog.EditNameDialog;
import com.nike.uploads.loaders.ImageFeedLoader;
import com.nike.uploads.model.DiskLruImageCache;
import com.nike.uploads.model.ImageUpload;
import com.nike.uploads.parsers.ImageJSONParser;

import java.util.List;


public class MainActivity extends FragmentActivity implements
        PagerImageFragment.OnFragmentInteractionListener, LoaderManager.LoaderCallbacks<List<ImageUpload>>,
        EditNameDialog.EditNameDialogListener {

    private static final String LOGTAG = "NIKE";

    ProgressBar pb;
    TextView output;
    GridView gridview;
    boolean insertIntoDB = true;
    boolean landscape = false;

    ViewPager pager;

    List<ImageUpload> uploadList;

    ImageDataSource dataSource;

    private DiskLruImageCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB

    public int PAGES = 0;
    public final static int LOOPS = 1000;
    public final int FIRST_PAGE = PAGES * LOOPS / 2;
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    private static final int THE_LOADER = 0x01;
    private static final int THE_DATABASE_LOADER = 0x02;

    private static final String feedUrl =
            "http://api.flickr.com/services/feeds/photos_public.gne?tags=nike&format=json";

    // constant for identifying the dialog
    private static final int DIALOG_ALERT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // GridView or FragmentViewPager
        setUpLayout();
        // Initialize disk cache on background thread
        new InitDiskCacheTask().execute();
//        this.deleteDatabase("images.db");
        // initialize datasource
        dataSource = new ImageDataSource(this);
        dataSource.open();

        requestData(feedUrl);
    }

    private void setUpLayout() {
        //check if to display Grid or Viewpager
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            pager = (ViewPager) findViewById(R.id.myviewpager);
            landscape = true;
        }
        else
            gridview = (GridView) findViewById(R.id.gridView1);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
        output = (TextView) findViewById(R.id.textView);
    }

    /********************************* LOADER FUNCTIONS **********************/


    @Override
    public Loader<List<ImageUpload>> onCreateLoader(int id, Bundle args) {
        Log.i(LOGTAG, "onCreateLoader()");
        String mDataUrl = null, mProjection = null;

        switch (id) {
            case THE_LOADER:
                return new ImageFeedLoader(this, dataSource);
            case THE_DATABASE_LOADER:
                // Returns a new CursorLoader
/*                return new CursorLoader(
                        this,   // Parent activity context
                        mDataUrl,        // Table to query
                        mProjection,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );*/
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<ImageUpload>> loader, List<ImageUpload> data) {
        Log.i(LOGTAG, "onLoadFinished()");
        if (data == null) {
            Toast.makeText(MainActivity.this, "Can't connect to web service", Toast.LENGTH_LONG).show();
            return;
        }
        uploadList = data;
        updateDisplay();
    }

    @Override
    public void onLoaderReset(Loader<List<ImageUpload>> loader) {
        Log.i(LOGTAG, "onLoaderReset()");
        gridview.setAdapter(null);
    }

    /*********************************** LOADER FUNCTIONS END **********************/

    class InitDiskCacheTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            synchronized (mDiskCacheLock) {
                mDiskLruCache = DiskLruImageCache.getInstance(MainActivity.this);
                mDiskCacheStarting = false; // Finished initialization
                mDiskCacheLock.notifyAll(); // Wake any waiting threads
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            // action with ID action_dialogue was selected
            case R.id.action_dialogue:
                Toast.makeText(this, "Dialogue selected", Toast.LENGTH_SHORT)
                        .show();
                showEditDialog();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // just a practice to make a dialog
    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EditNameDialog editNameDialog = new EditNameDialog();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", "Sad Title :(");
        args.putString("message", "Please use me, I am sad");
        editNameDialog.setArguments(args);

        editNameDialog.show(fm, "fragment_editname_dialog");
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        if(inputText.equals("OK"))
            Toast.makeText(this, ":) " + inputText, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, ":( " + inputText, Toast.LENGTH_SHORT).show();
    }

    private void requestData(String uri) {
        if (isOnline()) {
            MyTask task = new MyTask();
            task.execute(uri);
/*            Bundle data = new Bundle();
            data.putString("URI", uri);
//            getLoaderManager().initLoader(THE_DATABASE_LOADER, null, this).forceLoad();
            getSupportLoaderManager().initLoader(THE_LOADER, data, this).forceLoad();
            */
        } else {
            Toast.makeText(this, "Network Not available", Toast.LENGTH_LONG).show();
        }
    }

    protected void updateDisplay() {
        output.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);

        if(landscape)
        {
            PAGES = uploadList.size();
            ImagePagerAdapter adapter = new ImagePagerAdapter(pager, this, getSupportFragmentManager(), uploadList);
            pager.setAdapter(adapter);
            pager.setOnPageChangeListener(adapter);
            pager.setCurrentItem(FIRST_PAGE);
            pager.setOffscreenPageLimit(3);
            pager.setPageMargin(-200);
        }
        else {
            ImageAdapter adapter = new ImageAdapter(this, uploadList);
            gridview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    // checks if network connectivity is available or not
    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true; // we are ready to make a network request
        } else {
            return false;
        }
    }

/*    private class CursorAndString{
        Cursor cursor;
        String response;
    }*/

    private class MyTask extends AsyncTask<String, List<ImageUpload>, List<ImageUpload>>{

        @Override
        protected List<ImageUpload> doInBackground(String... params) {

//          Cursor cursor = dataSource.findAllWithCursor();
            uploadList = dataSource.findAll();
            if(uploadList != null) // no need to make network call
            {
                Log.i(LOGTAG, "----------------///////////////// No need for NETWORK CALL");
                insertIntoDB = false;
                return uploadList;
            }
            // else make a network call to get fresh feed data
            String content = HttpManager.getDataUrlConnection(params[0]);
            if(content == null || content.length() == 0)
                return null;

            uploadList = ImageJSONParser.parseFeed(content);
            if(uploadList == null)
                return null;

            return uploadList;
        }

        @Override
        protected void onPostExecute(List<ImageUpload> result) {
            if (result == null) {
                Toast.makeText(MainActivity.this, "Can't connect to web service", Toast.LENGTH_LONG).show();
                return;
            }
            updateDisplay();
            if(insertIntoDB)
                insertToDb();
        }
    }

    private void insertToDb() {
        int size = uploadList.size();
        for (int i=0; i<size; i++)
        {
            ImageUpload image = dataSource.create(uploadList.get(i));
            uploadList.get(i).setId(image.getId());
        }
    }

    public void onFragmentInteraction(Uri uri){};

    public int getPAGES() {
        return PAGES;
    }

    public void setPAGES(int PAGES) {
        this.PAGES = PAGES;
    }

}
