package com.uactiv.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.felipecsl.gifimageview.library.GifImageView;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.adapter.MutualFriendAdapter;
import com.uactiv.applozicchat.ApplozicChat;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.Home;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.MutualFriendData;
import com.uactiv.model.SkillDo;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.OnRecyclerItemClickListener;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import com.uactiv.model.MutualFriendModel;

public class BuddyUpDetailsActivity extends Activity implements OnClickListener,
        AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener {

    private String TAG = "BuddyUpDetailsActivity";
    public static Activity buddyupDetailsActivity = null;
    private CustomButton bottomLayout;
    private CheckBox checkBoxAddFav = null;
    private Intent intent = null;
    private GifImageView progressWheel = null;
    private BuddyModel buddyDetails = null;
    boolean isFav = false;
    boolean isView = true;
    boolean isFromFav = false;
    // private ArrayList<MutualFriendModel> mMutualFriendsList = new ArrayList<>();
    private ImageView imgRateOne = null;
    private ImageView imgRateTwo = null;
    private ImageView imgRateThree = null;
    private ImageView imgRateFour = null;
    private TextView tvSquash = null;
    private TextView tvParkour = null;
    private TextView tvYoga = null;
    private TextView tvactivtyfour = null;
    private CustomTextView tvPersonNames = null;
    private CustomTextView tvabout = null;
    private CustomTextView tvEarly = null;
    private CustomTextView tvDistace = null;
    private CustomTextView tvProfile = null;
    private ImageView badgeImage = null;
    private CustomTextView tvRating = null;
    private RecyclerView mMutualFriends = null;
    private CustomTextView tvFacebookSize = null;
    private String checkRating;
    CallbackManager callbackManager;
    ArrayList<MutualFriendData> mutualFriendList = new ArrayList<>();
    String fbuserid = null;
    String iuserid;
    String intentFav;
    RecyclerView mutual_recycle;
    String away;
    ApplozicChat applozicChat;
    String ismyFav = "";
    boolean isfav = false;
    boolean ismf = false;
    String push;
    String mytokan = "";

    //9833787060

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.buddyup_details);

        buddyupDetailsActivity = BuddyUpDetailsActivity.this;
        bindView();
        if (!AppConstants.isGestLogin(BuddyUpDetailsActivity.this)) {
            Utility.setScreenTracking(this, AppConstants.SCREEN_TRACKING_ID_BUDDYUPDETAILS);
        } else {
            Utility.setScreenTracking(this, "Guest login Buddy Profile details");
        }
        try {
            mytokan = AccessToken.getCurrentAccessToken().getToken();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        intent = getIntent();


        if (intent != null) {

            if (intent.getStringExtra("pickupevent") != null) {
                bindActivityPickUp();
            } else if (intent.getStringExtra("fevscreen") != null) {
                bindActivityPickUp();
            } else if (intent.getStringExtra("fromSelf") == null) {
                bindActivity();
            } else {


                if (Utility.isConnectingToInternet(BuddyUpDetailsActivity.this)) {

                    try {
                        progressWheel.setVisibility(View.VISIBLE);
                        progressWheel.startAnimation();
                        Map<String, String> param = new HashMap<>();
                        param.put("iduser", this.getIntent().getStringExtra("userfbid"));
                        param.put("favourite_userid", SharedPref.getInstance().getStringVlue(BuddyUpDetailsActivity.this, userId));
                        param.put("access_token", mytokan);
                        //  param.put("user_id", SharedPref.getInstance().getStringVlue(BuddyUpDetailsActivity.this, userId));
                        Log.d("param", "" + param.toString());
                        RequestHandler.getInstance().stringRequestVolley(BuddyUpDetailsActivity.this,
                                AppConstants.getBaseUrl(SharedPref.getInstance()
                                        .getBooleanValue(BuddyUpDetailsActivity.this, isStaging)) + userinfo,
                                param, this, 1);// getUserInfo
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Utility.showInternetError(BuddyUpDetailsActivity.this);
                }

                /* RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                StringRequest sr = new StringRequest(Request.Method.POST,AppConstants.getBaseUrlFacebook(SharedPref.getInstance().getBooleanValue(BuddyUpDetailsActivity.this, isStaging)) + facebookUser, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    Log.d("getresp",response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("fb_id", BuddyUpDetailsActivity.this.getIntent().getStringExtra("userfbid"));

                        return map;
                    }


                };
                queue.add(sr);*/

                bindActivityMF();


            }

        }

    }

    /**
     * bind activity view
     */
    private void bindActivity() {
        if (intent != null) {
            buddyDetails = (BuddyModel) getIntent().getSerializableExtra(serialKeyBuddy);
            isView = intent.getBooleanExtra("view", false);
            isFromFav = intent.getBooleanExtra("isFromFav", false);

            checkRating = intent.getExtras().getString("checkRating");
            intentFav = intent.getStringExtra("isfav");


            if (intentFav != null && !intentFav.isEmpty()) {
                if (intentFav.equals("1")) {
                    isFav = true;
                    checkBoxAddFav.setChecked(true);
                } else {
                    isFav = false;
                    checkBoxAddFav.setChecked(false);
                }
            }


            Bundle bundle = this.getIntent().getExtras();
            if (bundle != null) {
                away = bundle.getString("awaylocation");
                if (bundle.getString("userid") != null) {
                    getBuddyUserInfo(bundle.getString("userid"));
                    Utility.setScreenTracking(BuddyUpDetailsActivity.this, "Buddy up detail from Notifications.");
                }

                Log.d("awaylocation", away + "");
            }

            try {
                getBuddyUserInfo(buddyDetails.getUserid());
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }


            try {
                if (AccessToken.getCurrentAccessToken() != null) {
                    if (buddyDetails.getFacebookId().equalsIgnoreCase("0") || buddyDetails.getFacebookId().equalsIgnoreCase("")) {
                        mutual_recycle.setVisibility(View.GONE);
                        tvFacebookSize.setVisibility(View.GONE);
                    } else {
                        fbuserid = buddyDetails.getFacebookId();
                        setFacebookData();
                    }

                } else {
                    mutual_recycle.setVisibility(View.GONE);
                    tvFacebookSize.setVisibility(View.GONE);
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
                mutual_recycle.setVisibility(View.GONE);
                tvFacebookSize.setVisibility(View.GONE);
            }


        }


        if (isView) {
            checkBoxAddFav.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            //getBuddyUserInfo(intent.getStringExtra("userid"));
        } else {
            applyBusinessAccountLogic();
        }

        if (isFromFav) {
            checkBoxAddFav.setVisibility(View.GONE);
        }
    }

    private void bindActivityPickUp() {
        if (intent != null) {
            isView = intent.getBooleanExtra("view", false);
            getBuddyUserInfo(intent.getStringExtra("userid"));
            try {
                if (SharedPref.getInstance().getStringVlue(BuddyUpDetailsActivity.this, userId).equalsIgnoreCase(intent.getStringExtra("userid"))) {
                    bottomLayout.setVisibility(View.GONE);
                } else {
                    bottomLayout.setVisibility(View.VISIBLE);
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

        }


        if (isView) {
            // checkBoxAddFav.setVisibility(View.GONE);
            //  bottomLayout.setVisibility(View.GONE);
            //getBuddyUserInfo(intent.getStringExtra("userid"));
        } else {
            applyBusinessAccountLogic();
        }


    }

    private void bindActivityMF() {
        if (intent != null) {
            if (intent.getStringExtra("userfbid") != null) {
                fbuserid = this.getIntent().getStringExtra("userfbid");
                setFacebookData();
                bottomLayout.setVisibility(View.VISIBLE);
                ismf = true;

            }
        }

        if (isView) {
            //  checkBoxAddFav.setVisibility(View.GONE);
            //   bottomLayout.setVisibility(View.GONE);

        } else {
            applyBusinessAccountLogic();
        }

        if (isFromFav) {
            checkBoxAddFav.setVisibility(View.GONE);
            tvDistace.setVisibility(View.GONE);
        }

    }

    /**
     * show tutorial for buddyup request
     */

    public void showBuddyUpRequestTutorial() {
        ShowcaseView mShowcaseViewCreatePickup = new ShowcaseView.Builder(BuddyUpDetailsActivity.this)
                .setTarget(new ViewTarget(bottomLayout))
                .setContentText(getResources().getString(R.string.tutorial_msg_view_buddy_request))
                .setContentTextPaint(Utility.getTextPaint(BuddyUpDetailsActivity.this))
                .singleShot(AppConstants.TUTORIAL_BUDDY_UP_VIEW_ID)
                .hideOnTouchOutside()
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showFavTutorial();
                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                })
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);
        mShowcaseViewCreatePickup.setButtonPosition(lps);
        mShowcaseViewCreatePickup.setButtonText(getResources().getString(R.string.tutorial_got_it));
    }


    /**
     * show fav button tutorial
     */

    public void showFavTutorial() {
        ShowcaseView mShowcaseViewCreatePickup = new ShowcaseView.Builder(BuddyUpDetailsActivity.this)
                .setTarget(new ViewTarget(checkBoxAddFav))
                .setContentText(getString(R.string.tutorial_msg_fav))
                .setContentTextPaint(Utility.getTextPaint(BuddyUpDetailsActivity.this))
                .singleShot(AppConstants.TUTORIAL_ADD_FAV_ID)
                .hideOnTouchOutside()
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        mShowcaseViewCreatePickup.setButtonText(getResources().getString(R.string.tutorial_got_it));

    }


    /**
     * get buddyup list for given userId
     *
     * @param userID user id from intent
     */


    private void getBuddyUserInfo(String userID) {

        if (Utility.isConnectingToInternet(BuddyUpDetailsActivity.this)) {

            try {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                Map<String, String> param = new HashMap<>();
                param.put("iduser", "" + userID);
                param.put("access_token", mytokan);
                param.put("favourite_userid", SharedPref.getInstance().getStringVlue(BuddyUpDetailsActivity.this, userId));
                Log.d("param", "" + param.toString());
                RequestHandler.getInstance().stringRequestVolley(BuddyUpDetailsActivity.this,
                        AppConstants.getBaseUrl(SharedPref.getInstance()
                                .getBooleanValue(BuddyUpDetailsActivity.this, isStaging)) + userinfo,
                        param, this, 1);// getUserInfo
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(BuddyUpDetailsActivity.this);
        }

    }


    /**
     * apply business account restriction if he views user profile
     */
    private void applyBusinessAccountLogic() {

        if (SharedPref.getInstance().getBooleanValue(BuddyUpDetailsActivity.this, isbussiness)) {
            bottomLayout.setVisibility(View.GONE);
            checkBoxAddFav.setVisibility(View.VISIBLE);
            showFavTutorial(); // this is to show tutorial for business user to add fav
        } else {
            showBuddyUpRequestTutorial();
        }
    }


    private void setFacebookData() {
        //Get the all mutual friends
        // Log.i("FB ID==>", buddyDetails.getFacebookId());
        Log.d(" AccessToken", AccessToken.getCurrentAccessToken().getToken());
        Log.d("userfbid", fbuserid);

        // getMutualfriendApi(AccessToken.getCurrentAccessToken().getToken() + "", fbuserid);
        // if (!buddyDetails.getFacebookId().equalsIgnoreCase("0")) {
//        buddyDetails.getFacebookId();
        Bundle params = new Bundle();
        params.putString("fields", "context.fields(all_mutual_friends)");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "104581016747364",
                // buddyDetails.getFacebookId(),
                params,
                com.facebook.HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //   setMutualFriendAdapter(response);

                    }
                }
        ).executeAsync();
        // }
    }

    private void bindView() {
        Utility.setEventTracking(getApplicationContext(), "", AppConstants.SCREEN_TRACKING_ID_BUDDYUPDETAILS);

        tvPersonNames = (CustomTextView) findViewById(R.id.tvPersonNames);
        tvEarly = (CustomTextView) findViewById(R.id.tvEarly);
        tvabout = (CustomTextView) findViewById(R.id.tvBuddyDetails);
        tvPersonNames.setShadowLayer(10, 0, 0, ContextCompat.getColor(this, R.color.black));
        bottomLayout = (CustomButton) findViewById(R.id.bottomBarText);
        mMutualFriends = (RecyclerView) findViewById(R.id.mutual_recycle);
        tvFacebookSize = (CustomTextView) findViewById(R.id.tvFacebookSize);
        mMutualFriends.setLayoutManager(new LinearLayoutManager(BuddyUpDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(BuddyUpDetailsActivity.this, progressWheel);
        badgeImage = (ImageView) findViewById(R.id.imageView4);
        checkBoxAddFav = (CheckBox) findViewById(R.id.imageView2);
        checkBoxAddFav.setOnClickListener(this);
        tvProfile = (CustomTextView) findViewById(R.id.tvProfile);
        tvSquash = (TextView) findViewById(R.id.tvSquash);
        tvParkour = (TextView) findViewById(R.id.tvParkour);
        tvYoga = (TextView) findViewById(R.id.tvYoga);
        tvactivtyfour = (TextView) findViewById(R.id.tvRockClimbing);

        tvRating = (CustomTextView) findViewById(R.id.tvRating);
        imgRateOne = (ImageView) findViewById(R.id.rate_img_one);
        imgRateTwo = (ImageView) findViewById(R.id.rate_img_two);
        imgRateThree = (ImageView) findViewById(R.id.rate_img_three);
        imgRateFour = (ImageView) findViewById(R.id.rate_img_four);
        tvDistace = (CustomTextView) findViewById(R.id.tvDistace);

        mutual_recycle = (RecyclerView) findViewById(R.id.mutual_recycle);

        tvSquash.setVisibility(View.GONE);
        tvParkour.setVisibility(View.GONE);
        tvYoga.setVisibility(View.GONE);
        tvactivtyfour.setVisibility(View.GONE);
        mutual_recycle.setVisibility(View.GONE);
        tvFacebookSize.setVisibility(View.GONE);

        bottomLayout.setOnClickListener(this);
        badgeImage.setOnClickListener(this);
        findViewById(R.id.imageView1).setOnClickListener(this);


        if (!isView) {
            if (intent != null) {
                if (intent.getStringExtra("fromSelf") == null) {
                    displayUserInfo();
                } else {

                }

            }

        }

    }


    /**
     * display user info on view
     */

    private void displayUserInfo() {

        if (Utility.isNullCheck(buddyDetails.getImage())) {
            Utility.setImageUniversalLoader(BuddyUpDetailsActivity.this,
                    buddyDetails.getImage(), (ImageView) findViewById(R.id.imageView3));
        }

        if (!buddyDetails.getIsfav().equals("")) {
            if (buddyDetails.getIsfav().equals("true")) {
                isFav = true;
                checkBoxAddFav.setChecked(true);
            } else {
                isFav = false;
                checkBoxAddFav.setChecked(false);
            }
        }


        if (Utility.isNullCheck(buddyDetails.getName())) {
            tvPersonNames.setText(buddyDetails.getName());
        }
        if (isFromFav == true) {
            tvDistace.setVisibility(View.GONE);
        } else if (Utility.isNullCheck(away)) {


            tvDistace.setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.imgDistance)).setVisibility(View.GONE);
            tvDistace.setText(away);
        } else {
            tvDistace.setText("0 km away");
            tvDistace.setVisibility(View.VISIBLE);
        }


        if (Utility.isNullCheck(buddyDetails.getAge())) {
            Date birthday = null;
            try {
                birthday = AppConstants.sdf.parse(buddyDetails.getAge());
                tvEarly.setText(Utility.calculateAge(birthday, BuddyUpDetailsActivity.this, false));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        if (Utility.isNullCheck(buddyDetails.getAbout_yourself())) {
            tvabout.setText(StringEscapeUtils.unescapeJava(buddyDetails.getAbout_yourself()));
        } else {
            tvabout.setVisibility(View.GONE);
        }


        if (Utility.isNullCheck(buddyDetails.getBadge())) {
            tvProfile.setText(buddyDetails.getBadge());
            Utility.updateBadge(buddyDetails.getBadge(), badgeImage);
        }


        try {
            BigDecimal rating = Utility.round(buddyDetails.getRating(), buddyDetails.getRating_count());

            if (rating.compareTo(BigDecimal.ZERO) > 0) {
                int res = rating.compareTo(new BigDecimal("3.0"));

                if (res == 0 || res == 1) {
                    tvRating.setText("User Rating " + rating);
                    tvRating.setVisibility(View.VISIBLE);
                } else {
                    tvRating.setVisibility(View.GONE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        displayUserInterestWithRate();

    }


    /**
     * display rating with respective activity
     */

    private void displayUserInterestWithRate() {

        try {

            for (int i = 0; i < buddyDetails.getSkillDo().size(); i++) {

                SkillDo skillDo = buddyDetails.getSkillDo().get(i);

                switch (i) {
                    case 0:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvSquash.setVisibility(View.VISIBLE);
                            tvSquash.setText(skillDo.getActivty());
                        } else {
                            tvSquash.setVisibility(View.GONE);
                        }
                        setRatingImageResource(skillDo.getLevel(), imgRateOne);
                        break;
                    case 1:

                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvParkour.setVisibility(View.VISIBLE);
                            tvParkour.setText(skillDo.getActivty());
                        } else {
                            tvParkour.setVisibility(View.GONE);
                        }
                        setRatingImageResource(skillDo.getLevel(), imgRateTwo);

                        break;
                    case 2:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvYoga.setVisibility(View.VISIBLE);
                            tvYoga.setText(skillDo.getActivty());
                        } else {
                            tvYoga.setVisibility(View.INVISIBLE);
                        }
                        setRatingImageResource(skillDo.getLevel(), imgRateThree);

                        break;
                    case 3:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvactivtyfour.setVisibility(View.VISIBLE);
                            tvactivtyfour.setText(skillDo.getActivty());
                        } else {
                            tvactivtyfour.setVisibility(View.INVISIBLE);
                        }
                        setRatingImageResource(skillDo.getLevel(), imgRateFour);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * set rate star image according to the rating
     *
     * @param level     how much
     * @param imageView target to set image
     */
    private void setRatingImageResource(String level, ImageView imageView) {
        if (Utility.isNullCheck(level)) {

            switch (level) {
                case "1":

                    imageView.setImageResource(R.drawable.one_star);
                    break;
                case "2":
                    imageView.setImageResource(R.drawable.two_star);
                    break;
                case "3":
                    imageView.setImageResource(R.drawable.three_star);
                    break;
            }
        } else {
            imageView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // optional depending on your needs
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView2:
                if (!AppConstants.isGestLogin(BuddyUpDetailsActivity.this)) {
                    if (Utility.isConnectingToInternet(BuddyUpDetailsActivity.this)) {
                        try {
                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                            Map<String, String> param = new HashMap<>();
                            if (checkBoxAddFav.isChecked()) {
                                param.put("isfav", "1");
                                isFav = true;
                            } else {
                                param.put("isfav", "0");
                                isFav = false;
                            }
                            param.put("favourites", buddyDetails.getUserid());
                            param.put("iduser", SharedPref.getInstance().getStringVlue(BuddyUpDetailsActivity.this, userId));
                            RequestHandler.getInstance().stringRequestVolley(BuddyUpDetailsActivity.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(BuddyUpDetailsActivity.this, isStaging)) + addfavourites, param, this, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Utility.showInternetError(BuddyUpDetailsActivity.this);
                    }
                } else {
                    checkBoxAddFav.setChecked(false);
                    AppConstants.loginDialog(BuddyUpDetailsActivity.this);
                }

                break;
            case R.id.imageView4:


                if (!AppConstants.isGestLogin(BuddyUpDetailsActivity.this)) {
                    Utility.setEventTracking(BuddyUpDetailsActivity.this, "Buddy Profile details", "Badge Button on Buddy profile detail screen");
                } else {
                    Utility.setEventTracking(BuddyUpDetailsActivity.this, "Buddy Profile details", "Badge Button on Guest login Buddy profile detail screen");
                }

                final Dialog badgeDialog = new Dialog(BuddyUpDetailsActivity.this, R.style.Theme_Dialog);
                badgeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                badgeDialog.setContentView(R.layout.badge_details);
                RelativeLayout dismissalLayout = (RelativeLayout) badgeDialog.findViewById(R.id.mainLay);
                dismissalLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        badgeDialog.dismiss();
                        return false;
                    }
                });
                badgeDialog.setCanceledOnTouchOutside(true);
                badgeDialog.show();
                break;
            case R.id.bottomBarText:
                if (!AppConstants.isGestLogin(BuddyUpDetailsActivity.this)) {
                    if (buddyDetails != null) {
                        if (buddyDetails.getIsreceivebuddyrequest().equals("1")) {
                            if (!AppConstants.isGestLogin(BuddyUpDetailsActivity.this)) {
                                Utility.setEventTracking(BuddyUpDetailsActivity.this, "Buddy Profile details", "Buddy Up on Buddy profile detail screen");
                            } else {
                                Utility.setEventTracking(BuddyUpDetailsActivity.this, "Buddy Profile details", "Buddy Up on Guest login Buddy profile detail screen");
                            }

                            Intent buddyUpRequest_intent = new Intent(BuddyUpDetailsActivity.this, BuddyUpRequest.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(serialKeycreateBuddy, buddyDetails);
                            buddyUpRequest_intent.putExtras(bundle);
                            startActivity(buddyUpRequest_intent);
                        } else {
                            Utility.showToastMessage(BuddyUpDetailsActivity.this, "User is not accepting Buddy Up request");
                        }
                    }
                } else {
                    AppConstants.loginDialog(BuddyUpDetailsActivity.this);
                }

                break;
            case R.id.imageView1:
                if (!AppConstants.isGestLogin(BuddyUpDetailsActivity.this)) {
                    Utility.setEventTracking(BuddyUpDetailsActivity.this, "Buddy Profile details", "Back arrow on Buddy profile detail screen");
                } else {
                    Utility.setEventTracking(BuddyUpDetailsActivity.this, "Buddy Profile details", "Back arrow on Guest login Buddy profile detail screen");
                }


                onBackPressed();
                break;
        }
    }

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {

        /** flag == 0 AddFavorite API response
         *	flag == 1 UserInfoModel API API response
         *  flag == 2 Facebook MF API response
         *  flag == 3 server MF API response
         */
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        Log.d("myflag", flag + "");

        try {
            if (flag == 2) {
                jsonArray = new JSONArray(successResponse);
            } else if (flag == 5) {
                jsonArray = new JSONArray(successResponse);
            } else {
                jsonObject = new JSONObject(successResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressWheel.stopAnimation();
            progressWheel.setVisibility(View.GONE);
        }


        switch (flag) {
            case 0:
                Utility.setEventTracking(BuddyUpDetailsActivity.this, "Buddy Profile details", AppConstants.EVENT_TRACKING_ID_ADD_FAV);

                if (jsonObject != null) {
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        if (isFav) {
                            buddyDetails.setIsfav("1");
                            checkBoxAddFav.setChecked(true);
                            Utility.showToastMessage(BuddyUpDetailsActivity.this, getString(R.string.msg_favourite_added));
                            Utility.setEventTracking(getApplicationContext(), "Buddy Profile details", AppConstants.EVENT_TRACKING_ID_ADD_FAV);
                        } else {
                            buddyDetails.setIsfav("0");
                            checkBoxAddFav.setChecked(false);
                            Utility.showToastMessage(BuddyUpDetailsActivity.this, getString(R.string.msg_favourite_removed));
                            Utility.setEventTracking(getApplicationContext(), "Buddy Profile details", "Removed Buddyup from fevorites screen");
                        }

                      //  Home.notifiyArrayListChange.getBuddyModelAddedItems(intent.getIntExtra("position", 0), buddyDetails);
                        //Should be updatable from API need respnse as true or false
                    } else {
                        Utility.showToastMessage(BuddyUpDetailsActivity.this, jsonObject.optString(KEY_MSG));
                    }
                }

                break;

            case 1:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        getBuddyDetailsFromAPI(jsonObject.optJSONObject(KEY_DETAIL));

                        if (intent.getStringExtra("pickupevent") != null || intent.getStringExtra("fevscreen") != null) {

                            String userids = intent.getStringExtra("userid").toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");

                            String[] singleUser = userids.split(",");
                            applozicChat = new ApplozicChat(BuddyUpDetailsActivity.this);
                            for (int i = 0; i < singleUser.length; i++) {
                                //   applozicChat.addParticipant(singleUser[i], Integer.valueOf(getConversationsId()));
                            }

                            try {
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    if (buddyDetails.getFacebookId().equalsIgnoreCase("0") || buddyDetails.getFacebookId().equalsIgnoreCase("")) {
                                        mutual_recycle.setVisibility(View.GONE);
                                        tvFacebookSize.setVisibility(View.GONE);
                                    } else {
                                        fbuserid = buddyDetails.getFacebookId();
                                        setFacebookData();

                                    }

                                } else {
                                    mutual_recycle.setVisibility(View.GONE);
                                    tvFacebookSize.setVisibility(View.GONE);
                                }
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                                mutual_recycle.setVisibility(View.GONE);
                                tvFacebookSize.setVisibility(View.GONE);
                            }

                        }
                        //  setMutualFriendsAdapter();

                    }
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                }
                break;
            case 2:
                try {
                    if (jsonArray != null && jsonArray.length() > 0) {
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                        Log.i("FB getUser -->", "" + jsonArray.toString());
                        //Buddy Details get  ==> Inet

                        Intent intent = null;
                        intent = new Intent(this, BuddyUpDetailsActivity.class);
//                    Log.e("RatingJson *** ", "" + ((MainActivity) this).checkRatingJson);
//                    intent.putExtra("checkRating", "" + ((MainActivity) this).checkRatingJson);
//                    intent.putExtra("badgetype", buddylist.get(position).getImage() + "");
//                    intent.putExtra("isfav", buddylist.get(position).getIsfav() + "");
//                    intent.putExtra("position", position);
//                    intent.putExtra("userid", jsonArray.get(0).getUserid() + "");
                        ArrayList<BuddyModel> buddyList = new ArrayList<BuddyModel>();
                        buddyList = ResponseHandler.getInstance().storeBuddyList(jsonArray);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(serialKeyBuddy, buddyList.get(0));
                        intent.putExtras(bundle);
                        this.startActivity(intent);
                        ((Activity) this).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 3:
                JSONObject mutualObject = new JSONObject(successResponse);
                Log.d("****", fbuserid = "");
                MutualFriendData mutualFriend = null;
                Log.d("mutualfriends", new Gson().toJson(mutualObject));
                if (mutualObject.getJSONObject("data").has("context")) {
                    if (mutualObject.getJSONObject("data").getJSONObject("context").has("all_mutual_friends")) {
                        mutual_recycle.setVisibility(View.VISIBLE);
                        tvFacebookSize.setVisibility(View.VISIBLE);
                        JSONArray mutualfriends = new JSONArray(mutualObject.getJSONObject("data").getJSONObject("context").getString("all_mutual_friends"));
                        tvFacebookSize.setText("Mutual Friends (" + mutualfriends.length() + ")");
                        for (int i = 0; i < mutualfriends.length(); i++) {
                            mutualFriend = new MutualFriendData();
                            String url = mutualfriends.getJSONObject(i).getJSONObject("picture").getString("url");
                            mutualFriend.setName(mutualfriends.getJSONObject(i).getString("name"));
                            if (mutualfriends.getJSONObject(i).has("id")) {
                                mutualFriend.setId(mutualfriends.getJSONObject(i).getString("id"));
                            } else {
                                mutualFriend.setId("0");
                            }

                            mutualFriend.setUrl(url);
                            mutualFriendList.add(mutualFriend);
                        }

                        Log.d("mymutual", new Gson().toJson(mutualFriendList));
                        if (mutualFriendList.size() > 0) {
                            MutualFriendAdapter mutualFriendAdapter = new MutualFriendAdapter(this, mutualFriendList);
                            mMutualFriends.setAdapter(mutualFriendAdapter);
                            mutualFriendAdapter.setmItemClickListener(new OnRecyclerItemClickListener() {
                                @Override
                                public void onRecyclerItemClick(int position) {
                                    Log.i("Recycle MF==>", "Clicked");
                                    //Implement API(http://54.254.173.51/app/index.php/FacebookUser/getuser)

                                    try {
                                        if (!mutualFriendList.get(position).getId().equalsIgnoreCase("0")) {
                                            Intent pass = new Intent(BuddyUpDetailsActivity.this, BuddyUpDetailsActivity.class);
                                            pass.putExtra("userfbid", mutualFriendList.get(position).getId());
                                            pass.putExtra("fromSelf", "fromSelf");
                                            startActivity(pass);
                                        } else {
                                            Toast.makeText(BuddyUpDetailsActivity.this, "Not a UACTIV member", Toast.LENGTH_LONG).show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            mutual_recycle.setVisibility(View.GONE);
                            tvFacebookSize.setVisibility(View.GONE);
                        }


                    }
                } else {
                    mutual_recycle.setVisibility(View.GONE);
                    tvFacebookSize.setVisibility(View.GONE);
                }


                break;


            case 4:

                JSONArray userArray = new JSONArray(successResponse);

                String iduser = userArray.getJSONObject(0).getString("iduser");
                String firstname = userArray.getJSONObject(0).getString("firstname");
                String lastname = userArray.getJSONObject(0).getString("lastname");
                String email = userArray.getJSONObject(0).getString("email");
                String age = userArray.getJSONObject(0).getString("age");
                String image = userArray.getJSONObject(0).getString("image");
                String about_yourself = userArray.getJSONObject(0).getString("about_yourself");
                String badge = userArray.getJSONObject(0).getString("badge");
                String facebookid = userArray.getJSONObject(0).getString("facebookid");
                String user_type = userArray.getJSONObject(0).getString("user_type");

                int rated_count = Integer.parseInt(userArray.getJSONObject(0).getString("rated_count"));
                buddyDetails = new BuddyModel();
                buddyDetails.setUserid(iduser);
                buddyDetails.setName(firstname + " " + lastname);
                buddyDetails.setImage(image);
                buddyDetails.setAge(age);
                // buddyDetails.setAwayDistance(buddyObj.optString("distance"));
                buddyDetails.setIsfav(userArray.getJSONObject(0).getString("isfav"));
                buddyDetails.setBadge(badge);
                buddyDetails.setAbout_yourself(about_yourself);
                buddyDetails.setIsreceivebuddyrequest(userArray.getJSONObject(0).getString("isreceive_request"));
                buddyDetails.setUser_type(Integer.parseInt(user_type));
                // buddyDetails.setRating(buddyObj.optInt("rating"));
                buddyDetails.setRating_count(rated_count);
                buddyDetails.setEmail(email);
                // buddyDetails.setIsfav(userArray.getJSONObject(0).getString("fav"));
                /*buddyDetails.setPhone_no(buddyObj.optString("phone_no"));
                buddyDetails.setAddress(buddyObj.optString("address"));*/
                buddyDetails.setFacebookId(facebookid);

                JSONArray skillarr = new JSONArray(userArray.getJSONObject(0).getString("skills"));
                if (!buddyDetails.getIsfav().equals("")) {
                    if (buddyDetails.getIsfav().equals("true")) {
                        isFav = true;
                        checkBoxAddFav.setChecked(true);
                    } else {
                        isFav = false;
                        checkBoxAddFav.setChecked(false);
                    }
                }

                if (skillarr != null) {
                    ArrayList<SkillDo> skilltemp = new ArrayList<>();
                    SkillDo skillDo;
                    for (int j = 0; j < skillarr.length(); j++) {
                        skillDo = new SkillDo();
                        skillDo.setActivty(skillarr.getJSONObject(j).optString("activity"));
                        skillDo.setLevel(skillarr.getJSONObject(j).optString("level"));
                        skillDo.setActivity_type(skillarr.getJSONObject(j).optString("type"));
                        if (skillarr.getJSONObject(j).optInt("is_open") == 1) {
                            skillDo.setIsBookingOpen(true);
                        }

                        skilltemp.add(skillDo);
                    }
                    buddyDetails.setSkillDo(skilltemp);
                    for (int i = 0; i < skilltemp.size(); i++) {


                        switch (i) {
                            case 0:
                                if (Utility.isNullCheck(skilltemp.get(0).getActivty())) {
                                    tvSquash.setVisibility(View.VISIBLE);
                                    tvSquash.setText(skilltemp.get(0).getActivty());
                                } else {
                                    tvSquash.setVisibility(View.GONE);
                                }
                                setRatingImageResource(skilltemp.get(0).getLevel(), imgRateOne);
                                break;
                            case 1:

                                if (Utility.isNullCheck(skilltemp.get(1).getActivty())) {
                                    tvParkour.setVisibility(View.VISIBLE);
                                    tvParkour.setText(skilltemp.get(1).getActivty());
                                } else {
                                    tvParkour.setVisibility(View.GONE);
                                }
                                setRatingImageResource(skilltemp.get(1).getLevel(), imgRateTwo);

                                break;
                            case 2:
                                if (Utility.isNullCheck(skilltemp.get(2).getActivty())) {
                                    tvYoga.setVisibility(View.VISIBLE);
                                    tvYoga.setText(skilltemp.get(2).getActivty());
                                } else {
                                    tvYoga.setVisibility(View.INVISIBLE);
                                }
                                setRatingImageResource(skilltemp.get(2).getLevel(), imgRateThree);

                                break;
                            case 3:
                                if (Utility.isNullCheck(skilltemp.get(3).getActivty())) {
                                    tvactivtyfour.setVisibility(View.VISIBLE);
                                    tvactivtyfour.setText(skilltemp.get(3).getActivty());
                                } else {
                                    tvactivtyfour.setVisibility(View.INVISIBLE);
                                }
                                setRatingImageResource(skilltemp.get(3).getLevel(), imgRateFour);
                                break;
                            default:
                                break;
                        }
                    }

                }


                if (Utility.isNullCheck(image)) {
                    Utility.setImageUniversalLoader(BuddyUpDetailsActivity.this,
                            image, (ImageView) findViewById(R.id.imageView3));
                }


                   /* if (buddyDetails.getIsfav().equals("1")) {
                        isFav = true;
                        checkBoxAddFav.setChecked(true);
                    } else {
                        isFav = false;
                        checkBoxAddFav.setChecked(false);
                    }*/


                if (Utility.isNullCheck(firstname)) {
                    tvPersonNames.setText(firstname + " " + lastname);
                }

                 /*   if (Utility.isNullCheck(buddyDetails.getAwayDistance())) {
                        int away = (int) Double.parseDouble(buddyDetails.getAwayDistance());
                        tvDistace.setVisibility(View.VISIBLE);
                        ((ImageView) findViewById(R.id.imgDistance)).setVisibility(View.GONE);
                        tvDistace.setText(away + " km away");
                    }*/


                if (Utility.isNullCheck(age)) {
                    Date birthday = null;
                    try {
                        birthday = AppConstants.sdf.parse(age);
                        tvEarly.setText(Utility.calculateAge(birthday, BuddyUpDetailsActivity.this, false));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                displayUserInterestWithRate();

                if (Utility.isNullCheck(about_yourself)) {
                    tvabout.setText(about_yourself);
                } else {
                    tvabout.setVisibility(View.GONE);
                }


                if (Utility.isNullCheck(badge)) {
                    tvProfile.setText(badge);
                    Utility.updateBadge(badge, badgeImage);
                }


                try {
                    BigDecimal rating = Utility.round(rated_count, rated_count);

                    if (rating.compareTo(BigDecimal.ZERO) > 0) {
                        int res = rating.compareTo(new BigDecimal("3.0"));

                        if (res == 0 || res == 1) {
                            tvRating.setText("User Rating " + rating);
                            tvRating.setVisibility(View.VISIBLE);
                        } else {
                            tvRating.setVisibility(View.GONE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }


    private void getMutualfriendApi(String tokan, String userid) {
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", tokan);
        map.put("facebookid", fbuserid);
        RequestHandler.getInstance().stringRequestVolley(BuddyUpDetailsActivity.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(BuddyUpDetailsActivity.this, isStaging)) + getMutualFriend, map, this, 3);


    }

    private void getBuddyDetailsFromAPI(JSONObject buddyObj) {

        try {
            SkillDo skillDo = null;
            JSONArray skillArray = buddyObj.getJSONArray("skill");
            JSONArray imageArray = buddyObj.getJSONArray("images");
            buddyDetails = new BuddyModel();
            buddyDetails.setUserid(buddyObj.optString("iduser"));
            buddyDetails.setName(buddyObj.optString("firstname") + " " + buddyObj.optString("lastname"));
           // buddyDetails.setImage(buddyObj.optString("image"));
         //   Log.d("customimgsss", buddyObj.getJSONArray("images").getJSONObject(0).getString("image").toString());
           // push = AppConstants.getiamgebaseurl() + buddyObj.getJSONArray("images").getJSONObject(0).getString("image");
           /* buddyDetails.setImage(push.replace("\"", ""));
            Log.d("customimg", push.replace("\"", ""));*/
           if(imageArray.length()>0){
               buddyDetails.setImage(AppConstants.getiamgebaseurl() +imageArray.getJSONObject(0).getString("image"));
               Log.d("pickupimage", String.valueOf(AppConstants.getiamgebaseurl() + imageArray.getJSONObject(0).getString("image")));

           }

            buddyDetails.setAge(buddyObj.optString("age"));
            buddyDetails.setAwayDistance(buddyObj.optString("distance"));
            buddyDetails.setIsfav(buddyObj.optString("isfav"));
            buddyDetails.setBadge(buddyObj.optString("badge"));
            buddyDetails.setAbout_yourself(buddyObj.optString("about_yourself"));
            buddyDetails.setIsreceivebuddyrequest(buddyObj.optString("isreceive_request"));
            buddyDetails.setUser_type(buddyObj.optInt("user_type"));
            buddyDetails.setRating(buddyObj.optInt("rating"));
            buddyDetails.setRating_count(buddyObj.optInt("rated_count"));
            buddyDetails.setEmail(buddyObj.optString("email"));
            buddyDetails.setPhone_no(buddyObj.optString("phone_no"));
            buddyDetails.setAddress(buddyObj.optString("address"));
            buddyDetails.setFacebookId(buddyObj.optString("facebookid"));
            if (skillArray != null) {
                ArrayList<SkillDo> skilltemp = new ArrayList<>();
                for (int j = 0; j < skillArray.length(); j++) {
                    skillDo = new SkillDo();
                    skillDo.setActivty(skillArray.getJSONObject(j).optString("activity"));
                    skillDo.setIdskill(skillArray.getJSONObject(j).optString("activityId"));
                    skillDo.setLevel(skillArray.getJSONObject(j).optString("level"));
                    skillDo.setActivity_type(skillArray.getJSONObject(j).optString("type"));
                    if (skillArray.getJSONObject(j).optInt("is_open") == 1) {
                        skillDo.setIsBookingOpen(true);
                    }
                    skilltemp.add(skillDo);
                }
                buddyDetails.setSkillDo(skilltemp);
            }


            Log.d("****", fbuserid = "");
            MutualFriendData mutualFriend = null;
            try {
                Log.d("mutualfriends", new Gson().toJson(buddyObj.getJSONObject("mutual_friends")));
                mutualFriendList = new ArrayList<>();
                if (buddyObj.getJSONObject("mutual_friends").getJSONObject("data").has("context")) {
                    if (buddyObj.getJSONObject("mutual_friends").getJSONObject("data").getJSONObject("context").has("all_mutual_friends")) {
                        mutual_recycle.setVisibility(View.VISIBLE);
                        tvFacebookSize.setVisibility(View.VISIBLE);
                        JSONArray mutualfriends = new JSONArray(buddyObj.getJSONObject("mutual_friends").getJSONObject("data").getJSONObject("context").getString("all_mutual_friends"));
                        tvFacebookSize.setText("Mutual Friends (" + mutualfriends.length() + ")");
                        for (int i = 0; i < mutualfriends.length(); i++) {
                            mutualFriend = new MutualFriendData();
                            String url = mutualfriends.getJSONObject(i).getJSONObject("picture").getString("url");
                            mutualFriend.setName(mutualfriends.getJSONObject(i).getString("name"));
                            if (mutualfriends.getJSONObject(i).has("id")) {
                                mutualFriend.setId(mutualfriends.getJSONObject(i).getString("id"));
                            } else {
                                mutualFriend.setId("0");
                            }

                            if (mutualfriends.getJSONObject(i).has("iduser")) {
                                mutualFriend.setIduser(mutualfriends.getJSONObject(i).getString("iduser"));
                            } else {
                                mutualFriend.setIduser("0");
                            }


                            mutualFriend.setUrl(url);
                            mutualFriendList.add(mutualFriend);
                        }

                        try {

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        Log.d("mymutual", new Gson().toJson(mutualFriendList));
                        if (mutualFriendList.size() > 0) {
                            MutualFriendAdapter mutualFriendAdapter = new MutualFriendAdapter(this, mutualFriendList);
                            mMutualFriends.setAdapter(mutualFriendAdapter);
                            mutualFriendAdapter.setmItemClickListener(new OnRecyclerItemClickListener() {
                                @Override
                                public void onRecyclerItemClick(int position) {
                                    Log.i("Recycle MF==>", "Clicked");
                                    //Implement API(http://54.254.173.51/app/index.php/FacebookUser/getuser)

                                    try {
                                        if (!mutualFriendList.get(position).getId().equalsIgnoreCase("0")) {
                                            Intent pass = new Intent(BuddyUpDetailsActivity.this, BuddyUpDetailsActivity.class);
                                            pass.putExtra("userfbid", mutualFriendList.get(position).getIduser());
                                            pass.putExtra("fromSelf", "fromSelf");
                                            startActivity(pass);
                                        } else {
                                            Toast.makeText(BuddyUpDetailsActivity.this, "Not a UACTIV member", Toast.LENGTH_LONG).show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            mutual_recycle.setVisibility(View.GONE);
                            tvFacebookSize.setVisibility(View.GONE);
                        }


                    }
                } else {
                    mutual_recycle.setVisibility(View.GONE);
                    tvFacebookSize.setVisibility(View.GONE);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }



            displayUserInfo();

        } catch (Exception e) {
            e.printStackTrace();
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
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }


}
