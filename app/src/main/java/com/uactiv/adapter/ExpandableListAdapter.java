package com.uactiv.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.uactiv.R;
import com.uactiv.activity.PickUpGuest;
import com.uactiv.controller.StatusChangedListener;
import com.uactiv.model.NotifyModel;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import java.util.ArrayList;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AppConstants.urlConstants {
    public static final int HEADER = 0;
    public static final int CHILD = 1;
    Context mContext;
    StatusChangedListener statusChangedListener = null;
    SparseIntArray sparseIntArray = null;
    private ArrayList<NotifyModel> productList;


    public ExpandableListAdapter(Context context, ArrayList<NotifyModel> item) {
        this.productList = item;
        this.mContext = context;
        this.sparseIntArray = new SparseIntArray();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);
        switch (type) {
            case HEADER:
                View viewHeader = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.inflate_notification_header_lay, parent, false);
                ViewHolderHeader viewHolderHeader = new ViewHolderHeader(viewHeader);
                return viewHolderHeader;
            case CHILD:
                View viewChild = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custom_notification, parent, false);
                ViewHolder viewHolderChild = new ViewHolder(viewChild);
                return viewHolderChild;
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final NotifyModel item = productList.get(position);
        switch (item.getNotificationType()) {
            case HEADER:
                ViewHolderHeader holderHeader = new ViewHolderHeader(viewHolder.itemView);
                holderHeader.tvHeaderTitle.setText(item.getNotificationTypeTitle());
                break;
            case CHILD:
                ViewHolder holder = new ViewHolder(viewHolder.itemView);
                holder.tvCircle.setText("");
                holder.tvCircle.setVisibility(View.GONE);

                if ((productList.get(position).getRequest_count() != 0 &&
                        (productList.get(position).getRequest_count() > 0))) {
                    holder.tvCircle.setVisibility(View.VISIBLE);
                    holder.tvCircle.setText("" + productList.get(position).getRequest_count());
                }


                holder.countCircle.setText("");
                holder.countCircle.setVisibility(View.GONE);
                holder.tvDetails.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));


                if ((productList.get(position).getMsg_count() != 0 &&
                        productList.get(position).getMsg_count() > 0) || productList.get(position).isRead()) {

                    if (productList.get(position).getMsg_count() > 0) {
                        holder.countCircle.setVisibility(View.VISIBLE);
                        holder.countCircle.setText("" + productList.get(position).getMsg_count());
                    }
                    holder.tvDetails.setTextColor(mContext.getResources().getColor(R.color.green));
                }


                holder.tv_attending_count.setText("");
                holder.attending_cout_image.setVisibility(View.GONE);
                holder.tv_attending_count.setVisibility(View.GONE);


                if (productList.get(position).getAttending_count() != 0 &&
                        productList.get(position).getAttending_count() > 0) {

                    holder.attending_cout_image.setVisibility(View.VISIBLE);
                    holder.tv_attending_count.setVisibility(View.VISIBLE);
                    holder.tv_attending_count.setText("+" + productList.get(position).getAttending_count() + "".trim());
                }


                holder.tvGame.setText(productList.get(position).getGameDescription());
                holder.tvDetails.setText(productList.get(position).getMessage());


                Utility.setImageUniversalLoader(mContext, productList.get(position).getImage(), holder.imageProfile);

                holder.gameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(mContext, PickUpGuest.class);
                        intent.putExtra("from_schedule", false);
                        intent.putExtra("fragment", "map");
                        intent.putExtra("position", position + "");
                        intent.putExtra("status", productList.get(position).getStatus());
                        intent.putExtra("idschedule", productList.get(position).getIdschedule());
                        intent.putExtra("sstatus", productList.get(position).getIsActive());
                        intent.putExtra("attending_id", productList.get(position).getAccepted_id());
                        intent.putExtra("idUser", productList.get(position).getIdUser());
                        intent.putExtra("isUpComing", productList.get(position).isUpComing());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("notify_model", productList.get(position));
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);


                    }
                });
                holder.detailsLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //	if(Utility.isNullCheck(productList.get(position).getAccepted_id())){


                        if (productList.get(position).getStatus() != null) {

                            Intent intent = new Intent(mContext, PickUpGuest.class);
                            intent.putExtra("from_schedule", false);
                            intent.putExtra("fragment", "chat");
                            intent.putExtra("position", position + "");
                            intent.putExtra("status", productList.get(position).getStatus());
                            intent.putExtra("idschedule", productList.get(position).getIdschedule());
                            intent.putExtra("sstatus", productList.get(position).getIsActive());
                            intent.putExtra("attending_id", productList.get(position).getAccepted_id());
                            intent.putExtra("isUpComing", productList.get(position).isUpComing());
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("notify_model", productList.get(position));
                            intent.putExtras(bundle);


                            if(productList.get(position).isUpComing()) {
                                switch (productList.get(position).getType()) {

                                    case KEY_BUDDY_UP:

                                        if (productList.get(position).getStatus().equals(KEY_ACCEPTED)) {

                                            mContext.startActivity(intent);
                                        } else {
                                            Utility.showToastMessage(mContext, "You are not in " + productList.get(position).getType() + " chat group!");
                                        }

                                        break;
                                    case KEY_PICK_UP:

                                        if (Utility.isNullCheck(productList.get(position).getAccepted_id())) {

                                            if (productList.get(position).getStatus().equals(KEY_CREATED) || productList.get(position).getStatus().equals(KEY_ACCEPTED)) {

                                                mContext.startActivity(intent);
                                            } else {
                                                Utility.showToastMessage(mContext, "You are not in " + productList.get(position).getType() + " chat group!");
                                            }
                                        } else {
                                            Utility.showToastMessage(mContext, "No People are added in Pick Up!");
                                        }


                                        break;
                                    default:
                                        break;
                                }
                            }else {
                                mContext.startActivity(intent);
                            }
                        }



				/*}else {
                    Utility.showToastMessage(mContext,"You are not in "+productList.get(position).getType()+" chat group!");
				}*/


                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        return productList.get(position).getNotificationType();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setNewNotifications(ArrayList<NotifyModel> items) {
        if (items != null) {
            productList = items;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvGame, tvCircle, tvDetails, countCircle;
        RelativeLayout parentLayout, gameLayout, detailsLayout;
        CircleImageView imageProfile;
        ImageView attending_cout_image = null;
        CustomTextView tv_attending_count = null;

        public ViewHolder(View itemView) {
            super(itemView);
            tvGame = (TextView) itemView.findViewById(R.id.tvGame);
            tvCircle = (TextView) itemView.findViewById(R.id.tvCircle);
            tvDetails = (TextView) itemView.findViewById(R.id.tvDetails);
            countCircle = (TextView) itemView.findViewById(R.id.countCircle);
            imageProfile = (CircleImageView) itemView.findViewById(R.id.imgProfile);
            parentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
            gameLayout = (RelativeLayout) itemView.findViewById(R.id.gameLayout);
            detailsLayout = (RelativeLayout) itemView.findViewById(R.id.detailsLayout);
            attending_cout_image = (ImageView) itemView.findViewById(R.id.imgCount);
            tv_attending_count = (CustomTextView) itemView.findViewById(R.id.tv_attending_count);

        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {


        CustomTextView tvHeaderTitle = null;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            tvHeaderTitle = (CustomTextView) itemView.findViewById(R.id.headerTitle);


        }
    }

}