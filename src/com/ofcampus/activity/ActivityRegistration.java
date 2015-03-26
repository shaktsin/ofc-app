/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.Util.userType;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.RegistrationParser;
import com.ofcampus.parser.RegistrationParser.RegstrationInterface;

public class ActivityRegistration extends ActionBarActivity implements OnClickListener{

	
	private EditText edt_fstname,edt_lstname,edt_accname,edt_email,edt_pass,edt_confpass,edt_verifyCode;
	private Context context;
	private TextView rd_female,rd_male;
	private boolean isMaleSeleted=true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		context=ActivityRegistration.this;
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
		case R.id.registration_btn_registration:
			Util.HideKeyBoard(context, v);
			RegistrationEvent();
			break;

		case R.id.registration_rd_female:
			rd_female.setSelected(true);
			rd_male.setSelected(false);
			isMaleSeleted=false;
			break;
		case R.id.registration_rd_male:
			rd_female.setSelected(false);
			rd_male.setSelected(true);
			isMaleSeleted=true;
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * Initialize all the view id here,those are include in the layout.
	 */
	private void initilize() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("SignUp");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		edt_fstname=(EditText) findViewById(R.id.registration_edt_firstname);
		edt_lstname=(EditText) findViewById(R.id.registration_edt_lastname);
		edt_accname=(EditText) findViewById(R.id.registration_edt_accname);
		edt_email=(EditText) findViewById(R.id.registration_edt_email);
		edt_pass=(EditText) findViewById(R.id.registration_edt_password);
		edt_confpass=(EditText) findViewById(R.id.registration_edt_conpassword);
		
		edt_verifyCode=(EditText) findViewById(R.id.registration_edt_verifyCode);
		
		rd_female=(TextView) findViewById(R.id.registration_rd_female);
		rd_male=(TextView) findViewById(R.id.registration_rd_male);
		((TextView) findViewById(R.id.registration_btn_registration)).setOnClickListener(this);
		rd_female.setOnClickListener(this);
		rd_male.setOnClickListener(this);
		rd_male.setSelected(true);
	}
	
	/**
	 * After click on Sign Up button this event run.
	 */
	private void RegistrationEvent(){
		String firstName = edt_fstname.getText().toString(); 
		String lastName = edt_lstname.getText().toString();
//		String accountName = edt_accname.getText().toString();
		String email = edt_email.getText().toString();
		String password = edt_pass.getText().toString();
		String confpass = edt_confpass.getText().toString(); 
//		int gender=(isMaleSeleted)?0:1;
//		String Gender=gender+"";
		
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg)); 
			return;
		}
		
		if (firstName.length()==0) {
			Util.ShowToast(context, getResources().getString(R.string.registration_txt_error_firstname)); 
			return;
		}
		
		if (lastName.length()==0) {
			Util.ShowToast(context, getResources().getString(R.string.registration_txt_error_lasttname)); 
			return;
		}
		
//		if (accountName.length()==0) {
//			Util.ShowToast(context, getResources().getString(R.string.registration_txt_error_accname)); 
//			return;
//		}
		
		if (email.length()==0) {
			Util.ShowToast(context, getResources().getString(R.string.registration_txt_error_email)); 
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
		
		if (password.length()==0) {
			Util.ShowToast(context, getResources().getString(R.string.registration_txt_error_password)); 
			return;
		}
		
		if (confpass.length()==0) {
			Util.ShowToast(context, getResources().getString(R.string.registration_txt_error_repassword)); 
			return;
		}
		
		if (!password.equals(confpass)) {
			Util.ShowToast(context, getResources().getString(R.string.registration_txt_error_notmatched)); 
			return;
		}
		
		RegistrationParser mParser=new RegistrationParser();
//		JSONObject postBody = mParser.getjsonBody(firstName, lastName, accountName, email, password, instituteID, Gender, "false","false",userType.Normal);
		JSONObject postBody = mParser.getjsonBody(firstName, lastName, "", email, password, "", "", "false","false",userType.Normal);
		mParser.setRegstrationinterface(new RegstrationInterface() {
			
			@Override
			public void OnSuccess(UserDetails mDetails_) {
				((OfCampusApplication)getApplication()).mDetails=mDetails_;
				moveVerifyCode();
			}
			
			@Override
			public void OnError() {
				
			}
		}); 
		mParser.parse(context, postBody);
	}


	
	private void moveVerifyCode(){
		startActivity(new Intent(ActivityRegistration.this,ActivityVerifyCode.class));
		overridePendingTransition(0, 0);
		finish();
	}
}
