package com.uactiv.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uactiv.R;
import com.uactiv.application.UActiveApplication;
import com.uactiv.controller.CreatePickupNotifier;
import com.uactiv.controller.ResponseListener;
import com.uactiv.location.GPSTracker;
import com.uactiv.model.BusinessLocationDo;
import com.uactiv.model.BusinessLocationList;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.CustomAutoCompleteTextView;
import com.uactiv.utils.Finisher;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by buvaneswaran on 12/14/2015
 */
public class BusinessLocationMap extends FragmentActivity implements GoogleMap.OnCameraChangeListener,
        AppConstants.SharedConstants, ResponseListener, View.OnClickListener,Finisher {

    private final int REQUEST_CODE_GPS_TRACKER = 1;
    private final int FLAG_GET_BUSINESS_LOCATION = 100;
    private LatLng mLatLng;
    private String mPickupAddress;
    private AutoCompleteTextView atvPlaces;
    private String skill_name = null;
    private boolean isBookingOpen = false;
    private Intent intent = null;
    private ArrayList<BusinessLocationDo> businessLocationDoArrayList = null;
    private CircleImageView mProfileView = null;
    private TextView mTvName = null;
    private TextView mTvKmAway = null;
    private RelativeLayout cardLay = null;
    private CustomTextView btnDone = null;
    private boolean isLoginBusinessLocation = false;
    private BusinessLocationDo tempBusinessDo = null;
    private GPSTracker mGpsTracker;
    private String TAG = BusinessLocationMap.class.getSimpleName();
    private GoogleMap googleMap;
    private double Latitude, Longitude;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    private int clickedPosition = 0;
    private List<Marker> markers = new ArrayList<>();
    public static CreatePickupNotifier createPickupNotifier = null;
    public static Finisher finish;
    List<Address> addresses;
    Geocoder geocoder;
    private double Lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickuppoint);
        finish=this;
        mGpsTracker = new GPSTracker(BusinessLocationMap.this);
        geocoder = new Geocoder(this, Locale.getDefault());
        intent = getIntent();
        bindActivity();
    }


    /**
     * bind views
     */

    private void bindActivity() {
        atvPlaces = (CustomAutoCompleteTextView) findViewById(R.id.atv_places);
        btnDone = (CustomTextView) findViewById(R.id.btnDone);
        if (intent != null) {
            btnDone.setVisibility(View.GONE);
            skill_name = intent.getStringExtra("selectedSkill");
            isBookingOpen = intent.getBooleanExtra("isBookingOpen", false);
            atvPlaces.setVisibility(View.GONE);
            Utility.setScreenTracking(BusinessLocationMap.this, "Suggested locations on map");
        }

        if (!(mGpsTracker.canGetLocation())) {
            showSettingsAlert();
        }

        ((LinearLayout) findViewById(R.id.search_lay)).setVisibility(View.VISIBLE);
        cardLay = (RelativeLayout) findViewById(R.id.ly_pickup_events);
        cardLay.setVisibility(View.GONE);
        ((TextView) findViewById(R.id.tv_times)).setVisibility(View.GONE);
        mProfileView = (CircleImageView) findViewById(R.id.img_logo);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvKmAway = (TextView) findViewById(R.id.tv_time);
        ((ImageView) findViewById(R.id.map_pin)).setVisibility(View.GONE);

        Button button = (Button) findViewById(R.id.btn_done);
        button.setVisibility(View.GONE);

        atvPlaces.setEnabled(false);
        atvPlaces.setThreshold(1);

        btnDone.setOnClickListener(this);
        ((TextView) findViewById(R.id.back)).setOnClickListener(this);
        findViewById(R.id.ly_pickup_events).setOnClickListener(this);

        loadMapView();
        getBusinessLocations();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LatLng latLng = cameraPosition.target;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mGpsTracker != null) {
            mGpsTracker.stopUsingGPS();
        }
    }

    /**
     * get business locations from api
     */
    private void getBusinessLocations() {

        if (Utility.isConnectingToInternet(BusinessLocationMap.this)) {
            if (mGpsTracker.canGetLocation()) {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("latitude", String.valueOf(mGpsTracker.getLatitude()));
                parameters.put("longitude", String.valueOf(mGpsTracker.getLongitude()));
                parameters.put("activity", skill_name);
                try {
                    addresses = geocoder.getFromLocation(mGpsTracker.getLatitude(), mGpsTracker.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                parameters.put("city", addresses.get(0).getLocality());
                RequestHandler.getInstance().stringRequestVolley(BusinessLocationMap.this,
                        AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(BusinessLocationMap.this, isStaging)) + AppConstants.urlConstants.getSuggestedLocations,
                        parameters,
                        this,
                        FLAG_GET_BUSINESS_LOCATION);
            } else {
                showSettingsAlert();
            }
        } else {
            Utility.showInternetError(BusinessLocationMap.this);
        }
    }


    /**
     * check login user is business user
     *
     * @param businessUserId
     * @return
     */

    private boolean isLoggedInBusinessLocation(String businessUserId) {
        return SharedPref.getInstance().getStringVlue(BusinessLocationMap.this, userId).equalsIgnoreCase(businessUserId);
    }


    /**
     * show gps settings alert to enable
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BusinessLocationMap.this);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE_GPS_TRACKER);
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


    /**
     * load map view
     */
    private void loadMapView() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps)).getMap();
            googleMap.setOnMarkerClickListener(onMarkerClickListener);
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }

        }
    }


    /***
     * add markets to maps
     */

    private void addMarkersToMap() {

        if (businessLocationDoArrayList != null && businessLocationDoArrayList.size() > 0) {
            for (int i = 0; i < businessLocationDoArrayList.size(); i++) {
                LatLng ll = new LatLng(businessLocationDoArrayList.get(i).getLatitude(), businessLocationDoArrayList.get(i).getLongitude());
                BitmapDescriptor bitmapMarker;
                bitmapMarker = BitmapDescriptorFactory.fromResource(R.drawable.green_mappin);
                Marker marker = googleMap.addMarker(new MarkerOptions().position(ll).title(businessLocationDoArrayList.get(i).getBusiness_name())
                        .snippet(businessLocationDoArrayList.get(i).getBusiness_name()).icon(bitmapMarker));
                markers.add(marker);
                mHashMap.put(marker, i);
            }


            if (mGpsTracker.canGetLocation() && Utility.ischeckvalidLocation(mGpsTracker)) {
                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(new LatLng(mGpsTracker.getLatitude(), mGpsTracker.getLongitude())) // Sets the center of the map to location user
                        .zoom(10)                   // Sets the zoom
                        .bearing(90)// Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
            } else {
                if (markers != null && markers.size() > 0) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }
                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25, 25, 5);
                    googleMap.animateCamera(cu);
                }
            }


        } else {
            //Show dialog to redirect to user location map.
            moveToUserLocationMap();
        }


    }

    /**
     * navigate user to select own location if there is no location for current location or activity
     */
    private void moveToUserLocationMap() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(BusinessLocationMap.this);
        alertDialog.setTitle("Choose Location");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("No suggested locations available. Choose from Map?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(BusinessLocationMap.this, PickChoose.class));
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                return;
            }
        });
        alertDialog.show();
    }


    /**
     * api responses
     *
     * @param successResponse response value
     * @param flag            task id
     */

    @Override
    public void successResponse(String successResponse, int flag) {
        switch (flag) {
            case FLAG_GET_BUSINESS_LOCATION:
                googleMap.clear();
                if (!TextUtils.isEmpty(successResponse)) {
                    BusinessLocationList businessLocationList = Utility.getModelFromJson(successResponse, BusinessLocationList.class);
                    businessLocationDoArrayList = businessLocationList.getBusinessLocationsList();
                    addMarkersToMap();
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GPS_TRACKER)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!(mGpsTracker.canGetLocation())) {
                        showSettingsAlert();
                    }
                }
            }, 3000);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDone:
                atvPlaces.setText(mPickupAddress);
                Utility.setEventTracking(BusinessLocationMap.this, "Suggested locations on map", "Click on Done button on suggested locations map view");
                if (mLatLng != null && mPickupAddress != null & tempBusinessDo != null) {
                    //Here 0 means not interested to book
                    //ere 1 means interested to book
                    if (isBookingOpen) {
                        if (isLoginBusinessLocation) {
                            if (CreatePickUp.createPickupNotifier != null) {
                                CreatePickUp.createPickupNotifier.mapViewNotifier(mLatLng, mPickupAddress, "0", tempBusinessDo.getIdbusiness(), 1);
                            } else if (BuddyUpRequest.createPickupNotifier != null) {
                                BuddyUpRequest.createPickupNotifier.mapViewNotifier(mLatLng, mPickupAddress, "0", tempBusinessDo.getIdbusiness(), 1);
                            }
                            finish();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BusinessLocationMap.this);
                            builder.setTitle(UActiveApplication.mContext.getResources().getString(R.string.dialog_booking_title));
                            builder.setMessage(getResources().getString(R.string.dialog_msg_confirm_booking));
                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (CreatePickUp.createPickupNotifier != null) {
                                        CreatePickUp.createPickupNotifier.mapViewNotifier(mLatLng, mPickupAddress, "1", tempBusinessDo.getIdbusiness(), 1);
                                    } else if (BuddyUpRequest.createPickupNotifier != null) {
                                        BuddyUpRequest.createPickupNotifier.mapViewNotifier(mLatLng, mPickupAddress, "1", tempBusinessDo.getIdbusiness(), 1);
                                    }
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (CreatePickUp.createPickupNotifier != null) {
                                        CreatePickUp.createPickupNotifier.mapViewNotifier(mLatLng, mPickupAddress, "0", tempBusinessDo.getIdbusiness(), 1);
                                    } else if (BuddyUpRequest.createPickupNotifier != null) {
                                        BuddyUpRequest.createPickupNotifier.mapViewNotifier(mLatLng, mPickupAddress, "0", tempBusinessDo.getIdbusiness(), 1);
                                    }
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    } else {
                        if (CreatePickUp.createPickupNotifier != null) {
                            CreatePickUp.createPickupNotifier.mapViewNotifier(mLatLng, mPickupAddress, "0", tempBusinessDo.getIdbusiness(), 1);
                        } else if (BuddyUpRequest.createPickupNotifier != null) {
                            BuddyUpRequest.createPickupNotifier.mapViewNotifier(mLatLng, mPickupAddress, "0", tempBusinessDo.getIdbusiness(), 1);
                        }
                        finish();
                    }
                }
                break;
            case R.id.back:
                Utility.setEventTracking(getApplicationContext(), "Suggested locations on map", "click on the back button on suggested locations map view");
                if (mGpsTracker != null) {
                    mGpsTracker.stopUsingGPS();
                }

                onBackPressed();
                break;
            case R.id.ly_pickup_events:
                Utility.setEventTracking(getApplicationContext(), "Suggested locations on map", "click on the location name on suggested locations map view");

                Intent businessDetailIntent = new Intent(BusinessLocationMap.this, BusinessDetailsActivity.class);
                businessDetailIntent.putExtra("view", true);
                businessDetailIntent.putExtra("userid", businessLocationDoArrayList.get(clickedPosition).getIdbusiness());
                if (mLatLng != null && mPickupAddress != null & tempBusinessDo != null) {
                    businessDetailIntent.putExtra("latitude", mLatLng.latitude);
                    businessDetailIntent.putExtra("longitude", mLatLng.longitude);
                    businessDetailIntent.putExtra("mPickupAddress", mPickupAddress);
                    businessDetailIntent.putExtra("tempBusinessDo", tempBusinessDo.getIdbusiness());
                }

                businessDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(businessDetailIntent);
                //finish();
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                break;
        }
    }

    private GoogleMap.OnMarkerClickListener onMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            int position = mHashMap.get(marker);
            Utility.setEventTracking(BusinessLocationMap.this, "Suggested locations on map", "Click on the pin on map on suggested locations pins");

            BusinessLocationDo businessLocationDo = businessLocationDoArrayList.get(position);
            tempBusinessDo = businessLocationDo;
            clickedPosition = position;
            Log.e("position", "" + position);
            mLatLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            Log.e("mLatLng", String.valueOf(mLatLng));
            mPickupAddress = marker.getTitle();
            btnDone.setVisibility(View.VISIBLE);
            atvPlaces.setText(mPickupAddress);
            if (businessLocationDo != null) {
                cardLay.setVisibility(View.VISIBLE);
                Log.d("getIdbusiness ", "" + businessLocationDo.getIdbusiness());
                isLoginBusinessLocation = isLoggedInBusinessLocation(businessLocationDo.getIdbusiness());
                Utility.setImageUniversalLoader(BusinessLocationMap.this, businessLocationDo.getImage_url(), mProfileView);
                int away = (int) Double.parseDouble(businessLocationDo.getAwayFrom());
                mTvKmAway.setText("" + away + " km away");
                mTvName.setText("" + businessLocationDo.getBusiness_name().substring(0, 1).toUpperCase() + businessLocationDo.getBusiness_name().substring(1));
            } else {
                cardLay.setVisibility(View.GONE);
            }
            return false;
        }
    };



    @Override
    public void myfinish() {
        finish();
    }
}
