package com.ofcampus.model;

import java.util.ArrayList;

public class PrepareListForNewsAndJob {

	private ArrayList<CustomSpinnerDataSets> circlelist = new ArrayList<CustomSpinnerDataSets>();
	private ArrayList<CustomSpinnerDataSets> industrylist = new ArrayList<CustomSpinnerDataSets>();
	private ArrayList<CustomSpinnerDataSets> Citys = new ArrayList<CustomSpinnerDataSets>();

	private String replyEmail = "";
	private String replyPhone = "";
	private String replyWatsApp = "";

	public ArrayList<CustomSpinnerDataSets> getCirclelist() {
		return circlelist;
	}

	public void setCirclelist(ArrayList<CustomSpinnerDataSets> circlelist) {
		this.circlelist = circlelist;
	}

	public ArrayList<CustomSpinnerDataSets> getIndustrylist() {
		return industrylist;
	}

	public void setIndustrylist(ArrayList<CustomSpinnerDataSets> industrylist) {
		this.industrylist = industrylist;
	}

	public ArrayList<CustomSpinnerDataSets> getCitys() {
		return Citys;
	}

	public void setCitys(ArrayList<CustomSpinnerDataSets> citys) {
		Citys = citys;
	}

	public String getReplyEmail() {
		return replyEmail;
	}

	public void setReplyEmail(String replyEmail) {
		this.replyEmail = replyEmail;
	}

	public String getReplyPhone() {
		return replyPhone;
	}

	public void setReplyPhone(String replyPhone) {
		this.replyPhone = replyPhone;
	}

	public String getReplyWatsApp() {
		return replyWatsApp;
	}

	public void setReplyWatsApp(String replyWatsApp) {
		this.replyWatsApp = replyWatsApp;
	}

}
