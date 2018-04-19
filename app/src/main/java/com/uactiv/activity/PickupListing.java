package com.uactiv.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.belladati.httpclientandroidlib.util.ByteArrayBuffer;
import com.facebook.FacebookSdk;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.gson.Gson;
import com.uactiv.R;
import com.uactiv.adapter.Social_Feed_Adapter;
import com.uactiv.controller.ResponseListener;
import com.uactiv.interfaces.CustomRefresh;
import com.uactiv.interfaces.Customnotify;
import com.uactiv.model.Attendies;
import com.uactiv.model.AttendingModel;
import com.uactiv.model.Detail_;
import com.uactiv.model.DetailsList;
import com.uactiv.model.Newsfeedmodel;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.EndlessRecyclerViewNew;
import com.uactiv.utils.LinearLayoutManagerWithSmoothScroller;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PickupListing extends Fragment implements ResponseListener, AppConstants.SharedConstants, EndlessRecyclerViewNew.Pager,
        AppConstants.urlConstants,CustomRefresh,Customnotify{

    private EndlessRecyclerViewNew lists;

    private LinearLayoutManagerWithSmoothScroller manager;
    public Social_Feed_Adapter adapter;
    View view;
    GifImageView progressWheel = null;
    SwipeRefreshLayout swipeRefresh;
    HashMap<String, String> param;
    boolean isRefresh = false;
    public int limit = 10;
    public static int currentPageNumber = 1;
    private int total_count = 10;
    private int lastVisibleItem = 0;
    private int visibleThreshold = 4;
    Boolean loading = true;
    List<Newsfeedmodel> newsfeedmodelList;
    List<AttendingModel> attendingModelsList;
    DetailsList list;
    static int TOTAL_ITEMS_COUNT;
    private boolean isLazy = false;
    AttendingModel attendingModel;

    static List<DetailsList> detailArraylist;
    List<Attendies> attendiesList;
    private String feedtype;
    public static CustomRefresh customRefresh;
    public static Customnotify customnotify;
    public int send_items,page_left;
    RecyclerView.SmoothScroller smoothScroller;
    int myf=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_pickup_listing, null);
        FacebookSdk.sdkInitialize(getActivity()); // Facebook sdk initiations.
        param = new HashMap<>();
        customRefresh=(CustomRefresh)this;
        customnotify=(Customnotify)this;
        setupUI();
        progressWheel.setVisibility(View.VISIBLE);
        Utility.showProgressDialog(getActivity(), progressWheel);
        attendingModelsList = new ArrayList<>();
        progressWheel.startAnimation();
        isRefresh = false;
        newsfeedmodelList = new ArrayList<>();

        send_items=0;
//setup list adapter
        if(detailArraylist!=null && detailArraylist.size()>0){
            adapter = new Social_Feed_Adapter(getActivity(), getActivity(), detailArraylist);
            lists.setAdapter(adapter);
        }else{
            detailArraylist = new ArrayList<>();
            getfeed(currentPageNumber, send_items);
        }


        isLazy = false;
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);



        // Toast.makeText(getActivity(),getDensityName(getActivity()),Toast.LENGTH_LONG).show();



       swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                currentPageNumber=0;
                send_items=0;
                isLazy=false;
                myf=1;
                detailArraylist = new ArrayList<>();
                detailArraylist.clear();
                getfeed(currentPageNumber, send_items);
            }
        });
        return view;


    }
//get currunt screen density type
    private static String getDensityName(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        if (density >= 4.0) {
            return "xxxhdpi";
        }
        if (density >= 3.0) {
            return "xxhdpi";
        }
        if (density >= 2.0) {
            return "xhdpi";
        }
        if (density >= 1.5) {
            return "hdpi";
        }
        if (density >= 1.0) {
            return "mdpi";
        }
        return "ldpi";
    }
//getfeedlist api call
    public void getfeed(int pageno,int send_items) {
        param = new HashMap<>();
        send_items=send_items;
        param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));
        param.put("page", "" + pageno);
        param.put("send_items", "" + send_items);
        Log.d("pickupparams",new Gson().toJson(param));
        RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants
                        .getBaseUrl(SharedPref.getInstance()
                                .getBooleanValue(getActivity(), isStaging)) + getfeedlist,
                param, this, 0);
        loading = true;
        isLazy = true;

         currentPageNumber=pageno+1;
         Log.d("nextpage",currentPageNumber+"");
    }
