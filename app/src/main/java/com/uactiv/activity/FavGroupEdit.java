package com.uactiv.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexzh.circleimageview.CircleImageView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnSwipeListener;
import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.application.UActiveApplication;
import com.uactiv.controller.ResponseListener;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.FavouriteModel;
import com.uactiv.model.SkillDo;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FavGroupEdit extends Activity implements OnClickListener, AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener {

    public static FavGroupEditAdapter mAdapter;
    public static ArrayList<FavouriteModel> favGroupItems;
    public static SwipeMenuListView lvGroupMembers;
    TextView tvGroupName;
    CustomButton tvGroupAdd;
    Intent i = null;
    String menuPositon = null;
    GifImageView progressWheel = null;
    String groupList_Respones = null;
    ArrayList<BuddyModel> buddyModelArrayList = new ArrayList<>();
    EditText editGroupName;
    private ImageView imgBack, imgGroupEdit;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_group_edit);
        i = getIntent();
        favGroupItems = new ArrayList<FavouriteModel>();
        tvGroupAdd = (CustomButton) findViewById(R.id.tvGroupAdd);
        tvGroupName = (TextView) findViewById(R.id.tvGroupName);
        imgGroupEdit = (ImageView) findViewById(R.id.imgGroupEdit);
        lvGroupMembers = (SwipeMenuListView) findViewById(R.id.lvGroupMembers);
        lvGroupMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (buddyModelArrayList.size() > 0) {
                    Intent intent = new Intent(FavGroupEdit.this, BuddyUpDetailsActivity.class);
                    intent.putExtra("badgetype", buddyModelArrayList.get(position).getImage() + "");
                    intent.putExtra("isfav", "1");
                    intent.putExtra("position", position);
                    intent.putExtra("userid", buddyModelArrayList.get(position).getUserid() + "");
                    intent.putExtra("isFromFav", true);
                    BuddyModel buddydetails = buddyModelArrayList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(serialKeyBuddy, buddydetails);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        imgBack = (ImageView) findViewById(R.id.imgBack);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(FavGroupEdit.this, progressWheel);
        //onclick
        imgGroupEdit.setOnClickListener(this);
        tvGroupAdd.setOnClickListener(this);
        imgBack.setOnClickListener(this);


        if (i != null) {
            if (!TextUtils.isEmpty(i.getStringExtra("groupname"))) {
                tvGroupName.setText(i.getStringExtra("groupname"));
            }
        }


        if (i != null) {
            getGroupMembersList();
        }

        init();
    }


    private void getGroupMembersList() {

        if (Utility.isConnectingToInternet(FavGroupEdit.this)) {

            try {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                Map<String, String> param = new HashMap<>();
                param.put("idgroup", i.getStringExtra("groupid"));
                RequestHandler.getInstance().stringRequestVolley(FavGroupEdit.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(FavGroupEdit.this,isStaging)) + groupmemeberlist, param, this, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(FavGroupEdit.this);
        }

    }


    private void init() {
        mAdapter = new FavGroupEditAdapter(FavGroupEdit.this, favGroupItems);
        lvGroupMembers.setAdapter(mAdapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(FavGroupEdit.this);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.list_delete_icon);
                menu.addMenuItem(deleteItem);
            }
        };

        lvGroupMembers.setMenuCreator(creator);
        lvGroupMembers.setCloseInterpolator(new BounceInterpolator());
        lvGroupMembers.setOnSwipeListener(new OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });

        lvGroupMembers.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                if (Utility.isConnectingToInternet(FavGroupEdit.this)) {

                    try {

                        menuPositon = "" + position;
                        Log.e("TAG", "posimennu :" + menuPositon);
                        Map<String, String> param = new HashMap<>();
                        Log.e("TAG", ":" + favGroupItems.get(position).getId());
                        param.put("members", favGroupItems.get(position).getId());
                        param.put("idgroup", i.getStringExtra("groupid"));
                        progressWheel.setVisibility(View.VISIBLE);
                        progressWheel.startAnimation();
                        RequestHandler.getInstance().stringRequestVolley(FavGroupEdit.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(FavGroupEdit.this,isStaging)) + removefromgroup, param, FavGroupEdit.this, 1);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    Utility.showInternetError(FavGroupEdit.this);
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
    public void successResponse(String successResponse, int flag) {

        /** flag == 0 membergrouplist API response
         * flag == 1 removefromgroup API response
         * flag == 2 EditGroupnameAPI API response
         *
         */

        JSONObject jsonObject = null;

        try {

            if (!TextUtils.isEmpty(successResponse)) {
                jsonObject = new JSONObject(successResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            progressWheel.stopAnimation();
            progressWheel.setVisibility(View.GONE);
        }


        switch (flag) {

            case 0:

                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        try {
                            JSONArray jsonArray = jsonObject.getJSONArray(KEY_DETAIL);

                            if (jsonArray != null && jsonArray.length() > 0) {

                                favGroupItems = new ArrayList<>();

                                buddyModelArrayList = storeBuddyList(jsonArray);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject member = jsonArray.getJSONObject(i);
                                    favGroupItems.add(new FavouriteModel(member.optString("iduser"), member.optString("firstname") + " " + member.optString("lastname"), member.optString("image")));
                                }

                                mAdapter = new FavGroupEditAdapter(FavGroupEdit.this, favGroupItems);
                                lvGroupMembers.setAdapter(mAdapter);
                                progressWheel.stopAnimation();
                                progressWheel.setVisibility(View.GONE);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                        Utility.showToastMessage(FavGroupEdit.this, jsonObject.optString(KEY_MSG));
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }

                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                break;

            case 1:

                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                        if (!TextUtils.isEmpty(menuPositon)) {

                            Log.e("TAG", ":" + menuPositon);
                            mAdapter.remove(Integer.parseInt(menuPositon));

                        }

                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    } else {

                        Utility.showToastMessage(FavGroupEdit.this, jsonObject.optString(KEY_MSG));

                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);
                break;

            case 2:


                if (jsonObject != null) {

                    if (jsonObject.optString(resultcheck).equalsIgnoreCase(KEY_TRUE)) {
                        tvGroupName.setText(editGroupName.getText().toString());
                        groupList_Respones = jsonObject.toString();
                    } else {
                        Utility.showToastMessage(FavGroupEdit.this, jsonObject.optString(KEY_MSG));
                    }
                }
                progressWheel.stopAnimation();
                progressWheel.setVisibility(View.GONE);

                break;

            default:
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
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
    }

    public void setchangedItems(ArrayList<FavouriteModel> temparray) {

        if (mAdapter != null) {
            favGroupItems.clear();
            favGroupItems = temparray;
            mAdapter = new FavGroupEditAdapter(FavGroupEdit.this, favGroupItems);
            lvGroupMembers.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            onBackPressed();
        } else if (v == tvGroupAdd) {
            Utility.setEventTracking(FavGroupEdit.this,"Add member to group screen in favourites","Favourites members  on Add member to group screen in favorites");

            Intent intent = new Intent(FavGroupEdit.this, FavouriteAddMembers.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("groupmembers", favGroupItems);
            intent.putExtras(bundle);
            intent.putExtra("groupid", i.getStringExtra("groupid"));
            startActivity(intent);
        } else if (v == imgGroupEdit) {


            TextView tvOk, tvCancel;


            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_groupname_edit);
            dialog.setCancelable(false);
            dialog.show();

            //views
            tvOk = (TextView) dialog.findViewById(R.id.tvOk);
            tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
            editGroupName = (EditText) dialog.findViewById(R.id.editGroupName);
            editGroupName.setText("" + tvGroupName.getText().toString().trim());
            //set font type

            tvOk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvGroupName.length() > 0) {

                        if (!editGroupName.getText().toString().trim().equals(tvGroupName.getText().toString().trim())) {
                            AppConstants.hideViewKeyBoard(FavGroupEdit.this, editGroupName);
                            editGroupNameAPI(editGroupName.getText().toString().trim());
                        } else {
                            Toast.makeText(FavGroupEdit.this, "Group name should not be same!", Toast.LENGTH_SHORT).show();
                        }

                    }
                    dialog.dismiss();
                }
            });

            tvCancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    AppConstants.hideViewKeyBoard(FavGroupEdit.this, editGroupName);
                }
            });
        }
    }

    private void editGroupNameAPI(String groupname) {

        if (Utility.isConnectingToInternet(FavGroupEdit.this)) {

            try {
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(FavGroupEdit.this, userId));
                param.put("idgroup", i.getStringExtra("groupid"));
                param.put("groupname", groupname);
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                RequestHandler.getInstance().stringRequestVolley(FavGroupEdit.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(FavGroupEdit.this,isStaging)) + editgroup, param, FavGroupEdit.this, 2); // Edit Group name API

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            Utility.showInternetError(FavGroupEdit.this);
        }

    }

    @Override
    public void onBackPressed() {
        // code here to show dialog

        Log.e("groupList_Respones", ":" + groupList_Respones);

        if (groupList_Respones != null) {

            Log.e("onBackPressed", "onBackPressed");
            Intent output = new Intent();
            output.putExtra("grouplist", groupList_Respones.toString());
            setResult(RESULT_OK, output);
        }
        super.onBackPressed();  // optional depending on your needs
    }

    @Override
    protected void onResume() {
        getGroupMembersList();
        mAdapter.notifyDataSetChanged();
        super.onResume();


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
                    buddyModel.setImage(buddyObj.optString("image"));
                    buddyModel.setAge(buddyObj.optString("age"));
                    buddyModel.setAwayDistance(buddyObj.optString("distance"));
                    buddyModel.setIsfav("1");
                    buddyModel.setBadge(buddyObj.optString("badge"));
                    buddyModel.setAbout_yourself(buddyObj.optString("about_yourself"));
                    buddyModel.setIsreceivebuddyrequest(buddyObj.optString("isreceive_request"));
                    buddyModel.setUser_type(buddyObj.optInt("user_type"));
                    buddyModel.setRating(buddyObj.optInt("rating"));
                    buddyModel.setRating_count(buddyObj.optInt("rated_count"));
                    buddyModel.setEmail(buddyObj.optString("email"));
                    buddyModel.setPhone_no(buddyObj.optString("phone_no"));
                    buddyModel.setAddress(buddyObj.optString("address"));


                    if (skillArray != null) {

                        ArrayList<SkillDo> skilltemp = new ArrayList<>();

                        for (int j = 0; j < skillArray.length(); j++) {
                            skillDo = new SkillDo();
                            skillDo.setActivty(skillArray.getJSONObject(j).optString("activity"));
                            skillDo.setLevel(skillArray.getJSONObject(j).optString("level"));
                            skillDo.setActivity_type(skillArray.getJSONObject(j).optString("type"));
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

    //listview adapter
    public class FavGroupEditAdapter extends BaseAdapter {
        private List<FavouriteModel> favItems = null;
        private ArrayList<FavouriteModel> favAllItems;

        private Context mContext = null;

        FavGroupEditAdapter(Context context, List<FavouriteModel> favItems) {
            this.mContext = context;
            this.favItems = favItems;
            this.favAllItems = new ArrayList<FavouriteModel>();
            this.favAllItems.addAll(favItems);
        }

        public void notifyset(ArrayList<FavouriteModel> list) {
            favItems = list;
            favGroupItems = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return favItems.size();
        }

        @Override
        public FavouriteModel getItem(int position) {
            return favItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(int position) {
            Log.e("TAG", "adapter :" + menuPositon);
            if (favAllItems.size() > position) {
                Log.e("TAG", ":if adapter" + menuPositon);
                favAllItems.remove(position);
                favGroupItems.remove(position);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_favourite, parent, false))
                    : (ViewHolder) convertView.getTag();

           /* ViewHolder viewHolder = null;
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflator.inflate(R.layout.custom_favourite, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }*/

            viewHolder.tvFavName.setText(favItems.get(position).getName());

            if (Utility.isNullCheck(favItems.get(position).getImage())) {
                Utility.setImageUniversalLoader(UActiveApplication.mContext, favItems.get(position).getImage(), viewHolder.imgFav);
            }


            return convertView;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            favItems.clear();
            if (charText.length() == 0) {
                favItems.addAll(favAllItems);
            } else {
                for (FavouriteModel wp : favAllItems) {
                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        favItems.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

        class ViewHolder {
            TextView tvFavName;
            CircleImageView imgFav;

            ViewHolder(View view) {
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFav = ((CircleImageView) view.findViewById(R.id.imgFav));
                view.setTag(this);
            }
        }
    }
}
