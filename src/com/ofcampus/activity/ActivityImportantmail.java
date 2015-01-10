package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.ofcampus.R;
import com.ofcampus.adapter.ImportantMailListAdapter;
import com.ofcampus.adapter.ImportantMailListAdapter.ImportantMailListAdapterInterface;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.ImportantMailParser;
import com.ofcampus.parser.ImportantMailParser.ImportantMailParserInterface;

public class ActivityImportantmail  extends ActionBarActivity implements ImportantMailListAdapterInterface{
	
	private ListView mypostList;
	private ImportantMailListAdapter mImportantMailListAdapter;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_importantmail);

		context=ActivityImportantmail.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Important Mail");
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
		mypostList=(ListView)findViewById(R.id.activity_home_importantmaillist);
		mImportantMailListAdapter=new ImportantMailListAdapter(context, new ArrayList<JobDetails>());
		mImportantMailListAdapter.setImportantmaillistadapterinterface(this);
		mypostList.setAdapter(mImportantMailListAdapter);
	}
	
	private void loadMyPostData(){
		ImportantMailParser mImportantMailParser=new ImportantMailParser();
		mImportantMailParser.setImportantmailparserinterface(new ImportantMailParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<JobDetails> mJobList) {
				if (mJobList!=null && mJobList.size()>=1) {
					mImportantMailListAdapter.refreshData(mJobList);
				}
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mImportantMailParser.parse(context, mImportantMailParser.getBody(), UserDetails.getLoggedInUser(context).getAuthtoken());
		
	}

	
}