//read image from url
    public static byte[] getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(input);
            ByteArrayBuffer baf = new ByteArrayBuffer(5000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }


            return baf.toByteArray();
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
//setup UI elements
    public void setupUI() {
        lists = (EndlessRecyclerViewNew) view.findViewById(R.id.list);
        progressWheel = (GifImageView) view.findViewById(R.id.gifLoader);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        manager = new LinearLayoutManagerWithSmoothScroller(getActivity());

        lists.setLayoutManager(manager);
        lists.setPager(this);

        ((DefaultItemAnimator) lists.getItemAnimator()).setSupportsChangeAnimations(false);
        lists.setProgressView(R.layout.layout_progress_bar);
        RecyclerView.SmoothScroller smoothScroller=new LinearSmoothScroller(getActivity()){
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

        };

        lists.addOnScrollListener(onScrollListener);


//swipe to refresh functinality
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                currentPageNumber = 0;
                myf=1;
                detailArraylist = new ArrayList<>();
                isLazy=false;
                getfeed(currentPageNumber, limit);
            }
        });
       /* list.addOnScrollListener(new DetectScrollToEnd(manager,10) {
            @Override
            protected void onLoadMore() {
                currentPageNumber=currentPageNumber+1;
                Log.d("crrrr",currentPageNumber+"");
                getfeed(currentPageNumber,limit);
            }
        });*/
        //   list.addOnScrollListener(onScrollListener);


    }

