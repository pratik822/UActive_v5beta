package com.uactiv.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by moorthy on 12/1/2015.
 */
public class PickUpCategory implements Serializable {

    private String pickupid = null;
    private String iduser=null;
    private String eventdate = null;
    private String noofpeople = null;
    private String awayfrom = null;
    private String image = null;
    private String starttime = null;
    private String idschedule = null;
    private String status = null;
    private double Latitude = 0.0;
    private double Longitude = 0.0;
    private Date eventTimeStamp = null;
    private String full_date;

    public Date getEventTimeStamp() {
        return eventTimeStamp;
    }

    public String getFull_date() {
        return full_date;
    }

    public void setFull_date(String full_date) {
        this.full_date = full_date;
    }

    public void setEventTimeStamp(Date eventTimeStamp) {
        this.eventTimeStamp = eventTimeStamp;
    }

    public String getPickupid() {
        return pickupid;
    }

    public void setPickupid(String pickupid) {
        this.pickupid = pickupid;
    }

    public String getEventdate() {
        return eventdate;
    }

    public void setEventdate(String eventdate) {
        this.eventdate = eventdate;
    }

    public String getNoofpeople() {
        return noofpeople;
    }

    public void setNoofpeople(String noofpeople) {
        this.noofpeople = noofpeople;
    }

    public String getAwayfrom() {
        return awayfrom;
    }

    public void setAwayfrom(String awayfrom) {
        this.awayfrom = awayfrom;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getIdschedule() {
        return idschedule;
    }

    public void setIdschedule(String idschedule) {
        this.idschedule = idschedule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }
}
