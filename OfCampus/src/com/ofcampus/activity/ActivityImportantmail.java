/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.ImportantMailListAdapter;
import com.ofcampus.adapter.ImportantMailListAdapter.ImportantMailListAdapterInterface;
import com.ofcampus.databasehelper.ImportantJobTable;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.ImportantMailParser;
import com.ofcampus.parser.ImportantMailParser.ImportantMailParserInterface;
import com.ofcampus.parser.PostUnHideUnImpParser;
import com.ofcampus.parser.PostUnHideUnImpParser.PostUnHideUnImpParserInterface;

public class ActivityImportantmail extends ActionBarActivity implements ImportantMailListAdapterInterface {

	private ListView mypostList;
	private ImportantMailListAdapter mImportantMailListAdapter;
	private Context context;
	private String token = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_importantmail);

		context = ActivityImportantmail.this;
		token = UserDetails.getLoggedInUser(context).getAuthtoken();
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Bookmarked Posts");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initiliz();
		loadMyPostData();
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
	public void firstIDAndlastID(String fstID, String lstID) {

	}

	@Override
	public void unIportantEvent(JobDetails mJobDetails, int postion) {
		UnImptCalling(mJobDetails, 11, postion);
	}

	private void initiliz() {
		mypostList = (ListView) findViewById(R.id.activity_home_importantmaillist);
		mImportantMailListAdapter = new ImportantMailListAdapter(context, new ArrayList<JobDetails>());
		mImportantMailListAdapter.setImportantmaillistadapterinterface(this);
		mypostList.setAdapter(mImportantMailListAdapter);
	}

	private void loadMyPostData() {

		if (Util.hasConnection(context)) {
			ImportantMailParser mImportantMailParser = new ImportantMailParser();
			mImportantMailParser.setImportantmailparserinterface(new ImportantMailParserInterface() {

				@Override
				public void OnSuccess(ArrayList<JobDetails> mJobList) {
					if (mJobList != null && mJobList.size() >= 1) {
						mImportantMailListAdapter.refreshData(mJobList);
					}
				}

				@Override
				public void OnError() {

				}
			});
			mImportantMailParser.parse(context, mImportantMailParser.getBody(), UserDetails.getLoggedInUser(context).getAuthtoken());
		} else {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			new loadOffLineData().execute();
		}
	}

	private void UnImptCalling(final JobDetails mJobDetails, final int state, final int postion) {
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		PostUnHideUnImpParser PostUnHideUnImpParser = new PostUnHideUnImpParser();
		PostUnHideUnImpParser.setPostunhideunimpparserinterface(new PostUnHideUnImpParserInterface() {

			@Override
			public void OnSuccess() {
				mJobDetails.important = 0;
				ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
				mImportantMailListAdapter.unImportantJobRemove(mJobDetails, postion);
				((OfCampusApplication) getApplication()).isHidePostModify = true;
			}

			@Override
			public void OnError() {

			}
		});
		PostUnHideUnImpParser.parse(context, PostUnHideUnImpParser.getBody(state + "", mJobDetails.getPostid()), token);
	}

	/**
	 * Offline Data from Data Base.
	 */
	private class loadOffLineData extends AsyncTask<Void, Void, Void> {
		private ArrayList<JobDetails> arrayJob;

		@Override
		protected Void doInBackground(Void... params) {
			arrayJob = ImportantJobTable.getInstance(context).fatchImpJobData();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (arrayJob != null && arrayJob.size() >= 1) {
				mImportantMailListAdapter.refreshData(arrayJob);
			}
		}

	}

}