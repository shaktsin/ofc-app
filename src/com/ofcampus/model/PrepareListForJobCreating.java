package com.ofcampus.model;

import java.util.ArrayList;

public class PrepareListForJobCreating {

	private ArrayList<IndustryDetails> industrys = new ArrayList<IndustryDetails>();
	private ArrayList<CityDetails> Citys = new ArrayList<CityDetails>();
	private ArrayList<Circle> circlelist = new ArrayList<Circle>();

	private String replyEmail = "";
	private String replyPhone = "";
	private String replyWatsApp = "";

	public ArrayList<IndustryDetails> getIndustrys() {
		return industrys;
	}

	public void setIndustrys(ArrayList<IndustryDetails> industrys) {
		this.industrys = industrys;
	}

	public ArrayList<CityDetails> getCitys() {
		return Citys;
	}

	public void setCitys(ArrayList<CityDetails> citys) {
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

	public ArrayList<Circle> getCirclelist() {
		return circlelist;
	}

	public void setCirclelist(ArrayList<Circle> circlelist) {
		this.circlelist = circlelist;
	}

}
