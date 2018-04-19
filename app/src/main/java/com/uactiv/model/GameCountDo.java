package com.uactiv.model;

import java.io.Serializable;

/**
 * Created by moorthy on 9/21/2016.
 */
public class GameCountDo implements Serializable {

    int buddyUpCount;
    int pickUpCount;

    public int getBuddyUpCount() {
        return buddyUpCount;
    }

    public void setBuddyUpCount(int buddyUpCount) {
        this.buddyUpCount = buddyUpCount;
    }

    public int getPickUpCount() {
        return pickUpCount;
    }

    public void setPickUpCount(int pickUpCount) {
        this.pickUpCount = pickUpCount;
    }
}
