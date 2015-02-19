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

public class ImportantMailParser {

	private Context mContext;
	
	private String STATUS="status";
	private String RESULTS="results";
	
	
	private String EXCEPTION="exception";
	private String MESSAGES="messages";

	/*Job List Key*/
	private String POSTS="posts";
	private String POSTID="postId";
	private String SUBJECT="subject";
	private String ISB_JOBS="ISB JOBS";
	private String CONTENT="content";
	private String POSTEDON="postedOn";
	private String USERDTO="userDto";
	private String ID="id";
	private String NAME="name";
	private String IMAGE="image";
	private String REPLYDTO="replyDto";
	private String SHAREDTO="shareDto";

	private String REPLYEMAIL="replyEmail";
	private String REPLYPHONE="replyPhone";
	private String REPLYWATSAPP="replyWatsApp";
	
	private String POSTIMAGES="attachmentDtoList";
	private String IMAGES_ID="id";
	private String IMAGES_URL="url";
	
	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	public void parse(Context context, JSONObject postData,String authorization) { 
		this.mContext = context;
		ImportantMailAsync mImportantMailAsync = new ImportantMailAsync(mContext,postData,authorization);   
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mImportantMailAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mImportantMailAsync.execute();  
		}
	}
	
	
	private class ImportantMailAsync extends AsyncTask<Void, Void, Void>{ 
		private Context context;
		private String authenticationJson;
		private JSONObject postData;
		private String authorization;
		private boolean isTimeOut=false;
		private ProgressDialog mDialog;
		private ArrayList<JobDetails> mJobList;
		
		
		
		public ImportantMailAsync(Context mContext, JSONObject postData_, String authorization_) {
			this.context = mContext;
			this.postData = postData_;
			this.authorization = authorization_;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mDialog=new ProgressDialog(mContext);
			mDialog.setMessage("Loading...");
			mDialog.setCancelable(false);
			mDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				String[] responsedata =   Util.POST_JOB(Util.getImportantmailUrl(), postData, authorization);
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205"))?true:false;
				
				if (authenticationJson!=null && !authenticationJson.equals("")) {
					JSONObject mObject=new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode!=null && responsecode.equals("200")) {
						JSONObject Obj = mObject.getJSONObject(RESULTS); 
						if (Obj!=null && !Obj.equals("")) {
							String expt= Util.getJsonValue(Obj, EXCEPTION);
							if (expt.equals("false")) {
								mJobList = parseJSONData(Obj);
							}
						}
					}else if(responsecode!=null && responsecode.equals("500")){
						JSONObject userObj = mObject.getJSONObject(RESULTS);
						if (userObj!=null) {
							responseDetails=userObj.getJSONArray("messages").get(0).toString();
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
			
			if (mDialog!=null && mDialog.isShowing()) {
				mDialog.cancel();
				mDialog=null;
			}
			
			if (isTimeOut) {
				if (importantmailparserinterface != null) {
					importantmailparserinterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (mJobList!=null) {
					if (importantmailparserinterface!=null) {
						importantmailparserinterface.OnSuccess(mJobList); 
					}
				}else {
					Util.ShowToast(mContext, "Joblist parse error.");
				}
			}else if (responsecode.equals("500")){
				Util.ShowToast(mContext, responseDetails);
			}else {
				Util.ShowToast(mContext, "Joblist parse error.");
			}
		}
	}
	
	

	
	
	
	private ArrayList<JobDetails> parseJSONData(JSONObject obj){

		ArrayList<JobDetails> jobarray=null;
		try {
			JSONObject jsonobject=null;
			JSONArray jobjsonarray=obj.getJSONArray(POSTS) ;
			
			if (jobjsonarray != null && jobjsonarray.length() >= 1) {
				
				jobarray = new ArrayList<JobDetails>();
				
				for (int i = 0; i < jobjsonarray.length(); i++) {
					JobDetails mJobDetails=new JobDetails();
					jsonobject = jobjsonarray.getJSONObject(i);
					
					mJobDetails.setPostid(Util.getJsonValue(jsonobject, POSTID)); 
					mJobDetails.setSubject(Util.getJsonValue(jsonobject, SUBJECT));
					mJobDetails.setIsb_jobs(Util.getJsonValue(jsonobject, ISB_JOBS));
					mJobDetails.setContent(Util.getJsonValue(jsonobject, CONTENT));
					mJobDetails.setPostedon(Util.getJsonValue(jsonobject, POSTEDON));
					
					JSONObject userJSONobj=jsonobject.getJSONObject(USERDTO);
					mJobDetails.setId(Util.getJsonValue(userJSONobj, ID));
					mJobDetails.setName(Util.getJsonValue(userJSONobj, NAME));
					mJobDetails.setImage(Util.getJsonValue(userJSONobj, IMAGE));
					
					JSONObject rplJSONObj=jsonobject.getJSONObject(REPLYDTO);
					mJobDetails.setSharedto(Util.getJsonValue(jsonobject, SHAREDTO));
					
					mJobDetails.setReplyEmail(Util.getJsonValue(rplJSONObj, REPLYEMAIL));
					mJobDetails.setReplyPhone(Util.getJsonValue(rplJSONObj, REPLYPHONE));
					mJobDetails.setReplyWatsApp(Util.getJsonValue(rplJSONObj, REPLYWATSAPP)); 
					
					try {
						JSONArray imageJSONArray = jsonobject.getJSONArray(POSTIMAGES);
						if (imageJSONArray!=null && imageJSONArray.length()>=1) {
							ArrayList<ImageDetails> images=new ArrayList<ImageDetails>();
							for (int i1 = 0; i1 < imageJSONArray.length(); i1++) {
								JSONObject imgJSONObject = imageJSONArray.getJSONObject(i1);
								ImageDetails mImageDetails=new ImageDetails();
								mImageDetails.setImageID(Integer.parseInt(Util.getJsonValue(imgJSONObject, IMAGES_ID)));
								mImageDetails.setImageURL(Util.getJsonValue(imgJSONObject, IMAGES_URL));
								images.add(mImageDetails);
							}
							mJobDetails.setImages(images);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					jobarray.add(mJobDetails);
					mJobDetails=null;
				}
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jobarray;
		
	}
	
	
	public JSONObject getBody() {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
			jsObj.put("actionId", "2");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}
	
	
	public ImportantMailParserInterface importantmailparserinterface;

	public ImportantMailParserInterface getImportantmailparserinterface() {
		return importantmailparserinterface;
	}

	public void setImportantmailparserinterface(
			ImportantMailParserInterface importantmailparserinterface) {
		this.importantmailparserinterface = importantmailparserinterface;
	}

	public interface ImportantMailParserInterface {
		public void OnSuccess(ArrayList<JobDetails> mJobList);

		public void OnError();
	}

}

