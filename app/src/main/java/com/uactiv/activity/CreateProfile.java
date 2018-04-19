package com.uactiv.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.felipecsl.gifimageview.library.GifImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.soundcloud.android.crop.Crop;
import com.uactiv.R;
import com.uactiv.adapter.SpinnerAdapter;
import com.uactiv.applozicchat.ApplozicChat;
import com.uactiv.controller.IRunTimePermission;
import com.uactiv.controller.ResponseListener;
import com.uactiv.location.GPSTracker;
import com.uactiv.network.ImageUpLoader;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.RuntimeAccess;
import com.uactiv.utils.RuntimeAccess_one;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomEditText;
import com.uactiv.widgets.CustomTextView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CreateProfile extends Activity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, ResponseListener, AppConstants.urlConstants, Response.ErrorListener,
        AppConstants.SharedConstants, IRunTimePermission {


    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int PICK_GALLERY_IMAGE_REQUEST_CODE = 200;
    public static SimpleDateFormat ageSdf = new SimpleDateFormat("MM/DD/yyyy");
    private String TAG = getClass().getSimpleName();
    private EditText editDetail;
    private Spinner spinSkating, spinYoga, spinRunning, Badminton;
    private SpinnerAdapter spinGenderAdpter, spinSkateAdapter;
    private RatingBar ratingBar1, ratingBar2, ratingBar3, ratingBar4;

    private de.hdodenhof.circleimageview.CircleImageView profileimage;
    private CustomTextView spinAge;
    private ArrayList<String> activitylist = new ArrayList<>();
    private ArrayList<String> earlyItems = new ArrayList<>();
    private ArrayList<String> genderItems = new ArrayList<>();
    private Intent i = null;
    private String jsonValue = null;
    private GPSTracker gps = null;
    //  private CustomButton signup;
    private Bitmap sendbmp;
    private CustomTextView profilename;
    private ScrollView detailsscroll;
    private String login_type = null;
    private GifImageView progressWheel = null;
    private String agestr = null;
    private CustomEditText et_referralCode = null;
    private String referralCode;
    private String path;
    private Uri mImageCaptureUri;
    private Uri mCropedImagePath;
    private InputFilter filter;
    private ImageView iv_plus;
    int idx = 0;
    LinearLayout thirdviewlayout, fourthviewlayout;
    View thirdview, fourthview;
    private SeekBar seekBar;
    private CustomTextView spinGender;
    private View secondView;
    private ImageView iv_spin_one, iv_spin_two, iv_spin_three, iv_spin_four;
    int myprogress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        setContentView(R.layout.create_profile_layout);
        Utility.setScreenTracking(CreateProfile.this, AppConstants.SCREEN_TRACKING_ID_GET_ACTIV);
        loadSpinner();
        i = getIntent();
        if (i != null) {
            jsonValue = i.getStringExtra("profile_data");
            login_type = i.getStringExtra("logintype"); //if logintype 1 == facebook login
        }
        gps = new GPSTracker(CreateProfile.this);
        initView();
        getUserInterestActivityList();
        //  detailsscroll.smoothScrollT`o(0, 0);
        showReferralDialog();

        iv_plus = (ImageView) findViewById(R.id.iv_plus);
        thirdviewlayout = (LinearLayout) findViewById(R.id.thirdviewlayout);
        fourthviewlayout = (LinearLayout) findViewById(R.id.fourthviewlayout);

        thirdview = (View) findViewById(R.id.thirdview);
        fourthview = (View) findViewById(R.id.fourthview);

        iv_spin_one = (ImageView) findViewById(R.id.iv_spin_one);
        iv_spin_two = (ImageView) findViewById(R.id.iv_spin_two);
        iv_spin_three = (ImageView) findViewById(R.id.iv_spin_three);
        iv_spin_four = (ImageView) findViewById(R.id.iv_spin_four);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(40);
        Drawable d = getResources().getDrawable(R.drawable.myfootball);


        performSpinerClick();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                myprogress = progress;

                if (progress == 3) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_one));

                } else if (progress == 6) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_two));

                } else if (progress == 9) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_three));

                } else if (progress == 12) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_four));

                } else if (progress == 15) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_one));

                } else if (progress == 18) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_two));

                } else if (progress == 21) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_three));

                } else if (progress == 24) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_four));

                } else if (progress == 27) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_one));

                } else if (progress == 30) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_two));

                }
                if (progress == 33) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_three));

                }
                if (progress >= 35) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.myfootball_four));
                    Utility.setEventTracking(CreateProfile.this, "Create Profile page( Second screen)", "Get active! button on create profile");
                    String selectedSkill = null;
                    if (checkFile()) {

                        if (spinAge.length() > 0 && spinAge != null && !TextUtils.isEmpty(spinAge.getText().toString()) && !TextUtils.isEmpty(agestr) && agestr != null) {

                            if (spinGender.getText().length() <= 0) {

                                Utility.showToastMessage(CreateProfile.this, "Please select gender");
                            } else {

                                if (spinSkating.getSelectedItemPosition() != 0 || spinYoga.getSelectedItemPosition() != 0 || spinRunning.getSelectedItemPosition() != 0 || Badminton.getSelectedItemPosition() != 0) {

                                    selectedSkill = spinSkating.getSelectedItem().toString();

                                    if (!spinYoga.getSelectedItem().toString().trim().equals(selectedSkill) || spinYoga.getSelectedItem().toString().trim().equals("Select")) {

                                        selectedSkill = selectedSkill + "," + spinYoga.getSelectedItem().toString();

                                        String[] tem = selectedSkill.split(",");

                                        Log.e(TAG, "runn" + tem[0] + tem[1]);

                                        if (!spinRunning.getSelectedItem().toString().trim().equals(tem[0]) && !spinRunning.getSelectedItem().toString().trim().equals(tem[1]) || spinRunning.getSelectedItem().toString().trim().equals("Select")) {

                                            selectedSkill = selectedSkill + "," + spinRunning.getSelectedItem().toString();

                                            String[] tem1 = selectedSkill.split(",");

                                            Log.e(TAG, "spin" + tem1[0] + tem1[1] + tem1[2]);

                                            if (!Badminton.getSelectedItem().toString().trim().equals(tem1[0]) && !Badminton.getSelectedItem().toString().trim().equals(tem1[1]) && !Badminton.getSelectedItem().toString().trim().equals(tem1[2]) || Badminton.getSelectedItem().toString().trim().equals("Select")) {

                                                selectedSkill = selectedSkill + "," + Badminton.getSelectedItem().toString();

                                                String[] strings = getFilteredSkill(selectedSkill);

                                                Log.e(TAG, "selectedSkill " + strings[0] + " " + strings[1]);


                                                String ratestring = "";

                                                boolean b = false;

                                                if (strings[1].contains("0")) {

                                                    if (ratingBar1.getRating() > 0.0) {
                                                        ratestring = ratestring + "," + (int) ratingBar1.getRating();
                                                        b = true;
                                                    } else {
                                                        b = false;

                                                        Utility.showToastMessage(CreateProfile.this, "Please rate your activity ");
                                                        seekBar.setProgress(2);
                                                        return;
                                                    }
                                                }

                                                if (strings[1].contains("1")) {

                                                    if (ratingBar2.getRating() > 0.0) {
                                                        ratestring = ratestring + "," + (int) ratingBar2.getRating();
                                                        b = true;

                                                    } else {
                                                        b = false;
                                                        Utility.showToastMessage(CreateProfile.this, "Please rate your activity ");
                                                        seekBar.setProgress(2);
                                                        return;
                                                    }
                                                }
                                                if (strings[1].contains("2")) {

                                                    if (ratingBar3.getRating() > 0.0) {
                                                        ratestring = ratestring + "," + (int) ratingBar3.getRating();
                                                        b = true;
                                                    } else {
                                                        b = false;
                                                        Utility.showToastMessage(CreateProfile.this, "Please rate your activity ");
                                                        seekBar.setProgress(2);
                                                        return;
                                                    }
                                                }

                                                if (strings[1].contains("3")) {

                                                    if (ratingBar4.getRating() > 0.0) {
                                                        ratestring = ratestring + "," + (int) ratingBar4.getRating();
                                                        b = true;
                                                    } else {
                                                        b = false;
                                                        Utility.showToastMessage(CreateProfile.this, "Please rate your activity ");
                                                        seekBar.setProgress(2);
                                                        return;
                                                    }
                                                }

                                                Log.e("ratestring", "" + ratestring.substring(1));

                                                if (b) {
                                                    //    if (ratingBar1.getRating() != 0.0 && ratingBar2.getRating() != 0.0 && ratingBar3.getRating() != 0.0 && ratingBar4.getRating() != 0.0) {

                                                    if (Utility.isConnectingToInternet(CreateProfile.this)) {

                                                        //if (gps.canGetLocation() && gps.getLongitude() != 0.0 && gps.getLatitude() != 0.0) {

                                                        try {
                                                            JSONObject param = new JSONObject(jsonValue);

                                                            if (login_type.equals("1")) {

                                                                String firstname = param.optString("first_name");
                                                                String lastname = param.optString("last_name");
                                                                firstname = firstname.substring(0, 1).toUpperCase() + firstname.substring(1);
                                                                lastname = lastname.substring(0, 1).toUpperCase() + lastname.substring(1);
                                                                param.put("firstname", firstname);
                                                                param.put("lastname", lastname);
                                                                param.put("facebookid", param.optString("id"));
                                                                param.remove("image");
                                                            }

                                                            param.put("age", agestr);
                                                            param.put("gender", spinGender.getText().toString());
                                                            param.put("latitude", "" + gps.getLatitude());
                                                            param.put("longitude", "" + gps.getLongitude());
                                                            param.put("about_yourself", editDetail.getText().toString().trim());

                                                            //  param.put("skills", strings[0]);
                                                            Log.d("myindex",String.valueOf(spinSkating.getSelectedItemPosition()+","+spinYoga.getSelectedItemPosition())+","+spinRunning.getSelectedItemPosition()+","+Badminton.getSelectedItemPosition());
                                                            param.put("skills", String.valueOf(spinSkating.getSelectedItemPosition()+","+spinYoga.getSelectedItemPosition())+","+spinRunning.getSelectedItemPosition()+","+Badminton.getSelectedItemPosition());
                                                            // param.put("levels", (int) ratingBar1.getRating() + "," + (int) ratingBar2.getRating() + "," + (int) ratingBar3.getRating() + "," + (int) ratingBar4.getRating());
                                                            param.put("levels", ratestring.substring(1));
                                                            //  Log.e(TAG, "Regis parameter" + param.toString());

                                                            Map<String, Object> stringMap = jsonToMap(param);

                                                            Log.e(TAG + " Parameters", stringMap.toString());


                                                            if (stringMap != null) {
                                                                imageUpload(stringMap);
                                                            }


                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }


                                                    } else {
                                                        Utility.showInternetError(CreateProfile.this);
                                                        seekBar.setProgress(2);
                                                    }


                                                } else {

                                                    Utility.showToastMessage(CreateProfile.this, "Please rate your activity");
                                                    seekBar.setProgress(2);
                                                }

                                            } else {
                                                Utility.showToastMessage(CreateProfile.this, "Please select another activity");
                                                seekBar.setProgress(2);
                                            }


                                        } else {
                                            Utility.showToastMessage(CreateProfile.this, "Please select another activity");
                                            seekBar.setProgress(2);
                                        }

                                    } else {
                                        Utility.showToastMessage(CreateProfile.this, "Please select another activity");
                                        seekBar.setProgress(2);
                                    }


                                } else {
                                    Utility.showToastMessage(CreateProfile.this, "Please select activity");
                                    seekBar.setProgress(2);
                                }

                            }

                        } else {
                            Utility.showToastMessage(CreateProfile.this, "Please select valid date of birth");
                            seekBar.setProgress(2);
                        }
                    } else {
                        Utility.showToastMessage(CreateProfile.this, "please upload image!");
                        seekBar.setProgress(2);
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (myprogress <= 30) {
                    seekBar.setProgress(2);
                }
            }
        });

        iv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idx++;
                if (idx == 1) {
                    thirdviewlayout.setVisibility(View.VISIBLE);

                    secondView.setVisibility(View.VISIBLE);
                } else if (idx == 2) {
                    fourthviewlayout.setVisibility(View.VISIBLE);
                    fourthview.setVisibility(View.VISIBLE);
                    iv_plus.setVisibility(View.GONE);
                    thirdview.setVisibility(View.VISIBLE);
                }

            }
        });

        spinGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppConstants.ShowGenderDialog(CreateProfile.this, spinGender);
            }
        });
    }


    /**
     * show referral dialog
     */
    private void showReferralDialog() {

        final Dialog dialog1 = new Dialog(CreateProfile.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.referral_code);
        et_referralCode = (CustomEditText) dialog1.findViewById(R.id.comment_text);

        CustomTextView done = (CustomTextView) dialog1.findViewById(R.id.done);
        CustomTextView skip = (CustomTextView) dialog1.findViewById(R.id.skip);
        dialog1.setCancelable(false);
        dialog1.show();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!et_referralCode.getText().toString().trim().isEmpty()) {
                    referralCode = et_referralCode.getText().toString().trim();
                    referralCode.toUpperCase();
                    dialog1.dismiss();
                    Utility.setEventTracking(CreateProfile.this, "Create Profile page( Second screen)", "done button on enter referral code");

                } else {
                    Toast.makeText(getApplicationContext(), "Referral Code Can't be blank", Toast.LENGTH_LONG).show();
                }


            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.setEventTracking(CreateProfile.this, "Create Profile page( Second screen)", "skip button on enter referral code");
                dialog1.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        gps = new GPSTracker(CreateProfile.this);
    }

    private void loadSpinner() {

        earlyItems.add("Early 30's");
        earlyItems.add("Early 20's");
        earlyItems.add("Under 21");

        genderItems.add("Gender");
        genderItems.add("Male");
        genderItems.add("Female");

    }


    private void initView() {

        profilename = (CustomTextView) findViewById(R.id.profile_name);

        spinGender = (CustomTextView) findViewById(R.id.spinGender);
        spinSkating = (Spinner) findViewById(R.id.spinSkating);
        spinYoga = (Spinner) findViewById(R.id.spinYoga);
        spinRunning = (Spinner) findViewById(R.id.spinRunning);
        Badminton = (Spinner) findViewById(R.id.spinRunnin4);
        spinAge = (CustomTextView) findViewById(R.id.spinEarly);
        spinAge.setOnClickListener(this);
        spinGenderAdpter = new SpinnerAdapter(this, R.layout.custom_spinner_left, genderItems, 0);
        //  spinGender.setAdapter(spinGenderAdpter);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(CreateProfile.this, progressWheel);


        profileimage = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.edit_propic);
        profileimage.setOnClickListener(this);
        detailsscroll = (ScrollView) findViewById(R.id.detailsscroll);


        ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
        ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
        ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
        ratingBar4 = (RatingBar) findViewById(R.id.ratingBar4);
        editDetail = (EditText) findViewById(R.id.editDetail);
        secondView = (View) findViewById(R.id.secondView);


        if (!TextUtils.isEmpty(i.getStringExtra("name"))) {
            profilename.setText("Welcome " + i.getStringExtra("name") + "!");
        }

     /*   if (i.getStringExtra("gender") != null) {

            if (i.getStringExtra("gender").equals("male")) {
                spinGender.setSelection(1);
            } else if (i.getStringExtra("gender").equals("female")) {
                spinGender.setSelection(2);
            }
        }*/

        editDetail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                if (count == 0 && wordsLength >= AppConstants.MAX_WORDS) {
                    setCharLimit(editDetail, editDetail.getText().length());
                } else {
                    removeFilter(editDetail);
                }
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        try {
            JSONObject mTemp = new JSONObject(jsonValue);
            if (login_type.equals("1") && mTemp.optString("image") != null) {
                Date birthday = null;
                try {
                    agestr = mTemp.optString("birthday");
                    birthday = ageSdf.parse(mTemp.optString("birthday"));
                    spinAge.setText(Utility.calculateAge(birthday, CreateProfile.this, true));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DownloadBitmap().execute(new JSONObject(jsonValue).optString("image"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setSpinnerAdapter() {
        spinSkateAdapter = new SpinnerAdapter(this, R.layout.custom_spinner_activity, activitylist, 0);
        spinSkating.setAdapter(spinSkateAdapter);
        spinYoga.setAdapter(spinSkateAdapter);
        spinRunning.setAdapter(spinSkateAdapter);
        Badminton.setAdapter(spinSkateAdapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gps != null) {
            gps.stopUsingGPS();
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

    private void getUserInterestActivityList() {

        if (Utility.isConnectingToInternet(CreateProfile.this)) {
            try {
                Map<String, String> stringMap = new HashMap<>();
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                spinYoga.setEnabled(false);
                spinRunning.setEnabled(false);
                spinSkating.setEnabled(false);
                Badminton.setEnabled(false);
                RequestHandler.getInstance().stringRequestVolley(CreateProfile.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(CreateProfile.this, isStaging)) + getactivityList, stringMap, this, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(CreateProfile.this, "Internet not available,Cannot get activities", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.spinEarly:

                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(R.color.colorPrimary);
                dpd.show(getFragmentManager(), "Datepickerdialog");
                Utility.setEventTracking(CreateProfile.this, "", "DOB on create profile");
                //  imageuploadExample();

                break;

            case R.id.edit_propic:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    RuntimeAccess_one access = new RuntimeAccess_one(this, this);
                    access.grandPermission();
                } else {
                    selectImage();
                }
                break;
        }


    }

    private boolean checkratingvalidation(String rateChek) {

        boolean stat = false;

        if (rateChek != null && rateChek.length() > 0) {

            String[] ratecount = rateChek.split(",");

            if (rateChek != null && rateChek.length() > 0) {

                for (int j = 0; j < ratecount.length; j++) {

                    if (ratecount[j].contains("0") && ratingBar1.getRating() != 0.0) {
                        stat = true;
                    }
                    if (ratecount[j].contains("1") && ratingBar2.getRating() != 0.0) {
                        stat = true;
                    }
                    if (ratecount[j].contains("2") && ratingBar3.getRating() != 0.0) {
                        stat = true;
                    }
                    if (ratecount[j].contains("3") && ratingBar4.getRating() != 0.0) {
                        stat = true;
                    }
                }
            }
        }
        return stat;
    }


    public void performSpinerClick() {
        iv_spin_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinSkating.performClick();
            }
        });

        iv_spin_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinYoga.performClick();
            }
        });

        iv_spin_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinRunning.performClick();
            }
        });

        iv_spin_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Badminton.performClick();
            }
        });
    }

    private String[] getFilteredSkill(String selectedSkill) {
        {

            String finalSelectedSkill = "";
            String rateposioin = "";

            String[] arr = new String[2];

            if (selectedSkill != null) {

                String[] skill = selectedSkill.split(",");

                if (skill != null && skill.length > 0) {

                    for (int i = 0; i < skill.length; i++) {

                        if (skill[i] != null && !TextUtils.isEmpty(skill[i]) && !skill[i].equals("Select")) {

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


    }

    private void checkAllFieldsMadatory() {

        String selectedSkill = null;

        if (checkFile()) {


            if (spinAge.length() > 0 && spinAge != null && !TextUtils.isEmpty(spinAge.getText().toString()) && !TextUtils.isEmpty(agestr) && agestr != null) {

                if (spinGender.getText().length() <= 0) {

                    Utility.showToastMessage(CreateProfile.this, "Please select gender");
                } else {

                    if (spinSkating.getSelectedItemPosition() != 0) {
                        selectedSkill = spinSkating.getSelectedItem().toString();

                        if (spinYoga.getSelectedItemPosition() != 0) {
                            if (!spinYoga.getSelectedItem().toString().trim().equals(selectedSkill)) {

                                selectedSkill = selectedSkill + "," + spinYoga.getSelectedItem().toString();

                                if (spinRunning.getSelectedItemPosition() != 0) {

                                    String[] tem = selectedSkill.split(",");

                                    Log.e(TAG, "runn" + tem[0] + tem[1]);

                                    if (!spinRunning.getSelectedItem().toString().trim().equals(tem[0]) && !spinRunning.getSelectedItem().toString().trim().equals(tem[1])) {

                                        selectedSkill = selectedSkill + "," + spinRunning.getSelectedItem().toString();

                                        if (Badminton.getSelectedItemPosition() != 0) {

                                            String[] tem1 = selectedSkill.split(",");

                                            Log.e(TAG, "spinSquash" + tem1[0] + tem1[1] + tem1[2]);

                                            if (!Badminton.getSelectedItem().toString().trim().equals(tem1[0]) && !Badminton.getSelectedItem().toString().trim().equals(tem1[1]) && !Badminton.getSelectedItem().toString().trim().equals(tem1[2])) {

                                                selectedSkill = selectedSkill + "," + Badminton.getSelectedItem().toString();

                                                if (ratingBar1.getRating() != 0.0 && ratingBar2.getRating() != 0.0 && ratingBar3.getRating() != 0.0 && ratingBar4.getRating() != 0.0) {

                                                    if (editDetail.length() > 0) {


                                                        if (Utility.isConnectingToInternet(CreateProfile.this)) {

                                                            if (gps.canGetLocation() && gps.getLongitude() != 0.0 && gps.getLatitude() != 0.0) {

                                                                try {
                                                                    JSONObject param = new JSONObject(jsonValue);

                                                                    if (login_type.equals("1")) {

                                                                        String firstname = param.optString("first_name");
                                                                        String lastname = param.optString("last_name");
                                                                        firstname = firstname.substring(0, 1).toUpperCase() + firstname.substring(1);
                                                                        lastname = lastname.substring(0, 1).toUpperCase() + lastname.substring(1);
                                                                        param.put("firstname", firstname);
                                                                        param.put("lastname", lastname);
                                                                    }

                                                                    param.put("age", agestr);
                                                                    param.put("gender", spinGender.getText().toString());
                                                                    param.put("latitude", "" + gps.getLatitude());
                                                                    param.put("longitude", "" + gps.getLongitude());
                                                                    param.put("about_yourself", editDetail.getText().toString());
                                                                    param.put("skills", selectedSkill);
                                                                    param.put("levels", (int) ratingBar1.getRating() + "," + (int) ratingBar2.getRating() + "," + (int) ratingBar3.getRating() + "," + (int) ratingBar4.getRating());
                                                                    //  Log.e(TAG, "Regis parameter" + param.toString());

                                                                    Map<String, Object> stringMap = jsonToMap(param);

                                                                    Log.e(TAG + " Parameters", stringMap.toString());


                                                                    if (stringMap != null) {
                                                                        imageUpload(stringMap);
                                                                    }


                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }

                                                            } else {
                                                                gps.showSettingsAlert();
                                                            }


                                                        } else {
                                                            Utility.showInternetError(CreateProfile.this);
                                                        }


                                                    } else {
                                                        Utility.showToastMessage(CreateProfile.this, "Please write a short description about your self");
                                                    }

                                                } else {

                                                    Utility.showToastMessage(CreateProfile.this, "Please rate your activity");
                                                }

                                            } else {
                                                Utility.showToastMessage(CreateProfile.this, "Please select another activity");
                                            }

                                        } else {

                                            Utility.showToastMessage(CreateProfile.this, "Please select activity");
                                        }


                                    } else {
                                        Utility.showToastMessage(CreateProfile.this, "Please select another activity");
                                    }

                                } else {
                                    Utility.showToastMessage(CreateProfile.this, "Please select activity");
                                }

                            } else {
                                Utility.showToastMessage(CreateProfile.this, "Please select another activity");
                            }

                        } else {
                            Utility.showToastMessage(CreateProfile.this, "Please select activity");
                        }
                    } else {
                        Utility.showToastMessage(CreateProfile.this, "Please select activity");
                    }

                }

            } else {
                Utility.showToastMessage(CreateProfile.this, "Please select valid date of birth");
            }
        } else {
            Utility.showToastMessage(CreateProfile.this, "please upload image!");
        }

    }

    private boolean checkFile() {

        try {
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    Log.e("checkFile", "exists");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    public List<Object> toList(JSONArray  array) throws JSONException {
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

    private void imageUpload(Map<String, Object> param) {
        //Extras
        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(CreateProfile.this, gcm_token))) {
            param.put("device_id", SharedPref.getInstance().getStringVlue(CreateProfile.this, gcm_token));
        } else {
            param.put("device_id", "");
        }
        param.put("device_id", SharedPref.getInstance().getStringVlue(CreateProfile.this, gcm_token));
        param.put("device_type", "0"); //For android 0,iOs 1
        param.put("referral_code", referralCode);

        if (Utility.isNullCheck(param.toString())) {

            ImageUpLoader imageUpLoader = new ImageUpLoader(CreateProfile.this);
            Log.e("Selected Path", ":" + path);
            imageUpLoader.setFile(path);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.startAnimation();
            imageUpLoader.getResponse(AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(CreateProfile.this, isStaging)) + registration, this, param, 0);

        }
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        if (gps != null) {
            gps.stopUsingGPS();
        }

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int dayofMonth) {

        agestr = (month + 1) + "/" + dayofMonth + "/" + year;
        //agestr = dayofMonth + "/" + (month + 1) + "/" + year;

        Date birthday = null;
        try {
            birthday = ageSdf.parse(agestr);
            spinAge.setText(Utility.calculateAge(birthday, CreateProfile.this, true));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void successResponse(String successResponse, int flag) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(successResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (flag) {

            case 0:
                Utility.setEventTracking(CreateProfile.this, "", AppConstants.EVENT_TRACKING_ID_CREATE_ACCOUNT);
                if (jsonObject != null) {
                    try {
                        if (jsonObject.optString("result").equals("true")) {

                            AppsFlyerLib.getInstance().startTracking(this.getApplication(), "ha9E2njWD2UvuMvC3XySEF");
                            AppsFlyerLib.getInstance().enableUninstallTracking("858166060344"); // ADD THIS LINE HERE*/
                            Map<String, Object> eventValue = new HashMap<String, Object>();
                            eventValue.put("Register", "true");
                            AppsFlyerLib.getInstance().trackEvent(this, AFInAppEventType.COMPLETE_REGISTRATION, eventValue);


                            SharedPref.getInstance().setSharedValue(CreateProfile.this, islogin, true);
                            ResponseHandler.getInstance().storeProfileInfo(CreateProfile.this, jsonObject);
                            registerWithApplozic();
                            Intent intent = new Intent(CreateProfile.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {

                            final JSONObject finalJsonObject = jsonObject;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    progressWheel.stopAnimation();
                                    progressWheel.setVisibility(View.GONE);
                                    //         signup.setEnabled(true);
                                    Toast.makeText(CreateProfile.this, "" + finalJsonObject.optString(KEY_MSG), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (progressWheel.isAnimating()) {
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                        //       signup.setEnabled(true);
                    }
                } else {
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                    Toast.makeText(CreateProfile.this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                }
                break;

            case 1:

                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        JSONArray skillistArray = jsonObject.optJSONArray(KEY_DETAIL);

                        if (skillistArray != null && skillistArray.length() > 0) {

                            SharedPref.getInstance().getStringVlue(CreateProfile.this, Api_skill_list);

                            activitylist.clear();
                            activitylist.add("Select");

                            for (int i = 0; i < skillistArray.length(); i++) {
                                activitylist.add(skillistArray.optJSONObject(i).optString("activity"));
                            }
                            setSpinnerAdapter();


                        }

                    }
                }
                spinYoga.setEnabled(true);
                spinRunning.setEnabled(true);
                spinSkating.setEnabled(true);
                Badminton.setEnabled(true);
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }


    /**
     * register user with applozic domain
     */
    private void registerWithApplozic() {
        new ApplozicChat(this).loginWithApplozic();
    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {

        switch (flag) {
            case 0:
                //    signup.setEnabled(false);
                break;
            case 1:
                spinYoga.setEnabled(true);
                spinRunning.setEnabled(true);
                spinSkating.setEnabled(true);
                Badminton.setEnabled(true);
                break;
            default:
                break;
        }
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);

        Log.e("Error", "Error" + volleyError.getMessage());

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {


                    try {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        final String second = String.valueOf(System.currentTimeMillis());

                        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                "tmp_avatar_" + second + ".jpg"));

                        path = mImageCaptureUri.getPath();

                        try {

                            mCropedImagePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                    "crop_tmp_avatar_" + second + ".jpg"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //path=Environment.getExternalStorageDirectory()+File.separator+"tmp_avatar_"+".jpg";
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(CreateProfile.this, "error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    dialog.cancel();

                } else if (items[item].equals("Choose from Library")) {

                    final String second = String.valueOf(System.currentTimeMillis());

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),

                            "tmp_avatar_" + second + ".jpg"));
                    path = mImageCaptureUri.getPath();
                    //   path=Environment.getExternalStorageDirectory()+File.separator+"tmp_avatar_"+".jpg";
                    try {

                        mCropedImagePath = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                                "crop_tmp_avatar_" + second + ".jpg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_GALLERY_IMAGE_REQUEST_CODE);


                } else if (items[item].equals("Cancel")) {
                    profileimage.setBackgroundResource(R.drawable.photo);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

        switch (requestCode) {


            case PICK_GALLERY_IMAGE_REQUEST_CODE:


                if (resultCode == RESULT_OK) {

                    Uri selectedImage = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    path = picturePath;
                    cursor.close();
                    if (picturePath != null) {

                        File file = new File(path);
                        if (file.exists()) {

                            Crop.of(Uri.fromFile(file), mCropedImagePath)
                                    .withAspect(AppConstants.CROP_WIDTH, AppConstants.CROP_HEIGHT)
                                    .start(this);

                            //    Utility.setImageUniversalLoader(CreateProfile.this, Uri.fromFile(file).toString(), profileimage);
                        }

                    } else {
                        Log.e("null picturePath", "" + picturePath);
                    }
                }

                break;

            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE: {

                if (resultCode == RESULT_OK) {


                    if (mImageCaptureUri != null && mImageCaptureUri.getPath() != null) {

                        path = mImageCaptureUri.getPath();

                        if (path != null) {

                            File file = new File(path);
                            if (file.exists()) {
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                                transformbitmap(bitmap, file.getAbsolutePath());
                                Crop.of(Uri.fromFile(file), mCropedImagePath)
                                        .withAspect(AppConstants.CROP_WIDTH, AppConstants.CROP_HEIGHT)
                                        .start(this);
                                Utility.setImageUniversalLoader(CreateProfile.this, Uri.fromFile(file).toString(), profileimage);
                            } else {
                                Toast.makeText(CreateProfile.this,
                                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                                        .show();
                            }


                        }
                    } else {
                        Toast.makeText(CreateProfile.this,
                                "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                                .show();
                    }

                } else if (resultCode == RESULT_CANCELED) {

                    Toast.makeText(CreateProfile.this,
                            "User cancelled image capture", Toast.LENGTH_SHORT)
                            .show();
                } else {

                    Toast.makeText(CreateProfile.this,
                            "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                            .show();
                }

            }

            break;

            case Crop.REQUEST_CROP:

                if (resultCode == RESULT_OK && data != null) {

                    if (Crop.getOutput(data) != null) {

                        Log.e("Crop", String.valueOf(Crop.getOutput(data)));
                        Log.e("mCropedImagePath", String.valueOf(mCropedImagePath));

                        File file = new File(mCropedImagePath.getPath());
                        Log.e("isExisit", String.valueOf(file.exists()));

                        if (file.exists() && file != null) {
                            //profileimage.setImageResource(getBitmap(profileimage));
                            //Bitmap bitmap =getBitmap(Crop.getOutput(data).toString());
                            Utility.setImageUniversalLoader(CreateProfile.this, Crop.getOutput(data).toString(), profileimage);
                            path = file.getAbsolutePath();
                        } else {
                            Toast.makeText(CreateProfile.this,
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

    private Bitmap transformbitmap(Bitmap takenImage, String path) {

        Bitmap rotatedBitmap = null;
        try {
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(takenImage, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(takenImage, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(takenImage, 270);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in Image rotate");
        }

        return rotatedBitmap;
    }


    public Bitmap rotateImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap getBitmap(final String image) {
        ImageLoader imageloader;
        imageloader = ImageLoader.getInstance();
        imageloader.init(ImageLoaderConfiguration.createDefault(CreateProfile.this));
        return imageloader.loadImageSync(image);
    }

    private void storeImage(Bitmap image) throws IOException {

        File pictureFile = null;
        final String second = String.valueOf(System.currentTimeMillis());
        pictureFile = new File(Environment.getExternalStorageDirectory(),
                "tmp_avatar_" + second + ".jpg");
        pictureFile.createNewFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            path = pictureFile.getAbsolutePath();

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.d(TAG, "Error NullPointerException " + e.getMessage());
        }
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

    private class DownloadBitmap extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            sendbmp = getBitmap(params[0]);
            try {
                storeImage(sendbmp);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (sendbmp != null && checkFile()) {
                Utility.setImageUniversalLoader(CreateProfile.this, "file://" + path, profileimage);
            }

        }
    }

}
