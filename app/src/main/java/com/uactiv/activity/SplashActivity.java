package com.uactiv.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.uactiv.BuildConfig;
import com.uactiv.R;
import com.uactiv.application.UActiveApplication;
import com.uactiv.controller.IRunTimePermission;
import com.uactiv.controller.ResponseListener;
import com.uactiv.gcm.RegistrationIntentService;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.RuntimeAccess;
import com.uactiv.utils.RuntimeAccessSplash;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity implements AppConstants.SharedConstants, IRunTimePermission, ResponseListener {

    RuntimeAccessSplash mRuntimeAccess;
    private String TAG = "SplashActivity";
    boolean isDeepLinkingLaunch = false;
    Intent deepLinkingLaunchIntent = null;
    final int CHECK_VERSION_REQUEST_CODE = 101;
    private int mysession = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        AppsFlyerLib.getInstance().startTracking(this.getApplication(), "ha9E2njWD2UvuMvC3XySEF");
        AppsFlyerLib.getInstance().enableUninstallTracking("858166060344"); // ADD THIS LINE HERE*/

        Tracker tracker = UActiveApplication.getInstance().getDefaultTracker();
        tracker.send(new HitBuilders.ScreenViewBuilder()
                .setCampaignParamsFromUrl("")
                .build());

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        if (Utility.isConnectingToInternet(this)) {
            init();
            //checkVersion();
        } else {
            try {
                init();
            } catch (WindowManager.BadTokenException ex) {
                ex.printStackTrace();
            }


        }
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }


    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "SDK_INT");

            mRuntimeAccess = new RuntimeAccessSplash(this, this);
            mRuntimeAccess.grandPermission();

            if (AppConstants.getSession(SplashActivity.this) > 0) {
                mysession = AppConstants.getSession(SplashActivity.this);
                mysession++;
                SharedPref.getInstance().setSharedValue(SplashActivity.this, "APP_SESSION", mysession);
            } else {
                mysession++;
                SharedPref.getInstance().setSharedValue(SplashActivity.this, "APP_SESSION", mysession);
                onSuccess(3000);
            }
            Utility.setScreenTracking(SplashActivity.this, AppConstants.SCREEN_TRACKING_ID_PERMISSIONSCREEN);

        } else {
            Log.d(TAG, "SDK_INT else");
            // Pre-Marshmallow
            onSuccess(2000);
        }
    }

    private void checkVersion() {
        if (Utility.isConnectingToInternet(SplashActivity.this)) {
            String checkVersion = "https://uactiv.com/app/index.php/config/checkVersion";
            Map<String, String> param = new HashMap<>();
            param.put("device_type", "0");
            param.put("version", String.valueOf(getVersionNumber()));
            RequestHandler.getInstance().stringRequestVolley(SplashActivity.this, checkVersion, param, this, 0);
        } else {
            Utility.showInternetError(SplashActivity.this);
        }
    }
    private boolean checkWriteExternalPermission()
    {
        String permission = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private int getVersionNumber() {
        return BuildConfig.VERSION_CODE;
    }

    private void navigateToNext() {
        SharedPref.getInstance().setSharedValue(SplashActivity.this, PREF_GEST_LOGIN, false);
        if (SharedPref.getInstance().getBooleanValue(this, islogin)) {
            Log.d(TAG, "islogin");
            if (isDeepLinkingLaunch && deepLinkingLaunchIntent != null) {
                Log.d(TAG, "lanched DeepLinking");
                startActivity(deepLinkingLaunchIntent);
                finish();
            } else {
                Log.d(TAG, "lanched HomeScreen");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (SharedPref.getInstance().getBooleanValue(this, isStartUpExpired)) {
            Intent intent = new Intent(this, Sign_In.class);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "isStartUpExpired");
            Intent intent = new Intent(this, StartUpActivity.class);
            startActivity(intent);
            finish();
        }
        Log.d(TAG, "none");
    }

    private void initiateAnimo() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        animation.setDuration(3000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                /*navigateToNext*/
                navigateToNext();
                Log.d(TAG, "onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ImageView imageView = (ImageView) findViewById(R.id.imageView6);
        imageView.startAnimation(animation);
    }

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RuntimeAccess.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                //perms.put(android.Manifest.permission.WRITE_SETTINGS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (//perms.get(android.Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED
                    //perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                        ) {

                    Log.e("Running from OnRequest", "Permission");
                    // All Permissions Granted
                }
//                else if (perms.get(Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
//                    startActivity(new Intent("android.settings.action.MANAGE_WRITE_SETTINGS"));
//                }
                else {
                    // Permission Denied
                    Toast.makeText(this, "Some Permission are denied", Toast.LENGTH_SHORT)
                            .show();
                }
                 initiateAnimo();
                onSuccess(0);
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSuccess(int milis) {
        onActivityChange(milis);
    }

    private void onActivityChange(int milis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(SplashActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    initiateAnimo();
                }

            }
        }, milis);
    }


    @Override
    protected void onResume() {
        super.onResume();
      /*  if (Utility.isConnectingToInternet(this)) {
            checkVersion();
        } else {
            init();
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {

                Log.d("lola", referringParams.toString());

                if (SharedPref.getInstance().getBooleanValue(SplashActivity.this, islogin)) {

                    if (referringParams != null) {

                        if (referringParams.optBoolean("+clicked_branch_link")) {
                            Log.d(TAG, "status : " + referringParams.optString("status"));
                            deepLinkingLaunchIntent = new Intent(SplashActivity.this, PickUpGuest.class);
                            deepLinkingLaunchIntent.putExtra("from_schedule", false);
                            deepLinkingLaunchIntent.putExtra("isFromNotification", true);//isFromNotification
                            deepLinkingLaunchIntent.putExtra("fragment", "map");
                            deepLinkingLaunchIntent.putExtra("status", referringParams.optString("status"));
                            deepLinkingLaunchIntent.putExtra("position", "");
                            deepLinkingLaunchIntent.putExtra("isDeepLinking", true);
                            deepLinkingLaunchIntent.putExtra("sstatus", referringParams.optString("sstatus"));
                            deepLinkingLaunchIntent.setAction(Long.toString(System.currentTimeMillis()));
                            deepLinkingLaunchIntent.putExtra("idschedule", referringParams.optString("idschedule"));
                            isDeepLinkingLaunch = true;
                            //  startActivity(intent);
                        }
                    }
                }

            }
        }, this.getIntent().getData(), this);
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivityForResult(myAppLinkToMarket, CHECK_VERSION_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void showForceUpdateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
        alertDialog.setTitle("Update available");
        alertDialog.setMessage("We've gotten better! Please upgrade the UACTIV app to use our new features.");
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                launchMarket();
            }
        });

        alertDialog.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
                return;
            }
        });

        alertDialog.show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        if (requestCode == RESULT_OK) {
            switch (requestCode) {
                case CHECK_VERSION_REQUEST_CODE:
                    //  checkVersion();
                    break;
            }
        }
    }

    @Override
    public void successResponse(String successResponse, int flag) {
        /* flag == 0*/


        switch (flag) {
            case 0:
                if (!TextUtils.isEmpty(successResponse)) {
                    Log.d(TAG, "successResponse : " + successResponse);
                    try {
                        JSONObject rootResponse = new JSONObject(successResponse);
                        if (rootResponse != null) {
                            if (rootResponse.optBoolean("result")) {
                                init();
                            } else {
                                showForceUpdateDialog();
                                // init();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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


}
