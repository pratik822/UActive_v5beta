package com.uactiv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.activity.PickUpGuest;
import com.uactiv.model.ScheduleModel;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> implements AppConstants.SharedConstants {

    ArrayList<ScheduleModel> productList = new ArrayList<ScheduleModel>();
    Context mContext;
    String date;

    public ScheduleAdapter(Context context, ArrayList<ScheduleModel> item) {
        this.productList = item;
        this.mContext = context;

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void notifyDataSetChanged(ArrayList<ScheduleModel> scheduleModelArrayList) {
        if (scheduleModelArrayList != null) {
            this.productList = scheduleModelArrayList;
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_schedule, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //holder.tvDate.setBackgroundColor(productList.get(position).getColor());

        switch (productList.get(position).getType()) {

            case "buddyup":

                if (Utility.isNullCheck(productList.get(position).getActivity())) {

                    if (productList.get(position).getIduser().equals(SharedPref.getInstance().getStringVlue(mContext, userId))) {

                        holder.tvDetails.setText(productList.get(position).getActivity() + "  with " + productList.get(position).getMember());

                    } else {
                        holder.tvDetails.setText(productList.get(position).getActivity() + "  with " + productList.get(position).getFirstname());
                    }

                }
                break;
            case "pickup":

                if (Utility.isNullCheck(productList.get(position).getActivity())) {

                    if (productList.get(position).getScheduleMemberDoArrayList() != null
                            && productList.get(position).getScheduleMemberDoArrayList().size() > 0) {

                        Log.e("Schedule", "" + productList.get(position).getScheduleMemberDoArrayList().get(0).getFirstname());
                        //Patch done by Jeeva on 2-02-16
                        if (productList.get(position).getIduser().equals(SharedPref.getInstance().getStringVlue(mContext, userId))) {
                            //Host login...
                            /*if (productList.get(position).getScheduleMemberDoArrayList().size() != 0) {

								holder.tvDetails.setText(productList.get(position).getActivity() + " with " + productList.get(position).getScheduleMemberDoArrayList().get(0).getFirstname() + " and " + (productList.get(position).getScheduleMemberDoArrayList().size()) + " more");
							}*/

                            //Patch by moorthy

                            if ((productList.get(position).getScheduleMemberDoArrayList().size() - 1) == 0) {

                                holder.tvDetails.setText(productList.get(position).getActivity() + " with " + productList.get(position).getScheduleMemberDoArrayList().get(0).getFirstname());

                            } else if ((productList.get(position).getScheduleMemberDoArrayList().size() - 1) > 0) {
                                holder.tvDetails.setText(productList.get(position).getActivity() + " with " + productList.get(position).getScheduleMemberDoArrayList().get(0).getFirstname() + " and " + (productList.get(position).getScheduleMemberDoArrayList().size() - 1) + " more");
                            }

                        } else {
                            //Attendee login...
                            /*if (productList.get(position).getScheduleMemberDoArrayList().size() != 0) {

								holder.tvDetails.setText(productList.get(position).getActivity() + " with " + productList.get(position).getFirstname() + " and " + (productList.get(position).getScheduleMemberDoArrayList().size()) + " more");
							}*/

                            //patch done by moorthy

                            if ((productList.get(position).getScheduleMemberDoArrayList().size() - 1) == 0) {

                                holder.tvDetails.setText(productList.get(position).getActivity() + " with " + productList.get(position).getFirstname());

                            } else if ((productList.get(position).getScheduleMemberDoArrayList().size() - 1) > 0) {

                                holder.tvDetails.setText(productList.get(position).getActivity() + " with " + productList.get(position).getFirstname() + " and " + (productList.get(position).getScheduleMemberDoArrayList().size()) + " more");
                            }

                        }
                        //Patch done by Jeeva on 2-02-16*/

						/*if((productList.get(position).getScheduleMemberDoArrayList().size()-1 ) == 0){

							holder.tvDetails.setText(productList.get(position).getActivity() + " with " + productList.get(position).getScheduleMemberDoArrayList().get(0).getFirstname());

						} else if (( productList.get(position).getScheduleMemberDoArrayList().size() - 1 ) > 0){
							holder.tvDetails.setText(productList.get(position).getActivity() + " with " + productList.get(position).getScheduleMemberDoArrayList().get(0).getFirstname() + " and "+ ( productList.get(position).getScheduleMemberDoArrayList().size() - 1 ) +" more");
						}*/
                    } else {
                        holder.tvDetails.setText(productList.get(position).getActivity());
                    }
                }

                break;
            default:
                break;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (Utility.isNullCheck("" + productList.get(position).getDate())) {
            Calendar c = Calendar.getInstance();
            String currDate = sdf.format(c.getTime());
            String dbDate = sdf.format(productList.get(position).getDate());
            Log.e("*** Curr Date" + currDate, "DB Date" + dbDate);
            if (currDate.equalsIgnoreCase(dbDate)) {
                holder.tvDate.setText("Today");
            } else {
                holder.tvDate.setText(Utility.dateFormatsdf.format(productList.get(position).getDate()));
            }
        }


        holder.tvDate.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));

        if (position == 0) {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setBackgroundColor(mContext.getResources().getColor(R.color.green));
        } else if (position > 0) {

            if (sdf.format(productList.get(position).getDate()).equals(sdf.format(productList.get(position - 1).getDate()))) {
                holder.tvDate.setVisibility(View.GONE);
            } else {
                holder.tvDate.setVisibility(View.VISIBLE);
            }
        }

        if (Utility.isNullCheck(productList.get(position).getStart_time())) {
            holder.tvTime.setText(Utility.timeFormat24(productList.get(position).getStart_time()));
        }


        holder.parentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PickUpGuest.class);
                intent.putExtra("from_schedule", true);
                intent.putExtra("fragment", "map");
                intent.putExtra("sstatus", "active");
                intent.putExtra("isUpComing", productList.get(position).isUpComing());
                intent.putExtra("idschedule", "" + productList.get(position).getIdschedule());
                intent.putExtra("position", "" + position);
                mContext.startActivity(intent);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDate, tvTime, tvDetails;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvDetails = (TextView) itemView.findViewById(R.id.tvDetails);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
        }
    }


}
