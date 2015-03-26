/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;

import java.util.ArrayList;

public class IndustryDetails {

	private String industry_id = "";
	private String industry_name = "";
	private String industry_selected = "";
	public int isSelected=0;
	private ArrayList<IndustryRoleDetails> IndustryRoles = new ArrayList<IndustryRoleDetails>();
	

	public String getIndustry_id() {
		return industry_id;
	}

	public void setIndustry_id(String industry_id) {
		this.industry_id = industry_id;
	}

	public String getIndustry_name() {
		return industry_name;
	}

	public void setIndustry_name(String industry_name) {
		this.industry_name = industry_name;
	}

	public String getIndustry_selected() {
		return industry_selected;
	}

	public void setIndustry_selected(String industry_selected) {
		this.industry_selected = industry_selected;
	}
	
	
	public ArrayList<IndustryRoleDetails> getIndustryRoles() {
		return IndustryRoles;
	}

	public void setIndustryRoles(ArrayList<IndustryRoleDetails> industryRoles) {
		IndustryRoles = industryRoles;
	}

}
