/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright Dibakar MIstry, 2015.
 */

package com.ofcampus;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.crittercism.app.Crittercism;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.InstituteDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;

public class OfCampusApplication extends Application {

	public static GoogleAnalytics analytics;
	public static Tracker tracker;
	private static String gcmProjectKey = "981282250109";
	private static String gcmSerevrKey = "AIzaSyDFChuYp5OMPLAjDMSdEQjCCQCWxyw8d8I";

	private String TAG = "OfCampusApplication";
	// public OfCampusDBHelper DB_HELPER;
	public boolean fromMYPost = false;

	public ArrayList<InstituteDetails> institutes_;
	public ArrayList<JobDetails> filterJobs;
	public JobDetails jobdetails;
	public UserDetails mDetails;
	public CircleDetails mCircleDetails_;

	public boolean isProfileDataModify = false;
	public boolean isNewsDataModify = false;
	public boolean isPostDataModify = false;
	public boolean isclassifiedDataModify = false;
	public boolean isNewCircleCreated = false;

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

		// Google Analytics
		analytics = GoogleAnalytics.getInstance(this);
		analytics.setLocalDispatchPeriod(1800);
		tracker = analytics.newTracker("UA-64571981-1");
		tracker.enableExceptionReporting(true);
		tracker.enableAdvertisingIdCollection(true);
		tracker.enableAutoActivityTracking(true);
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
		// if (DB_HELPER == null) {
		// DB_HELPER = new OfCampusDBHelper(OfCampusApplication.this);
		// }
		// try {
		// DB_HELPER.getWritableDatabase();
		// DB_HELPER.openDataBase();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	private void closeDB() {
		// try {
		// DB_HELPER.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
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

	public void initPlayServices() {
		try {
			if (checkPlayServices()) {
				gcm = GoogleCloudMessaging.getInstance(this);
				regid = getRegistrationId();

				if (regid.length() == 0) {
					registerInBackground();
				} else {
					sendRegistrationIdToBackend();
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
						sendRegistrationIdToBackend();
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

	private void sendRegistrationIdToBackend() {
		AsyncTask<String, String, Boolean> task = new AsyncTask<String, String, Boolean>() {
			@Override
			protected Boolean doInBackground(String... objects) {
				try {
					UserDetails mDetails = UserDetails.getLoggedInUser(applicationContext);
					if (mDetails != null) {
						String[] responsedata = Util.ProfileUpdte(Util.getProfileUpdateUrl(), getJSONBody(regid), mDetails.getAuthtoken(), null);

						String authenticationJson = responsedata[1];
						boolean isTimeOut = (responsedata[0].equals("205")) ? true : false;

						if (authenticationJson != null && !authenticationJson.equals("")) {
							JSONObject mObject = new JSONObject(authenticationJson);
							String responsecode = Util.getJsonValue(mObject, "status");
							if (responsecode != null && responsecode.equals("200")) {

							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
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

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion();
		Log.e("tmessages", "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private JSONObject getJSONBody(String gcmid) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("gcmId", gcmid);
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	/**
	 * Update Alert
	 * 
	 * @param mContext
	 * @param tocken
	 */

	public void chackVersion(final Context mContext, final String tocken) {
		try {

			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			final String versionName = pInfo.versionName;
			int versionCode = pInfo.versionCode;
			AsyncTask<String, String, Boolean> task = new AsyncTask<String, String, Boolean>() {

				String appUpdateNeeded = "";
				String updateTitle = "";
				String updateMessage = "";

				@Override
				protected Boolean doInBackground(String... objects) {
					try {

						// String[] responsedata =
						// Util.POSTWithJSONAuth(Util.getVersionUpdate(),
						// getBody("1.0.2"), tocken);
						String[] responsedata = Util.POSTWithJSONAuth(Util.getVersionUpdate(), getBody(versionName), tocken);
						String authenticationJson = responsedata[1];
						boolean isTimeOut = (responsedata[0].equals("205")) ? true : false;
						if (authenticationJson != null && !authenticationJson.equals("")) {
							JSONObject mObject = new JSONObject(authenticationJson);
							String responsecode = Util.getJsonValue(mObject, "status");
							if (responsecode != null && responsecode.equals("200")) {
								JSONObject Obj = mObject.getJSONObject("results");
								if (Obj != null && !Obj.equals("")) {
									String expt = Util.getJsonValue(Obj, "exception");
									if (expt.equals("false")) {
										appUpdateNeeded = Util.getJsonValue(Obj, "appUpdate");
										updateTitle = Util.getJsonValue(Obj, "updateTitle");
										updateMessage = Util.getJsonValue(Obj, "updateMessage");

										if (appUpdateNeeded.equals("true")) {
											return true;
										}
									}
								}
							} else if (responsecode != null && responsecode.equals("500")) {

							}

						}

					} catch (Exception e) {
						Log.e("tmessages", e.toString());
					}

					return false;
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);

					if (result) {
						showUpgradeDialog(mContext, updateTitle, updateMessage);
					}

				}

			};

			if (android.os.Build.VERSION.SDK_INT >= 11) {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
			} else {
				task.execute(null, null, null);
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getBody(String appVersion) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("appVersion", appVersion);
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	private void showUpgradeDialog(final Context mContext, String title, String message) {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
		mBuilder.setTitle(title);
		mBuilder.setMessage(message);
		mBuilder.setNegativeButton("Update", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String appPackageName = getPackageName();
				try {
					mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
				}

				try {
					dialog.cancel();
					((Activity) mContext).finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mBuilder.setCancelable(false);
		mBuilder.show();
	}

}
