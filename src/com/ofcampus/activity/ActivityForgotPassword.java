package com.ofcampus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.parser.ForgotPasswordParser;
import com.ofcampus.parser.ForgotPasswordParser.ForgotPasswordParserInterface;

public class ActivityForgotPassword extends ActionBarActivity implements OnClickListener {

	private EditText edt_email;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgotpassword);

		context = ActivityForgotPassword.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Forgot Password");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initilize();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forgotpasswrd_btn_Submit:
			Util.HideKeyBoard(context, v);
			ForgotPasswordEvent(); 
			break;
		default:
			break;
		}
	}

	/**
	 * Initialize all the view id here,those are include in the layout.
	 */
	private void initilize() {
		edt_email = (EditText) findViewById(R.id.forgotpasswrd_edt_email);
		((TextView) findViewById(R.id.forgotpasswrd_btn_Submit)).setOnClickListener(this);
	}

	/**
	 * After click on Login button this event run.
	 */
	private void ForgotPasswordEvent() {
		String email = edt_email.getText().toString();		
	
		if (email.length()==0) {
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
		
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg)); 
			return;
		}
		
		ForgotPasswordParser mParser=new ForgotPasswordParser();
		mParser.setForgotpasswordparserinterface(new ForgotPasswordParserInterface() {
			
			@Override
			public void OnSuccess() {
				Util.ShowToast(context, "Please check your mailbox,password has been sent."); 
				onBackPressed();
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mParser.parse(context, mParser.getBody(email)); 
		
	}
}
