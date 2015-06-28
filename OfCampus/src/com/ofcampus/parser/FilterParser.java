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
import com.ofcampus.model.DocDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;

public class FilterParser {
	private Context mContext;

	private String STATUS = "status";
	private String RESULTS = "results";

	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	/* Job List Key */
	private String JOBCREATERESPONSELIST = "jobCreateResponseList";
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

	private String POSTIMAGES = "attachmentDtoList";
	private String ATTACHMENTTYPE = "attachmentType";
	private String ATTACHEDKEYIMAGE = "image";
	private String ATTACHEDKEYDOC = "docs";
	private String ATTACHMENT_ID = "id";
	private String ATTACHMENT_URL = "url";
	private String ATTACHMENT_NAME = "name";

	private String POSTTYPE = "postType";

	// * No of comment reply share count *//
	private String NUMREPLIES = "numReplies";
	private String NUMSHARED = "numShared";
	private String NUMCOMMENT = "numComment";
	private String NUMHIDES = "numHides";
	private String NUMIMPORTANT = "numImportant";
	private String NUMSPAM = "numSpam";
	private String NUMLIKES = "numLikes";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	public void parse(Context context, JSONObject postData, String authorization, boolean isShowingPG) {
		this.mContext = context;
		FilterParserAsync mFilterParserAsync = new FilterParserAsync(mContext, postData, authorization, isShowingPG);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mFilterParserAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mFilterParserAsync.execute();
		}
	}

	private class FilterParserAsync extends AsyncTask<Void, Void, Void> {
		private Context context;
		private String authenticationJson;
		private JSONObject postData;
		private boolean isTimeOut = false;
		private ProgressDialog mDialog;
		private ArrayList<JobDetails> JobList;
		private String authToken;
		private boolean isShowingPG_;

		public FilterParserAsync(Context mContext, JSONObject postData_, String authToken_, boolean isShowingPG) {
			this.context = mContext;
			this.postData = postData_;
			this.authToken = authToken_;
			this.isShowingPG_ = isShowingPG;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isShowingPG_) {
				mDialog = new ProgressDialog(mContext);
				mDialog.setMessage("Loading...");
				mDialog.setCancelable(false);
				mDialog.show();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				String[] responsedata = Util.POSTWithJSONAuth(Util.getFilterUrl(), postData, authToken);
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
								JobList = parseJSONData(Obj);
							}
						}
					} else if (responsecode != null && (responsecode.equals("500") || responsecode.equals("401"))) {
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
				if (filterparserinterface != null) {
					filterparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {

				if (JobList != null && JobList.size() >= 1) {
					if (filterparserinterface != null) {
						filterparserinterface.OnSuccess(JobList);
					}
				} else {
					Util.ShowToast(mContext, "NO data availble.");
				}
			} else if (responsecode.equals("500") || responsecode.equals("401")) {
				Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, "Filter Error.");
			}
		}
	}

	public ArrayList<JobDetails> parseJSONData(JSONObject obj) {

		ArrayList<JobDetails> jobarray = new ArrayList<JobDetails>();

		try {
			JSONObject jsonobject = null;

			JSONArray jobjsonarray = obj.getJSONArray(JOBCREATERESPONSELIST);

			if (jobjsonarray != null && jobjsonarray.length() >= 1) {

				for (int i = 0; i < jobjsonarray.length(); i++) {
					JobDetails mJobDetails = new JobDetails();
					jsonobject = jobjsonarray.getJSONObject(i);

					mJobDetails.setPostid(Util.getJsonValue(jsonobject, POSTID));
					mJobDetails.setSubject(Util.getJsonValue(jsonobject, SUBJECT));
					mJobDetails.setIsb_jobs(Util.getJsonValue(jsonobject, ISB_JOBS));
					mJobDetails.setContent(Util.getJsonValue(jsonobject, CONTENT));
					mJobDetails.setPostedon(Util.getJsonValue(jsonobject, POSTEDON));
					mJobDetails.setImportant((Util.getJsonValue(jsonobject, IMPORTANT).equals("true")) ? 1 : 0);
					mJobDetails.setLike((Util.getJsonValue(jsonobject, LIKED).equals("true")) ? 1 : 0);

					JSONObject userJSONobj = jsonobject.getJSONObject(USERDTO);
					mJobDetails.setId(Util.getJsonValue(userJSONobj, ID));
					mJobDetails.setName(Util.getJsonValue(userJSONobj, NAME));
					mJobDetails.setImage(Util.getJsonValue(userJSONobj, IMAGE));
					mJobDetails.setPostType(Util.getJsonValue(jsonobject, POSTTYPE));

					JSONObject rplJSONObj = jsonobject.getJSONObject(REPLYDTO);

					mJobDetails.setReplyEmail(Util.getJsonValue(rplJSONObj, REPLYEMAIL));
					mJobDetails.setReplyPhone(Util.getJsonValue(rplJSONObj, REPLYPHONE));
					mJobDetails.setReplyWatsApp(Util.getJsonValue(rplJSONObj, REPLYWATSAPP));

					mJobDetails.setSharedto(Util.getJsonValue(jsonobject, SHAREDTO));

					mJobDetails.setNumreplies(Util.getJsonValue(jsonobject, NUMREPLIES));
					mJobDetails.setNumshared(Util.getJsonValue(jsonobject, NUMSHARED));
					mJobDetails.setNumcomment(Util.getJsonValue(jsonobject, NUMCOMMENT));
					mJobDetails.setNumhides(Util.getJsonValue(jsonobject, NUMHIDES));
					mJobDetails.setNumimportant(Util.getJsonValue(jsonobject, NUMIMPORTANT));
					mJobDetails.setNumspam(Util.getJsonValue(jsonobject, NUMSPAM));
					mJobDetails.setNumlikes(Util.getJsonValue(jsonobject, NUMLIKES));

					try {
						JSONArray attachmentJSONArray = jsonobject.getJSONArray(POSTIMAGES);
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
									mDetails.setDocName(Util.getJsonValue(attachmentObj, ATTACHMENT_NAME));
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

					jobarray.add(mJobDetails);
					mJobDetails = null;
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jobarray;

	}

	public JSONObject getBody(String circle, String locationFilter, String industryFilter, String rolesFilter, String salaryFilter, String experienceFilter) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
			jsObj.put("circleFilter", circle);
			jsObj.put("locationFilter", locationFilter);
			jsObj.put("industryFilter", industryFilter);
			jsObj.put("rolesFilter", rolesFilter);
			jsObj.put("salaryFilter", salaryFilter);
			jsObj.put("experienceFilter", experienceFilter);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	public FilterParserInterface filterparserinterface;

	public FilterParserInterface getFilterparserinterface() {
		return filterparserinterface;
	}

	public void setFilterparserinterface(FilterParserInterface filterparserinterface) {
		this.filterparserinterface = filterparserinterface;
	}

	public interface FilterParserInterface {
		public void OnSuccess(ArrayList<JobDetails> jobList);

		public void OnError();
	}
}
