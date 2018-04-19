package com.uactiv.utils;

import android.content.Context;

import com.uactiv.application.UActiveApplication;


public class UActivePref {
	
static UActivePref preferenceInstance;
	
	public static void initInstance(Context context){
		if(preferenceInstance == null)
			preferenceInstance = new UActivePref(context);
	}
	
	private UActivePref(Context context){
		super();
	}
	
	public static UActivePref getInstance()
	{
		return preferenceInstance;
	}
	public static void setPickUpItem(String pickUpItem)
	{
		UActiveApplication.getSharedPreferences().edit().putString(AppConstants.PICKUP_ITEM_NAME, pickUpItem).commit();
	}

	public String getPickUpItem() {
		return UActiveApplication.getSharedPreferences().getString(AppConstants.PICKUP_ITEM_NAME, null);
	}
	public static void setBuddyUpItem(String buddyUpItem)
	{
		UActiveApplication.getSharedPreferences().edit().putString(AppConstants.BUDDYUP_ITEM_NAME, buddyUpItem).commit();
	}

	public String getBuddyUpItem() {
		return UActiveApplication.getSharedPreferences().getString(AppConstants.BUDDYUP_ITEM_NAME, null);
	}
}

