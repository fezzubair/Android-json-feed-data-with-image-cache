package com.nike.uploads.adapter;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.nike.uploads.MainActivity;
import com.nike.uploads.PagerImageFragment;
import com.nike.uploads.model.DiskLruImageCache;
import com.nike.uploads.model.ImageUpload;
import com.nike.uploads.model.ImageWithPb;
import com.nike.uploads.tasks.ImageLoaderTask;
import com.nike.uploads.tasks.PagerLinearLayout;

import java.util.List;

/**
 * Created by Fez on 8/1/2014.
 */
public class ImagePagerAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private ViewPager pager;
    private MainActivity context;
    private FragmentManager fm;
    private List<ImageUpload> uploadList;
    private int size;

//    private PagerLinearLayout cur = null;
//    private PagerLinearLayout next = null;

    public ImagePagerAdapter(final ViewPager pager, MainActivity context,
                             FragmentManager fm, List<ImageUpload> uploads) {
        super(fm);
        this.pager = pager;
        this.fm = fm;
        this.context = context;
        this.uploadList = uploads;
        this.size = uploads.size();
    }

    @Override
    public Fragment getItem(int position)
    {
        int newPosition = position % size;
        return PagerImageFragment.newInstance(context, newPosition, uploadList.get(newPosition));
    }

    @Override
    public int getCount(){ return size * MainActivity.LOOPS; }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {}

    @Override
    public void onPageScrollStateChanged(int state) {
       //pager.setCurrentItem(0,true);
    }
}
