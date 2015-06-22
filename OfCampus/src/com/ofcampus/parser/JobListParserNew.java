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
import android.text.TextUtils;

import com.ofcampus.Util;
import com.ofcampus.Util.JobDataReturnFor;
import com.ofcampus.databasehelper.JOBListTable;
import com.ofcampus.model.CityDetails;
import com.ofcampus.model.DocDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.IndustryDetails;
import com.ofcampus.model.IndustryRoleDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.JobList;

public class JobListParserNew {
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

	private String POSTTYPE = "postType";

	/* City List Key */
	// private String CITYDTOLIST = "cityDtoList";
	// private String CITY_ID = "id";
	// private String CITY_NAME = "name";
	// private String CITY_SELECTED = "selected";

	/* industry Roles List Key */
	// private String industryRolesDtoList = "industryRolesDtoList";
	// private String INDUSTRYROLES_ID = "id";
	// private String INDUSTRYROLES_NAME = "name";
	// private String INDUSTRYROLES_INDUSTRYID = "industryId";
	// private String INDUSTRYROLES_INDUSTRYNAME = "industryName";
	// private String INDUSTRYROLES_SELECTED = "selected";

	/* industry List Key */
	// private String industryDtoList = "industryDtoList";
	// private String INDUSTRY_ID = "id";
	// private String INDUSTRY_NAME = "name";
	// private String INDUSTRY_SELECTED = "selected";

	// * No of comment reply share count *//
	private String NUMREPLIES = "numReplies";
	private String NUMSHARED = "numShared";
	private String NUMCOMMENT = "numComment";
	private String NUMHIDES = "numHides";
	private String NUMIMPORTANT = "numImportant";
	private String NUMSPAM = "numSpam";
	private String NUMLIKES = "numLikes";

	private String LOCATIONS = "locations";
	private String LOCATIONNAME = "name";
	private String INDUSTRYROLESDTOLIST = "industryRolesDtoList";
	private String INDUSTRYNAME = "industryName";
	private String ROLE = "name";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	public boolean isShowingPG_;

