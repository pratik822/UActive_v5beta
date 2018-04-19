package com.uactiv.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.belladati.httpclientandroidlib.entity.mime.content.ByteArrayBody;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;
import com.uactiv.R;
import com.uactiv.adapter.SpinnerAdapter;
import com.uactiv.adapter.viewholder.CustomSpinnerAdapter;
import com.uactiv.controller.IRunTimePermission;
import com.uactiv.controller.ResponseListener;
import com.uactiv.location.GPSTracker;
import com.uactiv.model.ActivityList;
import com.uactiv.network.ImageUpLoader;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.RuntimeAccess;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProfileEdit extends Activity implements OnClickListener, AppConstants.SharedConstants,
        AppConstants.urlConstants, DatePickerDialog.OnDateSetListener, ResponseListener, IRunTimePermission, AdapterView.OnItemSelectedListener {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PICK_GALLERY_IMAGE_REQUEST_CODE = 200;
    private static final int PICK_MULTI_GALLERY_IMAGE_REQUEST_CODE = 300;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    TextView tvSave, tvCancel;
    EditText editFirstName, editLastName, editDetail;
    Spinner spinGender, spinSkating, spinYoga, spinRunning, spinSquash;
    CustomTextView spinEarly;
    SpinnerAdapter spinGenderAdpter, spinEarlyAdapter, spinYogaAdapter, spinRunAdapter, spinSquashAdapter;
    CustomSpinnerAdapter spinSkateAdapter;
    ArrayList<String> activitylist = new ArrayList<>();
    ArrayList<String> earlyItems = new ArrayList<>();
    ArrayList<String> genderItems = new ArrayList<>();
    ImageView prfile_pic = null;
    DisplayImageOptions options;
    String stringages = null;
    ImageLoader imageLoader = null;
    Bitmap sendbmp;
    BufferedReader bufferReader;
    String singleLine;
    String jsonResponse = "";
    ImageView selctpic = null;
    String TAG = getClass().getSimpleName();
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ByteArrayBody bab;
    int spinerPosition;
    RatingBar ratingBar1, ratingBar2, ratingBar3, ratingBar4;
    GifImageView progressWheel = null;
    String path;
    private Uri mImageCaptureUri, mCropedImagePath;
    private InputFilter filter;
    GPSTracker mGpsTracker;
    List<ActivityList> myActivityList;
    private ArrayList<ActivityList> mactivitylist = new ArrayList<>();
    String myskillnew;
    String skill_one,skill_two,skill_three,skill_four;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edit);
        loadspinner();
        initializeValues();
        Utility.setScreenTracking(this, AppConstants.SCREEN_TRACKING_ID_EDITUSERPROFILE);
    }

    private void loadspinner() {

        earlyItems.add("Early 30's");
        earlyItems.add("Early 20's");
        earlyItems.add("Under 21");
        genderItems.add("Gender");
        genderItems.add("Male");
        genderItems.add("Female");

        //  activitylist.add("Select activity");
        mactivitylist.add(new ActivityList("Select activity", false));
        String jsonArray = SharedPref.getInstance().getStringVlue(this, Api_skill_list); //All activities
        try {
            JSONArray activity = new JSONArray(jsonArray);
            if (activity != null && activity.length() > 0) {
                for (int i = 0; i < activity.length(); i++) {
                    JSONObject obj = activity.optJSONObject(i);
                    // && !obj.optString("type").equals("buddyup")
                    ActivityList activityList = new ActivityList();
                    activityList.setActivity(obj.optString("activity"));
                    activityList.setId(obj.optString("id"));
                    activityList.setIsBookingOpen(true);
                    mactivitylist.add(activityList);
                    Log.d("mylist", new Gson().toJson(mactivitylist))
                    ;
                    if (Utility.isNullCheck(obj.optString("activity"))) {
                        // activitylist.add(obj.optString("activity"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeValues() {
        //textview
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(ProfileEdit.this, progressWheel);
        tvSave = (TextView) findViewById(R.id.tvSave);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        prfile_pic = (ImageView) findViewById(R.id.imageView1);
        selctpic = (ImageView) findViewById(R.id.imageView2);
        selctpic.setOnClickListener(this);
        myActivityList = new ArrayList<>();

        spinEarly = (CustomTextView) findViewById(R.id.spinEarly);

        spinEarly.setOnClickListener(this);
        mGpsTracker = new GPSTracker(ProfileEdit.this);

        //edittext
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(ProfileEdit.this, firstname))) {
            editFirstName.setText(SharedPref.getInstance().getStringVlue(ProfileEdit.this, firstname));
        }

        editLastName = (EditText) findViewById(R.id.editLastName);

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(ProfileEdit.this, lastname))) {
            editLastName.setText(SharedPref.getInstance().getStringVlue(ProfileEdit.this, lastname));
        }

        editDetail = (EditText) findViewById(R.id.editDetail);


        editDetail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Nothing
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                if (count == 0 && wordsLength >= AppConstants.MAX_WORDS) {
                    setCharLimit(editDetail, editDetail.getText().length());
                } else {
                    removeFilter(editDetail);
                }
                //editDetail.setText(String.valueOf(wordsLength) + "/" );
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(ProfileEdit.this, about_yourself))) {
            editDetail.setText(StringEscapeUtils.unescapeJava(SharedPref.getInstance().getStringVlue(ProfileEdit.this, about_yourself)));
        }

        Log.e("nullcheck", "profileupdate" + Utility.isNullCheck(SharedPref.getInstance().getStringVlue(ProfileEdit.this, age)));


        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(ProfileEdit.this, age))) {

            stringages = SharedPref.getInstance().getStringVlue(ProfileEdit.this, age);

            Log.e("Agecalcualte", "profileupdate");
            Date birthday = null;
            try {
                birthday = AppConstants.sdf.parse(SharedPref.getInstance().getStringVlue(ProfileEdit.this, age));
                spinEarly.setText(Utility.calculateAge(birthday, ProfileEdit.this, true));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        spinGender = (Spinner) findViewById(R.id.spinGender);


        spinSkating = (Spinner) findViewById(R.id.spinSkating);
        spinYoga = (Spinner) findViewById(R.id.spinYoga);
        spinRunning = (Spinner) findViewById(R.id.spinRunning);
        spinSquash = (Spinner) findViewById(R.id.spinRunnin4);

        spinSkating.setOnItemSelectedListener(this);
        spinYoga.setOnItemSelectedListener(this);
        spinRunning.setOnItemSelectedListener(this);
        spinSquash.setOnItemSelectedListener(this);


        ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
        ratingBar4 = (RatingBar) findViewById(R.id.ratingBar4);


        Log.e("image", "profileupdate" + SharedPref.getInstance().getStringVlue(ProfileEdit.this, image));

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(ProfileEdit.this, image))) {

            /*Picasso.with(ProfileEdit.this)
                    .load(SharedPref.getInstance().getStringVlue(ProfileEdit.this, image))
                    .placeholder(R.drawable.ic_profile).centerCrop().fit()
                    .into(prfile_pic);
            */
            Utility.setImageUniversalLoader(ProfileEdit.this, SharedPref.getInstance().getStringVlue(ProfileEdit.this, image), prfile_pic);

        }
        //spinner values
        spinEarlyAdapter = new SpinnerAdapter(this, R.layout.custom_spinner_left, earlyItems, 0);


        spinGenderAdpter = new SpinnerAdapter(this, R.layout.custom_spinner_left, genderItems, 0);
        spinGender.setAdapter(spinGenderAdpter);

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(ProfileEdit.this, gender))) {

            if (SharedPref.getInstance().getStringVlue(ProfileEdit.this, gender).equals("male")) {

                spinGender.setSelection(1);
            } else {
                spinGender.setSelection(2);
            }
        }


        spinSkateAdapter = new CustomSpinnerAdapter(this, R.layout.custom_spinner_activity, mactivitylist, 0);

        spinSkating.setAdapter(spinSkateAdapter);
        spinYoga.setAdapter(spinSkateAdapter);
        spinRunning.setAdapter(spinSkateAdapter);
        spinSquash.setAdapter(spinSkateAdapter);

        spinSkating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    skill_one=mactivitylist.get(i).getId();
                   // myskillnew += mactivitylist.get(i).getId() + ",";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinYoga.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    skill_two=mactivitylist.get(i).getId();
                  //  myskillnew += mactivitylist.get(i).getId() + ",";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinRunning.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    skill_three=mactivitylist.get(i).getId();
                   // myskillnew += mactivitylist.get(i).getId() + ",";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinSquash.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    skill_four=mactivitylist.get(i).getId();
                  //  myskillnew += mactivitylist.get(i).getId() + ",";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //onclick
        tvSave.setOnClickListener(this);
        tvCancel.setOnClickListener(this);


        String jsonArray = SharedPref.getInstance().getStringVlue(ProfileEdit.this, skills);

        try {

            JSONArray activity = new JSONArray(jsonArray);

            for (int i = 0; i < activity.length(); i++) {

                JSONObject obj = activity.getJSONObject(i);

                if (i == 0) {

                    for (int j = 0; j < mactivitylist.size(); j++) {
                        if (obj.optString("activity").equalsIgnoreCase(mactivitylist.get(j).getActivity())) {
                            spinSkating.setSelection(j);
                            ratingBar1.setRating(Integer.parseInt(obj.optString("level")));
                        }
                    }


                }
                if (i == 1) {
                    for (int j = 0; j < mactivitylist.size(); j++) {
                        if (obj.optString("activity").equalsIgnoreCase(mactivitylist.get(j).getActivity())) {
                            spinYoga.setSelection(j);
                            ratingBar2.setRating(Integer.parseInt(obj.optString("level")));
                        }
                    }
                }
                if (i == 2) {
                    for (int j = 0; j < mactivitylist.size(); j++) {
                        if (obj.optString("activity").equalsIgnoreCase(mactivitylist.get(j).getActivity())) {
                            spinRunning.setSelection(j);
                            ratingBar3.setRating(Integer.parseInt(obj.optString("level")));
                        }
                    }
                }
                if (i == 3) {
                    for (int j = 0; j < mactivitylist.size(); j++) {
                        if (obj.optString("activity").equalsIgnoreCase(mactivitylist.get(j).getActivity())) {
                            spinSquash.setSelection(j);
                            ratingBar4.setRating(Integer.parseInt(obj.optString("level")));
                        }
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[]{filter});
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.imageView2:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    RuntimeAccess access = new RuntimeAccess(this, this);
                    access.grandPermission();
                } else {
                    selectImage();
                }
                break;
            case R.id.tvSave:
                Utility.setEventTracking(ProfileEdit.this, "Edit Profile screen", "Save on Edit Profile Screen");
                String selectedSkill = null;

                if (firstname.length() > 0) {

                    if (lastname.length() > 0) {

                        if (!TextUtils.isEmpty(age)) {

                            if (spinGender.getSelectedItemPosition() != 0) {


                                if (spinSkating.getSelectedItemPosition() != 0 || spinYoga.getSelectedItemPosition() != 0 || spinRunning.getSelectedItemPosition() != 0 || spinSquash.getSelectedItemPosition() != 0) {

                                    selectedSkill = spinSkating.getSelectedItem().toString();


                                    if (!spinYoga.getSelectedItem().toString().trim().equals(selectedSkill) || spinYoga.getSelectedItem().toString().trim().equals("Select activity")) {

                                        selectedSkill = selectedSkill + "," + spinYoga.getSelectedItem().toString();

                                        String[] tem = selectedSkill.split(",");

                                        Log.e(TAG, "runn" + tem[0] + tem[1]);

                                        if (!spinRunning.getSelectedItem().toString().trim().equals(tem[0]) && !spinRunning.getSelectedItem().toString().trim().equals(tem[1]) || spinRunning.getSelectedItem().toString().trim().equals("Select activity")) {

                                            selectedSkill = selectedSkill + "," + spinRunning.getSelectedItem().toString();

                                            String[] tem1 = selectedSkill.split(",");

                                            Log.e(TAG, "spin" + tem1[0] + tem1[1] + tem1[2]);

                                            if (!spinSquash.getSelectedItem().toString().trim().equals(tem1[0]) && !spinSquash.getSelectedItem().toString().trim().equals(tem1[1]) && !spinSquash.getSelectedItem().toString().trim().equals(tem1[2]) || spinSquash.getSelectedItem().toString().trim().equals("Select activity")) {

                                                selectedSkill = selectedSkill + "," + spinSquash.getSelectedItem().toString();

                                                String[] strings = getFilteredSkill(selectedSkill);

                                                Log.e(TAG, "selectedSkill " + strings[0] + " " + strings[1]);


                                                String ratestring = "";

                                                boolean b = false;

                                                if (strings[1].contains("0")) {

                                                    if (ratingBar1.getRating() != 0.0) {

                                                        ratestring = ratestring + "," + (int) ratingBar1.getRating();
                                                        b = true;
                                                    } else {
                                                        b = false;
                                                        Utility.showToastMessage(ProfileEdit.this, "Please rate your activity ");
                                                        return;
                                                    }
                                                }

                                                if (strings[1].contains("1")) {

                                                    if (ratingBar2.getRating() != 0.0) {

                                                        ratestring = ratestring + "," + (int) ratingBar2.getRating();
                                                        b = true;

                                                    } else {
                                                        b = false;
                                                        Utility.showToastMessage(ProfileEdit.this, "Please rate your activity ");
                                                        return;
                                                    }
                                                }
                                                if (strings[1].contains("2")) {

                                                    if (ratingBar3.getRating() != 0.0) {

                                                        ratestring = ratestring + "," + (int) ratingBar3.getRating();
                                                        b = true;
                                                    } else {
                                                        b = false;
                                                        Utility.showToastMessage(ProfileEdit.this, "Please rate your activity ");
                                                        return;
                                                    }
                                                }

                                                if (strings[1].contains("3")) {

                                                    if (ratingBar4.getRating() != 0.0) {
                                                        ratestring = ratestring + "," + (int) ratingBar4.getRating();
                                                        b = true;
                                                    } else {
                                                        b = false;
                                                        Utility.showToastMessage(ProfileEdit.this, "Please rate your activity ");
                                                        return;
                                                    }
                                                }

                                                Log.e("ratestring", "" + ratestring.substring(1));

                                                if (b) {
                                                    //    if (ratingBar1.getRating() != 0.0 && ratingBar2.getRating() != 0.0 && ratingBar3.getRating() != 0.0 && ratingBar4.getRating() != 0.0) {

                                                    if (Utility.isConnectingToInternet(ProfileEdit.this)) {

                                                        try {
                                                            JSONObject param = new JSONObject();

                                                            String firstname = editFirstName.getText().toString();
                                                            String lastname = editLastName.getText().toString();
                                                            firstname = firstname.substring(0, 1).toUpperCase() + firstname.substring(1);
                                                            lastname = lastname.substring(0, 1).toUpperCase() + lastname.substring(1);
                                                            param.put("firstname", firstname);
                                                            param.put("lastname", lastname);
                                                            param.put("age", stringages);
                                                            param.put("gender", spinGender.getSelectedItem().toString());
                                                            param.put("about_yourself", StringEscapeUtils.escapeJava(editDetail.getText().toString().trim()));
                                                           // Log.d("myphoto", myskillnew.replaceAll("null", ""));

                                                            if(!skill_one.isEmpty()){
                                                                myskillnew+=skill_one+",";
                                                            }
                                                            if(!skill_two.isEmpty()){
                                                                myskillnew+=skill_two+",";
                                                            }
                                                            if(!skill_three.isEmpty()){
                                                                myskillnew+=skill_three+",";
                                                            }
                                                            if(!skill_four.isEmpty()){
                                                                myskillnew+=skill_four+",";
                                                            }
                                                            if (myskillnew.endsWith(",")) {
                                                                myskillnew = myskillnew.replaceAll("null", "").substring(0, myskillnew.replaceAll("null", "").length() - 1);
                                                            }
                                                            param.put("skills", myskillnew);
                                                            param.put("levels", ratestring.substring(1));
                                                            param.put("user_type", "" + SharedPref.getInstance().getIntVlue(ProfileEdit.this, USER_TYPE));
                                                            param.put("latitude", "" + mGpsTracker.getLatitude());
                                                            param.put("longitude", "" + mGpsTracker.getLongitude());
                                                            //Log.e(TAG, "Regis parameter" + param.toString());

                                                            Map<String, Object> stringMap = jsonToMap(param);

                                                            Log.e(TAG + " Parameters", stringMap.toString());


                                                            if (stringMap != null) {

                                                                imageUpload(stringMap);
                                                            }


                                                            /// new SignUpAsync().execute(param.toString());
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                    } else {
                                                        Utility.showInternetError(ProfileEdit.this);
                                                    }


                                                } else {

                                                    Utility.showToastMessage(ProfileEdit.this, "Please rate your activity");
                                                }

                                            } else {
                                                Utility.showToastMessage(ProfileEdit.this, "Please select another activity");
                                            }


                                        } else {
                                            Utility.showToastMessage(ProfileEdit.this, "Please select another activity");
                                        }

                                    } else {
                                        Utility.showToastMessage(ProfileEdit.this, "Please select another activity");
                                    }


                                } else {
                                    Utility.showToastMessage(ProfileEdit.this, "Please select activity");
                                }
                            } else {
                                Utility.showToastMessage(ProfileEdit.this, "Please select gender");
                            }
                        } else {
                            Utility.showToastMessage(ProfileEdit.this, "Please update age");
                        }
                    } else {

                        Utility.showToastMessage(ProfileEdit.this, "Please enter lastName");
                    }

                } else {

                    Utility.showToastMessage(ProfileEdit.this, "Please enter firstName");
                }


                break;
            case R.id.tvCancel:
                onBackPressed();
                Utility.setEventTracking(ProfileEdit.this, "Edit Profile screen", "Cancel on Edit Profile screen");
                break;

            case R.id.spinEarly:

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(R.color.colorPrimary);
                dpd.show(getFragmentManager(), "Datepickerdialog");


                break;
        }


    }

    private String[] getFilteredSkill(String selectedSkill) {

        String finalSelectedSkill = "";
        String rateposioin = "";

        String[] arr = new String[2];

        if (selectedSkill != null) {

            String[] skill = selectedSkill.split(",");

            if (skill != null && skill.length > 0) {

                for (int i = 0; i < skill.length; i++) {

                    if (skill[i] != null && !TextUtils.isEmpty(skill[i]) && !skill[i].equals("Select activity")) {

                        finalSelectedSkill = finalSelectedSkill + "," + skill[i];

                        rateposioin = rateposioin + "," + i;
                    }
                }
            }

            arr[0] = finalSelectedSkill.substring(1);
            arr[1] = rateposioin.substring(1);
        }
        return arr;
    }

    private void imageUpload(Map<String, Object> param) {
        //Extras
        param.put("iduser", SharedPref.getInstance().getStringVlue(ProfileEdit.this, userId));

        if (Utility.isNullCheck(param.toString())) {

            ImageUpLoader imageUpLoader = new ImageUpLoader(ProfileEdit.this);
            Log.e("Selected Path", ":" + path);
            imageUpLoader.setFile(path);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.startAnimation();
            imageUpLoader.getResponse(AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(ProfileEdit.this, isStaging)) + editprofile, this, param, 0);

        }
    }

    public Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        final String second = String.valueOf(System.currentTimeMillis());
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileEdit.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    Utility.setEventTracking(ProfileEdit.this, "Add photo on edit profile screen", "Take photo");
                    try {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                "tmp_avatar_" + second + ".jpg"));
                        try {

                            mCropedImagePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                    "crop_tmp_avatar_" + second + ".jpg"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dialog.cancel();

                } else if (items[item].equals("Choose from Library")) {
                    Utility.setEventTracking(ProfileEdit.this, "Add photo on edit profile screen", "choose from library");
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_GALLERY_IMAGE_REQUEST_CODE);
                    try {

                        mCropedImagePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                "crop_tmp_avatar_" + second + ".jpg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (items[item].equals("Cancel")) {
                    Utility.setEventTracking(ProfileEdit.this, "Add photo on edit profile screen", "cancel on edit profile screen");
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                path = mImageCaptureUri.getPath();

                Log.e("buvanesh", "" + mImageCaptureUri.getPath());

                File file = new File(path);

                if (file.exists()) {

                    //Utility.setImageUniversalLoader(ProfileEdit.this, Uri.fromFile(file).toString(), prfile_pic);

                    Crop.of(Uri.fromFile(file), mCropedImagePath)
                            .withAspect(AppConstants.CROP_WIDTH, AppConstants.CROP_HEIGHT)
                            .start(this);

                }

            } else if (resultCode == RESULT_CANCELED) {

                Toast.makeText(ProfileEdit.this,
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {

                Toast.makeText(ProfileEdit.this,
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
        switch (requestCode) {


            case PICK_GALLERY_IMAGE_REQUEST_CODE:

                if (data != null) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    if (picturePath != null) {

                        path = picturePath;
                        File file = new File(path);
                        if (file.exists()) {
                            //Utility.setImageUniversalLoader(ProfileEdit.this, Uri.fromFile(file).toString(), prfile_pic);
                            Crop.of(Uri.fromFile(file), mCropedImagePath)
                                    .withAspect(AppConstants.CROP_WIDTH, AppConstants.CROP_HEIGHT)
                                    .start(this);

                        }
                    } else {
                        Log.e("null picturePath", "" + picturePath);
                    }
                }

                break;
            case Crop.REQUEST_CROP:

                if (resultCode == RESULT_OK) {

                    if (Crop.getOutput(data) != null) {

                        Log.e("Crop", String.valueOf(Crop.getOutput(data)));
                        Log.e("mCropedImagePath", String.valueOf(mCropedImagePath));

                        File file = new File(mCropedImagePath.getPath());
                        Log.e("isExisit", String.valueOf(file.exists()));

                        if (file.exists() && file != null) {
                            //profileimage.setImageResource(getBitmap(profileimage));
                            Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITPROFILEPICTURE);
                            Utility.setImageUniversalLoader(ProfileEdit.this, Crop.getOutput(data).toString(), prfile_pic);
                            path = file.getAbsolutePath();
                        } else {
                            Toast.makeText(ProfileEdit.this,
                                    "Sorry! Failed to update image", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                }
                break;

            default:
                break;


        }

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int dayofMonth) {

        stringages = dayofMonth + "/" + (month + 1) + "/" + year;

        Date birthday = null;
        try {
            birthday = AppConstants.sdf.parse(stringages);
            spinEarly.setText(Utility.calculateAge(birthday, ProfileEdit.this, true));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void successResponse(String successResponse, int flag) {

        Log.d(TAG, "editprofile response " + successResponse);

        switch (flag) {

            case 0:


                if (successResponse != null) {
                    try {
                        JSONObject object = new JSONObject(successResponse);
                        if (object.optString(resultcheck).equals(KEY_TRUE)) {

                            ResponseHandler.getInstance().storeProfileInfo(ProfileEdit.this, object);
                            Intent pic_change = new Intent(AppConstants.ACTION_PROFILE_PICTURE_CHANGED);
                            LocalBroadcastManager.getInstance(ProfileEdit.this).sendBroadcast(pic_change);
                            updateProfileInfoInApplozic();
                            Utility.showToastMessage(ProfileEdit.this, "Your profile is updated");
                            finish();
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        } else {
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            Utility.showToastMessage(ProfileEdit.this, object.optString(KEY_MSG));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    );

                } else {
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                    Toast.makeText(ProfileEdit.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }

    }

    private void updateProfileInfoInApplozic() {


        Log.d(TAG, "profile image : " + SharedPref.getInstance().getStringVlue(ProfileEdit.this, image));

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String displayName = SharedPref.getInstance().getStringVlue(ProfileEdit.this, firstname);
                String imageUrl = SharedPref.getInstance().getStringVlue(ProfileEdit.this, image);

                ApiResponse apiResponse1 = UserService.getInstance(ProfileEdit.this).updateDisplayNameORImageLink(displayName, imageUrl, null, null);
                if (apiResponse1 != null && apiResponse1.isSuccess()) {
                    Log.i("Image", "updated or disply name update");
                } else {
                    Log.i("update", "failed");
                }
            }
        });


        /*final RegisterUserClientService registerUserClientService = new RegisterUserClientService(ProfileEdit.this);
        final User user =registerUserClientService.getUserDetail();
        user.setDisplayName(SharedPref.getInstance().getStringVlue(ProfileEdit.this, firstname));
        user.setImageLink(SharedPref.getInstance().getStringVlue(ProfileEdit.this, image));

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    registerUserClientService.updateRegisteredAccount(user);
                    MobiComUserPreference mobiComUserPreference = MobiComUserPreference.getInstance(getApplicationContext());
                    mobiComUserPreference.setDisplayName(user.getDisplayName());
                    mobiComUserPreference.setImageLink(user.getImageLink());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/


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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RuntimeAccess.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                //perms.put(android.Manifest.permission.WRITE_SETTINGS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);


                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (//perms.get(android.Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED
                    //perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

                        ) {
                    onSuccess(0);
                    Log.e("Running from OnRequest", "Permission");
                    // All Permissions Granted
                }
//                else if (perms.get(Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
//                    startActivity(new Intent("android.settings.action.MANAGE_WRITE_SETTINGS"));
//                }
                else {
                    // Permission Denied
                    Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT)
                            .show();
                }


            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSuccess(int milis) {
        selectImage();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.id.spinSkating:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
            case R.id.spinYoga:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
            case R.id.spinRunning:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
            case R.id.spinRunnin4:
                Utility.setScreenTracking(this, AppConstants.EVENT_TRACKING_ID_EDITUSERACTIVITIES);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

