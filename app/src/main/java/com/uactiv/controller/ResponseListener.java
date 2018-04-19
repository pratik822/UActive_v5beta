package com.uactiv.controller;

import org.json.JSONException;
import org.json.JSONObject;


public interface ResponseListener {

    void successResponse(String successResponse, int flag) throws JSONException;

    void successResponse(JSONObject jsonObject, int flag);

    void errorResponse(String errorResponse, int flag);

    void removeProgress(Boolean hideFlag);

}
