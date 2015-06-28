package com.ofcampus.parser;

import com.ofcampus.R;

import android.content.Context;

public class HttpUrlConnection {

	private static String getBaseUrl(Context mContext) {
		// return mContext.getResources().getString(R.string.production_url);
		return mContext.getResources().getString(R.string.developing_url);
	}

	public static String getLoginUrl(Context mContext) {
		return getBaseUrl(mContext) + "user/login";
	}

	public static String getSignUp(Context mContext) {
		return getBaseUrl(mContext) + "user/signUp";
	}

	public static String getInstituteUrl(Context mContext) {
		return getBaseUrl(mContext) + "institute/all";
	}

	public static String getJobListUrl(Context mContext) {
		return getBaseUrl(mContext) + "jobs/list";
	}

	public static String getPrepareUrl(Context mContext) {
		return getBaseUrl(mContext) + "jobs/prepare";
	}

	public static String getcreateJobUrl(Context mContext) {
		return getBaseUrl(mContext) + "jobs/create";
	}

	public static String getJobDetailsUrl(Context mContext, String jobID) {
		return getBaseUrl(mContext) + "jobs/" + jobID;
	}

	public static String getJobSyncCountUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/post/sync";
	}

	public static String getJobHidetUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/react";
	}

	public static String getMyPostJobUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/myposts";
	}

	public static String getImportantmailUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/imp";
	}

	public static String getCommentPostUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/comment";
	}

	public static String getOldCommentsUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/comment/all";
	}

	public static String getReverseProcePostUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/react/reverse";
	}

	/*
	 * Job Filter Data
	 */
	public static String getFilterJobUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/filters/all";
	}

	public static String getFilterUrl(Context mContext) {
		return getBaseUrl(mContext) + "jobs/list?";
	}

	public static String getVerifyUrl(Context mContext) {
		return getBaseUrl(mContext) + "user/verify";
	}

	public static String getRegenerateTokenUrl(Context mContext) {
		return getBaseUrl(mContext) + "user/generate/token";
	}

	public static String getJobEditUrl(Context mContext) {
		return getBaseUrl(mContext) + "jobs/edit";
	}

	public static String getProfileUpdateUrl(Context mContext) {
		return getBaseUrl(mContext) + "user/update";
	}

	public static String getCreateCircleUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/create";
	}

	public static String getJoinCircleUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/join";
	}

	public static String getUnJoinCircleUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/unjoin";
	}

	public static String getUnJoinListCircleUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/all";
	}

	public static String getJoinListCircleUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/user/all";
	}

	public static String getJOBPostedProfileUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/user/profile";
	}

	public static String getCircleProfileUrl(Context mContext) {
		return getBaseUrl(mContext) + "post/circle/profile";
	}

	public static String getCircleActivateUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/activate";
	}

	public static String getCircleDeActivateUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/deactivate";
	}

	public static String getAcceptRequestUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/authorize";
	}

	public static String getRejectRequestUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/authorize/revoke";
	}

	public static String getAllPendingRequestUrl(Context mContext) {
		return getBaseUrl(mContext) + "circle/requests";
	}

	public static String getForGotPasswordUrl(Context mContext) {
		return getBaseUrl(mContext) + "user/forgot/password";
	}

	public static String getResetPasswordUrl(Context mContext) {
		return getBaseUrl(mContext) + "user/change/password";
	}

	// News Section
	public static String getNewsListUrl(Context mContext) {
		return getBaseUrl(mContext) + "feed/list";
	}

	public static String getPrepareNewsUrl(Context mContext) {
		return getBaseUrl(mContext) + "feed/prepare";
	}

	public static String getCreateNewsUrl(Context mContext) {
		return getBaseUrl(mContext) + "feed/create";
	}

	public static String getGetNewsfeedUrl(Context mContext, String postId) {
		return getBaseUrl(mContext) + "feed/" + postId;
	}

	public static String getEditNewsUrl(Context mContext) {
		return getBaseUrl(mContext) + "feed/edit";
	}

	public static String getSearchURL(Context mContext) {
		return getBaseUrl(mContext) + "search/all";
	}

	public static String getPreparClassifiedeUrl(Context mContext) {
		return getBaseUrl(mContext) + "classified/prepare";
	}

	public static String getCreateClassifiedeUrl(Context mContext) {
		return getBaseUrl(mContext) + "classified/create";
	}

	public static String getEditClassifiedeUrl(Context mContext) {
		return getBaseUrl(mContext) + "classified/edit";
	}

	public static String getGetClassifiedDetailsUrl(Context mContext, String postId) {
		return getBaseUrl(mContext) + "classified/" + postId;
	}

	public static String getGetClassifiedListUrl(Context mContext) {
		return getBaseUrl(mContext) + "classified/list";
	}

}
