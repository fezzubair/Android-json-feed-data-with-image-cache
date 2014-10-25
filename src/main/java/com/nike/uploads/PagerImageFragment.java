package com.nike.uploads;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nike.uploads.model.DiskLruImageCache;
import com.nike.uploads.model.ImageUpload;
import com.nike.uploads.model.ImageWithPb;
import com.nike.uploads.tasks.ImageLoaderTask;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PagerImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PagerImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PagerImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POS = "pos";
    private static final String ARG_IMAGE = "image";

    // TODO: Rename and change types of parameters
    private int position;
    private ImageUpload image;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PagerImageFragment.
     */
    public static Fragment newInstance(MainActivity context, int pos, ImageUpload image)
    {
        Bundle b = new Bundle();
        b.putInt(ARG_POS, pos);
        b.putParcelable(ARG_IMAGE, image);
        return Fragment.instantiate(context, PagerImageFragment.class.getName(), b);
    }
    public PagerImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POS);
            image = getArguments().getParcelable(ARG_IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        }

        View layout = inflater.inflate(R.layout.fragment_pager_image, container, false);

        TextView tv = (TextView) layout.findViewById(R.id.text);
        tv.setText(image.getTitle());

        ImageView iv = (ImageView) layout.findViewById(R.id.imageView);

        ProgressBar pb = (ProgressBar) layout.findViewById(R.id.progressBar);

        int length = image.getMediaUrl().length();
        DiskLruImageCache mDiskLruCache = DiskLruImageCache.getInstance(DiskLruImageCache.getContext());
        Bitmap alreadyBitmap = mDiskLruCache.getBitmap(image.getMediaUrl().substring(length-16).replace(".",""));
        if(alreadyBitmap != null) {
            iv.setImageBitmap(alreadyBitmap);
            pb.setVisibility(View.GONE);
        }
        else {
            pb.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);
            ImageWithPb image_with_pb = new ImageWithPb();
            image_with_pb.setImage(iv);
            image_with_pb.setPb(pb);
            image_with_pb.setImageUpload(image);
            // Launch a task to download the image from the JSON returned media URL
            new ImageLoaderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image_with_pb);
        }

        return layout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
