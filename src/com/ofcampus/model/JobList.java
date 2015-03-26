/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;

import java.util.ArrayList;

public class JobList {

	ArrayList<JobDetails> jobs = new ArrayList<JobDetails>();
	ArrayList<CityDetails> Citys = new ArrayList<CityDetails>();
	ArrayList<IndustryRoleDetails> IndustryRoles = new ArrayList<IndustryRoleDetails>();
	ArrayList<IndustryDetails> Industrys = new ArrayList<IndustryDetails>();

	public ArrayList<JobDetails> getJobs() {
		return jobs;
	}

	public void setJobs(ArrayList<JobDetails> jobs) {
		this.jobs = jobs;
	}

	public ArrayList<CityDetails> getCitys() {
		return Citys;
	}

	public void setCitys(ArrayList<CityDetails> citys) {
		Citys = citys;
	}

	public ArrayList<IndustryRoleDetails> getIndustryRoles() {
		return IndustryRoles;
	}

	public void setIndustryRoles(ArrayList<IndustryRoleDetails> industryRoles) {
		IndustryRoles = industryRoles;
	}

	public ArrayList<IndustryDetails> getIndustrys() {
		return Industrys;
	}

	public void setIndustrys(ArrayList<IndustryDetails> industrys) {
		Industrys = industrys;
	}

}
