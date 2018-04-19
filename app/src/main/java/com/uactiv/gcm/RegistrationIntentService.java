package com.uactiv.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.uactiv.R;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;

import java.io.IOException;


/**
 * is a class which extends IntentService of GCM and act based on that
 */
public class RegistrationIntentService extends IntentService implements AppConstants.SharedConstants {

    private static final String TAG = "RegIntentService";
    private final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.e(TAG, "GCM Registration Token: " + token);

            SharedPref.getInstance().setSharedValue(this, gcm_token, token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);


            sharedPreferences.edit().putBoolean(getResources().getString(R.string.sentTokenToServer), true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
               sharedPreferences.edit().putBoolean(getResources().getString(R.string.sentTokenToServer), false).apply();
        }
        Intent registrationComplete = new Intent(getResources().getString(R.string.registrationComplete));
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param registrationToken The new token.
     */
    private void sendRegistrationToServer(String registrationToken) {
        Log.d(TAG,"sendRegistrationToServer");
        // Add custom implementation, as needed.
        if (MobiComUserPreference.getInstance(this).isRegistered()) {
            try {
                new RegisterUserClientService(this).updatePushNotificationId(registrationToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
