package com.uactiv.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.api.conversation.ApplozicMqttIntentService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.service.ConversationService;
import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.MessageCommunicator;
import com.applozic.mobicomkit.uiwidgets.conversation.MobiComKitBroadcastReceiver;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComAttachmentSelectorActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobiComKitActivityInterface;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.MobicomLocationActivity;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.AudioMessageFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.ConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MultimediaOptionFragment;
import com.applozic.mobicomkit.uiwidgets.instruction.ApplozicPermissions;
import com.applozic.mobicommons.commons.core.utils.PermissionsUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.Conversation;
import com.applozic.mobicommons.people.contact.Contact;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.adapter.AttendingPeoplesAdapter;
import com.uactiv.adapter.MyAdapter;
import com.uactiv.adapter.QBChatAdapter;
import com.uactiv.application.UActiveApplication;
import com.uactiv.applozicchat.ApplozicChat;
import com.uactiv.controller.ResponseListener;
import com.uactiv.controller.StatusChangedListener;
import com.uactiv.fragment.Notification_Fragment;
import com.uactiv.fragment.Pastnotifiationfragment;
import com.uactiv.interfaces.OnAlertClickListener;
import com.uactiv.model.ChatDo;
import com.uactiv.model.FavouriteModel;
import com.uactiv.model.NotifyModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.utils.WorkaroundMapFragment;
import com.uactiv.views.CircularImageViews;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomEditText;
import com.uactiv.widgets.CustomTextView;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.applozic.mobicomkit.uiwidgets.ApplozicSetting.applozicSetting;
import static com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService.CONVERSATION_FRAGMENT;


