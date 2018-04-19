package com.uactiv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.uactiv.R;
import com.uactiv.model.MutualFriendData;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.OnRecyclerItemClickListener;
import com.uactiv.utils.Utility;

import java.util.ArrayList;

/**
 * Created by rameshg on 3/17/2017.
 */
public class MutualFriendAdapter extends RecyclerView.Adapter<MutualFriendAdapter.ViewHolder> implements AppConstants.urlConstants {

    ArrayList<MutualFriendData> mutualFriendList = new ArrayList<MutualFriendData>();
    Context mContext;
    private OnRecyclerItemClickListener mItemClickListener;

    @Override
    public int getItemCount() {
        return mutualFriendList.size();
    }

    public MutualFriendAdapter(Context context, ArrayList<MutualFriendData> item) {
        this.mutualFriendList = item;
        this.mContext = context;
        Utility.setEventTracking(mContext,"Buddy Profile details","mutual friend profiles on Buddy profile detail screen");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attending_custom_people, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


       /* holder.imgPeoples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                *//*if(Utility.isNullCheck(mutualFriendList.get(position).get())){
                    Intent intent_buddydetails = new Intent(mContext, BuddyUpDetailsActivity.class);
                    intent_buddydetails.putExtra("view", true);
                    intent_buddydetails.putExtra("userid", mutualFriendList.get(position).getId());
                    intent_buddydetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent_buddydetails);
                }*//*
            }
        });*/




        if (Utility.isNullCheck(mutualFriendList.get(position).getUrl())) {
            Utility.setImageUniversalLoaders(mContext, mutualFriendList.get(position).getUrl(), holder.imgPeoples);
            Log.d("getimgurl", mutualFriendList.get(position).getUrl());
        }
        holder.tvPeopleName.setText(mutualFriendList.get(position).getName());
        if (mutualFriendList.get(position).getId().equalsIgnoreCase("0")) {
            holder.ivselectorLogo.setVisibility(View.INVISIBLE);
        }else{
            holder.ivselectorLogo.setVisibility(View.VISIBLE);
        }
        /*if(!mutualFriendList.get(position).getId().equalsIgnoreCase("0")){
            //Set the smalla icon
            Log.i(""," ID ->"+mutualFriendList.get(position).getId());
        }else{
            //hide the icon
        }*/

    }

    public OnRecyclerItemClickListener getmItemClickListener() {
        return mItemClickListener;
    }

    public void setmItemClickListener(OnRecyclerItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvPeopleName;
        CircleImageView imgPeoples;
        ImageView ivselectorLogo;

        public ViewHolder(View itemView) {
            super(itemView);
            imgPeoples = (CircleImageView) itemView.findViewById(R.id.imgPeoples);
            tvPeopleName = (TextView) itemView.findViewById(R.id.tvPeopleName);
            ivselectorLogo = (ImageView) itemView.findViewById(R.id.ivselectorLogo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onRecyclerItemClick(getPosition());
            }
        }
    }


}
