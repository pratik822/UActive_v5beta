package com.uactiv.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.uactiv.R;
import com.uactiv.activity.CreatePickUpMapDetails;
import com.uactiv.activity.PickUpEventPage;
import com.uactiv.model.PickUpModel;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;

import java.util.ArrayList;

public class PickUpAdapter extends RecyclerView.Adapter<PickUpAdapter.ViewHolder> {

    ArrayList<PickUpModel> pickupList = new ArrayList<PickUpModel>();
    ArrayList<PickUpModel> all_temp_pickupList = new ArrayList<>();
    Context mContext;

    int itemposition = 0;

    public PickUpAdapter(Context context, ArrayList<PickUpModel> item) {
        this.pickupList = item;
        this.mContext = context;
        this.all_temp_pickupList.addAll(item);
    }

    @Override
    public int getItemCount() {
        return pickupList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_pickup, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final PickUpModel pickUpRow = pickupList.get(position);
        if (Utility.isNullCheck(pickUpRow.getActivityname())) {
            holder.tv_title.setText(pickUpRow.getActivityname());
        }

        switch (pickupList.get(position).getPickUpCategoryList().size()) {

            case 1:
                //update 1st row
                if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(0).getEventdate())) {
                    String time = pickupList.get(position).getPickUpCategoryList().get(0).getStarttime();
                    String date = pickupList.get(position).getPickUpCategoryList().get(0).getEventdate();
                    String awayfrom = pickupList.get(position).getPickUpCategoryList().get(0).getAwayfrom();
                    //String converteddate = Utility.dateFormats(date);

                    //String convertedtime = Utility.timeFormatChanage(time);
                  //  time = Utility.timeFormat24(time);
                    try {
                        int away = (int) Double.parseDouble(awayfrom);

                        if (Utility.isNullCheck(time)) {
                            holder.tv_name.setText(date + " @ " + time.substring(0, time.length() - 3) + "\n" + away + " km away");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople())) {
                    if (pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople().equals("0")) {
                        holder.tv_spot.setText("Unlimited spots");
                    } else {
                        if (pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople().equalsIgnoreCase("1")) {
                            holder.tv_spot.setText(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople() + " spot");
                        } else {
                            holder.tv_spot.setText(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople() + " spots");
                        }
                    }
                }

                if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(0).getImage())) {
                    Utility.setImageUniversalLoader(mContext, pickupList.get(position).getPickUpCategoryList().get(0).getImage(), holder.imageProfile1);
                }
                break;
            case 2:

                //update 1st row

                if (pickupList.get(position).getPickUpCategoryList().size() >= 2) {

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(0).getEventdate())) {

                        String time = pickupList.get(position).getPickUpCategoryList().get(0).getStarttime();
                        String date = pickupList.get(position).getPickUpCategoryList().get(0).getEventdate();
                        String awayfrom = pickupList.get(position).getPickUpCategoryList().get(0).getAwayfrom();

                     //   String converteddate = Utility.dateFormats(date);

                      //  time = Utility.timeFormat24(time);

                        //String convertedtime = Utility.timeFormatChanage(time);

                        int away = (int) Double.parseDouble(awayfrom);

                        if (Utility.isNullCheck(time)) {

                            holder.tv_name.setText(date + " @ " + time.substring(0, time.length() - 3) + "\n" + away + " km away");
                        }

                    }

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople())) {

                        if (pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople().equals("0")) {

                            holder.tv_spot.setText("Unlimited spots");
                        } else {

                            if (pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople().equalsIgnoreCase("1")) {

                                holder.tv_spot.setText(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople() + " spot");
                            } else {
                                holder.tv_spot.setText(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople() + " spots");
                            }

                        }
                    }

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(0).getImage())) {
                        Utility.setImageUniversalLoader(mContext, pickupList.get(position).getPickUpCategoryList().get(0).getImage(), holder.imageProfile1);
                    }

                    holder.pickUpListrow_two.setVisibility(View.VISIBLE);

                    //update 2nd row


                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(1).getEventdate())) {

                        String time = pickupList.get(position).getPickUpCategoryList().get(1).getStarttime();
                        String date = pickupList.get(position).getPickUpCategoryList().get(1).getEventdate();
                        String awayfrom = pickupList.get(position).getPickUpCategoryList().get(1).getAwayfrom();

                       // String converteddate = Utility.dateFormats(date);

                        //time = Utility.timeFormat24(time);
                        //String convertedtime = Utility.timeFormatChanage(time);

                        int away = (int) Double.parseDouble(awayfrom);

                        if (Utility.isNullCheck(time)) {

                            holder.tv_name1.setText(date + " @ " + time.substring(0, time.length() - 3) + "\n" + away + " km away");
                        }

                    }

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople())) {

                        if (pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople().equals("0")) {

                            holder.tv_spot1.setText("Unlimited spots");
                        } else {

                            if (pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople().equalsIgnoreCase("1")) {

                                holder.tv_spot1.setText(pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople() + " spot");
                            } else {
                                holder.tv_spot1.setText(pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople() + " spots");
                            }

                        }

                    }

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(1).getImage())) {

                        Utility.setImageUniversalLoader(mContext, pickupList.get(position).getPickUpCategoryList().get(1).getImage(), holder.imageProfile2);
                    }

                }
                break;

            default:

                if (pickupList.get(position).getPickUpCategoryList().size() >= 2) {

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(0).getEventdate())) {

                        String time = pickupList.get(position).getPickUpCategoryList().get(0).getStarttime();
                        String date = pickupList.get(position).getPickUpCategoryList().get(0).getEventdate();
                        String awayfrom = pickupList.get(position).getPickUpCategoryList().get(0).getAwayfrom();

                       // String converteddate = Utility.dateFormats(date);

                        //String convertedtime = Utility.timeFormatChanage(time);

                       // time = Utility.timeFormat24(time);

                        int away = (int) Double.parseDouble(awayfrom);

                        if (Utility.isNullCheck(time)) {
                         //   holder.tv_name.setText(converteddate + " @ " + time.substring(0, time.length() - 3) + "\n" + away + " km away");
                            holder.tv_name.setText(date + " @ " + time.substring(0, time.length() - 3) + "\n" + away + " km away");
                        }

                    }

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople())) {
                        //holder.tv_spot.setText(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople()+ " spots");

                        if (pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople().equals("0")) {

                            holder.tv_spot.setText("Unlimited spots");
                        } else {

                            if (pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople().equalsIgnoreCase("1")) {

                                holder.tv_spot.setText(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople() + " spot");
                            } else {
                                holder.tv_spot.setText(pickupList.get(position).getPickUpCategoryList().get(0).getNoofpeople() + " spots");
                            }

                        }

                    }

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(0).getImage())) {
                        Utility.setImageUniversalLoader(mContext, pickupList.get(position).getPickUpCategoryList().get(0).getImage(), holder.imageProfile1);
                    }

                    holder.pickUpListrow_two.setVisibility(View.VISIBLE);

                    //update 2nd row


                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(1).getEventdate())) {

                        String time = pickupList.get(position).getPickUpCategoryList().get(1).getStarttime();
                        String date = pickupList.get(position).getPickUpCategoryList().get(1).getEventdate();
                        String awayfrom = pickupList.get(position).getPickUpCategoryList().get(1).getAwayfrom();

                      //  String converteddate = Utility.dateFormats(date);

                        //String convertedtime = Utility.timeFormatChanage(time);
                      //  time = Utility.timeFormat24(time);

                        int away = (int) Double.parseDouble(awayfrom);

                        if (Utility.isNullCheck(time)) {

                            holder.tv_name1.setText(date + " @ " + time.substring(0, time.length() - 3) + "\n" + away + " km away");
                        }

                    }

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople())) {

                        //holder.tv_spot1.setText(pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople()+ " spots");

                        if (pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople().equals("0")) {

                            holder.tv_spot1.setText("Unlimited spots");
                        } else {

                            if (pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople().equalsIgnoreCase("1")) {

                                holder.tv_spot1.setText(pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople() + " spot");
                            } else {
                                holder.tv_spot1.setText(pickupList.get(position).getPickUpCategoryList().get(1).getNoofpeople() + " spots");
                            }

                        }
                    }

                    if (Utility.isNullCheck(pickupList.get(position).getPickUpCategoryList().get(1).getImage())) {
                        Utility.setImageUniversalLoader(mContext, pickupList.get(position).getPickUpCategoryList().get(1).getImage(), holder.imageProfile2);
                    }

                }
                break;
        }


        holder.tv_seeAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Calling","1");
                if (!AppConstants.isGestLogin(mContext)) {
                    Utility.setEventTracking(mContext,"Pickup Dashboard", AppConstants.EVENT_TRACKING_ID_PICKUPSEEALL);
                }else{
                    Utility.setEventTracking(mContext,"Pickup Dashboard","See all pickup button on Guest login pickup dashboard");
                }

                Intent intent = new Intent(mContext, CreatePickUpMapDetails.class);
                Bundle bundle = new Bundle();
                //  bundle.putSerializable("PICKUP", pickUpRow.getPickUpCategoryList()); changes on 21/
                bundle.putSerializable("PICKUP", pickUpRow);
                bundle.putSerializable("Activity",pickUpRow.getActivityname());
                intent.putExtras(bundle);
                intent.putExtra("position", "" + position);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        holder.pickUpListrow_one.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Calling","2");
                CreatePickUpMapDetails.isFlagSingle = false; // this flag for update single row.
                Intent intent = new Intent(mContext, PickUpEventPage.class);
                intent.putExtra("position", "" + position);
                intent.putExtra("SheduleId",pickUpRow.getPickUpCategoryList().get(0).getIdschedule());
                intent.putExtra("iduser",pickUpRow.getPickUpCategoryList().get(0).getIduser());
                intent.putExtra("CategoryItemPosition", "" + 0); // this is to find which item position clicked
                Bundle bundle = new Bundle();
                bundle.putSerializable("PICKUP", pickUpRow.getPickUpCategoryList().get(0));
                bundle.putSerializable("Activity", pickUpRow.getActivityname());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });

        holder.pickUpListrow_two.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Calling","3");
                CreatePickUpMapDetails.isFlagSingle = false; // this flag for update single row.
                Intent intent = new Intent(mContext, PickUpEventPage.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("PICKUP", pickUpRow.getPickUpCategoryList().get(1));
                intent.putExtra("SheduleId",pickUpRow.getPickUpCategoryList().get(1).getIdschedule());
                intent.putExtra("iduser",pickUpRow.getPickUpCategoryList().get(1).getIduser());
                bundle.putSerializable("Activity", pickUpRow.getActivityname());
                intent.putExtra("position", "" + position);
                intent.putExtra("CategoryItemPosition", "" + 1);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
            }
        });
    }

    public void pickupSkillFilter(String search_text) {

        pickupList.clear();
        // Toast.makeText(getActivity(), "size"+all_temp_buddyupArrayList.size(), Toast.LENGTH_SHORT).show();

        //text = text.toLowerCase(Locale.getDefault());

        if (search_text.length() == 0) {

            pickupList.addAll(all_temp_pickupList);
            notifyDataSetChanged();

        } else {

            for (PickUpModel pickUpModel : all_temp_pickupList) {

                //Log.e("Adapter",""+buddyModel.getName().toLowerCase(Locale.getDefault())+" size :" +buddylist.size());

                if (search_text.equals(pickUpModel.getActivityname())) {
                    pickupList.add(pickUpModel);
                }

            }
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        public TextView tv_title, tv_name, tv_spot, tv_name1, tv_spot1;
        public RelativeLayout parenLayout;
        CustomButton tv_seeAll;
        CircleImageView imageProfile1, imageProfile2;
        RelativeLayout pickUpListrow_two, pickUpListrow_one = null;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_spot = (TextView) itemView.findViewById(R.id.tv_spot);
            tv_name1 = (TextView) itemView.findViewById(R.id.tv_name1);
            tv_spot1 = (TextView) itemView.findViewById(R.id.tv_spot1);
            tv_seeAll = (CustomButton) itemView.findViewById(R.id.tv_seeAll);
            imageProfile1 = (CircleImageView) itemView.findViewById(R.id.img_logo);
            imageProfile2 = (CircleImageView) itemView.findViewById(R.id.img_logo1);
            parenLayout = (RelativeLayout) itemView.findViewById(R.id.parenLayout);
            pickUpListrow_two = (RelativeLayout) itemView.findViewById(R.id.pickUpListrow_two);
            pickUpListrow_one = (RelativeLayout) itemView.findViewById(R.id.pickUpListrow_one);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.pickUpListrow_one:
                    itemposition = 0;
                    break;
                case R.id.pickUpListrow_two:
                    itemposition = 1;
                    break;
                default:
                    break;
            }
        }
    }
}
