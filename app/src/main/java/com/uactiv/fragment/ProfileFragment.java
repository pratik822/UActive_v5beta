package com.uactiv.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.activity.MainActivity;
import com.uactiv.activity.ProfileEdit;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

public class ProfileFragment extends Fragment implements OnClickListener, AppConstants.SharedConstants {

    TextView tvPersonNames, tvDistace, tvRating;
    // /   ImageView imgDistance;
    CustomButton rlayout;
    CustomTextView tvage = null;
    ImageView profile = null;
    CustomTextView activity_one = null;
    CustomTextView activity_two = null;
    CustomTextView activity_three = null;
    CustomTextView activity_four = null;
    CustomTextView txt_abotme = null;

    ImageView skill_act_one = null;
    ImageView skill_act_two = null;
    ImageView skill_act_three = null;
    ImageView skill_act_four = null;
    String TAG = getClass().getSimpleName();
    private CustomTextView tvProfile = null;
    private ImageView badgeImage = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profilepage, container, false);
        Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_USER_PROFILE);
        initView(view);

        fillDataonView();

        return view;
    }

    private void fillDataonView() {

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), firstname))) {
            tvPersonNames.setText(SharedPref.getInstance().getStringVlue(getActivity(), firstname) + " " + SharedPref.getInstance().getStringVlue(getActivity(), lastname));
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), image))) {

           /* Picasso.with(getActivity())
                    .load(SharedPref.getInstance().getStringVlue(getActivity(), image))
                    .into(profile);*/

            /*Picasso.with(getActivity()).load(SharedPref.getInstance().getStringVlue(getActivity(), image)).placeholder(R.drawable.ic_profile)
                    .fit().centerCrop().into(profile);*/


            //Utility.SetUrlImage(SharedPref.getInstance().getStringVlue(getActivity(), image), profile);

            Utility.setImageUniversalLoader(getActivity(), SharedPref.getInstance().getStringVlue(getActivity(), image), profile);


        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), age))) {


            Log.e("Agecalcualte", "profileupdate");
            Date birthday = null;
            try {
                birthday = AppConstants.sdf.parse(SharedPref.getInstance().getStringVlue(getActivity(), age));
                tvage.setText(Utility.calculateAge(birthday, getActivity(), false));
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        try {
            Log.e(about_yourself, SharedPref.getInstance().getStringVlue(getActivity(), about_yourself));

            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), about_yourself))) {
                txt_abotme.setVisibility(View.VISIBLE);
                txt_abotme.setText(StringEscapeUtils.unescapeJava(SharedPref.getInstance().getStringVlue(getActivity(), about_yourself)));
            } else {
                txt_abotme.setVisibility(View.GONE);
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        //update overall rating.

        try {

            //  Log.e("user_rating",""+SharedPref.getInstance().getIntVlue(getActivity(),user_rating));
            //  Log.e("user_rating_count",""+SharedPref.getInstance().getIntVlue(getActivity(),user_rating_count));

            // float overallrate = (float)((SharedPref.getInstance().getIntVlue(getActivity(),user_rating))/(SharedPref.getInstance().getIntVlue(getActivity(),user_rating_count)));

            BigDecimal rating = Utility.round((SharedPref.getInstance().getIntVlue(getActivity(), user_rating)), (SharedPref.getInstance().getIntVlue(getActivity(), user_rating_count)));

            if (rating.compareTo(BigDecimal.ZERO) > 0) {

                tvRating.setVisibility(View.VISIBLE);
                tvRating.setText("User Rating " + rating);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateactivity();
    }

    private void updateactivity() {

        String jsonArray = SharedPref.getInstance().getStringVlue(getActivity(), skills);

        Log.e(TAG, ":" + jsonArray);

        activity_one.setVisibility(View.GONE);
        activity_two.setVisibility(View.GONE);
        activity_three.setVisibility(View.GONE);
        activity_four.setVisibility(View.GONE);
        skill_act_one.setVisibility(View.GONE);
        skill_act_two.setVisibility(View.GONE);
        skill_act_three.setVisibility(View.GONE);
        skill_act_four.setVisibility(View.GONE);

        try {

            JSONArray activity = new JSONArray(jsonArray);

            for (int i = 0; i < activity.length(); i++) {

                JSONObject obj = activity.getJSONObject(i);

                if (i == 0) {

                    if (Utility.isNullCheck(obj.optString("activity"))) {
                        activity_one.setVisibility(View.VISIBLE);
                        activity_one.setText(obj.optString("activity"));
                    }

                    if (Utility.isNullCheck(obj.optString("level"))) {

                        int rate = Integer.parseInt(obj.optString("level"));
                        skill_act_one.setVisibility(View.VISIBLE);
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
                    }
                }

                if (i == 1) {


                    if (Utility.isNullCheck(obj.optString("activity"))) {
                        activity_two.setVisibility(View.VISIBLE);
                        activity_two.setText(obj.optString("activity"));
                    }

                    if (Utility.isNullCheck(obj.optString("level"))) {

                        int rate = Integer.parseInt(obj.optString("level"));
                        skill_act_two.setVisibility(View.VISIBLE);
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
                    }

                }

                if (i == 2) {


                    if (Utility.isNullCheck(obj.optString("activity"))) {
                        activity_three.setVisibility(View.VISIBLE);
                        activity_three.setText(obj.optString("activity"));
                    }

                    if (Utility.isNullCheck(obj.optString("level"))) {

                        int rate = Integer.parseInt(obj.optString("level"));
                        skill_act_three.setVisibility(View.VISIBLE);
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
                    }


                }

                if (i == 3) {
                    if (Utility.isNullCheck(obj.optString("activity"))) {
                        activity_four.setVisibility(View.VISIBLE);
                        activity_four.setText(obj.optString("activity"));
                    }

                    if (Utility.isNullCheck(obj.optString("level"))) {
                        int rate = Integer.parseInt(obj.optString("level"));
                        skill_act_four.setVisibility(View.VISIBLE);
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
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), badge))) {
            tvProfile.setText(SharedPref.getInstance().getStringVlue(getActivity(), badge));
            Utility.updateBadge(SharedPref.getInstance().getStringVlue(getActivity(), badge), badgeImage);
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
        rlayout = (CustomButton) view.findViewById(R.id.rlayout);
        tvDistace = (TextView) view.findViewById(R.id.tvDistace);
        tvRating = (CustomTextView) view.findViewById(R.id.tvRating);
        txt_abotme = (CustomTextView) view.findViewById(R.id.tvBuddyDetails);
        //imgDistance = (ImageView) view.findViewById(R.id.imgDistance);
        badgeImage = (ImageView) view.findViewById(R.id.imageView4);
        badgeImage.setOnClickListener(this);
        tvProfile = (CustomTextView) view.findViewById(R.id.tvProfile);
        profile = (ImageView) view.findViewById(R.id.imageView3);
        activity_one = (CustomTextView) view.findViewById(R.id.tvSquash);
        activity_two = (CustomTextView) view.findViewById(R.id.tvParkour);
        activity_three = (CustomTextView) view.findViewById(R.id.tvYoga);
        activity_four = (CustomTextView) view.findViewById(R.id.tvRockClimbing);
        skill_act_one = (ImageView) view.findViewById(R.id.img_threestar);

        activity_one.setVisibility(View.GONE);
        activity_two.setVisibility(View.GONE);
        activity_three.setVisibility(View.GONE);
        activity_four.setVisibility(View.GONE);

        skill_act_two = (ImageView) view.findViewById(R.id.img_onestar);
        skill_act_three = (ImageView) view.findViewById(R.id.img_twostar);
        skill_act_four = (ImageView) view.findViewById(R.id.img_rockstar);

      /*  tvDistace.setVisibility(View.GONE);
        imgDistance.setVisibility(View.GONE);
        tvRating.setVisibility(View.GONE);*/
        // onclick
        rlayout.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlayout:
                Utility.setEventTracking(getActivity(), "My profile screen", "Edit Button My profile from menu");
                Intent intent = new Intent(getActivity(), ProfileEdit.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.imageView4:
                Utility.setEventTracking(getActivity(), "My profile screen", "Badge button My profile from menu");
                final Dialog badgeDialog = new Dialog(getActivity(), R.style.Theme_Dialog);
                badgeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                badgeDialog.setContentView(R.layout.badge_details);
                RelativeLayout dismissalLayout = (RelativeLayout) badgeDialog.findViewById(R.id.mainLay);

                CustomTextView tvBuddyups = (CustomTextView) badgeDialog.findViewById(R.id.tvBuddyups);
                CustomTextView tvPickups = (CustomTextView) badgeDialog.findViewById(R.id.tvPickups);
                JSONObject mJsonObj;

                try {
                    mJsonObj = new JSONObject(((MainActivity) getActivity()).checkRatingJson);
                    tvBuddyups.setText(getString(R.string.buddyups) + " " + mJsonObj.optJSONObject("count").optString("buddyup"));
                    tvPickups.setText(getString(R.string.pickups) + " " + mJsonObj.optJSONObject("count").optString("pickup"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dismissalLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        badgeDialog.dismiss();
                        return false;
                    }
                });
                badgeDialog.setCanceledOnTouchOutside(true);
                badgeDialog.show();
                break;
        }
    }
}