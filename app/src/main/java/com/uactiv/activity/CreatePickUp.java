package com.uactiv.activity;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicommons.people.channel.Channel;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.adapter.ActivitySpinnerAdapter;
import com.uactiv.adapter.PeoplesAdapter;
import com.uactiv.applozicchat.ApplozicChat;
import com.uactiv.applozicchat.IApplozic;
import com.uactiv.controller.CreatePickupNotifier;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.Notification_Fragment;
import com.uactiv.interfaces.OnAlertClickListener;
import com.uactiv.location.GPSTracker;
import com.uactiv.model.ActivityList;
import com.uactiv.model.FavouriteModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.CustomTimePickerDialognew;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomEditText;
import com.uactiv.widgets.CustomTextView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CreatePickUp extends Activity implements View.OnClickListener, OnTimeSetListener, ResponseListener,
        AppConstants.SharedConstants, AppConstants.urlConstants, CreatePickupNotifier,
        View.OnLongClickListener, IApplozic {

    static final int DATE_DIALOG_ID = 100;
    private final int UPDATE_CHAT_GROUP_ID = 5;
    public static CreatePickupNotifier createPickupNotifier = null;
    private final int REP_DELAY = 100;
    public int count = 0;
    private TextView Btn_public, Btn_private, tv_count, tvPublicPrivate;
    private LinearLayout btnbg;
    private RelativeLayout inviteLayout, invite_count_container;
    private CustomButton btn_create = null;
    private ImageView Btn_add, Btn_sub;
    private String startTime = null;
    private String endTime = null;
    private String mFinal_startTime = null;
    private String m_Final_endTime = null;
    private RecyclerView productRecycle = null;
    private LinearLayoutManager manager = null;
    private PeoplesAdapter mAdapter;
    private CustomTimePickerDialognew customTimePicker;
    private int timeCount = 0;
    private RelativeLayout tobar;
    private String mode = "public";
    private ScrollView root_scroll;
    private ArrayList<ActivityList> activitylist = new ArrayList<>();
    private ArrayList<String> earlyItems = new ArrayList<>();
    private ArrayList<String> genderItems = new ArrayList<>();
    private CustomEditText loaction_address = null;
    private CustomEditText editInformation = null;
    private GPSTracker gps = null;
    private GifImageView progressWheel = null;
    private CustomTextView invite_count;
    private SimpleDateFormat dateFormatsdf = new SimpleDateFormat("EEE, dd MMM yyyy");
    private SimpleDateFormat timeFormatsdf = new SimpleDateFormat("h:mma");
    private SimpleDateFormat web_foramt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private long startTimeMilliSec = 0;
    private int invite_count_num = 0;
    private double Lat, lng;
    private ArrayList<String> selectedInviteFav = new ArrayList<>();
    private Intent intent = null;
    private boolean isEdit = false;
    private Bundle bundle = null;
    private ArrayList<FavouriteModel> already_invitedAndAttednig = new ArrayList<FavouriteModel>();
    private ArrayList<FavouriteModel> already_invited = new ArrayList<FavouriteModel>();
    private ArrayList<FavouriteModel> final_SelectedFavID = new ArrayList<>();
    private ArrayList<String> seleted_GroupId = new ArrayList<>();
    private String idbusiness = null;
    private int isbusinesslocation = 1;
    private ArrayList<FavouriteModel> temFavouriteModels = new ArrayList<>();
    private String isBooked = "0";
    private Channel channel;
    private String groupId;
    private String TAG = getClass().getSimpleName();
    private Spinner spinner1;
    private Button btnDate, btnTime;
    private int mYear, mMonth, mDay, mMinute, mHour;
    private int attending_count = 0;
    private ApplozicChat applozicChat;
    private Handler repeatUpdateHandler = new Handler();
    private int mAutoIncrementOrDecrement = 0;
    private boolean isScheduleExists = true;
    private ActivityList list;
    private Map<String, String> param;
    private Boolean fromPickupPage;
    JSONArray invitedPeople;
    public static String iscreateread = "";
    List<Address> addresses;
    Geocoder geocoder;
    String myarea = "";
    String actId = "";
    ArrayList<String> spinId;
    ActivityList activityList;
    List<ActivityList> myActivityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_pickup);
        applozicChat = new ApplozicChat(this);
        createPickupNotifier = this;
        intent = getIntent();

        bindActivity();

        geocoder = new Geocoder(this, Locale.getDefault());

        fromPickupPage = intent.getBooleanExtra("fromPickupPage", false);

        if (fromPickupPage) {
            bundle = intent.getExtras();
            Utility.setScreenTracking(CreatePickUp.this, AppConstants.SCREEN_TRACKING_ID_CREATE_PICKUP);
            ((TextView) findViewById(R.id.title_create_pickup)).setText("Create Pick Up");

            btn_create.setText("Done");
            activitylist.clear();

            activitylist.add(new ActivityList("Select activity", false));
            boolean tempIsBooked = false;
            if (bundle.getString("isBookingOpen").equalsIgnoreCase("1")) {
                tempIsBooked = true;
            }
            isBooked = bundle.getString("isBookingOpen");
            Log.d("isBookingOpen", isBooked);
            activitylist.add(new ActivityList(bundle.getString("activity"), tempIsBooked));
            ActivitySpinnerAdapter adapter = new ActivitySpinnerAdapter(this, R.layout.spinner_title, activitylist, 0);
            spinner1.setAdapter(adapter);
            spinner1.setSelection(1);
            spinner1.setEnabled(false);

            this.groupId = bundle.getString("group_id");
            this.Lat = bundle.getDouble("latitude");
            this.lng = bundle.getDouble("longitude");
            isbusinesslocation = 1;
            idbusiness = bundle.getString("idbusiness");

            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bundle.getString("date") + " " + bundle.getString("start_time")));
                mYear = cal.get(Calendar.YEAR);
                mMonth = cal.get(Calendar.MONTH);
                mDay = cal.get(Calendar.DAY_OF_MONTH);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dat = dateFormat.parse(bundle.getString("date"));
                String convertedDate = dateFormatsdf.format(dat);
                btnDate.setText("" + convertedDate);

                //Fetching previous recordes (StartTime && EndTime)
                String time = Utility.timeFormatChanage(bundle.getString("start_time")).toLowerCase(Locale.getDefault());
                String end_time = Utility.timeFormatChanage(bundle.getString("end_time").toLowerCase(Locale.getDefault()));


                mFinal_startTime = bundle.getString("date") + " " + bundle.getString("start_time");
                m_Final_endTime = bundle.getString("date") + " " + bundle.getString("end_time");
                btnTime.setText(time + " - " + end_time.toLowerCase());

                startTime = time;
                endTime = end_time;


            } catch (ParseException e) {
                e.printStackTrace();
            }

            loaction_address.setText("" + bundle.getString("location"));

            String countString = bundle.getString("no_of_people");
            editInformation.setText("" + bundle.getString("message"));
            if (countString != null && countString.equalsIgnoreCase("0")) {
                tv_count.setHint("No limit");
                tv_count.setText("");
            } else {
                tv_count.setText(bundle.getString("no_of_people"));
            }
            count = Integer.parseInt(bundle.getString("no_of_people"));

            try {
                invitedPeople = new JSONArray(bundle.getString("invited"));
                if (invitedPeople != null && invitedPeople.length() > 0) {
                    for (int i = 0; i < invitedPeople.length(); i++) {
                        already_invited.add(new FavouriteModel(invitedPeople.optJSONObject(i).optString("idmember"),
                                invitedPeople.optJSONObject(i).optString("firstname") + " " + invitedPeople.optJSONObject(i).optString("lastname"),
                                invitedPeople.optJSONObject(i).optString("image")));

                        already_invitedAndAttednig.add(new FavouriteModel(invitedPeople.optJSONObject(i).optString("idmember"),
                                invitedPeople.optJSONObject(i).optString("firstname") + " " + invitedPeople.optJSONObject(i).optString("lastname"),
                                invitedPeople.optJSONObject(i).optString("image")));

                    }
                    if (already_invited.size() > 0) {
                        invite_count_container.setVisibility(View.VISIBLE);
                        productRecycle.setVisibility(View.VISIBLE);
                        temFavouriteModels = removeFavDuplicate(already_invited);
                        manager = new LinearLayoutManager(CreatePickUp.this, LinearLayoutManager.HORIZONTAL, false);
                        productRecycle.setLayoutManager(manager);
                        mAdapter = new PeoplesAdapter(CreatePickUp.this, temFavouriteModels);
                        productRecycle.setAdapter(mAdapter);
                        invite_count.setText("" + (already_invited.size()));
                        final_SelectedFavID = temFavouriteModels;
                        for (int i = 0; i <= final_SelectedFavID.size(); i++) {
                            selectedInviteFav.add(final_SelectedFavID.get(i).getId());
                        }

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (fromPickupPage) {


            } else {
                setUpViewMode();
            }

            onTextCountChangeListener();
        } else {
            setUpViewMode();
        }
    }

    /**
     * set up view mode
     */
    private void setUpViewMode() {
        isEdit = intent.getBooleanExtra("isEdit", false);
        if (isEdit) {
            bundle = intent.getExtras();
            updateEditModeUi();
        }
        if (isEdit) {
            Utility.setScreenTracking(CreatePickUp.this, AppConstants.SCREEN_TRACKING_ID_EDITPICKUP);
            ((TextView) findViewById(R.id.title_create_pickup)).setText("Edit Pick Up");
            btn_create.setText("Done");
        } else {
            Utility.setScreenTracking(CreatePickUp.this, AppConstants.SCREEN_TRACKING_ID_CREATE_PICKUP);
            ((TextView) findViewById(R.id.title_create_pickup)).setText("Create Pick Up");

            btn_create.setText("Done");
        }
    }


    /**
     * change app notificaiton count
     */
    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(CreatePickUp.this).sendBroadcast(registrationComplete);
    }


    /**
     * show edit mode ui to edit pick up
     */
    private void updateEditModeUi() {

        if (bundle != null) {
            activitylist.clear();

            activitylist.add(new ActivityList("Select activity", false));
            boolean tempIsBooked = false;
            if (bundle.getString("isBookingOpen").equalsIgnoreCase("1")) {
                tempIsBooked = true;
            }
            isBooked = bundle.getString("isBookingOpen");
            Log.d("isBookingOpen", isBooked);
            activitylist.add(new ActivityList(bundle.getString("activity"), tempIsBooked));
            ActivitySpinnerAdapter adapter = new ActivitySpinnerAdapter(this, R.layout.spinner_title, activitylist, 0);
            spinner1.setAdapter(adapter);

            spinner1.setSelection(1);
            spinner1.setEnabled(false);
          /*  Btn_add.setEnabled(false);
            Btn_sub.setEnabled(false);*/
            String countString = bundle.getString("no_of_people");
            if (countString != null && countString.equalsIgnoreCase("0")) {
                tv_count.setHint("No limit");
                tv_count.setText("");
            } else {
                tv_count.setText(bundle.getString("no_of_people"));
            }
            count = Integer.parseInt(bundle.getString("no_of_people"));
            editInformation.setText("" + StringEscapeUtils.unescapeJava(bundle.getString("message")));
            String bundleStringmode = bundle.getString("mode");
            Btn_private.setEnabled(false);
            Btn_public.setEnabled(false);

            if (bundleStringmode.equals("public")) {
                btnbg.setBackgroundResource(R.drawable.public_active_btn);
                tvPublicPrivate.setText("(Anybody will see this pickup)");
                mode = "public";
            } else if (bundleStringmode.equals("private")) {
                btnbg.setBackgroundResource(R.drawable.private_active_btn);
                tvPublicPrivate.setText("(Only people invited will see this pickup)");
                mode = "private";
            }
            this.groupId = bundle.getString("group_id");
            this.Lat = bundle.getDouble("latitude");
            this.lng = bundle.getDouble("longitude");
            this.isbusinesslocation = bundle.getInt("isbusinesslocation");
            this.idbusiness = bundle.getString("idbusiness");

            try {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bundle.getString("date") + " " + bundle.getString("start_time")));
                mYear = cal.get(Calendar.YEAR);
                mMonth = cal.get(Calendar.MONTH);
                mDay = cal.get(Calendar.DAY_OF_MONTH);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dat = dateFormat.parse(bundle.getString("date"));
                String convertedDate = dateFormatsdf.format(dat);
                btnDate.setText("" + convertedDate);

                //Fetching previous recordes (StartTime && EndTime)
                String time = Utility.timeFormatChanage(bundle.getString("start_time")).toLowerCase(Locale.getDefault());
                String end_time = Utility.timeFormatChanage(bundle.getString("end_time").toLowerCase(Locale.getDefault()));


                mFinal_startTime = bundle.getString("date") + " " + bundle.getString("start_time");
                m_Final_endTime = bundle.getString("date") + " " + bundle.getString("end_time");
                btnTime.setText(time + " - " + end_time.toLowerCase());

                startTime = time;
                endTime = end_time;


            } catch (ParseException e) {
                e.printStackTrace();
            }

            loaction_address.setText("" + bundle.getString("location"));
            //Add already invited user
            try {
                JSONArray invitedPeople = new JSONArray(bundle.getString("invited"));
                if (invitedPeople != null && invitedPeople.length() > 0) {
                    for (int
                         i = 0; i < invitedPeople.length(); i++) {
                        already_invited.add(new FavouriteModel(invitedPeople.optJSONObject(i).optString("idmember"),
                                invitedPeople.optJSONObject(i).optString("firstname") + " " + invitedPeople.optJSONObject(i).optString("lastname"),
                                invitedPeople.optJSONObject(i).optString("image")));

                        already_invitedAndAttednig.add(new FavouriteModel(invitedPeople.optJSONObject(i).optString("idmember"),
                                invitedPeople.optJSONObject(i).optString("firstname") + " " + invitedPeople.optJSONObject(i).optString("lastname"),
                                invitedPeople.optJSONObject(i).optString("image")));

                    }
                    setAlreadyInvitedPeople();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //add who are all in attnding list
            try {
                JSONArray invitedPeople = new JSONArray(bundle.getString("attending"));
                attending_count = invitedPeople.length();
                if (invitedPeople != null && invitedPeople.length() > 0) {
                    for (int i = 0; i < invitedPeople.length(); i++) {
                        already_invitedAndAttednig.add(new FavouriteModel(invitedPeople.optJSONObject(i).optString("idmember"),
                                invitedPeople.optJSONObject(i).optString("firstname") + " " + invitedPeople.optJSONObject(i).optString("lastname"),
                                invitedPeople.optJSONObject(i).optString("image")));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gps != null) {
            gps.stopUsingGPS();
        }
    }

    /***/
    private void onTextCountChangeListener() {
        tv_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int cnt, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int cnt) {
                if (tv_count.getText() != null && tv_count.getText().length() > 0) {
                    count = Integer.parseInt(tv_count.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * create Pick Up
     */
    private void createPickUp() {
        progressWheel.setVisibility(View.VISIBLE);
        progressWheel.startAnimation();
        if (fromPickupPage) {
            if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime) &&
                    !btnTime.getText().toString().equals("Set Time") && !TextUtils.isEmpty(mFinal_startTime) &&
                    !TextUtils.isEmpty(m_Final_endTime)) {
                if (!editInformation.getText().toString().isEmpty()) {
                    switch (mode) {
                        case "public":
                            if (isEdit) {
                                try {
                                    JSONArray attendingPeople = new JSONArray(bundle.getString("attending"));
                                    if (count == 0 || count >= attendingPeople.length()) {
                                        updatePickUp();
                                    } else {
                                        Utility.showToastMessage(CreatePickUp.this, "Already " + attendingPeople.length() + " members in Pick Up");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                callCreatePickup();
                            }
                            break;
                        case "private":
                            if (isEdit) {
                                try {
                                    JSONArray attendingPeople = new JSONArray(bundle.getString("attending"));

                                    if (count == 0 || count >= attendingPeople.length()) {
                                        updatePickUp();
                                    } else {
                                        Utility.showToastMessage(CreatePickUp.this, "Already " + attendingPeople.length() + " members in Pick Up");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                callCreatePickup();

                            }
                            break;
                    }
                } else {
                    Utility.showToastMessage(CreatePickUp.this, "Please provide description");
                    progressWheel.stopAnimation();
                }


            } else {
                Utility.showToastMessage(CreatePickUp.this, "Please select time");
                progressWheel.stopAnimation();
            }

        } else {
            if (spinner1.getSelectedItemPosition() != 0) {
                if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime) &&
                        !btnTime.getText().toString().equals("Set Time") && !TextUtils.isEmpty(mFinal_startTime) &&
                        !TextUtils.isEmpty(m_Final_endTime)) {
                    if (loaction_address.length() > 0 && Lat != 0.0 && lng != 0.0) {

                        switch (mode) {
                            case "public":
                                if (isEdit) {
                                    try {
                                        JSONArray attendingPeople = new JSONArray(bundle.getString("attending"));
                                        if (count == 0 || count >= attendingPeople.length()) {
                                            updatePickUp();
                                        } else {
                                            Utility.showToastMessage(CreatePickUp.this, "Already " + attendingPeople.length() + " members in Pick Up");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    callCreatePickup();
                                }
                                break;
                            case "private":
                                if (isEdit) {
                                    try {
                                        JSONArray attendingPeople = new JSONArray(bundle.getString("attending"));

                                        if (count == 0 || count >= attendingPeople.length()) {
                                            updatePickUp();
                                        } else {
                                            Utility.showToastMessage(CreatePickUp.this, "Already " + attendingPeople.length() + " members in Pick Up");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    callCreatePickup();

                                }
                                break;
                        }

                    } else {
                        Utility.showToastMessage(CreatePickUp.this, "Please enter address or location");
                    }
                } else {
                    Utility.showToastMessage(CreatePickUp.this, "Please select time");
                }

            } else {
                Utility.showToastMessage(CreatePickUp.this, "Please choose activity");
            }
        }

    }


    /**
     * do api call for create pick up
     */

    private void callCreatePickup() {

        if (Utility.isConnectingToInternet(CreatePickUp.this)) {
            try {
                btn_create.setClickable(false);
                // list = (ActivityList) spinner1.getSelectedItem();
                //  param = new HashMap<>();
                 /*   param.put("iduser", SharedPref.getInstance().getStringVlue(CreatePickUp.this, userId));
                    param.put("activity", list.getActivity().toString().trim());
                    param.put("start_time", mFinal_startTime);
                    param.put("end_time", m_Final_endTime);
                    param.put("location", loaction_address.getText().toString().replaceAll(",null", ""));
                    param.put("latitude", "" + Lat);
                    param.put("longitude", "" + lng);
                    param.put("message", editInformation.getText().toString().trim());
                    param.put("challenge", "0");
                    param.put("memberid", "" + selectedInviteFav.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                    param.put("idgroup", "" + seleted_GroupId.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                    param.put("mode", mode);
                    param.put("no_of_people", "" + count);
                    param.put("idbusiness", idbusiness);
                    param.put("isbusinesslocation", "" + isbusinesslocation);
                    param.put("is_booked", "" + isBooked);
                    param.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));*/
                applozicChat.createGroupWithOutMember("Pick Up", this);
                 /*   progressWheel.setVisibility(View.GONE);
                    progressWheel.stopAnimation();*/
                  /*  progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utility.showInternetError(CreatePickUp.this);
        }


    }


    /**
     * do update pickup by api call
     */
    private void updatePickUp() throws IOException {
        if (Utility.isConnectingToInternet(CreatePickUp.this)) {
            Map<String, String> params = new HashMap<>();
            params.put("idschedule", bundle.getString("idschedule"));
            params.put("date", btnDate.getText().toString());
            params.put("start_time", mFinal_startTime);
            params.put("end_time", m_Final_endTime);
            params.put("location", loaction_address.getText().toString().replaceAll(",null", ""));
            params.put("latitude", "" + Lat);
            params.put("longitude", "" + lng);
            addresses = geocoder.getFromLocation(Lat, lng, 1);
            try {
                myarea = addresses.get(0).getSubLocality();
                params.put("area", myarea);
                params.put("city", addresses.get(0).getLocality());
                try {
                    String address = addresses.get(0).getSubLocality();
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    Log.d("getadresss", address + "----" + city + "----" + knownName);
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }


            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }

            params.put("message", StringEscapeUtils.escapeJava(editInformation.getText().toString().trim()));
            params.put("challenge", "0");
            params.put("no_of_people", "" + count);
            params.put("memberid", "" + selectedInviteFav.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
            params.put("idgroup", "" + seleted_GroupId.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
            params.put("type", bundle.getString("type"));
            params.put("idbusiness", idbusiness);
            params.put("isbusinesslocation", "" + isbusinesslocation);
            params.put("is_booked", "" + isBooked);
            params.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));
            params.put("iduser", SharedPref.getInstance().getStringVlue(CreatePickUp.this, userId));
            btn_create.setEnabled(false);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.startAnimation();
            RequestHandler.getInstance().stringRequestVolley(CreatePickUp.this,
                    AppConstants.getBaseUrl(SharedPref.getInstance()
                            .getBooleanValue(CreatePickUp.this, isStaging)) + editschedule, params, this, 1);
        } else {
            Utility.showInternetError(CreatePickUp.this);
        }

    }

    /**
     * bind views
     */


    public void bindActivity() {
        tobar = (RelativeLayout) findViewById(R.id.tobar);
        myActivityList = new ArrayList<>();
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(CreatePickUp.this, progressWheel);
        loadSpinnerValues();
        btn_create = (CustomButton) findViewById(R.id.btn_create);
        tv_count = (TextView) findViewById(R.id.tv_count);
        Btn_add = (ImageView) findViewById(R.id.Btn_add);
        Btn_sub = (ImageView) findViewById(R.id.Btn_sub);
        Btn_public = (TextView) findViewById(R.id.Btn_public);
        Btn_private = (TextView) findViewById(R.id.Btn_private);
        editInformation = (CustomEditText) findViewById(R.id.editInformation);
        editInformation.setImeOptions(EditorInfo.IME_ACTION_DONE);
        inviteLayout = (RelativeLayout) findViewById(R.id.inviteLayout);
        tvPublicPrivate = (TextView) findViewById(R.id.tvPublicPrivate);
        productRecycle = (RecyclerView) findViewById(R.id.category_recycle);
        btnbg = (LinearLayout) findViewById(R.id.btn_bg);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinId = new ArrayList<>();
        spinner1.setOnItemSelectedListener(onActivitySelectedListener);
        btnDate = (Button) findViewById(R.id.btnDate);
        btnTime = (Button) findViewById(R.id.btnTime);
        root_scroll = (ScrollView) findViewById(R.id.root_scroll);
        loaction_address = (CustomEditText) findViewById(R.id.locationaddress);
        loaction_address.setHint("Address");
        loaction_address.setOnClickListener(this);
        invite_count = (CustomTextView) findViewById(R.id.invite_count);
        invite_count_container = (RelativeLayout) findViewById(R.id.invite_count_container);
        invite_count_container.setVisibility(View.GONE);
        productRecycle.setVisibility(View.GONE);
        ImageView imageView = (ImageView) findViewById(R.id.back);
        imageView.setOnClickListener(this);
        activitylist.add(new ActivityList("Select activity", false));
        myActivityList.add(new ActivityList("Select activity", false));
        String jsonArray = SharedPref.getInstance().getStringVlue(this, Api_skill_list); //All activities
        try {

            JSONArray activity = new JSONArray(jsonArray);

            if (activity != null && activity.length() > 0) {
                for (int i = 0; i < activity.length(); i++) {
                    JSONObject obj = activity.optJSONObject(i);

                    // && !obj.optString("type").equals("buddyup")
                    if (Utility.isNullCheck(obj.optString("activity"))) {

                        if (obj.optInt("is_open") == 1) {
                            activityList = new ActivityList();
                            activityList.setActivity(obj.optString("activity"));
                            activityList.setId(obj.optString("id"));
                            activityList.setIsBookingOpen(true);
                            activitylist.add(activityList);
                        } else {
                            ActivityList activityList = new ActivityList();
                            activityList.setActivity(obj.optString("activity"));
                            activityList.setId(obj.optString("id"));
                            activityList.setIsBookingOpen(true);
                            myActivityList.add(activityList);
                            activitylist.add(activityList);
                            Log.d("mylist", new Gson().toJson(myActivityList));
                            // activitylist.add(new ActivityList(obj.optString("activity"), false));

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivitySpinnerAdapter adapter = new ActivitySpinnerAdapter(this, R.layout.spinner_title, myActivityList, 0);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("myitem", myActivityList.get(i).getActivity() + "-" + myActivityList.get(i).getId());
                actId = myActivityList.get(i).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //onclick
        inviteLayout.setOnClickListener(this);
        Btn_public.setOnClickListener(this);
        Btn_private.setOnClickListener(this);
        Btn_add.setOnClickListener(this);
        Btn_sub.setOnClickListener(this);
        Btn_add.setOnLongClickListener(this);
        Btn_sub.setOnLongClickListener(this);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        btn_create.setOnClickListener(this);


        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);
        cal.set(mYear, mMonth, mDay);
        Date date = cal.getTime();
        btnDate.setText(dateFormatsdf.format(date));
        btnTime.setText("Set Time");
        //set limit text
        if (count == 0) {
            tv_count.setHint("No limit");
            tv_count.setText("");
        }
        Btn_add.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoIncrementOrDecrement > 0) {
                    mAutoIncrementOrDecrement = 0;
                }
                return false;
            }
        });

        Btn_sub.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoIncrementOrDecrement > 0) {
                    mAutoIncrementOrDecrement = 0;
                }
                return false;
            }
        });
        root_scroll.smoothScrollTo(0, 0);
    }

    /***/
    private void displaySpots() {
        if (count == 0) {
            count = attending_count;
        }
        if (count < 9999 && count >= 0) {
            count++;
        }
        if (count == 0) {
            tv_count.setHint("No limit");
            tv_count.setText("");
        } else {
            tv_count.setText(String.valueOf(count));
        }
    }

    /***/
    private void subtractSpots() {

        if (count > 0 && count <= 9999) {
            count--;
            if (count == attending_count) {
                count = 0;
            }
            if (count == 0) {
                tv_count.setHint("No limit");
                tv_count.setText("");
            } else {
                tv_count.setText(String.valueOf(count));
            }

        }

    }

    @Override
    public void onClick(View v) {
        if (Btn_public == v) {
            Utility.setEventTracking(CreatePickUp.this, "", AppConstants.EVENT_TRACKING_ID_PUBLIC);
            btnbg.setBackgroundResource(R.drawable.public_active_btn);
            tvPublicPrivate.setText("(Anybody will see this pickup)");
            mode = "public";
            Utility.setEventTracking(CreatePickUp.this, "Create Pick Up page", "Public button on Pick Up Request Page");
        } else if (v == Btn_private) {
            Utility.setEventTracking(CreatePickUp.this, "", AppConstants.EVENT_TRACKING_ID_PRIVATE);
            btnbg.setBackgroundResource(R.drawable.private_active_btn);
            tvPublicPrivate.setText("(Only people invited will see this pickup)");
            Utility.setEventTracking(CreatePickUp.this, "Create Pick Up page", "Private buttun on Pick Up Request Page");
            mode = "private";

        } else if (v == Btn_add) {
            displaySpots();
        } else if (v == Btn_sub) {
            subtractSpots();
        } else if (v == btnDate) {
            Calendar cal = Calendar.getInstance();
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DAY_OF_MONTH);
            mHour = cal.get(Calendar.HOUR_OF_DAY);
            mMinute = cal.get(Calendar.MINUTE);
            showDialog(DATE_DIALOG_ID);
        } else if (v == btnTime) {

            TimePickerDialog dialog = new TimePickerDialog(CreatePickUp.this, new OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                    if (timeCount == 1) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(mYear, mMonth, mDay, mHour, mMinute);

                        Date selected_time = calendar.getTime();

                        long selected_time_inmillisec = calendar.getTimeInMillis();
                        try {
                            if (Utility.requestTimelogic(CreatePickUp.this, selected_time_inmillisec, dateFormatsdf.parse(btnDate.getText().toString()))) {

                                startTimeMilliSec = selected_time_inmillisec;
                                mFinal_startTime = web_foramt.format(selected_time).toLowerCase();
                                startTime = timeFormatsdf.format(selected_time).toLowerCase();
                                //To increase one hour of the End Time of a Buddy/Pickup.
                                mHour = mHour + 1;
                                TimePickerDialog dialogs = new TimePickerDialog(CreatePickUp.this, new OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        mHour = i;
                                        mMinute = i1;
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.set(mYear, mMonth, mDay, mHour, mMinute);
                                        Date date = calendar.getTime();

                                        if (Utility.isvalidEndTime(new Date(startTimeMilliSec), date)) {

                                            m_Final_endTime = web_foramt.format(date).toLowerCase();

                                            endTime = timeFormatsdf.format(date).toLowerCase();

                                            btnTime.setText(startTime + " - " + endTime);

                                        } else {
                                            btnTime.setText("Set Time");
                                            Utility.showToastMessage(CreatePickUp.this, "Please choose valid time!");
                                        }

                                    }
                                }, mHour, minute, false);
                                dialogs.show();
                                //
                               /* customTimePicker = new CustomTimePickerDialognew(CreatePickUp.this, this, mHour, mMinute, false);
                                customTimePicker.setTitle("Set End Time");
                                customTimePicker.show();*/
                                timeCount = 0;
                            } else {
                                btnTime.setText("Set Time");
                                Utility.showToastMessage(CreatePickUp.this, getString(R.string.msg_buddy_up_start_time_error));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mHour = hourOfDay;
                        mMinute = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(mYear, mMonth, mDay, mHour, mMinute);
                        Date date = calendar.getTime();

                        if (Utility.isvalidEndTime(new Date(startTimeMilliSec), date)) {

                            m_Final_endTime = web_foramt.format(date).toLowerCase();

                            endTime = timeFormatsdf.format(date).toLowerCase();

                            btnTime.setText(startTime + " - " + endTime);

                        } else {
                            btnTime.setText("Set Time");
                            Utility.showToastMessage(CreatePickUp.this, "Please choose valid time!");
                        }

                    }
                }
            }, mHour + 1, mMinute, false);

          /*  customTimePicker = new CustomTimePickerDialognew(CreatePickUp.this, this, mHour + 1, mMinute, false);
            customTimePicker.setTitle("Set Start Time");
            customTimePicker.show();*/
            dialog.show();
            timeCount = 1;
        } else if (v == inviteLayout) {
            Utility.setEventTracking(CreatePickUp.this, "", AppConstants.EVENT_TRACKING_ID_PICKUP_INVITE);
            Utility.setScreenTracking(CreatePickUp.this, "Invite People on create Pick Up page");
            ActivityList activityList = ((ActivityList) spinner1.getSelectedItem());
            if (!(activityList.getActivity().equalsIgnoreCase("Select activity") || activityList.getActivity().toString().trim().equalsIgnoreCase(""))) {
                Intent intent = new Intent(CreatePickUp.this, InvitePeopleFav.class);
                intent.putExtra(activity, activityList.getActivity().toString().trim());

                if (bundle != null && bundle.getString("idschedule") != null) {
                    intent.putExtra("idschedule", bundle.getString("idschedule"));
                }
                intent.putExtra(invitecount, count);
                intent.putExtra(modekey, mode);
                if (fromPickupPage) {
                    intent.putExtra("frompickup", "frompickup");
                    intent.putExtra("isEdit", true);
                } else {
                    intent.putExtra("isEdit", isEdit);
                }

                Bundle bundle = new Bundle();
                bundle.putSerializable("invited_peoples", already_invitedAndAttednig);
                bundle.putSerializable("SelectedFavID", selectedInviteFav);
                bundle.putSerializable("SelectedFavDetails", final_SelectedFavID);
                bundle.putSerializable("seleted_GroupId", seleted_GroupId);
                intent.putExtras(bundle);
                startActivityForResult(intent, requestCodes);
                Utility.setEventTracking(CreatePickUp.this, "Create Pick Up page", "Invite friends on Pick Up Request Page");
            } else {
                Utility.showToastMessage(this, "Please select an activity and Invite");
            }
        }

        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                if (gps != null) {
                    gps.stopUsingGPS();
                }

                break;
            case R.id.btn_create:
                if(!editInformation.getText().toString().trim().isEmpty()){
                    createPickUp();
                }else{
                    Utility.showToastMessage(this, "Please enter description");
                }

                Utility.setScreenTracking(CreatePickUp.this, "Done button on pickup created page.");

                break;
            case R.id.locationaddress:
                if (spinner1.getSelectedItemPosition() != 0) {
                    showSelectLocationDialog();
                } else {
                    Utility.showToastMessage(this, "Please select an activity");
                }
                break;
        }
    }


    /***
     * show select location dialog
     */
    private void showSelectLocationDialog() {
        final ActivityList activityList = (ActivityList) spinner1.getSelectedItem();
        if (isEdit && isBooked.equalsIgnoreCase("1")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreatePickUp.this);
            builder.setMessage("You have already booked the venue. Are you sure you want to edit?");
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AppConstants.showLocatoinPicker(CreatePickUp.this, true, String.valueOf(spinner1.getSelectedItemPosition()), activityList.isBookingOpen());
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            AppConstants.showLocatoinPicker(CreatePickUp.this, true, String.valueOf(spinner1.getSelectedItemPosition()), activityList.isBookingOpen());
        }
    }


    /**
     * show already invited people in list
     */
    private void setAlreadyInvitedPeople() {
        if (already_invited.size() > 0) {
            invite_count_container.setVisibility(View.VISIBLE);
            productRecycle.setVisibility(View.VISIBLE);
            temFavouriteModels = removeFavDuplicate(already_invited);
            manager = new LinearLayoutManager(CreatePickUp.this, LinearLayoutManager.HORIZONTAL, false);
            productRecycle.setLayoutManager(manager);
            mAdapter = new PeoplesAdapter(CreatePickUp.this, temFavouriteModels);
            productRecycle.setAdapter(mAdapter);
            invite_count.setText("" + (already_invited.size()));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == requestCodes && resultCode == RESULT_OK && data != null) {
            if (data.getStringArrayListExtra(invitedata) != null) {
                manager = new LinearLayoutManager(CreatePickUp.this, LinearLayoutManager.HORIZONTAL, false);
                productRecycle.setLayoutManager(manager);
                invite_count_container.setVisibility(View.VISIBLE);
                productRecycle.setVisibility(View.VISIBLE);

                if (data.getStringArrayListExtra("DetailedArray") != null) {
                    if (data.getStringArrayListExtra("seleted_GroupId") != null && data.getStringArrayListExtra("seleted_GroupId").size() > 0) {
                        seleted_GroupId = data.getStringArrayListExtra("seleted_GroupId");
                    }

                    final_SelectedFavID = removeFavDuplicate((ArrayList<FavouriteModel>) data.getSerializableExtra("DetailedArray"));

                    if (isEdit && temFavouriteModels.size() > 0) {
                        final_SelectedFavID.addAll(temFavouriteModels);
                    }

                    selectedInviteFav = (ArrayList<String>) data.getSerializableExtra(invitedata);

                    final_SelectedFavID = removeFavDuplicate(final_SelectedFavID);
                    mAdapter = new PeoplesAdapter(CreatePickUp.this, final_SelectedFavID);
                    productRecycle.setAdapter(mAdapter);
                    if (final_SelectedFavID.size() <= 0) {
                        invite_count_container.setVisibility(View.GONE);
                        productRecycle.setVisibility(View.GONE);
                    }

                    invite_count_num = final_SelectedFavID.size();
                    invite_count.setText(String.valueOf(final_SelectedFavID.size()));
                }


            }
        }
    }


    /**
     * remove dulicates in arrray
     *
     * @param favmemberlist
     * @return
     */
    private ArrayList<FavouriteModel> removeFavDuplicate
    (ArrayList<FavouriteModel> favmemberlist) {


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

        final ArrayList newList = new ArrayList(set);
        return newList;
    }

    /**
     * show date picker dialog
     *
     * @param id
     * @return
     */
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, myDateListener, mYear, mMonth, mDay);
        }
        return null;
    }


    /**
     * set time values
     *
     * @param view      view
     * @param hourOfDay hour
     * @param minute    mins
     */

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (timeCount == 1) {
            mHour = hourOfDay;
            mMinute = minute;
            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, mMonth, mDay, mHour, mMinute);

            Date selected_time = calendar.getTime();

            long selected_time_inmillisec = calendar.getTimeInMillis();
            try {
                if (Utility.requestTimelogic(CreatePickUp.this, selected_time_inmillisec, dateFormatsdf.parse(btnDate.getText().toString()))) {

                    startTimeMilliSec = selected_time_inmillisec;
                    mFinal_startTime = web_foramt.format(selected_time).toLowerCase();
                    startTime = timeFormatsdf.format(selected_time).toLowerCase();
                    //To increase one hour of the End Time of a Buddy/Pickup.
                    mHour = mHour + 1;
                    TimePickerDialog dialog = new TimePickerDialog(CreatePickUp.this, new OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {

                        }
                    }, mHour, mMinute, false);
                    dialog.show();
                   /* customTimePicker = new CustomTimePickerDialognew(CreatePickUp.this, this, mHour, mMinute, false);
                    customTimePicker.setTitle("Set End Time");
                    customTimePicker.show();*/
                    timeCount = 0;
                } else {
                    btnTime.setText("Set Time");
                    Utility.showToastMessage(CreatePickUp.this, getString(R.string.msg_buddy_up_start_time_error));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            mHour = hourOfDay;
            mMinute = minute;
            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, mMonth, mDay, mHour, mMinute);
            Date date = calendar.getTime();

            if (Utility.isvalidEndTime(new Date(startTimeMilliSec), date)) {

                m_Final_endTime = web_foramt.format(date).toLowerCase();

                endTime = timeFormatsdf.format(date).toLowerCase();

                btnTime.setText(startTime + " - " + endTime);

            } else {
                btnTime.setText("Set Time");
                Utility.showToastMessage(CreatePickUp.this, "Please choose valid time!");
            }

        }
    }


    /**
     * load spinner values for all
     */
    private void loadSpinnerValues() {
        earlyItems.add("Early 30's");
        earlyItems.add("Early 20's");
        earlyItems.add("Under 21");
        genderItems.add("Gender");
        genderItems.add("Male");
        genderItems.add("Female");
    }


    /**
     * create applozic chat group
     *
     * @param idSchedule for event
     */

    private void createChatGroup(final String idSchedule) {
        new AsyncTask<String, String, Channel>() {
            @Override
            protected Channel doInBackground(String... params) {
                ChannelInfo channelInfo = new ChannelInfo();
                //  channelInfo.setClientGroupId(groupId);
                channelInfo.setGroupName("Pick Up");
                return ChannelService.getInstance(CreatePickUp.this).createChannel(channelInfo);
            }

            @Override
            protected void onPostExecute(Channel channel) {
                super.onPostExecute(channel);
                Log.d(TAG, "onPostExecute : " + channel);
                finish();
               /* if (channel != null){
                    updateCharGroupId(String.valueOf(channel.getKey()), idSchedule);
                }*/


            }
        }.execute();
    }


    /**
     * update group id with server
     *
     * @param groupId    group id from applozic
     * @param idschedule id schedule
     */
    private void updateCharGroupId(String groupId, String idschedule) {

        if (Utility.isConnectingToInternet(CreatePickUp.this)) {
            try {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                Map<String, String> param = new HashMap<>();
                param.put("idschedule", "" + idschedule);
                param.put("group_id", "" + groupId);
                RequestHandler.getInstance().stringRequestVolley(CreatePickUp.this, AppConstants
                                .getBaseUrl(SharedPref.getInstance()
                                        .getBooleanValue(CreatePickUp.this, isStaging)) + updateGroupId,
                        param, this, UPDATE_CHAT_GROUP_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(CreatePickUp.this);
        }

    }

    @Override
    public void successResponse(String successResponse, int flag) {

        /** flag == 0 create pickup API response
         *  flag == 1 update pickup API response
         *  flag == 2 update group id API response
         */

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(successResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }


        switch (flag) {

            case 0:
                Utility.setEventTracking(CreatePickUp.this, "Create PickUp", AppConstants.EVENT_TRACKING_ID_CREATE_PICK_UP);
                if (intent.getBooleanExtra("fromPickupPage", false)) {
                    if (jsonObject != null) {
                        if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                            SharedPref.getInstance().setSharedValue(CreatePickUp.this, notification_count, jsonObject.optInt("notification_count"));
                            notifyCountChanged();
                            btn_create.setClickable(true);
                            Log.d("mypickupresopnce", new Gson().toJson(jsonObject));
                            //   createChatGroup(jsonObject.optString(KEY_ID_SCHEDULE));
                            if (isScheduleExists == false) {
                                try {
                                    Map<String, String> param = new HashMap<>(3);
                                    param.put("iduser", SharedPref.getInstance().getStringVlue(CreatePickUp.this, userId));
                                    param.put("screen", "recreate_pick_up");
                                    RequestHandler.getInstance().stringRequestVolley(CreatePickUp.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(CreatePickUp.this, isStaging)) + "popup_detail", param, CreatePickUp.this, 2);

                                } catch (NullPointerException ex) {
                                    ex.printStackTrace();
                                }
                            }
                          /*  Map<String, String> param = new HashMap<>(3);
                            param.put("iduser", SharedPref.getInstance().getStringVlue(CreatePickUp.this, userId));
                            RequestHandler.getInstance().stringRequestVolley(CreatePickUp.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(CreatePickUp.this, isStaging)) + "popup_detail", param, CreatePickUp.this, 2);
*/
                            finish();
                            PickUpGuest.back.getBack();
                        } else {
                       /* progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);*/
                            if ((!(TextUtils.isEmpty(jsonObject.optString(KEY_IS_DUPLICATE)))) && jsonObject.optBoolean(KEY_IS_DUPLICATE)) { //We have duplicate entry
                                Utility.showAlertForExistingTimeSlot(CreatePickUp.this, getString(R.string.msg_duplicate_create_pickup), onAlertClickListener);

                            } else {
                                Utility.showToastMessage(CreatePickUp.this, jsonObject.optString(KEY_MSG));
                                try {
                                    Map<String, String> param = new HashMap<>(3);
                                    param.put("iduser", SharedPref.getInstance().getStringVlue(CreatePickUp.this, userId));
                                    param.put("screen", "recreate_pick_up");
                                    RequestHandler.getInstance().stringRequestVolley(CreatePickUp.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(CreatePickUp.this, isStaging)) + "popup_detail", param, CreatePickUp.this, 2);

                                } catch (NullPointerException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }


                    }

                } else {
                    if (jsonObject != null) {
                        if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                            SharedPref.getInstance().setSharedValue(CreatePickUp.this, notification_count, jsonObject.optInt("notification_count"));
                            notifyCountChanged();
                            btn_create.setClickable(true);

                            createChatGroup(jsonObject.optString(KEY_ID_SCHEDULE));


                        } else {
                       /* progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);*/
                            if ((!(TextUtils.isEmpty(jsonObject.optString(KEY_IS_DUPLICATE)))) && jsonObject.optBoolean(KEY_IS_DUPLICATE)) { //We have duplicate entry
                                Utility.showAlertForExistingTimeSlot(CreatePickUp.this, getString(R.string.msg_duplicate_create_pickup), onAlertClickListener);
                            } else {
                                Utility.showToastMessage(CreatePickUp.this, jsonObject.optString(KEY_MSG));
                            }
                        }

                    }
                }

                break;
            case 1:
                if (jsonObject != null) {
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);

                    if (jsonObject.optString("status").trim().contains("Invited")) {
                        Utility.showToastMessage(CreatePickUp.this, getResources().getString(R.string.pickup_invited));
                    } else {
                        try {
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            btn_create.setEnabled(true);
                            if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                                if (!jsonObject.optString("status").equals("null")) {
                                    applozicChat.sendCustomMsgtoGroup(groupId, jsonObject.optString("status"));
                                }
                                Utility.showToastMessage(CreatePickUp.this, getResources().getString(R.string.pickup_edited));
                                Intent i = new Intent();
                                i.putExtra("response", jsonObject.toString());
                                setResult(RESULT_OK, i);
                                onBackPressed();
                            } else {
                                if ((!(TextUtils.isEmpty(jsonObject.optString(KEY_IS_DUPLICATE)))) && jsonObject.optBoolean(KEY_IS_DUPLICATE)) { //We have duplicate entry
                                    Utility.showAlertForExistingTimeSlot(CreatePickUp.this, getString(R.string.msg_duplicate_create_buddy_up), onAlertClickListener);
                                    Notification_Fragment.inter.refresh();
                                } else {
                                    Utility.showToastMessage(CreatePickUp.this, jsonObject.optString(KEY_MSG));
                                }
                            }
                            btn_create.setEnabled(true);
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
                break;


            case 2:
                if (jsonObject != null) {

                    Log.d("session", AppConstants.getSession(CreatePickUp.this) + "");
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        String type = jsonObject.optString("type");
                        if (type.equalsIgnoreCase("pick_up") && CreatePickUp.iscreateread.isEmpty()) {
                            AppConstants.genralPopupEvent(CreatePickUp.this, 1, jsonObject, this, CreatePickUp.this, "pickup");
                            Log.d("show pickup dialog", "");
                        } else if (type.equalsIgnoreCase("promotional") && CreatePickUp.iscreateread.isEmpty()) {

                            if (jsonObject.optString("content").isEmpty()) {
                                AppConstants.genralPopupEvent(CreatePickUp.this, 3, jsonObject, this, CreatePickUp.this, "pickup");
                            } else {
                                AppConstants.genralPopupEvent(CreatePickUp.this, 2, jsonObject, this, CreatePickUp.this, "pickup");
                            }
                            //  SharedPref.getInstance().setSharedValue( BuddyUpRequest.this, "APP_SESSION", 1);


                            Log.d("show pramotional dialog", "");
                        } else if (type.equalsIgnoreCase("rateus") && CreatePickUp.iscreateread.isEmpty()) {
                            AppConstants.genralPopupEvent(CreatePickUp.this, 0, jsonObject, this, CreatePickUp.this, "pickup");

                        }
                        if (!CreatePickUp.iscreateread.isEmpty()) {
                            finish();
                            Notification_Fragment.inter.refresh();
                            PickUpGuest.pickupguest.finish();
                        }

                    } else {
                        finish();
                        Notification_Fragment.inter.refresh();
                        PickUpGuest.pickupguest.finish();
                    }

                }
                break;
            case UPDATE_CHAT_GROUP_ID:
                btn_create.setEnabled(true);
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                if (!intent.getBooleanExtra("fromPickupPage", false)) {
                    finish();
                }

                break;
            default:
                break;
        }

    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    public void mapViewNotifier(LatLng latLng, String Address, String isBooked, String
            idBusiness, int isbusinesslocation) {

        if (latLng != null && Address != null) {
            this.idbusiness = idBusiness;
            this.isbusinesslocation = isbusinesslocation;
            Lat = latLng.latitude;
            lng = latLng.longitude;
            loaction_address.setText(Address);
            this.isBooked = isBooked;


        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.Btn_add) {
            mAutoIncrementOrDecrement = 1;
        } else if (v.getId() == R.id.Btn_sub) {
            mAutoIncrementOrDecrement = 2;
        }
        repeatUpdateHandler.post(new RptUpdater());
        return false;
    }

    @Override
    public void getGroupId(String groupId) {

    }


    @Override
    public void getChannel(Channel mChannel) {

        if (Utility.isConnectingToInternet(CreatePickUp.this)) {
            try {
                ActivityList list = (ActivityList) spinner1.getSelectedItem();
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(CreatePickUp.this, userId));
                param.put("activity", actId);
                param.put("start_time", mFinal_startTime);
                param.put("end_time", m_Final_endTime);
                param.put("location", loaction_address.getText().toString().replaceAll(",null", ""));
                param.put("message", StringEscapeUtils.escapeJava(editInformation.getText().toString().trim()));

                param.put("challenge", "0");
                param.put("memberid", "" + selectedInviteFav.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                param.put("idgroup", "" + seleted_GroupId.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                param.put("mode", mode);
                param.put("no_of_people", "" + count);

                if (fromPickupPage) {

                    if (Lat == 0.0) {
                        param.put("latitude", "" + bundle.getDouble("latitude"));
                    } else {
                        param.put("latitude", "" + Lat);
                    }

                    if (lng == 0.0) {
                        param.put("longitude", "" + bundle.getDouble("longitude"));
                    } else {
                        param.put("longitude", "" + lng);
                    }
                    param.put("isbusinesslocation", "" + 1);
                    param.put("location", loaction_address.getText().toString().replaceAll(",null", ""));
                    param.put("idbusiness", idbusiness);


                } else {
                    param.put("idbusiness", idbusiness);
                    param.put("location", loaction_address.getText().toString().replaceAll(",null", ""));
                    param.put("latitude", "" + Lat);
                    param.put("longitude", "" + lng);
                    param.put("isbusinesslocation", "" + isbusinesslocation);
                }
                addresses = geocoder.getFromLocation(Lat, lng, 1);

                try {
                    myarea = addresses.get(0).getSubLocality();
                    param.put("area", myarea);
                    param.put("city", addresses.get(0).getLocality());
                    try {
                        String address = addresses.get(0).getSubLocality();
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        Log.d("getadresss", address + "----" + city + knownName);
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    } catch (IndexOutOfBoundsException ex) {
                        ex.printStackTrace();
                    }


                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }

                param.put("is_booked", "" + isBooked);
                param.put("group_id", "" + mChannel.getKey());
                param.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));
                RequestHandler.getInstance().stringRequestVolley(CreatePickUp.this,
                        AppConstants.getBaseUrl(SharedPref.getInstance()
                                .getBooleanValue(CreatePickUp.this, isStaging)) + createpickup, param, CreatePickUp.this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utility.showInternetError(CreatePickUp.this);
        }


    }

    @Override
    public void initFailure(String error_msg) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
        Toast.makeText(CreatePickUp.this, "Please try again", Toast.LENGTH_LONG);
    }


    /***/
    class RptUpdater implements Runnable {
        public void run() {
            if (mAutoIncrementOrDecrement == 1) {
                displaySpots();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            } else if (mAutoIncrementOrDecrement == 2) {
                subtractSpots();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            }
        }
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            mYear = year;
            mMonth = month;
            mDay = day;
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            Date date = calendar.getTime();

            if (Utility.isVaildrequestDateSetLogic(date)) {
                btnDate.setText(dateFormatsdf.format(date));
                btnTime.setText("Set Time");
                startTimeMilliSec = 0;
            } else {
                Utility.showToastMessage(CreatePickUp.this, "Please select valid date");
            }

        }
    };
    private AdapterView.OnItemSelectedListener onActivitySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ActivityList data = (ActivityList) parent.getItemAtPosition(position);
            Utility.setEventTracking(CreatePickUp.this, "Create Pick Up page", "Select Activity on Pick Up Request Page done");
            if (!isEdit) {
                if (!fromPickupPage) {
                    loaction_address.setText("");
                } else {
                    isbusinesslocation = 1;
                }

                Lat = 0.0;
                lng = 0.0;
                idbusiness = "";

                isBooked = "0";
                if (idbusiness.equalsIgnoreCase("")) {
                    try {
                        if (bundle.getString("idbusiness") != null) {
                            idbusiness = bundle.getString("idbusiness");
                        }
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                }


            }
            if (data.isBookingOpen() && !isEdit) {
                AppConstants.showUActivBookingDialog(CreatePickUp.this);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    private OnAlertClickListener onAlertClickListener = new OnAlertClickListener() {
        @Override
        public void onClickPositive() {
            isScheduleExists = false;
            createPickUp();

        }

        @Override
        public void onClickNegative() {
            isScheduleExists = true;
            //finish();
        }
    };
}