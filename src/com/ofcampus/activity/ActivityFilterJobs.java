package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.adapter.MyPostListAdapter;
import com.ofcampus.adapter.MyPostListAdapter.MyPostListAdapterInterface;
import com.ofcampus.model.JobDetails;

public class ActivityFilterJobs extends ActionBarActivity implements MyPostListAdapterInterface{
	
	private ListView mypostList;
	private MyPostListAdapter myPostListAdapter;
	public ArrayList<JobDetails> filterJobs_;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filterjobs);

		context=ActivityFilterJobs.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Filter Jobs");
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
		mypostList=(ListView)findViewById(R.id.activity_filter_joblist);
		myPostListAdapter=new MyPostListAdapter(context, new ArrayList<JobDetails>());
		myPostListAdapter.setMypostlistadapterinterface(this);
		mypostList.setAdapter(myPostListAdapter);
	}
	
	private void loadMyPostData(){
		filterJobs_=((OfCampusApplication)getApplication()).filterJobs;
		myPostListAdapter.refreshData(filterJobs_);
	}

	
}