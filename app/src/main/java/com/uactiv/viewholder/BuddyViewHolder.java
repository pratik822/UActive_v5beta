package com.uactiv.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.activity.MyImageview;
import com.uactiv.views.CircularImageViews;


/**
 * Created by moorthy on 3/4/2016.
 */
public class BuddyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public static int itemposition = 0;
    public TextView tvName, tvEarlyDistance, tvAwayDistance, tvSquash, tvactivtyfour;
    public TextView squashRating, tvParkour, tvPrakourRating, tvTypes, tvYoga;
    public ImageView imageType;
    public MyImageview imageProfile;
    public ImageView imgRateOne = null;
    public ImageView imgRateTwo = null;
    public ImageView imgRateThree = null;
    public ImageView imgRateFour = null;
    public RelativeLayout buddyUpParentLayout;

    public BuddyViewHolder(View itemView) {
        super(itemView);

        tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvEarlyDistance = (TextView) itemView.findViewById(R.id.tvEarlyDistance);
        tvAwayDistance = (TextView) itemView.findViewById(R.id.tvAwayDistance);

        tvSquash = (TextView) itemView.findViewById(R.id.tvSquash);
        //tvSquash.setVisibility(View.GONE);
        tvParkour = (TextView) itemView.findViewById(R.id.tvParkour);
        /// tvParkour.setVisibility(View.GONE);
        tvTypes = (TextView) itemView.findViewById(R.id.tvTypes);
        tvYoga = (TextView) itemView.findViewById(R.id.tvYoga);
        ///tvYoga.setVisibility(View.GONE);
        tvactivtyfour = (TextView) itemView.findViewById(R.id.tvactivtyfour);
        // tvactivtyfour.setVisibility(View.GONE);
        imageType = (ImageView) itemView.findViewById(R.id.imageType);
        imageProfile = (MyImageview) itemView.findViewById(R.id.imageProfile);
        buddyUpParentLayout = (RelativeLayout) itemView.findViewById(R.id.parentLayout);
        imgRateOne = (ImageView) itemView.findViewById(R.id.imag_act_one);
        imgRateTwo = (ImageView) itemView.findViewById(R.id.imag_act_two);
        imgRateThree = (ImageView) itemView.findViewById(R.id.imag_act_three);
        imgRateFour = (ImageView) itemView.findViewById(R.id.imag_act_four);

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
