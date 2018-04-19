package com.uactiv.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.felipecsl.gifimageview.library.GifImageView;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
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
import com.uactiv.utils.UActivePref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomAutoCompleteTextView;
import com.uactiv.widgets.CustomButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Home extends Fragment implements AppConstants.urlConstants, AppConstants.SharedConstants, ResponseListener, NotifiyArrayListChange, SwipeRefreshLayout.OnRefreshListener, GpsNotifier, EndlessRecyclerView.Pager, EndlessRecyclerViewNew.Pager {

    private static final int TAB_VIEW_PADDING_DIPS = 0;
    public static CustomAutoCompleteTextView buddyUpSearchAuto;
    public static NotifiyArrayListChange notifiyArrayListChange;
    public int limit = 5;
    public static int currentPageNumber = 0;
    public int BUDDYUPREQUESTCODE = 0;
    public int PICKUPUPREQUESTCODE = 1;
    public int BUDDYUPSEARCHREQUESTCODE = 2;
    String[] activities;
    Button btn_buddyup, btn_pickup;
    public static GpsNotifier gpsNotifier;
    EndlessRecyclerViewNew mRecyclerView = null;

    BuddyAdapter buddyupadapter;
    PickUpAdapter pickUpAdapter;
    LinearLayoutManager linearLayoutManager;
    public static ArrayList<BuddyModel> buddyList = new ArrayList<BuddyModel>();
    ArrayList<BuddyModel> search_buddyList = new ArrayList<BuddyModel>();

    static ArrayList<PickUpModel> pickUpArrayList = new ArrayList<PickUpModel>();
    ArrayList<PickUpModel> search_pickUpArrayList = new ArrayList<PickUpModel>();
    CustomButton createpickup;

    String fromstr = "";
    String navigation = "";
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


    public Home() {
        // Required empty public constructor
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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_layout, container, false);
        if (!AppConstants.isGestLogin(getActivity())) {
            Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_HOME);
        } else {
            Utility.setScreenTracking(getActivity(), "Guest login Buddy Up Dashboard");
        }

        handler = new Handler();
        gpsNotifier = this;
        mGpsTracker = new GPSTracker(getActivity());
        notifiyArrayListChange = this;
        LinearLayoutManagerWithSmoothScroller ln = new LinearLayoutManagerWithSmoothScroller(getActivity());
        // linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView = (EndlessRecyclerViewNew) rootView.findViewById(R.id.buddylist);
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
        emptyViewRoot = (LinearLayout) rootView.findViewById(R.id.emptyViewer);
        swipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.red, R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressWheel = (GifImageView) rootView.findViewById(R.id.gifLoader);
        Utility.showProgressDialog(getActivity(), progressWheel);
        btnRefreshLocation = (ImageView) rootView.findViewById(R.id.btn_refresh_location);
        buddyUpSearchAuto = (CustomAutoCompleteTextView) rootView.findViewById(R.id.searchs);
        buddyUpSearchAuto.setCursorVisible(false);
        mSearchClose = (ImageView) rootView.findViewById(R.id.searchClose);
        getActivtyList();
        buddyUpSearchAuto.setImeOptions(EditorInfo.IME_ACTION_DONE);
        Log.d("getbuddyup", new Gson().toJson(buddyList));
        JSONObject jsonObject = new JSONObject();

        if (!AppConstants.isGestLogin(getActivity())) {
            Map<String, String> param = new HashMap<>(3);
            param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
            param.put("screen", "dashboard");
            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "popup_detail", param, this, 6);
        }

        buddyUpSearchAuto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!AppConstants.isGestLogin(getActivity()) == true) {
                    if (buddyUpSearchAuto.length() > 0) {
                        if (!AppConstants.isGestLogin(getActivity())) {
                            Utility.setEventTracking(getActivity(), "Buddy Up Dashboard", "Search Box on Buddy up dashboard");
                        } else {
                            Utility.setEventTracking(getActivity(), "Buddy Up Dashboard", "Search Box on Guest login Buddy up dashboard");
                        }

                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                            if (fromstr.equals("buddy")) {
                                search_flag = false;
                                search_str = buddyUpSearchAuto.getText().toString().toLowerCase(Locale.getDefault());
                                mTempPageNumber = currentPageNumber;
                                resetPage();
                                search_buddyList = new ArrayList<BuddyModel>();
                                buddyup_search(search_str, search_flag, limit, currentPageNumber);
                            } else if (fromstr.equals("pick")) {
                                searchString = buddyUpSearchAuto.getText().toString().toLowerCase(Locale.getDefault());
                                pickup_search(getActivity(), buddyUpSearchAuto.getText().toString().toLowerCase(Locale.getDefault()));
                            }
                            return true;
                        }
                    }

                } else {

                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        if (fromstr.equals("buddy")) {
                            search_flag = false;
                            search_str = buddyUpSearchAuto.getText().toString().toLowerCase(Locale.getDefault());
                            mTempPageNumber = currentPageNumber;
                            resetPage();
                            search_buddyList = new ArrayList<BuddyModel>();
                            buddyup_search(search_str, search_flag, limit, currentPageNumber);
                        } else if (fromstr.equals("pick")) {
                            searchString = buddyUpSearchAuto.getText().toString().toLowerCase(Locale.getDefault());
                            pickup_search(getActivity(), buddyUpSearchAuto.getText().toString().toLowerCase(Locale.getDefault()));
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mSearchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromstr.equalsIgnoreCase("buddy")) {
                    currentPageNumber = mTempPageNumber;
                }
                AppConstants.hideViewKeyBoard(getActivity(), buddyUpSearchAuto);
                buddyUpSearchAuto.setCursorVisible(false);
                buddyUpSearchAuto.setText(null);

            }
        });


        buddyUpSearchAuto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (fromstr.contains("pick")) {
                    buddyUpSearchAuto.setFocusable(false);
                } else {
                    buddyUpSearchAuto.setCursorVisible(true);
                    buddyUpSearchAuto.setFocusable(true);
                    buddyUpSearchAuto.setFocusableInTouchMode(true);
                }
                return false;
            }
        });


        buddyUpSearchAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Home", "setOnClickListener");
                Log.d("fromstr", ": " + fromstr);
                if (fromstr.contains("pick")) {
                    buddyUpSearchAuto.setCursorVisible(false);
                    AppConstants.hideViewKeyBoard(getActivity(), buddyUpSearchAuto);
                    buddyUpSearchAuto.showDropDown();
                } else if (fromstr.equalsIgnoreCase("buddy")) {
                    buddyUpSearchAuto.setCursorVisible(true);
                }
            }
        });


        buddyUpSearchAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings({"unchecked", "static-access"})
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Home", "setOnItemClickListener");
                if (parent.getAdapter().getItem(position) != null) {
                    HashMap<String, String> hm = (HashMap<String, String>) parent.getAdapter().getItem(position);
                    UActivePref.getInstance().setPickUpItem(hm.get("pickUpNames"));
                    AppConstants.hideKeyBoard(getActivity());
                    getActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                    buddyUpSearchAuto.setText(hm.get("pickUpNames"));
                    if (fromstr.contains("pick")) {
                        searchString = hm.get("pickUpNames");
                        pickup_search(getActivity(), hm.get("pickUpNames"));
                    } else {
                        mTempPageNumber = currentPageNumber;
                        resetPage();
                        search_flag = true;
                        search_str = hm.get("pickUpNames").toLowerCase(Locale.getDefault());
                        search_buddyList = new ArrayList<BuddyModel>();
                        buddyup_search(search_str, search_flag, limit, currentPageNumber);
                    }
                }
            }
        });

        buddyUpSearchAuto.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //buddyUpSearchAuto.setCursorVisible(true);
                if (buddyUpSearchAuto.length() > 0) {
                    buddyUpSearchAuto.setCursorVisible(true);
                    mSearchClose.setVisibility(View.VISIBLE);
                } else {
                    mSearchClose.setVisibility(View.INVISIBLE);
                }


                if (fromstr != null && buddyUpSearchAuto.length() == 0) {
                    isSearch = false;
                    if (fromstr.contains("pick")) {
                        displayView(1);
                    } else {
                        if (buddyUpSearchAuto.length() == 0) {
                            if (buddyList != null) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                key = 0;
                                Log.d(TAG, "mTempPageNumber : " + mTempPageNumber + " mTempTotalItem :" + TOTAL_ITEMS_COUNT_BUDDY_UP);
                                currentPageNumber = mTempPageNumber;


                                buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
                                mRecyclerView.setAdapter(buddyupadapter);
                                if (buddyList.size() > 0) {
                                    emptyViewRoot.setVisibility(View.GONE);
                                    emptyViewBudddyUp.setVisibility(View.GONE);
                                }
                            }

                        }
                    }
                }

                Log.d("Home", "onTextChanged :" + fromstr);
                if (fromstr.equalsIgnoreCase("pick")) {
                    AppConstants.hideViewKeyBoard(getActivity(), buddyUpSearchAuto);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnRefreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE) == USER_TYPE_APP) {
                    updateLocationToServer();
                }
            }
        });


        btn_buddyup = (Button) rootView.findViewById(R.id.button1);
        createpickup = (CustomButton) rootView.findViewById(R.id.bottomBarText);


        btn_buddyup.setBackgroundResource(R.drawable.buttonpressed_btn3_first);
        btn_buddyup.setTextColor(getResources().getColorStateList(R.color.button_text_color));
        btn_buddyup.setAllCaps(false);
        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        btn_buddyup.setPadding(padding, padding, padding, padding);
        btn_buddyup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_BUDDYUPDASH);
                //  AppConstants.genralPopup(getActivity(), 3, jsonObject);
                if (!AppConstants.isGestLogin(getActivity())) {
                    Utility.setEventTracking(getActivity(), "Buddy Up Dashboard", "Buddy up button on Buddy up dashboard");
                } else {
                    Utility.setEventTracking(getActivity(), "Buddy Up Dashboard", "Buddy up button on Guest login Buddy up dashboard");
                }

                AppConstants.hideViewKeyBoard(getActivity(), buddyUpSearchAuto);
                displayView(0);
                buddyUpSearchAuto.getText().clear();

            }
        });


        btn_pickup = (Button) rootView.findViewById(R.id.button3);
        btn_pickup.setBackgroundResource(R.drawable.buttonpressed_btn3_first);
        btn_pickup.setTextColor(getResources().getColorStateList(R.color.button_text_color));
        btn_pickup.setAllCaps(false);

        btn_pickup.setPadding(padding, padding, padding, padding);
        btn_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.setRefreshing(false);
                //  AppConstants.genralPopup(getActivity(), 2, jsonObject);
                if (!AppConstants.isGestLogin(getActivity())) {
                    Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_PICKUPDASH);
                } else {
                    Utility.setScreenTracking(getActivity(), "Guest login Pick Up dashboard");
                }

                if (!AppConstants.isGestLogin(getActivity())) {
                    Utility.setEventTracking(getActivity(), "Pick Up Dashboard", "Pick up button on Pick Up dashboard");
                } else {
                    Utility.setEventTracking(getActivity(), "Pick Up Dashboard", "Pick up button on Guest login Pick Up dashboard");
                }


                buddyUpSearchAuto.setCursorVisible(false);
                fromstr = "pick";
                showPickUPTutorial();
                AppConstants.hideViewKeyBoard(getActivity(), buddyUpSearchAuto);
                buddyUpSearchAuto.getText().clear();
                displayView(1);

            }
        });

        Bundle bundle = this.getArguments();
        Bundle extras = getActivity().getIntent().getExtras();

        if (bundle != null && bundle.getString("setting")!="setting") {
            Log.d(TAG, "bundle");
            Log.d(TAG, "onLoading : " + bundle.getString("navi"));
            Log.d(TAG, "onLoading : " + fromstr);
            navigation = bundle.getString("navi");
             if (navigation.equalsIgnoreCase("buddy")) {
                displayView(0);
            } else if (navigation.equals("pick")) {
                fromstr = "pick";
                displayView(1);
            }
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
        } else {
            Log.d(TAG, "else");
            Log.d(TAG, "onLoading : " + fromstr);
            displayView(0);
        }

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
        onChangeBanner();
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


    public void showBuddyUpDetails() {

        String tutorialText = getResources().getString(R.string.tutorial_buddy_up);
        if (SharedPref.getInstance().getBooleanValue(getActivity(), isbussiness)) {
            tutorialText = getResources().getString(R.string.tutorial_buddy_up_business);
        }
        ShowcaseView mShowcaseView = new ShowcaseView.Builder(getActivity())
                .setTarget(new ViewTarget(btn_buddyup))
                .hideOnTouchOutside()
                .setContentText(tutorialText)
                .setContentTextPaint(Utility.getTextPaint(getActivity()))
                .singleShot(AppConstants.TUTORIAL_BUDDY_UP_ID)
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        mShowcaseView.setButtonText(getActivity().getString(R.string.tutorial_got_it));
    }


    private void resetPage() {
        currentPageNumber = 0;
        total_pages = 0;
    }

    private void onChangeBanner() {

        double latitude = 0.0, longitude = 0.0;
        if (mGpsTracker != null) {
            latitude = mGpsTracker.getLatitude();
            longitude = mGpsTracker.getLongitude();
            Log.e("Error on Valid City", "" + latitude);
        }

        if (Utility.isConnectingToInternet(getActivity())) {
            try {
                Map<String, String> param = new HashMap<>(3);
                param.put("city",AppConstants.getCityByLocation(getActivity(),latitude,longitude));
                Log.e("Valid City", "" + AppConstants.getCityByLocation(getActivity(),latitude,longitude));
                param.put("size", getResources().getString(R.string.device_reso));
                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + validCity, param, this, 5);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error on Valid City", "" + e.toString());
            }

        } else {
            Utility.showInternetError(getActivity());
        }
    }

    public void showPickUPTutorial() {

        String tutorialText = getResources().getString(R.string.tutorial_pick_up);
        if (SharedPref.getInstance().getBooleanValue(getActivity(), isbussiness)) {
            tutorialText = getResources().getString(R.string.tutorial_pick_up_business);
        }

        ShowcaseView mShowcaseViewCreatePickup = new ShowcaseView.Builder(getActivity())
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
        mShowcaseViewCreatePickup.setButtonText(getResources().getString(R.string.tutorial_got_it));
    }

    public void showCreatePickUPTutorial(){
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
                            getbudduylist(limit, currentPageNumber);
                            break;
                        case 1:
                            getpickuplist();
                            break;
                        case 2:
                            buddyup_search(search_str, search_flag, limit, currentPageNumber);
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

            if (mGpsTracker.canGetLocation()) {

                if (Utility.ischeckvalidLocation(mGpsTracker)) {

                    disableEmptyView();
                    try {
                        Map<String, String> param = new HashMap<>();
                        param.put("activity", "" + searchText.trim());
                        if (!AppConstants.isGestLogin(getActivity())) {
                            param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                            param.put("latitude", "" + mGpsTracker.getLatitude());
                            param.put("longitude", "" + mGpsTracker.getLongitude());
                            param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
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


                        progressWheel.setVisibility(View.VISIBLE);
                        progressWheel.startAnimation();
                        isSearch = true;
                        btn_buddyup.setEnabled(false);
                        btn_pickup.setEnabled(false);
                        if (!AppConstants.isGestLogin(getActivity())) {
                            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + pickupsearch, param, this, 4); // PickupSearch

                        } else {
                            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "pickupsearch_guest", param, this, 4); // PickupSearch

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mGpsTracker.showInvalidLocationAlert();
                }
            } else {

                showSettingsAlert(4);
            }

        } else {
            showInternetEmptyView();
            // Utility.showInternetError(getActivity());
        }

    }


    public void buddyup_search(String searchText, boolean isByActivity, int limit, int currposition) {

        if (Utility.isConnectingToInternet(getActivity())) {
            

            if (mGpsTracker.canGetLocation()) {
                if (Utility.ischeckvalidLocation(mGpsTracker)) {
                    disableEmptyView();
                    try {
                        Map<String, String> param = new HashMap<>();
                        if (!AppConstants.isGestLogin(getActivity())) {
                            param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                            param.put("latitude", "" + mGpsTracker.getLatitude());
                            param.put("longitude", "" + mGpsTracker.getLongitude());
                            param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                            param.put("limit", "" + limit);
                            param.put("page", "" + currposition);
                        } else {
                            param.put("iduser", "0");
                            param.put("latitude", "" + mGpsTracker.getLatitude());
                            param.put("longitude", "" + mGpsTracker.getLongitude());
                            param.put("limit", "" + limit);
                            param.put("user_type", "0");
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

                        if (isByActivity) {
                            param.put("search_name", "");
                            param.put("search_activity", searchText.trim());
                        } else {
                            param.put("search_name", searchText.trim());
                            param.put("search_activity", "");
                        }

                        isSearch = true;
                        if (currposition == 0) {
                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                        }
                        btn_buddyup.setEnabled(false);
                        btn_pickup.setEnabled(false);
                        // showBottomLoaderView();
                        if (!AppConstants.isGestLogin(getActivity())) {
                            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + buddysearch, param, this, 2);

                        } else {
                            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "search_guest", param, this, 2);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mGpsTracker.showInvalidLocationAlert();
                }
            } else {

                showSettingsAlert(BUDDYUPSEARCHREQUESTCODE);
            }
        } else {
            showInternetEmptyView();
            // Utility.showInternetError(getActivity());
        }

    }

    private void searchKeySkills(JSONArray skillistArray) {


        String skillSet = SharedPref.getInstance().getStringVlue(getActivity(), Api_skill_list);

        //  Log.e("skillSet",":"+skillSet);

        if (skillistArray != null) {

            try {
                if (skillistArray != null && skillistArray.length() > 0 && activity_list.size() <= 0) {

                    activity_list.clear();

                    for (int k = 0; k < skillistArray.length(); k++) {
                        activity_list.add(skillistArray.optJSONObject(k).optString("activity"));
                    }

                    activities = new String[activity_list.size()];
                    activities = activity_list.toArray(activities);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (activities != null && activities.length > 0) {

                for (int i = 0; i < activities.length; i++) {
                    HashMap<String, String> pickUpMap = new HashMap<String, String>();
                    pickUpMap.put("pickUpNames", activities[i]);
                    pickUpAutoList.add(pickUpMap);
                }
                String[] pickUpFrom = {"pickUpNames"};
                int[] pickUpTo = {R.id.txt};

                try {
                    if (buddyUpSearchAuto != null) {
                        SimpleAdapter pickUpSearchAdapter = new SimpleAdapter(getActivity(), pickUpAutoList, R.layout.autocomplete_layout, pickUpFrom, pickUpTo);
                        buddyUpSearchAuto.setAdapter(pickUpSearchAdapter);
                    }
                } catch (NullPointerException e) {
                    //e.getMessage();
                }
            }
        }


    }

    private synchronized void getpickuplist() {

        if (Utility.isConnectingToInternet(getActivity())) {
            if (mGpsTracker.canGetLocation()) {
                if (Utility.ischeckvalidLocation(mGpsTracker)) {
                    try {
                        disableEmptyView();
                        setUpDummyPickUpAdapter();
                        Map<String, String> param = new HashMap<>();
                        if (!AppConstants.isGestLogin(getActivity())) {
                            param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                            param.put("latitude", "" + mGpsTracker.getLatitude());
                            param.put("longitude", "" + mGpsTracker.getLongitude());
                        } else {
                            param.put("iduser", "0");
                            param.put("latitude", "" + mGpsTracker.getLatitude());
                            param.put("longitude", "" + mGpsTracker.getLongitude());
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
                        btn_buddyup.setEnabled(false);
                        btn_pickup.setEnabled(false);
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
                    mGpsTracker.showInvalidLocationAlert();
                }
            } else {
                showSettingsAlert(PICKUPUPREQUESTCODE);
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

    public synchronized void getbudduylist(int limit, int currposition) {


        Log.d(TAG, "TotalPages :" + total_pages + " current Position : " + currposition);

        /** this to condition for block request once all pages are loaded
         *
         */


        if (Utility.isConnectingToInternet(getActivity())) {
            if (mGpsTracker.canGetLocation()) {
                if ((lat > 0 || lang > 0) || (Utility.ischeckvalidLocation(mGpsTracker)) && !isBuddyUpListCalled) {// && currentPageNumber < total_pages
                    try {
                        lat = mGpsTracker.getLatitude();
                        lang = mGpsTracker.getLongitude();
                        isBuddyUpListCalled = true;
                        disableEmptyView();
                        Map<String, String> param = new HashMap<>();
                        if (!AppConstants.isGestLogin(getActivity())) {
                            param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                            param.put("latitude", "" + lat);
                            param.put("longitude", "" + lang);
                            param.put("limit", "" + limit);
                            param.put("page", "" + (currposition));
                            param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
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
                        } else {
                            param.put("iduser", "0");
                            param.put("latitude", "" + lat);
                            param.put("longitude", "" + lang);
                            param.put("limit", "" + limit);
                            param.put("page", "" + (currposition));
                            param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
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


                        if (!isRefresh && !isLazy) {
                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                        }
                        btn_buddyup.setEnabled(false);
                        btn_pickup.setEnabled(false);
                        // showBottomLoaderView();
                        if (!AppConstants.isGestLogin(getActivity())) {
                            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + buddyuplist, param, this, 0);

                        } else {
                            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "buddyuplist_guest", param, this, 0);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mGpsTracker.showInvalidLocationAlert();
                }
            } else {
                showSettingsAlert(BUDDYUPREQUESTCODE);
            }
        } else {
            showNoInternetEmptyView();
            Utility.showInternetError(getActivity());
        }
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

    @Override
    public void onResume() {
        super.onResume();
        //  mGpsTracker = new GPSTracker(getActivity());
    }

    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                isSearch = false;
                if (buddyList.size() == 0) {
                    isRefresh = false;
                    getbudduylist(limit, currentPageNumber);
                    btn_buddyup.setSelected(true);
                    btn_pickup.setSelected(false);
                    buddyUpSearchAuto.setHint("Search for a person or activity");
                    createpickup.setVisibility(View.GONE);
                    fromstr = "buddy";
                } else {

                    try {
                        fromSetting= this.getArguments().getString("setting");
                        if(fromSetting.equalsIgnoreCase("setting")){
                            getbudduylist(limit, currentPageNumber);
                            btn_buddyup.setSelected(true);
                            btn_pickup.setSelected(false);
                            buddyUpSearchAuto.setHint("Search for a person or activity");
                            createpickup.setVisibility(View.GONE);
                            fromstr = "buddy";

                        }
                    }catch (NullPointerException ex){
                        ex.printStackTrace();
                    }

                    disableEmptyView();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    //buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
                    mRecyclerView.setAdapter(buddyupadapter);
                    //onSetAdapter();
                    btn_buddyup.setSelected(true);
                    btn_pickup.setSelected(false);
                    buddyUpSearchAuto.setHint("Search for a person or activity");
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
                    getpickuplist();
                    btn_buddyup.setSelected(false);
                    btn_pickup.setSelected(true);
                    //buddyUpSearchAuto.setHint("Select an activity to join");
                    buddyUpSearchAuto.setHint("Search for an activity to join");
                    createpickup.setVisibility(View.VISIBLE);
                    fromstr = "pick";

                } else {
                    disableEmptyView();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    pickUpAdapter = new PickUpAdapter(getActivity(), pickUpArrayList);
                    mRecyclerView.setAdapter(pickUpAdapter);
                    btn_buddyup.setSelected(false);
                    btn_pickup.setSelected(true);
                    //buddyUpSearchAuto.setHint("Select an activity to join");
                    buddyUpSearchAuto.setHint("Search for an activity to join");
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


    @Override
    public void successResponse(String successResponse, int flag) {

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
                btn_buddyup.setEnabled(true);
                btn_pickup.setEnabled(true);
                isSearch = false;
                isBuddyUpListCalled = false;
                isLazy = false; // lazy loader disable
                isRefresh = false;
                setBottomViewLoader(false);
                break;

            case 1:
                setBottomViewLoader(false);
                Utility.setEventTracking(getActivity(), "", AppConstants.EVENT_TRACKING_ID_PICK_UP);
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

                btn_buddyup.setEnabled(true);
                btn_pickup.setEnabled(true);
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
                btn_buddyup.setEnabled(true);
                btn_pickup.setEnabled(true);
                isLazy = false; // lazy loader disable
                setBottomViewLoader(false);
                break;

            case 3:
                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        JSONArray skillistArray = jsonObject.optJSONArray(KEY_DETAIL);
                        if (skillistArray != null && skillistArray.length() > 0) {
                            SharedPref.getInstance().setSharedValue(getActivity(), Api_skill_list, skillistArray.toString());
                            searchKeySkills(skillistArray);
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

                            if (jsonObject1 != null && searchString.trim() != null) {

                                JSONArray skillistArray = jsonObject1.optJSONArray(searchString.trim());
                                //Patch done by Jeeva on 2-02-16
                                //if (skillistArray != null && skillistArray.length() > 0) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                search_pickUpArrayList = ResponseHandler.getInstance().getPickupSearch(skillistArray, searchString);
                                pickUpAdapter = new PickUpAdapter(getActivity(), search_pickUpArrayList);
                                mRecyclerView.setAdapter(pickUpAdapter);
                                progressWheel.stopAnimation();
                                progressWheel.setVisibility(View.GONE);
                                //  }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                btn_buddyup.setEnabled(true);
                btn_pickup.setEnabled(true);

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

   /* private void onSetAdapter() {
        String ratingJson = null;
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ratingJson = ((MainActivity) getActivity()).checkRatingJson;
        }
        buddyupadapter = new BuddyAdapter(getActivity(), search_buddyList, 1);
        mRecyclerView.setAdapter(buddyupadapter);
    }*/

    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(registrationComplete);
    }


    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        btn_buddyup.setEnabled(true);
        btn_pickup.setEnabled(true);
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {

        btn_buddyup.setEnabled(true);
        btn_pickup.setEnabled(true);
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }


    @Override
    public void getBuddyModelAddedItems(int position, BuddyModel model) {
        Log.e("isSearch", ": " + isSearch);
        if (isSearch) {
            if (model != null) {
                search_buddyList.set(position, model);
                buddyupadapter.notifyDataSetChanged();
            }
        } else {
            if (model != null) {
                buddyList.set(position, model);
                buddyupadapter.notifyDataSetChanged();
            }
        }
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
                btn_buddyup.setSelected(false);
                btn_pickup.setSelected(true);
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
                btn_buddyup.setSelected(false);
                btn_pickup.setSelected(true);
            }
        }
    }


    @Override
    public void onRefresh() {
        buddyUpSearchAuto.setText("");
        mSearchClose.setVisibility(View.INVISIBLE);
        if (fromstr != null) {
            isRefresh = true;
            if (fromstr.equals("pick")) {
                getpickuplist();
            } else {
                //mTempPageNumber = currentPageNumber;
                isSearch = false;
                key = 0;
                currentPageNumber = 0;
                buddyList = new ArrayList<>();
                getbudduylist(limit, currentPageNumber);
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }


    private void showNoInternetEmptyView() {
        emptyViewPickUp.setVisibility(View.GONE);
        emptyViewBudddyUp.setVisibility(View.GONE);
        emptyViewNoInternet.setVisibility(View.VISIBLE);
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
    public boolean shouldLoad() {
        boolean shouldLoad = false;

        if (fromstr.equals("buddy")) {
            if (key == 0) {
                //  Log.d(TAG,"TotalItemByWeb : "+ TOTAL_ITEMS_COUNT_BUDDY_UP + "BuddyupList :  : "+ buddyupadapter.getItemCount() );
                shouldLoad = (buddyupadapter.getItemCount() < TOTAL_ITEMS_COUNT_BUDDY_UP);
            } else if (key == 1) {
                //   Log.d(TAG,"TotalItemByWeb : "+ TOTAL_ITEMS_COUNT_BUDDY_UP_SEARCH + "BuddyupList :  : "+ buddyupadapter.getItemCount() );
                shouldLoad = (buddyupadapter.getItemCount() < TOTAL_ITEMS_COUNT_BUDDY_UP_SEARCH);
            }
            if (fromstr.equalsIgnoreCase("buddy") && shouldLoad) {
                try {
                    mRecyclerView.setRefreshing(true);
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }

            } else {
                mRecyclerView.setRefreshing(false);
            }
        } else {
            mRecyclerView.setRefreshing(false);
        }
        //  Log.d(TAG, "shouldLoad : " + shouldLoad);
        return shouldLoad;
    }

    @Override
    public void loadNextPage() {
        Log.d(TAG, "CurrentPosition : " + currentPageNumber);

        if (!isLazy && fromstr.equals("buddy")) {
            if (key == 0) {
                isLazy = true;
                mRecyclerView.setRefreshing(true);
                getbudduylist(limit, currentPageNumber);
            } else if (key == 1) {
                isLazy = true;
                mRecyclerView.setRefreshing(true);
                buddyup_search(search_str, search_flag, limit, currentPageNumber);
            }
        }
    }

}
