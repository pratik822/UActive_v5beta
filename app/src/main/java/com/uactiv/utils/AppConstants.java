package com.uactiv.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.uactiv.R;
import com.uactiv.activity.BuddyUpDetailsActivity;
import com.uactiv.activity.BusinessLocationMap;
import com.uactiv.activity.CreatePickUp;
import com.uactiv.activity.In_App_Browser;
import com.uactiv.activity.PickChoose;
import com.uactiv.activity.PickUpEventPage;
import com.uactiv.activity.PickUpGuest;
import com.uactiv.activity.RoundRectCornerImageView;
import com.uactiv.activity.Sign_In;
import com.uactiv.application.UActiveApplication;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.Notification_Fragment;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.VolleySingleton;
import com.uactiv.views.CircularImageViews;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomEditText;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class AppConstants {

    public static final String PICKUP_ITEM_NAME = "pickUpName";
    public static final String BUDDYUP_ITEM_NAME = "buddyUpName";
    public static final int MAX_WORDS = 100;
    public static final int TIME_LIMT_BUDDY_UP = 1800000;
    public static final int TIME_LIMT_PICK_UP = 60000;
    public static final int FACEBOOK_PIC_SIZE = 500;
    public static final String PROJECT_NUMBER = "606085224402";
    public static final String EXTRA_MESSAGE = "message";
    public static final String GCM_NOTIFICATION = "GCM Notification";
    public static final String GCM_DELETED_MESSAGE = "Deleted messages on server: ";
    public static final String GCM_INTENT_SERVICE = "GcmIntentService";
    public static final String GCM_SEND_ERROR = "Send error: ";
    public static final String GCM_RECEIVED = "Received: ";
    public static final String NEW_PUSH_EVENT = "new-push-event";
    public static final int CROP_WIDTH = 3;
    public static final int CROP_HEIGHT = 3;
    public static final int CHILD = 1;
    public static BusinessLocationMap businessLocationMap;
    static AlertDialog dialogs;
    static Context myctx;
    static android.app.AlertDialog dialog = null;
    /**
     * constants for Google analytics
     */
    //Screen tracking id's
    public final static String SCREEN_TRACKING_ID_LOGIN = "Login Screen";
    public final static String SCREEN_TRACKING_ID_GET_ACTIV = "Create Profile page( Second screen)";
    public final static String SCREEN_TRACKING_ID_HOME = "Buddy Up Dashboard";
    public final static String SCREEN_TRACKING_ID_NOTIFICATION = "Notification";
    public final static String SCREEN_TRACKING_ID_USER_PROFILE = "My profile screen";
    public final static String SCREEN_TRACKING_ID_BUSINESS_PROFILE = "Business profile screen";
    public final static String SCREEN_TRACKING_ID_FAVOURITES = "Favourites screen";
    public final static String SCREEN_TRACKING_ID_CREATE_PICKUP = "Create Pick Up page";
    public final static String SCREEN_TRACKING_ID_CREATE_BUDDYUP = "CreateBuddyUp";
    public final static String SCREEN_TRACKING_ID_SEE_ALL = "See all pick Ups on Map view";


    public final static String SCREEN_TRACKING_ID_BUDDYUPDASH = "Buddy Profile details";
    public final static String SCREEN_TRACKING_ID_PICKUPDASH = "Pick Up dashboard";
    public final static String SCREEN_TRACKING_ID_BUDDYUPDETAILS = "Buddy Up Details";
    public final static String SCREEN_TRACKING_ID_PICKUPDETAILS = "Pick Up detail page";
    public final static String SCREEN_TRACKING_ID_EDITBUDDYUP = "Edit Buddy Up";
    public final static String SCREEN_TRACKING_ID_EDITPICKUP = "Edit Pick Up";
    public final static String SCREEN_TRACKING_ID_EDITUSERPROFILE = "Edit Profile screen";
    public final static String SCREEN_TRACKING_ID_EDITBUSINESSPROFILE = "Business Profile edit screen";
    public final static String SCREEN_TRACKING_ID_BADGESPOPUP = "Congratulations for graduating screen";


    public final static String SCREEN_TRACKING_ID_SUGGESTEDLOCATION = "Suggested location detail page";
    public final static String SCREEN_TRACKING_ID_SUGGESTEDLOCATIONMAP = "Suggested locations on map";
    public final static String SCREEN_TRACKING_ID_SEEALLPICKUPONMAP = "See all pick Ups on Map view";
    public final static String SCREEN_TRACKING_ID_SEEALLPICKUPONLIST = "See all pick Ups on list view";
    public final static String SCREEN_TRACKING_ID_ALLPICKUPONMAP = "See all pick Ups on Map view";

    public final static String SCREEN_TRACKING_ID_CREATEPICKUPPAGE = "Create Pick Up page";
    public final static String SCREEN_TRACKING_ID_PICKUPREQUESTTOJOIN = "pick up requested to join";

    public final static String SCREEN_TRACKING_ID_INAPPNOTIFICATION = "In app Notification screen";
    public final static String SCREEN_TRACKING_ID_PICKUPDETAILFROMNOTIFICATION = "Pick up detail from Notifications screen";
    public final static String SCREEN_TRACKING_ID_PICKUPCHATSCREEN = "Pick Up chat screen";
    public final static String SCREEN_TRACKING_ID_BUDDYUPCHAT = "Buddy up chat screen";
    public final static String SCREEN_TRACKING_ID_BUDDYUPDETAILSFROMNOTIFIACTION = "Buddy up detail from Notifications";
    public final static String SCREEN_TRACKING_ID_SHEDULEDCALLENDER = "Schedule calender Page";

    public final static String SCREEN_TRACKING_ID_MENU = "Menu screen";
    public final static String SCREEN_TRACKING_ID_PROFILESCREEN = "My profile screen";
    public final static String SCREEN_TRACKING_ID_EDITPROFILESCREEN = "Edit Profile screen";
    public final static String SCREEN_TRACKING_ID_FAVOURITESCREEN = "Favourites screen";

    public final static String SCREEN_TRACKING_INAPPNOTIFICATION = "In app Notification screen";
    public final static String SCREEN_TRACKING_PICKUPDETAILNOTIFICATION = "Pick up detail from Notifications screen";
    public final static String SCREEN_TRACKING_PICKUPCHATSCREEN = "Pick Up chat screen";
    public final static String SCREEN_TRACKING_BUDDYUPCHATSCREEN = "Buddy up chat screen";
    public final static String SCREEN_TRACKING_BUDDYUPDETAILNOTIFICATION = "Buddy up detail from Notifications";
    public final static String SCREEN_TRACKING_SHEDULEDCALLENDERPAGE = "Schedule calender Page";


    public final static String SCREEN_TRACKING_ID_GROUPSCREEN = "Go to groups screen";
    public final static String SCREEN_TRACKING_ID_GROUPSCREENFEV = "View Group members screen in favourites";
    public final static String SCREEN_TRACKING_ID_ADDMEMBERTOGROUP = "Add member to group screen in favourites";
    public final static String SCREEN_TRACKING_ID_NEWGROUP = "New Group creation";
    public final static String SCREEN_TRACKING_ID_INVITEFRIENDS = "Invite Friends screen";
    public final static String SCREEN_TRACKING_ID_SETTINGS = "Settings";
    public static boolean isShown = false;
    public static boolean isShown_one = false;
    public static boolean isShown_two = false;
    public final static String SCREEN_TRACKING_ID_CHANGEPASSWORD = "Change password screen in settings";

    public final static String SCREEN_TRACKING_ID_CREATEPROFILEPAGE = "Create Profile page (first screen)";
    public final static String SCREEN_TRACKING_ID_CREATEPROFILEPAGESECOND = "Create Profile page( Second screen)";
    public final static String SCREEN_TRACKING_ID_GRADUATIONGSCREEN = "Congratulations for graduating screen";
    public final static String SCREEN_TRACKING_ID_BADGEPOPUP = "Badges Pop Up screen";
    public final static String SCREEN_TRACKING_ID_ABOUT = "About page";
    public final static String SCREEN_TRACKING_ID_BUSINESSPROFILE = "Business profile screen";
    public final static String SCREEN_TRACKING_ID_BUSINESSPROFILEEDIT = "Business profile edit page";
    public final static String SCREEN_TRACKING_ID_FORGOTPASSWORDSCREEN = "Forgot password screen";

    public final static String SCREEN_TRACKING_ID_PERMISSIONSCREEN = "Grant access to call, phone , camera, Access coarse location, Access fine location\n" +
            "Read External storage, Write external storage";

    public final static String SCREEN_TRACKING_ID_REVIEWTHEPICKUP = "Review the Pick Up! screen";


    //Event tracking id's
    public final static String EVENT_TRACKING_ID_LOGIN = "Login Button";
    public final static String EVENT_TRACKING_ID_LOGIN_FACEBOOK = "Login with Facebook";
    public final static String EVENT_TRACKING_ID_CREATE_ACCOUNT = "Create a new Account";
    public final static String EVENT_TRACKING_ID_NEXT = "Next";
    public final static String EVENT_TRACKING_ID_SIGN_UP_FACEBOOK = "Signup with facebook";
    public final static String EVENT_TRACKING_ID_FORGOT = "Forgot? Button on login screen";
    public final static String EVENT_TRACKING_ID_GET_ACTIV = "Get Activ";
    public final static String EVENT_TRACKING_ID_BUDDY_UP = "Buddy up";
    public final static String EVENT_TRACKING_ID_PICK_UP = "pickup";
    public final static String EVENT_TRACKING_ID_ADD_FAV = " Add to favorite on Buddy profile detail screen";
    public final static String EVENT_TRACKING_ID_BUDDY_UP_REQUEST = "Buddy up request";
    public final static String EVENT_TRACKING_ID_BUDDYUP_ACCEPT = "Buddy Up Accept";
    public final static String EVENT_TRACKING_ID_BUDDYUP_DECLINE = "Buddy Up Decline";
    public final static String EVENT_TRACKING_ID_SEND_REQUEST = "Send request";
    public final static String EVENT_TRACKING_ID_CHOOSE_FROM_MAP = "choose from map";
    public final static String EVENT_TRACKING_ID_CREATE_PICK_UP = "Pick Up Create Button";//Done/Pick Up Create
    public final static String EVENT_TRACKING_ID_PRIVATE = "private";
    public final static String EVENT_TRACKING_ID_PUBLIC = "public";
    public final static String EVENT_TRACKING_ID_PICKUP_INVITE = "Invite button Invite People on create Pick Up page";
    public final static String EVENT_TRACKING_ID_CHAT = "Chat";
    public final static String EVENT_TRACKING_ID_REQUEST_PICKUP_TO_JOIN = "pick up requested to join";
    public final static String EVENT_TRACKING_ID_PICKUP_ACCEPT = "Pick Up Accept";
    public final static String EVENT_TRACKING_ID_PICKUP_DECLINE = "Pick Up Decline";
    public final static String EVENT_TRACKING_ID_MENU = "menu";
    public final static String EVENT_TRACKING_ID_CANCEL = "cancel";
    public final static String EVENT_TRACKING_ID_ABANDON = "abandon";

    public final static String EVENT_TRACKING_ID_REPORT_USER = "Report User";
    public final static String EVENT_TRACKING_ID_CREATEGROUP = "Create Group";
    public final static String EVENT_TRACKING_ID_BOOKINGREQUEST = "Suggested locations on map";
    public final static String EVENT_TRACKING_ID_PICKUPSEEALL = "Pick Up See All";
    public final static String EVENT_TRACKING_ID_SKIPUSERRATING = "Skip User Rating";
    public final static String EVENT_TRACKING_ID_INPUTUSERRATING = "Input User Rating";
    public final static String EVENT_TRACKING_ID_CHNAGEGENDER = "Gender Preference on setting screen";
    public final static String EVENT_TRACKING_ID_RECEIVEBUDDYUPREQUESTOFF = "Receive buddy up Requests on setting screen";
    public final static String EVENT_TRACKING_ID_PUSHNOTIFICATIONSWITCHOFF = "Push Notifications on setting screen";
    public final static String EVENT_TRACKING_ID_CHANGE_PASSWORD = "Change Password";
    public final static String EVENT_TRACKING_ID_CHNAGESEARCHRADIUS = "Change Search Radius";
    public final static String EVENT_TRACKING_ID_EDITPROFILEPICTURE = "Edit Profile Picture";
    public final static String EVENT_TRACKING_ID_EDITUSERACTIVITIES = "Edit User activities";
    public final static String EVENT_TRACKING_ID_INVITEFRIENDSTOAPP = "Invite Friends to App";
    public final static String EVENT_TRACKING_ID_CHALLENGE = "Challenge";
    public final static String EVENT_TRACKING_ID_EDIT_PROFILE = "Edit Profile Picture";

    public final static String EVENT_TRACKING_ID_REFERRAL_WHATS_APP = "Referral Invite Whats App";
    public final static String EVENT_TRACKING_ID_REFERRAL_MESSAGE = "Referral Invite Message";
    public final static String EVENT_TRACKING_ID_REFERRAL_TWITTER = "Referral Invite Twitter";
    public final static String EVENT_TRACKING_ID_REFERRAL_FACEBOOK = "Referral Facebook";
    public final static String EVENT_TRACKING_ID_REFERRAL_GOOGLE_PLUS = "Referral Google Plus";
    public final static String EVENT_TRACKING_ID_REFERRAL_MAIL = "Referral Invite Mail";
    public final static String EVENT_TRACKING_ID_ABOUT_CONTACT_US = "Contact Us";
    public final static String EVENT_TRACKING_ID_ABOUT_TERMS = "Terms";
    public final static String EVENT_TRACKING_ID_ABOUT_PRIVACY_POLICY = "Privacy Policy";
    public final static String EVENT_TRACKING_ID_ABOUT_RATE_US = "Rate Us on play store";

    public final static String radius = " Search radius on setting screen ";
    public final static String Gender = " Gender Preference on setting screen";
    public final static String buddy = " Receive buddy up Requests on setting screen ";
    public final static String push = " Push Notifications on setting screen ";
    public final static String backarrow = "  Back Arrow on the Buddy up detail from Notifications ";
    public final static String chattoggle = "  chat toggle on Buddy up detail from Notifications ";
    public final static String report = "   Repot user on Buddy up detail from Notifications  ";
    public final static String pinonmap = "   Pin on the map on Buddy up detail from Notifications  ";
    public final static String cancel = "   cancel Buddy up on Buddy up detail from Notifications  ";
    public final static String editbuddy = "   Edit Buddy up on Buddy up detail from Notifications   ";

    public final static String backarrows = "    Back Arrow on Pick up detail from Notifications screen   ";
    public final static String toggle = "    chat toggle button on Pick up detail from Notifications screen   ";
    public final static String reports = "    Repot user on Pick up detail from Notifications screen   ";
    public final static String pins = "   pin on the map on Pick up detail from Notifications screen  ";
    public static String isread = "";
    public static String popup_idOne;
    public static String imageOne;


    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static String emailPatternTwoDecimalPo = "^[_A-Za-z0-9-\\\\+]+(\\\\.[_A-Za-z0-9-]+)*\n" + "@[A-Za-z0-9-]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$;\n";
    public static String emailPatternTwoDecimalPoint =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static SimpleDateFormat sor_sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static SimpleDateFormat NOTIFICATION_DATE_SORT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/UActive";
    public static String ACTION_NOTIFICATION_COUNT_CHANGED = "action.notificationcount";
    public static String ACTION_NEW_MESSAGE_RECEIVED = "action.new_message_received";
    public static String ACTION_PROFILE_PICTURE_CHANGED = "action.profile_changed.receiver";
    public static int TUTORIAL_BUDDY_UP_ID = 12;
    public static int TUTORIAL_BUDDY_UP_VIEW_ID = 14;
    public static int TUTORIAL_PICK_UP_ID = 16;
    public static int TUTORIAL_CREATE_PICK_UP_ID = 18;
    public static int TUTORIAL_VIEW_ACTIVITY_ID = 20;
    public static int TUTORIAL_VIEW_CHAT_ID = 22;
    public static int TUTORIAL_ADD_FAV_ID = 24;
    public static String[] FACEBOOK_LOGIN_PERMISSIONS = {"public_profile", "user_friends", "email", "user_birthday", "read_custom_friendlists"};
    public static String FACEBOOK_LOGIN_FIELDS = "id,name,email,gender,first_name,last_name,birthday";
    public static String FACEBOOK_FRIENDS_FIELDS = "id,name,picture,context.fields(mutual_friends)";
    static InputMethodManager mInputMethodManager;
    public static String ACTION_PAYMENT_RECIVED = "action.paymentrecived";

    //print log
    public static void Log(String string, String string2) {
        Log.e(string, string2);
    }

    public static void showPickupShareAlert(final Context context, final String message, final ShareDialog shareDialog) {

        EditText editUrl;
        Button btnShareUrl;
        CustomTextView tvShare = null;

        CustomTextView tvwatsapp = null;
        CustomTextView tvMessage = null;
        CustomTextView tvTwitter = null;
        CustomTextView tvFacebook = null;
        CustomTextView tvGooglePlus = null;
        CustomTextView tvMail = null;
        ImageView imageView1 = null;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.share_inflate);
        dialog.show();

        //views
        tvShare = (CustomTextView) dialog.findViewById(R.id.tvShare);
        editUrl = (EditText) dialog.findViewById(R.id.editUrl);
        btnShareUrl = (Button) dialog.findViewById(R.id.btnShareUrl);
        imageView1 = (ImageView) dialog.findViewById(R.id.imageView1);
        imageView1.setVisibility(View.INVISIBLE);
        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(context, "referalcode"))) {
            editUrl.setText("Ref Code: " + SharedPref.getInstance().getStringVlue(context, "referalcode"));
        }
        editUrl.setEnabled(false);
        btnShareUrl.setText("Invite Friends");
        tvShare.setText("Share the App");
        btnShareUrl.setVisibility(View.GONE);
        tvwatsapp = (CustomTextView) dialog.findViewById(R.id.tvWhatsApp);
        tvMessage = (CustomTextView) dialog.findViewById(R.id.tvMessage);
        tvTwitter = (CustomTextView) dialog.findViewById(R.id.tvTwitter);
        tvFacebook = (CustomTextView) dialog.findViewById(R.id.tvFacebook);
        tvGooglePlus = (CustomTextView) dialog.findViewById(R.id.tvGooglePlus);
        tvMail = (CustomTextView) dialog.findViewById(R.id.tvMail);

        //OnClicks
        tvwatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_WHATS_APP);
                shareWatsapp(context, message, null);

            }
        });


        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_MESSAGE);
                shareMessage(context, message);
            }
        });


        tvTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_TWITTER);
                shareTwitter(context, message);
            }
        });


        tvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (shareDialog != null) {
                    Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_FACEBOOK);
                    facebookShare(context, shareDialog, message);
                }


            }
        });


        tvGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_GOOGLE_PLUS);
                shareGooglePlus(context, message, null);
            }
        });


        tvMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_MAIL);
                shareMail(context, message, null);
            }
        });


    }

    public static void showShareAlert2(final Context context, String s, final String message, final String img_url, final ShareDialog shareDialog) {

        EditText editUrl;
        Button btnShareUrl;
        CustomTextView tvShare = null;

        CustomTextView tvwatsapp = null;
        CustomTextView tvMessage = null;
        CustomTextView tvTwitter = null;
        CustomTextView tvFacebook = null;
        CustomTextView tvGooglePlus = null;
        CustomTextView tvMail = null;
        ImageView imageView1 = null;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.pickup_share_inflate);
        dialog.show();

        //views
        tvShare = (CustomTextView) dialog.findViewById(R.id.tvShare);
        editUrl = (EditText) dialog.findViewById(R.id.editUrl);
        btnShareUrl = (Button) dialog.findViewById(R.id.btnShareUrl);
        imageView1 = (ImageView) dialog.findViewById(R.id.imageView1);
        imageView1.setVisibility(View.INVISIBLE);
        if (img_url != null && img_url.length() > 0) {
            editUrl.setText("" + img_url);
        } else
            editUrl.setEnabled(false);
        btnShareUrl.setText("Invite Friends");
        tvShare.setText("Share the Pick Up");
        tvShare.setText(message);
        btnShareUrl.setVisibility(View.GONE);
        tvwatsapp = (CustomTextView) dialog.findViewById(R.id.tvWhatsApp);
        tvMessage = (CustomTextView) dialog.findViewById(R.id.tvMessage);
        tvTwitter = (CustomTextView) dialog.findViewById(R.id.tvTwitter);
        tvFacebook = (CustomTextView) dialog.findViewById(R.id.tvFacebook);
        tvGooglePlus = (CustomTextView) dialog.findViewById(R.id.tvGooglePlus);
        tvMail = (CustomTextView) dialog.findViewById(R.id.tvMail);

        //OnClicks
        tvwatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareWatsapp(context, message + System.getProperty("line.separator") + img_url, null);
            }
        });


        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareMessage(context, message + System.getProperty("line.separator") + img_url);
            }
        });


        tvTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareTwitter(context, message + System.getProperty("line.separator") + img_url);
            }
        });


        tvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (shareDialog != null) {
                    facebookImageShare(context, shareDialog, message, img_url);
                }
            }
        });


        tvGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareGooglePlus(context, message + System.getProperty("line.separator") + img_url, null);
            }
        });


        tvMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareMail(context, message + System.getProperty("line.separator") + img_url, null);
            }
        });


    }

    public static void showInviteShareAlert(final Context context, final String message, final ShareDialog shareDialog) {

        EditText editUrl;
        Button btnShareUrl;
        CustomTextView tvShare = null;

        CustomTextView tvwatsapp = null;
        CustomTextView tvMessage = null;
        CustomTextView tvTwitter = null;
        CustomTextView tvFacebook = null;
        CustomTextView tvGooglePlus = null;
        CustomTextView tvMail = null;
        ImageView imageView1 = null;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.share_inflate);
        dialog.show();

        //views
        tvShare = (CustomTextView) dialog.findViewById(R.id.tvShare);
        editUrl = (EditText) dialog.findViewById(R.id.editUrl);
        btnShareUrl = (Button) dialog.findViewById(R.id.btnShareUrl);
        imageView1 = (ImageView) dialog.findViewById(R.id.imageView1);
        imageView1.setVisibility(View.INVISIBLE);
        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(context, "referalcode"))) {
            editUrl.setText("Ref Code: " + SharedPref.getInstance().getStringVlue(context, "referalcode"));
        }
        editUrl.setEnabled(false);
        btnShareUrl.setText("Invite Friends");
        tvShare.setText("Share the App");
        btnShareUrl.setVisibility(View.GONE);
        tvwatsapp = (CustomTextView) dialog.findViewById(R.id.tvWhatsApp);
        tvMessage = (CustomTextView) dialog.findViewById(R.id.tvMessage);
        tvTwitter = (CustomTextView) dialog.findViewById(R.id.tvTwitter);
        tvFacebook = (CustomTextView) dialog.findViewById(R.id.tvFacebook);
        tvGooglePlus = (CustomTextView) dialog.findViewById(R.id.tvGooglePlus);
        tvMail = (CustomTextView) dialog.findViewById(R.id.tvMail);

        //OnClicks
        tvwatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (!AppConstants.isGestLogin(context)) {
                    Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_WHATS_APP);
                } else {
                    Utility.setEventTracking(context, "Share Pickup Screen", "Guest login-" + AppConstants.EVENT_TRACKING_ID_REFERRAL_WHATS_APP);
                }

                shareWatsapp(context, message, null);

            }
        });


        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (!AppConstants.isGestLogin(context)) {
                    Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_MESSAGE);
                } else {
                    Utility.setEventTracking(context, "Share Pickup Screen", "Guest login-" + AppConstants.EVENT_TRACKING_ID_REFERRAL_MESSAGE);
                }
                shareMessage(context, message);
            }
        });


        tvTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (!AppConstants.isGestLogin(context)) {
                    Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_TWITTER);
                } else {
                    Utility.setEventTracking(context, "Share Pickup Screen", "Guest login-" + AppConstants.EVENT_TRACKING_ID_REFERRAL_TWITTER);
                }
                shareTwitter(context, message);
            }
        });


        tvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (shareDialog != null) {

                    if (!AppConstants.isGestLogin(context)) {
                        Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_FACEBOOK);
                    } else {
                        Utility.setEventTracking(context, "Share Pickup Screen", "Guest login-Referral Facebook");
                    }
                    facebookShare(context, shareDialog, message);
                }


            }
        });


        tvGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (!AppConstants.isGestLogin(context)) {
                    Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_GOOGLE_PLUS);
                } else {
                    Utility.setEventTracking(context, "Share Pickup Screen", "Guest login-" + AppConstants.EVENT_TRACKING_ID_REFERRAL_GOOGLE_PLUS);
                }
                shareGooglePlus(context, message, null);
            }
        });


        tvMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!AppConstants.isGestLogin(context)) {
                    Utility.setEventTracking(context, "Share Pickup Screen", AppConstants.EVENT_TRACKING_ID_REFERRAL_MAIL);
                } else {
                    Utility.setEventTracking(context, "Share Pickup Screen", "Guest login-" + AppConstants.EVENT_TRACKING_ID_REFERRAL_MAIL);
                }
                shareMail(context, message, null);
            }
        });


    }

    public static void facebookShare(Context context, ShareDialog shareDialog, String message) {

        Log.e("message", "message " + message);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Get Active Now...")
                    .setContentDescription(message)
                    .setContentUrl(Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName()))
                    // .setImageUrl(Uri.parse("http://doodlebluestaging.com//uactive//uploads//1448706809062profilepic_20151128105011.jpg"))
                    .build();
            shareDialog.show(linkContent);
        } else {
            Utility.showToastMessage(context, context.getString(R.string.msg_undefined_error));
        }
    }

    static void facebookImageShare(Context context, ShareDialog shareDialog, String message, String imageUrl) {

        if (imageUrl != null && imageUrl.length() > 0) {

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Get Active Now...")
                        .setContentDescription(message)
                        .setContentUrl(Uri.parse(imageUrl))
                        // .setImageUrl(Uri.parse(imageUrl))
                        .build();
                shareDialog.show(linkContent);
            } else {
                Utility.showToastMessage(context, context.getString(R.string.msg_undefined_error));
            }
        }
    }

    public static void showShareAlert(final Context context, final String message, final String img_url, String title, final ShareDialog shareDialog) {

        EditText editUrl;
        Button btnShareUrl;
        CustomTextView tvShare = null;

        CustomTextView tvwatsapp = null;
        CustomTextView tvMessage = null;
        CustomTextView tvTwitter = null;
        CustomTextView tvFacebook = null;
        CustomTextView tvGooglePlus = null;
        CustomTextView tvMail = null;
        ImageView imageView1 = null;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.pickup_share_inflate);
        dialog.show();

        //views
        tvShare = (CustomTextView) dialog.findViewById(R.id.tvShare);
        editUrl = (EditText) dialog.findViewById(R.id.editUrl);
        btnShareUrl = (Button) dialog.findViewById(R.id.btnShareUrl);
        imageView1 = (ImageView) dialog.findViewById(R.id.imageView1);
        imageView1.setVisibility(View.INVISIBLE);
        if (img_url != null && img_url.length() > 0) {
            editUrl.setText("" + img_url);
        } else
            editUrl.setEnabled(false);
        btnShareUrl.setText("Invite Friends");
        tvShare.setText("Share the Pick Up");
        tvShare.setText(title);
        btnShareUrl.setVisibility(View.GONE);
        tvwatsapp = (CustomTextView) dialog.findViewById(R.id.tvWhatsApp);
        tvMessage = (CustomTextView) dialog.findViewById(R.id.tvMessage);
        tvTwitter = (CustomTextView) dialog.findViewById(R.id.tvTwitter);
        tvFacebook = (CustomTextView) dialog.findViewById(R.id.tvFacebook);
        tvGooglePlus = (CustomTextView) dialog.findViewById(R.id.tvGooglePlus);
        tvMail = (CustomTextView) dialog.findViewById(R.id.tvMail);

        //OnClicks
        tvwatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareWatsapp(context, message + System.getProperty("line.separator") + img_url, null);
            }
        });


        tvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareMessage(context, message + System.getProperty("line.separator") + img_url);
            }
        });


        tvTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareTwitter(context, message + System.getProperty("line.separator") + img_url);
            }
        });


        tvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (shareDialog != null) {
                    facebookImageShare(context, shareDialog, message, img_url);
                }
            }
        });


        tvGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareGooglePlus(context, message + System.getProperty("line.separator") + img_url, null);
            }
        });


        tvMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareMail(context, message + System.getProperty("line.separator") + img_url, null);
            }
        });


    }

    /*  public static void showShareAlert(Context context) {
          EditText editUrl;
          Button btnShareUrl;
          final Dialog dialog = new Dialog(context);
          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
          dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
          dialog.setContentView(R.layout.share_inflate);
          dialog.show();

          //views
          editUrl = (EditText) dialog.findViewById(R.id.editUrl);
          btnShareUrl = (Button) dialog.findViewById(R.id.btnShareUrl);
      }
  */
    public static void hideKeyBoard(Context context) {
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void showKeyBoard(Context context) {
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideViewKeyBoard(Context mContext, View view) {
        try {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void shareWatsapp(Context context, String msg, String imagepath) {

        Utility.setEventTracking(context, "Invite friends screen", "Whatsapp on Invite friends screen");
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, msg);
        if (imagepath != null && !TextUtils.isEmpty(imagepath)) {
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + imagepath));
        }

        try {
            context.startActivity(whatsappIntent);
        } catch (ActivityNotFoundException ex) {
            Utility.showToastMessage(context, "Whatsapp have not been installed.");
        }
    }

    public static void shareMessage(Context context, String msg) {
        try {
            Utility.setEventTracking(context, "Invite friends screen", "Message on Invite friends screen");
            Intent intentsms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + ""));
            intentsms.putExtra("sms_body", msg);
            context.startActivity(intentsms);
        } catch (Exception e) {

        }

    }

    public static void shareTwitter(Context context, String msg) {
        try {
            Utility.setEventTracking(context, "Invite friends screen", "Twitter on Invite friends screen");
            // Check if the Twitter app is installed on the phone.
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");

//            intent.putExtra(Intent.EXTRA_TEXT, msg+" #UActiv");
//            intent.putExtra(Intent.EXTRA_TEXT, msg + context.getResources().getString(R.string.tag_uactiv));
            SpannableStringBuilder sb = new SpannableStringBuilder(context.getResources().getString(R.string.tag_uactiv));
            StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
            sb.setSpan(b, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make first 4 characters Bold

            intent.putExtra(Intent.EXTRA_TEXT, msg + sb);
            /*if(imageUrl != null && imageUrl.length() > 0) {
               // intent.putExtra(Intent.EXTRA_STREAM, imageUrl);
                intent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(imageUrl));
                Log.e("shareTwitter", "" + imageUrl);
            }*/
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Twitter is not installed on this device", Toast.LENGTH_LONG).show();

        }

    }

    public static void shareGooglePlus(Context context, String msg, String imageUrl) {
        //  String photoUri = null;
        try {
            Utility.setEventTracking(context, "Invite friends screen", "Gmail on Invite friends screen");
            //   photoUri = MediaStore.Images.Media.insertImage(
            //  context.getContentResolver(), tmpFile.getAbsolutePath(), null, null);
            Intent shareIntent;

            if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {

                shareIntent = ShareCompat.IntentBuilder.from((Activity) context)
                        .setText(msg)
                        .setType("image/jpeg")

                        //   .setStream(Uri.parse("file:///" + imageUrl))
                        .getIntent()
                        .setPackage("com.google.android.apps.plus");


                shareIntent.putExtra(Intent.EXTRA_TEXT, imageUrl);
            } else {

                shareIntent = ShareCompat.IntentBuilder.from((Activity) context)
                        .setText(msg)
                        .setType("text/plain")
                        .getIntent()
                        .setPackage("com.google.android.apps.plus");
            }

            context.startActivity(shareIntent);
        } catch (Exception e) {
            Utility.showToastMessage(context, "Google plus app not found!");
            e.printStackTrace();
            Log.e("Catch", "" + e.getMessage());
        }

    }

    public static void businessShareMailWithUs(Context context, String emailId) {
        Utility.setEventTracking(context, "Invite friends screen", "Mail on Invite friends screen");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailId});
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareMail(Context context, String msg, String imageUrl) {
        try {
            Utility.setEventTracking(context, "Invite friends screen", "MAil on Invite friends screen");
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("application/image");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "" + context.getResources().getString(R.string.app_name));
            emailIntent.putExtra(Intent.EXTRA_TEXT, msg);
            if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + imageUrl));
            }
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showReviewDialog(final Context context, final Bundle bundle, final String Url, final boolean isbusiness, final ResponseListener listener) {

        CircularImageViews profileImage = null;
        CustomButton btnSubmit = null;
        CustomTextView username = null;
        RatingBar review_ratingBar = null;
        CustomTextView tv_game_details = null;
        CustomTextView tv_leaveuscomment = null;
        CustomTextView tv_note_one = null;
        CustomTextView tv_note_two = null;
        CustomTextView headerText = null;
        ImageView btnClear = null;

        final String[] comment = {null};

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.review_dialog);
        dialog.setCancelable(false);
      /*  Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);*/
        profileImage = (CircularImageViews) dialog.findViewById(R.id.profileImage);
        btnSubmit = (CustomButton) dialog.findViewById(R.id.btn_submit);
        username = (CustomTextView) dialog.findViewById(R.id.tvBuddyName);
        review_ratingBar = (RatingBar) dialog.findViewById(R.id.ratingBar1);
        tv_game_details = (CustomTextView) dialog.findViewById(R.id.tv_activityname);
        tv_leaveuscomment = (CustomTextView) dialog.findViewById(R.id.tv_leavecomments);
        tv_note_one = (CustomTextView) dialog.findViewById(R.id.tv_note_one);
        tv_note_two = (CustomTextView) dialog.findViewById(R.id.tv_note_two);
        headerText = (CustomTextView) dialog.findViewById(R.id.headerText);
        btnClear = (ImageView) dialog.findViewById(R.id.btn_clear);
        if (!isbusiness) {
            if (bundle.getString("type").equals("buddyup")) {
                headerText.setText("Review your Buddy!");
                tv_note_one.setText(context.getText(R.string.buddyupnote_one));
                tv_note_two.setText(context.getText(R.string.buddyupnote_two));
            }
        } else {
            headerText.setText("Review Pick Up Venue");
            tv_note_one.setText(R.string.venue_note_one);
            tv_note_two.setText(context.getText(R.string.venue_note_two));
        }

        if (Utility.isNullCheck(bundle.getString("image"))) {
            Utility.setImageUniversalLoader(context, bundle.getString("image"), profileImage);
        }

        if (Utility.isNullCheck(bundle.getString("name"))) {
            username.setText("" + bundle.getString("name"));
        }

        if (Utility.isNullCheck(bundle.getString("activity"))) {
            tv_game_details.setText("" + bundle.getString("activity") + ": " + Utility.dateFormats(bundle.getString("date")) + " @" + Utility.timeFormatChanage(bundle.getString("start_time")));
        }

        dialog.show();

        final RatingBar finalReview_ratingBar = review_ratingBar;
        final CustomButton finalBtnSubmit = btnSubmit;
        final CustomButton finalBtnSubmit1 = btnSubmit;

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.setEventTracking(context, "", AppConstants.EVENT_TRACKING_ID_INPUTUSERRATING);

                if (finalReview_ratingBar.getRating() != 0) {

                    if (finalReview_ratingBar.getRating() <= 3) {


                        if (comment[0] == null) {
                            final Dialog dialog1 = new Dialog(context);
                            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog1.setContentView(R.layout.comment);

                            final CustomEditText customEditText = (CustomEditText) dialog1.findViewById(R.id.comment_text);
                            CustomTextView done = (CustomTextView) dialog1.findViewById(R.id.done);
                            if (comment[0] != null && comment.length > 0) {
                                customEditText.setText("" + comment[0]);
                            }

                            dialog1.show();
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Utility.setEventTracking(context, "Review the Pick Up! screen", "Done on Review pickup");
                                    if (customEditText.length() > 0) {
                                        comment[0] = customEditText.getText().toString().trim();
                                        dialog1.dismiss();
                                        try {
                                            Map<String, String> param = new HashMap<>(3);
                                            param.put("iduser", SharedPref.getInstance().getStringVlue(context, "UserId"));
                                            param.put("screen", "review_pick_up");
                                            RequestHandler.getInstance().stringRequestVolley(context, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(context, "isStaging")) + "popup_detail", param, listener, 8);

                                        } catch (NullPointerException ex) {
                                            ex.printStackTrace();
                                        }
                                    } else {
                                        Utility.showToastMessage(context, "Please write your review...");
                                    }
                                }
                            });

                        } else {
                            if (Utility.isConnectingToInternet(context)) {

                                try {
                                    final Map<String, String> param = new HashMap<String, String>();
                                    param.put("iduser", SharedPref.getInstance().getStringVlue(context, "UserId"));
                                    param.put("idmember", bundle.getString("iduser"));
                                    param.put("idschedule", bundle.getString("idschedule"));
                                    param.put("rating", "" + finalReview_ratingBar.getRating());
                                    if (isbusiness) {
                                        param.put("type", "business");
                                    } else {
                                        param.put("type", "user");
                                    }
                                    if (comment[0] == null) {
                                        param.put("comment", "");
                                    } else {
                                        param.put("comment", comment[0]);
                                    }
                                    //RequestHandler.getInstance().stringRequestVolley(context, Url,param,listener,flag);
                                    finalBtnSubmit1.setEnabled(false);
                                    finalBtnSubmit.setText("Please wait...");
                                    StringRequest req = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.e("RequestHandler String", response);

                                            try {
                                                JSONObject jsonObject = new JSONObject(response);

                                                if (jsonObject != null) {

                                                    if (jsonObject.optString("result").equals("true")) {

                                                        finalBtnSubmit1.setText("Thank You");
                                                        dialog.dismiss();
                                                        try {
                                                            Map<String, String> param = new HashMap<>(3);
                                                            param.put("iduser", SharedPref.getInstance().getStringVlue(context, "UserId"));
                                                            param.put("screen", "rate_your_buddy");
                                                            RequestHandler.getInstance().stringRequestVolley(context, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(context, "isStaging")) + "popup_detail", param, listener, 8);

                                                        } catch (NullPointerException ex) {
                                                            ex.printStackTrace();
                                                        }
                                                    } else {
                                                        Utility.showToastMessage(context, jsonObject.optString("msg"));
                                                        finalBtnSubmit1.setEnabled(true);
                                                    }
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                finalBtnSubmit1.setEnabled(true);
                                            }


                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            finalBtnSubmit1.setEnabled(true);
                                            finalBtnSubmit1.setText("submit");
                                            Utility.showToastMessage(context, "something went wrong!");
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params1 = new HashMap<String, String>();
                                            params1 = param;
                                            Log.e("Param", ":" + params1.toString());
                                            return params1;
                                        }

                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("Content-Type", "application/x-www-form-urlencoded");
                                            return params;
                                        }
                                    };
                                    req.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                    VolleySingleton.getInstance(context).addToRequestQueue(req);

                                } catch (Exception e) {

                                }

                            } else {
                                Utility.showInternetError(context);
                            }
                        }

                    } else {
                        if (Utility.isConnectingToInternet(context)) {

                            try {
                                final Map<String, String> param = new HashMap<String, String>();
                                param.put("iduser", SharedPref.getInstance().getStringVlue(context, "UserId"));
                                param.put("idmember", bundle.getString("iduser"));
                                param.put("idschedule", bundle.getString("idschedule"));
                                param.put("rating", "" + finalReview_ratingBar.getRating());
                                if (isbusiness) {
                                    param.put("type", "business");
                                } else {
                                    param.put("type", "user");
                                }
                                if (comment[0] == null) {
                                    param.put("comment", "");
                                } else {
                                    param.put("comment", comment[0]);
                                }
                                //param.put("comment",  comment[0]);
                                //RequestHandler.getInstance().stringRequestVolley(context, Url,param,listener,flag);
                                finalBtnSubmit1.setEnabled(false);
                                finalBtnSubmit.setText("Please wait...");
                                StringRequest req = new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e("RequestHandler String", response);

                                        try {
                                            JSONObject jsonObject = new JSONObject(response);

                                            if (jsonObject != null) {

                                                if (jsonObject.optString("result").equals("true")) {
                                                    finalBtnSubmit1.setText("Thank You");
                                                    dialog.dismiss();
                                                    try {
                                                        Map<String, String> param = new HashMap<>(3);
                                                        param.put("iduser", SharedPref.getInstance().getStringVlue(context, "UserId"));
                                                        RequestHandler.getInstance().stringRequestVolley(context, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(context, "isStaging")) + "popup_detail", param, listener, 8);

                                                    } catch (NullPointerException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                } else {
                                                    Utility.showToastMessage(context, jsonObject.optString("msg"));
                                                    finalBtnSubmit1.setEnabled(true);
                                                }
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            finalBtnSubmit1.setEnabled(true);
                                        }


                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        finalBtnSubmit1.setEnabled(true);
                                        finalBtnSubmit1.setText("submit");
                                        Utility.showToastMessage(context, "something went wrong!");
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params1 = new HashMap<String, String>();
                                        params1 = param;
                                        Log.e("Param", ":" + params1.toString());
                                        return params1;
                                    }

                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("Content-Type", "application/x-www-form-urlencoded");
                                        return params;
                                    }
                                };
                                req.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                VolleySingleton.getInstance(context).addToRequestQueue(req);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Utility.showInternetError(context);
                        }
                    }

                } else {
                    Utility.showToastMessage(context, "Rate star Atleast one!");
                }

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.setEventTracking(context, "", AppConstants.EVENT_TRACKING_ID_SKIPUSERRATING);
                dialog.dismiss();
            }
        });

        tv_leaveuscomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog1 = new Dialog(context);
                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog1.setContentView(R.layout.comment);

                final CustomEditText customEditText = (CustomEditText) dialog1.findViewById(R.id.comment_text);
                if (comment[0] != null && comment.length > 0) {
                    customEditText.setText("" + comment[0]);
                }
                CustomTextView done = (CustomTextView) dialog1.findViewById(R.id.done);
                dialog1.show();
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (customEditText.length() > 0) {
                            comment[0] = customEditText.getText().toString().trim();
                            dialog1.dismiss();
                        } else {
                            Utility.showToastMessage(context, "Please write your review...");
                        }
                    }
                });
            }
        });

    }

    public static void showUActivBookingDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(UActiveApplication.mContext.getResources().getString(R.string.dialog_booking_title));
        builder.setMessage(UActiveApplication.mContext.getResources().getString(R.string.dialog_msg_booking_info));
// Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static void ShowGenderDialog(Context context, final CustomTextView textView) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.ender_dialog, null);
        builder.setView(view);
        CustomTextView tv_male = (CustomTextView) view.findViewById(R.id.tv_male);
        CustomTextView tv_Female = (CustomTextView) view.findViewById(R.id.tv_female);
        tv_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("Male");
                dialogs.dismiss();
            }
        });

        tv_Female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("Female");
                dialogs.dismiss();
            }
        });

        dialogs = builder.create();
        dialogs.show();

    }

    public static void showLocatoinPicker(final Context context, final boolean ispickup, final String selectedSkill, final Boolean isBookingOpen) {

        final CharSequence[] items = {"Suggested Locations", "Choose from Map", "Cancel"};
        final String second = String.valueOf(System.currentTimeMillis());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (ispickup) {
            builder.setTitle("Add your pick up point");
        } else {
            builder.setTitle("Add your Buddy Up point");
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {

                    case 0:
                        if (ispickup) {
                            Utility.setEventTracking(context, "", AppConstants.EVENT_TRACKING_ID_BOOKINGREQUEST);
                        } else {
                            Utility.setEventTracking(context, "Add your Buddy Up Point", "Suggested Location Add your Buddy Up Point");

                        }

                        Intent intent = new Intent(context, BusinessLocationMap.class);
                        intent.putExtra("isBookingOpen", isBookingOpen);
                        intent.putExtra("selectedSkill", "" + selectedSkill);
                        context.startActivity(intent);
                        break;
                    case 1:
                        if (ispickup) {
                            Utility.setEventTracking(context, "", AppConstants.EVENT_TRACKING_ID_CHOOSE_FROM_MAP);
                        } else {
                            Utility.setEventTracking(context, "Add your Buddy Up Point", "Choose from map Add your Buddy Up Point");
                        }

                        context.startActivity(new Intent(context, PickChoose.class));
                        break;
                    case 2:
                        if (ispickup) {

                        } else {
                            Utility.setEventTracking(context, "Add your Buddy Up Point", " cancel Add your Buddy Up Point");
                        }
                        dialog.dismiss();
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void circleReveal(Activity act, int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View myView = act.findViewById(viewID);

        int width = myView.getWidth();

        if (posFromRight > 0)
            width -= (posFromRight * act.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (act.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= act.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);

        anim.setDuration((long) 220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();


    }

    public static void genralPopup(final Context ctx, int selector, final JSONObject jsonObject, final ResponseListener listener) {
        View View = null;
        Log.d("object", new Gson().toJson(jsonObject));

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (selector) {
            case 0:
                View = inflater.inflate(R.layout.popup_rateus, null);
                RelativeLayout close = (RelativeLayout) View.findViewById(R.id.iv_close);
                CustomButton btn_later = (CustomButton) View.findViewById(R.id.btn_later);
                CustomButton btn_rate = (CustomButton) View.findViewById(R.id.btn_rate);
                final String popup_idTwo = jsonObject.optString("popup_id");
                isShown = true;

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_idTwo);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();

                    }
                });
                btn_later.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  isread = "read";
                        //          SharedPref.getInstance().setSharedValue(ctx, "islater", true);
                        dialog.dismiss();

                    }
                });

                btn_rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            SharedPref.getInstance().setSharedValue(ctx, "islater", true);
                            Map<String, String> param = new HashMap<>(2);
                            param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                            param.put("idpopup", popup_idTwo);
                            RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                            dialog.dismiss();
                            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.uactiv")));

                        } catch (ActivityNotFoundException ex) {
                            ex.printStackTrace();
                        }

                    }
                });
                break;
            case 1:
                View = inflater.inflate(R.layout.popup_pickup_layout, null);
                RelativeLayout btn_close = (RelativeLayout) View.findViewById(R.id.btn_close);

                CircleImageView edit_propic = (CircleImageView) View.findViewById(R.id.edit_propic);
                final RoundRectCornerImageView iv_image = (RoundRectCornerImageView) View.findViewById(R.id.iv_image);
                CustomTextView tv_date = (CustomTextView) View.findViewById(R.id.tv_date);
                CustomTextView tv_time = (CustomTextView) View.findViewById(R.id.tv_time);
                CustomTextView tv_activity = (CustomTextView) View.findViewById(R.id.tv_activity);
                CustomTextView tv_description = (CustomTextView) View.findViewById(R.id.tv_description);
                CustomTextView tv_hostname = (CustomTextView) View.findViewById(R.id.tv_hostname);

                CustomButton btn_click = (CustomButton) View.findViewById(R.id.btn_click);

                String start_time = jsonObject.optString("start_time");


                String end_time = jsonObject.optString("end_time");
                Log.d("gettime", start_time + "--" + end_time);
                String image = jsonObject.optString("image");
                String logo = jsonObject.optString("logo");
                String content = jsonObject.optString("content");
                String activity = jsonObject.optString("activity");
                final String popup_id = jsonObject.optString("popup_id");
                final String idschedule = jsonObject.optString("idschedule");
                String date = Utility.dateFormatWithFull(jsonObject.optString("date"));
                tv_hostname.setText(jsonObject.optString("firstname") + " " + jsonObject.optString("lastname"));
                tv_date.setText(date);


                tv_time.setText(Utility.timeFormatChanage(start_time) + "-" + Utility.timeFormatChanage(end_time));

                tv_activity.setText(activity);
                tv_description.setText(content);
                if (!image.equalsIgnoreCase("")) {
                    UActiveApplication.getInstance().getImageLoader().loadImage(image, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            Log.d("imageload", "hii");
                            iv_image.setImageBitmap(loadedImage);
                            isShown = true;
                            dialog.show();
                        }
                    });
                    // Utility.setImageUniversalLoader(ctx, image, iv_image);


                } else {
                    iv_image.setImageResource(R.drawable.resume);
                }


                Utility.setImageUniversalLoader(ctx, logo, edit_propic);
                btn_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_id);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                        Intent intent = new Intent(ctx, PickUpEventPage.class);
                        intent.putExtra("SheduleId", idschedule);
                        intent.putExtra("frompickupPopup", "frompickupPopup");
                        ctx.startActivity(intent);
                        isread = "read";
                        dialog.dismiss();

                    }
                });

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_id);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();

                    }
                });
                break;
            case 2:
                View = inflater.inflate(R.layout.popup_pickup_one_layout, null);
                RelativeLayout btn_close1 = (RelativeLayout) View.findViewById(R.id.btn_close);
                final RoundRectCornerImageView iv_imageone = (RoundRectCornerImageView) View.findViewById(R.id.iv_image);
                CustomTextView tv_descriptionone = (CustomTextView) View.findViewById(R.id.tv_description);
                CustomButton btn_clickone = (CustomButton) View.findViewById(R.id.btn_click);

                String contentOne = jsonObject.optString("content");
                imageOne = jsonObject.optString("image");
                final String hyperlink = jsonObject.optString("hyperlink");
                popup_idOne = jsonObject.optString("popup_id");

                if (!imageOne.equalsIgnoreCase("")) {

                    //  Utility.setImageUniversalLoader(ctx, imageOne, iv_imageone);
                    UActiveApplication.getInstance().getImageLoader().loadImage(imageOne, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            Log.d("imageloadv", "hii");
                            iv_imageone.setImageBitmap(loadedImage);
                            isShown = true;
                            dialog.show();
                        }
                    });

                } else {
                    iv_imageone.setImageResource(R.drawable.resume);
                }


                tv_descriptionone.setText(contentOne);

                btn_close1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_idOne);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();

                    }
                });

                btn_clickone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_idOne);

                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);
                        Intent url = new Intent(ctx, In_App_Browser.class);
                        url.putExtra("url", hyperlink);
                        dialog.dismiss();
                        ctx.startActivity(url);

                    }
                });

                break;
            case 3:
                View = inflater.inflate(R.layout.popup_pickup_two_layout, null);
                RelativeLayout btn_close2 = (RelativeLayout) View.findViewById(R.id.btn_close);
                final RoundRectCornerImageView iv_imageones = (RoundRectCornerImageView) View.findViewById(R.id.iv_imagelo);
                popup_idOne = jsonObject.optString("popup_id");
                imageOne = jsonObject.optString("image");
                if (!imageOne.equalsIgnoreCase("")) {
                    //Utility.setImageUniversalLoader(ctx, imageOne, iv_imageones);
                    UActiveApplication.getInstance().getImageLoader().loadImage(imageOne, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            isShown = true;
                            iv_imageones.setImageBitmap(loadedImage);
                            dialog.show();
                        }
                    });
                    iv_imageones.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            isread = "read";
                            Map<String, String> param = new HashMap<>(2);
                            param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                            param.put("idpopup", popup_idOne);

                            RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);
                            Intent url = new Intent(ctx, In_App_Browser.class);
                            url.putExtra("url", jsonObject.optString("hyperlink"));
                            dialog.dismiss();
                            ctx.startActivity(url);
                        }
                    });

                } else {
                    iv_imageones.setImageResource(R.drawable.resume);
                }


                btn_close2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_idOne);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();
                    }
                });

                break;
        }


        builder.setView(View);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (selector != 0 || selector == 3) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }


        if (isShown == true) {
            dialog.show();
        }

    }

    public static void genralPopupEventRate(Context ctx, int selector, JSONObject jsonObject, final ResponseListener listener, final Activity act, final String pickup) {
        View View = null;
        Log.d("object", new Gson().toJson(jsonObject));
        myctx = ctx;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(myctx);
        LayoutInflater inflater = (LayoutInflater) myctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (selector) {
            case 0:
                View = inflater.inflate(R.layout.popup_rateus, null);
                isShown_one = true;
                RelativeLayout close = (RelativeLayout) View.findViewById(R.id.iv_close);
                CustomButton btn_later = (CustomButton) View.findViewById(R.id.btn_later);
                CustomButton btn_rate = (CustomButton) View.findViewById(R.id.btn_rate);
                final String popup_idTwo = jsonObject.optString("popup_id");

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        PickUpEventPage.pickupjoin = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, "UserId"));
                        param.put("idpopup", popup_idTwo);
                        RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(myctx, "true")) + "updatepopup", param, listener, 7);


                        dialog.dismiss();

                        // onBackPressed();


                    }
                });
                btn_later.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  isread = "read";
                        // SharedPref.getInstance().setSharedValue(myctx, "islater", true);
                        dialog.dismiss();

                        // onBackPressed();


                    }
                });

                btn_rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            SharedPref.getInstance().setSharedValue(myctx, "islater", true);
                            Map<String, String> param = new HashMap<>(2);
                            param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, "UserId"));
                            param.put("idpopup", popup_idTwo);
                            RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(myctx, "true")) + "updatepopup", param, listener, 7);

                            dialog.dismiss();
                            myctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.uactiv")));

                            // onBackPressed();


                        } catch (ActivityNotFoundException ex) {
                            ex.printStackTrace();
                        }

                    }
                });
                break;
            case 1:
                View = inflater.inflate(R.layout.popup_pickup_layout, null);
                RelativeLayout btn_close = (RelativeLayout) View.findViewById(R.id.btn_close);

                CircleImageView edit_propic = (CircleImageView) View.findViewById(R.id.edit_propic);
                final RoundRectCornerImageView iv_image = (RoundRectCornerImageView) View.findViewById(R.id.iv_image);
                CustomTextView tv_date = (CustomTextView) View.findViewById(R.id.tv_date);
                CustomTextView tv_time = (CustomTextView) View.findViewById(R.id.tv_time);
                CustomTextView tv_activity = (CustomTextView) View.findViewById(R.id.tv_activity);
                CustomTextView tv_description = (CustomTextView) View.findViewById(R.id.tv_description);
                CustomTextView tv_hostname = (CustomTextView) View.findViewById(R.id.tv_hostname);

                CustomButton btn_click = (CustomButton) View.findViewById(R.id.btn_click);

                String start_time = jsonObject.optString("start_time");


                String end_time = jsonObject.optString("end_time");
                String image = jsonObject.optString("image");
                String logo = jsonObject.optString("logo");
                String content = jsonObject.optString("content");
                String activity = jsonObject.optString("activity");
                final String popup_id = jsonObject.optString("popup_id");
                final String idschedule = jsonObject.optString("idschedule");
                String date = Utility.dateFormatWithFull(jsonObject.optString("date"));
                tv_hostname.setText(jsonObject.optString("firstname") + " " + jsonObject.optString("lastname"));
                tv_date.setText(date);
                tv_time.setText(Utility.timeFormatChanage(start_time) + "-" + Utility.timeFormatChanage(end_time));
                tv_activity.setText(activity);
                tv_description.setText(content);
                if (!image.equalsIgnoreCase("")) {
                    Utility.setImageUniversalLoader(ctx, image, iv_image);
                    UActiveApplication.getInstance().getImageLoader().loadImage(image, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            Log.d("imageload", "hii");
                            iv_image.setImageBitmap(loadedImage);
                            isShown_one = true;
                            dialog.show();
                        }
                    });
                } else {
                    iv_image.setImageResource(R.drawable.resume);
                }

                Utility.setImageUniversalLoader(ctx, logo, edit_propic);
                btn_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, "UserId"));
                        param.put("idpopup", popup_id);
                        RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(myctx, "true")) + "updatepopup", param, listener, 7);

                        Intent intent = new Intent(myctx, PickUpEventPage.class);
                        intent.putExtra("SheduleId", idschedule);
                        intent.putExtra("frompickupPopup", "frompickupPopup");
                        myctx.startActivity(intent);
                        isread = "read";
                        PickUpEventPage.pickupjoin = "read";
                        dialog.dismiss();

                        // onBackPressed();


                    }
                });

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        PickUpEventPage.pickupjoin = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, "UserId"));
                        param.put("idpopup", popup_id);
                        RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(myctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();

                        // onBackPressed();


                    }
                });
                break;
            case 2:
                View = inflater.inflate(R.layout.popup_pickup_one_layout, null);
                RelativeLayout btn_close1 = (RelativeLayout) View.findViewById(R.id.btn_close);
                final RoundRectCornerImageView iv_imageone = (RoundRectCornerImageView) View.findViewById(R.id.iv_image);
                CustomTextView tv_descriptionone = (CustomTextView) View.findViewById(R.id.tv_description);
                CustomButton btn_clickone = (CustomButton) View.findViewById(R.id.btn_click);

                String contentOne = jsonObject.optString("content");
                imageOne = jsonObject.optString("image");
                final String hyperlink = jsonObject.optString("hyperlink");
                popup_idOne = jsonObject.optString("popup_id");

                if (!imageOne.equalsIgnoreCase("")) {
                    UActiveApplication.getInstance().getImageLoader().loadImage(imageOne, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            Log.d("imageloadv", "hii");
                            iv_imageone.setImageBitmap(loadedImage);
                            isShown_one = true;
                            dialog.show();
                        }
                    });

                } else {
                    iv_imageone.setImageResource(R.drawable.resume);
                }
                tv_descriptionone.setText(contentOne);

                btn_close1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        PickUpEventPage.pickupjoin = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, "UserId"));
                        param.put("idpopup", popup_idOne);
                        RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(myctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();

                        // onBackPressed();


                    }
                });

                btn_clickone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        PickUpEventPage.pickupjoin = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, "UserId"));
                        param.put("idpopup", popup_idOne);

                        RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(myctx, "true")) + "updatepopup", param, listener, 7);
                        Intent url = new Intent(myctx, In_App_Browser.class);
                        url.putExtra("url", hyperlink);
                        dialog.dismiss();
                        myctx.startActivity(url);
                        // onBackPressed();


                    }
                });

                break;
            case 3:
                View = inflater.inflate(R.layout.popup_pickup_two_layout, null);
                RelativeLayout btn_close2 = (RelativeLayout) View.findViewById(R.id.btn_close);
                popup_idOne = jsonObject.optString("popup_id");
                final RoundRectCornerImageView iv_imageTwo = (RoundRectCornerImageView) View.findViewById(R.id.iv_image);
                imageOne = jsonObject.optString("image");

                if (!imageOne.equalsIgnoreCase("")) {
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                iv_imageTwo.setImageBitmap(getBitmapFromURL(imageOne));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();

                    // Utility.setImageUniversalLoader(View.getContext(), imageOne, iv_imageTwo);
                } else {
                    //       iv_imageTwo.setImageResource(R.drawable.resume);
                }


                btn_close2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        PickUpEventPage.pickupjoin = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(myctx, "UserId"));
                        param.put("idpopup", popup_idOne);
                        RequestHandler.getInstance().stringRequestVolley(myctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(myctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();

                        // onBackPressed();

                    }
                });

                break;
        }


        builder.setView(View);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (selector != 0 || selector == 3) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }


        if (isShown_one == true) {
            dialog.show();
        }


    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }

    public static void genralPopupEvent(final Context ctx, int selector, final JSONObject jsonObject, final ResponseListener listener, final Activity act, final String pickup) {
        View View = null;
        Log.d("object", new Gson().toJson(jsonObject));

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (selector) {
            case 0:
                View = inflater.inflate(R.layout.popup_rateus, null);
                isShown_two = true;
                RelativeLayout close = (RelativeLayout) View.findViewById(R.id.iv_close);
                CustomButton btn_later = (CustomButton) View.findViewById(R.id.btn_later);
                CustomButton btn_rate = (CustomButton) View.findViewById(R.id.btn_rate);
                final String popup_idTwo = jsonObject.optString("popup_id");

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        CreatePickUp.iscreateread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_idTwo);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);


                        dialog.dismiss();
                        act.finish();
                        // onBackPressed();
                        if (pickup.equalsIgnoreCase("pickup")) {
                            try {
                                Notification_Fragment.inter.refresh();
                                PickUpGuest.pickupguest.finish();
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }

                        } else {
                            if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                                BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                            }
                        }


                    }
                });
                btn_later.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // isread = "read";
                        //    SharedPref.getInstance().setSharedValue(ctx, "islater", true);
                        dialog.dismiss();
                        act.finish();
                        // onBackPressed();
                        if (pickup.equalsIgnoreCase("pickup")) {
                            try {
                                Notification_Fragment.inter.refresh();
                                PickUpGuest.pickupguest.finish();
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }

                        } else if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                            BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                        }


                    }
                });

                btn_rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            SharedPref.getInstance().setSharedValue(ctx, "islater", true);
                            Map<String, String> param = new HashMap<>(2);
                            param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                            param.put("idpopup", popup_idTwo);
                            RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                            dialog.dismiss();
                            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.uactiv")));
                            act.finish();
                            // onBackPressed();
                            if (pickup.equalsIgnoreCase("pickup")) {
                                try {
                                    Notification_Fragment.inter.refresh();
                                    PickUpGuest.pickupguest.finish();
                                } catch (NullPointerException ex) {
                                    ex.printStackTrace();
                                }

                            } else if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                                BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                            }

                        } catch (ActivityNotFoundException ex) {
                            ex.printStackTrace();
                        }

                    }
                });
                break;
            case 1:
                View = inflater.inflate(R.layout.popup_pickup_layout, null);
                RelativeLayout btn_close = (RelativeLayout) View.findViewById(R.id.btn_close);

                CircleImageView edit_propic = (CircleImageView) View.findViewById(R.id.edit_propic);
                final RoundRectCornerImageView iv_image = (RoundRectCornerImageView) View.findViewById(R.id.iv_image);
                CustomTextView tv_date = (CustomTextView) View.findViewById(R.id.tv_date);
                CustomTextView tv_time = (CustomTextView) View.findViewById(R.id.tv_time);
                CustomTextView tv_activity = (CustomTextView) View.findViewById(R.id.tv_activity);
                CustomTextView tv_description = (CustomTextView) View.findViewById(R.id.tv_description);
                CustomTextView tv_hostname = (CustomTextView) View.findViewById(R.id.tv_hostname);

                CustomButton btn_click = (CustomButton) View.findViewById(R.id.btn_click);

                String start_time = jsonObject.optString("start_time");


                String end_time = jsonObject.optString("end_time").substring(0, jsonObject.optString("end_time").length() - 3);
                String image = jsonObject.optString("image");
                String logo = jsonObject.optString("logo");
                String content = jsonObject.optString("content");
                String activity = jsonObject.optString("activity");
                final String popup_id = jsonObject.optString("popup_id");
                final String idschedule = jsonObject.optString("idschedule");
                String date = Utility.dateFormatWithFull(jsonObject.optString("date"));
                tv_hostname.setText(jsonObject.optString("firstname") + " " + jsonObject.optString("lastname"));
                tv_date.setText(date);
                tv_time.setText(start_time + "AM" + "-" + end_time + "AM");
                tv_activity.setText(activity);
                tv_description.setText(content);
                // Utility.setImageUniversalLoader(ctx, image, iv_image);
                Utility.setImageUniversalLoader(ctx, logo, edit_propic);
                UActiveApplication.getInstance().getImageLoader().loadImage(image, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Log.d("imageload", "hii");
                        iv_image.setImageBitmap(loadedImage);
                        isShown_two = true;
                        dialog.show();
                    }
                });
                btn_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_id);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                        Intent intent = new Intent(ctx, PickUpEventPage.class);
                        intent.putExtra("SheduleId", idschedule);
                        intent.putExtra("frompickupPopup", "frompickupPopup");
                        ctx.startActivity(intent);
                        isread = "read";
                        dialog.dismiss();
                        act.finish();
                        // onBackPressed();
                        if (pickup.equalsIgnoreCase("pickup")) {
                            try {
                                Notification_Fragment.inter.refresh();
                                PickUpGuest.pickupguest.finish();
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }

                        } else if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                            BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                        }

                    }
                });

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        CreatePickUp.iscreateread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_id);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();
                        act.finish();
                        // onBackPressed();
                        if (pickup.equalsIgnoreCase("pickup")) {
                            try {
                                Notification_Fragment.inter.refresh();
                                PickUpGuest.pickupguest.finish();
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }

                        } else if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                            BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                        }

                    }
                });
                break;
            case 2:
                View = inflater.inflate(R.layout.popup_pickup_one_layout, null);
                RelativeLayout btn_close1 = (RelativeLayout) View.findViewById(R.id.btn_close);
                final RoundRectCornerImageView iv_imageone = (RoundRectCornerImageView) View.findViewById(R.id.iv_image);
                CustomTextView tv_descriptionone = (CustomTextView) View.findViewById(R.id.tv_description);
                CustomButton btn_clickone = (CustomButton) View.findViewById(R.id.btn_click);

                String contentOne = jsonObject.optString("content");
                imageOne = jsonObject.optString("image");
                final String hyperlink = jsonObject.optString("hyperlink");
                popup_idOne = jsonObject.optString("popup_id");

                Utility.setImageUniversalLoader(ctx, imageOne, iv_imageone);
                tv_descriptionone.setText(contentOne);
                UActiveApplication.getInstance().getImageLoader().loadImage(imageOne, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Log.d("imageload", "hii");
                        iv_imageone.setImageBitmap(loadedImage);
                        isShown_two = true;
                        dialog.show();
                    }
                });


                btn_close1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        CreatePickUp.iscreateread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_idOne);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();
                        act.finish();
                        // onBackPressed();
                        if (pickup.equalsIgnoreCase("pickup")) {
                            try {
                                Notification_Fragment.inter.refresh();
                                PickUpGuest.pickupguest.finish();
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }

                        } else if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                            BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                        }

                    }
                });

                btn_clickone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        CreatePickUp.iscreateread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_idOne);

                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);
                        Intent url = new Intent(ctx, In_App_Browser.class);
                        url.putExtra("url", hyperlink);
                        dialog.dismiss();
                        ctx.startActivity(url);
                        act.finish();
                        // onBackPressed();
                        if (pickup.equalsIgnoreCase("pickup")) {
                            try {
                                Notification_Fragment.inter.refresh();
                                PickUpGuest.pickupguest.finish();
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }

                        } else if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                            BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                        }

                    }
                });

                break;
            case 3:
                View = inflater.inflate(R.layout.popup_pickup_two_layout, null);
                RelativeLayout btn_close2 = (RelativeLayout) View.findViewById(R.id.btn_close);
                popup_idOne = jsonObject.optString("popup_id");
                final RoundRectCornerImageView iv_imageTwo = (RoundRectCornerImageView) View.findViewById(R.id.iv_image);
                imageOne = jsonObject.optString("image");
                try {
                    UActiveApplication.getInstance().getImageLoader().loadImage(imageOne, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            Log.d("imageload", "hii");
                            iv_imageTwo.setImageBitmap(loadedImage);
                            isShown_two = true;
                            dialog.show();
                        }
                    });
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }

                iv_imageTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent url = new Intent(ctx, In_App_Browser.class);
                        url.putExtra("url", jsonObject.optString("hyperlink"));
                        dialog.dismiss();
                        ctx.startActivity(url);
                    }
                });
                btn_close2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isread = "read";
                        CreatePickUp.iscreateread = "read";
                        Map<String, String> param = new HashMap<>(2);
                        param.put("iduser", SharedPref.getInstance().getStringVlue(ctx, "UserId"));
                        param.put("idpopup", popup_idOne);
                        RequestHandler.getInstance().stringRequestVolley(ctx, AppConstants.getBaseUrlMarketing(SharedPref.getInstance().getBooleanValue(ctx, "true")) + "updatepopup", param, listener, 7);

                        dialog.dismiss();
                        act.finish();
                        // onBackPressed();
                        if (pickup.equalsIgnoreCase("pickup")) {
                            try {
                                Notification_Fragment.inter.refresh();
                                PickUpGuest.pickupguest.finish();
                            } catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }

                        } else if (BuddyUpDetailsActivity.buddyupDetailsActivity != null) {
                            BuddyUpDetailsActivity.buddyupDetailsActivity.finish();
                        }
                    }
                });

                break;
        }


        builder.setView(View);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (selector != 0 || selector == 3) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (isShown_two == true) {
            dialog.show();
        }


    }

    public static String getiamgebaseurl() {
      //  return "http://54.254.173.51/vijays/resources/uploads/";
         return "https://www.uactiv.com/upgrade/resources/uploads/";
    }

    public static BusinessLocationMap getInstance() {
        return businessLocationMap;
    }

    /***/
    public static String getDomain(boolean isStaging) {
        isStaging = false;
        Log.e("isStaging = ", "" + SharedPref.getInstance().getBooleanValue(UActiveApplication.getInstance().getApplicationContext(), SharedConstants.isStaging));
        //BASE_URL = "http://www.uactiv.com/";
        String base_url = "";

        if (isStaging)
            base_url = "http://54.254.173.51/vijays/api/v1.0.0/"; // Base Url development.
            //base_url = "http://52.77.232.206/"; // Base Url for newInstance development.
            //989252583
        else
            //base_url= "http://52.77.231.95/"; // live version for testing
            base_url = "https://www.uactiv.com/upgrade/api/v1.0.0/";// live version
        return base_url;
    }


    /***/
    public static String getBaseUrl(boolean isStaging) {
        isStaging = false;
        if (isStaging) {
            //return getDomain(isStaging) + "app/index.php/user/";  // development Ramesh 21th Marc 17
            return getDomain(isStaging) + "user/";
            //return getDomain(isStaging) + "app_1_1/index.php/user/";
            //return getDomain(isStaging) + "app_1_1/index.php/user/";
        } else {
            return getDomain(isStaging) + "user/";
        }
    }

    public static String getBaseUrlFacebook(boolean isStaging) {
        isStaging = false;
        if (isStaging) {
            return getDomain(isStaging) + "user/FacebookUser/";  // development
            // return getDomain(isStaging) + "app/index.php/FacebookUser/";  // development
            //return getDomain(isStaging) + "app_1_1/index.php/user/";
        } else {
            return getDomain(isStaging) + "index.php/FacebookUser/";
        }
    }

    public static String getBaseUrlMarketing(boolean isStaging) {
        isStaging = false;
        if (isStaging) {
            return getDomain(isStaging) + "app_uat/index.php/Marketing/";  // development
            // return getDomain(isStaging) + "app/index.php/FacebookUser/";  // development
            //return getDomain(isStaging) + "app_1_1/index.php/user/";
        } else {
            return getDomain(isStaging) + "index.php/Marketing/";
        }
    }

    public static String getBaseImgUrl(boolean isStaging) {
        return getDomain(isStaging);
    }

    public interface urlConstants {
        //String baseUrl = "http://doodlebluestaging.com/uactive/index.php/user/";
        //String domainName = "http://www.uactiv.co/"; // Base Url development.
        //String domainName = "http://www.uactiv.com/"; // Production
        //String domainName = getDomain(SharedPref.getInstance().getBooleanValue(SharedConstants.isStaging));
        //String baseUrl = domainName + "app/index.php/user/";
        /* String baseUrl = "http://54.254.202.191/app/index.php/user/";*/

        String resultcheck = "result";
        String registration = "registration";
        String login = "login";
        String forgotpassword = "forgotpassword";
        String checkfacebookid = "checkfacebookid";
        String checkEmail = "checkemail";
        String buddyuplist = "buddyuplist";
        String facebooklogin = "facebooklogin";
        String editprofile = "editprofile";
        String creategroup = "creategroup";
        String grouplist = "grouplist";
        String favouriteslist = "favouriteslist";
        String groupmemeberlist = "groupmemeberlist";
        String addtogroup = "addtogroup";
        String near_by_search = "get_nearby_search_buddy";
        String removefromgroup = "removefromgroup";
        String addfavourites = "addfavourites";
        String createpickup = "createpickup";
        String pickuplist = "pickuplist";
        String updatesetting = "updatesetting";
        String changePassword = "changepassword";
        String removegroup = "removegroup";
        String removefavourites = "removefavourites";
        String buddyuprequest = "buddyuprequest";
        String updatelocation = "updatelocation";
        String joinpickup = "joinpickup";
        String schedulelist = "schedulelist";
        String pickupdetail = "pickup_detail";
        String acceptinvites = "acceptinvites";
        String declineinvites = "declineinvites";
        String notification = "notification";
        String acceptpickuprequest = "accept_pickup_request";
        String declinepickuprequest = "decline_pickuprequest";
        String checkrating = "checkrating";
        String rating = "rating";
        String reportuser = "reportuser";
        String cancelpickup = "cancel_pickup";
        String cancelbuddyup = "cancel_buddyup";
        String abandonbuddyup = "abandon_buddyup";
        String abandonpickup = "abandon_pickup";
        String cancelpickuprequest = "cancel_request";
        String editschedule = "editschedule";
        String getactivityList = "activity";
        String buddysearch = "search";
        String pickupsearch = "pickupsearch";
        String editgroup = "editgroup";
        String userinfo = "userinfo";
        String logout = "logout";
        String latitude = "latitude";
        String longitude = "longitude";
        String updateDeviceId = "device";
        String deleteNotification = "delete_Notification";
        String sendInvitation = "sent_invitation";
        String getSuggestedLocations = "business_location";
        String facebookUser = "getuser";
        String chat = "chat";
        String savechat = "save_chat";
        String like_feed = "like_feed";
        //KeyValues
        String KEY_MSG = "msg";
        String KEY_TRUE = "true";
        String KEY_FALSE = "false";
        String KEY_NEW_USER = "true";
        String KEY_EXSIT_USER = "false";
        String KEY_DETAIL = "details";
        String serialKeyBuddy = "buddy";
        String serialKeycreateBuddy = "createBuddy";
        String KEY_BUDDY_UP = "buddyup";
        String KEY_PICK_UP = "pickup";
        String KEY_INVITED = "invited";
        String KEY_ACCEPTED = "accepted";
        //String KEY_PICKUP_COMPLETED = "created";
        String KEY_REJECTED = "rejected";
        String KEY_REQESTED = "requested";
        String KEY_ABANDON = "abandoned";
        String KEY_CANCEL = "cancelled";
        String KEY_COMPLETED = "completed";
        String KEY_ACTIVE = "active";
        String KEY_IN_ACTIVE = "inactive";
        String KEY_CREATED = "created";
        String KEY_CHAT = "chat";
        String KEY_CHANGED = "changed";
        String KEY_UPCOMING = "upcoming";
        String KEY_PAST = "past";
        String KEY_IS_DUPLICATE = "duplicate";
        String KEY_CREATE_SCHEDULE = "create_schedule";
        String KEY_ID_SCHEDULE = "schedule_id";
        int USER_TYPE_BUSINESS = 1;
        int USER_TYPE_APP = 0;

        String KEY_JSON_ARRAY_ATTENDEE = "attending";

        String validCity = "validCity";
        String getInvitePeople = "getInvitePeople";
        String getMutualFriend = "fbmutual_friend";
        String getnearby = "getnearbyPeople";
        String getgroups = "grouplist";
        String getfev = "favouriteslist";
        //String getMutualFriend="getThirddegreeFriend";

    }

    public static boolean isGestLogin(Context ctx) {
        boolean val = false;
        if (SharedPref.getInstance().getBooleanValue(ctx, "GEST_LOGIN") != null) {
            val = SharedPref.getInstance().getBooleanValue(ctx, "GEST_LOGIN");
        }
        return val;
    }

    public static void loginDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.popup_guest, null);
        ImageView btn_close = (ImageView) view.findViewById(R.id.btn_close);
        CustomButton btn_login = (CustomButton) view.findViewById(R.id.btn_login);
        CustomButton btn_signin = (CustomButton) view.findViewById(R.id.btn_signin);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogs.dismiss();
                Utility.setEventTracking(context, "Guest login popup screen", "Guest login popup close button.");
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.setEventTracking(context, "Guest login popup screen", "Guest login popup login button");
                Intent login = new Intent(context, Sign_In.class);
                login.putExtra("fromLogin", "login");
                context.startActivity(login);
            }
        });

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.setEventTracking(context, "Guest login popup screen", "Guest login popup sign In button");
                Intent login = new Intent(context, Sign_In.class);
                login.putExtra("fromLogin", "Sign_In");
                context.startActivity(login);
            }
        });

        builder.setView(view);
        dialogs = builder.create();
        dialogs.setCancelable(true);
        dialogs.show();
    }

    public static String getGender(Context ctx) {
        String mygender = "";
        if (SharedPref.getInstance().getStringVlue(ctx, "GEST_GENDER") != null) {
            mygender = SharedPref.getInstance().getStringVlue(ctx, "GEST_GENDER");
        }
        return mygender;
    }

    public static int getSession(Context ctx) {
        int session = 0;
        if (SharedPref.getInstance().getIntVlue(ctx, "APP_SESSION") > 0) {
            session = SharedPref.getInstance().getIntVlue(ctx, "APP_SESSION");
        }
        return session;
    }

    public static void setSession(Context ctx) {
        int session = 0;
        session++;
        SharedPref.getInstance().setSharedValue(ctx, "APP_SESSION", session);

    }

    public static String getRadius(Context ctx) {
        String radius = "";
        if (SharedPref.getInstance().getStringVlue(ctx, "GEST_RADIUS") != null) {
            radius = SharedPref.getInstance().getStringVlue(ctx, "GEST_RADIUS");
        }
        return radius;
    }

    public static String getCityByLocation(Context ctx, Double lati, Double longi) throws IOException {
        String mycity = "";
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lati, longi, 1);
        mycity = addresses.get(0).getLocality();
        return mycity;
    }

    public interface SharedConstants {
        String preferenceName = "UActive";

        //Basic userinfo

        String userId = "UserId";
        String firstname = "Firstname";
        String lastname = "Lastname";
        String email = "Email";
        String password = "Password";
        String age = "Age";
        String gender = "Gender";
        String image = "Profileimage";
        String about_yourself = "About_yourself";
        String badge = "Badge";
        String count = "Count";
        String facebook_link = "facebook_link";
        String facebookid = "facebookid";
        String radius_limit = "Radius_limit";
        String gender_pref = "Gender_pref";
        String isreceive_request = "isreceive_request";
        String isreceive_notification = "isreceive_notification";
        String status = "Status";
        String device_id = "device_id";
        String skills = "Skills";
        String skillss = "skills";
        String islogin = "islogin";
        String activity = "activity";
        String modekey = "mode";
        String invitedata = "invitedata";
        String invitegroupdata = "invitegroupdata";
        String invitecount = "invitecount";
        String referalcode = "referalcode";
        String gcm_token = "GCM_token";
        String USER_TYPE = "usertype";
        String isbussiness = "isbussiness";
        String user_rating = "rating";
        String user_rating_count = "rated_count";
        String business_locations = "business_locations";
        String address = "address";
        String phoneno = "phoneno";
        String landline = "landline";
        String businessName = "business_name";
        String businessType = "businessType";
        String Api_skill_list = "Api_skill_list";
        String notification_count = "notification_count";
        String schedule_count = "schedule_count";
        String isStartUpExpired = "startupScreens";
        String getGropId = "getGropId";
        String updateGroupId = "update_groupid";
        String getfeedlist = "getfeedlist";

        int requestCodes = 1;

        String FACEBOOK_FRIENDS_LIST = "friendsList";
        String isStaging = "isStaging";
        String PREF_IS_CHAT_LOGIN = "ischatlogin";
        String PREF_GEST_LOGIN = "GEST_LOGIN";
        String PREF_GEST_GENDER = "GEST_GENDER";
        String PREF_GEST_GEST_RADIUS = "GEST_RADIUS";
    }


}