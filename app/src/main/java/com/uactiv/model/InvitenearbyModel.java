package com.uactiv.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nirmal on 8/2/2016.
 */
public class InvitenearbyModel extends BaseModel implements Serializable {
    public Details details;

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public class Details implements Serializable {

        Near_by near_by=new Near_by();
    }
}
