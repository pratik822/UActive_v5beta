package com.uactiv.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.controller.ResponseListener;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ForgotPassword extends AppCompatActivity implements AppConstants.urlConstants, AppConstants.SharedConstants,ResponseListener {
     CustomEditText emailid;
     CustomButton dialogButton;
    GifImageView progressWheel = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        emailid = (CustomEditText)findViewById(R.id.forgot_email_ed);
        dialogButton = (CustomButton)findViewById(R.id.submit_area);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);






        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailid.getText().toString().matches(AppConstants.emailPattern) || emailid.getText().toString().matches(AppConstants.emailPatternTwoDecimalPoint)) {
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                    try {
                        Map<String, String> param = new HashMap<String, String>();
                        param.put("email", emailid.getText().toString().trim());
                        RequestHandler.getInstance().stringRequestVolley(ForgotPassword.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(ForgotPassword.this, isStaging)) + forgotpassword, param, ForgotPassword.this, 1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.showToastMessage(ForgotPassword.this, "Please enter valid email address");
                }

            }
        });

        
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {

    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {
        Utility.setEventTracking(ForgotPassword.this,"Login Screen", AppConstants.EVENT_TRACKING_ID_FORGOT);
        if (jsonObject != null) {
            Utility.showToastMessage(ForgotPassword.this, getString(R.string.msg_forgot_password));

        }
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }
}
