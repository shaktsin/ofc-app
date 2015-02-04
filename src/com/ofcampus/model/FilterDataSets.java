package com.ofcampus.model;

import java.util.ArrayList;

public class FilterDataSets {

	private ArrayList<IndustryDetails> arrayIndustry = new ArrayList<IndustryDetails>();
	private ArrayList<CityDetails> arrayCity = new ArrayList<CityDetails>();
	private ArrayList<Circle> arraCircles = new ArrayList<Circle>();
	private ArrayList<IndustryRoleDetails> arrRoleDetails = new ArrayList<IndustryRoleDetails>();

	private String EXP_Name = "";
	private String EXP_Min = "";
	private String EXP_Max = "";
	private String Salary_Name = "";
	private String Salary_Min = "";
	private String Salary_Max = "";

	public ArrayList<Circle> getArraCircles() {
		return arraCircles;
	}

	public void setArraCircles(ArrayList<Circle> arraCircles) {
		this.arraCircles = arraCircles;
	}

	public ArrayList<IndustryRoleDetails> getArrRoleDetails() {
		return arrRoleDetails;
	}

	public void setArrRoleDetails(ArrayList<IndustryRoleDetails> arrRoleDetails) {
		this.arrRoleDetails = arrRoleDetails;
	}

	public ArrayList<IndustryDetails> getArrayIndustry() {
		return arrayIndustry;
	}

	public void setArrayIndustry(ArrayList<IndustryDetails> arrayIndustry) {
		this.arrayIndustry = arrayIndustry;
	}

	public ArrayList<CityDetails> getArrayCity() {
		return arrayCity;
	}

	public void setArrayCity(ArrayList<CityDetails> arrayCity) {
		this.arrayCity = arrayCity;
	}

	public String getEXP_Name() {
		return EXP_Name;
	}

	public void setEXP_Name(String eXP_Name) {
		EXP_Name = eXP_Name;
	}

	public String getEXP_Min() {
		return EXP_Min;
	}

	public void setEXP_Min(String eXP_Min) {
		EXP_Min = eXP_Min;
	}

	public String getEXP_Max() {
		return EXP_Max;
	}

	public void setEXP_Max(String eXP_Max) {
		EXP_Max = eXP_Max;
	}

	public String getSalary_Name() {
		return Salary_Name;
	}

	public void setSalary_Name(String salary_Name) {
		Salary_Name = salary_Name;
	}

	public String getSalary_Min() {
		return Salary_Min;
	}

	public void setSalary_Min(String salary_Min) {
		Salary_Min = salary_Min;
	}

	public String getSalary_Max() {
		return Salary_Max;
	}

	public void setSalary_Max(String salary_Max) {
		Salary_Max = salary_Max;
	}

}
