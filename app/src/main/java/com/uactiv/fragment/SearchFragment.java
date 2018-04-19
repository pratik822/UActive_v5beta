package com.uactiv.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.uactiv.R;
import com.uactiv.adapter.BuddyAdapter;
import com.uactiv.adapter.PickUpAdapter;
import com.uactiv.model.BuddyModel;
import com.uactiv.model.PickUpModel;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.UActivePref;
import com.uactiv.widgets.CustomAutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class SearchFragment extends Fragment implements OnClickListener {

	String pickUpUpSearchList[] = { "Aerobics", "Badminton",
			"Basketball", "Boot camp", "Cricket", "Crossfit", "Cycling",
			"Dance", "Football", "Golf", " Gym", "Hockey", "Jogging", "Judo",
			"Karate", " Kick boxing ", " MMA", "Parkour", "Pilates", "Running",
			"Skateboarding", "Squash", "Swimming", "Table tennis", " Tennis",
			" Walking", " Weightlifting", "Yoga", "Zumba", };
	ImageView imgClear, imgBack;
	String navigation = "";
	Fragment fragment=null;


	ArrayList<HashMap<String,String>> pickUpAutoList = new ArrayList<HashMap<String,String>>();
	private CustomAutoCompleteTextView pickUpSearchAuto;
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	ArrayList<BuddyModel> temp_buddyupArrayList= new ArrayList<>();
	ArrayList<BuddyModel> buddyupArrayList= new ArrayList<>();
	ArrayList<BuddyModel> all_temp_buddyupArrayList= new ArrayList<>();
	ArrayList<PickUpModel> pickUpArrayList = new ArrayList<>();
	ArrayList<PickUpModel> temp_pickUpArrayList = new ArrayList<>();
	BuddyAdapter buddyupadapter = null;
	PickUpAdapter pickUpAdapter = null;
	LinearLayoutManager manager = null;

	RecyclerView recyclerView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_activity, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		temp_buddyupArrayList = buddyupArrayList;
		all_temp_buddyupArrayList.addAll(buddyupArrayList);
		//setbuddyupAdapter();
	}



	private void initView() {

		imgClear = (ImageView) getActivity().findViewById(R.id.imgClear);
		pickUpSearchAuto = (CustomAutoCompleteTextView) getActivity().findViewById(R.id.pickUpSearchs);
		recyclerView = (RecyclerView) getActivity().findViewById(R.id.searchList);
		manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
		recyclerView.setLayoutManager(manager);

		pickUpSearchAuto.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				imgClear.setVisibility(View.VISIBLE);
			}
		});

		Bundle bundle = this.getArguments();
		navigation = bundle.getString("navi");

		if (bundle != null) {
			String name = bundle.getString("from");

			buddyupArrayList = (ArrayList<BuddyModel>) bundle.getSerializable("BuddyupList");
			pickUpArrayList = (ArrayList<PickUpModel>) bundle.getSerializable("PickupList");

			if(name.contains("pickUp")){
				for (int i = 0; i < pickUpUpSearchList.length; i++) {
					HashMap< String, String> pickUpMap=new HashMap<String, String>();
					pickUpMap.put("pickUpNames", pickUpUpSearchList[i]);
					pickUpAutoList.add(pickUpMap);
				}

				String[] pickUpFrom = {"pickUpNames"};
				int[] pickUpTo = {R.id.txt};

				SimpleAdapter pickUpSearchAdapter = new SimpleAdapter(getActivity(), pickUpAutoList, R.layout.autocomplete_layout, pickUpFrom, pickUpTo);
				pickUpSearchAuto.setAdapter(pickUpSearchAdapter);
				pickUpSearchAuto.setThreshold(1);

				pickUpSearchAuto.setOnItemClickListener(new OnItemClickListener() {
					@SuppressWarnings({ "unchecked", "static-access" })
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						HashMap<String, String> hm = (HashMap<String, String>) parent.getAdapter().getItem(position);
						UActivePref.getInstance().setPickUpItem(hm.get("pickUpNames"));
						AppConstants.hideKeyBoard(getActivity());
						getActivity().overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
					}
				});
			}
			else{

				pickUpSearchAuto.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {


						buddyupadapter.buddyNameFilter(pickUpSearchAuto.getText().toString().toLowerCase(Locale.getDefault()));
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {

					}
				});
			}
		}
		imgClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pickUpSearchAuto.setText("");

				fragment = new Home();
				Bundle bundle = new Bundle();
				if (navigation.equals("buddy")) {
					bundle.putString("navi", "buddy");
					fragment.setArguments(bundle);
				} else if (navigation.equals("pick")) {
					bundle.putString("navi", "pick");
					fragment.setArguments(bundle);
				}
				if (fragment != null) {
					FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.searchparent, fragment);
					fragmentTransaction.commit();

				}
			}
		});
	}

	@Override
	public void onClick(View v) {

	}

	/*public void setbuddyupAdapter(){

		Log.e("temp_buddyupArrayList","temp_buddyupArrayList"+temp_buddyupArrayList.size());
		buddyupadapter = new BuddyAdapter(getActivity(), temp_buddyupArrayList);
		//recyclerView.setAdapter(buddyupadapter);
		//recyclerView.setHasFixedSize(true);
	}*/

	public void buddyFilter(String text){

		//temp_buddyupArrayList.clear();

		Toast.makeText(getActivity(), "size"+all_temp_buddyupArrayList.size(), Toast.LENGTH_SHORT).show();

		text = text.toLowerCase(Locale.getDefault());

		if(pickUpSearchAuto.length() == 0){

			temp_buddyupArrayList.addAll(all_temp_buddyupArrayList);
			//setbuddyupAdapter();

		}else {

			for (BuddyModel buddyModel : all_temp_buddyupArrayList) {

				Log.e("Adapter",""+buddyModel.getName().toLowerCase(Locale.getDefault())+"Txt" +pickUpSearchAuto.getText());

				if (buddyModel.getName().toLowerCase(Locale.getDefault()).contains(text)) {
					temp_buddyupArrayList.add(buddyModel);
					//setbuddyupAdapter();
				}
			}
		}

	}
}
