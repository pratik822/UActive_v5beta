package com.uactiv.pushnotification;

import android.os.Bundle;
import android.util.Log;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

public class ApplozicGcmListenerService extends GcmListenerService {

    private static final String TAG = "ApplozicGcmListener";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        if (MobiComPushReceiver.isMobiComPushNotification(data)) {

            Log.e(TAG, "Applozic Notification Processing...");

            MobiComPushReceiver.processMessageAsync(this, data);

            List<Message> latestMessageForEachContactOrGroup = new MobiComConversationService(getApplicationContext()).getLatestMessagesGroupByPeople();

            if (latestMessageForEachContactOrGroup != null) {
                Log.e(" Message Size = " + latestMessageForEachContactOrGroup.size(), " Chat = " + latestMessageForEachContactOrGroup.get(latestMessageForEachContactOrGroup.size() - 1));
            }
            return;


        }
    }
}