package com.uactiv.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.uactiv.R;
import com.uactiv.activity.StartUpActivity;
import com.uactiv.adapter.SliderFragmentAdapter;

/**
 * Created by moorthy on 3/22/2016.
 */
public class SlideImageFragment extends Fragment implements SliderFragmentAdapter.AnimationPageListener {

    private String TAG = "SlideImageFragment";

    public static SlideImageFragment newInstance(int page) {
        SlideImageFragment fragmentFirst = new SlideImageFragment();
        Bundle args = new Bundle();
        args.putInt(StartUpActivity.SLIDER_BUNDLE_KEY, page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    ImageView mImageView = null;
    Animation anim = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.frag_pager_one, container, false);
        mImageView = (ImageView) view.findViewById(R.id.img_ground);
       // anim = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_bounce);
      //  mImageView.startAnimation(anim);
        return view;
    }


    @Override
    public void onPageChanged(int position) {
        if (position==0){
            /*Play animation here*/
            Log.d(TAG,"onPageChanged");
            mImageView.startAnimation(anim);
        }
    }
}
