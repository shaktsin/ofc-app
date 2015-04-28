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

import com.ofcampus.Util;
import com.ofcampus.model.CircleDetails;
import com.ofcampus.model.DocDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.JobPostedUserDetails;

public class GetJobPostedUserProfileParser {
	private Context mContext;
	private String STATUS = "status";
	private String RESULTS = "results";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	private String ACCOUNTNAME = "name";
	private String PROFILEIMAGELINK = "image";
	private String FIRSTNAME = "firstName";
	private String LASTNAME = "lastName";
	private String EMAIL = "email";
	private String GRADYEAR = "yearOfGrad";

	private String POSTS = "posts";
	private String POSTID = "postId";
	private String SUBJECT = "subject";
	private String ISB_JOBS = "ISB JOBS";
	private String CONTENT = "content";
	private String POSTEDON = "postedOn";
	private String USERDTO = "userDto";
	private String ID = "id";
	private String NAME = "name";
	private String IMAGE = "image";
	private String REPLYDTO = "replyDto";

	private String REPLYEMAIL = "replyEmail";
	private String REPLYPHONE = "replyPhone";
	private String REPLYWATSAPP = "replyWatsApp";

	private String SHAREDTO = "shareDto";
	private String IMPORTANT = "important";
	private String LIKED = "liked";

	// * No of comment reply share count *//
	private String NUMREPLIES = "numReplies";
	private String NUMSHARED = "numShared";
	private String NUMCOMMENT = "numComment";
	private String NUMHIDES = "numHides";
	private String NUMIMPORTANT = "numImportant";
	private String NUMSPAM = "numSpam";
	private String NUMLIKES = "numLikes";

	private String POSTIMAGES = "attachmentDtoList";
	private String ATTACHMENTTYPE = "attachmentType";
	private String ATTACHEDKEYIMAGE = "image";
	private String ATTACHEDKEYDOC = "docs";
	private String ATTACHMENT_ID = "id";
	private String ATTACHMENT_URL = "url";

	private String POSTTYPE = "postType";

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

	private JobPostedUserDetails mJobPostedUserDetails = null;

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

