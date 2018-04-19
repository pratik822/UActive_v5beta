package com.uactiv.controller;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by buvaneswaran on 12/14/2015.
 */
public interface CreatePickupNotifier {

    void mapViewNotifier(LatLng latLng, String Address, String isBooked, String idBusiness, int isBusinessLocation);

}
