package com.uactiv.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.plumillonforge.android.chipview.ChipView;
import com.plumillonforge.android.chipview.ChipViewAdapter;
import com.plumillonforge.android.chipview.OnChipClickListener;
import com.robertlevonyan.views.chip.Chip;
import com.uactiv.R;
import com.uactiv.activity.MainActivity;
import com.uactiv.controller.ResponseListener;
import com.uactiv.network.RequestHandler;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Filter_fragment extends Fragment implements AppConstants.SharedConstants, AppConstants.urlConstants,ResponseListener {

    View view;
    Bundle bundle;
    List<String> mytempList, myCount;
    static List<String> mytempList_, mycount_;
    private CustomTextView tv_act;
    private RelativeLayout activitylayout;
    private SeekBar seekBar1;
    String activity = "";
    Chip chip;
    LinearLayout ln;
    ChipView chipview;
    int speedlimit;
    String mytokan;
    ChipViewAdapter adapterOverride;
    RadioGroup radioGroups;
    private CustomTextView tvRadius;
    private CustomButton tvSave;
    private CustomTextView tvPreferences;
    private RadioButton rv_male, fe_male, rv_both;
    RadioGroup radioGroup;
    private CheckBox checkBox;
    ImageView iv_back;
    List<com.plumillonforge.android.chipview.Chip> chipList = new ArrayList<com.plumillonforge.android.chipview.Chip>();
    String selectedSkill;
    RelativeLayout relativeLayout6;
    CustomTextView textView100;
    String mygender="";
    int seekbarprogress=0;
    Bundle b;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_filter_fragment, container, false);
        //  tv_act=(CustomTextView)view.findViewById(R.id.tv_act);
        activitylayout = (RelativeLayout) view.findViewById(R.id.activitylayout);

        chipview = (ChipView) view.findViewById(R.id.chipview);
        tvRadius = (CustomTextView) view.findViewById(R.id.tvRadius);
        seekBar1 = (SeekBar) view.findViewById(R.id.seekBar1);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        rv_male = (RadioButton) view.findViewById(R.id.rv_male);
        fe_male = (RadioButton) view.findViewById(R.id.fe_male);
        rv_both = (RadioButton) view.findViewById(R.id.rv_both);
        iv_back = (ImageView) view.findViewById(R.id.iv_back);
        tvPreferences = (CustomTextView) view.findViewById(R.id.tvPreferences);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        relativeLayout6 = (RelativeLayout) view.findViewById(R.id.relativeLayout6);
        textView100 = (CustomTextView) view.findViewById(R.id.textView100);
        myCount = new ArrayList<>();
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        final int idx = radioGroup.indexOfChild(radioButton);
        chipview.setChipLayoutRes(R.layout.chipclose);
        seekBar1.setMax(195);
        tvRadius.setText("5km");
        tvSave = (CustomButton) view.findViewById(R.id.tvSave);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active"))) {
            if (SharedPref.getInstance().getStringVlue(getActivity(), "mutual_active").equalsIgnoreCase("1")) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

        }

        tvPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPref.getInstance().setSharedValue(getActivity(), "GEST_GENDER", "Both");
                SharedPref.getInstance().setSharedValue(getActivity(), radius_limit, String.valueOf("200"));
                seekBar1.setProgress(195);
                tvRadius.setText("200km");
                rv_both.setChecked(true);
                checkBox.setChecked(false);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                switch (index) {
                    case 0: // first button
                        SharedPref.getInstance().setSharedValue(getActivity(), "GEST_GENDER", "Male");
                        break;
                    case 1: // secondbutton
                        SharedPref.getInstance().setSharedValue(getActivity(), "GEST_GENDER", "Female");
                        break;
                    case 2: // secondbutton
                        SharedPref.getInstance().setSharedValue(getActivity(), "GEST_GENDER", "Both");
                        break;
                }

            }
        });

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit))) {
            seekBar1.setProgress(Integer.parseInt(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit)));
            seekbarprogress=(Integer.parseInt(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit)));
            tvRadius.setText(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit) + "km");
        } else {
            if (AppConstants.getRadius(getActivity()) != null && AppConstants.getRadius(getActivity()) != "") {
                seekBar1.setProgress(Integer.parseInt(AppConstants.getRadius(getActivity())) - 5);
            } else {
                seekBar1.setProgress(195);
            }

        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), "GEST_GENDER"))) {
            String pref = SharedPref.getInstance().getStringVlue(getActivity(), "GEST_GENDER");

            if (pref.equalsIgnoreCase("Male")) {
                rv_male.setChecked(true);
            } else if (pref.equalsIgnoreCase("Female")) {
                fe_male.setChecked(true);
            } else {
                rv_both.setChecked(true);
            }
            //  radioGroup.setSelection(adapter.getPosition(pref));
        } else if (AppConstants.getGender(getActivity()) != null && AppConstants.getGender(getActivity()) != "") {
            String pref = AppConstants.getGender(getActivity());
            //   spinGender.setSelection(adapter.getPosition(pref));
        }

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 if(rv_male.isChecked()){
                     mygender="male";
                 }else if(fe_male.isChecked()){
                     mygender="Female";
                 }else{
                     mygender="both";
                 }

                if (!AppConstants.isGestLogin(getActivity())) {
                    Utility.setScreenTracking(getActivity(), AppConstants.EVENT_TRACKING_ID_CHNAGESEARCHRADIUS);
                } else {
                    Utility.setScreenTracking(getActivity(), "Change Search Radius on guest login.");
                }


                SharedPref.getInstance().setSharedValue(getActivity(), radius_limit, String.valueOf(seekbarprogress));
                speedlimit = seekbarprogress;
                tvRadius.setText(speedlimit+"");


                if (checkBox.isChecked()) {
                    SharedPref.getInstance().setSharedValue(getActivity(), "mutual_active", "1");
                } else {
                    SharedPref.getInstance().setSharedValue(getActivity(), "mutual_active", "0");
                }
                updateSettings(mygender,tvRadius.getText().toString());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                //Home.buddyList.clear();
                Buddyup_Fragment.search_buddyList.clear();
                intent.putExtra("adapterfrom", "buddyupadap");
                intent.putExtra("setting", "setting");
                intent.putExtra("filter", "true");
                intent.putExtra("skills", selectedSkill);

                startActivity(intent);
            }
        });
        //seekbar
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Utility.setEventTracking(getActivity(), "Settings", "Search radius on setting screen");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                seekbarprogress=progress;
                seekbarprogress = seekbarprogress + 5;
                tvRadius.setText(seekbarprogress + "km");

            }
        });
        //  adapterOverride=new MainChipViewAdapter(getActivity());

        //chip.setClosable(true);
        bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getSerializable("data") != null) {
                mytempList = (List<String>) bundle.getSerializable("data");
                mytempList_=mytempList;
                if (mytempList.size() > 0) {
                    for (int i = 0; i < mytempList.size(); i++) {
                        chipList.add(new com.uactiv.activity.Tag(mytempList.get(i)));

                    }

                }
            }else{
                if(mytempList_!=null && mytempList_.size()>0) {
                    if (mytempList_.size() > 0) {
                        for (int i = 0; i < mytempList_.size(); i++) {
                            chipList.add(new com.uactiv.activity.Tag(mytempList_.get(i)));

                        }

                    }
                }




            }
            if (bundle.getSerializable("dataCount") != null) {
                myCount = (List<String>) bundle.getSerializable("dataCount");
                mycount_=myCount;

                StringBuilder sb = new StringBuilder();
                Iterator<String> iter = mycount_.iterator();
                while (iter.hasNext()) {
                    if (sb.length() > 0)
                        sb.append(",");
                    sb.append(iter.next());
                }


                selectedSkill = sb.toString();
                Log.d("myskills", selectedSkill);
                chipview.setChipLayoutRes(R.layout.chipclose);
           /* MainChipViewAdapter adapter=new MainChipViewAdapter(getActivity());
            chipview.setAdapter(adapter);*/
                chipview.setChipList(chipList);
            }else{
                StringBuilder sb = new StringBuilder();
                Iterator<String> iter = myCount.iterator();
                while (iter.hasNext()) {
                    if (sb.length() > 0)
                        sb.append(",");
                    sb.append(iter.next());
                }


                selectedSkill = sb.toString();
                Log.d("myskills", selectedSkill);
                chipview.setChipLayoutRes(R.layout.chipclose);
           /* MainChipViewAdapter adapter=new MainChipViewAdapter(getActivity());
            chipview.setAdapter(adapter);*/
                chipview.setChipList(chipList);
            }

            if (bundle.getString("mytokan") != null) {
                mytokan = bundle.getString("mytokan");
                if (mytokan.isEmpty()) {
                    relativeLayout6.setVisibility(View.INVISIBLE);
                    textView100.setVisibility(View.INVISIBLE);
                }
            }

            Log.d("mydatacount", new Gson().toJson(myCount));
            ln = (LinearLayout) view.findViewById(R.id.ln);





            chipview.setOnChipClickListener(new OnChipClickListener() {
                @Override
                public void onChipClick(com.plumillonforge.android.chipview.Chip chip) {


                    for (int k = 0; k < chipList.size(); k++) {

                        if (chip.getText().toString().equalsIgnoreCase(chipList.get(k).toString())) {
                            Toast.makeText(getActivity(), "fd", Toast.LENGTH_LONG).show();
                            chipList.remove(k);
                            chipview.remove(chip);
                        }
                    }
                    chipview.setChipList(chipList);

                }
            });

            // tv_act.setText(activity);
        }else{
            if(mytempList_!=null && mytempList_.size()>0){
                if (mytempList_.size() > 0) {
                    for (int i = 0; i < mytempList_.size(); i++) {
                        chipList.add(new com.uactiv.activity.Tag(mytempList_.get(i)));

                    }

                }




            }
        }
        activitylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fliter_Activity_fragment fragment = new Fliter_Activity_fragment();
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                }
            }
        });

        return view;
    }

    private void updateSettings(String gender, String limit) {

        if (Utility.isConnectingToInternet(getActivity())) {

            try {
                Map<String, String> param = new HashMap<>();
                param.put("search_limit", "" + limit);
                param.put("gender_pref", gender);
                param.put("isreceive_request", "1");
                param.put("isreceive_notification", "1");

                if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), facebook_link))) {
                    param.put("facebook_link", "1");
                } else {
                    param.put("facebook_link", "0");
                }
                param.put("iduser", SharedPref.getInstance().getStringVlue(getActivity(), userId));

                RequestHandler.getInstance().stringRequestVolley(getActivity(), AppConstants.getBaseUrl(SharedPref.getInstance().getBooleanValue(getActivity(), isStaging)) + updatesetting, param, this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Utility.showInternetError(getActivity());
        }
    }

    @Override
    public void successResponse(String successResponse, int flag) throws JSONException {
        Log.d("updatesetting",successResponse);
    }

    @Override
    public void successResponse(JSONObject jsonObject, int flag) {

    }

    @Override
    public void errorResponse(String errorResponse, int flag) {

    }

    @Override
    public void removeProgress(Boolean hideFlag) {

    }
}
