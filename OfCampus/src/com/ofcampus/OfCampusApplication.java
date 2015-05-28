/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright Dibakar MIstry, 2015.
 */

package com.ofcampus;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.crittercism.app.Crittercism;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
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

	private static String gcmProjectKey = "417713273173";
	private static String gcmSerevrKey = "AIzaSyAPo4ELrlJ852Df5jtzwjHsMJBx0ggZjMc";
	// APA91bFeAvia1NW8enHYZhM_2dhNLoy3-FynM0c4P9UW0VIXrzbeOQVfGpJ7nCjg8hyhy-yMGj0-hWq1xNX4u2WysPJGUfkf0llt8jOy6XHwgsElDvRAYqPhge-snkETrjwVpX0sAxnZ

	private String TAG = "OfCampusApplication";
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

		// GCM
		initPlayServices();
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

	/***************************** GCM Implementation *******************************************/
	/******************************* http://gcm-alert.appspot.com/ ****************************/
	private GoogleCloudMessaging gcm;
	private String regid;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	private void initPlayServices() {
		try {
			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(this);
				regid = getRegistrationId();

				if (regid.length() == 0) {
					registerInBackground();
				} else {
					sendRegistrationIdToBackend(false);
				}
			} else {
				Log.d("tmessages", "No valid Google Play Services APK found.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				Log.i(TAG, "Error");
			} else {
				Log.i(TAG, "This device is not supported.");
			}
			return false;
		}
		return true;

	}

	private String getRegistrationId() {
		final SharedPreferences prefs = getGCMPreferences(applicationContext);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			Log.d("tmessages", "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			Log.d("tmessages", "App version changed.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(OfCampusApplication.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	public static int getAppVersion() {
		try {
			PackageInfo packageInfo = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private void registerInBackground() {
		AsyncTask<String, String, Boolean> task = new AsyncTask<String, String, Boolean>() {
			@Override
			protected Boolean doInBackground(String... objects) {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(applicationContext);
				}
				int count = 0;
				while (count < 1000) {
					try {
						count++;
						regid = gcm.register(gcmProjectKey);
						sendRegistrationIdToBackend(true);
						storeRegistrationId(applicationContext, regid);
						return true;
					} catch (Exception e) {
						Log.e("tmessages", e.toString());
					}
					try {
						if (count % 20 == 0) {
							Thread.sleep(60000 * 30);
						} else {
							Thread.sleep(5000);
						}
					} catch (InterruptedException e) {
						Log.e("tmessages", e.toString());
					}
				}
				return false;
			}
		};

		if (android.os.Build.VERSION.SDK_INT >= 11) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
		} else {
			task.execute(null, null, null);
		}
	}

	private void sendRegistrationIdToBackend(final boolean isNew) {
		AsyncTask<String, String, Boolean> task = new AsyncTask<String, String, Boolean>() {
			@Override
			protected Boolean doInBackground(String... objects) {

				return false;
			}
		};

		if (android.os.Build.VERSION.SDK_INT >= 11) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
		} else {
			task.execute(null, null, null);
		}
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion();
		Log.e("tmessages", "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

}
