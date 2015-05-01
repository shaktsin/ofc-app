package com.ofcampus.model;

import java.util.ArrayList;

public class CustomSpinnerDataSets {

	private String id = "";
	private String title = "";
	public int isSelected = 0;
	private ArrayList<CustomSpinnerDataSets> list = new ArrayList<CustomSpinnerDataSets>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(int isSelected) {
		this.isSelected = isSelected;
	}

	public ArrayList<CustomSpinnerDataSets> getList() {
		return list;
	}

	public void setList(ArrayList<CustomSpinnerDataSets> list) {
		this.list = list;
	}

}
