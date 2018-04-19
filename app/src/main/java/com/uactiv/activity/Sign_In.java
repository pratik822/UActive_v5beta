package com.uactiv.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.applozicchat.ApplozicChat;
import com.uactiv.controller.IRunTimePermission;
import com.uactiv.controller.ResponseListener;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.RuntimeAccess;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomEditText;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Sign_In extends AppCompatActivity implements View.OnClickListener, AppConstants.urlConstants, AppConstants.SharedConstants, ResponseListener,IRunTimePermission {
    CustomEditText signin_email_ed;
    CustomEditText signin_pwd_ed;
    Button signin_btn = null;
    CustomTextView txt_forgotpassword = null;
    String TAG = getClass().getSimpleName();
    Dialog dialog;
    CustomButton fbbtn;
    Intent turms;
    ViewFlipper vp;
    CallbackManager callbackManager;
    Animation outAnimation;
    InputMethodManager im;
    SoftKeyboard softKeyboard;
    RuntimeAccess mRuntimeAccess;
    //forgot password

    CustomEditText emailid;
    CustomButton dialogButton;
    LinearLayout mylay;


    //  private ProfileTracker profileTracker;

    GifImageView progressWheel = null;

    boolean isgcmenabled = false;
    ProfileTracker profileTracker;
    private JSONObject fb_profile_obj;


    //signup page

    EditText signinemailed;
    EditText signinpassed;
    EditText firstname = null;
    EditText lastname = null;
    Button signin = null;
    Button signup = null;
    CustomButton fbbtnone, signupone;
    ImageView logoicon;
    CustomTextView logoHeadingText;
    RelativeLayout myroot;
    boolean isOpened = false;
    Animation anim;
    CustomTextView txt_turms, txt_policy, txt_gestlogin;
    ScaleAnimation scale;
    private boolean wasOpened;
    ScrollView scroll;
    Display mDisplay;
    ScrollView myscroll;
    private String fromLogin;
    private int mysession=0;
    //normal login fb callback
    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            progressWheel.startAnimation();
            progressWheel.setVisibility(View.VISIBLE);

            Log.e(TAG, "accessToken" + loginResult.getAccessToken().getSource());

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object,
                                                GraphResponse response) {
                            // Application code
                            Log.d(TAG + "GraphResponse", ": " + response.toString());

                            if (object != null) {
                                Log.d(TAG + "object", ": " + object.toString());

                                getFriends();

                                Profile.fetchProfileForCurrentAccessToken();
                                Profile current = Profile.getCurrentProfile();

                                fb_profile_obj = object;

                                try {
                                    if (current != null && Utility.isNullCheck(current.getProfilePictureUri(AppConstants.FACEBOOK_PIC_SIZE, AppConstants.FACEBOOK_PIC_SIZE).toString())) {
                                        fb_profile_obj.put("image", "" + current.getProfilePictureUri(AppConstants.FACEBOOK_PIC_SIZE, AppConstants.FACEBOOK_PIC_SIZE).toString());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                                if (Utility.isNullCheck(object.optString("id"))) {

                                    try {
                                        Map<String, String> params = new HashMap<String, String>();
                                        Log.e(TAG, "ids " + object.optString("id"));
                                        params.put("facebookid", object.optString("id"));
                                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(Sign_In.this, gcm_token))) {
                                            params.put("device_id", SharedPref.getInstance().getStringVlue(Sign_In.this, gcm_token));
                                        } else {
                                            params.put("device_id", "");
                                        }
                                        params.put("device_type", "0");
                                        params.put("email", object.optString("email"));
                                        Log.e("email", "ids " + object.optString("email"));
                                        RequestHandler.getInstance().stringRequestVolley(Sign_In.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(Sign_In.this, isStaging)) + checkfacebookid, params, Sign_In.this, 2);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                progressWheel.stopAnimation();
                                progressWheel.setVisibility(View.GONE);
                                Toast.makeText(Sign_In.this, "Error in facebook login!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", AppConstants.FACEBOOK_LOGIN_FIELDS);
            request.setParameters(parameters);
            request.executeAsync();

        }


        @Override
        public void onCancel() {
            progressWheel.stopAnimation();
            progressWheel.setVisibility(View.GONE);
        }

        @Override
        public void onError(FacebookException e) {
            Log.e("FacebookException", "" + e.getMessage());
            progressWheel.stopAnimation();
            progressWheel.setVisibility(View.GONE);
        }
    };

    private FacebookCallback<LoginResult> mFacebookCallbackSignup = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.startAnimation();
            AccessToken accessToken = loginResult.getAccessToken();

            Log.e(TAG, "fb" + loginResult.getAccessToken().getSource());

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code


                            if (object != null) {
                                getFriends();
                                Profile.fetchProfileForCurrentAccessToken();
                                Profile current = Profile.getCurrentProfile();
                                fb_profile_obj = object;
                                try {
                                    if (current != null && current.getProfilePictureUri(AppConstants.FACEBOOK_PIC_SIZE, AppConstants.FACEBOOK_PIC_SIZE) != null) {

                                        try {
                                            fb_profile_obj.put("image", "" + current.getProfilePictureUri(AppConstants.FACEBOOK_PIC_SIZE, AppConstants.FACEBOOK_PIC_SIZE));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (NullPointerException e) {
                                    e.getMessage();
                                }
                                try {
                                    Map<String, String> params = new HashMap<String, String>();
                                    Log.e(TAG, "ids " + object.optString("id"));
                                    params.put("facebookid", object.optString("id"));
                                    params.put("email", object.optString("email"));
                                    RequestHandler.getInstance().stringRequestVolley(Sign_In.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(Sign_In.this, isStaging)) + checkfacebookid, params, Sign_In.this, 5);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", AppConstants.FACEBOOK_LOGIN_FIELDS);
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {
            progressWheel.stopAnimation();
            progressWheel.setVisibility(View.GONE);
        }

        @Override
        public void onError(FacebookException e) {
            progressWheel.stopAnimation();
            progressWheel.setVisibility(View.GONE);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.login_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mylay=(LinearLayout)findViewById(R.id.mylay);
        //anim = AnimationUtils.loadAnimation(this, R.anim.scale);
        mDisplay = getWindowManager().getDefaultDisplay();
        //  Toast.makeText(Sign_In.this, "My screen Density-" + getDensityName(Sign_In.this)+" "+"height is"+mDisplay.getHeight(), Toast.LENGTH_LONG).show();
        im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);

        Utility.setScreenTracking(Sign_In.this, AppConstants.SCREEN_TRACKING_ID_LOGIN);

        File dir = getFilesDir();

        int length = dir.list().length;

        Log.e("dir", String.valueOf(length));
        fromLogin = this.getIntent().getStringExtra("fromLogin");


        // Toast.makeText(getApplicationContext(), "myheight"+getDensityName(getApplicationContext()), Toast.LENGTH_LONG).show();
        callbackManager = CallbackManager.Factory.create();
        /*profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                Log.e("oldProfile image",":"+oldProfile.getProfilePictureUri(500,500));
                Log.e("currentProfile image",":"+currentProfile.getProfilePictureUri(500,500));
            }
        };*/


        initViews();
        getFBHashKey();
        if (fromLogin != null) {

            if (fromLogin.equalsIgnoreCase("Sign_In")) {
                vp.setDisplayedChild(2);
            }
        }
        ((CustomTextView) findViewById(R.id.signup)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                outAnimation = (Animation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_in_left);
                vp.setInAnimation(outAnimation);
                vp.setDisplayedChild(2);
            }
        });

        /*if (SharedPref.getInstance().getBooleanValue(Sign_In.this, islogin)) {
            Intent intent = new Intent(Sign_In.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/
    }


    private String getDensityName(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        if (density >= 4.0) {
            return "xxxhdpi";
        }
        if (density >= 3.0) {
            return "xxhdpi";
        }
        if (density >= 2.0) {
            return "xhdpi";
        }
        if (density >= 1.5) {
            return "hdpi";
        }
        if (density >= 1.0) {
            return "mdpi";
        }
        return "ldpi";
    }

    private void getFBHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "doodleblue.doodleblue.com",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void initViews() {
        signin_btn = (Button) findViewById(R.id.signin_btn);
        AppConstants.hideKeyboard(Sign_In.this);
        signin_btn.setOnClickListener(this);
        signin_email_ed = (CustomEditText) findViewById(R.id.signin_email_ed);
        // signin_email_ed.setText("pushtwo@gmail.com");
        signin_pwd_ed = (CustomEditText) findViewById(R.id.signin_pwd_ed);
        // signin_pwd_ed.setText("12345");
        txt_forgotpassword = (CustomTextView) findViewById(R.id.forgotbtn);
        txt_forgotpassword.setOnClickListener(this);
        fbbtn = (CustomButton) findViewById(R.id.fbbtn);
        vp = (ViewFlipper) findViewById(R.id.flipper);
        fbbtn.setOnClickListener(this);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        myscroll = (ScrollView) findViewById(R.id.myscroll);
        Utility.showProgressDialog(Sign_In.this, progressWheel);


        //forgot password

        emailid = (CustomEditText) findViewById(R.id.forgot_email_ed);
        dialogButton = (CustomButton) findViewById(R.id.submit_area);
        logoHeadingText = (CustomTextView) findViewById(R.id.textView5);
        dialogButton.setOnClickListener(this);


        //Signup

        signinemailed = (EditText) findViewById(R.id.signin_email_edone);
        signinpassed = (EditText) findViewById(R.id.signin_pwd_edone);
        fbbtnone = (CustomButton) findViewById(R.id.fbbtnone);
        signupone = (CustomButton) findViewById(R.id.signupone);
        logoicon = (ImageView) findViewById(R.id.imageView7);

      //  myroot = (RelativeLayout) findViewById(R.id.myroot);
        firstname = (EditText) findViewById(R.id.fname);
        lastname = (EditText) findViewById(R.id.lname);

        txt_turms = (CustomTextView) findViewById(R.id.txt_turms);
        txt_policy = (CustomTextView) findViewById(R.id.txt_policy);
        txt_gestlogin = (CustomTextView) findViewById(R.id.txt_gestlogin);
        scale = new ScaleAnimation((float) 1.0, (float) 1.0, (float) 1.0, (float) 1.0);

        // setListenerToRootView();
        //  txt_gestlogin.setPaintFlags(txt_turms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //txt_gestlogin.setTextColor(Color.BLUE);

        txt_turms.setOnClickListener(this);
        txt_policy.setOnClickListener(this);
        txt_gestlogin.setOnClickListener(this);
        fbbtnone.setOnClickListener(this);
        signupone.setOnClickListener(this);
        signin_btn.setOnClickListener(this);
        softKeyboard = new SoftKeyboard(mylay, im);
        softKeyboard.openSoftKeyboard();
        softKeyboard.closeSoftKeyboard();
        signinpassed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.hasFocus()) {
                    myscroll.smoothScrollTo(0, 300);
                }
            }
        });
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                Log.d("keyboardup", "no");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isOpened) {
                            scale = new ScaleAnimation((float) 1.0, (float) 1.0, (float) 1.0, (float) 1.0);
                            scale.setDuration(700);
                            scale.setFillAfter(true);
                            logoicon.startAnimation(scale);
                            logoHeadingText.setVisibility(View.VISIBLE);
                        } else {
                            Animation scaleDown = new ScaleAnimation((float) 0.7, (float) 1.0, (float) 0.7, (float) 1.0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -0.1f);
                            scaleDown.setDuration(800);
                            scaleDown.setFillAfter(true);
                            logoicon.startAnimation(scaleDown);
                            scaleDown.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    logoHeadingText.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });


                        }

                    }
                });

            }

            @Override
            public void onSoftKeyboardShow() {
                Log.d("keyboardup", "yes");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Rect r = new Rect();
                        isOpened = true;
                        Animation scaleDown = new ScaleAnimation((float) 1.0, (float) 0.7, (float) 1.0, (float) 0.7, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, -0.1f);
                        scaleDown.setDuration(700);
                        scaleDown.setFillAfter(true);
                        logoicon.startAnimation(scaleDown);
                        logoHeadingText.setVisibility(View.GONE);

                      /*  RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(250, 500, 200, 100);
                        myframe.setLayoutParams(params);*/
                    }
                });
                //* Rect r = new Rect();


            }
        });



    }


    private boolean keyboardShown(View rootView) {

        final int softKeyboardHeight = 100;
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        int heightDiff = rootView.getBottom() - r.bottom;
        return heightDiff > softKeyboardHeight * dm.density;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scale.setDuration(1000);
            scale.setFillAfter(true);
            logoicon.startAnimation(scale);
            logoHeadingText.setVisibility(View.VISIBLE);

            if (vp.getDisplayedChild() == 0) {
                finish();
            } else {
                outAnimation = (Animation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_out_right);
                vp.setOutAnimation(outAnimation);
                vp.setDisplayedChild(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.signin_btn:
                AppConstants.hideKeyboard(Sign_In.this);
                Utility.setEventTracking(Sign_In.this, "Login Screen", AppConstants.EVENT_TRACKING_ID_CREATE_ACCOUNT);
                SharedPref.getInstance().setSharedValue(Sign_In.this, PREF_GEST_LOGIN, false);
              /*  SharedPref.getInstance().setSharedValue(Sign_In.this, AppConstants.SharedConstants.isStaging, am_beta_signin.isChecked());
                AppConstants.getDomain(am_beta_signin.isChecked());
                Log.e("Native Matched or Not", "==" + am_beta_signin.isChecked());
                Log.e("Native Base Url", "" + AppConstants.getDomain(am_beta_signin.isChecked()));*/

                // if(Utility.isNullCheck( SharedPref.getInstance().getStringVlue(Sign_In.this,gcm_token))){
//                if (isStaging) {
//                    signin_email_ed.getText().toString().trim().replace(getResources().getString(R.string.staging_key), "");
//                }
                signInAPI();
                //}else {
                //    Utility.showToastMessage(Sign_In.this,"GCM not Registered");
                // }
                break;
            case R.id.forgotbtn:
                AppConstants.hideKeyboard(Sign_In.this);
                Utility.setEventTracking(Sign_In.this, "Login Screen", AppConstants.SCREEN_TRACKING_ID_FORGOTPASSWORDSCREEN);
                SharedPref.getInstance().setSharedValue(Sign_In.this, PREF_GEST_LOGIN, false);
                outAnimation = (Animation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_in_left);
                vp.setInAnimation(outAnimation);
                vp.showNext();

                break;

            case R.id.fbbtn:
                AppConstants.hideKeyboard(Sign_In.this);
                Utility.setEventTracking(Sign_In.this, "Login Screen", AppConstants.EVENT_TRACKING_ID_LOGIN_FACEBOOK);
                SharedPref.getInstance().setSharedValue(Sign_In.this, PREF_GEST_LOGIN, false);
           /*     SharedPref.getInstance().setSharedValue(Sign_In.this, AppConstants.SharedConstants.isStaging, am_beta_signin.isChecked());
                AppConstants.getDomain(am_beta_signin.isChecked());
                Log.e("FB Matched or Not", "==" + am_beta_signin.isChecked());
                Log.e("FB Base Url", "" + AppConstants.getDomain(am_beta_signin.isChecked()));
*/
                try {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (Utility.isConnectingToInternet(Sign_In.this)) {
                    loginwithfacebook();
                } else {
                    Utility.showInternetError(Sign_In.this);
                }
                break;

            case R.id.submit_area:
                AppConstants.hideKeyboard(Sign_In.this);
                if (emailid.getText().toString().matches(AppConstants.emailPattern) || emailid.getText().toString().matches(AppConstants.emailPatternTwoDecimalPoint)) {
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                    try {
                        Map<String, String> param = new HashMap<String, String>();
                        param.put("email", emailid.getText().toString().trim());
                        RequestHandler.getInstance().stringRequestVolley(Sign_In.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(Sign_In.this, isStaging)) + forgotpassword, param, Sign_In.this, 4);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.showToastMessage(Sign_In.this, "Please enter valid email address");
                }
                break;

            case R.id.signupone:
                AppConstants.hideKeyboard(Sign_In.this);
                Utility.setScreenTracking(Sign_In.this, AppConstants.SCREEN_TRACKING_ID_CREATEPROFILEPAGE);
                SharedPref.getInstance().setSharedValue(Sign_In.this, PREF_GEST_LOGIN, false);

                if (firstname.length() > 0) {

                    if (lastname.length() > 0) {

                        if (signinemailed.length() > 0) {
//                            if (signinemailed.getText().toString().trim().matches(AppConstants.emailPattern)) {
                            if (signinemailed.getText().toString().trim().matches(AppConstants.emailPattern) || signinemailed.getText().toString().trim().matches(AppConstants.emailPatternTwoDecimalPoint)) {
                                if (signinpassed.length() >= 4) {
                                    if (Utility.isConnectingToInternet(Sign_In.this)) {
                                        try {
                                            Map<String, String> stringMap = new HashMap<>();
                                            stringMap.put("email", signinemailed.getText().toString().trim());
                                            progressWheel.setVisibility(View.VISIBLE);
                                            progressWheel.startAnimation();
                                            JSONObject param = new JSONObject();
                                            param.put("firstname", firstname.getText().toString());
                                            param.put("lastname", lastname.getText().toString());
                                            param.put("email", signinemailed.getText().toString().trim());
                                            param.put("password", signinpassed.getText().toString().trim());
                                            Intent intent = new Intent(Sign_In.this, CreateProfile.class);
                                            intent.putExtra("profile_data", param.toString());
                                            intent.putExtra("logintype", "0"); // app 0
                                            intent.putExtra("name", firstname.getText().toString());
                                            intent.putExtra("gender", "");
                                            intent.putExtra("firstname",firstname.getText().toString());
                                            intent.putExtra("lastname",lastname.getText().toString());
                                            intent.putExtra("signinemailed",signinemailed.getText().toString());
                                            intent.putExtra("signinpassed",signinpassed.getText().toString());
                                            progressWheel.setVisibility(View.INVISIBLE);
                                            progressWheel.stopAnimation();
                                            startActivity(intent);

                                         //   RequestHandler.getInstance().stringRequestVolley(Sign_In.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(Sign_In.this, isStaging)) + checkEmail, stringMap, this, 6);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                        /*try {

                                            JSONObject param = new JSONObject();
                                            String firstna =firstname.getText().toString().trim();
                                            String lastna = lastname.getText().toString().trim();
                                            firstna = firstna.substring(0, 1).toUpperCase() + firstna.substring(1);
                                            lastna = lastna.substring(0, 1).toUpperCase() + lastna.substring(1);

                                            param.put("firstname", firstna);
                                            param.put("lastname", lastna);
                                            param.put("email", signinemailed.getText().toString().trim());
                                            param.put("password", signinpassed.getText().toString().trim());

                                            Intent intent = new Intent(Sign_Up.this, CreateProfile.class);
                                            intent.putExtra("profile_data", param.toString());
                                            intent.putExtra("logintype","0"); // app 0
                                            intent.putExtra("name", firstna);
                                            intent.putExtra("gender","");
                                            startActivity(intent);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }*/

                                    } else {
                                        Utility.showInternetError(Sign_In.this);
                                    }
                                } else {
                                    Utility.showToastMessage(Sign_In.this, "Please enter atleast 4 characters");
                                }
                            } else {
                                Utility.showToastMessage(Sign_In.this, "Please enter valid email address");
                            }
                        } else {
                            Utility.showToastMessage(Sign_In.this, "Please enter email address");
                        }
                    } else {
                        Utility.showToastMessage(Sign_In.this, "Please enter lastName");
                    }
                } else {
                    Utility.showToastMessage(Sign_In.this, "Please enter firstName");
                }
                break;

            case R.id.fbbtnone:
                AppConstants.hideKeyboard(Sign_In.this);
                SharedPref.getInstance().setSharedValue(Sign_In.this, PREF_GEST_LOGIN, false);
                try {
                    Utility.setEventTracking(Sign_In.this, "Create Profile page (first screen)", "SignUp with Facebook");

                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (Utility.isConnectingToInternet(Sign_In.this)) {
                    loginwithfacebooksignUp();
                } else {
                    Utility.showInternetError(Sign_In.this);
                }

                break;


            case R.id.txt_gestlogin:
                AppConstants.hideKeyboard(Sign_In.this);
                SharedPref.getInstance().setSharedValue(Sign_In.this, PREF_GEST_LOGIN, true);
                Intent homePage = new Intent(Sign_In.this, MainActivity.class);
                startActivity(homePage);
                finish();
                break;

            case R.id.txt_turms:
                AppConstants.hideKeyboard(Sign_In.this);
                Intent homeUrl = new Intent(Sign_In.this, In_App_Browser.class);
                homeUrl.putExtra("url", AppConstants.getDomain(true)+"terms-of-use.html");
                Log.d("geturl", AppConstants.getDomain(true)+"terms-of-use.html");
                startActivity(homeUrl);
                break;

            case R.id.txt_policy:
                AppConstants.hideKeyboard(Sign_In.this);
                Intent homeUrl1 = new Intent(Sign_In.this, In_App_Browser.class);
                homeUrl1.putExtra("url", AppConstants.getDomain(true)+"privacy-policy.html");
                Log.d("geturl", AppConstants.getDomain(true)+"privacy-policy.html");
                startActivity(homeUrl1);
                break;


        }
    }

    private void signInAPI() {
        String emailID = "";
        if (signin_email_ed.length() > 0) {
            emailID = signin_email_ed.getText().toString().trim().toLowerCase();
            Log.e("Email ID = ", "" + emailID);
            if (emailID.matches(AppConstants.emailPattern) || emailID.matches(AppConstants.emailPatternTwoDecimalPoint)) {
                if (signin_pwd_ed.length() >= 4) {
                    if (Utility.isConnectingToInternet(Sign_In.this)) {
                        try {
                            Utility.setEventTracking(Sign_In.this, "Login screen", AppConstants.EVENT_TRACKING_ID_LOGIN);
                            Map<String, String> params1 = new HashMap<String, String>();
                            params1.put("email", emailID);
                            params1.put("password", signin_pwd_ed.getText().toString().trim());
                            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(Sign_In.this, gcm_token))) {
                                params1.put("device_id", SharedPref.getInstance().getStringVlue(Sign_In.this, gcm_token));
                            } else {
                                params1.put("device_id", "");
                            }
                            params1.put("device_type", "0");
                            signin_btn.setEnabled(false);
                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                            RequestHandler.getInstance().stringRequestVolley(Sign_In.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(Sign_In.this, isStaging)) + login, params1, this, 0);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Utility.showInternetError(Sign_In.this);
                    }

                } else {
                    Utility.showToastMessage(Sign_In.this, "Please enter atleast 4 characters");
                }
            } else {
                Utility.showToastMessage(Sign_In.this, "Please enter valid email address");
            }
        } else {
            Utility.showToastMessage(Sign_In.this, "Please enter email address");
        }

    }

    private void loginwithfacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, mFacebookCallback);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(AppConstants.FACEBOOK_LOGIN_PERMISSIONS));


    }

    private void loginwithfacebooksignUp() {
        LoginManager.getInstance().registerCallback(callbackManager, mFacebookCallbackSignup);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(AppConstants.FACEBOOK_LOGIN_PERMISSIONS));

    }

    public void getFriends() {

        GraphRequest friendsRequest = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        Log.d(TAG, "FriendsList" + response.getJSONObject());
                        if (Utility.isNullCheck(response.getJSONObject().toString())) {
                            SharedPref.getInstance().setSharedValue(Sign_In.this, FACEBOOK_FRIENDS_LIST, response.getJSONObject().toString());
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields", AppConstants.FACEBOOK_FRIENDS_FIELDS);
        //friendsRequest.setParameters(parameters);
        friendsRequest.executeAsync();
    }


    private void loginWithAppLozic() {
        if (!(SharedPref.getInstance().getBooleanValue(this, AppConstants.SharedConstants.PREF_IS_CHAT_LOGIN))) {
            new ApplozicChat(this).loginWithApplozic();
        }
    }


    @Override
    public void successResponse(String successResponse, int flag) {


        /** flag == 0 login response
         *  flag == 1 forgetpassword response
         *    flag == 2 facebooklogin API  response
         *     flag == 4 forgotpassword API  response
         *     flag==5 signup
         */
        Log.e(TAG, "SuccessResponse" + successResponse);

        JSONObject jsonObject = null;

        if (!TextUtils.isEmpty(successResponse))

        {
            try {
                jsonObject = new JSONObject(successResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        switch (flag) {
            case 0:

                if (jsonObject != null) {

                    try {
                        if (jsonObject.optString(resultcheck).equals("true")) {
                            try {
                                ResponseHandler.getInstance().storeProfileInfo(Sign_In.this, jsonObject);  // Store ProfileInfo in sharedPrefernces.
                                SharedPref.getInstance().setSharedValue(Sign_In.this, islogin, true);

                                loginWithAppLozic();
                                Intent intent = new Intent(Sign_In.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utility.showToastMessage(Sign_In.this, getResources().getString(R.string.meg_login_failed));
                            signin_btn.setEnabled(true);
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;

            case 1:
                Utility.setEventTracking(Sign_In.this, "Login Screen", AppConstants.EVENT_TRACKING_ID_FORGOT);
                if (jsonObject != null) {
                    Utility.showToastMessage(Sign_In.this, getString(R.string.msg_forgot_password));
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                break;

            case 2:
                //fblogin is comming
                Utility.setEventTracking(Sign_In.this, "Login Screen", AppConstants.EVENT_TRACKING_ID_LOGIN_FACEBOOK);
                try {
                    if (jsonObject != null) {
                        if (jsonObject.optString(KEY_MSG).equals(KEY_NEW_USER)) {
                            if (fb_profile_obj != null) {
                                Intent i = new Intent(Sign_In.this, CreateProfile.class);
                                i.putExtra("logintype", "1"); // fb login flag ==1
                                i.putExtra("profile_data", fb_profile_obj.toString());
                                i.putExtra("name", fb_profile_obj.optString("name"));
                                i.putExtra("gender", fb_profile_obj.optString("gender"));
                                startActivity(i);
                                // finish();
                                progressWheel.stopAnimation();
                                progressWheel.setVisibility(View.GONE);
                            }
                        } else if (jsonObject.optString(KEY_MSG).equals(KEY_EXSIT_USER)) {

                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                            try {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("facebookid", fb_profile_obj.optString("id"));
                                params.put("image", fb_profile_obj.optString("image"));
                                if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(Sign_In.this, gcm_token))) {
                                    params.put("device_id", SharedPref.getInstance().getStringVlue(Sign_In.this, gcm_token));
                                } else {
                                    params.put("device_id", "");
                                }
                                //params.put("device_id", SharedPref.getInstance().getStringVlue(Sign_In.this, gcm_token));
                                params.put("device_type", "0");
                                RequestHandler.getInstance().stringRequestVolley(Sign_In.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(Sign_In.this, isStaging)) + facebooklogin, params, Sign_In.this, 3);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;


            case 3:

                if (jsonObject != null) {

                    try {

                        if (jsonObject.optString(resultcheck).equals("true")) {
                            try {
                                ResponseHandler.getInstance().storeProfileInfo(Sign_In.this, jsonObject);  // Store ProfileInfo in sharedPrefernces.
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            SharedPref.getInstance().setSharedValue(Sign_In.this, islogin, true);
                            loginWithAppLozic();
                            Intent intent = new Intent(Sign_In.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        } else {
                            Utility.showToastMessage(Sign_In.this, "" + jsonObject.optString("msg"));
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            signin_btn.setEnabled(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 4:
                Utility.setEventTracking(Sign_In.this, "Login Screen", AppConstants.EVENT_TRACKING_ID_FORGOT);
                if (jsonObject != null) {
                    Utility.showToastMessage(Sign_In.this, getString(R.string.msg_forgot_password));

                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                break;

            case 5:
                Utility.setEventTracking(Sign_In.this, "Login Screen", AppConstants.EVENT_TRACKING_ID_SIGN_UP_FACEBOOK);
                try {
                    if (jsonObject != null) {
                        Log.d("fbresponce",new Gson().toJson(jsonObject));
                        if (jsonObject.optString(KEY_MSG).equals(KEY_NEW_USER)) {
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            if (fb_profile_obj != null) {
                                Intent i = new Intent(Sign_In.this, CreateProfile.class);
                                Utility.setEventTracking(Sign_In.this, "Create Profile page (first screen)", "Next on Create Profile Screen");

                                i.putExtra("logintype", "1"); // fb login flag ==1
                                i.putExtra("profile_data", fb_profile_obj.toString());
                                i.putExtra("name", fb_profile_obj.optString("name"));
                                i.putExtra("gender", fb_profile_obj.optString("gender"));
                                startActivity(i);
                                finish();
                            }
                        } else if (jsonObject.optString(KEY_MSG).equals(KEY_EXSIT_USER)) {
                            Utility.showToastMessage(Sign_In.this, getString(R.string.user_alredy_exsit));
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 6:
                //   Utility.setEventTracking(Sign_Up.this,AppConstants.EVENT_TRACKING_ID_NEXT);
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                if (jsonObject != null && jsonObject.optString("msg").equals("available")) {
                    try {
                        JSONObject param = new JSONObject();
                        String firstna = firstname.getText().toString().trim();
                        String lastna = lastname.getText().toString().trim();
                        firstna = firstna.substring(0, 1).toUpperCase() + firstna.substring(1);
                        lastna = lastna.substring(0, 1).toUpperCase() + lastna.substring(1);
                        param.put("firstname", firstna);
                        param.put("lastname", lastna);
                        param.put("email", signinemailed.getText().toString().trim());
                        param.put("password", signinpassed.getText().toString().trim());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.showToastMessage(Sign_In.this, getString(R.string.msg_email_id_already_exist));
                }
                break;
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RuntimeAccess.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                //perms.put(android.Manifest.permission.WRITE_SETTINGS, PackageManager.PERMISSION_GRANTED);
                //  perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                //   perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                //   perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                //    perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);

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

//
                else {
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                    // Permission Denied
                    Toast.makeText(this, "Some Permission are denied", Toast.LENGTH_SHORT)
                            .show();
                }

                onSuccess(0);
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void successResponse(JSONObject jsonObject, int flag) {


        Log.e(TAG, "" + jsonObject.toString());


    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);

        switch (flag) {
            case 0:
                Utility.showToastMessage(Sign_In.this, getResources().getString(R.string.meg_login_failed));
                signin_btn.setEnabled(true);
                break;

        }

        signin_btn.setEnabled(true);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }


    private void forgotpasswordPopup() {
        dialog = new Dialog(Sign_In.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forgotpassword_dialog);

        // set the custom dialog components - text, image and button
        final CustomEditText emailid = (CustomEditText) dialog.findViewById(R.id.forgot_email_ed);
        final CustomButton dialogButton = (CustomButton) dialog.findViewById(R.id.forgotbtn);
        final AppCompatCheckBox am_beta_frgtpwd = (AppCompatCheckBox) dialog.findViewById(R.id.am_beta_frgtpwd);

        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.getInstance().setSharedValue(Sign_In.this, AppConstants.SharedConstants.isStaging, am_beta_frgtpwd.isChecked());
                AppConstants.getDomain(am_beta_frgtpwd.isChecked());
                if (emailid.length() > 0) {

                    Utility.setScreenTracking(Sign_In.this, "Forgot password screen");
                    if (emailid.getText().toString().matches(AppConstants.emailPattern) || emailid.getText().toString().matches(AppConstants.emailPatternTwoDecimalPoint)) {

                        try {
                            Map<String, String> param = new HashMap<String, String>();
                            param.put("email", emailid.getText().toString().trim());
                            RequestHandler.getInstance().stringRequestVolley(Sign_In.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(Sign_In.this, isStaging)) + forgotpassword, param, Sign_In.this, 1);
                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                            dialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Utility.showToastMessage(Sign_In.this, "Please enter valid email address");
                    }
                } else

                {
                    Utility.showToastMessage(Sign_In.this, "Please enter email address");
                }
            }
        });

        dialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccess(int milis) {


    }
}