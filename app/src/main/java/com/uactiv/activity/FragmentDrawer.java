package com.uactiv.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.uactiv.R;
import com.uactiv.adapter.NavigationDrawerAdapter;
import com.uactiv.model.NavDrawerItem;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;

import java.util.ArrayList;

;

public class FragmentDrawer extends Fragment implements AppConstants.SharedConstants {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView recyclerView;
    com.alexzh.circleimageview.CircleImageView mimgProfile;
    RelativeLayout Mnav_header_container;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;
    private String[] slidemenuitem = new String[] {"Home","My Profile","Favourites","Invite & Earn",
            "Settings","About"};
    private static final int[] ITEM_DRAWABLES = {R.drawable.menu_home_icon,
            R.drawable.menu_profile_icon,
            R.drawable.menu_fav_icon,
            R.drawable.menu_invite_icon,
            R.drawable.menu_setting_icon,
            R.drawable.menu_help_icon};
    String name;
    int sliderimage;
    ArrayList<NavDrawerItem> arraylist = new ArrayList<NavDrawerItem>();
    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // drawer labels
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
    }


    BroadcastReceiver mProfilepic_changedReceiver ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        mimgProfile = (com.alexzh.circleimageview.CircleImageView)layout.findViewById(R.id.imgProfile);
        Mnav_header_container = (RelativeLayout)layout.findViewById(R.id.nav_header_container);


        mProfilepic_changedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.e("PicchangedReceiver", "onReceive");
                if (intent != null && intent.getAction().equals(AppConstants.ACTION_PROFILE_PICTURE_CHANGED)) {

                    if(!TextUtils.isEmpty(SharedPref.getInstance().getStringVlue(getActivity(),image))){
                        /*Picasso.with(getActivity())
                                .load(SharedPref.getInstance().getStringVlue(getActivity(),image))
                                .placeholder(R.drawable.ic_profile).centerCrop().fit()
                                .into(mimgProfile);*/
                        Log.e("MainProcPic",""+ SharedPref.getInstance().getStringVlue(getActivity(),image));
                        Utility.setImageUniversalLoader(getActivity(), SharedPref.getInstance().getStringVlue(getActivity(), image), mimgProfile);
                    }

                }
            }
        };

        Mnav_header_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        for (int i = 0; i < slidemenuitem.length; i++) {
            name=slidemenuitem[i];
            sliderimage = ITEM_DRAWABLES[i];
            arraylist.add(new NavDrawerItem(name,sliderimage));
        }

        adapter = new NavigationDrawerAdapter(getActivity(), arraylist);
        recyclerView.setAdapter(adapter);
        adapter.notifyWithNewDataSet(arraylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    drawerListener.onDrawerItemSelected(view, position);
                    if (MainActivity.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {

                        MainActivity.mDrawerLayout.closeDrawer(Gravity.RIGHT);
                    } else {

                        MainActivity.mDrawerLayout.openDrawer(Gravity.RIGHT);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if(!TextUtils.isEmpty(SharedPref.getInstance().getStringVlue(getActivity(),image))){
            Utility.setImageUniversalLoader(getActivity(), SharedPref.getInstance().getStringVlue(getActivity(), image), mimgProfile);
            //Picasso.with(getActivity()).load(SharedPref.getInstance().getStringVlue(getActivity(), image)).fit().into(mimgProfile);
        }else{
            mimgProfile.setImageResource(R.drawable.deafult_user);
            mimgProfile.setBackgroundColor(Color.WHITE);
        }


        return layout;
    }

   /* public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }
*/
    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mProfilepic_changedReceiver, new IntentFilter(AppConstants.ACTION_PROFILE_PICTURE_CHANGED));
        super.onResume();
        Log.e(getClass().getName(),"onResume");
        if(!TextUtils.isEmpty(SharedPref.getInstance().getStringVlue(getActivity(),image))){
            Utility.setImageUniversalLoader(getActivity(), SharedPref.getInstance().getStringVlue(getActivity(), image), mimgProfile);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e(getClass().getName(), "onPause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mProfilepic_changedReceiver);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}
