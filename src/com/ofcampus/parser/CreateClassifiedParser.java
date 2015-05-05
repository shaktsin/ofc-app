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
import com.ofcampus.model.ImageDetails;
import com.ofcampus.model.JobDetails;

public class CreateClassifiedParser {

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
	private String SHAREDTO = "shareDto";

	private String POSTIMAGES = "attachmentDtoList";
	private String IMAGES_ID = "id";
	private String IMAGES_URL = "url";

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

				String[] responsedata = Util.POSTWithAuthJSONFile(Util.getCreateClassifiedeUrl(), obj_, auth, paths, "classified", docpdfPaths);
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
				if (createclassifiedinterface != null) {
					createclassifiedinterface.OnError();
				}
			} else if (responsecode.equals("200")) {
				if (mJobDetails != null) {
					if (createclassifiedinterface != null) {
						createclassifiedinterface.OnSuccess(mJobDetails);
						Util.ShowToast(mContext, "Classified Posted successfully.");
					}
				} else {
					Util.ShowToast(mContext, "Classified Post error.");
					if (createclassifiedinterface != null) {
						createclassifiedinterface.OnError();
					}
				}
			} else if (responsecode.equals("500")) {
				Util.ShowToast(mContext, responseDetails);
			} else {
				Util.ShowToast(mContext, "Classified Post error.");
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

			JSONObject userJSONobj = jsonobject.getJSONObject(USERDTO);
			mJobDetails.setId(Util.getJsonValue(userJSONobj, ID));
			mJobDetails.setName(Util.getJsonValue(userJSONobj, NAME));
			mJobDetails.setImage(Util.getJsonValue(userJSONobj, IMAGE));

			mJobDetails.setReplydto(Util.getJsonValue(jsonobject, REPLYDTO));
			mJobDetails.setSharedto(Util.getJsonValue(jsonobject, SHAREDTO));

			try {
				JSONArray imageJSONArray = jsonobject.getJSONArray(POSTIMAGES);
				if (imageJSONArray != null && imageJSONArray.length() >= 1) {
					ArrayList<ImageDetails> images = new ArrayList<ImageDetails>();
					for (int i = 0; i < imageJSONArray.length(); i++) {
						JSONObject imgJSONObject = imageJSONArray.getJSONObject(i);
						ImageDetails mImageDetails = new ImageDetails();
						mImageDetails.setImageID(Integer.parseInt(Util.getJsonValue(imgJSONObject, IMAGES_ID)));
						mImageDetails.setImageURL(Util.getJsonValue(imgJSONObject, IMAGES_URL));
						images.add(mImageDetails);
					}
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

	public CreateClassifiedInterface createclassifiedinterface;

	public CreateClassifiedInterface getCreateclassifiedinterface() {
		return createclassifiedinterface;
	}

	public void setCreateclassifiedinterface(CreateClassifiedInterface createclassifiedinterface) {
		this.createclassifiedinterface = createclassifiedinterface;
	}

	public interface CreateClassifiedInterface {
		public void OnSuccess(JobDetails mJobDetails);

		public void OnError();
	}

}
