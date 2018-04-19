package com.uactiv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.felipecsl.gifimageview.library.GifImageView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uactiv.R;
import com.uactiv.controller.InviteGroupNotify;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.GroupData;
import com.uactiv.fragment.Mydetailss;
import com.uactiv.location.GPSTracker;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.FavouriteModel;
import com.uactiv.model.IFavGroupNearModel;
import com.uactiv.model.InviteGroupModel;
import com.uactiv.model.InviteListsModel;
import com.uactiv.model.Nearbylistmodel;
import com.uactiv.model.SkillsModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.EndlessRecyclerViewNew;
import com.uactiv.utils.LinearLayoutManagerWithSmoothScroller;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomAutoCompleteTextView;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InvitePeopleFav extends Activity implements OnClickListener, AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener, InviteGroupNotify, EndlessRecyclerViewNew.Pager {


    private LinearLayoutManagerWithSmoothScroller manager;

    private static final int REQUEST_INVITE_GROUPS = 1;
    static InviteGroupNotify inviteGroupNotify = null;
    public static InviteListsModel mInviteListsModel, mGlobalInviteListsModel;
    public static IFavGroupNearModel mnearbymodel;
    Nearbylistmodel nearbylistmodel;
    public ArrayList<Integer> itemCount;
    public static ArrayList<FavouriteModel> arrayList, arrayListInvite;
    private FavAddAdapter mAdapter;
    InvitePeople mInvitePeopleAdapter;
    CustomAutoCompleteTextView editSearch;
    EndlessRecyclerViewNew listView = null;
    String activityVal, modeVal;
    ArrayList<BuddyModel> search_buddyList = new ArrayList<BuddyModel>();
    int currentPageNumber = 0;
    int limit = 10;
    int countVal;
    private ArrayList<InviteListsModel> mInvitePeoplelist;

    int TOTAL_ITEMS_COUNT;
    int total_count;
    private boolean isLazy = false;

    //ArrayList<String> selectedGroupFav = new ArrayList<>();
    ArrayList<String> selectedFav = new ArrayList<>();
    ArrayList<String> tempSelectedFav = new ArrayList<>();
    ArrayList<FavouriteModel> temparrayListInvite = new ArrayList<>();
    boolean isEdit, IspickupGuest;
    String idschedule;
    ArrayList<FavouriteModel> invited_peoples = null;
    ArrayList<String> final_SelectedFavID = null;
    ArrayList<String> seleted_GroupId = new ArrayList<>();
    ImageView searchClose = null;
    LinearLayout mEmptyViewFav = null;
    RelativeLayout mEmptyViewNoInternet = null;
    GifImageView progreesWheel = null;
    int activitySearchCount = 0;
    ArrayList<IFavGroupNearModel> mTempInvitePeople = new ArrayList<>();
    private CustomButton tvGroups;
    private ImageButton tvCancel;
    private RelativeLayout inviteLayout;
    private CustomTextView favorites_but, group_but, near_but;
    private GPSTracker gpsTracker = null;
    private int mKey = 0;
    ArrayList<HashMap<String, String>> pickUpAutoList = new ArrayList<HashMap<String, String>>();
    private String TAG = getClass().getSimpleName();
    InvitePeoplelist lis;
    ArrayList<IFavGroupNearModel> near_bylist, nearby_searchlist;
    ArrayList<SkillsModel> skillslist;
    InvitePeoplenearbyadapter peopleadapter;
    GPSTracker mGpsTracker = null;
    String push;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_peoplefav);
        mGpsTracker = new GPSTracker(this);
        mEmptyViewFav = (LinearLayout) findViewById(R.id.empty_view_no_fav);
        mEmptyViewNoInternet = (RelativeLayout) findViewById(R.id.empty_view_no_internet);
        mInvitePeoplelist = new ArrayList<>();
        inviteGroupNotify = InvitePeopleFav.this;
        isLazy = false;
        near_bylist = new ArrayList<>();

        activityVal = getIntent().getExtras().getString(activity);
        modeVal = getIntent().getExtras().getString(modekey);
        countVal = getIntent().getIntExtra(invitecount, 0);
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        IspickupGuest = getIntent().getBooleanExtra("isPickupGest", false);
        idschedule = getIntent().getExtras().getString("idschedule");
        Log.e("idschedule", "idschedule" + idschedule);

        final_SelectedFavID = (ArrayList<String>) getIntent().getSerializableExtra("SelectedFavID");
        seleted_GroupId = (ArrayList<String>) getIntent().getSerializableExtra("seleted_GroupId");

        Log.e("seleted_GroupId", "In InvitePeople" + seleted_GroupId.size());
        Log.e("seleted_GroupId", "In InvitePeople" + final_SelectedFavID.size());


        selectedFav = final_SelectedFavID;
        // getnearbysearchList();
        gpsTracker = new GPSTracker(InvitePeopleFav.this);

        if (isEdit) {
            invited_peoples = (ArrayList<FavouriteModel>) getIntent().getSerializableExtra("invited_peoples");
            Log.e("seleted_GroupId", "In InvitePeople" + invited_peoples.size());
        }


        progreesWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(InvitePeopleFav.this, progreesWheel);
        tvGroups = (CustomButton) findViewById(R.id.tvGroups);
        tvCancel = (ImageButton) findViewById(R.id.tvCancel);
        inviteLayout = (RelativeLayout) findViewById(R.id.inviteLayout);
        inviteLayout.setVisibility(View.VISIBLE);
        editSearch = (CustomAutoCompleteTextView) findViewById(R.id.editSearchs);

        searchClose = (ImageView) findViewById(R.id.searchClose);

        searchClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mKey == 3)
                    mKey = 2;
                editSearch.setText("");
                searchClose.setVisibility(View.GONE);
                activitySearchCount = 0;
                // getInviteList();
                AppConstants.hideViewKeyBoard(InvitePeopleFav.this, editSearch);
            }
        });

        favorites_but = (CustomTextView) findViewById(R.id.favorites_but);
        group_but = (CustomTextView) findViewById(R.id.group_but);
        near_but = (CustomTextView) findViewById(R.id.near_but);

        favorites_but.setSelected(true);
        favorites_but.setTextColor(getResources().getColor(R.color.white));

        favorites_but.setOnClickListener(this);
        group_but.setOnClickListener(this);
        near_but.setOnClickListener(this);

        //onclick
        tvGroups.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        inviteLayout.setOnClickListener(this);

        arrayList = new ArrayList<>();

        listView = (EndlessRecyclerViewNew) findViewById(R.id.favListView);
        manager = new LinearLayoutManagerWithSmoothScroller(InvitePeopleFav.this);
        listView.setLayoutManager(manager);
        listView.setPager(this);
        listView.setHasFixedSize(false);
        listView.setProgressView(R.layout.layout_progress_bar);
        listView.addOnScrollListener(onScrollListener);
        mKey = 0;
        if (arrayList != null && arrayList.size() > 0) {

        } else {
            getfevoriteList(currentPageNumber, limit);
            isLazy = false;
        }

        //getFavouriteList();
        //   getInviteList();

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mKey == 2) {
                    if (mKey == 2) {
                        if (editSearch.length() > 0) {
                            mKey = 3;
                            if (mInvitePeopleAdapter != null
                                    && mInviteListsModel.getDetails() != null && mInviteListsModel.getDetails().getNearBySearch() != null) {
                                mInvitePeopleAdapter.notifyDataChanged(mInviteListsModel.getDetails().getNearBySearch());
                            }

                        } else {
                            mKey = 2;
                            setAdapter();
                        }

                    } else if (mKey == 3 && editSearch.length() == 0) {
                        mKey = 2;
                        setAdapter();
                    }
                }


                Log.d(TAG, "mKey : " + mKey);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doSearch(editSearch.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        editSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings({"unchecked", "static-access"})
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter().getItem(position) != null) {
                    mKey = 3;
                    //setAdapter();
                    activitySearchCount = 0;
                    progreesWheel.setVisibility(View.VISIBLE);
                    progreesWheel.startAnimation();
                    if (mInvitePeopleAdapter != null
                            && mInviteListsModel.getDetails() != null && mInviteListsModel.getDetails().getNearBySearch() != null) {
                        mInvitePeopleAdapter.notifyDataChanged(mInviteListsModel.getDetails().getNearBySearch());
                    }
                    HashMap<String, String> hm = (HashMap<String, String>) parent.getAdapter().getItem(position);
                    editSearch.setText(hm.get("pickUpNames"));
                    Log.d("Set Adapt Edt cnt  = ", "" + mInvitePeopleAdapter.mInvitePeople.size());
                    doSearch(hm.get("pickUpNames"));
                }
            }
        });
        arrayListInvite = (ArrayList<FavouriteModel>) getIntent().getSerializableExtra("SelectedFavDetails");
        Log.e("SelectedFavDetails", ":" + arrayListInvite.size());

    }

    private void doSearch(String searchText) {
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        if (searchText.length() > 0) {
            searchClose.setVisibility(View.VISIBLE);
            if (mKey == 1 || mKey == 2) {
                filter(searchText, false);
            } else if (mKey == 3) {
                //  filternearby(searchText, false);
                invite_search(searchText);
            }
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

            Log.d("topRowVerticalPosition", "" + topRowVerticalPosition);
        }
    };

    private void showInternetView() {
        mEmptyViewFav.setVisibility(View.GONE);
        mEmptyViewNoInternet.setVisibility(View.VISIBLE);
    }

    private void showFavView() {
        mEmptyViewFav.setVisibility(View.VISIBLE);
        mEmptyViewNoInternet.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsTracker != null) {
            gpsTracker.stopUsingGPS();
        }

    }

    private void setActivityListAdapter() throws JSONException {

        String activityList = SharedPref.getInstance().getStringVlue(this, Api_skill_list);

        if (!TextUtils.isEmpty(activityList)) {
            pickUpAutoList = new ArrayList<>();
            JSONArray activityArrayObject = new JSONArray(activityList);
            if (activityArrayObject != null && activityArrayObject.length() > 0)
                for (int i = 0; i < activityArrayObject.length(); i++) {
                    HashMap<String, String> pickUpMap = new HashMap<String, String>();
                    pickUpMap.put("pickUpNames", activityArrayObject.optJSONObject(i).optString("activity"));
                    pickUpAutoList.add(pickUpMap);
                }
        }
        String[] pickUpFrom = {"pickUpNames"};
        int[] pickUpTo = {R.id.txt};

        try {
            if (editSearch != null) {
                SimpleAdapter pickUpSearchAdapter = new SimpleAdapter(this, pickUpAutoList, R.layout.autocomplete_layout, pickUpFrom, pickUpTo);
                editSearch.setAdapter(pickUpSearchAdapter);
            }
        } catch (NullPointerException e) {
            //e.getMessage();
        }

    }

    private void disableEmptyView() {
        mEmptyViewFav.setVisibility(View.GONE);
        mEmptyViewNoInternet.setVisibility(View.GONE);
    }

    /***/
    private void getFavouriteList() {
        if (Utility.isConnectingToInternet(InvitePeopleFav.this)) {
            disableEmptyView();
            try {
                Map<String, String> param = new HashMap<>();
                Log.e("iduser = ", "" + SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("iduser", SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                progreesWheel.setVisibility(View.VISIBLE);
                progreesWheel.startAnimation();
                RequestHandler.getInstance().stringRequestVolley(this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(InvitePeopleFav.this, isStaging)) + favouriteslist, param, this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showInternetView();
            // Utility.showInternetError(InvitePeopleFav.this);
        }
    }

    private void getnearbysearchList() {

        if (Utility.isConnectingToInternet(InvitePeopleFav.this)) {
            double latitude = 0.0, longitude = 0.0;
            if (gpsTracker != null) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
            }
            try {
                Map<String, String> param = new HashMap<>();
                Log.e("iduser = ", "" + SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("iduser", SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("latitude", "" + latitude);
                param.put("longitude", "" + longitude);
                param.put("user_type", "" + SharedPref.getInstance().getIntVlue(InvitePeopleFav.this, USER_TYPE));

                RequestHandler.getInstance().stringRequestVolley(this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(InvitePeopleFav.this, isStaging)) + near_by_search, param, this, 4);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showInternetView();
            // Utility.showInternetError(InvitePeopleFav.this);
        }
    }

    /***/
   /* private void getInviteList() {
        double latitude = 0.0, longitude = 0.0;
        if (gpsTracker != null) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }

        if (Utility.isConnectingToInternet(InvitePeopleFav.this)) {
            disableEmptyView();
            try {
                Map<String, String> param = new HashMap<>();
                Log.e("iduser = ", "" + SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("iduser", SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("latitude", "" + latitude);
                param.put("longitude", "" + longitude);
                param.put("user_type", "" + SharedPref.getInstance().getIntVlue(InvitePeopleFav.this, USER_TYPE));

                if (idschedule != null) {
                    param.put("idschedule", "" + idschedule);
                }
                progreesWheel.setVisibility(View.VISIBLE);
                progreesWheel.startAnimation();
                RequestHandler.getInstance().stringRequestVolley(this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(InvitePeopleFav.this, isStaging)) + getInvitePeople, param, this, 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showInternetView();
            // Utility.showInternetError(InvitePeopleFav.this);
        }
    }*/
    private void getnearbyList(int page, int limit) {
        double latitude = 0.0, longitude = 0.0;
        if (gpsTracker != null) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }

        if (Utility.isConnectingToInternet(InvitePeopleFav.this)) {
            disableEmptyView();
            try {
                Map<String, String> param = new HashMap<>();
                Log.e("iduser = ", "" + SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("iduser", SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("latitude", "" + latitude);
                param.put("longitude", "" + longitude);
                param.put("user_type", "" + SharedPref.getInstance().getIntVlue(InvitePeopleFav.this, USER_TYPE));
                param.put("radius_limit", "200");
                param.put("gender", "both");
                if (idschedule != null) {
                    param.put("idschedule", "" + idschedule);
                }
                param.put("page", "" + page);
                param.put("limit", "" + limit);
                if (!isLazy) {
                    progreesWheel.setVisibility(View.VISIBLE);
                } else {
                    progreesWheel.setVisibility(View.INVISIBLE);
                }

                progreesWheel.startAnimation();
                RequestHandler.getInstance().stringRequestVolley(this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(InvitePeopleFav.this, isStaging)) + getnearby, param, this, 3);

            } catch (Exception e) {
                e.printStackTrace();
            }
            isLazy = true;
        } else {
            showInternetView();
            // Utility.showInternetError(InvitePeopleFav.this);
        }
    }


    private void getGroups() {
        double latitude = 0.0, longitude = 0.0;
        if (gpsTracker != null) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }

        if (Utility.isConnectingToInternet(InvitePeopleFav.this)) {
            disableEmptyView();
            try {
                Map<String, String> param = new HashMap<>();
                Log.e("iduser = ", "" + SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("iduser", SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("latitude", "" + latitude);
                param.put("longitude", "" + longitude);
                param.put("user_type", "" + SharedPref.getInstance().getIntVlue(InvitePeopleFav.this, USER_TYPE));

                if (idschedule != null) {
                    param.put("idschedule", "" + idschedule);
                }
                progreesWheel.setVisibility(View.VISIBLE);
                progreesWheel.startAnimation();
                RequestHandler.getInstance().stringRequestVolley(this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(InvitePeopleFav.this, isStaging)) + getgroups, param, this, 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showInternetView();
            // Utility.showInternetError(InvitePeopleFav.this);
        }
    }

    private void getfevoriteList(int page, int limit) {
        double latitude = 0.0, longitude = 0.0;
        if (gpsTracker != null) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        }

        if (Utility.isConnectingToInternet(InvitePeopleFav.this)) {
            disableEmptyView();
            try {
                Map<String, String> param = new HashMap<>();
                Log.e("iduser = ", "" + SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("iduser", SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
                param.put("latitude", "" + latitude);
                param.put("longitude", "" + longitude);
                param.put("user_type", "" + SharedPref.getInstance().getIntVlue(InvitePeopleFav.this, USER_TYPE));

                if (idschedule != null) {
                    param.put("idschedule", "" + idschedule);
                }
                param.put("page", "" + page);
                param.put("limit", "" + limit);
                if (!isLazy) {
                    progreesWheel.setVisibility(View.VISIBLE);
                } else {
                    progreesWheel.setVisibility(View.INVISIBLE);
                }
                progreesWheel.startAnimation();
                RequestHandler.getInstance().stringRequestVolley(this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(InvitePeopleFav.this, isStaging)) + getfev, param, this, 0);
                isLazy = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showInternetView();
            // Utility.showInternetError(InvitePeopleFav.this);
        }
    }

    private void init(EndlessRecyclerViewNew listView) {
        mAdapter = new FavAddAdapter(arrayList);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(successResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }


        switch (flag) {
            case 0:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        JSONArray getfav = jsonObject.optJSONArray(KEY_DETAIL);

                        if (getfav != null) {
                            for (int i = 0; i < getfav.length(); i++) {
                                JSONObject favObj = getfav.optJSONObject(i);
                                Log.d("myiiiiiiiii", new Gson().toJson(getfav.getJSONObject(i).getJSONArray(("images")).getJSONObject(0).getString("image")));
                                arrayList.add(new FavouriteModel(favObj.optString("iduser"), favObj.optString("firstname") + " " + favObj.optString("lastname"), AppConstants.getiamgebaseurl() + getfav.getJSONObject(i).getJSONArray(("images")).getJSONObject(0).getString("image")));
                            }
                            listView.invalidate();
                            if (isEdit) {
                                if (arrayList != null && arrayList.size() > 0 && invited_peoples != null && invited_peoples.size() > 0) {
                                    arrayList = removeExsting();
                                }
                            }
                            //    arrayListInvite = new ArrayList<FavouriteModel>();


                        }

                        JSONObject page = new JSONObject(jsonObject.getString("page"));

                        if (page != null) {

                            TOTAL_ITEMS_COUNT = page.optInt("total_items");
                            currentPageNumber = page.optInt("current_page");
                            Log.d("LastPage ", currentPageNumber + "");
                            currentPageNumber = currentPageNumber + 1;
                            Log.d("NextPage ", currentPageNumber + "");
                            total_count = page.optInt("total_pages");
                        }


                    } else {
                        Utility.showToastMessage(InvitePeopleFav.this, jsonObject.optString(KEY_MSG));
                    }
                    if (!isLazy) {

                        if (arrayList.size() > 0) {
                            mAdapter = new FavAddAdapter(arrayList);
                            listView.setAdapter(mAdapter);
                            progreesWheel.setVisibility(View.INVISIBLE);
                            progreesWheel.stopAnimation();
                            listView.setRefreshing(false);
                        }
                    } else {
                        try {
                            mAdapter.notifyDataSetChanged();
                        } catch (NullPointerException ex) {

                        }

                    }
                }


                if (arrayList == null || arrayList.size() == 0) {
                    showFavView();
                } else {
                    disableEmptyView();
                }
                break;

            case 1:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        JSONArray getgrouplist = jsonObject.optJSONArray(KEY_DETAIL);
                        GroupData inviteNewlist = new GroupData();


                        List<Mydetailss> dlist = new ArrayList<>();
                        List<IFavGroupNearModel.Members> mlist = new ArrayList<>();


                        for (int i = 0; i < getgrouplist.length(); i++) {
                            Mydetailss d = new Mydetailss();
                            d.setIdgroup(getgrouplist.getJSONObject(i).getString("idgroup"));
                            d.setGroupname(getgrouplist.getJSONObject(i).getString("groupname"));
                            d.setIduser(getgrouplist.getJSONObject(i).getString("iduser"));
                            dlist.add(d);
                            JSONArray gemembergrouplist = getgrouplist.getJSONObject(i).getJSONArray("members");
                            for (int j = 0; j < gemembergrouplist.length(); j++) {
                                IFavGroupNearModel.Members m = new IFavGroupNearModel.Members();
                                m.setFirstname(gemembergrouplist.getJSONObject(j).getString("firstname"));
                                m.setIduser(gemembergrouplist.getJSONObject(j).getString("iduser"));
                                m.setLastname(gemembergrouplist.getJSONObject(j).getString("lastname"));
                                m.setImage(gemembergrouplist.getJSONObject(j).getString("image"));
                                mlist.add(m);
                                d.setMembers(mlist);

                            }
                            inviteNewlist.setDetails(dlist);


                        }
                        // dataList.add(inviteNewlist);

                        Log.d("myinvitelist", new Gson().toJson(inviteNewlist));


                        // inviteNewlist= Utility.getModelFromJson(jsonObject.toString(), GroupData.class);
                        //  Log.d("aaaaaaaaaa",new Gson().toJson(inviteNewlist));

                        //mInviteListsModel = Utility.getModelFromJson(jsonObject.toString(), InviteListsModel.class);


                        if (inviteNewlist != null) {
                            GroupInvitePeoplelist adapter = new GroupInvitePeoplelist(inviteNewlist, 1);
                            listView.setAdapter(adapter);
                       /*     setAdapter();*/
                            listView.setRefreshing(false);
                        }
                    } else {
                        Utility.showToastMessage(InvitePeopleFav.this, jsonObject.optString(KEY_MSG));
                    }
                    progreesWheel.setVisibility(View.INVISIBLE);
                    progreesWheel.stopAnimation();
                }
                break;
            case 2:
                finish();
                break;

            case 3:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        // mInviteListsModel = Utility.getModelFromJson(jsonObject.toString(), InviteListsModel.class);
                        //   mInviteListsModel = Utility.getModelFromJson(jsonObject.toString(), InviteListsModel.class);

                        JSONArray mainArray = new JSONArray(jsonObject.getString("details"));
                        // JSONObject nearby = new JSONObject(details.getString("near_by"));
                        // JSONArray mainArray = new JSONArray(nearby.getString("buddylist"));
                        for (int i = 0; i < mainArray.length(); i++) {
                            IFavGroupNearModel nearModel = new IFavGroupNearModel();
                            nearModel.setIduser(mainArray.getJSONObject(i).getString("iduser"));
                            // nearModel.setImage(mainArray.getJSONObject(i).getString("image"));
                            //   Log.d("customimg",Gson mainArray.getJSONObject(i).getJSONArray(("images")));
                            if (mainArray.getJSONObject(i).getJSONArray(("images")).length() > 0) {
                                nearModel.setImage(AppConstants.getiamgebaseurl() + mainArray.getJSONObject(i).getJSONArray(("images")).getJSONObject(0).getString("image"));

                            }

                          /*  push = AppConstants.getiamgebaseurl() + new JSONArray(mainArray.getJSONObject(i).getJSONArray(("images")).toString().replaceAll("\\[|\\]", "");
                            nearModel.setImage(push.replace("\"", ""));
                            Log.d("customimg", push.replace("\"", ""));
*/

                            //  Log.d("pickupimage", String.valueOf(AppConstants.getiamgebaseurl() + jsonArrayBuddy.getJSONObject(i).getJSONArray(("image")).getJSONObject(0).getString("image")));

                            nearModel.setLastname(mainArray.getJSONObject(i).getString("lastname"));
                            nearModel.setFirstname(mainArray.getJSONObject(i).getString("firstname"));
                            //  nearModel.setAbout_yourself(mainArray.getJSONObject(i).getString("about_yourself"));
                            nearModel.setBadge(mainArray.getJSONObject(i).getString("badge"));
                            //   nearModel.setIsreceive_request(mainArray.getJSONObject(i).getString("isreceive_request"));
                            nearModel.setRating(mainArray.getJSONObject(i).getString("rating"));
                            //   nearModel.setRated_count(mainArray.getJSONObject(i).getString("rated_count"));
                            //  nearModel.setUser_type(mainArray.getJSONObject(i).getString("user_type"));
                            // nearModel.setEmail(mainArray.getJSONObject(i).getString("email"));
                            // nearModel.setIschallenge_badge(mainArray.getJSONObject(i).getString("ischallenge_badge"));
                            nearModel.setFacebookid(mainArray.getJSONObject(i).getString("facebookid"));
                            //  nearModel.setFav(mainArray.getJSONObject(i).getString("fav"));
                            nearModel.setDistance(mainArray.getJSONObject(i).getString("distance"));
                            // JSONArray skills = new JSONArray(mainArray.getJSONObject(i).getString("skills"));

                            // nearModel.setPhone_no(mainArray.getJSONObject(i).getString("phone_no"));
                            //  nearModel.setAddress(mainArray.getJSONObject(i).getString("address"));

                      /*      if (skills.length() > 0) {
                                skillslist = new ArrayList<>();
                            }
                            for (int j = 0; j < skills.length(); j++) {
                                SkillsModel skillsModel = new SkillsModel();
                                skillsModel.setActivity(skills.getJSONObject(j).getString("activity"));
                                skillsModel.setLevel(skills.getJSONObject(j).getString("level"));
                                skillsModel.setIs_open(skills.getJSONObject(j).getString("type"));
                                skillsModel.setType(skills.getJSONObject(j).getString("is_open"));
                                skillslist.add(skillsModel);
                            }

                            nearModel.setSkills(skillslist);*/

                            near_bylist.add(nearModel);


                        }
                        JSONObject page = new JSONObject(jsonObject.getString("page"));

                        if (page != null) {

                            TOTAL_ITEMS_COUNT = page.optInt("total_items");
                            currentPageNumber = page.optInt("current_page");
                            Log.d("LastPage ", currentPageNumber + "");
                            currentPageNumber = currentPageNumber + 1;
                            Log.d("NextPage ", currentPageNumber + "");
                            total_count = page.optInt("total_pages");
                        }
                        if (!isLazy) {
                            // mInvitePeoplelist.add(mInviteListsModel);
                            if (near_bylist.size() > 0) {
                                try {
                                    lis = new InvitePeoplelist(near_bylist, mKey);
                                    listView.setAdapter(lis);
                                    listView.setRefreshing(false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        } else {
                            progreesWheel.setVisibility(View.INVISIBLE);
                            lis.notifyDataSetChanged();

                        }

                        isLazy = false;
                    } else {
                        Utility.showToastMessage(InvitePeopleFav.this, jsonObject.optString(KEY_MSG));
                    }


                }
                break;

            case 4:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        // mInviteListsModel = Utility.getModelFromJson(jsonObject.toString(), InviteListsModel.class);
                        //   mInviteListsModel = Utility.getModelFromJson(jsonObject.toString(), InviteListsModel.class);

                        JSONObject details = new JSONObject(jsonObject.getString("details"));
                        JSONArray mainArray = new JSONArray(details.getString("near_by_search"));
                        for (int i = 0; i < mainArray.length(); i++) {
                            IFavGroupNearModel nearModel = new IFavGroupNearModel();
                            nearModel.setIduser(mainArray.getJSONObject(i).getString("iduser"));
                            nearModel.setImage(mainArray.getJSONObject(i).getString("image"));
                            nearModel.setLastname(mainArray.getJSONObject(i).getString("lastname"));
                            nearModel.setFirstname(mainArray.getJSONObject(i).getString("firstname"));
                            nearModel.setAbout_yourself(mainArray.getJSONObject(i).getString("about_yourself"));
                            nearModel.setBadge(mainArray.getJSONObject(i).getString("badge"));
                            nearModel.setIsreceive_request(mainArray.getJSONObject(i).getString("isreceive_request"));
                            nearModel.setRating(mainArray.getJSONObject(i).getString("rating"));
                            nearModel.setRated_count(mainArray.getJSONObject(i).getString("rated_count"));
                            nearModel.setUser_type(mainArray.getJSONObject(i).getString("user_type"));
                            nearModel.setEmail(mainArray.getJSONObject(i).getString("email"));
                            nearModel.setIschallenge_badge(mainArray.getJSONObject(i).getString("ischallenge_badge"));
                            nearModel.setFav(mainArray.getJSONObject(i).getString("fav"));
                            nearModel.setDistance(mainArray.getJSONObject(i).getString("distance"));
                            JSONArray skills = new JSONArray(mainArray.getJSONObject(i).getString("skills"));

                            nearModel.setPhone_no(mainArray.getJSONObject(i).getString("phone_no"));
                            nearModel.setAddress(mainArray.getJSONObject(i).getString("address"));

                            if (skills.length() > 0) {
                                skillslist = new ArrayList<>();
                            }
                            for (int j = 0; j < skills.length(); j++) {
                                SkillsModel skillsModel = new SkillsModel();
                                skillsModel.setActivity(skills.getJSONObject(j).getString("activity"));
                                skillsModel.setLevel(skills.getJSONObject(j).getString("level"));
                                skillsModel.setIs_open(skills.getJSONObject(j).getString("type"));
                                skillsModel.setType(skills.getJSONObject(j).getString("is_open"));
                                skillslist.add(skillsModel);
                            }

                            nearModel.setSkills(skillslist);

                            nearby_searchlist.add(nearModel);

                            listView.setRefreshing(false);
                        }


                    } else {
                        Utility.showToastMessage(InvitePeopleFav.this, jsonObject.optString(KEY_MSG));
                    }


                }
                break;

            case 5:

                if (jsonObject != null) {
                    Log.d("myresppp", new Gson().toJson(jsonObject));
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        JSONObject details = new JSONObject(jsonObject.getString("details"));
                        // mInviteListsModel = Utility.getModelFromJson(jsonObject.toString(), InviteListsModel.class);
                        //   mInviteListsModel = Utility.getModelFromJson(jsonObject.toString(), InviteListsModel.class);

                        //   JSONObject details = new JSONObject(jsonObject.getString("near_by_search"));
                        JSONArray mainArray = new JSONArray(details.getString("buddylist"));
                        for (int i = 0; i < mainArray.length(); i++) {
                            IFavGroupNearModel nearModel = new IFavGroupNearModel();
                            nearModel.setIduser(mainArray.getJSONObject(i).getString("iduser"));
                            nearModel.setImage(mainArray.getJSONObject(i).getString("image"));
                            nearModel.setLastname(mainArray.getJSONObject(i).getString("lastname"));
                            nearModel.setFirstname(mainArray.getJSONObject(i).getString("firstname"));
                            nearModel.setAbout_yourself(mainArray.getJSONObject(i).getString("about_yourself"));
                            nearModel.setBadge(mainArray.getJSONObject(i).getString("badge"));
                            nearModel.setIsreceive_request(mainArray.getJSONObject(i).getString("isreceive_request"));
                            nearModel.setRating(mainArray.getJSONObject(i).getString("rating"));
                            nearModel.setRated_count(mainArray.getJSONObject(i).getString("rated_count"));
                            nearModel.setUser_type(mainArray.getJSONObject(i).getString("user_type"));
                            nearModel.setEmail(mainArray.getJSONObject(i).getString("email"));
                            nearModel.setIschallenge_badge(mainArray.getJSONObject(i).getString("ischallenge_badge"));
                            nearModel.setFav(mainArray.getJSONObject(i).getString("fav"));
                            nearModel.setDistance(mainArray.getJSONObject(i).getString("distance"));
                            JSONArray skills = new JSONArray(mainArray.getJSONObject(i).getString("skills"));

                            nearModel.setPhone_no(mainArray.getJSONObject(i).getString("phone_no"));
                            nearModel.setAddress(mainArray.getJSONObject(i).getString("address"));

                            if (skills.length() > 0) {
                                skillslist = new ArrayList<>();
                            }
                            for (int j = 0; j < skills.length(); j++) {
                                SkillsModel skillsModel = new SkillsModel();
                                skillsModel.setActivity(skills.getJSONObject(j).getString("activity"));
                                skillsModel.setLevel(skills.getJSONObject(j).getString("level"));
                                skillsModel.setIs_open(skills.getJSONObject(j).getString("type"));
                                skillsModel.setType(skills.getJSONObject(j).getString("is_open"));
                                skillslist.add(skillsModel);
                            }

                            nearModel.setSkills(skillslist);

                            nearby_searchlist.add(nearModel);
                            if (nearby_searchlist.size() > 0) {
                                lis = new InvitePeoplelist(nearby_searchlist, mKey);
                                listView.setAdapter(lis);
                                listView.setRefreshing(false);
                            }


                        }


                    } else {
                        Utility.showToastMessage(InvitePeopleFav.this, jsonObject.optString(KEY_MSG));
                    }


                }

                break;
        }

        progreesWheel.setVisibility(View.GONE);
        progreesWheel.stopAnimation();
    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {
        progreesWheel.setVisibility(View.GONE);
        progreesWheel.stopAnimation();
    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        progreesWheel.setVisibility(View.GONE);
        progreesWheel.stopAnimation();
    }

    @Override
    public void removeProgress(Boolean hideFlag) {
        progreesWheel.setVisibility(View.GONE);
        progreesWheel.stopAnimation();
    }

    private ArrayList<FavouriteModel> removeExsting() {
        ArrayList<FavouriteModel> temp_favouriteModels = new ArrayList<>();
        for (int j = 0; j < invited_peoples.size(); j++) {
            for (FavouriteModel book : arrayList) {
                if (book.getId().equals(invited_peoples.get(j).getId())) {
                    temp_favouriteModels.add(book);
                }
            }
            arrayList.removeAll(temp_favouriteModels);
        }

        temp_favouriteModels = removeFavDuplicate(arrayList);
        return temp_favouriteModels;
    }

    private ArrayList<FavouriteModel> removeFavDuplicate(ArrayList<FavouriteModel> favmemberlist) {

        System.out.println(favmemberlist);

        Set set = new TreeSet(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {

                if (lhs instanceof FavouriteModel && rhs instanceof FavouriteModel) {

                    if (((FavouriteModel) lhs).getId().equalsIgnoreCase(((FavouriteModel) rhs).getId())) {
                        return 0;
                    }
                }
                return 1;
            }
        });
        set.addAll(favmemberlist);

        System.out.println("\n***** After removing duplicates *******\n");

        final ArrayList newList = new ArrayList(set);

        System.out.println(set);

        return newList;
    }

    @Override
    public void getSelectedGroupItems(ArrayList<String> selectedFavGroup, ArrayList<InviteGroupModel> fullGroupList) {

        if (selectedFavGroup != null) {
            Log.d("inviteGroupModels", ":" + selectedFavGroup.toString());
            Log.d("selectedGroups", ":" + selectedFavGroup.toString());
        }
    }

    @Override
    public void addSelectedMembers(ArrayList<FavouriteModel> getSelectedMember, ArrayList<String> selectedMemberID, ArrayList<String> seleted_GroupId) {
        if (getSelectedMember != null && selectedMemberID != null) {
            Log.d("selectedMemberID", ": " + selectedMemberID.toString());
            Log.d("selectedFav", ": " + selectedFav.toString());
            this.seleted_GroupId = seleted_GroupId;
            Log.d("seleted_GroupId", ": " + seleted_GroupId.size());
            tempSelectedFav.addAll(selectedFav);
            tempSelectedFav.addAll(selectedMemberID);

            HashSet<String> listToSet = new HashSet<String>(tempSelectedFav);
            ArrayList<String> SelectedFavID = new ArrayList<String>(listToSet);
            Log.d("SelectedFavID's", ":" + SelectedFavID);

            //arrayListInvite.addAll(invited_peoples);
            temparrayListInvite.addAll(arrayListInvite);
            temparrayListInvite.addAll(getSelectedMember);
            HashSet<FavouriteModel> listToSet1 = new HashSet<FavouriteModel>(temparrayListInvite);
            ArrayList<FavouriteModel> SelectedFavDataSet = new ArrayList<FavouriteModel>(listToSet1);
            Log.d("SelectedFavDataSet", ":" + SelectedFavDataSet);

            tempSelectedFav = SelectedFavID;
            temparrayListInvite = SelectedFavDataSet;
            ShowButton(tempSelectedFav);
        }


    }

    @Override
    public void removeSelectedMembers(ArrayList<FavouriteModel> getSelectedMember, ArrayList<String> selectedMemberID, ArrayList<String> seleted_GroupId) {

        if (getSelectedMember != null && selectedMemberID != null) {

            Log.d("selectedMemberID", ": " + selectedMemberID.toString());
            Log.d("selectedFav", ": " + selectedFav.toString());

            tempSelectedFav.addAll(selectedFav);
            tempSelectedFav.removeAll(selectedMemberID);
            HashSet<String> listToSet = new HashSet<String>(tempSelectedFav);
            ArrayList<String> SelectedFavID = new ArrayList<String>(listToSet);
            Log.d("SelectedFavID's", ":" + SelectedFavID);


            temparrayListInvite.addAll(arrayListInvite);
            temparrayListInvite.removeAll(getSelectedMember);
            HashSet<FavouriteModel> listToSet1 = new HashSet<FavouriteModel>(temparrayListInvite);
            ArrayList<FavouriteModel> SelectedFavDataSet = new ArrayList<FavouriteModel>(listToSet1);
            Log.d("SelectedFavDataSet", ":" + SelectedFavDataSet);

            tempSelectedFav = SelectedFavID;
            temparrayListInvite = SelectedFavDataSet;
            ShowButton(tempSelectedFav);
        }

    }

    @Override
    public void onBackPressed() {
        Intent output = new Intent();
        output.putExtra(invitecount, 0);
        setResult(RESULT_OK, output);
        if (gpsTracker != null) {
            gpsTracker.stopUsingGPS();
        }

        finish();
        Utility.setEventTracking(InvitePeopleFav.this, "Invite People on create Pick Up page", "Back Arrow button Invite People on create Pick Up page");
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == tvCancel) {
            if (!IspickupGuest) {
                Intent output = new Intent();
                output.putExtra(invitecount, 0);
                setResult(RESULT_OK, output);
                finish();
            } else {
                finish();
            }

        } else if (v == tvGroups) {
            Intent intent = new Intent(InvitePeopleFav.this, InviteGroups.class);
            Utility.setEventTracking(InvitePeopleFav.this, "Invite People on create Pick Up page", "Group button Invite people on create pickup page");

            intent.putExtra("isEdit", isEdit);
            intent.putExtra(activity, activityVal);
            intent.putExtra(invitecount, countVal);
            intent.putExtra(modekey, modeVal);
            Bundle bundle = new Bundle();
            bundle.putSerializable("invited_peoples", invited_peoples);
            bundle.putSerializable("seleted_GroupId", seleted_GroupId);
            bundle.putSerializable("selectedMembers", selectedFav);
            intent.putExtras(bundle);
            startActivityForResult(intent, 5);

        } else if (v == inviteLayout) {
            // arrayListInvite =temparrayListInvite;

            Log.d("arrayListInvite", ":" + arrayListInvite.size());
            temparrayListInvite.addAll(arrayListInvite);
            tempSelectedFav.addAll(selectedFav);
            HashSet<String> listToSet1 = new HashSet<>(tempSelectedFav);
            ArrayList<String> final_SelectedFavID = new ArrayList<>(listToSet1);
            Log.d("WD dupSelectedFavID's", ":" + final_SelectedFavID);

            // selectedFav = tempSelectedFav;
            if (!IspickupGuest) {
                Intent output = new Intent();
                output.putExtra("DetailedArray", temparrayListInvite);
                output.putExtra(invitedata, final_SelectedFavID);
                Bundle bundle = new Bundle();
                bundle.putSerializable("seleted_GroupId", seleted_GroupId);
                output.putExtras(bundle);
                setResult(RESULT_OK, output);
                finish();
            } else {
                if (Utility.isConnectingToInternet(InvitePeopleFav.this)) {
                    progreesWheel.setVisibility(View.VISIBLE);
                    progreesWheel.startAnimation();
                    Map<String, String> params = new HashMap<>();
                    params.put("idschedule", idschedule);
                    params.put("memberid", "" + final_SelectedFavID.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                    params.put("idgroup", "" + seleted_GroupId.toString().replaceAll("\\[", "").replaceAll("\\]", ""));

                    RequestHandler.getInstance().stringRequestVolley(InvitePeopleFav.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(InvitePeopleFav.this, isStaging)) + sendInvitation, params, this, 2);
                } else {
                    Utility.showInternetError(InvitePeopleFav.this);
                }
            }
        }

        switch (v.getId()) {
            case R.id.favorites_but:
                pickUpAutoList = new ArrayList<>();
                editSearch.setAdapter(null);
                editSearch.setHint("Search");
                editSearch.setText("");
                searchClose.setVisibility(View.GONE);
                isntSelected();
                favorites_but.setSelected(true);
                favorites_but.setTextColor(getResources().getColor(R.color.white));
                mKey = 0;
                //mInvitePeopleAdapter.notifyDataSetInvalidated();
                Utility.setEventTracking(InvitePeopleFav.this, "Invite People on create Pick Up page", "Favorites button on Invite People on create Pick Up page");
                if (arrayList != null && arrayList.size() > 0) {
                    mAdapter = new FavAddAdapter(arrayList);
                    listView.setAdapter(mAdapter);
                } else {
                    getfevoriteList(currentPageNumber, limit);

                }


                // getInviteList();
                //mInvitePeopleAdapter.notifyDataChanged(null);
                //mInvitePeopleAdapter.notifyDataChanged(mInviteListsModel.getDetails().getFavourites());
                //mInvitePeopleAdapter.notifyDataSetInvalidated();
//                setAdapter();
                //mInvitePeopleAdapter.notifyDataChanged(null);
                //mInvitePeopleAdapter.notifyDataChanged(mInviteListsModel.getDetails().getFavourites());
                break;
            case R.id.group_but:
                pickUpAutoList = new ArrayList<>();
                getGroups();

                editSearch.setAdapter(null);
                editSearch.setHint("Search");
                editSearch.setText("");
                searchClose.setVisibility(View.GONE);
                isntSelected();
                Utility.setEventTracking(InvitePeopleFav.this, "Invite People on create Pick Up page", "Groups button Invite People on create Pick Up page");

                group_but.setSelected(true);
                group_but.setTextColor(getResources().getColor(R.color.white));
                mKey = 1;
                // setAdapter();
                //  getInviteList();
                //setAdapter();
                break;
            case R.id.near_but:
                editSearch.setText("");
                getnearbyList(1, limit);
                isLazy = false;
             /*   if (mInviteListsModel != null && mInviteListsModel.getDetails().getNearBy() != null) {
                    mGlobalInviteListsModel = mInviteListsModel;
                    setAdapter();

                } else {
                    getnearbyList(currentPageNumber,limit);
                    isLazy = false;
                }*/

                Utility.setEventTracking(InvitePeopleFav.this, "Invite People on create Pick Up page", "Near By Button Invite People on create Pick Up page");

                try {
                    setActivityListAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editSearch.setHint("Search for a person or activity");
                isntSelected();
                searchClose.setVisibility(View.GONE);
                near_but.setSelected(true);
                near_but.setTextColor(getResources().getColor(R.color.white));
                mKey = 2;
                setAdapter();
                //   getInviteList();
//                setAdapter();
                break;
            /*case R.id.search_but:
                editSearch.setVisibility(View.VISIBLE);
                search_but.setVisibility(View.GONE);
                invite_title.setVisibility(View.GONE);
                searchClose.setVisibility(View.VISIBLE);
                break;*/
        }
    }

    /***/
    private void setAdapter() {
        if (mInviteListsModel != null) {
            try {
                mInvitePeopleAdapter = new InvitePeople(mInviteListsModel, mKey);
                listView.invalidate();
                listView.setAdapter(mInvitePeopleAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void invite_search(String searchText) {
        nearby_searchlist = new ArrayList<>();
        Map<String, String> param = new HashMap<>();
        param.put("iduser", SharedPref.getInstance().getStringVlue(InvitePeopleFav.this, userId));
        param.put("latitude", "" + mGpsTracker.getLatitude());
        param.put("longitude", "" + mGpsTracker.getLongitude());
        param.put("user_type", "" + SharedPref.getInstance().getIntVlue(InvitePeopleFav.this, USER_TYPE));
        param.put("limit", "" + 10);
        param.put("page", "" + 1);
        param.put("radius_limit", "200");


        param.put("search_name", searchText.trim());
        RequestHandler.getInstance().stringRequestVolley(InvitePeopleFav.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(InvitePeopleFav.this, isStaging)) + buddysearch, param, this, 5);
    }

    /***/
    private void isntSelected() {
        favorites_but.setSelected(false);
        group_but.setSelected(false);
        near_but.setSelected(false);

        favorites_but.setTextColor(getResources().getColor(R.color.black));
        group_but.setTextColor(getResources().getColor(R.color.black));
        near_but.setTextColor(getResources().getColor(R.color.black));
    }

    public void ShowButton(ArrayList<String> selectedFav) {
        if (selectedFav.size() > 0) {
            inviteLayout.setVisibility(View.VISIBLE);
        } else {
            inviteLayout.setVisibility(View.GONE);
        }
    }

    /***/
    public void filter(String charText, boolean activitySearch) {
        mTempInvitePeople.clear();
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.length() > 0) {
            Log.d("Filter Adapt Count = ", "" + mAdapter.favAllItems.size());

            for (int i = 0; i < mInvitePeopleAdapter.mInvitePeople.size(); i++) {
                IFavGroupNearModel wp = mInvitePeopleAdapter.mInvitePeople.get(i);
                String name;

                if (!activitySearch) {
                    if (mInvitePeopleAdapter.key == 1)
                        name = wp.getGroupname();
                    else
                        name = wp.getFirstname();
                } else {
                    name = Utility.getJsonFromModel(wp, IFavGroupNearModel.class);
                    try {
                        JSONObject skillSet = new JSONObject(name);
                        name = skillSet.optJSONArray("skills").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    Log.e("Skills String " + i, "" + name);
                    mTempInvitePeople.add(wp);
                }
            }
        }

        if (near_but.isSelected() && mTempInvitePeople.size() == 0 && activitySearchCount == 0) {
            activitySearchCount++;
            Log.e("Calling Activity", "Search");
//            if (activitySearchCount > 4 && activitySearchCount < 8) {
            filter(charText, true);
//                activitySearchCount = 0;
            //activitySearchFlag = true;
//            }
        }

        Log.e("Invite Activity = ", "" + mTempInvitePeople.size());

        listView.invalidate();
        mInvitePeopleAdapter.count = mTempInvitePeople.size();
        if (mInvitePeopleAdapter != null) {
            mInvitePeopleAdapter.notifyDataChanged(mTempInvitePeople);
        }
        progreesWheel.setVisibility(View.GONE);
        progreesWheel.clearAnimation();
    }


    public void filternearby(String charText, boolean activitySearch) {
        mTempInvitePeople.clear();
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.length() > 0) {
            Log.d("Filter Adapt Count = ", "" + nearby_searchlist.size());

            for (int i = 0; i < nearby_searchlist.size(); i++) {
                IFavGroupNearModel wp = nearby_searchlist.get(i);
                String name;

                if (!activitySearch) {
                    if (lis.key == 1)
                        name = wp.getGroupname();
                    else
                        name = wp.getFirstname();
                } else {
                    name = Utility.getJsonFromModel(wp, IFavGroupNearModel.class);
                    try {
                        JSONObject skillSet = new JSONObject(name);
                        name = skillSet.optJSONArray("skills").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    Log.e("Skills String " + i, "" + name);
                    mTempInvitePeople.add(wp);
                }
            }
        }

        if (near_but.isSelected() && mTempInvitePeople.size() == 0 && activitySearchCount == 0) {
            activitySearchCount++;
            Log.e("Calling Activity", "Search");
//            if (activitySearchCount > 4 && activitySearchCount < 8) {
            filternearby(charText, true);
//                activitySearchCount = 0;
            //activitySearchFlag = true;
//            }
        }

        Log.e("Invite Activity = ", "" + mTempInvitePeople.size());

        listView.invalidate();
        lis.count = mTempInvitePeople.size();
        if (lis != null) {
            lis.notifyDataChanged(mTempInvitePeople);
            listView.setRefreshing(false);
        }
        progreesWheel.setVisibility(View.GONE);
        progreesWheel.clearAnimation();
    }


    @Override
    public boolean shouldLoad() {
        boolean shouldLoad = false;
        if (mKey == 0) {
            shouldLoad = (mAdapter.getItemCount() < TOTAL_ITEMS_COUNT);
            Log.d("shouldLoad", "shouldLoad");
            if (shouldLoad) {
                try {
                    Log.d("shouldLoad11111", "shouldLoad");
                    listView.setRefreshing(false);
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }

            } else {
                listView.setRefreshing(false);
            }
        } else {
            if (lis != null) {
                shouldLoad = (lis.getItemCount() < TOTAL_ITEMS_COUNT);
                Log.d("shouldLoad", "shouldLoad");
                if (shouldLoad) {
                    try {
                        Log.d("shouldLoad11111", "shouldLoad");
                        listView.setRefreshing(false);
                    } catch (IllegalStateException ex) {
                        ex.printStackTrace();
                    }

                } else {
                    listView.setRefreshing(false);
                }
            }

        }
        Log.d("shpuldload", shouldLoad + "");
        return shouldLoad;
    }

    @Override
    public void loadNextPage() {

        if (!isLazy) {
            if (mKey == 0) {
                listView.setRefreshing(true);
                Log.d("getcurruntpage", currentPageNumber + "");
                isLazy = true;
                getfevoriteList(currentPageNumber, limit);
            } else {
                listView.setRefreshing(false);
                Log.d("getcurruntpage", currentPageNumber + "");
                isLazy = true;
                getnearbyList(currentPageNumber, limit);
            }

        }
    }

    class InvitePeople extends RecyclerView.Adapter<InvitePeople.ViewHolder> {
        SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
        private int count;
        private int key;
        private ArrayList<IFavGroupNearModel> mInvitePeople;

        InvitePeople(InviteListsModel mInviteListModel, int key) {
            this.key = key;
            if (key == 0) {
                mInvitePeople = mInviteListModel.getDetails().getFavourites();
                count = mInviteListModel.getDetails().getFavourites().size();
            } else if (key == 1) {
                mInvitePeople = mInviteListModel.getDetails().getGroup();
                count = mInviteListModel.getDetails().getGroup().size();
            } else if (key == 2) {
                mInvitePeople = mInviteListModel.getDetails().getNearBy().getBuddylist();
                count = mInviteListModel.getDetails().getNearBy().getBuddylist().size();
            } else if (key == 3) {
                mInvitePeople = mInviteListModel.getDetails().getNearBySearch();
                count = mInviteListModel.getDetails().getNearBySearch().size();
            }
            Log.e("Key = " + key, "Count = " + count);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_add_members, null);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final ViewHolder viewHolder = new ViewHolder(holder.itemView);
            if (mInvitePeople.size() > position) {

                if (key == 1) {
                    viewHolder.tvFavName.setText(mInvitePeople.get(position).getGroupname());
                    viewHolder.imgFavAdd.setImageResource(R.drawable.default_user_icon);
                    viewHolder.checkAdd.setTag(mInvitePeople.get(position).getIdgroup());
                } else {
                    viewHolder.tvFavName.setText(mInvitePeople.get(position).getFirstname());
                    Utility.setImageUniversalLoader(InvitePeopleFav.this,
                            mInvitePeople.get(position).getImage(),
                            viewHolder.imgFavAdd);
                    viewHolder.checkAdd.setTag(mInvitePeople.get(position).getIduser());
                }

                if (key == 1) {
                    if (seleted_GroupId != null && seleted_GroupId.size() > 0) {
                        for (String id : seleted_GroupId) {
                            if (mInvitePeople.get(position).getIdgroup().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.get(position).getIduser()), true);
                            }
                        }
                    }
                } else {
                    if (final_SelectedFavID != null && final_SelectedFavID.size() > 0) {
                        for (String id : final_SelectedFavID) {
                            if (mInvitePeople.get(position).getIduser().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.get(position).getIduser()), true);
                            }
                        }
                    }
                }

                viewHolder.checkAdd.setVisibility(View.VISIBLE);
                viewHolder.invited_or_not.setVisibility(View.GONE);

                //if (isEdit) {
                if (mInvitePeople.get(position).getInvite_status() != null) {
                    if (mInvitePeople.get(position).getInvite_status().trim().length() > 0) {
                        viewHolder.checkAdd.setVisibility(View.GONE);
                        viewHolder.invited_or_not.setVisibility(View.VISIBLE);
                    }
                }
                //}

                if (key == 1) {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.get(position).getIduser())));
                } else {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.get(position).getIduser())));
                }

                viewHolder.checkAdd.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Invite Fav from without limit
                        String id = "";
                        if (key == 1)
                            id = mInvitePeople.get(position).getIdgroup();
                        else
                            id = mInvitePeople.get(position).getIduser();
                        selectFav(viewHolder, v, id, position);
                    }
                });

            }

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return count;
        }


        /***/
        private void selectFav(ViewHolder viewHolder, View v, String id, int position) {
            if (viewHolder.checkAdd.isChecked()) {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), true);

                if (key == 1) {
                    seleted_GroupId.add(id);
                    arrayListInvite.add(new FavouriteModel(mInvitePeople.get(position).getIdgroup(), mInvitePeople.get(position).getGroupname(), mInvitePeople.get(position).getImage()));
                } else {
                    selectedFav.add(id);
                    arrayListInvite.add(new FavouriteModel(mInvitePeople.get(position).getIduser(), mInvitePeople.get(position).getFirstname(), mInvitePeople.get(position).getImage()));
                }
            } else {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), false);

                if (key == 1) {
                    seleted_GroupId.remove(id);
                } else {
                    selectedFav.remove(id);
                }
                for (int i = 0; i < arrayListInvite.size(); i++) {
                    if (mInvitePeople.get(position).getIduser().equals(arrayListInvite.get(i).getId())) {
                        arrayListInvite.remove(i);
                    }
                }
            }
        }


        /***/
        public void notifyDataChanged(ArrayList<IFavGroupNearModel> mTempInvitePeople) {
            //notifyDataSetInvalidated();
            mInvitePeople.clear();
            if (mTempInvitePeople != null) {
                mInvitePeople.addAll(mTempInvitePeople);
            }
            super.notifyDataSetChanged();
        }

        /***/
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFavName;
            ImageView imgFavAdd;
            CheckBox checkAdd;
            CustomTextView invited_or_not;


            public ViewHolder(View view) {
                super(view);
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFavAdd = ((ImageView) view.findViewById(R.id.imgFavAdd));
                checkAdd = (CheckBox) view.findViewById(R.id.checkAdd);
                invited_or_not = (CustomTextView) view.findViewById(R.id.invited_or_not);
                view.setTag(this);

            }
        }
    }

    class GroupInvitePeoplelist extends RecyclerView.Adapter<GroupInvitePeoplelist.ViewHolder> {
        SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
        private int count;
        private int key;
        private GroupData mInvitePeople;

        GroupInvitePeoplelist(GroupData mymInvitePeople, int key) {
            this.key = key;
            this.mInvitePeople = mymInvitePeople;


        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_add_members, null);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final ViewHolder viewHolder = new ViewHolder(holder.itemView);
            if (mInvitePeople.getDetails().size() > position) {

                if (key == 1) {

                    viewHolder.tvFavName.setText(mInvitePeople.getDetails().get(position).getGroupname());
                    viewHolder.imgFavAdd.setImageResource(R.drawable.default_user_icon);
                    viewHolder.checkAdd.setTag(mInvitePeople.getDetails().get(position).getIdgroup());
                } else {
                    //  viewHolder.tvFavName.setText(mInvitePeople.get(0).getDetails().get(position).getFirstname());
                   /* Utility.setImageUniversalLoader(InvitePeopleFav.this,
                            mInvitePeople.get(position).getImage(), viewHolder.imgFavAdd);*/
                    viewHolder.checkAdd.setTag(mInvitePeople.getDetails().get(position).getIduser());
                }

                if (key == 1) {
                    if (seleted_GroupId != null && seleted_GroupId.size() > 0) {
                        for (String id : seleted_GroupId) {
                            if (mInvitePeople.getDetails().get(position).getIdgroup().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.getDetails().get(position).getIduser()), true);
                            }
                        }
                    }
                } else {
                    if (final_SelectedFavID != null && final_SelectedFavID.size() > 0) {
                        for (String id : final_SelectedFavID) {
                            if (mInvitePeople.getDetails().get(position).getIduser().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.getDetails().get(position).getIduser()), true);
                            }
                        }
                    }
                }

                viewHolder.checkAdd.setVisibility(View.VISIBLE);
                viewHolder.invited_or_not.setVisibility(View.GONE);

                //if (isEdit) {
           /*     if (mInvitePeople.get(0).getDetails().get(position).getInvite_status() != null) {
                    if (mInvitePeople.get(position).getInvite_status().trim().length() > 0) {
                        viewHolder.checkAdd.setVisibility(View.GONE);
                        viewHolder.invited_or_not.setVisibility(View.VISIBLE);
                    }
                }*/
                //}

                if (key == 1) {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.getDetails().get(position).getIduser())));
                } else {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.getDetails().get(position).getIduser())));
                }

                viewHolder.checkAdd.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Invite Fav from without limit
                        String id = "";
                        if (key == 1)
                            id = mInvitePeople.getDetails().get(position).getIdgroup();
                        else
                            id = mInvitePeople.getDetails().get(position).getIduser();
                        selectFav(viewHolder, v, id, position);
                    }
                });

            }

        }

        public void notifyDataChanged(ArrayList<GroupData> mTempInvitePeople) {
            //notifyDataSetInvalidated();
         /*   mInvitePeople.clear();
            if (mTempInvitePeople != null) {
                mInvitePeople.getDetails()addAll(mTempInvitePeople);
            }*/
            super.notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mInvitePeople.getDetails().size();
        }


        /***/
        private void selectFav(ViewHolder viewHolder, View v, String id, int position) {
            if (viewHolder.checkAdd.isChecked()) {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), true);

                if (key == 1) {
                    seleted_GroupId.add(id);
                    arrayListInvite.add(new FavouriteModel(mInvitePeople.getDetails().get(position).getIdgroup(), mInvitePeople.getDetails().get(position).getGroupname(), ""));
                } else {
                    selectedFav.add(id);
                    // arrayListInvite.add(new FavouriteModel(mInvitePeople.get(0).getDetails().get(position).getIduser(), mInvitePeople.get(0).getDetails().get(position).getFirstname(), mInvitePeople.get(position).getImage()));
                }
            } else {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), false);

                if (key == 1) {
                    seleted_GroupId.remove(id);
                } else {
                    selectedFav.remove(id);
                }
                for (int i = 0; i < arrayListInvite.size(); i++) {
                    if (mInvitePeople.getDetails().get(position).equals(arrayListInvite.get(i).getId())) {
                        arrayListInvite.remove(i);
                    }
                }
            }
        }


        /***/
     /*   public void notifyDataChanged(ArrayList<IFavGroupNearModel> mTempInvitePeople) {
            //notifyDataSetInvalidated();
            mInvitePeople.clear();
            if (mTempInvitePeople != null) {
                mInvitePeople.addAll(mTempInvitePeople);
            }
            super.notifyDataSetChanged();
        }
*/

        /***/
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFavName;
            ImageView imgFavAdd;
            CheckBox checkAdd;
            CustomTextView invited_or_not;


            public ViewHolder(View view) {
                super(view);
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFavAdd = ((ImageView) view.findViewById(R.id.imgFavAdd));
                checkAdd = (CheckBox) view.findViewById(R.id.checkAdd);
                invited_or_not = (CustomTextView) view.findViewById(R.id.invited_or_not);
                view.setTag(this);

            }
        }
    }

    class InvitePeoplelist extends RecyclerView.Adapter<InvitePeoplelist.ViewHolder> {
        SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
        private int count;
        private int key;
        private ArrayList<IFavGroupNearModel> mInvitePeople;

        InvitePeoplelist(ArrayList<IFavGroupNearModel> mymInvitePeople, int key) {
            this.key = key;
            this.mInvitePeople = mymInvitePeople;


        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_add_members, null);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final ViewHolder viewHolder = new ViewHolder(holder.itemView);
            if (mInvitePeople.size() > position) {

                if (key == 1) {

                    viewHolder.tvFavName.setText(mInvitePeople.get(position).getGroupname());
                    viewHolder.imgFavAdd.setImageResource(R.drawable.default_user_icon);
                    viewHolder.checkAdd.setTag(mInvitePeople.get(position).getIdgroup());
                } else {
                    viewHolder.tvFavName.setText(mInvitePeople.get(position).getFirstname());
                    Utility.setImageUniversalLoader(InvitePeopleFav.this,
                            mInvitePeople.get(position).getImage(), viewHolder.imgFavAdd);
                    viewHolder.checkAdd.setTag(mInvitePeople.get(position).getIduser());
                }

                if (key == 1) {
                    if (seleted_GroupId != null && seleted_GroupId.size() > 0) {
                        for (String id : seleted_GroupId) {
                            if (mInvitePeople.get(position).getIdgroup().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.get(position).getIduser()), true);
                            }
                        }
                    }
                } else {
                    if (final_SelectedFavID != null && final_SelectedFavID.size() > 0) {
                        for (String id : final_SelectedFavID) {
                            if (mInvitePeople.get(position).getIduser().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.get(position).getIduser()), true);
                            }
                        }
                    }
                }

                viewHolder.checkAdd.setVisibility(View.VISIBLE);
                viewHolder.invited_or_not.setVisibility(View.GONE);

                //if (isEdit) {
                if (mInvitePeople.get(position).getInvite_status() != null) {
                    if (mInvitePeople.get(position).getInvite_status().trim().length() > 0) {
                        viewHolder.checkAdd.setVisibility(View.GONE);
                        viewHolder.invited_or_not.setVisibility(View.VISIBLE);
                    }
                }
                //}

                if (key == 1) {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.get(position).getIduser())));
                } else {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.get(position).getIduser())));
                }

                viewHolder.checkAdd.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Invite Fav from without limit
                        String id = "";
                        if (key == 1)
                            id = mInvitePeople.get(position).getIdgroup();
                        else
                            id = mInvitePeople.get(position).getIduser();
                        selectFav(viewHolder, v, id, position);
                    }
                });

            }

        }

        public void notifyDataChanged(ArrayList<IFavGroupNearModel> mTempInvitePeople) {
            //notifyDataSetInvalidated();
            mInvitePeople.clear();
            if (mTempInvitePeople != null) {
                mInvitePeople.addAll(mTempInvitePeople);
            }
            super.notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mInvitePeople.size();
        }


        /***/
        private void selectFav(ViewHolder viewHolder, View v, String id, int position) {
            if (viewHolder.checkAdd.isChecked()) {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), true);

                if (key == 1) {
                    seleted_GroupId.add(id);
                    arrayListInvite.add(new FavouriteModel(mInvitePeople.get(position).getIdgroup(), mInvitePeople.get(position).getGroupname(), mInvitePeople.get(position).getImage()));
                } else {
                    selectedFav.add(id);
                    arrayListInvite.add(new FavouriteModel(mInvitePeople.get(position).getIduser(), mInvitePeople.get(position).getFirstname(), mInvitePeople.get(position).getImage()));
                }
            } else {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), false);

                if (key == 1) {
                    seleted_GroupId.remove(id);
                } else {
                    selectedFav.remove(id);
                }
                for (int i = 0; i < arrayListInvite.size(); i++) {
                    if (mInvitePeople.get(position).getIduser().equals(arrayListInvite.get(i).getId())) {
                        arrayListInvite.remove(i);
                    }
                }
            }
        }


        /***/
     /*   public void notifyDataChanged(ArrayList<IFavGroupNearModel> mTempInvitePeople) {
            //notifyDataSetInvalidated();
            mInvitePeople.clear();
            if (mTempInvitePeople != null) {
                mInvitePeople.addAll(mTempInvitePeople);
            }
            super.notifyDataSetChanged();
        }
*/

        /***/
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFavName;
            ImageView imgFavAdd;
            CheckBox checkAdd;
            CustomTextView invited_or_not;


            public ViewHolder(View view) {
                super(view);
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFavAdd = ((ImageView) view.findViewById(R.id.imgFavAdd));
                checkAdd = (CheckBox) view.findViewById(R.id.checkAdd);
                invited_or_not = (CustomTextView) view.findViewById(R.id.invited_or_not);
                view.setTag(this);

            }
        }
    }


    /***/
