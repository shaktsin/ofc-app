package com.ofcampus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;

public class ActivitySplash extends Activity {

	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		context=ActivitySplash.this;
		checkIsUserAlreadyLogedin();
	}
	
	
	/**
	 * Check is user already Log in or not.
	 */
	private void checkIsUserAlreadyLogedin(){
		UserDetails mUserDetails =UserDetails.getLoggedInUser(context);
		if (mUserDetails!=null && mUserDetails.getAuthtoken()!=null && !mUserDetails.getAuthtoken().equals("")) {
			moveToHomeScreen();
		}else {
			SplashDelay();
		}
	}

	/**
	 * Delay function for waiting in splash screen for some time.
	 */
	private void SplashDelay() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				moveToNextScreen();
			}
		}, Util.delaytime);
	}
	
	/**
	 * Move to Login Activity If New User.
	 */
	
	private void moveToNextScreen(){
		startActivity(new Intent(ActivitySplash.this,ActivityLogin.class));
		overridePendingTransition(0, 0);
		finish();
	}
	
	/**
	 * Move to Login Activity If Exist User.
	 */
	private void moveToHomeScreen(){
		startActivity(new Intent(ActivitySplash.this,ActivityHome.class));
		overridePendingTransition(0, 0);
		finish();
	}
}
