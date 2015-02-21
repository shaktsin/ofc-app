package com.ofcampus.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ofcampus.Util;

public class UserDetails {

	
	private String name="";
	private String email="";
	private String authtoken="";
	private boolean isVerify=false;
	
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
