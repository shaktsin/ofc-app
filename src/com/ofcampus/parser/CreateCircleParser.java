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

public class CreateCircleParser {
	private Context mContext;

	private String STATUS = "status";
	private String RESULTS = "results";
	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	private String SUCCESS = "success";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	public void parse(Context context, JSONObject postData, String authorization) {
		this.mContext = context;
		CreateCircleParserAsync mCreateCircleParserAsync = new CreateCircleParserAsync(mContext, postData, authorization);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mCreateCircleParserAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); 
		} else {
			mCreateCircleParserAsync.execute();
		}
	}

	private class CreateCircleParserAsync extends AsyncTask<Void, Void, Void> {
		private Context context;
		private String authenticationJson;
		private boolean isTimeOut = false;
		private ArrayList<InstituteDetails> Institutes;
		private JSONObject postData;
		private String authorization;
		private ProgressDialog mDialog;
		private String resSuccess = "";

		public CreateCircleParserAsync(Context mContext,
				JSONObject postData_, String authorization_) {
			this.context = mContext;
			this.postData = postData_;
			this.authorization = authorization_;
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
				String[] responsedata = Util.POST_JOB(Util.getCreateCircleUrl(), postData, authorization);
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
								resSuccess = "true";
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
				if (createcircleparserinterface != null) {
					createcircleparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (resSuccess != null && resSuccess.equalsIgnoreCase("true")) {
					if (createcircleparserinterface != null) {
						createcircleparserinterface.OnSuccess(); 
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

	public JSONObject getBody(String name, String moderate , String circleId) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("name", name);
			jsObj.put("moderate", moderate);
			jsObj.put("circleId", circleId);
			jsObj.put("appName", "ofCampus");
			jsObj.put("plateFormId", "0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	public CreateCircleParserInterface createcircleparserinterface;

	public CreateCircleParserInterface getCreatecircleparserinterface() {
		return createcircleparserinterface;
	}

	public void setCreatecircleparserinterface(
			CreateCircleParserInterface createcircleparserinterface) {
		this.createcircleparserinterface = createcircleparserinterface;
	}

	public interface CreateCircleParserInterface {
		public void OnSuccess();

		public void OnError();
	}
}
