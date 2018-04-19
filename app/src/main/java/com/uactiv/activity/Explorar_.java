package com.uactiv.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.Buddyup_Fragment;
import com.uactiv.fragment.Pickup_Fragment;
import com.uactiv.location.GPSTracker;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Explorar_ extends Fragment implements View.OnClickListener, AppConstants.urlConstants, AppConstants.SharedConstants, ResponseListener {

    View view;
    private ConstraintLayout buddyup_layout, pickup_layout, rewards_layout, mypickup;
    private CustomTextView tv_cityname;
    private FrameLayout frame;
    ImageView imageView17s, imageView15;
    Bundle bn;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String cityName;
    GPSTracker mGpsTracker;
    Boolean ischange = false;
    Geocoder geocoder;
    List<Address> addresses;
    GPSTracker tracker;
    String cityNamelocation;
    List<String> cityArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_explorar_, null);
        setUI();
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        cityArray = new ArrayList();
        cityArray.add("Mumbai");
        cityArray.add("Pune");
        cityArray.add("Delhi");
        cityArray.add("Gurgaon");
        cityArray.add("Choose from Location");
        tracker = new GPSTracker(getActivity());
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "setfromGPS"))) {

            if (pref.getString("cityname", null) != null) {
                if (tracker.canGetLocation() && SharedPref.getInstance().getStringVlue(getActivity(), "setfromGPS").equalsIgnoreCase("true")) {
                    try {
                        try {
                            addresses = geocoder.getFromLocation(tracker.getLatitude(), tracker.getLongitude(), 1);
                            cityNamelocation = addresses.get(0).getLocality();
                            editor.putString("cityname", cityNamelocation);
                            SharedPref.getInstance().setSharedValue(getActivity(), "lat", String.valueOf(tracker.getLatitude()));
                            SharedPref.getInstance().setSharedValue(getActivity(), "lang", String.valueOf(tracker.getLongitude()));
                            tv_cityname.setText(cityNamelocation);
                            for (int i = 0; i < cityArray.size(); i++) {
                                if (!cityNamelocation.equalsIgnoreCase(cityArray.get(i))) {
                                    imageView15.setImageResource(R.drawable.uact_new);
                                }
                            }
                            Log.d("cityname", cityNamelocation);
                            tracker.stopUsingGPS();
                        } catch (IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (pref.getString("cityname", null) != null) {

                        cityName = pref.getString("cityname", null);


                        if (cityName.equalsIgnoreCase("Mumbai")) {
                            tv_cityname.setText(cityName);
                            imageView15.setImageResource(R.drawable.mumbai);
                        } else if (cityName.equalsIgnoreCase("Pune")) {
                            tv_cityname.setText(cityName);
                            imageView15.setImageResource(R.drawable.pune);
                        } else if (cityName.equalsIgnoreCase("delhi")) {
                            tv_cityname.setText(cityName);
                            imageView15.setImageResource(R.drawable.delhi);
                        } else if (cityName.equalsIgnoreCase("Gurgaon")) {
                            tv_cityname.setText(cityName);
                            imageView15.setImageResource(R.drawable.gruru);
                        }

                        Log.d("currcity", cityName);
                    } else {
                        if (!checkLocationisAllow()) {
                            setalertDialog();
                        }


                    }
                }

            } else {
                if (tracker.canGetLocation()) {
                    try {
                        try {
                            addresses = geocoder.getFromLocation(tracker.getLatitude(), tracker.getLongitude(), 1);
                            cityNamelocation = addresses.get(0).getLocality();
                            editor.putString("cityname", cityNamelocation);
                            SharedPref.getInstance().setSharedValue(getActivity(), "lat", String.valueOf(tracker.getLatitude()));
                            SharedPref.getInstance().setSharedValue(getActivity(), "lang", String.valueOf(tracker.getLongitude()));
                            tv_cityname.setText(cityNamelocation);
                            for (int i = 0; i < cityArray.size(); i++) {
                                if (!cityNamelocation.equalsIgnoreCase(cityArray.get(i))) {
                                    imageView15.setImageResource(R.drawable.uact);
                                }

                            }
                            Log.d("cityname", cityNamelocation);
                            tracker.stopUsingGPS();
                        } catch (IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }


        tracker.stopUsingGPS();
        return view;


    }

    public void setUI() {
        buddyup_layout = (ConstraintLayout) view.findViewById(R.id.buddyup_layout);
        //pickup_layout = (ConstraintLayout) view.findViewById(R.id.pickup_layouts);
        mypickup = (ConstraintLayout) view.findViewById(R.id.mypickup);
        frame = (FrameLayout) view.findViewById(R.id.frame);
        frame.setOnClickListener(this);
        mypickup.setOnClickListener(this);
        rewards_layout = (ConstraintLayout) view.findViewById(R.id.rewards_layout);
        tv_cityname = (CustomTextView) view.findViewById(R.id.tv_cityname);
        imageView17s = (ImageView) view.findViewById(R.id.imageView17s);
        imageView15 = (ImageView) view.findViewById(R.id.imageView15);
        imageView15.setImageResource(R.drawable.mumbai);
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = pref.edit();
        imageView17s.setOnClickListener(this);
        buddyup_layout.setOnClickListener(this);

        //pickup_layout.setOnClickListener(this);
        rewards_layout.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (tracker != null) {
            tracker.stopUsingGPS();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tracker != null) {
            tracker.stopUsingGPS();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("destory", "destory");
        if (tracker != null) {
            tracker.stopUsingGPS();
            getActivity().stopService(new Intent(getActivity(), GPSTracker.class));
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

    private boolean checkLocationisAllow() {
        String permission = android.Manifest.permission.ACCESS_COARSE_LOCATION;
        int res = getActivity().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public void setalertDialog() {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_listview, null);
        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        ListView alert_list = (ListView) view.findViewById(R.id.alert_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, cityArray);
        alert_list.setAdapter(adapter);
        alert_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    imageView15.setImageResource(R.drawable.mumbai);
                    tv_cityname.setText(cityArray.get(i) + "");
                    editor.putString("cityname", tv_cityname.getText().toString());
                    SharedPref.getInstance().setSharedValue(getActivity(), "lat", "19.0600");
                    SharedPref.getInstance().setSharedValue(getActivity(), "lang", "72.8900");
                    SharedPref.getInstance().setSharedValue(getActivity(), "setfromGPS", "false");
                    ischange = true;

                    editor.commit();
                } else if (i == 1) {
                    imageView15.setImageResource(R.drawable.pune);
                    tv_cityname.setText(cityArray.get(i) + "");
                    editor.putString("cityname", tv_cityname.getText().toString());
                    SharedPref.getInstance().setSharedValue(getActivity(), "lat", "18.5308");
                    SharedPref.getInstance().setSharedValue(getActivity(), "lang", "73.8475");
                    SharedPref.getInstance().setSharedValue(getActivity(), "setfromGPS", "false");
                    ischange = true;

                    editor.commit();
                } else if (i == 2) {
                    imageView15.setImageResource(R.drawable.delhi);
                    tv_cityname.setText(cityArray.get(i) + "");
                    editor.putString("cityname", tv_cityname.getText().toString());
                    SharedPref.getInstance().setSharedValue(getActivity(), "lat", "28.6687");
                    SharedPref.getInstance().setSharedValue(getActivity(), "lang", "77.2303");
                    SharedPref.getInstance().setSharedValue(getActivity(), "setfromGPS", "false");
                    ischange = true;
                    editor.commit();
                } else if (i == 3) {
                    imageView15.setImageResource(R.drawable.gruru);
                    tv_cityname.setText(cityArray.get(i) + "");
                    SharedPref.getInstance().setSharedValue(getActivity(), "lat", "28.4511");
                    SharedPref.getInstance().setSharedValue(getActivity(), "lang", "77.0327");
                    editor.putString("cityname", tv_cityname.getText().toString());
                    SharedPref.getInstance().setSharedValue(getActivity(), "setfromGPS", "false");
                    ischange = true;
                    editor.commit();
                } else if (i == 4) {
                    mGpsTracker = new GPSTracker(getActivity());
                    if (mGpsTracker.canGetLocation()) {
                        SharedPref.getInstance().setSharedValue(getActivity(), "setfromGPS", "true");
                    } else {
                        showSettingsAlert(1);
                    }

                }
                SharedPref.getInstance().setSharedValue(getActivity(), radius_limit, String.valueOf("200"));
                Buddyup_Fragment.buddyList.clear();
                Buddyup_Fragment.search_buddyList.clear();
                Fragment fragment = new Explorar_();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
                dialog.dismiss();

            }
        });


    }

    private void updateSettings(String gender, String limit) {

        if (Utility.isConnectingToInternet(getActivity())) {

            try {
                Map<String, String> param = new HashMap<>();
                param.put("search_limit", "" + limit);
                param.put("gender_pref", gender);
                param.put("isreceive_request", "1");
                param.put("isreceive_notification", "1");

                if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), facebook_link))) {
                    param.put("facebook_link", "1");
                } else {
                    param.put("facebook_link", "0");
                }
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));

                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + updatesetting, param, this, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(getActivity());
        }
    }

    private void updateLocationToServer() {

        if (Utility.isConnectingToInternet(getActivity())) {

            if (mGpsTracker.canGetLocation()) {

                if (Utility.ischeckvalidLocation(mGpsTracker)) {

                    Animation fade1 = AnimationUtils.loadAnimation(getActivity(), R.anim.flip);
                    Map<String, String> params = new HashMap<String, String>();
                    if (!AppConstants.isGestLogin(getActivity())) {
                        params.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), "UserId"));
                    } else {
                        params.put("iduser", "0");
                    }

                    params.put("latitude", "" + mGpsTracker.getLatitude());
                    params.put("longitude", "" + mGpsTracker.getLongitude());
                    RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + updatelocation, params, this, 5);
                    fade1.cancel();
                } else {
                    mGpsTracker.showInvalidLocationAlert();
                }
            } else {
                showSettingsAlert(1);
            }
        } else {
            Utility.showInternetError(getActivity());
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.buddyup_layout:
                Fragment fragment = new Buddyup_Fragment();
                Bundle bs = new Bundle();
                bs.putString("setting", "setting");
                bs.putBoolean("ischange", ischange);

                if (fragment != null) {
                    fragment.setArguments(bs);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.pickup_Layout:
                Fragment fragments = new Pickup_Fragment();
                Bundle b = new Bundle();
                b.putString("frompickup", "frompickup");
                fragments.setArguments(b);
                if (fragments != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragments).addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
            case R.id.rewards_layout:
                Utility.setEventTracking(getActivity(), "Soccer League", "open in app browser");
                Intent url = new Intent(getActivity(), In_App_Browser.class);
                url.putExtra("url", "http://uactiv.com/upgrade/sst/cms/");
                url.putExtra("title", "Super Soccer Tournament");
                getActivity().startActivity(url);
                break;

            case R.id.mypickup:
                Fragment fragmentrs = new Pickup_Fragment();
                if (fragmentrs != null) {
                    Bundle b2 = new Bundle();
                    b2.putString("fromexplorar", "exp");
                    fragmentrs.setArguments(b2);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragmentrs).addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;

            case R.id.imageView17s:
                Fragment fragmentrs1 = new Pickup_Fragment();
                if (fragmentrs1 != null) {
                    Bundle b2 = new Bundle();
                    b2.putString("fromexplorar", "exp");
                    fragmentrs1.setArguments(b2);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragmentrs1).addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;

            case R.id.frame:
                setalertDialog();
                break;
        }

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
                break;
            case 1:
                Log.d("aaaaaaaa", new Gson().toJson(jsonObject));
                // SharedPref.getInstance().setSharedValue(getActivity(), "GEST_GENDER", "Both");
                SharedPref.getInstance().setSharedValue(getActivity(), radius_limit, String.valueOf("200"));
                Buddyup_Fragment.buddyList.clear();
                Buddyup_Fragment.search_buddyList.clear();
                Intent main = new Intent(getActivity(), MainActivity.class);
                startActivity(main);
                break;

        }

    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {

    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }



/*
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            }

        } else {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(Explorar_.this);
            builder1.setMessage("Are you sure you want to exit the app?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                            homeIntent.addCategory(Intent.CATEGORY_HOME);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(homeIntent);
                            dialog.cancel();

                        }
                    });
            builder1.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();


        }


    }*/


}
