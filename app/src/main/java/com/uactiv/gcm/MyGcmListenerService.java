package com.uactiv.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.google.android.gms.gcm.GcmListenerService;
import com.uactiv.R;
import com.uactiv.activity.MainActivity;
import com.uactiv.activity.PickUpGuest;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * is a class which extends GcmListenerService of GCM and act based on that
 */
public class MyGcmListenerService extends GcmListenerService implements AppConstants.urlConstants, AppConstants.SharedConstants {

    private final String TAG = "GcmListenerService";

    public static final int NOTIFICATION_ID = 1;


    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(String from, Bundle data) {

        if(MobiComPushReceiver.isMobiComPushNotification(data)) {
            MobiComPushReceiver.processMessageAsync(this, data);
            return;
        }

        Log.d("onMessageReceived : ", "" + data.toString());

        String message = data.getString("message");
        String generalPush = data.getString("general_push");
        notifymessage();
        if (message != null) {
            try {
                JSONObject messageObj = new JSONObject(message);
                if (messageObj != null) {
                    if (messageObj.optString("msg_type").equals("schedule")) {
                        sendNotification(messageObj, false);
                    }
                    if (messageObj.optInt("badge") > 0) {
                        SharedPref.getInstance().setSharedValue(this, notification_count, messageObj.optInt("badge"));
                        notifyCountChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(generalPush)) {
            try {
                Intent intent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                JSONObject generalMessage = new JSONObject(generalPush);
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(generalMessage.optString("msg")))
                        .setContentText(generalMessage.optString("msg"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param jsonObject GCM message received.
     */
    private void sendNotification(JSONObject jsonObject, boolean ischat) {

        JSONObject message = jsonObject;

        // Log.e("gcm message", message.optString("message"));

        String contentMessage = "";
        Intent intent = new Intent(this, PickUpGuest.class);
        intent.putExtra("from_schedule", false);
        intent.putExtra("isFromNotification", true);  //isFromNotification

        if (ischat) {
            intent.putExtra("fragment", "chat");
            intent.putExtra("status", KEY_ACCEPTED);
            contentMessage = message.optString("firstname") + " : " + message.optString("msg");
        } else {
            intent.putExtra("fragment", "map");
            intent.putExtra("status", message.optString("status"));
            contentMessage = message.optString("msg");
        }
        intent.putExtra("position", "");
        intent.putExtra("sstatus", message.optString("sstatus"));
       // intent.putExtra("isDeepLinking", true);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        intent.putExtra("idschedule", message.optString("idschedule"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentMessage))
                .setContentText(contentMessage)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setPriority(Notification.PRIORITY_MAX)
                // .setStyle(notiStyle)
                .setContentIntent(pendingIntent);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(m, notificationBuilder.build());


      /*  Intent registrationComplete = new Intent(AppConstants.ACTION_NEW_MESSAGE_RECEIVED);
        registrationComplete.putExtra(AppConstants.EXTRA_MESSAGE, "" + message.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);*/
    }


    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void notifymessage() {
        Intent intnet = new Intent(AppConstants.ACTION_PAYMENT_RECIVED);
        sendBroadcast(intnet);

    }
}
