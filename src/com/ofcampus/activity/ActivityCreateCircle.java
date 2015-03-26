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

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CreateCircleParser;
import com.ofcampus.parser.CreateCircleParser.CreateCircleParserInterface;
import com.ofcampus.ui.CustomEditText;
import com.ofcampus.ui.CustomTextView;

public class ActivityCreateCircle extends ActionBarActivity implements OnClickListener {

	
	private static Context context;
	private static String Authtoken="";
	private CustomEditText edt_CircleName;
	private CustomTextView txt_modarator;
	private String isModarator="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createcircle);

		context = ActivityCreateCircle.this;
		
		Authtoken = UserDetails.getLoggedInUser(context).getAuthtoken();
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Create Club");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initilizView();

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
	
	private void initilizView() {
		edt_CircleName=(CustomEditText)findViewById(R.id.fragm_createcircle_edt_verifyCode);
		((CustomTextView)findViewById(R.id.fragm_createcircle_btn_submit)).setOnClickListener(this);
		txt_modarator =(CustomTextView)findViewById(R.id.fragm_createcircle_txtmodarator);
		txt_modarator.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragm_createcircle_btn_submit:
			createCircleEvent();
			break;
		case R.id.fragm_createcircle_txtmodarator:
			txt_modarator.setSelected(txt_modarator.isSelected()?false:true); 
			isModarator = (txt_modarator.isSelected())?"false":"true";
			break;

		default:
			break;
		}
	}
	
	private void createCircleEvent(){
		
		String circleName=edt_CircleName.getText().toString().trim();
		
		if (circleName!=null && circleName.equals("")) {
			Util.ShowToast(context,getResources().getString(R.string.enter_circlename));
			return;
		}
		
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		CreateCircleParser mCircleParser=new CreateCircleParser();
		mCircleParser.setCreatecircleparserinterface(new CreateCircleParserInterface() {
			
			@Override
			public void OnSuccess() {
				Util.ShowToast(context,"Succesfully Created Your Circle.");
				((OfCampusApplication)context.getApplicationContext()).isNewCircleCreated=true;
				onBackPressed();
			}
			
			@Override
			public void OnError() {
				Util.ShowToast(context,"Circle Create Error.");
			}
		});
		mCircleParser.parse(context, mCircleParser.getBody(circleName, isModarator, "3"), Authtoken);
	}
}