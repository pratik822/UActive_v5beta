package com.uactiv.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by moorthy on 9/22/2016.
 */
public class BusinessLocationList implements Serializable {

    @SerializedName("business_location")
    ArrayList<BusinessLocationDo> businessLocationsList;

    public ArrayList<BusinessLocationDo> getBusinessLocationsList() {
        return businessLocationsList;
    }

    public void setBusinessLocationsList(ArrayList<BusinessLocationDo> businessLocationsList) {
        this.businessLocationsList = businessLocationsList;
    }
}
