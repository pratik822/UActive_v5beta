package com.uactiv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.uactiv.R;
import com.uactiv.adapter.SliderFragmentAdapter;
import com.uactiv.gcm.RegistrationIntentService;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.widgets.CircleIndicator;
import com.uactiv.widgets.CustomTextView;

public class StartUpActivity extends AppCompatActivity implements AppConstants.SharedConstants, View.OnClickListener,AppConstants.urlConstants {

    public static final String SLIDER_BUNDLE_KEY = "pagerPosition";
    private String TAG = "MainActivity";
    private SliderFragmentAdapter mSliderFragmentAdapter = null;
    private int pagePosition = 0;

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
           // Log.d(TAG, "onPageScrolled");
        }

        @Override
        public void onPageSelected(int position) {
           // Log.d(TAG, "onPageSelected");
            pagePosition = position;
            // mSliderFragmentAdapter.onAdapterPageChanged(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
           // Log.d(TAG, "onPageScrollStateChanged");
        }
    };
    private ViewPager mViewPager = null;
    private CircleIndicator mCircleIndicator = null;
    private CustomTextView mSkip = null;
    private ImageView mArrowMark = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        Log.d(TAG, "onCreate");
        mViewPager = (ViewPager) findViewById(R.id.slider_pager);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.indicator_custom);
        mSkip = (CustomTextView) findViewById(R.id.tutorial_skip);
        mArrowMark = (ImageView) findViewById(R.id.img_arrow);
        mSkip.setOnClickListener(this);
        mArrowMark.setOnClickListener(this);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("page",state+"");
            /*    if (state==0 && pagePosition==3){
                    Intent intent = new Intent(StartUpActivity.this, Sign_In.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_in_left);
                }*/

            }
        });

         /*   mViewPager.setOnTouchListener(new OnSwipeTouchListener(StartUpActivity.this){

                @Override
                public void onSwipeLeft() {
                    super.onSwipeLeft();
                    if(pagePosition==3){

                        Intent intent = new Intent(StartUpActivity.this, Sign_In.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_in_left);
                    }


                }
            });*/



      /*  mViewPager.setOnTouchListener(new OnSwipeTouchListener(StartUpActivity.this){

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();

                   Intent intent = new Intent(StartUpActivity.this, Sign_In.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_in_left);

            }
        });*/

        Log.d("TAG","isStartUpExpired"+ SharedPref.getInstance().getBooleanValue(StartUpActivity.this, isStartUpExpired));

        if (SharedPref.getInstance().getBooleanValue(StartUpActivity.this, islogin)) {
            Intent intent = new Intent(StartUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (SharedPref.getInstance().getBooleanValue(StartUpActivity.this, isStartUpExpired)) {
            Intent intent = new Intent(StartUpActivity.this, Sign_In.class);
            startActivity(intent);
            finish();
        }/*else {
            Intent intent = new Intent(StartUpActivity.this, Sign_In.class);
            startActivity(intent);
            finish();
        }*/
        setViewPager();




    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, getResources().getInteger(R.integer.playservicesresolutionrequest))
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void setViewPager() {
        mSliderFragmentAdapter = new SliderFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSliderFragmentAdapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);
        mCircleIndicator.setViewPager(mViewPager);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {

            case R.id.tutorial_skip:
                intent = new Intent(StartUpActivity.this, Sign_In.class);
                startActivity(intent);
                finish();
                break;
            case R.id.img_arrow:
                if (pagePosition == 3) {
                    intent = new Intent(StartUpActivity.this, Sign_In.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_in_left);
                } else {
                    mViewPager.setCurrentItem(pagePosition + 1);
                }
                break;
            default:
                break;
        }
    }


}
