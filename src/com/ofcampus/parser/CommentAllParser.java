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

public class CommentAllParser {

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
//	private String COMMENTLISTRESPONSE="commentListResponse";
	private String COMMENTRESPONSELIST="commentResponseList";
	private String COMMENTEDON="commentedOn";
	private String COMMENTID="commentId";
	
	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	
	
	
	
	public void parse(Context context,JSONObject body, String authtoken) {  
		this.mContext = context;
		List<NameValuePair> postData = getparamBody(authtoken);
		
		CommentAllParserAsync mCommentAllParserAsync = new CommentAllParserAsync(mContext,body,authtoken);   
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mCommentAllParserAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mCommentAllParserAsync.execute();  
		}
	}
	
	
	private class CommentAllParserAsync extends AsyncTask<Void, Void, Void>{
		private Context context;
		private String authenticationJson;
		private boolean isTimeOut=false;
		private ArrayList<JobDetails> arrayJobsComment;
		private ProgressDialog mDialog;
		private JSONObject postData;
		private String authtoken="";
		
		public CommentAllParserAsync(Context mContext, JSONObject body, String authtoken_) { 
			this.context = mContext;
			this.postData = body; 
			this.authtoken=authtoken_; 
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			mDialog=new ProgressDialog(mContext);
//			mDialog.setMessage("Loading...");
//			mDialog.setCancelable(false);
//			mDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				
				String[] responsedata =	 Util.POST_JOB(Util.getOldCommentsUrl(), postData, authtoken);
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
			
//			if (mDialog!=null && mDialog.isShowing()) {
//				mDialog.cancel();
//				mDialog=null;
//			}
			
			if (isTimeOut) {
				if (commentallparserinterface != null) { 
					commentallparserinterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (arrayJobsComment!=null) {
					if (commentallparserinterface!=null) {
						commentallparserinterface.OnSuccess(arrayJobsComment); 
					}
				}else {
					Util.ShowToast(mContext, "Load old Comment parser error.");
					if (commentallparserinterface != null) {
						commentallparserinterface.OnError(); 
					}
				}
			}else if (responsecode.equals("500")){
				Util.ShowToast(mContext, responseDetails);
				if (commentallparserinterface != null) {
					commentallparserinterface.OnError(); 
				}
			}else {
				Util.ShowToast(mContext, "Load old Comment parser error.");
				if (commentallparserinterface != null) {
					commentallparserinterface.OnError(); 
				}
			}
		}
	}
	
	
	private ArrayList<JobDetails> getParseData(JSONObject jsonobject){


		ArrayList<JobDetails> arrayJobsComment = null; 
		try {
			JSONArray commentArrJsonArray = null; 
			if (jsonobject!=null) {
				commentArrJsonArray = jsonobject.getJSONArray(COMMENTRESPONSELIST);
				 
				 if (commentArrJsonArray!=null && commentArrJsonArray.length()>=1) {
					 arrayJobsComment=new ArrayList<JobDetails>();
						for (int i = 0; i < commentArrJsonArray.length(); i++) {
							
							JSONObject commenObj=commentArrJsonArray.getJSONObject(i);
							
							JobDetails jobComment=new JobDetails();
							jobComment.setPostid(Util.getJsonValue(commenObj, POSTID)); 
							jobComment.setCommentID(Util.getJsonValue(commenObj, COMMENTID));
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
		return arrayJobsComment;
		
	}
	
	public static List<NameValuePair> getparamBody(String authorization) {
		List<NameValuePair> pairsofEducation = new ArrayList<NameValuePair>();
		pairsofEducation.add(new BasicNameValuePair("Authorization", authorization));
		return pairsofEducation;
	}
	
	public JSONObject getBody(String actionId , String postId,String commentID) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("commentId", commentID);
			jsObj.put("postId", postId);
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	
	public CommentAllParserInterface commentallparserinterface;

	public CommentAllParserInterface getCommentallparserinterface() {
		return commentallparserinterface;
	}

	public void setCommentallparserinterface(
			CommentAllParserInterface commentallparserinterface) {
		this.commentallparserinterface = commentallparserinterface;
	}

	public interface CommentAllParserInterface {
		public void OnSuccess(ArrayList<JobDetails> arrayJobsComment);

		public void OnError();
	}
}
