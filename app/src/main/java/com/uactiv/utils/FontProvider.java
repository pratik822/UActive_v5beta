package com.uactiv.utils;

import android.graphics.Typeface;

import com.uactiv.application.UActiveApplication;


public class FontProvider {

	public static Typeface getBrandonBoldFont()
	{
		return Typeface.createFromAsset(UActiveApplication.mContext.getAssets(),"fonts/Brandon_bld.otf");
	}

	public static Typeface getBrandonMedFont()
	{
		return Typeface.createFromAsset(UActiveApplication.mContext.getAssets(),"fonts/Brandon_med.otf");
	}

	public static Typeface getBrandonRegFont()
	{
		return Typeface.createFromAsset(UActiveApplication.mContext.getAssets(),"fonts/Brandon_reg.otf");
	}

}
