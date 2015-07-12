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
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.DocDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.JobPostedUserDetails;

public class LoadMoreUserCircleParser {

	private Context mContext;
	private String STATUS = "status";
	private String RESULTS = "results";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	private String CIRCLEDTOLIST = "circleDtoList";
	private String CIRCLEID = "id";
	private String CIRCLENAME = "name";
	private String CIRCLESELECTED = "selected";
	private String CIRCLEMEMBERS = "members";
	private String CIRCLEPOSTS = "posts";
	private String CIRCLEJOINED = "joined";
	private String CIRCLEADMIN = "admin";
	private String CIRCLEMODERATE = "moderate";
	private String CIRCLEREQUESTS = "requests";

	private ArrayList<CircleDetails> circleList = null;

	public void parse(Context context, JSONObject postData, String authorization, boolean b) {
		this.mContext = context;
		Async mAsync = new Async(mContext, postData, authorization, b);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mAsync.execute();
		}
	}

	private class Async extends AsyncTask<Void, Void, Void> {
		private String authenticationJson;
		private boolean isTimeOut = false;
		private JSONObject postData;
		private String authorization;
		private ProgressDialog mDialog;
		private String resSuccess = "";
		private boolean isPGShowing = false;

		public Async(Context mContext, JSONObject postData_, String authorization_, boolean b) {
			this.postData = postData_;
			this.authorization = authorization_;
			this.isPGShowing = b;
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

				String[] responsedata = Util.POSTWithJSONAuth(Util.getJoinListCircleUrl(), postData, authorization);

				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205")) ? true : false;

				if (authenticationJson != null && !authenticationJson.equals("")) {
					JSONObject mObject = new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode != null && responsecode.equals("200")) {
						JSONObject Obj = mObject.getJSONObject(RESULTS);

						if (Obj != null) {
							parseData(Obj);
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
				error();
			} else if (responsecode.equals("200")) {
				if (circleList != null) {
					if (loadMoreCircleParserInterface != null) {
						loadMoreCircleParserInterface.OnSuccess(circleList);
					}
				} else {
					loadMoreCircleParserInterface.NoData();
				}
			} else if (responsecode.equals("500")) {
				// Util.ShowToast(mContext, responseDetails);
				error();
			} else {
				Util.ShowToast(mContext, mContext.getResources().getString(R.string.serever_error_msg));
				error();
			}
		}
	}

	private void error() {
		if (loadMoreCircleParserInterface != null) {
			loadMoreCircleParserInterface.OnError();
		}
	}

	private void parseData(JSONObject jsObj) {

		try {
			JSONArray jsonarrayCIRCLE = jsObj.getJSONArray(CIRCLEDTOLIST);

			if (jsonarrayCIRCLE != null && jsonarrayCIRCLE.length() >= 1) {
				circleList = new ArrayList<CircleDetails>();
				for (int i = 0; i < jsonarrayCIRCLE.length(); i++) {
					JSONObject circleObj = jsonarrayCIRCLE.getJSONObject(i);

					CircleDetails mCircleDetails = new CircleDetails();

					mCircleDetails.setId(Util.getJsonValue(circleObj, CIRCLEID));
					mCircleDetails.setName(Util.getJsonValue(circleObj, CIRCLENAME));
					mCircleDetails.setSelected(Util.getJsonValue(circleObj, CIRCLESELECTED));
					mCircleDetails.setMembers(Util.getJsonValue(circleObj, CIRCLEMEMBERS));
					mCircleDetails.setPosts(Util.getJsonValue(circleObj, CIRCLEPOSTS));
					mCircleDetails.setJoined(Util.getJsonValue(circleObj, CIRCLEJOINED));
					mCircleDetails.setAdmin(Util.getJsonValue(circleObj, CIRCLEADMIN));
					mCircleDetails.setModerate(Util.getJsonValue(circleObj, CIRCLEMODERATE));
					circleList.add(mCircleDetails);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public LoadMoreCircleParserInterface loadMoreCircleParserInterface;

	public LoadMoreCircleParserInterface getLoadMoreCircleParserInterface() {
		return loadMoreCircleParserInterface;
	}

	public void setLoadMoreCircleParserInterface(LoadMoreCircleParserInterface loadMoreCircleParserInterface) {
		this.loadMoreCircleParserInterface = loadMoreCircleParserInterface;
	}

	public interface LoadMoreCircleParserInterface {
		public void OnSuccess(ArrayList<CircleDetails> circleList);

		public void NoData();

		public void OnError();
	}

	// {"circleId":22,"pageNo":1, "perPage":8, "appName":"ofCampus",
	// "plateFormId":0}
	public JSONObject getBody(String userId, String pageNo, String perPage) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("userId", userId);
			jsObj.put("appName", "ofCampus");
			jsObj.put("plateFormId", "0");
			jsObj.put("pageNo", pageNo);
			jsObj.put("perPage", perPage);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

}
