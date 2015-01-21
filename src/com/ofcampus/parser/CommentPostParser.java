package com.ofcampus.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ofcampus.Util;
import com.ofcampus.model.JobDetails;

public class CommentPostParser {


	private Context mContext;
	private String STATUS="status";
	private String RESULTS="results";
//	private String EXCEPTION="exception";
//	private String MESSAGES="messages";


	/*Job List Key*/
	private String POSTID="postId";
	private String CONTENT="content";
	private String COMMENTEDON="commentedOn";
	private String USERDTO="userDto";
	private String ID="id";
	private String NAME="name";
	private String IMAGE="image";
	

    	
    	
    	
	
	/**CommentList Key*/
	private String COMMENTLISTRESPONSE="commentListResponse";

	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	
		
	public void parse(Context context,JSONObject body, String authtoken) {  
		this.mContext = context;
		
		JobDetailsAsyncAsync mJobDetailsAsyncAsync = new JobDetailsAsyncAsync(mContext,body,authtoken); 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mJobDetailsAsyncAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mJobDetailsAsyncAsync.execute();  
		}
	}
	
	
	private class JobDetailsAsyncAsync extends AsyncTask<Void, Void, Void>{
		private Context context;
		private String authenticationJson;
		private boolean isTimeOut=false;
		private JobDetails mJobDetails;
		private ProgressDialog mDialog;
		private JSONObject postData;
		private String authtoken="";
		
		public JobDetailsAsyncAsync(Context mContext, JSONObject body, String authtoken_) { 
			this.context = mContext;
			this.postData = body; 
			this.authtoken=authtoken_; 
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
				
				String[] responsedata =	 Util.POST_JOB(Util.getCommentPostUrl(), postData, authtoken);
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205"))?true:false;
				
				if (authenticationJson!=null && !authenticationJson.equals("")) {
					JSONObject mObject=new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode!=null && responsecode.equals("200")) {
						JSONObject Obj = mObject.getJSONObject(RESULTS);
						if (Obj!=null) {
							mJobDetails = getParseData(Obj);
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
				if (commentpostparserinterface != null) { 
					commentpostparserinterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (mJobDetails!=null) {
					if (commentpostparserinterface!=null) {
						commentpostparserinterface.OnSuccess(mJobDetails);  
					}
				}else {
					Util.ShowToast(mContext, "Comment post parser error.");
					if (commentpostparserinterface != null) {
						commentpostparserinterface.OnError(); 
					}
				}
			}else if (responsecode.equals("500")){
				Util.ShowToast(mContext, responseDetails);
			}else {
				Util.ShowToast(mContext, "Comment post parser error.");
			}
		}
	}
	
	
	private JobDetails getParseData(JSONObject jsonobject){

		JobDetails mJobDetails=null;
		try {
			mJobDetails  =new JobDetails();
			mJobDetails.setPostid(Util.getJsonValue(jsonobject, POSTID)); 
			mJobDetails.setContent(Util.getJsonValue(jsonobject, CONTENT));
			
			JSONObject userJSONobj=jsonobject.getJSONObject(USERDTO);
			mJobDetails.setId(Util.getJsonValue(userJSONobj, ID));
			mJobDetails.setName(Util.getJsonValue(userJSONobj, NAME));
			mJobDetails.setImage(Util.getJsonValue(userJSONobj, IMAGE));
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mJobDetails;
		
	}
	
	public JSONObject getBody(String actionId , String postId,String comment) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("postReactionId", actionId);
			jsObj.put("postId", postId);
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
			jsObj.put("comment", comment);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	public CommentPostParserInterface commentpostparserinterface;

	public CommentPostParserInterface getCommentpostparserinterface() {
		return commentpostparserinterface;
	}

	public void setCommentpostparserinterface(
			CommentPostParserInterface commentpostparserinterface) {
		this.commentpostparserinterface = commentpostparserinterface;
	}

	public interface CommentPostParserInterface {
		public void OnSuccess(JobDetails mJobDetails);

		public void OnError();
	}



}
