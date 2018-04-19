package com.uactiv.adapter;

import android.support.v7.widget.RecyclerView;

import com.uactiv.model.NotifyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public abstract class NotificationStickyBaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private ArrayList<NotifyModel> items = new ArrayList<NotifyModel>();

    public NotificationStickyBaseAdapter() {
        setHasStableIds(false);
    }

    public void add(NotifyModel object) {
        items.add(object);
        notifyDataSetChanged();
    }

    public void add(int index, NotifyModel object) {
        items.add(index, object);
        notifyDataSetChanged();
    }

    public void addAll(Collection<? extends NotifyModel> collection) {
        if (collection != null) {
            items.addAll(collection);
            notifyDataSetChanged();
        }
    }


    public void newNotification(ArrayList<NotifyModel> notifyModelArrayList){
        if (notifyModelArrayList != null) {
            items = notifyModelArrayList;
            notifyDataSetChanged();
        }
    }
    public void addAll(NotifyModel... items) {
        addAll(Arrays.asList(items));
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void remove(NotifyModel object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public void remove(int object) {
        items.remove(object);
        notifyDataSetChanged();
    }

    public NotifyModel getItem(int position) {
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