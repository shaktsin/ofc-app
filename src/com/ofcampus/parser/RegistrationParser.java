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
import com.ofcampus.Util.userType;
import com.ofcampus.model.UserDetails;

public class RegistrationParser {
private Context mContext;
	
	private String STATUS="status";
	private String RESULTS="results";
//	private String EXCEPTION="exception";
//	private String MESSAGES="messages";
	private String NAME="name";
	private String EMAIL="email";
	private String AUTHTOKEN="authToken";
	private String VERIFIED="verified";
	
	
	private String PROFILEIMAGELINK="image";
	private String FIRSTNAME="firstName";
	private String LASTNAME="lastName";
	private String YEAROFGRAD="yearOfGrad";

	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	public void parse(Context context,JSONObject postBody) {  
		this.mContext = context;
		loginAsync mLoginAsync = new loginAsync(mContext,postBody);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mLoginAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			mLoginAsync.execute();
		}
	}
	
	
	private class loginAsync extends AsyncTask<Void, Void, Void>{
		private Context context;
		private String authenticationJson;
		private UserDetails mDetails;
		private boolean isTimeOut=false;
		private ProgressDialog mDialog;
		private JSONObject obj_=null;
		
		public loginAsync(Context mContext, JSONObject obj) {
			this.context = mContext;
			this.obj_ = obj; 
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
				
				String[] responsedata =	Util.POST(Util.getSignUp(), obj_);
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
				if (regstrationinterface != null) {
					regstrationinterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (mDetails!=null) {
					if (regstrationinterface!=null) {
						regstrationinterface.OnSuccess(mDetails);
					}
				}else {
					Util.ShowToast(mContext, "Login error.");
				}
			}else if (responsecode.equals("500")){
				Util.ShowToast(mContext, responseDetails);
			}else {
				Util.ShowToast(mContext, "Login error.");
			}
		}
	}
	
	public static List<NameValuePair> getparamBody(String firstName,
			String lastName, String accountName, String email, String password,
			String instituteId, String gender, String verified, String thirdPartAuth) { 

		List<NameValuePair> pairsofEducation = new ArrayList<NameValuePair>();

		pairsofEducation.add(new BasicNameValuePair("firstName", firstName));
		pairsofEducation.add(new BasicNameValuePair("lastName", lastName));
		pairsofEducation.add(new BasicNameValuePair("accountName", accountName));
		pairsofEducation.add(new BasicNameValuePair("email", email));
//		pairsofEducation.add(new BasicNameValuePair("password", password));
//		pairsofEducation.add(new BasicNameValuePair("rePassword", password));
		pairsofEducation.add(new BasicNameValuePair("instituteId", instituteId));
		pairsofEducation.add(new BasicNameValuePair("gender", gender));
		pairsofEducation.add(new BasicNameValuePair("verified", verified));
		pairsofEducation.add(new BasicNameValuePair("thirdPartAuth", thirdPartAuth));

		return pairsofEducation;
	}
	
	public JSONObject getjsonBody(String firstName, String lastName,
			String accountName, String email, String password,
			String instituteId, String gender, String verified,
			String thirdPartAuth, userType user) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("firstName", firstName);
			jsObj.put("lastName", lastName);
//			jsObj.put("accountName", accountName);
			jsObj.put("email", email);
//			jsObj.put("instituteId", instituteId);
//			jsObj.put("gender", gender);
			if (user == userType.Normal) {
				jsObj.put("password", password);
				jsObj.put("rePassword", password);
			}
//			jsObj.put("verified", verified);
			jsObj.put("thirdPartAuth", thirdPartAuth);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jsObj;
	}
	

    	 
	public RegstrationInterface regstrationinterface;

	public RegstrationInterface getRegstrationinterface() {
		return regstrationinterface;
	}

	public void setRegstrationinterface(RegstrationInterface regstrationinterface) {
		this.regstrationinterface = regstrationinterface;
	}

	public interface RegstrationInterface {
		public void OnSuccess(UserDetails mDetails);

		public void OnError();
	}
	
}