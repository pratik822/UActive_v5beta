package com.uactiv.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicommons.json.GsonUtils;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.gson.Gson;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.uactiv.R;
import com.uactiv.adapter.NotificationHeaderAdapter;
import com.uactiv.applozicchat.ApplozicChat;
import com.uactiv.controller.ItemDelete;
import com.uactiv.controller.ResponseListener;
import com.uactiv.controller.StatusChangedListener;
import com.uactiv.model.NotifyModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.utils.myinter;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notification_Fragment extends Fragment implements AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener, StatusChangedListener, SwipeRefreshLayout.OnRefreshListener, ItemDelete, myinter {

    public static StatusChangedListener statusChangedListener = null;
    private RecyclerView mRecyclerViewNotificationList;
    private LinearLayoutManager manager = null;
    private NotificationHeaderAdapter mAdapter;
    private ArrayList<NotifyModel> items = new ArrayList<>();
    private GifImageView progressWheel = null;
    private boolean isResumeRefresh = false;
    private boolean isRefresh = false; //refer pull to refresh or not
    private SwipeRefreshLayout refreshLayout = null;
    private LinearLayout mEmptyViewNotification = null;
    private RelativeLayout mEmptyViewNoInternetConnection = null;
    private CustomTextView delete_past_notifications, cancel_delete;
    private CustomTextView btn_past;
    private LinearLayout delete_container;
    public AppContactService appContactService;
    protected MessageDatabaseService mMessageDatabaseService;
    private final int REQUEST_CODE_GET_NOTIFICATION_LIST = 0;
    private final int REQUEST_CODE_GET_PAST_NOTIFICATION_LIST = 2;
    private String TAG = getClass().getSimpleName();
    private ApplozicChat mApplozicChatHandler;
    public static myinter inter;

    public Notification_Fragment() {
        // Required empty public constructor
    }

    public void onDelete(boolean canDelete) {
        delete_container.setVisibility(View.GONE);
        if (canDelete) {
            delete_container.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageDatabaseService = new MessageDatabaseService(getActivity());
        inter = (myinter) this;


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_fragment, container, false);
        Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_NOTIFICATION);
        mApplozicChatHandler = new ApplozicChat(getActivity());
        appContactService = new AppContactService(getActivity());


        if (SharedPref.getInstance().getBooleanValue(getActivity(), isbussiness)) {
            ((TextView) rootView.findViewById(R.id.tv_empty_state_text)).setText(R.string.empty_view_msg_notifications_business_user);
        }

        mEmptyViewNotification = (LinearLayout) rootView.findViewById(R.id.empty_view_notification);
        mEmptyViewNoInternetConnection = (RelativeLayout) rootView.findViewById(R.id.empty_view_no_internet);
        statusChangedListener = this;
        btn_past = (CustomTextView) rootView.findViewById(R.id.button6);
        btn_past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pastnotifiationfragment fragment = new Pastnotifiationfragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
        mRecyclerViewNotificationList = (RecyclerView) rootView.findViewById(R.id.notifyListView);
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewNotificationList.setLayoutManager(manager);
        progressWheel = (GifImageView) rootView.findViewById(R.id.gifLoader);
        Utility.showProgressDialog(getActivity(), progressWheel);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.notificationRefresh);
        refreshLayout.setOnRefreshListener(this);

        delete_container = (LinearLayout) rootView.findViewById(R.id.delete_container);
        delete_container.setVisibility(View.GONE);

        delete_past_notifications = (CustomTextView) rootView.findViewById(R.id.delete_past_notifications);
        delete_past_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    showMenuAlert("", "Are you sure you want to delete?");
                }
            }
        });
        //  getPastNotficationLsitFromAPI();
        cancel_delete = (CustomTextView) rootView.findViewById(R.id.cancel_delete);
        cancel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_container.setVisibility(View.GONE);
                //setNotificationAdapter();
                if (mAdapter != null) {
                    mAdapter.removeDelete();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        isRefresh = false;
        isResumeRefresh = false;
        getNotficationLsitFromAPI();

        registerNewMessageReceiver();
        // Inflate the layout for this fragment
        return rootView;
    }


    /**
     * register receiver to get new messages
     */
    private void registerNewMessageReceiver() {
        IntentFilter intentFilter = new IntentFilter(AppConstants.ACTION_NEW_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNewMessageReceived, intentFilter);
    }


    private void unregisterNewMessageReceiver() {
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mNewMessageReceived);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showMenuAlert(String title, String message) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // On pressing Settings button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "yes its from notificationFragment");
                onDeleteNotificationsAPI(mAdapter.getIdsToDelete());
                delete_container.setVisibility(View.GONE);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //delete_container.setVisibility(View.GONE);
                return;
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void onDeleteNotificationsAPI(String idschedule) {
        if (Utility.isConnectingToInternet(getActivity())) {
            try {
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                param.put("idschedule", idschedule);
                showwheel();
                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + deleteNotification, param, this, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setNotificationAdapter() {
        mAdapter = new NotificationHeaderAdapter(getActivity(), items, this, this);
        mRecyclerViewNotificationList.setAdapter(mAdapter);
    /*    final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerViewNotificationList.addItemDecoration(headersDecor);*/
    }

    private void getNotficationLsitFromAPI() {
        if (Utility.isConnectingToInternet(getActivity())) {
            Map<String, String> params = new HashMap<>();
            params.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
            if (!isRefresh) {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
            }
            mEmptyViewNoInternetConnection.setVisibility(View.GONE);
            mEmptyViewNotification.setVisibility(View.GONE);
            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + notification, params, this, REQUEST_CODE_GET_NOTIFICATION_LIST);
        } else {
            items = new ArrayList<>();
            setNotificationAdapter();
            mEmptyViewNoInternetConnection.setVisibility(View.VISIBLE);
            mEmptyViewNotification.setVisibility(View.GONE);
            // Utility.showInternetError(getActivity());
        }
    }


    private void getPastNotficationLsitFromAPI() {
        if (Utility.isConnectingToInternet(getActivity())) {
            Map<String, String> params = new HashMap<>();
            params.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
            params.put("page", "1");

            if (!isRefresh) {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
            }
            mEmptyViewNoInternetConnection.setVisibility(View.GONE);
            mEmptyViewNotification.setVisibility(View.GONE);
            RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + "past_notification", params, this, REQUEST_CODE_GET_PAST_NOTIFICATION_LIST);
        } else {
            items = new ArrayList<>();
            setNotificationAdapter();
            mEmptyViewNoInternetConnection.setVisibility(View.VISIBLE);
            mEmptyViewNotification.setVisibility(View.GONE);
            // Utility.showInternetError(getActivity());
        }
    }


    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {
        /** flag == 0 notification API reponse
         */
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(successResponse);
        } catch (Exception e) {
            e.printStackTrace();
            progressWheel.stopAnimation();
            progressWheel.setVisibility(View.GONE);
        }

        Log.d(TAG, "successResponse : " + flag);
        switch (flag) {
            case REQUEST_CODE_GET_NOTIFICATION_LIST:
                Log.d(TAG, "jsonObject  : " + jsonObject);
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        JSONArray getNotification = jsonObject.optJSONArray(KEY_UPCOMING);
                        JSONArray getNotificationPast = jsonObject.optJSONArray(KEY_PAST);
                        items.clear();
                        Log.d(TAG, "result   : " + getNotification + getNotificationPast);
                        if (getNotification != null || getNotificationPast != null) {
                            if (appContactService == null) {
                                appContactService = new AppContactService(getActivity());
                            }
                            items = ResponseHandler.getInstance().getNotificationList(getActivity(), jsonObject, appContactService);

                            if (isResumeRefresh && items.size() > 0 && mAdapter != null) {
                                Log.d(TAG, "newNotification");
                                setNotificationAdapter();
                            } else {
                                Log.d(TAG, "setNotificationAdapter");
                                setNotificationAdapter();
                            }
                            SharedPref.getInstance().setSharedValue(getActivity(), "notification_count", jsonObject.optInt("notification_count"));
                            notifyCountChanged();
                        }
                    } else {
                        Utility.showToastMessage(getActivity(), jsonObject.optString(KEY_MSG));
                    }
                }
                if (items.size() == 0) {
                    mEmptyViewNotification.setVisibility(View.VISIBLE);
                    mEmptyViewNoInternetConnection.setVisibility(View.GONE);
                } else {
                    mEmptyViewNotification.setVisibility(View.GONE);
                    mEmptyViewNoInternetConnection.setVisibility(View.GONE);
                    //showActivityDetailsTutorial();
                }
                break;
            case 1:
                try {
                    if (jsonObject.optBoolean("result")) {
                        delete();

                        if (mAdapter != null) {
                            Log.d(TAG, "deleted schedules id : " + mAdapter.getIdsToDelete());
                            clearUnReadCount();
                        }
                        getNotficationLsitFromAPI();
                    } else
                        Utility.showToastMessage(getActivity(), "Something went wrong!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 2:
                Log.d("pastttt", new Gson().toJson(jsonObject));
                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        JSONArray getNotificationPast = jsonObject.optJSONArray(KEY_PAST);

                        if (getNotificationPast != null) {
                            if (appContactService == null) {
                                appContactService = new AppContactService(getActivity());
                            }
                            items = ResponseHandler.getInstance().getNotificationList(getActivity(), jsonObject, appContactService);
                            if (isResumeRefresh && items.size() > 0 && mAdapter != null) {
                                Log.d(TAG, "newNotification");
                                setNotificationAdapter();
                            } else {
                                Log.d(TAG, "setNotificationAdapter");
                                setNotificationAdapter();
                            }
                            SharedPref.getInstance().setSharedValue(getActivity(), "notification_count", jsonObject.optInt("notification_count"));
                            notifyCountChanged();
                        }
                    }
                }
                break;
        }
        if (progressWheel.getVisibility() == View.VISIBLE) {
            progressWheel.stopAnimation();
            progressWheel.setVisibility(View.GONE);
        }


    }

    private void clearUnReadCount() {
        if (mMessageDatabaseService != null) {
            List<String> conversationId = getClearedNotificationConversationId();
            if (conversationId != null) {
                for (String channelId : conversationId) {
                    Log.d(TAG, "channelId :" + channelId.trim());
                    channelId = channelId.replaceAll("\\s", "");
                    mMessageDatabaseService.updateReadStatusForChannel(channelId.trim());
                }
            }
        }
    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void delete() {
        progressWheel.setVisibility(View.GONE);
        progressWheel.stopAnimation();
    }

    @Override
    public void showwheel() {
        progressWheel.setVisibility(View.VISIBLE);
        progressWheel.startAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumeRefresh = true;
        getNotficationLsitFromAPI();
    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void onStatusChanged(Object model, int position) {
        if (model != null) {
            if (model instanceof NotifyModel) {
                NotifyModel notifyModel = (NotifyModel) model;
                items.set(position, notifyModel);
                setNotificationAdapter();
            }
        }
    }

    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(registrationComplete);
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        getNotficationLsitFromAPI();
        refreshLayout.setRefreshing(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNewMessageReceiver();
    }

    private BroadcastReceiver mNewMessageReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, String.valueOf(intent));
            Message messageItem = null;
            if (intent.getAction().equalsIgnoreCase(AppConstants.ACTION_NEW_MESSAGE_RECEIVED)) {
                String message = intent.getStringExtra(MobiComKitConstants.MESSAGE_JSON_INTENT);
                if (!(TextUtils.isEmpty(message))) {
                    messageItem = (Message) GsonUtils.getObjectFromJson(intent.getStringExtra(MobiComKitConstants.MESSAGE_JSON_INTENT), Message.class);
                    if (messageItem != null) {
                        updateMessageCount(messageItem);
                    }
                }

            }
        }
    };

    private void updateMessageCount(Message messageItem) {
        if (mAdapter != null) {
            if (mAdapter.getItemCount() > 0) {
                //checkGroupIdPresence
                if (items != null && items.size() > 0) {
                    for (int i = 0; i < items.size(); i++) {
                        NotifyModel notifyModel = items.get(i);
                        if (!(TextUtils.isEmpty(notifyModel.getGroup_id()))) {
                            if (notifyModel.getGroup_id().equalsIgnoreCase(String.valueOf(messageItem.getGroupId()))) {
                                notifyModel.setMsg_count(notifyModel.getMsg_count() + 1);
                                notifyModel = getMessage(notifyModel, messageItem);
                                items.remove(i);
                                items.add(0, notifyModel);
                                mAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    private NotifyModel getMessage(NotifyModel notifyModel, Message message) {
        if (notifyModel.getStatus().equals(KEY_ACCEPTED) || notifyModel.getStatus().equals(KEY_CREATED)) {
            try {
                if (notifyModel.isUpComing())
                    notifyModel.setMessage(mApplozicChatHandler.getLastMessageFromGroup(message.getGroupId(), appContactService));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return notifyModel;
    }

    private boolean isGroupIdNotNull(String conversationId) {
        return !(TextUtils.isEmpty(conversationId) || conversationId.equalsIgnoreCase("0") || conversationId.equalsIgnoreCase("null"));
    }

    public List<String> getClearedNotificationConversationId() {
        List<String> conversationId = new ArrayList<>();
        String scheduledId = mAdapter.getIdsToDelete();
        if (TextUtils.isEmpty(scheduledId)) {
            return conversationId;
        }
        String[] id = scheduledId.split(",");
        if (id.length > 0 && items != null) {
            conversationId = new ArrayList<>();
            for (String anId : id) {
                for (int j = 0; j < items.size(); j++) {
                    if (items.get(j).getIdschedule().equalsIgnoreCase(anId.trim())) {
                        if (isGroupIdNotNull(items.get(j).getGroup_id().trim())) {
                            conversationId.add(items.get(j).getGroup_id().trim());
                        }
                    }
                }
            }
        }
        return conversationId;
    }

    @Override
    public void refresh() {
        isResumeRefresh = true;
        getNotficationLsitFromAPI();
    }
}
