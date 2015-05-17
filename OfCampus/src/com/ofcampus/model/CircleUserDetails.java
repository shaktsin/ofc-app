/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;

public class CircleUserDetails {

	private String userid = "";
	private String username = "";
	private String userimage = "";
	private String usercircles = "";
	private String useryearofgrad = "";
	private String emailId = "";
	private String memberSince = "";

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserimage() {
		return userimage;
	}

	public void setUserimage(String userimage) {
		this.userimage = userimage;
	}

	public String getUsercircles() {
		return usercircles;
	}

	public void setUsercircles(String usercircles) {
		this.usercircles = usercircles;
	}

	public String getUseryearofgrad() {
		return useryearofgrad;
	}

	public void setUseryearofgrad(String useryearofgrad) {
		this.useryearofgrad = useryearofgrad;
	}

	public String getMemberSince() {
		return memberSince;
	}

	public void setMemberSince(String memberSince) {
		this.memberSince = memberSince;
	}

}
