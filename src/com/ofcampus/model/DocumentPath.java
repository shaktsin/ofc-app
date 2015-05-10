package com.ofcampus.model;

import java.util.LinkedHashMap;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.ofcampus.Util;

public class DocumentPath {

	public LinkedHashMap<String, String> mapPath = new LinkedHashMap<String, String>();

	public void savePath(Context context) {
		SharedPreferences.Editor prefsEditor = Util.getPrefs(context).edit();
		Gson gson = new Gson();
		String json = gson.toJson(this);
		prefsEditor.putString("savePath", json);
		prefsEditor.commit();
	}

	public static DocumentPath getPath(Context context) {
		SharedPreferences mPrefs = Util.getPrefs(context);
		Gson gson = new Gson();
		String json = mPrefs.getString("savePath", "");
		DocumentPath obj = gson.fromJson(json, DocumentPath.class);
		return obj;
	}

}
