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

import com.google.android.gms.analytics.i;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.DocDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;

public class JobPostParser {

	private Context mContext;
	private String STATUS = "status";
	private String RESULTS = "results";
	// private String EXCEPTION="exception";
	// private String MESSAGES="messages";

	/* Job List Key */
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

	private String LOCATIONS = "locations";
	private String LOCATIONNAME = "name";
	private String INDUSTRYROLESDTOLIST = "industryRolesDtoList";
	private String INDUSTRYNAME = "industryName";
	private String ROLE = "name";

	private String EXPTO = "to";
	private String EXPFROM = "from";
	private String SALARYTO = "salaryTo";
	private String SALARYFROM = "salaryFrom";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	public void parse(Context context, JSONObject obj, String auth, ArrayList<String> paths, ArrayList<String> docpdfPaths) {
		this.mContext = context;
		jobPostAsyncAsync mjobPostAsyncAsync = new jobPostAsyncAsync(mContext, obj, auth, paths, docpdfPaths);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mjobPostAsyncAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mjobPostAsyncAsync.execute();
		}
	}

	private class jobPostAsyncAsync extends AsyncTask<Void, Void, Void> {
		private Context context;
		private String authenticationJson;
		private boolean isTimeOut = false;
		private JobDetails mJobDetails;
		private ProgressDialog mDialog;
		private JSONObject obj_ = null;
		private ArrayList<String> paths;
		private ArrayList<String> docpdfPaths;
		private String auth = "";

		public jobPostAsyncAsync(Context mContext, JSONObject obj, String auth_, ArrayList<String> paths_, ArrayList<String> docpdfPaths_) {
			this.context = mContext;
			this.obj_ = obj;
			this.auth = auth_;
			this.paths = paths_;
			this.docpdfPaths = docpdfPaths_;
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

				ArrayList<String> newFilePaths = new ArrayList<String>();
				if (paths != null && paths.size() >= 1) {
					for (String path : paths) {
						try {
							String returnpath = Util.compressImage(mContext, path);
							newFilePaths.add(returnpath);
						} catch (Exception e) {
							newFilePaths.add(path);
							e.printStackTrace();
						}
					}
				}

				String[] responsedata = Util.POSTWithAuthJSONFile(Util.getcreateJobUrl(), obj_, auth, newFilePaths, "jobs", docpdfPaths);
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205")) ? true : false;

				if (authenticationJson != null && !authenticationJson.equals("")) {
					JSONObject mObject = new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode != null && responsecode.equals("200")) {
						JSONObject Obj = mObject.getJSONObject(RESULTS);
						if (Obj != null) {
							mJobDetails = getParseData(Obj);
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
				if (jobpostparserinterface != null) {
					jobpostparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (mJobDetails != null) {
					if (jobpostparserinterface != null) {
						jobpostparserinterface.OnSuccess(mJobDetails);
						Util.ShowToast(mContext, "Job Posted successfully.");
					}
				} else {
					// Util.ShowToast(mContext, "Job Post error.");
					// if (jobpostparserinterface != null) {
					// jobpostparserinterface.OnError();
					// }
				}
			} else if (responsecode.equals("500")) {
				// Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, mContext.getResources().getString(R.string.serever_error_msg));
			}
		}
	}

	private JobDetails getParseData(JSONObject jsonobject) {

		JobDetails mJobDetails = null;

		try {
			mJobDetails = new JobDetails();
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

			try {
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
						String inds = Util.getJsonValue(industryJSONArray.getJSONObject(j), INDUSTRYNAME);
						industry = industry + ((industry.contains(inds)) ? "" : (inds + ","));
						role = role + Util.getJsonValue(industryJSONArray.getJSONObject(j), ROLE) + ",";
					}
				}

				location = (TextUtils.isEmpty(location)) ? "" : ("#" + location);
				industry = (TextUtils.isEmpty(industry)) ? "" : ("#" + industry);
				role = (TextUtils.isEmpty(role)) ? "" : ("#" + role);

				String expandsal = "";
				try {
					String expto = Util.getJsonValue(jsonobject, EXPTO) + "yr";
					String expfrom = Util.getJsonValue(jsonobject, EXPFROM) + "yr";
					String salto = Util.getJsonValue(jsonobject, SALARYTO) + "lc";
					String salfrm = Util.getJsonValue(jsonobject, SALARYFROM) + "lc";

					expandsal = (TextUtils.isEmpty(expto) && TextUtils.isEmpty(expfrom) && TextUtils.isEmpty(salto) && TextUtils.isEmpty(salfrm)) ? "" : ("#" + expto + "-" + expfrom + "," + salto
							+ "-" + salfrm);
				} catch (Exception e) {
					e.printStackTrace();
				}

				mJobDetails.setLocationandinds(Util.removeLastChr(location) + Util.removeLastChr(industry) + Util.removeLastChr(role) + expandsal);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mJobDetails;

	}

	public JobPostParserInterface jobpostparserinterface;

	public JobPostParserInterface getJobpostparserinterface() {
		return jobpostparserinterface;
	}

	public void setJobpostparserinterface(JobPostParserInterface jobpostparserinterface) {
		this.jobpostparserinterface = jobpostparserinterface;
	}

	public interface JobPostParserInterface {
		public void OnSuccess(JobDetails mJobDetails);

		public void OnError();
	}

}