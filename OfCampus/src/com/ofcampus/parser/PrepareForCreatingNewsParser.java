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

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.Circle;
import com.ofcampus.model.CityDetails;
import com.ofcampus.model.IndustryDetails;
import com.ofcampus.model.IndustryRoleDetails;
import com.ofcampus.model.PrepareListForJobCreating;

public class PrepareForCreatingNewsParser {

	private Context mContext;
	private String responsecode = "";
	private String responseDetails = "";

	private String STATUS = "status";
	private String RESULTS = "results";
	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	private String INDUSTRYDTOLIST = "industryDtoList";
	private String ID = "id";
	private String NAME = "name";
	private String SELECTED = "selected";

	private String INDUSTRYROLESDTOLIST = "industryRolesDtoList";
	private String INDUSTRYROLES_ID = "id";
	private String INDUSTRYROLES_NAME = "name";
	private String INDUSTRYROLES_INDUSTRYID = "industryId";
	private String INDUSTRYROLES_INDUSTRYNAME = "industryName";
	private String INDUSTRYROLES_SELECTED = "selected";

	/* City List Key */
	private String CITYDTOLIST = "cityDtoList";
	private String CITY_ID = "id";
	private String CITY_NAME = "name";
	private String CITY_SELECTED = "selected";

	private String REPLYEMAIL = "replyEmail";
	private String REPLYPHONE = "replyPhone";
	private String REPLYWATSAPP = "replyWatsApp";

	private String CIRCLEDTOLIST = "circleDtoList";
	private String CIRCLE_ID = "id";
	private String CIRCLE_NAME = "name";
	private String CIRCLE_SELECTED = "selected";

	public void parse(Context context, String auth) {
		this.mContext = context;
		Async mAsync = new Async(mContext, auth);
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
		private String auth;
		private PrepareListForJobCreating mPrepareListForJobCreating;
		private ProgressDialog mDialog;

		public Async(Context mContext, String auth_) {
			this.context = mContext;
			this.auth = auth_;
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
				String[] responsedata = Util.POSTWithJSONAuth(Util.getPrepareNewsUrl(), null, auth);
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
								mPrepareListForJobCreating = parseJSONData(Obj);
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
				if (newsprepareparserinterface != null) {
					newsprepareparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (mPrepareListForJobCreating != null) {
					if (newsprepareparserinterface != null) {
						newsprepareparserinterface.OnSuccess(mPrepareListForJobCreating);
					}
				} else {
					// Util.ShowToast(mContext, "Parse error.");
				}
			} else if (responsecode.equals("500")) {
				// Util.ShowToast(mContext, responseDetails);
				// if (newsprepareparserinterface != null) {
				// newsprepareparserinterface.OnError();
				// }
			} else {
				Util.ShowToast(mContext, mContext.getResources().getString(R.id.serever_error_msg));
				// if (newsprepareparserinterface != null) {
				// newsprepareparserinterface.OnError();
				// }
			}

		}
	}

