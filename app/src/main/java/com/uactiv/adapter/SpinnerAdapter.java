package com.uactiv.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uactiv.R;

import java.util.ArrayList;


public class SpinnerAdapter extends ArrayAdapter<String> {

	private int hidingItemIndex;
	ArrayList<String> obj;
	Context context;

	public SpinnerAdapter(Activity context, int textViewResourceId,
						  ArrayList<String> objects, int hidingItemIndex) {
		super(context, textViewResourceId, objects);
		obj = new ArrayList<>();
		obj = objects;
		this.context = context;
		this.hidingItemIndex = hidingItemIndex;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView v = (TextView) super.getView(position, convertView, parent);
		return v;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = null;
		if (v == null) {

			LayoutInflater li = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.custom_spinner, parent, false);

		}
		TextView tv = (TextView) v.findViewById(R.id.tvSpinner);
		tv.setText(obj.get(position));
		return v;
	}
}
