/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ofcampus.Util;
import com.ofcampus.Util.SearchType;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.SearchData;

public class SearchParser {

	private String STATUS = "status";
	private String RESULTS = "results";

	private String EXCEPTION = "exception";
	private String MESSAGES = "messages";

	/* Job List Key */
	private String POSTS = "posts";
	private String ID = "id";
	private String SUBJECT = "subject";
	private String TYPE = "type";

	private String CIRCLES = "circles";
	private String CIRCLE_ID = "id";
	private String CIRCLE_NAME = "name";

	private String USERS = "users";
	private String USER_ID = "id";
	private String FIRSTNAME = "firstName";
	private String LASTNAME = "lastName";
	private String ACCOUNTNAME = "accountName";

	/* Response JSON key value */
	private String responsecode = "";
	private String responseDetails = "";

	private String authenticationJson;
	private boolean isTimeOut = false;
	public ArrayList<SearchData> SearchDataList;

	public void doInBackground_(JSONObject postData, String authorization) {
		try {
			String[] responsedata = Util.POSTWithJSONAuth(Util.getSearchURL(), postData, authorization);
			authenticationJson = responsedata[1];
			isTimeOut = (responsedata[0].equals("205")) ? true : false;

			if (authenticationJson != null && !authenticationJson.equals("")) {
				JSONObject mObject = new JSONObject(authenticationJson);
				responsecode = Util.getJsonValue(mObject, STATUS);
				if (responsecode != null && responsecode.equals("200")) {
					JSONObject Obj = mObject.getJSONObject(RESULTS);
					if (Obj != null && !Obj.equals("")) {
						String expt = Util.getJsonValue(Obj, EXCEPTION);
						if (expt.equals("false")) {
							parseJSONData(Obj);
						}
					}
				} else if (responsecode != null && (responsecode.equals("500") || responsecode.equals("401"))) {
					JSONObject userObj = mObject.getJSONObject(RESULTS);
					if (userObj != null) {
						responseDetails = userObj.getJSONArray("messages").get(0).toString();
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void parseJSONData(JSONObject obj) {

		SearchDataList = new ArrayList<SearchData>();

		try {
			JSONObject object = null;

			JSONArray postsJSONarray = obj.getJSONArray(POSTS);
			JSONArray circlesJSONarray = obj.getJSONArray(CIRCLES);
			JSONArray usersJSONarray = obj.getJSONArray(USERS);

			if (postsJSONarray != null && postsJSONarray.length() >= 1) {
				for (int i = 0; i < postsJSONarray.length(); i++) {
					SearchData mSearchData = new SearchData();
					object = postsJSONarray.getJSONObject(i);
					mSearchData.setId(Util.getJsonValue(object, ID));
					mSearchData.setData(Util.getJsonValue(object, SUBJECT));
					mSearchData.setDatatype(Util.getJsonValue(object, TYPE));
					mSearchData.setmSearchType(SearchType.POSTS);
					SearchDataList.add(mSearchData);
				}
			}

			if (circlesJSONarray != null && circlesJSONarray.length() >= 1) {
				for (int i = 0; i < circlesJSONarray.length(); i++) {
					SearchData mSearchData = new SearchData();
					object = circlesJSONarray.getJSONObject(i);
					mSearchData.setId(Util.getJsonValue(object, CIRCLE_ID));
					mSearchData.setData(Util.getJsonValue(object, CIRCLE_NAME));
					mSearchData.setDatatype("");
					mSearchData.setmSearchType(SearchType.CIRCLE);
					SearchDataList.add(mSearchData);
				}
			}

			if (usersJSONarray != null && usersJSONarray.length() >= 1) {
				for (int i = 0; i < usersJSONarray.length(); i++) {
					SearchData mSearchData = new SearchData();
					object = usersJSONarray.getJSONObject(i);
					mSearchData.setId(Util.getJsonValue(object, USER_ID));
					mSearchData.setData(Util.getJsonValue(object, FIRSTNAME) + " " + Util.getJsonValue(object, LASTNAME));
					mSearchData.setDatatype("");
					mSearchData.setmSearchType(SearchType.USERS);
					SearchDataList.add(mSearchData);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getBody(String token) {
		JSONObject jsObj = new JSONObject();
		try {
			jsObj.put("token", token);
			jsObj.put("appName", "ofCampus");
			jsObj.put("plateFormId", "0");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsObj;
	}

	public SearchParserInterface searchparserinterface;

	public SearchParserInterface getSearchparserinterface() {
		return searchparserinterface;
	}

	public void setSearchparserinterface(SearchParserInterface searchparserinterface) {
		this.searchparserinterface = searchparserinterface;
	}

	public interface SearchParserInterface {
		public void OnSuccess(ArrayList<JobDetails> jobList);

		public void OnError();
	}

}
