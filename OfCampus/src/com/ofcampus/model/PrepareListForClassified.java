package com.ofcampus.model;

import java.util.ArrayList;

public class PrepareListForClassified {

	private ArrayList<CustomSpinnerDataSets> circlelist = new ArrayList<CustomSpinnerDataSets>();
	private ArrayList<CustomSpinnerDataSets> primarycatlist = new ArrayList<CustomSpinnerDataSets>();
	private ArrayList<CustomSpinnerDataSets> Citys = new ArrayList<CustomSpinnerDataSets>();

	public ArrayList<CustomSpinnerDataSets> getCirclelist() {
		return circlelist;
	}

	public void setCirclelist(ArrayList<CustomSpinnerDataSets> circlelist) {
		this.circlelist = circlelist;
	}

	public ArrayList<CustomSpinnerDataSets> getPrimarycatlist() {
		return primarycatlist;
	}

	public void setPrimarycatlist(ArrayList<CustomSpinnerDataSets> primarycatlist) {
		this.primarycatlist = primarycatlist;
	}

	public ArrayList<CustomSpinnerDataSets> getCitys() {
		return Citys;
	}

	public void setCitys(ArrayList<CustomSpinnerDataSets> citys) {
		Citys = citys;
	}

}
