package com.uactiv.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alexzh.circleimageview.CircleImageView;
import com.applozic.mobicommons.people.channel.Channel;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.adapter.ActivitySpinnerAdapter;
import com.uactiv.applozicchat.ApplozicChat;
import com.uactiv.applozicchat.IApplozic;
import com.uactiv.controller.CreatePickupNotifier;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.Notification_Fragment;
import com.uactiv.fragment.Pastnotifiationfragment;
import com.uactiv.interfaces.OnAlertClickListener;
import com.uactiv.location.GPSTracker;
import com.uactiv.model.ActivityList;
import com.uactiv.model.BuddyModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.CustomTimePickerDialog;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomEditText;
import com.uactiv.widgets.CustomTextView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BuddyUpRequest extends AppCompatActivity implements OnClickListener, OnTimeSetListener, AppConstants.urlConstants,
        AppConstants.SharedConstants, ResponseListener, CreatePickupNotifier, IApplozic {

    private static final int DATE_DIALOG_ID = 100;
    public static CreatePickupNotifier createPickupNotifier = null;
    private ArrayList<ActivityList> activityItems = new ArrayList<>();
    private String startTime = null, endTime = null;
    private CustomTimePickerDialog customTimePicker;
    private int count = 0;
    private RelativeLayout bottomLayout;
    private Intent intent = null;
    private BuddyModel buddyDetails = null;
    private GifImageView progressWheel = null;
    private GPSTracker gpsTracker = null;
    private CustomTextView tvAddress = null;
    private CheckBox isChallenge = null;
    private CustomButton btn_sendrequest = null;
    private CircleImageView buddyupimage = null;
    private CircleImageView challenage_buddyupimage = null;
    private CircleImageView challenage_userimage = null;
    private CustomTextView tvBuddyName = null;
    private SimpleDateFormat dateFormatsdf = new SimpleDateFormat("EEE,dd MMM yyyy");
    private SimpleDateFormat timeFormatsdf = new SimpleDateFormat("h:mma");
    private SimpleDateFormat web_foramt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private long startTimeMilliSec = 0;
    private double Lat = 0.0, lng = 0.0;
    private boolean isEdit;
    private Bundle bundle;
    private String idbusiness = "";
    private int isbusinesslocation = 0;
    private String mFinal_startTime = null;
    private String m_Final_endTime = null;
    private CustomTextView tvChallengeText = null;
    private String TAG = "BuddyUpRequest";
    private String groupId;
    private TextView headerText;
    private CustomEditText editText;
    private Spinner spinner;
    private Button tvDate, tvTime;
    private int mYear, mMonth, mDay, mMinute, mHour;
    private String isBooked = "0";
    private ApplozicChat applozicChat;

    private boolean isScheduleExists = true;

    private String image, buddyimage, activity, location, date, fname, lname;
    Boolean isBookingOpen;
    private ActivitySpinnerAdapter adapter;
    private String idbuddy;
    private String start_time, end_time;
    private String group_id;
    private String message;
    private String ischallenge;
    List<String>actId;
    private String skillId;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buddyup_request);
        applozicChat = new ApplozicChat(this);
        CreatePickUp.createPickupNotifier = null;
        actId=new ArrayList<>();
        Utility.setScreenTracking(BuddyUpRequest.this, "Buddy Up Request page");
        Utility.setScreenTracking(BuddyUpRequest.this, "Buddy Up create page");
        this.createPickupNotifier = this;
        notifyCountChanged();
        intent = getIntent();


        if (intent != null) {
            isEdit = intent.getBooleanExtra("isEdit", false);
            buddyDetails = (BuddyModel) getIntent().getSerializableExtra(serialKeycreateBuddy);
        }

        bindActivity();

        if (isEdit) {
            Utility.setScreenTracking(BuddyUpRequest.this, AppConstants.SCREEN_TRACKING_ID_EDITBUDDYUP);
            headerText.setText("Edit Buddy Up");
            btn_sendrequest.setText("Done");
            if (intent != null) {
                bundle = intent.getExtras();
                updateEditView(bundle);
            }
        } else {
            Utility.setScreenTracking(BuddyUpRequest.this, AppConstants.SCREEN_TRACKING_ID_CREATE_BUDDYUP);
            headerText.setText("Buddy Up Request");

            if (intent != null && intent.getBooleanExtra("fromBuddyupPage", false)) {
                image = intent.getStringExtra("image");
                buddyimage = intent.getStringExtra("buddyimage");
                activity = intent.getStringExtra("activity");
                location = intent.getStringExtra("location");
                date = intent.getStringExtra("date");
                fname = intent.getStringExtra("fname");
                lname = intent.getStringExtra("lname");
                isBookingOpen = intent.getBooleanExtra("isBookingOpen", false);
                idbuddy = intent.getStringExtra("idbuddy");
                start_time = intent.getStringExtra("start_time");
                end_time = intent.getStringExtra("end_time");
                group_id = intent.getStringExtra("group_id");
                message = intent.getStringExtra("buddymessage");
                editText.setText("" + StringEscapeUtils.escapeJava(message));

                Utility.setImageUniversalLoader(BuddyUpRequest.this, image, buddyupimage);
                Utility.setImageUniversalLoader(BuddyUpRequest.this, SharedPref.getInstance().getStringVlue(BuddyUpRequest.this, "Profileimage"), challenage_userimage);
                Utility.setImageUniversalLoader(BuddyUpRequest.this, image, challenage_buddyupimage);
                tvAddress.setText(location);
                Calendar cal = Calendar.getInstance();
                try {
                    mYear = cal.get(Calendar.YEAR);
                    mMonth = cal.get(Calendar.MONTH);
                    mDay = cal.get(Calendar.DAY_OF_MONTH);
                    cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(start_time + " " + end_time));

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    Date dat = dateFormat.parse(date);

                    String convertedDate = dateFormatsdf.format(dat);

                    tvDate.setText(convertedDate);


                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (Utility.isNullCheck(fname)) {
                    tvBuddyName.setText("" + fname + " " + lname);
                    tvChallengeText.setText("Make your buddy up request competitive by challenging " + fname + " " + lname + ". Game on!");
                }
                if (Utility.isNullCheck(activity)) {
                    activityItems.add(new ActivityList(activity, isBookingOpen));
                    adapter = new ActivitySpinnerAdapter(this, R.layout.spinner_title, activityItems, 0);
                    spinner.setAdapter(adapter);
                }


                if (Utility.isNullCheck(intent.getStringExtra("ischallenge"))) {
                    if (intent.getStringExtra("ischallenge").equals("0")) {
                        isChallenge.setChecked(false);
                    } else {
                        isChallenge.setChecked(true);
                    }
                }

            } else {
                updateUi();
            }

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsTracker != null) {
            gpsTracker.stopUsingGPS();
        }


    }

    /**
     * display buddy up details for edit mode
     *
     * @param bundle buddyUp details
     */
    private void updateEditView(Bundle bundle) {

        if (bundle != null) {
            boolean tempIsBooking = false;
            if (bundle.getString("isBookingOpen").equalsIgnoreCase("1")) {
                tempIsBooking = true;
            }
            isBooked = bundle.getString("isBookingOpen");

            if (Utility.isNullCheck(bundle.getString("skill"))) {
                activityItems.add(new ActivityList(bundle.getString("skill"), tempIsBooking));
            }
            adapter = new ActivitySpinnerAdapter(this, R.layout.spinner_title, activityItems, 0);
            spinner.setAdapter(adapter);
            spinner.setSelection(1);

            if (Utility.isNullCheck(bundle.getString("firstname"))) {
                tvBuddyName.setText("" + bundle.getString("firstname"));
                tvChallengeText.setText("Make your buddy up request competitive by challenging " + bundle.getString("firstname") + ". Game on!");
            }
            if (Utility.isNullCheck(bundle.getString("message"))) {
                editText.setText("" + StringEscapeUtils.unescapeJava(bundle.getString("message")));
            }
            if (Utility.isNullCheck(bundle.getString("ischallenge"))) {
                if (bundle.getString("ischallenge").equals("0")) {
                    isChallenge.setChecked(false);
                } else {
                    isChallenge.setChecked(true);
                }
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

                tvDate.setText(convertedDate);

                String time = Utility.timeFormatChanage(bundle.getString("start_time")).toLowerCase();

                String end_time = Utility.timeFormatChanage(bundle.getString("end_time")).toLowerCase();

                tvTime.setText("" + time + "-" + end_time.toLowerCase());
                startTime = time;
                endTime = end_time;
                mFinal_startTime = bundle.getString("date") + " " + bundle.getString("start_time");
                m_Final_endTime = bundle.getString("date") + " " + bundle.getString("end_time");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tvAddress.setText("" + bundle.getString("location"));

            if (Utility.isNullCheck(bundle.getString("image"))) {
                Utility.setImageUniversalLoader(BuddyUpRequest.this, bundle.getString("image"), buddyupimage);
                Utility.setImageUniversalLoader(BuddyUpRequest.this, bundle.getString("image"), challenage_buddyupimage);
                Utility.setImageUniversalLoader(BuddyUpRequest.this, SharedPref.getInstance().getStringVlue(BuddyUpRequest.this, image), challenage_userimage);
            }
        }
        updateUiIfEditMode();
    }


    /**
     * update ui mode if its edit mode
     */
    private void updateUiIfEditMode() {
        spinner.setEnabled(false);
        isChallenge.setEnabled(false);
    }


    /**
     * display buddy up details
     */

    private void updateUi() {

        if (Utility.isNullCheck(buddyDetails.getImage())) {
            Utility.setImageUniversalLoader(BuddyUpRequest.this, buddyDetails.getImage(), buddyupimage);
            Utility.setImageUniversalLoader(BuddyUpRequest.this, buddyDetails.getImage(), challenage_buddyupimage);
            Utility.setImageUniversalLoader(BuddyUpRequest.this, SharedPref.getInstance().getStringVlue(BuddyUpRequest.this, image), challenage_userimage);
        }

        if (Utility.isNullCheck(buddyDetails.getName())) {
            tvBuddyName.setText(buddyDetails.getName());
            tvChallengeText.setText("Make your buddy up request competitive by challenging " + buddyDetails.getName() + ". Game on!");
        }

        if (buddyDetails.getSkillDo() != null) {

            try {
                for (int j = 0; j < buddyDetails.getSkillDo().size(); j++) {
                    actId.add(buddyDetails.getSkillDo().get(j).getIdskill());
                    activityItems.add(new ActivityList(buddyDetails.getSkillDo().get(j).getActivty(), buddyDetails.getSkillDo().get(j).isBookingOpen()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ActivitySpinnerAdapter adapter = new ActivitySpinnerAdapter(this, R.layout.spinner_title, activityItems, 0);
            spinner.setAdapter(adapter);
        }

    }


    /**
     * create buddy up request
     */

    private void buddyUpRequest() {
        if (activityItems.size() > 1) {

            if (spinner.getSelectedItemPosition() != 0) {

                if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime) &&
                        !tvTime.getText().toString().equals("Set Time") &&
                        !TextUtils.isEmpty(mFinal_startTime) &&
                        !TextUtils.isEmpty(m_Final_endTime)) {

                    if (tvAddress.length() > 0 && Lat != 0.0 && lng != 0.0) {
                        if (Utility.isConnectingToInternet(BuddyUpRequest.this)) {
                            btn_sendrequest.setEnabled(false);
                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                            applozicChat.createGroupWithOutMember("Buddy Up", this);

                        } else {
                            Utility.showInternetError(BuddyUpRequest.this);
                        }
                    } else {
                        Utility.showToastMessage(BuddyUpRequest.this, "Please choose location");
                    }
                } else {
                    Utility.showToastMessage(BuddyUpRequest.this, "Please select time");
                }
            } else {
                Utility.showToastMessage(BuddyUpRequest.this, "Please select activity");
            }
        } else {
            Utility.showToastMessage(BuddyUpRequest.this, "You dont have mutual Activity");
        }


    }


    /**
     * update app notification count
     */
    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(BuddyUpRequest.this).sendBroadcast(registrationComplete);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("response", "null");
        setResult(RESULT_CANCELED, i);
        if (gpsTracker != null) {
            gpsTracker.stopUsingGPS();
        }

        Utility.setEventTracking(BuddyUpRequest.this, "Buddy Up create page", "Back arrow on Buddy Up create Page");
        if (gpsTracker != null) {
            gpsTracker.stopUsingGPS();
        }
        finish();

    }


    /**
     * update or edit buddy up request
     */

    private void updateBuddyUpRequest() {

        if (!TextUtils.isEmpty(startTime) && !(TextUtils.isEmpty(endTime)) &&
                !tvTime.getText().toString().equals("Set Time") &&
                !TextUtils.isEmpty(mFinal_startTime) && !TextUtils.isEmpty(m_Final_endTime)) {

            if (tvAddress.length() > 0 && Lat != 0.0 && lng != 0.0) {

                if (Utility.isConnectingToInternet(BuddyUpRequest.this)) {
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("idschedule", bundle.getString("idschedule"));
                    params.put("start_time", mFinal_startTime);
                    params.put("end_time", m_Final_endTime);
                    params.put("location", tvAddress.getText().toString().replaceAll(",null", ""));
                    params.put("latitude", "" + Lat);
                    params.put("longitude", "" + lng);
                    params.put("message", StringEscapeUtils.escapeJava(editText.getText().toString()));
                    params.put("challenge", isChallenge());
                    params.put("memberid", "");
                    params.put("type", bundle.getString("type"));
                    params.put("idbusiness", idbusiness);
                    params.put("isbusinesslocation", "" + isbusinesslocation);
                    params.put("is_booked", "" + isBooked);
                    params.put("iduser", SharedPref.getInstance().getStringVlue(BuddyUpRequest.this, userId));
                    params.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));
                    RequestHandler.getInstance().stringRequestVolley(BuddyUpRequest.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(BuddyUpRequest.this, isStaging)) + editschedule, params, this, 1);
                } else {
                    Utility.showInternetError(BuddyUpRequest.this);
                }
            } else {
                Utility.showToastMessage(BuddyUpRequest.this, "Please choose location");
            }
        } else {
            Utility.showToastMessage(BuddyUpRequest.this, "Please select time");
        }


    }

    private void updateBuddyUpRequestPickupView() {

        if (!TextUtils.isEmpty(start_time) && !(TextUtils.isEmpty(end_time))) {

            if (location.length() > 0) {

                if (Utility.isConnectingToInternet(BuddyUpRequest.this)) {
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                    applozicChat.createGroupWithOutMember("Buddy Up", this);
                } else {
                    Utility.showInternetError(BuddyUpRequest.this);
                }
            } else {
                Utility.showToastMessage(BuddyUpRequest.this, "Please choose location");
            }
        } else {
            Utility.showToastMessage(BuddyUpRequest.this, "Please select time");
        }


    }


    /**
     * get challenge values
     *
     * @return isChallenge accepted or not
     */
    private String isChallenge() {
        String isChallenge = "0";
        if (this.isChallenge.isChecked()) {
            isChallenge = "1";
        }
        return isChallenge;
    }

    private void bindActivity() {
        btn_sendrequest = (CustomButton) findViewById(R.id.bottomBarText);
        tvBuddyName = (CustomTextView) findViewById(R.id.tvBuddyName);
        tvChallengeText = (CustomTextView) findViewById(R.id.tvChallenge);
        headerText = (TextView) findViewById(R.id.headerText);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        tvDate = (Button) findViewById(R.id.tvDate);
        tvAddress = (CustomTextView) findViewById(R.id.tvAddress);
        tvAddress.setHint("Address");
        tvAddress.setOnClickListener(this);
        tvTime = (Button) findViewById(R.id.tvTime);
        editText = (CustomEditText) findViewById(R.id.editText);
        //editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(onActivitySelectedListener);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(BuddyUpRequest.this, progressWheel);
        isChallenge = (CheckBox) findViewById(R.id.imgTick);
        isChallenge.setChecked(false);

        buddyupimage = (CircleImageView) findViewById(R.id.buddyRequestImage);
        challenage_buddyupimage = (CircleImageView) findViewById(R.id.imageView1);
        challenage_userimage = (CircleImageView) findViewById(R.id.imageView3);

        // set text for header & bottom
        headerText.setText("Buddy Up Request");
        if (!intent.getBooleanExtra("fromBuddyupPage", false)) {
            activityItems.add(new ActivityList("Select Activity", false));
        }

        //calendar
        Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        mHour = cal.get(Calendar.HOUR_OF_DAY);
        mMinute = cal.get(Calendar.MINUTE);
        cal.set(mYear, mMonth, mDay);
        Date date = cal.getTime();
        tvDate.setText(dateFormatsdf.format(date));
        tvTime.setText("Set Time");
        //set onclick listener
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        btn_sendrequest.setOnClickListener(this);
        (findViewById(R.id.leftBackImage)).setOnClickListener(this);
        isChallenge.setOnClickListener(this);
        tvDate.setOnClickListener(this);
        tvTime.setOnClickListener(this);
        isChallenge.setChecked(true);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.leftBackImage:
                onBackPressed();
                break;
            case R.id.imgTick:
                Utility.setEventTracking(BuddyUpRequest.this, "Buddy Up create page", "It’s a challenge on Buddy Up create Page");
                break;
            case R.id.tvDate:
                showDialog(DATE_DIALOG_ID);
                break;
            case R.id.tvTime:

                TimePickerDialog dialogs = new TimePickerDialog(BuddyUpRequest.this, new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Date selected_time = null;

                        if (count == 1) {
                            mHour = i;
                            mMinute = i1;
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(mYear, mMonth, mDay, mHour, mMinute);
                            selected_time = calendar.getTime();
                            long selected_time_inmillisec = calendar.getTimeInMillis();
                            try {

                                Utility.setEventTracking(BuddyUpRequest.this, "Buddy Up Dashboard", "Time on Buddy Up create Page");

                                //if(Utility.requestTimelogic(BuddyUpRequest.this,selected_time_inmillisec,dateFormatsdf.parse(tvDate.getText().toString()))){ patch by moorthy on 08-02-16 for 30 min issue
                                if (Utility.requestTimelogic(BuddyUpRequest.this, selected_time_inmillisec,
                                        dateFormatsdf.parse(tvDate.getText().toString()))) {
                                    startTimeMilliSec = selected_time_inmillisec;
                                    mFinal_startTime = web_foramt.format(selected_time).toLowerCase();
                                    startTime = timeFormatsdf.format(selected_time).toLowerCase();

                                    //To increase one hour of the End Time of a Buddy/Pickup.
                                    mHour = mHour + 1;
                                    //
                                 /*   customTimePicker = new CustomTimePickerDialog(BuddyUpRequest.this, this, mHour, mMinute, false);
                                    customTimePicker.setTitle("Set End Time");
                                    customTimePicker.show();*/
                                    TimePickerDialog dialogs = new TimePickerDialog(BuddyUpRequest.this, new OnTimeSetListener() {
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

                                                tvTime.setText(startTime + " - " + endTime);

                                            } else {
                                                tvTime.setText("Set Time");
                                                Utility.showToastMessage(BuddyUpRequest.this, "Please choose valid time!");
                                            }

                                        }
                                    }, mHour, mMinute, false);
                                    dialogs.show();
                                    count = 0;
                                } else {
                                    tvTime.setText("Set Time");
                                    Utility.showToastMessage(BuddyUpRequest.this, getString(R.string.msg_buddy_up_start_time_error));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mHour = i;
                            mMinute = i1;
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(mYear, mMonth, mDay, mHour, mMinute);
                            Date date = calendar.getTime();
                            if (Utility.isvalidEndTime(new Date(startTimeMilliSec), date)) {

                                m_Final_endTime = web_foramt.format(date).toLowerCase();
                                Log.i("m_Final_endTime", ":" + m_Final_endTime);
                                endTime = timeFormatsdf.format(date).toLowerCase();
                                tvTime.setText(startTime + " - " + endTime);

                            } else {
                                tvTime.setText("Set Time");
                                Utility.showToastMessage(BuddyUpRequest.this, "Please choose valid time!");
                            }

                        }
                    }
                }, mHour + 1, mMinute, false);
                dialogs.show();
             /*   customTimePicker = new CustomTimePickerDialog(BuddyUpRequest.this, this, mHour + 1, mMinute, false);
                customTimePicker.show();*/
                //    customTimePicker.setTitle("Set Start Time");
                count = 1;
                break;
            case R.id.bottomBarText:
                Utility.setEventTracking(BuddyUpRequest.this, "Buddy Up create page", "Send Request button on Buddy Up create Page");
                if (intent != null && intent.getBooleanExtra("fromBuddyupPage", false)) {
                    if (!TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime) &&
                            !tvTime.getText().toString().equals("Set Time") &&
                            !TextUtils.isEmpty(mFinal_startTime) &&
                            !TextUtils.isEmpty(m_Final_endTime)) {
                        updateBuddyUpRequestPickupView();
                    } else {
                        Utility.showToastMessage(BuddyUpRequest.this, "Please select time");
                    }

                } else {
                    if (isEdit) {
                        updateBuddyUpRequest();
                    } else {
                        buddyUpRequest();
                    }
                }

                break;
            case R.id.tvAddress:
                if (spinner.getSelectedItemPosition() != 0) {
                    Utility.setEventTracking(BuddyUpRequest.this, "Buddy Up Dashboard", "Buddy Up Location on Buddy Up create Page");
                    // gpsTracker = new GPSTracker(BuddyUpRequest.this);
                    final ActivityList activityList = (ActivityList) spinner.getSelectedItem();
                    if (isEdit && isBooked.equalsIgnoreCase("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BuddyUpRequest.this);
                        builder.setMessage("You have already booked the venue. Are you sure you want to edit?");
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AppConstants.showLocatoinPicker(BuddyUpRequest.this, false,
                                        activityList.getActivity().toString().trim(),
                                        activityList.isBookingOpen());
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
                        AppConstants.showLocatoinPicker(BuddyUpRequest.this,
                                false, activityList.getActivity().toString().trim(),
                                activityList.isBookingOpen());
                    }
                } else {
                    Utility.showToastMessage(this, "Please select an activity");
                }
                break;
            default:
                break;

        }
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
     * set time on text
     *
     * @param view      time pickerview
     * @param hourOfDay hour
     * @param minute    mins
     */

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Date selected_time = null;

        if (count == 1) {
            mHour = hourOfDay;
            mMinute = minute;
            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, mMonth, mDay, mHour, mMinute);
            selected_time = calendar.getTime();
            long selected_time_inmillisec = calendar.getTimeInMillis();
            try {

                Utility.setEventTracking(BuddyUpRequest.this, "Buddy Up Dashboard", "Time on Buddy Up create Page");

                //if(Utility.requestTimelogic(BuddyUpRequest.this,selected_time_inmillisec,dateFormatsdf.parse(tvDate.getText().toString()))){ patch by moorthy on 08-02-16 for 30 min issue
                if (Utility.requestTimelogic(BuddyUpRequest.this, selected_time_inmillisec,
                        dateFormatsdf.parse(tvDate.getText().toString()))) {
                    startTimeMilliSec = selected_time_inmillisec;
                    mFinal_startTime = web_foramt.format(selected_time).toLowerCase();
                    startTime = timeFormatsdf.format(selected_time).toLowerCase();

                    //To increase one hour of the End Time of a Buddy/Pickup.
                    mHour = mHour + 1;
                    //
                    customTimePicker = new CustomTimePickerDialog(BuddyUpRequest.this, this, mHour, mMinute, false);
                    customTimePicker.setTitle("Set End Time");
                    customTimePicker.show();
                    count = 0;
                } else {
                    tvTime.setText("Set Time");
                    Utility.showToastMessage(BuddyUpRequest.this, getString(R.string.msg_buddy_up_start_time_error));
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
                Log.i("m_Final_endTime", ":" + m_Final_endTime);
                endTime = timeFormatsdf.format(date).toLowerCase();
                tvTime.setText(startTime + " - " + endTime);

            } else {
                tvTime.setText("Set Time");
                Utility.showToastMessage(BuddyUpRequest.this, "Please choose valid time!");
            }

        }

    }

    @Override
    public void successResponse(String successResponse, int flag) {

        /** flag == 0 buddyup API response
         *  flag  == 1 editschedule API response.
         */

        JSONObject jsonObject = null;

        if (!TextUtils.isEmpty(successResponse)) {
            try {
                jsonObject = new JSONObject(successResponse);
            } catch (Exception e) {
                e.printStackTrace();
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
            }
        }

        switch (flag) {

            case 0:
                Utility.setEventTracking(BuddyUpRequest.this, "", AppConstants.EVENT_TRACKING_ID_BUDDY_UP_REQUEST);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                System.out.println("Buudyup-starttime" + seconds);
                if (jsonObject != null) {
                    try {
                        if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            btn_sendrequest.setEnabled(true);
                            SharedPref.getInstance().setSharedValue(BuddyUpRequest.this, notification_count, jsonObject.optInt("notification_count"));
                            notifyCountChanged();
                            //Utility.showToastMessage(BuddyUpRequest.this, "Buddy Up Created!");
                            Utility.showToastMessage(BuddyUpRequest.this, getResources().getString(R.string.buddyup_created));

                         //  getback.getBack();
                            this.finish();
                            Pastnotifiationfragment.getback.getBack();
                            if (!intent.getBooleanExtra("fromBuddyupPage", false)) {
                                Log.d("poyeeeeeee", "" + intent.getBooleanExtra("fromBuddyupPage", false));
                                // onBackPressed();
                                if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                                    BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                                }
                            }

                        } else {

                            Log.d(TAG, "string not null  : " + (jsonObject.optString(KEY_IS_DUPLICATE) != null));
                            Log.d(TAG, "empty check  : " + !(TextUtils.isEmpty(jsonObject.optString(KEY_IS_DUPLICATE))));
                            Log.d(TAG, "check boolean  : " + jsonObject.optBoolean(KEY_IS_DUPLICATE));


                            if ((!(TextUtils.isEmpty(jsonObject.optString(KEY_IS_DUPLICATE)))) && jsonObject.optBoolean(KEY_IS_DUPLICATE)) { //We have duplicate entry
                                Utility.showAlertForExistingTimeSlot(BuddyUpRequest.this, getString(R.string.msg_duplicate_create_buddy_up), onAlertClickListener);
                            } else {
                                Utility.showToastMessage(BuddyUpRequest.this, jsonObject.optString(KEY_MSG));
                            }
                        }
                        btn_sendrequest.setEnabled(true);
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);

                        if (isScheduleExists == false && intent.getBooleanExtra("fromBuddyupPage", false)) {
                            Map<String, String> param = new HashMap<>(3);
                            param.put("iduser", SharedPref.getInstance().getStringVlue(BuddyUpRequest.this, userId));
                            param.put("screen", "recreate_buddy_up");
                            RequestHandler.getInstance().stringRequestVolley(BuddyUpRequest.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(BuddyUpRequest.this, isStaging)) + "popup_detail", param, this, 2);

                        } else {

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Buudyup-endtime" + System.currentTimeMillis());
                break;

            case 1:
                if (jsonObject != null) {

                    if (jsonObject.optString("status").trim().contains("Invited")) {
                        Utility.showToastMessage(BuddyUpRequest.this, getResources().getString(R.string.buddyup_created));
                    } else if (jsonObject.optString("status").trim().contains("null")) {

                    } else {
                        try {
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            btn_sendrequest.setEnabled(true);
                            if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                                if (!jsonObject.optString("status").equals("null")) {
                                    applozicChat.sendCustomMsgtoGroup(groupId, jsonObject.optString("status"));
                                }
                                Utility.showToastMessage(BuddyUpRequest.this, getResources().getString(R.string.buddyup_edited));
                                Intent i = new Intent();
                                i.putExtra("response", jsonObject.toString());
                                setResult(RESULT_OK, i);
                                finish();
                            } else {
                                if ((!(TextUtils.isEmpty(jsonObject.optString(KEY_IS_DUPLICATE)))) && jsonObject.optBoolean(KEY_IS_DUPLICATE)) { //We have duplicate entry
                                    Utility.showAlertForExistingTimeSlot(BuddyUpRequest.this, getString(R.string.msg_duplicate_create_buddy_up), onAlertClickListener);
                                } else {
                                    Utility.showToastMessage(BuddyUpRequest.this, jsonObject.optString(KEY_MSG));
                                }
                            }
                            btn_sendrequest.setEnabled(true);
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
                    Log.d("popupresponce", new Gson().toJson(jsonObject));
                    Log.d("session", AppConstants.getSession(BuddyUpRequest.this) + "");
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        String type = jsonObject.optString("type");
                        if (type.equalsIgnoreCase("pick_up") && AppConstants.isread.isEmpty()) {
                            AppConstants.genralPopupEvent(BuddyUpRequest.this, 1, jsonObject, this, BuddyUpRequest.this, "buddyup");
                            Log.d("show pickup dialog", "");
                        } else if (type.equalsIgnoreCase("promotional") && AppConstants.isread.isEmpty()) {

                            if (jsonObject.optString("content").isEmpty()) {
                                AppConstants.genralPopupEvent(BuddyUpRequest.this, 3, jsonObject, this, BuddyUpRequest.this, "buddyup");
                            } else {
                                AppConstants.genralPopupEvent(BuddyUpRequest.this, 2, jsonObject, this, BuddyUpRequest.this, "buddyup");
                            }
                            //  SharedPref.getInstance().setSharedValue( BuddyUpRequest.this, "APP_SESSION", 1);


                            Log.d("show pramotional dialog", "");
                        } else if (type.equalsIgnoreCase("rateus") && AppConstants.isread.isEmpty()) {
                            AppConstants.genralPopupEvent(BuddyUpRequest.this, 0, jsonObject, this, BuddyUpRequest.this, "buddyup");

                        }
                        if (!AppConstants.isread.isEmpty()) {
                            if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                                BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                            }
                            finish();
                        }

                    } else {

                        if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                            BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                        }
                        finish();
                    }

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
        btn_sendrequest.setEnabled(true);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }


    /**
     * choose business locations
     *
     * @param latLng             location
     * @param Address            text address to display
     * @param isBooked           isbooked or not
     * @param idBusiness         isbusiness user or not
     * @param isBusinessLocation isbusiness location or not
     */

    @Override
    public void mapViewNotifier(LatLng latLng, String Address, String isBooked, String idBusiness, int isBusinessLocation) {
        if (latLng != null && Address != null) {
            this.idbusiness = idBusiness;
            this.isbusinesslocation = isBusinessLocation;
            Lat = latLng.latitude;
            lng = latLng.longitude;
            tvAddress.setText(Address);
            this.isBooked = isBooked;
        }
    }

    @Override
    public void getGroupId(String groupId) {

    }


    /**
     * to get applozic channal for created buddyup
     *
     * @param mChannel buddy up Channel
     */

    @Override
    public void getChannel(Channel mChannel) {
        try {
            if (intent != null && intent.getBooleanExtra("fromBuddyupPage", false)) {

                Map<String, String> params = new HashMap<String, String>();
                params.put("iduser", SharedPref.getInstance().getStringVlue(BuddyUpRequest.this, userId));
                params.put("activity", skillId);
                params.put("start_time", mFinal_startTime);
                params.put("end_time", m_Final_endTime);
                params.put("location", tvAddress.getText().toString().replaceAll(",null", ""));
                params.put("latitude", "" + "22.447766");
                params.put("longitude", "" + "13.498456");
                params.put("message", StringEscapeUtils.escapeJava(editText.getText().toString()));
                params.put("challenge", isChallenge());
                params.put("memberid", idbuddy);
                params.put("idbusiness", idbusiness);
                params.put("isbusinesslocation", "" + isbusinesslocation);
                params.put("is_booked", "" + isBooked);
                params.put("group_id", "" + group_id);
                params.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));
                btn_sendrequest.setEnabled(false);
                RequestHandler.getInstance().stringRequestVolley(BuddyUpRequest.this,
                        AppConstants.getBaseUrl(SharedPref.getInstance()
                                .getBooleanValue(BuddyUpRequest.this, isStaging)) + buddyuprequest, params, BuddyUpRequest.this, 0);


            } else {
                ActivityList activityList = (ActivityList) spinner.getSelectedItem();
                Map<String, String> params = new HashMap<String, String>();
                params.put("iduser", SharedPref.getInstance().getStringVlue(BuddyUpRequest.this, userId));
                params.put("activity", skillId);
                params.put("start_time", mFinal_startTime);
                params.put("end_time", m_Final_endTime);
                params.put("location", tvAddress.getText().toString().replaceAll(",null", ""));
                params.put("latitude", "" + Lat);
                params.put("longitude", "" + lng);
                params.put("message", StringEscapeUtils.escapeJava(editText.getText().toString()));
                params.put("challenge", isChallenge());
                params.put("memberid", buddyDetails.getUserid());
                params.put("idbusiness", idbusiness);
                params.put("isbusinesslocation", "" + isbusinesslocation);
                params.put("is_booked", "" + isBooked);
                params.put("group_id", "" + mChannel.getKey());
                params.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));
                btn_sendrequest.setEnabled(false);
                RequestHandler.getInstance().stringRequestVolley(BuddyUpRequest.this,
                        AppConstants.getBaseUrl(SharedPref.getInstance()
                                .getBooleanValue(BuddyUpRequest.this, isStaging)) + buddyuprequest, params, BuddyUpRequest.this, 0);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initFailure(String error_msg) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
        btn_sendrequest.setEnabled(true);
        Utility.showToastMessage(this, "Please try again");
    }


    private AdapterView.OnItemSelectedListener onActivitySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            skillId=actId.get(position);
            if (!isEdit) {
                Utility.setEventTracking(BuddyUpRequest.this, "Buddy Up create page", "Select Activity on Buddy Up create Page");
                if (intent != null && intent.getBooleanExtra("fromBuddyupPage", false)) {
                    tvAddress.setText(location);
                } else {
                    tvAddress.setText("");
                }

                Lat = 0.0;
                lng = 0.0;
                idbusiness = "";
                isbusinesslocation = 0;
                isBooked = "0";
            }
            ActivityList data = (ActivityList) parent.getItemAtPosition(position);
            if (data.isBookingOpen() && !(isEdit)) {
                AppConstants.showUActivBookingDialog(BuddyUpRequest.this);

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int year, int month, int day) {
            Utility.setEventTracking(BuddyUpRequest.this, "Buddy Up create page", "Date on Buddy Up create Page");
            mYear = year;
            mMonth = month;
            mDay = day;
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            Date date = calendar.getTime();
            if (Utility.isVaildrequestDateSetLogic(date)) {
                tvDate.setText(dateFormatsdf.format(date));
                tvTime.setText("Set Time");
                startTimeMilliSec = 0;
            } else {
                Utility.showToastMessage(BuddyUpRequest.this, "Please select valid date");
            }
        }
    };

    private OnAlertClickListener onAlertClickListener = new OnAlertClickListener() {
        @Override
        public void onClickPositive() {
            isScheduleExists = false;
            if (intent != null && intent.getBooleanExtra("fromBuddyupPage", false)) {
                updateBuddyUpRequestPickupView();
                Notification_Fragment.inter.refresh();
            } else {
                if (isEdit) {
                    updateBuddyUpRequest();
                } else {
                    buddyUpRequest();
                }
            }

        }

        @Override
        public void onClickNegative() {
            isScheduleExists = true;
        }
    };
}

