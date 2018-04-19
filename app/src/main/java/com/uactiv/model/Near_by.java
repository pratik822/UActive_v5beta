package com.uactiv.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Pratik on 11/11/2017.
 */

public class Near_by {
    public ArrayList<IFavGroupNearModel> near_by;
    @SerializedName("near_by_search")
    public ArrayList<IFavGroupNearModel> nearBySearch;

    public ArrayList<IFavGroupNearModel> getNear_by() {
        return near_by;
    }

    public void setNear_by(ArrayList<IFavGroupNearModel> near_by) {
        this.near_by = near_by;
    }

    public ArrayList<IFavGroupNearModel> getNearBySearch() {
        return nearBySearch;
    }

    public void setNearBySearch(ArrayList<IFavGroupNearModel> nearBySearch) {
        this.nearBySearch = nearBySearch;
    }
}
