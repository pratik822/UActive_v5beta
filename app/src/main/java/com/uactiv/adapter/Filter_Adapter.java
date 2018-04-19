package com.uactiv.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.uactiv.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Filter_Adapter extends ArrayAdapter<String> {

    private final Activity context;
    NotifiyListener notifiyListener = null;
    SparseBooleanArray mSparseBooleanArrayChecked = null;
    ArrayList<String> addeditemCount = new ArrayList<String>();
    int leftspot = 0;
    int no_of_spots = 0;
    View mTempView;
    String TAG = "MyAdapter";
    ViewHolder viewHolder = null;
    onSelectAllItemClickListener mOnSelectAllItemClickListener = null;
    private ArrayList<String> list = new ArrayList<String>();

    public Filter_Adapter(Activity context, ArrayList<String> list) {
        super(context, R.layout.list_filter_row, list);
        this.context = context;
        this.list = list;
        this.mSparseBooleanArrayChecked = new SparseBooleanArray();
        this.leftspot = leftspot;
        this.no_of_spots = no_of_spots;
        Log.e("leftspot", "" + leftspot);
        Log.e("no_of_spots", "" + no_of_spots);
    }


    public void onClickSelectAll() {
        addeditemCount = new ArrayList<>();
        mSparseBooleanArrayChecked = new SparseBooleanArray();
        for (int i = 0; i < this.list.size(); i++) {
            Log.d(TAG, "onClickSelectAll" + i);
            mSparseBooleanArrayChecked.put(i, true);
            addeditemCount.add(this.list.get(i));
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


        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.list_filter_row, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_activity = (TextView) convertView.findViewById(R.id.tv_activity);
            viewHolder.ck_textview = (CheckBox) convertView.findViewById(R.id.ck_textview);
            viewHolder.ck_textview.setChecked(mSparseBooleanArrayChecked.get(position));
            viewHolder.tv_activity.setText(list.get(position));
            viewHolder.ck_textview.setTag(position);
            final ViewHolder finalViewHolder = viewHolder;

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tv_activity, viewHolder.tv_activity);
            convertView.setTag(R.id.ck_textview, viewHolder.ck_textview);
            mTempView = convertView;
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.ck_textview.setChecked(mSparseBooleanArrayChecked.get(position));
        }
        viewHolder.ck_textview.setChecked(mSparseBooleanArrayChecked.get(position));
        viewHolder.ck_textview.setTag(position); // This line is important.


        final ViewHolder finalViewHolder = viewHolder;

        viewHolder.ck_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //getCheckedMembers(finalViewHolder, v, position);
              //  notifiyListener.getSelectedItem(viewHolder.ck_textview.isChecked(), position);

                mSparseBooleanArrayChecked.put((Integer) v.getTag(), finalViewHolder.ck_textview.isChecked());

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

        if (finalViewHolder.ck_textview.isChecked()) {
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
        void onSelectAll(ArrayList<String> allCheckedItems, ArrayList<String> addedItemCount);
    }

    static class ViewHolder {
        protected TextView tv_activity;
        protected CheckBox ck_textview;
    }
}
