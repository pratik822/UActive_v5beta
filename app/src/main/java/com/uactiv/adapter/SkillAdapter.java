package com.uactiv.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uactiv.R;
import com.uactiv.model.SkillDo;

import java.util.ArrayList;


public class SkillAdapter extends BaseAdapter {
    private Context context;
    ArrayList<SkillDo> data = new ArrayList<>();

    public SkillAdapter(Context context, ArrayList<SkillDo> data) {
        this.context = context;
        this.data = data;

        Log.e("buvan",":"+data.get(0).getActivty());
        Log.e("buvan",":"+data.get(1).getActivty());


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.skillrowitem, null);
            // set value into textview
            TextView activity = (TextView) gridView.findViewById(R.id.tvactivity);
            ImageView act_imag = (ImageView) gridView.findViewById(R.id.imag_act);

            activity.setText(data.get(position).getActivty());

            Log.e("SkillAdapter",":"+data.get(position).getActivty());

            switch (data.get(position).getLevel()) {

                case "1":
                    act_imag.setImageResource(R.drawable.one_star);
                    break;
                case "2":
                    act_imag.setImageResource(R.drawable.two_star);
                    break;
                case "3":
                    act_imag.setImageResource(R.drawable.three_star);
                    break;
            }


        } else {
            gridView = (View) convertView;
        }


        return gridView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}