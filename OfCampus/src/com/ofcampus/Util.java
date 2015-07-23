/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright Dibakar MIstry, 2015.
 */

package com.ofcampus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Util {

	public static int connectTimeout = 10000;
	public static int socketTimeout = 30000;
	public static int delaytime = 2000;

	public static int progressRngwdth = 2;

	public static long delay = 5 * 1000;
	public static long period = 30 * 1000;

	// private static String baseUrl = "http://205.147.110.176:8080/api/";
	private static String baseUrl = "http://ofcampus.com/api/";

	private static String SDCardPath = "OfCampus/Document";
	private static String SDCardPathForImage = "OfCampus/Images";
	public static String defaultYear = "2014";

	// public static String TITLES[] = { "My Profile", "My Posts",
	// "Bookmarked Posts", "Hidden Posts", "Clubs", "Settings", "Logout" };
	public static String TITLES[] = { "My Posts", "Bookmarked Posts", "Hidden Posts", "Clubs", "Chapters", "Settings", "Logout" };
	public static String sendto[] = { "Everyone", "Class of 2014", "General Management Club", "Finance Club", "Hyderabad Chapter" };

	public static String TOOLTITLE[] = { "Post Details", "News Details", "Classified Details" };
	public static String TOOLTITLE_FILTER[] = { "Filter Posts", "Filter News" };
	public static String BUNDLE_KEY[] = { "CAMEFROM", "COMMENTORDETAILS" };

	// public static int ICONS[] = { R.drawable.ic_profile,
	// R.drawable.ic_mypost,
	// R.drawable.ic_impmail, R.drawable.ic_recycle, R.drawable.ic_circle,
	// R.drawable.ic_settings, R.drawable.ic_logout };
	public static int ICONS[] = { R.drawable.ic_mypost, R.drawable.ic_impmail, R.drawable.ic_recycle, R.drawable.ic_circle, R.drawable.ic_circle, R.drawable.ic_settings, R.drawable.ic_logout };

	public static String shareVia = "\nShared via ofCampus";

	public static enum userType {
		Normal, Gmail, Facebook
	}

	public static enum jobListCallFor {
		Normal, refresh
	}

	public static enum JobDataReturnFor {
		Normal, syncdata
	}

	public static enum SearchType {
		USERS, CIRCLE, POSTS
	}

	public static enum PostType {
		NEWS, POST, CLASSIFIEDS
	}

	public static enum ClassifSpinnerType {
		SINGLESELECTION, MULTISELECTION
	}

	public static String getSDCardPath() {
		return SDCardPath;
	}

	/******************** URl List **********************/
	public static String getLoginUrl() {
		return baseUrl + "user/login";
	}

	public static String getSignUp() {
		return baseUrl + "user/signUp";
	}

	public static String getInstituteUrl() {
		return baseUrl + "institute/all";
	}

	public static String getJobListUrl() {
		return baseUrl + "jobs/list";
	}

	public static String getPrepareUrl() {
		return baseUrl + "jobs/prepare";
	}

	public static String getcreateJobUrl() {
		return baseUrl + "jobs/create";
	}

	public static String getJobDetailsUrl(String jobID) {
		return baseUrl + "jobs/" + jobID;
	}

	public static String getJobSyncCountUrl() {
		return baseUrl + "post/post/sync";
	}

	public static String getJobHidetUrl() {
		return baseUrl + "post/react";
	}

	public static String getMyPostJobUrl() {
		return baseUrl + "post/myposts";
	}

	public static String getImportantmailUrl() {
		return baseUrl + "post/imp";
	}

	public static String getCommentPostUrl() {
		return baseUrl + "post/comment";
	}

	public static String getOldCommentsUrl() {
		return baseUrl + "post/comment/all";
	}

	public static String getReverseProcePostUrl() {
		return baseUrl + "post/react/reverse";
	}

	/*
	 * Job Filter Data
	 */
	public static String getFilterJobUrl() {
		return baseUrl + "post/filters/all";
	}

	public static String getFilterUrl() {
		return baseUrl + "jobs/list?";
	}

	public static String getVerifyUrl() {
		return baseUrl + "user/verify";
	}

	public static String getRegenerateTokenUrl() {
		return baseUrl + "user/generate/token";
	}

	public static String getJobEditUrl() {
		return baseUrl + "jobs/edit";
	}

	public static String getProfileUpdateUrl() {
		return baseUrl + "user/update";
	}

	public static String getCreateCircleUrl() {
		return baseUrl + "circle/create";
	}

	public static String getJoinCircleUrl() {
		return baseUrl + "circle/join";
	}

	public static String getUnJoinCircleUrl() {
		return baseUrl + "circle/unjoin";
	}

	public static String getUnJoinListCircleUrl() {
		return baseUrl + "circle/all";
	}

	public static String getJoinListCircleUrl() {
		return baseUrl + "circle/user/all";
	}

	public static String getJOBPostedProfileUrl() {
		return baseUrl + "post/user/profile";
	}

	public static String getCircleProfileUrl() {
		return baseUrl + "post/circle/profile";
	}

	public static String getCircleActivateUrl() {
		return baseUrl + "circle/activate";
	}

	public static String getCircleDeActivateUrl() {
		return baseUrl + "circle/deactivate";
	}

	public static String getAcceptRequestUrl() {
		return baseUrl + "circle/authorize";
	}

	public static String getRejectRequestUrl() {
		return baseUrl + "circle/authorize/revoke";
	}

	public static String getAllPendingRequestUrl() {
		return baseUrl + "circle/requests";
	}

	public static String getForGotPasswordUrl() {
		return baseUrl + "user/forgot/password";
	}

	public static String getResetPasswordUrl() {
		return baseUrl + "user/change/password";
	}

	// News Section
	public static String getNewsListUrl() {
		return baseUrl + "feed/list";
	}

	public static String getPrepareNewsUrl() {
		return baseUrl + "feed/prepare";
	}

	public static String getCreateNewsUrl() {
		return baseUrl + "feed/create";
	}

	public static String getGetNewsfeedUrl(String postId) {
		return baseUrl + "feed/" + postId;
	}

	public static String getEditNewsUrl() {
		return baseUrl + "feed/edit";
	}

	public static String getSearchURL() {
		return baseUrl + "search/all";
	}

	public static String getPreparClassifiedeUrl() {
		return baseUrl + "classified/prepare";
	}

	public static String getCreateClassifiedeUrl() {
		return baseUrl + "classified/create";
	}

	public static String getEditClassifiedeUrl() {
		return baseUrl + "classified/edit";
	}

	public static String getGetClassifiedDetailsUrl(String postId) {
		return baseUrl + "classified/" + postId;
	}

	public static String getGetClassifiedListUrl() {
		return baseUrl + "classified/list";
	}

	public static String getVersionUpdate() {
		return baseUrl + "app/update";
	}

	public static String getUserPost() {
		return baseUrl + "post/user/posts";
	}

	public static String getCirclePosts() {
		return baseUrl + "post/circle/posts";
	}

	public static String getCircleUsers() {
		return baseUrl + "api/circle/users";
	}

	/******************** URl List **********************/

	/**
	 * Show Alert Toast message.
	 */
	public static void ShowToast(Context context, String msg) {
		Toast mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}

	public static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences("OfCampus", Activity.MODE_PRIVATE);
	}

	/**
	 * Check device Internet connection.
	 */
	public static boolean hasConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mobileNetwork != null && mobileNetwork.isConnected()) {
			return true;
		}

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			return true;
		}
		return false;
	}

	public static void HideKeyBoard(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public final static boolean isValidEmail(String email) {
		if (email == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		}
	}

	public final static boolean isValidEmail_again(String email) {
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static String Gender(String gender) {
		if (gender.equalsIgnoreCase("male")) {
			return "0";
		} else {
			return "1";
		}
	}

	/**
	 * Name Value pair request.
	 */
	public static String[] PostRequest(List<NameValuePair> postData, String url) {
		String res = "";
		String[] responData = { "", "" };
		try {
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httppost.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
			HttpResponse httpResponse = httpclient.execute(httppost);
			// httpResponse.getStatusLine();
			// HttpEntity entity = httpResponse.getEntity();

			res = EntityUtils.toString(httpResponse.getEntity());
			responData[0] = "200";
			responData[1] = res;

		} catch (ConnectTimeoutException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (ParseException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IOException e) {
			responData[0] = "205";
			e.printStackTrace();
		}
		return responData;
	}

	/**
	 * Name Value pair request.
	 */
	public static String[] GetRequest(List<NameValuePair> postData, String url) {
		String res = "";
		String[] responData = { "", "" };
		try {
			HttpGet httpget = new HttpGet(url);
			httpget.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpget.setHeader(postData.get(0).getName(), postData.get(0).getValue());
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
			HttpResponse httpResponse = httpclient.execute(httpget);
			// httpResponse.getStatusLine();
			// HttpEntity entity = httpResponse.getEntity();

			res = EntityUtils.toString(httpResponse.getEntity());
			responData[0] = "200";
			responData[1] = res;

		} catch (ConnectTimeoutException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (ParseException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IOException e) {
			responData[0] = "205";
			e.printStackTrace();
		}
		return responData;
	}

	// HTTP GET request
	public static String[] sendGet(String url) {
		String Response = "";

		String[] responData = { "", "" };

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setConnectTimeout(connectTimeout); // set timeout to 5 seconds
			con.setReadTimeout(socketTimeout);

			// optional default is GET
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			Response = response.toString();
			System.out.println(response.toString());
			responData[0] = "200";
			responData[1] = Response;
		} catch (java.net.SocketTimeoutException e) {
			e.printStackTrace();
			responData[0] = "205";
		} catch (Exception e) {
			e.printStackTrace();
			responData[0] = "205";
		}

		return responData;
	}

	/**
	 * 
	 * @param url
	 * @param jsonObject
	 * @return
	 */
	public static String[] POSTWithJSON(String url, JSONObject jsonObject) {
		InputStream inputStream = null;
		String result = "";
		String[] responData = { "", "" };

		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
			HttpPost httpPost = new HttpPost(url);
			String json = "";
			if (jsonObject != null) {
				json = jsonObject.toString();
			}
			StringEntity se = new StringEntity(json);
			httpPost.setEntity(se);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			HttpResponse httpResponse = httpclient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
			responData[0] = "200";
			responData[1] = result;
		} catch (UnsupportedEncodingException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IllegalStateException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IOException e) {
			responData[0] = "205";
			e.printStackTrace();
		}

		return responData;
	}

	/**
	 * 
	 * @param url
	 * @param jsonObject
	 * @param auth
	 * @return
	 */
	public static String[] POSTWithJSONAuth(String url, JSONObject jsonObject, String auth) {
		InputStream inputStream = null;
		String result = "";
		String[] responData = { "", "" };

		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
			HttpPost httpPost = new HttpPost(url);
			String json = "";
			if (jsonObject != null) {
				json = jsonObject.toString();
			}
			StringEntity se = new StringEntity(json);
			httpPost.setEntity(se);
			httpPost.setHeader("Authorization", auth);
			httpPost.setHeader("Content-type", "application/json");
			HttpResponse httpResponse = httpclient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
			responData[0] = "200";
			responData[1] = result;
		} catch (UnsupportedEncodingException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IllegalStateException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IOException e) {
			responData[0] = "205";
			e.printStackTrace();
		}

		return responData;
	}

	public static String[] POSTWithAuthJSONFile(String url, JSONObject jsonObject, String auth, ArrayList<String> paths, String JSONTAG, ArrayList<String> docpdfPaths) {
		InputStream inputStream = null;
		String result = "";
		String[] responData = { "", "" };

		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
			HttpPost httpPost = new HttpPost(url);

			String json = "";
			json = jsonObject.toString();

			httpPost.setHeader("Authorization", auth);
			// httpPost.setHeader("Content-type", "application/json");

			Log.d("POST_BODY", jsonObject.toString());

			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(HTTP.UTF_8));
			httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			File lfile = null;
			reqEntity.addPart(JSONTAG, new StringBody(json, Charset.forName(HTTP.UTF_8)));
			if (paths != null && paths.size() >= 1) {
				for (String path : paths) {
					if (path != null) {
						lfile = new File(path);
						FileBody lFileBody_ = new FileBody(lfile);
						reqEntity.addPart("iFile", lFileBody_);
					}
				}
			}
			File jFile = null;
			if (docpdfPaths != null && docpdfPaths.size() >= 1) {
				for (String docpdf : docpdfPaths) {
					if (docpdf != null) {
						jFile = new File(docpdf);
						FileBody jFileBody_ = new FileBody(jFile);
						reqEntity.addPart("jFile", jFileBody_);
					}
				}
			}

			httpPost.setEntity(reqEntity);
			HttpResponse httpResponse = httpclient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
			responData[0] = "200";
			responData[1] = result;

			Log.d("RESPONSE_BODY", result);

		} catch (UnsupportedEncodingException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IllegalStateException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IOException e) {
			responData[0] = "205";
			e.printStackTrace();
		}

		return responData;
	}

	/**
	 * 
	 * @param url
	 * @param jsonObject
	 * @param auth
	 * @param path
	 * @return
	 */
	public static String[] ProfileUpdte(String url, JSONObject jsonObject, String auth, String path) {
		InputStream inputStream = null;
		String result = "";
		String[] responData = { "", "" };

		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
			HttpPost httpPost = new HttpPost(url);

			String json = "";
			json = jsonObject.toString();
			httpPost.setHeader("Authorization", auth);
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			File lfile = null;
			reqEntity.addPart("prof", new StringBody(json, Charset.forName(HTTP.UTF_8)));
			if (path != null && !path.equals("")) {
				lfile = new File(path);
				FileBody lFileBody_ = new FileBody(lfile);
				reqEntity.addPart("iFile", lFileBody_);
			}

			httpPost.setEntity(reqEntity);
			HttpResponse httpResponse = httpclient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
			responData[0] = "200";
			responData[1] = result;

		} catch (UnsupportedEncodingException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IllegalStateException e) {
			responData[0] = "205";
			e.printStackTrace();
		} catch (IOException e) {
			responData[0] = "205";
			e.printStackTrace();
		}

		return responData;
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;
		inputStream.close();
		return result;

	}

	public static String getJsonValue(JSONObject jsObject, String Key) {
		String value = "";
		try {
			if (jsObject.has(Key)) {
				value = jsObject.getString(Key);
				value = value.equalsIgnoreCase("null") ? "" : value;
			} else {
				value = "";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static void shareIntent(Context mContext, String subject, String content) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			intent.putExtra(Intent.EXTRA_TEXT, content);
			mContext.startActivity(Intent.createChooser(intent, "Share using"));
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void onShareClick(Context mcContext, View v, String subject, String content) {

		try {
			List<String> PackageName = getShareApplication();
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			Intent share = new Intent(android.content.Intent.ACTION_SEND);
			share.setType("text/plain");
			List<ResolveInfo> resInfo = mcContext.getPackageManager().queryIntentActivities(share, 0);
			if (!resInfo.isEmpty()) {
				for (ResolveInfo info : resInfo) {
					Intent targetedShare = new Intent(android.content.Intent.ACTION_SEND);
					targetedShare.setType("text/plain"); // put here your mime//
															// type
					if (PackageName.contains(info.activityInfo.packageName.toLowerCase())) {
						targetedShare.putExtra(Intent.EXTRA_SUBJECT, subject);
						targetedShare.putExtra(Intent.EXTRA_TEXT, content + shareVia);
						targetedShare.setPackage(info.activityInfo.packageName.toLowerCase());
						targetedShareIntents.add(targetedShare);
					}
				}
				Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Share using...");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[] {}));
				mcContext.startActivity(chooserIntent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static List<String> getShareApplication() {
		List<String> mList = new ArrayList<String>();
		mList.add("com.facebook.katana");
		mList.add("com.twitter.android");
		mList.add("com.google.android.gm");
		mList.add("com.whatsapp");
		mList.add("com.android.mms");
		return mList;
	}

	public static boolean isContainDocFile(String url) {
		if (url.contains(".doc")) {
			return true;
		} else if (url.contains(".DOC")) {
			return true;
		}
		if (url.contains(".docx")) {
			return true;
		} else if (url.contains(".DOCX")) {
			return true;
		}
		if (url.contains(".pdf")) {
			return true;
		} else if (url.contains(".PDF")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDocFile(String url) {
		if (url.contains(".doc")) {
			return true;
		} else if (url.contains(".DOC")) {
			return true;
		}
		if (url.contains(".docx")) {
			return true;
		} else if (url.contains(".DOCX")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isPdfFile(String url) {
		if (url.contains(".pdf")) {
			return true;
		} else if (url.contains(".PDF")) {
			return true;
		} else {
			return false;
		}
	}

	public static void viewerOpen(Context mContext, String filePath) {
		try {
			if (Util.isDocFile(filePath)) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				String type = "application/msword";
				intent.setDataAndType(Uri.fromFile(new File(filePath)), type);
				mContext.startActivity(intent);
			} else if (Util.isPdfFile(filePath)) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				String type = "application/pdf";
				intent.setDataAndType(Uri.fromFile(new File(filePath)), type);
				mContext.startActivity(intent);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isFileExist(String filePath) {
		try {
			String[] splt = filePath.split("/");
			final String fileNAme = splt[splt.length - 1];
			File extStore = Environment.getExternalStorageDirectory();
			File myFile = new File(extStore.getAbsolutePath() + ("/" + SDCardPath + "/" + fileNAme));
			return (myFile.exists()) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Returns capitalized string
	 * 
	 * Util.capitalize(null) = null Util.capitalize("") = ""
	 * Util.capitalize("cat") = "Cat" Util.capitalize("cAt") = "CAt"
	 * 
	 * @param str
	 * @return
	 */
	public static String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuilder(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
	}

	public static String getPostedOnText(final String time) {
		String postedOn = null;
		try {
			DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMM yyyy, hh:mm a");
			DateTime postedDateTime = formatter.parseDateTime(time);
			DateTime currentDateTime = new DateTime();
			final Period period = new Period(postedDateTime, currentDateTime);
			if (period.getDays() >= 3) {
				String splittime = time.substring(0, time.indexOf(","));
				postedOn = "Posted on " + splittime;
			} else if (period.getDays() >= 1 && period.getDays() < 3) {
				if (period.getDays() == 1) {
					postedOn = "Posted " + period.getDays() + " day ago";
				} else {
					postedOn = "Posted " + period.getDays() + " days ago";
				}
			} else if (period.getHours() < 24 && period.getHours() >= 1) {
				if (period.getHours() == 1) {
					postedOn = "Posted " + period.getHours() + " hour ago";
				} else {
					postedOn = "Posted " + period.getHours() + " hours ago";
				}
			} else if (period.getHours() < 1 && period.getMinutes() >= 1) {
				postedOn = "Posted " + period.getMinutes() + " mins ago";
			} else if (period.getMinutes() < 1) {
				postedOn = "Just now";
			} else {
				postedOn = "Posted on " + time;
			}
		} catch (Exception ex) {
			postedOn = "Posted on " + time;
		}
		return postedOn;
	}

	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	public static String removeLastChr(String str) {
		if (str.length() >= 1) {
			str = str.substring(0, str.length() - 2);
		}
		return str;
	}

	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, Resources resources) {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
		return (int) px;
	}

	/**
	 * Image Processing :
	 */

	public static String compressImage(Context mContext, String actualPath) {
		/**
		 * Get Bitmap from file path
		 */
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap data = BitmapFactory.decodeFile(actualPath, options);

		/**
		 * Where you want to store new compressed image.
		 */
		String path = getFilename();

		String filePath = getRealPathFromURI(mContext, path);
		Bitmap scaledBitmap = null;

		// by setting this field as true, the actual bitmap pixels are not
		// loaded in the memory. Just the bounds are loaded. If
		// you try the use the bitmap here, you will get null.
		options.inJustDecodeBounds = true;
		Bitmap bmp = data;
		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;

		Point displaysize = OfCampusApplication.displaySize;

		// max Height and width values of the compressed image is taken as
		// 816x612
		// float maxHeight = 816.0f;
		// float maxWidth = 612.0f;
		float maxHeight = displaysize.y;
		float maxWidth = displaysize.x;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		// width and height values are set maintaining the aspect ratio of the
		// image
		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;
			}
		}

		// setting inSampleSize value allows to load a scaled down version of
		// the original image
		options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

		// inJustDecodeBounds set to false to load the actual bitmap
		options.inJustDecodeBounds = false;

		// this options allow android to claim the bitmap memory if it runs low
		// on memory
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16 * 1024];

		try {
			// load the bitmap from its path
			bmp = data;
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();

		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();
		}

		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float) options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

		// check the rotation of the image and display it properly
		// ExifInterface exif;
		try {
			Matrix matrix = new Matrix();
			// matrix.postRotate(rotation);
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileOutputStream out = null;
		String filename = filePath;
		try {
			out = new FileOutputStream(filename);
			// write the compressed bitmap at the destination specified by
			// filename.
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return filename;

	}

	private static String getRealPathFromURI(Context mContext, String contentURI) {
		Uri contentUri = Uri.parse(contentURI);
		Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
		if (cursor == null) {
			return contentUri.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
	}

	public static String getFilename() {
		File file = new File(Environment.getExternalStorageDirectory().getPath(), SDCardPathForImage);
		if (!file.exists()) {
			file.mkdirs();
		}
		String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
		return uriSting;

	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}

}
