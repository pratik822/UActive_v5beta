package com.applozic.mobicomkit.uiwidgets.conversation.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.ConversationFragment;

import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

/**
 * Created by User on 23-05-2015.
 */
public interface MobiComKitActivityInterface {

    int REQUEST_CODE_FULL_SCREEN_ACTION = 301;
    int INSTRUCTION_DELAY = 5000;

    void onQuickConversationFragmentItemClick(View view, Contact contact, Channel channel, Integer conversationId);

    void startContactActivityForResult();

    void addFragments(FragmentActivity fragmentActivity, Fragment fragmentToAdd, String fragmentTag);

    void addFragment(ConversationFragment conversationFragment);


    void updateLatestMessage(Message message, String number);

    void removeConversation(Message message, String number);

    void startActivityForResult(Intent intent, int code);

    void showErrorMessageView(String errorMessage);

    void retry();

    int getRetryCount();

    void processingLocation();

    void processLocation();

    void processVideoRecording();

    void isTakePhoto(boolean isTakePhoto);

    void processContact();

    void processCameraAction();

    void isAttachment(boolean isAttachment);

    void processAttachment();

    void showAudioRecordingDialog();

    Uri getVideoFileUri();

    void setVideoFileUri(Uri videoFileUri);

    Uri getCapturedImageUri();

    void setCapturedImageUris(Uri capturedImageUri);

    Contact getContact() ;

    Channel getmChannel() ;

    Integer getConversationId();

    String getScheduleId();
}
