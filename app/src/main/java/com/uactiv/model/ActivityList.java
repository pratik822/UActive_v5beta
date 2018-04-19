package com.uactiv.model;

/**
 * Created by moorthy on 4/25/2016.
 */
public class ActivityList {
    private String activity = null;
    private String id = null;
    private boolean isBookingOpen = false;


    public ActivityList(){

    }
    public ActivityList(String activity){
        this.activity= activity;
    }
    public ActivityList(String activity,Boolean isBookingOpen){
        this.activity= activity;
        this.isBookingOpen = isBookingOpen;
    }


    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public boolean isBookingOpen() {
        return isBookingOpen;
    }

    public void setIsBookingOpen(boolean isBookingOpen) {
        this.isBookingOpen = isBookingOpen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}


