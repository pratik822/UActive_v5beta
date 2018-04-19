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
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomButton;
import com.uactiv.widgets.CustomTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pickup_Filter_fragment extends Fragment implements AppConstants.SharedConstants, AppConstants.urlConstants {

    View view;
    Bundle bundle;
    List<String> mytempList;
    private CustomTextView tv_act;
    private RelativeLayout activitylayout;
    private SeekBar seekBar1;
    String activity = "";
    Chip chip;
    LinearLayout ln;
    ChipView chipview;
    int speedlimit;
    ChipViewAdapter adapterOverride;
    RadioGroup radioGroup;
    private CustomTextView tvRadius;
    private CustomButton tvSave;
    private RadioButton rv_male, fe_male, rv_both;
    ImageView iv_back;
    List<com.plumillonforge.android.chipview.Chip> chipList = new ArrayList<com.plumillonforge.android.chipview.Chip>();
    RelativeLayout spinnerLayout,relativeLayout6;
    View view2;
    CustomTextView textView100;
    CustomTextView tvPreferences;
    List<String> mytempList1,myCount;
    static List<String> mytempList1_,myCount_;


    String selectedSkill="";
    int seekbarprogress=0;

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
        tvPreferences=(CustomTextView)view.findViewById(R.id.tvPreferences);

        chipview.setChipLayoutRes(R.layout.chipclose);
        seekBar1.setMax(195);
        tvRadius.setText("5km");
        pickupHide(view);
        tvSave = (CustomButton) view.findViewById(R.id.tvSave);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                switch (index) {
                    case 0: // first button
                        SharedPref.getInstance().setSharedValue(getActivity(), gender_pref, "Male");
                        break;
                    case 1: // secondbutton
                        SharedPref.getInstance().setSharedValue(getActivity(), gender_pref, "Female");
                        break;
                    case 2: // secondbutton
                        SharedPref.getInstance().setSharedValue(getActivity(), gender_pref, "Both");
                        break;
                }

            }
        });

        tvPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPref.getInstance().setSharedValue(getActivity(), "GEST_GENDER", "Both");
                SharedPref.getInstance().setSharedValue(getActivity(), radius_limit, String.valueOf("200"));
                seekBar1.setProgress(195);

            }
        });

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit))) {
            seekBar1.setProgress(Integer.parseInt(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit)));
            tvRadius.setText(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit) + "km");
            seekbarprogress=(Integer.parseInt(SharedPref.getInstance().getStringVlue(getActivity(), radius_limit)));
        } else {
            if (AppConstants.getRadius(getActivity()) != null && AppConstants.getRadius(getActivity()) != "") {
                seekBar1.setProgress(Integer.parseInt(AppConstants.getRadius(getActivity())) );
            } else {
                seekBar1.setProgress(195);
            }

        }

        if (Utility.isNullCheck(SharedPref.getInstance().getStringVlue(getActivity(), gender_pref))) {
            String pref = SharedPref.getInstance().getStringVlue(getActivity(), gender_pref);

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
        /*        Fragment fragment = new Buddyup_Fragment();
                *//*intent.putExtra("adapterfrom", "buddyupadap");
                intent.putExtra("setting", "setting");*//*
                Bundle bn = new Bundle();
                bn.putString("setting", "setting");
                bn.putString("adapterfrom", "pickupupadap");*/
                /*if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragment.setArguments(bn);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null);
                    fragmentTransaction.commit();
                }*/

                if (!AppConstants.isGestLogin(getActivity())) {
                    Utility.setScreenTracking(getActivity(), AppConstants.EVENT_TRACKING_ID_CHNAGESEARCHRADIUS);
                } else {
                    Utility.setScreenTracking(getActivity(), "Change Search Radius on guest login.");
                }

                seekbarprogress = seekbarprogress;
                SharedPref.getInstance().setSharedValue(getActivity(), PREF_GEST_GEST_RADIUS, String.valueOf(seekbarprogress));
                SharedPref.getInstance().setSharedValue(getActivity(), radius_limit, String.valueOf(seekbarprogress));
                speedlimit = seekbarprogress;
                tvRadius.setText(seekbarprogress + "km");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                Pickup_Fragment.pickUpArrayList.clear();
                intent.putExtra("adapterfrom", "pickupadap");
                intent.putExtra("skills", selectedSkill.replaceAll("null",""));
                intent.putExtra("setting", "setting");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                /*Intent intent = new Intent(getActivity(), MainActivity.class);
                Home.buddyList.clear();
                intent.putExtra("adapterfrom", "buddyupadap");
                intent.putExtra("setting", "setting");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);*/
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
            mytempList1 = (List<String>) bundle.getSerializable("data");
            myCount=(List<String>) bundle.getSerializable("dataCount");
            mytempList1_=mytempList1;
            myCount_=myCount;


            ln = (LinearLayout) view.findViewById(R.id.ln);

            for (int i = 0; i < mytempList1.size(); i++) {
                chipList.add(new com.uactiv.activity.Tag(mytempList1.get(i)));
                // activity+=mytempList.get(i)+", ";
                //  chip.setChipText(mytempList.get(i));

            }
            StringBuilder sb = new StringBuilder();
            Iterator<String> iter = myCount.iterator();
            while (iter.hasNext()) {
                if (sb.length() > 0)
                    sb.append(",");
                sb.append(iter.next());
            }



         selectedSkill=sb.toString();

            chipview.setChipLayoutRes(R.layout.chipclose);
          /* MainChipViewAdapter adapter=new MainChipViewAdapter(getActivity());
            chipview.setAdapter(adapter);*/
            chipview.setChipList(chipList);


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
            if(mytempList1_!=null && mytempList1_.size()>0){
                for (int i = 0; i < mytempList1_.size(); i++) {
                    chipList.add(new com.uactiv.activity.Tag(mytempList1_.get(i)));
                    // activity+=mytempList.get(i)+", ";
                    //  chip.setChipText(mytempList.get(i));

                }
                StringBuilder sb = new StringBuilder();
                Iterator<String> iter = myCount_.iterator();
                while (iter.hasNext()) {
                    if (sb.length() > 0)
                        sb.append(",");
                    sb.append(iter.next());
                }



                selectedSkill=sb.toString();

                chipview.setChipLayoutRes(R.layout.chipclose);
          /* MainChipViewAdapter adapter=new MainChipViewAdapter(getActivity());
            chipview.setAdapter(adapter);*/
                chipview.setChipList(chipList);
            }

        }

        activitylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bn=new Bundle();
                bn.putString("frompickup","");
                SharedPref.getInstance().setSharedValue(getActivity(), radius_limit, String.valueOf(seekbarprogress));
                Fliter_Activity_fragment fragment = new Fliter_Activity_fragment();
                fragment.setArguments(bn);
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

    public void pickupHide(View rootView){
        spinnerLayout = (RelativeLayout) rootView.findViewById(R.id.spinnerLayout);
        relativeLayout6 = (RelativeLayout) rootView.findViewById(R.id.relativeLayout6);
        textView100=(CustomTextView)view.findViewById(R.id.textView100);
        textView100.setVisibility(View.INVISIBLE);
        view2 = (View) rootView.findViewById(R.id.view2);
        view2.setVisibility(View.INVISIBLE);
        relativeLayout6.setVisibility(View.INVISIBLE);
        spinnerLayout.setVisibility(View.INVISIBLE);
    }

}