public class PickUpGuest extends AppCompatActivity implements OnClickListener, AppConstants.SharedConstants,
        AppConstants.urlConstants, ResponseListener, MyAdapter.NotifiyListener, MyAdapter.onSelectAllItemClickListener,
        MessageCommunicator, MobiComKitActivityInterface,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback,getPickupback {


    ///////////---Applozic---///////////////
    public static final int LOCATION_SERVICE_ENABLE = 1001;
    public static final String GOOGLE_API_KEY_META_DATA = "com.google.android.geo.API_KEY";
    public static final String ACTIVITY_TO_OPEN_ONCLICK_OF_CALL_BUTTON_META_DATA = "activity.open.on.call.button.click";
    protected static final long UPDATE_INTERVAL = 500;
    protected static final long FASTEST_INTERVAL = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String API_KYE_STRING = "YOUR_GEO_API_KEY";
    private static final String TAG = PickUpGuest.class.getSimpleName();
    public static boolean ischat_enabled = false;
    private static Uri capturedImageUri;
    private static int retry;
    public LinearLayout layout;
    public Snackbar snackbar;
    private String myattenting;
    protected MobiComKitBroadcastReceiver mobiComKitBroadcastReceiver;
    protected GoogleApiClient googleApiClient;
    protected ConversationFragment conversation;
    String geoApiKey;
    String activityToOpenOnClickOfCallButton;
    boolean isTakePhoto;
    boolean isAttachment;
    Integer currentConversationId;
    ConversationUIService conversationUIService;
    GoogleMap googleMap;
    ScrollView ScrollView1;
    TextView tvFinger, tvChat;
    RelativeLayout shareLayout;
    ApplozicPermissions applozicPermission;
    AppContactService baseContactService;
    String urlString;
    private String isInvite;
    /////////////////////////////////////
    LinearLayout linLayout;
    ImageView imgBack;
    RecyclerView attending_recycle = null;
    // RecyclerView chat_window_recycle = null;
    RecyclerView mInvitesRecyclerView = null;
    ListView request_recycle = null;
    LinearLayoutManager manager = null;
    AttendingPeoplesAdapter mAdapter;
    AttendingPeoplesAdapter mInvitesAdapter;
    ArrayList<FavouriteModel> attending_items = new ArrayList<>();
    ArrayList<FavouriteModel> mInvitedPeoples = new ArrayList<>();
    ArrayList<FavouriteModel> request_items;
    String fragstr = "";
    GifImageView progressWheel = null;
    Intent intent = null;
    CustomTextView invite_count;
    RelativeLayout inviteLayout, invite_count_container;
    RelativeLayout mInvitePeopleLayout;
    CustomTextView tvName = null;
    CustomTextView tvGameDescription = null;
    CustomTextView tvDate = null;
    CustomTextView tvTime = null;
    CustomTextView tvLocation = null;
    CustomTextView tvSpots = null;
    CustomTextView tvAttending = null;
    CustomTextView tvGameName = null;
    CustomTextView tvspotleft = null;
    CustomEditText et_message = null;
    CustomButton btn_send = null;
    CustomTextView shareText = null;
    CustomButton fb_share = null;
    CustomButton twitter_share = null;
    RelativeLayout profileLayout = null;
    RelativeLayout challangeLay = null;
    CustomButton challangeShare = null;
    ImageView imgSpots = null;
    String status = "";
    CustomButton btn_accept = null;
    CustomButton btn_decline = null;
    String type = "";
    boolean isbuddyuprequest = false;
    boolean ispickuprequest = false;
    boolean isIspickuprequest_accepted = false;
    MyAdapter request_adapter = null;
    CustomTextView btnSelectAll = null;
    String idUser = null;
    QBChatAdapter mChatAdapterWeb;
    private String a;
    ArrayList<String> selectedMembersId = new ArrayList<>();
    ArrayList<FavouriteModel> new_attending_items = new ArrayList<>();
    int noOfpeole = 0;
    StatusChangedListener statusChangedListener = null;
    boolean isaccepted = false;
    boolean isdeclined = false;
    NotifyModel notify_model = null;
    int position;
    CustomTextView request_status = null;
    CustomTextView request_statusone = null;
    boolean ispickupinivite = false;
    CustomTextView optiontv1, optiontv2, optiontv3;
    View simpleview, simpleview1;
    int height = 0;
    int width = 0;
    String userID = null;
    Point p;
    String API_METHOD_NAME = null;
    JSONObject pickup_details = null;
    ResponseListener responseListener = null;
    ShareDialog shareDialog = null;
    boolean isMenuEnable = true;
    ImageView popup_menu;
    String isActive = null;
    CustomTextView tvProfileType;
    WorkaroundMapFragment fragmentById = null;
    boolean isfromnotification = false;
    boolean isFromDeepLinking = false;
    //SwipeRefreshLayout swipeRefreshLayout = null;
    ArrayList<ChatDo> chatDoArrayList = null;

    boolean isEventExpried = false;
    boolean isFromSchedule;
    boolean isUpComing;
    LinearLayout show_status_bar = null;
    LinearLayout show_status_barone = null;
    boolean canInvite = true;
    String mChatGroupId = null;
    String idschedule = null;
    public static int chat = 0;

    ApplozicChat applozicChat;
    ArrayList<String> channelMemberNames = new ArrayList<>();
    private Contact contact;
    private Channel mChannel;
    private Uri videoFileUri;
    private boolean isScheduleExists = true;
    private CallbackManager callbackManager;
    private String payflag;
    private String history_status = null;
    private String user_type;
    private CustomButton btn_payment;
    private LinearLayout show_payment_bar;
    private String pay_button;
    private String checkchat = null;
    private String game = null;
    private String itsnotification = "";
    private LinearLayout show_past_status_bar;
    private String notification_type = "";
    private CustomButton request_recreate;
    public static Activity pickupguest;
    private Boolean isHost = false;
    private String invited;

    LinearLayout show_status_bar_chat;
    CustomButton request_status_chat;
    public static getPickupback back;

    private OnAlertClickListener onAlertClickListener = new OnAlertClickListener() {
        @Override
        public void onClickPositive() {
            isScheduleExists = false;
            BuddyInvitesRequest(acceptinvites);
            Map<String, String> param = new HashMap<>(3);
            try {
                param.put("iduser", SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                param.put("screen", "buddy_up_request_accept");
                RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + "popup_detail", param, PickUpGuest.this, 10);
                Log.d("xxxxxxx", "3");
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

        }

        @Override
        public void onClickNegative() {
            isScheduleExists = true;
            /** finsh activity if nav  from notification as well as not showing challange pop up
             *
             */
            if (isfromnotification && (pickup_details != null && pickup_details.optInt("ischallenge") != 1)) {
                // finish();
            }
        }
    };


    //


    private final int UPDATE_CHAT_GROUP_ID = 101;
    private final int API_REQUEST_CODE_REQUET_TO_JOIN = 8;


    @Override
    public void addFragments(FragmentActivity fragmentActivity, Fragment fragmentToAdd, String fragmentTag) {
        show_status_bar.setVisibility(View.GONE);
        show_status_barone.setVisibility(View.GONE);
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.rrLayoutchat, fragmentToAdd,
                fragmentTag);

        if (supportFragmentManager.getBackStackEntryCount() > 1
                && !ConversationUIService.MESSGAE_INFO_FRAGMENT.equalsIgnoreCase(fragmentTag)) {
            supportFragmentManager.popBackStack();
        }
        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();
        supportFragmentManager.executePendingTransactions();

    }

    private void doInitializationOnChatModule() {
        new MobiComConversationService(this).processLastSeenAtStatus();
        geoApiKey = Utils.getMetaDataValue(this, GOOGLE_API_KEY_META_DATA);
        activityToOpenOnClickOfCallButton = Utils.getMetaDataValue(this, ACTIVITY_TO_OPEN_ONCLICK_OF_CALL_BUTTON_META_DATA);
        applozicPermission = new ApplozicPermissions(this, layout);
        if (Utils.hasMarshmallow()) {
            applozicPermission.checkRuntimePermissionForStorage();
        }
        retry = 0;
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(PickUpGuest.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        UserService.getInstance(this).processSyncUserBlock();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pickup_guest);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        btn_payment = (CustomButton) findViewById(R.id.btn_payment);
        show_payment_bar = (LinearLayout) findViewById(R.id.show_payment_bar);

        show_status_bar_chat=(LinearLayout)findViewById(R.id.show_status_bar_chat);
        request_status_chat=(CustomButton)findViewById(R.id.request_status_chat);


        pickupguest = PickUpGuest.this;
        back=(getPickupback)this;
        new LoadingData().execute();
        // notifyreciver();
        FacebookSdk.sdkInitialize(PickUpGuest.this); // Facebook sdk initiations.
        notifyCountChanged();
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(PickUpGuest.this);
        responseListener = this;
        intent = getIntent();
        initView();
        try {
            populateData();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        if (isfromnotification == false) {


            if (game == null) {
                try {
                    if (!type.equalsIgnoreCase("buddyup")) {
                        Utility.setEventTracking(getApplicationContext(), "", AppConstants.SCREEN_TRACKING_ID_BUDDYUPDETAILS);
                        if (fragstr.equalsIgnoreCase("map") && isActive.equalsIgnoreCase("active") && (status.equalsIgnoreCase("accepted"))) {
                            ischat_enabled = true;
                            Utility.setEventTracking(getApplicationContext(), "Buddy Up chat screen", AppConstants.SCREEN_TRACKING_ID_BUDDYUPCHAT);
                            if (isGroupIdNotNull()) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ConversationActivity.TAKE_CHAT_ALLOW = !(isUpComing) || status.equals(KEY_ABANDON) || isActive.equals(KEY_IN_ACTIVE) || status.equals(KEY_CANCEL);
                                        onChatScreen();

                                    }
                                }, 100);

                            }
                        } else if (type.equalsIgnoreCase("pickup") && status.equalsIgnoreCase("accepted") || status.equalsIgnoreCase("created")) {
                            if (notify_model != null) {

                                if (notify_model.getAttending_count() > 0) {
                                    ischat_enabled = true;
                                    Utility.setEventTracking(getApplicationContext(), "Pickup chat screen", AppConstants.SCREEN_TRACKING_ID_PICKUPCHATSCREEN);

                                    if (isGroupIdNotNull()) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ConversationActivity.TAKE_CHAT_ALLOW = !(isUpComing) || status.equals(KEY_ABANDON) || isActive.equals(KEY_IN_ACTIVE) || status.equals(KEY_CANCEL);
                                                onChatScreen();
                                            }
                                        }, 100);

                                    }
                                }
                            }

                        }
                    }
                } catch (NullPointerException ex) {

                }


            }
        }


    }


    private void populateData() {
        if (intent != null) {
            fragstr = intent.getStringExtra("fragment");
            status = intent.getStringExtra("status");
            isActive = intent.getStringExtra("sstatus");
            type = intent.getStringExtra("type");
            idschedule = intent.getStringExtra("idschedule");
            mChatGroupId = intent.getStringExtra("group_id");
            myattenting = intent.getStringExtra("attending");
            notification_type = intent.getStringExtra("notification_type");
            //  checkchat=intent.getStringExtra("checkchat");
            //getChannel(mChatGroupId);
            isFromSchedule = intent.getBooleanExtra("from_schedule", false);
            isfromnotification = intent.getBooleanExtra("isFromNotification", false);
            isUpComing = intent.getBooleanExtra("isUpComing", true);
            isFromDeepLinking = intent.getBooleanExtra("isDeepLinking", false);
            idUser = intent.getStringExtra("idUser");
            itsnotification = intent.getStringExtra("itsnotification");


            if (notification_type != null && notification_type != "") {
                if (notification_type.equalsIgnoreCase("PAST") && type.equals(KEY_BUDDY_UP)) {
                    show_past_status_bar.setVisibility(View.VISIBLE);
                    request_recreate.setText("Recreate this Buddy Up");

                } else if (notification_type.equalsIgnoreCase("PAST") && type.equals(KEY_PICK_UP)) {
                    isHost = true;

                } else {
                    show_past_status_bar.setVisibility(View.GONE);
                }
            }

            request_recreate.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (notification_type.equalsIgnoreCase("PAST") && type.equals(KEY_BUDDY_UP)) {
                        Intent buddyUpIntent = new Intent(PickUpGuest.this, BuddyUpRequest.class);
                        buddyUpIntent.putExtra("fromBuddyupPage", true);
                        buddyUpIntent.putExtra("image", notify_model.getImage());
                        buddyUpIntent.putExtra("buddyimage", notify_model.getBuddyimage());
                        buddyUpIntent.putExtra("ischallenge", notify_model.getIschallenge());
                        buddyUpIntent.putExtra("activity", notify_model.getActivity());
                        buddyUpIntent.putExtra("location", notify_model.getLocation());
                        buddyUpIntent.putExtra("date", notify_model.getDate());
                        buddyUpIntent.putExtra("fname", notify_model.getFname());
                        buddyUpIntent.putExtra("lname", notify_model.getLname());
                        buddyUpIntent.putExtra("isBookingOpen", notify_model.getIsBookingOpen());
                        buddyUpIntent.putExtra("start_time", notify_model.getStart_time());
                        buddyUpIntent.putExtra("end_time", notify_model.getEnd_time());
                        buddyUpIntent.putExtra("idbuddy", notify_model.getIdUser());
                        buddyUpIntent.putExtra("group_id", notify_model.getGroup_id());
                        buddyUpIntent.putExtra("message", notify_model.getMessage());
                        buddyUpIntent.putExtra("buddymessage", notify_model.getBuddyUpMsg());
                        startActivity(buddyUpIntent);
                        finish();
                    } else if (notification_type.equalsIgnoreCase("PAST") && type.equals(KEY_PICK_UP)) {
                        Intent pickUpIntent = new Intent(PickUpGuest.this, CreatePickUp.class);
                        pickUpIntent.putExtra("fromPickupPage", true);
                        Bundle bundle_pickup = new Bundle();
                        bundle_pickup.putString("no_of_people", String.valueOf(notify_model.getNo_of_people()));
                        bundle_pickup.putString("invited", invited);
                        bundle_pickup.putString("attending", String.valueOf(notify_model.getAttending_count()));
                        bundle_pickup.putString("idschedule", intent.getStringExtra("idschedule"));
                        bundle_pickup.putString("type", type);
                        bundle_pickup.putString("group_id", notify_model.getGroup_id());
                        bundle_pickup.putString("message", notify_model.getPickupmsg());
                        bundle_pickup.putString("activity", notify_model.getActivity());
                        bundle_pickup.putString("mode", "");
                        bundle_pickup.putString("date", notify_model.getDate());
                        bundle_pickup.putString("end_time", "");
                        bundle_pickup.putString("location", notify_model.getLocation());
                        bundle_pickup.putDouble("latitude", 19.0760);
                        bundle_pickup.putDouble("longitude", 72.8777);
                        bundle_pickup.putString("idbusiness", notify_model.getIdbusiness());
                        bundle_pickup.putString("idschedule", notify_model.getIdschedule());
                        bundle_pickup.putString("isBookingOpen", String.valueOf(notify_model.getIsBookingOpen()));
                        pickUpIntent.putExtras(bundle_pickup);
                        startActivity(pickUpIntent);

                    }
                }
            });

            if (itsnotification != null) {
                System.out.println("itsnotification");
            }

            game = intent.getStringExtra("game");
            try {
                if (idUser != null && mChatGroupId != null && status.equalsIgnoreCase("accepted")) {
                    try {
                        applozicChat.addParticipant(idUser, Integer.valueOf(mChatGroupId));

                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (NumberFormatException ex) {

            }


            System.out.println("refresh==" + fragstr + "-" + status + "-" + type + "-" + idschedule + "-" + mChatGroupId + "" + isFromSchedule + "-" + isfromnotification + "-" + isUpComing + "-" + isFromDeepLinking + "-" + idUser);


            try {
                statusChangedListener = Notification_Fragment.statusChangedListener;
                notify_model = (NotifyModel) intent.getSerializableExtra("notify_model");
                if (notify_model.getMessage().equalsIgnoreCase("accepted")) {
                    request_status.setTextColor(Color.parseColor("#4eba89"));
                }
                if (!(TextUtils.isEmpty(intent.getStringExtra("position")))) {
                    position = Integer.parseInt(intent.getStringExtra("position"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (fragstr != null) {
                if (!TextUtils.isEmpty(intent.getStringExtra("idschedule"))) {
                    getPickupDetailsFromAPI(intent.getStringExtra("idschedule")); // here is the API Call for getPickupdetails
                }
                if (fragstr.equals("map")) {
                    linLayout.setBackgroundResource(R.drawable.top_finger_icon_selected);
                    findViewById(R.id.bottomLayoutchat).setVisibility(View.GONE);
                    findViewById(R.id.rrLayoutchat).setVisibility(View.GONE);
                    show_status_bar_chat.setVisibility(View.GONE);
                    findViewById(R.id.rrLayout).setVisibility(View.VISIBLE);
                } else if (fragstr.equals("chat")) {
                    popup_menu.setVisibility(View.INVISIBLE);
                    ischat_enabled = true;
                    linLayout.setBackgroundResource(R.drawable.top_chat_icon_selected);
                    findViewById(R.id.show_status_bar).setVisibility(View.GONE);
                    findViewById(R.id.bottomLayoutchat).setVisibility(View.GONE);

                    findViewById(R.id.rrLayoutchat).setVisibility(View.VISIBLE);
                    show_status_bar_chat.setVisibility(View.GONE);

                    findViewById(R.id.rrLayout).setVisibility(View.GONE);
                    findViewById(R.id.bottomLayout).setVisibility(View.GONE); //  this is // TODO: 3/28/2017  changes in 28 march 2017
                    findViewById(R.id.bottomLayoutchat).setVisibility(View.GONE);


                    //mChatGroupId = idschedule;
                    //Log.d(TAG,"onCreate: Chat Id : "  + mChatGroupId);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isGroupIdNotNull()) {
                                ConversationActivity.TAKE_CHAT_ALLOW = !(isUpComing) || status.equals(KEY_ABANDON) || isActive.equals(KEY_IN_ACTIVE) || status.equals(KEY_CANCEL);
                                onChatScreen();
                            }
                        }
                    }, 300);

                }

            }
        }


    }

    private boolean isGroupIdNotNull() {
        return !(TextUtils.isEmpty(mChatGroupId) || mChatGroupId.equalsIgnoreCase("0") || mChatGroupId.equalsIgnoreCase("null"));
    }

    private void notifyreciver() {
        IntentFilter intentFilter = new IntentFilter(AppConstants.ACTION_PAYMENT_RECIVED);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mNewMessageReceived, intentFilter);
    }

    public void createChannelForOlderVersion(final ArrayList<String> channelMemberNames) {
        Log.d(TAG, "createChannelForOlderVersion");
        new AsyncTask<String, String, Channel>() {
            @Override
            protected Channel doInBackground(String... params) {
                ChannelInfo channelInfo = new ChannelInfo();
                //  channelInfo.setClientGroupId(idschedule);
                channelInfo.setGroupName(getChatGroupName());
                channelInfo.setGroupMemberList(channelMemberNames);
                return ChannelService.getInstance(PickUpGuest.this).createChannel(channelInfo);
            }

            @Override
            protected void onPostExecute(Channel channel) {
                super.onPostExecute(channel);
                Log.d(TAG, "new channel created onPostExecute : " + channel);
                if (channel != null) {
                    mChannel = channel;
                    mChatGroupId = String.valueOf(mChannel.getKey());
                    if (ischat_enabled) {
                        onChatScreen();
                    }
                    updateCharGroupId(String.valueOf(mChannel.getKey()));
                }


            }
        }.execute();
    }

    private void onChatScreen() {

        Log.d(TAG, "onChatScreen");

        if (isGroupIdNotNull()) {
            mChannel = ChannelService.getInstance(this).getChannel(Integer.parseInt(mChatGroupId));
            // Log.d(TAG,"mChannel : "+ mChannel);
            if (mChannel != null) {
                popup_menu.setVisibility(View.INVISIBLE);
                ischat_enabled = true;
                linLayout.setBackgroundResource(R.drawable.top_chat_icon_selected);
                findViewById(R.id.show_status_bar).setVisibility(View.GONE);
                findViewById(R.id.rrLayoutchat).setVisibility(View.VISIBLE);
                show_status_bar_chat.setVisibility(View.GONE);
                findViewById(R.id.rrLayout).setVisibility(View.GONE);
                findViewById(R.id.bottomLayout).setVisibility(View.GONE); //  this is // TODO: 3/28/2017  changes in 28 march 2017
                findViewById(R.id.bottomLayoutchat).setVisibility(View.GONE);
                Log.d(TAG, "TAKE_CHAT_ALLOW : " + !(ConversationActivity.TAKE_CHAT_ALLOW));

                if (isUpComing && !(ConversationActivity.TAKE_CHAT_ALLOW)) {
                    Log.d(TAG, "showbar no");
                /*if (isUpComing) {*/
                    //LinearLayout show_status_bar = (LinearLayout) findViewById(R.id.show_status_bar);
                    show_status_bar.setVisibility(View.GONE);

                } else {
                    //  LinearLayout show_status_bar = (LinearLayout) findViewById(R.id.show_status_bar);
                    Log.d(TAG, "showbar yes");
                    show_status_bar_chat.setVisibility(View.VISIBLE);
                    show_status_bar.setVisibility(View.VISIBLE);
                }
                if (conversation == null) {
                    Log.d(TAG, " replace channel : " + mChannel);
                    conversation = new ConversationFragment(null, mChannel, null);
                    addFragments(this, conversation, CONVERSATION_FRAGMENT);
                }
            } else {
                Log.d(TAG, "tring to create new channel for chat window becacuse there is no channel avaliable");
                if (isUpComing)
                    createNewgroupVersion(); // create group if not exsit
            }
        } else {
            Log.d(TAG, "tring to create new channel for chat window becacuse there is no group id avaliable");
            if (isUpComing)
                createNewgroupVersion(); //  create group if not exist
        }
    }


    private boolean isHost() {
        return userID.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
    }

    public void setMessageFromNotificationModel() {
        if (notify_model != null) {
            request_status.setText(notify_model.getMessage());
            request_status_chat.setText(notify_model.getMessage());

            if (notify_model.getMessage().equalsIgnoreCase("Buddy Up Completed!")) {
                request_status.setText("Completed!");
                request_status.setTextColor(Color.parseColor("#1CD591"));

                request_status_chat.setText("Completed!");
                request_status_chat.setBackgroundColor(Color.parseColor("#1CD591"));

            } else if (notify_model.getMessage().equalsIgnoreCase("Pick Up Completed!")) {
                request_status.setText("Completed!");
                request_status.setTextColor(Color.parseColor("#1CD591"));

                request_status_chat.setText("Completed");
                request_status_chat.setBackgroundColor(Color.parseColor("#1CD591"));

                mInvitePeopleLayout.setVisibility(View.GONE);

            } else if (notify_model.getMessage().contains("Pick Up Expired!")) {
                request_status.setText("Expired!");
                request_status.setTextColor(getResources().getColor(R.color.expirecolor));

                request_status_chat.setText("Expired");
                request_status_chat.setBackgroundColor(getResources().getColor(R.color.expirecolor));

                mInvitePeopleLayout.setVisibility(View.GONE);

            } else if (notify_model.getMessage().equalsIgnoreCase(" Buddy Up Expired")) {
                request_status.setText(R.string.msg_buddyup_request_expired);
                request_status.setTextColor(getResources().getColor(R.color.expirecolor));

                request_status_chat.setText("Expired");
                request_status_chat.setBackgroundColor(getResources().getColor(R.color.expirecolor));

            } else if (notify_model.getMessage().equalsIgnoreCase(" Pick Up Request Pending")) {
                request_status.setTextColor(getResources().getColor(R.color.pendingcolor));

                request_status_chat.setText("Pick Up Request Pending");
                request_status_chat.setBackgroundColor(getResources().getColor(R.color.pendingcolor));

            }
        }
    }

    private void showAcceptDeclinebtn() {

        Log.d(TAG, "status " + status);
        if (payflag.equalsIgnoreCase("1") && user_type.equalsIgnoreCase("1") && isFromDeepLinking == true && status.equalsIgnoreCase("")) {
            findViewById(R.id.show_payment_bar).setVisibility(View.VISIBLE);
            btn_payment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    urlString = "https://www.uactiv.com/payment.php?uid=" + SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId) + "&pid=" + idschedule + "&url=" + pay_button;
                    Log.d("paymenturl", urlString);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.android.chrome");
                    try {
                        startActivity(intent);
                        finish();
                    } catch (ActivityNotFoundException ex) {
                        Uri uri = Uri.parse(urlString);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setDataAndType(uri, "text/html");
                        browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                        browserIntent.setData(uri);
                        try {
                            startActivity(browserIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else if (payflag.equalsIgnoreCase("1") && user_type.equalsIgnoreCase("1") && isfromnotification == true && status.equalsIgnoreCase("")) {
            findViewById(R.id.show_payment_bar).setVisibility(View.VISIBLE);
            btn_payment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    urlString = "https://www.uactiv.com/payment.php?uid=" + SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId) + "&pid=" + idschedule + "&url=" + pay_button;
                    Log.d("paymenturl", urlString);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.android.chrome");
                    try {
                        startActivity(intent);
                        finish();
                    } catch (ActivityNotFoundException ex) {
                        Uri uri = Uri.parse(urlString);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setDataAndType(uri, "text/html");
                        browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                        browserIntent.setData(uri);
                        try {
                            startActivity(browserIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else

        {

            if (isfromnotification == true && payflag.equalsIgnoreCase("0") && status.equalsIgnoreCase("")) {
                show_status_bar.setVisibility(View.VISIBLE);
                show_status_barone.setVisibility(View.VISIBLE);
                request_statusone.setText(R.string.txt_request_to_join);
                request_status.setVisibility(View.VISIBLE);
                request_status.setText(R.string.txt_request_to_join);
            } else {
                show_status_bar.setVisibility(View.GONE);
                show_status_barone.setVisibility(View.GONE);
            }

        }


        //final LinearLayout show_status_bar = (LinearLayout) findViewById(R.id.show_status_bar);

        // status will return whether its requested,invited,rejected
        if (Utility.isNullCheck(status)) {
            Log.d(TAG, "Yes!.Its preparing bottom statusbar view");
            //((LinearLayout) findViewById(R.id.bottomLayout)).setVisibility(View.VISIBLE);
            if (type.equals(KEY_BUDDY_UP)) {
                //this block will execute if type is buddyup
                // show accept & decline button for to accept buddy up request if inivites else show status alone whether accepted or not.
                switch (status) {

                    case KEY_INVITED:
                        if (!isEventExpried && isUpComing) {
                            if (!status.equalsIgnoreCase("invited") && !type.equalsIgnoreCase("buddyup")) {
                                findViewById(R.id.bottomLayout).setVisibility(View.GONE);

                                fb_share.setVisibility(View.GONE);
                                twitter_share.setVisibility(View.GONE);
                                isbuddyuprequest = true;
                                Log.e("KEY_INVITED", "KEY_INVITED" + isbuddyuprequest);
                                isMenuEnable = false;
                            } else {
                                isbuddyuprequest = true;
                                findViewById(R.id.bottomLayout).setVisibility(View.VISIBLE);
                                isMenuEnable = false;
                            }

                        } else {
                            show_status_bar.setVisibility(View.VISIBLE);
                            setMessageFromNotificationModel();
                        }
                        if (isUpComing && isEventExpried) {
                            request_status.setText(R.string.msg_buddyup_request_expired);
                            request_status.setTextColor(getResources().getColor(R.color.expirecolor));

                            request_status_chat.setText(R.string.msg_buddyup_request_expired);
                            request_status_chat.setBackgroundColor(getResources().getColor(R.color.expirecolor));
                        }

                        break;
                    case KEY_ACCEPTED:
                        //Show bottombar text as BuddyUp Accepted.
                        if (!ischat_enabled) {
                            Utility.setEventTracking(PickUpGuest.this, "Buddy up detail page", "Buddy up Request acepted Button");


                            /** commented because of we have popup facebook share dialog
                             *
                             */
                            challangeShare.setVisibility(View.VISIBLE); /// to share challenge popup
                            show_status_bar.setVisibility(View.VISIBLE);
                            request_status.setText(R.string.msg_buddyup_accepted);
                            request_status.setTextColor(Color.parseColor("#4eba89"));
                            Utility.setScreenTracking(getApplicationContext(), "Buddy Up chat screen");
                        }
                        /** patch done by moorthy on 18-03-2016
                         *
                         */
                        if (!isUpComing) {
                            show_status_bar.setVisibility(View.VISIBLE);
//                            request_status.setText(R.string.msg_buddyup_accepted);

                            setMessageFromNotificationModel();
                            challangeShare.setVisibility(View.GONE); // disable while in past
                        }
                        /** patch done by moorthy on 18-03-2016
                         *
                         */
                        break;
                    case KEY_REJECTED:
                        //Show bottombar text as BuddyUp Rejected.
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setText(R.string.msg_buddyup_declined);
                        request_status.setTextColor(getResources().getColor(R.color.red));

                        request_status_chat.setText(R.string.msg_buddyup_declined);
                        request_status_chat.setBackgroundColor(getResources().getColor(R.color.red));

                        Utility.setEventTracking(PickUpGuest.this, "Buddy up detail page", "Buddy up Reject Button");
                        fb_share.setVisibility(View.GONE);
                        twitter_share.setVisibility(View.GONE);
                        shareLayout.setVisibility(View.GONE);
                        break;
                    case KEY_REQESTED:
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setText(R.string.msg_buddyup_request_pending);
                        request_status.setTextColor(getResources().getColor(R.color.pendingcolor));
                        Utility.setEventTracking(PickUpGuest.this, "Buddy up detail page", "Buddy up Request Button");
                        popup_menu.setVisibility(View.INVISIBLE); // To disable menu popup option.
                        break;
                    case KEY_ABANDON:
                        isMenuEnable = false;
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setTextColor(getResources().getColor(R.color.red));
                        Utility.setEventTracking(PickUpGuest.this, "Buddy up detail page", "Buddy up Request cancel");

                        //request_status.setText(R.string.msg_buddyup_abanded);
                        //Patch changed by moorthy
                        //For buddyup only text changed for abandon buddy up as cancel buddyup..
                        request_status.setText(R.string.msg_buddy_up_cancelled);

                        request_status_chat.setText(R.string.msg_buddy_up_cancelled);
                        request_status_chat.setBackgroundColor(getResources().getColor(R.color.red));

                        fb_share.setVisibility(View.GONE);
                        twitter_share.setVisibility(View.GONE);
                        shareLayout.setVisibility(View.GONE);
                        //Patch changed by moorthy
                        break;

                    case KEY_CANCEL:
                        isMenuEnable = true;
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setTextColor(getResources().getColor(R.color.red));
                        request_status.setText(R.string.msg_buddy_up_cancelled);

                        request_status_chat.setText(R.string.msg_buddy_up_cancelled);
                        request_status_chat.setBackgroundColor(getResources().getColor(R.color.red));

                        fb_share.setVisibility(View.GONE);
                        twitter_share.setVisibility(View.GONE);
                        shareLayout.setVisibility(View.GONE);
                        break;
                    //Patch done by Jeeva on 2-02-16
                    case KEY_CREATED:
                        fb_share.setVisibility(View.GONE);
                        twitter_share.setVisibility(View.GONE);
                        isMenuEnable = true;
                        if (!isEventExpried) {
                            show_status_bar.setVisibility(View.VISIBLE);
                            request_status.setTextColor(getResources().getColor(R.color.pendingcolor));
                            request_status.setText(R.string.msg_buddyup_request_pending);
                        }

                        if (!isUpComing) {
                            show_status_bar.setVisibility(View.VISIBLE);
//                            request_status.setText(R.string.msg_buddyup_request_expired);
                            setMessageFromNotificationModel();
                        }
                        break;
                    //Patch done by Jeeva on 2-02-16
                    default:
                        break;
                }


            } else if (type.equals(KEY_PICK_UP)) {

                canInvite = true;
                String status_pickup = status.trim();
                Log.d("inside_status_pickup", status_pickup);

                switch (status_pickup.toLowerCase(Locale.getDefault())) {
                    case KEY_CREATED:
                        Log.d("inside_key_created", KEY_CREATED);
                        Log.e("Called", "PU KEY_PICKUP_COMPLETED");
                        if (!isUpComing) {
                            Log.d("inside_isumcomingd", isUpComing + "");
                            show_status_bar.setVisibility(View.VISIBLE);
                        } else {
                            if (payflag.equalsIgnoreCase("1") && user_type.equalsIgnoreCase("1") && isFromDeepLinking == true) {
                                findViewById(R.id.show_payment_bar).setVisibility(View.VISIBLE);
                                btn_payment.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        urlString = "https://www.uactiv.com/payment.php?uid=" + SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId) + "&pid=" + idschedule + "&url=" + pay_button;
                                        Log.d("paymenturl", urlString);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setPackage("com.android.chrome");
                                        try {
                                            startActivity(intent);
                                            finish();
                                        } catch (ActivityNotFoundException ex) {
                                            Uri uri = Uri.parse(urlString);
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                            browserIntent.setDataAndType(uri, "text/html");
                                            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                                            browserIntent.setData(uri);
                                            try {
                                                startActivity(browserIntent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            } else if (isfromnotification == true && payflag.equalsIgnoreCase("1") && user_type.equalsIgnoreCase("1") && status.equalsIgnoreCase("created")) {

                                findViewById(R.id.show_payment_bar).setVisibility(View.VISIBLE);
                                btn_payment.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        urlString = "https://www.uactiv.com/payment.php?uid=" + SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId) + "&pid=" + idschedule + "&url=" + pay_button;
                                        Log.d("paymenturl", urlString);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setPackage("com.android.chrome");
                                        try {
                                            startActivity(intent);
                                            finish();
                                        } catch (ActivityNotFoundException ex) {
                                            Uri uri = Uri.parse(urlString);
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                            browserIntent.setDataAndType(uri, "text/html");
                                            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                                            browserIntent.setData(uri);
                                            try {
                                                startActivity(browserIntent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });


                            } else {
                                if (isfromnotification == true && payflag.equalsIgnoreCase("0") && status.equalsIgnoreCase("")) {
                                    show_status_bar.setVisibility(View.VISIBLE);
                                    show_status_barone.setVisibility(View.VISIBLE);
                                    request_statusone.setText(R.string.txt_request_to_join);
                                    request_status.setVisibility(View.VISIBLE);
                                    request_status.setText(R.string.txt_request_to_join);
                                } else {
                                    show_status_bar.setVisibility(View.GONE);
                                    show_status_barone.setVisibility(View.GONE);
                                }
                            }


                        }
                        setMessageFromNotificationModel();
                        break;
                    case KEY_INVITED:


                        Log.d("invitedd", "isEventExpried : " + isEventExpried + " isUpComing : " + isUpComing + "payflag" + payflag + "history" + history_status + "user_type" + user_type + "paybtn " + pay_button);

                        if (!isEventExpried && isUpComing) {
                            if (payflag.equalsIgnoreCase("1") && user_type.equalsIgnoreCase("1") && isFromDeepLinking == true && history_status.equalsIgnoreCase("null")) {
                                findViewById(R.id.show_payment_bar).setVisibility(View.VISIBLE);
                                btn_payment.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        urlString = "https://www.uactiv.com/payment.php?uid=" + SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId) + "&pid=" + idschedule + "&url=" + pay_button;
                                        Log.d("paymenturl", urlString);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setPackage("com.android.chrome");
                                        try {
                                            startActivity(intent);
                                            finish();
                                        } catch (ActivityNotFoundException ex) {
                                            Uri uri = Uri.parse(urlString);
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                            browserIntent.setDataAndType(uri, "text/html");
                                            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                                            browserIntent.setData(uri);
                                            try {
                                                startActivity(browserIntent);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });

                            } else {
                                if (payflag.equalsIgnoreCase("1") && user_type.equalsIgnoreCase("1") && history_status.equalsIgnoreCase("invited")) {
                                    findViewById(R.id.show_payment_bar).setVisibility(View.VISIBLE);
                                    btn_payment.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            urlString = "https://www.uactiv.com/payment.php?uid=" + SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId) + "&pid=" + idschedule + "&url=" + pay_button;
                                            Log.d("paymenturl", urlString);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.setPackage("com.android.chrome");
                                            try {
                                                startActivity(intent);
                                                finish();
                                            } catch (ActivityNotFoundException ex) {
                                                Uri uri = Uri.parse(urlString);
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                                browserIntent.setDataAndType(uri, "text/html");
                                                browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                                                browserIntent.setData(uri);
                                                try {
                                                    startActivity(browserIntent);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    findViewById(R.id.bottomLayout).setVisibility(View.VISIBLE);
                                    findViewById(R.id.show_payment_bar).setVisibility(View.GONE);
                                    isbuddyuprequest = true; //it may change
                                    ispickupinivite = true;
                                    Log.e("PIKUP", "KEY_INVITED");
                                    isMenuEnable = false;
                                }
                            }


                        } else {
                            show_status_bar.setVisibility(View.VISIBLE);
                            //  request_status.setText(R.string.msg_pickup_request_expired);

                            setMessageFromNotificationModel();
                          /*  if (!isHost()) {
                                show_status_bar.setVisibility(View.VISIBLE);
                                request_status.setText(R.string.msg_pickup_request_expired);
                            } else {
                                show_status_bar.setVisibility(View.GONE);
                            }*/
                        }
                        if (isUpComing && isEventExpried) {
                            request_status.setText(R.string.msg_pickup_request_expired);
                            request_status.setTextColor(getResources().getColor(R.color.expirecolor));

                            request_status_chat.setText(R.string.msg_pickup_request_expired);
                            request_status_chat.setBackgroundColor(getResources().getColor(R.color.expirecolor));
                        }
                        break;
                    case KEY_ACCEPTED:
                        //Show bottombar text as Pickup Accepted.
                        Log.e("PIKUP", "KEY_ACCEPTED");
                        if (!ischat_enabled) {
                            if (!isHost()) {
                                inviteLayout.setVisibility(View.GONE);
                                show_status_bar.setVisibility(View.VISIBLE);
                                request_status.setText(R.string.msg_pickup_request_accepted);
                                request_status.setTextColor(Color.parseColor("#4eba89"));
                            } else {
                                show_status_bar.setVisibility(View.GONE);
                            }
                        }

                        /** patch done by moorthy on 18-03-2016
                         *
                         */

                        if (!isUpComing) {
                            show_status_bar.setVisibility(View.VISIBLE);
                            // request_status.setText(R.string.msg_pickup_request_expired);
                            setMessageFromNotificationModel();


                        }
                        /*else
                        {
                            show_status_bar.setVisibility(View.VISIBLE);
                            request_status.setText(notify_model.getMessage());
                        }*/
                        /** patch done by moorthy on 18-03-2016
                         *
                         */

                        break;
                    case KEY_REJECTED:
                        //Show bottombar text as pickup Rejected.
                        Log.e("PIKUP", "KEY_REJECTED");
                        if (!isHost()) {
                            show_status_bar.setVisibility(View.VISIBLE);
                            Log.e("showAcceptDeclinebtn", "PU Declined");
                            request_status.setText(R.string.msg_pickup_req_declined);
                            request_status.setTextColor(getResources().getColor(R.color.red));

                            request_status_chat.setText(R.string.msg_pickup_req_declined);
                            request_status_chat.setBackgroundColor(getResources().getColor(R.color.red));
                            isMenuEnable = false;
                        } else {
                            show_status_bar.setVisibility(View.GONE);
                        }
                        canInvite = false;
                        shareLayout.setVisibility(View.GONE);
                        break;
                    case KEY_REQESTED:
                        if (!isHost()) {
                            if (!isEventExpried) {
                                inviteLayout.setVisibility(View.GONE);
                                show_status_bar.setVisibility(View.VISIBLE);
                                request_status.setText(R.string.msg_pickup_request_pending);
                                request_status.setTextColor(getResources().getColor(R.color.pendingcolor));
                                ispickuprequest = true;
                            } else {
                                inviteLayout.setVisibility(View.GONE);
                                show_status_bar.setVisibility(View.VISIBLE);
                                request_status.setText(R.string.msg_pickup_request_expired);
                                request_status.setTextColor(getResources().getColor(R.color.expirecolor));

                                request_status_chat.setText("Expired");
                                request_status_chat.setBackgroundColor(getResources().getColor(R.color.expirecolor));
                            }
                        } else {
                            show_status_bar.setVisibility(View.GONE);
                        }
                        Log.e("PIKUP", "KEY_REQESTED");
                        break;

                    case KEY_ABANDON:
                        if (!isHost()) {
                            isMenuEnable = false;
                            show_status_bar.setVisibility(View.VISIBLE);
                            request_status.setText(R.string.msg_pickup_abandoned);
                            request_status.setTextColor(getResources().getColor(R.color.red));
                        } else {
                            show_status_bar.setVisibility(View.GONE);
                        }
                        shareLayout.setVisibility(View.GONE);
                        canInvite = false;
                        break;

                    case KEY_CANCEL:
                        isMenuEnable = false;
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setText(R.string.msg_pickup_request_cancelled);
                        request_status.setTextColor(getResources().getColor(R.color.red));
                        request_status_chat.setText(R.string.msg_buddy_up_cancelled);
                        request_status_chat.setBackgroundColor(getResources().getColor(R.color.red));
                        shareLayout.setVisibility(View.GONE);
                        canInvite = false;
                        break;
                    case KEY_CHANGED:
                        if (isfromnotification) {
                            show_status_bar.setVisibility(View.VISIBLE);
                            request_status.setText(R.string.msg_schedule_changed);
                            request_status.setTextColor(getResources().getColor(R.color.green));
                        }
                        break;


                    /** added for past notification scnario to show status bar
                     * 17-3-2016
                     */

                    /*case KEY_CREATED:
                        if (!isUpComing) {
                            request_status.setText(R.string.msg_pickup_created);
                            request_status.setBackgroundColor(getResources().getColor(R.color.green));
                            request_status.setVisibility(View.VISIBLE);
                        }
                        break;*/
/** added for past notification scnario to show status bar
 * 17-3-2016
 */
                    default:
                        break;
                }

            }
        } else if (isFromSchedule) {

            Log.d(TAG, "Yes!.Its preparing schedule layout");

            if (isUpComing) {
                if (type.equals(KEY_BUDDY_UP)) {
                    show_status_bar.setVisibility(View.GONE);
                    request_status.setText(R.string.msg_buddyup_accepted);
                    request_status.setTextColor(Color.parseColor("#4eba89"));

                    challangeShare.setVisibility(View.VISIBLE);
                    /** commented because of we have popup facebook share dialog
                     *
                     */

                   /* fb_share.setVisibility(View.VISIBLE);
                    twitter_share.setVisibility(View.VISIBLE);*/


                    /** commented because of we have popup facebook share dialog
                     *
                     */
                }
            } else {
                switch (type) {
                    case KEY_PICK_UP:
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setText(R.string.msg_pick_up_completed);
                        break;
                    case KEY_BUDDY_UP:
                        show_status_bar.setVisibility(View.VISIBLE);
                        request_status.setText(R.string.msg_buddy_up_completed);
                        break;
                }
            }
        }

        Log.d(TAG, "isFromDeepLinking :" + isFromDeepLinking);

        if (isFromDeepLinking && !(Utility.isNullCheck(status))) {
            // TODO: 2/10/2017 this is for to manage that,deeplinking url enable request join features
            Log.d(TAG, "setup request to join");
            if (Utility.isNullCheck(type)) {
                switch (type) {
                    case KEY_PICK_UP:
                        if (!(isHost()) && !(isBusinessAccount())) {
                            show_status_bar.setVisibility(View.VISIBLE);
                            request_status.setVisibility(View.VISIBLE);
                            request_status.setText(R.string.txt_request_to_join);
                        }
                        break;
                }
            }
        }

        //** to disable menu option for past notification
        if (!isUpComing) {
            popup_menu.setVisibility(View.INVISIBLE);
            fb_share.setVisibility(View.GONE);
            twitter_share.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
        }
    }

    private boolean isBusinessAccount() {
        if (SharedPref.getInstance().getBooleanValue(this, isbussiness)) {
            return true;
        }
        return false;
    }

    private BroadcastReceiver mNewMessageReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, String.valueOf(intent));
            Message messageItem = null;
            if (intent.getAction().equalsIgnoreCase(AppConstants.ACTION_PAYMENT_RECIVED)) {
                Log.d("payment Recived", "payment");

            }
        }
    };

    /***/
    private void showLinkDialog(String[] links) {
        final Dialog linkDialog = new Dialog(this, R.style.Theme_Dialog);
        linkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        linkDialog.setContentView(R.layout.url_link_dialog);
        LinearLayout linkUrl_Container = (LinearLayout) linkDialog.findViewById(R.id.linkUrl_Container);

        for (int i = 0; i < links.length; i++) {
            TextView linkUrl = new TextView(this);
            linkUrl.setText(links[i]);
            linkUrl.setTag(links[i]);
            linkUrl.setTextSize(getResources().getDimension(R.dimen.text_size_3));
            linkUrl.setTextColor(getResources().getColor(R.color.com_facebook_button_send_background_color_pressed));
            linkUrl.setPaintFlags(linkUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            linkUrl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    String url = v.getTag().toString();
                    if (!url.startsWith("http://") && !url.startsWith("https://"))
                        url = "http://" + url;
                    Log.e("Link ", "" + url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
            });
            linkUrl_Container.addView(linkUrl);
        }
        linkDialog.setCanceledOnTouchOutside(true);
        linkDialog.setCancelable(true);
        linkDialog.show();
    }

    private void updateCharGroupId(String groupId) {

        if (Utility.isConnectingToInternet(PickUpGuest.this)) {
            try {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                Map<String, String> param = new HashMap<>();
                param.put("idschedule", "" + idschedule);
                param.put("group_id", "" + groupId);
                RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants
                                .getBaseUrl(SharedPref.getInstance()
                                        .getBooleanValue(PickUpGuest.this, isStaging)) + updateGroupId,
                        param, this, UPDATE_CHAT_GROUP_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(PickUpGuest.this);
        }

    }

    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private int getAttendeeCout() {
        return attending_items.size();
    }


    private void updatePickUpUI(JSONObject ObjectDetails) throws JSONException {

        pickup_details = ObjectDetails;

       /* *//** this is not recommendable but for applozic push notification we have to keep this :(
         *
         *//*
        status  = ObjectDetails.optString("history_status");
        *//** this is not recommendable but for applozic push notification we have to keep this :(
         *
         */
        if (notify_model != null) {
            notify_model.setIdbusiness(pickup_details.optString("idbusiness"));
        }


      try {
            if(pickup_details.optJSONArray("invited")!=null){
                if (pickup_details.optJSONArray("invited").length() > 0) {
                    invite_count.setVisibility(View.VISIBLE);
                    //invite_count_container.setVisibility(View.VISIBLE);
                    invite_count.setText("" + (pickup_details.optJSONArray("invited").length()));
                } else {
                    invite_count.setVisibility(View.GONE);
                }
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();

        }


        int left = -1;

        if (Utility.isNullCheck(ObjectDetails.optString("iduser"))) {
            userID = ObjectDetails.optString("iduser");
            if (isFromDeepLinking || isfromnotification) {
                popup_menu.setVisibility(View.GONE);
                payflag = ObjectDetails.optString("pay_flag");
                user_type = ObjectDetails.optString("user_type");
                history_status = ObjectDetails.optString("history_status");
                pay_button = ObjectDetails.optString("pay_button");


                Log.d(TAG, "payflag : " + payflag);
            } else {
                history_status = ObjectDetails.optString("history_status");
                payflag = ObjectDetails.optString("pay_flag");
                user_type = ObjectDetails.optString("user_type");
                pay_button = ObjectDetails.optString("pay_button");
            }

        }

        if (Utility.isNullCheck(ObjectDetails.optString("no_of_people"))) {
            try {
                noOfpeole = ObjectDetails.optInt("no_of_people");
                notify_model.setNo_of_people(noOfpeole);
                notify_model.setPickupmsg(ObjectDetails.optString("message"));
                if (noOfpeole <= 0) {
                    tvspotleft.setText("( Unlimited spots )");
                    btnSelectAll.setVisibility(View.VISIBLE);
                } else {
                    btnSelectAll.setVisibility(View.INVISIBLE);
                    //Patch done by Jeeva on 2-02-16
                    left = noOfpeole - ObjectDetails.optJSONArray("attending").length();
                    if (left == 1) {
                        tvspotleft.setText("( " + left + " spot left )"); // Should not show -1 spots left like that ---->>>> issue fixed.
                    } else if (left == 0 || left > 1) {
                        tvspotleft.setText("( " + left + " spots left )");
                    }
                    //Patch done by Jeeva on 2-02-16
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (!(TextUtils.isEmpty(ObjectDetails.optString("type")))) {
            type = ObjectDetails.optString("type");
            if (type.equals(KEY_BUDDY_UP)) {
                inviteLayout.setVisibility(View.GONE);
                tvProfileType.setVisibility(View.GONE); //Remove hosting label if buddyup
                shareLayout.setVisibility(View.GONE);
                shareText.setText("Share this buddy Up!");
                if (notify_model != null) {
                    notify_model.setIschallenge(ObjectDetails.optString("ischallenge"));
                    notify_model.setBuddyUpMsg(ObjectDetails.getString("message"));
                }
                if (ObjectDetails.optString("ischallenge").equals("1")) {
                    challangeLay.setVisibility(View.VISIBLE);
                    updateChallangeLay(ObjectDetails);
                } else {
                    challangeLay.setVisibility(View.GONE); //if not challage this event disable challange card!
                }
            } else if (type.equals(KEY_PICK_UP)) {
                if (isHost == true && isHost()) {
                    show_past_status_bar.setVisibility(View.VISIBLE);
                    request_recreate.setText("Recreate this Pick Up");
                }

                if (isHost() && isActive.equalsIgnoreCase(KEY_ACTIVE)) {
                    mInvitePeopleLayout.setVisibility(View.VISIBLE);


                } else if (isHost()) {

                } else {
                    mInvitePeopleLayout.setVisibility(View.GONE);
                }

                tvProfileType.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.VISIBLE);

                if (ObjectDetails.optString("iduser").equals(SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId))) {
                    //Show Request card & Waiting list;
                    //When attending got 9/9 then if any request to showlike waiting list
                    //update Requestlist in View

                    if (ObjectDetails.optJSONArray("requests") != null && ObjectDetails.optJSONArray("requests").length() > 0) {

                        if (left == 0 && noOfpeole > 0) {
                            ((CustomTextView) findViewById(R.id.tvRequests)).setText(getResources().getString(R.string.msg_title_waiting_list));
                        }
                        findViewById(R.id.requestLayout).setVisibility(View.VISIBLE);
                        addListItemsForRequest(ObjectDetails); // Add requested members into request card view.
                        if (request_items != null && request_items.size() > 0 && noOfpeole == 0) {
                            btnSelectAll.setVisibility(View.VISIBLE);
                        }

                    } else {
                        findViewById(R.id.requestLayout).setVisibility(View.GONE);
                    }

                    //show invites list card
                    if (ObjectDetails.optJSONArray("invited") != null && ObjectDetails.optJSONArray("invited").length() > 0) {
                        findViewById(R.id.inviteLayout).setVisibility(View.VISIBLE);
                        addListItemsForInvites(ObjectDetails);
                        invited = ObjectDetails.optJSONArray("invited").toString();
                    } else {
                        findViewById(R.id.inviteLayout).setVisibility(View.GONE);
                    }

                }
            }
        }

        if (!TextUtils.isEmpty(ObjectDetails.optString("image"))) {

            //Here needs to update HostImage According to the type buddyup

            //USER A - Login display USER B pic.

            switch (type) {

                case KEY_BUDDY_UP:


                    if (ObjectDetails.optString("buddyimage") != null) {

                        Utility.setImageUniversalLoader(PickUpGuest.this, AppConstants.getiamgebaseurl()+ObjectDetails.optString("buddyimage"), (CircularImageViews) findViewById(R.id.imageView1));
                        if (notify_model != null) {
                            notify_model.setImage(AppConstants.getiamgebaseurl()+ObjectDetails.optString("buddyimage"));
                            notify_model.setBuddyimage(ObjectDetails.optString("image"));

                            if (!TextUtils.isEmpty(ObjectDetails.optString("buddyfirstname"))) {
                                notify_model.setFname(ObjectDetails.optString("buddyfirstname"));
                                notify_model.setLname(ObjectDetails.optString("buddylastname"));
                                notify_model.setIsBookingOpen(Boolean.parseBoolean(ObjectDetails.getString("is_booked")));
                                notify_model.setGroup_id(ObjectDetails.getString("group_id"));
                                notify_model.setIdUser(pickup_details.optString("idbuddy"));

                                //notify_model.setMessage(ObjectDetails.getString("message"));

                            }
                        }
                        try {
                            if (ObjectDetails.getString("message").contains("completed") || ObjectDetails.getString("message").equalsIgnoreCase("")) {
                                request_status.setText("Completed!");
                            }


                            tvName.setText(ObjectDetails.optString("buddyfirstname") + " " + ObjectDetails.optString("buddylastname"));


                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }

                    }


                    break;
                case KEY_PICK_UP:

                    Utility.setImageUniversalLoader(PickUpGuest.this, ObjectDetails.optString("image"), (CircularImageViews) findViewById(R.id.imageView1));
                    if (!TextUtils.isEmpty(ObjectDetails.optString("firstname"))) {
                        tvName.setText(ObjectDetails.optString("firstname") + " " + ObjectDetails.optString("lastname"));
                    }


                    break;
            }

        }

        if (!TextUtils.isEmpty(ObjectDetails.optString("activity"))) {

            if (type.equals(KEY_PICK_UP)) {
                tvGameName.setText(ObjectDetails.optString("activity") + " - " + "Pick Up");

            } else if (type.equals(KEY_BUDDY_UP)) {
                tvGameName.setText(ObjectDetails.optString("activity") + " - " + "Buddy Up");
            }
        }


        if (Utility.isNullCheck(ObjectDetails.optString("message"))) {
            tvGameDescription.setVisibility(View.VISIBLE);
            tvGameDescription.setText(StringEscapeUtils.unescapeJava(ObjectDetails.optString("message")));

        } else {
            tvGameDescription.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(ObjectDetails.optString("date"))) {
            String date = Utility.dateFormatWithFull(ObjectDetails.optString("date"));
            if (!TextUtils.isEmpty(date)) {
                tvDate.setText(date);
            }
        }
        if (!TextUtils.isEmpty(ObjectDetails.optString("location"))) {
            tvLocation.setText(ObjectDetails.optString("location"));
            if (notify_model != null) {
                notify_model.setLocation(ObjectDetails.optString("location"));
            }
        }

        if (type.equals(KEY_PICK_UP)) {
            if (!TextUtils.isEmpty(ObjectDetails.optString("no_of_people"))) {
                if (ObjectDetails.optInt("no_of_people") <= 0 || ObjectDetails.optString("no_of_people").equals("No limit")) {
                    tvSpots.setText("Unlimited spots");
                } else {
                    //Patch done by Jeeva on 2-02-16.
                    if (ObjectDetails.optInt("no_of_people") > 1) {
                        tvSpots.setText(ObjectDetails.optInt("no_of_people") + " spots");
                    } else if (ObjectDetails.optInt("no_of_people") == 1) {
                        tvSpots.setText(ObjectDetails.optInt("no_of_people") + " spot");
                    }
                    //Patch done by Jeeva on 2-02-16.
                }
            }
        } else if (type.equals(KEY_BUDDY_UP)) {
            tvSpots.setVisibility(View.GONE);
            imgSpots.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(ObjectDetails.optString("start_time"))) {
            String start_time = Utility.timeFormatChanage(ObjectDetails.optString("start_time"));
            String end_time = Utility.timeFormatChanage(ObjectDetails.optString("end_time"));
            if (notify_model != null) {
                notify_model.setStart_time(start_time);
                notify_model.setEnd_time(end_time);
            }


            if (!TextUtils.isEmpty(start_time)) {
                if (!TextUtils.isEmpty(end_time)) {
                    tvTime.setText(start_time + " - " + end_time);
                } else {
                    tvTime.setText(start_time + "");
                }
            }

            if (type.equals(KEY_BUDDY_UP)) {
                isEventExpried = Utility.endTimelogic(ObjectDetails.optString("start_time") + " " + ObjectDetails.optString("date"), AppConstants.TIME_LIMT_BUDDY_UP);
            } else if (type.equals(KEY_PICK_UP)) {
                isEventExpried = Utility.endTimelogic(ObjectDetails.optString("start_time") + " " + ObjectDetails.optString("date"), AppConstants.TIME_LIMT_PICK_UP);
            }
            Log.d(TAG, "isEventExpried : " + String.valueOf(isEventExpried));
        }

        if (type.equals(KEY_PICK_UP)) {

            if (!TextUtils.isEmpty(ObjectDetails.optString("no_of_people"))) {

                if (ObjectDetails.optInt("no_of_people") <= 0 || ObjectDetails.optString("no_of_people").equals("No limit")) {
                    try{
                        tvAttending.setText("Attending (" + ObjectDetails.optJSONArray("attending").length() + ")");
                        if (ObjectDetails.optJSONArray("attending").length() <= 0) {
                            attending_recycle.setVisibility(View.GONE);
                        }
                    }catch (NullPointerException ex){
                        tvAttending.setText("Attending (" + 0 + ")");
                        attending_recycle.setVisibility(View.GONE);

                    }

                } else {
                    try {
                        tvAttending.setText("Attending (" + ObjectDetails.optJSONArray("attending").length() + "/" + ObjectDetails.optString("no_of_people") + ")");
                        if (ObjectDetails.optJSONArray("attending").length() <= 0) {
                            attending_recycle.setVisibility(View.GONE);
                        }
                    }catch (NullPointerException ex){
                        ex.printStackTrace();
                 //       tvAttending.setText("Attending (" + ObjectDetails.optJSONArray("attending").length() + "/" + ObjectDetails.optString("no_of_people") + ")");
                        attending_recycle.setVisibility(View.GONE);

                    }

                }
            }
        } else if (type.equals(KEY_BUDDY_UP)) {
            profileLayout.setVisibility(View.GONE);
        }


        /** check whether listed pickup or buddyup state is active or not.,.
         *
         */
        if (!ischat_enabled)
            popup_menu.setVisibility(View.VISIBLE);

        Log.d(TAG, "Prepare Bottom status bar");

        setBottomStatusBar();

        addGoogleMap(ObjectDetails.optDouble("latitude"), ObjectDetails.optDouble("longitude"));


        if (isFromDeepLinking) {
            popup_menu.setVisibility(View.INVISIBLE);
        }
    }


    private boolean isPickUpDetailsIsNotNull() {
        if (pickup_details != null) {
            return true;
        }
        return false;
    }

    private boolean isAttendeeDetailsIsNotNull() {
        if (isPickUpDetailsIsNotNull() && pickup_details.optJSONArray(KEY_JSON_ARRAY_ATTENDEE) != null) {
            return true;
        }
        return false;
    }

    private boolean isCurrentUserIsAttendee() {

        if (isPickUpDetailsIsNotNull() && isAttendeeDetailsIsNotNull()) {
            for (int i = 0; i < pickup_details.optJSONArray(KEY_JSON_ARRAY_ATTENDEE).length(); i++) {
                if (pickup_details.optJSONArray(KEY_JSON_ARRAY_ATTENDEE).optJSONObject(i).optString("idmember").equalsIgnoreCase(getUserId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setBottomStatusBar() {

        if (isActive != null) {

            Log.d(TAG, "isActive :" + isActive + " type " + type);

            if (isActive != null && isActive.equals(KEY_ACTIVE)) { //state is active show accept decline button

                Log.d(TAG, "schedule is in active mode");
                showAcceptDeclinebtn();


            } else if (isActive != null && type != null && isActive.equals(KEY_IN_ACTIVE)) {

                isMenuEnable = false;
                //       final LinearLayout show_status_bar = (LinearLayout) findViewById(R.id.show_status_bar);
                show_status_bar.setVisibility(View.VISIBLE);
                request_status.setVisibility(View.VISIBLE);

                Log.d(TAG, "schedule is in Inactive mode");

                switch (type) {

                    case KEY_BUDDY_UP:
                        Log.d(TAG, "buddy up");
                        if (status.equals(KEY_ABANDON)) {
                            //changed message abanon to cancel buddyup
                            isMenuEnable = true;
                            request_status.setText(R.string.msg_buddy_up_cancelled);
                            request_status_chat.setText(R.string.msg_buddy_up_cancelled);
                            request_status_chat.setBackgroundColor(getResources().getColor(R.color.red));
                        } else if (status.equals(KEY_REJECTED)) {
                            request_status.setText(R.string.msg_buddyup_declined);
                            request_status.setTextColor(getResources().getColor(R.color.red));
                        } else {
                            isMenuEnable = true;
                            request_status.setText(R.string.msg_buddy_up_cancelled);
                            request_status_chat.setText(R.string.msg_buddy_up_cancelled);
                            request_status_chat.setBackgroundColor(getResources().getColor(R.color.red));
                        }
                        request_status.setTextColor(ContextCompat.getColor(this, R.color.red));

                        if (userID.equals(SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId))) {
                            popup_menu.setVisibility(View.VISIBLE);
                        }

                        break;
                    case KEY_PICK_UP:
                        Log.d(TAG, "pick up");
                        shareLayout.setVisibility(View.GONE);
                        request_status.setText(R.string.msg_pickup_cancelled);
                        request_status.setTextColor(ContextCompat.getColor(this, R.color.red));
                        request_status_chat.setText(R.string.msg_buddy_up_cancelled);
                        request_status_chat.setBackgroundColor(getResources().getColor(R.color.red));
                        if (userID.equals(SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId))) {
                            popup_menu.setVisibility(View.INVISIBLE);
                        }

                        switch (status) {
                            case KEY_ABANDON:
                                request_status.setText(R.string.msg_pickup_abandoned);
                                break;
                            case KEY_REJECTED:
                                request_status.setText(R.string.msg_pickup_req_declined);
                                break;
                            case KEY_CANCEL:
                                if (isHost()) {
                                    request_status.setText(R.string.msg_pickup_cancelled);
                                } else {
                                    if (getAttendeeCout() > 0) {
                                        boolean isAttendee = isCurrentUserIsAttendee();
                                        if (isAttendee) {
                                            request_status.setText(R.string.msg_pickup_cancelled);
                                        } else {
                                            request_status.setText(R.string.msg_pickup_request_cancelled);
                                        }
                                    } else {
                                        request_status.setText(R.string.msg_pickup_request_cancelled);
                                    }
                                }
                                // request_status.setText(R.string.msg_pickup_cancelled);
                                break;
                            case KEY_ACCEPTED:
                                request_status.setText(R.string.msg_pickup_cancelled);
                                break;
                            case KEY_INVITED:
                                request_status.setText(R.string.msg_pickup_cancelled);
                                break;
                            case KEY_REQESTED:
                                request_status.setText(R.string.msg_pickup_request_pending);
                                request_status.setTextColor(getResources().getColor(R.color.pendingcolor));
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
        }

    }

    /**
     * update challenage card profilePics
     *
     * @param ObjectDetails
     */

    private void updateChallangeLay(JSONObject ObjectDetails) {
        Log.d("iiiiii",new Gson().toJson(ObjectDetails));
        if (ObjectDetails != null) {
            if (Utility.isNullCheck(ObjectDetails.optString("image"))) {
                if (notify_model != null) {
                    notify_model.setImage(ObjectDetails.optString("image"));
                }

                //Utility.setImageUniversalLoader(PickUpGuest.this, ObjectDetails.optString("buddyimage"), (CircularImageView) findViewById(R.id.imageView));
                //Utility.setImageUniversalLoader(PickUpGuest.this, ObjectDetails.optString("image"), (CircularImageView) findViewById(R.id.imageView));
            }

            if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(PickUpGuest.this, image))) {
                //Utility.setImageUniversalLoader(PickUpGuest.this, SharedPref.getInstance().getStringVlue(PickUpGuest.this, image), (CircularImageView) findViewById(R.id.imageView3));
                //Utility.setImageUniversalLoader(PickUpGuest.this, ObjectDetails.optJSONObject("opponent").optString("image"), (CircularImageView) findViewById(R.id.imageView3));
            }

            Utility.setImageUniversalLoader(PickUpGuest.this, ObjectDetails.optString("image"), (CircularImageViews) findViewById(R.id.imageView3));
            Utility.setImageUniversalLoader(PickUpGuest.this, ObjectDetails.optString("buddyimage"), (CircularImageViews) findViewById(R.id.imageView));
            CustomTextView challengerText = (CustomTextView) findViewById(R.id.tvChallenge);
            //challengerText.setText(SharedPref.getInstance().getStringVlue(PickUpGuest.this, firstname) + " has challenged " + ObjectDetails.optString("buddyfirstname") + " to " + ObjectDetails.optString("activity"));
            //challengerText.setText(SharedPref.getInstance().getStringVlue(PickUpGuest.this, firstname) + " has challenged " + ObjectDetails.optString("buddyfirstname") + " to " + ObjectDetails.optString("activity") + " on " + getResources().getString(R.string.app_name) + " !");
            challengerText.setText(ObjectDetails.optString("firstname") + " has challenged " + ObjectDetails.optString("buddyfirstname") + " to " + ObjectDetails.optString("activity") + " on " + getResources().getString(R.string.app_name) + " !");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ischat_enabled = false; //added for instant notification
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mobiComKitBroadcastReceiver);
    }

    public void initView() {
        applozicChat = new ApplozicChat(PickUpGuest.this);
        layout = (LinearLayout) findViewById(R.id.footerAd);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(PickUpGuest.this, progressWheel);
        tvFinger = (TextView) findViewById(R.id.tvFinger);
        tvChat = (TextView) findViewById(R.id.tvChat);
        linLayout = (LinearLayout) findViewById(R.id.linLayout);
        ScrollView1 = (ScrollView) findViewById(R.id.ScrollView1);
        shareLayout = (RelativeLayout) findViewById(R.id.shareLayout);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        attending_recycle = (RecyclerView) findViewById(R.id.attending_recycle);
        mInvitesRecyclerView = (RecyclerView) findViewById(R.id.invites_recycle);
        show_status_bar = (LinearLayout) findViewById(R.id.show_status_bar);
        show_status_barone = (LinearLayout) findViewById(R.id.show_status_barone);
        tvName = (CustomTextView) findViewById(R.id.tvName);
        tvGameName = (CustomTextView) findViewById(R.id.tvGameName);
        tvGameDescription = (CustomTextView) findViewById(R.id.tvGameDescription);
        tvDate = (CustomTextView) findViewById(R.id.tvDate);
        tvTime = (CustomTextView) findViewById(R.id.tvTime);
        tvLocation = (CustomTextView) findViewById(R.id.tvLocation);
        tvSpots = (CustomTextView) findViewById(R.id.tvSpots);
        tvAttending = (CustomTextView) findViewById(R.id.tvAttending);
        tvspotleft = (CustomTextView) findViewById(R.id.tvspotleft);
        profileLayout = (RelativeLayout) findViewById(R.id.profileLayout);
        imgSpots = (ImageView) findViewById(R.id.imgSpots);
        challangeLay = (RelativeLayout) findViewById(R.id.challengelay);
        challangeShare = (CustomButton) findViewById(R.id.dialog_share);
        challangeShare.setOnClickListener(this);
        challangeShare.setVisibility(View.GONE);
        request_recycle = (ListView) findViewById(R.id.request_list);
        btn_accept = (CustomButton) findViewById(R.id.btn_accept);
        btn_decline = (CustomButton) findViewById(R.id.btn_decline);
        request_status = (CustomTextView) findViewById(R.id.request_status);
        request_statusone = (CustomTextView) findViewById(R.id.request_statusone);
        request_status.setOnClickListener(this);
        request_statusone.setOnClickListener(this);
        shareText = (CustomTextView) findViewById(R.id.shareText);
        fb_share = (CustomButton) findViewById(R.id.share);
        twitter_share = (CustomButton) findViewById(R.id.tweet);
        tvProfileType = (CustomTextView) findViewById(R.id.tvProfileType);

        et_message = (CustomEditText) findViewById(R.id.et_messagebox);
        btn_send = (CustomButton) findViewById(R.id.btn_send);
        btnSelectAll = (CustomTextView) findViewById(R.id.select_all);
        invite_count = (CustomTextView) findViewById(R.id.invite_count);
        invite_count_container = (RelativeLayout) findViewById(R.id.invite_count_container);
        invite_count_container.setVisibility(View.GONE);
        inviteLayout = (RelativeLayout) findViewById(R.id.inviteLayout);
        mInvitePeopleLayout = (RelativeLayout) findViewById(R.id.invitelayouts);
        show_past_status_bar = (LinearLayout) findViewById(R.id.show_past_status_bar);
        request_recreate = (CustomButton) findViewById(R.id.request_recreate);
        btnSelectAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSelectAll.getText().toString().equalsIgnoreCase("Deselect All")) {
                    request_adapter.onClickDeSelectAll();
                    btnSelectAll.setText("Select All");
                } else if (btnSelectAll.getText().toString().equalsIgnoreCase("Select All")) {
                    request_adapter.onClickSelectAll();
                    btnSelectAll.setText("Deselect All");
                }
            }
        });
        request_recreate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "WIP", Toast.LENGTH_LONG).show();
            }
        });
        btnSelectAll.setVisibility(View.INVISIBLE);
        btn_send.setOnClickListener(this);
        fragmentById = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = (fragmentById).getMap();
       /* ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);*/
        fb_share.setOnClickListener(this);
        twitter_share.setOnClickListener(this);
        btn_accept.setOnClickListener(this);
        btn_decline.setOnClickListener(this);
        //onclick
        tvFinger.setOnClickListener(this);
        tvChat.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        challangeLay.setOnClickListener(this);

        popup_menu = (ImageView) findViewById(R.id.gridIcon);


        mInvitePeopleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FavouriteModel> already_invitedAndAttednig = new ArrayList<FavouriteModel>();
                ArrayList<FavouriteModel> final_SelectedFavID = new ArrayList<>();
                ArrayList<String> seleted_GroupId = new ArrayList<>();
                ArrayList<String> selectedInviteFav = new ArrayList<>();
                //Add already invited user
                try {
                    JSONArray invitedPeople = new JSONArray(pickup_details.optJSONArray("invited").toString());
                    if (invitedPeople != null && invitedPeople.length() > 0) {
                        for (int i = 0; i < invitedPeople.length(); i++) {
                            already_invitedAndAttednig.add(new FavouriteModel(invitedPeople.optJSONObject(i).optString("idmember"),
                                    invitedPeople.optJSONObject(i).optString("firstname") + " " + invitedPeople.optJSONObject(i).optString("lastname"),
                                    invitedPeople.optJSONObject(i).optString("image")));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //add who are all in attnding list

                int count = 0;
                try {
                    JSONArray invitedPeople = new JSONArray(pickup_details.optJSONArray("attending").toString());
                    if (invitedPeople != null && invitedPeople.length() > 0) {
                        for (int i = 0; i < invitedPeople.length(); i++) {
                            already_invitedAndAttednig.add(new FavouriteModel(invitedPeople.optJSONObject(i).optString("idmember"), invitedPeople.optJSONObject(i).optString("firstname") + " " + invitedPeople.optJSONObject(i).optString("lastname"), invitedPeople.optJSONObject(i).optString("image")));
                        }
                    }
                    count = Integer.parseInt(pickup_details.optString("no_of_people"));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (canInvite) {
                    Intent i = new Intent(PickUpGuest.this, InvitePeopleFav.class);
                    i.putExtra(activity, pickup_details.optString("activity"));
                    i.putExtra("isEdit", true);
                    i.putExtra("isPickupGest", true);
                    i.putExtra("idschedule", intent.getStringExtra("idschedule"));
                    i.putExtra(invitecount, count);
                    i.putExtra(modekey, pickup_details.optString("mode"));
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("invited_peoples", already_invitedAndAttednig);
                    bundle.putSerializable("SelectedFavID", selectedInviteFav);
                    bundle.putSerializable("SelectedFavDetails", final_SelectedFavID);
                    bundle.putSerializable("seleted_GroupId", seleted_GroupId);
                    i.putExtras(bundle);
                    startActivityForResult(i, 2);
                }

            }
        });

        popup_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (p != null) {
                    showPopup(PickUpGuest.this, p);
                }
            }

        });


        final CircularImageViews circleImageView = (CircularImageViews) findViewById(R.id.imageView1);
        circleImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_buddydetails = null;

                if (TextUtils.isEmpty(type)) {
                    return;
                }
                switch (type) {

                    case KEY_BUDDY_UP:

                        if (pickup_details != null) {

                            if (pickup_details.optString("idbuddy") != null && pickup_details.optString("user_type") != null) {

                                if (pickup_details.optInt("user_type") == USER_TYPE_APP) {
                                    intent_buddydetails = new Intent(PickUpGuest.this, BuddyUpDetailsActivity.class);

                                } else if (pickup_details.optInt("user_type") == USER_TYPE_BUSINESS) {
                                    intent_buddydetails = new Intent(PickUpGuest.this, BusinessDetailsActivity.class);
                                }
                                // intent_buddydetails = new Intent(PickUpGuest.this, BuddyUpDetailsActivity.class);
                                intent_buddydetails.putExtra("view", true);
                                intent_buddydetails.putExtra("userid", pickup_details.optString("idbuddy"));

                                intent_buddydetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent_buddydetails);
                                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                            }
                        }
                        break;
                    case KEY_PICK_UP:

                        if (pickup_details != null && pickup_details.optString("user_type") != null) {

                            if (pickup_details.optInt("user_type") == USER_TYPE_APP) {

                                intent_buddydetails = new Intent(PickUpGuest.this, BuddyUpDetailsActivity.class);

                            } else if (pickup_details.optInt("user_type") == USER_TYPE_BUSINESS) {

                                intent_buddydetails = new Intent(PickUpGuest.this, BusinessDetailsActivity.class);
                            }

                            //intent_buddydetails = new Intent(PickUpGuest.this, BuddyUpDetailsActivity.class);
                            intent_buddydetails.putExtra("view", true);
                            intent_buddydetails.putExtra("userid", userID);
                            intent_buddydetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent_buddydetails);
                            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

                        }


                        break;
                    default:
                        break;
                }
            }
        });

        findViewById(R.id.imgBack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * here call API to get previous message by api's
     */

    private void refreshItems() {
        // swipeRefreshLayout.setRefreshing(false);
    }


    /**
     * send message to user B
     */

    private void sendMessage() {
        if (Utility.isConnectingToInternet(PickUpGuest.this)) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date today = Calendar.getInstance().getTime();
            String reportDate = simpleDateFormat.format(today);

            Log.e("reportDate", "" + reportDate);

            try {
                Map<String, String> param = new HashMap<>();
                param.put("idschedule", "" + intent.getStringExtra("idschedule"));
                param.put("iduser", "" + SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                param.put("message", et_message.getText().toString());
                param.put("local_time", reportDate);
                if (pickup_details.optString("iduser").equals(SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId))) {

                    param.put("creater", "owner");
                } else {
                    param.put("creater", "member");
                }
                //progressWheel.setVisibility(View.VISIBLE);
                //progressWheel.startAnimation();
                btn_send.setEnabled(false);
                RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + savechat, param, this, 6); //send message

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(PickUpGuest.this);
        }


    }

    /**
     * get pickup details & buddyup details to show
     *
     * @param idSchedule
     */

    private void getPickupDetailsFromAPI(String idSchedule) {

        if (Utility.isConnectingToInternet(PickUpGuest.this)) {

            try {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                Map<String, String> param = new HashMap<>();
                if (!AppConstants.isGestLogin(PickUpGuest.this)) {
                    param.put("idschedule", "" + idSchedule);
                    param.put("iduser", "" + SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                } else {
                    param.put("idschedule", "" + idSchedule);
                    param.put("iduser", "0");
                }

                if (status != null) {
                    param.put("isread", "1");
                }
                if (!AppConstants.isGestLogin(PickUpGuest.this)) {
                    RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + pickupdetail, param, this, 0);

                } else {
                    RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + "pickupdetail_guest", param, this, 0);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(PickUpGuest.this);
        }

    }

    private void addListItemsForInvites(JSONObject jsonObject) {
        mInvitedPeoples = new ArrayList<FavouriteModel>();
        mInvitedPeoples.clear();
        if (jsonObject != null) {
            try {
                JSONArray attendingarray = jsonObject.optJSONArray("invited");

                if (attendingarray != null && attendingarray.length() > 0) {
                    mInvitesRecyclerView.setVisibility(View.VISIBLE);
                    ((CustomTextView) findViewById(R.id.tvInvitesCount)).setText("Invited (" + attendingarray.length() + ")");
                    for (int i = 0; i < attendingarray.length(); i++) {
                        JSONObject rowjson = attendingarray.optJSONObject(i);
                        mInvitedPeoples.add(new FavouriteModel(rowjson.optString("idmember"), rowjson.optString("firstname") + " " + rowjson.optString("lastname"), AppConstants.getiamgebaseurl()+rowjson.optString("image"))); //AppConstants.getiamgebaseurl()+attendingarray.getJSONObject(i).getJSONArray(("images")).getJSONObject(0).getString("image")
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        manager = new LinearLayoutManager(PickUpGuest.this, LinearLayoutManager.HORIZONTAL, false);
        mInvitesRecyclerView.setLayoutManager(manager);
        mInvitesAdapter = new AttendingPeoplesAdapter(PickUpGuest.this, mInvitedPeoples);
        mInvitesRecyclerView.setAdapter(mInvitesAdapter);


    }

    /**
     * add members to attending card view
     *
     * @param jsonObject
     */

    private void addListItemsForAttending(JSONObject jsonObject) {
        attending_items = new ArrayList<>();
        attending_items.clear();

        if (jsonObject != null) {
            try {
                JSONArray attendingarray = jsonObject.optJSONArray("attending");
                if (attendingarray != null && attendingarray.length() > 0) {
                    attending_recycle.setVisibility(View.VISIBLE);
                    for (int i = 0; i < attendingarray.length(); i++) {
                        JSONObject rowjson = attendingarray.optJSONObject(i);
                        attending_items.add(new FavouriteModel(rowjson.optString("idmember"), rowjson.optString("firstname"), rowjson.optString("image")));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        manager = new LinearLayoutManager(PickUpGuest.this, LinearLayoutManager.HORIZONTAL, false);
        attending_recycle.setLayoutManager(manager);
        mAdapter = new AttendingPeoplesAdapter(PickUpGuest.this, attending_items);
        attending_recycle.setAdapter(mAdapter);

    }

    private void addListItemsForRequest(JSONObject jsonObject) {

        request_items = new ArrayList<FavouriteModel>();
        request_items.clear();

        if (jsonObject != null) {
            try {
                JSONArray attendingarray = jsonObject.optJSONArray("requests");
                ispickuprequest = true;
                if (attendingarray != null && attendingarray.length() > 0) {
                    Log.e("requests length()", "" + attendingarray.length());
                    for (int i = 0; i < attendingarray.length(); i++) {
                        JSONObject rowjson = attendingarray.optJSONObject(i);
                        request_items.add(new FavouriteModel(rowjson.optString("idmember"), rowjson.optString("firstname") + " " + rowjson.optString("lastname"), rowjson.optString("image")));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request_recycle.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        request_adapter = new MyAdapter(PickUpGuest.this, request_items, noOfpeole - attending_items.size(), noOfpeole);
        request_adapter.setOnNotifyChagedListener(this);
        request_adapter.setOnSelectAllItemListener(this);
        request_recycle.setAdapter(request_adapter);


    }


    //It will also act as waiting list

    private void addGoogleMap(final double lat, final double longi) {

        try {
            if (fragmentById != null) {
                //googleMap = (fragmentById).getMap();
                googleMap.getUiSettings().setScrollGesturesEnabled(false);


                if (googleMap != null) {
                    LatLng latLng = new LatLng(lat, longi);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.green_mappin)));
                }


                fragmentById.setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        ScrollView1.requestDisallowInterceptTouchEvent(true);
                        Intent intent = new Intent(PickUpGuest.this, PickUpGuest_Map.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("lati", lat);
                        intent.putExtra("longi", longi);
                        startActivity(intent);
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean canAbleToChat() {
        if (!(TextUtils.isEmpty(status))) {
            return status.equals(KEY_ABANDON) ||
                    status.equals(KEY_ACCEPTED) ||
                    status.equals(KEY_CANCEL) ||
                    status.equals(KEY_COMPLETED);
        }
        return false;

    }


    @Override
    public void onClick(View v) {
        if (v == shareLayout) {

            if (pickup_details != null && Utility.isNullCheck(pickup_details.optString("sharelink"))) {
                String message = getString(R.string.pickup_share_text);
                String share_imag_url = pickup_details.optString("sharelink");
                AppConstants.showShareAlert(PickUpGuest.this, "Check out this exciting Pick Up on UACTIV!", message, share_imag_url, shareDialog);
                if (itsnotification != "") {
                    Utility.setEventTracking(PickUpGuest.this, "Pick up detail from Notifications screen", "Share pickup in Pick up detail page from notification screen");

                }

            }
        } else if (v == imgBack) {
            onBackPressed();
        } else if (v == tvFinger) {

            AppConstants.hideViewKeyBoard(PickUpGuest.this, et_message);

            if (pickup_details == null) {
                return;
            }

            showRequestAcceptDeclineButtons();

            ischat_enabled = false;

            if (Utility.isNullCheck(type)) {

                if (isfromnotification) {
                    Log.d(TAG, "showAcceptDeclinebtn 1");
                   /* if (isHost()) {
                        show_status_bar.setVisibility(View.GONE);
                    } else {
                        show_status_bar.setVisibility(View.VISIBLE);
                    }*/
                    showAcceptDeclinebtn();


                } else {
                    if (!(isFromSchedule)) {
                        Log.d(TAG, "showAcceptDeclinebtn 2");
                        //showAcceptDeclinebtn(); // chnaged for attendee pick up request cancelled status
                        setBottomStatusBar();
                    } else {
                        if (type.equals(KEY_BUDDY_UP)) {
                            show_status_bar.setVisibility(View.GONE);
                        }
                    }
                }

            }
            if (!isUpComing) {
                popup_menu.setVisibility(View.INVISIBLE);
            } else {
                popup_menu.setVisibility(View.VISIBLE);
            }
            linLayout.setBackgroundResource(R.drawable.top_finger_icon_selected);
            findViewById(R.id.bottomLayoutchat).setVisibility(View.GONE);
            findViewById(R.id.rrLayoutchat).setVisibility(View.GONE);
            findViewById(R.id.rrLayout).setVisibility(View.VISIBLE);
            show_status_bar_chat.setVisibility(View.GONE);

          /*  if (!isUpComing) {
                show_status_bar.setVisibility(View.VISIBLE);
            }*/

        }
        String message = getResources().getString(R.string.buddyup_share_text);

        switch (v.getId()) {

            case R.id.btn_accept:

                isaccepted = true;

                Log.e("OnClick", "Accepted" + " isbuddyuprequest" + isbuddyuprequest);
                Log.e("OnClick", "Accepted" + " ispickuprequest" + ispickuprequest);

                if (isbuddyuprequest) {

                    Log.e("OnClick", "BuddyUp");

                    BuddyInvitesRequest(acceptinvites);
                    Utility.setEventTracking(PickUpGuest.this, "BuddyUp detail page", AppConstants.EVENT_TRACKING_ID_BUDDYUP_ACCEPT);
                    Utility.setEventTracking(PickUpGuest.this, "BuddyUp detail page", "Buddyup accept from Buddyup up detail page from notification screen");
                } else if (ispickuprequest) {

                    String waiting_List_check = ((CustomTextView) findViewById(R.id.tvRequests)).getText().toString();

                    if (!waiting_List_check.equalsIgnoreCase("Waitings")) {
                        isIspickuprequest_accepted = true;
                        PickupInvitesRequest(acceptpickuprequest);
                        Utility.setEventTracking(PickUpGuest.this, "Pick Up detail page", AppConstants.EVENT_TRACKING_ID_PICKUP_ACCEPT);
                        Utility.setEventTracking(PickUpGuest.this, "Pick Up detail page", "Pickup accept from pick up detail page from notification screen");
                    } else {
                        Toast.makeText(PickUpGuest.this, "Maximum no peoples for game already added!", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
            case R.id.btn_decline:

                Log.e("OnClick", "Decline" + " isbuddyuprequest" + isbuddyuprequest);
                Log.e("OnClick", "Decline" + " ispickuprequest" + ispickuprequest);
                isaccepted = false;
                isdeclined = true;
                if (isbuddyuprequest) {

                    Log.e("OnClick", "BuddyUp");

                    BuddyInvitesRequest(declineinvites);
                    Utility.setEventTracking(PickUpGuest.this, "Buddyup Up detail page", AppConstants.EVENT_TRACKING_ID_BUDDYUP_DECLINE);
                    Utility.setEventTracking(PickUpGuest.this, "Buddyup Up detail page", "Buddyup declines from Buddyup detail page from notification screen");

                } else if (ispickuprequest) {

                    String waiting_List_check = ((CustomTextView) findViewById(R.id.tvRequests)).getText().toString();

                    if (!waiting_List_check.equals("Waitings")) {

                        Log.e("OnClick", "PickUp");
                        isIspickuprequest_accepted = false;
                        PickupInvitesRequest(declinepickuprequest);
                        Utility.setEventTracking(PickUpGuest.this, "Pick Up detail page", AppConstants.EVENT_TRACKING_ID_PICKUP_DECLINE);

                    } else {
                        Toast.makeText(PickUpGuest.this, "Maximum no peoples for game already added!", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.share:
                AppConstants.facebookShare(PickUpGuest.this, shareDialog, pickup_details.optString("activity") + " " + message);
                break;
            case R.id.tweet:
                AppConstants.shareTwitter(PickUpGuest.this, pickup_details.optString("activity") + " " + message);
                break;
            /*case R.id.btn_send:
                if (et_message.length() > 0) {
                    if (pickup_details != null) {
                        sendMessage();
                    }
                }
                break;*/
            case R.id.dialog_share:
                if (pickup_details != null) {
                    challengeSharedialog();
                }
                break;
            case R.id.tvChat:
                if (pickup_details != null) {
                    if (status != null && type != null) {
                        Log.d(TAG, "chat user status : " + status);
                        Utility.setScreenTracking(PickUpGuest.this, "Pick Up chat screen");
                        Utility.setEventTracking(PickUpGuest.this, "Pick up detail from Notifications screen", "chat toggel button on pickup detail from notification.");

                        loadChatFragment();
                    } else if (isFromSchedule) {
                        Log.d(TAG, "Schedule Attending: " + attending_items.size());
                        if (attending_items != null && attending_items.size() > 0) {
                            ConversationActivity.TAKE_CHAT_ALLOW = false;
                            ischat_enabled = true;
                            onChatScreen();
                            // loadChatFragment();
                        } else {
                            //Utility.showToastMessage(PickUpGuest.this, "No people are added in " + KEY_PICK_UP + " chat group!");
                            if (!userID.equalsIgnoreCase("")) {
                                if (userID.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
                                    Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when  your " + getScheduleName() + " is accepted.");
                                } else {
                                    Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when you accept this " + getScheduleName());
                                }
                            } else {
                                if (userID.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
                                    Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when User's Join " + getScheduleName());
                                } else {
                                    Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when you are part of this" + getScheduleName());
                                }
                            }
                        }
                    }
                }

                break;
            case R.id.request_status:
                Log.d(TAG, "onclick");
                if (isFromDeepLinking && !(Utility.isNullCheck(status))) {
                    Log.d(TAG, "onclick1");
                    if (request_status.getText().toString().equalsIgnoreCase(getString(R.string.txt_request_to_join))) {
                        Log.d(TAG, "onclick12");
                        if (Utility.isConnectingToInternet(PickUpGuest.this)) {
                            Log.d(TAG, "onclick3");
                            requestToJoin();
                        } else {
                            Utility.showInternetError(PickUpGuest.this);
                        }
                    }
                } else if (isfromnotification && status.equalsIgnoreCase("created")) {
                    Log.d(TAG, "onclick1");
                    if (request_status.getText().toString().equalsIgnoreCase(getString(R.string.txt_request_to_join))) {
                        Log.d(TAG, "onclick12");
                        if (Utility.isConnectingToInternet(PickUpGuest.this)) {
                            Log.d(TAG, "onclick3");
                            requestToJoin();
                        } else {
                            Utility.showInternetError(PickUpGuest.this);
                        }
                    }
                } else if (isfromnotification && status.equalsIgnoreCase("")) {
                    Log.d(TAG, "onclick1");
                    if (request_status.getText().toString().equalsIgnoreCase(getString(R.string.txt_request_to_join))) {
                        Log.d(TAG, "onclick12");
                        if (Utility.isConnectingToInternet(PickUpGuest.this)) {
                            Log.d(TAG, "onclick3");
                            requestToJoin();
                        } else {
                            Utility.showInternetError(PickUpGuest.this);
                        }
                    }
                }
                break;
            case R.id.request_statusone:
                Log.d(TAG, "onclick");
                if (isFromDeepLinking && !(Utility.isNullCheck(status))) {
                    Log.d(TAG, "onclick1");
                    if (request_statusone.getText().toString().equalsIgnoreCase(getString(R.string.txt_request_to_join))) {
                        Log.d(TAG, "onclick12");
                        if (Utility.isConnectingToInternet(PickUpGuest.this)) {
                            Log.d(TAG, "onclick3");
                            requestToJoin();
                        } else {
                            Utility.showInternetError(PickUpGuest.this);
                        }
                    }
                } else if (isfromnotification && status.equalsIgnoreCase("created")) {
                    Log.d(TAG, "onclick1");
                    if (request_statusone.getText().toString().equalsIgnoreCase(getString(R.string.txt_request_to_join))) {
                        Log.d(TAG, "onclick12");
                        if (Utility.isConnectingToInternet(PickUpGuest.this)) {
                            Log.d(TAG, "onclick3");
                            requestToJoin();
                        } else {
                            Utility.showInternetError(PickUpGuest.this);
                        }
                    }
                } else if (isfromnotification && status.equalsIgnoreCase("")) {
                    Log.d(TAG, "onclick1");
                    if (request_statusone.getText().toString().equalsIgnoreCase(getString(R.string.txt_request_to_join))) {
                        Log.d(TAG, "onclick12");
                        if (Utility.isConnectingToInternet(PickUpGuest.this)) {
                            Log.d(TAG, "onclick3");
                            requestToJoin();
                        } else {
                            Utility.showInternetError(PickUpGuest.this);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void loadChatFragment() {

       /* if (isFromSchedule) {
            ConversationActivity.TAKE_CHAT_ALLOW = false;
            onChatScreen();
        } else {*/
        if (type.equalsIgnoreCase(KEY_BUDDY_UP)) {

            if (canAbleToChat()) {
                // disable the chat box
// enable the chat box
                ConversationActivity.TAKE_CHAT_ALLOW = !(isUpComing) || status.equals(KEY_ABANDON) || isActive.equals(KEY_IN_ACTIVE) || status.equals(KEY_CANCEL);
                ischat_enabled = true;
                chat = 1;
                show_status_bar.setVisibility(View.GONE);
                onChatScreen();
            } else {
                //Utility.showToastMessage(PickUpGuest.this, "No people are added in " + KEY_BUDDY_UP + " chat group!");
                // Utility.showToastMessage(PickUpGuest.this, getScheduleName() + " not active");

                if (!userID.equalsIgnoreCase("")) {
                    if (userID.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
                        Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when  your " + getScheduleName() + " is accepted.");
                    } else {
                        Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when you accept this " + getScheduleName());
                    }
                } else {
                    if (userID.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
                        Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when User's Join " + getScheduleName());
                    } else {
                        Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when you are part of this" + getScheduleName());
                    }
                }

            }

        } else {

            boolean isAllowForCreatedUser = (pickup_details.optJSONArray("attending") != null && getAttendeeCout() > 0) && (status.equals(KEY_CREATED));

            if (canAbleToChat() || isAllowForCreatedUser) {

                if (getAttendeeCout() > 0 || pickup_details.optJSONArray("abandoned").length() > 0) {
                    ischat_enabled = true;
                    //if (isGroupIdNotNull()) {
                    boolean isAllow = (!(isUpComing) || status.equals(KEY_ABANDON) || isActive.equals(KEY_IN_ACTIVE) || status.equals(KEY_CANCEL));
                    ConversationActivity.TAKE_CHAT_ALLOW = isAllow;
                    show_status_bar.setVisibility(View.GONE);
                    onChatScreen();

                } else {
                    if (!userID.equalsIgnoreCase("")) {
                        if (userID.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
                            Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when User's Join " + getScheduleName());
                        } else {
                            Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when you are part of this" + getScheduleName());
                        }
                    } else {
                        if (userID.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
                            Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when User's Join " + getScheduleName());
                        } else {
                            Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when you are part of this " + getScheduleName());
                        }
                    }

                    // Utility.showToastMessage(PickUpGuest.this, "No people are added in " + KEY_PICK_UP + " chat group!");
                }

            } else {
                if (!userID.equalsIgnoreCase("")) {
                    if (userID.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
                        Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when User's Join " + getScheduleName());
                    } else {
                        Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when you are part of this " + getScheduleName());
                    }
                } else {
                    if (userID.equalsIgnoreCase(SharedPref.getInstance().getStringVlue(this, userId))) {
                        Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when User's Join " + getScheduleName());
                    } else {
                        Utility.showToastMessage(PickUpGuest.this, "Chat accessible only when you are part of this" + getScheduleName());
                    }
                }

                // Utility.showToastMessage(PickUpGuest.this, "No people are added in " + KEY_PICK_UP + " chat group!");
            }
        }

        // }
    }


    private void requestToJoin() {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("iduser", SharedPref.getInstance().getStringVlue(this, userId));
            params.put("idschedule", idschedule);
            params.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));
            show_status_bar.setEnabled(false);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.startAnimation();
            show_status_barone.setVisibility(View.GONE);
            RequestHandler.getInstance()
                    .stringRequestVolley(PickUpGuest.this,
                            AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging))
                                    + joinpickup,
                            params, this, API_REQUEST_CODE_REQUET_TO_JOIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getChatGroupName() {
        if (type.equalsIgnoreCase(KEY_BUDDY_UP))
            return "Buddy Up";
        else
            return "Pick Up";
    }


    private void createNewgroupVersion() {

        channelMemberNames = new ArrayList<>();
        JSONArray attendingarray = pickup_details.optJSONArray("attending");
        if (attendingarray != null && attendingarray.length() > 0) {
            for (int i = 0; i < attendingarray.length(); i++) {
                JSONObject rowjson = attendingarray.optJSONObject(i);
                channelMemberNames.add(rowjson.optString("idmember"));
            }
        }

        createChannelForOlderVersion(channelMemberNames);
    }

    private void BuddyInvitesRequest(String methodname) {
        Log.e("buvanesh", "" + SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
        if (type != null)

            if (Utility.isConnectingToInternet(PickUpGuest.this)) {
                try {
                    Map<String, String> param = new HashMap<>();
                    param.put("iduser", SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                    param.put("idschedule", intent.getStringExtra("idschedule"));
                    param.put("type", type);
                    param.put(KEY_CREATE_SCHEDULE, String.valueOf(isScheduleExists));
                    btn_accept.setEnabled(false);
                    btn_decline.setEnabled(false);
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                    RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + methodname, param, this, 1);//Buddyup inivites.
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Utility.showInternetError(PickUpGuest.this);
            }

    }

    private void PickupInvitesRequest(String methodname) {

        if (Utility.isConnectingToInternet(PickUpGuest.this)) {
            try {
                Log.e("size:: 000", "" + selectedMembersId.size());
                Log.e("memberId:: 000", "" + selectedMembersId.toString());

                Map<String, String> param = new HashMap<>();
                param.put("idmember", "" + selectedMembersId.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "")); // here memberid sholud be update.
                param.put("idschedule", intent.getStringExtra("idschedule"));


                Log.e("idmember:: 000", "" + param.get("idmember"));
                btn_accept.setEnabled(false);
                btn_decline.setEnabled(false);
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + methodname, param, this, 2);//Pickup inivites
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(PickUpGuest.this);
        }

    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        if (isfromnotification || isFromDeepLinking) {
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        if (itsnotification != "") {
            Utility.setEventTracking(PickUpGuest.this, "Pick up detail from Notifications screen", "Back arrow fram pickup detail notifications");
        }
        AppConstants.hideViewKeyBoard(UActiveApplication.mContext, et_message);
        finish();
        //super.onBackPressed();  // optional depending on your needs

    }


    private boolean isSuitableUserToCreateGroup() {
        return TextUtils.isEmpty(mChatGroupId)
                || mChatGroupId.equalsIgnoreCase("0")
                || mChatGroupId.equalsIgnoreCase("null") &&
                (status.equalsIgnoreCase(KEY_ACCEPTED) || status.equalsIgnoreCase(KEY_CREATED));
    }


    private String getUserFirstName() {
        return SharedPref.getInstance().getStringVlue(this, firstname);
    }

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {

        /**  flag == 0 for pickupdetail API response
         *   flag == 1 for BuddyUp Accept Decline API response
         *   flag == 2 for PickUp Accept Decline API response
         *   flag == 3 for reportuser API response
         *   flag = 4 MenuAPIcalls API
         *   flag == 5 getChatHistory API.
         *   flag  == 6 send message API
         *   flag = = 7 request to join
         */


        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(successResponse);
            Log.d("buddyup", new Gson().toJson(jsonObject));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "flag : " + flag);


        switch (flag) {
            case 0:
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        if (jsonObject.optJSONObject(KEY_DETAIL) != null) {
                            mChatGroupId = jsonObject.optJSONObject(KEY_DETAIL).optString("group_id");
                            addListItemsForAttending(jsonObject.optJSONObject(KEY_DETAIL));
                            updatePickUpUI(jsonObject.optJSONObject(KEY_DETAIL));
                            pickup_details = jsonObject.optJSONObject(KEY_DETAIL);
                            Log.d(TAG, "success group id : " + mChatGroupId + " isUpComing :" + isUpComing);
                            if (isSuitableUserToCreateGroup() && isUpComing) {
                                Log.d(TAG, "working on version control");
                                createNewgroupVersion();
                            }

                           /* if (mChatGroupId != null && !mChatGroupId.equalsIgnoreCase("0") && !mChatGroupId.equalsIgnoreCase("null")) {
                                Log.e("GroupID", "" + mChatGroupId);

                            } else {
                                if (isUpComing || isActive.equals(KEY_ACTIVE)) {
                                    createNewgroupVersion();
                                }
                            }*/
                        }
                    } else {
                        Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                    }
                    //** to disable menu option for past notification
                    if (!isUpComing) {
                        popup_menu.setVisibility(View.INVISIBLE);
                        fb_share.setVisibility(View.GONE);
                        twitter_share.setVisibility(View.GONE);
                        shareLayout.setVisibility(View.GONE);
                    }
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);

                break;

            case 1:

                Log.d(TAG, "isbuddyuprequest : " + isbuddyuprequest);

                if (jsonObject != null) {

                    //  Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                    btn_accept.setEnabled(true);
                    btn_decline.setEnabled(true);
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                    boolean isChallenge = false;
                    boolean isAlertForExistingTimeSlotShown = false;

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        setStatusDataSetChanged();

                        if (isbuddyuprequest && isaccepted && !ispickupinivite) {

                            if (pickup_details != null && pickup_details.optInt("ischallenge") == 1) {
                                show_status_bar.setVisibility(View.VISIBLE);
                                request_status.setText(getResources().getString(R.string.msg_buddyup_accepted));
                                request_status.setTextColor(Color.parseColor("#4eba89"));
                                isChallenge = true;
                                challangeShare.setVisibility(View.VISIBLE); /// to share challenge popup
                                challengeSharedialog();


                                if (isAlertForExistingTimeSlotShown == false) {
                                    Map<String, String> params = new HashMap<>(3);
                                    try {
                                        params.put("iduser", SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                                        params.put("screen", "buddy_up_request_accept");
                                        RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + "popup_detail", params, PickUpGuest.this, 10);

                                    } catch (NullPointerException ex) {
                                        ex.printStackTrace();
                                    }

                                }


                            }
                        }
                    } else if (jsonObject.optString(KEY_IS_DUPLICATE).equalsIgnoreCase(KEY_TRUE)) {

                        /** this is to eliminate time confilict in schedules
                         *
                         */


                        if (isbuddyuprequest && isaccepted) {
                            if ((!(TextUtils.isEmpty(jsonObject.optString(KEY_IS_DUPLICATE)))) && jsonObject.optBoolean(KEY_IS_DUPLICATE)) { //We have duplicate entry
                                isAlertForExistingTimeSlotShown = true;
                                if (isAlertForExistingTimeSlotShown == false) {
                                    Map<String, String> param = new HashMap<>(3);
                                    try {
                                        param.put("iduser", SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                                        param.put("screen", "buddy_up_request_accept");
                                        RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + "popup_detail", param, PickUpGuest.this, 10);
                                        Log.d("xxxxxxx", "3");
                                    } catch (NullPointerException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                Utility.showAlertForExistingTimeSlot(PickUpGuest.this, getString(R.string.msg_duplicate_accept_buddy_up_request), onAlertClickListener);
                                Log.d("xxxxxxx", "4");
                            } else {
                                Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                                Log.d("xxxxxxx", "1");

                            }
                        } else {
                            Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                            Log.d("xxxxxxx", "2");
                        }


                        /** this is to eliminate time confilict in schedules
                         *
                         */
                    } else {
                        Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                        Log.d("xxxxxxx", "5");
                    }
                    if (isfromnotification && !isChallenge && !(isAlertForExistingTimeSlotShown)) {

                        // finish();
                        challangeShare.setVisibility(View.VISIBLE); /// to share challenge popup
                        show_status_bar.setVisibility(View.VISIBLE);
                        // request_status.setText(R.string.msg_buddyup_accepted);
                    }
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
             /*   Map<String, String> param = new HashMap<>(3);
                try {
                    param.put("iduser", SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                    RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + "popup_detail", param, PickUpGuest.this, 10);

                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }*/
                break;

            case 2:

                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        setStatusDataSetChanged();
                        if (isIspickuprequest_accepted) {
                            status = KEY_CREATED;
                            isIspickuprequest_accepted = false;
                            getPickupDetailsFromAPI(intent.getStringExtra("idschedule"));
                        } else {
                            status = KEY_CREATED;
                            getPickupDetailsFromAPI(intent.getStringExtra("idschedule"));
                        }

                        try {
                            HashMap<String,String>param=new HashMap<>();
                            param.put("iduser", SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                            param.put("screen", "pick_up_request_accept");
                            RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + "popup_detail", param, PickUpGuest.this, 11);
                            Log.d("xxxxxxx", "3");
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }

                    } else {
                        Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                    }

                    //Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                    btn_accept.setEnabled(true);
                    btn_decline.setEnabled(true);

                    findViewById(R.id.bottomLayout).setVisibility(View.GONE);

                    if (isfromnotification) {
                        //  finish();
                    }
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                break;

            case 3:
                Utility.setEventTracking(PickUpGuest.this, "", AppConstants.EVENT_TRACKING_ID_REPORT_USER);
                //repoertuser API.
                if (jsonObject != null) {
                    Utility.showToastMessage(getApplicationContext(), getString(R.string.msg_report_user));
                    if (itsnotification != "") {
                        Utility.setEventTracking(PickUpGuest.this, "Pick up detail from Notifications screen", "report user on pickup detail from notification.");
                    }

                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                if (isfromnotification) {
                    //   finish();
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                break;


            case 4:

                //MenuAPIcalls API.

                if (jsonObject != null) {

                    //Utility.showToastMessage(getApplicationContext(), jsonObject.optString(KEY_MSG));


                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE) && API_METHOD_NAME != null) {

                        switch (API_METHOD_NAME) {

                            case cancelbuddyup:
                                applozicChat.sendCustomMsgtoGroup(mChatGroupId, getString(R.string.txt_applozic_custom_msg_buddyup_cancelled));

                                Utility.setEventTracking(PickUpGuest.this, "", AppConstants.EVENT_TRACKING_ID_CANCEL);
                                if (notify_model != null) {
                                    notify_model.setStatus(KEY_CANCEL);
                                    status = KEY_CANCEL;
                                    statusChangedListener.onStatusChanged(notify_model, position);
                                }
                                //    Utility.showToastMessage(PickUpGuest.this, getString(R.string.cancel_buddyup_msg));
                                break;
                            case cancelpickup:
                                applozicChat.sendCustomMsgtoGroup(mChatGroupId, getString(R.string.txt_applozic_custom_msg_pick_up_cancelled));
                                Utility.setEventTracking(PickUpGuest.this, "", AppConstants.EVENT_TRACKING_ID_CANCEL);
                                if (notify_model != null) {
                                    notify_model.setStatus(KEY_CANCEL);
                                    status = KEY_CANCEL;
                                    statusChangedListener.onStatusChanged(notify_model, position);
                                }
                                //       Utility.showToastMessage(PickUpGuest.this, getString(R.string.pickup_cancelled_msg));
                                break;
                            case abandonbuddyup:
                                applozicChat.sendCustomMsgtoGroup(mChatGroupId, getString(R.string.txt_applozic_custom_msg_buddyup_cancelled));
                                applozicChat.removeParticipant(getUserId(), Integer.valueOf(getConversationsId()));
                                Utility.setEventTracking(PickUpGuest.this, "", AppConstants.EVENT_TRACKING_ID_ABANDON);
                                if (notify_model != null) {
                                    notify_model.setStatus(KEY_ABANDON);
                                    status = KEY_ABANDON;
                                    statusChangedListener.onStatusChanged(notify_model, position);
                                }
                                //     Utility.showToastMessage(PickUpGuest.this, getString(R.string.buddy_up_aband_msg));
                                break;
                            case abandonpickup:
                                // applozicChat.sendCustomMsgtoGroup(mChatGroupId, getUserFirstName() + getString(R.string.txt_applozic_custom_msg_abandoned));
                                applozicChat.removeParticipant(getUserId(), Integer.valueOf(getConversationsId()));
                                if (itsnotification != "") {
                                    Utility.setEventTracking(PickUpGuest.this, "Pick up detail from Notifications screen", "abandon pickup on pickup detail from notification.");
                                }
                                Utility.setEventTracking(PickUpGuest.this, "", AppConstants.EVENT_TRACKING_ID_ABANDON);
                                if (notify_model != null) {
                                    notify_model.setStatus(KEY_ABANDON);
                                    status = KEY_ABANDON;
                                    statusChangedListener.onStatusChanged(notify_model, position);
                                }
                                //    Utility.showToastMessage(PickUpGuest.this, getString(R.string.pick_up_aband_msg));
                                break;
                            case cancelpickuprequest:
                                Utility.setEventTracking(PickUpGuest.this, "", AppConstants.EVENT_TRACKING_ID_CANCEL);
                                if (notify_model != null) {
                                    notify_model.setStatus(KEY_CANCEL);
                                    status = KEY_CANCEL;
                                    statusChangedListener.onStatusChanged(notify_model, position);
                                }
                                //   Utility.showToastMessage(PickUpGuest.this, getString(R.string.pick_up_aband_msg));
                                break;
                            default:
                                break;
                        }
                        progressWheel.setVisibility(View.GONE);
                        progressWheel.stopAnimation();
                    } else {
                        Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                    }
                    finish();
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                break;
            case 5:

                /** get chat historyfrom API
                 *
                 */
                Utility.setEventTracking(PickUpGuest.this, "", AppConstants.EVENT_TRACKING_ID_CHAT);

                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        JSONArray chatHistory = jsonObject.optJSONArray(KEY_CHAT);

                        if (chatHistory != null && chatHistory.length() > 0) {

                            chatDoArrayList = new ArrayList<>();

                            chatDoArrayList = ResponseHandler.getInstance().getChatHistory(PickUpGuest.this, chatHistory);

                            setChatAdapter();

                        }
                        ///progressWheel.stopAnimation();
                        // progressWheel.setVisibility(View.GONE);
                    }

                }

                break;

            case 6:

                /** send message
                 *
                 */
                // progressWheel.stopAnimation();
                //  progressWheel.setVisibility(View.GONE);
                btn_send.setEnabled(true);
                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        et_message.setText("");

                        JSONArray chatHistory = jsonObject.optJSONArray(KEY_CHAT);

                        if (chatHistory != null && chatHistory.length() > 0) {

                            chatDoArrayList = new ArrayList<>();

                            chatDoArrayList = ResponseHandler.getInstance().getChatHistory(PickUpGuest.this, chatHistory);

                            setChatAdapter();

                        }
                        //Utility.showToastMessage(PickUpGuest.this,jsonObject.optString("msg"));

                    }
                }
                break;
            case UPDATE_CHAT_GROUP_ID:
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                if (ischat_enabled)
                    onChatScreen();

                Utility.setEventTracking(PickUpGuest.this, "", "Pick Up chat screen");
                break;
            case API_REQUEST_CODE_REQUET_TO_JOIN:
                Utility.setEventTracking(PickUpGuest.this, "", AppConstants.EVENT_TRACKING_ID_REQUEST_PICKUP_TO_JOIN);
                if (jsonObject != null) {
                    try {
                        if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                            show_status_bar.setEnabled(false);
                            request_status.setText(R.string.msg_pickup_request_pending);
                            status = KEY_REQESTED;
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            SharedPref.getInstance().setSharedValue(PickUpGuest.this, notification_count, jsonObject.optInt("notification_count"));
                            notifyCountChanged();
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);


                        } else {
                            show_status_bar.setEnabled(true);
                            if ((!(TextUtils.isEmpty(jsonObject.optString(KEY_IS_DUPLICATE)))) && jsonObject.optBoolean(KEY_IS_DUPLICATE)) { //We have duplicate entry
                                Utility.showAlertForExistingTimeSlot(PickUpGuest.this, getString(R.string.msg_duplicate_request_join), onAlertClickListener);
                            } else {
                                Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                            }
                        }
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 10:
                if (jsonObject != null) {
                    if (jsonObject != null) {
                        try {
                            Log.d("session", AppConstants.getSession(PickUpGuest.this) + "");
                            if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                                if (jsonObject.optString("type").equalsIgnoreCase("pick_up") && AppConstants.isread.isEmpty()) {
                                    AppConstants.genralPopupEvent(PickUpGuest.this, 1, jsonObject, this, PickUpGuest.this, "buddyup");
                                    Log.d("show pickup dialog", "");
                                } else if (jsonObject.optString("type").equalsIgnoreCase("promotional") && AppConstants.isread.isEmpty()) {

                                    if (jsonObject.optString("content").isEmpty()) {
                                        AppConstants.genralPopupEvent(PickUpGuest.this, 3, jsonObject, this, PickUpGuest.this, "buddyup");
                                    } else {
                                        AppConstants.genralPopupEvent(PickUpGuest.this, 2, jsonObject, this, PickUpGuest.this, "buddyup");
                                    }
                                    //  SharedPref.getInstance().setSharedValue(getActivity(), "APP_SESSION", 1);

                                    Log.d("show pramotional dialog", "");
                                } else if (jsonObject.optString("type").equalsIgnoreCase("rateus") && AppConstants.isread.isEmpty()) {
                                    if (!SharedPref.getInstance().getBooleanValue(PickUpGuest.this, "islater")) {
                                        AppConstants.genralPopupEvent(PickUpGuest.this, 0, jsonObject, this, PickUpGuest.this, "buddyup");
                                    }

                                }

                            }else{
                                if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                                    BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                                BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                            }
                        }


                    }

                }
                break;
            case 11:
                if (jsonObject != null) {
                    if (jsonObject != null) {
                        try {
                            Log.d("session", AppConstants.getSession(PickUpGuest.this) + "");
                            if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                                if (jsonObject.optString("type").equalsIgnoreCase("pick_up") && AppConstants.isread.isEmpty()) {
                                    AppConstants.genralPopupEventRate(PickUpGuest.this, 1, jsonObject, this, PickUpGuest.this, "buddyup");
                                    Log.d("show pickup dialog", "");
                                } else if (jsonObject.optString("type").equalsIgnoreCase("promotional") && AppConstants.isread.isEmpty()) {

                                    if (jsonObject.optString("content").isEmpty()) {
                                        AppConstants.genralPopupEventRate(PickUpGuest.this, 3, jsonObject, this, PickUpGuest.this, "buddyup");
                                    } else {
                                        AppConstants.genralPopupEventRate(PickUpGuest.this, 2, jsonObject, this, PickUpGuest.this, "buddyup");
                                    }
                                    //  SharedPref.getInstance().setSharedValue(getActivity(), "APP_SESSION", 1);

                                    Log.d("show pramotional dialog", "");
                                } else if (jsonObject.optString("type").equalsIgnoreCase("rateus") && AppConstants.isread.isEmpty()) {
                                    if (!SharedPref.getInstance().getBooleanValue(PickUpGuest.this, "islater")) {
                                        AppConstants.genralPopupEventRate(PickUpGuest.this, 0, jsonObject, this, PickUpGuest.this, "buddyup");
                                    }

                                }

                            }else{

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();

                        }


                    }

                }
                break;


            default:
                break;
        }
    }

    private String getUserId() {
        return SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId);
    }

    private String getConversationsId() {
        return mChatGroupId;
    }

    private void setChatAdapter() {

        Log.e("chatDoArrayList.size()", ":" + chatDoArrayList.size());

        if (chatDoArrayList != null && chatDoArrayList.size() > 0) {
            LinearLayoutManager manager = new LinearLayoutManager(PickUpGuest.this, LinearLayoutManager.VERTICAL, false);
            // manager.setReverseLayout(true);
            manager.setStackFromEnd(true);
            //chat_window_recycle.setLayoutManager(manager);
            mChatAdapterWeb = new QBChatAdapter(PickUpGuest.this, chatDoArrayList);
            //chat_window_recycle.setAdapter(mChatAdapterWeb);
            //chat_window_recycle.scrollToPosition(chatDoArrayList.size() - 1);
        }

    }

    /**
     * Remove duplicates in arraylist
     *
     * @param favmemberlist
     * @return
     */


    private ArrayList<FavouriteModel> removeFavDuplicate(ArrayList<FavouriteModel> favmemberlist) {

        System.out.println(favmemberlist);

        ArrayList newList = null;

        if (favmemberlist != null) {

            Set set = new TreeSet(new Comparator() {
                @Override
                public int compare(Object lhs, Object rhs) {

                    if (lhs instanceof FavouriteModel && rhs instanceof FavouriteModel) {

                        if (((FavouriteModel) lhs).getId().equalsIgnoreCase(((FavouriteModel) rhs).getId())) {
                            return 0;
                        }
                    }
                    return 1;
                }
            });
            set.addAll(favmemberlist);

            System.out.println("\n***** After removing duplicates *******\n");

            newList = new ArrayList(set);

            System.out.println(set);
        }

        return newList;
    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        btn_accept.setEnabled(true);
        btn_decline.setEnabled(true);
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }

    @Override
    public void getSelectedItem(boolean isChecked, int position) {

        //This is for to enable accept Decline button to requested card.

        if (isChecked) {
            this.selectedMembersId.add(request_items.get(position).getId());
            Log.e("added", "added");
        } else {
            Log.e("removed", "removed");
            this.selectedMembersId.remove("" + request_items.get(position).getId());
        }


        /** enable accept & decline buttions if its upcoming event also it must be a valid event
         *
         */

        Log.d(TAG, "isEventExpried :" + isEventExpried + " " + selectedMembersId.toString());
        showRequestAcceptDeclineButtons();

        Log.e("getSelectedItem", "" + selectedMembersId.toString());
    }

    private void showRequestAcceptDeclineButtons() {

        if (!(isEventExpried) && isUpComing) {

            if (selectedMembersId != null) {
                Log.d(TAG, "getSelectedItem :" + selectedMembersId.size());

                if (selectedMembersId.size() > 0 && isActive != null && isActive.equals(KEY_ACTIVE)) {
                    btnSelectAll.setText("Deselect All");
                    findViewById(R.id.bottomLayout).setVisibility(View.VISIBLE);
                } else {
                    btnSelectAll.setText("Select All");
                    findViewById(R.id.bottomLayout).setVisibility(View.GONE);
                }

            }

        } else {
            show_status_bar.setVisibility(View.VISIBLE);
            request_status.setText(R.string.msg_pickup_request_expired);
            request_status.setTextColor(getResources().getColor(R.color.expirecolor));

            request_status_chat.setText("Expired");
            request_status_chat.setBackgroundColor(getResources().getColor(R.color.expirecolor));
        }
    }

    public void setStatusDataSetChanged() {

        findViewById(R.id.bottomLayout).setVisibility(View.GONE);
        request_status.setVisibility(View.VISIBLE);
        Log.e("setStatusDataSetChanged", "isaccepted :" + isaccepted + "isdeclined :" + isdeclined);


        Log.d(TAG, "******************");
        Log.d(TAG, "isbuddyuprequest : " + isbuddyuprequest);
        Log.d(TAG, "ispickupinivite : " + ispickupinivite);
        Log.d(TAG, "isaccepted : " + isaccepted);
        Log.d(TAG, "isdeclined : " + isdeclined);
        Log.d(TAG, "ispickuprequest : " + ispickuprequest);

        Log.d(TAG, "******************");

        if (isbuddyuprequest) {

            if (ispickupinivite) {
                //Update pickup status

                if (isaccepted) {

                    applozicChat.addParticipant(getUserId(), Integer.valueOf(getConversationsId()));
                    request_status.setText("PickUp Invite Accepted!");
                    //show_status_bar.setVisibility(View.VISIBLE);
                    if (notify_model != null)
                        notify_model.setStatus(KEY_ACCEPTED);
                    status = KEY_ACCEPTED;
                    if (statusChangedListener != null)
                        statusChangedListener.onStatusChanged(notify_model, position);
                    // moveRequestToAttending(); ///
                    moveInivtedUserToAttending();

                } else if (isdeclined) {
                    request_status.setText("PickUp Invite Declined!");
                    if (notify_model != null)
                        notify_model.setStatus(KEY_REJECTED);
                    status = KEY_REJECTED;
                    if (statusChangedListener != null)
                        statusChangedListener.onStatusChanged(notify_model, position);
                }

            } else {

                if (isaccepted) {

                    applozicChat.addParticipant(getUserId(), Integer.valueOf(getConversationsId()));
                    request_status.setText(R.string.msg_buddyup_accepted);
                    request_status.setTextColor(Color.parseColor("#4eba89"));
                    if (notify_model != null)
                        notify_model.setStatus(KEY_ACCEPTED);
                    status = KEY_ACCEPTED;
                    show_status_bar.setVisibility(View.VISIBLE);
                    if (statusChangedListener != null)
                        statusChangedListener.onStatusChanged(notify_model, position);

                    /** commented because of we have popup facebook share dialog
                     *
                     */

                    /*fb_share.setVisibility(View.VISIBLE);
                    twitter_share.setVisibility(View.VISIBLE);*/


                    /** commented because of we have popup facebook share dialog
                     *
                     */

                    // To show challenge share dialog.
                    //Here Shouuld show Buddyup invites.asd

                } else if (isdeclined) {
                    show_status_bar.setVisibility(View.VISIBLE);
                    request_status.setText(R.string.msg_buddyup_declined);
                    request_status.setTextColor(getResources().getColor(R.color.red));
                    if (notify_model != null)
                        notify_model.setStatus(KEY_REJECTED);
                    status = KEY_REJECTED;
                    if (statusChangedListener != null)
                        statusChangedListener.onStatusChanged(notify_model, position);
                }
            }
        } else if (ispickuprequest) {

            if (isaccepted) {

                String userids = selectedMembersId.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");

                String[] singleUser = userids.split(",");
                for (int i = 0; i < singleUser.length; i++) {
                    applozicChat.addParticipant(singleUser[i], Integer.valueOf(getConversationsId()));
                }
                request_status.setText(R.string.msg_pickup_request_accepted);
                request_status.setTextColor(Color.parseColor("#4eba89"));
                if (notify_model != null)
                    notify_model.setStatus(KEY_ACCEPTED);
                status = KEY_ACCEPTED;
                if (statusChangedListener != null)
                    statusChangedListener.onStatusChanged(notify_model, position);

                selectedMembersId = new ArrayList<>(); // for clear data

            } else if (isdeclined) {

                request_status.setText(R.string.msg_pickup_req_declined);
                if (notify_model != null)
                    notify_model.setStatus(KEY_REJECTED);
                status = KEY_REJECTED;
                if (statusChangedListener != null)
                    statusChangedListener.onStatusChanged(notify_model, position);
            }
        }

    }

    private void moveInivtedUserToAttending() {

        // Log.d("moveInivtedUserToAttending",""+attending_items.size()+ ""+ mAdapter +""+""+ pickup_details);

        if (attending_items != null && mAdapter != null && pickup_details != null) {

            attending_recycle.setVisibility(View.VISIBLE);

            attending_items.add(new FavouriteModel(SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId), SharedPref.getInstance().getStringVlue(PickUpGuest.this, firstname), SharedPref.getInstance().getStringVlue(PickUpGuest.this, image)));

            manager = new LinearLayoutManager(PickUpGuest.this, LinearLayoutManager.HORIZONTAL, false);
            attending_recycle.setLayoutManager(manager);

            mAdapter = new AttendingPeoplesAdapter(PickUpGuest.this, attending_items);
            attending_recycle.setAdapter(mAdapter);

            if (pickup_details.optInt("no_of_people") <= 0 || pickup_details.optString("no_of_people").equals("No limit")) {

                tvAttending.setText("Attending (" + attending_items.size() + ")");

                if (attending_items.size() <= 0) {

                    attending_recycle.setVisibility(View.GONE);
                }
            } else {

                tvAttending.setText("Attending (" + attending_items.size() + "/" + pickup_details.optString("no_of_people") + ")");

                if (attending_items.size() <= 0) {

                    attending_recycle.setVisibility(View.GONE);
                }
            }


            try {

                if (noOfpeole <= 0) {
                    tvspotleft.setText("( Unlimited spots )");
                } else {
                    //Patch done by Jeeva on 2-02-16.
                    int left = noOfpeole - attending_items.size();
                    if (left == 1) {
                        tvspotleft.setText("( " + left + " spot left )"); // Should not show -1 spots left like that ---->>>> issue fixed.
                    } else if (left > 1) {
                        tvspotleft.setText("( " + left + " spots left )");
                    }
                    //Patch done by Jeeva on 2-02-16.
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        int[] location = new int[2];
        ImageView button = (ImageView) findViewById(R.id.gridIcon);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        button.getLocationOnScreen(location);
        height = button.getMeasuredHeight();
        width = button.getMeasuredHeight();
        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];
        Log.e("onWindowFocusChanged", "p.x :" + p.x + "p.y :" + p.y);
    }

    private void showPopup(final Activity context, Point p) {
        Utility.setEventTracking(PickUpGuest.this, "", AppConstants.EVENT_TRACKING_ID_MENU);
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(viewGroup);
        popup.setContentView(layout);
        popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popup.setFocusable(true);
        optiontv1 = (CustomTextView) layout.findViewById(R.id.optiontxt1);

        optiontv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();

                String title = null;
                String message = null;
                Resources res = getResources();
                switch (API_METHOD_NAME) {
                    case cancelpickup:
                        title = getResources().getString(R.string.title_cancel_pickup);
                        break;
                    case cancelbuddyup:
                        title = getResources().getString(R.string.title_cancel_buddyup);
                        break;
                    case cancelpickuprequest:
                        title = getResources().getString(R.string.title_cancel_pickup_request);
                        break;
                    case abandonpickup:
                        title = getResources().getString(R.string.title_abandon_pickup);
                        break;
                    case abandonbuddyup:
                        title = getResources().getString(R.string.title_cancel_buddyup);
                        break;
                }
                message = "Are you sure?";
                showMenuAlert(title, message);

            }
        });

        optiontv2 = (CustomTextView) layout.findViewById(R.id.optiontxt2);
        optiontv3 = (CustomTextView) layout.findViewById(R.id.optiontxt3);
        optiontv2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (userID != null) {
                    popup.dismiss();
                    showReportUserDialog();
                }
            }
        });

        optiontv3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (type != null && pickup_details != null) {
                    switch (type) {
                        case KEY_BUDDY_UP:
                            popup.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putString("idschedule", intent.getStringExtra("idschedule"));
                            bundle.putString("group_id", pickup_details.optString("group_id"));
                            bundle.putString("type", type);
                            bundle.putString("skill", pickup_details.optString("activity"));
                            bundle.putString("ischallenge", pickup_details.optString("ischallenge"));
                            bundle.putString("message", pickup_details.optString("message"));

                            if (pickup_details.optJSONArray("attending") != null && pickup_details.optJSONArray("attending").length() > 0) {
                                bundle.putString("memberid", pickup_details.optJSONArray("attending").optJSONObject(0).optString("idmember"));
                                bundle.putString("firstname", pickup_details.optJSONArray("attending").optJSONObject(0).optString("firstname"));
                                bundle.putString("image", pickup_details.optJSONArray("attending").optJSONObject(0).optString("image"));

                            } else if (pickup_details.optJSONArray("invited") != null && pickup_details.optJSONArray("invited").length() > 0) {

                                bundle.putString("memberid", pickup_details.optJSONArray("invited").optJSONObject(0).optString("idmember"));
                                bundle.putString("firstname", pickup_details.optJSONArray("invited").optJSONObject(0).optString("firstname"));
                                bundle.putString("image", pickup_details.optJSONArray("invited").optJSONObject(0).optString("image"));
                            }


                            bundle.putString("date", pickup_details.optString("date"));
                            bundle.putString("start_time", pickup_details.optString("start_time"));
                            bundle.putString("end_time", pickup_details.optString("end_time"));
                            bundle.putString("location", pickup_details.optString("location"));
                            bundle.putDouble("latitude", pickup_details.optDouble("latitude"));
                            bundle.putDouble("longitude", pickup_details.optDouble("longitude"));
                            bundle.putInt("isbusinesslocation", pickup_details.optInt("isbusinesslocation"));
                            bundle.putString("idbusiness", pickup_details.optString("idbusiness"));
                            bundle.putString("isBookingOpen", pickup_details.optString("is_booked"));

                            final Intent intent_buddyreq = new Intent(PickUpGuest.this, BuddyUpRequest.class);
                            intent_buddyreq.putExtras(bundle);
                            intent_buddyreq.putExtra("isEdit", true);
                            startActivityForResult(intent_buddyreq, 1);
                            /*final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PickUpGuest.this);
                            alertDialog.setTitle("Edit schedule");
                            alertDialog.setCancelable(false);
                            alertDialog.setMessage("Are you sure?");
                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    return;
                                }
                            });
                            alertDialog.show();*/
                            break;

                        case KEY_PICK_UP:
                            popup.dismiss();
                            Log.e("PickupData", "" + pickup_details.toString());
                            final Intent intent_pickupreq = new Intent(PickUpGuest.this, CreatePickUp.class);
                            Bundle bundle_pickup = new Bundle();
                            bundle_pickup.putString("no_of_people", pickup_details.optString("no_of_people"));
                            bundle_pickup.putString("invited", pickup_details.optJSONArray("invited").toString());
                            bundle_pickup.putString("attending", pickup_details.optJSONArray("attending").toString());
                            bundle_pickup.putString("idschedule", intent.getStringExtra("idschedule"));
                            bundle_pickup.putString("type", type);
                            bundle_pickup.putString("group_id", pickup_details.optString("group_id"));
                            bundle_pickup.putString("message", pickup_details.optString("message"));
                            bundle_pickup.putString("activity", pickup_details.optString("activity"));
                            bundle_pickup.putString("mode", pickup_details.optString("mode"));
                            bundle_pickup.putString("date", pickup_details.optString("date"));
                            bundle_pickup.putString("start_time", pickup_details.optString("start_time"));
                            bundle_pickup.putString("end_time", pickup_details.optString("end_time"));
                            bundle_pickup.putString("location", pickup_details.optString("location"));
                            bundle_pickup.putDouble("latitude", pickup_details.optDouble("latitude"));
                            bundle_pickup.putDouble("longitude", pickup_details.optDouble("longitude"));
                            bundle_pickup.putInt("isbusinesslocation", pickup_details.optInt("isbusinesslocation"));
                            bundle_pickup.putString("idbusiness", pickup_details.optString("idbusiness"));
                            bundle_pickup.putString("isBookingOpen", pickup_details.optString("is_booked"));

                            intent_pickupreq.putExtras(bundle_pickup);
                            intent_pickupreq.putExtra("isEdit", true);
                            startActivityForResult(intent_pickupreq, 1);

                            /*final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(PickUpGuest.this);
                            alertDialog1.setTitle("Edit schedule");
                            alertDialog1.setCancelable(false);
                            alertDialog1.setMessage("Are you sure?");
                            alertDialog1.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    return;
                                }
                            });
                            alertDialog1.show();*/
                            break;
                    }
                }
            }
        });


        simpleview = layout.findViewById(R.id.view);
        simpleview1 = layout.findViewById(R.id.view1);
        visibilityOptionMenu(userID);
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());
        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, height + p.x, height + p.y);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        conversationUIService.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1 && data != null) {
                switch (requestCode) {
                    case 1:
                        String response = data.getStringExtra("response");
                        try {
                            responseListener.successResponse(response, 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (data != null) {
                            Log.i("response", "" + response);
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (jsonObject != null) {
                                if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                                    Log.e("entered", "KEY_DETAIL" + jsonObject.optJSONObject(KEY_DETAIL));
                                    if (jsonObject.optJSONObject(KEY_DETAIL) != null) {
                                        try {
                                            updatePickUpUI(jsonObject.optJSONObject(KEY_DETAIL));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        addListItemsForAttending(jsonObject.optJSONObject(KEY_DETAIL));
                                        Log.e("updated", "true");
                                    }
                                } else {
                                    Log.e("*** Msg On Edit", "" + jsonObject.optString(KEY_MSG));
                                    Utility.showToastMessage(PickUpGuest.this, jsonObject.optString(KEY_MSG));
                                }
                                progressWheel.stopAnimation();
                                progressWheel.setVisibility(View.GONE);
                            }
                            Log.d("responseListener", "responseListener");
                        }
                        break;
                }
            }
        }
        if (requestCode == 2) {
            if (!TextUtils.isEmpty(intent.getStringExtra("idschedule"))) {
                getPickupDetailsFromAPI(intent.getStringExtra("idschedule")); // here is the API Call for getPickupdetails
            }
        }


        if (requestCode == LOCATION_SERVICE_ENABLE) {
            if (((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                googleApiClient.connect();
            } else {
                Toast.makeText(this, R.string.unable_to_fetch_location, Toast.LENGTH_LONG).show();
            }


        }
    }

    public void showReportUserDialog() {
        final Dialog dialog1 = new Dialog(PickUpGuest.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog1.setContentView(R.layout.comment);
        final String[] review = {null};
        final CustomEditText customEditText = (CustomEditText) dialog1.findViewById(R.id.comment_text);
        CustomTextView done = (CustomTextView) dialog1.findViewById(R.id.done);
        CustomTextView headerText = (CustomTextView) dialog1.findViewById(R.id.headerText);
        headerText.setText("Report User");
        customEditText.setHint("Write something...");
        dialog1.show();
        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                if (customEditText.length() > 0) {
                    dialog1.dismiss();
                    callReportUserAPI(customEditText.getText().toString().trim());
                } else {
                    Utility.showToastMessage(PickUpGuest.this, "Please write your review...");
                }
            }
        });


    }

    private void callReportUserAPI(String reviewText) {

        if (reviewText != null && reviewText.length() > 0 && userID != null) {

            if (Utility.isConnectingToInternet(PickUpGuest.this)) {
                try {
                    Map<String, String> param = new HashMap<>();
                    param.put("idschedule", intent.getStringExtra("idschedule"));
                    param.put("iduser", userID);
                    param.put("reported_by", SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                    param.put("comment", reviewText);
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                    RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + reportuser, param, this, 3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Utility.showInternetError(PickUpGuest.this);
            }

        }
    }

    private void callMenuOptionAPI() {

        if (API_METHOD_NAME != null) {

            if (Utility.isConnectingToInternet(PickUpGuest.this)) {
                try {
                    Map<String, String> param = new HashMap<>();

                    if (API_METHOD_NAME.equals(abandonpickup) || API_METHOD_NAME.equals(cancelpickuprequest)) {
                        param.put("iduser", SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId));
                    }
                    param.put("idschedule", intent.getStringExtra("idschedule"));
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                    RequestHandler.getInstance().stringRequestVolley(PickUpGuest.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(PickUpGuest.this, isStaging)) + API_METHOD_NAME, param, this, 4);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Utility.showInternetError(PickUpGuest.this);
            }
        }
    }

    private void visibilityOptionMenu(String userid) {

        if (userid != null && userid.length() > 0) {

            switch (type) {

                case KEY_BUDDY_UP:
                    optiontv2.setVisibility(View.VISIBLE);
                    optiontv2.setText("Report User");


                    if (userid.equals(SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId))) {
                       /* if (isMenuEnable && status != null && !status.equals(KEY_REJECTED)) {*/ // commented on 06/02/16 for schedule menu option
                        if (isMenuEnable) {// newly added! on

                            if (isActive != null && isActive.equals(KEY_ACTIVE)) {
                                optiontv1.setVisibility(View.VISIBLE);
                                optiontv1.setText("Cancel BuddyUp");
                                API_METHOD_NAME = cancelbuddyup;
                                optiontv3.setVisibility(View.VISIBLE);
                                optiontv3.setText("Edit BuddyUp");
                                simpleview.setVisibility(View.VISIBLE);
                                simpleview1.setVisibility(View.VISIBLE);
                            }
                        } else {
                            popup_menu.setVisibility(View.INVISIBLE);
                        }


                    } else {


                       /* optiontv2.setVisibility(View.VISIBLE);
                        optiontv2.setText("Report User");
                        simpleview.setVisibility(View.VISIBLE);*/

                        if (isMenuEnable) {

                            if (isActive != null && isActive.equals(KEY_ACTIVE)) {

                                optiontv1.setVisibility(View.VISIBLE);
                                /** patch by moorthy
                                 *  optiontv1.setText("Abandon BuddyUp");
                                 *
                                 *  changed abandon buddy up text to cancel buddyup.
                                 */
                                optiontv1.setText("Cancel BuddyUp");
                                API_METHOD_NAME = abandonbuddyup;
                                simpleview.setVisibility(View.VISIBLE);
                                simpleview1.setVisibility(View.VISIBLE);
                            }
                        } else {
                            optiontv1.setVisibility(View.GONE);
                            simpleview.setVisibility(View.GONE);

                        }


                    }
                    break;
                case KEY_PICK_UP:

                    if (userid.equals(SharedPref.getInstance().getStringVlue(PickUpGuest.this, userId))) {

                        /*if (isMenuEnable && status != null && !status.equals(KEY_REJECTED)) {*/ //commented on 06/02/16 for schdeule menu
                        if (isMenuEnable) {
                            optiontv1.setVisibility(View.VISIBLE);
                            optiontv1.setText("Cancel PickUp");
                            simpleview.setVisibility(View.GONE);
                            API_METHOD_NAME = cancelpickup;
                            simpleview1.setVisibility(View.VISIBLE);
                            optiontv3.setVisibility(View.VISIBLE);
                            optiontv3.setText("Edit PickUp");
                        } else {
                            popup_menu.setVisibility(View.INVISIBLE);
                        }

                    } else {


                        optiontv2.setVisibility(View.VISIBLE);
                        optiontv2.setText("Report User");

                        if (isFromSchedule) {

                            if (isMenuEnable) {
                                optiontv1.setVisibility(View.VISIBLE);
                                optiontv1.setText("Abandon Pick Up");
                                API_METHOD_NAME = abandonpickup;
                            }

                        } else {

                            if (isMenuEnable && status != null) {

                                if (status.endsWith(KEY_ACCEPTED)) {
                                    optiontv1.setVisibility(View.VISIBLE);
                                    optiontv1.setText("Abandon Pick Up");
                                    API_METHOD_NAME = abandonpickup;
                                } else {
                                    optiontv1.setVisibility(View.VISIBLE);
                                    optiontv1.setText("Cancel Request");
                                    API_METHOD_NAME = cancelpickuprequest; // here needs to call cancelrequest
                                }

                            } else {
                                optiontv1.setVisibility(View.GONE);
                                simpleview.setVisibility(View.GONE);
                            }
                        }
                    }

                    break;
                default:
                    break;

            }
        }

    }

    public void challengeSharedialog() {
        // custom dialog
        final Dialog dialog = new Dialog(PickUpGuest.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.custom_dialog_pickup_position);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final String message = getResources().getString(R.string.buddyup_share_text);


        CustomTextView challengerText = (CustomTextView) dialog.findViewById(R.id.tvChallenge);
        //challengerText.setText(SharedPref.getInstance().getStringVlue(PickUpGuest.this, firstname) + " has challenged " + pickup_details.optString("buddyfirstname") + " to " + pickup_details.optString("activity") + " on UACTIV !");
        challengerText.setText(pickup_details.optString("firstname") + " has challenged " + pickup_details.optJSONObject("opponent").optString("firstname") + " to " + pickup_details.optString("activity") + " on UACTIV !");

//        Utility.setImageUniversalLoader(PickUpGuest.this, SharedPref.getInstance().getStringVlue(PickUpGuest.this, image), (CircularImageView) dialog.findViewById(R.id.img_challenger_one));
//        Utility.setImageUniversalLoader(PickUpGuest.this, pickup_details.optString("buddyimage"), (CircularImageView) dialog.findViewById(R.id.img_challenger_two));

        Utility.setImageUniversalLoader(PickUpGuest.this, pickup_details.optString("image"), (CircularImageViews) dialog.findViewById(R.id.img_challenger_one));
        Utility.setImageUniversalLoader(PickUpGuest.this, pickup_details.optJSONObject("opponent").optString("image"), (CircularImageViews) dialog.findViewById(R.id.img_challenger_two));

        dialog.findViewById(R.id.share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                View view = dialog.getWindow().getDecorView().getRootView();
                view.setDrawingCacheEnabled(true);
                shareBitmap(message, view, shareDialog);

                //AppConstants.facebookShare(PickUpGuest.this, shareDialog, pickup_details.optString("activity") + " " + message);
            }
        });

        dialog.findViewById(R.id.tweet).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConstants.shareTwitter(PickUpGuest.this, pickup_details.optString("activity") + " " + message);
            }
        });


        dialog.show();
    }

    private void shareBitmap(String caption, View view, ShareDialog shareDialog) {
        //  ShareDialog mShareDialog = new ShareDialog(PickUpGuest.this);
        //View v1 = getWindow().getDecorView().getRootView();
        // v1.setDrawingCacheEnabled(true);
        Bitmap myBitmap = view.getDrawingCache();
        shareBitmap(PickUpGuest.this, shareDialog, caption, myBitmap);
    }

    private void shareBitmap(Context context, ShareDialog shareDialog, String caption, Bitmap photo) {
        facebookImageShare(context, shareDialog, caption, photo);
    }

    public void facebookImageShare(final Context mContext, ShareDialog shareDialog, String caption, Bitmap photo) {

        if (photo != null && shareDialog != null) {

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                SharePhoto mSharePhoto = new SharePhoto.Builder().setBitmap(photo).setCaption(caption).build();
                SharePhotoContent mBuilder = new SharePhotoContent.Builder().addPhoto(mSharePhoto).build();
                shareDialog.show(mBuilder);
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.d(TAG, "onSuccess : ");
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel : ");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        Log.d(TAG, "onError : " + e.getMessage());
                        Toast.makeText(mContext, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            } else {
                Toast.makeText(mContext, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSelectAll(ArrayList<FavouriteModel> allCheckedItems, ArrayList<String> addedItemCount) {
        if (!isEventExpried) {
            selectedMembersId = new ArrayList<>();
            if (addedItemCount != null) {

                Log.e(TAG + "onSelectAll ", "" + addedItemCount.size());

                if (addedItemCount.size() > 0 && isActive != null && isActive.equals(KEY_ACTIVE)) {
                    Log.e("onSelectAll ", "isActive :in if");
                    findViewById(R.id.bottomLayout).setVisibility(View.VISIBLE);

                } else {
                    Log.e("onSelectAll ", "isActive :in else");
                    findViewById(R.id.bottomLayout).setVisibility(View.GONE);
                }
                this.selectedMembersId.addAll(addedItemCount);
                new_attending_items = new ArrayList<>();
                new_attending_items = allCheckedItems;
            }

        } else {
            show_status_bar.setVisibility(View.VISIBLE);
            request_status.setText(R.string.msg_pickup_request_expired);
            request_status.setTextColor(getResources().getColor(R.color.expirecolor));

            request_status_chat.setText("Expired");
            request_status_chat.setBackgroundColor(getResources().getColor(R.color.expirecolor));
        }
        Log.e("onSelectAll MembersId", "" + selectedMembersId.toString());
    }

    public void showMenuAlert(String title, String message) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PickUpGuest.this);

        // Setting Dialog Title
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // On pressing Settings button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callMenuOptionAPI();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



   /* @Override
    public void getmChannel(Channel mChannel) {
        try {
           // Log.d(TAG,"ClientGroupId : "+mChannel.getClientGroupId());
            if (mChannel != null) {
                mChatGroupId = "" + mChannel.getKey();
                updateCharGroupId(idschedule, "" + mChannel.getKey());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/


    @Override
    public void showErrorMessageView(String message) {
        layout.setVisibility(View.VISIBLE);
        snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", new OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.setDuration(Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snackbar.getView();
        TextView textView = (TextView) group.findViewById(R.id.snackbar_action);
        textView.setTextColor(Color.YELLOW);
        group.setBackgroundColor(ContextCompat.getColor(PickUpGuest.this, R.color.error_background_color));
        TextView txtView = (TextView) group.findViewById(R.id.snackbar_text);
        txtView.setMaxLines(5);
        snackbar.show();
    }

    @Override
    public void retry() {
        retry++;
    }

    @Override
    public int getRetryCount() {
        return retry;
    }

    public void dismissErrorMessage() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        final String userKeyString = MobiComUserPreference.getInstance(this).getSuUserKeyString();
        Intent intent = new Intent(this, ApplozicMqttIntentService.class);
        intent.putExtra(ApplozicMqttIntentService.USER_KEY_STRING, userKeyString);
        startService(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mobiComKitBroadcastReceiver, BroadcastService.getIntentFilter());
        Intent subscribeIntent = new Intent(this, ApplozicMqttIntentService.class);
        subscribeIntent.putExtra(ApplozicMqttIntentService.SUBSCRIBE, true);
        startService(subscribeIntent);

        if (!Utils.isInternetAvailable(this)) {
            String errorMessage = getResources().getString(R.string.internet_connection_not_available);
            showErrorMessageView(errorMessage);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mobiComKitBroadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionsUtils.REQUEST_STORAGE) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.storage_permission_granted);
                if (isAttachment) {
                    isAttachment = false;
                    processAttachment();
                }
            } else {
                showSnackBar(R.string.storage_permission_not_granted);
            }


        } else if (requestCode == PermissionsUtils.REQUEST_LOCATION) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.location_permission_granted);
                processingLocation();
            } else {
                showSnackBar(R.string.location_permission_not_granted);
            }


        } else if (requestCode == PermissionsUtils.REQUEST_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_state_permission_granted);
            } else {
                showSnackBar(R.string.phone_state_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CALL_PHONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_call_permission_granted);
                processCall(contact, currentConversationId);
            } else {
                showSnackBar(R.string.phone_call_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_AUDIO_RECORD) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.record_audio_permission_granted);
                showAudioRecordingDialog();
            } else {
                showSnackBar(R.string.record_audio_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_camera_permission_granted);
                if (isTakePhoto) {
                    processCameraAction();
                } else {
                    processVideoRecording();
                }
            } else {
                showSnackBar(R.string.phone_camera_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CONTACT) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.contact_permission_granted);
                processContact();
            } else {
                showSnackBar(R.string.contact_permission_not_granted);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void processingLocation() {
        if (ApplozicSetting.getInstance(this).isLocationSharingViaMap() && !TextUtils.isEmpty(geoApiKey) && !API_KYE_STRING.equals(geoApiKey)) {
            Intent toMapActivity = new Intent(this, MobicomLocationActivity.class);
            startActivityForResult(toMapActivity, MultimediaOptionFragment.REQUEST_CODE_SEND_LOCATION);
            Log.i("test", "Activity for result strarted");


        } else {
            //================= START GETTING LOCATION WITHOUT LOADING MAP AND SEND LOCATION AS TEXT===============


            if (!((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.location_services_disabled_title)
                        .setMessage(R.string.location_services_disabled_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.location_service_settings, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, LOCATION_SERVICE_ENABLE);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(PickUpGuest.this, R.string.location_sending_cancelled, Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                googleApiClient.disconnect();
                googleApiClient.connect();
            }


            //=================  END ===============


        }


    }

    public void processLocation() {
        if (Utils.hasMarshmallow()) {
            new ApplozicPermissions(this, layout).checkRuntimePermissionForLocation();
        } else {
            processingLocation();
        }
    }

    @Override
    public void onQuickConversationFragmentItemClick(View view, Contact contact, Channel channel, Integer conversationId) {
        Log.d(TAG, "onQuickConversationFragmentItemClick");
        conversation = new ConversationFragment(contact, channel, conversationId);
        // addFragment(this, conversation, ConversationUIService.CONVERSATION_FRAGMENT);
        if (channel != null)
            this.mChannel = channel;
        this.contact = contact;
        this.currentConversationId = conversationId;
    }

    @Override
    public void startContactActivityForResult() {
        Log.d(TAG, "startContactActivityForResult");
        conversationUIService.startContactActivityForResult();
    }

    @Override
    public void addFragment(ConversationFragment conversationFragment) {
        Log.d(TAG, "addFragment");
        // addFragment(this, conversationFragment, ConversationUIService.CONVERSATION_FRAGMENT);
        conversation = conversationFragment;
    }

    @Override
    public void updateLatestMessage(Message message, String formattedContactNumber) {
        Log.d(TAG, "updateLatestMessage");
        conversationUIService.updateLatestMessage(message, formattedContactNumber);
    }

    @Override
    public void removeConversation(Message message, String formattedContactNumber) {
        Log.d(TAG, "removeConversation");
        conversationUIService.removeConversation(message, formattedContactNumber);
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (mCurrentLocation == null) {
                Toast.makeText(this, R.string.waiting_for_current_location, Toast.LENGTH_SHORT).show();
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(UPDATE_INTERVAL);
                locationRequest.setFastestInterval(FASTEST_INTERVAL);
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }
            if (mCurrentLocation != null && conversation != null) {
                conversation.attachLocation(mCurrentLocation);
            }
        } catch (Exception e) {
        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(((Object) this).getClass().getSimpleName(),
                "onConnectionSuspended() called.");


    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            if (conversation != null && location != null) {
                conversation.attachLocation(location);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            showErrorDialog(connectionResult.getErrorCode());
        }


    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
    }

    public Uri getCapturedImageUri() {
        return capturedImageUri;
    }

    public void setCapturedImageUris(Uri capturedImageUri) {
        PickUpGuest.capturedImageUri = capturedImageUri;
    }


    public void showSnackBar(int resId) {
        snackbar = Snackbar.make(layout, resId,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public Uri getVideoFileUri() {
        return videoFileUri;
    }

    public void setVideoFileUri(Uri videoFileUri) {
        this.videoFileUri = videoFileUri;
    }

    public void isTakePhoto(boolean takePhoto) {
        this.isTakePhoto = takePhoto;
    }

    public void isAttachment(boolean attachment) {
        this.isAttachment = attachment;
    }

    public void showAudioRecordingDialog() {


        if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfPermissionForAudioRecording(this)) {
            new ApplozicPermissions(this, layout).requestAudio();
        } else if (PermissionsUtils.isAudioRecordingPermissionGranted(this)) {
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            DialogFragment fragment = AudioMessageFragment.newInstance();
            FragmentTransaction fragmentTransaction = supportFragmentManager
                    .beginTransaction().add(fragment, "dialog");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();


        } else {


            if (ApplozicSetting.getInstance(this).getTextForAudioPermissionNotFound() == null) {
                showSnackBar(R.string.applozic_audio_permission_missing);
            } else {
                snackbar = Snackbar.make(layout, ApplozicSetting.getInstance(this).getTextForAudioPermissionNotFound(),
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
            }


        }
    }

    public void processCall(Contact contactObj, Integer conversationId) {
        this.contact = baseContactService.getContactById(contactObj.getContactIds());
        this.currentConversationId = conversationId;
        try {
            if (activityToOpenOnClickOfCallButton != null) {
                Intent callIntent = new Intent(this, Class.forName(activityToOpenOnClickOfCallButton));
                if (currentConversationId != null) {
                    Conversation conversation = ConversationService.getInstance(this).getConversationByConversationId(currentConversationId);
                    callIntent.putExtra(ConversationUIService.TOPIC_ID, conversation.getTopicId());
                }
                callIntent.putExtra(ConversationUIService.CONTACT, contact);
                startActivity(callIntent);
            } else if (applozicSetting.isActionDialWithoutCallingEnabled()) {
                if (!TextUtils.isEmpty(contact.getContactNumber())) {
                    Intent callIntent;
                    String uri = "tel:" + contact.getContactNumber().trim();
                    callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse(uri));
                    startActivity(callIntent);
                }
            } else {
                if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForCallPermission(this)) {
                    applozicPermission.requestCallPermission();
                } else if (PermissionsUtils.isCallPermissionGranted(this)) {
                    if (!TextUtils.isEmpty(contact.getContactNumber())) {
                        Intent callIntent;
                        String uri = "tel:" + contact.getContactNumber().trim();
                        callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse(uri));
                        startActivity(callIntent);
                    }
                } else {
                    snackbar = Snackbar.make(layout, R.string.phone_call_permission_not_granted,
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }


        } catch (Exception e) {
            Log.i("ConversationActivity", "Call permission is not added in androidManifest");
        }
    }

    public void processCameraAction() {
        try {
            if (PermissionsUtils.isCameraPermissionGranted(this)) {
                imageCapture();
            } else {
                if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForCameraPermission(this)) {
                    applozicPermission.requestCameraPermission();
                } else {
                    imageCapture();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processVideoRecording() {
        try {
            if (PermissionsUtils.isCameraPermissionGranted(this)) {
                showVideoCapture();
            } else {
                if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForCameraPermission(this)) {
                    applozicPermission.requestCameraPermission();
                } else {
                    showVideoCapture();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processContact() {
        if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForContactPermission(this)) {
            applozicPermission.requestContactPermission();
        } else {
            Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            contactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(contactIntent, MultimediaOptionFragment.REQUEST_CODE_CONTACT_SHARE);
        }
    }

    public void imageCapture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_" + ".jpeg";
            photoFile = FileClientService.getFilePath(imageFileName, this, "image/jpeg");
            // Continue only if the File was successfully created
            if (photoFile != null) {
                capturedImageUri = Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
                startActivityForResult(cameraIntent, MultimediaOptionFragment.REQUEST_CODE_TAKE_PHOTO);
            }
        }
    }

    public void processAttachment() {
        if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForStoragePermission(this)) {
            applozicPermission.requestStoragePermissions();
        } else {
            Intent intentPick = new Intent(this, MobiComAttachmentSelectorActivity.class);
            startActivityForResult(intentPick, MultimediaOptionFragment.REQUEST_MULTI_ATTCAHMENT);
        }
    }

    public Contact getContact() {
        return contact;
    }

    @Override
    public Channel getmChannel() {
        return mChannel;
    }


    public Integer getConversationId() {
        return currentConversationId;
    }

    @Override
    public String getScheduleId() {
        return idschedule;
    }


    private String getScheduleName() {
        if (!(TextUtils.isEmpty(type))) {
            switch (type) {
                case KEY_BUDDY_UP:
                    return "Buddy Up";
                case KEY_PICK_UP:
                    return "Pick Up";
            }
        }
        return "Pick Up";
    }


    public void showVideoCapture() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "VID_" + timeStamp + "_" + ".mp4";
        File fileUri = FileClientService.getFilePath(imageFileName, this, "video/mp4");
        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileUri));
        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        videoFileUri = Uri.fromFile(fileUri);
        startActivityForResult(videoIntent, MultimediaOptionFragment.REQUEST_CODE_CAPTURE_VIDEO_ACTIVITY);
    }

    @Override
    public void onMapReady(GoogleMap googlemap) {
        googleMap = googlemap;
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    @Override
    public void getBack() {
        finish();
//        Pastnotifiationfragment.getback.getBack();
    }

    private class LoadingData extends AsyncTask<URL, Integer, Long> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            baseContactService = new AppContactService(PickUpGuest.this);
            conversationUIService = new ConversationUIService(PickUpGuest.this);
            mobiComKitBroadcastReceiver = new MobiComKitBroadcastReceiver(PickUpGuest.this, conversationUIService);
        }

        protected Long doInBackground(URL... urls) {
            doInitializationOnChatModule();
            return null;
        }

    }


}
