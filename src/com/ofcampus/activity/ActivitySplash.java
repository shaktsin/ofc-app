package com.ofcampus.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.Util;
import com.ofcampus.R;
import com.ofcampus.model.InstituteDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.InstituteParser;
import com.ofcampus.parser.InstituteParser.InstituteParserInterface;

public class ActivitySplash extends Activity {

	private Context context;
	private boolean isMoveTONextScreen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		context=ActivitySplash.this;
		checkIsUserAlreadyLogedin();
	}
	
	private void checkIsUserAlreadyLogedin(){
		UserDetails mUserDetails =UserDetails.getLoggedInUser(context);
		if (mUserDetails!=null && mUserDetails.getAuthtoken()!=null && !mUserDetails.getAuthtoken().equals("")) {
			moveToHomeScreen();
		}else {
			fatchInstituteData();
		}
	}

	/**
	 * Delay function for waiting in splash screen for some time.
	 */
	private void SplashDelay() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (isMoveTONextScreen) {
					moveToNextScreen();
				}
			}
		}, Util.delaytime);
	}
	
	
	private void fatchInstituteData(){
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg)); 
			return;
		}
		
		InstituteParser mInstituteParser=new InstituteParser();
		mInstituteParser.setInstituteparserinterface(new InstituteParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<InstituteDetails> institutes) {
				if (institutes!=null && institutes.size()>=1) {
					((OfCampusApplication)getApplication()).institutes_=institutes;
					isMoveTONextScreen=true;
					SplashDelay();
				}
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mInstituteParser.parse(context);
	}

	
	private void moveToNextScreen(){
		startActivity(new Intent(ActivitySplash.this,ActivityInstituteList.class));
		overridePendingTransition(0, 0);
		finish();
	}
	
	private void moveToHomeScreen(){
		startActivity(new Intent(ActivitySplash.this,ActivityHome.class));
		overridePendingTransition(0, 0);
		finish();
	}
}