/*    class InvitePeople extends BaseAdapter {
        SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
        private int count;
        private int key;
        private ArrayList<IFavGroupNearModel> mInvitePeople;

        InvitePeople(InviteListsModel mInviteListModel, int key) {
            this.key = key;
            if (key == 0) {
                mInvitePeople = mInviteListModel.getDetails().getFavourites();
                count = mInviteListModel.getDetails().getFavourites().size();
            } else if (key == 1) {
                mInvitePeople = mInviteListModel.getDetails().getGroup();
                count = mInviteListModel.getDetails().getGroup().size();
            } else if (key == 2) {
                mInvitePeople = mInviteListModel.getDetails().getNearBy().getBuddylist();
                count = mInviteListModel.getDetails().getNearBy().getBuddylist().size();
            } else if (key == 3) {
                mInvitePeople = mInviteListModel.getDetails().getNearBySearch();
                count = mInviteListModel.getDetails().getNearBySearch().size();
            }
            Log.e("Key = " + key, "Count = " + count);
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_add_members, parent, false))
                    : (ViewHolder) convertView.getTag();

            if (mInvitePeople.size() > position) {

                if (key == 1) {
                    viewHolder.tvFavName.setText(mInvitePeople.get(position).getGroupname());
                    viewHolder.imgFavAdd.setImageResource(R.drawable.default_user_icon);
                    viewHolder.checkAdd.setTag(mInvitePeople.get(position).getIdgroup());
                } else {
                    viewHolder.tvFavName.setText(mInvitePeople.get(position).getFirstname());
                    Utility.setImageUniversalLoader(InvitePeopleFav.this,
                            mInvitePeople.get(position).getImage(),
                            viewHolder.imgFavAdd);
                    viewHolder.checkAdd.setTag(mInvitePeople.get(position).getIduser());
                }

                if (key == 1) {
                    if (seleted_GroupId != null && seleted_GroupId.size() > 0) {
                        for (String id : seleted_GroupId) {
                            if (mInvitePeople.get(position).getIdgroup().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.get(position).getIduser()), true);
                            }
                        }
                    }
                } else {
                    if (final_SelectedFavID != null && final_SelectedFavID.size() > 0) {
                        for (String id : final_SelectedFavID) {
                            if (mInvitePeople.get(position).getIduser().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.get(position).getIduser()), true);
                            }
                        }
                    }
                }

                viewHolder.checkAdd.setVisibility(View.VISIBLE);
                viewHolder.invited_or_not.setVisibility(View.GONE);

                //if (isEdit) {
                if (mInvitePeople.get(position).getInvite_status() != null) {
                    if (mInvitePeople.get(position).getInvite_status().trim().length() > 0) {
                        viewHolder.checkAdd.setVisibility(View.GONE);
                        viewHolder.invited_or_not.setVisibility(View.VISIBLE);
                    }
                }
                //}

                if (key == 1) {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.get(position).getIduser())));
                } else {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.get(position).getIduser())));
                }

                viewHolder.checkAdd.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Invite Fav from without limit
                        String id = "";
                        if (key == 1)
                            id = mInvitePeople.get(position).getIdgroup();
                        else
                            id = mInvitePeople.get(position).getIduser();
                        selectFav(viewHolder, v, id, position);
                    }
                });

            }


            return convertView;
        }


        *//***//*
        private void selectFav(ViewHolder viewHolder, View v, String id, int position) {
            if (viewHolder.checkAdd.isChecked()) {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), true);

                if (key == 1) {
                    seleted_GroupId.add(id);
                    arrayListInvite.add(new FavouriteModel(mInvitePeople.get(position).getIdgroup(), mInvitePeople.get(position).getGroupname(), mInvitePeople.get(position).getImage()));
                } else {
                    selectedFav.add(id);
                    arrayListInvite.add(new FavouriteModel(mInvitePeople.get(position).getIduser(), mInvitePeople.get(position).getFirstname(), mInvitePeople.get(position).getImage()));
                }
            } else {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), false);

                if (key == 1) {
                    seleted_GroupId.remove(id);
                } else {
                    selectedFav.remove(id);
                }
                for (int i = 0; i < arrayListInvite.size(); i++) {
                    if (mInvitePeople.get(position).getIduser().equals(arrayListInvite.get(i).getId())) {
                        arrayListInvite.remove(i);
                    }
                }
            }
        }


        *//***//*
        public void notifyDataChanged(ArrayList<IFavGroupNearModel> mTempInvitePeople) {
            notifyDataSetInvalidated();
            mInvitePeople.clear();
            if (mTempInvitePeople != null) {
                mInvitePeople.addAll(mTempInvitePeople);
            }
            super.notifyDataSetChanged();
        }

        *//***//*
        class ViewHolder {
            TextView tvFavName;
            ImageView imgFavAdd;
            CheckBox checkAdd;
            CustomTextView invited_or_not;

            ViewHolder(View view) {
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFavAdd = ((ImageView) view.findViewById(R.id.imgFavAdd));
                checkAdd = (CheckBox) view.findViewById(R.id.checkAdd);
                invited_or_not = (CustomTextView) view.findViewById(R.id.invited_or_not);
                view.setTag(this);
            }
        }
    }*/

    /***/

    class FavAddAdapter extends RecyclerView.Adapter<FavAddAdapter.Viewholders> {
        SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
        private ArrayList<FavouriteModel> favAllItems = new ArrayList<>();

        FavAddAdapter(ArrayList<FavouriteModel> favItems) {
            this.favAllItems = favItems;
            itemCount = new ArrayList<>();
            mSparseBooleanArray = new SparseBooleanArray();
        }

        @Override
        public Viewholders onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_add_members, null);
            return new Viewholders(view);
        }

        @Override
        public void onBindViewHolder(Viewholders holder, final int position) {
            final Viewholders viewHolder = new Viewholders(holder.itemView);
            viewHolder.tvFavName.setText(favAllItems.get(position).getName());

            Picasso.with(InvitePeopleFav.this)
                    .load(favAllItems.get(position).getImage())
                    .placeholder(R.drawable.ic_profile).centerCrop().fit()
                    .into(viewHolder.imgFavAdd);


            //	viewHolder.imgFavAdd.setImageResource(favAllItems.get(position).getImage());


            viewHolder.checkAdd.setTag(position);


            if (final_SelectedFavID != null && final_SelectedFavID.size() > 0) {
                for (String id : final_SelectedFavID) {
                    if (favAllItems.get(position).getId().equals(id)) {
                        mSparseBooleanArray.put((Integer) viewHolder.checkAdd.getTag(), true);
                    }
                }
            }

            viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(position));

            viewHolder.checkAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Invite Fav from without limit
                    String id = favAllItems.get(position).getId();
                    selectFav(viewHolder, v, id, position);

                }
            });
        }

        @Override
        public int getItemCount() {
            return favAllItems.size();
        }

        public void remove(int position) {
            if (favAllItems.size() > position) {
                favAllItems.remove(position);
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        }

        class Viewholders extends RecyclerView.ViewHolder {
            TextView tvFavName;
            ImageView imgFavAdd;
            CheckBox checkAdd;

            public Viewholders(View view) {
                super(view);
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFavAdd = ((ImageView) view.findViewById(R.id.imgFavAdd));
                checkAdd = (CheckBox) view.findViewById(R.id.checkAdd);
                view.setTag(this);
            }
        }

        private void selectFav(Viewholders viewHolder, View v, String id, int position) {

            if (viewHolder.checkAdd.isChecked()) {
                mSparseBooleanArray.put((Integer) v.getTag(), true);
                selectedFav.add(id);
                arrayListInvite.add(new FavouriteModel(favAllItems.get(position).getId(), favAllItems.get(position).getName(), favAllItems.get(position).getImage()));

            } else {
                mSparseBooleanArray.put((Integer) v.getTag(), false);
                selectedFav.remove(id);
                for (int i = 0; i < arrayListInvite.size(); i++) {
                    if (favAllItems.get(position).getId().equals(arrayListInvite.get(i).getId())) {
                        arrayListInvite.remove(i);
                    }
                }
            }
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            favAllItems.clear();
            if (charText.length() == 0) {
                favAllItems.addAll(favAllItems);
            } else {
                for (FavouriteModel wp : favAllItems) {
                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        favAllItems.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    class InvitePeoplenearbyadapter extends BaseAdapter {
        SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
        private int count;
        private int key;
        ArrayList<IFavGroupNearModel> mInvitePeople;

        InvitePeoplenearbyadapter(ArrayList<IFavGroupNearModel> mInviteListModel, int key) {
            this.key = key;
            this.mInvitePeople = mInviteListModel;
            Log.e("Key = " + key, "Count = " + count);
        }

        @Override
        public int getCount() {
            return mInvitePeople.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_add_members, parent, false))
                    : (ViewHolder) convertView.getTag();

            if (mInvitePeople.size() > position) {

                if (key == 1) {
                    viewHolder.tvFavName.setText(mInvitePeople.get(position).getGroupname());
                    viewHolder.imgFavAdd.setImageResource(R.drawable.default_user_icon);
                    viewHolder.checkAdd.setTag(mInvitePeople.get(position).getIdgroup());
                } else {
                    viewHolder.tvFavName.setText(mInvitePeople.get(position).getFirstname());
                    Utility.setImageUniversalLoader(InvitePeopleFav.this,
                            mInvitePeople.get(position).getImage(),
                            viewHolder.imgFavAdd);
                    viewHolder.checkAdd.setTag(mInvitePeople.get(position).getIduser());
                }

                if (key == 1) {
                    if (seleted_GroupId != null && seleted_GroupId.size() > 0) {
                        for (String id : seleted_GroupId) {
                            if (mInvitePeople.get(position).getIdgroup().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.get(position).getIduser()), true);
                            }
                        }
                    }
                } else {
                    if (final_SelectedFavID != null && final_SelectedFavID.size() > 0) {
                        for (String id : final_SelectedFavID) {
                            if (mInvitePeople.get(position).getIduser().equals(id)) {
                                mSparseBooleanArray.put(Integer.parseInt(mInvitePeople.get(position).getIduser()), true);
                            }
                        }
                    }
                }

                viewHolder.checkAdd.setVisibility(View.VISIBLE);
                viewHolder.invited_or_not.setVisibility(View.GONE);

                //if (isEdit) {
                if (mInvitePeople.get(position).getInvite_status() != null) {
                    if (mInvitePeople.get(position).getInvite_status().trim().length() > 0) {
                        viewHolder.checkAdd.setVisibility(View.GONE);
                        viewHolder.invited_or_not.setVisibility(View.VISIBLE);
                    }
                }
                //}

                if (key == 1) {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.get(position).getIduser())));
                } else {
                    viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(Integer.parseInt(mInvitePeople.get(position).getIduser())));
                }

                viewHolder.checkAdd.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Invite Fav from without limit
                        String id = "";
                        if (key == 1)
                            id = mInvitePeople.get(position).getIdgroup();
                        else
                            id = mInvitePeople.get(position).getIduser();
                        selectFav(viewHolder, v, id, position);
                    }
                });

            }


            return convertView;
        }


        /***/
        private void selectFav(ViewHolder viewHolder, View v, String id, int position) {
            if (viewHolder.checkAdd.isChecked()) {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), true);

                if (key == 1) {
                    seleted_GroupId.add(id);
                    arrayListInvite.add(new FavouriteModel(mInvitePeople.get(position).getIdgroup(), mInvitePeople.get(position).getGroupname(), mInvitePeople.get(position).getImage()));
                } else {
                    selectedFav.add(id);
                    arrayListInvite.add(new FavouriteModel(mInvitePeople.get(position).getIduser(), mInvitePeople.get(position).getFirstname(), mInvitePeople.get(position).getImage()));
                }
            } else {
                mSparseBooleanArray.put(Integer.parseInt(v.getTag().toString()), false);

                if (key == 1) {
                    seleted_GroupId.remove(id);
                } else {
                    selectedFav.remove(id);
                }
                for (int i = 0; i < arrayListInvite.size(); i++) {
                    if (mInvitePeople.get(position).getIduser().equals(arrayListInvite.get(i).getId())) {
                        arrayListInvite.remove(i);
                    }
                }
            }
        }


        /***/
        public void notifyDataChanged(ArrayList<IFavGroupNearModel> mTempInvitePeople) {
            notifyDataSetInvalidated();
            mInvitePeople.clear();
            if (mTempInvitePeople != null) {
                mInvitePeople.addAll(mTempInvitePeople);
            }
            super.notifyDataSetChanged();
        }

        /***/
        class ViewHolder {
            TextView tvFavName;
            ImageView imgFavAdd;
            CheckBox checkAdd;
            CustomTextView invited_or_not;

            ViewHolder(View view) {
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFavAdd = ((ImageView) view.findViewById(R.id.imgFavAdd));
                checkAdd = (CheckBox) view.findViewById(R.id.checkAdd);
                invited_or_not = (CustomTextView) view.findViewById(R.id.invited_or_not);
                view.setTag(this);
            }
        }
    }