//scroll detected
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ?
                    0 : recyclerView.getChildAt(0).getTop();
            swipeRefresh.setEnabled((topRowVerticalPosition >= 0));
            Log.d("topRowVerticalPosition", "" + topRowVerticalPosition);
        }
    };

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {
        JSONObject mainobj = new JSONObject(successResponse);
        //json responce for socialfeed

        JSONArray successResponsejson = new JSONArray(new JSONObject(successResponse).getString("details"));
        Log.d("pickuplisting", new Gson().toJson(successResponse));

        Detail_ detail_;
        List<Detail_>detaillist = null;

        for (int i = 0; i < successResponsejson.length(); i++) {
            list = new DetailsList();
            feedtype=successResponsejson.getJSONObject(i).getString("feedtype");
            if(feedtype.equalsIgnoreCase("pick_up")){

                String idfeed = successResponsejson.getJSONObject(i).getString("idfeed");
                list.setIdfeed(idfeed);

                String idUser=successResponsejson.getJSONObject(i).getString("iduser");
                list.setIduser(idUser);

                String is_admin=successResponsejson.getJSONObject(i).getString("is_admin");
                list.setIs_admin(is_admin);

                String start_date=successResponsejson.getJSONObject(i).getString("start_date");
                list.setStart_date(start_date);

                String feedtype = successResponsejson.getJSONObject(i).getString("feedtype");
                list.setFeedtype(feedtype);

                String fisrtname = successResponsejson.getJSONObject(i).getString("firstname");
                list.setFirstname(fisrtname);

                String image  = successResponsejson.getJSONObject(i).getString("image");
                list.setImage(image);

                String idschedule = successResponsejson.getJSONObject(i).getString("idschedule");

                list.setIdschedule(idschedule);


                String created = successResponsejson.getJSONObject(i).getString("created");

                list.setCreated(created);

                String status = successResponsejson.getJSONObject(i).getString("status");

                list.setStatus(status);

                String actImage = successResponsejson.getJSONObject(i).getString("activityimage");

                list.setActivityimage(actImage);




                String activity = successResponsejson.getJSONObject(i).getString("activity");

                list.setActivity(activity);


                String start_time = successResponsejson.getJSONObject(i).getString("start_time");
                list.setStart_time(start_time);


                String area = successResponsejson.getJSONObject(i).getString("area");
                list.setArea(area);



                String spot = successResponsejson.getJSONObject(i).getString("spot");

                list.setSpot(spot);

                String share = successResponsejson.getJSONObject(i).getString("share");

                list.setShare(share);


                String attending = successResponsejson.getJSONObject(i).getString("attending");

                list.setAttending(attending);

                String paid = successResponsejson.getJSONObject(i).getString("paid");

                list.setPaid(paid);
                String likecount = successResponsejson.getJSONObject(i).getString("likecount");

                list.setLikecount(likecount);

                String liked = successResponsejson.getJSONObject(i).getString("liked");

                list.setLiked(liked);

                JSONArray attendinJsonArray = new JSONArray(successResponsejson.getJSONObject(i).getString("attendiees"));
                if (attendinJsonArray.length() > 0) {
                    attendiesList = new ArrayList<>();
                    for (int j = 0; j < attendinJsonArray.length(); j++) {
                        Attendies attendies = new Attendies();
                        AttendingModel attendingModel = new AttendingModel();
                        String iduser = attendinJsonArray.getJSONObject(j).getString("iduser");
                        attendingModel.setIduser(iduser);
                        attendies.setIduser(iduser);

                        String images = attendinJsonArray.getJSONObject(j).getString("image");
                        attendies.setImage(images);
                        attendingModel.setImage(images);
                        attendingModelsList.add(attendingModel);
                        attendiesList.add(attendies);
                    }
                    list.setAttendiees(attendiesList);
                }



            }else if(feedtype.equalsIgnoreCase("article")){
                String idfeed = successResponsejson.getJSONObject(i).getString("idfeed");
                list.setIdfeed(idfeed);


                String feedtype = successResponsejson.getJSONObject(i).getString("feedtype");
                list.setFeedtype(feedtype);

                String description = successResponsejson.getJSONObject(i).getString("description");
                list.setDescription(description);

                String title = successResponsejson.getJSONObject(i).getString("title");
                list.setTitle(title);

                String image = successResponsejson.getJSONObject(i).getString("image");
                list.setActivityimage(image);

                String share = successResponsejson.getJSONObject(i).getString("share");

                list.setShare(share);

                String user_image = successResponsejson.getJSONObject(i).getString("user_image");
                list.setImage(user_image);

                String link = successResponsejson.getJSONObject(i).getString("link");
                list.setLink(link);

                String created = successResponsejson.getJSONObject(i).getString("created");
                list.setCreated(created);

                String firstname = successResponsejson.getJSONObject(i).getString("firstname");
                list.setFirstname(firstname);

                String status = successResponsejson.getJSONObject(i).getString("status");
                list.setStatus(status);

                String credit = successResponsejson.getJSONObject(i).getString("credit");
                list.setCredit(credit);

                String activity = successResponsejson.getJSONObject(i).getString("activity");
                list.setActivity(activity);

                String liked = successResponsejson.getJSONObject(i).getString("liked");
                list.setLiked(liked);

                String likecount = successResponsejson.getJSONObject(i).getString("likecount");
                list.setLikecount(likecount);


            }else if(feedtype.equalsIgnoreCase("video")){
                detail_=new Detail_();
                detaillist=new ArrayList<>();
                String idfeed = successResponsejson.getJSONObject(i).getString("idfeed");
                list.setIdfeed(idfeed);

                String idUser=successResponsejson.getJSONObject(i).getString("iduser");
                list.setIduser(idUser);


                String feedtype = successResponsejson.getJSONObject(i).getString("feedtype");
                list.setFeedtype(feedtype);

                String description = successResponsejson.getJSONObject(i).getString("description");
                list.setDescription(description);

                String share = successResponsejson.getJSONObject(i).getString("share");

                list.setShare(share);


                String image = successResponsejson.getJSONObject(i).getString("image");
                list.setImage(image);


                String created = successResponsejson.getJSONObject(i).getString("created");
                list.setCreated(created);

                String firstname = successResponsejson.getJSONObject(i).getString("firstname");
                list.setFirstname(firstname);

                list.setActivity(activity);

                String liked = successResponsejson.getJSONObject(i).getString("liked");
                list.setLiked(liked);

                String likecount = successResponsejson.getJSONObject(i).getString("likecount");
                list.setLikecount(likecount);

                detail_.setLink(successResponsejson.getJSONObject(i).getJSONArray("details").getJSONObject(0).getString("link"));
                detail_.setImage(successResponsejson.getJSONObject(i).getJSONArray("details").getJSONObject(0).getString("image"));
                detaillist.add(detail_);
                list.setDetails(detaillist);

            }else if(feedtype.equalsIgnoreCase("photo")){
                detail_=new Detail_();
                detaillist=new ArrayList<>();
                String idfeed = successResponsejson.getJSONObject(i).getString("idfeed");
                list.setIdfeed(idfeed);



                String idUser=successResponsejson.getJSONObject(i).getString("iduser");
                list.setIduser(idUser);


                String feedtype = successResponsejson.getJSONObject(i).getString("feedtype");
                list.setFeedtype(feedtype);

                String description = successResponsejson.getJSONObject(i).getString("description");
                list.setDescription(description);

                String share = successResponsejson.getJSONObject(i).getString("share");

                list.setShare(share);


                String image = successResponsejson.getJSONObject(i).getString("image");
                list.setImage(image);


                String created = successResponsejson.getJSONObject(i).getString("created");
                list.setCreated(created);

                String firstname = successResponsejson.getJSONObject(i).getString("firstname");
                list.setFirstname(firstname);

                list.setActivity(activity);

                String liked = successResponsejson.getJSONObject(i).getString("liked");
                list.setLiked(liked);

                String likecount = successResponsejson.getJSONObject(i).getString("likecount");
                list.setLikecount(likecount);

                JSONArray mainarr=new JSONArray(successResponsejson.getJSONObject(i).getString("details"));
                for (int j=0;j<mainarr.length();j++){
                    detail_=new Detail_();
                    detail_.setImage(mainarr.getJSONObject(j).getString("image"));
                    detaillist.add(detail_);
                }

                list.setDetails(detaillist);


            }

            detailArraylist.add(list);




        }
        JSONObject page = new JSONObject(mainobj.getString("page"));

        if (page != null) {

            //  totalItems = page.optInt("total_items");
            TOTAL_ITEMS_COUNT = page.optInt("total_items");
            currentPageNumber = page.optInt("current_page");
            send_items=page.optInt("send_items");
            Log.d("LastPage ", currentPageNumber + ""+send_items);
            currentPageNumber = currentPageNumber + 1;
            Log.d("NextPage ", currentPageNumber + "");
            total_count = page.optInt("total_pages");
            page_left= page.optInt("page_left");
        }

        if(page_left==0){
            lists.setRefreshing(false);
        }
        Log.d("atttt", new Gson().toJson(detailArraylist));

        Log.d("atttt", new Gson().toJson(detailArraylist));

        if (!isLazy) {

            if (detailArraylist.size() > 0) {
                adapter = new Social_Feed_Adapter(getActivity(), getActivity(), detailArraylist);
                lists.setAdapter(adapter);


            }
        } else if(myf==1){
            adapter = new Social_Feed_Adapter(getActivity(), getActivity(), detailArraylist);
            lists.setAdapter(adapter);
            myf=0;
           // adapter.notifyItemRangeChanged(0, detailArraylist.size(), "test");

        }else {
            adapter.notifyDataSetChanged();
        }


        if (isRefresh) {
            swipeRefresh.setRefreshing(false);
        }
        isLazy = false;
        progressWheel.setVisibility(View.GONE);
        progressWheel.stopAnimation();
    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {
        Log.d("resp", new Gson().toJson(jsonObject));

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {
        progressWheel.setVisibility(View.GONE);
        progressWheel.stopAnimation();
    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }
//load more functinality for social feed
    @Override
    public boolean shouldLoad() {
        boolean shouldLoad = false;
        shouldLoad = (adapter.getItemCount() < TOTAL_ITEMS_COUNT);
        Log.d("shouldLoad", "shouldLoad");
        if (shouldLoad) {
            try {
                Log.d("shouldLoad11111", "shouldLoad");
                lists.setRefreshing(true);
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }

        } else {
            lists.setRefreshing(false);
        }
        Log.d("shpuldload", shouldLoad + "");
        return shouldLoad;
    }

    @Override
    public void loadNextPage() {
        if (!isLazy) {
            lists.setRefreshing(true);
            Log.d("getcurruntpage", currentPageNumber + "");
            isLazy = true;

            getfeed(currentPageNumber, send_items);

        }


    }



    @Override
    public void donotifychange() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void doRefresh(int pos) {
        adapter.notifyItemChanged(pos,"lola");
    }
}
