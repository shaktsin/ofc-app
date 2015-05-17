///*
// * This is the source code of OfCampus for Android v. 1.0.0.
// * You should have received a copy of the license in this archive (see LICENSE).
// * Copyright @Dibakar_Mistry, 2015.
// */
//package com.ofcampus.parser;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Build;
//
//import com.ofcampus.Util;
//import com.ofcampus.model.JobDetails;
//
//public class JobDetailsParser {
//
//	private Context mContext;
//	private String STATUS="status";
//	private String RESULTS="results";
////	private String EXCEPTION="exception";
////	private String MESSAGES="messages";
//
//
//	/*Job List Key*/
//	private String POSTID="postId";
//	private String SUBJECT="subject";
//	private String ISB_JOBS="ISB JOBS";
//	private String CONTENT="content";
//	private String POSTEDON="postedOn";
//	private String USERDTO="userDto";
//	private String ID="id";
//	private String NAME="name";
//	private String IMAGE="image";
//	private String REPLYDTO="replyDto";
//	private String SHAREDTO="shareDto";
//
//	/*Response JSON key value*/
//	private String responsecode="";
//	private String responseDetails="";
//	
//	public void parse(Context context,String id,String auth) {  
//		this.mContext = context;
//		List<NameValuePair> postData = getparamBody(auth);
//		
//		JobDetailsAsyncAsync mJobDetailsAsyncAsync = new JobDetailsAsyncAsync(mContext,postData,id); 
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			mJobDetailsAsyncAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		} else {
//			mJobDetailsAsyncAsync.execute();  
//		}
//	}
//	
//	
//	private class JobDetailsAsyncAsync extends AsyncTask<Void, Void, Void>{
//		private Context context;
//		private String authenticationJson;
//		private boolean isTimeOut=false;
//		private JobDetails mJobDetails;
//		private ProgressDialog mDialog;
//		private List<NameValuePair> postData;
//		private String id="";
//		
//		public JobDetailsAsyncAsync(Context mContext, List<NameValuePair> postData_, String id_) {
//			this.context = mContext;
//			this.postData = postData_; 
//			this.id=id_; 
//		}
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			mDialog=new ProgressDialog(mContext);
//			mDialog.setMessage("Loading...");
//			mDialog.setCancelable(false);
//			mDialog.show();
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			
//			try {
//				
//				String[] responsedata =	Util.GetRequest(postData,Util.getJobDetailsUrl(id));
//				authenticationJson = responsedata[1];
//				isTimeOut = (responsedata[0].equals("205"))?true:false;
//				
//				if (authenticationJson!=null && !authenticationJson.equals("")) {
//					JSONObject mObject=new JSONObject(authenticationJson);
//					responsecode = Util.getJsonValue(mObject, STATUS);
//					if (responsecode!=null && responsecode.equals("200")) {
//						JSONObject Obj = mObject.getJSONObject(RESULTS);
//						if (Obj!=null) {
//							mJobDetails = getParseData(Obj);
//						}
//					}else if(responsecode!=null && responsecode.equals("500")){
//						JSONObject userObj = mObject.getJSONObject(RESULTS);
//						if (userObj!=null) {
//							responseDetails=userObj.getJSONArray("messages").get(0).toString();
//						}
//					}
//					
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//			
//			if (mDialog!=null && mDialog.isShowing()) {
//				mDialog.cancel();
//				mDialog=null;
//			}
//			
//			if (isTimeOut) {
//				if (jobdetailsparserinterface != null) { 
//					jobdetailsparserinterface.OnError(); 
//				}
//			}else if (responsecode.equals("200")) {
//				if (mJobDetails!=null) {
//					if (jobdetailsparserinterface!=null) {
//						jobdetailsparserinterface.OnSuccess(mJobDetails);
////						Util.ShowToast(mContext, "Job Details.");
//					}
//				}else {
//					Util.ShowToast(mContext, "Job Post error.");
//					if (jobdetailsparserinterface != null) {
//						jobdetailsparserinterface.OnError(); 
//					}
//				}
//			}else if (responsecode.equals("500")){
//				Util.ShowToast(mContext, responseDetails);
//			}else {
//				Util.ShowToast(mContext, "Job Post error.");
//			}
//		}
//	}
//	
//	
//	private JobDetails getParseData(JSONObject jsonobject){
//
//		JobDetails mJobDetails =null;
//		
//		try {
//			mJobDetails =new JobDetails();
//			mJobDetails.setPostid(Util.getJsonValue(jsonobject, POSTID)); 
//			mJobDetails.setSubject(Util.getJsonValue(jsonobject, SUBJECT));
//			mJobDetails.setIsb_jobs(Util.getJsonValue(jsonobject, ISB_JOBS));
//			mJobDetails.setContent(Util.getJsonValue(jsonobject, CONTENT));
//			mJobDetails.setPostedon(Util.getJsonValue(jsonobject, POSTEDON));
//			
//			JSONObject userJSONobj=jsonobject.getJSONObject(USERDTO);
//			mJobDetails.setId(Util.getJsonValue(userJSONobj, ID));
//			mJobDetails.setName(Util.getJsonValue(userJSONobj, NAME));
//			mJobDetails.setImage(Util.getJsonValue(userJSONobj, IMAGE));
//			
//			mJobDetails.setReplydto(Util.getJsonValue(jsonobject, REPLYDTO));
//			mJobDetails.setSharedto(Util.getJsonValue(jsonobject, SHAREDTO));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		return mJobDetails;
//		
//	}
//	
//	public static List<NameValuePair> getparamBody(String authorization) {
//		List<NameValuePair> pairsofEducation = new ArrayList<NameValuePair>();
//		pairsofEducation.add(new BasicNameValuePair("Authorization", authorization));
//		return pairsofEducation;
//	}
//	
//	public JobDetailsParserInterface jobdetailsparserinterface;
//
//	public JobDetailsParserInterface getJobdetailsparserinterface() {
//		return jobdetailsparserinterface;
//	}
//
//	public void setJobdetailsparserinterface(
//			JobDetailsParserInterface jobdetailsparserinterface) {
//		this.jobdetailsparserinterface = jobdetailsparserinterface;
//	}
//
//	public interface JobDetailsParserInterface {
//		public void OnSuccess(JobDetails mJobDetails);
//
//		public void OnError();
//	}
// }
