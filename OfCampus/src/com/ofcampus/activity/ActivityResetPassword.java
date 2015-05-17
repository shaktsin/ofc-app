/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
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
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.ResetPaswordParser;
import com.ofcampus.parser.ResetPaswordParser.ResetPaswordParserInterface;

public class ActivityResetPassword extends ActionBarActivity implements OnClickListener {

	private EditText edt_password, edt_retypepassword;
	private Context context;
	private String Auth = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword);

		context = ActivityResetPassword.this;
		Auth = UserDetails.getLoggedInUser(context).getAuthtoken();
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Reset Password");
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
		case R.id.resetpass_btn_reset:
			Util.HideKeyBoard(context, v);
			ResetPasswordEvent();
			break;
		default:
			break;
		}
	}

	/**
	 * Initialize all the view id here,those are include in the layout.
	 */
	private void initilize() {
		edt_password = (EditText) findViewById(R.id.resetpass_edt_password);
		edt_retypepassword = (EditText) findViewById(R.id.resetpass_edt_retppassword);

		((TextView) findViewById(R.id.resetpass_btn_reset)).setOnClickListener(this);
	}

	/**
	 * After click on Login button this event run.
	 */
	private void ResetPasswordEvent() {
		String password = edt_password.getText().toString();
		String retyppassword = edt_retypepassword.getText().toString();

		if (password.length() == 0) {
			Util.ShowToast(context, getResources().getString(R.string.resetpassword_enterpassword));
			return;
		}

		if (retyppassword.length() == 0) {
			Util.ShowToast(context, getResources().getString(R.string.resetpassword_retypepassword));
			return;
		}

		if (!retyppassword.equalsIgnoreCase(password)) {
			Util.ShowToast(context, getResources().getString(R.string.resetpassword_validation));
			return;
		}

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		ResetPaswordParser mParser = new ResetPaswordParser();
		mParser.setResetpaswordparserinterface(new ResetPaswordParserInterface() {

			@Override
			public void OnSuccess() {
				Util.ShowToast(context, "Successfully reset your password");
				onBackPressed();
			}

			@Override
			public void OnError() {

			}
		});

		mParser.parse(context, mParser.getBody(password, retyppassword), Auth);

	}
}