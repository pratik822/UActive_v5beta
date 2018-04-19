package com.uactiv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.net.URLDecoder;

/**
 * Created by moorthy on 10/12/2016.
 */
public class ReferralBroadCastReceiver extends BroadcastReceiver {


    String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        String rawReferrerString = intent.getStringExtra("referrer");
        if (rawReferrerString != null) {
            try {
                rawReferrerString = URLDecoder.decode(rawReferrerString, "UTF-8");
                Log.d(TAG, "rawReferrerString : " + rawReferrerString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
