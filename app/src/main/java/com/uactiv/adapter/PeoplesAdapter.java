package com.uactiv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.uactiv.R;
import com.uactiv.model.FavouriteModel;
import com.uactiv.utils.Utility;

import java.util.ArrayList;

public class PeoplesAdapter extends RecyclerView.Adapter<PeoplesAdapter.ViewHolder> {

    ArrayList<FavouriteModel> productList = new ArrayList<FavouriteModel>();
    Context mContext;

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public PeoplesAdapter(Context context, ArrayList<FavouriteModel> item) {
        this.productList = item;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_people, parent, false);
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

        if(Utility.isNullCheck(productList.get(position).getImage())){
            Utility.setImageUniversalLoader(mContext,productList.get(position).getImage(),holder.imgPeoples);
        }

       /* Picasso.with(mContext)
                .load(productList.get(position).getImage())
                .placeholder(R.drawable.ic_profile).centerCrop().fit()
                .into(holder.imgPeoples);*/

        holder.tvPeopleName.setText(productList.get(position).getName());
    }

}