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

public class EditClassifiedParser {

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

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	public void parse(Context context, JSONObject obj, String auth, ArrayList<String> paths, ArrayList<String> docpdfPaths) {
		this.mContext = context;
		Async mAsync = new Async(mContext, obj, auth, paths, docpdfPaths);
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
		private JobDetails mJobDetails;
		private ProgressDialog mDialog;
		private JSONObject obj_ = null;
		private ArrayList<String> paths;
		private ArrayList<String> docpdfPaths;
		private String auth = "";

		public Async(Context mContext, JSONObject obj, String auth_, ArrayList<String> paths_, ArrayList<String> docpdfPaths_) {
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

				String[] responsedata = Util.POSTWithAuthJSONFile(Util.getEditClassifiedeUrl(), obj_, auth, paths, "classified", docpdfPaths);
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
				if (editclassifiedparserinterface != null) {
					editclassifiedparserinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (mJobDetails != null) {
					if (editclassifiedparserinterface != null) {
						editclassifiedparserinterface.OnSuccess(mJobDetails);
						Util.ShowToast(mContext, "Classified Modify successfully.");
					}
				} else {
//					Util.ShowToast(mContext, "Classified Modify error.");
//					if (editclassifiedparserinterface != null) {
//						editclassifiedparserinterface.OnError();
//					}
				}
			} else if (responsecode.equals("500")) {
//				Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, mContext.getResources().getString(R.string.serever_error_msg));
			}
		}
	}

	private JobDetails getParseData(JSONObject jsonobject) {

		JobDetails mJobDetails = null;

		try {
			mJobDetails=new JobDetails();
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

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mJobDetails;

	}

	public EditClassifiedParserInterface editclassifiedparserinterface;

	public EditClassifiedParserInterface getEditclassifiedparserinterface() {
		return editclassifiedparserinterface;
	}

	public void setEditclassifiedparserinterface(EditClassifiedParserInterface editclassifiedparserinterface) {
		this.editclassifiedparserinterface = editclassifiedparserinterface;
	}

	public interface EditClassifiedParserInterface {
		public void OnSuccess(JobDetails mJobDetails);

		public void OnError();
	}

}
