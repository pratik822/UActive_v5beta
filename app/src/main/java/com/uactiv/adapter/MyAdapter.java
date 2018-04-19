package com.uactiv.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.activity.BuddyUpDetailsActivity;
import com.uactiv.model.FavouriteModel;
import com.uactiv.utils.Utility;
import com.uactiv.views.CircularImageViews;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MyAdapter extends ArrayAdapter<FavouriteModel> {

    private final Activity context;
    NotifiyListener notifiyListener = null;
    SparseBooleanArray mSparseBooleanArrayChecked = null;
    ArrayList<String> addeditemCount = new ArrayList<String>();
    int leftspot = 0;
    int no_of_spots = 0;
    View mTempView;
    String TAG = "MyAdapter";
    onSelectAllItemClickListener mOnSelectAllItemClickListener = null;
    private ArrayList<FavouriteModel> list = new ArrayList<FavouriteModel>();

    public MyAdapter(Activity context, ArrayList<FavouriteModel> list, int leftspot, int no_of_spots) {
        super(context, R.layout.cons_list_row, list);
        this.context = context;
        this.list = list;
        this.mSparseBooleanArrayChecked = new SparseBooleanArray();
        this.leftspot = leftspot;
        this.no_of_spots = no_of_spots;
        Log.e("leftspot", "" + leftspot);
        Log.e("no_of_spots", "" + no_of_spots);
    }

    public void notifyWithNewDataSet(ArrayList<FavouriteModel> frequentItems) {
        this.list = frequentItems;
        notifyDataSetChanged();
    }


    public void onClickSelectAll() {
        addeditemCount = new ArrayList<>();
        mSparseBooleanArrayChecked = new SparseBooleanArray();
        for (int i = 0; i < this.list.size(); i++) {
            Log.d(TAG, "onClickSelectAll" + i);
            mSparseBooleanArrayChecked.put(i, true);
            addeditemCount.add(this.list.get(i).getId());
        }
        removeDuplicate(addeditemCount);
        mOnSelectAllItemClickListener.onSelectAll(list, addeditemCount);
        notifyDataSetChanged();
    }

    private ArrayList<String> removeDuplicate(ArrayList<String> allData) {

// add elements to al, including duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(allData);
        allData.clear();
        allData.addAll(hs);
        return allData;
    }


    public void onClickDeSelectAll() {
        mSparseBooleanArrayChecked = new SparseBooleanArray();
        addeditemCount = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            mSparseBooleanArrayChecked.put(i, false);
        }
        removeDuplicate(addeditemCount);
        mOnSelectAllItemClickListener.onSelectAll(list, addeditemCount);
        notifyDataSetChanged();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.cons_list_row, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.memberName);
            viewHolder.img = (CircularImageViews) convertView.findViewById(R.id.profileImage);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.check1);
            viewHolder.checkbox.setChecked(mSparseBooleanArrayChecked.get(position));
            viewHolder.checkbox.setTag(position);
            final ViewHolder finalViewHolder = viewHolder;

            if (no_of_spots != 0 && leftspot == 0) {
                viewHolder.checkbox.setVisibility(View.GONE);
            }
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.txt, viewHolder.title);
            convertView.setTag(R.id.check1, viewHolder.checkbox);
            mTempView = convertView;
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.checkbox.setChecked(mSparseBooleanArrayChecked.get(position));
        }
        viewHolder.checkbox.setChecked(mSparseBooleanArrayChecked.get(position));
        viewHolder.checkbox.setTag(position); // This line is important.

        viewHolder.title.setText(list.get(position).getName());

        if (Utility.isNullCheck(list.get(position).getImage())) {
            Utility.setImageUniversalLoader(getContext(), list.get(position).getImage(), viewHolder.img);
        }

        final ViewHolder finalViewHolder = viewHolder;

        viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (no_of_spots == 0) {

                    //getCheckedMembers(finalViewHolder, v, position);
                    notifiyListener.getSelectedItem(finalViewHolder.checkbox.isChecked(), position);

                } else {

                    if (leftspot > 0) {

                        if (finalViewHolder.checkbox.isChecked()) {

                            Log.e("addeditemCount.size()", "" + addeditemCount.size() + " leftspot " + leftspot);
                            //Limit selected user to accept.
                            if (addeditemCount.size() < leftspot) {

                                //getCheckedMembers(finalViewHolder, v, position);
                                notifiyListener.getSelectedItem(finalViewHolder.checkbox.isChecked(), position);
                            } else {
                                finalViewHolder.checkbox.setChecked(false);
                            }

                        } else {
                            notifiyListener.getSelectedItem(finalViewHolder.checkbox.isChecked(), position);
                            //getCheckedMembers(finalViewHolder, v, position);
                        }
                    }
                }

                mSparseBooleanArrayChecked.put((Integer) v.getTag(), finalViewHolder.checkbox.isChecked());

            }
        });


        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNullCheck(list.get(position).getId())) {
                    Intent intent_buddydetails = new Intent(context, BuddyUpDetailsActivity.class);
                    intent_buddydetails.putExtra("view", true);
                    intent_buddydetails.putExtra("userid", list.get(position).getId());
                    intent_buddydetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent_buddydetails);
                }
            }
        });

        return convertView;
    }

    public void setOnNotifyChagedListener(NotifiyListener notifiyListener) {
        this.notifiyListener = notifiyListener;
    }

    public void setOnSelectAllItemListener(onSelectAllItemClickListener onSelectAllItemListener) {
        this.mOnSelectAllItemClickListener = onSelectAllItemListener;
    }

    private void getCheckedMembers(ViewHolder finalViewHolder, View v, int position) {

        if (finalViewHolder.checkbox.isChecked()) {
            mSparseBooleanArrayChecked.put((Integer) v.getTag(), true);
        } else {
            mSparseBooleanArrayChecked.put((Integer) v.getTag(), false);
        }
        //notifiyListener.getSelectedItem(position);
    }

    public interface NotifiyListener {
        void getSelectedItem(boolean isChecked, int position);
    }

    public interface onSelectAllItemClickListener {
        void onSelectAll(ArrayList<FavouriteModel> allCheckedItems, ArrayList<String> addedItemCount);
    }

    static class ViewHolder {
        protected TextView title;
        protected CheckBox checkbox;
        private CircularImageViews img;
    }
}
