package com.uactiv.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.uactiv.R;
import com.uactiv.activity.CreatePickUpMapDetails;
import com.uactiv.activity.PickUpEventPage;
import com.uactiv.model.PickUpCategory;
import com.uactiv.utils.Utility;

import java.util.ArrayList;

/**
 * Created by buvaneswaran on 12/15/2015.
 */
public class PickupMapDetailAdapter extends RecyclerView.Adapter<PickupMapDetailAdapter.ViewHolder> {

    ArrayList<PickUpCategory> pickUpCategories = new ArrayList<PickUpCategory>();
    Context mContext;
    //GridLayoutManager layoutManager = null;
    String activityName;
    int pickupmodelItempos;
    @Override
    public int getItemCount() {
        return pickUpCategories.size();
    }

    public PickupMapDetailAdapter(Context context, ArrayList<PickUpCategory> item, String activityName, int pickupmodelItempos) {
        this.pickUpCategories = item;
        this.mContext = context;
        this.activityName=activityName;
        this.pickupmodelItempos =pickupmodelItempos;
        //layoutManager = new GridLayoutManager(mContext,2);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mapview_inflate, parent, false);
        return new ViewHolder(v);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView mapInflateTitle, inNoofPeople, inAwayFrom, inTime;
        CircleImageView tv_avathar;
        RelativeLayout root_lay ;

        public ViewHolder(View itemView) {
            super(itemView);


            mapInflateTitle = (TextView)itemView. findViewById(R.id.tv_name);
            inNoofPeople = (TextView) itemView.findViewById(R.id.tv_spot);
            inAwayFrom = (TextView) itemView.findViewById(R.id.tv_time);
            inTime = (TextView)itemView.findViewById(R.id.tv_times);
            tv_avathar=(CircleImageView)itemView.findViewById(R.id.img_logo);
            root_lay = (RelativeLayout) itemView.findViewById(R.id.root_lay);



        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        final PickUpCategory singledata = pickUpCategories.get(position);

        String date = singledata.getEventdate();
       // String converteddate = Utility.dateFormats(date);
        String converteddate = date;
        String awayfrom = singledata.getAwayfrom();
        String time = singledata.getStarttime();
        int away = (int) Double.parseDouble(awayfrom);
        if (Utility.isNullCheck(time)) {
            holder.inAwayFrom.setText(away + " km away");
        }

        if (Utility.isNullCheck(activityName)) {

            holder.mapInflateTitle.setText(activityName);
        }
        if (Utility.isNullCheck(singledata.getNoofpeople())) {

            if(singledata.getNoofpeople().equals("0")){

                holder.inNoofPeople.setText("Unlimited spots");
            }else {
                holder.inNoofPeople.setText(singledata.getNoofpeople() + " spots");
            }


        }

        holder.inTime.setText(date + " @ " + time.substring(0, time.length() - 3));


        /*Picasso.with(mContext)
                .load(singledata.getImage())
                .skipMemoryCache()
                .placeholder(R.drawable.ic_profile).centerCrop().fit()
                .into(holder.tv_avathar);
*/
        if(Utility.isNullCheck(singledata.getImage())) {
            Utility.setImageUniversalLoader(mContext, singledata.getImage(), holder.tv_avathar);
        }

        holder.root_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.setEventTracking(mContext,"See all pick Ups on list view",singledata.getPickupid());

                CreatePickUpMapDetails.isFlagSingle = false;
                Intent intent = new Intent(mContext, PickUpEventPage.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("PICKUP", singledata);
                bundle.putSerializable("Activity", activityName);
                intent.putExtra("from_map",true);
                intent.putExtra("position", "" + pickupmodelItempos);
                intent.putExtra("CategoryItemPosition",""+position);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });
    }















}
