package com.ofcampus.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ofcampus.Util;

public class CountSyncParser {
private Context mContext;
	
	private String STATUS="status";
	private String RESULTS="results";
	
	
	private String EXCEPTION="exception";
	private String MESSAGES="messages";
	
	/*Sync List Key*/
	private String JOBCOUNT="jobCount";
	private String CLASSCOUNT="classCount";
	private String MEETCOUNT="meetCount";
	
	
	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	public String[] parse(Context context, JSONObject postData,String authorization) { 
		
		this.mContext = context;
		String authenticationJson;
		boolean isTimeOut=false;
		String[] counts={"","",""};
		
		try {
			String[] responsedata =  Util.POST_JOB(Util.getJobSyncCountUrl(), postData, authorization);
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
							counts[0]=Util.getJsonValue(Obj, JOBCOUNT);
							counts[1]=Util.getJsonValue(Obj, CLASSCOUNT);
							counts[2]=Util.getJsonValue(Obj, MEETCOUNT);
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
		return counts; 
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
