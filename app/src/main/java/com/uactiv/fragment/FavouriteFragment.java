package com.uactiv.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.activity.BuddyUpDetailsActivity;
import com.uactiv.activity.FavGroup;
import com.uactiv.controller.ResponseListener;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.SkillDo;
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

public class FavouriteFragment extends Fragment implements OnClickListener, AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener, TextWatcher {

    public String fav_position;
    FavouriteAdapter mAdapter;
    // ArrayList<BuddyModel> favItem;
    GifImageView progressWheel = null;
    ArrayList<BuddyModel> buddyModelArrayList = new ArrayList<>();
    ImageView searchClose = null;
    LinearLayout mEmptyViewFav = null;
    RelativeLayout mEmptyViewNoInternet = null;
    private CustomEditText editSearch;
    private CustomButton tvGroup;
    private SwipeMenuListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_FAVOURITES);
        View view = inflater.inflate(R.layout.favourite, container, false);
        mEmptyViewFav = (LinearLayout) view.findViewById(R.id.empty_view_no_fav);
        mEmptyViewNoInternet = (RelativeLayout) view.findViewById(R.id.empty_view_no_internet);
        listView = (SwipeMenuListView) view.findViewById(R.id.favListView);
        progressWheel = (GifImageView) view.findViewById(R.id.gifLoader);
        Utility.showProgressDialog(getActivity(), progressWheel);
        searchClose = (ImageView) view.findViewById(R.id.searchClose);
        searchClose.setVisibility(View.GONE);
        tvGroup = (CustomButton) view.findViewById(R.id.tvGroup);
        tvGroup.setOnClickListener(this);
        editSearch = (CustomEditText) view.findViewById(R.id.editSearch);
        getFavouriteList();
        editSearch.addTextChangedListener(this);
        Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_FAVOURITES);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                BuddyModel data = (BuddyModel) parent.getItemAtPosition(position);

                if (buddyModelArrayList.size() > 0) {

                    Log.d("position", "" + position);
                    Log.d("getUserid", "" + data.getUserid());
                    Intent intent = new Intent(getActivity(), BuddyUpDetailsActivity.class);
                    intent.putExtra("badgetype", data.getImage() + "");
                    intent.putExtra("isfav", "1");
                    intent.putExtra("position", position);
                    intent.putExtra("userid", data.getUserid() + "");
                    intent.putExtra("isFromFav", true);
                    intent.putExtra("ismyFav", "fev");
                    intent.putExtra("fevscreen", "fevscreen");
                    //BuddyModel buddydetails = buddyModelArrayList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(serialKeyBuddy, data);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
        searchClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    mAdapter.filter("");
                }
                editSearch.setText("");
            }
        });
        return view;
    }

    private void getFavouriteList() {

        if (Utility.isConnectingToInternet(getActivity())) {
            disableEmptyView();
            try {
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                param.put("page", "1");

                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();

                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + favouriteslist, param, this, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showInternetView();
            //   Utility.showInternetError(getActivity());
        }

    }


    private void showInternetView() {
        mEmptyViewFav.setVisibility(View.GONE);
        mEmptyViewNoInternet.setVisibility(View.VISIBLE);
    }

    private void showFavView() {
        mEmptyViewFav.setVisibility(View.VISIBLE);
        mEmptyViewNoInternet.setVisibility(View.GONE);
    }


    private void disableEmptyView() {
        mEmptyViewFav.setVisibility(View.GONE);
        mEmptyViewNoInternet.setVisibility(View.GONE);
    }


    private void reomveFavourite(String favUserID) {

        if (Utility.isConnectingToInternet(getActivity())) {

            try {
                disableEmptyView();
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                param.put("favourites", favUserID);
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + removefavourites, param, this, 1);


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showInternetView();
            //   Utility.showInternetError(getActivity());
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  initializeViews();
        init();
    }

   /* private void initializeViews() {
        favItem = new ArrayList<FavouriteModel>();
        //edit search
    }
*/

    private void init() {


        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.list_delete_icon);
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);
        listView.setCloseInterpolator(new BounceInterpolator());
        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {

            }

            @Override
            public void onSwipeEnd(int position) {

            }
        });

        listView.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                try {
                    fav_position = "" + position;
                    reomveFavourite(buddyModelArrayList.get(position).getUserid());
                } catch (Exception e) {
                    e.printStackTrace();
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
        /** flag == 0 Favouritelist API reponse
         * flag == 1 remove favourites API response
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
                        JSONArray getfav = jsonObject.optJSONArray(KEY_DETAIL);

                        if (getfav != null) {

                            buddyModelArrayList = storeBuddyList(getfav);

                            /*for (int i = 0; i < getfav.length(); i++) {
                                JSONObject favObj = getfav.optJSONObject(i);
                                favItem.add(new FavouriteModel(favObj.optString("iduser"), favObj.optString("firstname") + " " + favObj.optString("lastname"), favObj.optString("image")));
                            }*/
                            mAdapter = new FavouriteAdapter(buddyModelArrayList);
                            listView.setAdapter(mAdapter);
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        }

                    } else {
                        Utility.showToastMessage(getActivity(), jsonObject.optString(KEY_MSG));
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                }

                if (buddyModelArrayList == null || buddyModelArrayList.size() == 0) {
                    showFavView();
                } else {
                    disableEmptyView();
                }
                break;


            case 1:

                if (jsonObject != null) {
                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        Log.e("moorthy", "remove" + Integer.parseInt(fav_position));
                        buddyModelArrayList.remove(Integer.parseInt(fav_position));
                        mAdapter = new FavouriteAdapter(buddyModelArrayList);
                        listView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                        // Utility.showToastMessage(getActivity(), getResources().getString(R.string.fav_removed));

                    } else {
                        Utility.showToastMessage(getActivity(), jsonObject.optString(KEY_MSG));
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                }
                if (buddyModelArrayList == null || buddyModelArrayList.size() == 0) {
                    showFavView();
                } else {
                    disableEmptyView();
                }
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

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Utility.setEventTracking(getActivity(), "Favourite Screen", "Search bar on Favourite Screen");
        if (editSearch.length() > 0) {
            searchClose.setVisibility(View.VISIBLE);
        } else {
            searchClose.setVisibility(View.GONE);
        }
        String text = editSearch.getText().toString().toLowerCase(Locale.getDefault());
        if (mAdapter != null) {
            mAdapter.filter(text);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        if (v == tvGroup) {
            Intent intent = new Intent(getActivity(), FavGroup.class);
            startActivity(intent);
            Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_GROUPSCREEN);
            Utility.setEventTracking(getActivity(), "Favourite Screen", "Go to Group on Favourite Screen");
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
                    //  JSONArray skillArray = buddyObj.getJSONArray("skills");
                    buddyModel = new BuddyModel();
                    buddyModel.setUserid(buddyObj.optString("iduser"));
                    buddyModel.setName(buddyObj.optString("firstname") + " " + buddyObj.optString("lastname"));
                    //buddyModel.setImage(buddyObj.optString("image"));
                    buddyModel.setImage(AppConstants.getiamgebaseurl() + jsonArrayBuddy.getJSONObject(i).getJSONArray(("image")).getJSONObject(0).getString("image"));
                    Log.d("pickupimage", String.valueOf(AppConstants.getiamgebaseurl() + jsonArrayBuddy.getJSONObject(i).getJSONArray(("image")).getJSONObject(0).getString("image")));


                    buddyModel.setAge(buddyObj.optString("age"));
                    buddyModel.setAwayDistance(buddyObj.optString("distance"));
                    buddyModel.setIsfav(buddyObj.optString("fav"));
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

               /*     if (skillArray != null) {

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
                    }*/

                    buddyModelArrayList.add(buddyModel);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }
        return buddyModelArrayList;
    }

    //listview adapter
    class FavouriteAdapter extends BaseAdapter {

        private List<BuddyModel> favItems = null;
        private ArrayList<BuddyModel> favAllItems = new ArrayList<>();

        FavouriteAdapter(List<BuddyModel> favItems) {
            this.favItems = favItems;
            this.favAllItems.addAll(favItems);
            Log.d("favAllItems constrctor", "" + favAllItems.size());
        }

        @Override
        public int getCount() {
            return favItems.size();
        }

        @Override
        public BuddyModel getItem(int position) {
            return favItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(int position) {
            if (favAllItems.size() > position) {
                favAllItems.remove(position);
                favItems.remove(position);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_favourite, parent, false))
                    : (ViewHolder) convertView.getTag();
            viewHolder.tvFavName.setText(favItems.get(position).getName());
            //	viewHolder.imgFav.setImageResource(R.drawable.challenge_text_profile);

            if (Utility.isNullCheck(favItems.get(position).getImage())) {
                Utility.setImageUniversalLoader(getActivity(), favItems.get(position).getImage(), viewHolder.imgFav);
            }
            return convertView;
        }

        public void filter(String charText) {

            Log.d("favAllItems size:", "" + favAllItems.size());

            charText = charText.toLowerCase(Locale.getDefault());
            favItems.clear();
            if (charText.length() == 0) {
                favItems.addAll(favAllItems);
                notifyDataSetChanged();
            } else {
                for (BuddyModel wp : favAllItems) {
                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        favItems.add(wp);
                        notifyDataSetChanged();
                    }
                }
            }

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
