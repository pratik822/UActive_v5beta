package com.uactiv.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.uactiv.R;

/**
 * Created by moorthy on 9/13/2016.
 */
public class ProgressViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public ProgressViewHolder(View itemView) {
        super(itemView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
    }
}
