package com.uactiv.application;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.L;
import com.uactiv.R;
import com.uactiv.utils.UActivePref;
import com.uactiv.views.CircularImageViews;

import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;


public class UActiveApplication extends Application {

    public static Context mContext;

    private static SharedPreferences mSharedPreferences;
    private static UActiveApplication instance;
    private Tracker mTracker;

    public static UActiveApplication getInstance() {
        return instance;
    }

    public static ImageLoader imageloader;

    public static SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    DisplayImageOptions optionsWithPlaceHolder;
    DisplayImageOptions optionsForBannerImage;
    private String TEST_FAIRY_APP_TOKEN = "5aebca7f76c6367e6a3c48fe4bfd4e32a18116df";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = getApplicationContext();
        initSingletons();
        AppEventsLogger.activateApp(this);
        initSharedPreferences();
        instance = this;
        Branch.getAutoInstance(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .imageDownloader(new BaseImageDownloader(this, 1000, 2000))
                .build();

        imageloader = ImageLoader.getInstance();
        imageloader.init(config);
        L.disableLogging();
        optionsWithPlaceHolder = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_profile)
                .showImageOnLoading(R.drawable.ic_profile)
                .showImageOnFail(R.drawable.ic_profile)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();

        optionsForBannerImage = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_banner_bg)
                .showImageOnFail(R.drawable.default_banner_bg)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer()).build();
    }


    public ImageLoader getImageLoader() {
        return imageloader;
    }

    public void loadImage(String url, CircularImageViews circularImageView) {
        imageloader.displayImage(url, circularImageView, optionsWithPlaceHolder);
    }

    public void loadBannerImage(String url, ImageView circularImageView) {
        imageloader.displayImage(url, circularImageView, optionsForBannerImage);
    }

    /***//*
    private void initBranch() {
        Branch.getInstance().initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {

            }
        });
    }*/
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initSingletons() {
        UActivePref.initInstance(mContext);
    }

    private void initSharedPreferences() {
        mSharedPreferences = getSharedPreferences("CARMA_PREF", MODE_PRIVATE);

    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            mTracker.enableAutoActivityTracking(false);


        }
        return mTracker;
    }
}
