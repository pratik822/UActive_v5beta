package com.uactiv.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.uactiv.fragment.SlideImageFragment;
import com.uactiv.fragment.SlideImageFragmentFour;
import com.uactiv.fragment.SlideImageFragmentThree;
import com.uactiv.fragment.SlideImageFragmentTwo;

public class SliderFragmentAdapter extends FragmentPagerAdapter {

    private int ITEMS = 4;
    //  private PagerChangedListener pagerChangedListener = null;
    private SlideImageFragment slideImageFragment = null;
    private SlideImageFragmentTwo slideImageFragmentTwo = null;
    private SlideImageFragmentThree slideImageFragmentThree = null;
    private SlideImageFragmentFour slideImageFragmentFour = null;
    private AnimationPageListener animationPageListener;

    public SliderFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return ITEMS;
    }

    public void onAdapterPageChanged(int position) {
        animationPageListener.onPageChanged(position);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment mTempFrag = null;

        switch (position) {
            case 0: // Fragment # 0 - This will show image
                slideImageFragment = new SlideImageFragment().newInstance(0);
                animationPageListener = (AnimationPageListener) slideImageFragment;
                return slideImageFragment;
            case 1: // Fragment # 1 - This will show image
                slideImageFragmentTwo = new SlideImageFragmentTwo().newInstance(1);
                animationPageListener = (AnimationPageListener) slideImageFragmentTwo;
                return slideImageFragmentTwo;
            case 2:
                slideImageFragmentThree = new SlideImageFragmentThree().newInstance(3);
                animationPageListener = (AnimationPageListener) slideImageFragmentThree;
                return slideImageFragmentThree;
            case 3:
                slideImageFragmentFour = new SlideImageFragmentFour().newInstance(4);
                animationPageListener = (AnimationPageListener) slideImageFragmentFour;
                return slideImageFragmentFour;


            default:// Fragment # 2-9 - Will show list
                return null;
        }
    }

    public interface AnimationPageListener {
        void onPageChanged(int position);
    }

}