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
import com.ofcampus.model.CircleUserDetails;

public class LoadMoreCircleUserParser {

	private Context mContext;

	private String STATUS = "status";
	private String RESULTS = "results";
	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	private String SUCCESS = "success";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	private String USERDTOLIST = "userDtoList";
	private String USERID = "id";
	private String USERNAME = "name";
	private String USERIMAGE = "image";
	private String USEREMAIL = "emailId";
	private String MEMBERSINCE = "memberSince";
	private String USERCIRCLES = "circles";
	private String USERYEAROFGRAD = "yearOfGrad";

	private ArrayList<CircleUserDetails> userList = null;

	public void parse(Context context, JSONObject postData, String authorization) {
		this.mContext = context;
		CircleProfileParserAsync mCircleProfileParserAsync = new CircleProfileParserAsync(mContext, postData, authorization);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mCircleProfileParserAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mCircleProfileParserAsync.execute();
		}
	}

	private class CircleProfileParserAsync extends AsyncTask<Void, Void, Void> {
		private String authenticationJson;
		private boolean isTimeOut = false;
		private JSONObject postData;
		private String authorization;
		private ProgressDialog mDialog;
		private String resSuccess = "";

		public CircleProfileParserAsync(Context mContext, JSONObject postData_, String authorization_) {
			this.postData = postData_;
			this.authorization = authorization_;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// mDialog = new ProgressDialog(mContext);
			// mDialog.setMessage("Loading...");
			// mDialog.setCancelable(false);
			// mDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				String[] responsedata = Util.POSTWithJSONAuth(Util.getCircleUsers(), postData, authorization);
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// if (mDialog != null && mDialog.isShowing()) {
			// mDialog.cancel();
			// mDialog = null;
			// }
			if (isTimeOut) {
				if (loadMoreCircleUserParserInterface != null) {
					loadMoreCircleUserParserInterface.OnError();
				}
			} else if (responsecode.equals("200")) {

				if (userList != null) {
					if (loadMoreCircleUserParserInterface != null) {
						loadMoreCircleUserParserInterface.OnSuccess(userList);
					}
				} else {
					// Util.ShowToast(mContext, "No Data available");
				}
			} else if (responsecode.equals("500")) {
				// Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, mContext.getResources().getString(R.string.serever_error_msg));
			}

		}
	}


	public JSONObject getBody(String circleId, String pageNo, String perPage) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("circleId", circleId);
			jsObj.put("pageNo", pageNo);
			jsObj.put("perPage", perPage);
			jsObj.put("appName", "ofCampus");
			jsObj.put("plateFormId", "0");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	private void parseData(JSONObject jsObj) {

		try {
			JSONArray jsonarrayUserDto = jsObj.getJSONArray(USERDTOLIST);
			if (jsonarrayUserDto != null && jsonarrayUserDto.length() >= 1) {
				userList = new ArrayList<CircleUserDetails>();
				for (int i = 0; i < jsonarrayUserDto.length(); i++) {
					JSONObject usereObj = jsonarrayUserDto.getJSONObject(i);

					CircleUserDetails mCircleUserDetails = new CircleUserDetails();

					mCircleUserDetails.setUserid(Util.getJsonValue(usereObj, USERID));
					mCircleUserDetails.setUsername(Util.getJsonValue(usereObj, USERNAME));
					mCircleUserDetails.setUserimage(Util.getJsonValue(usereObj, USERIMAGE));
					mCircleUserDetails.setEmailId(Util.getJsonValue(usereObj, USEREMAIL));
					mCircleUserDetails.setMemberSince(Util.getJsonValue(usereObj, MEMBERSINCE));
					mCircleUserDetails.setUsercircles(Util.getJsonValue(usereObj, USERCIRCLES));
					mCircleUserDetails.setUseryearofgrad(Util.getJsonValue(usereObj, USERYEAROFGRAD));

					userList.add(mCircleUserDetails);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public LoadMoreCircleUserParserInterface loadMoreCircleUserParserInterface;

	public LoadMoreCircleUserParserInterface getLoadMoreCircleUserParserInterface() {
		return loadMoreCircleUserParserInterface;
	}

	public void setLoadMoreCircleUserParserInterface(LoadMoreCircleUserParserInterface loadMoreCircleUserParserInterface) {
		this.loadMoreCircleUserParserInterface = loadMoreCircleUserParserInterface;
	}

	public interface LoadMoreCircleUserParserInterface {
		public void OnSuccess(ArrayList<CircleUserDetails> userList);

		public void OnError();
	}

}
