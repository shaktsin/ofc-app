/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ofcampus.Util;

public class VerifyUserParser {

	private Context mContext;

	private String STATUS = "status";
	private String RESULTS = "results";
	// private String EXCEPTION="exception";
	private String VERIFIED = "verified";
	private String NAME = "name";
	private String EMAIL = "email";
	private String AUTHTOKEN = "authToken";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	public void parse(Context context, JSONObject json, String auth) {
		this.mContext = context;
		VerifyUserParserAsync mVerifyUserParserAsync = new VerifyUserParserAsync(mContext, json, auth);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mVerifyUserParserAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mVerifyUserParserAsync.execute();
		}
	}

	private class VerifyUserParserAsync extends AsyncTask<Void, Void, Void> {
		private Context context;
		private String authenticationJson, auth;
		private JSONObject postData;
		private boolean success = false;
		private boolean isTimeOut = false;
		private ProgressDialog mDialog;

		public VerifyUserParserAsync(Context mContext, JSONObject json, String auth) {
			this.context = mContext;
			this.postData = json;
			this.auth = auth;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = new ProgressDialog(mContext);
			mDialog.setMessage("Loading...");
			mDialog.setCancelable(false);
			mDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				String[] responsedata = Util.POSTWithJSONAuth(Util.getVerifyUrl(), postData, auth);
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205")) ? true : false;

				if (authenticationJson != null && !authenticationJson.equals("")) {
					JSONObject mObject = new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode != null && responsecode.equals("200")) {
						JSONObject userObj = mObject.getJSONObject(RESULTS);
						success = (Util.getJsonValue(userObj, "success").equals("true")) ? true : false;
					} else if (responsecode != null && responsecode.equals("500")) {
						JSONObject userObj = mObject.getJSONObject(RESULTS);
						if (userObj != null) {
							responseDetails = userObj.getJSONArray("messages").get(0).toString();
						}
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (mDialog != null && mDialog.isShowing()) {
				mDialog.cancel();
				mDialog = null;
			}

			if (isTimeOut) {
				if (verifyuserpsinterface != null) {
					verifyuserpsinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (verifyuserpsinterface != null) {
					verifyuserpsinterface.OnSuccess(success);
				}

			} else if (responsecode.equals("500")) {
				Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, "Verify error.");
			}
		}
	}

	public JSONObject getparamBody(String code) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("token", code);
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	public VerifyUserPsInterface verifyuserpsinterface;

	public VerifyUserPsInterface getVerifyuserpsinterface() {
		return verifyuserpsinterface;
	}

	public void setVerifyuserpsinterface(VerifyUserPsInterface verifyuserpsinterface) {
		this.verifyuserpsinterface = verifyuserpsinterface;
	}

	public interface VerifyUserPsInterface {
		public void OnSuccess(boolean success);

		public void OnError();
	}
}