package com.uactiv.activity;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.uactiv.R;
import com.uactiv.applozicchat.ApplozicChat;
import com.uactiv.controller.BannerImageListener;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.Buddyup_Fragment;
import com.uactiv.fragment.BusinessProfileFragment;
import com.uactiv.fragment.FavouriteFragment;
import com.uactiv.fragment.Help_Fragment;
import com.uactiv.fragment.Home;
import com.uactiv.fragment.Notification_Fragment;
import com.uactiv.fragment.Pickup_Fragment;
import com.uactiv.fragment.ProfileFragment;
import com.uactiv.fragment.SettingsFragment;
import com.uactiv.gcm.RegistrationIntentService;
import com.uactiv.location.GPSTracker;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements BannerImageListener, FragmentDrawer.FragmentDrawerListener, AppConstants.SharedConstants, ResponseListener, AppConstants.urlConstants {

    public static DrawerLayout mDrawerLayout;
    public static ImageButton imgMessage, imgCalendar, imgMenuIcon;
    private static String TAG = MainActivity.class.getSimpleName();
    public String checkRatingJson = "";


    public Fragment fragment = null;

    ImageButton imgDashBoard;
    GPSTracker gpsTracker = null;
    // CallbackManager callbackManager;
    ShareDialog shareDialog;
    TextView tv_notification_count = null;
    TextView tv_schedule_count = null;
    String bannerUrl = null;
    // private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private BroadcastReceiver countReceiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ResponseListener mResponseListener = null;
    MessageDatabaseService messageDatabaseService;
    public static String From_Page = "";
    String mytokan = "";
    String filterSkills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResponseListener = this;
        FacebookSdk.sdkInitialize(MainActivity.this); // Facebook sdk initialtion.
        if (!(SharedPref.getInstance().getBooleanValue(this, AppConstants.SharedConstants.PREF_IS_CHAT_LOGIN))) {
            new ApplozicChat(this).loginWithApplozic();
        }



        SharedPref.getInstance().setSharedValue(MainActivity.this, isStartUpExpired, true);

        shareDialog = new ShareDialog(MainActivity.this);
        if(this.getIntent().getStringExtra("fromfragment")==null){
            gpsTracker = new GPSTracker(MainActivity.this);
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(getResources().getString(R.string.sentTokenToServer), false);
                if (sentToken) {
                    Map<String, String> params = new HashMap<>(2);
                    params.put("iduser", SharedPref.getInstance().getStringVlue(MainActivity.this, userId));
                    params.put("device_id", SharedPref.getInstance().getStringVlue(MainActivity.this, gcm_token));


                    RequestHandler.getInstance().stringRequestVolley(MainActivity.this,
                            AppConstants.getBaseUrl(SharedPref.getInstance()
                                    .getBooleanValue(MainActivity.this, isStaging)) + updateDeviceId,
                            params, mResponseListener, 0);
                    Log.i(TAG, "gcm_send_message");
                } else {
                    Log.i(TAG, "token_error_message");
                }
            }
        };

        countReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && (intent.getAction().equals(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED)
                        || intent.getAction().equals(MobiComKitConstants.APPLOZIC_UNREAD_COUNT))) {
                    setNotificationCount();
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


        // updateLoactionToServer();

        imgDashBoard = (ImageButton) findViewById(R.id.imgDashBoard);
        imgMessage = (ImageButton) findViewById(R.id.imgMessage);
        imgCalendar = (ImageButton) findViewById(R.id.imgCalendar);
        tv_schedule_count = (TextView) findViewById(R.id.schedule_count);
        tv_notification_count = (TextView) findViewById(R.id.notification_count);
        if(this.getIntent().getStringExtra("skills")!=null){
            filterSkills=this.getIntent().getStringExtra("skills");
            Log.d("sssssk",filterSkills);
        }




        imgDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConstants.hideViewKeyBoard(MainActivity.this, Home.buddyUpSearchAuto);
                Utility.setEventTracking(MainActivity.this, "Home Page Tabs", "Home Dashboard Tab Click");
                imgDashBoard.setSelected(true);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(false);
                imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
                Bundle mBundle = new Bundle();
                mBundle.putString("navi", "buddy");
                From_Page = "home";

                //commented by pratik
                fragment = new Explorar_();
                fragment.setArguments(mBundle);
                //  AppConstants.genralPopup(MainActivity.this,0, jsonObject);
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    if (imgDashBoard.isSelected()) {
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        Log.d("isselected", "");
                    } else {
                        fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    }

                    fragmentTransaction.commit();
                }

            }
        });

        imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppConstants.hideViewKeyBoard(MainActivity.this, Home.buddyUpSearchAuto);
                //      AppConstants.genralPopup(MainActivity.this,1, jsonObject);
                Utility.setEventTracking(MainActivity.this, "Home Page Tabs", "Notification Tab Click");
                if (gpsTracker != null) {
                    gpsTracker.stopUsingGPS();
                }

                imgDashBoard.setSelected(false);
                imgMessage.setSelected(true);
                //  imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
                imgCalendar.setSelected(false);
                imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
                From_Page = "notification";
                getSupportFragmentManager().popBackStack();
                Fragment fragment = new Notification_Fragment();
                transactFragment(fragment);
            }
        });

        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppConstants.hideViewKeyBoard(MainActivity.this, Home.buddyUpSearchAuto);
                if (gpsTracker != null) {
                    gpsTracker.stopUsingGPS();
                }
                Utility.setEventTracking(MainActivity.this, "Home Page Tabs", "Calender Tab Click");

                tv_schedule_count.setVisibility(View.GONE);
                imgDashBoard.setSelected(false);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(true);
                imgCalendar.setBackgroundResource(R.drawable.social_feed_pressed);
                From_Page = "cal";
                getSupportFragmentManager().popBackStack();

                PickupListing listfragment = new PickupListing();
                transactFragment(listfragment);
            }
        });
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setDrawerListener(this);
        if (this.getIntent().getStringExtra("ispickup") != null) {
            fragment = new Pickup_Fragment();

            Bundle bn = new Bundle();

            fragment.setArguments(bn);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment).commit();

        } else {
            if(this.getIntent().getStringExtra("adapterfrom")!=null){
                if(this.getIntent().getStringExtra("adapterfrom").equalsIgnoreCase("buddyupadap")){
                    Buddyup_Fragment fragment=new Buddyup_Fragment();
                    Bundle bn=new Bundle();
                    bn.putString("setting","setting");
                    bn.putString("adapterfrom","adapterfrom");
                    bn.putString("filter","true");
                    bn.putString("skills",filterSkills);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragment.setArguments(bn);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }else{
                    Pickup_Fragment fragment=new Pickup_Fragment();
                    Bundle bn=new Bundle();
                    bn.putString("adapterfrom","pickupadap");
                    bn.putString("skills",filterSkills);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragment.setArguments(bn);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }

            }else{
                displayView(0);
            }


        }

        ((ImageView) findViewById(R.id.imgMenuIcon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsTracker != null) {
                    gpsTracker.stopUsingGPS();
                }
                AppConstants.hideViewKeyBoard(MainActivity.this, Home.buddyUpSearchAuto);
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                }
                try {

                } catch (Exception e) {
                    Log.d("Error", "" + e.toString());
                }
            }
        });

        messageDatabaseService = new MessageDatabaseService(this);
        setNotificationCount();
        LocalBroadcastManager.getInstance(this).registerReceiver(countReceiver,
                new IntentFilter(MobiComKitConstants.APPLOZIC_UNREAD_COUNT));
    }

    private void setNotificationCount() {
        int notification = SharedPref.getInstance().getIntVlue(MainActivity.this, notification_count);

        if (messageDatabaseService == null)
            messageDatabaseService = new MessageDatabaseService(this);

        Log.d(TAG, "app unread count : " + notification);
        notification = notification + messageDatabaseService.getTotalUnreadCount();
        Log.d(TAG, "message unread count : " + messageDatabaseService.getTotalUnreadCount());
        Log.d(TAG, "total unread count : " + notification);

        if (tv_notification_count != null && notification != 0) {
            tv_notification_count.setVisibility(View.VISIBLE);
            tv_notification_count.setText(String.valueOf(notification));
        } else {
            tv_notification_count.setVisibility(View.GONE);
        }
        tv_schedule_count.setVisibility(View.GONE);
    }

    /***/
    private void onChangeBanner() {
        double latitude = 0.0, longitude = 0.0;
        if(this.getIntent().getStringExtra("fromfragment")==null){
            if (gpsTracker != null) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                Log.e("Error on Valid City", "" + latitude);
            }
        }


        if (Utility.isConnectingToInternet(MainActivity.this)) {
            try {
                Map<String, String> param = new HashMap<>(3);
                param.put("city", AppConstants.getCityByLocation(MainActivity.this, latitude, longitude));
                param.put("size", getResources().getString(R.string.device_reso));
                RequestHandler.getInstance().stringRequestVolley(MainActivity.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(MainActivity.this, isStaging)) + validCity, param, this, 2);

            } catch (Exception e) {
                e.printStackTrace();

                Log.e("Error on Valid City", "" + e.toString());
            }

        } else {
            Utility.showInternetError(MainActivity.this);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("aaaaaaaaaa",requestCode+""+"---"+requestCode+"");
        Intent pass=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(pass);

       {
            if (gpsTracker.canGetLocation()) {

                if (Home.gpsNotifier != null) {
                    Home.gpsNotifier.GpsNotifier(requestCode);
                }
            } else {
             //   showSettingsAlert(requestCode);
            }
        }



    }

    public void showSettingsAlert(final int arg) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, arg);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                return;
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    //b.chaudhari@gmail.vom

    private void transactFragment(Fragment fragment) {
        if (!AppConstants.isGestLogin(MainActivity.this)) {
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (imgDashBoard.isSelected()) {
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    Log.d("isselected", "");
                } else {
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                }

                fragmentTransaction.commit();
            }
        } else {
            AppConstants.loginDialog(MainActivity.this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //  gpsTracker = new GPSTracker(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(getResources().getString(R.string.registrationComplete)));
        LocalBroadcastManager.getInstance(this).registerReceiver(countReceiver, new IntentFilter(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(this, GPSTracker.class));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(countReceiver);
        stopService(new Intent(this, GPSTracker.class));
        SharedPref.getInstance().setSharedValue(MainActivity.this, PREF_GEST_LOGIN, false);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        if (gpsTracker != null) {
            gpsTracker.stopUsingGPS();
        }


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        // Utility.setEventTracking(MainActivity.this,AppConstants.SCREEN_TRACKING_ID_MENU);
        switch (position) {
            case 0:
                fragment = new Explorar_();
                Bundle bn = new Bundle();
       /*         if (this.getIntent().getStringExtra("setting") != null) {
                    bn.putString("setting", "setting");
                    fragment.setArguments(bn);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                }*/
                //imgCalendar.setSelected(false);
                // imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);


                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

                imgDashBoard.setSelected(true);
                imgMessage.setSelected(false);
                imgDashBoard.setSelected(true);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(false);
                imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
                Utility.setEventTracking(MainActivity.this, "Menu screen", "Home On Menu done");
                Log.d("inside drawer", "drawer");
                break;
            case 1:

                if (SharedPref.getInstance().getBooleanValue(MainActivity.this, isbussiness)) {
                    fragment = new BusinessProfileFragment();
                } else {
                    fragment = new ProfileFragment();
                }
                imgDashBoard.setSelected(false);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(false);
                imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
                if (!AppConstants.isGestLogin(MainActivity.this)) {

                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                    imgDashBoard.setSelected(true);
                    imgMessage.setSelected(false);
                    imgCalendar.setSelected(false);
                } else {

                    AppConstants.loginDialog(MainActivity.this);

                }

                if (!AppConstants.isGestLogin(MainActivity.this)) {
                    Utility.setEventTracking(MainActivity.this, "Menu screen", "my Profile On Menu done");
                } else {
                    Utility.setEventTracking(MainActivity.this, "Menu screen", "my Profile On Menu done");
                }

                if (!AppConstants.isGestLogin(MainActivity.this)) {
                    Utility.setScreenTracking(MainActivity.this, "Menu screen");
                } else {
                    Utility.setScreenTracking(MainActivity.this, "Guest login Menu screen.");
                }


                break;
            case 2:

                fragment = new FavouriteFragment();
                imgDashBoard.setSelected(false);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(false);
                imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
                if (!AppConstants.isGestLogin(MainActivity.this)) {
                    Utility.setEventTracking(MainActivity.this, "Menu screen", "Favorites On Menu done");
                } else {
                    Utility.setEventTracking(MainActivity.this, "Menu screen", "Favorites On Guest login Menu done");
                }

                if (!AppConstants.isGestLogin(MainActivity.this)) {

                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                    imgDashBoard.setSelected(true);
                    imgMessage.setSelected(false);
                    imgCalendar.setSelected(false);
                } else {

                    AppConstants.loginDialog(MainActivity.this);

                }

                break;
            case 3:
                if (!AppConstants.isGestLogin(MainActivity.this)) {
                    showShareDialog();
                    imgDashBoard.setSelected(false);
                    imgMessage.setSelected(false);
                    imgCalendar.setSelected(false);
                    imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
                    if (!AppConstants.isGestLogin(MainActivity.this)) {
                        Utility.setEventTracking(MainActivity.this, "Menu screen", "Invite friends On Menu done");

                    } else {
                        Utility.setEventTracking(MainActivity.this, "Menu screen", "Invite friends On Guest login Menu done");

                    }

                } else {
                    AppConstants.loginDialog(MainActivity.this);
                }


                break;
            case 4:
                fragment = new SettingsFragment();
                imgDashBoard.setSelected(false);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(false);
                imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();
                imgDashBoard.setSelected(true);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(false);

                if (!AppConstants.isGestLogin(MainActivity.this)) {
                    Utility.setEventTracking(MainActivity.this, "Menu screen", "Settings On Menu done");
                } else {
                    Utility.setEventTracking(MainActivity.this, "Menu screen", "Settings On Guest login  Menu done");
                }

                break;
            case 5:
                fragment = new Help_Fragment();
                imgDashBoard.setSelected(false);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(false);
                imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();
                imgDashBoard.setSelected(true);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(false);

                if (!AppConstants.isGestLogin(MainActivity.this)) {
                    Utility.setEventTracking(MainActivity.this, "Menu screen", "About On Menu done");
                } else {
                    Utility.setEventTracking(MainActivity.this, "Menu screen", "About On Guest login Menu done");
                }
                break;
            default:
                break;
        }




      /*  if (position == 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            imgDashBoard.setSelected(true);
            imgMessage.setSelected(false);
            imgCalendar.setSelected(false);
            Log.d("inside drawer1", "drawer1");
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
            fragmentTransaction.commit();
            imgDashBoard.setSelected(true);
            imgMessage.setSelected(false);
            imgCalendar.setSelected(false);
        }*/
    }

    private void showShareDialog() {
        String ref = "";
        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(MainActivity.this, referalcode))) {
            ref = SharedPref.getInstance().getStringVlue(MainActivity.this, referalcode);
        }

       /* final String msg = "" + "Use referral code: " + ref + "\n \n" + ""
                + "Check out " + getResources().getString(R.string.app_name) + ", a cool app to find buddies and organize sports & fitness activities.\n \n" + "http://play.google.com/store/apps/details?id="
                + getApplicationContext().getPackageName();*/

        final String msg = "" + "Use referral code: " + ref + "\n \n" + ""
                + "Check out " + getResources().getString(R.string.app_name) + ", a cool app to find buddies and organize sports & fitness activities.\n \n" + "https://uactiv.com/inviteDownload.html";
        //"https://asbm9.app.goo.gl/Scq6";

        AppConstants.showInviteShareAlert(MainActivity.this, msg, shareDialog);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                imgDashBoard.setSelected(true);
                imgMessage.setSelected(false);
                imgMessage.setSelected(false);
                imgCalendar.setSelected(false);
                imgCalendar.setBackgroundResource(R.drawable.social_feed_normal);
            }

            if (gpsTracker != null) {
                gpsTracker.stopUsingGPS();
            }
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }
        } else {

            finish();

        }


    }

    private void shareBitmap(String caption, View view, ShareDialog shareDialog) {
        Bitmap myBitmap = view.getDrawingCache();
        shareBitmap(this, shareDialog, caption, myBitmap);
    }

    private void shareBitmap(Context context, ShareDialog shareDialog, String caption, Bitmap photo) {
        facebookImageShare(context, shareDialog, caption, photo);
    }

    public void facebookImageShare(Context mContext, ShareDialog shareDialog, String caption, Bitmap photo) {
        if (photo != null && shareDialog != null)
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                SharePhoto mSharePhoto = new SharePhoto.Builder().setBitmap(photo).setCaption(caption).build();
                SharePhotoContent mBuilder = new SharePhotoContent.Builder().addPhoto(mSharePhoto).build();
                shareDialog.show(mBuilder);
            } else {
                Toast.makeText(mContext, "Error Occurred!", Toast.LENGTH_SHORT).show();
            }
    }



    @Override
    public void successResponse(String successResponse, int flag) {
        /** flag = 0 update device Id
         *  flag == 1 reviewlist.
         */

        Log.e(TAG, "" + successResponse);

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(successResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (flag) {
            case 0:
                if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                    Log.d(TAG, "DeviceId updated!");
                }
                break;

            case 1:
                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        checkRatingJson = jsonObject.toString();
                        JSONArray finishedList = jsonObject.optJSONArray(KEY_DETAIL);
                        if (finishedList != null && finishedList.length() > 0) {
                            for (int i = 0; i < finishedList.length(); i++) {
                                Bundle bundle = new Bundle();
                                bundle.putString("iduser", finishedList.optJSONObject(i).optString("iduser"));
                                bundle.putString("image", finishedList.optJSONObject(i).optString("image"));
                                bundle.putString("name", finishedList.optJSONObject(i).optString("firstname") + " " + finishedList.optJSONObject(i).optString("lastname"));
                                bundle.putString("location", finishedList.optJSONObject(i).optString("location"));
                                bundle.putString("idschedule", finishedList.optJSONObject(i).optString("idschedule"));
                                bundle.putString("type", finishedList.optJSONObject(i).optString("type"));
                                bundle.putString("activity", finishedList.optJSONObject(i).optString("activity"));
                                bundle.putString("start_time", finishedList.optJSONObject(i).optString("start_time"));
                                bundle.putString("date", finishedList.optJSONObject(i).optString("date"));

                                AppConstants.showReviewDialog(MainActivity.this, bundle, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(MainActivity.this, isStaging)) + rating, false, this);

                                if (finishedList.optJSONObject(i).optString("isbusinesslocation").equals("1")) {
                                    AppConstants.showReviewDialog(MainActivity.this, bundle, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(MainActivity.this, isStaging)) + rating, true, this);
                                }
                            }
                        }

                        //Update user badge
                        if (Utility.isNullCheck(jsonObject.optString("badge")) && !(SharedPref.getInstance().getBooleanValue(MainActivity.this, isbussiness))) {
                            String oldBadge = SharedPref.getInstance().getStringVlue(this, badge);
                            String newBadge = jsonObject.optString("badge");
                            Log.d(TAG, "BadgetOldBadge : " + oldBadge);
                            Log.d(TAG, "newBadge : " + newBadge);
                            if (oldBadge != null && newBadge != null) {
                                if (newBadge.length() > 0 && !(oldBadge.trim().equalsIgnoreCase(newBadge.trim()))) {
                                    Log.e("Badge categ *** " + jsonObject.optString("badge"), "" + SharedPref.getInstance().getStringVlue(this, badge));
                                  //  showBadgeUpdateDialog(newBadge);
//                                    SharedPref.getInstance().setSharedValue(MainActivity.this, badge, jsonObject.optString("badge"));
                                }
                            }
                            SharedPref.getInstance().setSharedValue(MainActivity.this, badge, jsonObject.optString("badge"));
                        }
                    }
                    if (gpsTracker != null) {
                        gpsTracker.stopUsingGPS();
                    }
                }
                break;
            case 2:


                //get the android imageurl and set banner image from JSON response using key called "android_img".
                //Patch Done by Jeeva on 06-08-2016...
                /*{"details":{"android_img":"app\/images\/city\/drawable-hdpi\/uactive.png","img":"app\/images\/city\/uactive.png","name":"Chennai"},"result":true}*/
                if (jsonObject != null) {
                    if (jsonObject.optBoolean(resultcheck)) {
                        JSONObject detailsObject = jsonObject.optJSONObject(KEY_DETAIL);
                        if (detailsObject != null && detailsObject.length() > 0) {
                            String url = jsonObject.optString("android_img");
                            if (Utility.isNullCheck(url)) {
                                String bannerUrl = AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(MainActivity.this, isStaging)) + url;
                                setBannerUrl(bannerUrl);
                                // Log.e(" Valid bannerUrl", "" + bannerUrl);
                            }
                        }
                    }
                }
                //Patch Done by Jeeva on 06-08-2016...

                break;
            case 8:
                if (jsonObject != null) {

                    Log.d("sessionlola", AppConstants.getSession(MainActivity.this) + "");
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        String type = jsonObject.optString("type");
                        if (type.equalsIgnoreCase("pick_up")) {
                            AppConstants.genralPopupEventRate(MainActivity.this, 1, jsonObject, this, MainActivity.this, "pickup");
                            Log.d("show pickup dialog", "");
                        } else if (type.equalsIgnoreCase("promotional")) {

                            if (jsonObject.optString("content").isEmpty()) {
                                AppConstants.genralPopupEventRate(MainActivity.this, 3, jsonObject, this, MainActivity.this, "pickup");
                            } else {
                                AppConstants.genralPopupEventRate(MainActivity.this, 2, jsonObject, this, MainActivity.this, "pickup");
                            }
                            //  SharedPref.getInstance().setSharedValue( MainActivity.this, "APP_SESSION", 1);


                            Log.d("show pramotional dialog", "");
                        } else if (type.equalsIgnoreCase("rateus")) {
                            if (!SharedPref.getInstance().getBooleanValue(MainActivity.this, "islater")) {
                                AppConstants.genralPopupEventRate(MainActivity.this, 0, jsonObject, this, MainActivity.this, "pickup");
                            }


                        }


                    }

                }
                break;

            default:
                break;
        }


    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {

    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }


    @Override
    public String getBannerImage() {
        return bannerUrl;
    }
}
