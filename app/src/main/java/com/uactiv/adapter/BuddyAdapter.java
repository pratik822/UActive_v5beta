package com.uactiv.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uactiv.R;
import com.uactiv.activity.BuddyUpDetailsActivity;
import com.uactiv.activity.BusinessDetailsActivity;
import com.uactiv.activity.MainActivity;
import com.uactiv.adapter.viewholder.ProgressViewHolder;
import com.uactiv.application.UActiveApplication;
import com.uactiv.interfaces.OnLoadMoreListener;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.SkillDo;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.Utility;
import com.uactiv.viewholder.BuddyViewHolder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BuddyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AppConstants.urlConstants {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    public ArrayList<BuddyModel> buddylist = new ArrayList<>();
    public ArrayList<BuddyModel> all_temp_buddyupArrayList = new ArrayList<>();
    public Context mContext;
    RecyclerView.LayoutManager mLayoutManager;
    int away;
    int match = 0;
    /*  private int visibleThreshold = 5;
      private int lastVisibleItem, totalItemCount;
      private boolean loading;*/
    private OnLoadMoreListener onLoadMoreListener;
    private String TAG = getClass().getSimpleName();

/*
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;*/

    public BuddyAdapter(Context context, ArrayList<BuddyModel> item) {
        mLayoutManager = new LinearLayoutManager(context);
        this.buddylist = item;
        this.mContext = context;
        all_temp_buddyupArrayList.addAll(item);
    }


    @Override
    public int getItemCount() {
        return buddylist.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public void addNewItemList(ArrayList<BuddyModel> buddyModels) {
        if (buddyModels != null) {
            this.buddylist = buddyModels;
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_buddy, parent, false);
        viewHolder = new BuddyViewHolder(v);
        return viewHolder;
    }

    /* public void setLoaded() {
         loading = false;
         try {
             for (int i=0;i<buddylist.size();i++) {
                 if(buddylist.get(i) == null){
                     Log.d(TAG,"position");
                     buddylist.remove(i);
                     notifyDataSetChanged();
                 }
             }
         }catch (Exception e){
             e.printStackTrace();
         }

     }*/
   /* public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }*/
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder buddyViewHolder, final int position) {

        if (buddyViewHolder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) buddyViewHolder).progressBar.setIndeterminate(true);
        } else if (buddyViewHolder instanceof BuddyViewHolder) {

            final BuddyViewHolder holder = (BuddyViewHolder) buddyViewHolder;

            BuddyModel singledata = buddylist.get(position);
            if (Utility.isNullCheck(singledata.getName())) {
                holder.tvName.setText(singledata.getName());
            }

            if (Utility.isNullCheck(singledata.getAge())) {

                Date birthday = null;
                try {
                    birthday = AppConstants.sdf.parse(singledata.getAge());
                    holder.tvEarlyDistance.setText(Utility.calculateAge(birthday, mContext, false));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } else {
                holder.tvEarlyDistance.setVisibility(View.INVISIBLE);
            }
            if (Utility.isNullCheck(singledata.getMutual_friends())) {
                if (!singledata.getAwayDistance().equalsIgnoreCase("0")) {
                    holder.tvAwayDistance.setText(singledata.getAwayDistance() + " Km Away");
                } else {
                    holder.tvAwayDistance.setText(singledata.getAwayDistance() + " Km Away");
                }

            }


            if (Utility.isNullCheck(singledata.getAwayDistance())) {

                away = (int) Double.parseDouble(singledata.getAwayDistance());


                match = away;
            }


            if (Utility.isNullCheck(singledata.getBadge())) {
                holder.tvTypes.setVisibility(View.VISIBLE);
                holder.tvTypes.setText(singledata.getBadge());
                if (singledata.getBadgeImage() != 0)
                    holder.imageType.setImageResource(singledata.getBadgeImage());
            } else {
                holder.tvTypes.setVisibility(View.INVISIBLE);
            }

            if (Utility.isNullCheck(singledata.getImage())) {

                Picasso.with(UActiveApplication.getInstance().getApplicationContext())
                        .load(singledata.getImage())
                        .placeholder(R.drawable.profiless).centerCrop().fit()
                        .error(R.drawable.profiless).centerCrop()
                        .into(holder.imageProfile);

                //Utility.SetUrlImage(singledata.getImage(),holder.imageProfile);


                // imageLoader.init(config);
                //imageLoader.displayImage(singledata.getImage(), holder.imageProfile, options);

                //Utility.setImageUniversalLoader(mContext, singledata.getImage(), holder.imageProfile);
                //UActiveApplication.getInstance().loadImage(singledata.getImage(), holder.imageProfile);

            } else {
                holder.imageProfile.setImageResource(R.drawable.profiless);
            }

            holder.tvTypes.setVisibility(View.VISIBLE);

            if (buddylist.get(position).getUser_type() == USER_TYPE_BUSINESS) {
                holder.imgRateOne.setVisibility(View.GONE);
                holder.imgRateTwo.setVisibility(View.GONE);
                holder.imgRateThree.setVisibility(View.GONE);
                holder.imgRateFour.setVisibility(View.GONE);
                holder.imageType.setImageResource(R.drawable.certified);
                holder.tvTypes.setVisibility(View.GONE);
                holder.tvEarlyDistance.setVisibility(View.GONE);

            }


            ArrayList<SkillDo> skillList = singledata.getSkillDo();

            // Log.e("SkillDo",":"+skillList.get(0).getActivty() +skillList.get(1).getActivty() + skillList.get(2).getActivty() + skillList.get(3).getActivty());


            switch (skillList.size()) {
                case 1:
                    holder.tvSquash.setVisibility(View.VISIBLE);
                    holder.tvParkour.setVisibility(View.GONE);
                    holder.tvYoga.setVisibility(View.GONE);
                    holder.tvactivtyfour.setVisibility(View.GONE);


                    if (buddylist.get(position).getUser_type() == USER_TYPE_APP) {
                        holder.imgRateOne.setVisibility(View.VISIBLE);
                    }
                    holder.imgRateTwo.setVisibility(View.GONE);
                    holder.imgRateThree.setVisibility(View.GONE);
                    holder.imgRateFour.setVisibility(View.GONE);

                    if (Utility.isNullCheck(skillList.get(0).getActivty())) {
                        holder.tvSquash.setText(skillList.get(0).getActivty());
                        setRating(holder.imgRateOne, skillList.get(0).getLevel());
                    } else {
                        holder.tvSquash.setVisibility(View.GONE);
                        holder.imgRateOne.setVisibility(View.GONE);
                    }


                    break;
                case 2:
                    holder.tvSquash.setVisibility(View.VISIBLE);
                    holder.tvParkour.setVisibility(View.VISIBLE);
                    holder.tvYoga.setVisibility(View.GONE);
                    holder.tvactivtyfour.setVisibility(View.GONE);

                    if (buddylist.get(position).getUser_type() == USER_TYPE_APP) {
                        holder.imgRateOne.setVisibility(View.VISIBLE);
                        holder.imgRateTwo.setVisibility(View.VISIBLE);
                    }


                    holder.imgRateThree.setVisibility(View.GONE);
                    holder.imgRateFour.setVisibility(View.GONE);


                    if (Utility.isNullCheck(skillList.get(0).getActivty())) {
                        holder.tvSquash.setText(skillList.get(0).getActivty());
                        setRating(holder.imgRateOne, skillList.get(0).getLevel());
                    } else {
                        holder.tvSquash.setVisibility(View.GONE);
                        holder.imgRateOne.setVisibility(View.GONE);
                    }


                    if (Utility.isNullCheck(skillList.get(1).getActivty())) {
                        holder.tvParkour.setText(skillList.get(1).getActivty());
                        setRating(holder.imgRateTwo, skillList.get(1).getLevel());
                    } else {
                        holder.tvParkour.setVisibility(View.GONE);
                        holder.imgRateTwo.setVisibility(View.GONE);
                    }

                    //holder.tvSquash.setText(skillList.get(0).getActivty());
                    // holder.tvParkour.setText(skillList.get(1).getActivty());

                    // setRating(holder.imgRateOne, skillList.get(0).getLevel());
                    // setRating(holder.imgRateTwo, skillList.get(1).getLevel());
                    break;
                case 3:
                    holder.tvSquash.setVisibility(View.VISIBLE);
                    holder.tvParkour.setVisibility(View.VISIBLE);
                    holder.tvYoga.setVisibility(View.VISIBLE);
                    holder.tvactivtyfour.setVisibility(View.GONE);

                    if (buddylist.get(position).getUser_type() == USER_TYPE_APP) {
                        holder.imgRateOne.setVisibility(View.VISIBLE);
                        holder.imgRateTwo.setVisibility(View.VISIBLE);
                        holder.imgRateThree.setVisibility(View.VISIBLE);
                    }


                    holder.imgRateFour.setVisibility(View.GONE);


                    if (Utility.isNullCheck(skillList.get(0).getActivty())) {
                        holder.tvSquash.setText(skillList.get(0).getActivty());
                        setRating(holder.imgRateOne, skillList.get(0).getLevel());
                    } else {
                        holder.tvSquash.setVisibility(View.GONE);
                        holder.imgRateOne.setVisibility(View.GONE);
                    }


                    if (Utility.isNullCheck(skillList.get(1).getActivty())) {
                        holder.tvParkour.setText(skillList.get(1).getActivty());
                        setRating(holder.imgRateTwo, skillList.get(1).getLevel());
                    } else {
                        holder.tvParkour.setVisibility(View.GONE);
                        holder.imgRateTwo.setVisibility(View.GONE);
                    }


                    if (Utility.isNullCheck(skillList.get(2).getActivty())) {
                        holder.tvYoga.setText(skillList.get(2).getActivty());
                        setRating(holder.imgRateThree, skillList.get(2).getLevel());
                    } else {
                        holder.tvYoga.setVisibility(View.GONE);
                        holder.imgRateThree.setVisibility(View.GONE);
                    }


                    break;
                case 4:
                    holder.tvSquash.setVisibility(View.VISIBLE);
                    holder.tvParkour.setVisibility(View.VISIBLE);
                    holder.tvYoga.setVisibility(View.VISIBLE);
                    holder.tvactivtyfour.setVisibility(View.VISIBLE);


                    if (buddylist.get(position).getUser_type() == USER_TYPE_APP) {
                        holder.imgRateOne.setVisibility(View.VISIBLE);
                        holder.imgRateTwo.setVisibility(View.VISIBLE);
                        holder.imgRateThree.setVisibility(View.VISIBLE);
                        holder.imgRateFour.setVisibility(View.VISIBLE);

                    }

                    if (Utility.isNullCheck(skillList.get(0).getActivty())) {
                        holder.tvSquash.setText(skillList.get(0).getActivty());
                        setRating(holder.imgRateOne, skillList.get(0).getLevel());
                    } else {
                        holder.tvSquash.setVisibility(View.GONE);
                        holder.imgRateOne.setVisibility(View.GONE);
                    }


                    if (Utility.isNullCheck(skillList.get(1).getActivty())) {
                        holder.tvParkour.setText(skillList.get(1).getActivty());
                        setRating(holder.imgRateTwo, skillList.get(1).getLevel());
                    } else {
                        holder.tvParkour.setVisibility(View.GONE);
                        holder.imgRateTwo.setVisibility(View.GONE);
                    }


                    if (Utility.isNullCheck(skillList.get(2).getActivty())) {
                        holder.tvYoga.setText(skillList.get(2).getActivty());
                        setRating(holder.imgRateThree, skillList.get(2).getLevel());
                    } else {
                        holder.tvYoga.setVisibility(View.GONE);
                        holder.imgRateThree.setVisibility(View.GONE);
                    }

                    if (Utility.isNullCheck(skillList.get(3).getActivty())) {
                        holder.tvactivtyfour.setText(skillList.get(3).getActivty());
                        setRating(holder.imgRateFour, skillList.get(3).getLevel());
                    } else {
                        holder.tvactivtyfour.setVisibility(View.GONE);
                        holder.imgRateFour.setVisibility(View.GONE);
                    }

                    break;

                case 5:
                    holder.tvSquash.setVisibility(View.VISIBLE);
                    holder.tvParkour.setVisibility(View.VISIBLE);
                    holder.tvYoga.setVisibility(View.VISIBLE);
                    holder.tvactivtyfour.setVisibility(View.VISIBLE);

                    if (buddylist.get(position).getUser_type() == USER_TYPE_APP) {

                        holder.imgRateOne.setVisibility(View.VISIBLE);
                        holder.imgRateTwo.setVisibility(View.VISIBLE);
                        holder.imgRateThree.setVisibility(View.VISIBLE);
                        holder.imgRateFour.setVisibility(View.VISIBLE);

                    }


                    if (Utility.isNullCheck(skillList.get(0).getActivty())) {
                        holder.tvSquash.setText(skillList.get(0).getActivty());
                        setRating(holder.imgRateOne, skillList.get(0).getLevel());
                    } else {
                        holder.tvSquash.setVisibility(View.GONE);
                        holder.imgRateOne.setVisibility(View.GONE);
                    }


                    if (Utility.isNullCheck(skillList.get(1).getActivty())) {
                        holder.tvParkour.setText(skillList.get(1).getActivty());
                        setRating(holder.imgRateTwo, skillList.get(1).getLevel());
                    } else {
                        holder.tvParkour.setVisibility(View.GONE);
                        holder.imgRateTwo.setVisibility(View.GONE);
                    }


                    if (Utility.isNullCheck(skillList.get(2).getActivty())) {
                        holder.tvYoga.setText(skillList.get(2).getActivty());
                        setRating(holder.imgRateThree, skillList.get(2).getLevel());
                    } else {
                        holder.tvYoga.setVisibility(View.GONE);
                        holder.imgRateThree.setVisibility(View.GONE);
                    }

                    if (Utility.isNullCheck(skillList.get(3).getActivty())) {
                        holder.tvactivtyfour.setText(skillList.get(3).getActivty());
                        setRating(holder.imgRateFour, skillList.get(3).getLevel());
                    } else {
                        holder.tvactivtyfour.setVisibility(View.GONE);
                        holder.imgRateFour.setVisibility(View.GONE);
                    }
                    break;

                case 6:
                    holder.tvSquash.setVisibility(View.VISIBLE);
                    holder.tvParkour.setVisibility(View.VISIBLE);
                    holder.tvYoga.setVisibility(View.VISIBLE);
                    holder.tvactivtyfour.setVisibility(View.VISIBLE);

                    if (buddylist.get(position).getUser_type() == USER_TYPE_APP) {
                        holder.imgRateOne.setVisibility(View.VISIBLE);
                        holder.imgRateTwo.setVisibility(View.VISIBLE);
                        holder.imgRateThree.setVisibility(View.VISIBLE);
                        holder.imgRateFour.setVisibility(View.VISIBLE);
                    }


                    if (Utility.isNullCheck(skillList.get(0).getActivty())) {
                        holder.tvSquash.setText(skillList.get(0).getActivty());
                        setRating(holder.imgRateOne, skillList.get(0).getLevel());
                    } else {
                        holder.tvSquash.setVisibility(View.GONE);
                        holder.imgRateOne.setVisibility(View.GONE);
                    }


                    if (Utility.isNullCheck(skillList.get(1).getActivty())) {
                        holder.tvParkour.setText(skillList.get(1).getActivty());
                        setRating(holder.imgRateTwo, skillList.get(1).getLevel());
                    } else {
                        holder.tvParkour.setVisibility(View.GONE);
                        holder.imgRateTwo.setVisibility(View.GONE);
                    }


                    if (Utility.isNullCheck(skillList.get(2).getActivty())) {
                        holder.tvYoga.setText(skillList.get(2).getActivty());
                        setRating(holder.imgRateThree, skillList.get(2).getLevel());
                    } else {
                        holder.tvYoga.setVisibility(View.GONE);
                        holder.imgRateThree.setVisibility(View.GONE);
                    }

                    if (Utility.isNullCheck(skillList.get(3).getActivty())) {
                        holder.tvactivtyfour.setText(skillList.get(3).getActivty());
                        setRating(holder.imgRateFour, skillList.get(3).getLevel());
                    } else {
                        holder.tvactivtyfour.setVisibility(View.GONE);
                        holder.imgRateFour.setVisibility(View.GONE);
                    }

                    break;

                default:
                    break;
            }


            holder.buddyUpParentLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    if (!AppConstants.isGestLogin(mContext)) {
                        Utility.setEventTracking(mContext, "Buddy Up Dashboard", "Buddy Up profile on Buddy up dashboard");
                    } else {
                        Utility.setEventTracking(mContext, "Buddy Up Dashboard", "Buddy Up profile on Guest login Buddy up dashboard");
                    }

                    if (buddylist.size() > 0)
                        if (buddylist.get(position).getUser_type() == USER_TYPE_APP) {
                            intent = new Intent(mContext, BuddyUpDetailsActivity.class);
                        } else if (buddylist.get(position).getUser_type() == USER_TYPE_BUSINESS) {
                            intent = new Intent(mContext, BusinessDetailsActivity.class);
                        }
                    Log.e("RatingJson *** ", "" + ((MainActivity) mContext).checkRatingJson);
                    intent.putExtra("checkRating", "" + ((MainActivity) mContext).checkRatingJson);
                    intent.putExtra("badgetype", buddylist.get(position).getImage() + "");
                    intent.putExtra("isfav", buddylist.get(position).getIsfav() + "");
                    intent.putExtra("position", position);
                    intent.putExtra("userid", buddylist.get(position).getUserid() + "");
                    intent.putExtra("userfbid", buddylist.get(position).getFacebookId());
                    BuddyModel buddydetails = buddylist.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(serialKeyBuddy, buddydetails);
                    bundle.putString("awaylocation",String.valueOf(away)+"km away");
                    intent.putExtras(bundle);

                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                }
            });
        }
    }


    void setRating(ImageView view, String rating) {
        switch (rating) {

            case "1":
                view.setImageResource(R.drawable.ic_star_one);
                break;
            case "2":
                view.setImageResource(R.drawable.ic_two_star);
                break;
            case "3":
                view.setImageResource(R.drawable.ic_three_star);
                break;
            default:

                break;
        }
    }


    public void buddyNameFilter(String text) {
        buddylist.clear();
        // Toast.makeText(getActivity(), "size"+all_temp_buddyupArrayList.size(), Toast.LENGTH_SHORT).show();
        text = text.toLowerCase(Locale.getDefault());
        if (text.length() == 0) {
            buddylist.addAll(all_temp_buddyupArrayList);
            notifyDataSetChanged();
        } else {
            for (BuddyModel buddyModel : all_temp_buddyupArrayList) {
                Log.e("Adapter", "" + buddyModel.getName().toLowerCase(Locale.getDefault()) + " size :" + buddylist.size());
                if (buddyModel.getName().toLowerCase(Locale.getDefault()).contains(text)) {
                    buddylist.add(buddyModel);
                    notifyDataSetChanged();
                }
            }
        }
    }

    public void buddySkillFilter(String skill) {
        buddylist.clear();
        // Toast.makeText(getActivity(), "size"+all_temp_buddyupArrayList.size(), Toast.LENGTH_SHORT).show();
        //text = text.toLowerCase(Locale.getDefault());
        if (skill.length() == 0) {
            buddylist.addAll(all_temp_buddyupArrayList);
            notifyDataSetChanged();
        } else {
            for (BuddyModel buddyModel : all_temp_buddyupArrayList) {
                //Log.e("Adapter",""+buddyModel.getName().toLowerCase(Locale.getDefault())+" size :" +buddylist.size());
                ArrayList<SkillDo> skillDo = buddyModel.getSkillDo();
                for (int i = 0; i < skillDo.size(); i++) {
                    if (skill.equals(skillDo.get(i).getActivty())) {
                        buddylist.add(buddyModel);
                        notifyDataSetChanged();
                    }
                }
            }
        }
    }
    /*@Override
    public int getItemViewType(int position) {
        return buddylist.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }*/

}





