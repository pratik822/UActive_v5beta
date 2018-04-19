package com.uactiv.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FavouriteModel implements Serializable {


    @SerializedName("idmember")
    private String id = null;

    @SerializedName("firstname")
    private String firstName;

    @SerializedName("lastname")
    private String lastName;


    private String name = null;

    @SerializedName("image")
    private String image = null;

    public FavouriteModel(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        if (!(TextUtils.isEmpty(firstName))) {
            return firstName;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
