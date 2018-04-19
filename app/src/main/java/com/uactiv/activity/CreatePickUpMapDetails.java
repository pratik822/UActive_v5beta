package com.uactiv.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uactiv.R;
import com.uactiv.adapter.PickupMapDetailAdapter;
import com.uactiv.controller.NotifiyArrayListChange;
import com.uactiv.location.GPSTracker;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.PickUpCategory;
import com.uactiv.model.PickUpModel;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.CustomTimePickerDialog;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class CreatePickUpMapDetails extends FragmentActivity implements OnClickListener, NotifiyArrayListChange {

    public static NotifiyArrayListChange notifiyArrayListChange = null;
    public static int single_item_position;
    public static boolean isFlagSingle = false;
    final int TIME_DIALOG_ID = 0;
    private LinearLayout maptoggle, horizontaldatelayout;
    private GoogleMap googleMap;
    @SuppressLint("WrongViewCast")
    private CustomTextView Btn_map, Btn_list;
    private RelativeLayout lay_mapview, lay_list, ly_pickup_events;
    private RecyclerView recyclerView;
    private int selectdate = -1;
    private int selecteduser = 0;
    private ArrayList<String> month = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> searchdate = new ArrayList<>();
    private String activityName;
    private CustomTimePickerDialog customTimePicker;
    private SimpleDateFormat dateFormatsdf = new SimpleDateFormat("dd MMM yyyy");
    private SimpleDateFormat timeFormatsdf = new SimpleDateFormat("h:mm");
    private Date startTime, endTime;
    private LinearLayoutManager manager = null;
    //it's indicate with select start or end time btn
    private boolean flag = true;
    //it's indicate with select time fliter btn
    private boolean timeflag = false;
    private String selectedDate = "";
    @SuppressLint("WrongViewCast")
    private CustomTextView mapInflateTitle, inNoofPeople, inAwayFrom, inTime;
    private CircleImageView tv_avathar;
    private int clickCount = 0;
    private ArrayList<PickUpCategory> pickUpCategories = null;
    private ArrayList<PickUpCategory> mTempArray = null;
    private ArrayList<PickUpCategory> updatedpickUpCategories = new ArrayList<>();
    private int hour, minute;
    private int yr, months, day;
    private PickUpModel pickUpModel = null;
    private int pickUpModelItemposition;
    private PickupMapDetailAdapter pickupMapDetailAdapter;
    private boolean isupickUpCategories;
    @SuppressLint("WrongViewCast")
    private double Latitude, Longitude;
    private Button btnDate, btnTime;
    private CustomButton mSpecText[];
    private String TAG = "CreatePickUpMapDetails";
    @SuppressLint("WrongViewCast")
    private CustomTextView mAll;
    GPSTracker gps;
    String curruntView = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_pickup_map);
        Utility.setScreenTracking(CreatePickUpMapDetails.this, AppConstants.SCREEN_TRACKING_ID_SEE_ALL);

        Intent intent = getIntent();
        notifiyArrayListChange = this;
        if (intent != null) {
            activityName = intent.getStringExtra("Activity");
            pickUpModel = (PickUpModel) getIntent().getSerializableExtra("PICKUP");
            pickUpCategories = pickUpModel.getPickUpCategoryList();
            pickUpModelItemposition = Integer.parseInt(intent.getStringExtra("position"));

        }
        bindActivity();
    }


    /**
     * bind views
     */
    private void bindActivity() {
        mTempArray = removeDuplicate(pickUpCategories);
        mapInflateTitle = (CustomTextView) findViewById(R.id.tv_name);
        inNoofPeople = (CustomTextView) findViewById(R.id.tv_spot);
        inAwayFrom = (CustomTextView) findViewById(R.id.tv_time);
        inTime = (CustomTextView) findViewById(R.id.tv_times);
        tv_avathar = (CircleImageView) findViewById(R.id.img_logo);
        ly_pickup_events = (RelativeLayout) findViewById(R.id.ly_pickup_events);
        btnDate = (Button) findViewById(R.id.btnDate);
        btnTime = (Button) findViewById(R.id.btnTime);
        mAll = (CustomTextView) findViewById(R.id.allbtn);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        mAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selected(-1);
                mAll.setSelected(true);
                selectdate = pickUpCategories.size() + 1;
                doChangeBackground(selectdate);
            }
        });
        mAll.setSelected(true);
        Calendar today = Calendar.getInstance();
        yr = today.get(Calendar.YEAR);
        months = today.get(Calendar.MONTH);
        day = today.get(Calendar.DAY_OF_MONTH);
        loadingMapView();
        horizontaldatelayout = (LinearLayout) findViewById(R.id.datemainlinlay);
        getNext30Days();
        addCalenderWithPresentDate();
        lay_mapview = (RelativeLayout) findViewById(R.id.lay_mapview);
        lay_list = (RelativeLayout) findViewById(R.id.lay_list);
        maptoggle = (LinearLayout) findViewById(R.id.maptoggle);
        Btn_map = (CustomTextView) findViewById(R.id.btn_map);
        Btn_list = (CustomTextView) findViewById(R.id.btn_list);
        //set visibility
        lay_mapview.setVisibility(View.VISIBLE);
        lay_list.setVisibility(View.GONE);
        Btn_list.setOnClickListener(this);
        Btn_map.setOnClickListener(this);
        ly_pickup_events.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        pickUpCategories = orderByChronological(pickUpCategories);

        pickupMapDetailAdapter = new PickupMapDetailAdapter(this, pickUpCategories, activityName, pickUpModelItemposition);
        recyclerView.setAdapter(pickupMapDetailAdapter);
        isupickUpCategories = true;
        recyclerView.setHasFixedSize(true);
        doChangeBackground(pickUpCategories.size() + 1);
        ((ImageView) findViewById(R.id.imageView1)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /**
     * sort chronological order
     *
     * @param pickUpCategories
     * @return
     */
    private ArrayList<PickUpCategory> orderByChronological(ArrayList<PickUpCategory> pickUpCategories) {

        if (pickUpCategories != null && pickUpCategories.size() > 0) {

            Collections.sort(pickUpCategories, new Comparator<PickUpCategory>() {
                @Override
                public int compare(PickUpCategory lhs, PickUpCategory rhs) {
                    try {
                        return lhs.getEventdate().compareTo(rhs.getEventdate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return 0;
                }

                @Override
                public boolean equals(Object object) {
                    return false;
                }
            });
        }


        return pickUpCategories;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gps != null) {
            gps.stopUsingGPS();
        }

    }

    /**
     * Mapview initialization
     */
    private void initializationMapView() {
        if (googleMap == null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * get month strings
     *
     * @param month
     * @return
     */

    public String getMonth(int month) {
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return monthNames[month - 1];
    }


    /**
     * remove duplicates from list
     *
     * @param contactArrayList
     * @return
     */
    private ArrayList<PickUpCategory> removeDuplicate(ArrayList<PickUpCategory> contactArrayList) {


        Set set = new TreeSet(new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {

                if (lhs instanceof PickUpCategory && rhs instanceof PickUpCategory) {

                    if (((PickUpCategory) lhs).getEventdate().equalsIgnoreCase(((PickUpCategory) rhs).getEventdate())) {
                        return 0;
                    }
                }
                return 1;
            }
        });
        set.addAll(contactArrayList);

        System.out.println("\nAfter removing duplicates\n");

        final ArrayList newList = new ArrayList(set);

        System.out.println(set);

        return newList;
    }


    private void addCalenderWithPresentDate() {

        mSpecText = new CustomButton[mTempArray.size()];
        Typeface font = Typeface.createFromAsset(getAssets(),
                "fonts/Brandon_bld.otf");
        for (int i = 0; i < mTempArray.size(); i++) {
            mSpecText[i] = new CustomButton(this, null);
           // String[] dateArray = mTempArray.get(i).getEventdate().split("-");
            Log.d(TAG, mTempArray.get(i).getEventdate() + "");
            try {
                Log.d("mydatesss",new Gson().toJson(mTempArray.get(i).getEventdate()));
                mSpecText[i].setText(mTempArray.get(i).getEventdate());
            }catch (ArrayIndexOutOfBoundsException ex){
                ex.printStackTrace();
            }

            mSpecText[i].setBackgroundResource(R.drawable.createpickupbg);
            mSpecText[i].setTextColor(ContextCompat.getColor(CreatePickUpMapDetails.this, R.color.createpickuptext));
            mSpecText[i].setAllCaps(false);
            mSpecText[i].setId(i);
            /*mSpecText[i].setTextAppearance(this, android.R.style.TextAppearance_Small);*/
            mSpecText[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            mSpecText[i].setTypeface(font);
            horizontaldatelayout.addView(mSpecText[i]);
            final int finalI = i;
            mSpecText[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected(finalI);
                    mAll.setSelected(false);
                    selectdate = mSpecText[finalI].getId();
                    selectedDate = mTempArray.get(selectdate).getEventdate();
                    doChangeBackground(selectdate);
                }
            });
        }
    }


    /**
     * set selected
     *
     * @param select position
     */
    public void selected(int select) {
        for (int i = 0; i < mTempArray.size(); i++) {
            if (select != i) {
                mSpecText[i].setSelected(false);
            } else {
                mSpecText[i].setSelected(true);
            }
        }
    }

    /*loadingMapView loading

    */
    private void loadingMapView() {

        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            Latitude = gps.getLatitude();
            Longitude = gps.getLongitude();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        try {
            initializationMapView();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            //Marker click passed on showing the user details
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    try {
                        int store = Integer.parseInt(marker.getSnippet());
                        Log.e("store", " " + store);
                        selecteduser = store;
                        if (selectdate >= 0) {
                            isupickUpCategories = false;
                            mapMarkerChanges(store, updatedpickUpCategories);
                        } else {
                            isupickUpCategories = true;
                            mapMarkerChanges(store, pickUpCategories);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e", " " + e);
        }

        if (selectdate >= 0) {
            mapMarkerChanges(0, updatedpickUpCategories);
        } else {
            mapMarkerChanges(0, pickUpCategories);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == Btn_map) {
            maptoggle.setBackgroundResource(R.drawable.top_location_active);
            lay_mapview.setVisibility(View.VISIBLE);
            lay_list.setVisibility(View.GONE);
            curruntView = "map";
            if (!AppConstants.isGestLogin(CreatePickUpMapDetails.this)) {
                Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on Map view", "Map view button on See all pick Ups on map view");
            } else {
                Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on Map view", "Map view button on Guest login See all pick Ups on map view");

            }

        } else if (v == Btn_list) {
            maptoggle.setBackgroundResource(R.drawable.top_menu_active);
            lay_mapview.setVisibility(View.INVISIBLE);
            lay_list.setVisibility(View.VISIBLE);
            curruntView = "list";
            Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on List view", "List view button on See all pick Ups on Map view");
            Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on list view", activityName);
            Utility.setScreenTracking(CreatePickUpMapDetails.this, "See all pick Ups on list view");

            if (!AppConstants.isGestLogin(CreatePickUpMapDetails.this)) {
                Utility.setScreenTracking(CreatePickUpMapDetails.this, "See all pick Ups on list view");
            } else {
                Utility.setScreenTracking(CreatePickUpMapDetails.this, "Guest login See all pick Ups on list view");
            }

        } else if (v == ly_pickup_events) {

            isFlagSingle = true;
            Intent intent = new Intent(CreatePickUpMapDetails.this, PickUpEventPage.class);
            Bundle bundle = new Bundle();
            if (selectdate >= 0) {
                bundle.putSerializable("PICKUP", updatedpickUpCategories.get(selecteduser));

            } else {
                bundle.putSerializable("PICKUP", pickUpCategories.get(selecteduser));
            }
            intent.putExtra("CategoryItemPosition", "" + selecteduser);
            intent.putExtra("position", "" + pickUpModelItemposition);
            bundle.putSerializable("Activity", activityName);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else if (v == btnTime) {
            flag = true;
            showDialog(TIME_DIALOG_ID);
        } else if (v == btnDate) {
            flag = false;
            showDialog(TIME_DIALOG_ID);
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(CreatePickUpMapDetails.this, mTimeSetListener, hour, minute, false);

        }
        return null;
    }


    /**
     * change background according to position
     *
     * @param position
     */
    void doChangeBackground(int position) {
        try {
            if (position == pickUpCategories.size() + 1) {
                updatedpickUpCategories = new ArrayList<>();
                updatedpickUpCategories.addAll(pickUpCategories);
                updatedpickUpCategories = orderByChronological(updatedpickUpCategories);
                pickupMapDetailAdapter = new PickupMapDetailAdapter(this, updatedpickUpCategories, activityName, pickUpModelItemposition);
                recyclerView.setAdapter(pickupMapDetailAdapter);
                recyclerView.setHasFixedSize(true);
                isupickUpCategories = false;
                mapMarkerChanges(0, updatedpickUpCategories);
            } else if (selectdate >= 0) {

                updatedpickUpCategories = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yy-mm-dd");
                //selected date
                Date date1 = sdf.parse(searchdate.get(position));
                //compare date list of date
                Date date2;
                for (int j = 0; j < pickUpCategories.size(); j++) {
                    date2 = sdf.parse(pickUpCategories.get(j).getFull_date());
                    if (!AppConstants.isGestLogin(CreatePickUpMapDetails.this)) {
                        Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on Map view", "Different dates button on See all pick Ups on Map view");

                    } else {
                        Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on Map view", "Different dates button on Guest login See all pick Ups on Map view");

                    }
                    if (date1.equals(date2)) {

                        if (timeflag) {
                            if (startTime != null) {
                                if (endTime != null) {
                                    if (startTime.compareTo(doDateConversation(pickUpCategories.get(j).getEventdate(), pickUpCategories.get(j).getStarttime())) == -1 ||
                                            startTime.compareTo(doDateConversation(pickUpCategories.get(j).getEventdate(), pickUpCategories.get(j).getStarttime())) == 0) {
                                        if (endTime.compareTo(doDateConversation(pickUpCategories.get(j).getEventdate(), pickUpCategories.get(j).getStarttime())) == 1 ||
                                                endTime.compareTo(doDateConversation(pickUpCategories.get(j).getEventdate(), pickUpCategories.get(j).getStarttime())) == 0) {
                                            updatedpickUpCategories.add(pickUpCategories.get(j));
                                        }
                                    }
                                } else {
                                    if (startTime.compareTo(doDateConversation(pickUpCategories.get(j).getEventdate(), pickUpCategories.get(j).getStarttime())) == -1 ||
                                            startTime.compareTo(doDateConversation(pickUpCategories.get(j).getEventdate(), pickUpCategories.get(j).getStarttime())) == 0) {
                                        updatedpickUpCategories.add(pickUpCategories.get(j));
                                    }

                                }
                            }

                        } else {
                            updatedpickUpCategories.add(pickUpCategories.get(j));
                        }
                    }
                }


                updatedpickUpCategories = orderByChronological(updatedpickUpCategories);
                pickupMapDetailAdapter = new PickupMapDetailAdapter(this, updatedpickUpCategories, activityName, pickUpModelItemposition);
                recyclerView.setAdapter(pickupMapDetailAdapter);
                recyclerView.setHasFixedSize(true);
                isupickUpCategories = false;
                mapMarkerChanges(0, updatedpickUpCategories);

            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    /**
     * do date conversation
     *
     * @param date
     * @param time
     * @return
     */
    private Date doDateConversation(String date, String time) {
        try {
            String dat = date + " " + time;
            SimpleDateFormat givenFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return givenFormat.parse(dat);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * click marker position
     * User Array list
     *
     * @param position
     * @param pickUpCategories
     */
    void mapMarkerChanges(int position, ArrayList<PickUpCategory> pickUpCategories) {
        googleMap.clear();
        int size = pickUpCategories.size();
        if (pickUpCategories.size() > 0) {

            for (int j = 0; j < size; j++) {
                if (j == position) {

                    Marker melbourne = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(pickUpCategories.get(j).getLatitude(), pickUpCategories.get(j).getLongitude()))
                            .snippet("" + j)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.yellow_mappin)));
                    String date = pickUpCategories.get(j).getEventdate();
                    //String converteddate = Utility.dateFormats(date);
                    String awayfrom = pickUpCategories.get(j).getAwayfrom();
                    String time = pickUpCategories.get(j).getStarttime();
                    int away = (int) Double.parseDouble(awayfrom);
                    if (Utility.isNullCheck(time)) {
                        inAwayFrom.setText(away + " km away");
                    }
                    mapInflateTitle.setText(activityName);

                    if (!AppConstants.isGestLogin(CreatePickUpMapDetails.this)) {
                        Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on Map view", activityName);
                    } else {
                        Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on Guest login Map view", activityName);
                    }

                    if (pickUpCategories.get(j).getNoofpeople().equals("0")) {

                        inNoofPeople.setText("Unlimited spots");
                    } else {
                        if (pickUpCategories.get(j).getNoofpeople().equals("1")) {
                            inNoofPeople.setText(pickUpCategories.get(j).getNoofpeople() + " spot");
                        } else {
                            inNoofPeople.setText(pickUpCategories.get(j).getNoofpeople() + " spots");
                        }
                    }
                    inTime.setText(date + " @ " + time.substring(0, time.length() - 3));

                    Picasso.with(CreatePickUpMapDetails.this)
                            .load(pickUpCategories.get(j).getImage())
                            .skipMemoryCache()
                            .placeholder(R.drawable.ic_profile).centerCrop().fit()
                            .into(tv_avathar);

                    single_item_position = position;

                } else {
                    Marker melbourne = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(pickUpCategories.get(j).getLatitude(), pickUpCategories.get(j).getLongitude()))
                            .snippet("" + j)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_mappin)));
                }
            }
            ly_pickup_events.setVisibility(View.VISIBLE);
            CameraPosition cameraPosition1 = new CameraPosition.Builder()
                    .target(new LatLng(pickUpCategories.get(position).getLatitude(), pickUpCategories.get(position).getLongitude())) // Sets the center of the map to location user
                    .zoom(11)                   // Sets the zoom
                    .bearing(90)// Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
        } else {
            ly_pickup_events.setVisibility(View.INVISIBLE);
            Toast.makeText(CreatePickUpMapDetails.this, "No Event", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * get current date upto 30 days. recording horizontal scroll list
     */
    public void getNext30Days() {

        month = new ArrayList<>();
        date = new ArrayList<>();
        searchdate = new ArrayList<>();
        for (int i = 0; i < mTempArray.size(); i++) {
            searchdate.add(mTempArray.get(i).getFull_date());
        }

    }


    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        finish();
        isFlagSingle = false;
        if (curruntView.equalsIgnoreCase("map")) {

            if (!AppConstants.isGestLogin(CreatePickUpMapDetails.this)) {
                Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on Map view", "Back Arrow on See all pick Ups on Map view");
            } else {
                Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on Map view", "Back Arrow on See all pick Ups on Guest login Map view");
            }
        } else {
            if (!AppConstants.isGestLogin(CreatePickUpMapDetails.this)) {
                Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on list view", "Back Arrow on See all pick Ups on list view");
            }else{
                Utility.setEventTracking(CreatePickUpMapDetails.this, "See all pick Ups on list view", "Back Arrow on See all pick Ups on Guset login list view");
            }


        }

        if (gps != null) {
            gps.stopUsingGPS();
        }

        super.onBackPressed();  // optional depending on your needs
    }


    @Override
    public void getBuddyModelAddedItems(int position, BuddyModel model) {

    }

    @Override
    public void getPickUpModelAddedItems(int pickUpItemPosition, int categoryItemPosition,
                                         PickUpCategory model, String activityName) {
        if (model != null) {
            if (isupickUpCategories && pickUpCategories != null && pickUpCategories.size() > 0) {
                pickUpCategories.set(categoryItemPosition, model);
                pickUpCategories = orderByChronological(pickUpCategories);
            } else if (updatedpickUpCategories.size() > 0) {
                updatedpickUpCategories.set(categoryItemPosition, model);
                updatedpickUpCategories = orderByChronological(updatedpickUpCategories);
            }
            if (pickupMapDetailAdapter != null) {
                pickupMapDetailAdapter.notifyDataSetChanged();
            }
        }
    }


    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
            hour = hourOfDay;
            minute = minuteOfHour;
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
            Date date = new Date(0, 0, 0, hour, minute);
            Date date1 = new Date(0, 0, 0, hour, minute);
            if (selectdate >= 0) {
                try {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cal.setTime(sdf.parse(selectedDate));
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = dateFormat.format(cal.getTime());
                    date1 = dateFormat.parse(formattedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            String strDate = timeFormat.format(date);
            timeflag = true;
            try {
                if (flag) {
                    endTime = date1;
                    btnTime.setText("" + strDate);
                    doChangeBackground(selectdate);
                } else {
                    startTime = date1;
                    btnDate.setText("" + strDate);
                    doChangeBackground(selectdate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}

