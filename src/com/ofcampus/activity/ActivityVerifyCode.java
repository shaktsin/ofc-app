package com.ofcampus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.ResendTokenCodeParser;
import com.ofcampus.parser.ResendTokenCodeParser.ResendCodeInterface;
import com.ofcampus.parser.VerifyUserParser;
import com.ofcampus.parser.VerifyUserParser.VerifyUserPsInterface;

public class ActivityVerifyCode extends Activity implements OnClickListener{

	private EditText edt_verifyCode; 
	private Context context;
	private UserDetails mUserDetails;
	private String Authtoken="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_verifycode);
		
		context=ActivityVerifyCode.this;
		initilize();
		mUserDetails=((OfCampusApplication)getApplication()).mDetails;
		if (mUserDetails!=null) {
			Authtoken = mUserDetails.getAuthtoken();
		}
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.registration_btn_Codesubmit:
			Util.HideKeyBoard(context, v);
			VerifyCodeEvent(); 
			break;
		case R.id.login_txt_forsignup:
			startActivity(new Intent(ActivityVerifyCode.this,ActivityRegistration.class));
			overridePendingTransition(0, 0);
			break;
		case R.id.registration_btn_resend:
			ResendTokenEvent();
			break;
			

		default:
			break;
		}
	}

	
	/**
	 * Initialize all the view id here,those are include in the layout.
	 */
	private void initilize() {
		edt_verifyCode=(EditText) findViewById(R.id.registration_edt_verifyCode); 
		((TextView) findViewById(R.id.registration_btn_Codesubmit)).setOnClickListener(this);
		((TextView) findViewById(R.id.registration_btn_resend)).setOnClickListener(this);
	}
	
	/**
	 * After click on Login button this event run.
	 */
	private void VerifyCodeEvent(){
		String verifyCode = edt_verifyCode.getText().toString();
		
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg)); 
			return;
		}
		
		if (verifyCode.length()==0) {
			Util.ShowToast(context, getResources().getString(R.string.login_scr_verifycode_msg)); 
			return;
		}
		
		VerifyUserParser mVerifyUserParser=new VerifyUserParser();
		mVerifyUserParser.setVerifyuserpsinterface(new VerifyUserPsInterface() {
			
			@Override
			public void OnSuccess(boolean success) {
				if (success) {
					moveToHome();
					mUserDetails.saveInPreferense(context);
				}else {
					onBackPressed();
				}
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mVerifyUserParser.parse(context, mVerifyUserParser.getparamBody(verifyCode),Authtoken);
		
	}
	
	
	private void ResendTokenEvent(){
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg)); 
			return;
		}
		
		ResendTokenCodeParser mCodeParser=new ResendTokenCodeParser();
		mCodeParser.setResendcodeinterface(new ResendCodeInterface() {
			
			@Override
			public void OnSuccess(boolean success) {
				if (success) {
					Util.ShowToast(context, "Please check your mailbox."); 
				}
				
			}
			
			@Override
			public void OnError() {
			}
		});
		mCodeParser.parse(context, Authtoken);
	}
	
	private void moveToHome(){
		startActivity(new Intent(ActivityVerifyCode.this,ActivityHome.class));
		overridePendingTransition(0, 0);
		finish();
	}
}