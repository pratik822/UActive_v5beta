package com.uactiv.applozicchat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MessageIntentService;
import com.applozic.mobicomkit.api.conversation.MobiComMessageService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicChannelAddMemberTask;
import com.applozic.mobicomkit.uiwidgets.async.ApplozicChannelLeaveMember;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelMetadata;
import com.applozic.mobicommons.people.contact.Contact;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nirmal on 11/24/2016.
 */
public class ApplozicChat implements AppConstants.SharedConstants {
    private ArrayList<String> channelMemberNames = new ArrayList<>();
    private Activity activity;
    private ChannelInfo channelInfo;
    private Channel channel = null;
    private String chatgroupid;
    private String groupName;
    private IApplozic mIApplozic;
    private String TAG = getClass().getSimpleName();

    /*//To send custom msg to group copy and paste the below method to "com.applozic.mobicomkit.api.conversation - Message.class"
    public Message(int groupId, String message) {
        this.groupId = groupId;
        this.message = message;
    }
    */

    public ApplozicChat(Activity activity) {
        this.activity = activity;

    }

    public void chatInit() {

    }

    /***/
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void createGroupWithMember(String groupName, ArrayList<String> channelMemberNames, IApplozic mIApplozic) {
        this.mIApplozic = mIApplozic;
        this.groupName = groupName;
        this.channelMemberNames = channelMemberNames;
        new CreateGroup().execute();
    }

    public void createGroupWithOutMember(String groupName, IApplozic mIApplozic) {
        this.mIApplozic = mIApplozic;
        this.groupName = groupName;
        new CreateGroup().execute();
    }

    /***/
    public void addParticipant(String userId, int conversationId) {
        Log.e("userId", userId);
        Log.e("conversationId", "" + conversationId);

        ApplozicChannelAddMemberTask.ChannelAddMemberListener channelAddMemberListener = new ApplozicChannelAddMemberTask.ChannelAddMemberListener() {
            @Override
            public void onSuccess(String response, Context context) {
                //Response will be "success" if user is added successfully
                Log.i("ApplozicChannelMember", "Add Response:" + response);
            }

            @Override
            public void onFailure(String response, Exception e, Context context) {
                Log.i("ApplozicChannelMember", "error:" + response);
            }
        };

        ApplozicChannelAddMemberTask applozicChannelAddMemberTask = new ApplozicChannelAddMemberTask(activity, conversationId, userId, channelAddMemberListener);//pass channel key and userId whom you want to add to channel
        applozicChannelAddMemberTask.execute((Void) null);
    }

    /***/
    public void removeParticipant(String userId, int conversationId) {
        ApplozicChannelLeaveMember.ChannelLeaveMemberListener channelLeaveMemberListener = new ApplozicChannelLeaveMember.ChannelLeaveMemberListener() {
            @Override
            public void onSuccess(String response, Context context) {
                Log.i("ApplozicChannel", "Leave member respone:" + response);
            }

            @Override
            public void onFailure(String response, Exception e, Context context) {

            }
        };

        ApplozicChannelLeaveMember applozicChannelLeaveMember = new ApplozicChannelLeaveMember(activity, conversationId, userId, channelLeaveMemberListener);//pass channelKey and userId
        applozicChannelLeaveMember.execute((Void) null);
    }

