package com.ofcampus.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.JobListBaseAdapter;
import com.ofcampus.adapter.JobListBaseAdapter.jobListInterface;
import com.ofcampus.databasehelper.ImportantJobTable;
import com.ofcampus.databasehelper.JOBListTable;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.PostJobHideMarkedParser;
import com.ofcampus.parser.PostJobHideMarkedParser.PostJobHideMarkedParserInterface;
import com.ofcampus.ui.ReplyDialog;

public class JobsFragment extends Fragment  implements jobListInterface,OnRefreshListener{

	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;
    private ListView joblist;
    private RelativeLayout footer_pg;
    private JobListBaseAdapter mJobListAdapter;
    private String tocken = "";
    
    /***For Load more****/
    private int minimumofsets = 5,mLastFirstVisibleItem = 0;
    private boolean loadingMore = false;
    
	/**/
	private SwipeRefreshLayout swipeLayout;
	
	
	public String firsttJobID="",lastJobID="";
	
	
    
	public static JobsFragment newInstance(int position,Context mContext) {
		JobsFragment f = new JobsFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		context=mContext;
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view=inflater.inflate(R.layout.fragment_jobs, null);
		initilizView(view);
		initilizeSwipeRefresh(view);
		loadProfileData();
		return view;
	}
	
	@Override
	public void convertViewOnClick(JobDetails mJobDetails) {
		
		((OfCampusApplication)context.getApplicationContext()).jobdetails=mJobDetails;
		Intent mIntent = new Intent(context,ActivityComment.class);
		Bundle mBundle=new Bundle();
		mBundle.putString("key_dlorcmt", Util.TOOLTITLE[1]);
		mIntent.putExtras(mBundle);
		startActivity(mIntent);
		((Activity) context).overridePendingTransition(0, 0); 
	}
	
	@Override
	public void firstIDAndlastID(String fstID, String lstID){
		firsttJobID=fstID;
		lastJobID=lstID;
	}
	
	
	@Override 
	public void arrowHideClieckEvent(JobDetails mJobDetails){
		HideCalling(mJobDetails,1);
	}
	
	@Override 
	public void arrowSpamClieckEvent(JobDetails mJobDetails){
		HideCalling(mJobDetails,3);
	}
	
	@Override 
	public void impClieckEvent(JobDetails mJobDetails){
		HideCalling(mJobDetails,2);	
	}
	
	@Override 
	public void unimpClieckEvent(JobDetails mJobDetails){
		HideCalling(mJobDetails,11);	
	}
	
	@Override 
	public void replyClickEvent(JobDetails mJobDetails){
		new ReplyDialog(context, mJobDetails);
	}
	
	@Override 
	public void commentClickEvent(JobDetails mJobDetails) {
		((OfCampusApplication)context.getApplicationContext()).jobdetails=mJobDetails;
		Intent mIntent = new Intent(context,ActivityComment.class);
		Bundle mBundle=new Bundle();
		mBundle.putString("key_dlorcmt", Util.TOOLTITLE[0]);
		mIntent.putExtras(mBundle);
		startActivity(mIntent);
		((Activity) context).overridePendingTransition(0, 0); 
	}
	
	@Override 
	public void onRefresh() {
		if (jobsfrginterface!=null) {
			jobsfrginterface.pulltorefreshcall(firsttJobID);
		}
	}
	
	
	
	private void loadProfileData() {
		UserDetails mUserDetails = UserDetails.getLoggedInUser(context);
		tocken = mUserDetails.getAuthtoken();
		if (jobsfrginterface!=null) {
			jobsfrginterface.firstLoadCall();
		}
	}
	
	private void initilizView(View view) {
		joblist = (ListView) view.findViewById(R.id.activity_home_joblist);
		footer_pg = (RelativeLayout) view.findViewById(R.id.activity_home_footer_pg);
		
		
		
		mJobListAdapter=new JobListBaseAdapter(context, new ArrayList<JobDetails>());
		mJobListAdapter.setJoblistinterface(this);
		joblist.setAdapter(mJobListAdapter);
		
		
		joblist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if (mJobListAdapter != null
						&& totalItemCount > minimumofsets
						&& (lastInScreen == totalItemCount) && !(loadingMore)) {
					if (mLastFirstVisibleItem < firstVisibleItem) {
						if (!Util.hasConnection(context)) {
							Util.ShowToast(context,context.getResources().getString(R.string.internetconnection_msg));
							return;
						}
						Log.i("SCROLLING DOWN", "TRUE");
						footer_pg.setVisibility(View.VISIBLE);
						loadingMore = true;
						if (jobsfrginterface!=null) {
							jobsfrginterface.loadcall(lastJobID);
						}
					}
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		});

	}
	
	
	private void initilizeSwipeRefresh(View v) {
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.pull_blue_bright,
				R.color.pull_green_light, R.color.pull_orange_light,
				R.color.pull_red_light);
	}
	
	
	public void refreshComplete() {
		if (swipeLayout.isRefreshing()) {
			swipeLayout.setRefreshing(false);
		}
		if (footer_pg.getVisibility()==View.VISIBLE) {
			footer_pg.setVisibility(View.GONE);
			loadingMore = false;
		}
		
	}
	
	
	public void refreshDataInAdapter(ArrayList<JobDetails> jobs){
		firsttJobID=jobs.get(0).getPostid();
		mJobListAdapter.refreshData(jobs);
	}
	
	public void refreshSwipeDataInAdapter(ArrayList<JobDetails> jobs){
		refreshComplete();
		firsttJobID=jobs.get(0).getPostid();
		mJobListAdapter.refreshSwipeData(jobs);
	}
	
	public void refreshLoadMoreDataInAdapter(ArrayList<JobDetails> jobs){
		refreshComplete();
		firsttJobID=jobs.get(0).getPostid();
		mJobListAdapter.refreshloadmoreData(jobs);
	}

	private void HideCalling(final JobDetails mJobDetails, final int state){   
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context,getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		PostJobHideMarkedParser markedParser=new PostJobHideMarkedParser();
		markedParser.setPostjobhidemarkedparserinterface(new PostJobHideMarkedParserInterface() {
			
			@Override
			public void OnSuccess() {
				if (state==1 || state==3) {
					JOBListTable.getInstance(context).deleteSpamJOb(mJobDetails);
					mJobListAdapter.hideJob(mJobDetails);
				}else if (state==2 ) {
					ArrayList<JobDetails> arr=new ArrayList<JobDetails>();
					mJobDetails.important=1;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).inserJobData(mJobDetails);
					mJobListAdapter.importantJob(mJobDetails);
				}else if (state==11 ) {
					ArrayList<JobDetails> arr=new ArrayList<JobDetails>();
					mJobDetails.important=0;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
					mJobListAdapter.unimportantJob(mJobDetails);
				}
			}
			
			@Override
			public void OnError() {
			
			}
		});
		markedParser.parse(context, markedParser.getBody(state+"", mJobDetails.getPostid()), tocken);  
	}

	
	public JobsFrgInterface jobsfrginterface;

	public JobsFrgInterface getJobsfrginterface() {
		return jobsfrginterface;
	}

	public void setJobsfrginterface(JobsFrgInterface jobsfrginterface) {
		this.jobsfrginterface = jobsfrginterface;
	}

	public interface JobsFrgInterface {
		public void pulltorefreshcall(String jobID);

		public void loadcall(String jobID);
		
		public void firstLoadCall();
	}
	
}