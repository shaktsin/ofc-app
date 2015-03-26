/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.parser;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;

public class EditProfileParser {


	
	private Context mContext;
	private String STATUS="status";
	private String RESULTS="results";


	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	private String ACCOUNTNAME="accountName";
	private String GRADYEAR="gradYear";
	private String PROFILEIMAGELINK="profileImageLink";
	private String FIRSTNAME="firstName";
	private String LASTNAME="lastName";
	private String YEAROFGRAD="yearOfGrad";
	private String EMAIL="email";
	
	
	
	public void parse(Context context, JSONObject obj,String auth, String paths) {   
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
		private UserDetails UDetails;
		private ProgressDialog mDialog;
		private JSONObject obj_=null;
		private String paths;
		private String auth="";
		
		public EditProfileParserAsync(Context mContext, JSONObject obj, String auth_,String paths_) { 
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
				
				String[] responsedata =	Util.ProfileUpdte(Util.getProfileUpdateUrl(), obj_,auth,paths);
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205"))?true:false;
				
				if (authenticationJson!=null && !authenticationJson.equals("")) {
					JSONObject mObject=new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode!=null && responsecode.equals("200")) {
						JSONObject userObj = mObject.getJSONObject(RESULTS);
						UserDetails mDetails=UserDetails.getLoggedInUser(mContext);
						mDetails.name=Util.getJsonValue(userObj, ACCOUNTNAME);
						mDetails.fstname=Util.getJsonValue(userObj, FIRSTNAME);
						mDetails.lstname=Util.getJsonValue(userObj, LASTNAME);
						mDetails.accountname=Util.getJsonValue(userObj, ACCOUNTNAME);
						mDetails.image=Util.getJsonValue(userObj, PROFILEIMAGELINK);
						mDetails.yearPass=Util.getJsonValue(userObj, GRADYEAR);
						UDetails=mDetails;
						mDetails.saveInPreferense(mContext);
						
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
				if (UDetails!=null) {
					if (meditprofileparserinterface!=null) {
						meditprofileparserinterface.OnSuccess(UDetails);
						Util.ShowToast(mContext, "Profile Updated successfully.");
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
				Util.ShowToast(mContext, "Profile Updated Error.");
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
		public void OnSuccess(UserDetails UDetails);

		public void OnError();
	}



}