	public JSONObject getBody(String authorization) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("Authorization", "0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	private PrepareListForJobCreating parseJSONData(JSONObject obj) {

		PrepareListForJobCreating mPrepareListForJobCreating = new PrepareListForJobCreating();

		try {

			JSONObject jsonobject = null;
			mPrepareListForJobCreating.setReplyEmail(Util.getJsonValue(obj, REPLYEMAIL));
			mPrepareListForJobCreating.setReplyPhone(Util.getJsonValue(obj, REPLYPHONE));
			mPrepareListForJobCreating.setReplyWatsApp(Util.getJsonValue(obj, REPLYWATSAPP));

			JSONArray industryjsonarray = obj.getJSONArray(INDUSTRYDTOLIST);
			JSONArray cityjsonarray = obj.getJSONArray(CITYDTOLIST);
			JSONArray circlejsonarray = obj.getJSONArray(CIRCLEDTOLIST);

			if (industryjsonarray != null && industryjsonarray.length() >= 1) {

				ArrayList<IndustryDetails> industrys = new ArrayList<IndustryDetails>();
				for (int i = 0; i < industryjsonarray.length(); i++) {
					IndustryDetails mIndustryDetails = new IndustryDetails();
					jsonobject = industryjsonarray.getJSONObject(i);
					mIndustryDetails.setIndustry_id(Util.getJsonValue(jsonobject, ID));
					mIndustryDetails.setIndustry_name(Util.getJsonValue(jsonobject, NAME));
					mIndustryDetails.setIndustry_selected(Util.getJsonValue(jsonobject, SELECTED));

					JSONArray industryRolejsonarray = jsonobject.getJSONArray(INDUSTRYROLESDTOLIST);
					if (industryRolejsonarray != null && industryRolejsonarray.length() >= 1) {

						ArrayList<IndustryRoleDetails> industryrolerray = new ArrayList<IndustryRoleDetails>();

						for (int j = 0; j < industryRolejsonarray.length(); j++) {
							IndustryRoleDetails mRoleDetails = new IndustryRoleDetails();
							JSONObject object = industryRolejsonarray.getJSONObject(j);
							mRoleDetails.setIndustryroles_id(Util.getJsonValue(object, INDUSTRYROLES_ID));
							mRoleDetails.setIndustryroles_name(Util.getJsonValue(object, INDUSTRYROLES_NAME));
							mRoleDetails.setIndustryroles_industryid(Util.getJsonValue(object, INDUSTRYROLES_INDUSTRYID));
							mRoleDetails.setIndustryroles_industryname(Util.getJsonValue(object, INDUSTRYROLES_INDUSTRYNAME));
							mRoleDetails.setIndustryroles_selected(Util.getJsonValue(object, INDUSTRYROLES_SELECTED));
							industryrolerray.add(mRoleDetails);
							mRoleDetails = null;
						}
						mIndustryDetails.setIndustryRoles(industryrolerray);
					}
					industrys.add(mIndustryDetails);
				}
				mPrepareListForJobCreating.setIndustrys(industrys);
			}

			if (cityjsonarray != null && cityjsonarray.length() >= 1) {

				ArrayList<CityDetails> cityarray = new ArrayList<CityDetails>();

				for (int i = 0; i < cityjsonarray.length(); i++) {
					CityDetails mCityDetails = new CityDetails();
					jsonobject = cityjsonarray.getJSONObject(i);

					mCityDetails.setCity_id(Util.getJsonValue(jsonobject, CITY_ID));
					mCityDetails.setCity_name(Util.getJsonValue(jsonobject, CITY_NAME));
					mCityDetails.setCity_selected(Util.getJsonValue(jsonobject, CITY_SELECTED));
					cityarray.add(mCityDetails);
					mCityDetails = null;
				}
				mPrepareListForJobCreating.setCitys(cityarray);
			}

			if (circlejsonarray != null && circlejsonarray.length() >= 1) {

				ArrayList<Circle> circleArray = new ArrayList<Circle>();

				for (int i = 0; i < circlejsonarray.length(); i++) {
					Circle mCircle = new Circle();
					jsonobject = circlejsonarray.getJSONObject(i);

					mCircle.setCircleid(Util.getJsonValue(jsonobject, CIRCLE_ID));
					mCircle.setCirclename(Util.getJsonValue(jsonobject, CIRCLE_NAME));
					mCircle.setCircleselected(Util.getJsonValue(jsonobject, CIRCLE_SELECTED));
					circleArray.add(mCircle);
					mCircle = null;
				}
				mPrepareListForJobCreating.setCirclelist(circleArray);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mPrepareListForJobCreating;
	}

	public NewsPrepareParserInterface newsprepareparserinterface;

	public NewsPrepareParserInterface getNewsprepareparserinterface() {
		return newsprepareparserinterface;
	}

	public void setNewsprepareparserinterface(NewsPrepareParserInterface newsprepareparserinterface) {
		this.newsprepareparserinterface = newsprepareparserinterface;
	}

	public interface NewsPrepareParserInterface {
		public void OnSuccess(PrepareListForJobCreating mPrepareListForJobCreating);

		public void OnError();
	}

}
