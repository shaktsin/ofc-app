/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;

import android.content.Context;
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

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.JobListBaseAdapter;
import com.ofcampus.adapter.JobListBaseAdapter.jobListInterface;
import com.ofcampus.databasehelper.ImportantJobTable;
import com.ofcampus.databasehelper.JOBListTable;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.JobListParserNew;
import com.ofcampus.parser.JobListParserNew.JobListParserNewInterface;
import com.ofcampus.parser.PostJobHideMarkedParser;
import com.ofcampus.parser.PostJobHideMarkedParser.PostJobHideMarkedParserInterface;
import com.ofcampus.parser.PostUnHideUnImpParser;
import com.ofcampus.parser.PostUnHideUnImpParser.PostUnHideUnImpParserInterface;
import com.ofcampus.ui.ReplyDialog;

public class FragmentJobs extends Fragment implements jobListInterface, OnRefreshListener {

	private static final String ARG_POSITION = "position";
	private static Context context;
	private ListView joblist;
	private RelativeLayout footer_pg;
	public JobListBaseAdapter mJobListAdapter;
	private String tocken = "";

	private SwipeRefreshLayout swipeLayout;

	private int pageNo = 0;
	private int pagecount = 8;
	private int minimumofsets = 7, mLastFirstVisibleItem = 0;
	private boolean loadingMore = false;

	public static FragmentJobs newInstance(int position, Context mContext) {
		FragmentJobs f = new FragmentJobs();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		context = mContext;
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		tocken = UserDetails.getLoggedInUser(context).getAuthtoken();
		View view = inflater.inflate(R.layout.fragment_jobs, null);
		initilizView(view);
		initilizeSwipeRefresh(view);
		firstCalling(true);
		return view;
	}
	
	@Override
	public void arrowHideClieckEvent(JobDetails mJobDetails) {
		HideCalling(mJobDetails, 1);
	}

	@Override
	public void arrowSpamClieckEvent(JobDetails mJobDetails) {
		HideCalling(mJobDetails, 3);
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

	@Override
	public void replyClickEvent(JobDetails mJobDetails) {
		new ReplyDialog(context, mJobDetails);
	}

	@Override
	public void onRefresh() {
		String jobCount = ((ActivityHome) context).count[1];
		if (jobCount != null && !jobCount.equals("") && !jobCount.equals("0")) {
			firstCalling(false);
		} else {
			refreshComplete();
		}
	}

	/**
	 * Initialize The View:
	 */
	private void initilizView(View view) {
		joblist = (ListView) view.findViewById(R.id.activity_home_joblist);
		footer_pg = (RelativeLayout) view.findViewById(R.id.activity_home_footer_pg);

		mJobListAdapter = new JobListBaseAdapter(context, new ArrayList<JobDetails>());
		mJobListAdapter.setJoblistinterface(this);
		joblist.setAdapter(mJobListAdapter);

		joblist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if (mJobListAdapter != null && totalItemCount > minimumofsets && (lastInScreen == totalItemCount) && !(loadingMore)) {
					if (mLastFirstVisibleItem < firstVisibleItem) {
						if (!Util.hasConnection(context)) {
							Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
							return;
						}
						Log.i("SCROLLING DOWN", "TRUE");
						footer_pg.setVisibility(View.VISIBLE);
						loadingMore = true;
						loadData(false, pageNo, pagecount);
					}
				}
				mLastFirstVisibleItem = firstVisibleItem;
			}
		});

	}

	/**
	 * Initialize PullToRefresh:
	 */
	private void initilizeSwipeRefresh(View v) {
		swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.pull_blue_bright, R.color.pull_green_light, R.color.pull_orange_light, R.color.pull_red_light);
	}

	public void firstCalling(boolean b) {
		resetAllCond();
		loadData(b, 0, 8);
	}

	private void resetAllCond() {
		pageNo = 0;
		pagecount = 8;
		minimumofsets = 7;
		mLastFirstVisibleItem = 0;
	}

	/**
	 * Initial Load Job Calling.
	 * 
	 * @param j
	 * @param i
	 */
	public void loadData(boolean isShowingPG, final int pageNo_, int pagecount_) {

		
		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, getResources().getString(R.string.internetconnection_msg));
			return;
		}

		JobListParserNew mParserNew = new JobListParserNew();
		mParserNew.setJoblistparsernewinterface(new JobListParserNewInterface() {

			@Override
			public void OnSuccess(ArrayList<JobDetails> jobList) {
				if (jobList != null && jobList.size() >= 1) {
					if (pageNo == 0) {
						mJobListAdapter.refreshData(jobList);
						pageNo = pageNo_ + 1;
					} else {
						mJobListAdapter.refreshloadmoreData(jobList);
						pageNo = pageNo_ + 1;
					}
				}
				refreshComplete();
			}

			@Override
			public void OnError() {
				refreshComplete();
			}
		});
		mParserNew.isShowingPG_ = isShowingPG;
		mParserNew.parse(context, mParserNew.getBody(pageNo_, pagecount_), tocken);
	}

	public void refreshComplete() {
		if (swipeLayout.isRefreshing()) {
			swipeLayout.setRefreshing(false);
//			Util.ShowToast(context, "No more Jobs updated.");
			if (jobsfrginterface!=null) {
				jobsfrginterface.pullToRefreshCallCompleteForJob();
			}
		}
		if (footer_pg.getVisibility() == View.VISIBLE) {
			footer_pg.setVisibility(View.GONE);
			loadingMore = false;
//			Util.ShowToast(context, "No more Jobs.");
		}
		
		
	}
	
	private void setMinOfSetLimit(){
		minimumofsets = minimumofsets - 1;
		pageNo = (minimumofsets/8);
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
				if (state == 1 || state == 3) {
					JOBListTable.getInstance(context).deleteSpamJOb(mJobDetails);
					mJobListAdapter.hideJob(mJobDetails);
					firstCalling(false);
				} else if (state == 2) {
					ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
					mJobDetails.important = 1;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).inserJobData(mJobDetails);
					mJobListAdapter.importantJob(mJobDetails);
				} else if (state == 11) {
					ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
					mJobDetails.important = 0;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
					mJobListAdapter.unimportantJob(mJobDetails);
				} else if (state == 13) {
					// ArrayList<JobDetails> arr=new ArrayList<JobDetails>();
					// mJobDetails.like=0;
					// arr.add(mJobDetails);
					// JOBListTable.getInstance(context).inserJobData(arr);
					// ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
					mJobListAdapter.likRefreshJob(mJobDetails);
				}
			}

			@Override
			public void OnError() {

			}
		});
		markedParser.parse(context, markedParser.getBody(state + "", mJobDetails.getPostid()), tocken);
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
				mJobListAdapter.unimportantJob(mJobDetails);
			}

			@Override
			public void OnError() {

			}
		});
		PostUnHideUnImpParser.parse(context, PostUnHideUnImpParser.getBody(state + "", mJobDetails.getPostid()), tocken);
	}

	public JobsFrgInterface jobsfrginterface;

	public JobsFrgInterface getJobsfrginterface() {
		return jobsfrginterface;
	}

	public void setJobsfrginterface(JobsFrgInterface jobsfrginterface) {
		this.jobsfrginterface = jobsfrginterface;
	}

	public interface JobsFrgInterface {
		public void pullToRefreshCallCompleteForJob();

	}

}