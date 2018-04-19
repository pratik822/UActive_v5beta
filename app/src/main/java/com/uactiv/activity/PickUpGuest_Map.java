package com.uactiv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uactiv.R;
import com.uactiv.utils.WorkaroundMapFragment;

public class PickUpGuest_Map extends FragmentActivity {

	GoogleMap googleMap;
	ImageView imgBack;
	Intent intent =null;
	double lat ;
	double longi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pickup_guest_map);
		intent = getIntent();

		if(intent != null){
			lat = intent.getDoubleExtra("lati",0.0);
			longi = intent.getDoubleExtra("longi",0.0);
			Log.e("lat",":"+lat+"longi "+longi);
		}

		imgBack = (ImageView) findViewById(R.id.imgBack);
		imgBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});


		addGoogleMap();

		/*googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {

				if(gpsTracker.canGetLocation() && gpsTracker.getLocation() != null) {
					try {
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
								Uri.parse("http://maps.google.com/maps?saddr=" +gpsTracker.getLatitude()+","+gpsTracker.getLongitude() + "&daddr=" + marker.getPosition().latitude+","+marker.getPosition().longitude));
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				}else {
					gpsTracker.showSettingsAlert();
				}
				return false;
			}
		});*/



	}

	private void addGoogleMap() {

		googleMap = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.getUiSettings().setScrollGesturesEnabled(true);
		if (googleMap != null) {
			LatLng latLng = new LatLng(lat, longi);
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
			googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.green_mappin)));
		}


		/*((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
			@Override
			public void onTouch() {
				ScrollView1.requestDisallowInterceptTouchEvent(true);
				Intent intent = new Intent(PickUpGuest.this, PickUpGuest_Map.class);
				intent.putExtra("lati", lat);
				intent.putExtra("longi", longi);
				startActivity(intent);
			}
		});*/

	}

	/*private void addGoogleMap() {
		googleMap = ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.getUiSettings().setScrollGesturesEnabled(false);

		if (googleMap != null) {
			LatLng latLng = new LatLng(lat, longi);
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
			googleMap.addMarker(new MarkerOptions().position(latLng).icon(
			BitmapDescriptorFactory.fromResource(R.drawable.map_location_btn)));
		}



	}*/


	@Override
	public void onBackPressed()
	{
		// code here to show dialog
		super.onBackPressed();  // optional depending on your needs
	}
}
