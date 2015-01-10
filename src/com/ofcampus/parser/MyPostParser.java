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
import com.ofcampus.model.JobDetails;

public class MyPostParser  {

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

	
	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	public void parse(Context context, JSONObject postData,String authorization) { 
		this.mContext = context;
		MyPostAsync mMyPostAsync = new MyPostAsync(mContext,postData,authorization);  
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mMyPostAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mMyPostAsync.execute(); 
		}
	}
	
	
	private class MyPostAsync extends AsyncTask<Void, Void, Void>{ 
		private Context context;
		private String authenticationJson;
		private JSONObject postData;
		private String authorization;
		private boolean isTimeOut=false;
		private ProgressDialog mDialog;
		private ArrayList<JobDetails> mJobList;
		
		
		
		public MyPostAsync(Context mContext, JSONObject postData_, String authorization_) {
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
				String[] responsedata =   Util.POST_JOB(Util.getMyPostJobUrl(), postData, authorization);
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
				if (mypostparserinterface != null) {
					mypostparserinterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (mJobList!=null) {
					if (mypostparserinterface!=null) {
						mypostparserinterface.OnSuccess(mJobList);
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
					
					mJobDetails.setReplydto(Util.getJsonValue(jsonobject, REPLYDTO));
					mJobDetails.setSharedto(Util.getJsonValue(jsonobject, SHAREDTO));

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
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}
	
	
	public MyPostParserInterface mypostparserinterface;

	public MyPostParserInterface getMypostparserinterface() {
		return mypostparserinterface;
	}

	public void setMypostparserinterface(
			MyPostParserInterface mypostparserinterface) {
		this.mypostparserinterface = mypostparserinterface;
	}

	public interface MyPostParserInterface {
		public void OnSuccess(ArrayList<JobDetails> mJobList);

		public void OnError();
	}

}
