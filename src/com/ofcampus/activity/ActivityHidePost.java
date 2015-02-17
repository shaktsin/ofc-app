package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.HideJobListAdapter;
import com.ofcampus.adapter.HideJobListAdapter.HideJobListInterface;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.HideJobListParser;
import com.ofcampus.parser.HideJobListParser.HideJobListParserInterface;
import com.ofcampus.parser.PostUnHideUnImpParser;
import com.ofcampus.parser.PostUnHideUnImpParser.PostUnHideUnImpParserInterface;

public class ActivityHidePost extends ActionBarActivity implements HideJobListInterface{
	
	private ListView mypostList;
	private HideJobListAdapter mHideJobListAdapter; 
	private Context context;
	private String Authtoken="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mypost);

		context=ActivityHidePost.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Hide Posts");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Authtoken = UserDetails.getLoggedInUser(context).getAuthtoken();
		
		initiliz();
		loadMyPostData();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0,0);
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
	public void arrowUnHideClieckEvent(JobDetails mJobDetails) {
		HideCalling(mJobDetails,12);
	}
	
	private void initiliz(){
		mypostList=(ListView)findViewById(R.id.activity_home_myPostlist);
		mHideJobListAdapter=new HideJobListAdapter(context, new ArrayList<JobDetails>());
		mHideJobListAdapter.setHidejoblistinterface(this);
		mypostList.setAdapter(mHideJobListAdapter);
	}
	
	private void loadMyPostData(){
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		HideJobListParser mHideJobListParser=new HideJobListParser();
		mHideJobListParser.setHidejoblistparserinterface(new HideJobListParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<JobDetails> mJobList) {
				if (mJobList!=null && mJobList.size()>=1) {
					mHideJobListAdapter.refreshData(mJobList);
				}
				
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mHideJobListParser.parse(context, mHideJobListParser.getBody(), Authtoken);
		
	}
	
	private void HideCalling(final JobDetails mJobDetails, final int state){   
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		PostUnHideUnImpParser mPostUnHideUnImpParser=new PostUnHideUnImpParser();
		mPostUnHideUnImpParser.setPostunhideunimpparserinterface(new PostUnHideUnImpParserInterface() {
			
			@Override
			public void OnSuccess() {
				loadMyPostData();
			}
			
			@Override
			public void OnError() { 
				
			}
		});
		mPostUnHideUnImpParser.parse(context, mPostUnHideUnImpParser.getBody(state+"", mJobDetails.getPostid()), Authtoken);  
	}
}