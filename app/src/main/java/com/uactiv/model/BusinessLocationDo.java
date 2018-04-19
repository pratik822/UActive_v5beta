package com.uactiv.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by moorthy on 12/24/2015.
 */
public class BusinessLocationDo {

    @SerializedName("idbusiness")
    private String idbusiness;

    @SerializedName("business_name")
    private String business_name ;

    @SerializedName("image")
    private String image_url ;

    @SerializedName("latitude")
    private double latitude ;

    @SerializedName("longitude")
    private double longitude ;

    @SerializedName("awayfrom")
    private String awayFrom ;

    public String getIdbusiness() {
        return idbusiness;
    }

    public void setIdbusiness(String idbusiness) {
        this.idbusiness = idbusiness;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAwayFrom() {
        return awayFrom;
    }

    public void setAwayFrom(String awayFrom) {
        this.awayFrom = awayFrom;
    }
}
