package com.uactiv.model;

import java.util.ArrayList;

/**
 * Created by pratikb on 09-11-2017.
 */
public class NearBy {
    Pagedetails pageDetail=new Pagedetails();

    public ArrayList<IFavGroupNearModel> buddylist;

    public ArrayList<IFavGroupNearModel> getBuddylist() {
        return buddylist;
    }

    public void setBuddylist(ArrayList<IFavGroupNearModel> buddylist) {
        this.buddylist = buddylist;
    }
}
