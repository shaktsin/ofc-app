package com.ofcampus.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.ofcampus.Util;
import com.ofcampus.model.Circle;
import com.ofcampus.model.CityDetails;
import com.ofcampus.model.FilterDataSets;
import com.ofcampus.model.IndustryDetails;
import com.ofcampus.model.IndustryRoleDetails;

public class FilterJobParser {

private Context mContext;
	
	private String STATUS="status";
	private String RESULTS="results";
	
	
	private String EXCEPTION="exception";
	private String MESSAGES="messages";
	
	/*Job List Key*/
	private String GENFILTERDTO="genFilterDto";
	private String CIRCLEDTOLIST="circleDtoList";
	private String SPECIFICFILTERMAP="specificFilterMap";
	private String JOBS="jobs";
	
	private String CIRCLE_ID="id";
	private String CIRCLE_NAME="name";
	private String CIRCLE_SELECTED="selected";
	
	private String RODTOLIST="roDtoList";
	private String INDLIST="indList";
	
	private String IND_ID="id";
	private String IND_NAME="name";
	private String IND_SELECTED="selected";
	
	private String INDUSTRYROLESDTOLIST="industryRolesDtoList";
	
	private String INDUSTRYROLES_ID="id";
	private String INDUSTRYROLE_NAME="name";
	private String INDUSTRYROLE_INDUSTRYID="industryId";
	private String INDUSTRYROLE_INDUSTRYNAME="industryName";
	private String INDUSTRYROLE_SELECTED="selected";
	
	
	private String CITYLIST="cityList";
	
	private String CITY_ID="id";
	private String CITY_NAME="name";
	private String CITY_SELECTED="selected";
	
	
	
	private String RANGEDTOMAP="rangeDtoMap";
	private String EXPERIENCE="Experience";
	private String SALARY="Salary";
	
	private String NAME="name";
	private String MIN="min";
	private String MAX="max";
	
	
	
	
	/*Response JSON key value*/
	private String responsecode="";
	private String responseDetails="";
	
