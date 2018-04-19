package com.uactiv.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nirmal on 8/1/2016.
 */
public class INearByModel implements Serializable {

    private ArrayList<SkillsModel> skills;
    private String iduser;
    private String fav;
    private String user_type;
    private String image;
    private String lastname;
    private String ischallenge_badge;
    private String rated_count;
    private String firstname;
    private String distance;
    private String address;
    private String email;
    private String phone_no;
    private String age;
    private String badge;
    private String rating;
    private String about_yourself;
    private String isreceive_request;

    public ArrayList<SkillsModel> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<SkillsModel> skills) {
        this.skills = skills;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getIschallenge_badge() {
        return ischallenge_badge;
    }

    public void setIschallenge_badge(String ischallenge_badge) {
        this.ischallenge_badge = ischallenge_badge;
    }

    public String getRated_count() {
        return rated_count;
    }

    public void setRated_count(String rated_count) {
        this.rated_count = rated_count;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAbout_yourself() {
        return about_yourself;
    }

    public void setAbout_yourself(String about_yourself) {
        this.about_yourself = about_yourself;
    }

    public String getIsreceive_request() {
        return isreceive_request;
    }

    public void setIsreceive_request(String isreceive_request) {
        this.isreceive_request = isreceive_request;
    }
}
