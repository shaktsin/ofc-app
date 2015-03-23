package com.ofcampus.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;

public class LoginParser {

	private Context mContext;
	
	private String STATUS="status";
	private String RESULTS="results";
//	private String EXCEPTION="exception";
	private String VERIFIED="verified";
	private String NAME="name";
	private String EMAIL="email";
	private String AUTHTOKEN="authToken";

	
	private String PROFILEIMAGELINK="image";
	private String FIRSTNAME="firstName";
	private String LASTNAME="lastName";
	private String YEAROFGRAD="yearOfGrad";

	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	public void parse(Context context, String email, String password) { 
		this.mContext = context;
		List<NameValuePair> postData =getparamBody(email, password);
		loginAsync mLoginAsync = new loginAsync(mContext,postData);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mLoginAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mLoginAsync.execute();
		}
	}
	
	
	private class loginAsync extends AsyncTask<Void, Void, Void>{
		private Context context;
		private String authenticationJson;
		private List<NameValuePair> postData; 
		private UserDetails mDetails;
		private boolean isTimeOut=false;
		private ProgressDialog mDialog;

		public loginAsync(Context mContext, List<NameValuePair> postData_) {
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
				String[] responsedata =  Util.PostRequest(postData, Util.getLoginUrl());
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205"))?true:false;
				
				if (authenticationJson!=null && !authenticationJson.equals("")) {
					JSONObject mObject=new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode!=null && responsecode.equals("200")) {
						JSONObject userObj = mObject.getJSONObject(RESULTS);
						mDetails=new UserDetails();
						mDetails.setName(Util.getJsonValue(userObj, NAME));
						mDetails.setEmail(Util.getJsonValue(userObj, EMAIL));
						mDetails.setAuthtoken(Util.getJsonValue(userObj, AUTHTOKEN));
						
						mDetails.fstname=Util.getJsonValue(userObj, FIRSTNAME);
						mDetails.lstname=Util.getJsonValue(userObj, LASTNAME);
						mDetails.image=Util.getJsonValue(userObj, PROFILEIMAGELINK);
						mDetails.yearPass=Util.getJsonValue(userObj, YEAROFGRAD);
						
						
						mDetails.setVerify(((Util.getJsonValue(userObj, VERIFIED)).equals("true"))?true:false);
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
				if (logininterface != null) {
					logininterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (mDetails!=null) {
					if (logininterface!=null) {
						logininterface.OnSuccess(mDetails);
					}
				}else {
					Util.ShowToast(mContext, "Login error.");
				}
			}else if (responsecode.equals("500")){
				Util.ShowToast(mContext, "Invalid Credentials");
			}else {
				Util.ShowToast(mContext, "Login error.");
			}
		}
	}
	
	public static List<NameValuePair> getparamBody(String email,String password) {
		List<NameValuePair> pairsofEducation = new ArrayList<NameValuePair>();
		pairsofEducation.add(new BasicNameValuePair("email", email));
		pairsofEducation.add(new BasicNameValuePair("password", password));
		return pairsofEducation;
	}
	
	
	public LoginInterface logininterface;

	public LoginInterface getLogininterface() {
		return logininterface;
	}

	public void setLogininterface(LoginInterface logininterface) {
		this.logininterface = logininterface;
	}


	public interface LoginInterface{
		public void OnSuccess(UserDetails mDetails);
		public void OnError();
	}
	
	
	
	
	
}
