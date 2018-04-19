package com.uactiv.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.Gson;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.uactiv.R;
import com.uactiv.activity.PickUpGuest;
import com.uactiv.adapter.viewholder.StikcyHeaderViewHolder;
import com.uactiv.controller.ItemDelete;
import com.uactiv.controller.ResponseListener;
import com.uactiv.fragment.Notification_Fragment;
import com.uactiv.model.NotifyModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * Created by moorthy on 3/14/2016
 */
public class NotificationHeaderAdapter extends NotificationStickyBaseAdapter implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>, AppConstants.urlConstants, AppConstants.SharedConstants, ResponseListener {

    private String TAG = getClass().getSimpleName();
    public static ItemViewHolder tempItemViewHolder;
    Context mContext;
    ItemDelete itemDelete;
    int postion;
    String idshedule;
    String title;
    private ArrayList<String> selectedItems;
    private boolean selectedItemBool = false;
    private Notification_Fragment mNotification_Fragment;
    MessageDatabaseService messageDatabaseService;

    public NotificationHeaderAdapter(Context context, ArrayList<NotifyModel> notifyModelArrayList, ItemDelete itemDelete, Notification_Fragment mNotification_Fragment) {
        this.mContext = context;
        this.itemDelete = itemDelete;
        newNotification(notifyModelArrayList);
        selectedItems = new ArrayList<>();
        this.mNotification_Fragment = mNotification_Fragment;
        Log.d("hii",new Gson().toJson(notifyModelArrayList));

        Utility.setScreenTracking(mContext, AppConstants.SCREEN_TRACKING_ID_INAPPNOTIFICATION);
    }

