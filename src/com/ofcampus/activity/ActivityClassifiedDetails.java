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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.CommentRecycleAdapter;
import com.ofcampus.adapter.CommentRecycleAdapter.commentItemClickListner;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.ClassifiedDetailsParser;
import com.ofcampus.parser.ClassifiedDetailsParser.ClassifiedDetailsParserInterface;
import com.ofcampus.parser.CommentAllParser;
import com.ofcampus.parser.CommentAllParser.CommentAllParserInterface;
import com.ofcampus.parser.CommentPostParser;
import com.ofcampus.parser.CommentPostParser.CommentPostParserInterface;

public class ActivityClassifiedDetails extends ActionBarActivity implements OnClickListener, commentItemClickListner {

	public static int REQUEST_CODEFOREDITCLASSIFIED = 20001;
	public static String DATAMODIFY = "DataModify";

	private ListView commentListView;
	private CommentRecycleAdapter mCommentRecycleAdapter;
	private JobDetails mJobDetails;
	private UserDetails mUserDetails;
	private String JObID = "";
	private EditText edt_comment;
	private RelativeLayout rel_comnt;
	private Context mContext;

	private String toolHeaderTitle = "";
	private boolean isFromDetails = false;
	public boolean fromMYPost_ = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);

		loadBundleValue();
		mContext = ActivityClassifiedDetails.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle(toolHeaderTitle);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		fromMYPost_ = ((OfCampusApplication) getApplication()).fromMYPost;
		initilize();
		loadData();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		((OfCampusApplication) mContext.getApplicationContext()).fromMYPost = false;
		overridePendingTransition(0, 0);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (fromMYPost_) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_postedit, menu);
			return super.onCreateOptionsMenu(menu);
		} else {
			return true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_editpost:
			((OfCampusApplication) getApplication()).jobdetails = mJobDetails;
			Intent mIntent = new Intent(ActivityClassifiedDetails.this, ActivityEditClassified.class);
			startActivityForResult(mIntent, REQUEST_CODEFOREDITCLASSIFIED);
			overridePendingTransition(0, 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_comment_btnsend:
			commentPostProcess();
			Util.HideKeyBoard(mContext, v);
			break;

		default:
			break;
		}
	}

	@Override
	public void loadoldData(String commentId) {
		reloadOldData(commentId);
	}

	@Override
	public void commentbuttonCliek() {
		if (!isFromDetails) {
			return;
		}
		if (rel_comnt.getVisibility() == View.GONE) {
			rel_comnt.setVisibility(View.VISIBLE);
			edt_comment.setFocusable(true);
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODEFOREDITCLASSIFIED && resultCode == RESULT_OK && data != null) {
			boolean isModify = data.getExtras().getBoolean(DATAMODIFY);
			if (isModify) {
				loadData();
			}
		}
	}

	private void loadBundleValue() {
		try {
			toolHeaderTitle = getIntent().getExtras().getString(Util.BUNDLE_KEY[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		isFromDetails = toolHeaderTitle.contains("Details") ? true : false;
	}

	private void initilize() {
		((ImageView) findViewById(R.id.activity_comment_btnsend)).setOnClickListener(this);
		rel_comnt = (RelativeLayout) findViewById(R.id.activity_comment_rel_comnt);
		edt_comment = (EditText) findViewById(R.id.activity_comment_edt_cmnt);
		commentListView = (ListView) findViewById(R.id.activity_comment_comntlist);
		mCommentRecycleAdapter = new CommentRecycleAdapter(mContext, new ArrayList<JobDetails>());
		mCommentRecycleAdapter.setCommentitemclicklistner(this);
		commentListView.setAdapter(mCommentRecycleAdapter);
	}

	private void loadData() {

		mUserDetails = UserDetails.getLoggedInUser(mContext);
		mJobDetails = ((OfCampusApplication) getApplication()).jobdetails;
		JObID = mJobDetails.getPostid();

		rel_comnt.setVisibility(isFromDetails ? View.GONE : View.VISIBLE);

		ArrayList<JobDetails> arrayList = new ArrayList<JobDetails>();
		if (mJobDetails != null) {
			arrayList.add(mJobDetails);
			mCommentRecycleAdapter.refreshView(arrayList, 0);
		}

		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext, getResources().getString(R.string.internetconnection_msg));
			// onBackPressed();
			return;
		}

		ClassifiedDetailsParser mDetailsParser = new ClassifiedDetailsParser();
		mDetailsParser.setClassifieddetailsparserinterface(new ClassifiedDetailsParserInterface() {

			@Override
			public void OnSuccess(ArrayList<JobDetails> arrayJobsComment, int totalCommentCount) {
				if (arrayJobsComment != null && arrayJobsComment.size() >= 1) {
					mJobDetails = arrayJobsComment.get(0);
					mCommentRecycleAdapter.refreshView(arrayJobsComment, totalCommentCount);
				}
			}

			@Override
			public void OnError() {

			}
		});
		mDetailsParser.parse(mContext, JObID, mUserDetails.getAuthtoken());

	}

	private void commentPostProcess() {
		String comment = edt_comment.getText().toString();

		if (comment != null && comment.length() == 0) {
			Util.ShowToast(mContext, "Enter comment and then click on send button.");
			return;
		}

		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext, getResources().getString(R.string.internetconnection_msg));
			onBackPressed();
			return;
		}

		CommentPostParser mCommentPostParser = new CommentPostParser();
		mCommentPostParser.setCommentpostparserinterface(new CommentPostParserInterface() {

			@Override
			public void OnSuccess(JobDetails mJobDetails) {
				if (mJobDetails != null) {
					mCommentRecycleAdapter.refreshView(mJobDetails);
					edt_comment.setText("");
					Util.ShowToast(mContext, "Comment Posted successfully.");
					commentListView.setSelection(commentListView.getAdapter().getCount() - 1);
				}
			}

			@Override
			public void OnError() {

			}
		});

		mCommentPostParser.parse(mContext, mCommentPostParser.getBody("4", JObID, comment), mUserDetails.getAuthtoken());

	}

	private void reloadOldData(String commentID) {
		if (commentID != null && commentID.equals("")) {
			mCommentRecycleAdapter.loadOldCommentError();
			return;
		}

		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext, getResources().getString(R.string.internetconnection_msg));
			onBackPressed();
			return;
		}

		CommentAllParser mAllParser = new CommentAllParser();
		mAllParser.setCommentallparserinterface(new CommentAllParserInterface() {

			@Override
			public void OnSuccess(ArrayList<JobDetails> arrayJobsComment) {
				if (arrayJobsComment != null && arrayJobsComment.size() >= 1) {
					mCommentRecycleAdapter.loadOldCommentView(arrayJobsComment);
				}
			}

			@Override
			public void OnError() {
				mCommentRecycleAdapter.loadOldCommentError();
			}
		});
		mAllParser.parse(mContext, mAllParser.getBody("", JObID, commentID), mUserDetails.getAuthtoken());
	}
}