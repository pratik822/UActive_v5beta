package com.applozic.mobicomkit.uiwidgets.conversation.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.MessageCommunicator;
import com.applozic.mobicomkit.uiwidgets.conversation.MobiComKitBroadcastReceiver;
import com.applozic.mobicomkit.uiwidgets.conversation.adapter.TitleNavigationAdapter;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComQuickConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MultimediaOptionFragment;
import com.applozic.mobicomkit.uiwidgets.instruction.InstructionUtil;
import com.applozic.mobicomkit.uiwidgets.people.activity.MobiComKitPeopleActivity;

import com.applozic.mobicommons.commons.core.utils.Support;
import com.applozic.mobicommons.commons.image.ImageUtils;
import com.applozic.mobicommons.file.FilePathFinder;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.ChannelUtils;
import com.applozic.mobicommons.people.contact.Contact;
import com.applozic.mobicommons.people.channel.Channel;

import java.util.ArrayList;


abstract public class MobiComActivityForFragment extends ActionBarActivity implements ActionBar.OnNavigationListener,
        MessageCommunicator, MobiComKitActivityInterface {

    public static final int REQUEST_CODE_FULL_SCREEN_ACTION = 301;
    public static final int REQUEST_CODE_CONTACT_GROUP_SELECTION = 101;
    public static final int LOCATION_SERVICE_ENABLE = 1001;
    public static final int REQUEST_CODE_ATTACHMENT_ACTION = 201;
    public static final int ACCOUNT_REGISTERED = 121;
    public static final int INSTRUCTION_DELAY = 5000;
    protected static final long UPDATE_INTERVAL = 5;
    protected static final long FASTEST_INTERVAL = 1;
    private static final String TAG = "MobiComActivity";
    public static String currentOpenedUserId;
    public static boolean mobiTexterBroadcastReceiverActivated;
    public static String title = "Conversations";
    protected static boolean HOME_BUTTON_ENABLED = false;
    protected ActionBar mActionBar;
    protected MobiComKitBroadcastReceiver mobiComKitBroadcastReceiver;
    protected MobiComQuickConversationFragment quickConversationFragment;
    protected MobiComConversationFragment conversationFragment;
    // Title navigation Spinner data
    protected ArrayList<SpinnerNavItem> navSpinner;
    // Navigation adapter
    protected TitleNavigationAdapter adapter;
    protected BaseContactService baseContactService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mobiComKitBroadcastReceiver = new MobiComKitBroadcastReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InstructionUtil.enabled = true;
        //   mobiTexterBroadcastReceiverActivated = Boolean.TRUE;
        //   LocalBroadcastManager.getInstance(this).registerReceiver(mobiComKitBroadcastReceiver, BroadcastService.getIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        InstructionUtil.enabled = false;
        // mobiTexterBroadcastReceiverActivated = Boolean.FALSE;
        // LocalBroadcastManager.getInstance(this).unregisterReceiver(mobiComKitBroadcastReceiver);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (true) {
            menu.removeItem(R.id.start_new);
        } else {
            menu.removeItem(R.id.dial);
            menu.removeItem(R.id.deleteConversation);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.removeItem(R.id.conversations);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public abstract void processLocation();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if ((requestCode == MultimediaOptionFragment.REQUEST_CODE_ATTACH_PHOTO ||
                requestCode == MultimediaOptionFragment.REQUEST_CODE_TAKE_PHOTO)
                && resultCode == RESULT_OK) {
            Uri selectedFileUri = (intent == null ? null : intent.getData());
            if (selectedFileUri == null) {
                selectedFileUri = conversationFragment.getMultimediaOptionFragment().getCapturedImageUri();
                ImageUtils.addImageToGallery(FilePathFinder.getPath(this, selectedFileUri), this);
            }

            if (selectedFileUri == null) {
                Bitmap photo = (Bitmap) intent.getExtras().get("data");
                selectedFileUri = ImageUtils.getImageUri(getApplicationContext(), photo);
            }
            conversationFragment.loadFile(selectedFileUri);

            Log.i(TAG, "File uri: " + selectedFileUri);
        } else if (requestCode == REQUEST_CODE_CONTACT_GROUP_SELECTION && resultCode == RESULT_OK) {
            checkForStartNewConversation(intent);
        }
    }

    @Override
    public abstract void startContactActivityForResult();

    public void startContactActivityForResult(Intent intent, Message message, String messageContent) {
        if (message != null) {
            intent.putExtra(MobiComKitPeopleActivity.FORWARD_MESSAGE, GsonUtils.getJsonFromObject(message, message.getClass()));
        }
        if (messageContent != null) {
            intent.putExtra(MobiComKitPeopleActivity.SHARED_TEXT, messageContent);
        }

        startActivityForResult(intent, REQUEST_CODE_CONTACT_GROUP_SELECTION);
    }

    public abstract void startContactActivityForResult(Message message, String messageContent);

    @Override
    public void onQuickConversationFragmentItemClick(View view, Contact contact,Channel channel,Integer conversationId) {
        TextView textView = (TextView) view.findViewById(R.id.unreadSmsCount);
        textView.setVisibility(View.GONE);
        //openConversationFragment(contact);
    }

   /* public void openConversationFragment(Contact contact) {
        InstructionUtil.hideInstruction(this, R.string.info_message_sync);
        InstructionUtil.hideInstruction(this, R.string.instruction_open_conversation_thread);
        conversationFragment.loadConversation(contact);
    }

    public void openConversationFragment(Channel channel) {
        conversationFragment.loadConversation(channel);
    }*/

    public void loadLatestInConversationFragment() {
        if (conversationFragment.getContact() != null || conversationFragment.getChannel() != null) {
            return;
        }
        String latestContact = quickConversationFragment.getLatestContact();
        if (latestContact != null) {
            Contact contact = baseContactService.getContactById(latestContact);
            //conversationFragment.loadConversation(contact);
        }
    }


    //Note: Workaround for LGE device bug: https://github.com/adarshmishra/MobiTexter/issues/374
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND) || super.onKeyDown(keyCode, event);
    }
