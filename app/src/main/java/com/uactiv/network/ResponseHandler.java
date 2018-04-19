package com.uactiv.network;


import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.uactiv.R;
import com.uactiv.applozicchat.ApplozicChat;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.ChatDo;
import com.uactiv.model.NotifyModel;
import com.uactiv.model.PickUpCategory;
import com.uactiv.model.PickUpModel;
import com.uactiv.model.ScheduleMemberDo;
import com.uactiv.model.ScheduleModel;
import com.uactiv.model.SkillDo;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.BeanComparator;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by jeeva on 10/5/2015.
 */
public class ResponseHandler implements AppConstants.SharedConstants, AppConstants.urlConstants {

    private static ResponseHandler responseHandler = null;
    String TAG = getClass().getSimpleName();
    private ArrayList<String> activitylist = null;
    String push;


    public static ResponseHandler getInstance() {
        if (responseHandler != null) {
            return responseHandler;
        } else {
            responseHandler = new ResponseHandler();
            return responseHandler;
        }
    }

    public void addActivity(Context context) {

        activitylist = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(SharedPref.getInstance().getStringVlue(context, skills));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                activitylist.add(jsonObject.optString("activity"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void storeProfileInfo(Context context, JSONObject response) throws JSONException {

        //     "created_on": null, //Not soted in shared.
        //    "device_type": "0",

        if (response != null) {

            JSONObject profileInfo = response.getJSONObject("details");

            Log.e("storeProfileInfo OBj", profileInfo.toString());

            if (profileInfo != null) {

                SharedPref.getInstance().setSharedValue(context, userId, profileInfo.optString("iduser"));
                SharedPref.getInstance().setSharedValue(context, "use", profileInfo.optString("iduser"));

                Log.e("storeProfileInfo1", "" + SharedPref.getInstance().getStringVlue(context, userId));

                SharedPref.getInstance().setSharedValue(context, firstname, profileInfo.optString("firstname"));
                SharedPref.getInstance().setSharedValue(context, lastname, profileInfo.optString("lastname"));
                SharedPref.getInstance().setSharedValue(context, email, profileInfo.optString("email"));
                SharedPref.getInstance().setSharedValue(context, password, profileInfo.optString("password"));
                SharedPref.getInstance().setSharedValue(context, age, profileInfo.optString("age"));
                SharedPref.getInstance().setSharedValue(context, gender, profileInfo.optString("gender"));
Log.d("customimage",AppConstants.getiamgebaseurl() + profileInfo.getJSONArray("images").getJSONObject(0).getString("image"));
                SharedPref.getInstance().setSharedValue(context, image,AppConstants.getiamgebaseurl() + profileInfo.getJSONArray("images").getJSONObject(0).getString("image"));

                SharedPref.getInstance().setSharedValue(context, about_yourself, profileInfo.optString("about_yourself"));


                SharedPref.getInstance().setSharedValue(context, badge, profileInfo.optString("badge"));


                SharedPref.getInstance().setSharedValue(context, count, profileInfo.optString("count"));

                SharedPref.getInstance().setSharedValue(context, facebook_link, profileInfo.optString("facebook_link"));


                SharedPref.getInstance().setSharedValue(context, facebookid, profileInfo.optString("facebookid"));


                SharedPref.getInstance().setSharedValue(context, radius_limit, profileInfo.optString("radius_limit"));


                SharedPref.getInstance().setSharedValue(context, gender_pref, profileInfo.optString("gender_pref"));


                SharedPref.getInstance().setSharedValue(context, isreceive_request, profileInfo.optString("isreceive_request"));


                SharedPref.getInstance().setSharedValue(context, isreceive_request, profileInfo.optString("isreceive_request"));


                SharedPref.getInstance().setSharedValue(context, status, profileInfo.optString("status"));
                SharedPref.getInstance().setSharedValue(context, isreceive_notification, profileInfo.optString("isreceive_notification"));
                SharedPref.getInstance().setSharedValue(context, device_id, profileInfo.optString("device_id"));

                SharedPref.getInstance().setSharedValue(context, skills, profileInfo.optJSONArray("skill").toString());

                SharedPref.getInstance().setSharedValue(context, referalcode, profileInfo.optString("referral_code"));

                SharedPref.getInstance().setSharedValue(context, USER_TYPE, profileInfo.optInt("user_type"));

                SharedPref.getInstance().setSharedValue(context, rating, profileInfo.optInt("rating"));

                SharedPref.getInstance().setSharedValue(context, user_rating_count, profileInfo.optInt("rated_count"));

                SharedPref.getInstance().setSharedValue(context, latitude, profileInfo.optString("latitude"));

                SharedPref.getInstance().setSharedValue(context, longitude, profileInfo.optString("longitude"));


                if (profileInfo.optInt("user_type") != USER_TYPE_APP) {
                    SharedPref.getInstance().setSharedValue(context, "thisisbusiness", true);
                    SharedPref.getInstance().setSharedValue(context, isbussiness, true);
                    SharedPref.getInstance().setSharedValue(context, phoneno, profileInfo.optString("phone_no"));
                    SharedPref.getInstance().setSharedValue(context, address, profileInfo.optString("address"));
                    SharedPref.getInstance().setSharedValue(context, businessName, profileInfo.optString("business_name"));
                    SharedPref.getInstance().setSharedValue(context, landline, profileInfo.optString("landline"));
                    SharedPref.getInstance().setSharedValue(context, businessType, profileInfo.optString("business_type"));
                }


                //   }

            }
        }

    }

    public ArrayList<BuddyModel> storeBuddyList(JSONArray jsonArrayBuddy) {

        ArrayList<BuddyModel> buddyModelArrayList = new ArrayList<>();

        BuddyModel buddyModel = null;

        SkillDo skillDo = null;

        if (jsonArrayBuddy != null) {

            for (int i = 0; i < jsonArrayBuddy.length(); i++) {

                try {

                    JSONObject buddyObj = jsonArrayBuddy.getJSONObject(i);
                    JSONArray skillArray = buddyObj.getJSONArray("skills");
                    buddyModel = new BuddyModel();
                    buddyModel.setUserid(buddyObj.optString("iduser"));
                    buddyModel.setName(buddyObj.optString("firstname") + " " + buddyObj.optString("lastname"));

                    push = AppConstants.getiamgebaseurl() + new JSONArray(buddyObj.optString("image")).toString().replaceAll("\\[|\\]", "");
                    buddyModel.setImage(push.replace("\"", ""));
                    Log.d("customimg", push.replace("\"", ""));

                    buddyModel.setAge(buddyObj.optString("age"));
                    buddyModel.setAwayDistance(buddyObj.optString("distance"));
                    buddyModel.setIsfav(buddyObj.optString("fav"));
                    buddyModel.setBadge(buddyObj.optString("badge"));
                    buddyModel.setMutual_friends(buddyObj.optString("mutual_friends"));
                    buddyModel.setAbout_yourself(buddyObj.optString("about_yourself"));
                    buddyModel.setIsreceivebuddyrequest(buddyObj.optString("isreceive_request"));
                    buddyModel.setUser_type(buddyObj.optInt("user_type"));
                    buddyModel.setRating(buddyObj.optInt("rating"));
                    buddyModel.setRating_count(buddyObj.optInt("rated_count"));
                    buddyModel.setEmail(buddyObj.optString("email"));
                    buddyModel.setPhone_no(buddyObj.optString("phone_no"));
                    buddyModel.setAddress(buddyObj.optString("address"));
                    buddyModel.setBadgeImage(updateBadge(buddyObj.optString("badge")));
                    buddyModel.setFacebookId(buddyObj.optString("facebookid"));

                    if (skillArray != null) {

                        ArrayList<SkillDo> skilltemp = new ArrayList<>();

                        for (int j = 0; j < skillArray.length(); j++) {
                            skillDo = new SkillDo();
                            skillDo.setActivty(skillArray.getJSONObject(j).optString("activity"));
                            skillDo.setLevel(skillArray.getJSONObject(j).optString("level"));
                            skillDo.setActivity_type(skillArray.getJSONObject(j).optString("type"));
                            if (skillArray.getJSONObject(j).optInt("is_open") == 1) {
                                skillDo.setIsBookingOpen(true);
                            }
                            skilltemp.add(skillDo);
                        }
                        buddyModel.setSkillDo(skilltemp);
                    }

                    buddyModelArrayList.add(buddyModel);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }
        return buddyModelArrayList;
    }


    private int updateBadge(String badge) {

        switch (badge) {
            case "Rookie":
                return R.drawable.batchicon_a;
            case "Contender":
                return R.drawable.batchicon_b;

            case "All Star":
                return R.drawable.batchicon_c;

            case "Ace":
                return R.drawable.batchicon_d;

            case "Champion":
                return R.drawable.batchicon_e;

            case "Hercules/Bia":
                return R.drawable.batchicon_f;

            case "Hercules":
                return R.drawable.batchicon_f;

            default:
                return 0;
        }

    }

    public ArrayList<ScheduleModel> getScheduleList(JSONArray jsonArray, boolean isUpComing) {

        ArrayList<ScheduleModel> scheduleModelArrayList = null;
        ArrayList<ScheduleMemberDo> scheduleMemberDoArrayList = null;

        if (jsonArray != null) {

            scheduleModelArrayList = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {


                ScheduleModel scheduleModel = new ScheduleModel();
                scheduleModel.setIsUpComing(isUpComing);
                scheduleModel.setIdschedule(jsonArray.optJSONObject(i).optString("idschedule"));
                scheduleModel.setIduser(jsonArray.optJSONObject(i).optString("iduser"));

                String dtStart = jsonArray.optJSONObject(i).optString("date") + " " + jsonArray.optJSONObject(i).optString("start_time");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = format.parse(dtStart);
                    scheduleModel.setDate(date);
                } catch (ParseException e) {

                    e.printStackTrace();
                    scheduleModel.setDate(null);
                }


                scheduleModel.setStart_time(jsonArray.optJSONObject(i).optString("start_time"));
                scheduleModel.setActivity(jsonArray.optJSONObject(i).optString("activity"));
                scheduleModel.setType(jsonArray.optJSONObject(i).optString("type"));
                scheduleModel.setFirstname(jsonArray.optJSONObject(i).optString("firstname"));

                if (jsonArray.optJSONObject(i).optString("type").equals("buddyup")) {

                    scheduleModel.setMember(jsonArray.optJSONObject(i).optString("member"));

                } else if (jsonArray.optJSONObject(i).optString("type").equals("pickup")) {

                    JSONArray pickupMemberlist = jsonArray.optJSONObject(i).optJSONArray("member");

                    Log.d("ResponseHandler", "" + pickupMemberlist.toString());

                    if (pickupMemberlist != null) {
                        scheduleMemberDoArrayList = new ArrayList<>();

                        if (pickupMemberlist.length() > 0) {
                            for (int k = 0; k < pickupMemberlist.length(); k++) {
                                ScheduleMemberDo scheduleMemberDo = new ScheduleMemberDo();
                                scheduleMemberDo.setFirstname(pickupMemberlist.optJSONObject(k).optString("firstname"));
                                scheduleMemberDo.setIduser(pickupMemberlist.optJSONObject(k).optString("iduser"));
                                scheduleMemberDoArrayList.add(scheduleMemberDo);
                            }
                            scheduleModel.setScheduleMemberDoArrayList(scheduleMemberDoArrayList);
                        }
                    }

                }

                scheduleModelArrayList.add(scheduleModel);
            }
        }
        return scheduleModelArrayList;
    }


    private Date dateFormat(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<PickUpModel> getPickupSearch(JSONArray pickupjsonArray, String searchString) {

        ArrayList<PickUpModel> pickUpModelArrayList = new ArrayList<>();

        ArrayList<PickUpCategory> PickUpCategoryCategories;

        try {
            if (pickupjsonArray.length() > 0 && pickupjsonArray != null && searchString != null && searchString.length() > 0) {

                PickUpModel pickupmodel = new PickUpModel();
                pickupmodel.setActivityname("" + searchString);
                PickUpCategoryCategories = new ArrayList<>();
                for (int j = 0; j < pickupjsonArray.length(); j++) {

                    PickUpCategory pickUpCategory = new PickUpCategory();
                    pickUpCategory.setImage(pickupjsonArray.optJSONObject(j).optString("image"));
                    pickUpCategory.setEventdate(pickupjsonArray.optJSONObject(j).optString("date"));
                    pickUpCategory.setStarttime(pickupjsonArray.optJSONObject(j).optString("start_time"));
                    pickUpCategory.setNoofpeople(pickupjsonArray.optJSONObject(j).optString("no_of_people"));
                    pickUpCategory.setAwayfrom(pickupjsonArray.optJSONObject(j).optString("distance"));
                    pickUpCategory.setLatitude(pickupjsonArray.optJSONObject(j).optDouble("latitude"));
                    pickUpCategory.setLongitude(pickupjsonArray.optJSONObject(j).optDouble("longitude"));
                    pickUpCategory.setIdschedule(pickupjsonArray.optJSONObject(j).optString("idschedule"));
                    pickUpCategory.setStatus(pickupjsonArray.optJSONObject(j).optString("status"));
                    pickUpCategory.setEventTimeStamp(dateFormat(pickUpCategory.getEventdate() + " " + pickUpCategory.getStarttime()));


                    PickUpCategoryCategories.add(pickUpCategory);
                    //PickUpCategoryCategories =orderByChronological(PickUpCategoryCategories);
                }

                //Patch done by Jeeva on 2-02-16
                BeanComparator comparator = new BeanComparator(PickUpCategory.class, "getEventTimeStamp", true);
                Collections.sort(PickUpCategoryCategories, comparator);
                pickupmodel.setPickUpCategoryList(PickUpCategoryCategories);
                //Patch done by Jeeva on 2-02-16

                pickUpModelArrayList.add(pickupmodel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pickUpModelArrayList;
    }


    public ArrayList<PickUpModel> storePickupList(Context context, JSONObject jsonObject) {

        Log.e(TAG, ":Pickuplist" + jsonObject.toString());

        //addActivity(context);
        String[] activities = null;

        ArrayList<PickUpModel> pickUpModelArrayList = new ArrayList<>();
        ArrayList<PickUpCategory> PickUpCategoryCategories;

        if (jsonObject != null) {
            try {
                String activity = jsonObject.optString("skills");
                if (activity != null) {
                    activities = activity.split(",");
                    Log.e("activity = ", "" + activities.length);
                    JSONObject jsonpickupObj = jsonObject.getJSONObject(KEY_DETAIL);
//                if (activitylist != null && activitylist.size() > 0) {
                    for (int i = 0; i < activities.length; i++) {
                        if (jsonpickupObj.has(activities[i])) {

                            JSONArray pickupjsonArray = jsonpickupObj.optJSONArray(activities[i]);
                            try {
                                if (pickupjsonArray.length() > 0 && pickupjsonArray != null) {
                                    PickUpModel pickupmodel = new PickUpModel();
                                    pickupmodel.setActivityname(activities[i]);
                                    PickUpCategoryCategories = new ArrayList<>();
                                    for (int j = 0; j < pickupjsonArray.length(); j++) {
                                        PickUpCategory pickUpCategory = new PickUpCategory();
                                        pickUpCategory.setIduser(pickupjsonArray.optJSONObject(j).optString("iduser"));
                                        try {
                                            pickUpCategory.setImage(AppConstants.getiamgebaseurl() +pickupjsonArray.getJSONObject(j).getJSONArray(("image")).getJSONObject(0).getString("image"));
                                            Log.d("pickupimage", String.valueOf(AppConstants.getiamgebaseurl() +pickupjsonArray.getJSONObject(j).getJSONArray(("image")).getJSONObject(0).getString("image")));

                                        } catch (JSONException ex) {

                                        }


                                        //    pickUpCategory.setImage(AppConstants.getiamgebaseurl() +pickupjsonArray.getJSONObject(j).getJSONArray(("image")).getJSONObject(0).getString("image"));
                                        //  Log.d("pickupimage", String.valueOf(AppConstants.getiamgebaseurl() +pickupjsonArray.getJSONObject(j).getJSONArray(("image")).getJSONObject(0).getString("image")));
                                        //pickupjsonArray.getJSONObject(j).getJSONObject("images").toString();

                                        pickUpCategory.setEventdate(pickupjsonArray.optJSONObject(j).optString("pickup_date"));
                                        pickUpCategory.setFull_date(pickupjsonArray.optJSONObject(j).optString("full_date"));
                                        pickUpCategory.setStarttime(pickupjsonArray.optJSONObject(j).optString("start_time"));
                                        pickUpCategory.setNoofpeople(pickupjsonArray.optJSONObject(j).optString("no_of_people"));
                                        pickUpCategory.setAwayfrom(pickupjsonArray.optJSONObject(j).optString("distance"));
                                        pickUpCategory.setLatitude(pickupjsonArray.optJSONObject(j).optDouble("latitude"));
                                        pickUpCategory.setLongitude(pickupjsonArray.optJSONObject(j).optDouble("longitude"));
                                        pickUpCategory.setIdschedule(pickupjsonArray.optJSONObject(j).optString("idschedule"));
                                        pickUpCategory.setStatus(pickupjsonArray.optJSONObject(j).optString("status"));
                                        PickUpCategoryCategories.add(pickUpCategory);
                                        //PickUpCategoryCategories = orderByChronological(PickUpCategoryCategories);
                                        pickupmodel.setPickUpCategoryList(PickUpCategoryCategories);
                                        /*Log.e("inserted", activities[i] + "" + j);
                                        Log.e("inserted", "" + PickUpCategoryCategories.get(j).getLatitude());
                                        Log.e("inserted", "" + PickUpCategoryCategories.get(j).getLongitude());*/
                                    }

                                    pickUpModelArrayList.add(pickupmodel);
                                    Log.e("insertedfulllist of", activities[i]);
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
//                }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return pickUpModelArrayList;
    }

    public void sortArray(ArrayList<String> items) {

        Collections.sort(items, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {

                //   return lhs.getDate().compareTo(rhs.getDate());

                try {
                    return AppConstants.sor_sdf.parse(lhs).compareTo(AppConstants.sor_sdf.parse(rhs));
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

                return 0;
            }

            @Override
            public boolean equals(Object object) {
                return false;
            }
        });
    }

    public void storeBusinessLocations(Context context, JSONObject jsonObject) {

        if (jsonObject != null) {
            try {
                JSONArray bussinessLocations = jsonObject.optJSONArray("business_location");

                if (bussinessLocations != null && bussinessLocations.length() > 0) {

                    SharedPref.getInstance().setSharedValue(context, business_locations, bussinessLocations.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<ChatDo> getChatHistory(Context context, JSONArray chatHistory) {

        ArrayList<ChatDo> chatDoArrayList = new ArrayList<>();

        for (int i = 0; i < chatHistory.length(); i++) {

            JSONObject chat = chatHistory.optJSONObject(i);
            ChatDo chatDo = new ChatDo();
            chatDo.setIdchat(chat.optString("idchat"));

            if (chat.optString("iduser").equals(SharedPref.getInstance().getStringVlue(context, userId))) {
                chatDo.setIsLeft(true);
            } else {
                chatDo.setIsLeft(false);
            }
            chatDo.setIduser(chat.optString("iduser"));
            chatDo.setUsername(chat.optString("firstname") + " " + chat.optString("lastname"));
            chatDo.setIdschedule(chat.optString("idschedule"));
            Log.e("message", "" + chat.optString("message"));
            chatDo.setMessage(chat.optString("message"));
            chatDo.setCreated_on(chat.optString("local_time"));
            chatDoArrayList.add(chatDo);
        }

        return chatDoArrayList;
    }

    private ArrayList<PickUpCategory> orderByChronological(ArrayList<PickUpCategory> pickUpCategories) {

        if (pickUpCategories != null && pickUpCategories.size() > 0) {

            Collections.sort(pickUpCategories, new Comparator<PickUpCategory>() {
                @Override
                public int compare(PickUpCategory lhs, PickUpCategory rhs) {
                    try {
                        return lhs.getEventdate().compareTo(rhs.getEventdate());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return 0;
                }

                @Override
                public boolean equals(Object object) {
                    return false;
                }
            });
        }


        return pickUpCategories;

    }

    public synchronized ArrayList<NotifyModel> getNotificationList(Context context, JSONObject jsonObject, AppContactService appContactService) throws JSONException {

        ArrayList<NotifyModel> notifyModelsArrayList = new ArrayList<>(2);
        ArrayList<NotifyModel> notifyModelsArrayList2 = new ArrayList<>(2);
        if (jsonObject != null) {
            JSONArray getNotification = jsonObject.optJSONArray(KEY_UPCOMING);
            JSONArray getNotificationPast = jsonObject.optJSONArray(KEY_PAST);
            if (getNotification != null && getNotification.length() > 0)
                notifyModelsArrayList = orderNotificationItems(getNotification(context, getNotification, "UPCOMING", true, appContactService));
            if (getNotificationPast != null && getNotificationPast.length() > 0)
                notifyModelsArrayList2 = getNotification(context, getNotificationPast, "PAST", false, appContactService);
            notifyModelsArrayList.addAll(orderNotificationItems(notifyModelsArrayList2));
        }

        return notifyModelsArrayList;
    }

    private synchronized ArrayList<NotifyModel> getNotification(Context context, JSONArray getNotification, String title, boolean isUpComing,
                                                                AppContactService appContactService) throws JSONException {
        ArrayList<NotifyModel> notifyModels = new ArrayList<>();
        ApplozicChat applozicChat = new ApplozicChat((Activity) context);

        for (int i = 0; i < getNotification.length(); i++) {
            NotifyModel notifyModel = new NotifyModel();
            notifyModel.setNotificationTypeTitle(title);
            notifyModel.setIsUpComing(isUpComing);
            notifyModel.setNotificationType(AppConstants.CHILD);
            notifyModel.setNotificationTypeTitle(title);
            JSONObject favObj = getNotification.optJSONObject(i);
            notifyModel.setImage(AppConstants.getiamgebaseurl()+favObj.getString("image"));

            //  notifyModel.setImage(AppConstants.getiamgebaseurl() + getNotification.getJSONObject(i).getJSONArray(("image")).getJSONObject(0).getString("image"));

            if (favObj.optString("message").endsWith("Accepted") && context != null) {
                notifyModel.setColor(ContextCompat.getColor(context, R.color.green));

            } else if (favObj.optString("message").endsWith("Declined") && context != null) {
                notifyModel.setColor(ContextCompat.getColor(context, R.color.red));

            } else if (context != null) {
                notifyModel.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
            notifyModel.setIdUser(favObj.optString("iduser"));
            notifyModel.setIdschedule(favObj.optString("idschedule"));
            notifyModel.setActivity(favObj.optString("activity"));
            notifyModel.setDate(favObj.optString("date"));
            notifyModel.setAbandoned_count(favObj.optInt("abandoned_count"));
            notifyModel.setStart_time(favObj.optString("start_time"));
            //notifyModel.setMessage(favObj.optString("message"));
            if (!favObj.optString("group_id").equalsIgnoreCase("null")) {
                if (favObj.optString("status").equals(KEY_ACCEPTED) || favObj.optString("status").equals(KEY_CREATED)) {
                    try {
                        if (isUpComing)
                            notifyModel.setMessage(applozicChat.getLastMessageFromGroup(favObj.optInt("group_id"), appContactService));
                        if (TextUtils.isEmpty(notifyModel.getMessage())) {
                            notifyModel.setMessage(favObj.optString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // notifyModel.setMessage(favObj.optString("message"));
                    }
                } else {
                    notifyModel.setMessage(favObj.optString("message"));
                }
            } else {
                notifyModel.setMessage(favObj.optString("message"));
            }


            notifyModel.setStatus(favObj.optString("status"));


            notifyModel.setGroup_id(favObj.optString("group_id"));

            //Log.e("group_id", favObj.optString("group_id"));

            String gameDescription;
            try {
                gameDescription = favObj.optString("activity") + " : " + Utility.dateFormatWithFull(favObj.optString("date")) + " @ " + Utility.timeFormatChanage(favObj.optString("start_time"));
            } catch (Exception e) {
                gameDescription = favObj.optString("activity");
                e.printStackTrace();
            }
            notifyModel.setGameDescription(gameDescription);
            notifyModel.setIsActive(favObj.optString("sstatus"));
            notifyModel.setRequest_count(favObj.optInt("requested_count"));

            if (!favObj.optString("group_id").equalsIgnoreCase("null") && !favObj.optString("group_id").equalsIgnoreCase("0") && !TextUtils.isEmpty(favObj.optString("group_id"))) {
                notifyModel.setMsg_count(applozicChat.getUnreadMsgCountFromGroup(favObj.optInt("group_id")));

            }


            /* if (isValidGroupId("group_id") ) {
                if (favObj.optString("status").equals(KEY_ACCEPTED) || favObj.optString("status").equals(KEY_CREATED)) {
                    notifyModel.setMsg_count(applozicChat.getUnreadMsgCountFromGroup(favObj.optInt("group_id")));
                }
            }
            */
            notifyModel.setAttending_count(favObj.optInt("accepted_count"));
            notifyModel.setAccepted_id(favObj.optString("accepted_id"));
            notifyModel.setType(favObj.optString("type"));
            if (favObj.optInt("isread") == 0) {
                notifyModel.setIsRead(true);
            }

            //  Log.d(TAG, "before created At :" + i + " " + favObj.optString("created_on"));

            if (isValidGroupId(favObj.optString("group_id"))) {
                notifyModel.setUpdatedAt(getUpdatedTime(context, favObj.optString("group_id"), favObj.optString("created_on")));
            } else {
                notifyModel.setUpdatedAt(favObj.optString("created_on"));
            }

            // Log.d(TAG, "after created At :" + i + " " + notifyModel.getUpdatedAt());
            notifyModels.add(notifyModel);
        }

        return notifyModels;
    }


    private boolean isValidGroupId(String groupId) {
        if (!groupId.equalsIgnoreCase("null") && !groupId.equalsIgnoreCase("0") && !TextUtils.isEmpty(groupId)) {
            return true;
        }
        return false;
    }

    private String getUpdatedTime(Context context, String groupId, String createdOn) {
        try {
            List<Message> messageList = new MessageDatabaseService(context).getLatestMessageByChannelKey(Integer.parseInt(groupId));
            if (messageList.size() > 0) {
                // Log.d(TAG, "message :" + messageList.get(0).getMessage());
                return Utility.getDateFromMilliSeconds(messageList.get(0).getCreatedAtTime());
            } else {
                return createdOn;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return createdOn;
        }
    }

    public static String getFormattedDate(Long timestamp) {
        // boolean sameDay = isSameDay(timestamp);
        Date date = new Date(timestamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd MMM");
        return simpleDateFormat.format(date);
    }


    private ArrayList<NotifyModel> orderNotificationItems(ArrayList<NotifyModel> arrayList) {

        if (arrayList != null && arrayList.size() > 0) {
            Collections.sort(arrayList, Collections.reverseOrder(new Comparator<NotifyModel>() {
                @Override
                public int compare(NotifyModel lhs, NotifyModel rhs) {
                    try {
                        return lhs.getUpdatedAt().compareTo(rhs.getUpdatedAt());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            }));
        }

        return arrayList;

    }
}
