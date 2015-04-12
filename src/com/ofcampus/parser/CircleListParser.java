/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.ofcampus.Util;
import com.ofcampus.model.CircleDetails;

public class CircleListParser {


	private Context mContext;

	private String STATUS = "status";
	private String RESULTS = "results";
	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	private String SUCCESS = "success";
	
	private String ID = "id";
	private String NAME = "name";
	private String SELECTED = "selected";
	private String MEMBERS = "members";
	private String POSTS = "posts";
	private String JOINED = "joined";
	private String ADMIN = "admin";
	private String MODERATE = "moderate";
    
    
	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	public void parse(Context context, JSONObject postData, String authorization, boolean b) { 
		this.mContext = context;
		CircleListParserAsync mCircleListParserAsync = new CircleListParserAsync(mContext, postData, authorization,b);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mCircleListParserAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); 
		} else {
			mCircleListParserAsync.execute();  
		}
	}

	private class CircleListParserAsync extends AsyncTask<Void, Void, Void> {
		private Context context;
		private String authenticationJson;
		private boolean isTimeOut = false;
		private ArrayList<CircleDetails> circlerList;
		private JSONObject postData;
		private String authorization;
		private ProgressDialog mDialog;
		private String resSuccess = "";
		private boolean isPGShowing=false;

		public CircleListParserAsync(Context mContext,
				JSONObject postData_, String authorization_, boolean b) {
			this.context = mContext;
			this.postData = postData_;
			this.authorization = authorization_;
			this.isPGShowing=b;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isPGShowing) {
				mDialog = new ProgressDialog(mContext);
				mDialog.setMessage("Processing your request...");
				mDialog.setCancelable(false);
				mDialog.show();
			}
			
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				String[] responsedata = Util.POSTWithJSONAuth(Util.getUnJoinListCircleUrl(), postData, authorization);
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205")) ? true : false;

//				Log.e("UnJoinList", authenticationJson.toString());
				
				if (authenticationJson != null && !authenticationJson.equals("")) {
					JSONObject mObject = new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode != null && responsecode.equals("200")) {
						JSONObject Obj = mObject.getJSONObject(RESULTS);
						if (Obj != null && !Obj.equals("")) {
							String expt = Util.getJsonValue(Obj, EXCEPTION);
							if (expt.equals("false")) {
								circlerList = parseData(Obj);
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
				if (circlelistparserinterface != null) {
					circlelistparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (circlerList != null && circlerList.size()>=1) {
					if (circlelistparserinterface != null) {
						circlelistparserinterface.OnSuccess(circlerList);   
					}
				} else {
					Util.ShowToast(mContext, "No more Join circle available");
				}
			} else if (responsecode.equals("500")) {
				Util.ShowToast(mContext, "No more Join circle available");
				error();
			} else {
				Util.ShowToast(mContext, "Error occure.");
				error();
			}

		}
	}
	
	private void error(){
		if (circlelistparserinterface != null) {
			circlelistparserinterface.OnError();
		}
	}
	private ArrayList<CircleDetails> parseData(JSONObject obj){
		ArrayList<CircleDetails> circlerList = null;
		try {
			JSONArray circleJSONArray=obj.getJSONArray("circleDtoList");
			if (circleJSONArray!=null && circleJSONArray.length()>=1) {
				circlerList=new ArrayList<CircleDetails>();
				for (int i = 0; i < circleJSONArray.length(); i++) {
					JSONObject circlJSONObj = circleJSONArray.getJSONObject(i);
					CircleDetails mCircleDetails =new CircleDetails();
					
					mCircleDetails.setId(Util.getJsonValue(circlJSONObj, ID));
					mCircleDetails.setName(Util.getJsonValue(circlJSONObj, NAME));
					mCircleDetails.setSelected(Util.getJsonValue(circlJSONObj, SELECTED));
					mCircleDetails.setMembers(Util.getJsonValue(circlJSONObj, MEMBERS));
					mCircleDetails.setPosts(Util.getJsonValue(circlJSONObj, POSTS));
					mCircleDetails.setJoined(Util.getJsonValue(circlJSONObj, JOINED));
					mCircleDetails.setAdmin(Util.getJsonValue(circlJSONObj, ADMIN));
					mCircleDetails.setModerate(Util.getJsonValue(circlJSONObj, MODERATE));
					circlerList.add(mCircleDetails);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return circlerList;
	}
	
	public JSONObject getBody(int pageNo_,int pagecount_) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("pageNo", pageNo_+""); 
			jsObj.put("perPage", pagecount_+"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	public CircleListParserInterface circlelistparserinterface;

	public CircleListParserInterface getCirclelistparserinterface() {
		return circlelistparserinterface;
	}

	public void setCirclelistparserinterface(
			CircleListParserInterface circlelistparserinterface) {
		this.circlelistparserinterface = circlelistparserinterface;
	}

	public interface CircleListParserInterface {
		public void OnSuccess(ArrayList<CircleDetails> circlerList);

		public void OnError();
	}
}
