package com.uactiv.model;

import java.io.Serializable;

/**
 * Created by moorthy on 11/7/2015.
 */
public class SkillDo implements Serializable{

    private String idskill = null;
    private String activty = null;
    private String level = null;
    private String activity_type = null;
    private boolean isBookingOpen = false;

    public String getIdskill() {
        return idskill;
    }

    public void setIdskill(String idskill) {
        this.idskill = idskill;
    }

    public String getActivty() {
        return activty;
    }

    public void setActivty(String activty) {
        this.activty = activty;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public boolean isBookingOpen() {
        return isBookingOpen;
    }

    public void setIsBookingOpen(boolean isBookingOpen) {
        this.isBookingOpen = isBookingOpen;
    }
}
