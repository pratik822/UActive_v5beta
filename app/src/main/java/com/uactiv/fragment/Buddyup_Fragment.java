package com.uactiv.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.applozic.mobicommons.commons.core.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.felipecsl.gifimageview.library.GifImageView;
import com.github.yasevich.endlessrecyclerview.EndlessRecyclerView;
import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.activity.MainActivity;
import com.uactiv.adapter.BuddyAdapter;
import com.uactiv.application.UActiveApplication;
import com.uactiv.controller.BannerImageListener;
import com.uactiv.controller.GpsNotifier;
import com.uactiv.controller.NotifiyArrayListChange;
import com.uactiv.controller.ResponseListener;
import com.uactiv.location.GPSTracker;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.PickUpCategory;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.EndlessRecyclerViewNew;
import com.uactiv.utils.LinearLayoutManagerWithSmoothScroller;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Buddyup_Fragment extends Fragment implements AppConstants.urlConstants, AppConstants.SharedConstants, ResponseListener, NotifiyArrayListChange, SwipeRefreshLayout.OnRefreshListener, GpsNotifier, EndlessRecyclerView.Pager, EndlessRecyclerViewNew.Pager {

    private static final int TAB_VIEW_PADDING_DIPS = 0;
    //  public static CustomAutoCompleteTextView buddyUpSearchAuto;
    public static NotifiyArrayListChange notifiyArrayListChange;
    public int limit = 5;
    public static int currentPageNumber = 0;
    public int BUDDYUPREQUESTCODE = 0;
    public int PICKUPUPREQUESTCODE = 1;
    //public int BUDDYUPSEARCHREQUESTCODE = 2;
    String[] activities;
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    public static GpsNotifier gpsNotifier;
    EndlessRecyclerViewNew mRecyclerView = null;
    Bundle bn;
    BuddyAdapter buddyupadapter;

    LinearLayoutManager linearLayoutManager;
    public static ArrayList<BuddyModel> buddyList = new ArrayList<BuddyModel>();
    public static ArrayList<BuddyModel> search_buddyList = new ArrayList<BuddyModel>();


    String fromstr = "";
    String navigation = "";
    String TAG = getClass().getSimpleName();
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
    SearchView searchView;
    protected Handler handler;
    public boolean search_flag = false;
    public String search_str = "";
    private android.support.v7.widget.SearchView search;

    static private int TOTAL_ITEMS_COUNT_BUDDY_UP = 0;
    private int TOTAL_ITEMS_COUNT_BUDDY_UP_SEARCH = 0;
    android.support.v7.widget.Toolbar collapsing_toolbar;
    private CustomTextView tb_txt;
    private ImageView ic_backs;
    String mytokan = "";
    SharedPreferences pref;
    String cityName = "";
    Boolean isfilter = false;
    Boolean isload = false;
    String skills = "";
    public String checkRatingJson = "";
    ShareDialog shareDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_buddyup_, container, false);

        collapsing_toolbar = (android.support.v7.widget.Toolbar) rootView.findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setNavigationIcon(R.drawable.pune);
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        bn = this.getArguments();
        shareDialog = new ShareDialog(getActivity());
        if (bn != null) {
            if (bn.getString("filter") != null) {
                isfilter = true;
            }
            if (bn.getString("skills") != null) {
                skills = bn.getString("skills").replaceAll("null", "");
                Log.d("myskils", skills.replaceAll("null", ""));
            }
        }


        if (pref.getString("cityname", null) != null) {
            cityName = pref.getString("cityname", null);
            Log.d("currcity", cityName);
        }

        if (!AppConstants.isGestLogin(getActivity())) {
            Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_HOME);
        } else {
            Utility.setScreenTracking(getActivity(), "Guest login Buddy Up Dashboard");
        }
        try {
            mytokan = AccessToken.getCurrentAccessToken().getToken();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        Log.d("mytokan", mytokan);
        ((AppCompatActivity) getActivity()).setSupportActionBar(collapsing_toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        collapsing_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getActivity(), "pooooo", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        collapsing_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "pooooo", Toast.LENGTH_LONG).show();

            }
        });
        collapsing_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case android.R.id.home:
                        Utils.toggleSoftKeyBoard(getActivity(), true);
                      /*  Fragment fragment = new Buddyup_Fragment();
                        if (fragment != null) {
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                            fragmentTransaction.commit();
                        }*/
                        break;
                }

                return true;
            }
        });

        handler = new Handler();
        gpsNotifier = this;
        mGpsTracker = new GPSTracker(getActivity());
        notifiyArrayListChange = this;
        LinearLayoutManagerWithSmoothScroller ln = new LinearLayoutManagerWithSmoothScroller(getActivity());
        // linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView = (EndlessRecyclerViewNew) rootView.findViewById(R.id.buddylist);

        //  search = (android.support.v7.widget.SearchView) rootView.findViewById(R.id.search);
        // search.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search for a person"+ "</font>"));


        ic_backs = (ImageView) rootView.findViewById(R.id.iv_back);
        tb_txt = (CustomTextView) rootView.findViewById(R.id.tb_txt);


      /*  search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tb_txt.setVisibility(View.INVISIBLE);
                AppConstants.circleReveal(getActivity(), R.id.collapsing_toolbar, 1, true, true);
            }
        });*/


      /* search.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose()
            {
               Toast.makeText(getActivity(),"gg",Toast.LENGTH_LONG).show();
           //     tb_txt.setVisibility(View.VISIBLE);
                buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
                Log.d("getbuddies", new Gson().toJson(buddyList));
                mRecyclerView.setAdapter(buddyupadapter);
                return false;
            }
        });*/

        ic_backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("fromfragment", "hi");
                startActivity(intent);
                getActivity().finish();
            }
        });

     /*   search.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(getActivity(),"gpoog",Toast.LENGTH_LONG).show();
                return false;
            }
        });


       // search.
        search.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(),"gpoog",Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
           //     Toast.makeText(getActivity(),"lllll",Toast.LENGTH_LONG).show();

                    buddyup_search(newText, search_flag, limit, 0);
                 //   search.setIconified(false);

                return false;
            }
        });*/

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
        //search
      /*  buddyUpSearchAuto = (CustomAutoCompleteTextView) rootView.findViewById(R.id.searchs);
        buddyUpSearchAuto.setCursorVisible(false);*/
        // mSearchClose = (ImageView) rootView.findViewById(R.id.searchClose);
        //buddyUpSearchAuto.setImeOptions(EditorInfo.IME_ACTION_DONE);

        getActivtyList();
        Log.d("getbuddyup", new Gson().toJson(buddyList));
        JSONObject jsonObject = new JSONObject();

        if (!AppConstants.isGestLogin(getActivity())) {
            Map<String, String> param = new HashMap<>(3);
            param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
            param.put("screen", "dashboard");
            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "popup_detail", param, this, 6);
        }


