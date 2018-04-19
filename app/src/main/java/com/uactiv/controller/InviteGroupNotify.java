package com.uactiv.controller;

import com.uactiv.model.FavouriteModel;
import com.uactiv.model.InviteGroupModel;

import java.util.ArrayList;


/**
 * Created by moorthy on 12/17/2015.
 */
public interface InviteGroupNotify {

    void getSelectedGroupItems(ArrayList<String> selectedFavGroup, ArrayList<InviteGroupModel> fullGroupList);
    void addSelectedMembers(ArrayList<FavouriteModel> getSelectedMember, ArrayList<String> selectedMemberID, ArrayList<String> groupIds);
    void removeSelectedMembers(ArrayList<FavouriteModel> getSelectedMember, ArrayList<String> selectedMemberID, ArrayList<String> groupIds);

}
