package com.uactiv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.model.BuddyModel;

import java.util.ArrayList;


public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    ArrayList<BuddyModel> productList = new ArrayList<BuddyModel>();
    Context mContext;

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public GroupAdapter (Context context, ArrayList<BuddyModel> item) {
        this.productList = item;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_buddy, parent, false);
        return new ViewHolder(v);
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvEarlyDistance, tvAwayDistance,  tvSquash;
        public TextView squashRating, tvParkour, tvPrakourRating, tvTypes, tvYoga;
        private ImageView imageType, imageProfile;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvEarlyDistance = (TextView) itemView.findViewById(R.id.tvEarlyDistance);
            tvAwayDistance = (TextView) itemView.findViewById(R.id.tvAwayDistance);
//            /tvPeople = (TextView) itemView.findViewById(R.id.tvPeople);
            tvSquash = (TextView) itemView.findViewById(R.id.tvSquash);
            tvParkour = (TextView) itemView.findViewById(R.id.tvParkour);
            tvTypes = (TextView) itemView.findViewById(R.id.tvTypes);
            tvYoga = (TextView) itemView.findViewById(R.id.tvYoga);
            imageType = (ImageView) itemView.findViewById(R.id.imageType);
            imageProfile = (ImageView)itemView.findViewById(R.id.imageProfile);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

    }
}


