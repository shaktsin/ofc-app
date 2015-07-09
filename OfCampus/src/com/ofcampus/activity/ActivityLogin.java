/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.LoginParser;
import com.ofcampus.parser.LoginParser.LoginInterface;

public class ActivityLogin extends Activity implements OnClickListener {

	private LinearLayout LoginBox;
	private EditText edt_email, edt_pass;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		context = ActivityLogin.this;
		initilize();
		// edt_email.setText("dibakar@ofcampus.com");
		// edt_pass.setText("123456");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn_login:
			Util.HideKeyBoard(context, v);
			LoginEvent();
			break;
		case R.id.login_btn_signup:
			startActivity(new Intent(ActivityLogin.this, ActivityRegistration.class));
			overridePendingTransition(0, 0);
			break;
		case R.id.login_txt_forgotpass:
			startActivity(new Intent(ActivityLogin.this, ActivityForgotPassword.class));
			overridePendingTransition(0, 0);
			break;

		default:
			break;
		}
	}

	/**
	 * Initialize all the view id here,those are include in the layout.
	 */
	private void initilize() {
		LoginBox = (LinearLayout) findViewById(R.id.LoginBox);
		edt_email = (EditText) findViewById(R.id.login_edt_email);
		edt_pass = (EditText) findViewById(R.id.login_edt_passw);
		LoginBox.setVisibility(View.GONE);
		((TextView) findViewById(R.id.login_btn_login)).setOnClickListener(this);
		((TextView) findViewById(R.id.login_btn_signup)).setOnClickListener(this);
		((TextView) findViewById(R.id.login_txt_forgotpass)).setOnClickListener(this);
		gotoNext(100);
	}

	/**
	 * Login animation
	 * 
	 * @author DIBAKARMISTRY
	 * @param delaytime
	 */
	private void gotoNext(int delaytime) {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Animation animTranslate = AnimationUtils.loadAnimation(ActivityLogin.this, R.anim.translate);
				animTranslate.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation arg0) {
					}

					@Override
					public void onAnimationRepeat(Animation arg0) {
					}

					@Override
					public void onAnimationEnd(Animation arg0) {
						LoginBox.setVisibility(View.VISIBLE);
						Animation animFade = AnimationUtils.loadAnimation(ActivityLogin.this, R.anim.fade);
						LoginBox.startAnimation(animFade);
					}
				});
				TextView imgLogo = (TextView) findViewById(R.id.login_textlogo);
				imgLogo.startAnimation(animTranslate);
			}
		}, delaytime);
	}

	/**
	 * After click on Login button this event run.
	 */
	private void LoginEvent() {
		String email = edt_email.getText().toString();
		String Pass = edt_pass.getText().toString();

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		if (email.length() == 0) {
			Util.ShowToast(context, getResources().getString(R.string.login_scr_email_msg));
			return;
		}

		if (!Util.isValidEmail(email)) {
			Util.ShowToast(context, getResources().getString(R.string.login_scr_error_email_msg));
			return;
		}

		if (!Util.isValidEmail_again(email)) {
			Util.ShowToast(context, getResources().getString(R.string.login_scr_error_email_msg));
			return;
		}

		if (Pass.length() == 0) {
			Util.ShowToast(context, getResources().getString(R.string.login_scr_edtpass_msg));
			return;
		}

		LoginParser mLoginParser = new LoginParser();
		mLoginParser.setLogininterface(new LoginInterface() {

			@Override
			public void OnSuccess(UserDetails mDetails) {
				if (mDetails != null) {
					if (mDetails.isVerify()) {
						mDetails.saveInPreferense(context);
						// Util.ShowToast(context, "Login Successful");
						moveToHome();
					} else {
						((OfCampusApplication) getApplication()).mDetails = mDetails;
						verifyUser();
					}
				}
			}

			@Override
			public void OnError() {
				Util.ShowToast(context, context.getResources().getString(R.id.serever_error_msg));
			}
		});
		mLoginParser.parse(context, email, Pass);
	}

	private void moveToHome() {
		startActivity(new Intent(ActivityLogin.this, ActivityHome.class));
		overridePendingTransition(0, 0);
		finish();
	}

	private void verifyUser() {
		startActivity(new Intent(ActivityLogin.this, ActivityVerifyCode.class));
		overridePendingTransition(0, 0);
		finish();
	}
}