package com.ofcampus.parser;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ofcampus.Util;
import com.ofcampus.model.JobDetails;

public class EditProfileParser {


	
	private Context mContext;
	private String STATUS="status";
	private String RESULTS="results";


	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	public void parse(Context context, JSONObject obj,String auth, ArrayList<String> paths) {   
		this.mContext = context;
		EditProfileParserAsync mEditProfileParserAsync = new EditProfileParserAsync(mContext,obj,auth,paths); 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mEditProfileParserAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); 
		} else {
			mEditProfileParserAsync.execute();   
		}
	}
	
	
	private class EditProfileParserAsync extends AsyncTask<Void, Void, Void>{
		private Context context;
		private String authenticationJson;
		private boolean isTimeOut=false;
		private JobDetails mJobDetails;
		private ProgressDialog mDialog;
		private JSONObject obj_=null;
		private ArrayList<String> paths;
		private String auth="";
		
		public EditProfileParserAsync(Context mContext, JSONObject obj, String auth_, ArrayList<String> paths_) { 
			this.context = mContext;
			this.obj_ = obj; 
			this.auth=auth_;
			this.paths=paths_;
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
				
				String[] responsedata =	Util.POST_JOBNEW(Util.getJobEditUrl(), obj_,auth,paths);
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205"))?true:false;
				
				if (authenticationJson!=null && !authenticationJson.equals("")) {
					JSONObject mObject=new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode!=null && responsecode.equals("200")) {
						JSONObject Obj = mObject.getJSONObject(RESULTS);
						
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
				if (meditprofileparserinterface != null) {
					meditprofileparserinterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (mJobDetails!=null) {
					if (meditprofileparserinterface!=null) {
						meditprofileparserinterface.OnSuccess(mJobDetails);
						Util.ShowToast(mContext, "Job Posted successfully.");
					}
				}else {
					Util.ShowToast(mContext, "Edit profile error.");
					if (meditprofileparserinterface != null) {
						meditprofileparserinterface.OnError();   
					}
				}
			}else if (responsecode.equals("500")){
				Util.ShowToast(mContext, responseDetails);
			}else {
				Util.ShowToast(mContext, "Edit profile error.");
			}
		}
	}
	

	
	public mEditProfileParserInterface meditprofileparserinterface;

	public mEditProfileParserInterface getMeditprofileparserinterface() {
		return meditprofileparserinterface;
	}

	public void setMeditprofileparserinterface(
			mEditProfileParserInterface meditprofileparserinterface) {
		this.meditprofileparserinterface = meditprofileparserinterface;
	}
	public interface mEditProfileParserInterface { 
		public void OnSuccess(JobDetails mJobDetails);

		public void OnError();
	}



}
