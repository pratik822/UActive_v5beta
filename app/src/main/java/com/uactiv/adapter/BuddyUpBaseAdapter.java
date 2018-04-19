package com.uactiv.adapter;

import android.support.v7.widget.RecyclerView;

import com.uactiv.model.BuddyModel;
import com.uactiv.viewholder.BuddyViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public abstract class BuddyUpBaseAdapter<VH extends BuddyViewHolder> extends RecyclerView.Adapter<BuddyViewHolder> {

    private ArrayList<BuddyModel> items = new ArrayList<BuddyModel>();

    public BuddyUpBaseAdapter() {
        setHasStableIds(false);
    }

    public void add(BuddyModel object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, BuddyModel object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends BuddyModel> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }


    public void newNotification(ArrayList<BuddyModel> notifyModelArrayList) {
        if (notifyModelArrayList != null) {
            items = notifyModelArrayList;
        }
    }

    public void addAll(BuddyModel... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(String object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public BuddyModel getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}