package com.uactiv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.felipecsl.gifimageview.library.GifImageView;
import com.uactiv.R;
import com.uactiv.adapter.ScheduleAdapter;
import com.uactiv.controller.ResponseListener;
import com.uactiv.model.ScheduleModel;
import com.uactiv.network.RequestHandler;
import com.uactiv.network.ResponseHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class ScheduleFragment extends Fragment implements AppConstants.SharedConstants, AppConstants.urlConstants, ResponseListener, SwipeRefreshLayout.OnRefreshListener {
    View view;
    ArrayList<ScheduleModel> items = new ArrayList<ScheduleModel>();
    RecyclerView productRecycle = null;
    LinearLayoutManager manager = null;
    ScheduleAdapter mAdapter;
    GifImageView progressWheel = null;
    SwipeRefreshLayout refreshLayout = null;
    boolean isRefresh = false;
    String TAG = "ScheduleFragment";

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    LinearLayout mEmptyViewSchedule = null;
    RelativeLayout mEmptyViewNoInternetConnection = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.schedule_fragment, container, false);
        mEmptyViewSchedule = (LinearLayout) rootView.findViewById(R.id.empty_view_schedule);
        mEmptyViewNoInternetConnection = (RelativeLayout) rootView.findViewById(R.id.empty_view_no_internet);
        productRecycle = (RecyclerView) rootView.findViewById(R.id.sceduleListView);
        progressWheel = (GifImageView) rootView.findViewById(R.id.gifLoader);
        Utility.showProgressDialog(getActivity(), progressWheel);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.scheduleRefresh);
        refreshLayout.setOnRefreshListener(this);
        initializeValues();
        isRefresh = false;
        getscheduleList();
        Utility.setEventTracking(getActivity(),"", AppConstants.SCREEN_TRACKING_ID_SHEDULEDCALLENDER);
        Utility.setScreenTracking(getActivity(),"Schedule calender Page");

        return rootView;
    }


    private void getscheduleList() {

        if (Utility.isConnectingToInternet(getActivity())) {
            try {
                if(!isRefresh) {
                    progressWheel.setVisibility(View.VISIBLE);
                    progressWheel.startAnimation();
                }
                mEmptyViewNoInternetConnection.setVisibility(View.GONE);
                mEmptyViewSchedule.setVisibility(View.GONE);
                Map<String, String> param = new HashMap<>();
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(),isStaging)) + schedulelist, param, this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mEmptyViewNoInternetConnection.setVisibility(View.VISIBLE);
            mEmptyViewSchedule.setVisibility(View.GONE);
          //  Utility.showInternetError(getActivity());
        }
    }

    private void notifyCountChanged() {
        Intent registrationComplete = new Intent(AppConstants.ACTION_NOTIFICATION_COUNT_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(registrationComplete);
    }

    private void initializeValues() {


        //items.clear();
        //items.add(new ScheduleModel("Today, 5 August 2015", "15:30", "Tennis with Ritika and 5 others", getResources().getColor(R.color.green)));
        //items.add(new ScheduleModel("Tomorrow, 6 August 2015", "18:30", "Football with Kunal", getResources().getColor(R.color.colorPrimary)));
        //items.add(new ScheduleModel("Sunday, 12 August 2015", "18:30", "Squash with Atish", getResources().getColor(R.color.colorPrimary)));
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //manager.setStackFromEnd(true);
        productRecycle.setLayoutManager(manager);
        sortArray();
     /*   mAdapter = new ScheduleAdapter(getActivity(), items,0);
        productRecycle.setAdapter(mAdapter);*/
    }

    public void sortArray() {

        try {
            if(items != null && items.size() > 0)
            Collections.sort(items, new Comparator<ScheduleModel>() {
                @Override
                public int compare(ScheduleModel lhs, ScheduleModel rhs) {
                    try {
                        return lhs.getDate().compareTo(rhs.getDate());
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
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void successResponse(String successResponse, int flag) {

        /** schedulelist API response
         *
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
                    progressWheel.stopAnimation();
                    progressWheel.setVisibility(View.GONE);

                    if (jsonObject.optString(resultcheck).equals(KEY_TRUE)) {
                        /*int newDataposition = 0;
                        items = ResponseHandler.getInstance().getScheduleList(jsonObject.optJSONArray(KEY_PAST),false);

                        sortArray();

                        if(items.size() > 0){
                            newDataposition = (items.size());
                        }
                        ArrayList<ScheduleModel> mTempItems = ResponseHandler.getInstance().getScheduleList(jsonObject.optJSONArray(KEY_UPCOMING),true);
                        if(mTempItems.size() == 0){
                            newDataposition = -1;
                        }

                        if(mTempItems.size() > 0) {
                           items.addAll(mTempItems);
                        }
                        */
                        items = ResponseHandler.getInstance().getScheduleList(jsonObject.optJSONArray(KEY_UPCOMING),true);
                        sortArray();
                        SharedPref.getInstance().setSharedValue(getActivity(), notification_count, jsonObject.optInt("notification_count"));
                        SharedPref.getInstance().setSharedValue(getActivity(), schedule_count, 0);
                        notifyCountChanged();
                      // sortArray();
                        if(mAdapter == null) {
                            mAdapter = new ScheduleAdapter(getActivity(), items);
                            productRecycle.setAdapter(mAdapter);
                           /* if(newDataposition > 0) {
                                Log.d(TAG,": "+newDataposition);
                                manager.scrollToPositionWithOffset(newDataposition, 0);
                            }*/
                        }else {
                            mAdapter.notifyDataSetChanged(items);
                        }


                    } else {
                        Utility.showToastMessage(getActivity(), jsonObject.optString(KEY_MSG));
                        progressWheel.stopAnimation();
                        progressWheel.setVisibility(View.GONE);
                    }
                }
                if(items.size() ==0){
                    mEmptyViewSchedule.setVisibility(View.VISIBLE);
                    mEmptyViewNoInternetConnection.setVisibility(View.GONE);
                }else {
                    mEmptyViewSchedule.setVisibility(View.GONE);
                    mEmptyViewNoInternetConnection.setVisibility(View.GONE);
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
    public void onResume() {
        super.onResume();
        getscheduleList();
        Log.e("onResume", "onResume");
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        getscheduleList();
        refreshLayout.setRefreshing(false);
    }
}
