package com.uactiv.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.maps.model.LatLng;
import com.uactiv.R;
import com.uactiv.controller.IRunTimePermission;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.Home;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.SkillDo;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BusinessDetailsActivity extends Activity implements OnClickListener,
        AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener, IRunTimePermission {

    private String TAG = "BusinessDetailsActivity";
    private CustomButton bottomLayout;
    private CheckBox checkBoxaddFav = null;
    private Intent intent = null;
    private GifImageView progressWheel = null;
    private BuddyModel buddyDetails = null;
    private ImageView imgRateOne = null;
    private ImageView imgRateTwo = null;
    private ImageView imgRateThree = null;
    private ImageView imgRateFour = null;
    private ImageView imgRateFive = null;
    private ImageView imgRateSix = null;
    private TextView tvSquash = null;
    private TextView tvParkour = null;
    private TextView tvYoga = null;
    private TextView tvactivtyfour = null;
    private TextView tvactivtyfive = null;
    private TextView tvactivtysix = null;
    private CustomTextView tvPersonNames = null;
    private CustomTextView tvabout = null;
    private CustomTextView tvEarly = null;
    private CustomTextView tvDistace = null;
    private CustomTextView tvProfile = null;
    private ImageView badgeImage = null;
    private CustomTextView tvRating = null;
    private CustomTextView tvEmail = null;
    private CustomTextView tvAdderess = null;
    private CustomTextView tvPhoneno = null;
    private CustomTextView tvLandline = null;
    private boolean isFav = false;
    public static Activity buddyupDetailsActivity = null;
    private boolean isView = false;
    private CustomTextView btnDone;
    private String  mPickupAddress,tempBusinessDo;
    private double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);
        buddyupDetailsActivity = BusinessDetailsActivity.this;
        intent = getIntent();
        bindActivity();
        if (intent != null) {
            isView = intent.getBooleanExtra("view", false);
            buddyDetails = (BuddyModel) getIntent().getSerializableExtra(serialKeyBuddy);
            latitude=intent.getDoubleExtra("latitude",0);
            longitude=intent.getDoubleExtra("longitude",0);
            mPickupAddress=intent.getStringExtra("mPickupAddress");
            tempBusinessDo=intent.getStringExtra("tempBusinessDo");
            try {
                btnDone.setVisibility(View.INVISIBLE);
            }catch (NullPointerException ex){
                ex.printStackTrace();
            }



        }

        applyBusinessAccountUi();
        if(mPickupAddress!=""){
            btnDone.setVisibility(View.VISIBLE);
        }
    }


    /** bind activity
     *
     */
    private void bindActivity() {
        tvPersonNames = (CustomTextView) findViewById(R.id.tvPersonNames);
        btnDone=(CustomTextView)findViewById(R.id.btnDone);
        tvEarly = (CustomTextView) findViewById(R.id.tvEarly);
        tvabout = (CustomTextView) findViewById(R.id.tvBuddyDetails);
        tvPersonNames.setShadowLayer(10, 0, 0, getResources().getColor(R.color.black));
        bottomLayout = (CustomButton) findViewById(R.id.btnEditAccount);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(BusinessDetailsActivity.this, progressWheel);
        badgeImage = (ImageView) findViewById(R.id.imageView4);
        checkBoxaddFav = (CheckBox) findViewById(R.id.imageView2);
        checkBoxaddFav.setOnClickListener(this);
        tvProfile = (CustomTextView) findViewById(R.id.tvProfile);
        tvSquash = (TextView) findViewById(R.id.tvSquash);
        tvParkour = (TextView) findViewById(R.id.tvParkour);
        tvYoga = (TextView) findViewById(R.id.tvYoga);
        tvactivtyfour = (TextView) findViewById(R.id.tvRockClimbing);
        tvactivtyfive = (TextView) findViewById(R.id.tv_act_five);
        tvactivtysix = (TextView) findViewById(R.id.tv_act_six);
        tvRating = (CustomTextView) findViewById(R.id.tvRating);
        imgRateOne = (ImageView) findViewById(R.id.rate_img_one);
        imgRateTwo = (ImageView) findViewById(R.id.rate_img_two);
        imgRateThree = (ImageView) findViewById(R.id.rate_img_three);
        imgRateFour = (ImageView) findViewById(R.id.rate_img_four);
        imgRateFive = (ImageView) findViewById(R.id.rate_img_five);
        imgRateSix = (ImageView) findViewById(R.id.rate_img_six);
        tvDistace = (CustomTextView) findViewById(R.id.tvDistace);
        tvEmail = (CustomTextView) findViewById(R.id.tvEmail);
        tvAdderess = (CustomTextView) findViewById(R.id.tvAddress);
        tvPhoneno = (CustomTextView) findViewById(R.id.tvphoneno);
        tvLandline = (CustomTextView) findViewById(R.id.tvLandLine);
        tvPhoneno.setOnClickListener(this);
        tvLandline.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
        tvAdderess.setOnClickListener(this);
        bottomLayout.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        (findViewById(R.id.imageView1)).setOnClickListener(this);
        Utility.setScreenTracking(getApplicationContext(),"Business Profile Screen");
        if (!isView) {
            updateBusinessProfileUi();
        } else {
            getBusinessUserInfo(intent.getStringExtra("userid"));
        }


    }


    /** apply business user ui
     *
     */
    private void applyBusinessAccountUi() {
        bottomLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        checkBoxaddFav.setVisibility(View.GONE);
        checkBoxaddFav.setEnabled(false);
    }



    /** get business profile details from api
     *
     * @param userId for user
     */

    private void getBusinessUserInfo(String userId) {

        if (Utility.isConnectingToInternet(BusinessDetailsActivity.this)) {
            try {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                Map<String, String> param = new HashMap<>();
                param.put("iduser", "" + userId);
                Log.e("param", "" + param.toString());
                RequestHandler.getInstance().stringRequestVolley(BusinessDetailsActivity.this,
                        AppConstants.getBaseUrl(SharedPref.getInstance()
                                .getBooleanValue(BusinessDetailsActivity.this, isStaging)) + userinfo, param, this, 1);// getUserInfo
                Utility.setEventTracking(getApplicationContext(),"", AppConstants.SCREEN_TRACKING_ID_SUGGESTEDLOCATION);


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utility.showInternetError(BusinessDetailsActivity.this);
        }

    }


    /** update business profile details
     *
     */
    private void updateBusinessProfileUi() {

        if (Utility.isNullCheck(buddyDetails.getImage())) {
            Utility.setImageUniversalLoader(BusinessDetailsActivity.this,
                    buddyDetails.getImage(), (ImageView) findViewById(R.id.imageView3));
        }


        if (buddyDetails.getIsfav().equals("1")) {
            isFav = true;
            checkBoxaddFav.setChecked(true);
        } else {
            isFav = false;
            checkBoxaddFav.setChecked(false);
        }

        if (Utility.isNullCheck(buddyDetails.getName())) {
            tvPersonNames.setText(buddyDetails.getName());
        }

        if (Utility.isNullCheck(buddyDetails.getAwayDistance())) {
            int away = (int) Double.parseDouble(buddyDetails.getAwayDistance());
            tvDistace.setVisibility(View.VISIBLE);
            (findViewById(R.id.imgDistance)).setVisibility(View.GONE);
            tvDistace.setText(away + " km away");
        }


        if (Utility.isNullCheck(buddyDetails.getBusinessType())) {
            tvEarly.setText(buddyDetails.getBusinessType());
        }

        if (Utility.isNullCheck(buddyDetails.getAbout_yourself())) {
            tvabout.setText(buddyDetails.getAbout_yourself());
        } else {
            tvabout.setVisibility(View.GONE);
        }


        if (Utility.isNullCheck(buddyDetails.getEmail())) {
            tvEmail.setText("" + buddyDetails.getEmail());
        }
        if (Utility.isNullCheck(buddyDetails.getPhone_no())) {
            tvPhoneno.setText("" + buddyDetails.getPhone_no());
        }

        if (Utility.isNullCheck(buddyDetails.getLandLine())) {
            tvLandline.setVisibility(View.VISIBLE);
            tvLandline.setText("" + buddyDetails.getLandLine());
        } else {
            tvLandline.setVisibility(View.GONE);
        }
        if (Utility.isNullCheck(buddyDetails.getAddress())) {
            tvAdderess.setText("" + buddyDetails.getAddress());
        }

        tvProfile.setVisibility(View.GONE);
        badgeImage.setImageResource(R.drawable.certified);

        try {
            BigDecimal rating = Utility.round(buddyDetails.getRating(), buddyDetails.getRating_count());
            if (rating.compareTo(BigDecimal.ZERO) > 0) {
                int res = rating.compareTo(new BigDecimal("3.0"));
                Log.d(TAG, "res " + res);
                if (res == 0 || res == 1) {
                    tvRating.setText("User Rating " + rating);
                    tvRating.setVisibility(View.VISIBLE);
                    Log.d(TAG, "rating applied");
                } else {
                    tvRating.setVisibility(View.GONE);
                    Log.d(TAG, "rating not applied");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateUserActivity();

    }


    /** display user activity on ui
     *
     */
    private void updateUserActivity() {

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
                        updateRate(skillDo.getLevel(), imgRateOne);
                        break;
                    case 1:

                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvParkour.setVisibility(View.VISIBLE);
                            tvParkour.setText(skillDo.getActivty());
                        } else {
                            tvParkour.setVisibility(View.GONE);
                        }
                        updateRate(skillDo.getLevel(), imgRateTwo);

                        break;
                    case 2:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvYoga.setVisibility(View.VISIBLE);
                            tvYoga.setText(skillDo.getActivty());
                        } else {
                            tvYoga.setVisibility(View.GONE);
                        }
                        updateRate(skillDo.getLevel(), imgRateThree);

                        break;
                    case 3:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvactivtyfour.setVisibility(View.VISIBLE);
                            tvactivtyfour.setText(skillDo.getActivty());
                        } else {
                            tvactivtyfour.setVisibility(View.GONE);
                        }
                        updateRate(skillDo.getLevel(), imgRateFour);
                        break;


                    case 4:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvactivtyfive.setVisibility(View.VISIBLE);
                            tvactivtyfive.setText(skillDo.getActivty());
                        } else {
                            tvactivtyfive.setVisibility(View.GONE);
                        }
                        updateRate(skillDo.getLevel(), imgRateFive);
                        break;


                    case 5:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvactivtysix.setVisibility(View.VISIBLE);
                            tvactivtysix.setText(skillDo.getActivty());
                        } else {
                            tvactivtysix.setVisibility(View.GONE);
                        }
                        updateRate(skillDo.getLevel(), imgRateSix);
                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** update rating image
     *
     * @param level level
     * @param imageView imageview
     */

    private void updateRate(String level, ImageView imageView) {
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
        finish();
    }


    /** perform on clicks
     *
     * @param v view
     */

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imageView2:
                doAddFav();
                break;

            case R.id.tvphoneno:
                if (Utility.isNullCheck(buddyDetails.getPhone_no())) {
                    call(buddyDetails.getPhone_no());
                }
                break;
            case R.id.tvLandLine:
                if (Utility.isNullCheck(buddyDetails.getLandLine())) {
                    call(buddyDetails.getLandLine());
                }
                break;
            case R.id.tvEmail:
                if (Utility.isNullCheck(buddyDetails.getEmail())) {
                    AppConstants.businessShareMailWithUs(BusinessDetailsActivity.this, buddyDetails.getEmail());
                }
                break;

            case R.id.tvAddress:
                if (Utility.isNullCheck(buddyDetails.getAddress())) {
                    if (CreatePickUp.createPickupNotifier != null) {
                        CreatePickUp.createPickupNotifier.mapViewNotifier(new LatLng(latitude,longitude), buddyDetails.getAddress(), "0", tempBusinessDo, 1);
                    } else if (BuddyUpRequest.createPickupNotifier != null) {
                        BuddyUpRequest.createPickupNotifier.mapViewNotifier(new LatLng(latitude,longitude), buddyDetails.getAddress(), "0", tempBusinessDo, 1);
                    }
                    finish();
                }
                break;
            case R.id.btnEditAccount:
                if (buddyDetails.getIsreceivebuddyrequest().equals("1")) {
                    Intent buddyUpRequest_intent = new Intent(BusinessDetailsActivity.this, BuddyUpRequest.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(serialKeycreateBuddy, buddyDetails);
                    buddyUpRequest_intent.putExtras(bundle);
                    startActivity(buddyUpRequest_intent);
                } else {
                    Utility.showToastMessage(BusinessDetailsActivity.this, "User is not accepting Buddy Up request");
                }
                break;
            case R.id.imageView1:
                onBackPressed();
                break;

            case R.id.btnDone:
                if (CreatePickUp.createPickupNotifier != null) {
                    CreatePickUp.createPickupNotifier.mapViewNotifier(new LatLng(latitude,longitude), mPickupAddress, "0", tempBusinessDo, 1);
                    BusinessLocationMap.finish.myfinish();
                  //  BusinessLocationMap.createPickupNotifier.mapViewNotifier(new LatLng(latitude,longitude), mPickupAddress, "0", tempBusinessDo, 1);
                } else if (BuddyUpRequest.createPickupNotifier != null) {
                    BuddyUpRequest.createPickupNotifier.mapViewNotifier(new LatLng(latitude,longitude), mPickupAddress, "0", tempBusinessDo, 1);

                }
                finish();
                break;
            default:
                break;
        }
    }


    /** add favourites
     *
     */
    private void doAddFav() {
        if (Utility.isConnectingToInternet(BusinessDetailsActivity.this)) {
            try {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                Map<String, String> param = new HashMap<>();
                if (checkBoxaddFav.isChecked()) {
                    param.put("isfav", "1");
                    isFav = true;
                } else {
                    param.put("isfav", "0");
                    isFav = false;
                }
                param.put("favourites", buddyDetails.getUserid());
                param.put("iduser", SharedPref.getInstance().getStringVlue(BusinessDetailsActivity.this, userId));
                RequestHandler.getInstance().stringRequestVolley(BusinessDetailsActivity.this,
                        AppConstants.getBaseUrl(SharedPref.getInstance()
                                .getBooleanValue(BusinessDetailsActivity.this, isStaging)) + addfavourites, param, this, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(BusinessDetailsActivity.this);
        }
    }


    /** requesting android M persmission to call
     *
      * @param number number to call
     */

    public void call(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.System.canWrite(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("You need to grant access to make a call permission")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Log.e("MainActivity", "error starting permission intent", e);
                            }
                        }
                    })
                    .show();
            return;
        }
        startActivity(callIntent);
    }


    /** api responses
     *
     * @param successResponse response
     * @param flag task id
     */

    @Override
    public void successResponse(String successResponse, int flag) {

        /** flag == 0 Addfavorite API response
         * 	flag == 1 userInfoAPI API response
         *
         */
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(successResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (flag) {
            case 0:
                if (jsonObject != null) {
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        if (isFav) {
                            buddyDetails.setIsfav("1");
                            checkBoxaddFav.setChecked(true);
                        } else {
                            buddyDetails.setIsfav("0");
                            checkBoxaddFav.setChecked(false);
                        }
                        Home.notifiyArrayListChange.getBuddyModelAddedItems(intent.getIntExtra("position", 0), buddyDetails);
                        //Should be updatable from API need respnse as true or false
                    } else {
                        Utility.showToastMessage(BusinessDetailsActivity.this, jsonObject.optString(KEY_MSG));
                    }
                }

                break;
            case 1:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        getBuddyDetailsFromAPI(jsonObject.optJSONObject(KEY_DETAIL));
                    }
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                }

                break;
            default:
                break;
        }


    }


    /** get userInfo from object
     *
     * @param buddyObj details object
     */
    private void getBuddyDetailsFromAPI(JSONObject buddyObj) {

        try {
            SkillDo skillDo = null;
            JSONArray skillArray = buddyObj.getJSONArray("skill");
            buddyDetails = new BuddyModel();
            buddyDetails.setUserid(buddyObj.optString("iduser"));
            buddyDetails.setName(buddyObj.optString("firstname") + " " + buddyObj.optString("lastname"));
            buddyDetails.setImage(buddyObj.optString("image"));
            buddyDetails.setAge(buddyObj.optString("age"));
            buddyDetails.setAwayDistance(buddyObj.optString("distance"));
            buddyDetails.setIsfav(buddyObj.optString("fav"));
            buddyDetails.setBadge(buddyObj.optString("badge"));
            buddyDetails.setAbout_yourself(buddyObj.optString("about_yourself"));
            buddyDetails.setIsreceivebuddyrequest(buddyObj.optString("isreceive_request"));
            buddyDetails.setUser_type(buddyObj.optInt("user_type"));
            buddyDetails.setRating(buddyObj.optInt("rating"));
            buddyDetails.setRating_count(buddyObj.optInt("rated_count"));
            buddyDetails.setEmail(buddyObj.getJSONObject("business_detail").optString("email"));
            buddyDetails.setPhone_no(buddyObj.getJSONObject("business_detail").optString("phone_no"));
            buddyDetails.setLandLine(buddyObj.getJSONObject("business_detail").optString("landline"));
            buddyDetails.setAddress(buddyObj.getJSONObject("business_detail").optString("address"));
            buddyDetails.setBusinessType(buddyObj.getJSONObject("business_detail").optString("business_type"));
            if (skillArray != null) {

                ArrayList<SkillDo> skilltemp = new ArrayList<>();

                for (int j = 0; j < skillArray.length(); j++) {
                    skillDo = new SkillDo();
                    skillDo.setActivty(skillArray.getJSONObject(j).optString("activity"));
                    skillDo.setLevel(skillArray.getJSONObject(j).optString("level"));
                    skillDo.setActivity_type(skillArray.getJSONObject(j).optString("type"));
                    skilltemp.add(skillDo);
                }
                buddyDetails.setSkillDo(skilltemp);
            }


            updateBusinessProfileUi();

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

    }

    @Override
    public void onSuccess(int milis) {

    }
}
