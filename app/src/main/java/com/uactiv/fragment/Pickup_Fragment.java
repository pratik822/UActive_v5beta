package com.uactiv.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.felipecsl.gifimageview.library.GifImageView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.github.yasevich.endlessrecyclerview.EndlessRecyclerView;
import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.activity.CreatePickUp;
import com.uactiv.activity.MainActivity;
import com.uactiv.adapter.BuddyAdapter;
import com.uactiv.adapter.PickUpAdapter;
import com.uactiv.application.UActiveApplication;
import com.uactiv.controller.BannerImageListener;
import com.uactiv.controller.GpsNotifier;
import com.uactiv.controller.NotifiyArrayListChange;
import com.uactiv.controller.ResponseListener;
import com.uactiv.location.GPSTracker;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.PickUpCategory;
import com.uactiv.model.PickUpModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.EndlessRecyclerViewNew;
import com.uactiv.utils.LinearLayoutManagerWithSmoothScroller;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Pickup_Fragment extends Fragment implements AppConstants.urlConstants, AppConstants.SharedConstants, ResponseListener, NotifiyArrayListChange, SwipeRefreshLayout.OnRefreshListener, GpsNotifier, EndlessRecyclerView.Pager, EndlessRecyclerViewNew.Pager {


    private static final int TAB_VIEW_PADDING_DIPS = 0;
    //public static CustomAutoCompleteTextView buddyUpSearchAuto;
    public static NotifiyArrayListChange notifiyArrayListChange;
    public int limit = 5;
    public static int currentPageNumber = 0;
    public int BUDDYUPREQUESTCODE = 0;
    public int PICKUPUPREQUESTCODE = 1;
    public int BUDDYUPSEARCHREQUESTCODE = 2;
    String[] activities;
    String skills;
    public static GpsNotifier gpsNotifier;
    EndlessRecyclerViewNew mRecyclerView = null;
    Bundle mybundel;
    JSONArray skillistArray;
    BuddyAdapter buddyupadapter;
    PickUpAdapter pickUpAdapter;
    LinearLayoutManager linearLayoutManager;
    public static ArrayList<BuddyModel> buddyList = new ArrayList<BuddyModel>();
    ArrayList<BuddyModel> search_buddyList = new ArrayList<BuddyModel>();

    static ArrayList<PickUpModel> pickUpArrayList = new ArrayList<PickUpModel>();
    static ArrayList<PickUpModel> search_pickUpArrayList = new ArrayList<PickUpModel>();
    CustomButton createpickup;

    String fromstr = "";

    String TAG = getClass().getSimpleName();
    ArrayList<HashMap<String, String>> pickUpAutoList = new ArrayList<HashMap<String, String>>();
    ArrayList<String> activity_list = new ArrayList<String>();
    String searchString;
    boolean isSearch = false;
    ImageView btnRefreshLocation = null;
    GPSTracker mGpsTracker = null;
    ImageView mSearchClose = null;
    boolean isRefresh = false;
    GifImageView progressWheel = null;
    LinearLayout emptyViewBudddyUp = null;
    LinearLayout emptyViewPickUp = null;
    LinearLayout emptyViewNoInternet = null;
    LinearLayout emptyViewRoot = null;
    ImageView bannerImage = null;
    BannerImageListener bannerImageListener = null;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isBuddyUpListCalled = false;
    double lat = 0, lang = 0;

    int total_pages = 1;
    String fromSetting;

    public int key = 0;
    private boolean isLazy = false;
    private int mTempPageNumber = 0;

    protected Handler handler;
    public boolean search_flag = false;
    public String search_str = "";

    static private int TOTAL_ITEMS_COUNT_BUDDY_UP = 0;
    private int TOTAL_ITEMS_COUNT_BUDDY_UP_SEARCH = 0;
    private ImageView ic_backs;
    ImageView pfilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pickup_, container, false);
        if (!AppConstants.isGestLogin(getActivity())) {
            Utility.setScreenTracking(getActivity(), "Pick up dashboard");
        } else {
            Utility.setScreenTracking(getActivity(), "Guest login Pick up dashboard");
        }


        handler = new Handler();
        gpsNotifier = this;
        mGpsTracker = new GPSTracker(getActivity());
        notifiyArrayListChange = this;
        LinearLayoutManagerWithSmoothScroller ln = new LinearLayoutManagerWithSmoothScroller(getActivity());
        // linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView = (EndlessRecyclerViewNew) rootView.findViewById(R.id.buddylist);
        mybundel = this.getArguments();
        mRecyclerView.setLayoutManager(ln);
        mRecyclerView.setProgressView(R.layout.layout_progress_bar);
        key = 0;
        buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
        mRecyclerView.setAdapter(buddyupadapter);
        mRecyclerView.setPager(this);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.addOnScrollListener(onScrollListener);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        emptyViewBudddyUp = (LinearLayout) rootView.findViewById(R.id.empty_view_buddy_up);
        emptyViewPickUp = (LinearLayout) rootView.findViewById(R.id.empty_view_pick_up);
        emptyViewNoInternet = (LinearLayout) rootView.findViewById(R.id.empty_view_no_internet);
        pfilter = (ImageView) rootView.findViewById(R.id.pfilter);
        emptyViewRoot = (LinearLayout) rootView.findViewById(R.id.emptyViewer);
        swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.red, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressWheel = (GifImageView) rootView.findViewById(R.id.gifLoader);
        Utility.showProgressDialog(getActivity(), progressWheel);
        btnRefreshLocation = (ImageView) rootView.findViewById(R.id.btn_refresh_location);
        ic_backs = (ImageView) rootView.findViewById(R.id.ic_backs);
        mSearchClose = (ImageView) rootView.findViewById(R.id.searchClose);

        getActivtyList();

        Log.d("getbuddyup", new Gson().toJson(buddyList));
        JSONObject jsonObject = new JSONObject();

        if (!AppConstants.isGestLogin(getActivity())) {
            Map<String, String> param = new HashMap<>(3);
            param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
            param.put("screen", "dashboard");
            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "popup_detail", param, this, 6);
        }

        createpickup = (CustomButton) rootView.findViewById(R.id.bottomBarText);
        ic_backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("fromfragment", "hi");
                startActivity(intent);
                getActivity().finish();
            }
        });

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);


        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.getString("skills") != null) {
            skills = bundle.getString("skills").replaceAll("null", "");

            Log.d("myskils", skills.replaceAll("null", ""));
        }
        Bundle extras = getActivity().getIntent().getExtras();
        // Bundle[{fromfragment=hi}]
        if (bundle != null && bundle.getString("setting") != "setting") {
            Log.d(TAG, "bundle");
            Log.d(TAG, "onLoading : " + bundle.getString("navi"));
            Log.d(TAG, "onLoading : " + fromstr);
            fromstr = "pick";
            displayView(1);
        } else if (extras != null) {

            Log.d(TAG, "extras");
            Log.d(TAG, "onLoading : " + extras.getString("adapterfrom"));
            Log.d(TAG, "onLoading : " + fromstr);
            String datas = extras.getString("adapterfrom");
            if (datas != null) {
                if (datas.equals("buddyupadap")) {
                    displayView(0);
                } else if (datas.equals("pickupadap")) {
                    fromstr = "pick";
                    displayView(1);
                }
            }
        } else if (bundle != null && bundle.getString("frompickup") != null) {
            displayView(1);
        } else {
            Log.d(TAG, "else");
            Log.d(TAG, "onLoading : " + fromstr);
            displayView(1);
        }
        pfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pickup_Filter_fragment fragment = new Pickup_Filter_fragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
        createpickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppConstants.isGestLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), CreatePickUp.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    if (mGpsTracker != null) {
                        mGpsTracker.stopUsingGPS();
                    }
                } else {
                    AppConstants.loginDialog(getActivity());
                }

            }
        });


        //Patch Done By Jeeva on 08-06-2016...
        bannerImage = (ImageView) rootView.findViewById(R.id.header);
        bannerImageListener = new MainActivity();

        String bannerUrl = bannerImageListener.getBannerImage();

        if (Utility.isNullCheck(bannerUrl)) {
            UActiveApplication.getInstance().loadBannerImage(bannerUrl, bannerImage);
        }

        //To show buddyup tutorials
        // showBuddyUpDetails();


        return rootView;
    }


    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ?
                    0 : recyclerView.getChildAt(0).getTop();
            swipeRefreshLayout.setEnabled((topRowVerticalPosition >= 0));
            Log.d("topRowVerticalPosition", "" + topRowVerticalPosition);
        }
    };

    public void showPickUPTutorial() {

        String tutorialText = getResources().getString(R.string.tutorial_pick_up);
        if (SharedPref.getInstance().getBooleanValue(getActivity(), isbussiness)) {
            tutorialText = getResources().getString(R.string.tutorial_pick_up_business);
        }

        /*ShowcaseView mShowcaseViewCreatePickup = new ShowcaseView.Builder(getActivity())
                .setTarget(new ViewTarget(btn_pickup))
                .hideOnTouchOutside()
                .setContentText(tutorialText)
                .setContentTextPaint(Utility.getTextPaint(getActivity()))
                .singleShot(AppConstants.TUTORIAL_PICK_UP_ID)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showCreatePickUPTutorial();
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
        mShowcaseViewCreatePickup.setButtonText(getResources().getString(R.string.tutorial_got_it));*/
    }


    public void showCreatePickUPTutorial() {
        ShowcaseView mShowcaseViewCreatePickup = new ShowcaseView.Builder(getActivity())
                .setTarget(new ViewTarget(createpickup))
                .setContentText(getResources().getString(R.string.tutorial_msg_create_pickup))
                .setContentTextPaint(Utility.getTextPaint(getActivity()))
                .singleShot(AppConstants.TUTORIAL_CREATE_PICK_UP_ID)
                .hideOnTouchOutside()
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

    private void showBuddyUpEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        emptyViewRoot.setVisibility(View.VISIBLE);
        emptyViewBudddyUp.setVisibility(View.VISIBLE);
        emptyViewPickUp.setVisibility(View.GONE);
        emptyViewNoInternet.setVisibility(View.GONE);
    }

    private void showPickUpEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        emptyViewRoot.setVisibility(View.VISIBLE);
        emptyViewBudddyUp.setVisibility(View.GONE);
        emptyViewPickUp.setVisibility(View.VISIBLE);
        emptyViewNoInternet.setVisibility(View.GONE);
    }

    private void showInternetEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        emptyViewRoot.setVisibility(View.VISIBLE);
        emptyViewBudddyUp.setVisibility(View.GONE);
        emptyViewPickUp.setVisibility(View.GONE);
        emptyViewNoInternet.setVisibility(View.VISIBLE);
    }

    private void disableEmptyView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        emptyViewRoot.setVisibility(View.GONE);
        emptyViewBudddyUp.setVisibility(View.GONE);
        emptyViewPickUp.setVisibility(View.GONE);
        emptyViewNoInternet.setVisibility(View.GONE);
    }

    private void updateLocationToServer() {

        if (Utility.isConnectingToInternet(getActivity())) {

            if (mGpsTracker.canGetLocation()) {

                if (Utility.ischeckvalidLocation(mGpsTracker)) {

                    Animation fade1 = AnimationUtils.loadAnimation(getActivity(), R.anim.flip);
                    btnRefreshLocation.startAnimation(fade1);
                    Map<String, String> params = new HashMap<String, String>();
                    if (!AppConstants.isGestLogin(getActivity())) {
                        params.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                    } else {
                        params.put("iduser", "0");
                    }

                    params.put("latitude", "" + mGpsTracker.getLatitude());
                    params.put("longitude", "" + mGpsTracker.getLongitude());
                    RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + updatelocation, params, this, 5);
                    fade1.cancel();
                } else {
                    mGpsTracker.showInvalidLocationAlert();
                }
            } else {
                showSettingsAlert(5);
            }
        } else {
            Utility.showInternetError(getActivity());
        }

    }

    private void waitingForGps(final int i) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRecyclerView != null) {
                    switch (i) {
                        case 0:
                            // getbudduylist(limit, currentPageNumber);
                            break;
                        case 1:
                            getpickuplist();
                            break;
                        case 2:
                            //  buddyup_search(search_str, search_flag, limit, currentPageNumber);
                            break;
                        case 4:
                            pickup_search(getActivity(), searchString);
                            break;
                        case 5:
                            updateLocationToServer();
                            break;

                    }
                }
            }
        }, 3000);


    }

    private void getActivtyList() {

        if (Utility.isConnectingToInternet(getActivity())) {
            try {
                Map<String, String> stringMap = new HashMap<>();
                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + getactivityList, stringMap, this, 3);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void pickup_search(Context context, String searchText) {
        //  mRecyclerView.setRefreshing(false);
        if (Utility.isConnectingToInternet(getActivity())) {


            disableEmptyView();
            try {
                Map<String, String> param = new HashMap<>();
                param.put("activityId", skills);
                if (!AppConstants.isGestLogin(getActivity())) {
                    param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                    if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                        param.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                        param.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                    } else {
                        param.put("latitude", "" + mGpsTracker.getLatitude());
                        param.put("longitude", "" + mGpsTracker.getLongitude());
                    }
                    param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                    if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit))) {
                        param.put("radius_limit", SharedPref.getInstance().getStringVlue(getActivity(), radius_limit));
                    } else {
                        param.put("radius_limit", "100");
                    }
                } else {
                    param.put("iduser", "0");
                    param.put("latitude", "" + mGpsTracker.getLatitude());
                    param.put("longitude", "" + mGpsTracker.getLongitude());
                    param.put("user_type", "0");
                    if (AppConstants.getRadius(getActivity()) != null && AppConstants.getRadius(getActivity()) != "") {
                        param.put("radius_limit", AppConstants.getRadius(getActivity()));
                    } else {
                        param.put("radius_limit", "100");
                    }
                }

                Log.d("pickupparam", new Gson().toJson(param));

                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                isSearch = true;

                if (!AppConstants.isGestLogin(getActivity())) {
                    RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + pickupsearch, param, this, 4); // PickupSearch

                } else {
                    RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "pickupsearch_guest", param, this, 4); // PickupSearch

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            showInternetEmptyView();
            // Utility.showInternetError(getActivity());
        }

    }

    private synchronized void getpickuplist() {

        if (Utility.isConnectingToInternet(getActivity())) {

            try {
                disableEmptyView();
                setUpDummyPickUpAdapter();
                Map<String, String> param = new HashMap<>();
                if (!AppConstants.isGestLogin(getActivity())) {
                    param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));

                    if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                        param.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                        param.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                    } else {
                        param.put("latitude", "" + mGpsTracker.getLatitude());
                        param.put("longitude", "" + mGpsTracker.getLongitude());
                    }

                } else {
                    param.put("iduser", "0");
                    if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                        param.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                        param.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                    } else {
                        param.put("latitude", "" + mGpsTracker.getLatitude());
                        param.put("longitude", "" + mGpsTracker.getLongitude());
                    }
                    if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
                        param.put("gender_pref", AppConstants.getGender(getActivity()));
                    } else {
                        param.put("gender_pref", "Both");
                    }

                    if (AppConstants.getRadius(getActivity()) != null && AppConstants.getRadius(getActivity()) != "") {
                        param.put("radius_limit", AppConstants.getRadius(getActivity()));
                    } else {
                        param.put("radius_limit", "100");
                    }
                }
                param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));

                // param.put("iduser", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                if (!isRefresh) {
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                }

                if (!AppConstants.isGestLogin(getActivity())) {
                    RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + pickuplist, param, this, 1);
                } else {
                    RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "pickuplist_guest", param, this, 1);

                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error on PickUp List", "" + e.toString());
            }


        } else {
            showNoInternetEmptyView();
            Utility.showInternetError(getActivity());
        }
    }

    private void setUpDummyPickUpAdapter() {
        pickUpAdapter = new PickUpAdapter(getActivity(), new ArrayList<PickUpModel>());
        mRecyclerView.setAdapter(pickUpAdapter);
    }

    public void showSettingsAlert(final int arg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getActivity().startActivityForResult(intent, arg);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                return;
            }
        });
        alertDialog.show();
    }
   /* private void showBottomLoaderView(){
        if(isLazy){
            mLoadMoreView.setVisibility(View.VISIBLE);
        }
    }*/


    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                isSearch = false;
                if (buddyList.size() == 0) {
                    isRefresh = false;
                    //getbudduylist(limit, currentPageNumber);

                    //   buddyUpSearchAuto.setHint("Search for a person or activity");
                    createpickup.setVisibility(View.GONE);
                    fromstr = "buddy";
                } else {

                    try {
                        fromSetting = this.getArguments().getString("setting");
                        if (fromSetting.equalsIgnoreCase("setting")) {
                            //   getbudduylist(limit, currentPageNumber);
                            //       buddyUpSearchAuto.setHint("Search for a person or activity");
                            createpickup.setVisibility(View.GONE);
                            fromstr = "buddy";

                        }
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                    disableEmptyView();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    //buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
                    mRecyclerView.setAdapter(buddyupadapter);
                    //onSetAdapter();
                    //  buddyUpSearchAuto.setHint("Search for a person or activity");
                    createpickup.setVisibility(View.GONE);
                    fromstr = "buddy";
                    //     showEmptyAlertImage(buddyList.size());

                }
                break;

            case 1:
                fromstr = "pick";
                Log.d(TAG + " pickUp size() :", "" + pickUpArrayList.size());
                isSearch = false;
                if (pickUpArrayList.size() == 0) {
                    isRefresh = false;
                    //disableEmptyView();
                    //  mRecyclerView.setVisibility(View.VISIBLE);
                    if (mybundel != null && mybundel.getString("adapterfrom") != null) {
                        if (mybundel.getString("adapterfrom").equalsIgnoreCase("pickupadap")) {
                            pickup_search(getActivity(), "");
                        }
                    } else if (search_pickUpArrayList.size() > 0) {
                        pickUpAdapter = new PickUpAdapter(getActivity(), search_pickUpArrayList);
                        mRecyclerView.setAdapter(pickUpAdapter);
                    } else {
                        getpickuplist();
                    }


                    //buddyUpSearchAuto.setHint("Select an activity to join");
                    //     buddyUpSearchAuto.setHint("Search for an activity to join");
                    createpickup.setVisibility(View.VISIBLE);
                    fromstr = "pick";

                } else {
                    disableEmptyView();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    pickUpAdapter = new PickUpAdapter(getActivity(), pickUpArrayList);
                    mRecyclerView.setAdapter(pickUpAdapter);
                    //buddyUpSearchAuto.setHint("Select an activity to join");
                    //   buddyUpSearchAuto.setHint("Search for an activity to join");
                    createpickup.setVisibility(View.VISIBLE);
                    fromstr = "pick";
                    //        showEmptyAlertImage(pickUpArrayList.size());
                }

                break;
            case 2:
                fragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("from", "BuddyUp");
                bundle.putString("navi", fromstr);
                bundle.putSerializable("BuddyupList", buddyList);
                bundle.putSerializable("PickupList", buddyList);
                fragment.setArguments(bundle);
                //getActivity().overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.push_out_to_bottom);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.homemain, fragment);
            fragmentTransaction.commit();

        }
    }

    private void setBottomViewLoader(boolean loading) {
        mRecyclerView.setRefreshing(loading);
    }


    private void dismissBottomLoaderView() {
          /*  isLazy = false;
            mLoadMoreView.setVisibility(View.GONE);*/
       /* if (isLazy) {
            if(key ==0){
                buddyupadapter.notifyItemRemoved(buddyList.size());
            }else if(key ==1){
                buddyupadapter.notifyItemRemoved(search_buddyList.size());
            }
        }*/
        isLazy = false;
        //buddyupadapter.setLoaded();
    }

    private void resetPage() {
        currentPageNumber = 0;
        total_pages = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("destory", "destory");
        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
            getActivity().stopService(new Intent(getActivity(), GPSTracker.class));
        }


    }


    @Override
    public void onRefresh() {
        //  buddyUpSearchAuto.setText("");
//        mSearchClose.setVisibility(View.INVISIBLE);
        if (fromstr != null) {
            isRefresh = true;
            try {
                Pickup_Filter_fragment.mytempList1_.clear();
                Pickup_Filter_fragment.myCount_.clear();

            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            if (fromstr.equals("pick")) {
                getpickuplist();
            } else {
                //mTempPageNumber = currentPageNumber;
                isSearch = false;
                key = 0;
                currentPageNumber = 0;
                buddyList = new ArrayList<>();

            }
        }
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public boolean shouldLoad() {
        boolean shouldLoad = false;

        mRecyclerView.setRefreshing(false);
        //  Log.d(TAG, "shouldLoad : " + shouldLoad);
        return shouldLoad;
    }

    @Override
    public void loadNextPage() {
        Log.d(TAG, "CurrentPosition : " + currentPageNumber);


    }

    @Override
    public void GpsNotifier(int i) {
        try {
            // mGpsTracker = new GPSTracker(getActivity());
            if (mRecyclerView != null) {
                waitingForGps(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {
        Log.e(TAG, successResponse);
        Log.e("jsonresponce", new Gson().toJson(successResponse));

        /** flag == 0 for buddyuplist APi response
         *  flag ==1 for pickuplist api response.
         *  flag ==2 for search buddyup api response
         *  flag ==3 for activity api response
         *  flag ==4 for pick search api response
         *  flag ==6 for Popup details
         */

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(successResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (flag) {
            case 0:
                dismissBottomLoaderView();
                Utility.setEventTracking(getActivity(), "", AppConstants.EVENT_TRACKING_ID_BUDDY_UP);
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        try {
                            JSONArray jsonArrayBuddy = jsonObject.optJSONArray(KEY_DETAIL);
                            if (jsonArrayBuddy != null) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                ResponseHandler.getInstance().storeBusinessLocations(getActivity(), jsonObject);
                                JSONObject page = jsonObject.optJSONObject("page");
                                if (page != null) {
                                    //  totalItems = page.optInt("total_items");
                                    TOTAL_ITEMS_COUNT_BUDDY_UP = page.optInt("total_items");
                                    currentPageNumber = page.optInt("current_page");
                                    Log.d(TAG, "LastPage " + currentPageNumber);
                                    currentPageNumber = currentPageNumber + 1;

                                    total_pages = page.optInt("total_pages");
                                    Log.d(TAG, "isRefresh : " + isRefresh);
                                    Log.d(TAG, "NextPage " + currentPageNumber);
                                    Log.d(TAG, "Page : " + page.toString());
                                }
                                if (!isRefresh) {
                                    buddyList.addAll(ResponseHandler.getInstance().storeBuddyList(jsonArrayBuddy));
                                    buddyupadapter.notifyDataSetChanged();
                                } else {
                                    buddyList = new ArrayList<>();
                                    buddyList.addAll(ResponseHandler.getInstance().storeBuddyList(jsonArrayBuddy));
                                    buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
                                    Log.d("getbuddies", new Gson().toJson(buddyList));
                                    mRecyclerView.setAdapter(buddyupadapter);
                                    buddyupadapter.notifyDataSetChanged();
                                }

                                Log.e(TAG + "notification_count", "" + jsonObject.optInt("notification_count"));
                                SharedPref.getInstance().setSharedValue(getActivity(), notification_count, jsonObject.optInt("notification_count"));
                                SharedPref.getInstance().setSharedValue(getActivity(), schedule_count, jsonObject.optInt("schedule_count"));
                                notifyCountChanged();


                            }
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            isLazy = false;

                        } catch (Exception e) {
                            e.printStackTrace();
                            //   showEmptyAlertImage(buddyList.size());
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        }

                    } else {
                        //  showEmptyAlertImage(buddyList.size());
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }

                }

                if (buddyList == null || buddyList.size() == 0) {
                    showBuddyUpEmptyView();
                } else {
                    disableEmptyView();
                }
                isSearch = false;
                isBuddyUpListCalled = false;
                isLazy = false; // lazy loader disable
                isRefresh = false;
                setBottomViewLoader(false);
                break;

            case 1:
                setBottomViewLoader(false);
                Utility.setEventTracking(getActivity(), "", AppConstants.EVENT_TRACKING_ID_PICK_UP);
                Log.d("mypickupresponce", new Gson().toJson(jsonObject));
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        try {
                            JSONObject jsonpickupObj = jsonObject.getJSONObject(KEY_DETAIL);
                            if (jsonpickupObj != null) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                pickUpArrayList = ResponseHandler.getInstance().storePickupList(getActivity(), jsonObject);//jsonpickupObj
                                Log.e("Pickups List = ", "" + pickUpArrayList.size());
                                pickUpAdapter = new PickUpAdapter(getActivity(), pickUpArrayList);
                                mRecyclerView.setAdapter(pickUpAdapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                }


                if (pickUpArrayList == null || pickUpArrayList.size() == 0) {
                    showPickUpEmptyView();
                } else {
                    disableEmptyView();
                }

                isSearch = false;

                break;


            case 2:
                dismissBottomLoaderView();
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        try {
                            JSONArray jsonArrayBuddy = jsonObject.getJSONArray(KEY_DETAIL);
                            if (jsonArrayBuddy != null) {
                                Log.d(TAG, "search result size : " + jsonArrayBuddy.length());
                                mRecyclerView.setVisibility(View.VISIBLE);
                                ResponseHandler.getInstance().storeBusinessLocations(getActivity(), jsonObject);
                                JSONObject page = jsonObject.optJSONObject("page");
                                Log.d(TAG, "Page Search : " + String.valueOf(page));
                                if (page != null) {
                                    Log.d(TAG, "********** search **********");
                                    //  totalItems = page.optInt("total_items");
                                    TOTAL_ITEMS_COUNT_BUDDY_UP_SEARCH = page.optInt("total_items");
                                    currentPageNumber = page.optInt("current_page");
                                    Log.d(TAG, "LastPage " + currentPageNumber);
                                    currentPageNumber = currentPageNumber + 1;
                                    Log.d(TAG, "NextPage " + currentPageNumber);
                                    total_pages = page.optInt("total_pages");
                                }

                                key = 1;
                                search_buddyList.addAll(ResponseHandler.getInstance().storeBuddyList(jsonArrayBuddy));
                                if (buddyupadapter == null) {
                                    buddyupadapter = new BuddyAdapter(getActivity(), search_buddyList);
                                    mRecyclerView.setAdapter(buddyupadapter);
                                } else {
                                    buddyupadapter.addNewItemList(search_buddyList);
                                }

                                Log.d(TAG, "adapter result size : " + search_buddyList.size());
                                Log.d(TAG, "key : " + key);

                            }
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        }


                    } else {
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                }
                if (search_buddyList == null || search_buddyList.size() == 0) {
                    showBuddyUpEmptyView();
                } else {
                    disableEmptyView();
                }
                isLazy = false; // lazy loader disable
                setBottomViewLoader(false);
                break;

            case 3:
                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        JSONArray skillistArray = jsonObject.optJSONArray(KEY_DETAIL);
                        if (skillistArray != null && skillistArray.length() > 0) {
                            SharedPref.getInstance().setSharedValue(getActivity(), Api_skill_list, skillistArray.toString());
                            //searchKeySkills(skillistArray);
                        }


                    }
                }
                if (mGpsTracker != null) {
                    mGpsTracker.stopUsingGPS();
                }
                break;

            case 4:
                setBottomViewLoader(false);
                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        JSONObject jsonObject1 = jsonObject.optJSONObject(KEY_DETAIL);
                        try {

                            if (jsonObject1 != null) {


                                String skills = jsonObject.getString("skills");
                                String[] myskills = skills.split(",");
                                Log.d("myskills", new Gson().toJson(myskills));
                                search_pickUpArrayList = ResponseHandler.getInstance().storePickupList(getActivity(), jsonObject);
                                pickUpAdapter = new PickUpAdapter(getActivity(), search_pickUpArrayList);
                                mRecyclerView.setAdapter(pickUpAdapter);
                                mRecyclerView.setVisibility(View.VISIBLE);
                                progressWheel.stopAnimation();
                                progressWheel.setVisibility(View.GONE);
                                ;
                                //  }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    /*    skillistArray = jsonObject1.optJSONArray(searchString.trim());
                        search_pickUpArrayList = ResponseHandler.getInstance().getPickupSearch(skillistArray, searchString);
                        pickUpAdapter = new PickUpAdapter(getActivity(), search_pickUpArrayList);
                        mRecyclerView.setAdapter(pickUpAdapter);*/

                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                        isSearch = true;
                    }
                }
                if (search_pickUpArrayList == null || search_pickUpArrayList.size() == 0) {
                    showPickUpEmptyView();
                } else {
                    disableEmptyView();
                }
                break;

            case 5:

                //get the android imageurl and set banner image from JSON response using key called "android_img".
                //Patch Done by Jeeva on 06-08-2016...
                /*{"details":{"android_img":"app\/images\/city\/drawable-hdpi\/uactive.png","img":"app\/images\/city\/uactive.png","name":"Chennai"},"result":true}*/
                if (jsonObject != null) {
                    Boolean aBoolean = jsonObject.optBoolean(resultcheck);
                    Log.e(" Valid ", "" + aBoolean);
                    if (aBoolean) {
                        JSONObject detailsObject = jsonObject.optJSONObject(KEY_DETAIL);
                        if (detailsObject != null) {
                            if (detailsObject.has("android_img")) {
                                String url = detailsObject.optString("android_img");
                                if (Utility.isNullCheck(url)) {
                                    String bannerUrl = AppConstants.getBaseImgUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + url;
                                    if (Utility.isNullCheck(bannerUrl)) {
                                        /** patch done by moorthy 13-09-2016
                                         *
                                         */
                                        UActiveApplication.getInstance().loadBannerImage(bannerUrl, bannerImage);
                                        /** patch done by moorthy 13-09-2016
                                         *
                                         */
                                    }
                                }
                            }
                        }
                    }
                }
                //Patch Done by Jeeva on 06-08-2016...
                break;

            case 6:
                if (jsonObject != null) {
                    if (jsonObject != null) {
                        Log.d("session", AppConstants.getSession(getActivity()) + "");
                        if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                            String type = jsonObject.optString("type");
                            if (type.equalsIgnoreCase("pick_up") && AppConstants.isread.isEmpty()) {
                                AppConstants.genralPopup(getActivity(), 1, jsonObject, this);
                                Log.d("show pickup dialog", "");
                            } else if (type.equalsIgnoreCase("promotional") && AppConstants.isread.isEmpty()) {

                                if (jsonObject.optString("content").isEmpty()) {
                                    AppConstants.genralPopup(getActivity(), 3, jsonObject, this);
                                } else {
                                    AppConstants.genralPopup(getActivity(), 2, jsonObject, this);
                                }
                                //  SharedPref.getInstance().setSharedValue(getActivity(), "APP_SESSION", 1);


                                Log.d("show pramotional dialog", "");
                            } else if (type.equalsIgnoreCase("rateus") && AppConstants.isread.isEmpty()) {
                                if (!SharedPref.getInstance().getBooleanValue(getActivity(), "islater")) {
                                    AppConstants.genralPopup(getActivity(), 0, jsonObject, this);
                                }


                            }


                        }
           /*             if(AppConstants.getSession(getActivity())>=3){
                            SharedPref.getInstance().setSharedValue(getActivity(), "APP_SESSION", 0);
                        }
                        if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                            String type = jsonObject.optString("type");
                            if (type.equalsIgnoreCase("pick_up")) {
                                if (AppConstants.getSession(getActivity())== 1 || AppConstants.getSession(getActivity()) == 2) {
                                    AppConstants.genralPopup(getActivity(), 1, jsonObject);
                                  //  SharedPref.getInstance().setSharedValue(getActivity(), "APP_SESSION",1);
                                }

                                Log.d("show pickup dialog", "");
                            } else if (type.equalsIgnoreCase("promotional")) {
                                if (AppConstants.getSession(getActivity()) == 1 || AppConstants.getSession(getActivity()) == 2) {
                                    if (jsonObject.optString("content").isEmpty()) {
                                        AppConstants.genralPopup(getActivity(), 3, jsonObject);
                                    } else {
                                        AppConstants.genralPopup(getActivity(), 2, jsonObject);
                                    }
                                  //  SharedPref.getInstance().setSharedValue(getActivity(), "APP_SESSION", 1);

                                }


                                Log.d("show pramotional dialog", "");
                            } else if (type.equalsIgnoreCase("rateus")) {
                                if (AppConstants.getSession(getActivity()) == 1 || AppConstants.getSession(getActivity()) == 2) {
                                    AppConstants.genralPopup(getActivity(), 0, jsonObject);
                                //    SharedPref.getInstance().setSharedValue(getActivity(), "APP_SESSION", 0);

                                }


                            }


                        }*/
                    }

                }

                break;


            case 7:
                Log.d("getresp", new Gson().toJson(jsonObject));
                break;


            default:

                break;
        }
        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
        }

    }

    private void showNoInternetEmptyView() {
        emptyViewPickUp.setVisibility(View.GONE);
        emptyViewBudddyUp.setVisibility(View.GONE);
        emptyViewNoInternet.setVisibility(View.VISIBLE);
    }

    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(registrationComplete);
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
    public void getBuddyModelAddedItems(int position, BuddyModel model) {

    }

    @Override
    public void getPickUpModelAddedItems(int pickUpItemposition, int categoryItemPosition, PickUpCategory model, String activityName) {
        Log.e("isSearch", ": " + isSearch);
        if (isSearch) {

            if (model != null && activityName != null) {

                ArrayList<PickUpCategory> templist = search_pickUpArrayList.get(pickUpItemposition).getPickUpCategoryList();
                templist.set(categoryItemPosition, model);
                PickUpModel pickUpModel = new PickUpModel();
                pickUpModel.setActivityname(activityName);
                pickUpModel.setPickUpCategoryList(templist);
                search_pickUpArrayList.set(pickUpItemposition, pickUpModel);
                pickUpAdapter.notifyDataSetChanged();
            }

        } else {

            if (model != null && activityName != null) {

                ArrayList<PickUpCategory> templist = pickUpArrayList.get(pickUpItemposition).getPickUpCategoryList();
                templist.set(categoryItemPosition, model);
                PickUpModel pickUpModel = new PickUpModel();
                pickUpModel.setActivityname(activityName);
                pickUpModel.setPickUpCategoryList(templist);
                pickUpArrayList.set(pickUpItemposition, pickUpModel);
                pickUpAdapter.notifyDataSetChanged();
            }
        }
    }
}