//search
       /* buddyUpSearchAuto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                        }
                        return true;
                    }
                }
                return false;
            }
        });*/

      /*  mSearchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromstr.equalsIgnoreCase("buddy")) {
                    currentPageNumber = mTempPageNumber;
                }
                AppConstants.hideViewKeyBoard(getActivity(), buddyUpSearchAuto);
                buddyUpSearchAuto.setCursorVisible(false);
                buddyUpSearchAuto.setText(null);

            }
        });*/


    /*    buddyUpSearchAuto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                    buddyUpSearchAuto.setCursorVisible(true);
                    buddyUpSearchAuto.setFocusable(true);
                    buddyUpSearchAuto.setFocusableInTouchMode(true);

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

                    mTempPageNumber = currentPageNumber;
                    resetPage();
                    search_flag = true;
                    search_str = hm.get("pickUpNames").toLowerCase(Locale.getDefault());
                    search_buddyList = new ArrayList<BuddyModel>();
                    buddyup_search(search_str, search_flag, limit, currentPageNumber);

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
        });*/

       /* btnRefreshLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE) == USER_TYPE_APP) {
                    updateLocationToServer();
                }
            }
        });*/


        Bundle bundle = this.getArguments();
        Bundle extras = getActivity().getIntent().getExtras();

        if (bundle != null && bundle.getString("setting") != "setting") {
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
            } else {
                displayView(0);
            }
        } else {
            Log.d(TAG, "else");
            Log.d(TAG, "onLoading : " + fromstr);
            displayView(0);
        }


        //Patch Done By Jeeva on 08-06-2016...
        bannerImage = (ImageView) rootView.findViewById(R.id.header);
        bannerImageListener = new MainActivity();

        String bannerUrl = bannerImageListener.getBannerImage();

        if (Utility.isNullCheck(bannerUrl)) {
            UActiveApplication.getInstance().loadBannerImage(bannerUrl, bannerImage);
        }

        //To show buddyup tutorials
        // showBuddyUpDetails();
        //   onChangeBanner();
        return rootView;
        // Inflate the layout for this fragment

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuInflater inflater = getActivity().getMenuInflater();
        //menu.clear();
        //    inflater.inflate(R.menu.buddymenu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                Toast.makeText(getActivity(), "Tl", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
    //9867614671

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.buddymenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem filter = menu.findItem(R.id.menu_filter);


        searchView = (SearchView) MenuItemCompat.getActionView(item);
        ImageView iv = (ImageView) MenuItemCompat.getActionView(filter);
        iv.setImageResource(R.drawable.ic_filters);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putString("mytokan", mytokan);
                Filter_fragment fragment = new Filter_fragment();
                fragment.setArguments(b);
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
        searchView.setIconifiedByDefault(false);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_ALWAYS | MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        MenuItemCompat.setActionView(item, searchView);
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Utils.toggleSoftKeyBoard(getActivity(), true);
                Fragment fragment = new Buddyup_Fragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }

                return true;
            }
        });
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + "Search for a person" + "</font>"));
        searchView.onActionViewCollapsed();
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        ImageView searchIconView = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchIconView.setImageResource(R.drawable.ic_search);
        ImageView mCloseButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        ImageView searchClose = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setVisibility(View.VISIBLE);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   searchView.onActionViewCollapsed();
                // searchView.setQuery("", false);
                Utils.toggleSoftKeyBoard(getActivity(), true);
                Fragment fragment = new Buddyup_Fragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // buddyup_search(newText, false, limit, 1);
                getbudduysearchlist(newText, false, limit, 1);

                return false;
            }
        });


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        getRatingList();
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
    public void onDetach() {
        super.onDetach();

    }

    private void getRatingList() {
        if (Utility.isConnectingToInternet(getActivity())) {
            try {
                Log.e("ReviewAPI", "ReviewAPI");
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + checkrating, param, this, 9);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error on PickUp List", "" + e.toString());
            }
        } else {
            Utility.showInternetError(getActivity());
        }
    }

    @Override
    public void onRefresh() {
        //   buddyUpSearchAuto.setText("");
        //  mSearchClose.setVisibility(View.INVISIBLE);
        if (fromstr != null) {
            isRefresh = true;
            if (fromstr.equals("pick")) {
                //getpickuplist();
            } else {
                //mTempPageNumber = currentPageNumber;
                isSearch = false;
                key = 0;
                currentPageNumber = 0;
                buddyList = new ArrayList<>();
                try {
                    Filter_fragment.mytempList_.clear();
                    Filter_fragment.mycount_.clear();
                }catch (NullPointerException ex){
                    ex.printStackTrace();
                }

                SharedPref.getInstance().setSharedValue(getActivity(), "GEST_GENDER", "Both");
                SharedPref.getInstance().setSharedValue(getActivity(), radius_limit, String.valueOf("200"));
                currentPageNumber = 1;
                //  buddyup_search(search_str, search_flag, limit, currentPageNumber);
                getbudduylist(10, currentPageNumber);
            }
        }
        swipeRefreshLayout.setRefreshing(false);

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
        bn = null;
        isload = true;
        if (!isLazy && fromstr.equals("buddy")) {
            if (key == 0) {
                isLazy = true;
                mRecyclerView.setRefreshing(true);
                getbudduylist(limit, currentPageNumber);
            } else if (key == 1) {
                isLazy = true;
                mRecyclerView.setRefreshing(true);
                search_flag = false;
                buddyup_search(search_str, search_flag, limit, currentPageNumber);
            }
        }

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

                                }

                                if (bn != null && bn.getString("setting").equalsIgnoreCase("setting")) {
                                    buddyList = new ArrayList<>();
                                    buddyList.addAll(ResponseHandler.getInstance().storeBuddyList(jsonArrayBuddy));
                                    buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
                                    Log.d("getbuddies", new Gson().toJson(buddyList));
                                    mRecyclerView.setAdapter(buddyupadapter);
                                    buddyupadapter.notifyDataSetChanged();
                                } else if (isfilter == true || isload == true) {
                                    buddyList.addAll(ResponseHandler.getInstance().storeBuddyList(jsonArrayBuddy));
                                    buddyupadapter.notifyDataSetChanged();
                                } else {
                                    buddyList.addAll(ResponseHandler.getInstance().storeBuddyList(jsonArrayBuddy));
                                    buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
                                    mRecyclerView.setAdapter(buddyupadapter);
                                }


                                {


                                  /*  buddyList = new ArrayList<>();
                                    buddyList.addAll(ResponseHandler.getInstance().storeBuddyList(jsonArrayBuddy));
                                    buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
                                    Log.d("getbuddies", new Gson().toJson(buddyList));
                                    mRecyclerView.setAdapter(buddyupadapter);
                                    buddyupadapter.notifyDataSetChanged();*/
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
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        try {
                            JSONObject jsonpickupObj = jsonObject.getJSONObject(KEY_DETAIL);
                            if (jsonpickupObj != null) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                // pickUpArrayList = ResponseHandler.getInstance().storePickupList(getActivity(), jsonObject);//jsonpickupObj
                              /*  Log.e("Pickups List = ", "" + pickUpArrayList.size());
                                pickUpAdapter = new PickUpAdapter(getActivity(), pickUpArrayList);
                                mRecyclerView.setAdapter(pickUpAdapter);*/
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                }


                disableEmptyView();

                isSearch = false;

                break;


            case 2:
                //   dismissBottomLoaderView();


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

                disableEmptyView();

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
                                        // UActiveApplication.getInstance().loadBannerImage(bannerUrl, bannerImage);
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

            case 8:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        try {
                            JSONArray jsonArrayBuddy = jsonObject.getJSONArray(KEY_DETAIL);
                            if (jsonArrayBuddy != null) {
                                Log.d(TAG, "mysearch result size : " + jsonArrayBuddy.length());
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
                                search_buddyList = new ArrayList<>();
                                search_buddyList.addAll(ResponseHandler.getInstance().storeBuddyList(jsonArrayBuddy));
                                if (buddyupadapter == null) {
                                    buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
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

            case 9:
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

                                AppConstants.showReviewDialog(getActivity(), bundle, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + rating, false, this);

                                if (finishedList.optJSONObject(i).optString("isbusinesslocation").equals("1")) {
                                    AppConstants.showReviewDialog(getActivity(), bundle, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + rating, true, this);
                                }
                            }
                        }

                        //Update user badge
                        if (Utility.isNullCheck(jsonObject.optString("badge")) && !(SharedPref.getInstance().getBooleanValue(getActivity(), isbussiness))) {
                            String oldBadge = SharedPref.getInstance().getStringVlue(getActivity(), badge);
                            String newBadge = jsonObject.optString("badge");
                            Log.d(TAG, "BadgetOldBadge : " + oldBadge);
                            Log.d(TAG, "newBadge : " + newBadge);
                            if (oldBadge != null && newBadge != null) {
                                if (newBadge.length() > 0 && !(oldBadge.trim().equalsIgnoreCase(newBadge.trim()))) {
                                    Log.e("Badge categ *** " + jsonObject.optString("badge"), "" + SharedPref.getInstance().getStringVlue(getActivity(), badge));
                                    showBadgeUpdateDialog(newBadge);
//                                    SharedPref.getInstance().setSharedValue(MainActivity.this, badge, jsonObject.optString("badge"));
                                }
                            }
                            SharedPref.getInstance().setSharedValue(getActivity(), badge, jsonObject.optString("badge"));
                        }
                    }

                }
                break;


            default:

                break;
        }
        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
        }

    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        Log.d("error", errorResponse + flag);

    }

    @Override
    public void removeProgress(Boolean hideFlag) {

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

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showBadgeUpdateDialog(String newBadge) {
        if (!AppConstants.isGestLogin(getActivity())) {
            Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_BADGESPOPUP);
        } else {
            Utility.setScreenTracking(getActivity(), "Guest login Badges Pop Up screen");
        }

        final Dialog badgeDialog = new Dialog(getActivity(), R.style.Theme_Dialog);
        badgeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        badgeDialog.setContentView(R.layout.grade_update_badge);
        final LinearLayout parent = (LinearLayout) badgeDialog.findViewById(R.id.main_lay);
        CustomTextView badge_fb_share = (CustomTextView) badgeDialog.findViewById(R.id.badge_fb_share);
        CustomTextView badge_thanks = (CustomTextView) badgeDialog.findViewById(R.id.badge_thanks);
        final CustomTextView badge_txt = (CustomTextView) badgeDialog.findViewById(R.id.badge_txt);
        ImageView batch_icon = (ImageView) badgeDialog.findViewById(R.id.batch_icon);
        CustomTextView batch_name = (CustomTextView) badgeDialog.findViewById(R.id.batch_name);
        Utility.updateBadge(newBadge, batch_icon); // set badge image to imageview
        batch_name.setText(newBadge); // set badge name
        SharedPref.getInstance().setSharedValue(getActivity(), badge, newBadge);
        badge_fb_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Working on FB Share", Toast.LENGTH_SHORT).show();
                shareBitmap(badge_txt.getText().toString(), parent, shareDialog);
                badgeDialog.dismiss();
            }
        });

        badge_thanks.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                badgeDialog.dismiss();
                return false;
            }
        });
        badgeDialog.setCanceledOnTouchOutside(true);
        badgeDialog.show();
        SharedPref.getInstance().setSharedValue(getActivity(), badge, newBadge);
    }

    private void shareBitmap(String caption, View view, ShareDialog shareDialog) {
        Bitmap myBitmap = view.getDrawingCache();
        shareBitmap(getActivity(), shareDialog, caption, myBitmap);
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
                param.put("city", AppConstants.getCityByLocation(getActivity(), latitude, longitude));
                Log.e("Valid City", "" + AppConstants.getCityByLocation(getActivity(), latitude, longitude));
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

    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(registrationComplete);
    }

    private void showBuddyUpEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        emptyViewRoot.setVisibility(View.VISIBLE);
        emptyViewBudddyUp.setVisibility(View.VISIBLE);
        emptyViewPickUp.setVisibility(View.GONE);
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
                    if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                        params.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                        params.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                    } else {
                        params.put("latitude", "" + mGpsTracker.getLatitude());
                        params.put("longitude", "" + mGpsTracker.getLongitude());
                    }


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

                            break;
                        case 2:
                            buddyup_search(search_str, search_flag, limit, currentPageNumber);
                            break;
                        case 4:

                            break;
                        case 5:
                            updateLocationToServer();
                            break;

                    }
                }
            }
        }, 3000);


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
                    //  pickUpAutoList.add(pickUpMap);
                }
                String[] pickUpFrom = {"pickUpNames"};
                int[] pickUpTo = {R.id.txt};

            /*    try {
                    if (buddyUpSearchAuto != null) {
                        //  SimpleAdapter pickUpSearchAdapter = new SimpleAdapter(getActivity(), pickUpAutoList, R.layout.autocomplete_layout, pickUpFrom, pickUpTo);
                        //    buddyUpSearchAuto.setAdapter(pickUpSearchAdapter);
                    }
                } catch (NullPointerException e) {
                    //e.getMessage();
                }*/
            }
        }


    }

    public synchronized void getbudduysearchlist(String searchText, boolean isByActivity, int limit, int currposition) {


        Log.d(TAG, "TotalPages :" + total_pages + " current Position : " + currposition);

        /** this to condition for block request once all pages are loaded
         *
         */


        if (Utility.isConnectingToInternet(getActivity())) {


                try {
                    lat = mGpsTracker.getLatitude();
                    lang = mGpsTracker.getLongitude();
                    isBuddyUpListCalled = true;
                    disableEmptyView();
                    Map<String, String> param = new HashMap<>();

                    if (!AppConstants.isGestLogin(getActivity())) {
                        param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                            param.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                            param.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                        } else {
                            param.put("latitude", "" + lat);
                            param.put("longitude", "" + lang);
                        }

                        param.put("limit", "" + limit);
                        param.put("page", "" + (currposition));
                        param.put("search_name", searchText);
                        param.put("search_activity", "");
                        param.put("access_token", "");
                        param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                          /*  param.put("city",cityName);*/
                        if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
                            param.put("gender_pref", AppConstants.getGender(getActivity()));
                        } else {
                            param.put("gender_pref", "Both");
                        }
                        if (isByActivity) {
                            param.put("search_name", "");
                            param.put("search_activity", skills);
                        } else {
                            param.put("search_name", searchText.trim());
                            param.put("search_activity", "");
                        }
                           /* if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                                param.put("show_mutual_friends",SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                            }*/
                          /*  if (AppConstants.getRadius(getActivity()) != null && AppConstants.getRadius(getActivity()) != "") {
                                param.put("radius_limit", AppConstants.getRadius(getActivity()));
                            } else {
                                param.put("radius_limit", "100");
                            }*/

                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit))) {
                            param.put("radius_limit", SharedPref.getInstance().getStringVlue(getActivity(), radius_limit));
                        } else {
                            param.put("radius_limit", "100");
                        }

                    } else {
                        param.put("iduser", "0");

                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                            param.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                            param.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                        } else {
                            param.put("latitude", "" + lat);
                            param.put("longitude", "" + lang);
                        }
                        param.put("limit", "" + limit);
                        //  param.put("city", cityName);
                          /*  if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                                param.put("show_mutual_friends",SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                            }
                            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                                param.put("show_mutual_friends",SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                            }*/
                        param.put("page", "" + (currposition));
                        param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                        param.put("access_token", "");

                           /* if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
                                param.put("gender_pref", AppConstants.getGender(getActivity()));
                            } else {
                                param.put("gender_pref", "Both");
                            }*/


                          /*  if (AppConstants.getRadius(getActivity()) != null && AppConstants.getRadius(getActivity()) != "") {
                                param.put("radius_limit", AppConstants.getRadius(getActivity()));
                            } else {
                                param.put("radius_limit", "100");
                            }*/
                    }

                    //   param.put("access_token", mytokan);

                    if (!isRefresh && !isLazy) {
                        progressWheel.setVisibility(View.VISIBLE);
                        progressWheel.startAnimation();
                    }

                    // showBottomLoaderView();
                    Log.d("buddyparam", new Gson().toJson(param));
                    if (!AppConstants.isGestLogin(getActivity())) {

                        RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + buddysearch, param, this, 8);

                    } else {
                        RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "buddyuplist_guest", param, this, 8);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


    }

    public synchronized void getbudduylist(int limit, int currposition) {


        Log.d(TAG, "TotalPages :" + total_pages + " current Position : " + currposition);

        /** this to condition for block request once all pages are loaded
         *
         */


        if (Utility.isConnectingToInternet(getActivity())) {

            if (!isBuddyUpListCalled) {// && currentPageNumber < total_pages
                try {
                    lat = mGpsTracker.getLatitude();
                    lang = mGpsTracker.getLongitude();
                    isBuddyUpListCalled = true;
                    disableEmptyView();
                    Map<String, String> param = new HashMap<>();
                    if (!AppConstants.isGestLogin(getActivity())) {
                        param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                            param.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                            param.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                        } else {
                            param.put("latitude", "" + lat);
                            param.put("longitude", "" + lang);
                        }
                        param.put("limit", "" + limit);
                        param.put("page", "" + (currposition));
                        param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                        //   param.put("city", cityName);
                        if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
                            param.put("gender_pref", AppConstants.getGender(getActivity()));
                        } else {
                            param.put("gender_pref", "Both");
                        }
                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                            param.put("show_mutual_friends", SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                        }
                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit))) {
                            param.put("radius_limit", SharedPref.getInstance().getStringVlue(getActivity(), radius_limit));
                        } else {
                            param.put("radius_limit", "100");
                        }
                    } else {
                        param.put("iduser", "0");
                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                            param.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                            param.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                        } else {
                            param.put("latitude", "" + lat);
                            param.put("longitude", "" + lang);
                        }
                        param.put("limit", "" + limit);
                        //    param.put("city", cityName);
                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                            param.put("show_mutual_friends", SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                        }
                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                            param.put("show_mutual_friends", SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                        }
                        param.put("page", "" + (currposition));
                        param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                        param.put("access_token", "");

                        if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
                            param.put("gender_pref", AppConstants.getGender(getActivity()));
                        } else {
                            param.put("gender_pref", "Both");
                        }


                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit))) {
                            param.put("radius_limit", SharedPref.getInstance().getStringVlue(getActivity(), radius_limit));
                        } else {
                            param.put("radius_limit", "100");
                        }
                    }

                    param.put("access_token", "");

                    if (!isRefresh && !isLazy) {
                        progressWheel.setVisibility(View.VISIBLE);
                        progressWheel.startAnimation();
                    }

                    // showBottomLoaderView();
                    Log.d("buddyparam", new Gson().toJson(param));
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


         /*   if (mGpsTracker.canGetLocation()) {
                if ((lat > 0 || lang > 0) || (Utility.ischeckvalidLocation(mGpsTracker)) && !isBuddyUpListCalled) {// && currentPageNumber < total_pages
                    try {
                        lat = mGpsTracker.getLatitude();
                        lang = mGpsTracker.getLongitude();
                        isBuddyUpListCalled = true;
                        disableEmptyView();
                        Map<String, String> param = new HashMap<>();
                        if (!AppConstants.isGestLogin(getActivity())) {
                            param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                                param.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                                param.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                            } else {
                                param.put("latitude", "" + lat);
                                param.put("longitude", "" + lang);
                            }
                            param.put("limit", "" + limit);
                            param.put("page", "" + (currposition));
                            param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                            //   param.put("city", cityName);
                            if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
                                param.put("gender_pref", AppConstants.getGender(getActivity()));
                            } else {
                                param.put("gender_pref", "Both");
                            }
                            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                                param.put("show_mutual_friends", SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                            }
                            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit))) {
                                param.put("radius_limit", SharedPref.getInstance().getStringVlue(getActivity(), radius_limit));
                            } else {
                                param.put("radius_limit", "100");
                            }
                        } else {
                            param.put("iduser", "0");
                            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "lat"))) {
                                param.put("latitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lat"));
                                param.put("longitude", "" + SharedPref.getInstance().getStringVlue(getActivity(), "lang"));
                            } else {
                                param.put("latitude", "" + lat);
                                param.put("longitude", "" + lang);
                            }
                            param.put("limit", "" + limit);
                            //    param.put("city", cityName);
                            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                                param.put("show_mutual_friends", SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                            }
                            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                                param.put("show_mutual_friends", SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                            }
                            param.put("page", "" + (currposition));
                            param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                            param.put("access_token", "");

                            if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
                                param.put("gender_pref", AppConstants.getGender(getActivity()));
                            } else {
                                param.put("gender_pref", "Both");
                            }


                            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit))) {
                                param.put("radius_limit", SharedPref.getInstance().getStringVlue(getActivity(), radius_limit));
                            } else {
                                param.put("radius_limit", "100");
                            }
                        }

                        param.put("access_token", "");

                        if (!isRefresh && !isLazy) {
                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                        }

                        // showBottomLoaderView();
                        Log.d("buddyparam", new Gson().toJson(param));
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
            }*/
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

    private void showNoInternetEmptyView() {
        emptyViewPickUp.setVisibility(View.GONE);
        emptyViewBudddyUp.setVisibility(View.GONE);
        emptyViewNoInternet.setVisibility(View.VISIBLE);
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


    private void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                isSearch = false;
                if (buddyList.size() == 0) {
                    isRefresh = false;
                    currentPageNumber = 1;
                    getbudduylist(limit, currentPageNumber);
                    // buddyUpSearchAuto.setHint("Search for a person or activity");

                    fromstr = "buddy";
                } else {

                    try {
                        fromSetting = this.getArguments().getString("setting");
                        if (fromSetting.equalsIgnoreCase("setting")) {
                            currentPageNumber = 1;
                            if (isfilter == false && search_buddyList.size() > 0) {
                            /*    if(this.getArguments().getBoolean("ischange")==true){
                                    isRefresh = false;
                                    currentPageNumber = 1;
                                    getbudduylist(limit, currentPageNumber);
                                }else {
                                    buddyupadapter = new BuddyAdapter(getActivity(), search_buddyList);
                                    mRecyclerView.setAdapter(buddyupadapter);
                                }*/
                                buddyupadapter = new BuddyAdapter(getActivity(), search_buddyList);
                                mRecyclerView.setAdapter(buddyupadapter);


                            } else {

                                if (isfilter == true) {
                                    if (search_buddyList.size() > 0) {
                                        buddyupadapter = new BuddyAdapter(getActivity(), search_buddyList);
                                        mRecyclerView.setAdapter(buddyupadapter);
                                    } else {
                                        getbudduysearchlist(search_str, true, limit, currentPageNumber);

                                    }

                                }


                            }


                            //  buddyUpSearchAuto.setHint("Search for a person or activity");

                            fromstr = "buddy";

                        }
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                        if (search_buddyList.size() > 0) {
                            isRefresh = false;
                            currentPageNumber = 1;
                            getbudduylist(limit, currentPageNumber);

                        } else {
                            //   buddyup_search(search_str, search_flag, limit, currentPageNumber);
                        }
                    }

                    disableEmptyView();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    //buddyupadapter = new BuddyAdapter(getActivity(), buddyList);
                    mRecyclerView.setAdapter(buddyupadapter);
                    //onSetAdapter();
                    //  buddyUpSearchAuto.setHint("Search for a person or activity");

                    fromstr = "buddy";
                    //     showEmptyAlertImage(buddyList.size());

                }
                break;

            case 1:
                fromstr = "pick";
                isSearch = false;

                disableEmptyView();
                mRecyclerView.setVisibility(View.VISIBLE);
                //buddyUpSearchAuto.setHint("Select an activity to join");
                //   buddyUpSearchAuto.setHint("Search for an activity to join");

                fromstr = "pick";
                //        showEmptyAlertImage(pickUpArrayList.size());


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

    public void buddyup_search(String searchText, boolean isByActivity, int limit, int currposition) {

        if (Utility.isConnectingToInternet(getActivity())) {


                    //  disableEmptyView();
                    try {
                        Map<String, String> param = new HashMap<>();
                        if (!AppConstants.isGestLogin(getActivity())) {
                            param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                            param.put("latitude", "" + mGpsTracker.getLatitude());
                            param.put("longitude", "" + mGpsTracker.getLongitude());
                            param.put("user_type", "" + SharedPref.getInstance().getIntVlue(getActivity(), USER_TYPE));
                            param.put("limit", "" + limit);
                            param.put("page", "" + currposition);
                            //param.put("city",cityName);
                        } else {
                            param.put("iduser", "0");
                            param.put("latitude", "" + mGpsTracker.getLatitude());
                            param.put("longitude", "" + mGpsTracker.getLongitude());
                            param.put("limit", "" + limit);
                            //     param.put("city", cityName);
                            param.put("user_type", "0");
                            param.put("access_token", "" + "");

                        }
                        param.put("access_token", "");
                        if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
                        } else {
                            param.put("gender_pref", "Both");
                        }
                        param.put("gender_pref", AppConstants.getGender(getActivity()));

                      /*  if (AppConstants.getRadius(getActivity()) != null && AppConstants.getRadius(getActivity()) != "") {
                            param.put("radius_limit", AppConstants.getRadius(getActivity()));
                        } else {
                            param.put("radius_limit", "100");
                        }*/
                        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
                            param.put("show_mutual_friends", SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"));
                        }
                        if (isByActivity) {
                            param.put("search_name", "");
                            param.put("search_activity", searchText.trim());
                        } else {
                            param.put("search_name", searchText.trim());
                            param.put("search_activity", "");
                        }
                        param.put("search_activity", skills);

                        Log.d("search_param", new Gson().toJson(param));


                        isSearch = true;
                        if (currposition == 0) {
                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                        }
                        // showBottomLoaderView();
                        if (!AppConstants.isGestLogin(getActivity())) {
                            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + buddysearch, param, this, 8);

                        } else {
                            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "search_guest", param, this, 8);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

        } else {
            showInternetEmptyView();
            // Utility.showInternetError(getActivity());
        }

    }

    private void setBottomViewLoader(boolean loading) {
        mRecyclerView.setRefreshing(loading);
    }

    public void showBuddyUpDetails() {

        String tutorialText = getResources().getString(R.string.tutorial_buddy_up);
        if (SharedPref.getInstance().getBooleanValue(getActivity(), isbussiness)) {
            tutorialText = getResources().getString(R.string.tutorial_buddy_up_business);
        }
        /*ShowcaseView mShowcaseView = new ShowcaseView.Builder(getActivity())
                .setTarget(new ViewTarget(btn_buddyup))
                .hideOnTouchOutside()
                .setContentText(tutorialText)
                .setContentTextPaint(Utility.getTextPaint(getActivity()))
                .singleShot(AppConstants.TUTORIAL_BUDDY_UP_ID)
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        mShowcaseView.setButtonText(getActivity().getString(R.string.tutorial_got_it));*/
    }
}
