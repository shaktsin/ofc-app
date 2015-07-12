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
import com.ofcampus.model.DocDetails;
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;

public class LoadMoreUserPostParser {

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
	private String ATTACHMENT_NAME = "name";

	private String POSTTYPE = "postType";

	private ArrayList<JobDetails> posts = null;

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

				String[] responsedata = Util.POSTWithJSONAuth(Util.getUserPost(), postData, authorization);

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
				if (posts != null) {
					if (loadMorePostParserInterface != null) {
						loadMorePostParserInterface.OnSuccess(posts);
					}
				} else {
					loadMorePostParserInterface.NoData();
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
		if (loadMorePostParserInterface != null) {
			loadMorePostParserInterface.OnError();
		}
	}

	private void parseData(JSONObject jsObj) {

		try {
			JSONArray jsonarrayPOSTA = jsObj.getJSONArray(POSTS);

			if (jsonarrayPOSTA != null && jsonarrayPOSTA.length() >= 1) {
				posts = new ArrayList<JobDetails>();

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

					posts.add(mJobDetails);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public LoadMorePostParserInterface loadMorePostParserInterface;

	public LoadMorePostParserInterface getLoadMorePostParserInterface() {
		return loadMorePostParserInterface;
	}

	public void setLoadMorePostParserInterface(LoadMorePostParserInterface loadMorePostParserInterface) {
		this.loadMorePostParserInterface = loadMorePostParserInterface;
	}

	public interface LoadMorePostParserInterface {
		public void OnSuccess(ArrayList<JobDetails> posts);

		public void NoData();

		public void OnError();
	}

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
