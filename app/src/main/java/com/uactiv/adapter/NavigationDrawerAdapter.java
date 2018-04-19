package com.uactiv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.model.NavDrawerItem;
import com.uactiv.utils.FontProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private View view;
    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
    public void delete(ArrayList<NavDrawerItem> data)
    {

        this.data = data;
        data.clear();
    }
    public void notifyWithNewDataSet(ArrayList<NavDrawerItem> data)
    {
        Log.e("aa", "notify");
        this.data = data;
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_sliding, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.img.setImageResource(current.getSliderimg());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            img = (ImageView)itemView.findViewById(R.id.imgMenu);
            title.setTypeface(FontProvider.getBrandonMedFont());
        }
    }
}
