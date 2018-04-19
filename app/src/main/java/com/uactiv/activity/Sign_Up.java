package com.uactiv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.controller.ResponseListener;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Sign_Up extends AppCompatActivity implements View.OnClickListener, AppConstants.urlConstants, ResponseListener, AppConstants.SharedConstants {

    String TAG = getClass().getSimpleName();
    EditText signinemailed;
    EditText signinpassed;
    EditText firstname = null;
    EditText lastname = null;
    Button signin = null;
    Button signup = null;
    CustomButton fbbtn = null;
    CallbackManager callbackManager;
    JSONObject fb_profile_obj = null;
    GifImageView progressWheel = null;
    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
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
                                    RequestHandler.getInstance().stringRequestVolley(Sign_Up.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(Sign_Up.this,isStaging)) + checkfacebookid, params, Sign_Up.this, 0);
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
        setContentView(R.layout.signup);
        callbackManager = CallbackManager.Factory.create();
        initViews();
    }

    private void initViews() {
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(Sign_Up.this, progressWheel);
        signinemailed = (EditText) findViewById(R.id.signin_email_ed);
        signinpassed = (EditText) findViewById(R.id.signin_pwd_ed);
        firstname = (EditText) findViewById(R.id.fname);
        lastname = (EditText) findViewById(R.id.lname);
        ((Button) findViewById(R.id.signin)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });


        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(this);
        fbbtn = (CustomButton) findViewById(R.id.fbbtn);
        fbbtn.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.signup:
                Utility.setScreenTracking(Sign_Up.this, AppConstants.SCREEN_TRACKING_ID_CREATEPROFILEPAGE);

                if (firstname.length() > 0) {

                    if (lastname.length() > 0) {

                        if (signinemailed.length() > 0) {
//                            if (signinemailed.getText().toString().trim().matches(AppConstants.emailPattern)) {
                            if (signinemailed.getText().toString().trim().matches(AppConstants.emailPattern) || signinemailed.getText().toString().trim().matches(AppConstants.emailPatternTwoDecimalPoint)) {
                                if (signinpassed.length() >= 4) {
                                    if (Utility.isConnectingToInternet(Sign_Up.this)) {
                                        try {
                                            Map<String, String> stringMap = new HashMap<>();
                                            stringMap.put("email", signinemailed.getText().toString().trim());
                                            progressWheel.setVisibility(View.VISIBLE);
                                            progressWheel.startAnimation();
                                            RequestHandler.getInstance().stringRequestVolley(Sign_Up.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(Sign_Up.this,isStaging)) + checkEmail, stringMap, this, 1);
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
                                        Utility.showInternetError(Sign_Up.this);
                                    }
                                } else {
                                    Utility.showToastMessage(Sign_Up.this, "Please enter atleast 4 characters");
                                }
                            } else {
                                Utility.showToastMessage(Sign_Up.this, "Please enter valid email address");
                            }
                        } else {
                            Utility.showToastMessage(Sign_Up.this, "Please enter email address");
                        }
                    } else {
                        Utility.showToastMessage(Sign_Up.this, "Please enter lastName");
                    }
                } else {
                    Utility.showToastMessage(Sign_Up.this, "Please enter firstName");
                }
                break;

            case R.id.fbbtn:
                try {
                    Utility.setEventTracking(Sign_Up.this,"Create Profile page (first screen)","SignUp with Facebook");

                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (Utility.isConnectingToInternet(Sign_Up.this)) {
                    loginwithfacebook();
                } else {
                    Utility.showInternetError(Sign_Up.this);
                }

                break;
        }


    }

    private void loginwithfacebook() {

        LoginManager.getInstance().registerCallback(callbackManager, mFacebookCallback);

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(AppConstants.FACEBOOK_LOGIN_PERMISSIONS));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void successResponse(String successResponse, int flag) {


        /** flag == 0 check facebookid API response
         *flag == 1 check checkEmailID API response
         */
        JSONObject mJsonObject = null;
        try {
            mJsonObject = new JSONObject(successResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (flag) {

            case 0:
                Utility.setEventTracking(Sign_Up.this,"Login Screen", AppConstants.EVENT_TRACKING_ID_SIGN_UP_FACEBOOK);
                try {
                    if (mJsonObject != null) {
                        if (mJsonObject.optString(KEY_MSG).equals(KEY_NEW_USER)) {
                            if (fb_profile_obj != null) {
                                Intent i = new Intent(Sign_Up.this, CreateProfile.class);
                                Utility.setEventTracking(Sign_Up.this,"Create Profile page (first screen)","Next on Create Profile Screen");

                                i.putExtra("logintype", "1"); // fb login flag ==1
                                i.putExtra("profile_data", fb_profile_obj.toString());
                                i.putExtra("name", fb_profile_obj.optString("name"));
                                i.putExtra("gender", fb_profile_obj.optString("gender"));

                                startActivity(i);
                                finish();
                            }
                        } else if (mJsonObject.optString(KEY_MSG).equals(KEY_EXSIT_USER)) {
                            Utility.showToastMessage(Sign_Up.this, getString(R.string.user_alredy_exsit));
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
             //   Utility.setEventTracking(Sign_Up.this,AppConstants.EVENT_TRACKING_ID_NEXT);
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                if (mJsonObject != null && mJsonObject.optString("msg").equals("available")) {
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
                        Intent intent = new Intent(Sign_Up.this, CreateProfile.class);
                        intent.putExtra("profile_data", param.toString());
                        intent.putExtra("logintype", "0"); // app 0
                        intent.putExtra("name", firstna);
                        intent.putExtra("gender", "");
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.showToastMessage(Sign_Up.this, getString(R.string.msg_email_id_already_exist));
                }
                break;
        }

    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {


    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }

    public void getFriends() {

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        Log.d(TAG, "FriendsList" + response.getJSONObject());
                        if (Utility.isNullCheck(response.getJSONObject().toString())) {
                            SharedPref.getInstance().setSharedValue(Sign_Up.this, FACEBOOK_FRIENDS_LIST, response.getJSONObject().toString());
                        }

                        /*new GraphRequest(
                                AccessToken.getCurrentAccessToken(),
                                "/{user-id}/picture",
                                null,
                                HttpMethod.GET,
                                new GraphRequest.Callback() {
                                    public void onCompleted(GraphResponse response) {
            *//* handle the result *//*
                                    }
                                }
                        ).executeAsync();
*/
                    }
                }
        ).executeAsync();


    }
}