    public void getConversationId(){

    }
    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }
    public String getIdsToDelete() {
        String ids = selectedItems.toString();
        ids = ids.replace("[", "").replace("]", "").trim();
        //Log.e("IDs to Delete", "" + ids);
        return ids;
    }

    public void removeDelete() {
        selectedItems.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_notification, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemViewHolder holder = new ItemViewHolder(viewHolder.itemView);
        holder.tvDetails.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.tvDetails.setSelected(true);
        holder.tvDetails.requestFocus();
        if (position == 0 && getItemCount() > 0) {
            tempItemViewHolder = holder;
            showTutorials();
        }

        final NotifyModel notifyModel = getItem(position);

        holder.selectOrNot.setVisibility(View.GONE);
        if (selectedItems.contains(notifyModel.getIdschedule())) {
            holder.selectOrNot.setVisibility(View.VISIBLE);
        }

        holder.tvCircle.setText("");
        holder.tvCircle.setVisibility(View.GONE);


        if ((notifyModel.getRequest_count() != 0 &&
                (notifyModel.getRequest_count() > 0))) {
            holder.tvCircle.setVisibility(View.VISIBLE);
            holder.tvCircle.setText(String.valueOf(notifyModel.getRequest_count()));

        }

        holder.countCircle.setText("");
        holder.countCircle.setVisibility(View.INVISIBLE);
        holder.tvDetails.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

        if ((notifyModel.getMsg_count() != 0 &&
                notifyModel.getMsg_count() > 0) || notifyModel.isRead()) {
            if (notifyModel.getMsg_count() > 0) {
                holder.countCircle.setVisibility(View.VISIBLE);
                holder.countCircle.setText(String.valueOf(notifyModel.getMsg_count()));
            }
            holder.tvDetails.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        }

        holder.tv_attending_count.setText("");
        holder.attending_cout_image.setVisibility(View.GONE);
        holder.tv_attending_count.setVisibility(View.GONE);

        if (notifyModel.getAttending_count() != 0 &&
                notifyModel.getAttending_count() > 0) {
            holder.attending_cout_image.setVisibility(View.VISIBLE);
            holder.tv_attending_count.setVisibility(View.VISIBLE);
            holder.tv_attending_count.setText("+" + notifyModel.getAttending_count() + "".trim());
        }

        if(!notifyModel.getGameDescription().isEmpty()||!notifyModel.getGameDescription().equalsIgnoreCase("null")){
            holder.tvGame.setText(notifyModel.getGameDescription());
        }else{
            holder.tvGame.setText("");
        }



        holder.tvDetails.setText(notifyModel.getMessage());


        holder.delete.setVisibility(View.GONE);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postion = position;
                idshedule = notifyModel.getIdschedule();
                String message = "Are you sure?";
                showMenuAlert(title, message);
            }
        });

        holder.parentLayout.setTag(position);
        holder.gameLayout.setTag(position);
        holder.detailsLayout.setTag(position);


        holder.detailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!selectedItemBool || getItem(Integer.parseInt(v.getTag().toString())).isUpComing()) {
                    Utility.setEventTracking(mContext, "In app notification screen", "Notification white bar on app notifiaction screen");
                    if (notifyModel.getStatus() != null) {
                        Intent intent = new Intent(mContext, PickUpGuest.class);
                        intent.putExtra("from_schedule", false);
                        intent.putExtra("fragment", "map");
                        intent.putExtra("position", position + "");
                        intent.putExtra("status", notifyModel.getStatus());
                        intent.putExtra("idschedule", notifyModel.getIdschedule());
                        intent.putExtra("sstatus", notifyModel.getIsActive());
                        intent.putExtra("attending_id", notifyModel.getAccepted_id());
                        intent.putExtra("isUpComing", notifyModel.isUpComing());
                        intent.putExtra("idUser", notifyModel.getIdUser());
                        intent.putExtra("type", notifyModel.getType());
                        intent.putExtra("group_id", notifyModel.getGroup_id());
                        intent.putExtra("attending", notifyModel.getAttending_count());
                        intent.putExtra("itsnotification","itsnotification");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("notify_model", notifyModel);
                        intent.putExtras(bundle);

                        if(notifyModel.getMessage().equalsIgnoreCase("Pick Up Declined")){


                        }

                        switch (notifyModel.getType()) {

                            case KEY_BUDDY_UP:


                                if (notifyModel.getStatus().equals(KEY_ABANDON) || notifyModel.getStatus().equals(KEY_ACCEPTED) || notifyModel.getStatus().equals(KEY_CANCEL) || notifyModel.getStatus().equals("completed")) {

                                    if (notifyModel.getGroup_id() != null && !notifyModel.getGroup_id().equalsIgnoreCase("0") && !notifyModel.getGroup_id().equalsIgnoreCase("null") && !TextUtils.isEmpty(notifyModel.getGroup_id())) {
                                        intent.putExtra("fragment", "chat");
                                        mContext.startActivity(intent);
                                        //chat(notifyModel.getGroup_id(), notifyModel.getType(), notifyModel.isUpComing(), notifyModel.getStatus(), notifyModel.getIsActive());
                                    } else {
                                        intent.putExtra("fragment", "chat");
                                        mContext.startActivity(intent);
                                    }
                                } else {
                                    intent.putExtra("fragment", "map");
                                    mContext.startActivity(intent);
                                    // Utility.showToastMessage(mContext, "You are not in " + notifyModel.getType() + " chat group!");
                                }

                                break;
                            case KEY_PICK_UP:

                                if (notifyModel.getStatus().equals(KEY_ACCEPTED) || (notifyModel.getStatus().equals(KEY_CREATED) && notifyModel.getAttending_count() > 0) || notifyModel.getStatus().equals(KEY_ABANDON) || notifyModel.getStatus().equals(KEY_CANCEL) || notifyModel.getStatus().equals("completed")) {

                                    if (notifyModel.getAttending_count() > 0 || notifyModel.getAbandoned_count() > 0) {

                                        if (notifyModel.getGroup_id() != null && !notifyModel.getGroup_id().equalsIgnoreCase("0") && !notifyModel.getGroup_id().equalsIgnoreCase("null") && !TextUtils.isEmpty(notifyModel.getGroup_id())) {
                                            intent.putExtra("fragment", "map");
                                            intent.putExtra("checkchat", "chat");
                                            mContext.startActivity(intent);
                                            // chat(notifyModel.getGroup_id(), notifyModel.getType(), notifyModel.isUpComing(), notifyModel.getStatus(), notifyModel.getIsActive());
                                        } else {
                                            intent.putExtra("fragment", "chat");
                                            mContext.startActivity(intent);
                                        }
                                    } else {
                                        intent.putExtra("fragment", "map");
                                        mContext.startActivity(intent);
                                    }
                                } else {

                                    intent.putExtra("fragment", "map");
                                    mContext.startActivity(intent);
                                    //Utility.showToastMessage(mContext, "You are not in " + notifyModel.getType() + " chat group!");
                                }


                                break;
                            default:
                                break;
                        }
                    }
                } else {
                    if (selectedItems.contains(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule())) {
                        selectedItems.remove(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                        holder.selectOrNot.setVisibility(View.GONE);
                        Log.e("***DO detLayout Remove", "" + getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                    } else {
                        selectedItems.add(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                        holder.selectOrNot.setVisibility(View.VISIBLE);
                        mNotification_Fragment.onDelete(true);
                        Log.e("***DO detLayout Add", "" + getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                    }
                    if (selectedItems.size() == 0) {
                        selectedItemBool = false;
                        mNotification_Fragment.onDelete(false);
                    }
                }
            }
        });


        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "parentLayout setOnLongClickListener ");
                if (!getItem(Integer.parseInt(v.getTag().toString())).isUpComing()) {
                    if (selectedItems.contains(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule())) {
                        selectedItems.remove(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                        holder.selectOrNot.setVisibility(View.GONE);
                        Log.e("***DL gameLayout Remove", "" + getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                    } else {
                        selectedItems.add(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                        holder.selectOrNot.setVisibility(View.VISIBLE);
                        mNotification_Fragment.onDelete(true);
                        selectedItemBool = true;
                        Log.e("***DL gameLayout Add", "" + getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                    }

                    if (selectedItems.size() == 0) {
                        selectedItemBool = false;
                        mNotification_Fragment.onDelete(false);
                    }
                }
                return true;
            }
        });

        Utility.setImageUniversalLoader(mContext, notifyModel.getImage(), holder.imageProfile);

        holder.gameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "gameLayout setOnClickListener ");
                Utility.setEventTracking(mContext, "In app notification screen", "Notification blue bar on app notifiaction screen");

                if (!selectedItemBool || getItem(Integer.parseInt(v.getTag().toString())).isUpComing()) {
                    Log.d("status = " + notifyModel.getStatus(), "Message = " + notifyModel.getMessage());
                    Intent intent = new Intent(mContext, PickUpGuest.class);
                    intent.putExtra("from_schedule", false);
                    intent.putExtra("fragment", "map");
                    intent.putExtra("game", "game");
                    intent.putExtra("position", position + "");
                    intent.putExtra("status", notifyModel.getStatus());
                    intent.putExtra("idschedule", notifyModel.getIdschedule());
                    intent.putExtra("sstatus", notifyModel.getIsActive());
                    intent.putExtra("attending_id", notifyModel.getAccepted_id());
                    intent.putExtra("isUpComing", notifyModel.isUpComing());
                    intent.putExtra("idUser", notifyModel.getIdUser());
                    intent.putExtra("type", notifyModel.getType());
                    intent.putExtra("group_id", notifyModel.getGroup_id());
                    intent.putExtra("notification_type",notifyModel.getNotificationTypeTitle());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("notify_model", notifyModel);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                } else {
                    if (selectedItems.contains(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule())) {
                        selectedItems.remove(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                        holder.selectOrNot.setVisibility(View.GONE);
                        Log.e("***DO gameLayout Remove", "" + getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                    } else {
                        selectedItems.add(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                        holder.selectOrNot.setVisibility(View.VISIBLE);
                        mNotification_Fragment.onDelete(true);
                        Log.e("***DO gameLayout Add", "" + getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                    }
                    if (selectedItems.size() == 0) {
                        selectedItemBool = false;
                        mNotification_Fragment.onDelete(false);
                    }
                }

               /* if(read !=null && notifyModel.getRequest_count() > 0) {
                    read.readed(notifyModel, position);
                }*/
            }
        });


        holder.detailsLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, " detailsLayout setOnLongClickListener");
                if (!getItem(Integer.parseInt(v.getTag().toString())).isUpComing()) {
                    if (selectedItems.contains(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule())) {
                        selectedItems.remove(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                        holder.selectOrNot.setVisibility(View.GONE);
                        Log.e("***DL detLayout Remove", "" + getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                    } else {
                        selectedItems.add(getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                        selectedItemBool = true;
                        holder.selectOrNot.setVisibility(View.VISIBLE);
                        mNotification_Fragment.onDelete(true);
                        Log.e("***DL detLayout Add", "" + getItem(Integer.parseInt(v.getTag().toString())).getIdschedule());
                    }

                    if (selectedItems.size() == 0) {
                        selectedItemBool = false;
                        mNotification_Fragment.onDelete(false);
                    }
                }
                return true;
            }
        });


    }

    private void chat(String chatgroupid, String type, boolean isUpComing, String status, String isActive) {
        if (!TextUtils.isEmpty(chatgroupid) && !chatgroupid.equals("null") && !TextUtils.isEmpty(type)) {
            Intent intent = new Intent(mContext, ConversationActivity.class);
            intent.putExtra(ConversationActivity.TAKE_ORDER, true);
            intent.putExtra(ConversationUIService.GROUP_ID, Integer.valueOf(chatgroupid));
            intent.putExtra(ConversationActivity.TAKE_CHAT, false);
            if (!isUpComing || status.equals(KEY_ABANDON) || isActive.equals(KEY_IN_ACTIVE) || status.equals(KEY_CANCEL)) {
                intent.putExtra(ConversationActivity.TAKE_CHAT, true);
            } else {

                intent.putExtra(ConversationActivity.TAKE_CHAT, false);
            }
            if (type.equalsIgnoreCase("buddyup"))
                intent.putExtra(ConversationUIService.GROUP_NAME, "Buddy Up");
            else
                intent.putExtra(ConversationUIService.GROUP_NAME, "Pick Up");

            mContext.startActivity(intent);

        }
    }

    public void showMenuAlert(String title, String message) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // On pressing Settings button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "yes its from notification header");
                callDeleteConversation(idshedule);
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

    private void callDeleteConversation(String idschedule) {

        if (Utility.isConnectingToInternet(mContext)) {
            try {
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(mContext, userId));
                param.put("idschedule", idschedule);
                itemDelete.showwheel();
                RequestHandler.getInstance().stringRequestVolley(mContext, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(mContext, isStaging)) + deleteNotification, param, this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private void showTutorials() {


        new MaterialShowcaseView.Builder((Activity) mContext)
                .setTextTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/Brandon_bld.otf"))
                .setTarget(tempItemViewHolder.itemView.findViewById(R.id.gameLayout))
                .setDismissText(mContext.getResources().getString(R.string.tutorial_got_it))
                .withRectangleShape()
                .setDismissOnTouch(true)
                .singleUse(String.valueOf(AppConstants.TUTORIAL_VIEW_CHAT_ID))
                .setContentTextColor(mContext.getResources().getColor(R.color.white))
                .setMaskColour(mContext.getResources().getColor(R.color.transparent_gray))
                .setContentText(mContext.getString(R.string.tutorial_msg_view_activity_details))
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        new MaterialShowcaseView.Builder((Activity) mContext)
                                .setTextTypeface(Typeface.createFromAsset(mContext.getAssets(),"fonts/Brandon_bld.otf"))
                                .setTarget(tempItemViewHolder.itemView.findViewById(R.id.detailsLayout))
                                .setDismissText(mContext.getResources().getString(R.string.tutorial_got_it))
                                .withRectangleShape()
                                .setDismissOnTouch(true)
                                .setContentTextColor(mContext.getResources().getColor(R.color.white))
                                .setContentText(mContext.getString(R.string.tutorils_msg_view_chat))
                                .setMaskColour(mContext.getResources().getColor(R.color.transparent_gray))
                                .setListener(new IShowcaseListener() {
                                    @Override
                                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {

                                    }
                                }).show();
                    }
                }).show();


    }

    @Override
    public long getHeaderId(int position) {
        if (getItem(position) != null) {
            return getItem(position).getNotificationTypeTitle().charAt(0);
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inflate_notification_header_lay, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int i) {
        StikcyHeaderViewHolder viewHolder = new StikcyHeaderViewHolder(holder.itemView);

        if (getItem(i).isUpComing()) {
            viewHolder.mTitle.setBackgroundColor(ContextCompat.getColor(mContext, R.color.green));
        } else {
            viewHolder.mTitle.setBackgroundColor(ContextCompat.getColor(mContext, R.color.red));
        }
        viewHolder.mTitle.setText(getItem(i).getNotificationTypeTitle());
    }

    public void showActivityDetailsTutorial() {
        ShowcaseView mShowcaseViewCreatePickup = new ShowcaseView.Builder((Activity) mContext)
                .setTarget(new ViewTarget(tempItemViewHolder.itemView.findViewById(R.id.imgProfile)))
                .setContentText(mContext.getString(R.string.tutorils_msg_view_chat))
                .setContentTextPaint(Utility.getTextPaint(mContext))
                .singleShot(AppConstants.TUTORIAL_VIEW_ACTIVITY_ID)

                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                        showFavTutorial();
                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {

                    }
                })
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        mShowcaseViewCreatePickup.setButtonText(mContext.getString(R.string.tutorial_got_it));
    }

    public void showFavTutorial() {
        ShowcaseView mShowcaseViewCreatePickup = new ShowcaseView.Builder((Activity) mContext)
                .setTarget(new ViewTarget(tempItemViewHolder.itemView.findViewById(R.id.gameLayout)))
                .setContentText(mContext.getString(R.string.tutorial_msg_view_activity_details))
                .setContentTextPaint(Utility.getTextPaint(mContext))
                //.singleShot(AppConstants.TUTORIAL_ADD_FAV_ID)
                .blockAllTouches()
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        mShowcaseViewCreatePickup.setButtonText(mContext.getResources().getString(R.string.tutorial_got_it));
    }

    @Override
    public void successResponse(String successResponse, int flag) {
        Log.e("successResponse", successResponse);
        try {
            JSONObject jsonObject = new JSONObject(successResponse);
            if (jsonObject.optBoolean("result")) {
                itemDelete.delete();
                remove(postion);
                Log.e("deleted", successResponse);
            } else
                Utility.showToastMessage(mContext, "Some thing went wrong!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {

    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvGame, tvCircle, tvDetails, countCircle;
        RelativeLayout parentLayout, gameLayout, detailsLayout;
        CircleImageView imageProfile;
        ImageView delete;
        ImageView attending_cout_image = null;
        CustomTextView tv_attending_count = null;
        ImageView selectOrNot;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvGame = (TextView) itemView.findViewById(R.id.tvGame);
            tvCircle = (TextView) itemView.findViewById(R.id.tvCircle);
            tvDetails = (TextView) itemView.findViewById(R.id.tvDetails);
            countCircle = (TextView) itemView.findViewById(R.id.countCircle);
            imageProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
            gameLayout = (RelativeLayout) itemView.findViewById(R.id.gameLayout);
            detailsLayout = (RelativeLayout) itemView.findViewById(R.id.detailsLayout);
            attending_cout_image = (ImageView) itemView.findViewById(R.id.imgCount);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            tv_attending_count = (CustomTextView) itemView.findViewById(R.id.tv_attending_count);
            selectOrNot = (ImageView) itemView.findViewById(R.id.selectOrNot);

        }
    }


    private class ClearNotification {
        private String scheduleId;
        private String conversationGroupId;

        public ClearNotification(String scheduleId) {
            this.scheduleId = scheduleId;
        }

        public ClearNotification(String scheduleId, String conversationGroupId) {
            this.scheduleId = scheduleId;
            this.conversationGroupId = conversationGroupId;
        }

        public String getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(String scheduleId) {
            this.scheduleId = scheduleId;
        }

        public String getConversationGroupId() {
            return conversationGroupId;
        }

        public void setConversationGroupId(String conversationGroupId) {
            this.conversationGroupId = conversationGroupId;
        }
    }
}
