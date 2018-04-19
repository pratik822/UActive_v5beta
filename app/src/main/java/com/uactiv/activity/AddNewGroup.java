package com.uactiv.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.uactiv.R;
import com.uactiv.controller.ResponseListener;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;

import org.json.JSONArray;
import org.json.JSONObject;


public class AddNewGroup extends Activity implements OnClickListener, AppConstants.urlConstants, AppConstants.SharedConstants, ResponseListener {
    private ImageView imgBack, imgGroupEdit;
    private CustomButton tvGroupAdd;
    private RecyclerView mRvGroupList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnew_group);
        bindActivity();

    }

    private void bindActivity() {
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgGroupEdit = (ImageView) findViewById(R.id.imgGroupEdit);
        tvGroupAdd = (CustomButton) findViewById(R.id.tvGroupAdd);
        mRvGroupList = (RecyclerView) findViewById(R.id.grouplist);

        //onclick
        tvGroupAdd.setOnClickListener(this);
        imgGroupEdit.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        Utility.setEventTracking(AddNewGroup.this, "", AppConstants.SCREEN_TRACKING_ID_NEWGROUP);
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            finish();
        } else if (v == tvGroupAdd) {
            Intent intent = new Intent(AddNewGroup.this, FavouriteAddMembers.class);
            startActivity(intent);

        } else if (v == imgGroupEdit) {

        }
    }

    @Override
    public void successResponse(String successResponse, int flag) {

        /** flag ==0 for createnew group API response
         *
         */

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(successResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (flag) {
            case 0:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        try {
                            JSONArray detdetails = jsonObject.getJSONArray(KEY_DETAIL);
                            for (int i = 0; i < detdetails.length(); i++) {
                                JSONObject chid = detdetails.getJSONObject(i);
                                //	FavGroup.favGroupItems.add(new GroupModel(R.drawable.default_user_icon,chid.optString("idgroup"), chid.optString("groupname"),chid.optString("status"),chid.optString("created_on")));
                            }
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Utility.showToastMessage(AddNewGroup.this, jsonObject.optString(KEY_MSG));
                    }
                }
                break;
        }


    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {

    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }
}
