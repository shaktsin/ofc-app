/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.gplushelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.Plus.Builder;
import com.google.api.services.plus.model.Person;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.ofcampus.R;
import com.ofcampus.R.style;

public class GPlusDialog extends Dialog{

	private Activity context;
	
	private final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
	
	private ProgressDialog mSpinner;
	private WebView mWebView;
	private LinearLayout mContent;
	private SharedPreferences prefs;
	private RelativeLayout bacgroundLayout;
	private Person profile = null;
	private AccessTokenResponse accessTokenResponse;
	
	
	public GPlusDialog(Context context) {
		super(context, R.style.Theme_Dialog_Translucent);
		getWindow().getAttributes().windowAnimations = style.Theme_Dialog_Translucent;
		this.context = (Activity) context;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSpinner = new ProgressDialog(context);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setCancelable(false);
		mSpinner.setMessage("Loading...");

		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);
		setBackground();
		
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		int w = display.getWidth();
		addContentView(mContent, new FrameLayout.LayoutParams(w,ViewGroup.LayoutParams.FILL_PARENT));
	}
	private void setBackground() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Drawable bg = getContext().getResources().getDrawable(
				R.drawable.roundedshapedialogfortwitter);
		bacgroundLayout = new RelativeLayout(getContext());
		bacgroundLayout.setLayoutParams(FILL);
		bacgroundLayout.setGravity(Gravity.FILL_VERTICAL);
		bacgroundLayout.setBackgroundDrawable(bg);

		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.clearCache(true);
		mWebView.clearFormData();
		mWebView.clearHistory();
		
		String googleAuthorizationRequestUrl = new GoogleAuthorizationRequestUrl(
				GPlusCredentialStore.CLIENT_ID,
				GPlusCredentialStore.REDIRECT_URI,
				GPlusCredentialStore.SCOPE).build();
		mWebView.loadUrl(googleAuthorizationRequestUrl);
		
		mWebView.setWebViewClient(new GPlusWebClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		RelativeLayout.LayoutParams FILL_ = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		FILL_.addRule(RelativeLayout.BELOW, 1);
		mWebView.setLayoutParams(FILL_);
		bacgroundLayout.addView(mWebView);
		mContent.addView(bacgroundLayout);
	}
	
	
	private class GPlusWebClient extends WebViewClient {


		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (mSpinner!=null && !mSpinner.isShowing()) {
				mSpinner.show();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (url.startsWith(GPlusCredentialStore.REDIRECT_URI)) {
				try {
					if (url.indexOf("code=") != -1) {
						String code = url.substring(GPlusCredentialStore.REDIRECT_URI.length() + 7, url.length());
						dismiss();
						
						if (mSpinner!=null && mSpinner.isShowing()) {
							mSpinner.cancel();
						}
						
						GPlusProfileDataAsync mGPlusProfileDataAsync = new GPlusProfileDataAsync(code); 
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							mGPlusProfileDataAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							mGPlusProfileDataAsync.execute();
						}

						mWebView.clearHistory();
						mWebView.clearView();
						mWebView.destroy();

					} else if (url.indexOf("error=") != -1) {
						mWebView.setVisibility(View.INVISIBLE);
						GPlusCredentialStore.getInstance(prefs).clearCredentials();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}else {
				if (mSpinner!=null && mSpinner.isShowing()) {
					mSpinner.cancel();
				}
			}
		}
	}
	
	
	
	public class GPlusProfileDataAsync extends AsyncTask<String, Integer, Long> {

		private String code;
		
		private String ID="id";
		private String EMAIL="email";
		private String VERIFIED_EMAIL="verified_email";
		private String NAME="name";
		private String GIVEN_NAME="given_name";
		private String FAMILY_NAME="family_name";
		private String LINK="link";
		private String PICTURE="picture";
		private String GENDER="gender";
		
		private GPlusUser mUser;
		
		
		public GPlusProfileDataAsync(String code) {
			this.code = code;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mSpinner!=null && !mSpinner.isShowing()) {
				mSpinner.setCancelable(false);
				mSpinner.setMessage("Profile load...");
				mSpinner.show();
			}
		}

		@Override
		protected Long doInBackground(String... arg0) {
			try {
				accessTokenResponse = new GoogleAuthorizationCodeGrant(
						new NetHttpTransport(), new JacksonFactory(),
						GPlusCredentialStore.CLIENT_ID,
						GPlusCredentialStore.CLIENT_SECRET, code,
						GPlusCredentialStore.REDIRECT_URI)
						.execute();

				JsonFactory jsonFactory = new JacksonFactory();
				HttpTransport transport = new NetHttpTransport();
				GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(
						accessTokenResponse.accessToken, transport,
						jsonFactory,
						GPlusCredentialStore.CLIENT_ID,
						GPlusCredentialStore.CLIENT_SECRET,
						accessTokenResponse.refreshToken);

				Builder b = Plus.builder(transport, jsonFactory).setApplicationName("Simple-Google-Plus/1.0");
				b.setHttpRequestInitializer(accessProtectedResource);

				Plus plus = b.build();
				profile = plus.people().get("me").execute();

				JSONObject objJson = null;
				try {
					objJson = new JSONObject(profile.getName().toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestProperty("Authorization", "Bearer "+ accessTokenResponse.accessToken);
				String content = CharStreams.toString(new InputStreamReader(urlConnection.getInputStream(), Charsets.UTF_8));
				JSONObject userObj =	new JSONObject(content);
				if (userObj!=null) {
					mUser=new GPlusUser();
					mUser.setID(getJsonValue(userObj, ID));
					mUser.setEmail(getJsonValue(userObj, EMAIL));
					mUser.setVerified_email(getJsonValue(userObj, VERIFIED_EMAIL));
					mUser.setName(getJsonValue(userObj, NAME));
					mUser.setGiven_name(getJsonValue(userObj, GIVEN_NAME));
					mUser.setFamily_name(getJsonValue(userObj, FAMILY_NAME));
					mUser.setLink(getJsonValue(userObj, LINK));
					mUser.setPicture(getJsonValue(userObj, PICTURE));
					mUser.setGender(getJsonValue(userObj, GENDER));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Long result) {

			super.onPostExecute(result);
			if (mSpinner!=null && mSpinner.isShowing()) {
				mSpinner.cancel();
			}
			if (mUser!=null && mUser.getEmail()!=null && !mUser.getEmail().equals("") ) {
				if (gplusdialoglistner!=null) {
					gplusdialoglistner.OnSuccess(mUser);
				}
			} else {
				if (gplusdialoglistner!=null) {
					gplusdialoglistner.OnProfileLoadError();
				}
			}
		}
	}
	
	public String getJsonValue(JSONObject jsObject, String Key) {
		String value = "";
		try {
			if (jsObject.has(Key)) {
				value = jsObject.getString(Key);
			} else {
				value = "";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
	public GPlusDialogListner gplusdialoglistner;
	
	public GPlusDialogListner getGplusdialoglistner() {
		return gplusdialoglistner;
	}

	public void setGplusdialoglistner(GPlusDialogListner gplusdialoglistner) {
		this.gplusdialoglistner = gplusdialoglistner;
	}

	public interface GPlusDialogListner {
		public void OnSuccess(GPlusUser mUser);
		public void OnPageLoadError();
		public void OnProfileLoadError();
	} 
	
	public static void ShowAlert(Context mContext,String msg){
		Toast mToast=Toast.makeText(mContext, msg, 200);
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mToast.show();
	}

}
