package com.uactiv.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.controller.ResponseListener;
import com.uactiv.model.GroupModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FavGroup extends Activity implements OnClickListener, ResponseListener, AppConstants.SharedConstants, AppConstants.urlConstants {

    public static FavGroupAdapter mAdapter;
    CustomButton tvGroupNew;
    ArrayList<GroupModel> favGroupItems;
    SwipeMenuListView lvGroups;
    GifImageView progressWheel = null;
    //String menuPositon;
    int FAV_GROUP_INTENT_REQUEST_CODE = 2;
    private ImageView imgBack;
    private ImageView searchClose = null;
    private CustomEditText searchEditText = null;
    private LinearLayout mEmptyViewGroups = null;
    private RelativeLayout mEmptyViewNoInternet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_group);
        mEmptyViewGroups = (LinearLayout) findViewById(R.id.empty_view_no_groups);
        mEmptyViewNoInternet = (RelativeLayout) findViewById(R.id.empty_view_no_internet);
        favGroupItems = new ArrayList<GroupModel>();
        tvGroupNew = (CustomButton) findViewById(R.id.tvGroupNew);
        lvGroups = (SwipeMenuListView) findViewById(R.id.lvGroups);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(FavGroup.this, progressWheel);
        searchClose = (ImageView) findViewById(R.id.searchClose);
        searchClose.setVisibility(View.GONE);
        searchEditText = (CustomEditText) findViewById(R.id.editSearch);


        //onclick
        tvGroupNew.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        setListValues();

        init();

        getGroupList();

        searchClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                searchEditText.setText("");
                getGroupList();
            }
        });


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (searchEditText.length() == 0) {
                    searchEditText.setCursorVisible(true);
                    Utility.setEventTracking(FavGroup.this, "Favourites screen", "Search bar on Favourites screen");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mAdapter != null) {
                    mAdapter.filter(searchEditText.getText().toString().trim());
                }


                if (searchEditText.length() > 0) {
                    searchClose.setVisibility(View.VISIBLE);
                } else {
                    searchClose.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void showInternetView() {
        mEmptyViewGroups.setVisibility(View.GONE);
        mEmptyViewNoInternet.setVisibility(View.VISIBLE);
    }

    private void showGroupView() {
        mEmptyViewGroups.setVisibility(View.VISIBLE);
        mEmptyViewNoInternet.setVisibility(View.GONE);
    }


    private void disableEmptyView() {
        mEmptyViewGroups.setVisibility(View.GONE);
        mEmptyViewNoInternet.setVisibility(View.GONE);
    }

    private void getGroupList() {

        if (Utility.isConnectingToInternet(FavGroup.this)) {
            disableEmptyView();
            Map<String, String> param = new HashMap<>();
            param.put("iduser", SharedPref.getInstance().getStringVlue(FavGroup.this, userId));
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.startAnimation();
            RequestHandler.getInstance().stringRequestVolley(FavGroup.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(FavGroup.this, isStaging)) + grouplist, param, FavGroup.this, 0);

        } else {
            showInternetView();
            //Utility.showInternetError(FavGroup.this);
        }

    }

    private void setListValues() {
        favGroupItems.clear();
        mAdapter = new FavGroupAdapter(favGroupItems);
        lvGroups.setAdapter(mAdapter);
    }

    private void init() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(FavGroup.this);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.list_delete_icon);
                menu.addMenuItem(deleteItem);
            }
        };

        lvGroups.setMenuCreator(creator);
        lvGroups.setCloseInterpolator(new BounceInterpolator());
        lvGroups.setOnSwipeListener(new OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });

        //listview onitem click
        lvGroups.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Utility.setEventTracking(FavGroup.this, "Go to groups screen", " New Button on Go to Groups screen");
                Intent intent = new Intent(FavGroup.this, FavGroupEdit.class);
                intent.putExtra("groupname", favGroupItems.get(position).getGroupname());
                intent.putExtra("groupid", favGroupItems.get(position).getGroupid());
                startActivityForResult(intent, FAV_GROUP_INTENT_REQUEST_CODE);
            }
        });

        lvGroups.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        try {
                            if (Utility.isConnectingToInternet(FavGroup.this)) {

                                try {
                                    Map<String, String> param = new HashMap<>();
                                    param.put("iduser", SharedPref.getInstance().getStringVlue(FavGroup.this, userId));
                                    param.put("idgroup", favGroupItems.get(position).getGroupid());
                                    progressWheel.setVisibility(View.VISIBLE);
                                    progressWheel.startAnimation();
                                    RequestHandler.getInstance().stringRequestVolley(FavGroup.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(FavGroup.this, isStaging)) + removegroup, param, FavGroup.this, 2);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                Utility.showInternetError(FavGroup.this);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FAV_GROUP_INTENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            if (requestCode == FAV_GROUP_INTENT_REQUEST_CODE && data.getStringExtra("grouplist") != null) {

                if (Utility.isNullCheck(data.getStringExtra("grouplist"))) {
                    this.successResponse(data.getStringExtra("grouplist"), 0);
                }
            }

        }
    }

    @Override
    public void successResponse(String successResponse, int flag) {

        /** flag == 0 grouplsit API response
         *  flag == 1 creategroup API response
         *  flag == 2 deletegroup API response
         */
        JSONObject jsonObject = null;

        try {

            jsonObject = new JSONObject(successResponse);

        } catch (Exception e) {

            e.printStackTrace();
        }


        switch (flag) {

            case 0:

                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        //   Utility.setEventTracking(this, AppConstants.EVENT_TRACKING_ID_CREATEGROUP);
                        JSONArray getgrouplist = jsonObject.optJSONArray(KEY_DETAIL);

                        if (getgrouplist != null && getgrouplist.length() > 0) {

                            favGroupItems = new ArrayList<>();

                            for (int i = 0; i < getgrouplist.length(); i++) {

                                try {
                                    JSONObject groupObj = getgrouplist.getJSONObject(i);

                                    favGroupItems.add(new GroupModel(R.drawable.default_user_icon, groupObj.optString("idgroup"), groupObj.optString("groupname"), groupObj.optString("status"), groupObj.optString("created_on")));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                            mAdapter = new FavGroupAdapter(favGroupItems);
                            lvGroups.setAdapter(mAdapter);
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);

                        } else {
                            favGroupItems = new ArrayList<>();
                            mAdapter = new FavGroupAdapter(favGroupItems);
                            lvGroups.setAdapter(mAdapter);
                        }

                    } else {
                        Utility.showToastMessage(FavGroup.this, jsonObject.optString(KEY_MSG));
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);

                    }
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);

                if (favGroupItems == null || favGroupItems.size() == 0) {
                    showGroupView();
                } else {
                    disableEmptyView();
                }
                break;

            case 1:

                if (jsonObject != null) {

                    {

                        if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                            try {
                          //      JSONArray detdetails = jsonObject.getJSONArray(KEY_DETAIL);
                         //       favGroupItems.clear();

                       /*         for (int i = 0; i < detdetails.length(); i++) {
                                    JSONObject chid = detdetails.getJSONObject(i);
                                    favGroupItems.add(new GroupModel(R.drawable.default_user_icon, chid.optString("idgroup"), chid.optString("groupname"), chid.optString("status"), chid.optString("created_on")));
                                }

                                mAdapter = new FavGroupAdapter(favGroupItems);
                                lvGroups.setAdapter(mAdapter);*/
                              //  mAdapter.notifyDataSetChanged();
                                progressWheel.stopAnimation();
                                progressWheel.setVisibility(View.GONE);
                                //Utility.showToastMessage(FavGroup.this, detdetails.getJSONObject(detdetails.length() - 1).optString("groupname") + getResources().getString(R.string.group_created_msg));
                                Intent i = new Intent(FavGroup.this, FavouriteAddMembers.class);
                             //   i.putExtra("groupid", detdetails.getJSONObject(detdetails.length() - 1).optString("idgroup"));
                                startActivity(i);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            Utility.showToastMessage(FavGroup.this, jsonObject.optString(KEY_MSG));
                        }


                    }

                }

                break;

            case 2:
                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        //mAdapter.remove(Integer.parseInt(menuPositon));
                        this.successResponse(jsonObject.toString(), 0);
                    } else {
                        Utility.showToastMessage(FavGroup.this, "" + jsonObject.optString(KEY_MSG));
                    }
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);
                }
                if (favGroupItems == null || favGroupItems.size() == 0) {
                    showGroupView();
                } else {
                    disableEmptyView();
                }
                break;

        }


    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {


    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                onBackPressed();
                Utility.setEventTracking(FavGroup.this, "Go to groups screen", "Back Arrow on Go to Groups screen");
                break;
            case R.id.tvGroupNew:
                CustomButton tvOk, tvCancel;
                final EditText editGroupName;
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_groupname_edit);
                dialog.setCancelable(false);
                dialog.show();

                AppConstants.showKeyBoard(this);


                //views
                tvOk = (CustomButton) dialog.findViewById(R.id.tvOk);
                tvCancel = (CustomButton) dialog.findViewById(R.id.tvCancel);
                editGroupName = (EditText) dialog.findViewById(R.id.editGroupName);


                tvOk.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editGroupName.length() > 0) {
                            Utility.setEventTracking(FavGroup.this, "New Group creation", "OK on New Group creation screen");
                            if (Utility.isConnectingToInternet(FavGroup.this)) {
                                disableEmptyView();
                                AppConstants.hideViewKeyBoard(FavGroup.this, editGroupName);
                                progressWheel.setVisibility(View.VISIBLE);
                                progressWheel.startAnimation();
                                Map<String, String> param = new HashMap<String, String>();
                                param.put("groupname", editGroupName.getText().toString().trim());
                                param.put("iduser", SharedPref.getInstance().getStringVlue(FavGroup.this, userId));
                                RequestHandler.getInstance().stringRequestVolley(FavGroup.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(FavGroup.this, isStaging)) + creategroup, param, FavGroup.this, 1);
                                dialog.dismiss();
                            } else {
                                Utility.showInternetError(FavGroup.this);
                            }
                        } else {
                            Utility.showToastMessage(FavGroup.this, "Please enter group name");
                        }


                    }
                });

                tvCancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Utility.setEventTracking(FavGroup.this, "New Group creation", "cancel on New Group creation screen");

                        //AppConstants.hideKeyBoard(FavGroup.this);
                    }
                });
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }

    //listview adapter
    class FavGroupAdapter extends BaseAdapter {
        private List<GroupModel> favItems = null;
        private ArrayList<GroupModel> favAllItems;

        FavGroupAdapter(List<GroupModel> favItems) {
            this.favItems = favItems;
            this.favAllItems = new ArrayList<GroupModel>();
            this.favAllItems.addAll(favItems);
        }

        public void checkItemsetChanged(List<GroupModel> favItems) {
            this.favAllItems.addAll(favAllItems);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return favItems.size();
        }

        @Override
        public GroupModel getItem(int position) {
            return favItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(int position) {
            if (favAllItems.size() > position) {
                favAllItems.remove(position);
                favGroupItems.remove(position);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_fav_group, parent, false))
                    : (ViewHolder) convertView.getTag();

            viewHolder.tvFavName.setText(favItems.get(position).getGroupname());
            viewHolder.imgFav.setImageResource(favItems.get(position).getImage());

            return convertView;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            favItems.clear();
            if (charText.length() == 0) {
                favItems.addAll(favAllItems);
            } else {
                for (GroupModel wp : favAllItems) {
                    if (wp.getGroupname().toLowerCase(Locale.getDefault()).contains(charText)) {
                        favItems.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

        class ViewHolder {
            TextView tvFavName;
            ImageView imgFav;

            ViewHolder(View view) {
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFav = ((ImageView) view.findViewById(R.id.imgFav));
                view.setTag(this);
            }
        }
    }
}
