package com.ofcampus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.ofcampus.R;
import com.ofcampus.model.CircleProfile;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CircleProfileParser;
import com.ofcampus.parser.CircleProfileParser.CircleProfileParserInterface;

public class ActivityCircleProfile extends ActionBarActivity {

	private Context context;
	private String authorization="",circleId="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circleprofile);

		context=ActivityCircleProfile.this;
		initilize();
	}
	
	

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, 0);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

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
	
	
	private void initilize() {
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Circle Profile");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		authorization=UserDetails.getLoggedInUser(context).getAuthtoken();
		try {
			circleId=getIntent().getExtras().getString("CircleID");
			loadData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadData(){
		
		CircleProfileParser mCircleProfileParser=new CircleProfileParser();
		mCircleProfileParser.setCircleprofileparserinterface(new CircleProfileParserInterface() {
			
			@Override
			public void OnSuccess(CircleProfile mCircleProfile) {
				Log.e("TAG", mCircleProfile.toString());
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mCircleProfileParser.parse(context, mCircleProfileParser.getBody(circleId, "0", "8"), authorization);
	}

}
