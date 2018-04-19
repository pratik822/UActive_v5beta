package com.uactiv.fragment;

import android.content.Intent;
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
public class SlideImageFragmentFour extends Fragment implements SliderFragmentAdapter.AnimationPageListener {
 Intent intent;

    public static SlideImageFragmentFour newInstance(int page) {
        SlideImageFragmentFour fragmentFirst = new SlideImageFragmentFour();
        Bundle args = new Bundle();
        args.putInt(StartUpActivity.SLIDER_BUNDLE_KEY, page);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.frag_pager_three, container, false);
        return mView;
    }

    @Override
    public void onPageChanged(int position) {
           }
}