	public FilterDataSets parse(Context context, String authorization) { 
		
		this.mContext = context;
		String authenticationJson;
		boolean isTimeOut=false;
		FilterDataSets mFilterDataSets = null;
		
		try {			
			String[] responsedata =  Util.GetRequest(getparamBody(authorization), Util.getFilterJobUrl());
			
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
							mFilterDataSets =parseJSONData(Obj); 
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
		return mFilterDataSets; 
	}
	
	
	
	
	public FilterDataSets parseJSONData(JSONObject obj){

		FilterDataSets mFilterDataSets=new FilterDataSets();
		
		try {
			JSONObject jsonobject=obj.getJSONObject(SPECIFICFILTERMAP).getJSONObject(JOBS);
			JSONArray industryJSONArray=jsonobject.getJSONArray(INDLIST);
			
			JSONArray generaljsonarray=obj.getJSONObject(GENFILTERDTO).getJSONArray(CIRCLEDTOLIST) ;
			JSONArray roljsonarray=jsonobject.getJSONArray(RODTOLIST) ;
			
			JSONArray cityjsonarray=jsonobject.getJSONArray(CITYLIST) ;
			JSONObject rangemapJSONObj=jsonobject.getJSONObject(RANGEDTOMAP);
			
			if (industryJSONArray != null && industryJSONArray.length() >= 1) {
				
				ArrayList<IndustryDetails> indusarray = new ArrayList<IndustryDetails>();
				for (int i = 0; i < industryJSONArray.length(); i++) { 
					
					JSONObject industryOBJ=industryJSONArray.getJSONObject(i);
					IndustryDetails mIndustryDetails=new IndustryDetails();
					
					mIndustryDetails.setIndustry_id(Util.getJsonValue(industryOBJ, IND_ID));
					mIndustryDetails.setIndustry_name(Util.getJsonValue(industryOBJ, IND_NAME));
					mIndustryDetails.setIndustry_selected(Util.getJsonValue(industryOBJ, IND_SELECTED));
					
					JSONArray induRolJSONArray=industryOBJ.getJSONArray(INDUSTRYROLESDTOLIST);
					
					ArrayList<IndustryRoleDetails> indsRolArray=null;
					if (induRolJSONArray != null && induRolJSONArray.length() >= 1) {
						
						indsRolArray=new ArrayList<IndustryRoleDetails>();
						
						for (int j = 0; j < induRolJSONArray.length(); j++) {
							JSONObject industryRolOBJ=induRolJSONArray.getJSONObject(j);
							IndustryRoleDetails mIndustryRoleDetails=new IndustryRoleDetails();
							
							mIndustryRoleDetails.setIndustryroles_id(Util.getJsonValue(industryRolOBJ, INDUSTRYROLES_ID));
							mIndustryRoleDetails.setIndustryroles_name(Util.getJsonValue(industryRolOBJ, INDUSTRYROLE_NAME));
							mIndustryRoleDetails.setIndustryroles_selected(Util.getJsonValue(industryRolOBJ, INDUSTRYROLE_SELECTED));
							mIndustryRoleDetails.setIndustryroles_industryid(Util.getJsonValue(industryRolOBJ, INDUSTRYROLE_INDUSTRYID));
							mIndustryRoleDetails.setIndustryroles_industryname(Util.getJsonValue(industryRolOBJ, INDUSTRYROLE_INDUSTRYNAME));
							indsRolArray.add(mIndustryRoleDetails);
						}
					}
					mIndustryDetails.setIndustryRoles(indsRolArray);
					indusarray.add(mIndustryDetails);
				}
				mFilterDataSets.setArrayIndustry(indusarray);
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
				mFilterDataSets.setArrayCity(cityarray);
			}
			
			if (generaljsonarray != null && generaljsonarray.length() >= 1) {
				
				ArrayList<Circle> arrCircles=new ArrayList<Circle>();
				
				for (int i = 0; i < generaljsonarray.length(); i++) {
					Circle mCircle=new Circle();
					jsonobject = generaljsonarray.getJSONObject(i);
					
					mCircle.setCircleid(Util.getJsonValue(jsonobject, CIRCLE_ID));
					mCircle.setCirclename(Util.getJsonValue(jsonobject, CIRCLE_NAME));
					mCircle.setCircleselected(Util.getJsonValue(jsonobject, CIRCLE_SELECTED));
					arrCircles.add(mCircle);
					mCircle=null;
				}
				mFilterDataSets.setArraCircles(arrCircles);
			}
			
			if (roljsonarray != null && roljsonarray.length() >= 1) {
				
				ArrayList<IndustryRoleDetails> arrIndustryRoleDetails=new ArrayList<IndustryRoleDetails>();
				
				for (int i = 0; i < roljsonarray.length(); i++) {
					IndustryRoleDetails mIndustryRoleDetails=new IndustryRoleDetails();
					jsonobject = roljsonarray.getJSONObject(i);
					
					mIndustryRoleDetails.setIndustryroles_id(Util.getJsonValue(jsonobject, INDUSTRYROLES_ID));
					mIndustryRoleDetails.setIndustryroles_name(Util.getJsonValue(jsonobject, INDUSTRYROLE_NAME));
					mIndustryRoleDetails.setIndustryroles_selected(Util.getJsonValue(jsonobject, INDUSTRYROLE_SELECTED));
					mIndustryRoleDetails.setIndustryroles_industryid(Util.getJsonValue(jsonobject, INDUSTRYROLE_INDUSTRYID));
					mIndustryRoleDetails.setIndustryroles_industryname(Util.getJsonValue(jsonobject, INDUSTRYROLE_INDUSTRYNAME));
					
					arrIndustryRoleDetails.add(mIndustryRoleDetails); 
					mIndustryRoleDetails=null;
				}
				mFilterDataSets.setArrRoleDetails(arrIndustryRoleDetails);
			}
			
			JSONObject expObj=rangemapJSONObj.getJSONObject(EXPERIENCE);
			JSONObject salObj=rangemapJSONObj.getJSONObject(SALARY);
			
			mFilterDataSets.setEXP_Name(Util.getJsonValue(expObj, NAME));
			mFilterDataSets.setEXP_Min(Util.getJsonValue(expObj, MIN));
			mFilterDataSets.setEXP_Max(Util.getJsonValue(expObj, MAX));
			
			mFilterDataSets.setSalary_Name(Util.getJsonValue(salObj, NAME));
			mFilterDataSets.setSalary_Min(Util.getJsonValue(salObj, MIN));
			mFilterDataSets.setSalary_Max(Util.getJsonValue(salObj, MAX));
			
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return mFilterDataSets;
		
	}
	
	
	public static List<NameValuePair> getparamBody(String authorization) {
		List<NameValuePair> pairsofEducation = new ArrayList<NameValuePair>();
		pairsofEducation.add(new BasicNameValuePair("Authorization", authorization));
		return pairsofEducation;
	}

}
