package com.ofcampus.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ofcampus.Util;
import com.ofcampus.model.JobDetails;

public class CommentParser {

	private Context mContext;
	private String STATUS="status";
	private String RESULTS="results";
//	private String EXCEPTION="exception";
//	private String MESSAGES="messages";


	/*Job List Key*/
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
	
	/**CommentList Key*/
	private String COMMENTLISTRESPONSE="commentListResponse";
	private String COMMENTRESPONSELIST="commentResponseList";
	private String COMMENTEDON="commentedOn";
	
	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	
	
	
	
	public void parse(Context context,String id,String auth) {  
		this.mContext = context;
		List<NameValuePair> postData = getparamBody(auth);
		
		JobDetailsAsyncAsync mJobDetailsAsyncAsync = new JobDetailsAsyncAsync(mContext,postData,id); 
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
		private ArrayList<JobDetails> arrayJobsComment;
		private ProgressDialog mDialog;
		private List<NameValuePair> postData;
		private String id="";
		
		public JobDetailsAsyncAsync(Context mContext, List<NameValuePair> postData_, String id_) {
			this.context = mContext;
			this.postData = postData_; 
			this.id=id_; 
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
				
				String[] responsedata =	Util.GetRequest(postData,Util.getJobDetailsUrl(id));
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205"))?true:false;
				
				if (authenticationJson!=null && !authenticationJson.equals("")) {
					JSONObject mObject=new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode!=null && responsecode.equals("200")) {
						JSONObject Obj = mObject.getJSONObject(RESULTS);
						if (Obj!=null) {
							arrayJobsComment = getParseData(Obj);
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
				if (commentparserinterface != null) { 
					commentparserinterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (arrayJobsComment!=null) {
					if (commentparserinterface!=null) {
						commentparserinterface.OnSuccess(arrayJobsComment); 
					}
				}else {
					Util.ShowToast(mContext, "Job details parser error.");
					if (commentparserinterface != null) {
						commentparserinterface.OnError(); 
					}
				}
			}else if (responsecode.equals("500")){
				Util.ShowToast(mContext, responseDetails);
			}else {
				Util.ShowToast(mContext, "Job details parser error.");
			}
		}
	}
	
	
	private ArrayList<JobDetails> getParseData(JSONObject jsonobject){


		ArrayList<JobDetails> arrayJobsComment = null; 
		try {
			arrayJobsComment=new ArrayList<JobDetails>();
			JobDetails mJobDetails  =new JobDetails();
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
			
			arrayJobsComment.add(mJobDetails);
			
			try {
				JSONObject commentJsonObj=jsonobject.getJSONObject(COMMENTLISTRESPONSE);
				JSONArray commentArrJsonArray = null; 
				if (commentJsonObj!=null) {
					commentArrJsonArray = commentJsonObj.getJSONArray(COMMENTRESPONSELIST);
					 
					 if (commentArrJsonArray!=null && commentArrJsonArray.length()>=1) {
							for (int i = 0; i < commentArrJsonArray.length(); i++) {
								
								JSONObject commenObj=commentArrJsonArray.getJSONObject(i);
								
								JobDetails jobComment=new JobDetails();
								jobComment.setPostid(Util.getJsonValue(commenObj, POSTID)); 
								jobComment.setContent(Util.getJsonValue(commenObj, CONTENT));
								jobComment.setPostedon(Util.getJsonValue(commenObj, COMMENTEDON));
								JSONObject jobCommentuserJSONobj=commenObj.getJSONObject(USERDTO);
								jobComment.setId(Util.getJsonValue(jobCommentuserJSONobj, ID));
								jobComment.setName(Util.getJsonValue(jobCommentuserJSONobj, NAME));
								jobComment.setImage(Util.getJsonValue(jobCommentuserJSONobj, IMAGE));
								arrayJobsComment.add(jobComment);
							}
						}
					 
				}
				
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arrayJobsComment;
		
	}
	
	public static List<NameValuePair> getparamBody(String authorization) {
		List<NameValuePair> pairsofEducation = new ArrayList<NameValuePair>();
		pairsofEducation.add(new BasicNameValuePair("Authorization", authorization));
		return pairsofEducation;
	}
	
	public CommentParserInterface commentparserinterface;

	public CommentParserInterface getCommentparserinterface() {
		return commentparserinterface;
	}

	public void setCommentparserinterface(
			CommentParserInterface commentparserinterface) {
		this.commentparserinterface = commentparserinterface;
	}

	public interface CommentParserInterface {
		public void OnSuccess(ArrayList<JobDetails> arrayJobsComment); 

		public void OnError();
	}
}
