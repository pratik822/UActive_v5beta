package com.uactiv.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.controller.ResponseListener;
import com.uactiv.model.FavouriteModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavouriteAddMembers extends Activity implements OnClickListener, ResponseListener, AppConstants.SharedConstants, AppConstants.urlConstants {

    private ListView lvAddMembers;
    ArrayList<FavouriteModel> favmember_arrayList;
    FavAddAdapter mAdapter;
    private CustomButton tvCancel;
    CustomButton rLyBottom;
    String TAG = getClass().getSimpleName();
    ArrayList<String> addeditemCount = new ArrayList<String>();

    Intent intent;
    GifImageView progressWheel = null;
    ArrayList<FavouriteModel> favouriteModels =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fav_add_members);

        intent = getIntent();

        if(intent !=null){

            favouriteModels = (ArrayList<FavouriteModel>) intent.getSerializableExtra("groupmembers");
        }

        progressWheel = (GifImageView) findViewById(R.id.gifLoader);
        Utility.showProgressDialog(FavouriteAddMembers.this, progressWheel);
        lvAddMembers = (ListView) findViewById(R.id.lvAddMembers);
        rLyBottom = (CustomButton) findViewById(R.id.rLyBottom);
        rLyBottom.setVisibility(View.GONE);
        tvCancel = (CustomButton) findViewById(R.id.tvCancel);

        //onclick
        rLyBottom.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        favmember_arrayList = new ArrayList<FavouriteModel>();
        favmember_arrayList.clear();

        getFavouriteList();

      /*  for (int i = 0; i < 10; i++) {
            favmember_arrayList.add(new FavouriteModel(""+i, "NAme"+i, "image"));
        }
*/
        mAdapter = new FavAddAdapter(FavouriteAddMembers.this, favmember_arrayList);

        lvAddMembers.setAdapter(mAdapter);

    }

    private void getFavouriteList() {

        if (Utility.isConnectingToInternet(FavouriteAddMembers.this)) {

            try {
                progressWheel.setVisibility(View.VISIBLE);
                progressWheel.startAnimation();
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(FavouriteAddMembers.this, userId));
                param.put("page", "1");
                RequestHandler.getInstance().stringRequestVolley(FavouriteAddMembers.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(FavouriteAddMembers.this,isStaging)) + favouriteslist, param, this, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }

    }

    @Override
    public void successResponse(String successResponse, int flag) {


        {
            /** flag == 0 Favouritelist API reponse
             *	flag == 1 addtomembers group API reponse
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

                                for (int i = 0; i < getfav.length(); i++) {
                                    JSONObject favObj = getfav.optJSONObject(i);
                                    favmember_arrayList.add(new FavouriteModel(favObj.optString("iduser"), favObj.optString("firstname") + " " + favObj.optString("lastname"), favObj.optString("image")));
                                }

                                if(favouriteModels != null) {
                                    favmember_arrayList = standardSort(favmember_arrayList, favouriteModels);
                                }

                                mAdapter = new FavAddAdapter(FavouriteAddMembers.this, favmember_arrayList );
                                lvAddMembers.setAdapter(mAdapter);
                                progressWheel.stopAnimation();
                                progressWheel.setVisibility(View.GONE);
                            }

                        } else {
                            Utility.showToastMessage(FavouriteAddMembers.this, jsonObject.optString(KEY_MSG));
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                        }
                    }

                    break;

                case 1:

                    if (jsonObject != null) {

                        if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                            ArrayList<FavouriteModel> temparray = new ArrayList<>();

                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray(KEY_DETAIL);

                                if (jsonArray != null) {

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject member = jsonArray.getJSONObject(i);
                                        temparray.add(new FavouriteModel(member.optString("iduser"), member.optString("firstname") + " " + member.optString("lastname"), member.optString("image")));

                                    }

                                    if (FavGroupEdit.mAdapter != null) {
                                       // new FavGroupEdit().setchangedItems(temparray);
                                        progressWheel.stopAnimation();
                                        progressWheel.setVisibility(View.GONE);
                                        rLyBottom.setEnabled(true);
                                    }
                                    progressWheel.stopAnimation();
                                    progressWheel.setVisibility(View.GONE);
                                    finish();

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            progressWheel.stopAnimation();
                            progressWheel.setVisibility(View.GONE);
                            rLyBottom.setEnabled(true);
                            Utility.showToastMessage(FavouriteAddMembers.this, jsonObject.optString(KEY_MSG));
                        }

                    }
                    break;
            }

        }

    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        progressWheel.stopAnimation();
        progressWheel.setVisibility(View.GONE);
        rLyBottom.setEnabled(true);
    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }

    public class FavAddAdapter extends BaseAdapter {
        Context context;
        ArrayList<FavouriteModel> items = new ArrayList<FavouriteModel>();
        LayoutInflater mInflater;

        SparseBooleanArray mSparseBooleanArray = null;

        public FavAddAdapter(Context context, ArrayList<FavouriteModel> items) {
            mSparseBooleanArray = new SparseBooleanArray();
            this.context = context;
            this.items = items;

        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public FavouriteModel getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup arg2) {

            final FavouriteModel item = getItem(position);
            ViewHolder holder = new ViewHolder();
            View listView = convertView;

            if (listView == null) {
                mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listView = mInflater.inflate(R.layout.custom_add_members, null);
                holder.tvFavName = (TextView) listView.findViewById(R.id.tvFavName);
                holder.imgFavAdd = (CircleImageView) listView.findViewById(R.id.imgFavAdd);
                holder.checkAdd = (CheckBox) listView.findViewById(R.id.checkAdd);
                listView.setTag(holder);
            } else {
                holder = (ViewHolder) listView.getTag();
            }


            if (Utility.isNullCheck(items.get(position).getImage())) {

                /*Picasso.with(FavouriteAddMembers.this)
                        .load(items.get(position).getImage())
                        .placeholder(R.drawable.ic_profile).centerCrop().fit()
                        .into(holder.imgFavAdd);*/

                Log.e("adapter image",""+items.get(position).getImage());

                Utility.setImageUniversalLoader(getApplicationContext(),items.get(position).getImage(),holder.imgFavAdd);
            }

            holder.checkAdd.setTag(position);
            holder.checkAdd.setChecked(mSparseBooleanArray.get(position));

            //	holder.imgFavAdd.setImageResource(item.getImage());
            holder.tvFavName.setText(item.getName());



            final ViewHolder finalHolder = holder;

            holder.checkAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.setEventTracking(FavouriteAddMembers.this,"Add member to group screen in favourites","Select Button on Add member to group screen in favorites");


                    if (finalHolder.checkAdd.isChecked()) {

                        mSparseBooleanArray.put((Integer) v.getTag(), true);

                        addeditemCount.add(item.getId());

                    } else {

                        mSparseBooleanArray.put((Integer) v.getTag(), false);

                        addeditemCount.remove(item.getId());
                    }

                    if (addeditemCount.size() > 0) {
                        rLyBottom.setVisibility(View.VISIBLE);
                    } else {
                        rLyBottom.setVisibility(View.GONE);
                    }


                    Log.e(TAG, "seleted items :" + addeditemCount.toString());
                }
            });




            return listView;
        }


        class ViewHolder {
            TextView tvFavName;
            CircleImageView imgFavAdd;
            CheckBox checkAdd;
        }

    }


    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.tvCancel:
                Utility.setEventTracking(FavouriteAddMembers.this,"View Group members screen in favorites","Back Arrow on view Group members screen");
                onBackPressed();
            break;

            case R.id.rLyBottom:
                if (intent != null) {
                    Utility.setEventTracking(FavouriteAddMembers.this,"View Group members screen in favorites","Add Button on view Group members screen");
                    if(!TextUtils.isEmpty(intent.getStringExtra("groupid"))){

                        if (Utility.isConnectingToInternet(FavouriteAddMembers.this)) {

                            try {
                                progressWheel.setVisibility(View.VISIBLE);
                                progressWheel.startAnimation();
                                Map<String, String> param = new HashMap<>();
                                param.put("members", addeditemCount.toString().replaceAll("\\[", "").replaceAll("\\]", ""));
                                param.put("idgroup", intent.getStringExtra("groupid"));
                                rLyBottom.setEnabled(true);
                                RequestHandler.getInstance().stringRequestVolley(FavouriteAddMembers.this, AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(FavouriteAddMembers.this,isStaging)) + addtogroup, param, this, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            Utility.showInternetError(FavouriteAddMembers.this);
                        }

                    }

                }

                break;
        }

    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }

    public static ArrayList<FavouriteModel> standardSort(ArrayList<FavouriteModel> obj1, ArrayList<FavouriteModel> obj2)
    {
        ArrayList<FavouriteModel> returnList = new ArrayList<FavouriteModel>();
        for (FavouriteModel obj1Element: obj1)
        {
            boolean found = false;
            for (FavouriteModel obj2Element: obj2)
            {
                if ( obj1Element.getId().equals(obj2Element.getId()))
                {
                    found = true;
                }
            }
            if (!found)
            {
                returnList.add(obj1Element);
            }
        }

        return returnList;
    }
}
