package com.uactiv.model;

import java.io.Serializable;

public class NotifyModel implements Serializable {

    private static final long serialVersionUID = -7060210544600464481L;
    private int notificationType = 0;
    private String notificationTypeTitle = null;
    private String message = null;
    private String idUser;
    private String ischallenge;
    private String status;
    private String idschedule;
    private String image;
    private String start_time;
    private String date;
    private String type;
    private String activity;
    private String GameDescription;
    private String isActive;
    private String updatedAt;
    private String buddyimage;
    private String location;
    private String fname;
    private String lname;
    private Boolean isBookingOpen;
    private String end_time;
    private String buddyUpMsg;
    private Integer no_of_people;
    private String idbusiness;
    private String isbusinesslocation;
    private String pickupmsg;



    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getAbandoned_count() {
        return abandoned_count;
    }

    public void setAbandoned_count(int abandoned_count) {
        this.abandoned_count = abandoned_count;
    }

    private int abandoned_count;

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    private String group_id;
    private int color;
    private String accepted_id = null;
    private int attending_count = 0;
    private int request_count = 0;
    private int msg_count = 0;
    private boolean isRead = false;
    private boolean isUpComing = false;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationTypeTitle() {
        return notificationTypeTitle;
    }

    public void setNotificationTypeTitle(String notificationTypeTitle) {
        this.notificationTypeTitle = notificationTypeTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIschallenge() {
        return ischallenge;
    }

    public void setIschallenge(String ischallenge) {
        this.ischallenge = ischallenge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdschedule() {
        return idschedule;
    }

    public void setIdschedule(String idschedule) {
        this.idschedule = idschedule;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getGameDescription() {
        return GameDescription;
    }

    public void setGameDescription(String gameDescription) {
        GameDescription = gameDescription;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getAccepted_id() {
        return accepted_id;
    }

    public void setAccepted_id(String accepted_id) {
        this.accepted_id = accepted_id;
    }

    public int getAttending_count() {
        return attending_count;
    }

    public void setAttending_count(int attending_count) {
        this.attending_count = attending_count;
    }

    public int getRequest_count() {
        return request_count;
    }

    public void setRequest_count(int request_count) {
        this.request_count = request_count;
    }

    public int getMsg_count() {
        return msg_count;
    }

    public void setMsg_count(int msg_count) {
        this.msg_count = msg_count;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isUpComing() {
        return isUpComing;
    }

    public void setIsUpComing(boolean isUpComing) {
        this.isUpComing = isUpComing;
    }

    public String getBuddyimage() {
        return buddyimage;
    }

    public void setBuddyimage(String buddyimage) {
        this.buddyimage = buddyimage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public Boolean getIsBookingOpen() {
        return isBookingOpen;
    }

    public void setIsBookingOpen(Boolean isBookingOpen) {
        this.isBookingOpen = isBookingOpen;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getBuddyUpMsg() {
        return buddyUpMsg;
    }

    public void setBuddyUpMsg(String buddyUpMsg) {
        this.buddyUpMsg = buddyUpMsg;
    }

    public Integer getNo_of_people() {
        return no_of_people;
    }

    public void setNo_of_people(Integer no_of_people) {
        this.no_of_people = no_of_people;
    }

    public String getIdbusiness() {
        return idbusiness;
    }

    public void setIdbusiness(String idbusiness) {
        this.idbusiness = idbusiness;
    }

    public String getIsbusinesslocation() {
        return isbusinesslocation;
    }

    public void setIsbusinesslocation(String isbusinesslocation) {
        this.isbusinesslocation = isbusinesslocation;
    }

    public String getPickupmsg() {
        return pickupmsg;
    }

    public void setPickupmsg(String pickupmsg) {
        this.pickupmsg = pickupmsg;
    }
}
