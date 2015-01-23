package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.CommentRecycleAdapter;
import com.ofcampus.adapter.CommentRecycleAdapter.commentItemClickListner;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.CommentAllParser;
import com.ofcampus.parser.CommentAllParser.CommentAllParserInterface;
import com.ofcampus.parser.CommentParser;
import com.ofcampus.parser.CommentParser.CommentParserInterface;
import com.ofcampus.parser.CommentPostParser;
import com.ofcampus.parser.CommentPostParser.CommentPostParserInterface;

public class ActivityComment extends ActionBarActivity implements OnClickListener,commentItemClickListner{

    private ListView commentListView;
	private CommentRecycleAdapter mCommentRecycleAdapter;
	private JobDetails mJobDetails;
	private UserDetails mUserDetails;
	private String JObID="";
	private EditText edt_comment;
	private Context mContext;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);

		mContext=ActivityComment.this;
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("Comment");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		initilize();
		loadData();
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
	
	
	
	private void initilize() {
		((ImageView)findViewById(R.id.activity_comment_btnsend)).setOnClickListener(this);
		edt_comment=(EditText)findViewById(R.id.activity_comment_edt_cmnt);
		commentListView=(ListView)findViewById(R.id.activity_comment_comntlist);
		mCommentRecycleAdapter=new CommentRecycleAdapter(mContext, new ArrayList<JobDetails>());
		mCommentRecycleAdapter.setCommentitemclicklistner(this);
		commentListView.setAdapter(mCommentRecycleAdapter);
	}
	
	private void loadData() {

		mUserDetails = UserDetails.getLoggedInUser(mContext);
		mJobDetails = ((OfCampusApplication) getApplication()).jobdetails;		
		JObID=mJobDetails.getPostid();
		
		ArrayList<JobDetails> arrayList=new ArrayList<JobDetails>();
		if (mJobDetails!=null) {
			arrayList.add(mJobDetails);
			mCommentRecycleAdapter.refreshView(arrayList,0);
		}
		

		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext,getResources().getString(R.string.internetconnection_msg));
			onBackPressed();
			return;
		}

		CommentParser mCommentParser = new CommentParser();
		mCommentParser.setCommentparserinterface(new CommentParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<JobDetails> arrayJobsComment,int totalCommentCount) {
				if (arrayJobsComment!=null && arrayJobsComment.size()>=1) {
					mCommentRecycleAdapter.refreshView(arrayJobsComment,totalCommentCount);
				}
				
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mCommentParser.parse(mContext, JObID, mUserDetails.getAuthtoken());
	}
	
	
	private void commentPostProcess(){
		String comment=edt_comment.getText().toString();
		
		if (comment!=null && comment.length()==0) {
			Util.ShowToast(mContext,"Enter comment and then click on send button.");
			return;
		}
		
		
		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext,getResources().getString(R.string.internetconnection_msg));
			onBackPressed();
			return;
		}
		
		CommentPostParser mCommentPostParser=new CommentPostParser();
		mCommentPostParser.setCommentpostparserinterface(new CommentPostParserInterface() {
			
			@Override
			public void OnSuccess(JobDetails mJobDetails) {
				if (mJobDetails!=null) {
					mCommentRecycleAdapter.refreshView(mJobDetails);
					edt_comment.setText("");
					Util.ShowToast(mContext, "Comment Posted successfully."); 
					commentListView.setSelection(commentListView.getAdapter().getCount()-1);
				}
			}
			
			@Override
			public void OnError() {
				
			}
		});
		
		mCommentPostParser.parse(mContext, mCommentPostParser.getBody("4", JObID,comment), mUserDetails.getAuthtoken());

	}

	private void reloadOldData(String commentID){
		if (commentID!=null && commentID.equals("")) {
			mCommentRecycleAdapter.loadOldCommentError();
			return;
		}
		
		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext,getResources().getString(R.string.internetconnection_msg));
			onBackPressed();
			return;
		}
		
		
		CommentAllParser mAllParser=new CommentAllParser();
		mAllParser.setCommentallparserinterface(new CommentAllParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<JobDetails> arrayJobsComment) {
				if (arrayJobsComment!=null && arrayJobsComment.size()>=1) {
					mCommentRecycleAdapter.loadOldCommentView(arrayJobsComment);
				}
			}
			
			@Override
			public void OnError() {
				mCommentRecycleAdapter.loadOldCommentError();
			}
		});
		mAllParser.parse(mContext,mAllParser.getBody("", JObID, commentID), mUserDetails.getAuthtoken());
	}
}