/*    class FavAddAdapter extends BaseAdapter {
        SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
        private ArrayList<FavouriteModel> favAllItems = new ArrayList<>();

        FavAddAdapter(ArrayList<FavouriteModel> favItems) {
            this.favAllItems = favItems;
            itemCount = new ArrayList<>();
            mSparseBooleanArray = new SparseBooleanArray();
        }

        @Override
        public int getCount() {
            return favAllItems.size();
        }

        @Override
        public FavouriteModel getItem(int position) {
            return favAllItems.get(position);
    }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(int position) {
            if (favAllItems.size() > position) {
                favAllItems.remove(position);
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_add_members, parent, false))
                    : (ViewHolder) convertView.getTag();

            viewHolder.tvFavName.setText(favAllItems.get(position).getName());

            Picasso.with(InvitePeopleFav.this)
                    .load(favAllItems.get(position).getImage())
                    .placeholder(R.drawable.ic_profile).centerCrop().fit()
                    .into(viewHolder.imgFavAdd);


            //	viewHolder.imgFavAdd.setImageResource(favAllItems.get(position).getImage());


            viewHolder.checkAdd.setTag(position);


            if (final_SelectedFavID != null && final_SelectedFavID.size() > 0) {
                for (String id : final_SelectedFavID) {
                    if (favAllItems.get(position).getId().equals(id)) {
                        mSparseBooleanArray.put((Integer) viewHolder.checkAdd.getTag(), true);
                    }
                }
            }

            viewHolder.checkAdd.setChecked(mSparseBooleanArray.get(position));

            viewHolder.checkAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Invite Fav from without limit
                    String id = favAllItems.get(position).getId();
                    selectFav(viewHolder, v, id, position);

                }
            });
            return convertView;
        }

        private void selectFav(ViewHolder viewHolder, View v, String id, int position) {

            if (viewHolder.checkAdd.isChecked()) {
                mSparseBooleanArray.put((Integer) v.getTag(), true);
                selectedFav.add(id);
                arrayListInvite.add(new FavouriteModel(favAllItems.get(position).getId(), favAllItems.get(position).getName(), favAllItems.get(position).getImage()));

            } else {
                mSparseBooleanArray.put((Integer) v.getTag(), false);
                selectedFav.remove(id);
                for (int i = 0; i < arrayListInvite.size(); i++) {
                    if (favAllItems.get(position).getId().equals(arrayListInvite.get(i).getId())) {
                        arrayListInvite.remove(i);
                    }
                }
            }
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            favAllItems.clear();
            if (charText.length() == 0) {
                favAllItems.addAll(favAllItems);
            } else {
                for (FavouriteModel wp : favAllItems) {
                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        favAllItems.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

        *//***//*
        class ViewHolder {
            TextView tvFavName;
            ImageView imgFavAdd;
            CheckBox checkAdd;

            ViewHolder(View view) {
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFavAdd = ((ImageView) view.findViewById(R.id.imgFavAdd));
                checkAdd = (CheckBox) view.findViewById(R.id.checkAdd);
                view.setTag(this);
            }
        }
    }*/

}


