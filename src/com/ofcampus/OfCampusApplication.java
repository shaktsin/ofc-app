package com.ofcampus;

import java.util.ArrayList;

import com.ofcampus.model.InstituteDetails;
import com.ofcampus.model.JobDetails;

import android.app.Application;
import android.content.res.Configuration;

public class OfCampusApplication extends Application {

	public ArrayList<InstituteDetails> institutes_;
	public String instituteid="";
	public JobDetails jobdetails;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

}
