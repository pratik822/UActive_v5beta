package com.uactiv.controller;

import com.uactiv.model.BuddyModel;
import com.uactiv.model.PickUpCategory;


/**
 * Created by moorthy on 11/30/2015.
 */
public interface NotifiyArrayListChange {

    void getBuddyModelAddedItems(int position, BuddyModel model);

    void getPickUpModelAddedItems(int pickUpItemposition, int categoryItemPosition, PickUpCategory model, String activityName);
}
