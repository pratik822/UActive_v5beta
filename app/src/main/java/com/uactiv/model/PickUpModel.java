package com.uactiv.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PickUpModel implements Serializable{

	private String activityname = null;
	private String full_date;
	private ArrayList<PickUpCategory> pickUpCategoryList = null;

	public String getActivityname() {
		return activityname;
	}

	public void setActivityname(String activityname) {
		this.activityname = activityname;
	}

	public ArrayList<PickUpCategory> getPickUpCategoryList() {
		return pickUpCategoryList;
	}

	public void setPickUpCategoryList(ArrayList<PickUpCategory> pickUpCategoryList) {
		this.pickUpCategoryList = pickUpCategoryList;
	}

	public String getFull_date() {
		return full_date;
	}

	public void setFull_date(String full_date) {
		this.full_date = full_date;
	}
}