				String[] responsedata = Util.POSTWithJSONAuth(Util.getJOBPostedProfileUrl(), postData, authorization);

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
				if (getjobposteduserprofileparserinterface != null) {
					getjobposteduserprofileparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (mJobPostedUserDetails != null) {
					if (getjobposteduserprofileparserinterface != null) {
						getjobposteduserprofileparserinterface.OnSuccess(mJobPostedUserDetails);
					}
				} else {
					getjobposteduserprofileparserinterface.NoData();
				}
			} else if (responsecode.equals("500")) {
				Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, "Error occured");
			}
		}
	}

	private void parseData(JSONObject jsObj) {

		try {
			JSONArray jsonarrayPOSTA = jsObj.getJSONArray(POSTS);
			JSONArray jsonarrayCIRCLE = jsObj.getJSONArray(CIRCLEDTOLIST);

			mJobPostedUserDetails = new JobPostedUserDetails();

			mJobPostedUserDetails.setAccountname(Util.getJsonValue(jsObj, ACCOUNTNAME));
			mJobPostedUserDetails.setFirstname(Util.getJsonValue(jsObj, FIRSTNAME));
			mJobPostedUserDetails.setLastname(Util.getJsonValue(jsObj, LASTNAME));
			mJobPostedUserDetails.setProfileimagelink(Util.getJsonValue(jsObj, PROFILEIMAGELINK));
			mJobPostedUserDetails.setEmail(Util.getJsonValue(jsObj, EMAIL));
			mJobPostedUserDetails.setGradyear(Util.getJsonValue(jsObj, GRADYEAR));

			if (jsonarrayPOSTA != null && jsonarrayPOSTA.length() >= 1) {
				ArrayList<JobDetails> posts = new ArrayList<JobDetails>();

				for (int i = 0; i < jsonarrayPOSTA.length(); i++) {
					JSONObject postObj = jsonarrayPOSTA.getJSONObject(i);

					JobDetails mJobDetails = new JobDetails();
					mJobDetails.setPostid(Util.getJsonValue(postObj, POSTID));
					mJobDetails.setSubject(Util.getJsonValue(postObj, SUBJECT));
					mJobDetails.setIsb_jobs(Util.getJsonValue(postObj, ISB_JOBS));
					mJobDetails.setContent(Util.getJsonValue(postObj, CONTENT));
					mJobDetails.setPostedon(Util.getJsonValue(postObj, POSTEDON));
					mJobDetails.setImportant((Util.getJsonValue(postObj, IMPORTANT).equals("true")) ? 1 : 0);
					mJobDetails.setLike((Util.getJsonValue(postObj, LIKED).equals("true")) ? 1 : 0);

					JSONObject userJSONobj = postObj.getJSONObject(USERDTO);
					mJobDetails.setId(Util.getJsonValue(userJSONobj, ID));
					mJobDetails.setName(Util.getJsonValue(userJSONobj, NAME));
					mJobDetails.setImage(Util.getJsonValue(userJSONobj, IMAGE));
					mJobDetails.setPostType(Util.getJsonValue(postObj, POSTTYPE));

					JSONObject rplJSONObj = postObj.getJSONObject(REPLYDTO);

					mJobDetails.setReplyEmail(Util.getJsonValue(rplJSONObj, REPLYEMAIL));
					mJobDetails.setReplyPhone(Util.getJsonValue(rplJSONObj, REPLYPHONE));
					mJobDetails.setReplyWatsApp(Util.getJsonValue(rplJSONObj, REPLYWATSAPP));

					mJobDetails.setSharedto(Util.getJsonValue(postObj, SHAREDTO));

					// *No of count */
					mJobDetails.setNumreplies(Util.getJsonValue(postObj, NUMREPLIES));
					mJobDetails.setNumshared(Util.getJsonValue(postObj, NUMSHARED));
					mJobDetails.setNumcomment(Util.getJsonValue(postObj, NUMCOMMENT));
					mJobDetails.setNumhides(Util.getJsonValue(postObj, NUMHIDES));
					mJobDetails.setNumimportant(Util.getJsonValue(postObj, NUMIMPORTANT));
					mJobDetails.setNumspam(Util.getJsonValue(postObj, NUMSPAM));
					mJobDetails.setNumlikes(Util.getJsonValue(postObj, NUMLIKES));

					try {
						JSONArray attachmentJSONArray = postObj.getJSONArray(POSTIMAGES);
						if (attachmentJSONArray != null && attachmentJSONArray.length() >= 1) {
							ArrayList<ImageDetails> images = new ArrayList<ImageDetails>();
							ArrayList<DocDetails> docList = new ArrayList<DocDetails>();

							for (int i1 = 0; i1 < attachmentJSONArray.length(); i1++) {
								JSONObject attachmentObj = attachmentJSONArray.getJSONObject(i1);
								if (Util.getJsonValue(attachmentObj, ATTACHMENTTYPE).equals(ATTACHEDKEYIMAGE)) {
									ImageDetails mImageDetails = new ImageDetails();
									mImageDetails.setImageID(Integer.parseInt(Util.getJsonValue(attachmentObj, ATTACHMENT_ID)));
									mImageDetails.setImageURL(Util.getJsonValue(attachmentObj, ATTACHMENT_URL));
									images.add(mImageDetails);
								} else if (Util.getJsonValue(attachmentObj, ATTACHMENTTYPE).equals(ATTACHEDKEYDOC)) {
									DocDetails mDetails = new DocDetails();
									mDetails.setDocID(Integer.parseInt(Util.getJsonValue(attachmentObj, ATTACHMENT_ID)));
									mDetails.setDocURL(Util.getJsonValue(attachmentObj, ATTACHMENT_URL));
									// mDetails.setDocsize(docsize);
									docList.add(mDetails);
								}
							}
							mJobDetails.setDoclist(docList);
							mJobDetails.setImages(images);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					posts.add(mJobDetails);
				}
				mJobPostedUserDetails.setArrayPost(posts);
			}
			if (jsonarrayCIRCLE != null && jsonarrayCIRCLE.length() >= 1) {
				ArrayList<CircleDetails> circles = new ArrayList<CircleDetails>();
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
					circles.add(mCircleDetails);
				}
				mJobPostedUserDetails.setArrayCircle(circles);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public GetJobPostedUserProfileParserInterface getjobposteduserprofileparserinterface;

	public GetJobPostedUserProfileParserInterface getGetjobposteduserprofileparserinterface() {
		return getjobposteduserprofileparserinterface;
	}

	public void setGetjobposteduserprofileparserinterface(GetJobPostedUserProfileParserInterface getjobposteduserprofileparserinterface) {
		this.getjobposteduserprofileparserinterface = getjobposteduserprofileparserinterface;
	}

	public interface GetJobPostedUserProfileParserInterface {
		public void OnSuccess(JobPostedUserDetails mJobPostedUserDetails);

		public void NoData();

		public void OnError();
	}

	// {"userId":11,"pageNo":0, "perPage":8, "appName":"ofCampus",
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