//
//    @Override
//    public void onEmojiconBackspaceClicked(View view) {
//        conversationFragment.onEmojiconBackspace();
//    }
//
//    @Override
//    public void onEmojiconClicked(Emojicon emojicon) {
//        conversationFragment.onEmojiconClicked(emojicon);
//    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void setNavSpinner(ArrayList<SpinnerNavItem> navSpinner) {
        this.navSpinner = navSpinner;
    }

    public void setAdapter(TitleNavigationAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void updateLatestMessage(Message message, String formattedContactNumber) {
        quickConversationFragment.updateLatestMessage(message, formattedContactNumber);
    }

    @Override
    public void removeConversation(Message message, String formattedContactNumber) {
        quickConversationFragment.removeConversation(message, formattedContactNumber);
    }

    public void checkForStartNewConversation(Intent intent) {
        Contact contact = null;
        Channel channel = null;

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if ("text/plain".equals(intent.getType())) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    startContactActivityForResult(null, sharedText);
                }
            } else if (intent.getType().startsWith("image/")) {
                //Todo: use this for image forwarding
            }
        }

        final Uri uri = intent.getData();
        if (uri != null) {
            Long contactId = intent.getLongExtra("contactId", 0);
            if (contactId == 0) {
                //Todo: show warning that the user doesn't have any number stored.
                return;
            }
            contact = baseContactService.getContactById(String.valueOf(contactId));
        }

        Integer groupId = intent.getIntExtra("groupId", -1);
        String groupName = intent.getStringExtra("groupName");
        if (groupId != -1) {
            channel = ChannelUtils.fetchGroup(this, groupId, groupName);
        }

        String contactNumber = intent.getStringExtra("contactNumber");
        boolean firstTimeMTexterFriend = intent.getBooleanExtra("firstTimeMTexterFriend", false);
        if (!TextUtils.isEmpty(contactNumber)) {
            contact = baseContactService.getContactById(contactNumber);
            conversationFragment.setFirstTimeMTexterFriend(firstTimeMTexterFriend);
        }

        String userId = intent.getStringExtra("userId");
        if (!TextUtils.isEmpty(userId)) {
            contact = new Contact(this, userId);
            //Todo: Load contact details from server.
        }

        String messageJson = intent.getStringExtra(MobiComKitConstants.MESSAGE_JSON_INTENT);
        if (!TextUtils.isEmpty(messageJson)) {
            Message message = (Message) GsonUtils.getObjectFromJson(messageJson, Message.class);
            contact = baseContactService.getContactById(message.getTo());
        }

        boolean support = intent.getBooleanExtra(Support.SUPPORT_INTENT_KEY, false);
        if (support) {
            contact = new Support(this).getSupportContact();
        }

        if (contact != null) {
            //openConversationFragment(contact);
        }

        if (channel != null) {
            //openConversationFragment(channel);
        }

        String forwardMessage = intent.getStringExtra(MobiComKitPeopleActivity.FORWARD_MESSAGE);
        if (!TextUtils.isEmpty(forwardMessage)) {
            Message messageToForward = (Message) GsonUtils.getObjectFromJson(forwardMessage, Message.class);
            conversationFragment.forwardMessage(messageToForward, contact);
        }

        String sharedText = intent.getStringExtra(MobiComKitPeopleActivity.SHARED_TEXT);
        if (!TextUtils.isEmpty(sharedText)) {
            conversationFragment.sendMessage(sharedText);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        if (i == 0) {
            return false;
        }
        SpinnerNavItem spinnerNavItem = navSpinner.get(i);
        Contact contact = spinnerNavItem.getContact();
        contact.setContactNumber(spinnerNavItem.getContactNumber());
        //contact.setFormattedContactNumber(ContactNumberUtils.getPhoneNumber(spinnerNavItem.getContactNumber(), MobiComUserPreference.getInstance(this).getCountryCode()));
        conversationFragment.loadConversation(contact,null);
        return false;
    }

    //TODO: need to figure it out if this Can be improve by listeners in individual fragments
    @Override
    public void onBackPressed() {
        if (conversationFragment != null && conversationFragment.emoticonsFrameLayout.getVisibility() == View.VISIBLE) {
            conversationFragment.emoticonsFrameLayout.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
        this.finish();
    }
}
