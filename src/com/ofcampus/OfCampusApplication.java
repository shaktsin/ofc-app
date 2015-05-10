/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright Dibakar MIstry, 2015.
 */

package com.ofcampus;

import java.util.ArrayList;
import java.util.TreeMap;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.WindowManager;

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
	public boolean fromMYPost = false;

	public ArrayList<InstituteDetails> institutes_;
	public ArrayList<JobDetails> filterJobs;
	public JobDetails jobdetails;
	public UserDetails mDetails;
	public CircleDetails mCircleDetails_;

	public boolean isHidePostModify = false;
	public boolean editPostSuccess = false;
	public boolean editPostSuccessForHome = false;
	public boolean editPostSuccessForNews = false;
	public boolean profileEditSuccess = false;
	public boolean isNewCircleCreated = false;
	public boolean isUserCame = false;
	
//	public boolean isHidePostModify = false;
//	public boolean isMyPostModify = false;
//	public boolean isImportantPostModify = false;
//	
//	public boolean isNewsModify = false;
//	public boolean isJobModify = false;
//	public boolean isClassifiedModify = false;
//	
//	public boolean isCircleModify = false;
	

	/****** For Gallery ********/

	public static Context applicationContext = null;
	public static volatile Handler applicationHandler = null;
	public static Point displaySize = new Point();
	public static float density = 1;

	@Override
	public void onCreate() {
		super.onCreate();
		initilizeDB();
		initImageLoader(getApplicationContext());
		Crittercism.initialize(getApplicationContext(), "5519900b7fa1f3d21c00633d");

		// For Gallery Only
		applicationContext = getApplicationContext();
		applicationHandler = new Handler(applicationContext.getMainLooper());
		checkDisplaySize();
		density = OfCampusApplication.applicationContext.getResources().getDisplayMetrics().density;

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
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs() // Remove
																																		// for
																																		// release
																																		// app
				.memoryCacheExtraOptions(480, 800).threadPoolSize(5).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public static int dp(float value) {
		return (int) Math.ceil(density * value);
	}

	public static void checkDisplaySize() {
		try {
			WindowManager manager = (WindowManager) OfCampusApplication.applicationContext.getSystemService(Context.WINDOW_SERVICE);
			if (manager != null) {
				Display display = manager.getDefaultDisplay();
				if (display != null) {
					if (android.os.Build.VERSION.SDK_INT < 13) {
						displaySize.set(display.getWidth(), display.getHeight());
					} else {
						display.getSize(displaySize);
					}
				}
			}
		} catch (Exception e) {
		}
	}
	
	
	
	
	/* Do Not modify it*/
	
	private TreeMap<String, JobDetails> jobsList =new TreeMap<String, JobDetails>();
	private TreeMap<String, JobDetails> newsList =new TreeMap<String, JobDetails>();
	private TreeMap<String, JobDetails> classifiedList =new TreeMap<String, JobDetails>();
	
	private ArrayList<JobDetails> jobs_ = new ArrayList<JobDetails>(jobsList.values());
	private ArrayList<JobDetails> news_ = new ArrayList<JobDetails>(newsList.values());
	private ArrayList<JobDetails> classifieds_ = new ArrayList<JobDetails>(classifiedList.values());
	
	/*Get LIst Method*/
	public ArrayList<JobDetails> getJobList(){
		return jobs_;
	}
	
	public ArrayList<JobDetails> getNewsList(){
		return news_;
	}
	
	public ArrayList<JobDetails> getclassifiedList(){
		return classifieds_;
	}
	
	/*Add List Method*/
	public void addJobList(ArrayList<JobDetails> jobs){
		for (JobDetails job : jobs) {
			jobsList.put(job.getPostid(), job);
		}
	}
	
	public void addNewsList(ArrayList<JobDetails> News){
		for (JobDetails New : News) {
			jobsList.put(New.getPostid(), New);
		}
	}
	
	public void addclassifiedList(ArrayList<JobDetails> classifieds){
		for (JobDetails classified : classifieds) {
			jobsList.put(classified.getPostid(), classified);
		}
	}
	
	/* Modify Data*/
	public void modifyJob(JobDetails job){
		jobsList.put(job.getPostid(), job);
	}
	
	public void modifyNews(JobDetails New){
		jobsList.put(New.getPostid(), New);
	}
	
	public void modifyclassified(JobDetails classified){
		jobsList.put(classified.getPostid(), classified);
	}
	
	
	
	
	

}
