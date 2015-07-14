/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;

import java.util.ArrayList;

public class CircleProfile {

	private String circlename = "";
	private String circlemembers = "";
	public String circlejoined = "";
	private String circledesc = "";
	private String hide = "";

	private String totalPosts = "";
	private String members = "";

	public String getTotalPosts() {
		return totalPosts;
	}

	public void setTotalPosts(String totalPosts) {
		this.totalPosts = totalPosts;
	}

	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
	}

	public String getHide() {
		return hide;
	}

	public void setHide(String hide) {
		this.hide = hide;
	}

	public String getCircledesc() {
		return circledesc;
	}

	public void setCircledesc(String circledesc) {
		this.circledesc = circledesc;
	}

	private ArrayList<JobDetails> arrayPost = new ArrayList<JobDetails>();
	private ArrayList<CircleUserDetails> arrayCircle = new ArrayList<CircleUserDetails>();

	public String getCirclename() {
		return circlename;
	}

	public void setCirclename(String circlename) {
		this.circlename = circlename;
	}

	public String getCirclemembers() {
		return circlemembers;
	}

	public void setCirclemembers(String circlemembers) {
		this.circlemembers = circlemembers;
	}

	public String getCirclejoined() {
		return circlejoined;
	}

	public void setCirclejoined(String circlejoined) {
		this.circlejoined = circlejoined;
	}

	public ArrayList<JobDetails> getArrayPost() {
		return arrayPost;
	}

	public void setArrayPost(ArrayList<JobDetails> arrayPost) {
		this.arrayPost = arrayPost;
	}

	public ArrayList<CircleUserDetails> getArrayCircle() {
		return arrayCircle;
	}

	public void setArrayCircle(ArrayList<CircleUserDetails> arrayCircle) {
		this.arrayCircle = arrayCircle;
	}

}
