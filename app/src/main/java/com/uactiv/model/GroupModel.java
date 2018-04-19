package com.uactiv.model;

import java.io.Serializable;

/**
 * Created by moorthy on 11/6/2015.
 */
public class GroupModel implements Serializable {

    private int image ;
    private String groupid = null;
    private String groupname = null;
    private String status = null;
    private String createdeon = null;

    public GroupModel(int image,String groupid ,String groupname,String status,String createdeon) {
        super();
        this.image = image;
        this.groupid = groupid;
        this.groupname = groupname;
        this.status= status;
        this.createdeon = createdeon;
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
}
