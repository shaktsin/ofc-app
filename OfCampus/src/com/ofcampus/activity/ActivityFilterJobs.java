/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.FilterJobsAdapter;
import com.ofcampus.adapter.FilterJobsAdapter.filterListInterface;
import com.ofcampus.databasehelper.ImportantJobTable;
import com.ofcampus.databasehelper.JOBListTable;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.PostJobHideMarkedParser;
import com.ofcampus.parser.PostUnHideUnImpParser;
import com.ofcampus.parser.PostJobHideMarkedParser.PostJobHideMarkedParserInterface;
import com.ofcampus.parser.PostUnHideUnImpParser.PostUnHideUnImpParserInterface;

public class ActivityFilterJobs extends ActionBarActivity implements filterListInterface {

	private ListView mypostList;
	private FilterJobsAdapter mFilterJobsAdapter;
	public ArrayList<JobDetails> filterJobs_;
	private Context context;
	private String title = "Filter";
	private String token = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filterjobs);

		context = ActivityFilterJobs.this;
		try {
			title = getIntent().getExtras().getString(Util.BUNDLE_KEY[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(title);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initiliz();
		loadMyPostData();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		returnResult();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			returnResult();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initiliz() {
		mypostList = (ListView) findViewById(R.id.activity_filter_joblist);
		mFilterJobsAdapter = new FilterJobsAdapter(context, new ArrayList<JobDetails>());
		mFilterJobsAdapter.setFilterlistinterface(this);
		mypostList.setAdapter(mFilterJobsAdapter);
	}

	private void loadMyPostData() {
		token = UserDetails.getLoggedInUser(context).getAuthtoken();
		filterJobs_ = ((OfCampusApplication) getApplication()).filterJobs;
		mFilterJobsAdapter.refreshData(filterJobs_);
	}

	@Override
	public void impClieckEvent(JobDetails mJobDetails) {
		HideCalling(mJobDetails, 2);
	}

	@Override
	public void unimpClieckEvent(JobDetails mJobDetails) {
		UnImptCalling(mJobDetails, 11);
	}

	@Override
	public void likeCliekEvent(JobDetails mJobDetails) {
		HideCalling(mJobDetails, 13);
	}

	private void HideCalling(final JobDetails mJobDetails, final int state) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		PostJobHideMarkedParser markedParser = new PostJobHideMarkedParser();
		markedParser.setPostjobhidemarkedparserinterface(new PostJobHideMarkedParserInterface() {

			@Override
			public void OnSuccess() {
				if (state == 2) {
					ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
					mJobDetails.important = 1;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).inserJobData(mJobDetails);
					mFilterJobsAdapter.importantJob(mJobDetails);
				} else if (state == 11) {
					ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
					mJobDetails.important = 0;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
					mFilterJobsAdapter.unimportantJob(mJobDetails);
				} else if (state == 13) {
					mFilterJobsAdapter.likRefreshJob(mJobDetails);
				}
				isDataModify = true;
			}

			@Override
			public void OnError() {

			}
		});
		markedParser.parse(context, markedParser.getBody(state + "", mJobDetails.getPostid()), token);
	}

	private void UnImptCalling(final JobDetails mJobDetails, final int state) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		PostUnHideUnImpParser PostUnHideUnImpParser = new PostUnHideUnImpParser();
		PostUnHideUnImpParser.setPostunhideunimpparserinterface(new PostUnHideUnImpParserInterface() {

			@Override
			public void OnSuccess() {
				ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
				mJobDetails.important = 0;
				arr.add(mJobDetails);
				JOBListTable.getInstance(context).inserJobData(arr);
				ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
				mFilterJobsAdapter.unimportantJob(mJobDetails);
				isDataModify = true;
			}

			@Override
			public void OnError() {

			}
		});
		PostUnHideUnImpParser.parse(context, PostUnHideUnImpParser.getBody(state + "", mJobDetails.getPostid()), token);
	}

	private boolean isDataModify = false;

	private void returnResult() {
		try {
			Intent intent = new Intent();
			Bundle mBundle = new Bundle();
			mBundle.putBoolean("isDataModify", isDataModify);
			intent.putExtras(mBundle);
			setResult(RESULT_OK, intent);
			overridePendingTransition(0, 0);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}