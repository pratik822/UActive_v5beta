package com.uactiv.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.uactiv.R;
import com.uactiv.widgets.CustomTextView;

/**
 * Created by moorthy on 3/14/2016.
 */
public class StikcyHeaderViewHolder extends RecyclerView.ViewHolder{
    public CustomTextView mTitle = null;
    public LinearLayout headerParentLay = null;

    public StikcyHeaderViewHolder(View view){
        super(view);
        mTitle  = (CustomTextView) view.findViewById(R.id.headerTitle);
        headerParentLay = (LinearLayout) view.findViewById(R.id.headerParentLay);
    }
}
