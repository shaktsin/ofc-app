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
import com.ofcampus.model.CityDetails;
import com.ofcampus.model.IndustryDetails;
import com.ofcampus.model.IndustryRoleDetails;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.JobList;

public class JobListParser  {

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
	private String SHAREDTO="shareDto";
	
	/*City List Key*/
	private String CITYDTOLIST="cityDtoList";
	private String CITY_ID="id";
	private String CITY_NAME="name";
	private String CITY_SELECTED="selected";
	
	/*industry Roles List Key*/
	private String industryRolesDtoList="industryRolesDtoList";
	private String INDUSTRYROLES_ID="id";
	private String INDUSTRYROLES_NAME="name";
	private String INDUSTRYROLES_INDUSTRYID="industryId";
	private String INDUSTRYROLES_INDUSTRYNAME="industryName";
	private String INDUSTRYROLES_SELECTED="selected";
	
	/*industry List Key*/
	private String industryDtoList="industryDtoList";
	private String INDUSTRY_ID="id";
	private String INDUSTRY_NAME="name";
	private String INDUSTRY_SELECTED="selected";
	
	
	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	public void parse(Context context, String authorization) { 
		this.mContext = context;
		List<NameValuePair> postData =getparamBody(authorization);
		joblistAsync mjoblistAsync = new joblistAsync(mContext,postData);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mjoblistAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mjoblistAsync.execute(); 
		}
	}
	
	
	private class joblistAsync extends AsyncTask<Void, Void, Void>{ 
		private Context context;
		private String authenticationJson;
		private List<NameValuePair> postData; 
		private boolean isTimeOut=false;
		private ProgressDialog mDialog;
		private JobList mJobList;
		
		
		
		public joblistAsync(Context mContext, List<NameValuePair> postData_) {
			this.context = mContext;
			this.postData = postData_;
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
				String[] responsedata =  Util.GetRequest(postData, Util.getJobListUrl());
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
				if (joblistparserinterface != null) {
					joblistparserinterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (mJobList!=null) {
					if (joblistparserinterface!=null) {
						joblistparserinterface.OnSuccess(mJobList);
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
	
	

	
	
	
	private JobList parseJSONData(JSONObject obj){

		JobList mJobList=new JobList();
		
		try {
			JSONObject jsonobject=null;
			
			JSONArray jobjsonarray=obj.getJSONArray(JOBCREATERESPONSELIST) ;
			JSONArray cityjsonarray=obj.getJSONArray(CITYDTOLIST) ;
			JSONArray industryrolejsonarray=obj.getJSONArray(industryRolesDtoList) ;
			JSONArray industryjsonarray=obj.getJSONArray(industryDtoList) ;
			
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
					
					JSONObject userJSONobj=jsonobject.getJSONObject(USERDTO);
					mJobDetails.setId(Util.getJsonValue(userJSONobj, ID));
					mJobDetails.setName(Util.getJsonValue(userJSONobj, NAME));
					mJobDetails.setImage(Util.getJsonValue(userJSONobj, IMAGE));
					
					mJobDetails.setReplydto(Util.getJsonValue(jsonobject, REPLYDTO));
					mJobDetails.setSharedto(Util.getJsonValue(jsonobject, SHAREDTO));

					jobarray.add(mJobDetails);
					mJobDetails=null;
				}
				mJobList.setJobs(jobarray);
			}
			
			if (cityjsonarray != null && cityjsonarray.length() >= 1) {
				
				ArrayList<CityDetails> cityarray=new ArrayList<CityDetails>();
				
				for (int i = 0; i < cityjsonarray.length(); i++) {
					CityDetails mCityDetails=new CityDetails();
					jsonobject = cityjsonarray.getJSONObject(i);
					
					mCityDetails.setCity_id(Util.getJsonValue(jsonobject, CITY_ID));
					mCityDetails.setCity_name(Util.getJsonValue(jsonobject, CITY_NAME));
					mCityDetails.setCity_selected(Util.getJsonValue(jsonobject, CITY_SELECTED));
					cityarray.add(mCityDetails);
					mCityDetails=null;
				}
				mJobList.setCitys(cityarray);
			}
			
			if (industryrolejsonarray != null && industryrolejsonarray.length() >= 1) {
				
				ArrayList<IndustryRoleDetails> industryrolerray=new ArrayList<IndustryRoleDetails>();
				
				for (int i = 0; i < industryrolejsonarray.length(); i++) {
					IndustryRoleDetails mRoleDetails=new IndustryRoleDetails();
					jsonobject = industryrolejsonarray.getJSONObject(i);
					
					mRoleDetails.setIndustryroles_id(Util.getJsonValue(jsonobject, INDUSTRYROLES_ID));
					mRoleDetails.setIndustryroles_name(Util.getJsonValue(jsonobject, INDUSTRYROLES_NAME));
					mRoleDetails.setIndustryroles_industryid(Util.getJsonValue(jsonobject, INDUSTRYROLES_INDUSTRYID));
					mRoleDetails.setIndustryroles_industryname(Util.getJsonValue(jsonobject, INDUSTRYROLES_INDUSTRYNAME));
					mRoleDetails.setIndustryroles_selected(Util.getJsonValue(jsonobject, INDUSTRYROLES_SELECTED));
					industryrolerray.add(mRoleDetails);
					mRoleDetails=null;
				}
				mJobList.setIndustryRoles(industryrolerray);
				
			}
			
			if (industryjsonarray != null && industryjsonarray.length() >= 1) {
				
				ArrayList<IndustryDetails> industryarray=new ArrayList<IndustryDetails>();
				
				for (int i = 0; i < industryjsonarray.length(); i++) {
					IndustryDetails mIndustryDetails=new IndustryDetails();
					jsonobject = industryjsonarray.getJSONObject(i);
					
					mIndustryDetails.setIndustry_id(Util.getJsonValue(jsonobject, INDUSTRY_ID));
					mIndustryDetails.setIndustry_name(Util.getJsonValue(jsonobject, INDUSTRY_NAME));
					mIndustryDetails.setIndustry_selected(Util.getJsonValue(jsonobject, INDUSTRY_SELECTED));
					industryarray.add(mIndustryDetails);
					mIndustryDetails=null;
					
				}
				mJobList.setIndustrys(industryarray); 
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return mJobList;
		
	}
	
	
	
	public static List<NameValuePair> getparamBody(String authorization) {
		List<NameValuePair> pairsofEducation = new ArrayList<NameValuePair>();
		pairsofEducation.add(new BasicNameValuePair("Authorization", authorization));
		return pairsofEducation;
	}
	
	
	public JobListParserInterface joblistparserinterface;

	public JobListParserInterface getJoblistparserinterface() {
		return joblistparserinterface;
	}

	public void setJoblistparserinterface(
			JobListParserInterface joblistparserinterface) {
		this.joblistparserinterface = joblistparserinterface;
	}

	public interface JobListParserInterface {
		public void OnSuccess(JobList mJobList); 

		public void OnError();
	}
	
}
