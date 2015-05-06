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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.adapter.ClassifiedListBaseAdapter;
import com.ofcampus.adapter.ClassifiedListBaseAdapter.ClassifiedListInterface;
import com.ofcampus.databasehelper.ImportantJobTable;
import com.ofcampus.databasehelper.JOBListTable;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.ClassifiedListParser;
import com.ofcampus.parser.ClassifiedListParser.ClassifiedListParserInterface;
import com.ofcampus.parser.PostJobHideMarkedParser;
import com.ofcampus.parser.PostJobHideMarkedParser.PostJobHideMarkedParserInterface;
import com.ofcampus.parser.PostUnHideUnImpParser;
import com.ofcampus.parser.PostUnHideUnImpParser.PostUnHideUnImpParserInterface;
import com.ofcampus.ui.ReplyDialog;

public class FragmentClassifieds extends Fragment implements OnClickListener, ClassifiedListInterface, OnRefreshListener {

	private static final String ARG_POSITION = "position";
	private static Context context;
	private int position;

	private ListView classifiedlist;
	private RelativeLayout footer_pg;
	private ClassifiedListBaseAdapter mClassifiedListAdapter;
	private String tocken = "";

	private SwipeRefreshLayout swipeLayout;

	private int pageNo = 0;
	private int pagecount = 8;
	private int minimumofsets = 7, mLastFirstVisibleItem = 0;
	private boolean loadingMore = false;

	public static FragmentClassifieds newInstance(int position, Context mContext) {
		FragmentClassifieds f = new FragmentClassifieds();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		context = mContext;
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_classifieds, null);
		initilizView(view);
		initilizeSwipeRefresh(view);
		firstCalling(true);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
	}

	@Override
	public void replyClickEvent(JobDetails mJobDetails) {
		new ReplyDialog(context, mJobDetails);
	}

	@Override
	public void onRefresh() {
		String jobCount = ((ActivityHome) context).count[2];
		if (jobCount != null && !jobCount.equals("") && !jobCount.equals("0")) {
			firstCalling(false);
		} else {
			refreshComplete();
		}
	}

	private void initilizView(View view) {
		classifiedlist = (ListView) view.findViewById(R.id.activity_home_classifiedlist);
		footer_pg = (RelativeLayout) view.findViewById(R.id.activity_home_footer_pg);

		mClassifiedListAdapter = new ClassifiedListBaseAdapter(context, new ArrayList<JobDetails>());
		mClassifiedListAdapter.setClassifiedlistinterface(this);
		classifiedlist.setAdapter(mClassifiedListAdapter);

		classifiedlist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if (mClassifiedListAdapter != null && totalItemCount > minimumofsets && (lastInScreen == totalItemCount) && !(loadingMore)) {
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
	 * Initial Load News Calling.
	 * 
	 * @param j
	 * @param i
	 * @param b
	 */
	public void loadData(boolean isShowingPG, final int pageNo_, int pagecount_) {
		UserDetails mUserDetails = UserDetails.getLoggedInUser(context);
		tocken = mUserDetails.getAuthtoken();

		if (!Util.hasConnection(context)) {
			Util.ShowToast(context, context.getResources().getString(R.string.internetconnection_msg));
			return;
		}

		ClassifiedListParser mClassifiedListParser = new ClassifiedListParser();
		mClassifiedListParser.setNewsfeedlistparserinterface(new ClassifiedListParserInterface() {

			@Override
			public void OnSuccess(ArrayList<JobDetails> classifiedsList) {
				if (classifiedsList != null && classifiedsList.size() >= 1) {
					if (pageNo == 0) {
						mClassifiedListAdapter.refreshData(classifiedsList);
						pageNo = pageNo_ + 1;
					} else {
						mClassifiedListAdapter.refreshloadmoreData(classifiedsList);
						pageNo = pageNo_ + 1;
						minimumofsets = minimumofsets + pagecount;
					}
				}
				try {
					if (pageNo_ == 0 && classifiedsList.size() == 0) {
						// Util.ShowToast(context, "No more Jobs.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				refreshComplete();

			}

			@Override
			public void OnError() {
				refreshComplete();
			}
		});
		mClassifiedListParser.isShowingPG_ = isShowingPG;
		mClassifiedListParser.parse(context, mClassifiedListParser.getBody(), tocken);
	}

	/**
	 * Check progress showing or not.
	 */
	public void refreshComplete() {
		if (swipeLayout.isRefreshing()) {
			swipeLayout.setRefreshing(false);
			// Util.ShowToast(context, "No more Jobs updated.");
			if (fgclassifiedinterface!=null) {
				fgclassifiedinterface.pullToRefreshCallCompleteForClass();
			}
		}
		if (footer_pg.getVisibility() == View.VISIBLE) {
			footer_pg.setVisibility(View.GONE);
			loadingMore = false;
			// Util.ShowToast(context, "No more Jobs.");
		}

	}

	@Override
	public void hideClieckEvent(JobDetails mJobDetails) {
		HideCalling(mJobDetails, 1);
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
				if (state == 1 || state == 3) {
					JOBListTable.getInstance(context).deleteSpamJOb(mJobDetails);
					mClassifiedListAdapter.hideNews(mJobDetails);
					firstCalling(false);
				} else if (state == 2) {
					ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
					mJobDetails.important = 1;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).inserJobData(mJobDetails);
					mClassifiedListAdapter.importantNews(mJobDetails);
				} else if (state == 11) {
					ArrayList<JobDetails> arr = new ArrayList<JobDetails>();
					mJobDetails.important = 0;
					arr.add(mJobDetails);
					JOBListTable.getInstance(context).inserJobData(arr);
					ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
					mClassifiedListAdapter.unimportantNews(mJobDetails);
				} else if (state == 13) {
					// ArrayList<JobDetails> arr=new ArrayList<JobDetails>();
					// mJobDetails.like=0;
					// arr.add(mJobDetails);
					// JOBListTable.getInstance(context).inserJobData(arr);
					// ImportantJobTable.getInstance(context).deleteUnimpJOb(mJobDetails);
					mClassifiedListAdapter.likRefreshJob(mJobDetails);
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
				mClassifiedListAdapter.unimportantNews(mJobDetails);
			}

			@Override
			public void OnError() {

			}
		});
		PostUnHideUnImpParser.parse(context, PostUnHideUnImpParser.getBody(state + "", mJobDetails.getPostid()), tocken);
	}

	/**
	 * Interface for News Fragment.
	 */
	public FgClassifiedInterface fgclassifiedinterface;

	public FgClassifiedInterface getFgclassifiedinterface() {
		return fgclassifiedinterface;
	}

	public void setFgclassifiedinterface(FgClassifiedInterface fgclassifiedinterface) {
		this.fgclassifiedinterface = fgclassifiedinterface;
	}

	public interface FgClassifiedInterface {
		public void pullToRefreshCallCompleteForClass();// After pull to refresh
														// complete remove the
														// notification.
	}

}