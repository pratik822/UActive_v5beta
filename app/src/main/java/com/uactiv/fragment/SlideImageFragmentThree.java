package com.uactiv.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uactiv.R;
import com.uactiv.activity.StartUpActivity;
import com.uactiv.adapter.SliderFragmentAdapter;

/**
 * Created by moorthy on 3/22/2016.
 */
public class SlideImageFragmentThree extends Fragment implements SliderFragmentAdapter.AnimationPageListener{


    public static SlideImageFragmentThree newInstance(int page) {
        SlideImageFragmentThree fragmentFirst = new SlideImageFragmentThree();
        Bundle args = new Bundle();
        args.putInt(StartUpActivity.SLIDER_BUNDLE_KEY, page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.frag_pager_four, container, false);
      /*  if (getArguments() != null) {
            position = getArguments().getInt(MainActivity.SLIDER_BUNDLE_KEY);
        }*/
        return mView;
    }

    @Override
    public void onPageChanged(int position) {

    }
}
