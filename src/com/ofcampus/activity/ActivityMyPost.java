package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.ofcampus.R;
import com.ofcampus.adapter.MyPostListAdapter;
import com.ofcampus.adapter.MyPostListAdapter.MyPostListAdapterInterface;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.MyPostParser;
import com.ofcampus.parser.MyPostParser.MyPostParserInterface;

public class ActivityMyPost extends ActionBarActivity implements MyPostListAdapterInterface{
	
	private ListView mypostList;
	private MyPostListAdapter myPostListAdapter;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mypost);

		context=ActivityMyPost.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("My Posts");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
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
	public void convertViewOnClick(JobDetails mJobDetails) {
		
	}

	@Override
	public void firstIDAndlastID(String fstID, String lstID) {
		
	}
	
	private void initiliz(){
		mypostList=(ListView)findViewById(R.id.activity_home_myPostlist);
		myPostListAdapter=new MyPostListAdapter(context, new ArrayList<JobDetails>());
		myPostListAdapter.setMypostlistadapterinterface(this);
		mypostList.setAdapter(myPostListAdapter);
	}
	
	private void loadMyPostData(){
		MyPostParser myPostParser=new MyPostParser();
		myPostParser.setMypostparserinterface(new MyPostParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<JobDetails> mJobList) {
				if (mJobList!=null && mJobList.size()>=1) {
					myPostListAdapter.refreshData(mJobList);
				}
				
			}
			
			@Override
			public void OnError() {
				
			}
		});
		myPostParser.parse(context, myPostParser.getBody(), UserDetails.getLoggedInUser(context).getAuthtoken());
		
	}

	
}