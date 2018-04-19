package com.uactiv.model;

/**
 * Created by moorthy on 1/13/2016.
 */
public class ChatDo {

    private String idchat =null;
    private String iduser =null;
    private String idschedule =null;
    private String message =null;
    private String created_on = null;
    private boolean isLeft;
    private String username = null;

    public String getIdchat() {
        return idchat;
    }

    public void setIdchat(String idchat) {
        this.idchat = idchat;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getIdschedule() {
        return idschedule;
    }

    public void setIdschedule(String idschedule) {
        this.idschedule = idschedule;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setIsLeft(boolean isLeft) {
        this.isLeft = isLeft;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
