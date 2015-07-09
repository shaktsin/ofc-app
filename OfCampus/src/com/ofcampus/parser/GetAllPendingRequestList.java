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
import com.ofcampus.model.CircleUserDetails;

public class GetAllPendingRequestList {

	private Context mContext;

	private String STATUS = "status";
	private String RESULTS = "results";
	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	private String SUCCESS = "success";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	// private String POSTS="posts";
	// private String POSTID="postId";
	// private String SUBJECT="subject";
	// private String CONTENT="content";
	// private String POSTEDON="postedOn";
	// private String REPLYEMAIL="replyEmail";
	// private String REPLYPHONE="replyPhone";
	// private String REPLYWATSAPP="replyWatsApp";
	// private String SHAREEMAIL="shareEmail";
	// private String SHAREPHONE="sharePhone";
	// private String SHAREWATSAPP="shareWatsApp";
	//
	// private String USERDTO="userDto";
	// private String ID="id";
	// private String NAME="name";
	// private String IMAGE="image";
	//
	// private String CIRCLENAME="name";
	// private String CIRCLEMEMBERS="members";
	// private String CIRCLEJOINED="joined";

	private String USERDTOLIST = "userDtoList";
	private String USERID = "id";
	private String USERNAME = "name";
	private String USERIMAGE = "image";
	private String USERCIRCLES = "circles";
	private String USERYEAROFGRAD = "yearOfGrad";

	private ArrayList<CircleUserDetails> arrayCircle;

	public void parse(Context context, JSONObject postData, String authorization) {
		this.mContext = context;
		PendingRequestParserAsync mPendingRequestParserAsync = new PendingRequestParserAsync(mContext, postData, authorization);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mPendingRequestParserAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mPendingRequestParserAsync.execute();
		}
	}

	private class PendingRequestParserAsync extends AsyncTask<Void, Void, Void> {
		private String authenticationJson;
		private boolean isTimeOut = false;
		private JSONObject postData;
		private String authorization;
		private ProgressDialog mDialog;
		private String resSuccess = "";

		public PendingRequestParserAsync(Context mContext, JSONObject postData_, String authorization_) {
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
				String[] responsedata = Util.POSTWithJSONAuth(Util.getAllPendingRequestUrl(), postData, authorization);
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
				if (pendingrequestparserinterface != null) {
					pendingrequestparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {

				if (arrayCircle != null) {
					if (pendingrequestparserinterface != null) {
						pendingrequestparserinterface.OnSuccess(arrayCircle);
					}
				} else {
//					Util.ShowToast(mContext, "No Data available");
				}
			} else if (responsecode.equals("500")) {
//				Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, mContext.getResources().getString(R.string.serever_error_msg));
			}

		}
	}

	// {"circleId":21,"pageNo":0, "perPage":8, "appName":"ofCampus",
	// "plateFormId":0}
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
				arrayCircle = new ArrayList<CircleUserDetails>();
				for (int i = 0; i < jsonarrayUserDto.length(); i++) {
					JSONObject usereObj = jsonarrayUserDto.getJSONObject(i);

					CircleUserDetails mCircleUserDetails = new CircleUserDetails();

					mCircleUserDetails.setUserid(Util.getJsonValue(usereObj, USERID));
					mCircleUserDetails.setUsername(Util.getJsonValue(usereObj, USERNAME));
					mCircleUserDetails.setUserimage(Util.getJsonValue(usereObj, USERIMAGE));
					mCircleUserDetails.setUsercircles(Util.getJsonValue(usereObj, USERCIRCLES));
					mCircleUserDetails.setUseryearofgrad(Util.getJsonValue(usereObj, USERYEAROFGRAD));

					arrayCircle.add(mCircleUserDetails);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public PendingRequestParserInterface pendingrequestparserinterface;

	public PendingRequestParserInterface getPendingrequestparserinterface() {
		return pendingrequestparserinterface;
	}

	public void setPendingrequestparserinterface(PendingRequestParserInterface pendingrequestparserinterface) {
		this.pendingrequestparserinterface = pendingrequestparserinterface;
	}

	public interface PendingRequestParserInterface {
		public void OnSuccess(ArrayList<CircleUserDetails> arrayCircle);

		public void OnError();
	}

}
