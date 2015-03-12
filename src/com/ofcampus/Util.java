package com.ofcampus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Util {

	public static int connectTimeout = 10000;
	public static int socketTimeout = 30000;
	public static int delaytime = 2000;

	public static long delay = 5 * 1000;
	public static long period = 30 * 1000;

	private static String baseUrl = "http://205.147.110.176:8080/api/";

	public static String TITLES[] = { "My Profile", "My Posts", "Important Mail", "Hide Post", "Circle", "Settings", "Logout" };
	
	
	public static String sendto[] = { "Everyone", "Class of 2014","General Management Club", "Finance Club", "Hyderabad Chapter" };
	
	public static String TOOLTITLE[] = { "Comment", "Details" };

	public static int ICONS[] = { R.drawable.ic_profile, R.drawable.ic_mypost,
			R.drawable.ic_impmail, R.drawable.ic_recycle, R.drawable.ic_circle,
			R.drawable.ic_settings, R.drawable.ic_logout };

	public static enum userType {
		Normal, Gmail, Facebook
	}

	public static enum jobListCallFor {
		Normal, refresh
	}

	public static enum JobDataReturnFor {
		Normal, syncdata
	}

	/********************URl List**********************/
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
	
	public static String getAllCircleUrl() {
		return baseUrl + "circle/all";
	}
	
	public static String getJOBPostedProfileUrl() {
		return baseUrl + "post/user/profile";
	}
	
	public static String getCircleProfileUrl() {
		return baseUrl + "post/circle/profile";
	}
	
	
	
	
	/********************URl List**********************/
	
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
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetwork != null && wifiNetwork.isConnected()) {
			return true;
		}

		NetworkInfo mobileNetwork = cm
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
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
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
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
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
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
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httppost.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
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
			httpget.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httpget.setHeader(postData.get(0).getName(), postData.get(0)
					.getValue());
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
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
	public static String[] sendGet(String url){
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

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
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
	
	public static String[] POST(String url, JSONObject jsonObject) {
		InputStream inputStream = null;
		String result = "";
		String[] responData = { "", "" };

		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
			HttpPost httpPost = new HttpPost(url);
			String json = "";
			if (jsonObject!=null) {
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

	public static String[] POST_JOB(String url, JSONObject jsonObject,
			String auth) {
		InputStream inputStream = null;
		String result = "";
		String[] responData = { "", "" };

		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
			HttpPost httpPost = new HttpPost(url);
			String json = "";
			if (jsonObject!=null) {
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
	
	
	
	public static String[] POST_JOBNEW(String url, JSONObject jsonObject,String auth,ArrayList<String> paths) {
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
//			httpPost.setHeader("Content-type", "application/json");
			
			
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			File lfile = null;
			reqEntity.addPart("jobs", new StringBody(json));
			if (paths != null && paths.size() >= 1) {
				for (String path : paths) {
					if (path != null) {
						lfile = new File(path);
						FileBody lFileBody_ = new FileBody(lfile);
						reqEntity.addPart("iFile", lFileBody_);
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
	 
	public static String[] ProfileUpdte(String url, JSONObject jsonObject,String auth,String path) {
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
			reqEntity.addPart("prof", new StringBody(json));
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

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
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

	public static void shareIntent(Context mContext, String subject,
			String content) {
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

	public static void onShareClick(Context mcContext, View v, String subject,
			String content) {

		try {
			List<String> PackageName = getShareApplication();
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			Intent share = new Intent(android.content.Intent.ACTION_SEND);
			share.setType("text/plain");
			List<ResolveInfo> resInfo = mcContext.getPackageManager()
					.queryIntentActivities(share, 0);
			if (!resInfo.isEmpty()) {
				for (ResolveInfo info : resInfo) {
					Intent targetedShare = new Intent(
							android.content.Intent.ACTION_SEND);
					targetedShare.setType("text/plain"); // put here your mime//
															// type
					if (PackageName.contains(info.activityInfo.packageName
							.toLowerCase())) {
						targetedShare.putExtra(Intent.EXTRA_SUBJECT, subject);
						targetedShare.putExtra(Intent.EXTRA_TEXT, content);
						targetedShare.setPackage(info.activityInfo.packageName
								.toLowerCase());
						targetedShareIntents.add(targetedShare);
					}
				}
				Intent chooserIntent = Intent.createChooser(
						targetedShareIntents.remove(0), "Share using...");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						targetedShareIntents.toArray(new Parcelable[] {}));
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

	/**
	 * Reply Text via Email.
	 * 
	 * @param mcContext
	 * @param SUBJECT
	 * @param TEXT
	 * @param to
	 */
	private void replyViaEmailCalling(Context mcContext, String SUBJECT,
			String TEXT, String to) {
		try {
			Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			sendIntent.setType("plain/text");
			sendIntent.setClassName("com.google.android.gm",
					"com.google.android.gm.ComposeActivityGmail");
			sendIntent.putExtra(Intent.EXTRA_CC, new String[] { to });
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "My Incident Report");
			sendIntent.putExtra(Intent.EXTRA_TEXT, TEXT);
			mcContext.startActivity(sendIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
