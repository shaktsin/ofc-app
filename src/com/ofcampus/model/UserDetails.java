/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ofcampus.Util;

public class UserDetails {

	public String userID = "";
	public String name = "";
	public String email = "";
	public String authtoken = "";
	public boolean isVerify = false;

	public String fstname = "";
	public String lstname = "";
	public String accountname = "";

	public String yearPass = "";
	public String image = "";

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getImage() {
		return image;
	}

	public String getAccountname() {
		return accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getFstname() {
		return fstname;
	}

	public void setFstname(String fstname) {
		this.fstname = fstname;
	}

	public String getLstname() {
		return lstname;
	}

	public void setLstname(String lstname) {
		this.lstname = lstname;
	}

	public String getYearPass() {
		return yearPass;
	}

	public void setYearPass(String yearPass) {
		this.yearPass = yearPass;
	}

	public boolean isVerify() {
		return isVerify;
	}

	public void setVerify(boolean isVerify) {
		this.isVerify = isVerify;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAuthtoken() {
		return authtoken;
	}

	public void setAuthtoken(String authtoken) {
		this.authtoken = authtoken;
	}

	public void saveInPreferense(Context context) {
		SharedPreferences.Editor prefsEditor = Util.getPrefs(context).edit();
		Gson gson = new Gson();
		String json = gson.toJson(this);
		prefsEditor.putString("LoggedInUser", json);
		prefsEditor.commit();
	}

	public static UserDetails getLoggedInUser(Context context) {
		SharedPreferences mPrefs = Util.getPrefs(context);
		Gson gson = new Gson();
		String json = mPrefs.getString("LoggedInUser", "");
		UserDetails obj = gson.fromJson(json, UserDetails.class);
		return obj;
	}

	public static void logoutUser(Context context) {
		SharedPreferences.Editor prefsEditor = Util.getPrefs(context).edit();
		Gson gson = new Gson();
		String json = gson.toJson(null);
		prefsEditor.putString("LoggedInUser", json);
		prefsEditor.commit();
	}

}
