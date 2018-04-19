package com.uactiv.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Admin on 7/14/2015.
 */
public class BuddyModel implements Serializable {


    private static final long serialVersionUID = -7060210544600464481L;


    private String userid = null;
    private String mutual_friends=null;

    private String image = null;

    public String getMutual_friends() {
        return mutual_friends;
    }

    public void setMutual_friends(String mutual_friends) {
        this.mutual_friends = mutual_friends;
    }

    private String badge = null;

    private String name = null;

    private String age = null;

    private String awayDistance = null;

    private String distance = null;

    private ArrayList<SkillDo> skillDo = null;
    private String isfav = null;

    private String about_yourself = null;

    private String isreceivebuddyrequest = null;

    private int user_type ;
    private int rating ;

    private int rating_count;
    private String email =null;
    private String phone_no =null;
    private String address = null;
    private String landLine = null;
    private String businessType = null;
    private int badgeImage  ;
    private String facebookId = null;

    private GameCountDo gameCountDo;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAwayDistance() {
        return awayDistance;
    }

    public void setAwayDistance(String awayDistance) {
        this.awayDistance = awayDistance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public ArrayList<SkillDo> getSkillDo() {
        return skillDo;
    }

    public void setSkillDo(ArrayList<SkillDo> skillDo) {
        this.skillDo = skillDo;
    }

    public String getIsfav() {
        return isfav;
    }

    public void setIsfav(String isfav) {
        this.isfav = isfav;
    }

    public String getAbout_yourself() {
        return about_yourself;
    }

    public void setAbout_yourself(String about_yourself) {
        this.about_yourself = about_yourself;
    }

    public String getIsreceivebuddyrequest() {
        return isreceivebuddyrequest;
    }

    public void setIsreceivebuddyrequest(String isreceivebuddyrequest) {
        this.isreceivebuddyrequest = isreceivebuddyrequest;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRating_count() {
        return rating_count;
    }

    public void setRating_count(int rating_count) {
        this.rating_count = rating_count;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLandLine() {
        return landLine;
    }

    public void setLandLine(String landLine) {
        this.landLine = landLine;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public int getBadgeImage() {
        return badgeImage;
    }

    public void setBadgeImage(int badgeImage) {
        this.badgeImage = badgeImage;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }


    public GameCountDo getGameCountDo() {
        return gameCountDo;
    }

    public void setGameCountDo(GameCountDo gameCountDo) {
        this.gameCountDo = gameCountDo;
    }
}
