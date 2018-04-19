package com.uactiv.controller;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by moorthy on 12/28/2015.
 */
public interface BusinessLocationSelectListener {
    void OnBusinessLocationSelected(String location, LatLng latLng, String idBusiness);
}
