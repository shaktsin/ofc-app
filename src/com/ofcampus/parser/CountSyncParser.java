package com.ofcampus.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ofcampus.Util;
import com.ofcampus.databasehelper.JOBListTable;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.JobList;

public class CountSyncParser {
private Context mContext;
	
	private String STATUS="status";
	private String RESULTS="results";
	
	
	private String EXCEPTION="exception";
	private String MESSAGES="messages";
	
	/*Job List Key*/
	private String JOBCREATERESPONSELIST="jobCreateResponseList";
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
	
	private String REPLYEMAIL="replyEmail";
	private String REPLYPHONE="replyPhone";
	private String REPLYWATSAPP="replyWatsApp";
	
	private String SHAREDTO="shareDto";
	private String IMPORTANT="important";
	
	
	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	public ArrayList<JobDetails> parse(Context context, JSONObject postData,String authorization) { 
		
		this.mContext = context;
		String authenticationJson;
		boolean isTimeOut=false;
		JobList mJobList;
		ArrayList<JobDetails> jobs = null;
		
		try {
			String[] responsedata =  Util.POST_JOB(Util.getJobListUrl(), postData, authorization);
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
							mJobList =parseJSONData(Obj);
							jobs =mJobList.getJobs();
							if (jobs!=null && jobs.size()>=1) {
								JOBListTable.getInstance(mContext).inserJobData(mJobList.getJobs());
							}
						}
					}
				}else if(responsecode!=null && (responsecode.equals("500") || responsecode.equals("401"))){
					JSONObject userObj = mObject.getJSONObject(RESULTS);
					if (userObj!=null) {
						responseDetails=userObj.getJSONArray("messages").get(0).toString();
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jobs; 
	}
	
	
	
	
	public JobList parseJSONData(JSONObject obj){

		JobList mJobList=new JobList();
		
		try {
			JSONObject jsonobject=null;
			
			JSONArray jobjsonarray=obj.getJSONArray(JOBCREATERESPONSELIST) ;
			
			if (jobjsonarray != null && jobjsonarray.length() >= 1) {
				
				ArrayList<JobDetails> jobarray = new ArrayList<JobDetails>();
				
				for (int i = 0; i < jobjsonarray.length(); i++) {
					JobDetails mJobDetails=new JobDetails();
					jsonobject = jobjsonarray.getJSONObject(i);
					
					mJobDetails.setPostid(Util.getJsonValue(jsonobject, POSTID)); 
					mJobDetails.setSubject(Util.getJsonValue(jsonobject, SUBJECT));
					mJobDetails.setIsb_jobs(Util.getJsonValue(jsonobject, ISB_JOBS));
					mJobDetails.setContent(Util.getJsonValue(jsonobject, CONTENT));
					mJobDetails.setPostedon(Util.getJsonValue(jsonobject, POSTEDON));
					mJobDetails.setImportant((Util.getJsonValue(jsonobject, IMPORTANT).equals("true"))?1:0);
					JSONObject userJSONobj=jsonobject.getJSONObject(USERDTO);
					mJobDetails.setId(Util.getJsonValue(userJSONobj, ID));
					mJobDetails.setName(Util.getJsonValue(userJSONobj, NAME));
					mJobDetails.setImage(Util.getJsonValue(userJSONobj, IMAGE));
					
					JSONObject rplJSONObj=jsonobject.getJSONObject(REPLYDTO);
					
					mJobDetails.setReplyEmail(Util.getJsonValue(rplJSONObj, REPLYEMAIL));
					mJobDetails.setReplyPhone(Util.getJsonValue(rplJSONObj, REPLYPHONE));
					mJobDetails.setReplyWatsApp(Util.getJsonValue(rplJSONObj, REPLYWATSAPP)); 
					mJobDetails.setSharedto(Util.getJsonValue(jsonobject, SHAREDTO));
					mJobDetails.setISSyncData(""+1); 

					jobarray.add(mJobDetails);
					mJobDetails=null;
				}
				mJobList.setJobs(jobarray);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return mJobList;
		
	}
	
	
	public JSONObject getBody(String postId, String operation) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
			jsObj.put("postId", postId);
			jsObj.put("operation", operation);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}
	
	

	public JSONObject getBody(String postId) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("plateFormId", "0");
			jsObj.put("appName", "ofCampus");
			jsObj.put("postId", postId);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

}
