package com.uactiv.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.controller.ResponseListener;
import com.uactiv.model.FavouriteModel;
import com.uactiv.model.InviteGroupModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class InviteGroups extends Activity implements OnClickListener, AppConstants.SharedConstants, ResponseListener, AppConstants.urlConstants {

    InviteGroupAdapter mAdapter;
    ArrayList<InviteGroupModel> favGroupItems;
    RelativeLayout bottomLayout;
    ListView lvGroup;
    ArrayList<String> selectedgroups = new ArrayList<>();
    GifImageView progressWheel = null;
    String activityVal, modeVal;
    int countVal;
    Intent intent = null;
    ArrayList<String> selectedFav = new ArrayList<>();
    CustomTextView tvInvite = null;
    boolean isEdit;
    ArrayList<FavouriteModel> invited_peoples = null;
    ArrayList<String> seleted_GroupId = new ArrayList<>();
    private ImageView imgBack;
    private LinearLayout mEmptyViewGroups = null;
    private RelativeLayout mEmptyViewNoInternet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_groups);
        mEmptyViewGroups = (LinearLayout) findViewById(R.id.empty_view_no_groups);
        mEmptyViewNoInternet = (RelativeLayout) findViewById(R.id.empty_view_no_internet);
        intent = getIntent();
        if (intent != null) {
            activityVal = intent.getExtras().getString(activity);
            countVal = intent.getIntExtra(invitecount, 0);
            modeVal = intent.getStringExtra(modekey);
            isEdit = intent.getBooleanExtra("isEdit", false);
            seleted_GroupId = (ArrayList<String>) getIntent().getSerializableExtra("seleted_GroupId");


            Log.e("seleted_GroupId", "" + seleted_GroupId.size());
            if (isEdit) {
                invited_peoples = (ArrayList<FavouriteModel>) getIntent().getSerializableExtra("invited_peoples");
            }

        }
        favGroupItems = new ArrayList<InviteGroupModel>();
        imgBack = (ImageView) findViewById(R.id.imgBack);
        lvGroup = (ListView) findViewById(R.id.lvGroup);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        bottomLayout.setVisibility(View.GONE);
        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(InviteGroups.this, progressWheel);
        tvInvite = (CustomTextView) findViewById(R.id.tvInvite);

        tvInvite.setOnClickListener(this);
        //onclick
        imgBack.setOnClickListener(this);
        bottomLayout.setOnClickListener(this);

        setListValues();
        init();

        getGroupList();
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

        if (Utility.isConnectingToInternet(InviteGroups.this)) {
            disableEmptyView();
            Map<String, String> param = new HashMap<>();
            param.put("iduser", SharedPref.getInstance().getStringVlue(InviteGroups.this, userId));
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.startAnimation();
            RequestHandler.getInstance().stringRequestVolley(InviteGroups.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(InviteGroups.this,isStaging)) + grouplist, param, InviteGroups.this, 0);

        } else {
            showInternetView();
         //   Utility.showInternetError(InviteGroups.this);
        }

    }

    private void setListValues() {
        favGroupItems.clear();
    }

    private void init() {

        mAdapter = new InviteGroupAdapter(favGroupItems);
        lvGroup.setAdapter(mAdapter);
        lvGroup.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (selectedgroups.equals(favGroupItems.get(position).getGroupid())) {
                    selectedgroups.remove(favGroupItems.get(position).getGroupid());
                } else {
                    selectedgroups.add(favGroupItems.get(position).getGroupid());
                }


            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            finish();
        } else if (v == bottomLayout) {
            finish();
        }

        switch (v.getId()) {
            case R.id.tvInvite:
            /*	Intent output = new Intent();
			//	output.putExtra("FulldataList", favGroupItems);
			//	output.putExtra("SelectedGroups", selectedFav);
				setResult(RESULT_OK, output);*/

                Intent data = new Intent();
                Bundle bundle = new Bundle();

                //	bundle.putSerializable("FulldataList", favGroupItems);
                //bundle.putSerializable("Selec

                //InvitePeopleFav.inviteGroupNotify.getSelectedGroupItems(selectedFav,favGroupItems);

                Log.e("tvInvite", "OnClick");
                finish();
                break;
            default:
                break;
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
                        JSONArray getgrouplist = jsonObject.optJSONArray(KEY_DETAIL);

                        if (getgrouplist != null) {

                            //favGroupItems =getFiteredList(getgrouplist);


                            favGroupItems = getFiteredListTEst(getgrouplist);
                            mAdapter = new InviteGroupAdapter(favGroupItems);
                            lvGroup.setAdapter(mAdapter);
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        }

                    } else {
                        Utility.showToastMessage(InviteGroups.this, jsonObject.optString(KEY_MSG));
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                }

                if (favGroupItems == null || favGroupItems.size() == 0) {
                    showGroupView();
                } else {
                    disableEmptyView();
                }

                break;

            default:
                break;
        }


    }

    private ArrayList<InviteGroupModel> getFiteredList(JSONArray getgrouplist) {


        ArrayList<InviteGroupModel> inviteGroupModels = new ArrayList<>();

        ArrayList<FavouriteModel> selcetedMembers = new ArrayList<>();

        ArrayList<String> selcetedMembersID = null;

        for (int i = 0; i < getgrouplist.length(); i++) {


            try {
                JSONObject groupObj = getgrouplist.getJSONObject(i);
                JSONArray memberArrayList = groupObj.optJSONArray("members"); //Member ArrayList

                if (memberArrayList != null && memberArrayList.length() > 0) {

                    InviteGroupModel inviteGroupModel = new InviteGroupModel();

                    for (int j = 0; j < memberArrayList.length(); j++) {

                        selcetedMembersID = new ArrayList<>();

                        JSONObject memberjsonObject = memberArrayList.optJSONObject(j); //Getting single date for chcek Skills match.

                        String skillfromAPi = memberjsonObject.optString("skills");

                        if (skillfromAPi != null) {

                            String[] array_skill = skillfromAPi.split(",");

                            for (int k = 0; k < array_skill.length; k++) {


                                if (array_skill[k] != null) {

                                    //if(array_skill[k].equals(activityVal)){

                                    inviteGroupModel.setGroupid(groupObj.optString("idgroup"));
                                    inviteGroupModel.setGroupname(groupObj.optString("groupname")); //To Update Groups.
                                    selcetedMembers.add(new FavouriteModel(memberjsonObject.optString("iduser"), memberjsonObject.optString("firstname") + " " + memberjsonObject.optString("lastname"), memberjsonObject.optString("image")));//To get Meber for activity
                                    selcetedMembersID.add(memberjsonObject.optString("iduser"));
                                    Log.e("SelectedMembers", "ID " + selcetedMembersID.toString());

                                    //}
                                }
                            }

                        }

                        inviteGroupModel.setSelectedMemberId(selcetedMembersID);

                    }


                    inviteGroupModel.setSelectedMemberList(selcetedMembers);
                    inviteGroupModels.add(inviteGroupModel);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            //favGroupItems.add(new InviteGroupModel(R.drawable.default_user_icon, groupObj.optString("idgroup"), groupObj.optString("groupname"), groupObj.optString("status"), groupObj.optString("created_on")));
        }

        return inviteGroupModels;
    }

    private ArrayList<InviteGroupModel> getFiteredListTEst(JSONArray getgrouplist) {


        ArrayList<InviteGroupModel> inviteGroupModelArrayList = new ArrayList<>();

        ArrayList<FavouriteModel> selcetedMembers = null;

        ArrayList<String> selcetedMembersID = null;

        for (int i = 0; i < getgrouplist.length(); i++) {

            try {
                JSONObject groupObj = getgrouplist.getJSONObject(i);

                JSONArray memberArrayList = groupObj.optJSONArray("members"); //Member ArrayList

                if (memberArrayList != null && memberArrayList.length() > 0) {

                    InviteGroupModel inviteGroupModel = null;
                    selcetedMembersID = new ArrayList<>();
                    selcetedMembers = new ArrayList<>();

                    for (int j = 0; j < memberArrayList.length(); j++) {

                        inviteGroupModel = new InviteGroupModel();

                        JSONObject memberjsonObject = memberArrayList.optJSONObject(j); //Getting single date for chcek Skills match.
                        inviteGroupModel.setGroupid(groupObj.optString("idgroup"));
                        inviteGroupModel.setGroupname(groupObj.optString("groupname")); //To Update Groups.

                        selcetedMembers.add(new FavouriteModel(memberjsonObject.optString("iduser"), memberjsonObject.optString("firstname") + " " + memberjsonObject.optString("lastname"), memberjsonObject.optString("image")));//To get Meber for activity
                        Log.e("Matched User", "" + memberjsonObject.optString("iduser") + " GroupID " + groupObj.optString("idgroup"));
                        selcetedMembersID.add(memberjsonObject.optString("iduser"));

                        if (isEdit && invited_peoples != null && invited_peoples.size() > 0) {
                            selcetedMembers = removeExsting(selcetedMembers);
                            selcetedMembersID = removeExstingMemberID(selcetedMembersID);
                        }

                        inviteGroupModel.setSelectedMemberList(selcetedMembers);
                        inviteGroupModel.setSelectedMemberId(selcetedMembersID);

                    }

                    if (selcetedMembersID.size() > 0) {
                        inviteGroupModelArrayList.add(inviteGroupModel);
                    }

                    Log.e("Group selectedMembers", "" + selcetedMembersID.toString() + "selcetedMembers Size " + selcetedMembers.size() + " GroupID" + groupObj.optString("idgroup"));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        return inviteGroupModelArrayList;
    }

    private ArrayList<String> removeExstingMemberID(ArrayList<String> added) {

        ArrayList<String> temp_favouriteModels = new ArrayList<>();


        for (int j = 0; j < invited_peoples.size(); j++) {

            for (String id : added) {

                if (id.equals(invited_peoples.get(j).getId())) {
                    temp_favouriteModels.add(id);
                }

            }
            added.removeAll(temp_favouriteModels);


        }

        Log.e("Size", "" + added.size());


        return added;
    }

    /**
     * remove alredy invited user from group list
     *
     * @param added
     * @return
     */


    private ArrayList<FavouriteModel> removeExsting(ArrayList<FavouriteModel> added) {

        ArrayList<FavouriteModel> temp_favouriteModels = new ArrayList<>();


        for (int j = 0; j < invited_peoples.size(); j++) {

            for (FavouriteModel book : added) {
                if (book.getId().equals(invited_peoples.get(j).getId())) {
                    temp_favouriteModels.add(book);
                }
            }
            added.removeAll(temp_favouriteModels);
        }

        temp_favouriteModels = removeFavDuplicate(added);

        return temp_favouriteModels;
    }

    /**
     * remove dupliate entry from arraylist
     *
     * @param favmemberlist
     * @return
     */

    private ArrayList<FavouriteModel> removeFavDuplicate(ArrayList<FavouriteModel> favmemberlist) {

        System.out.println(favmemberlist);

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

        final ArrayList newList = new ArrayList(set);

        System.out.println(set);

        return newList;
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

    //listview adapter
    class InviteGroupAdapter extends BaseAdapter {
        SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
        private List<InviteGroupModel> favItems = null;
        private ArrayList<InviteGroupModel> favAllItems;
        private ArrayList<Integer> itemCount = new ArrayList<Integer>();

        InviteGroupAdapter(List<InviteGroupModel> favItems) {
            this.favItems = favItems;
            this.favAllItems = new ArrayList<InviteGroupModel>();
            this.favAllItems.addAll(favItems);
            itemCount.clear();
        }

        @Override
        public int getCount() {
            return favItems.size();
        }

        @Override
        public InviteGroupModel getItem(int position) {
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
            final ViewHolder viewHolder = convertView == null
                    ? new ViewHolder(convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_fav_group, parent, false)) : (ViewHolder) convertView.getTag();
            viewHolder.tvFavName.setText(favItems.get(position).getGroupname());

            viewHolder.checkGroup.setTag(position);

            if (seleted_GroupId != null && seleted_GroupId.size() > 0) {

                for (String id : seleted_GroupId) {

                    if (id.equals(favItems.get(position).getGroupid())) {
                        mSparseBooleanArray.put((Integer) viewHolder.checkGroup.getTag(), true);
                    }
                }
            }

            viewHolder.checkGroup.setChecked(mSparseBooleanArray.get(position));

            Log.e("Size", ":" + favItems.get(position).getSelectedMemberList().size());

            viewHolder.tv_count.setText("" + favItems.get(position).getSelectedMemberList().size());

            viewHolder.tv_count.setVisibility(View.GONE);

            viewHolder.checkGroup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    checkItems(viewHolder.checkGroup, position, v);


                  /*  if (modeVal.equals("private")) {

                        if (favItems.get(position).getSelectedMemberId().size() <= countVal) {

                            checkItems(viewHolder.checkGroup, position, v);

                        } else {
                            viewHolder.checkGroup.setChecked(false);
                            Toast.makeText(InviteGroups.this, "The group has " + favItems.get(position).getSelectedMemberList().size() + " members, limit is " + countVal + " members. Please change the limit or select members individually !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        checkItems(viewHolder.checkGroup, position, v);

							*//*	if (viewHolder.checkGroup.isChecked()) {

									mSparseBooleanArray.put((Integer) v.getTag(), true);
									selectedFav.add(favItems.get(position).getGroupid());
									InvitePeopleFav.inviteGroupNotify.addSelectedMembers(favItems.get(position).getSelectedMemberList(), favItems.get(position).getSelectedMemberId());

								} else {

									mSparseBooleanArray.put((Integer) v.getTag(), false);
									selectedFav.remove(favItems.get(position).getGroupid());
									InvitePeopleFav.inviteGroupNotify.addSelectedMembers(favItems.get(position).getSelectedMemberList(), favItems.get(position).getSelectedMemberId());
								}
								if (selectedFav.size() > 0) {

									bottomLayout.setVisibility(View.VISIBLE);

								} else {
									bottomLayout.setVisibility(View.GONE);
								}
*//*


                    }*/
                    Log.e("Selected", ":" + selectedFav.toString());


                }
            });
            return convertView;


        }

        void checkItems(CheckBox checkGroup, int position, View v) {

            if (checkGroup.isChecked()) {
                seleted_GroupId.add(favItems.get(position).getGroupid());
                mSparseBooleanArray.put((Integer) v.getTag(), true);
                selectedFav.add(favItems.get(position).getGroupid());
                InvitePeopleFav.inviteGroupNotify.addSelectedMembers(favItems.get(position).getSelectedMemberList(), favItems.get(position).getSelectedMemberId(), seleted_GroupId);
            } else {
                seleted_GroupId.remove(favItems.get(position).getGroupid());
                mSparseBooleanArray.put((Integer) v.getTag(), false);
                selectedFav.remove(favItems.get(position).getGroupid());
                InvitePeopleFav.inviteGroupNotify.removeSelectedMembers(favItems.get(position).getSelectedMemberList(), favItems.get(position).getSelectedMemberId(), seleted_GroupId);
            }

            bottomLayout.setVisibility(View.VISIBLE);
        /*	if (selectedFav.size() > 0) {

				bottomLayout.setVisibility(View.VISIBLE);

			} else {
				bottomLayout.setVisibility(View.GONE);
			}*/

        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            favItems.clear();
            if (charText.length() == 0) {
                favItems.addAll(favAllItems);
            } else {
                for (InviteGroupModel wp : favAllItems) {
                    if (wp.getGroupname().toLowerCase(Locale.getDefault()).contains(charText)) {
                        favItems.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

        class ViewHolder {
            TextView tvFavName;
            ImageView imgFavs;
            CheckBox checkGroup;
            CustomTextView tv_count;

            ViewHolder(View view) {
                tvFavName = ((TextView) view.findViewById(R.id.tvFavName));
                imgFavs = ((ImageView) view.findViewById(R.id.imgFav));
                checkGroup = (CheckBox) view.findViewById(R.id.checkGroup);
                tv_count = (CustomTextView) view.findViewById(R.id.count);
                checkGroup.setVisibility(View.VISIBLE);

                view.setTag(this);
            }
        }
    }

}
