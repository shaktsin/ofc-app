/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright Dibakar MIstry, 2015.
 */


package com.ofcampus;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.crittercism.app.Crittercism;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ofcampus.databasehelper.OfCampusDBHelper;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.InstituteDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;

public class OfCampusApplication extends Application {


	public OfCampusDBHelper DB_HELPER;
	public boolean fromMYPost=false;
	
	public ArrayList<InstituteDetails> institutes_;
	public ArrayList<JobDetails> filterJobs;
	public JobDetails jobdetails;
	public UserDetails mDetails;
	public CircleDetails mCircleDetails_;
	
	public boolean isHidePostModify=false;
	public boolean editPostSuccess=false;
	public boolean editPostSuccessForHome=false;
	public boolean profileEditSuccess=false;
	public boolean isNewCircleCreated=false;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initilizeDB();
		initImageLoader(getApplicationContext());
		Crittercism.initialize(getApplicationContext(), "5519900b7fa1f3d21c00633d");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onTerminate() {
		closeDB();
		super.onTerminate();
	}
	
	/**
	 * Related to Data Base.
	 */
	private void initilizeDB() {
		if (DB_HELPER == null) {
			DB_HELPER = new OfCampusDBHelper(OfCampusApplication.this);
		}
		try {
			DB_HELPER.getWritableDatabase();
			DB_HELPER.openDataBase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeDB() {
		try {
			DB_HELPER.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize Image Loader.
	 */
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

}
