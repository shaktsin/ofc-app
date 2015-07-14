/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;

import java.util.ArrayList;

public class JobPostedUserDetails {

	private String accountname = "";
	private String profileimagelink = "";
	private String firstname = "";
	private String lastname = "";
	private String email = "";
	private String gradyear = "";
	private String totalPosts = "0";
	private String totalCircle = "0";

	public String getTotalPosts() {
		return totalPosts;
	}

	public void setTotalPosts(String totalPosts) {
		this.totalPosts = totalPosts;
	}

	public String getTotalCircle() {
		return totalCircle;
	}

	public void setTotalCircle(String totalCircle) {
		this.totalCircle = totalCircle;
	}

	private ArrayList<JobDetails> arrayPost = new ArrayList<JobDetails>();
	private ArrayList<CircleDetails> arrayCircle = new ArrayList<CircleDetails>();

	public String getAccountname() {
		return accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}

	public String getProfileimagelink() {
		return profileimagelink;
	}

	public void setProfileimagelink(String profileimagelink) {
		this.profileimagelink = profileimagelink;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGradyear() {
		return gradyear;
	}

	public void setGradyear(String gradyear) {
		this.gradyear = gradyear;
	}

	public ArrayList<JobDetails> getArrayPost() {
		return arrayPost;
	}

	public void setArrayPost(ArrayList<JobDetails> arrayPost) {
		this.arrayPost = arrayPost;
	}

	public ArrayList<CircleDetails> getArrayCircle() {
		return arrayCircle;
	}

	public void setArrayCircle(ArrayList<CircleDetails> arrayCircle) {
		this.arrayCircle = arrayCircle;
	}

}