    private void notifyCountChanged(Context context) {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(registrationComplete);
    }
    /***/
    public void loginWithApplozic() {

        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse, final Context context) {
                ApplozicSetting.getInstance(context).showStartNewButton().showPriceOption();
                //Basic settings...
                ApplozicSetting.getInstance(context).showStartNewGroupButton()
                        .setCompressedImageSizeInMB(5)
                        .enableImageCompression()
                        .setMaxAttachmentAllowed(5);
                ApplozicClient.getInstance(context).setContextBasedChat(true).setHandleDial(true);
                ApplozicSetting.getInstance(context).enableRegisteredUsersContactCall();//To enable the applozic Registered Users Contact Note:for disable that you can comment this line of code
                SharedPref.getInstance().setSharedValue(context, AppConstants.SharedConstants.PREF_IS_CHAT_LOGIN, true);//Your preference name
                sendGcmTokenToServer();
                notifyCountChanged(context);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(activity).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage(exception.toString());
                alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, activity.getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                if (!activity.isFinishing()) {
                    alertDialog.show();
                }
            }
        };

        User user = new User();
        user.setUserId(SharedPref.getInstance().getStringVlue(activity, userId));
        user.setEmail(SharedPref.getInstance().getStringVlue(activity, email));
        user.setPassword(SharedPref.getInstance().getStringVlue(activity, userId));
        user.setDisplayName(SharedPref.getInstance().getStringVlue(activity, firstname));
        user.setContactNumber("");
        user.setImageLink(SharedPref.getInstance().getStringVlue(activity, image));
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());
        UserLoginTask mAuthTask = new UserLoginTask(user, listener, activity);
        mAuthTask.execute((Void) null);
    }

    /***/
    public void sendGcmTokenToServer() {

        PushNotificationTask pushNotificationTask = null;
        PushNotificationTask.TaskListener listener = new PushNotificationTask.TaskListener() {
            @Override
            public void onSuccess(RegistrationResponse registrationResponse) {
                Log.d("RegistrationResponse", "" + registrationResponse);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                exception.printStackTrace();
            }
        };
        pushNotificationTask = new PushNotificationTask(SharedPref.getInstance().getStringVlue(activity, gcm_token), listener, activity);
        pushNotificationTask.execute((Void) null);
    }

    /***/
    public void navigateToOneToOneChat(String senderId, String name) {
        if (senderId != null) {
            Intent intent = new Intent(activity, ConversationActivity.class);
            intent.putExtra(ConversationUIService.USER_ID, senderId);
            intent.putExtra("takeOrder", true);
            intent.putExtra(ConversationUIService.DISPLAY_NAME, name); //put it for displaying the title.
            activity.startActivity(intent);
        }
    }

    /***/
    public void navigateToGroupChat(String groupId, String groupName) {
        if (groupId != null) {
            Intent intent = new Intent(activity, ConversationActivity.class);
            intent.putExtra(ConversationUIService.GROUP_ID, groupId);
            intent.putExtra("takeOrder", true);
            intent.putExtra(ConversationUIService.GROUP_NAME, groupName); //put it for displaying the title.
            activity.startActivity(intent);
        }
    }

    /***/
    public void sendCustomMsgtoGroup(String groupId, String msg) {
        Message message = new Message(Integer.parseInt(groupId), msg);
        new MobiComMessageService(activity, MessageIntentService.class).sendChannelCustomMessage(message);
        //new MobiComMessageService(activity, MessageIntentService.class).sendCustomMessage(message);
    }

    /***/
    public void sendCustomMsgtoOneToOne(String senderId, String msg) {
        Message message = new Message(Integer.parseInt(senderId), msg);
        new MobiComMessageService(activity, MessageIntentService.class).sendCustomMessage(message);
    }

    /***/
    public int getUnreadMsgCountFromGroup(int channelId) {
        MessageDatabaseService messageDatabaseService = new MessageDatabaseService(activity);
        return messageDatabaseService.getUnreadMessageCountForChannel(channelId);
    }

    /***/
    public int getUnreadMsgCountFromOneToOneChat(int senderId) {
        MessageDatabaseService messageDatabaseService = new MessageDatabaseService(activity);
        return messageDatabaseService.getUnreadMessageCountForChannel(senderId);
    }

    /***/
    public void deleteChannelConversation(String channelId) {
        if (channelId != null && channelId.trim().length() > 0) {
            MessageDatabaseService messageDatabaseService = new MessageDatabaseService(activity);
            messageDatabaseService.deleteChannelConversation(Integer.parseInt(channelId));
        }
    }

    /***/
    public String getLastMessageFromOneToOne(String senderId) {
        MessageDatabaseService messageDatabaseService = new MessageDatabaseService(activity);
        List<Message> mMsglist = messageDatabaseService.getLatestMessage(senderId);
        if (mMsglist != null && mMsglist.size() > 0)
            return mMsglist.get(0).getMessage();
        else
            return "";
    }


    private Message getmessage(int groupId) {
        List<Message> messageList = new MessageDatabaseService(activity).getLatestMessageByChannelKey(groupId);
        for (Message message : messageList) {
            return message;
        }
        return null;
    }


    /***/
    public String getLastMessageFromGroup(int groupId, AppContactService appContactService) {

        Message message = getmessage(groupId);
        String currentUserName = SharedPref.getInstance().getStringVlue(activity, firstname);

        Log.d(TAG, "currentUserName : " + currentUserName);

        String notificationText = "";
        Contact displayNameContact = null;
        if (message != null) {
            if (message.getGroupId() != null) {
                if (message.getGroupId() == groupId) {
                    String name = "";
                    try {
                        if (message.getTo() != null) {
                            displayNameContact = appContactService.getContactById(message.getTo());
                            name = "" + displayNameContact.getDisplayName();
                        } else {
                            name = currentUserName;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        name = "";
                    }


                    if (message.getContentType() == Message.ContentType.LOCATION.getValue()) {
                        notificationText = "Location shared";
                    } else if (message.getContentType() == Message.ContentType.AUDIO_MSG.getValue()) {
                        notificationText = "Audio shared";
                    } else if (message.getContentType() == Message.ContentType.VIDEO_MSG.getValue()) {
                        notificationText = "Video shared";
                    } else if (message.hasAttachment() && TextUtils.isEmpty(message.getMessage())) {
                        notificationText = "File shared";
                    } else {
                        //To avoid custom messages
                        if (message.isChannelCustomMessage()) {
                            notificationText = "";
                        } else {
                            notificationText = message.getMessage();
                        }

                    }
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(notificationText)) {
                        notificationText = name + " : " + notificationText;
                    }

                    return notificationText;
                }
            }
        }
        return "";
    }


    /***/
    public class CreateGroup extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            channelInfo = new ChannelInfo(groupName, channelMemberNames);
            ChannelMetadata channelMetadata = new ChannelMetadata();
            channelMetadata.setCreateGroupMessage(ChannelMetadata.ADMIN_NAME + " created " + ChannelMetadata.GROUP_NAME);
            channelMetadata.setAddMemberMessage(ChannelMetadata.ADMIN_NAME + " added " + ChannelMetadata.USER_NAME);
            channelMetadata.setRemoveMemberMessage(ChannelMetadata.ADMIN_NAME + " removed " + ChannelMetadata.USER_NAME);
            channelMetadata.setGroupNameChangeMessage(ChannelMetadata.USER_NAME + " changed name " + ChannelMetadata.GROUP_NAME);
            channelMetadata.setJoinMemberMessage(ChannelMetadata.USER_NAME + " accepted");
            channelMetadata.setGroupLeftMessage(ChannelMetadata.USER_NAME + " abandoned " + ChannelMetadata.GROUP_NAME);
            channelMetadata.setGroupIconChangeMessage(ChannelMetadata.USER_NAME + " changed icon");
            channelMetadata.setDeletedGroupMessage(ChannelMetadata.ADMIN_NAME + " deleted " + ChannelMetadata.GROUP_NAME);
            channelInfo.setChannelMetadata(channelMetadata);
            if (channel == null) {
                channel = ChannelService.getInstance(activity).createChannel(channelInfo);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (channel != null) {
                    chatgroupid = "" + channel.getKey();
                    mIApplozic.getGroupId(chatgroupid);
                    mIApplozic.getChannel(channel);
                } else {
                    mIApplozic.initFailure("Applozic group creation failed. Please try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
