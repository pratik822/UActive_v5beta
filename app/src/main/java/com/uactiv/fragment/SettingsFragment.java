package com.uactiv.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.facebook.login.LoginManager;
import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.activity.MainActivity;
import com.uactiv.activity.Sign_In;
import com.uactiv.adapter.SpinnerAdapter;
import com.uactiv.controller.ResponseListener;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment implements OnClickListener, AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener {

    TextView tvSignOut, tvRadius, tvChangePwd;
    CustomButton tvCancel, tvSave;
    EditText editEmail, editPassword;
    Spinner spinGender;
    SeekBar seekBar;
    ArrayList<String> gender = new ArrayList<>();
    SpinnerAdapter adapter = null;
    int speedlimit;
    SwitchCompat tvReceiveBuddyUpChk = null;
    SwitchCompat receivenotification = null;
    CheckBox checkFb = null;
    GifImageView progressWheel = null;
    EditText editConfirmPwd;
    private String TAG = getClass().getSimpleName();
    private RelativeLayout disablelayout;
    Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        if (!AppConstants.isGestLogin(getActivity())) {
            Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_SETTINGS);
        }else{
            Utility.setScreenTracking(getActivity(),"Guest login Settings");
        }


        if (SharedPref.getInstance().getBooleanValue(getActivity(), isbussiness)) {
            view.findViewById(R.id.buddyLayout).setVisibility(View.GONE);
            view.findViewById(R.id.tvUserText).setVisibility(View.GONE);
            view.findViewById(R.id.view3).setVisibility(View.GONE);
        }


        gender.add("Both");
        gender.add("Male");
        gender.add("Female");

        initViews(view);

        updatePreferneceUI();

        if (AppConstants.isGestLogin(getActivity())) {
            hideViewGustLogin();
        }


        return view;
    }


    public void hideViewGustLogin() {
        tvReceiveBuddyUpChk.setEnabled(false);
        receivenotification.setChecked(true);
        receivenotification.setEnabled(false);
        tvChangePwd.setEnabled(false);
        editEmail.setEnabled(false);
        editPassword.setEnabled(false);
        tvSignOut.setEnabled(false);
        disablelayout.setBackgroundColor(Color.parseColor("#ddd8d4"));


    }

    public String encryptPassword(final String password) {
        MessageDigest digest;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePreferneceUI() {

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit))) {
            seekBar.setProgress(Integer.parseInt(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit)));
            tvRadius.setText(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit) + "km");
        } else {
            if (AppConstants.getRadius(getActivity()) != null && AppConstants.getRadius(getActivity()) != "") {
                seekBar.setProgress(Integer.parseInt(AppConstants.getRadius(getActivity())) - 5);
            } else {
                seekBar.setProgress(195);
            }

        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), gender_pref))) {
            String pref = SharedPref.getInstance().getStringVlue(getActivity(), gender_pref);
            spinGender.setSelection(adapter.getPosition(pref));
        } else if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
            String pref = AppConstants.getGender(getActivity());
            spinGender.setSelection(adapter.getPosition(pref));
        }


        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), isreceive_request))) {
            String pref = SharedPref.getInstance().getStringVlue(getActivity(), isreceive_request);

            if (pref.equals("1")) {
                tvReceiveBuddyUpChk.setChecked(true);
            } else {
                tvReceiveBuddyUpChk.setChecked(false);
            }
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), isreceive_notification))) {
            String pref = SharedPref.getInstance().getStringVlue(getActivity(), isreceive_notification);

            if (pref.equals("1")) {
                receivenotification.setChecked(true);
            } else {
                receivenotification.setChecked(false);
            }
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), facebookid)) && !SharedPref.getInstance().getStringVlue(getActivity(), facebookid).equals("0")) {

            String pref = SharedPref.getInstance().getStringVlue(getActivity(), facebookid);

            //  if(pref.equals("1")){

            checkFb.setChecked(true);

            //  }else{

            // }
        } else {
            checkFb.setChecked(false);
        }


        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), email))) {
            editEmail.setText(SharedPref.getInstance().getStringVlue(getActivity(), email));
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), password))) {
            editPassword.setText(SharedPref.getInstance().getStringVlue(getActivity(), password));
        }


    }

    private void logOut() {

        if (Utility.isConnectingToInternet(getActivity())) {
            try {
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + logout, param, this, 1); //For logout API
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utility.showInternetError(getActivity());
        }
    }

    private void updateSettings() {

        if (Utility.isConnectingToInternet(getActivity())) {

            try {
                Map<String, String> param = new HashMap<>();
                param.put("search_limit", "" + speedlimit);
                param.put("gender_pref", spinGender.getSelectedItem().toString());

                if (tvReceiveBuddyUpChk.isChecked()) {

                    param.put("isreceive_request", "1");
                } else {
                    param.put("isreceive_request", "0");
                }

                if (receivenotification.isChecked()) {

                    param.put("isreceive_notification", "1");
                } else {
                    param.put("isreceive_notification", "0");
                }
                //param.put("password", editPassword.getText().toString());

                if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), facebook_link))) {
                    param.put("facebook_link", "1");
                } else {
                    param.put("facebook_link", "0");
                }
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));

                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + updatesetting, param, this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(getActivity());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initViews(View view) {

        tvSignOut = (TextView) view.findViewById(R.id.tvSignOut);
        tvSignOut.setOnClickListener(this);
        tvCancel = (CustomButton) view.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(this);
        tvSave = (CustomButton) view.findViewById(R.id.tvSave);
        tvSave.setOnClickListener(this);
        editEmail = (EditText) view.findViewById(R.id.editEmail);
        editEmail.setEnabled(false);
        progressWheel = (GifImageView) view.findViewById(R.id.gifLoader);
        Utility.showProgressDialog(getActivity(), progressWheel);
        editPassword = (EditText) view.findViewById(R.id.editPassword);
        editPassword.setFocusableInTouchMode(true);
        editPassword.setEnabled(false);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar1);
        tvRadius = (TextView) view.findViewById(R.id.tvRadius);
        tvChangePwd = (TextView) view.findViewById(R.id.tvChangePwd);
        disablelayout=(RelativeLayout)view.findViewById(R.id.disablelayout);

        tvReceiveBuddyUpChk = (SwitchCompat) view.findViewById(R.id.tvReceiveBuddyUpChk);
        tvReceiveBuddyUpChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utility.setScreenTracking(getActivity(), AppConstants.EVENT_TRACKING_ID_RECEIVEBUDDYUPREQUESTOFF);
            }
        });

        receivenotification = (SwitchCompat) view.findViewById(R.id.toggleButton2);
        receivenotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utility.setScreenTracking(getActivity(), AppConstants.EVENT_TRACKING_ID_PUSHNOTIFICATIONSWITCHOFF);

                Utility.setEventTracking(getActivity(), "Settings", "Push notifications on setting page");
            }
        });

        checkFb = (CheckBox) view.findViewById(R.id.checkFb);
        checkFb.setChecked(false);
        checkFb.setFocusableInTouchMode(false);
        checkFb.setClickable(false);
        tvChangePwd.setOnClickListener(this);

        //spinner gender
        spinGender = (Spinner) view.findViewById(R.id.SpinGender);
        adapter = new SpinnerAdapter(getActivity(), R.layout.spinner_title, gender, 0);
        spinGender.setAdapter(adapter);
        spinGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPref.getInstance().setSharedValue(getActivity(), PREF_GEST_GENDER, gender.get(position));
                Utility.setScreenTracking(getActivity(), AppConstants.EVENT_TRACKING_ID_CHNAGEGENDER);

                if (!AppConstants.isGestLogin(getActivity())) {
                    Utility.setEventTracking(getActivity(), "Settings", "Gender Preference on Setting screen");
                }else{
                    Utility.setEventTracking(getActivity(), "Settings", "Gender Preference on Guest login Setting screen");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //seekbar default value
        seekBar.setMax(195);
        seekBar.setProgress(195);
        tvRadius.setText("5km");
        //seekbar
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Utility.setEventTracking(getActivity(), "Settings", "Search radius on setting screen");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if (!AppConstants.isGestLogin(getActivity())) {
                    Utility.setScreenTracking(getActivity(), AppConstants.EVENT_TRACKING_ID_CHNAGESEARCHRADIUS);
                }else{
                    Utility.setScreenTracking(getActivity(), "Change Search Radius on guest login.");
                }

                progress = progress + 5;
                SharedPref.getInstance().setSharedValue(getActivity(), PREF_GEST_GEST_RADIUS, String.valueOf(progress));
                speedlimit = progress;
                tvRadius.setText(progress + "km");

            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v == tvCancel) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        } else if (v == tvChangePwd) {
            Utility.setScreenTracking(getActivity(), "Change password screen in settings");
            final EditText editOldPwd, editNewPwd;
            final TextView btnCancel, btnSave;
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_change_pwd);
            dialog.show();

            editOldPwd = (EditText) dialog.findViewById(R.id.editOldPwd);
            editNewPwd = (EditText) dialog.findViewById(R.id.editNewPwd);
            editConfirmPwd = (EditText) dialog.findViewById(R.id.editConfirmPwd);
            btnCancel = (TextView) dialog.findViewById(R.id.btnCancel);
            btnSave = (TextView) dialog.findViewById(R.id.btnSave);


            btnCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Utility.setEventTracking(getActivity(), "Change password screen in settings", "cancel  on Change password screen in settings");
                }
            });

            btnSave.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.setEventTracking(getActivity(), "Change password screen in settings", "Save on Change password screen in settings");
                    String decrptedOldPassword = SharedPref.getInstance().getStringVlue(getActivity(), password);
                    Log.e(TAG + "decrptedPassword", "" + decrptedOldPassword);
                    String decrptedEnteredPassword = encryptPassword(editOldPwd.getText().toString().trim());
                    Log.e(TAG + "decrptedEnteredPassword", "" + decrptedEnteredPassword);

                    if (decrptedOldPassword.equals(decrptedEnteredPassword)) {

                        if (editNewPwd.getText().toString().equals(editConfirmPwd.getText().toString()) && editNewPwd.length() >= 4 && editConfirmPwd.length() >= 4) {
                            progressWheel.setVisibility(View.VISIBLE);
                            progressWheel.startAnimation();
                            updatePaswordAPI(editConfirmPwd.getText().toString());
                            editPassword.setText(encryptPassword(editConfirmPwd.getText().toString()));
                            dialog.dismiss();
                        } else {
                            Utility.showToastMessage(getActivity(), "Please enter valid new password!");
                        }

                    } else {
                        Utility.showToastMessage(getActivity(), "Old password is incorrect");
                    }


                }
            });
        }

        switch (v.getId()) {

            case R.id.tvSignOut:
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                tvSignOut.setEnabled(false);
                if (Utility.isConnectingToInternet(getActivity())) {
                    Utility.setEventTracking(getActivity(), "Settings", "Sign Out button on on setting screen");
                    logOut();
                } else {
                    Utility.showInternetError(getActivity());
                }

                break;

            case R.id.tvSave:
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                tvSave.setEnabled(false);
                if (AppConstants.isGestLogin(getActivity())) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("adapterfrom", "buddyupadap");
                    intent.putExtra("setting", "setting");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                } else {
                    updateSettings();
                }

                break;
        }

    }


    private void updatePaswordAPI(String password) {

        if (Utility.isConnectingToInternet(getActivity())) {

            try {

                Map<String, String> param = new HashMap<>();
                param.put("password", password);
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + changePassword, param, this, 2);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(getActivity());
        }
    }


    @Override
    public void successResponse(String successResponse, int flag) {

        /** flag == 0 update settings API response
         *  flag == 1 for logout API response.
         */

        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(successResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (flag) {

            case 0:

                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        try {
                            ResponseHandler.getInstance().storeProfileInfo(getActivity(), jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Utility.showToastMessage(getActivity(), "Settings successfully updated");
                    } else {
                        Utility.showToastMessage(getActivity(), jsonObject.optString(KEY_MSG));
                    }
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                    tvSave.setEnabled(true);
                }
                getFragmentManager().popBackStack();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                Home.buddyList.clear();
                intent.putExtra("adapterfrom", "buddyupadap");
                intent.putExtra("setting", "setting");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

                break;
            case 1:

                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        SharedPref.getInstance().setSharedValue(getActivity(), "ischatlogin", false);
                        SharedPref.getInstance().setSharedValue(getActivity(), islogin, false);
                        new UserClientService(getActivity()).logout();
                        Intent i = new Intent(getActivity(), Sign_In.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        getActivity().finish();

                        try {
                            SharedPref.getInstance().clearPreference(getActivity());
                            Log.e("userId", "" + SharedPref.getInstance().getStringVlue(getActivity(), userId));
                            LoginManager.getInstance().logOut();
                            SharedPref.getInstance().setSharedValue(getActivity(), isStartUpExpired, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Utility.showToastMessage(getActivity(), "Logout Failed!");
                    }
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                    tvSignOut.setEnabled(true);
                }

                break;
            case 2:
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                if (jsonObject != null) {
                    if (jsonObject.optBoolean(resultcheck)) {
                        SharedPref.getInstance().setSharedValue(getActivity(), password, encryptPassword(editConfirmPwd.getText().toString().trim()));
                        Utility.showToastMessage(getActivity(), "Password has been updated!");
                        Utility.setScreenTracking(getActivity(), AppConstants.EVENT_TRACKING_ID_CHANGE_PASSWORD);
                    } else {
                        Utility.showToastMessage(getActivity(), "Password cannot be updated!");
                    }
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
    public void errorResponse(String errorResponse, int flag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }
}
