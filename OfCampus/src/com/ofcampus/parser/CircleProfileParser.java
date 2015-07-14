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
import com.ofcampus.model.CircleProfile;
import com.ofcampus.model.CircleUserDetails;
import com.ofcampus.model.JobDetails;

public class CircleProfileParser {

	private Context mContext;

	private String STATUS = "status";
	private String RESULTS = "results";
	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	private String SUCCESS = "success";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	private String POSTS = "posts";
	private String POSTID = "postId";
	private String SUBJECT = "subject";
	private String CONTENT = "content";
	private String POSTEDON = "postedOn";
	private String REPLYEMAIL = "replyEmail";
	private String REPLYPHONE = "replyPhone";
	private String REPLYWATSAPP = "replyWatsApp";
	private String SHAREEMAIL = "shareEmail";
	private String SHAREPHONE = "sharePhone";
	private String SHAREWATSAPP = "shareWatsApp";

	private String POSTTYPE = "postType";

	private String USERDTO = "userDto";
	private String ID = "id";
	private String NAME = "name";
	private String IMAGE = "image";

	private String CIRCLENAME = "name";
	private String CIRCLEDESC = "desc";
	private String CIRCLEMEMBERS = "members";
	private String CIRCLEJOINED = "joined";
	private String HIDE = "hide";

	private String USERDTOLIST = "userDtoList";
	private String USERID = "id";
	private String USERNAME = "name";
	private String USERIMAGE = "image";
	private String USEREMAIL = "emailId";
	private String MEMBERSINCE = "memberSince";
	private String USERCIRCLES = "circles";
	private String USERYEAROFGRAD = "yearOfGrad";

	private CircleProfile mCircleProfile;

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
				String[] responsedata = Util.POSTWithJSONAuth(Util.getCircleProfileUrl(), postData, authorization);
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
				if (circleprofileparserinterface != null) {
					circleprofileparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {

				if (mCircleProfile != null) {
					if (circleprofileparserinterface != null) {
						circleprofileparserinterface.OnSuccess(mCircleProfile);
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

	// {"circleId":22,"pageNo":0, "perPage":8, "appName":"ofCampus",
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
			JSONArray jsonarrayPOSTA = jsObj.getJSONArray(POSTS);
			JSONArray jsonarrayUserDto = jsObj.getJSONArray(USERDTOLIST);

			mCircleProfile = new CircleProfile();

			mCircleProfile.setCirclejoined(Util.getJsonValue(jsObj, CIRCLEJOINED));
			mCircleProfile.setCirclename(Util.getJsonValue(jsObj, CIRCLENAME));
			mCircleProfile.setCircledesc(Util.getJsonValue(jsObj, CIRCLEDESC));
			mCircleProfile.setCirclemembers(Util.getJsonValue(jsObj, CIRCLEMEMBERS));
			mCircleProfile.setHide(Util.getJsonValue(jsObj, HIDE));

			if (jsonarrayPOSTA != null && jsonarrayPOSTA.length() >= 1) {
				ArrayList<JobDetails> posts = new ArrayList<JobDetails>();

				for (int i = 0; i < jsonarrayPOSTA.length(); i++) {
					JSONObject postObj = jsonarrayPOSTA.getJSONObject(i);

					JobDetails mJobDetails = new JobDetails();
					mJobDetails.setPostid(Util.getJsonValue(postObj, POSTID));
					mJobDetails.setSubject(Util.getJsonValue(postObj, SUBJECT));
					mJobDetails.setContent(Util.getJsonValue(postObj, CONTENT));
					mJobDetails.setPostedon(Util.getJsonValue(postObj, POSTEDON));
					mJobDetails.setReplyEmail(Util.getJsonValue(postObj, REPLYEMAIL));
					mJobDetails.setReplyPhone(Util.getJsonValue(postObj, REPLYPHONE));
					mJobDetails.setReplyWatsApp(Util.getJsonValue(postObj, REPLYWATSAPP));
					mJobDetails.setPostType(Util.getJsonValue(postObj, POSTTYPE));

					JSONObject userJSONobj = postObj.getJSONObject(USERDTO);
					mJobDetails.setId(Util.getJsonValue(userJSONobj, ID));
					mJobDetails.setName(Util.getJsonValue(userJSONobj, NAME));
					mJobDetails.setImage(Util.getJsonValue(userJSONobj, IMAGE));

					posts.add(mJobDetails);
				}
				mCircleProfile.setArrayPost(posts);
			}
			if (jsonarrayUserDto != null && jsonarrayUserDto.length() >= 1) {
				ArrayList<CircleUserDetails> arrayCircleUserDetails = new ArrayList<CircleUserDetails>();
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

					arrayCircleUserDetails.add(mCircleUserDetails);
				}
				mCircleProfile.setArrayCircle(arrayCircleUserDetails);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public CircleProfileParserInterface circleprofileparserinterface;

	public CircleProfileParserInterface getCircleprofileparserinterface() {
		return circleprofileparserinterface;
	}

	public void setCircleprofileparserinterface(CircleProfileParserInterface circleprofileparserinterface) {
		this.circleprofileparserinterface = circleprofileparserinterface;
	}

	public interface CircleProfileParserInterface {
		public void OnSuccess(CircleProfile mCircleProfile);

		public void OnError();
	}

}
