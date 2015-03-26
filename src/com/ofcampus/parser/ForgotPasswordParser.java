/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.parser;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ofcampus.Util;
import com.ofcampus.model.InstituteDetails;

public class ForgotPasswordParser {

	private Context mContext;
	private String STATUS = "status";
	private String RESULTS = "results";
	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	private String SUCCESS = "success";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	public void parse(Context context, JSONObject postData) {
		this.mContext = context;
		Async mAsync = new Async(mContext, postData); 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); 
		} else { 
			mAsync.execute();  
		}
	}

	private class Async extends AsyncTask<Void, Void, Void> {
		private Context context;
		private String authenticationJson;
		private boolean isTimeOut = false;
		private ArrayList<InstituteDetails> Institutes;
		private JSONObject postData;
		private ProgressDialog mDialog;
		private String resSuccess = "";

		public Async(Context mContext,
				JSONObject postData_) {
			this.context = mContext;
			this.postData = postData_;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog = new ProgressDialog(mContext);
			mDialog.setMessage("Processing your request...");
			mDialog.setCancelable(false);
			mDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				String[] responsedata = Util.POST(Util.getForGotPasswordUrl(), postData);
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205")) ? true : false;

				if (authenticationJson != null && !authenticationJson.equals("")) {
					JSONObject mObject = new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode != null && responsecode.equals("200")) {
						JSONObject Obj = mObject.getJSONObject(RESULTS);
						if (Obj != null && !Obj.equals("")) {
							String expt = Util.getJsonValue(Obj, EXCEPTION);
							if (expt.equals("false")) {
								resSuccess = Util.getJsonValue(Obj, SUCCESS);
							}
						}
					} else if (responsecode != null && responsecode.equals("500")) {
						JSONObject userObj = mObject.getJSONObject(RESULTS);
						if (userObj != null) {
							responseDetails = userObj.getJSONArray("messages").get(0).toString();
						}
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
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
				if (forgotpasswordparserinterface != null) {
					forgotpasswordparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (resSuccess != null && resSuccess.equalsIgnoreCase("true")) {
					if (forgotpasswordparserinterface != null) {
						forgotpasswordparserinterface.OnSuccess();     
					}
				} else {
					Util.ShowToast(mContext, "Error occure.");
				}
			} else if (responsecode.equals("500")) {
				Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, "Error occure.");
			}

		}
	}
	
//	{"email":"shakti@ofcampus.com", "appName":"ofCampus", "plateFormId":0}
	
	public JSONObject getBody(String email) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("email", email);
			jsObj.put("appName", "ofCampus");
			jsObj.put("plateFormId", "0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	public ForgotPasswordParserInterface forgotpasswordparserinterface;

	public ForgotPasswordParserInterface getForgotpasswordparserinterface() {
		return forgotpasswordparserinterface;
	}

	public void setForgotpasswordparserinterface(
			ForgotPasswordParserInterface forgotpasswordparserinterface) {
		this.forgotpasswordparserinterface = forgotpasswordparserinterface;
	}

	public interface ForgotPasswordParserInterface {
		public void OnSuccess();

		public void OnError();
	}

}
