package com.uactiv.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.activity.BusinessProfileEdit;
import com.uactiv.model.SkillDo;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;

public class BusinessProfileFragment extends Fragment implements OnClickListener, AppConstants.SharedConstants {

    TextView tvPersonNames, tvDistace, tvRating;
    // /   ImageView imgDistance;
    CustomTextView tvage = null;
    ImageView profile = null;
    CustomTextView activity_one = null;
    CustomTextView activity_two = null;
    CustomTextView activity_three = null;
    CustomTextView activity_four = null;
    CustomTextView txt_abotme = null;
    RelativeLayout top_lay = null;
    String TAG = getClass().getSimpleName();
    ImageView skill_act_one = null;
    ImageView skill_act_two = null;
    ImageView skill_act_three = null;
    ImageView skill_act_four = null;
    private CustomTextView tvactivtyfive = null;
    private CustomTextView tvactivtysix = null;
    private CustomTextView tvEmail = null;
    private CustomTextView tvAdderess = null;
    private ImageView imgRateFive = null;
    private ImageView imgRateSix = null;
    private CustomTextView tvPhoneno = null;
    private CustomTextView tvLandLine = null;
    private CustomTextView tvProfile = null;
    private ImageView badgeImage = null;
    private CustomButton btnEditAccount = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_business_profile, container, false);
        Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_BUSINESS_PROFILE);
        initView(view);

        fillDataonView();

        return view;
    }

    private void fillDataonView() {

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), firstname))) {
            tvPersonNames.setText(SharedPref.getInstance().getStringVlue(getActivity(), firstname) + " " + SharedPref.getInstance().getStringVlue(getActivity(), lastname));
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), image))) {

            Utility.setImageUniversalLoader(getActivity(), SharedPref.getInstance().getStringVlue(getActivity(), image), profile);
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), businessType))) {
                tvage.setText(SharedPref.getInstance().getStringVlue(getActivity(), businessType));
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), about_yourself))) {
            txt_abotme.setVisibility(View.VISIBLE);
            txt_abotme.setText(SharedPref.getInstance().getStringVlue(getActivity(), about_yourself));
        } else {
            txt_abotme.setVisibility(View.GONE);
        }

        //update overall rating.
        try {

            /*int overallrate = (SharedPref.getInstance().getIntVlue(getActivity(), user_rating) / SharedPref.getInstance().getIntVlue(getActivity(), user_rating_count));

            if (overallrate >= 3) {

                tvRating.setVisibility(View.VISIBLE);
                tvRating.setText("User Rating " + overallrate);
            }*/


            BigDecimal rating = Utility.round((SharedPref.getInstance().getIntVlue(getActivity(), user_rating)), (SharedPref.getInstance().getIntVlue(getActivity(), user_rating_count)));

            if (rating.compareTo(BigDecimal.ZERO) > 0) {
                tvRating.setVisibility(View.VISIBLE);
                tvRating.setText("User Rating " + rating);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateactivity1();
    }

    private void updateactivity1() {

        try {
            ArrayList<SkillDo> skillDoArrayList = null;
            try {

                skillDoArrayList = new ArrayList<>();
                String jsonString = SharedPref.getInstance().getStringVlue(getActivity(), skills);
                JSONArray jsonArray = new JSONArray(jsonString);

                if (jsonArray != null && jsonArray.length() > 0) {
                    skillDoArrayList.clear();
                    for (int j = 0; j < jsonArray.length(); j++) {
                        SkillDo skillDo = new SkillDo();
                        skillDo.setActivty(jsonArray.optJSONObject(j).optString("activity"));
                        skillDoArrayList.add(skillDo);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e("SkillDo().size()", ":" + skillDoArrayList.size());


            activity_one.setVisibility(View.GONE);
            activity_two.setVisibility(View.GONE);
            activity_three.setVisibility(View.GONE);
            activity_four.setVisibility(View.GONE);
            tvactivtyfive.setVisibility(View.GONE);
            tvactivtysix.setVisibility(View.GONE);

            for (int i = 0; i < skillDoArrayList.size(); i++) {

                SkillDo skillDo = skillDoArrayList.get(i);

                switch (i) {

                    case 0:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            activity_one.setVisibility(View.VISIBLE);
                            activity_one.setText(skillDo.getActivty());
                        } else {
                            activity_one.setVisibility(View.GONE);
                        }

                        break;
                    case 1:

                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            activity_two.setVisibility(View.VISIBLE);
                            activity_two.setText(skillDo.getActivty());
                        } else {
                            activity_two.setVisibility(View.GONE);
                        }


                        break;
                    case 2:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            activity_three.setVisibility(View.VISIBLE);
                            activity_three.setText(skillDo.getActivty());
                        } else {
                            activity_three.setVisibility(View.GONE);
                        }


                        break;
                    case 3:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            activity_four.setVisibility(View.VISIBLE);
                            activity_four.setText(skillDo.getActivty());
                        } else {
                            activity_four.setVisibility(View.GONE);
                        }

                        break;


                    case 4:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvactivtyfive.setVisibility(View.VISIBLE);
                            tvactivtyfive.setText(skillDo.getActivty());
                        } else {
                            tvactivtyfive.setVisibility(View.GONE);
                        }

                        break;


                    case 5:
                        if (Utility.isNullCheck(skillDo.getActivty())) {
                            tvactivtysix.setVisibility(View.VISIBLE);
                            tvactivtysix.setText(skillDo.getActivty());
                        } else {
                            tvactivtysix.setVisibility(View.GONE);
                        }

                        break;

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), email))) {
            tvEmail.setText("" + SharedPref.getInstance().getStringVlue(getActivity(), email));
        }


        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), badge))) {

            tvProfile.setText(SharedPref.getInstance().getStringVlue(getActivity(), badge));
            tvProfile.setVisibility(View.GONE);
            badgeImage.setImageResource(R.drawable.certified);
            //Utility.updateBadge(SharedPref.getInstance().getStringVlue(getActivity(),badge),badgeImage);
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), phoneno))) {

            tvPhoneno.setText("" + SharedPref.getInstance().getStringVlue(getActivity(), phoneno));
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), landline))) {

            tvLandLine.setVisibility(View.VISIBLE);
            tvLandLine.setText("" + SharedPref.getInstance().getStringVlue(getActivity(), landline));
        } else {
            tvLandLine.setVisibility(View.GONE);
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), address))) {
            tvAdderess.setText("" + SharedPref.getInstance().getStringVlue(getActivity(), address));
        }
    }


    public void call(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.System.canWrite(getActivity())) {
            new AlertDialog.Builder(getActivity())
                    .setMessage("You need to grant access to make a call permission" )
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Log.e("MainActivity", "error starting permission intent", e);
                            }
                        }
                    })
                    .show();
            return;
        }

        startActivity(callIntent);
    }

    private void updateactivity() {

        String jsonArray = SharedPref.getInstance().getStringVlue(getActivity(), skills);

        Log.e(TAG, ":" + jsonArray);

      /*  try {

            JSONArray activity = new JSONArray(jsonArray);

            for (int i = 0; i < activity.length(); i++) {

                JSONObject obj = activity.getJSONObject(i);

                if (i == 0) {

                    if (Utility.isNullCheck(obj.optString("activity"))) {
                        activity_one.setText(obj.optString("activity"));
                    }

                   *//* if (Utility.isNullCheck(obj.optString("level"))) {

                        int rate = Integer.parseInt(obj.optString("level"));

                        switch (rate) {

                            case 1:
                                skill_act_one.setImageResource(R.drawable.one_star);
                                break;
                            case 2:
                                skill_act_one.setImageResource(R.drawable.two_star);
                                break;
                            case 3:
                                skill_act_one.setImageResource(R.drawable.three_star);
                                break;
                        }
                    }*//*
                }

                if (i == 1) {


                    if (Utility.isNullCheck(obj.optString("activity"))) {
                        activity_two.setText(obj.optString("activity"));
                    }

                   *//*  if (Utility.isNullCheck(obj.optString("level"))) {

                        int rate = Integer.parseInt(obj.optString("level"));

                        switch (rate) {

                            case 1:
                                skill_act_two.setImageResource(R.drawable.one_star);
                                break;
                            case 2:
                                skill_act_two.setImageResource(R.drawable.two_star);
                                break;
                            case 3:
                                skill_act_two.setImageResource(R.drawable.three_star);
                                break;
                        }
                    }*//*

                }

                if (i == 2) {


                    if (Utility.isNullCheck(obj.optString("activity"))) {
                        activity_three.setText(obj.optString("activity"));
                    }

                    *//*if (Utility.isNullCheck(obj.optString("level"))) {

                        int rate = Integer.parseInt(obj.optString("level"));

                        switch (rate) {

                            case 1:
                                skill_act_three.setImageResource(R.drawable.one_star);
                                break;
                            case 2:
                                skill_act_three.setImageResource(R.drawable.two_star);
                                break;
                            case 3:
                                skill_act_three.setImageResource(R.drawable.three_star);
                                break;
                        }
                    }*//*


                }

                if (i == 3) {


                    if (Utility.isNullCheck(obj.optString("activity"))) {
                        activity_four.setText(obj.optString("activity"));
                    }

                   *//* if (Utility.isNullCheck(obj.optString("level"))) {

                        int rate = Integer.parseInt(obj.optString("level"));

                        switch (rate) {

                            case 1:
                                skill_act_four.setImageResource(R.drawable.one_star);
                                break;
                            case 2:
                                skill_act_four.setImageResource(R.drawable.two_star);
                                break;
                            case 3:
                                skill_act_four.setImageResource(R.drawable.three_star);
                                break;
                        }
                    }*//*
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), email))) {
            tvEmail.setText("Email: " + SharedPref.getInstance().getStringVlue(getActivity(), email));
        }


        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), badge))) {

            tvProfile.setText(SharedPref.getInstance().getStringVlue(getActivity(), badge));
            tvProfile.setVisibility(View.GONE);
            badgeImage.setImageResource(R.drawable.certified);
            //Utility.updateBadge(SharedPref.getInstance().getStringVlue(getActivity(),badge),badgeImage);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult");
        fillDataonView();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }


    protected void initView(View view) {
        tvPersonNames = (TextView) view.findViewById(R.id.tvPersonNames);
        tvPersonNames.setShadowLayer(10, 0, 0, ContextCompat.getColor(getActivity(), R.color.black));
        tvage = (CustomTextView) view.findViewById(R.id.tvEarly);
        tvDistace = (TextView) view.findViewById(R.id.tvDistace);
        tvRating = (CustomTextView) view.findViewById(R.id.tvRating);
        txt_abotme = (CustomTextView) view.findViewById(R.id.tvBuddyDetails);
        badgeImage = (ImageView) view.findViewById(R.id.imageView4);
        tvProfile = (CustomTextView) view.findViewById(R.id.tvProfile);
        profile = (ImageView) view.findViewById(R.id.imageView3);
        activity_one = (CustomTextView) view.findViewById(R.id.tvSquash);
        activity_two = (CustomTextView) view.findViewById(R.id.tvParkour);
        activity_three = (CustomTextView) view.findViewById(R.id.tvYoga);
        activity_four = (CustomTextView) view.findViewById(R.id.tvRockClimbing);
        tvactivtyfive = (CustomTextView) view.findViewById(R.id.tv_act_five);
        tvactivtysix = (CustomTextView) view.findViewById(R.id.tv_act_six);
        tvEmail = (CustomTextView) view.findViewById(R.id.tvEmail);
        tvAdderess = (CustomTextView) view.findViewById(R.id.tvAddress);
        tvPhoneno = (CustomTextView) view.findViewById(R.id.tvphoneno);
        tvLandLine = (CustomTextView) view.findViewById(R.id.tvLandLine);
        btnEditAccount = (CustomButton) view.findViewById(R.id.btnEditAccount);
        btnEditAccount.setOnClickListener(this);
        tvPhoneno.setOnClickListener(this);
        tvLandLine.setOnClickListener(this);
        tvAdderess.setOnClickListener(this);
        tvEmail.setOnClickListener(this);
        top_lay = (RelativeLayout) view.findViewById(R.id.lay);
        top_lay.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvphoneno:
                if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), phoneno))) {
                    call(SharedPref.getInstance().getStringVlue(getActivity(), phoneno));
                }
                break;
            case R.id.tvLandLine:
                if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), landline))) {
                    call(SharedPref.getInstance().getStringVlue(getActivity(), landline));
                }
                break;
            case R.id.btnEditAccount:
                Intent intent = new Intent(getActivity(), BusinessProfileEdit.class);
                startActivityForResult(intent, 0);
                //startActivity(new Intent(getActivity(), BusinessProfileEdit.class));
                break;
            case R.id.tvAddress:

                if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), address))) {
                    try {
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + SharedPref.getInstance().getStringVlue(getActivity(), address));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }catch (ActivityNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.tvEmail:
                if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), email))) {
                    AppConstants.businessShareMailWithUs(getActivity(), SharedPref.getInstance().getStringVlue(getActivity(), email));
                }
                break;
            default:
                break;
        }
    }
}