package com.uactiv.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.controller.ResponseListener;
import com.uactiv.model.ActivityList;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fliter_Activity_fragment extends Fragment implements AppConstants.urlConstants, AppConstants.SharedConstants, ResponseListener {
    View view;
    private ListView listView;
    private List<String> myList;
    private List<String> mytempList, mycount;

    private ArrayAdapter<String> adapter;
    private CustomTextView tv_apply;
    private CustomTextView tv_act;
    SparseBooleanArray sp;
    ArrayList<String> activity_list = new ArrayList<String>();
    ArrayList<HashMap<String, String>> pickUpAutoList = new ArrayList<HashMap<String, String>>();
    String[] activities;
    private CustomTextView tv_reset;
    Bundle bn;
    ImageView iv_back;
    private ArrayList<ActivityList> mactivitylist = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fliter__activity_fragment, container, false);
        bn = this.getArguments();
        listView = (ListView) view.findViewById(R.id.list);
        iv_back=(ImageView)view.findViewById(R.id.iv_back);
        tv_reset = (CustomTextView) view.findViewById(R.id.tv_reset);
        tv_apply = (CustomTextView) view.findViewById(R.id.tv_apply);
        myList = new ArrayList<>();
        mytempList = new ArrayList<>();
        mycount = new ArrayList<>();
        myList.add("Item1");
        myList.add("Item2");
        myList.add("Item3");
        myList.add("Item4");
        myList.add("Item5");
        getActivtyList();

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fliter_Activity_fragment fragment = new Fliter_Activity_fragment();

                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                }
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        tv_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bn != null && bn.getString("frompickup") != null) {
                    Pickup_Filter_fragment fragment = new Pickup_Filter_fragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("data", (Serializable) mytempList);
                    mBundle.putSerializable("dataCount", (Serializable) mycount);

                    fragment.setArguments(mBundle);

                    if (fragment != null) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.commit();
                    }
                } else {

                    Filter_fragment fragment = new Filter_fragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("data", (Serializable) mytempList);
                    mBundle.putSerializable("dataCount", (Serializable) mycount);

                    fragment.setArguments(mBundle);

                    if (fragment != null) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        fragmentTransaction.commit();
                    }

                }


            }
        });
        return view;
    }

    private void searchKeySkills(JSONArray skillistArray) {


        String skillSet = SharedPref.getInstance().getStringVlue(getActivity(), Api_skill_list);

        //  Log.e("skillSet",":"+skillSet);

        if (skillistArray != null) {

            try {
                if (skillistArray != null && skillistArray.length() > 0 && activity_list.size() <= 0) {

                    activity_list.clear();

                    for (int k = 0; k < skillistArray.length(); k++) {
                        activity_list.add(skillistArray.optJSONObject(k).optString("activity"));
                        ActivityList activityList = new ActivityList();
                        activityList.setActivity(skillistArray.optJSONObject(k).optString("activity"));
                        activityList.setId(skillistArray.optJSONObject(k).optString("id"));
                        activityList.setIsBookingOpen(true);
                        mactivitylist.add(activityList);
                        Log.d("mylist", new Gson().toJson(mactivitylist));

                    }
                    for(int k=0;k<mactivitylist.size();k++){
                        activity_list.add(mactivitylist.get(k).getActivity());
                    }

                    activities = new String[activity_list.size()];
                    activities = activity_list.toArray(activities);
                    adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_multiple_choice, activity_list);
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listView.setAdapter(adapter);


                /*    for (int i=0;i<activities.length;i++){
                        String str = "";
                        CheckedTextView tv = (CheckedTextView) listView.getChildAt(i);
                        if(tv.isChecked()){
                            tv.setCheckMarkDrawable(R.drawable.bill);
                        }else{
                            tv.setCheckMarkDrawable(null);
                        }
                    }*/
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String str = "";
                      /*      ListView lv = (ListView) adapterView;
                            CheckedTextView tv = (CheckedTextView) lv.getChildAt(i);
                            if(tv.isChecked()){
                                tv.setCheckMarkDrawable(R.drawable.bill);
                            }else{
                                tv.setCheckMarkDrawable(null);
                            }*/


                            sp = listView.getCheckedItemPositions();
                            tv_apply.setVisibility(View.VISIBLE);

                            mytempList = new ArrayList<>();
                            mycount = new ArrayList<>();
                            for (int j = 0; j < mactivitylist.size(); j++) {
                                try {
                                    if (sp.valueAt(j)) {
                                        mytempList.add(mactivitylist.get(sp.keyAt(j)).getActivity() + "");
                                        Log.d("yyyyy", String.valueOf((sp.keyAt(j))));
                                        mycount.add(mactivitylist.get(sp.keyAt(j)).getId() + "");
                                    }
                                    //
                                    // str+=sp.keyAt(j)+",";

                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();

                                }

                            }


                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (activities != null && activities.length > 0) {

                for (int i = 0; i < activities.length; i++) {
                    HashMap<String, String> pickUpMap = new HashMap<String, String>();
                    pickUpMap.put("pickUpNames", activities[i]);
                    pickUpAutoList.add(pickUpMap);
                }
                String[] pickUpFrom = {"pickUpNames"};
                int[] pickUpTo = {R.id.txt};

            }
        }


    }

    public void removeSelection() {
        sp = new SparseBooleanArray();
        adapter.notifyDataSetChanged();
    }

    private void getActivtyList() {

        if (Utility.isConnectingToInternet(getActivity())) {
            try {
                Map<String, String> stringMap = new HashMap<>();
                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + getactivityList, stringMap, this, 3);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {
        JSONObject jsonObject = new JSONObject(successResponse);
        if (jsonObject != null) {

            if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {

                JSONArray skillistArray = jsonObject.optJSONArray(KEY_DETAIL);
                if (skillistArray != null && skillistArray.length() > 0) {
                    SharedPref.getInstance().setSharedValue(getActivity(), Api_skill_list, skillistArray.toString());
                    searchKeySkills(skillistArray);
                }


            }
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
}
