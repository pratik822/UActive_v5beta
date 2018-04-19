package com.uactiv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.uactiv.R;
import com.uactiv.activity.BuddyUpDetailsActivity;
import com.uactiv.model.FavouriteModel;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.Utility;

import java.util.ArrayList;


public class AttendingPeoplesAdapter extends RecyclerView.Adapter<AttendingPeoplesAdapter.ViewHolder> implements AppConstants.urlConstants {

    ArrayList<FavouriteModel> productList = new ArrayList<FavouriteModel>();
    Context mContext;

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public AttendingPeoplesAdapter(Context context, ArrayList<FavouriteModel> item) {
        this.productList = item;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attending_custom_peopless, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPeopleName;
        CircleImageView imgPeoples;

        public ViewHolder(View itemView) {
            super(itemView);
            imgPeoples = (CircleImageView) itemView.findViewById(R.id.imgPeoples);
            tvPeopleName = (TextView) itemView.findViewById(R.id.tvPeopleName);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        holder.imgPeoples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Utility.isNullCheck(productList.get(position).getId())){
                    Intent intent_buddydetails = new Intent(mContext, BuddyUpDetailsActivity.class);
                    intent_buddydetails.putExtra("view", true);
                    intent_buddydetails.putExtra("pickupevent", "pickupevent");
                    intent_buddydetails.putExtra("userid", productList.get(position).getId());
                    mContext.startActivity(intent_buddydetails);
                }
            }
        });


        if(Utility.isNullCheck(productList.get(position).getImage())){

            Utility.setImageUniversalLoader(mContext,productList.get(position).getImage(),holder.imgPeoples);
        }



        holder.tvPeopleName.setText(productList.get(position).getName());
    }

}