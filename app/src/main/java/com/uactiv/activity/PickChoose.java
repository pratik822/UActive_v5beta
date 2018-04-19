package com.uactiv.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.uactiv.R;
import com.uactiv.location.GPSTracker;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

/**
 * Created by buvaneswaran on 12/14/2015.
 */
public class PickChoose extends FragmentActivity {

    private String TAG = getClass().getSimpleName();
    private GPSTracker gps;
    private GoogleMap googleMap;
    private double Latitude, Longitude;
    private LatLng latLngs;
    private String pickupAddress;
    private AutoCompleteTextView atvPlaces;
    private Intent intent = null;
    private boolean isFromEdit = false;
    static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 9;
    boolean addressflag = false;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickuppoint);
        button = (Button) findViewById(R.id.btn_done);
        button.setVisibility(View.GONE);
        intent = getIntent();

        if (intent != null) {
            isFromEdit = intent.getBooleanExtra("isFromEdit", false);
        }

        TextView textView = (TextView) findViewById(R.id.back);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

       /* if(!isFromEdit) {

            if (CreatePickUp.createPickupNotifier != null) {
                textView.setText("Select your Pick up point");

            } else if (BuddyUpRequest.createPickupNotifier != null) {
                textView.setText("Select your Buddy up point");
            }
        }else {
            textView.setText("Select your business Location");
        }*/


        ((CustomTextView) findViewById(R.id.btnDone)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickupAddress = atvPlaces.getText().toString().replaceAll(",null", "");

                if (!isFromEdit) {
                    if (CreatePickUp.createPickupNotifier != null) {
                        CreatePickUp.createPickupNotifier.mapViewNotifier(latLngs, pickupAddress, "0", "", 0);

                    } else if (BuddyUpRequest.createPickupNotifier != null) {
                        BuddyUpRequest.createPickupNotifier.mapViewNotifier(latLngs, pickupAddress, "0", "", 0);
                    }
                } else {
                    BusinessProfileEdit.addressPicker.mapViewNotifier(latLngs, pickupAddress, "0", "", 0);
                }

                if(gps!=null){
                    gps.stopUsingGPS();
                }
                finish();
            }
        });


        atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
        atvPlaces.setThreshold(1);

        atvPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addressflag = true;
                    Intent intent = null;
                    LatLngBounds latLngBounds;
                    if (googleMap != null) {
                        latLngBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                        if (latLngBounds != null) {
                            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).setBoundsBias(latLngBounds)
                                    .build(PickChoose.this);
                        } else {
                            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(PickChoose.this);
                        }

                    } else {
                        intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                .build(PickChoose.this);
                    }

                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        loadingMapview();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                pickupAddress = "" + place.getName() + "," + place.getAddress();
                latLngs = place.getLatLng();
                // animateCamera(place.getLatLng());
                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(new LatLng(latLngs.latitude, latLngs.longitude)) // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)// Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
                atvPlaces.setText("" + place.getName() + "," + place.getAddress());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    private void animateCamera(LatLng latLng) {
        googleMap.clear();
        CameraPosition cameraPosition1 = new CameraPosition.Builder()
                .target(latLng) // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)// Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
    }



    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.e("data", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Mapview initilize
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void loadingMapview() {
        gps = new GPSTracker(this);

        if (isFromEdit) {
            Latitude = intent.getDoubleExtra("latitude", 0.0);
            Longitude = intent.getDoubleExtra("longitude", 0.0);
        } else {
            //check if GPS enabled
            if (gps.canGetLocation()) {
                if (Utility.ischeckvalidLocation(gps)) {
                    Latitude = gps.getLatitude();
                    Longitude = gps.getLongitude();
                } else {
                    gps.showInvalidLocationAlert();
                }
            } else {
                gps.showSettingsAlert();
            }
        }

        try {
            initilizeMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.clear();
            CameraPosition cameraPosition1 = new CameraPosition.Builder()
                    .target(new LatLng(Latitude, Longitude)) // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)// Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));

            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener(){
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {

                    if (!addressflag) {
                        googleMap.clear();

                        LatLng latLng = googleMap.getCameraPosition().target;

                        try {
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(PickChoose.this, Locale.getDefault());
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            latLngs = latLng;

                            if (!address.isEmpty()) {
                                ((AutoCompleteTextView) findViewById(R.id.atv_places)).setText(address + "," + city + "," + state + "," + country);
                                if (postalCode != null) {
                                    ((AutoCompleteTextView) findViewById(R.id.atv_places)).append("," + postalCode + ".");
                                } else {
                                    ((AutoCompleteTextView) findViewById(R.id.atv_places)).append(".");
                                }
                            } else {
                                googleMap.clear();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        addressflag = false;
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
