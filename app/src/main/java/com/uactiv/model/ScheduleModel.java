package com.uactiv.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ScheduleModel implements Serializable {

	private String filterDate = null;

	private String member = null;

	private String iduser = null;

	private String idschedule = null;

	private String start_time = null;

	private String firstname = null;

	private String type = null;

	private Date date = null;

	private String activity = null;

	private boolean isUpComing = false;

	private ArrayList<ScheduleMemberDo> scheduleMemberDoArrayList = null;

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getIduser() {
		return iduser;
	}

	public void setIduser(String iduser) {
		this.iduser = iduser;
	}

	public String getIdschedule() {
		return idschedule;
	}

	public void setIdschedule(String idschedule) {
		this.idschedule = idschedule;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public ArrayList<ScheduleMemberDo> getScheduleMemberDoArrayList() {
		return scheduleMemberDoArrayList;
	}

	public void setScheduleMemberDoArrayList(ArrayList<ScheduleMemberDo> scheduleMemberDoArrayList) {
		this.scheduleMemberDoArrayList = scheduleMemberDoArrayList;
	}

	public String getFilterDate() {
		return filterDate;
	}

	public void setFilterDate(String filterDate) {
		this.filterDate = filterDate;
	}

	public boolean isUpComing() {
		return isUpComing;
	}

	public void setIsUpComing(boolean isUpComing) {
		this.isUpComing = isUpComing;
	}
}
