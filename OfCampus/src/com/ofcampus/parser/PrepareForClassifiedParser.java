/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.CustomSpinnerDataSets;
import com.ofcampus.model.PrepareListForClassified;

public class PrepareForClassifiedParser {

	private Context mContext;
	private String responsecode = "";
	private String responseDetails = "";

	private String STATUS = "status";
	private String RESULTS = "results";
	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	/* City List Key */
	private String CITYDTOLIST = "cityDtoList";
	private String CITY_ID = "id";
	private String CITY_NAME = "name";
	private String CITY_SELECTED = "selected";

	/* PRIMARYCATEGORY */
	private String PRIMARYCATEGORYLIST = "primaryCategoryDtoList";
	private String PRIMARYCATEGORY_ID = "id";
	private String PRIMARYCATEGORY_NAME = "name";
	private String PRIMARYCATEGORY_SELECTED = "selected";

	/* PRIMARYCATEGORY */
	private String SECONDARYCATLIST = "secondaryCategoryDtoList";
	private String SECONDARYCAT_ID = "id";
	private String SECONDARYCAT_NAME = "name";
	private String SECONDARYCAT_SELECTED = "selected";

	private String CIRCLEDTOLIST = "circleDtoList";
	private String CIRCLE_ID = "id";
	private String CIRCLE_NAME = "name";
	private String CIRCLE_SELECTED = "selected";

	public void parse(Context context, String authorization) {
		this.mContext = context;
		List<NameValuePair> postData = getparamBody(authorization);

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
		private List<NameValuePair> postData;
		private PrepareListForClassified mPrepareListForClassified;
		private ProgressDialog mDialog;

		public Async(Context mContext, List<NameValuePair> postData_) {
			this.context = mContext;
			this.postData = postData_;
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
				String[] responsedata = Util.GetRequest(postData, Util.getPreparClassifiedeUrl());
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
								mPrepareListForClassified = parseJSONData(Obj);
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
				Util.ShowToast(mContext, "Server error.");
				if (prepareparserinterface != null) {
					prepareparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (mPrepareListForClassified != null) {
					if (prepareparserinterface != null) {
						prepareparserinterface.OnSuccess(mPrepareListForClassified);
					}
				} else {
//					Util.ShowToast(mContext, "Parse error.");
				}
			} else if (responsecode.equals("500")) {
//				Util.ShowToast(mContext, responseDetails);
//				if (prepareparserinterface != null) {
//					prepareparserinterface.OnError();
//				}
			} else {
//				Util.ShowToast(mContext, "Parse error.");
//				if (prepareparserinterface != null) {
//					prepareparserinterface.OnError();
//				}
				Util.ShowToast(mContext, mContext.getResources().getString(R.id.serever_error_msg));
			}

		}
	}

	public static List<NameValuePair> getparamBody(String authorization) {
		List<NameValuePair> pairsofEducation = new ArrayList<NameValuePair>();
		pairsofEducation.add(new BasicNameValuePair("Authorization", authorization));
		return pairsofEducation;
	}

	private PrepareListForClassified parseJSONData(JSONObject obj) {

		PrepareListForClassified mPrepareListForClassified = new PrepareListForClassified();

		try {
			JSONObject jsonobject = null;
			JSONArray primarycategoryjsonarray = obj.getJSONArray(PRIMARYCATEGORYLIST);

			JSONArray cityjsonarray = obj.getJSONArray(CITYDTOLIST);
			JSONArray circlejsonarray = obj.getJSONArray(CIRCLEDTOLIST);

			if (primarycategoryjsonarray != null && primarycategoryjsonarray.length() >= 1) {

				ArrayList<CustomSpinnerDataSets> primaryarray = new ArrayList<CustomSpinnerDataSets>();
				for (int i = 0; i < primarycategoryjsonarray.length(); i++) {

					jsonobject = primarycategoryjsonarray.getJSONObject(i);
					CustomSpinnerDataSets mCustomSpinnerDataSets = new CustomSpinnerDataSets();

					mCustomSpinnerDataSets.setId(Util.getJsonValue(jsonobject, PRIMARYCATEGORY_ID));
					mCustomSpinnerDataSets.setTitle(Util.getJsonValue(jsonobject, PRIMARYCATEGORY_NAME));
					mCustomSpinnerDataSets.setIsSelected(0);

					try {
						JSONArray secondarycatjsonarray = jsonobject.getJSONArray(SECONDARYCATLIST);
						if (secondarycatjsonarray != null && secondarycatjsonarray.length() >= 1) {

							ArrayList<CustomSpinnerDataSets> seccatarray = new ArrayList<CustomSpinnerDataSets>();
							for (int j = 0; j < secondarycatjsonarray.length(); j++) {

								jsonobject = secondarycatjsonarray.getJSONObject(j);
								CustomSpinnerDataSets secondarycat = new CustomSpinnerDataSets();

								secondarycat.setId(Util.getJsonValue(jsonobject, SECONDARYCAT_ID));
								secondarycat.setTitle(Util.getJsonValue(jsonobject, SECONDARYCAT_NAME));
								secondarycat.setIsSelected(0);
								seccatarray.add(secondarycat);
							}
							mCustomSpinnerDataSets.setList(seccatarray);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					primaryarray.add(mCustomSpinnerDataSets);
				}
				mPrepareListForClassified.setPrimarycatlist(primaryarray);
			}

			if (cityjsonarray != null && cityjsonarray.length() >= 1) {

				ArrayList<CustomSpinnerDataSets> cityarray = new ArrayList<CustomSpinnerDataSets>();

				for (int i = 0; i < cityjsonarray.length(); i++) {
					jsonobject = cityjsonarray.getJSONObject(i);
					CustomSpinnerDataSets mCustomSpinnerDataSets = new CustomSpinnerDataSets();

					mCustomSpinnerDataSets.setId(Util.getJsonValue(jsonobject, CITY_ID));
					mCustomSpinnerDataSets.setTitle(Util.getJsonValue(jsonobject, CITY_NAME));
					mCustomSpinnerDataSets.setIsSelected(0);
					cityarray.add(mCustomSpinnerDataSets);
				}
				mPrepareListForClassified.setCitys(cityarray);
			}

			if (circlejsonarray != null && circlejsonarray.length() >= 1) {

				ArrayList<CustomSpinnerDataSets> circleArray = new ArrayList<CustomSpinnerDataSets>();

				for (int i = 0; i < circlejsonarray.length(); i++) {
					jsonobject = circlejsonarray.getJSONObject(i);
					CustomSpinnerDataSets mCustomSpinnerDataSets = new CustomSpinnerDataSets();

					mCustomSpinnerDataSets.setId(Util.getJsonValue(jsonobject, CIRCLE_ID));
					mCustomSpinnerDataSets.setTitle(Util.getJsonValue(jsonobject, CIRCLE_NAME));
					mCustomSpinnerDataSets.setIsSelected(0);
					circleArray.add(mCustomSpinnerDataSets);
				}
				mPrepareListForClassified.setCirclelist(circleArray);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mPrepareListForClassified;
	}

	public PrepareParserInterface prepareparserinterface;

	public PrepareParserInterface getPrepareparserinterface() {
		return prepareparserinterface;
	}

	public void setPrepareparserinterface(PrepareParserInterface prepareparserinterface) {
		this.prepareparserinterface = prepareparserinterface;
	}

	public interface PrepareParserInterface {
		public void OnSuccess(PrepareListForClassified mPrepareListForClassified);

		public void OnError();
	}

}
