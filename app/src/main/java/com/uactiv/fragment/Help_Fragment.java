package com.uactiv.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.uactiv.R;
import com.uactiv.utils.AppConstants;
import com.uactiv.utils.SharedPref;
import com.uactiv.utils.Utility;
import com.uactiv.widgets.CustomTextView;

import java.util.Calendar;


public class Help_Fragment extends Fragment implements View.OnClickListener {


    public Help_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private CustomTextView mContactUs = null;
    private CustomTextView mRateUs = null;
    private CustomTextView mTerms = null;
    private CustomTextView mPolicy = null;
    private CustomTextView mCopyRighted = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        bindFragment(view);
        if (!AppConstants.isGestLogin(getActivity())) {
            Utility.setScreenTracking(getActivity(), AppConstants.SCREEN_TRACKING_ID_ABOUT);
        }else{
            Utility.setScreenTracking(getActivity(), "Guest login About page");
        }

        return view;
    }

    private void bindFragment(View view) {
        mContactUs = (CustomTextView) view.findViewById(R.id.txt_contact_us);
        mRateUs = (CustomTextView) view.findViewById(R.id.txt_rate_us);
        mTerms = (CustomTextView) view.findViewById(R.id.txt_terms);
        mPolicy = (CustomTextView) view.findViewById(R.id.txt_policy);
        mCopyRighted = (CustomTextView) view.findViewById(R.id.txtCopyRightYear);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        if (year < 2016) {
            mCopyRighted.setText("Copyright © 2016 Uactive Technology Private Limited");
        } else {
            mCopyRighted.setText("Copyright © " + year + " Uactive Technology Private Limited");
        }


        mContactUs.setOnClickListener(this);
        mRateUs.setOnClickListener(this);
        mTerms.setOnClickListener(this);
        mPolicy.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_contact_us:
                composeEmail(getResources().getString(R.string.support_mail_id), "Feedback on");
                Utility.setEventTracking(getActivity(),"About page", AppConstants.EVENT_TRACKING_ID_ABOUT_CONTACT_US);
                break;
            case R.id.txt_rate_us:
                launchMarket();
                Utility.setEventTracking(getActivity(),"About page", AppConstants.EVENT_TRACKING_ID_ABOUT_RATE_US);
                break;
            case R.id.txt_terms:
                Utility.setEventTracking(getActivity(),"About page", AppConstants.EVENT_TRACKING_ID_ABOUT_TERMS);
                openUrl(AppConstants.getDomain(SharedPref.getInstance().getBooleanValue(getActivity(), AppConstants.SharedConstants.isStaging)) + getResources().getString(R.string.privacy_terms_url));
                break;
            case R.id.txt_policy:
                Utility.setEventTracking(getActivity(),"About page", AppConstants.EVENT_TRACKING_ID_ABOUT_PRIVACY_POLICY);
                openUrl(AppConstants.getDomain(SharedPref.getInstance().getBooleanValue(getActivity(), AppConstants.SharedConstants.isStaging)) + getResources().getString(R.string.privacy_policy_url));
                break;
            default:
                break;
        }
    }

    private void openUrl(String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Cannot load url", Toast.LENGTH_SHORT).show();
        }
    }

    public void composeEmail(String addresses, String subject) {
        //Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setType("*/*");
        //intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        //intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        //if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
        ////   startActivity(intent);
        //}

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        String aEmailList[] = {addresses};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("plain/text");
        //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "My message body.");
        startActivity(emailIntent);
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }
}
