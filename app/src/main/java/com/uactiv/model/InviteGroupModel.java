package com.uactiv.model;

import java.util.ArrayList;

/**
 * Created by moorthy on 12/16/2015.
 */
public class InviteGroupModel {

    private int image;
    private String groupid = null;
    private String groupname = null;
    private String status = null;
    private String createdeon = null;
    private ArrayList<String> selectedMemberId = null;
    private ArrayList<FavouriteModel> selectedMemberList = null;


    public ArrayList<String> getSelectedMemberId() {
        return selectedMemberId;
    }

    public void setSelectedMemberId(ArrayList<String> selectedMemberId) {
        this.selectedMemberId = selectedMemberId;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getCreatedeon() {
        return createdeon;
    }

    public void setCreatedeon(String createdeon) {
        this.createdeon = createdeon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public ArrayList<FavouriteModel> getSelectedMemberList() {
        return selectedMemberList;
    }

    public void setSelectedMemberList(ArrayList<FavouriteModel> selectedMemberList) {
        this.selectedMemberList = selectedMemberList;
    }
}
