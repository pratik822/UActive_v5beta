package com.uactiv.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uactiv.R;
import com.uactiv.adapter.AttendingPeoplesAdapter;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.Home;
import com.uactiv.interfaces.OnAlertClickListener;
import com.uactiv.model.FavouriteModel;
import com.uactiv.model.PickUpModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.utils.WorkaroundMapFragment;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PickUpEventPage extends FragmentActivity implements OnClickListener,
        AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener {
    String paymentStatus = null;
    GoogleMap googleMap;
    CustomButton bottomBarText;
    ScrollView ScrollView1;
    RecyclerView productRecycle = null;
    LinearLayoutManager manager = null;
    AttendingPeoplesAdapter mAdapter;
    ArrayList<FavouriteModel> items;
    ScrollView scrollView;
    GifImageView progressWheel = null;
    Intent intent = null;
    com.uactiv.model.PickUpCategory PickUpCategory = null;
    CustomTextView tvName = null;
    CustomTextView tvGameName = null;
    CustomTextView tvGameDescription = null;
    CustomTextView tvDate = null;
    CustomTextView tvTime = null;
    CustomTextView tvLocation = null;
    CustomTextView tvSpots = null;
    CustomTextView tvAttending = null;
    CustomTextView tv_spot = null;
    CircleImageView ic_profille = null;
    private int spotsize = 0;

    //int position ;
    PickUpModel pickUpModel = null;
    int pickupItemPosition;
    int CategoryItemPosition;
    String activity_name = null;
    ShareDialog shareDialog = null;
    JSONObject pickupdetailObj;
    boolean isfrom_map = false;
    boolean isEventExpried = true;
    private ImageView imgShare;
    private TextView headerText;
    RelativeLayout mInvitedPeopleLayout;
    ArrayList<FavouriteModel> mInvitedPeoples = new ArrayList<>();
    RecyclerView mInvitesRecyclerView = null;
    AttendingPeoplesAdapter mInvitesAdapter;
    private boolean isScheduleExists = true;
    private String usertypeString;
    private String pay_flag;
    private String pay_button;

    private String notification, iduser, idSchedule;
    private String status = null;
    private String group_id;
    private double Latitude;
    private double Longitude;
    private String SheduleId;
    private String sheduleId="";
    private String type;
    Uri data;
    String urlString;
    String[] separated;
    String frompickupPopup;
    public static String  pickupjoin="";
    private CustomTextView request_status = null;
    private
    LinearLayout show_status_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickup_event);

        if (!AppConstants.isGestLogin(PickUpEventPage.this)) {
            Utility.setScreenTracking(this, AppConstants.SCREEN_TRACKING_ID_PICKUPDETAILS);
        } else {
            Utility.setScreenTracking(this, "Guest login Pick Up detail page");
        }
        googleMap = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        FacebookSdk.sdkInitialize(PickUpEventPage.this); // Facebook sdk initialtion.
        shareDialog = new ShareDialog(PickUpEventPage.this);
        initViews();
        intent = getIntent();
        //get uri data

        data = getIntent().getData();
        if (data != null) {
            //get schma
            Log.d("aaa", data.toString());
            separated = data.toString().split("=");
            Log.d("aaa", separated[1]);
            String scheme = data.getScheme(); // "http"
            Log.d("scheme", data.getScheme());
            //get server name
            sheduleId = separated[1];
            getpickupdetailsFromAPI(separated[1]);
        } else {
            if (intent.getStringExtra("frompickupPopup") != null) {
                if (intent.getStringExtra("SheduleId") != null && !intent.getStringExtra("frompickupPopup").isEmpty()) {
                    SheduleId = intent.getStringExtra("SheduleId");
                    frompickupPopup = intent.getStringExtra("SheduleId");
                    getpickupdetailsFromAPI(frompickupPopup);
                }
            } else {
                frompickupPopup = intent.getStringExtra("SheduleId");
                if(sheduleId.isEmpty()){
                    SheduleId=frompickupPopup;
                }
                getpickupdetailsFromAPI(frompickupPopup);
            }


            if (intent != null) {
                if (intent.getStringExtra("notification") != null) {
                    notification = intent.getStringExtra("notification");
                    status = intent.getStringExtra("status");
                    Utility.setScreenTracking(PickUpEventPage.this, "Pick up detail from Notifications screen");
                    getpickupdetailsFromAPI(intent.getStringExtra("idSchedule"));
                } else if (frompickupPopup != null && frompickupPopup.isEmpty()) {
                    PickUpCategory = (com.uactiv.model.PickUpCategory) getIntent().getSerializableExtra("PICKUP");
                    pickupItemPosition = Integer.parseInt(intent.getStringExtra("position"));
                    CategoryItemPosition = Integer.parseInt(intent.getStringExtra("CategoryItemPosition"));
                    activity_name = intent.getStringExtra("Activity");
                    isfrom_map = intent.getBooleanExtra("from_map", false);
                    status = PickUpCategory.getStatus();
                    Latitude = PickUpCategory.getLatitude();
                    Longitude = PickUpCategory.getLongitude();
                    //PickUpCategory.getLatitude(), PickUpCategory.getLongitude()
                    Log.e("Status", "" + PickUpCategory.getStatus());
                    Log.e("getIdschedule", "" + PickUpCategory.getIdschedule());
                } else if (intent.getStringExtra("frompickupPopup") == null) {
                    PickUpCategory = (com.uactiv.model.PickUpCategory) getIntent().getSerializableExtra("PICKUP");
                    pickupItemPosition = Integer.parseInt(intent.getStringExtra("position"));
                    CategoryItemPosition = Integer.parseInt(intent.getStringExtra("CategoryItemPosition"));
                    activity_name = intent.getStringExtra("Activity");
                    isfrom_map = intent.getBooleanExtra("from_map", false);
                    status = PickUpCategory.getStatus();
                    Latitude = PickUpCategory.getLatitude();
                    Longitude = PickUpCategory.getLongitude();
                    if (PickUpCategory != null)
                        getpickupdetailsFromAPI(PickUpCategory.getIdschedule());
                }


            }

            if (frompickupPopup != null && frompickupPopup.isEmpty()) {
                if (intent.getStringExtra("idSchedule") == null) {
                    getpickupdetailsFromAPI(PickUpCategory.getIdschedule());
                    scrollView.smoothScrollTo(0, 0);
                    isBusinessAccount();
                }
            }

        }


    }

    private boolean isBusinessAccount() {
        Boolean b = SharedPref.getInstance().getBooleanValue(PickUpEventPage.this, "thisisbusiness");
        if (b == true) {
            return true;
        }
     /*   if (SharedPref.getInstance().getBooleanValue(PickUpEventPage.this, isbussiness)) {
            bottomBarText.setVisibility(View.GONE);
            return true;
        }*/
        return false;
    }

    private void getpickupdetailsFromAPI(String idschedule) {

        Log.e("Get into ", "getpickupdetailsFromAPI()");

        if (Utility.isConnectingToInternet(PickUpEventPage.this)) {
            try {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                Map<String, String> param = new HashMap<>();

                Log.d("userid", idschedule + "--" + SharedPref.getInstance().getStringVlue(PickUpEventPage.this, userId));
                if (!AppConstants.isGestLogin(PickUpEventPage.this)) {
                    param.put("idschedule", idschedule);
                    param.put("isread", "0");
                    param.put("iduser", SharedPref.getInstance().getStringVlue(PickUpEventPage.this, userId));
                    RequestHandler.getInstance().stringRequestVolley(PickUpEventPage.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpEventPage.this, isStaging)) + pickupdetail, param, this, 1);

                } else {
                    param.put("idschedule", idschedule);
                    param.put("iduser", "0");
                    param.put("gender_pref", "both");
                    param.put("radius_limit", "100");
                    RequestHandler.getInstance().stringRequestVolley(PickUpEventPage.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpEventPage.this, isStaging)) + "pickupdetail_guest", param, this, 1);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(PickUpEventPage.this);
        }

    }

    private void initViews() {
        tvName = (CustomTextView) findViewById(R.id.tvName);
        tvGameName = (CustomTextView) findViewById(R.id.tvGameName);
        tvGameDescription = (CustomTextView) findViewById(R.id.tvGameDescription);
        tvDate = (CustomTextView) findViewById(R.id.tvDate);
        tvTime = (CustomTextView) findViewById(R.id.tvTime);
        tvLocation = (CustomTextView) findViewById(R.id.tvLocation);
        tvSpots = (CustomTextView) findViewById(R.id.tvSpots);
        tvAttending = (CustomTextView) findViewById(R.id.tvAttending);
        tv_spot = (CustomTextView) findViewById(R.id.tv_spot);
        mInvitesRecyclerView = (RecyclerView) findViewById(R.id.invites_recycle);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(PickUpEventPage.this, progressWheel);
        scrollView = (ScrollView) findViewById(R.id.ScrollView1);
        imgShare = (ImageView) findViewById(R.id.imgShare);
        imgShare.setVisibility(View.VISIBLE);
        imgShare.setOnClickListener(this);
        headerText = (TextView) findViewById(R.id.headerText);
        bottomBarText = (CustomButton) findViewById(R.id.bottomBarText);
        mInvitedPeopleLayout = (RelativeLayout) findViewById(R.id.inviteLayout);
        mInvitedPeopleLayout.setVisibility(View.GONE);
        bottomBarText.setOnClickListener(this);
        request_status=(CustomTextView)findViewById(R.id.request_status);
        headerText.setText("Pick-Up");
        //bottomBarText.setText("Request to join");

        ScrollView1 = (ScrollView) findViewById(R.id.ScrollView1);
        productRecycle = (RecyclerView) findViewById(R.id.category_recycle);
        ic_profille = (CircleImageView) findViewById(R.id.imageView1);
        show_status_bar=(LinearLayout)findViewById(R.id.show_status_bar);


        //final CircleImageView circleImageView = (CircleImageView) findViewById(R.id.imageView1);
        ic_profille.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_buddydetails = null;

                if (pickupdetailObj != null && pickupdetailObj.optString("user_type") != null) {

                    if (pickupdetailObj.optInt("user_type") == USER_TYPE_APP) {
                        intent_buddydetails = new Intent(PickUpEventPage.this, BuddyUpDetailsActivity.class);

                    } else if (pickupdetailObj.optInt("user_type") == USER_TYPE_BUSINESS) {
                        intent_buddydetails = new Intent(PickUpEventPage.this, BusinessDetailsActivity.class);
                    }
                    intent_buddydetails.putExtra("view", true);
                    intent_buddydetails.putExtra("userid", pickupdetailObj.optString("iduser"));
                    intent_buddydetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent_buddydetails);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

                }
            }
        });
    }


    /***/
    private void showLinkDialog(String[] links) {
        final Dialog linkDialog = new Dialog(this, R.style.Theme_Dialog);
        linkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        linkDialog.setContentView(R.layout.url_link_dialog);
        LinearLayout linkUrl_Container = (LinearLayout) linkDialog.findViewById(R.id.linkUrl_Container);

        for (int i = 0; i < links.length; i++) {
            TextView linkUrl = new TextView(this);
            linkUrl.setText(links[i]);
            linkUrl.setTag(links[i]);
            linkUrl.setTextSize(getResources().getDimension(R.dimen.text_size_3));
            linkUrl.setTextColor(getResources().getColor(R.color.com_facebook_button_send_background_color_pressed));
            linkUrl.setPaintFlags(linkUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            linkUrl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = v.getTag().toString();
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;
                    Log.e("Link ", "" + url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
            linkUrl_Container.addView(linkUrl);
        }
        linkDialog.setCanceledOnTouchOutside(true);
        linkDialog.setCancelable(true);
        linkDialog.show();
    }

    private void updatePickUpUI(JSONObject ObjectDetails) throws JSONException {

        pickupdetailObj = ObjectDetails;
        usertypeString = ObjectDetails.optString("user_type");
        pay_flag = ObjectDetails.optString("pay_flag");
        pay_button = ObjectDetails.optString("pay_button");
        if (data != null) {
            status = ObjectDetails.optString("status");
        }

        type = ObjectDetails.optString("type");
        group_id = ObjectDetails.optString("group_id");
        iduser = ObjectDetails.optString("iduser");
        Log.i("usertype:", usertypeString + " ==pay_flag:" + pay_flag + "==Userid:" + SharedPref.getInstance().getStringVlue(PickUpEventPage.this, userId) + "sheduleid===" + SheduleId + "  ==pay_button:" + pay_button);
        paymentStatus = ObjectDetails.optString("history_status");
        paymentStatus(paymentStatus);


        if (!TextUtils.isEmpty(ObjectDetails.optString("image"))) {

            Picasso.with(PickUpEventPage.this)
                    .load(ObjectDetails.optString("image"))
                    .placeholder(R.drawable.ic_profile).centerCrop().fit()
                    .into(ic_profille);
        }

        if (!TextUtils.isEmpty(ObjectDetails.optString("activity"))) {
            tvGameName.setText(ObjectDetails.optString("activity") + "- Pick Up");
        }

        if (Utility.isNullCheck(ObjectDetails.optString("message"))) {
            tvGameDescription.setText(StringEscapeUtils.unescapeJava(ObjectDetails.optString("message").trim()));
            /*tvGameDescription.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvGameDescription.getText().length() > 0) {
                        String[] links = Utility.parseAndGetURLsfromText(tvGameDescription.getText().toString());
                        if (links != null && links.length > 0) {
                            showLinkDialog(links);
                        }
                    }
                }
            });*/
        } else {
            tvGameDescription.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(ObjectDetails.optString("date"))) {

            String date = Utility.dateFormatWithFull(ObjectDetails.optString("date"));
            if (!TextUtils.isEmpty(date)) {
                tvDate.setText(date);
            }
        }

        if (!TextUtils.isEmpty(ObjectDetails.optString("location"))) {
            tvLocation.setText(ObjectDetails.optString("location"));
        }


        if (!TextUtils.isEmpty(ObjectDetails.optString("no_of_people"))) {

            //tvSpots.setText(ObjectDetails.optString("no_of_people") + " spots");
            spotsize = Integer.parseInt(ObjectDetails.optString("spots_flag"));


            if (ObjectDetails.optInt("no_of_people") <= 0 || ObjectDetails.optString("no_of_people").equals("No limit")) {

                tvSpots.setText("Unlimited spots");

            } else {

                if (ObjectDetails.optInt("no_of_people") == 1) {

                    tvSpots.setText(ObjectDetails.optInt("no_of_people") + " spot");
                } else {
                    tvSpots.setText(ObjectDetails.optInt("no_of_people") + " spots");
                }
            }
        }


        if (!TextUtils.isEmpty(ObjectDetails.optString("start_time"))) {

           /* String time =Utility.timeFormatChanage(ObjectDetails.optString("start_time"));
            if(!TextUtils.isEmpty(time)) {
                tvTime.setText(time);
            }*/

            String start_time = Utility.timeFormatChanage(ObjectDetails.optString("start_time"));
            String end_time = Utility.timeFormatChanage(ObjectDetails.optString("end_time"));
            if (!TextUtils.isEmpty(start_time)) {
                if (!TextUtils.isEmpty(end_time)) {

                    tvTime.setText(start_time + " - " + end_time);
                } else {
                    tvTime.setText(start_time + "");
                }

                Log.e("isExpired", "" + Utility.endTimelogic(ObjectDetails.optString("start_time") + " " + ObjectDetails.optString("date"), AppConstants.TIME_LIMT_PICK_UP));

                //isEventExpried = Utility.endTimelogic(ObjectDetails.optString("start_time") + " " + ObjectDetails.optString("date"));


            }

        }



        if (!TextUtils.isEmpty(ObjectDetails.optString("firstname"))) {
            tvName.setText(ObjectDetails.optString("firstname") + " " + ObjectDetails.optString("lastname"));
        }

        if (!TextUtils.isEmpty(ObjectDetails.optString("no_of_people"))) {


            try {
                if (ObjectDetails.optInt("no_of_people") <= 0) {
                    tvAttending.setText("Attending (" + ObjectDetails.optJSONArray("attending").length() + ")");
                } else {
                    tvAttending.setText("Attending (" + ObjectDetails.optJSONArray("attending").length() + "/" + ObjectDetails.optString("no_of_people") + ")");
                }

                if (ObjectDetails.optInt("no_of_people") <= 0) {
                    tv_spot.setText("( Unlimited spots )");

                } else {
                    int left = ObjectDetails.optInt("no_of_people") - ObjectDetails.optJSONArray("attending").length();
                    if (left == 1) {
                        tv_spot.setText(" (" + left + " spot left )");
                    } else {
                        tv_spot.setText(" (" + left + " spots left )");
                    }
                }


            } catch (Exception e) {
                tvAttending.setText("Attending ( 0 )");
                tv_spot.setText("( Unlimited spots )");
                e.printStackTrace();
            }
        }

        /*if (!(isBusinessAccount())) {
            setUpBottomStatusBar(ObjectDetails.optString("iduser"));
        }*/
        setUpBottomStatusBar(ObjectDetails.optString("iduser"));
    }


    /**
     * prepare bottom status bar if user is not business user
     *
     * @param currentUserId isHost is current user.
     */

    private void setUpBottomStatusBar(String currentUserId) {
        if (Utility.isNullCheck(status)) {
            Log.e("StatusKey", "" + status);
            //PickUpCategory.getStatus();
            if (usertypeString.equalsIgnoreCase("1") && pay_flag.equalsIgnoreCase("1")) {
                bottomBarText.setText("Make Payment");
                bottomBarText.setEnabled(true);

                if (!AppConstants.isGestLogin(PickUpEventPage.this)) {
                    Utility.setEventTracking(PickUpEventPage.this, "Pick Up detail page", "Make a payment on Pick Up detail page");
                } else {
                    Utility.setEventTracking(PickUpEventPage.this, "Pick Up detail page", "Make a payment on Guest login Pick Up detail page");
                }

            } else {
                switch (status) {
                    case KEY_INVITED:
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setText("Invite Request Pending!");
                        bottomBarText.setText("Invite Request Pending!");
                        request_status.setTextColor(getResources().getColor(R.color.pendingcolor));
                        bottomBarText.setVisibility(View.GONE);
                        bottomBarText.setEnabled(false);
                        break;
                    case KEY_ACCEPTED:
                        //Show bottombar text as Pickup Accepted.
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setText(R.string.msg_pickup_request_accepted);
                        request_status.setTextColor(Color.parseColor("#4eba89"));
                        bottomBarText.setText(R.string.msg_pickup_request_accepted);
                        bottomBarText.setEnabled(false);
                        bottomBarText.setVisibility(View.GONE);
                        Utility.setEventTracking(PickUpEventPage.this, "Pickup up detail page", "Pickup up Request Accepted");

                        break;
                    case KEY_REJECTED:
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setText(R.string.msg_pickup_req_declined);
                        request_status.setTextColor(getResources().getColor(R.color.red));
                        //Show bottombar text as Pickup Rejected.
                        bottomBarText.setText(R.string.msg_pickup_req_declined);
                        bottomBarText.setVisibility(View.GONE);
                        Utility.setEventTracking(PickUpEventPage.this, "Pickup up detail page", "Pickup up Request Decline");
                        //bottomBarText.setBackground(getResources().getColor(R.color.red));
                        bottomBarText.setEnabled(false);
                        break;
                    case KEY_REQESTED:
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setText(R.string.msg_pickup_request_pending);
                        request_status.setTextColor(getResources().getColor(R.color.pendingcolor));
                        bottomBarText.setEnabled(false);
                        bottomBarText.setText(R.string.msg_pickup_request_pending);
                        Utility.setEventTracking(PickUpEventPage.this, "Pickup up detail page", "Pickup up Request Pending");
                        break;
                    case KEY_ABANDON:
                        bottomBarText.setEnabled(true);
                        //bottomBarText.setText(R.string.msg_pickup_abandoned);
                        bottomBarText.setText(R.string.txt_request_to_join);
                        Utility.setEventTracking(PickUpEventPage.this, "Pickup up detail page", "Pickup up Request to join");
                        break;
                    case KEY_CANCEL:
                        bottomBarText.setEnabled(false);
                        request_status.setText(R.string.pickup_cancelled_msg);
                        bottomBarText.setText(R.string.pickup_cancelled_msg);
                        bottomBarText.setVisibility(View.GONE);
                        Utility.setEventTracking(PickUpEventPage.this, "Pickup up detail page", "Pickup up Request cancel");
                        break;
                    default:
                        show_status_bar.setVisibility(View.GONE);
                        bottomBarText.setText("Request to join");
                        bottomBarText.setEnabled(true);
                        break;
                }
            }
        } else {
            /*if(isEventExpried){
                bottomBarText.setEnabled(false);
                bottomBarText.setText(R.string.msg_pickup_request_expired);
            }else*/
            if (pay_flag.equalsIgnoreCase("1")) {
                if (spotsize == 1) {
                    bottomBarText.setText("Make Payment");
                    bottomBarText.setEnabled(true);
                } else {
                    bottomBarText.setText("Spots are Filled");
                    bottomBarText.setEnabled(false);
                }

            } else if (pay_flag.equalsIgnoreCase("0")) {
                {
                    if (spotsize == 0) {
                        bottomBarText.setText("Spots are Filled");
                        bottomBarText.setEnabled(false);
                    } else {
                        bottomBarText.setText("Request to join");
                        bottomBarText.setEnabled(true);
                    }

                }
            }

            if (currentUserId.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
                bottomBarText.setVisibility(View.GONE);
            } else {
                bottomBarText.setVisibility(View.VISIBLE);
            }
            if (isBusinessAccount()) {
                bottomBarText.setVisibility(View.GONE);
            }

        }

     /*   if (currentUserId.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
            bottomBarText.setVisibility(View.GONE);
        } else {
            bottomBarText.setVisibility(View.VISIBLE);
        }*/
        paymentStatus(paymentStatus);
    }

    private void requestToJoin() {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("iduser", SharedPref.getInstance().getStringVlue(PickUpEventPage.this, userId));
            if (PickUpCategory.getIdschedule() == null) {
                params.put("idschedule", frompickupPopup);

            } else {
                params.put("idschedule", PickUpCategory.getIdschedule());
            }

            params.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));
            bottomBarText.setEnabled(false);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.startAnimation();


            if (!AppConstants.isGestLogin(PickUpEventPage.this)) {
                Utility.setEventTracking(PickUpEventPage.this, "pickup detail page", "Join Pick Up button on Pick Up detail page");
            } else {
                Utility.setEventTracking(PickUpEventPage.this, "pickup detail page", "Join Pick Up button on Guest login Pick Up detail page");
            }

            RequestHandler.getInstance()
                    .stringRequestVolley(PickUpEventPage.this,
                            AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpEventPage.this, isStaging)) + joinpickup,
                            params, this, 0);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> params = new HashMap<String, String>();
            params.put("iduser", SharedPref.getInstance().getStringVlue(PickUpEventPage.this, userId));
            params.put("idschedule", frompickupPopup);
            params.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));
            bottomBarText.setEnabled(false);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.startAnimation();


            if (!AppConstants.isGestLogin(PickUpEventPage.this)) {
                Utility.setEventTracking(PickUpEventPage.this, "pickup detail page", "Join Pick Up button on Pick Up detail page");
            } else {
                Utility.setEventTracking(PickUpEventPage.this, "pickup detail page", "Join Pick Up button on Guest login Pick Up detail page");
            }

            RequestHandler.getInstance()
                    .stringRequestVolley(PickUpEventPage.this,
                            AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpEventPage.this, isStaging)) + joinpickup,
                            params, this, 0);

            progressWheel.setVisibility(View.GONE);
            progressWheel.stopAnimation();
        }
    }


    private void addListItems(JSONObject jsonObject) {
        items = new ArrayList<FavouriteModel>();
        items.clear();

        if (jsonObject != null) {
            try {
                JSONArray attendingarray = jsonObject.optJSONArray("attending");
                if (attendingarray != null && attendingarray.length() > 0) {

                    for (int i = 0; i < attendingarray.length(); i++) {
                        JSONObject rowjson = attendingarray.optJSONObject(i);
                        items.add(new FavouriteModel(rowjson.optString("idmember"), rowjson.optString("firstname"), rowjson.optString("image")));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        manager = new LinearLayoutManager(PickUpEventPage.this, LinearLayoutManager.HORIZONTAL, false);
        productRecycle.setLayoutManager(manager);

        mAdapter = new AttendingPeoplesAdapter(PickUpEventPage.this, items);
        productRecycle.setAdapter(mAdapter);

        if (items.size() <= 0) {
            productRecycle.setVisibility(View.GONE);
        }

    }

    private void addGoogleMap( double latitude, double longitude) {
        final Double lat,lang;


        googleMap = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.getUiSettings().setScrollGesturesEnabled(false);



        lat=latitude;
        lang=longitude;

        if (PickUpCategory != null) {
            latitude = PickUpCategory.getLatitude();
            longitude = PickUpCategory.getLongitude();
        }else{

            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    ScrollView1.requestDisallowInterceptTouchEvent(true);
                    Intent intent = new Intent(PickUpEventPage.this, PickUpGuest_Map.class);
                    intent.putExtra("lati", lang);
                    intent.putExtra("longi", lat);
                    startActivity(intent);
                }
            });
        }


        if (googleMap != null) {
            LatLng latLng = new LatLng(Latitude, Longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

            googleMap.addMarker(new MarkerOptions().position(
                    new LatLng(Latitude, Longitude)).icon(
                    BitmapDescriptorFactory
                            .fromResource(R.drawable.map_location_btn)));
        }
        final double finalLatitude = latitude;
        final double finalLongitude = longitude;
        if (PickUpCategory != null) {
            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    ScrollView1.requestDisallowInterceptTouchEvent(true);
                    Intent intent = new Intent(PickUpEventPage.this, PickUpGuest_Map.class);
                    intent.putExtra("lati", finalLatitude);
                    intent.putExtra("longi", finalLongitude);
                    startActivity(intent);
                }
            });
        }


        ((ImageView) findViewById(R.id.leftBackImage)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppConstants.isGestLogin(PickUpEventPage.this)) {
                    Utility.setEventTracking(PickUpEventPage.this, "Pick Up detail page", "Back Arrow on Pick Up detail page");
                } else {
                    Utility.setEventTracking(PickUpEventPage.this, "Pick Up detail page", "Back Arrow on Guest login Pick Up detail page");
                }


                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == imgShare) {

            if (!AppConstants.isGestLogin(PickUpEventPage.this)) {
                Utility.setEventTracking(PickUpEventPage.this, "Pick Up detail page", " Share Pick Up button on Pick Up detail page");
            } else {
                Utility.setEventTracking(PickUpEventPage.this, "Pick Up detail page", " Share Pick Up button on Guest login Pick Up detail page");
            }

            if (pickupdetailObj != null && Utility.isNullCheck(pickupdetailObj.optString("sharelink"))) {
                String message = getString(R.string.pickup_share_text);
                String share_imag_url = pickupdetailObj.optString("sharelink");
                AppConstants.showShareAlert(PickUpEventPage.this, "Check out this exciting Pick Up on UACTIV!", message, share_imag_url, shareDialog);
            }
        }

        switch (v.getId()) {
            case R.id.bottomBarText:
                if (!AppConstants.isGestLogin(PickUpEventPage.this)) {
                    if (pay_flag.equalsIgnoreCase("1")) {
                        Log.i("Payment Clicked==>", "Clicked");
                    /*Intent paymentIntent = new Intent(this, MakePaymentActivity.class);
                    paymentIntent.putExtra("pay_button", pay_button);
                    startActivity(paymentIntent);*/

                        //String urlString = "http://124.30.44.228/html_apps/apaynow.html";
                        urlString = "https://www.uactiv.com/payment.php?uid=" + SharedPref.getInstance().getStringVlue(PickUpEventPage.this, userId) + "&pid=" + SheduleId + "&url=" + pay_button;
                        Log.d("paymenturl", urlString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.chrome");
                        try {
                            this.startActivity(intent);
                            finish();
                        } catch (ActivityNotFoundException ex) {
                            Uri uri = Uri.parse(urlString);
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setDataAndType(uri, "text/html");
                            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                            browserIntent.setData(uri);
                            try {
                                startActivity(browserIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        requestToJoin();
                    }
                } else {
                    AppConstants.loginDialog(PickUpEventPage.this);
                }

                //requestToJoin();


                break;
            default:
                break;
        }
    }


    private OnAlertClickListener onAlertClickListener = new OnAlertClickListener() {
        @Override
        public void onClickPositive() {
            isScheduleExists = false;
            requestToJoin();
        }

        @Override
        public void onClickNegative() {
            isScheduleExists = true;
            //finish();
        }
    };

  /*  @Override
    public void onBackPressed() {
        // code here to show dialog
        Intent intent = new Intent(PickUpEventPage.this, MainActivity.class);
        intent.putExtra("adapterfrom", "pickupadap");
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        finish();
        super.onBackPressed();  // optional depending on your needs
    }*/


    /*@Override // Recenly made chage after geting bulk changes.
    public void onBackPressed()
    {
        // code here to show dialog
        Intent intent = new Intent(PickUpEventPage.this, MainActivity.class);
        intent.putExtra("adapterfrom","pickupadap");
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        finish();
        super.onBackPressed();  //
    }*/

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {

        /** flag==0 Requesttojoin API response
         *  flag == 1 pickupdetail API response
         */

        JSONObject jsonObject = null;

        if (!TextUtils.isEmpty(successResponse)) {
            try {
                jsonObject = new JSONObject(successResponse);
                Log.d("responce", new Gson().toJson(jsonObject));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        switch (flag) {

            case 0:
                // Utility.setEventTracking(PickUpEventPage.this, AppConstants.EVENT_TRACKING_ID_REQUEST_PICKUP_TO_JOIN);
                if (jsonObject != null) {
                    try {
                        if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                            bottomBarText.setEnabled(false);
                            show_status_bar.setVisibility(View.VISIBLE);
                            request_status.setText(R.string.msg_pickup_request_pending);
                            request_status.setTextColor(getResources().getColor(R.color.pendingcolor));
                            bottomBarText.setVisibility(View.GONE);
                            bottomBarText.setText("PickUp Request Pending!");
                            PickUpCategory.setStatus(KEY_REQESTED);
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            Home.notifiyArrayListChange.getPickUpModelAddedItems(pickupItemPosition, CategoryItemPosition, PickUpCategory, activity_name);
                            if (isfrom_map) {
                                CreatePickUpMapDetails.notifiyArrayListChange.getPickUpModelAddedItems(pickupItemPosition, CategoryItemPosition, PickUpCategory, activity_name);
                            } else if (CreatePickUpMapDetails.isFlagSingle) {
                                CreatePickUpMapDetails.notifiyArrayListChange.getPickUpModelAddedItems(pickupItemPosition, CreatePickUpMapDetails.single_item_position, PickUpCategory, activity_name);
                            }

                            SharedPref.getInstance().setSharedValue(PickUpEventPage.this, notification_count, jsonObject.optInt("notification_count"));
                            notifyCountChanged();

                            Map<String, String> param = new HashMap<>(3);
                            try {
                                param.put("iduser", SharedPref.getInstance().getStringVlue(PickUpEventPage.this, userId));
                                param.put("screen", "join_pick_up");
                                AppConstants.isread = "";
                                RequestHandler.getInstance().stringRequestVolley(PickUpEventPage.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(PickUpEventPage.this, isStaging)) + "popup_detail", param, PickUpEventPage.this, 2);

                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }


                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            //finish();
                        } else {
                            if ((!(TextUtils.isEmpty(jsonObject.optString(KEY_IS_DUPLICATE)))) && jsonObject.optBoolean(KEY_IS_DUPLICATE)) {
                                //We have duplicate entry
                                Utility.showAlertForExistingTimeSlot(PickUpEventPage.this, getString(R.string.msg_duplicate_request_join), onAlertClickListener);
                            } else {
                                Utility.showToastMessage(PickUpEventPage.this, jsonObject.optString(KEY_MSG));

                            }

                        }
                        bottomBarText.setEnabled(true);
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                }

                break;


            case 1:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        if (jsonObject.optJSONObject(KEY_DETAIL) != null) {
                            updatePickUpUI(jsonObject.optJSONObject(KEY_DETAIL));

                            addListItems(jsonObject.optJSONObject(KEY_DETAIL));
                            try {
                                Longitude = jsonObject.optJSONObject(KEY_DETAIL).getDouble("longitude");
                                Latitude = jsonObject.optJSONObject(KEY_DETAIL).getDouble("latitude");
                                Log.i("Map value==>", Longitude + "==" + Latitude);
                            } catch (Exception e) {

                            }
                            addGoogleMap(Longitude,Latitude);
                        }
                        if (data != null) {
                            Intent pass = new Intent(PickUpEventPage.this, PickUpGuest.class);
                            pass.putExtra("fragment", "fragment");
                            pass.putExtra("idUser", SharedPref.getInstance().getStringVlue(PickUpEventPage.this, userId));
                            pass.putExtra("status", "accepted");
                            pass.putExtra("sstatus", status);
                            pass.putExtra("type", type);
                            pass.putExtra("group_id", group_id);
                            pass.putExtra("idschedule", sheduleId);
                            startActivity(pass);
                            finish();
                        }

                    } else {
                        Utility.showToastMessage(PickUpEventPage.this, jsonObject.optString(KEY_MSG));
                    }
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                break;

            case 2:
                if (jsonObject != null) {
                    if (jsonObject != null) {
                        try {
                            Log.d("session", AppConstants.getSession(PickUpEventPage.this) + "");
                            if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                                String type = jsonObject.optString("type");
                                if (type.equalsIgnoreCase("pick_up") && PickUpEventPage.pickupjoin.isEmpty()) {
                                    AppConstants.genralPopupEventRate(PickUpEventPage.this, 1, jsonObject, this, PickUpEventPage.this, "pickup");
                                    Log.d("show pickup dialog", "");
                                } else if (type.equalsIgnoreCase("promotional") && PickUpEventPage.pickupjoin.isEmpty()) {

                                    if (jsonObject.optString("content").isEmpty()) {
                                        AppConstants.genralPopupEventRate(PickUpEventPage.this, 3, jsonObject, this, PickUpEventPage.this, "pickup");
                                    } else {
                                        AppConstants.genralPopupEventRate(PickUpEventPage.this, 2, jsonObject, this, PickUpEventPage.this, "pickup");
                                    }
                                    //  SharedPref.getInstance().setSharedValue(getActivity(), "APP_SESSION", 1);


                                    Log.d("show pramotional dialog", "");
                                } else if (type.equalsIgnoreCase("rateus") && PickUpEventPage.pickupjoin.isEmpty()) {
                                    if (!SharedPref.getInstance().getBooleanValue(PickUpEventPage.this, "islater")) {
                                        AppConstants.genralPopupEventRate(PickUpEventPage.this, 0, jsonObject, this, PickUpEventPage.this, "pickup");
                                    }


                                }


                            } else {

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                }
                break;
            default:
                break;

        }

    }


    public void paymentStatus(String history_status) {

        if (history_status.equalsIgnoreCase("accepted")) {
            show_status_bar.setVisibility(View.VISIBLE);
            request_status.setText("Request Accepted!");
            bottomBarText.setText("Pick Up Request Accepted!");
            bottomBarText.setVisibility(View.GONE);
            bottomBarText.setEnabled(false);
        }else if(history_status.equalsIgnoreCase("requested")){
            show_status_bar.setVisibility(View.VISIBLE);
            request_status.setText("Request Pending!");
            bottomBarText.setText("Request Pending!");
            bottomBarText.setVisibility(View.GONE);
            bottomBarText.setEnabled(false);
        }


    }

    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(PickUpEventPage.this).sendBroadcast(registrationComplete);
    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        bottomBarText.setEnabled(true);
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }

    private void addListItemsForInvites(JSONObject jsonObject) {

        mInvitedPeoples = new ArrayList<FavouriteModel>();
        mInvitedPeoples.clear();
        if (jsonObject != null) {
            try {
                JSONArray attendingarray = jsonObject.optJSONArray("invited");
                if (attendingarray != null && attendingarray.length() > 0) {
                    mInvitedPeopleLayout.setVisibility(View.VISIBLE);
                    ((CustomTextView) findViewById(R.id.tvInvitesCount)).setText("Invited (" + attendingarray.length() + ")");
                    for (int i = 0; i < attendingarray.length(); i++) {
                        JSONObject rowjson = attendingarray.optJSONObject(i);
                        mInvitedPeoples.add(new FavouriteModel(rowjson.optString("idmember"), rowjson.optString("firstname") + " " + rowjson.optString("lastname"), rowjson.optString("image")));
                    }
                } else {
                    mInvitedPeopleLayout.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        manager = new LinearLayoutManager(PickUpEventPage.this, LinearLayoutManager.HORIZONTAL, false);
        mInvitesRecyclerView.setLayoutManager(manager);
        mInvitesAdapter = new AttendingPeoplesAdapter(PickUpEventPage.this, mInvitedPeoples);
        Log.d("eeee", new Gson().toJson(mInvitedPeoples));
        mInvitesRecyclerView.setAdapter(mInvitesAdapter);


    }
}