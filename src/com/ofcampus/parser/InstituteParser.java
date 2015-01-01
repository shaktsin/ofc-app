package com.ofcampus.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.ofcampus.Util;
import com.ofcampus.model.InstituteDetails;

public class InstituteParser {
	
	private Context mContext;
	private String responsecode="";
	
	private String STATUS="status";
	private String RESULTS="results";
	private String INSTITUTEDTOLIST="instituteDtoList";
	private String ID="id";
	private String NM="nm";
	private String EMSUFFIX="emSuffix";
	private String THPARTYAUTH="thPartyAuth";
	private String PROVIDER="provider";

	
	public void parse(Context context) { 
		this.mContext = context;
		instituteAsync minstituteAsync = new instituteAsync(mContext);  
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			minstituteAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			minstituteAsync.execute();
		}
	}
	
	
	private class instituteAsync extends AsyncTask<Void, Void, Void>{
		private Context context;
		private String authenticationJson; 
		private boolean isTimeOut=false;
		private ArrayList<InstituteDetails> Institutes;
		
		public instituteAsync(Context mContext) {
			this.context = mContext;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				String[] responsedata = Util.sendGet(Util.getInstituteUrl());
				authenticationJson = responsedata[1];
				isTimeOut = (responsedata[0].equals("205"))?true:false;
				if (authenticationJson!=null && !authenticationJson.equals("")) {
					JSONObject mObject=new JSONObject(authenticationJson);
					responsecode = Util.getJsonValue(mObject, STATUS);
					if (responsecode!=null && responsecode.equals("200")) {
						JSONObject resultObj = mObject.getJSONObject(RESULTS);
						if (resultObj!=null && !resultObj.equals("")) {
							JSONArray instituteArray=resultObj.getJSONArray(INSTITUTEDTOLIST);
							if (instituteArray!=null && instituteArray.length()>=1) {
								Institutes=new ArrayList<InstituteDetails>();
								for (int i = 0; i < instituteArray.length(); i++) {
									JSONObject institute = instituteArray.getJSONObject(i);
									InstituteDetails mDetails=new InstituteDetails();
									mDetails.setId(Util.getJsonValue(institute, ID));
									mDetails.setNm(Util.getJsonValue(institute, NM));
									mDetails.setEmsuffix(Util.getJsonValue(institute, EMSUFFIX));
									mDetails.setThpartyauth(Util.getJsonValue(institute, THPARTYAUTH));
									mDetails.setProvider(Util.getJsonValue(institute, PROVIDER));
									Institutes.add(mDetails);
								}
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			
			if (isTimeOut) {
				if (instituteparserinterface != null) {
					instituteparserinterface.OnError(); 
				}
			}else if (responsecode.equals("200")) {
				if (Institutes!=null) {
					if (instituteparserinterface!=null) {
						instituteparserinterface.OnSuccess(Institutes);
					}
				}else {
					Util.ShowToast(mContext, "Login error.");
				}
			}else if (responsecode.equals("500")){
				Util.ShowToast(mContext, "Please check your email and password.");
			}else {
				Util.ShowToast(mContext, "Login error.");
			}
			
		}
	}
	
	public InstituteParserInterface instituteparserinterface;

	public InstituteParserInterface getInstituteparserinterface() {
		return instituteparserinterface;
	}

	public void setInstituteparserinterface(
			InstituteParserInterface instituteparserinterface) {
		this.instituteparserinterface = instituteparserinterface;
	}

	public interface InstituteParserInterface {
		public void OnSuccess(ArrayList<InstituteDetails> institutes); 
		public void OnError();
	}
	
	
}
