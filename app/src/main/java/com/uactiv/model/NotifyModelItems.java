package com.uactiv.model;

/**
 * Created by moorthy on 3/11/2016.
 */
public class NotifyModelItems {

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
    private int color;
    private String accepted_id = null;
    private int attending_count = 0;
    private int request_count = 0;
    private int msg_count = 0;
    private boolean isRead = false;

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
}