	public void parse(Context context, JSONObject postData_, String authToken_) {
		this.mContext = context;
		this.postData = postData_;
		this.authToken = authToken_;
		joblistAsync mjoblistAsync = new joblistAsync();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mjoblistAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mjoblistAsync.execute();
		}
	}

	private String authenticationJson;
	private JSONObject postData;
	private boolean isTimeOut = false;
	private ArrayList<JobDetails> jobList;
	private String authToken;

	private class joblistAsync extends AsyncTask<Void, Void, Void> {
		private ProgressDialog pgDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (isShowingPG_) {
				pgDialog = new ProgressDialog(mContext);
				pgDialog.setMessage("Loading...");
				pgDialog.setCancelable(false);
				pgDialog.show();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {

			doingBGWork();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (pgDialog != null && pgDialog.isShowing()) {
				pgDialog.cancel();
				pgDialog = null;
			}

			if (isTimeOut) {
				if (joblistparsernewinterface != null) {
					joblistparsernewinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (jobList != null) {
					if (joblistparsernewinterface != null) {
						joblistparsernewinterface.OnSuccess(jobList);
					}
				} else {
					Util.ShowToast(mContext, "No more Job");
				}
			} else if (responsecode.equals("500") || responsecode.equals("401")) {
				Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, "Joblist parse error.");
			}
		}
	}

	public ArrayList<JobDetails> bgSyncCalling(Context context, JSONObject postData_, String authToken_) {
		this.mContext = context;
		this.postData = postData_;
		this.authToken = authToken_;
		doingBGWork();
		return jobList;
	}

	public void doingBGWork() {
		try {
			String[] responsedata = Util.POSTWithJSONAuth(Util.getJobListUrl(), postData, authToken);
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
							jobList = parseJSONData(Obj);
							ArrayList<JobDetails> arrayJobInDB = JOBListTable.getInstance(mContext).fatchJobData(JobDataReturnFor.Normal);
							if (arrayJobInDB == null) {
								JOBListTable.getInstance(mContext).inserJobData(jobList);
							} else if (arrayJobInDB != null && arrayJobInDB.size() < 12) {
								int size = 12 - arrayJobInDB.size();
								if (jobList != null && jobList.size() >= 1) {
									JOBListTable.getInstance(mContext).inserJobData(jobList, size);
								}
							}
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
	}

	public ArrayList<JobDetails> parseJSONData(JSONObject obj) {

		JobList mJobList = new JobList();

		try {
			JSONObject jsonobject = null;

			JSONArray jobjsonarray = obj.getJSONArray(JOBCREATERESPONSELIST);
			// JSONArray cityjsonarray = obj.getJSONArray(CITYDTOLIST);
			// JSONArray industryrolejsonarray =
			// obj.getJSONArray(industryRolesDtoList);
			// JSONArray industryjsonarray = obj.getJSONArray(industryDtoList);

			if (jobjsonarray != null && jobjsonarray.length() >= 1) {

				ArrayList<JobDetails> jobarray = new ArrayList<JobDetails>();

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

					// *No of count */
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
									// mDetails.setDocsize(docsize);
									docList.add(mDetails);
								}
							}
							mJobDetails.setDoclist(docList);
							mJobDetails.setImages(images);
						}

						String location = "";
						String industry = "";
						String role = "";

						JSONArray locationJSONArray = jsonobject.getJSONArray(LOCATIONS);

						if (locationJSONArray != null && locationJSONArray.length() >= 1) {

							for (int j = 0; j < locationJSONArray.length(); j++) {
								location = location + Util.getJsonValue(locationJSONArray.getJSONObject(j), LOCATIONNAME) + ",";
							}
						}

						JSONArray industryJSONArray = jsonobject.getJSONArray(INDUSTRYROLESDTOLIST);

						if (industryJSONArray != null && industryJSONArray.length() >= 1) {

							for (int j = 0; j < industryJSONArray.length(); j++) {
								industry = industry + Util.getJsonValue(industryJSONArray.getJSONObject(j), INDUSTRYNAME) + ",";
								role = role + Util.getJsonValue(industryJSONArray.getJSONObject(j), ROLE) + ",";
							}
						}

						location = (TextUtils.isEmpty(location)) ? "" : ("#" + location);
						industry = (TextUtils.isEmpty(industry)) ? "" : ("#" + industry);
						role = (TextUtils.isEmpty(role)) ? "" : ("#" + role);

						mJobDetails.setLocationandinds(Util.removeLastChr(location) + Util.removeLastChr(industry) + Util.removeLastChr(role));

					} catch (Exception e) {
						e.printStackTrace();
					}

					jobarray.add(mJobDetails);
					mJobDetails = null;
				}
				mJobList.setJobs(jobarray);
			}

			// if (cityjsonarray != null && cityjsonarray.length() >= 1) {
			//
			// ArrayList<CityDetails> cityarray = new ArrayList<CityDetails>();
			//
			// for (int i = 0; i < cityjsonarray.length(); i++) {
			// CityDetails mCityDetails = new CityDetails();
			// jsonobject = cityjsonarray.getJSONObject(i);
			//
			// mCityDetails.setCity_id(Util.getJsonValue(jsonobject, CITY_ID));
			// mCityDetails.setCity_name(Util.getJsonValue(jsonobject,
			// CITY_NAME));
			// mCityDetails.setCity_selected(Util.getJsonValue(jsonobject,
			// CITY_SELECTED));
			// cityarray.add(mCityDetails);
			// mCityDetails = null;
			// }
			// mJobList.setCitys(cityarray);
			// }
			//
			// if (industryrolejsonarray != null &&
			// industryrolejsonarray.length() >= 1) {
			//
			// ArrayList<IndustryRoleDetails> industryrolerray = new
			// ArrayList<IndustryRoleDetails>();
			//
			// for (int i = 0; i < industryrolejsonarray.length(); i++) {
			// IndustryRoleDetails mRoleDetails = new IndustryRoleDetails();
			// jsonobject = industryrolejsonarray.getJSONObject(i);
			//
			// mRoleDetails.setIndustryroles_id(Util.getJsonValue(jsonobject,
			// INDUSTRYROLES_ID));
			// mRoleDetails.setIndustryroles_name(Util.getJsonValue(jsonobject,
			// INDUSTRYROLES_NAME));
			// mRoleDetails.setIndustryroles_industryid(Util.getJsonValue(jsonobject,
			// INDUSTRYROLES_INDUSTRYID));
			// mRoleDetails.setIndustryroles_industryname(Util.getJsonValue(jsonobject,
			// INDUSTRYROLES_INDUSTRYNAME));
			// mRoleDetails.setIndustryroles_selected(Util.getJsonValue(jsonobject,
			// INDUSTRYROLES_SELECTED));
			// industryrolerray.add(mRoleDetails);
			// mRoleDetails = null;
			// }
			// mJobList.setIndustryRoles(industryrolerray);
			//
			// }
			//
			// if (industryjsonarray != null && industryjsonarray.length() >= 1)
			// {
			//
			// ArrayList<IndustryDetails> industryarray = new
			// ArrayList<IndustryDetails>();
			//
			// for (int i = 0; i < industryjsonarray.length(); i++) {
			// IndustryDetails mIndustryDetails = new IndustryDetails();
			// jsonobject = industryjsonarray.getJSONObject(i);
			//
			// mIndustryDetails.setIndustry_id(Util.getJsonValue(jsonobject,
			// INDUSTRY_ID));
			// mIndustryDetails.setIndustry_name(Util.getJsonValue(jsonobject,
			// INDUSTRY_NAME));
			// mIndustryDetails.setIndustry_selected(Util.getJsonValue(jsonobject,
			// INDUSTRY_SELECTED));
			// industryarray.add(mIndustryDetails);
			// mIndustryDetails = null;
			//
			// }
			// mJobList.setIndustrys(industryarray);
			// }

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return mJobList.getJobs();

	}

	public JSONObject getBody(String postId, String operation) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
			jsObj.put("postId", postId);
			jsObj.put("operation", operation);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	public JSONObject getBody() {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	public JobListParserNewInterface joblistparsernewinterface;

	public JobListParserNewInterface getJoblistparsernewinterface() {
		return joblistparsernewinterface;
	}

	public void setJoblistparsernewinterface(JobListParserNewInterface joblistparsernewinterface) {
		this.joblistparsernewinterface = joblistparsernewinterface;
	}

	public interface JobListParserNewInterface {
		public void OnSuccess(ArrayList<JobDetails> jobList);

		public void OnError();
	}
}
