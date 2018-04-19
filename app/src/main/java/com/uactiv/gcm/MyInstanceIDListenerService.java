package com.uactiv.gcm;

import android.content.Intent;
import android.util.Log;

import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * is a class which extends InstanceIDListenerService of GCM and act based on that
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    private final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        InstanceID instance = InstanceID.getInstance(getApplicationContext());
        try {
            String refreshedToken = instance.getToken("858166060344", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (refreshedToken != null) {
                AppsFlyerLib.getInstance().updateServerUninstallToken(getApplicationContext(), refreshedToken); // ADD THIS LINE HERE
            }
        } catch (Throwable e) {
            Log.e("MyInstanceIdService", "onTokenRefresh: Couldn't get the refreshed token.", e);
        }
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

    }
    // [END refresh_token]
}
